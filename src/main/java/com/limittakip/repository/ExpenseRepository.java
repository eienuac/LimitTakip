package com.limittakip.repository;

import com.limittakip.entity.Expense;
import com.limittakip.enums.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Expense entity'si icin DAO katmani.
 * Spring Data JPA sayesinde temel CRUD islemleri otomatik saglanir.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Belirli bir kategorideki ve belirli bir tarihten sonraki harcamalarin toplam tutarini doner.
     *
     * @param kategori  sorgulanacak kategori
     * @param startDate baslangic tarihi (15. gun dongusu)
     * @return toplam tutar
     */
    @Query("SELECT COALESCE(SUM(e.tutar), 0) FROM Expense e WHERE e.kategori = :kategori AND e.tarih >= :startDate")
    double sumTutarByKategoriAfter(@Param("kategori") Kategori kategori, @Param("startDate") java.time.LocalDateTime startDate);

    /**
     * Belirli bir tarihten sonraki tum harcamalarin toplam tutarini doner.
     *
     * @param startDate baslangic tarihi (15. gun dongusu)
     * @return toplam tutar
     */
    @Query("SELECT COALESCE(SUM(e.tutar), 0) FROM Expense e WHERE e.tarih >= :startDate")
    double sumAllTutarAfter(@Param("startDate") java.time.LocalDateTime startDate);

    /**
     * Mevcut fatura donemindeki (belirli bir tarihten sonraki) tum harcamalari siler.
     *
     * @param startDate fatura donemi baslangici
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("DELETE FROM Expense e WHERE e.tarih >= :startDate")
    void deleteExpensesAfter(@Param("startDate") java.time.LocalDateTime startDate);
}
