
package backend;

import com.jgoodies.looks.BorderStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Frame;   
import java.io.File;
import javax.swing.SwingUtilities;
import java.time.temporal.ChronoUnit;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;





/**
 *
 * @author USER
 */
public class MenuLaporan extends javax.swing.JPanel {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    public MenuLaporan() {
        initComponents();
        initComponents();
        Koneksi DB = new Koneksi();
        DB.config();
        con = DB.con;
        
        label_username.setText(Login.Session.getUsername());
        
        cmb_jenis.addItem("Laporan Penyewaan");
        cmb_jenis.addItem("Laporan Pengembalian");
        cmb_jenis.addItem("Laporan Pendapatan");
        cmb_jenis.addItem("Laporan Barang Bermasalah");
        
        cmb_jenis.setOpaque(false);
        cmb_jenis.setBackground(new Color(0, 0, 0, 0));
        cmb_jenis.setForeground(Color.WHITE);
        cmb_jenis.setBorder(null);
     
    }

    private void tampilkanLaporanPenyewaan(String dari, String sampai, DefaultTableModel model) throws SQLException {
        model.setColumnIdentifiers(new Object[]{"No", "ID Sewa", "Nama Pelanggan", "Nama Barang", "Jumlah", "Harga Sewa", "Subtotal"});

        String sql = "SELECT p.id_sewa, pl.nama_pelanggan, b.nama_barang, d.qty, b.harga_sewa, (d.qty * b.harga_sewa) AS subtotal " +
                "FROM penyewaan p " +
                "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan " +
                "JOIN detail_sewa d ON p.id_sewa = d.id_sewa " +
                "JOIN barang b ON d.id_barang = b.id_barang " +
                "WHERE p.tgl_sewa BETWEEN ? AND ? " +
                "ORDER BY p.id_sewa";

        pst = con.prepareStatement(sql);
        pst.setString(1, dari);
        pst.setString(2, sampai);
        rs = pst.executeQuery();

        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                    no++,
                    rs.getString("id_sewa"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("nama_barang"),
                    rs.getInt("qty"),
                    "Rp" + rs.getInt("harga_sewa"),
                    "Rp" + rs.getInt("subtotal")
            });
        }

        table_laporan.setModel(model);

        table_laporan.getColumnModel().getColumn(0).setPreferredWidth(40);
        table_laporan.getColumnModel().getColumn(1).setPreferredWidth(80);
        table_laporan.getColumnModel().getColumn(2).setPreferredWidth(140);
        table_laporan.getColumnModel().getColumn(3).setPreferredWidth(140);
        table_laporan.getColumnModel().getColumn(4).setPreferredWidth(60);
        table_laporan.getColumnModel().getColumn(5).setPreferredWidth(100);
        table_laporan.getColumnModel().getColumn(6).setPreferredWidth(120);
    }

    private void tampilkanLaporanPengembalian(String dari, String sampai, DefaultTableModel model) throws SQLException {
        model.setColumnIdentifiers(new Object[]{
            "No", "ID Kembali", "Nama Pelanggan", "Nama Barang", 
            "Jumlah Kembali", "Kondisi", "Denda Barang", "Total Denda"
        });

        String sql = "SELECT k.id_kembali, pl.nama_pelanggan, b.nama_barang, d.jumlah AS jumlah_kembali, " +
                     "d.kondisi, d.denda_barang, k.denda_keterlambatan " +
                     "FROM pengembalian k " +
                     "JOIN penyewaan p ON k.id_sewa = p.id_sewa " +
                     "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan " +
                     "JOIN detail_pengembalian d ON k.id_kembali = d.id_kembali " +
                     "JOIN barang b ON d.id_barang = b.id_barang " +
                     "WHERE k.tgl_kembali BETWEEN ? AND ? " +
                     "ORDER BY k.id_kembali";

        pst = con.prepareStatement(sql);
        pst.setString(1, dari);
        pst.setString(2, sampai);
        rs = pst.executeQuery();

        int no = 1;
        while (rs.next()) {
            int dendaBarang = rs.getInt("denda_barang");
            int dendaKeterlambatan = rs.getInt("denda_keterlambatan");
            int totalDenda = dendaBarang + dendaKeterlambatan;

            model.addRow(new Object[]{
                no++,
                rs.getString("id_kembali"),
                rs.getString("nama_pelanggan"),
                rs.getString("nama_barang"),
                rs.getInt("jumlah_kembali"),
                rs.getString("kondisi"),
                "Rp" + dendaBarang,
                "Rp" + totalDenda
            });
        }

        table_laporan.setModel(model);

        table_laporan.getColumnModel().getColumn(0).setPreferredWidth(40);
        table_laporan.getColumnModel().getColumn(1).setPreferredWidth(80);
        table_laporan.getColumnModel().getColumn(2).setPreferredWidth(140);
        table_laporan.getColumnModel().getColumn(3).setPreferredWidth(140);
        table_laporan.getColumnModel().getColumn(4).setPreferredWidth(80);
        table_laporan.getColumnModel().getColumn(5).setPreferredWidth(100);
        table_laporan.getColumnModel().getColumn(6).setPreferredWidth(140);
        table_laporan.getColumnModel().getColumn(7).setPreferredWidth(120);
    }

    private void tampilkanPendapatanPenyewaan(String dari, String sampai, DefaultTableModel model) throws SQLException {
        model.setColumnIdentifiers(new Object[]{
            "No", "ID Sewa", "Nama Pelanggan", "Tanggal Sewa", "Jumlah Item", 
            "Total Bayar", "Nama Pegawai", "Durasi Sewa"
        });

        String sql = "SELECT p.id_sewa, pl.nama_pelanggan, p.tgl_sewa, p.tgl_rencana_kembali, " +
                     "SUM(ds.qty) AS jumlah_item, p.bayar, u.nama_pengguna " +
                     "FROM penyewaan p " +
                     "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan " +
                     "JOIN detail_sewa ds ON p.id_sewa = ds.id_sewa " +
                     "JOIN pengguna u ON p.id_pengguna = u.id_pengguna " +
                     "WHERE p.tgl_sewa BETWEEN ? AND ? " +
                     "GROUP BY p.id_sewa";

        pst = con.prepareStatement(sql);
        pst.setString(1, dari);
        pst.setString(2, sampai);
        rs = pst.executeQuery();

        int no = 1;
        int totalPendapatan = 0;

        while (rs.next()) {
            String tglSewa = rs.getString("tgl_sewa");
            String tglKembali = rs.getString("tgl_rencana_kembali");

            long durasi = 0;
            try {
                LocalDate start = LocalDate.parse(tglSewa);
                LocalDate end = LocalDate.parse(tglKembali);
                durasi = ChronoUnit.DAYS.between(start, end);
            } catch (Exception e) {
            }

            int bayar = rs.getInt("bayar");
            totalPendapatan += bayar;

            model.addRow(new Object[]{
                no++,
                rs.getString("id_sewa"),
                rs.getString("nama_pelanggan"),
                tglSewa,
                rs.getInt("jumlah_item"),
                "Rp" + bayar,
                rs.getString("nama_pengguna"),
                durasi + " hari"
            });
        }

        table_laporan.setModel(model);

        table_laporan.getColumnModel().getColumn(0).setPreferredWidth(40);
        table_laporan.getColumnModel().getColumn(1).setPreferredWidth(80);
        table_laporan.getColumnModel().getColumn(2).setPreferredWidth(140);
        table_laporan.getColumnModel().getColumn(3).setPreferredWidth(100);
        table_laporan.getColumnModel().getColumn(4).setPreferredWidth(90);
        table_laporan.getColumnModel().getColumn(5).setPreferredWidth(110);
        table_laporan.getColumnModel().getColumn(6).setPreferredWidth(120);
        table_laporan.getColumnModel().getColumn(7).setPreferredWidth(110);

        // Tampilkan total ke txt_total_pendapatan
        txt_total_pendapatan.setText("Rp" + totalPendapatan);
    }

    private void tampilkanPendapatanPengembalian(String dari, String sampai, DefaultTableModel model) throws SQLException {
    model.setColumnIdentifiers(new Object[]{
        "No", "ID Kembali", "Nama Penyewa", "Tanggal Kembali", 
        "Denda Keterlambatan", "Denda Barang", 
        "Total Bayar", "Nama Pegawai"
    });

    String sql = "SELECT k.id_kembali, pl.nama_pelanggan, k.tgl_kembali, k.denda_keterlambatan, k.bayar, u.nama_pengguna, " +
                 "COALESCE(SUM(dk.denda_barang), 0) AS denda_barang " +
                 "FROM pengembalian k " +
                 "JOIN penyewaan p ON k.id_sewa = p.id_sewa " +
                 "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan " +
                 "JOIN pengguna u ON p.id_pengguna = u.id_pengguna " +  // <- fix di sini
                 "LEFT JOIN detail_pengembalian dk ON k.id_kembali = dk.id_kembali " +
                 "WHERE k.tgl_kembali BETWEEN ? AND ? " +
                 "GROUP BY k.id_kembali, pl.nama_pelanggan, k.tgl_kembali, k.denda_keterlambatan, k.bayar, u.nama_pengguna";

    pst = con.prepareStatement(sql);
    pst.setString(1, dari);
    pst.setString(2, sampai);
    rs = pst.executeQuery();

    int no = 1;
    int totalPendapatan = 0;

    while (rs.next()) {
        int keterlambatan = rs.getInt("denda_keterlambatan");
        int dendaBarang = rs.getInt("denda_barang");
        int totalBayar = rs.getInt("bayar");

        totalPendapatan += totalBayar;

        model.addRow(new Object[]{
            no++,
            rs.getString("id_kembali"),
            rs.getString("nama_pelanggan"),
            rs.getString("tgl_kembali"),
            "Rp" + keterlambatan,
            "Rp" + dendaBarang,
            "Rp" + totalBayar,
            rs.getString("nama_pengguna")
        });
    }

    table_laporan.setModel(model);

    table_laporan.getColumnModel().getColumn(0).setPreferredWidth(40);
    table_laporan.getColumnModel().getColumn(1).setPreferredWidth(90);
    table_laporan.getColumnModel().getColumn(2).setPreferredWidth(140);
    table_laporan.getColumnModel().getColumn(3).setPreferredWidth(100);
    table_laporan.getColumnModel().getColumn(4).setPreferredWidth(140);
    table_laporan.getColumnModel().getColumn(5).setPreferredWidth(180);
    table_laporan.getColumnModel().getColumn(6).setPreferredWidth(120);
    table_laporan.getColumnModel().getColumn(7).setPreferredWidth(140);

    txt_total_pendapatan.setText("Rp" + totalPendapatan);
}

    private void tampilkanLaporanBarangBermasalah(String dari, String sampai, DefaultTableModel model) throws SQLException {
        model.setColumnIdentifiers(new Object[]{
            "No", "ID Kembali", "Tanggal Kembali", "Nama Pelanggan", 
            "Nama Barang", "Jumlah", "Kondisi", "Denda Barang"
        });

        String sql = "SELECT k.id_kembali, k.tgl_kembali, pl.nama_pelanggan, b.nama_barang, " +
                     "d.jumlah, d.kondisi, d.denda_barang " +
                     "FROM pengembalian k " +
                     "JOIN penyewaan p ON k.id_sewa = p.id_sewa " +
                     "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan " +
                     "JOIN detail_pengembalian d ON k.id_kembali = d.id_kembali " +
                     "JOIN barang b ON d.id_barang = b.id_barang " +
                     "WHERE k.tgl_kembali BETWEEN ? AND ? " +
                     "AND (LOWER(d.kondisi) = 'rusak' OR LOWER(d.kondisi) = 'hilang') " +
                     "ORDER BY k.tgl_kembali";

        pst = con.prepareStatement(sql);
        pst.setString(1, dari);
        pst.setString(2, sampai);
        rs = pst.executeQuery();

        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                no++,
                rs.getString("id_kembali"),
                rs.getString("tgl_kembali"),
                rs.getString("nama_pelanggan"),
                rs.getString("nama_barang"),
                rs.getInt("jumlah"),
                rs.getString("kondisi"),
                "Rp" + rs.getInt("denda_barang")
            });
        }

        table_laporan.setModel(model);

        table_laporan.getColumnModel().getColumn(0).setPreferredWidth(40); 
        table_laporan.getColumnModel().getColumn(1).setPreferredWidth(90);  
        table_laporan.getColumnModel().getColumn(2).setPreferredWidth(100);  
        table_laporan.getColumnModel().getColumn(3).setPreferredWidth(140);  
        table_laporan.getColumnModel().getColumn(4).setPreferredWidth(140);  
        table_laporan.getColumnModel().getColumn(5).setPreferredWidth(60);  
        table_laporan.getColumnModel().getColumn(6).setPreferredWidth(80);  
        table_laporan.getColumnModel().getColumn(7).setPreferredWidth(120);  
    }

//    private void exportToExcel(JTable table, String jenisLaporan, String periode) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Data Laporan");
//        TableModel model = table.getModel();
//
//        // Font
//        Font font = workbook.createFont();
//        font.setFontName("Calibri");
//        font.setFontHeightInPoints((short) 11);
//
//        Font titleFont = workbook.createFont();
//        titleFont.setFontName("Calibri");
//        titleFont.setFontHeightInPoints((short) 14);
//        titleFont.setBold(true);
//
//        // Styles
//        CellStyle titleStyle = workbook.createCellStyle();
//        titleStyle.setFont(titleFont);
//        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//
//        CellStyle subTitleStyle = workbook.createCellStyle();
//        subTitleStyle.setFont(font);
//        subTitleStyle.setAlignment(HorizontalAlignment.CENTER);
//
//        CellStyle headerStyle = workbook.createCellStyle();
//        headerStyle.setFont(font);
//        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        headerStyle.setBorderBottom(BorderStyle.THIN);
//        headerStyle.setBorderTop(BorderStyle.THIN);
//        headerStyle.setBorderLeft(BorderStyle.THIN);
//        headerStyle.setBorderRight(BorderStyle.THIN);
//        headerStyle.setAlignment(HorizontalAlignment.CENTER);
//
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setFont(font);
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setBorderTop(BorderStyle.THIN);
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setBorderRight(BorderStyle.THIN);
//        cellStyle.setAlignment(HorizontalAlignment.LEFT);
//
//        CellStyle numberStyle = workbook.createCellStyle();
//        numberStyle.cloneStyleFrom(cellStyle);
//        numberStyle.setAlignment(HorizontalAlignment.RIGHT);
//
//        // Judul Laporan
//        Row titleRow = sheet.createRow(0);
//        Cell titleCell = titleRow.createCell(0);
//        titleCell.setCellValue("LAPORAN " + jenisLaporan.toUpperCase());
//        titleCell.setCellStyle(titleStyle);
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, model.getColumnCount() - 1));
//
//        // Periode
//        Row periodeRow = sheet.createRow(1);
//        Cell periodeCell = periodeRow.createCell(0);
//        periodeCell.setCellValue("Periode: " + periode);
//        periodeCell.setCellStyle(subTitleStyle);
//        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, model.getColumnCount() - 1));
//
//        // Header
//        Row headerRow = sheet.createRow(3);
//        for (int col = 0; col < model.getColumnCount(); col++) {
//            Cell cell = headerRow.createCell(col);
//            cell.setCellValue(model.getColumnName(col));
//            cell.setCellStyle(headerStyle);
//        }
//
//        // Data
//        for (int row = 0; row < model.getRowCount(); row++) {
//            Row excelRow = sheet.createRow(row + 4);
//            for (int col = 0; col < model.getColumnCount(); col++) {
//                Cell cell = excelRow.createCell(col);
//                Object value = model.getValueAt(row, col);
//                String val = value != null ? value.toString() : "";
//
//                if (val.startsWith("Rp") || val.matches("\\d+")) {
//                    cell.setCellStyle(numberStyle);
//                } else {
//                    cell.setCellStyle(cellStyle);
//                }
//
//                cell.setCellValue(val);
//            }
//        }
//
//        // Auto-size kolom
//        for (int col = 0; col < model.getColumnCount(); col++) {
//            sheet.autoSizeColumn(col);
//        }
//
//        // Simpan
//        String tanggal = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String namaFile = "Laporan_" + jenisLaporan.replaceAll("\\s+", "_") + "_" + tanggal + ".xlsx";
//
//        JFileChooser chooser = new JFileChooser();
//        chooser.setSelectedFile(new File(namaFile));
//        int pilih = chooser.showSaveDialog(null);
//
//        if (pilih == JFileChooser.APPROVE_OPTION) {
//            try (FileOutputStream out = new FileOutputStream(chooser.getSelectedFile())) {
//                workbook.write(out);
//                workbook.close();
//                JOptionPane.showMessageDialog(null, "Berhasil export ke Excel!");
//            }
//        } else {
//            workbook.close();
//            JOptionPane.showMessageDialog(null, "Export dibatalkan.");
//        }
//    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        page_laporan = new javax.swing.JPanel();
        txt_total_pendapatan = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txt_tgl1 = new com.toedter.calendar.JDateChooser();
        txt_tgl2 = new com.toedter.calendar.JDateChooser();
        cmb_jenis = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_laporan = new custom.JTable_custom();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_export = new javax.swing.JButton();
        btn_tampil = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_username = new javax.swing.JLabel();

        page_laporan.setBackground(new java.awt.Color(255, 244, 232));
        page_laporan.setForeground(new java.awt.Color(230, 230, 230));
        page_laporan.setPreferredSize(new java.awt.Dimension(836, 666));
        page_laporan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_total_pendapatan.setBorder(null);
        page_laporan.add(txt_total_pendapatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 595, 120, 24));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Group 133.png"))); // NOI18N
        page_laporan.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 590, 140, 40));
        page_laporan.add(txt_tgl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 162, 130, 30));
        page_laporan.add(txt_tgl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(275, 162, 130, 30));

        cmb_jenis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_jenisActionPerformed(evt);
            }
        });
        page_laporan.add(cmb_jenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(252, 113, 190, 28));

        table_laporan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "", "", "", "", "", "", "", ""
            }
        ));
        jScrollPane2.setViewportView(table_laporan);

        page_laporan.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 220, 620, 360));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Laporan.png"))); // NOI18N
        page_laporan.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 190, 37));

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_laporan.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Group 133.png"))); // NOI18N
        page_laporan.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 140, 40));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Group 133.png"))); // NOI18N
        page_laporan.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 160, 140, 40));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Group 135.png"))); // NOI18N
        page_laporan.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 110, 220, 40));

        jLabel2.setFont(new java.awt.Font("Candara", 1, 14)); // NOI18N
        jLabel2.setText("Total Pendapatan :");
        page_laporan.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 600, 120, -1));

        btn_tampil.setContentAreaFilled(false);

        btn_tampil.setBorderPainted(false);
        btn_export.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Button Export.png"))); // NOI18N
        btn_export.setBorder(null);
        btn_export.setContentAreaFilled(false);
        btn_export.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Button Export Select.png"))); // NOI18N
        btn_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exportActionPerformed(evt);
            }
        });
        page_laporan.add(btn_export, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 160, -1, -1));

        btn_tampil.setContentAreaFilled(false);

        btn_tampil.setBorderPainted(false);
        btn_tampil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Button Tampilkan.png"))); // NOI18N
        btn_tampil.setBorder(null);
        btn_tampil.setContentAreaFilled(false);
        btn_tampil.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Button Tampilkan Select.png"))); // NOI18N
        btn_tampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilActionPerformed(evt);
            }
        });
        page_laporan.add(btn_tampil, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 161, -1, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/laporan/Form Laporan.png"))); // NOI18N
        page_laporan.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 670, 550));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_laporan.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        label_username.setText("Username");
        page_laporan.add(label_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 836, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(page_laporan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 666, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(page_laporan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilActionPerformed

        String jenis = cmb_jenis.getSelectedItem() == null ? "" : cmb_jenis.getSelectedItem().toString();

        if (jenis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih jenis laporan terlebih dahulu!");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date tgl1 = txt_tgl1.getDate();
        Date tgl2 = txt_tgl2.getDate();

        if (tgl1 == null || tgl2 == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih tanggal awal dan akhir!");
            return;
        }

        String dari = sdf.format(tgl1);
        String sampai = sdf.format(tgl2);

        DefaultTableModel model = (DefaultTableModel) table_laporan.getModel();
        model.setRowCount(0);

        try {
            if (jenis.equals("Laporan Penyewaan")) {
                tampilkanLaporanPenyewaan(dari, sampai, model);
            } else if (jenis.equals("Laporan Pengembalian")) {
                tampilkanLaporanPengembalian(dari, sampai, model);
            } else if (jenis.equals("Laporan Pendapatan")) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                DialogPilihPendapatan dialog = new DialogPilihPendapatan(parentFrame);
                dialog.setVisible(true);
                int pilihan = dialog.getPilihan();

                if (pilihan == 0) {
                    tampilkanPendapatanPenyewaan(dari, sampai, model);
                } else if (pilihan == 1) {
                    tampilkanPendapatanPengembalian(dari, sampai, model);
                } else {
                    return;
                }
            }else if (jenis.equals("Laporan Barang Bermasalah")) {
                tampilkanLaporanBarangBermasalah(dari, sampai, model);
            } else {
                JOptionPane.showMessageDialog(this, "Laporan belum tersedia.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal ambil data laporan: " + e.getMessage());
        }

    }//GEN-LAST:event_btn_tampilActionPerformed

    private void cmb_jenisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_jenisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_jenisActionPerformed

    private void btn_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exportActionPerformed
//       String jenis = cmb_jenis.getSelectedItem() == null ? "" : cmb_jenis.getSelectedItem().toString();
//        if (jenis.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Silakan pilih jenis laporan terlebih dahulu!");
//            return;
//        }
//
//        if (txt_tgl1.getDate() == null || txt_tgl2.getDate() == null) {
//            JOptionPane.showMessageDialog(this, "Silakan pilih tanggal awal dan akhir!");
//            return;
//        }
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
//        String periode = sdf.format(txt_tgl1.getDate()) + " - " + sdf.format(txt_tgl2.getDate());
//
//        try {
//            exportToExcel(table_laporan, jenis, periode);
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(this, "Gagal export ke Excel: " + ex.getMessage());
//        }
    }//GEN-LAST:event_btn_exportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_export;
    private javax.swing.JButton btn_tampil;
    private javax.swing.JComboBox<String> cmb_jenis;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_username;
    private javax.swing.JPanel page_laporan;
    private custom.JTable_custom table_laporan;
    private com.toedter.calendar.JDateChooser txt_tgl1;
    private com.toedter.calendar.JDateChooser txt_tgl2;
    private javax.swing.JTextField txt_total_pendapatan;
    // End of variables declaration//GEN-END:variables
}
