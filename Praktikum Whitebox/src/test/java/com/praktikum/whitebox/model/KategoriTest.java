package com.praktikum.whitebox.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White Box Test untuk class Kategori
 * Mencakup semua constructor, getter/setter, equals, hashCode, dan toString
 */
@DisplayName("White Box Test - Class Kategori")
public class KategoriTest {

    private Kategori kategori;

    @BeforeEach
    void setUp() {
        kategori = new Kategori("KTG001", "Elektronik", "Barang-barang elektronik");
    }

    // ========================= Constructor Tests =========================

    @Test
    @DisplayName("Constructor tanpa parameter - nilai default null dan aktif=false")
    void testConstructorTanpaParameter() {
        Kategori k = new Kategori();
        assertNull(k.getKode());
        assertNull(k.getNama());
        assertNull(k.getDeskripsi());
        // boolean default Java = false
        assertFalse(k.isAktif());
    }

    @Test
    @DisplayName("Constructor dengan parameter - inisialisasi atribut dan aktif=true")
    void testConstructorDenganParameter() {
        assertEquals("KTG001", kategori.getKode());
        assertEquals("Elektronik", kategori.getNama());
        assertEquals("Barang-barang elektronik", kategori.getDeskripsi());
        assertTrue(kategori.isAktif());
    }

    // ========================= Getter & Setter Tests =========================

    @Test
    @DisplayName("Getter dan Setter berfungsi dengan benar")
    void testGetterSetter() {
        kategori.setKode("KTG002");
        kategori.setNama("Pakaian");
        kategori.setDeskripsi("Kategori baju dan celana");
        kategori.setAktif(false);

        assertEquals("KTG002", kategori.getKode());
        assertEquals("Pakaian", kategori.getNama());
        assertEquals("Kategori baju dan celana", kategori.getDeskripsi());
        assertFalse(kategori.isAktif());
    }

    // ========================= equals() Tests =========================

    @Test
    @DisplayName("equals() true jika kode sama")
    void testEqualsTrue() {
        Kategori k2 = new Kategori("KTG001", "Gadget", "Produk gadget");
        assertTrue(kategori.equals(k2));
    }

    @Test
    @DisplayName("equals() false jika kode berbeda")
    void testEqualsFalseKodeBerbeda() {
        Kategori k2 = new Kategori("KTG002", "Gadget", "Produk gadget");
        assertFalse(kategori.equals(k2));
    }

    @Test
    @DisplayName("equals() false jika objek null atau beda tipe")
    void testEqualsFalseNullDanTipeLain() {
        assertFalse(kategori.equals(null));
        assertFalse(kategori.equals("bukan objek kategori"));
    }

    @Test
    @DisplayName("equals() refleksif, simetris, dan transitif")
    void testEqualsRefleksifSimetrisTransitif() {
        Kategori k1 = new Kategori("KTG001", "Elektronik", "Barang elektronik");
        Kategori k2 = new Kategori("KTG001", "Elektronik", "Barang elektronik");
        Kategori k3 = new Kategori("KTG001", "Elektronik", "Barang elektronik");

        // refleksif
        assertEquals(k1, k1);
        // simetris
        assertEquals(k1, k2);
        assertEquals(k2, k1);
        // transitif
        assertEquals(k1, k2);
        assertEquals(k2, k3);
        assertEquals(k1, k3);
    }

    // ========================= hashCode() Tests =========================

    @Test
    @DisplayName("hashCode() sama jika kode sama")
    void testHashCodeSama() {
        Kategori k2 = new Kategori("KTG001", "Gadget", "Produk gadget");
        assertEquals(kategori.hashCode(), k2.hashCode());
    }

    @Test
    @DisplayName("hashCode() berbeda jika kode berbeda")
    void testHashCodeBerbeda() {
        Kategori k2 = new Kategori("KTG002", "Gadget", "Produk gadget");
        assertNotEquals(kategori.hashCode(), k2.hashCode());
    }

    // ========================= toString() Tests =========================

    @Test
    @DisplayName("toString() mengandung semua informasi penting")
    void testToString() {
        String result = kategori.toString();
        assertTrue(result.contains("KTG001"));
        assertTrue(result.contains("Elektronik"));
        assertTrue(result.contains("Barang-barang elektronik"));
        assertTrue(result.contains("true"));
    }

    @Test
    @DisplayName("toString() tetap valid meskipun nilai null")
    void testToStringDenganNull() {
        Kategori k = new Kategori();
        String result = k.toString();
        assertNotNull(result);
        assertTrue(result.contains("Kategori"));
    }
    // ===== TEST Additional Constructor Cases =====
    @Test
    @DisplayName("Constructor dengan parameter - test boundary values")
    void testConstructorDenganParameterBoundary() {
        Kategori k = new Kategori("ABC", "Elektronik", "Deskripsi");
        assertEquals("ABC", k.getKode());
        assertEquals("Elektronik", k.getNama());
        assertEquals("Deskripsi", k.getDeskripsi());
        assertTrue(k.isAktif());
    }

    // ===== TEST Additional Setter Cases =====
    @Test
    @DisplayName("Setter aktif dengan berbagai nilai boolean")
    void testSetAktifParameterized() {
        kategori.setAktif(true);
        assertTrue(kategori.isAktif());

        kategori.setAktif(false);
        assertFalse(kategori.isAktif());
    }

    // ===== TEST Additional Equals Cases =====
    @Test
    @DisplayName("equals() dengan objek yang sama harus true")
    void testEqualsSameObject() {
        assertTrue(kategori.equals(kategori));
    }

    @Test
    @DisplayName("equals() dengan kode null harus false")
    void testEqualsDenganKodeNull() {
        Kategori k1 = new Kategori();
        Kategori k2 = new Kategori("KTG001", "Nama", "Deskripsi");
        assertFalse(k1.equals(k2));
    }

    // ===== TEST Additional HashCode Cases =====
    @Test
    @DisplayName("hashCode() konsisten")
    void testHashCodeKonsisten() {
        int hashCode1 = kategori.hashCode();
        int hashCode2 = kategori.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    // ===== TEST Edge Cases =====
    @Test
    @DisplayName("Setter dengan nilai null")
    void testSetterDenganNull() {
        kategori.setKode(null);
        kategori.setNama(null);
        kategori.setDeskripsi(null);

        assertNull(kategori.getKode());
        assertNull(kategori.getNama());
        assertNull(kategori.getDeskripsi());
    }
}
