package com.praktikum.whitebox.service;

import com.praktikum.whitebox.model.Produk;
import com.praktikum.whitebox.repository.RepositoryProduk;
import com.praktikum.whitebox.util.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ServiceInventaris covering branches to reach >90% coverage.
 * Tests are written to avoid unnecessary stubbings.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceInventaris - Comprehensive Unit Tests")
class ServiceInventarisTest {

    @Mock
    private RepositoryProduk repo;

    private ServiceInventaris service;
    private Produk aktifProduk;

    @BeforeEach
    void setUp() {
        service = new ServiceInventaris(repo);
        aktifProduk = new Produk("P001", "Laptop", "Elektronik", 10_000_000, 10, 5);
        aktifProduk.setAktif(true);
    }

    // ---------- tambahProduk ----------
    @Test
    @DisplayName("tambahProduk: gagal ketika produk tidak valid (ValidationUtils)")
    void tambahProduk_InvalidProduk() {
        Produk invalid = new Produk("X", "A", "K", -1, -1, -1); // invalid according to ValidationUtils
        // No repo interaction expected
        boolean res = service.tambahProduk(invalid);
        assertFalse(res);
        verifyNoInteractions(repo);
    }

    @Test
    @DisplayName("tambahProduk: gagal ketika produk sudah ada")
    void tambahProduk_AlreadyExists() {
        when(repo.cariByKode("P001")).thenReturn(Optional.of(aktifProduk));
        boolean res = service.tambahProduk(aktifProduk);
        assertFalse(res);
        verify(repo).cariByKode("P001");
        verify(repo, never()).simpan(any());
    }

    @Test
    @DisplayName("tambahProduk: berhasil ketika valid dan tidak ada duplikat")
    void tambahProduk_Success() {
        when(repo.cariByKode("P001")).thenReturn(Optional.empty());
        when(repo.simpan(aktifProduk)).thenReturn(true);

        boolean res = service.tambahProduk(aktifProduk);
        assertTrue(res);
        verify(repo).cariByKode("P001");
        verify(repo).simpan(aktifProduk);
    }

    @Test
    @DisplayName("tambahProduk: simpan mengembalikan false -> service false")
    void tambahProduk_SimpanFalse() {
        when(repo.cariByKode("P001")).thenReturn(Optional.empty());
        when(repo.simpan(aktifProduk)).thenReturn(false);

        boolean res = service.tambahProduk(aktifProduk);
        assertFalse(res);
        verify(repo).simpan(aktifProduk);
    }

    // ---------- hapusProduk ----------
    @Test
    @DisplayName("hapusProduk: gagal jika kode tidak valid")
    void hapusProduk_InvalidKode() {
        // invalid kode like "??" -> ValidationUtils.isValidKodeProduk false
        boolean res = service.hapusProduk("??");
        assertFalse(res);
        verifyNoInteractions(repo);
    }

    @Test
    @DisplayName("hapusProduk: gagal jika produk tidak ditemukan")
    void hapusProduk_NotFound() {
        when(repo.cariByKode("P001")).thenReturn(Optional.empty());
        boolean res = service.hapusProduk("P001");
        assertFalse(res);
        verify(repo).cariByKode("P001");
        verify(repo, never()).hapus(anyString());
    }

    @Test
    @DisplayName("hapusProduk: gagal jika stok > 0")
    void hapusProduk_StokMasihAda() {
        Produk p = new Produk("P002", "Mouse", "Elektronik", 200_000, 3, 1);
        when(repo.cariByKode("P002")).thenReturn(Optional.of(p));
        boolean res = service.hapusProduk("P002");
        assertFalse(res);
        verify(repo).cariByKode("P002");
        verify(repo, never()).hapus(anyString());
    }

    @Test
    @DisplayName("hapusProduk: sukses ketika stok 0 dan repo.hapus true")
    void hapusProduk_Success() {
        Produk p = new Produk("P003", "Cable", "Elektronik", 50_000, 0, 0);
        when(repo.cariByKode("P003")).thenReturn(Optional.of(p));
        when(repo.hapus("P003")).thenReturn(true);

        boolean res = service.hapusProduk("P003");
        assertTrue(res);
        verify(repo).hapus("P003");
    }

    @Test
    @DisplayName("hapusProduk: repo.hapus mengembalikan false -> service false")
    void hapusProduk_RepoFails() {
        Produk p = new Produk("P004", "Adapter", "Elektronik", 75_000, 0, 0);
        when(repo.cariByKode("P004")).thenReturn(Optional.of(p));
        when(repo.hapus("P004")).thenReturn(false);

        boolean res = service.hapusProduk("P004");
        assertFalse(res);
        verify(repo).hapus("P004");
    }

    // ---------- cariProdukByKode ----------
    @Test
    @DisplayName("cariProdukByKode: empty untuk kode invalid")
    void cariProdukByKode_InvalidKode() {
        Optional<Produk> res = service.cariProdukByKode("!!");
        assertTrue(res.isEmpty());
        verifyNoInteractions(repo);
    }

    @Test
    @DisplayName("cariProdukByKode: return when present")
    void cariProdukByKode_Present() {
        when(repo.cariByKode("P001")).thenReturn(Optional.of(aktifProduk));
        Optional<Produk> res = service.cariProdukByKode("P001");
        assertTrue(res.isPresent());
        assertEquals("Laptop", res.get().getNama());
        verify(repo).cariByKode("P001");
    }

    // ---------- cariProdukByNama / Kategori ----------
    @Test
    @DisplayName("cariProdukByNama: delegasi ke repository")
    void cariProdukByNama_Test() {
        when(repo.cariByNama("Laptop")).thenReturn(Collections.singletonList(aktifProduk));
        List<Produk> list = service.cariProdukByNama("Laptop");
        assertEquals(1, list.size());
        verify(repo).cariByNama("Laptop");
    }

    @Test
    @DisplayName("cariProdukByKategori: delegasi ke repository")
    void cariProdukByKategori_Test() {
        when(repo.cariByKategori("Elektronik")).thenReturn(Collections.singletonList(aktifProduk));
        List<Produk> list = service.cariProdukByKategori("Elektronik");
        assertEquals(1, list.size());
        verify(repo).cariByKategori("Elektronik");
    }

    // ---------- updateStok ----------
    @Test
    @DisplayName("updateStok: gagal jika kode invalid atau stok negatif")
    void updateStok_InvalidInput() {
        // invalid kode
        boolean r1 = service.updateStok("!!", 5);
        assertFalse(r1);
        // negative stok
        boolean r2 = service.updateStok("P001", -1);
        assertFalse(r2);
        verifyNoInteractions(repo);
    }

    @Test
    @DisplayName("updateStok: gagal jika produk tidak ditemukan")
    void updateStok_NotFound() {
        when(repo.cariByKode("P001")).thenReturn(Optional.empty());
        boolean res = service.updateStok("P001", 8);
        assertFalse(res);
        verify(repo).cariByKode("P001");
        verify(repo, never()).updateStok(anyString(), anyInt());
    }

    @Test
    @DisplayName("updateStok: sukses ketika repo.updateStok true")
    void updateStok_Success() {
        when(repo.cariByKode("P001")).thenReturn(Optional.of(aktifProduk));
        when(repo.updateStok("P001", 8)).thenReturn(true);

        boolean res = service.updateStok("P001", 8);
        assertTrue(res);
        verify(repo).updateStok("P001", 8);
    }

    @Test
    @DisplayName("updateStok: repo.updateStok false -> service false")
    void updateStok_RepoFalse() {
        when(repo.cariByKode("P001")).thenReturn(Optional.of(aktifProduk));
        when(repo.updateStok("P001", 8)).thenReturn(false);

        boolean res = service.updateStok("P001", 8);
        assertFalse(res);
        verify(repo).updateStok("P001", 8);
    }

    // ---------- keluarStok ----------
    @Test
    @DisplayName("keluarStok: invalid kode or non-positive jumlah -> false")
    void keluarStok_InvalidInput() {
        assertFalse(service.keluarStok("!", 1));
        assertFalse(service.keluarStok("P001", 0));
        verifyNoInteractions(repo);
    }

    @Test
    @DisplayName("keluarStok: produk tidak ada atau tidak aktif -> false")
    void keluarStok_NotPresentOrInactive() {
        when(repo.cariByKode("P001")).thenReturn(Optional.empty());
        assertFalse(service.keluarStok("P001", 1));
        verify(repo).cariByKode("P001");

        // inactive product
        Produk inactive = new Produk("P010", "Old", "Elektronik", 1000, 5, 1);
        inactive.setAktif(false);
        when(repo.cariByKode("P010")).thenReturn(Optional.of(inactive));
        assertFalse(service.keluarStok("P010", 1));
        verify(repo).cariByKode("P010");
    }

    @Test
    @DisplayName("keluarStok: gagal ketika stok kurang")
    void keluarStok_StokKurang() {
        Produk p = new Produk("P020", "Item", "Cat", 1000, 2, 1);
        p.setAktif(true);
        when(repo.cariByKode("P020")).thenReturn(Optional.of(p));
        assertFalse(service.keluarStok("P020", 5));
        verify(repo).cariByKode("P020");
        verify(repo, never()).updateStok(anyString(), anyInt());
    }

    @Test
    @DisplayName("keluarStok: sukses ketika stok cukup dan repo.updateStok true")
    void keluarStok_Success() {
        Produk p = new Produk("P030", "Item2", "Cat", 1000, 5, 1);
        p.setAktif(true);
        when(repo.cariByKode("P030")).thenReturn(Optional.of(p));
        when(repo.updateStok("P030", 2)).thenReturn(true); // 5 - 3 = 2

        assertTrue(service.keluarStok("P030", 3));
        verify(repo).updateStok("P030", 2);
    }

    @Test
    @DisplayName("keluarStok: repo.updateStok false -> service false")
    void keluarStok_RepoFails() {
        Produk p = new Produk("P040", "Item3", "Cat", 1000, 5, 1);
        p.setAktif(true);
        when(repo.cariByKode("P040")).thenReturn(Optional.of(p));
        when(repo.updateStok("P040", 2)).thenReturn(false);

        assertFalse(service.keluarStok("P040", 3));
        verify(repo).updateStok("P040", 2);
    }

    // ---------- masukStok ----------
    @Test
    @DisplayName("masukStok: invalid kode or jumlah non-positive -> false")
    void masukStok_InvalidInput() {
        assertFalse(service.masukStok("!", 1));
        assertFalse(service.masukStok("P001", 0));
        verifyNoInteractions(repo);
    }

    @Test
    @DisplayName("masukStok: produk tidak ditemukan atau tidak aktif -> false")
    void masukStok_NotFoundOrInactive() {
        when(repo.cariByKode("P001")).thenReturn(Optional.empty());
        assertFalse(service.masukStok("P001", 5));
        verify(repo).cariByKode("P001");

        Produk inactive = new Produk("P050", "Old2", "Cat", 100, 1, 1);
        inactive.setAktif(false);
        when(repo.cariByKode("P050")).thenReturn(Optional.of(inactive));
        assertFalse(service.masukStok("P050", 5));
        verify(repo).cariByKode("P050");
    }

    @Test
    @DisplayName("masukStok: sukses when repo.updateStok true")
    void masukStok_Success() {
        Produk p = new Produk("P060", "ItemAdd", "Cat", 100, 2, 1);
        p.setAktif(true);
        when(repo.cariByKode("P060")).thenReturn(Optional.of(p));
        when(repo.updateStok("P060", 7)).thenReturn(true); // 2 + 5 = 7

        assertTrue(service.masukStok("P060", 5));
        verify(repo).updateStok("P060", 7);
    }

    @Test
    @DisplayName("masukStok: repo.updateStok false -> service false")
    void masukStok_RepoFails() {
        Produk p = new Produk("P070", "ItemAdd2", "Cat", 100, 2, 1);
        p.setAktif(true);
        when(repo.cariByKode("P070")).thenReturn(Optional.of(p));
        when(repo.updateStok("P070", 6)).thenReturn(false); // 2 + 4 = 6

        assertFalse(service.masukStok("P070", 4));
        verify(repo).updateStok("P070", 6);
    }

    // ---------- getProdukStokMenipis / getProdukStokHabis ----------
    @Test
    @DisplayName("getProdukStokMenipis delegasi ke repo")
    void getProdukStokMenipis_Test() {
        when(repo.cariProdukStokMenipis()).thenReturn(Collections.singletonList(aktifProduk));
        List<Produk> out = service.getProdukStokMenipis();
        assertEquals(1, out.size());
        verify(repo).cariProdukStokMenipis();
    }

    @Test
    @DisplayName("getProdukStokHabis delegasi ke repo")
    void getProdukStokHabis_Test() {
        Produk habis = new Produk("P080", "Habis", "Cat", 50, 0, 1);
        when(repo.cariProdukStokHabis()).thenReturn(Collections.singletonList(habis));
        List<Produk> out = service.getProdukStokHabis();
        assertEquals(1, out.size());
        verify(repo).cariProdukStokHabis();
    }

    // ---------- hitungTotalNilaiInventaris ----------
    @Test
    @DisplayName("hitungTotalNilaiInventaris: hanya produk aktif dihitung")
    void hitungTotalNilaiInventaris_Test() {
        Produk p1 = new Produk("P101", "A", "X", 10_000, 2, 1); p1.setAktif(true);
        Produk p2 = new Produk("P102", "B", "X", 5_000, 3, 1); p2.setAktif(true);
        Produk p3 = new Produk("P103", "C", "X", 2_000, 10, 1); p3.setAktif(false);

        when(repo.cariSemua()).thenReturn(Arrays.asList(p1, p2, p3));

        double expected = (10_000 * 2) + (5_000 * 3);
        assertEquals(expected, service.hitungTotalNilaiInventaris(), 0.0001);
        verify(repo).cariSemua();
    }

    // ---------- hitungTotalStok ----------
    @Test
    @DisplayName("hitungTotalStok: hanya produk aktif dihitung")
    void hitungTotalStok_Test() {
        Produk p1 = new Produk("P201", "A", "X", 10_000, 2, 1); p1.setAktif(true);
        Produk p2 = new Produk("P202", "B", "X", 5_000, 3, 1); p2.setAktif(true);
        Produk p3 = new Produk("P203", "C", "X", 2_000, 10, 1); p3.setAktif(false);

        when(repo.cariSemua()).thenReturn(Arrays.asList(p1, p2, p3));

        int expected = 2 + 3;
        assertEquals(expected, service.hitungTotalStok());
        verify(repo).cariSemua();
    }
    // ===== TEST MASUK STOK =====
    @Test
    @DisplayName("Masuk stok gagal - kode invalid")
    void testMasukStokKodeInvalid() {
        boolean result = service.masukStok("", 5);
        assertFalse(result);
    }

    @Test
    @DisplayName("Masuk stok gagal - jumlah <= 0")
    void testMasukStokJumlahInvalid() {
        boolean result = service.masukStok("PROD001", 0);
        assertFalse(result);
    }

    @Test
    @DisplayName("Masuk stok berhasil - produk aktif")
    void testMasukStokBerhasil() {
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        when(repo.updateStok("PROD001", 15)).thenReturn(true);

        boolean result = service.masukStok("PROD001", 5);
        assertTrue(result);
        verify(repo).updateStok("PROD001", 15);
    }

    // ===== TEST HAPUS PRODUK =====
    @Test
    @DisplayName("Hapus produk gagal - kode invalid")
    void testHapusProdukKodeInvalid() {
        boolean result = service.hapusProduk("");
        assertFalse(result);
    }

    @Test
    @DisplayName("Hapus produk gagal - stok masih ada")
    void testHapusProdukStokMasihAda() {
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        boolean result = service.hapusProduk("PROD001");
        assertFalse(result);
    }

    @Test
    @DisplayName("Hapus produk berhasil - stok 0")
    void testHapusProdukBerhasil() {
        aktifProduk.setStok(0);
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        when(repo.hapus("PROD001")).thenReturn(true);

        boolean result = service.hapusProduk("PROD001");
        assertTrue(result);
    }

    // ===== TEST UPDATE STOK =====
    @Test
    @DisplayName("Update stok gagal - stok baru negatif")
    void testUpdateStokNegatif() {
        boolean result = service.updateStok("PROD001", -1);
        assertFalse(result);
    }

    @Test
    @DisplayName("Update stok gagal - kode invalid")
    void testUpdateStokKodeInvalid() {
        boolean result = service.updateStok("", 10);
        assertFalse(result);
    }

    @Test
    @DisplayName("Update stok gagal - produk tidak ditemukan")
    void testUpdateStokProdukTidakAda() {
        when(repo.cariByKode("PROD001")).thenReturn(Optional.empty());
        boolean result = service.updateStok("PROD001", 10);
        assertFalse(result);
    }

    @Test
    @DisplayName("Update stok berhasil")
    void testUpdateStokBerhasil() {
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        when(repo.updateStok("PROD001", 20)).thenReturn(true);

        boolean result = service.updateStok("PROD001", 20);
        assertTrue(result);
    }

    // ===== TEST HITUNG TOTAL STOK =====
    @Test
    @DisplayName("Hitungan total stok semua produk aktif")
    void testHitungTotalStok() {
        Produk p1 = new Produk("P1", "Laptop", "Elektronik", 10000, 2, 1);
        Produk p2 = new Produk("P2", "Mouse", "Elektronik", 5000, 3, 1);
        Produk p3 = new Produk("P3", "Keyboard", "Elektronik", 3000, 4, 1);
        p3.setAktif(false); // non aktif

        when(repo.cariSemua()).thenReturn(Arrays.asList(p1, p2, p3));

        int totalStok = service.hitungTotalStok();
        assertEquals(5, totalStok); // 2 + 3
    }

    // ===== TEST CARI PRODUK =====
    @Test
    @DisplayName("Cari produk by kode - invalid")
    void testCariProdukByKodeInvalid() {
        Optional<Produk> result = service.cariProdukByKode("");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Cari produk by nama")
    void testCariProdukByNama() {
        when(repo.cariByNama("Laptop")).thenReturn(Collections.singletonList(aktifProduk));
        List<Produk> result = service.cariProdukByNama("Laptop");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Cari produk by kategori")
    void testCariProdukByKategori() {
        when(repo.cariByKategori("Elektronik")).thenReturn(Collections.singletonList(aktifProduk));
        List<Produk> result = service.cariProdukByKategori("Elektronik");
        assertEquals(1, result.size());
    }

    // ===== TEST KELUAR STOK =====
    @Test
    @DisplayName("Keluar stok gagal - kode invalid")
    void testKeluarStokKodeInvalid() {
        boolean result = service.keluarStok("", 5);
        assertFalse(result);
    }

    @Test
    @DisplayName("Keluar stok gagal - jumlah invalid")
    void testKeluarStokJumlahInvalid() {
        boolean result = service.keluarStok("PROD001", 0);
        assertFalse(result);
    }

    @Test
    @DisplayName("Keluar stok gagal - produk tidak ditemukan")
    void testKeluarStokProdukTidakAda() {
        when(repo.cariByKode("PROD001")).thenReturn(Optional.empty());
        boolean result = service.keluarStok("PROD001", 5);
        assertFalse(result);
    }

    @Test
    @DisplayName("Keluar stok gagal - produk tidak aktif")
    void testKeluarStokProdukTidakAktif() {
        aktifProduk.setAktif(false);
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        boolean result = service.keluarStok("PROD001", 5);
        assertFalse(result);
    }

    // ===== TEST TAMBAH PRODUK =====
    @Test
    @DisplayName("Tambah produk invalid")
    void testTambahProdukInvalid() {
        Produk invalidProduk = new Produk("", "A", "B", -1, -1, -1);
        boolean result = service.tambahProduk(invalidProduk);
        assertFalse(result);
    }

    @Test
    @DisplayName("Cari produk by kode valid")
    void testCariProdukByKodeValid() {
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        Optional<Produk> result = service.cariProdukByKode("PROD001");
        assertTrue(result.isPresent());
        assertEquals("PROD001", result.get().getKode());
    }

    // ===== TEST PRODUK TIDAK AKTIF =====
    @Test
    @DisplayName("Masuk stok gagal - produk tidak aktif")
    void testMasukStokProdukTidakAktif() {
        aktifProduk.setAktif(false);
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        boolean result = service.masukStok("PROD001", 5);
        assertFalse(result);
    }

    @Test
    @DisplayName("Update stok gagal - produk tidak aktif")
    void testUpdateStokProdukTidakAktif() {
        aktifProduk.setAktif(false);
        when(repo.cariByKode("PROD001")).thenReturn(Optional.of(aktifProduk));
        boolean result = service.updateStok("PROD001", 10);
        assertFalse(result);
    }

    // ===== TEST GET PRODUK STOK HABIS =====
    @Test
    @DisplayName("Get produk stok habis")
    void testGetProdukStokHabis() {
        Produk habisProduk = new Produk("P999", "Stok Habis", "Elektronik", 1000, 0, 5);
        when(repo.cariProdukStokHabis()).thenReturn(Collections.singletonList(habisProduk));

        List<Produk> result = service.getProdukStokHabis();
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStok());
    }
}
