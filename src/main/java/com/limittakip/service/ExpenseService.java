package com.limittakip.service;

import com.limittakip.entity.Expense;
import com.limittakip.enums.Kategori;
import com.limittakip.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Harcama islemlerinin is mantigi katmani.
 * Gelen mesajlarin parse edilmesi, kaydi ve rapor uretimi burada yapilir.
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Value("${harcama.aylik-limit}")
    private double aylikLimit;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * WhatsApp'tan gelen ham mesaji isler.
     * "rapor" komutu geldiginde rapor uretir.
     * "[kategori] [tutar]" formatindaki mesajlari parse ederek kaydeder.
     * Gecersiz formatta null doner (hicbir islem yapilmaz).
     *
     * @param mesaj kullanicidan gelen ham mesaj
     * @return botun donecegi yanit metni veya null
     */
    public String mesajIsle(String mesaj) {
        if (mesaj == null || mesaj.trim().isEmpty()) {
            return null;
        }

        String temizMesaj = mesaj.trim().toLowerCase();

        // Rapor komutu kontrolu
        if (temizMesaj.equals("rapor")) {
            return raporOlustur();
        }

        // Clear komutu kontrolu (Mevcut donemi sifirlar)
        if (temizMesaj.equals("clear")) {
            java.time.LocalDateTime start = getCycleStartDate();
            expenseRepository.deleteExpensesAfter(start);
            double kalanLimit = kalanLimitHesapla(); // 5000 donmeli
            return "Mevcut fatura dönemi harcamaları sıfırlandı! Güncel Kalan Limit: " + kalanLimit + " TL.";
        }

        // Mesaji parse et: [kategori] [tutar]
        String[] parcalar = temizMesaj.split("\\s+");
        if (parcalar.length != 2) {
            return helpMessage();
        }

        String kategoriText = parcalar[0];
        String tutarText = parcalar[1];

        // Kategori eslestirme
        Kategori kategori = Kategori.fromText(kategoriText);
        if (kategori == null) {
            return "Geçersiz kategori! ❌\n\n" + helpMessage();
        }

        // Tutar parse
        double tutar;
        try {
            tutar = Double.parseDouble(tutarText);
        } catch (NumberFormatException e) {
            return "Geçersiz tutar! ❌ Lütfen sayısal bir değer girin.\n\n" + helpMessage();
        }

        // Kaydet
        Expense expense = new Expense(kategori, tutar);
        expenseRepository.save(expense);

        // Guncel kalan limiti hesapla
        double kalanLimit = kalanLimitHesapla();

        return "Kaydedildi! Kategori: " + kategori.name() + ", Tutar: " + tutar + " TL. "
                + "Güncel Kalan Limit: " + kalanLimit + " TL.";
    }

    private String helpMessage() {
        return "Lütfen şu formatlardan birini kullanın:\n"
                + "1️⃣ *Kategori Tutar* (Örn: yemek 150)\n"
                + "2️⃣ *rapor* (Mevcut dönem harcamalarını görmek için)\n"
                + "3️⃣ *clear* (Mevcut dönem harcamalarını sıfırlamak için)\n\n"
                + "Desteklenen Kategoriler:\n"
                + "🟢 yemek\n"
                + "🟢 kahve\n"
                + "🟢 yakıt\n"
                + "🟢 diger";
    }

    /**
     * Mevcut fatura donemindeki (ayın 15'inden 15'ine) harcamalari ve kalan limiti iceren rapor uretir.
     *
     * @return formatlanmis rapor metni
     */
    private String raporOlustur() {
        java.time.LocalDateTime start = getCycleStartDate();
        StringBuilder rapor = new StringBuilder("📊 Harcama Raporu (15.05 - 15.06):\n");
        rapor.append("Dönem Başlangıcı: ").append(start.toLocalDate()).append("\n\n");

        for (Kategori kategori : Kategori.values()) {
            double toplam = expenseRepository.sumTutarByKategoriAfter(kategori, start);
            rapor.append("- ").append(kategori.name()).append(": ").append(toplam).append(" TL\n");
        }

        double kalanLimit = kalanLimitHesapla();
        rapor.append("\nGüncel Kalan Limit: ").append(kalanLimit).append(" TL.");

        return rapor.toString();
    }

    /**
     * Mevcut fatura donemi baslangicini (her ayin 15'i saat 00:00) doner.
     */
    private java.time.LocalDateTime getCycleStartDate() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (now.getDayOfMonth() >= 15) {
            return now.withDayOfMonth(15).withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else {
            return now.minusMonths(1).withDayOfMonth(15).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
    }

    /**
     * Aylik limitten mevcut donemdeki (ayın 15'inden sonraki) harcamalari cikararak kalan limiti hesaplar.
     *
     * @return kalan limit (TL)
     */
    private double kalanLimitHesapla() {
        java.time.LocalDateTime start = getCycleStartDate();
        double toplamHarcama = expenseRepository.sumAllTutarAfter(start);
        return aylikLimit - toplamHarcama;
    }
}
