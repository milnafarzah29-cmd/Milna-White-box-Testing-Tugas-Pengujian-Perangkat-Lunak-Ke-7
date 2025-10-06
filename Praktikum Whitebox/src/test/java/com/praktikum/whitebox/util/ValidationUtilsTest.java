package com.praktikum.whitebox.util;

import com.praktikum.whitebox.model.Kategori;
import com.praktikum.whitebox.model.Produk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Improved ValidationUtils Test for High Branch Coverage")
class ValidationUtilsTest {

    // ===== TEST isValidKodeProduk =====
    @Test
    void testIsValidKodeProduk_AllBranches() {
        // null
        assertFalse(ValidationUtils.isValidKodeProduk(null));
        // kosong
        assertFalse(ValidationUtils.isValidKodeProduk(""));
        // hanya spasi
        assertFalse(ValidationUtils.isValidKodeProduk("   "));
        // valid
        assertTrue(ValidationUtils.isValidKodeProduk("AB12"));
        // terlalu panjang
        assertFalse(ValidationUtils.isValidKodeProduk("ABCDEFGHIJK"));
        // karakter tidak valid
        assertFalse(ValidationUtils.isValidKodeProduk("ABC 123"));
        assertFalse(ValidationUtils.isValidKodeProduk("ABC@123"));
    }

    // ===== TEST isValidNama =====
    @Test
    void testIsValidNama_AllBranches() {
        // null dan kosong
        assertFalse(ValidationUtils.isValidNama(null));
        assertFalse(ValidationUtils.isValidNama(""));
        assertFalse(ValidationUtils.isValidNama("   "));
        // valid batas bawah & atas
        assertTrue(ValidationUtils.isValidNama("abc"));
        assertTrue(ValidationUtils.isValidNama("a".repeat(100)));
        // terlalu pendek
        assertFalse(ValidationUtils.isValidNama("ab"));
        // terlalu panjang
        assertFalse(ValidationUtils.isValidNama("a".repeat(101)));
    }

    // ===== TEST isValidHarga =====
    @Test
    void testIsValidHarga_Branches() {
        assertTrue(ValidationUtils.isValidHarga(1));
        assertFalse(ValidationUtils.isValidHarga(0));
        assertFalse(ValidationUtils.isValidHarga(-1));
    }

    // ===== TEST isValidStok =====
    @Test
    void testIsValidStok_Branches() {
        assertTrue(ValidationUtils.isValidStok(0));
        assertTrue(ValidationUtils.isValidStok(10));
        assertFalse(ValidationUtils.isValidStok(-1));
    }

    // ===== TEST isValidStokMinimum =====
    @Test
    void testIsValidStokMinimum_Branches() {
        assertTrue(ValidationUtils.isValidStokMinimum(0));
        assertFalse(ValidationUtils.isValidStokMinimum(-5));
    }

    // ===== TEST isValidPersentase =====
    @Test
    void testIsValidPersentase_Branches() {
        assertTrue(ValidationUtils.isValidPersentase(0));
        assertTrue(ValidationUtils.isValidPersentase(50));
        assertTrue(ValidationUtils.isValidPersentase(100));
        assertFalse(ValidationUtils.isValidPersentase(-0.1));
        assertFalse(ValidationUtils.isValidPersentase(100.1));
    }

    // ===== TEST isValidKuantitas =====
    @Test
    void testIsValidKuantitas_Branches() {
        assertTrue(ValidationUtils.isValidKuantitas(1));
        assertFalse(ValidationUtils.isValidKuantitas(0));
        assertFalse(ValidationUtils.isValidKuantitas(-1));
    }

    // ===== TEST isValidKategori =====
    @Test
    void testIsValidKategori_AllBranches() {
        // Null kategori
        assertFalse(ValidationUtils.isValidKategori(null));

        // Valid kategori
        Kategori valid = new Kategori("KAT1", "Elektronik", "Deskripsi singkat");
        assertTrue(ValidationUtils.isValidKategori(valid));

        // Deskripsi null
        Kategori noDesc = new Kategori("KAT2", "Makanan", null);
        assertTrue(ValidationUtils.isValidKategori(noDesc));

        // Deskripsi <= 500 karakter
        Kategori shortDesc = new Kategori("KAT3", "Fashion", "a".repeat(500));
        assertTrue(ValidationUtils.isValidKategori(shortDesc));

        // Deskripsi > 500 karakter
        Kategori longDesc = new Kategori("KAT4", "Fashion", "a".repeat(501));
        assertFalse(ValidationUtils.isValidKategori(longDesc));

        // Kode invalid
        Kategori badKode = new Kategori("??", "Elektronik", "desc");
        assertFalse(ValidationUtils.isValidKategori(badKode));

        // Nama invalid
        Kategori badNama = new Kategori("K01", "A", "desc");
        assertFalse(ValidationUtils.isValidKategori(badNama));
    }

    // ===== TEST isValidProduk =====
    @Test
    void testIsValidProduk_AllBranches() {
        // Null produk
        assertFalse(ValidationUtils.isValidProduk(null));

        // Produk valid
        Produk valid = new Produk("P001", "Laptop", "Elektronik", 10000, 10, 2);
        assertTrue(ValidationUtils.isValidProduk(valid));

        // Produk invalid kode
        Produk badKode = new Produk("?", "Laptop", "Elektronik", 10000, 10, 2);
        assertFalse(ValidationUtils.isValidProduk(badKode));

        // Produk invalid nama
        Produk badNama = new Produk("P001", "A", "Elektronik", 10000, 10, 2);
        assertFalse(ValidationUtils.isValidProduk(badNama));

        // Produk invalid kategori
        Produk badKategori = new Produk("P001", "Laptop", "", 10000, 10, 2);
        assertFalse(ValidationUtils.isValidProduk(badKategori));

        // Produk invalid harga
        Produk badHarga = new Produk("P001", "Laptop", "Elektronik", -1, 10, 2);
        assertFalse(ValidationUtils.isValidProduk(badHarga));

        // Produk invalid stok
        Produk badStok = new Produk("P001", "Laptop", "Elektronik", 10000, -1, 2);
        assertFalse(ValidationUtils.isValidProduk(badStok));

        // Produk invalid stok minimum
        Produk badStokMin = new Produk("P001", "Laptop", "Elektronik", 10000, 10, -5);
        assertFalse(ValidationUtils.isValidProduk(badStokMin));

        // Produk stok < stok minimum (masih valid karena tidak dicek di logika)
        Produk stokKurang = new Produk("P001", "Laptop", "Elektronik", 10000, 1, 5);
        assertTrue(ValidationUtils.isValidProduk(stokKurang));
    }

    // ===== TEST Additional Kode Produk Cases =====
    @Test
    @DisplayName("Kode produk dengan spasi harus invalid")
    void testKodeProdukDenganSpasi() {
        assertFalse(ValidationUtils.isValidKodeProduk("ABC 123"));
    }

    @Test
    @DisplayName("Kode produk dengan karakter special harus invalid")
    void testKodeProdukDenganKarakterSpecial() {
        assertFalse(ValidationUtils.isValidKodeProduk("ABC@123"));
    }

    // ===== TEST Additional Nama Cases =====
    @Test
    @DisplayName("Nama dengan spasi di trim harus valid")
    void testNamaDenganSpasiDiTrim() {
        assertTrue(ValidationUtils.isValidNama("  Laptop Gaming  "));
    }

    // ===== TEST Additional Harga Cases =====
    @Test
    @DisplayName("Harga dengan nilai decimal harus valid")
    void testHargaDenganDecimal() {
        assertTrue(ValidationUtils.isValidHarga(1000.50));
    }

    // ===== TEST Additional Kategori Cases =====
    @Test
    @DisplayName("Kategori dengan deskripsi null harus valid")
    void testKategoriDenganDeskripsiNull() {
        Kategori kategori = new Kategori("CAT123", "Category Name", null);
        assertTrue(ValidationUtils.isValidKategori(kategori));
    }

    // ===== TEST Additional Produk Cases =====
    @Test
    @DisplayName("Produk dengan semua field null harus invalid")
    void testProdukDenganSemuaFieldNull() {
        Produk produk = new Produk(null, null, null, -1, -1, -1);
        assertFalse(ValidationUtils.isValidProduk(produk));
    }

    @Test
    @DisplayName("Produk dengan stok 0 harus valid")
    void testProdukDenganStokNol() {
        Produk produk = new Produk("PROD001", "Laptop", "Elektronik", 10000, 0, 2);
        assertTrue(ValidationUtils.isValidProduk(produk));
    }
}
