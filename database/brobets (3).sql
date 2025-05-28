-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 28 Bulan Mei 2025 pada 08.33
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `brobets`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `barang`
--

CREATE TABLE `barang` (
  `id_barang` varchar(20) NOT NULL,
  `nama_barang` varchar(30) NOT NULL,
  `harga_sewa` int(11) NOT NULL,
  `kategori` varchar(20) NOT NULL,
  `stok` int(11) NOT NULL,
  `status` enum('Tersedia','Disewa','Rusak','Hilang','Maintenance') NOT NULL,
  `harga_beli` int(11) NOT NULL,
  `barcode` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `barang`
--

INSERT INTO `barang` (`id_barang`, `nama_barang`, `harga_sewa`, `kategori`, `stok`, `status`, `harga_beli`, `barcode`) VALUES
('BRG001', 'Tenda', 20000, 'Peralatan', 3, 'Tersedia', 80000, NULL),
('BRG002', 'Ransel', 40000, 'Tas', 6, 'Tersedia', 30000, NULL),
('BRG003', 'Kompor Kecil', 26000, 'Peralatan', 4, 'Tersedia', 50000, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `barang_bermasalah`
--

CREATE TABLE `barang_bermasalah` (
  `id_brg_bermasalah` varchar(20) NOT NULL,
  `id_barang` varchar(20) NOT NULL,
  `nama_barang` varchar(20) NOT NULL,
  `stok` int(11) NOT NULL,
  `harga_beli` int(11) NOT NULL,
  `status` enum('Rusak','Hilang','Maintenance') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `detail_pengembalian`
--

CREATE TABLE `detail_pengembalian` (
  `id_detail_kembali` varchar(20) NOT NULL,
  `id_kembali` varchar(20) NOT NULL,
  `id_barang` varchar(20) NOT NULL,
  `jumlah` int(10) NOT NULL,
  `kondisi` enum('Baik','Rusak','Hilang','') NOT NULL,
  `denda_barang` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `detail_pengembalian`
--

INSERT INTO `detail_pengembalian` (`id_detail_kembali`, `id_kembali`, `id_barang`, `jumlah`, `kondisi`, `denda_barang`) VALUES
('DTP001', 'PM001', 'BRG001', 1, 'Baik', 0),
('DTP002', 'PM001', 'BRG001', 1, 'Baik', 0),
('DTP003', 'PM001', 'BRG002', 1, 'Baik', 0),
('DTP004', 'PM001', 'BRG002', 1, 'Rusak', 30000),
('DTP005', 'PM002', 'BRG001', 1, 'Baik', 0),
('DTP006', 'PM002', 'BRG001', 1, 'Baik', 0),
('DTP007', 'PM003', 'BRG001', 1, 'Baik', 0),
('DTP008', 'PM003', 'BRG001', 0, 'Hilang', 0),
('DTP009', 'PM003', 'BRG001', 1, 'Baik', 0),
('DTP010', 'PM003', 'BRG002', 1, 'Baik', 0);

--
-- Trigger `detail_pengembalian`
--
DELIMITER $$
CREATE TRIGGER `penambah_stok` AFTER INSERT ON `detail_pengembalian` FOR EACH ROW BEGIN
  UPDATE barang
  SET stok = stok + NEW.jumlah
  WHERE id_barang = NEW.id_barang;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `detail_sewa`
--

CREATE TABLE `detail_sewa` (
  `id_detail` varchar(20) NOT NULL,
  `id_sewa` varchar(20) NOT NULL,
  `id_barang` varchar(20) NOT NULL,
  `qty` int(11) NOT NULL,
  `sub_total` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `detail_sewa`
--

INSERT INTO `detail_sewa` (`id_detail`, `id_sewa`, `id_barang`, `qty`, `sub_total`) VALUES
('DTS001', 'PN001', 'BRG001', 2, 40000),
('DTS002', 'PN001', 'BRG002', 2, 80000),
('DTS003', 'PN002', 'BRG001', 2, 40000),
('DTS004', 'PN003', 'BRG002', 1, 40000),
('DTS005', 'PN003', 'BRG001', 3, 60000);

--
-- Trigger `detail_sewa`
--
DELIMITER $$
CREATE TRIGGER `Pengurangan_Stok` AFTER INSERT ON `detail_sewa` FOR EACH ROW BEGIN
  UPDATE barang
  SET stok = stok - NEW.qty
  WHERE id_barang = NEW.id_barang;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `pelanggan`
--

CREATE TABLE `pelanggan` (
  `id_pelanggan` varchar(20) NOT NULL,
  `nama_pelanggan` varchar(50) NOT NULL,
  `no_hp` varchar(13) NOT NULL,
  `poin` int(11) NOT NULL,
  `status` enum('aktif','nonaktif') DEFAULT 'aktif',
  `status_reward` enum('tidak tersedia','tersedia','sudah digunakan') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pelanggan`
--

INSERT INTO `pelanggan` (`id_pelanggan`, `nama_pelanggan`, `no_hp`, `poin`, `status`, `status_reward`) VALUES
('PL012', 'Doan', '08776658', 1, 'aktif', 'tidak tersedia'),
('PL013', 'Nabeel', '0000000', 0, 'aktif', 'tidak tersedia'),
('PL014', 'Nayla', '0098765433', 1, 'aktif', 'tidak tersedia');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pemasok`
--

CREATE TABLE `pemasok` (
  `id_pemasok` varchar(25) NOT NULL,
  `nama_pemasok` varchar(25) NOT NULL,
  `no_telp` varchar(13) NOT NULL,
  `alamat` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `pengembalian`
--

CREATE TABLE `pengembalian` (
  `id_kembali` varchar(20) NOT NULL,
  `id_sewa` varchar(20) NOT NULL,
  `tgl_kembali` date NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `denda_keterlambatan` int(11) DEFAULT NULL,
  `total_denda` int(11) NOT NULL,
  `bayar` int(11) NOT NULL,
  `kembalian` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pengembalian`
--

INSERT INTO `pengembalian` (`id_kembali`, `id_sewa`, `tgl_kembali`, `status`, `denda_keterlambatan`, `total_denda`, `bayar`, `kembalian`) VALUES
('PM001', 'PN001', '2025-05-22', 'Tepat Waktu', 0, 30000, 30000, 0),
('PM002', 'PN002', '2025-05-25', 'Terlambat', 20000, 20000, 20000, 0),
('PM003', 'PN003', '2025-05-27', 'Tepat Waktu', 0, 80000, 100000, 20000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `pengguna`
--

CREATE TABLE `pengguna` (
  `id_pengguna` varchar(5) NOT NULL,
  `nama_lengkap` varchar(50) NOT NULL,
  `email` varchar(25) NOT NULL,
  `nama_pengguna` varchar(20) NOT NULL,
  `password` varchar(8) NOT NULL,
  `no_hp` varchar(13) NOT NULL,
  `alamat` text NOT NULL,
  `role` enum('Admin','Pegawai') NOT NULL,
  `rfid` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pengguna`
--

INSERT INTO `pengguna` (`id_pengguna`, `nama_lengkap`, `email`, `nama_pengguna`, `password`, `no_hp`, `alamat`, `role`, `rfid`) VALUES
('UR001', 'doan', 'doansri@gmail.com', 'doan', 'doan', '98765432', 'Bodo', 'Pegawai', '');

-- --------------------------------------------------------

--
-- Struktur dari tabel `penyewaan`
--

CREATE TABLE `penyewaan` (
  `id_sewa` varchar(20) NOT NULL,
  `id_pelanggan` varchar(20) NOT NULL,
  `id_pengguna` varchar(5) NOT NULL,
  `tgl_sewa` date NOT NULL,
  `tgl_rencana_kembali` date NOT NULL,
  `total_harga` int(11) NOT NULL,
  `bayar` int(11) NOT NULL,
  `kembalian` int(11) NOT NULL,
  `jaminan` enum('KTP','SIM','KTM','FC KK') NOT NULL,
  `Status` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `penyewaan`
--

INSERT INTO `penyewaan` (`id_sewa`, `id_pelanggan`, `id_pengguna`, `tgl_sewa`, `tgl_rencana_kembali`, `total_harga`, `bayar`, `kembalian`, `jaminan`, `Status`) VALUES
('PN001', 'PL012', 'UR001', '2025-05-22', '2025-05-31', 120000, 150000, 30000, 'SIM', 'Sudah Kembali'),
('PN002', 'PL013', 'UR001', '2025-05-22', '2025-05-23', 40000, 100000, 60000, 'KTP', 'Sudah Kembali'),
('PN003', 'PL014', 'UR001', '2025-05-26', '2025-05-30', 100000, 100000, 0, 'SIM', 'Sudah Kembali');

-- --------------------------------------------------------

--
-- Struktur dari tabel `stok_masuk`
--

CREATE TABLE `stok_masuk` (
  `id_stok_masuk` varchar(20) NOT NULL,
  `id_barang` varchar(20) NOT NULL,
  `kategori` varchar(20) NOT NULL,
  `qty` int(11) NOT NULL,
  `harga` int(11) NOT NULL,
  `id_pemasok` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id_barang`),
  ADD UNIQUE KEY `barcode` (`barcode`),
  ADD KEY `idx_barang_status` (`status`);

--
-- Indeks untuk tabel `barang_bermasalah`
--
ALTER TABLE `barang_bermasalah`
  ADD PRIMARY KEY (`id_brg_bermasalah`),
  ADD KEY `id_barang` (`id_barang`);

--
-- Indeks untuk tabel `detail_pengembalian`
--
ALTER TABLE `detail_pengembalian`
  ADD PRIMARY KEY (`id_detail_kembali`),
  ADD KEY `id_pengembalian` (`id_kembali`),
  ADD KEY `id_barang` (`id_barang`),
  ADD KEY `idx_detail_pengembalian` (`id_kembali`);

--
-- Indeks untuk tabel `detail_sewa`
--
ALTER TABLE `detail_sewa`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `item_id` (`id_barang`),
  ADD KEY `sewa_id` (`id_sewa`);

--
-- Indeks untuk tabel `pelanggan`
--
ALTER TABLE `pelanggan`
  ADD PRIMARY KEY (`id_pelanggan`);

--
-- Indeks untuk tabel `pemasok`
--
ALTER TABLE `pemasok`
  ADD PRIMARY KEY (`id_pemasok`);

--
-- Indeks untuk tabel `pengembalian`
--
ALTER TABLE `pengembalian`
  ADD PRIMARY KEY (`id_kembali`),
  ADD KEY `sewa_id` (`id_sewa`);

--
-- Indeks untuk tabel `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id_pengguna`),
  ADD UNIQUE KEY `rfid` (`rfid`);

--
-- Indeks untuk tabel `penyewaan`
--
ALTER TABLE `penyewaan`
  ADD PRIMARY KEY (`id_sewa`),
  ADD KEY `customer_id` (`id_pelanggan`),
  ADD KEY `user_id` (`id_pengguna`),
  ADD KEY `idx_penyewaan_pelanggan` (`id_pelanggan`);

--
-- Indeks untuk tabel `stok_masuk`
--
ALTER TABLE `stok_masuk`
  ADD PRIMARY KEY (`id_stok_masuk`),
  ADD KEY `id_barang` (`id_barang`),
  ADD KEY `id_pemasok` (`id_pemasok`);

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `barang_bermasalah`
--
ALTER TABLE `barang_bermasalah`
  ADD CONSTRAINT `relasi_barang_bermasalah` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`);

--
-- Ketidakleluasaan untuk tabel `detail_pengembalian`
--
ALTER TABLE `detail_pengembalian`
  ADD CONSTRAINT `join_barang` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`),
  ADD CONSTRAINT `join_pengembalian` FOREIGN KEY (`id_kembali`) REFERENCES `pengembalian` (`id_kembali`);

--
-- Ketidakleluasaan untuk tabel `detail_sewa`
--
ALTER TABLE `detail_sewa`
  ADD CONSTRAINT `detail_sewa_ibfk_1` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`),
  ADD CONSTRAINT `detail_sewa_ibfk_2` FOREIGN KEY (`id_sewa`) REFERENCES `penyewaan` (`id_sewa`);

--
-- Ketidakleluasaan untuk tabel `pemasok`
--
ALTER TABLE `pemasok`
  ADD CONSTRAINT `join_stok_masuk` FOREIGN KEY (`id_pemasok`) REFERENCES `stok_masuk` (`id_pemasok`);

--
-- Ketidakleluasaan untuk tabel `pengembalian`
--
ALTER TABLE `pengembalian`
  ADD CONSTRAINT `pengembalian_ibfk_1` FOREIGN KEY (`id_sewa`) REFERENCES `penyewaan` (`id_sewa`);

--
-- Ketidakleluasaan untuk tabel `penyewaan`
--
ALTER TABLE `penyewaan`
  ADD CONSTRAINT `penyewaan_ibfk_1` FOREIGN KEY (`id_pelanggan`) REFERENCES `pelanggan` (`id_pelanggan`),
  ADD CONSTRAINT `penyewaan_ibfk_2` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`);

--
-- Ketidakleluasaan untuk tabel `stok_masuk`
--
ALTER TABLE `stok_masuk`
  ADD CONSTRAINT `stok_masuk_ibfk_1` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
