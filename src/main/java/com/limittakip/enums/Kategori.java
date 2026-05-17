package com.limittakip.enums;

import java.util.Locale;

/**
 * Harcama kategorilerini temsil eden Enum sinifi.
 * Sadece 4 sabit kategori desteklenir.
 */
public enum Kategori {

    YEMEK,
    KAHVE,
    YAKIT,
    DIGER;

    /**
     * Gelen metin ile enum sabitini eslestirir.
     * Eslesen bir kategori bulunamazsa null doner.
     * Not: Locale.ENGLISH kullanilir, cunku Turkce locale'de
     * "i".toUpperCase() -> "İ" donusumu enum eslesmesini bozar.
     *
     * @param text kullanicidan gelen kategori metni
     * @return eslesen Kategori veya null
     */
    public static Kategori fromText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Kategori.valueOf(text.toUpperCase(Locale.ENGLISH).trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
