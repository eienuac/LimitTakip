package com.limittakip.entity;

import com.limittakip.enums.Kategori;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Harcama (Expense) entity sinifi.
 * Veritabanindaki 'expenses' tablosunu temsil eder.
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kategori kategori;

    @Column(nullable = false)
    private double tutar;

    @Column(nullable = false)
    private LocalDateTime tarih;

    /**
     * JPA icin gerekli bos constructor.
     */
    public Expense() {
    }

    /**
     * Yeni bir harcama olusturur. Tarih otomatik olarak o anki zamana atanir.
     *
     * @param kategori harcama kategorisi
     * @param tutar    harcama tutari (TL)
     */
    public Expense(Kategori kategori, double tutar) {
        this.kategori = kategori;
        this.tutar = tutar;
        this.tarih = LocalDateTime.now();
    }

    // ===== Getter ve Setter Metodlari =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public double getTutar() {
        return tutar;
    }

    public void setTutar(double tutar) {
        this.tutar = tutar;
    }

    public LocalDateTime getTarih() {
        return tarih;
    }

    public void setTarih(LocalDateTime tarih) {
        this.tarih = tarih;
    }
}
