-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 30, 2025 at 08:59 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

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
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `id_barang` varchar(20) NOT NULL,
  `nama_barang` varchar(30) NOT NULL,
  `harga_sewa` int(11) NOT NULL,
  `kategori` varchar(20) NOT NULL,
  `stok` int(11) NOT NULL,
  `status` enum('Tersedia','Disewa','Rusak','Hilang','Maintenance') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `detail_sewa`
--

CREATE TABLE `detail_sewa` (
  `id_detail` varchar(20) NOT NULL,
  `id_sewa` varchar(20) NOT NULL,
  `id_barang` varchar(20) NOT NULL,
  `qty` int(11) NOT NULL,
  `sub_total` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pelanggan`
--

CREATE TABLE `pelanggan` (
  `id_pelanggan` varchar(20) NOT NULL,
  `nama_pelanggan` varchar(50) NOT NULL,
  `no_hp` varchar(13) NOT NULL,
  `poin` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pengembalian`
--

CREATE TABLE `pengembalian` (
  `id_kembali` varchar(20) NOT NULL,
  `id_sewa` varchar(20) NOT NULL,
  `tgl_kembali` date NOT NULL,
  `status` enum('Tepat waktu','Terlambat','Tidak kembali') NOT NULL,
  `denda` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pengguna`
--

CREATE TABLE `pengguna` (
  `id_pengguna` varchar(5) NOT NULL,
  `nama_lengkap` varchar(50) NOT NULL,
  `email` varchar(15) NOT NULL,
  `nama_pengguna` varchar(20) NOT NULL,
  `password` varchar(8) NOT NULL,
  `no_hp` varchar(13) NOT NULL,
  `alamat` text NOT NULL,
  `role` enum('Admin','Pegawai') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `penyewaan`
--

CREATE TABLE `penyewaan` (
  `id_sewa` varchar(20) NOT NULL,
  `id_pelanggan` varchar(20) NOT NULL,
  `id_pengguna` varchar(5) NOT NULL,
  `tgl_sewa` date NOT NULL,
  `tgl_kembali` date NOT NULL,
  `total_harga` int(11) NOT NULL,
  `jaminan` enum('KTP','SIM','KTM') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stok_masuk`
--

CREATE TABLE `stok_masuk` (
  `id_stok_masuk` varchar(20) NOT NULL,
  `id_barang` varchar(20) NOT NULL,
  `qty` int(11) NOT NULL,
  `harga` int(11) NOT NULL,
  `pemasok` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id_barang`);

--
-- Indexes for table `detail_sewa`
--
ALTER TABLE `detail_sewa`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `item_id` (`id_barang`),
  ADD KEY `sewa_id` (`id_sewa`);

--
-- Indexes for table `pelanggan`
--
ALTER TABLE `pelanggan`
  ADD PRIMARY KEY (`id_pelanggan`);

--
-- Indexes for table `pengembalian`
--
ALTER TABLE `pengembalian`
  ADD PRIMARY KEY (`id_kembali`),
  ADD KEY `sewa_id` (`id_sewa`);

--
-- Indexes for table `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id_pengguna`);

--
-- Indexes for table `penyewaan`
--
ALTER TABLE `penyewaan`
  ADD PRIMARY KEY (`id_sewa`),
  ADD KEY `customer_id` (`id_pelanggan`),
  ADD KEY `user_id` (`id_pengguna`);

--
-- Indexes for table `stok_masuk`
--
ALTER TABLE `stok_masuk`
  ADD PRIMARY KEY (`id_stok_masuk`),
  ADD KEY `id_barang` (`id_barang`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_sewa`
--
ALTER TABLE `detail_sewa`
  ADD CONSTRAINT `detail_sewa_ibfk_1` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`),
  ADD CONSTRAINT `detail_sewa_ibfk_2` FOREIGN KEY (`id_sewa`) REFERENCES `penyewaan` (`id_sewa`);

--
-- Constraints for table `pengembalian`
--
ALTER TABLE `pengembalian`
  ADD CONSTRAINT `pengembalian_ibfk_1` FOREIGN KEY (`id_sewa`) REFERENCES `penyewaan` (`id_sewa`);

--
-- Constraints for table `penyewaan`
--
ALTER TABLE `penyewaan`
  ADD CONSTRAINT `penyewaan_ibfk_1` FOREIGN KEY (`id_pelanggan`) REFERENCES `pelanggan` (`id_pelanggan`),
  ADD CONSTRAINT `penyewaan_ibfk_2` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`);

--
-- Constraints for table `stok_masuk`
--
ALTER TABLE `stok_masuk`
  ADD CONSTRAINT `stok_masuk_ibfk_1` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
