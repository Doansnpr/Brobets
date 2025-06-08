
package backend;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

public class MenuDashPeg extends javax.swing.JPanel {
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    public MenuDashPeg() {
        initComponents();
        Koneksi DB = new Koneksi();
        DB.config();
        con = DB.con;
        
        comboGrafik.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            switch (comboGrafik.getSelectedItem().toString()) {
                case "Pendapatan Bulanan" -> tampilkanGrafikPendapatanBulanan();
                case "Pendapatan Tahunan" -> tampilkanGrafikPendapatanTahunan();
                case "Total Denda" -> tampilkanGrafikDenda();
                case "Barang Bermasalah" -> tampilkanGrafikBarangBermasalah();
            }
        }
    });

        
        Color backgroundWarna = new Color(255, 244, 232);
        panelGrafik.setBackground(backgroundWarna);
        panelGrafik.setOpaque(true);
        
        label_username.setText(Login.Session.getUsername());
        configureDashboard(); 
        tampilkanGrafikPendapatanBulanan();
    }
    
    public void setUsername(String namaUser) {
        label_username.setText(namaUser); 
    }
    
    
    private void configureDashboard() {
        try {
            loadJumlahPelanggan();
            loadJumlahStokItem();
            loadJumlahPenyewaan();
            loadJumlahPengembalian();
            loadJumlahStokMasuk();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void tampilkanGrafikDariQuery(String sql, DefaultCategoryDataset dataset, String judul, String labelX, String labelY) {
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String kategori = rs.getString(1);
                int nilai = rs.getInt(2);
                dataset.addValue(nilai, judul, kategori);
            }

            JFreeChart chart = ChartFactory.createLineChart(judul, labelX, labelY, dataset);
            chart.setBackgroundPaint(new Color(0, 0, 0, 0));
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(new Color(0, 0, 0, 0));
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, new Color(52, 152, 219));
            renderer.setSeriesStroke(0, new BasicStroke(2.5f));
            plot.setRenderer(renderer);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setOpaque(false);
            chartPanel.setBackground(new Color(0, 0, 0, 0));
            chartPanel.setPreferredSize(panelGrafik.getSize());

            panelGrafik.removeAll();
            panelGrafik.setLayout(new BorderLayout());
            panelGrafik.add(chartPanel, BorderLayout.CENTER);
            panelGrafik.revalidate();
            panelGrafik.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
  
    private void tampilkanGrafikPendapatanBulanan() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = """
            SELECT 
                DATE_FORMAT(tgl, '%b') AS bulan,
                SUM(total_bayar) AS pendapatan
            FROM (
                SELECT tgl_sewa AS tgl, bayar AS total_bayar FROM penyewaan
                UNION ALL
                SELECT tgl_kembali AS tgl, bayar AS total_bayar FROM pengembalian
            ) AS gabungan
            GROUP BY bulan
            ORDER BY STR_TO_DATE(bulan, '%b')
        """;

        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String bulan = rs.getString("bulan");
                int pendapatan = rs.getInt("pendapatan");
                dataset.addValue(pendapatan, "Pendapatan", bulan);
            }

            JFreeChart chart = ChartFactory.createLineChart(
                "Grafik Pendapatan Bulanan",
                "Bulan",
                "Pendapatan (Rp)",
                dataset);

            chart.setBackgroundPaint(new Color(0, 0, 0, 0));
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(new Color(0, 0, 0, 0));
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, new Color(52, 152, 219)); // biru modern
            renderer.setSeriesStroke(0, new BasicStroke(2.5f));
            plot.setRenderer(renderer);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setOpaque(false);
            chartPanel.setBackground(new Color(0, 0, 0, 0));
            chartPanel.setPreferredSize(panelGrafik.getSize());

            panelGrafik.removeAll();
            panelGrafik.setLayout(new BorderLayout());
            panelGrafik.add(chartPanel, BorderLayout.CENTER);
            panelGrafik.revalidate();
            panelGrafik.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat grafik bulanan: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void tampilkanGrafikPendapatanTahunan() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = """
            SELECT 
                YEAR(tgl) AS tahun,
                SUM(total_bayar) AS pendapatan
            FROM (
                SELECT tgl_sewa AS tgl, bayar AS total_bayar FROM penyewaan
                UNION ALL
                SELECT tgl_kembali AS tgl, bayar AS total_bayar FROM pengembalian
            ) AS gabungan
            GROUP BY tahun
            ORDER BY tahun
        """;

        tampilkanGrafikDariQuery(sql, dataset, "Pendapatan Tahunan", "Tahun", "Pendapatan (Rp)");
    }
    
    private void tampilkanGrafikDenda() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    String sql = """
        SELECT 
            DATE_FORMAT(tgl_kembali, '%b') AS bulan,
            SUM(total_denda) AS total_denda
        FROM pengembalian
        GROUP BY bulan
        ORDER BY STR_TO_DATE(bulan, '%b')
    """;

    tampilkanGrafikDariQuery(sql, dataset, "Total Denda per Bulan", "Bulan", "Denda (Rp)");
}

    private void tampilkanGrafikBarangBermasalah() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    String sql = """
        SELECT kondisi, COUNT(*) AS jumlah
        FROM detail_pengembalian
        WHERE kondisi IN ('Rusak', 'Hilang')
        GROUP BY kondisi
    """;

    tampilkanGrafikDariQuery(sql, dataset, "Barang Bermasalah", "Kondisi", "Jumlah Barang");
}


    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        page_dashpeg = new javax.swing.JPanel();
        card_pelanggan = new custom.panel2_custom();
        pelanggan = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        card_items = new custom.panel2_custom();
        stok = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        card_penyewaan = new custom.panel2_custom();
        penyewaan = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        card_pengembalian = new custom.panel2_custom();
        pengembalian = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        card_stok = new custom.panel2_custom();
        stok_masuk = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_username = new javax.swing.JLabel();
        panelGrafik = new javax.swing.JPanel();
        comboGrafik = new javax.swing.JComboBox<>();

        setLayout(new java.awt.CardLayout());

        page_dashpeg.setBackground(new java.awt.Color(255, 244, 232));
        page_dashpeg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        card_pelanggan.setBackground(new java.awt.Color(255, 244, 232));
        card_pelanggan.setRoundBottomLeft(10);
        card_pelanggan.setRoundBottomRight(10);
        card_pelanggan.setRoundTopLeft(10);
        card_pelanggan.setRoundTopRight(10);
        card_pelanggan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        card_pelanggan.add(pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 130, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Line 232.png"))); // NOI18N
        card_pelanggan.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 240, 10));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Vector.png"))); // NOI18N
        card_pelanggan.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, 30, 40));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Ellipse 13.png"))); // NOI18N
        card_pelanggan.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 50, 60));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/PELANGGAN.png"))); // NOI18N
        card_pelanggan.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, 20));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Rectangle 132.png"))); // NOI18N
        card_pelanggan.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        page_dashpeg.add(card_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 91, -1, -1));

        card_items.setBackground(new java.awt.Color(255, 244, 232));
        card_items.setRoundBottomLeft(10);
        card_items.setRoundBottomRight(10);
        card_items.setRoundTopLeft(10);
        card_items.setRoundTopRight(10);
        card_items.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        card_items.add(stok, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 130, 30));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Line 232.png"))); // NOI18N
        card_items.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 240, 10));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Vector2.png"))); // NOI18N
        card_items.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, 30, 40));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Ellipse 13.png"))); // NOI18N
        card_items.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 50, 60));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/ITEMS.png"))); // NOI18N
        card_items.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, 20));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Rectangle 132.png"))); // NOI18N
        card_items.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        page_dashpeg.add(card_items, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 91, -1, -1));

        card_penyewaan.setBackground(new java.awt.Color(255, 244, 232));
        card_penyewaan.setRoundBottomLeft(10);
        card_penyewaan.setRoundBottomRight(10);
        card_penyewaan.setRoundTopLeft(10);
        card_penyewaan.setRoundTopRight(10);
        card_penyewaan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        card_penyewaan.add(penyewaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 130, 30));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Line 232.png"))); // NOI18N
        card_penyewaan.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 240, 10));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Vector-2.png"))); // NOI18N
        card_penyewaan.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, 30, 40));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Ellipse 13.png"))); // NOI18N
        card_penyewaan.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 50, 60));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/PENYEWAAN.png"))); // NOI18N
        card_penyewaan.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, 20));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Rectangle 132.png"))); // NOI18N
        card_penyewaan.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        page_dashpeg.add(card_penyewaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 91, -1, -1));

        card_pengembalian.setBackground(new java.awt.Color(255, 244, 232));
        card_pengembalian.setRoundBottomLeft(10);
        card_pengembalian.setRoundBottomRight(10);
        card_pengembalian.setRoundTopLeft(10);
        card_pengembalian.setRoundTopRight(10);
        card_pengembalian.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        card_pengembalian.add(pengembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 130, 30));

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Line 232.png"))); // NOI18N
        card_pengembalian.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 240, 10));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Vector3.png"))); // NOI18N
        card_pengembalian.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, 40, 40));

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Ellipse 13.png"))); // NOI18N
        card_pengembalian.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 50, 60));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/PENGEMBALIAN.png"))); // NOI18N
        card_pengembalian.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, 20));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Rectangle 132.png"))); // NOI18N
        card_pengembalian.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 120));

        page_dashpeg.add(card_pengembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 222, -1, -1));

        card_stok.setBackground(new java.awt.Color(255, 244, 232));
        card_stok.setRoundBottomLeft(10);
        card_stok.setRoundBottomRight(10);
        card_stok.setRoundTopLeft(10);
        card_stok.setRoundTopRight(10);
        card_stok.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        card_stok.add(stok_masuk, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 130, 30));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Line 232.png"))); // NOI18N
        card_stok.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 240, 10));

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Vector-1.png"))); // NOI18N
        card_stok.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, 40, 40));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Ellipse 13.png"))); // NOI18N
        card_stok.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 50, 60));

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/STOK MASUK.png"))); // NOI18N
        card_stok.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, 20));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Rectangle 132.png"))); // NOI18N
        card_stok.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 120));

        page_dashpeg.add(card_stok, new org.netbeans.lib.awtextra.AbsoluteConstraints(432, 222, -1, -1));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 76.png"))); // NOI18N
        page_dashpeg.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 312, 37));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_dashpeg.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_dashpeg.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        label_username.setText("Username");
        page_dashpeg.add(label_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        panelGrafik.setBackground(new java.awt.Color(255, 244, 232));
        page_dashpeg.add(panelGrafik, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 400, 630, 240));

        comboGrafik.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendapatan Bulanan", "Pendapatan Tahunan", "Total Denda", "Barang Bermasalah" }));
        page_dashpeg.add(comboGrafik, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 352, 160, 30));

        add(page_dashpeg, "card2");
    }// </editor-fold>//GEN-END:initComponents

private void loadJumlahPelanggan() throws SQLException {
        String query = "SELECT COUNT(*) AS jumlah_pelanggan FROM pelanggan WHERE status = 'Aktif'";
        setLabelData(query, pelanggan);
    }
    
    private void loadJumlahStokItem() throws SQLException {
        String query = "SELECT SUM(stok) AS total_stok FROM barang";
        setLabelData(query, stok);
    }
    
    private void loadJumlahPenyewaan() throws SQLException {
        String query = "SELECT COUNT(*) AS jumlah_penyewaan FROM penyewaan";
        setLabelData(query, penyewaan);
    }
    
    private void loadJumlahPengembalian() throws SQLException {
        String query = "SELECT COUNT(*) AS jumlah_pengembalian FROM pengembalian";
        setLabelData(query, pengembalian);
    }
    
    private void loadJumlahStokMasuk() throws SQLException {
        String query = "SELECT COUNT(*) AS jumlah_stok_masuk FROM stok_masuk";
        setLabelData(query, stok_masuk);
    }
    
    private void setLabelData(String query, JLabel label) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/brobets";
        String userDb = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, userDb, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int jumlah = rs.getInt(1);
                label.setText(String.valueOf(jumlah));
            } else {
                label.setText("0");
            }

            label.setForeground(Color.WHITE); 
            label.setFont(new Font("Arial", Font.PLAIN, 20));
            label.setHorizontalAlignment(SwingConstants.LEFT);
        }
    }
    
    
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private custom.panel2_custom card_items;
    private custom.panel2_custom card_pelanggan;
    private custom.panel2_custom card_pengembalian;
    private custom.panel2_custom card_penyewaan;
    private custom.panel2_custom card_stok;
    private javax.swing.JComboBox<String> comboGrafik;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel label_username;
    private javax.swing.JPanel page_dashpeg;
    private javax.swing.JPanel panelGrafik;
    private javax.swing.JLabel pelanggan;
    private javax.swing.JLabel pengembalian;
    private javax.swing.JLabel penyewaan;
    private javax.swing.JLabel stok;
    private javax.swing.JLabel stok_masuk;
    // End of variables declaration//GEN-END:variables
}
