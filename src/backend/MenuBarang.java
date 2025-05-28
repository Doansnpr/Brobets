/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package backend;

import static backend.Koneksi.con;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import backend.FormatHarga;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
/**
 *
 * @author Khafila Maulidiyah W
 */
public class MenuBarang extends javax.swing.JPanel {

     Connection con;
    PreparedStatement pst;
    ResultSet rs;

    private String selectedStatus = "Tersedia";
    private String selectedkategori = "Tenda";
    
    public MenuBarang() {
        initComponents();
        Koneksi DB = new Koneksi();
        DB.config();
        con = DB.con;

        txt_search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                cariBarang();
                cariRusak();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                cariBarang();
                cariRusak();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                cariBarang();
                cariRusak();
            }
        });
        
        txt_search1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                cariRusak();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                cariRusak();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                cariRusak();
            }
        });
        
        load_table();
        ((AbstractDocument) txt_harga.getDocument()).setDocumentFilter(new FormatHarga());
        ((AbstractDocument) txt_beli.getDocument()).setDocumentFilter(new FormatHarga());
        ((AbstractDocument) txt_harga1.getDocument()).setDocumentFilter(new FormatHarga());
        ((AbstractDocument) txt_beli1.getDocument()).setDocumentFilter(new FormatHarga());
        ((AbstractDocument) txt_stok.getDocument()).setDocumentFilter(new filter());
        ((AbstractDocument) txt_stok1.getDocument()).setDocumentFilter(new filter());

        MenuBarang.EnumComboBoxLoader.loadEnumFromDatabase(con, cmb_status);
        populateStatusComboBox();
        MenuBarang.EnumComboBoxKat.loadEnumFromDatabase(con, cmb_kategori);
        populateKategoriComboBox();

        // Buat combobox transparan
        cmb_status.setOpaque(false);
        cmb_status.setBackground(new Color(0, 0, 0, 0));
        cmb_status.setForeground(Color.WHITE);
        cmb_status.setBorder(null);

        //cmb_status1 transparan
        cmb_status1.setOpaque(false);
        cmb_status1.setBackground(new Color(0, 0, 0, 0));
        cmb_status1.setForeground(Color.WHITE);
        cmb_status1.setBorder(null);

        //buat dropdown transparan
        cmb_status.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent) {
                    ((JComponent) c).setOpaque(false);
                }
                return c;
            }
        });

        //buat dropdown cmb_status1 transparan
        cmb_status1.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent) {
                    ((JComponent) c).setOpaque(false);
                }
                return c;
            }
        });

        // Buat combobox transparan
        cmb_kategori.setOpaque(false);
        cmb_kategori.setBackground(new Color(0, 0, 0, 0));
        cmb_kategori.setForeground(Color.WHITE);
        cmb_kategori.setBorder(null);

        //cmb_status1 transparan
        cmb_kategori1.setOpaque(false);
        cmb_kategori1.setBackground(new Color(0, 0, 0, 0));
        cmb_kategori1.setForeground(Color.WHITE);
        cmb_kategori1.setBorder(null);

        //buat dropdown transparan
        cmb_kategori.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent) {
                    ((JComponent) c).setOpaque(false);
                }
                return c;
            }
        });

        //buat dropdown cmb_status1 transparan
        cmb_kategori1.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent) {
                    ((JComponent) c).setOpaque(false);
                }
                return c;
            }
        });
    }
    
    
     public void populateStatusComboBox() {
        try {
            String query = "SELECT DISTINCT status FROM barang";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            Set<String> statusSet = new HashSet<>();

            cmb_status1.removeAllItems();

            while (rs.next()) {
                String status = rs.getString("status");
                if (status != null) {
                    status = status.trim();
                    if (!status.isEmpty() && statusSet.add(status)) {
                        cmb_status1.addItem(status);
                    }
                }
            }

            String[] manualStatuses = {"Rusak", "Hilang", "Maintenance"};
            for (String manualStatus : manualStatuses) {
                if (statusSet.add(manualStatus)) {
                    cmb_status1.addItem(manualStatus);
                }
            }

            rs.close();
            pst.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void populateKategoriComboBox() {
        try {
            String query = "SELECT DISTINCT kategori FROM barang";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            Set<String> kategoriset = new HashSet<>();

            cmb_kategori1.removeAllItems();

            while (rs.next()) {
                String kategori = rs.getString("kategori");
                if (kategori != null) {
                    kategori = kategori.trim();
                    if (!kategori.isEmpty() && kategoriset.add(kategori)) {
                        cmb_kategori1.addItem(kategori);
                    }
                }
            }

            String[] manualStatuses = {"Tenda", "Kursi & meja", "Carrier", "Peralatan masak", "Peralatan tidur", "Peralatan pendukung", "Aksesori outdoor"};
            for (String manualKategori : manualStatuses) {
                if (kategoriset.add(manualKategori)) {
                    cmb_kategori1.addItem(manualKategori);
                }
            }

            rs.close();
            pst.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateID(String tableName, String idColumn, String prefix) {
        String newID = prefix + "001";
        try {
            String sql = "SELECT " + idColumn + " FROM " + tableName + " ORDER BY " + idColumn + " DESC LIMIT 1";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                String lastID = rs.getString(1);
                int num = Integer.parseInt(lastID.substring(prefix.length())) + 1;
                newID = prefix + String.format("%03d", num);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal generate ID: " + e.getMessage());
        }
        return newID;
    }

    private void load_table() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Harga Sewa");
        model.addColumn("Kategori");
        model.addColumn("Stok");
        model.addColumn("Status");
        model.addColumn("Harga Beli");
        model.addColumn("Barcode");

        try {
            String sql = "SELECT * FROM barang";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),});
            }

            tbl_barang.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void tambahbrgrusak() {
        int selectedRow = tbl_barang.getSelectedRow();

        if (selectedRow < 0) {
            // Tidak memilih barang, langsung buka halaman rusak
            page_main.removeAll();
            page_main.add(page_rusak);
            page_main.repaint();
            page_main.revalidate();
            tampilkanDataBarangRusak();
            return;
        }

        String idBarang = tbl_barang.getValueAt(selectedRow, 0).toString();
        String namaBarang = tbl_barang.getValueAt(selectedRow, 1).toString();
        int stok = Integer.parseInt(tbl_barang.getValueAt(selectedRow, 4).toString());
        int hargaBeli = Integer.parseInt(tbl_barang.getValueAt(selectedRow, 6).toString());

        if (stok <= 0) {
            JOptionPane.showMessageDialog(this, "Stok barang habis, tidak bisa dipindahkan ke barang rusak.");
            return;
        }

        // Dialog untuk memilih kondisi rusak
        String[] kondisi = {"Rusak", "Hilang", "Maintenance"};
        String selectedKondisi = (String) JOptionPane.showInputDialog(
                this,
                "Pilih kondisi barang:",
                "Kondisi Barang Rusak",
                JOptionPane.QUESTION_MESSAGE,
                null,
                kondisi,
                kondisi[0]
        );

        if (selectedKondisi != null) {
            // Input jumlah barang rusak
            String jumlahInput = JOptionPane.showInputDialog(this, "Masukkan jumlah barang yang rusak:");

            if (jumlahInput == null || jumlahInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak boleh kosong.");
                return;
            }

            int jumlahRusak;
            try {
                jumlahRusak = Integer.parseInt(jumlahInput);
                if (jumlahRusak <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.");
                    return;
                }
                if (jumlahRusak > stok) {
                    JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.");
                return;
            }

            try {
                con = Koneksi.getConnection();
                String idRusak = generateID("barang_bermasalah", "id_brg_bermasalah", "BMS");
                String sqlInsert = "INSERT INTO barang_bermasalah (id_brg_bermasalah, id_barang, nama_barang, stok, harga_beli, status) VALUES (?, ?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sqlInsert);
                pst.setString(1, idRusak);
                pst.setString(2, idBarang);
                pst.setString(3, namaBarang);
                pst.setInt(4, jumlahRusak);
                pst.setInt(5, hargaBeli);
                pst.setString(6, selectedKondisi);
                pst.executeUpdate();

                // Kurangi stok di tabel barang
                String sqlUpdateStok = "UPDATE barang SET stok = stok - ? WHERE id_barang = ?";
                PreparedStatement pst2 = con.prepareStatement(sqlUpdateStok);
                pst2.setInt(1, jumlahRusak);
                pst2.setString(2, idBarang);
                pst2.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data barang rusak berhasil ditambahkan.");

                page_main.removeAll();
                page_main.add(page_rusak);
                page_main.repaint();
                page_main.revalidate();

                load_table(); // refresh tabel barang
                tampilkanDataBarangRusak(); // refresh tabel rusak
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
            }
        }
    }

    private void tampilkanDataBarangRusak() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Stok");
        model.addColumn("Harga Beli");
        model.addColumn("Status");

        try {
            con = Koneksi.getConnection();
            String sql = "SELECT id_brg_bermasalah, id_barang, nama_barang, stok, harga_beli, status FROM barang_bermasalah";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("stok"),
                    rs.getInt("harga_beli"),
                    rs.getString("status")
                });
            }

            tbl_rusak.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data barang rusak: " + e.getMessage());
        }
    }
    
    private void cariBarang() {
        String keyword = txt_search.getText().trim();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Harga sewa");
        model.addColumn("Harga beli");
        model.addColumn("Kategori");
        model.addColumn("Stok");
        model.addColumn("Status");

        try {
            Koneksi.config();
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT id_barang, nama_barang, harga_sewa, kategori, stok, status, harga_beli, barcode "
                    + "FROM barang WHERE nama_barang LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("harga_sewa"),
                    rs.getInt("harga_beli"),
                    rs.getString("kategori"),
                    rs.getInt("stok"),
                    rs.getString("status")
                });
            }

            tbl_barang.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari data: " + e.getMessage());
        }
    }
    
    private void cariRusak() {
        String keyword = txt_search1.getText().trim();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Harga beli");
        model.addColumn("Kategori");
        model.addColumn("Status");

        try {
            Koneksi.config();
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT id_brg_bermasalah, id_barang, nama_barang, stok, harga_beli, status "
                    + "FROM barang_bermasalah WHERE nama_barang LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("stok"),
                    rs.getInt("harga_beli"),
                    rs.getString("status")
                });
            }


            tbl_rusak.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari data: " + e.getMessage());
        }
    }

    public class EnumComboBoxLoader {

        public static void loadEnumFromDatabase(Connection con, JComboBox<String> comboBox) {
            String query = "SHOW COLUMNS FROM barang LIKE 'status'";

            try (PreparedStatement pst = con.prepareStatement(query); ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    String type = rs.getString("Type");

                    String enumValues = type.substring(type.indexOf("(") + 1, type.indexOf(")"));
                    String[] values = enumValues.replace("'", "").split(",");

                    comboBox.removeAllItems();
                    for (String value : values) {
                        String formatted = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
                        comboBox.addItem(formatted);
                    }
                } else {
                    System.out.println("Kolom 'status' tidak ditemukan.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal memuat enum: " + e.getMessage());
            }
        }
    }
    
    public class EnumComboBoxKat {

        public static void loadEnumFromDatabase(Connection con, JComboBox<String> comboBox) {
            String query = "SHOW COLUMNS FROM barang LIKE 'kategori'";

            try (PreparedStatement pst = con.prepareStatement(query); ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    String type = rs.getString("Type");

                    String enumValues = type.substring(type.indexOf("(") + 1, type.indexOf(")"));
                    String[] values = enumValues.replace("'", "").split(",");

                    comboBox.removeAllItems();
                    for (String value : values) {
                        String formatted = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
                        comboBox.addItem(formatted);
                    }
                } else {
                    System.out.println("Kolom 'kategori' tidak ditemukan.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal memuat enum: " + e.getMessage());
            }
        }
    }
    
    public class DialogBarcodeGenerator extends JDialog {

    private String idBarang, namaBarang;
    private BufferedImage barcodeImage;
    private JLabel lblBarcode;

    public DialogBarcodeGenerator(String idBarang, String namaBarang) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;

        setTitle("Generate Barcode");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblBarcode = new JLabel("", SwingConstants.CENTER);
        add(lblBarcode, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnSimpan = new JButton("Simpan ke Tabel");
        JButton btnCetak = new JButton("Cetak");
        JButton btnCancel = new JButton("Cancel");
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnCetak);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        String barcodeText = idBarang + "-" + namaBarang;
        generateBarcode(barcodeText);


        btnSimpan.addActionListener(e -> simpanBarcodeKeDB(idBarang));
        btnCetak.addActionListener(e -> cetakBarcode());
        btnCancel.addActionListener(e -> dispose());
    }

    private void generateBarcode(String text) {
    try {
        int width = 400;
        int height = 150;

        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, width, height);
        barcodeImage = MatrixToImageWriter.toBufferedImage(matrix);

        lblBarcode.setIcon(new ImageIcon(barcodeImage));
        lblBarcode.setPreferredSize(new Dimension(width, height));
        lblBarcode.revalidate();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal generate barcode: " + e.getMessage());
    }
}


    private void simpanBarcodeKeDB(String idBarang) {
    try {
        String barcodeText = idBarang + "-" + namaBarang; // barcode = ID + Nama
        Connection conn = Koneksi.getConnection(); // koneksi DB
        String sql = "UPDATE barang SET barcode = ? WHERE id_barang = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, barcodeText); // nilai barcode disimpan
        ps.setString(2, idBarang);    // kondisi WHERE tetap pakai ID
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Barcode disimpan ke database.");
        load_table(); // refresh tabel di main window, jika perlu
        dispose();    // ✅ tutup JDialog setelah simpan sukses
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error simpan: " + e.getMessage());
    }
}


    private void cetakBarcode() {
        try {
            File outputfile = new File("barcode_" + idBarang + ".png");
            ImageIO.write(barcodeImage, "png", outputfile);
            Desktop.getDesktop().open(outputfile); // buka file
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal cetak barcode: " + e.getMessage());
        }
    }
    }
    
     private void clearTambahBarang() {
        txt_nama.setText("");
        txt_harga.setText("");
        txt_beli.setText("");
        txt_stok.setText("");

        txt_nama.setForeground(Color.BLACK);
        txt_harga.setForeground(Color.BLACK);
        txt_beli.setForeground(Color.BLACK);
        txt_stok.setForeground(Color.BLACK);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        page_main = new javax.swing.JPanel();
        page_barang = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        btn_search = new javax.swing.JButton();
        txt_search = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_rusak = new javax.swing.JButton();
        btn_barcode = new javax.swing.JButton();
        btn_ubah = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_barang = new custom.JTable_custom();
        page_tambah = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        form_tambah = new javax.swing.JPanel();
        txt_harga = new javax.swing.JTextField();
        txt_nama = new javax.swing.JTextField();
        txt_beli = new javax.swing.JTextField();
        txt_stok = new javax.swing.JTextField();
        cmb_kategori = new javax.swing.JComboBox<>();
        cmb_status = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        btn_simpan = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        page_ubah = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        btn_update = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        form_ubah = new javax.swing.JPanel();
        txt_nama1 = new javax.swing.JTextField();
        txt_beli1 = new javax.swing.JTextField();
        txt_harga1 = new javax.swing.JTextField();
        txt_stok1 = new javax.swing.JTextField();
        cmb_kategori1 = new javax.swing.JComboBox<>();
        cmb_status1 = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        page_rusak = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        btn_search1 = new javax.swing.JButton();
        txt_search1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btn_back = new javax.swing.JButton();
        btn_selesai = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_rusak = new custom.JTable_custom();

        setLayout(new java.awt.CardLayout());

        page_main.setBackground(new java.awt.Color(255, 244, 232));
        page_main.setLayout(new java.awt.CardLayout());

        page_barang.setBackground(new java.awt.Color(255, 244, 232));
        page_barang.setForeground(new java.awt.Color(230, 230, 230));
        page_barang.setPreferredSize(new java.awt.Dimension(836, 666));
        page_barang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/teks daftar barang.png"))); // NOI18N
        page_barang.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 300, 37));

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_barang.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        btn_search.setContentAreaFilled(false);

        btn_search.setBorderPainted(false);
        btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Search.png"))); // NOI18N
        btn_search.setBorder(null);
        btn_search.setContentAreaFilled(false);
        btn_search.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Search Select.png"))); // NOI18N
        btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchActionPerformed(evt);
            }
        });
        page_barang.add(btn_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 170, 50, 40));

        txt_search.setBackground(new java.awt.Color(238, 236, 227));
        txt_search.setBorder(null);
        page_barang.add(txt_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 181, 170, 20));
        page_barang.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 290, -1));

        btn_tambah.setContentAreaFilled(false);

        btn_tambah.setBorderPainted(false);
        btn_tambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/tambah.png"))); // NOI18N
        btn_tambah.setBorder(null);
        btn_tambah.setContentAreaFilled(false);
        btn_tambah.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/tambah (select).png"))); // NOI18N
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });
        page_barang.add(btn_tambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 210, 50));

        btn_hapus.setContentAreaFilled(false);
        btn_hapus.setBorderPainted(false);
        btn_hapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Hapus.png"))); // NOI18N
        btn_hapus.setBorder(null);
        btn_hapus.setContentAreaFilled(false);
        btn_hapus.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Hapus Select.png"))); // NOI18N
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });
        page_barang.add(btn_hapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 170, -1, 40));

        btn_ubah.setContentAreaFilled(false);

        btn_ubah.setBorderPainted(false);
        btn_rusak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/rusak.png"))); // NOI18N
        btn_rusak.setBorder(null);
        btn_rusak.setContentAreaFilled(false);
        btn_rusak.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/rusak (select).png"))); // NOI18N
        btn_rusak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_rusakActionPerformed(evt);
            }
        });
        page_barang.add(btn_rusak, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 170, -1, 40));

        btn_ubah.setContentAreaFilled(false);

        btn_ubah.setBorderPainted(false);
        btn_barcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/barcode.png"))); // NOI18N
        btn_barcode.setBorder(null);
        btn_barcode.setContentAreaFilled(false);
        btn_barcode.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/barcode (select).png"))); // NOI18N
        btn_barcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_barcodeActionPerformed(evt);
            }
        });
        page_barang.add(btn_barcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(398, 170, -1, 40));

        btn_ubah.setContentAreaFilled(false);

        btn_ubah.setBorderPainted(false);
        btn_ubah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/ubah.png"))); // NOI18N
        btn_ubah.setBorder(null);
        btn_ubah.setContentAreaFilled(false);
        btn_ubah.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/ubah (select).png"))); // NOI18N
        btn_ubah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ubahActionPerformed(evt);
            }
        });
        page_barang.add(btn_ubah, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 170, -1, 40));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/search.png"))); // NOI18N
        page_barang.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 310, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/BG Button.png"))); // NOI18N
        page_barang.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 720, 65));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_barang.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        jLabel27.setText("Username");
        page_barang.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        tbl_barang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Barang", "Nama Barang", "Harga Sewa", "Harga Beli", "Kategori", "Stok", "Status", "barcode"
            }
        ));
        jScrollPane1.setViewportView(tbl_barang);

        page_barang.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, 730, 360));

        page_main.add(page_barang, "card2");

        page_tambah.setBackground(new java.awt.Color(255, 244, 232));
        page_tambah.setPreferredSize(new java.awt.Dimension(836, 666));
        page_tambah.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/teks tambah barang.png"))); // NOI18N
        page_tambah.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 330, 37));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_tambah.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        form_tambah.setBackground(new java.awt.Color(255, 244, 232));
        form_tambah.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_harga.setText("Harga Sewa"); txt_harga.setForeground(Color.GRAY);
        txt_harga.setBackground(new java.awt.Color(255, 244, 232));
        txt_harga.setForeground(new java.awt.Color(0, 0, 0));
        txt_harga.setBorder(null);
        form_tambah.add(txt_harga, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 230, 410, 20));

        txt_nama.setBackground(new java.awt.Color(255, 244, 232));
        txt_nama.setForeground(new java.awt.Color(0, 0, 0));
        txt_nama.setBorder(null);
        form_tambah.add(txt_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 183, 410, 20));

        txt_beli.setText("Harga Beli"); txt_beli.setForeground(Color.GRAY);
        txt_beli.setBackground(new java.awt.Color(255, 244, 232));
        txt_beli.setForeground(new java.awt.Color(0, 0, 0));
        txt_beli.setToolTipText("");
        txt_beli.setBorder(null);
        form_tambah.add(txt_beli, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 278, 410, 20));

        txt_stok.setText("Stok"); txt_stok.setForeground(Color.GRAY);
        txt_stok.setBackground(new java.awt.Color(255, 244, 232));
        txt_stok.setForeground(new java.awt.Color(0, 0, 0));
        txt_stok.setToolTipText("");
        txt_stok.setBorder(null);
        form_tambah.add(txt_stok, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 372, 410, 20));

        cmb_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        form_tambah.add(cmb_kategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 326, 410, 20));

        cmb_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_statusActionPerformed(evt);
            }
        });
        form_tambah.add(cmb_status, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 419, 410, 20));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/form tambah ubah.png"))); // NOI18N
        form_tambah.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 560, 490));

        page_tambah.add(form_tambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 580, 490));

        btn_simpan.setContentAreaFilled(false);

        btn_simpan.setBorderPainted(false);
        btn_simpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/simpan.png"))); // NOI18N
        btn_simpan.setContentAreaFilled(false);
        btn_simpan.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/simpan (select).png"))); // NOI18N
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });
        page_tambah.add(btn_simpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 580, 100, 40));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_tambah.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        jLabel31.setText("Username");
        page_tambah.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        page_main.add(page_tambah, "card3");

        page_ubah.setBackground(new java.awt.Color(255, 244, 232));
        page_ubah.setPreferredSize(new java.awt.Dimension(836, 666));
        page_ubah.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/teks ubah Barang.png"))); // NOI18N
        page_ubah.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 330, 37));

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_ubah.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        btn_simpan.setContentAreaFilled(false);

        btn_simpan.setBorderPainted(false);
        btn_update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/ubah.png"))); // NOI18N
        btn_update.setBorder(null);
        btn_update.setContentAreaFilled(false);
        btn_update.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/ubah (select).png"))); // NOI18N
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });
        page_ubah.add(btn_update, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 580, 100, 40));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_ubah.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        jLabel35.setText("Username");
        page_ubah.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        form_ubah.setBackground(new java.awt.Color(255, 244, 232));
        form_ubah.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_nama.setText("Nama Barang"); txt_nama.setForeground(Color.GRAY);
        txt_nama1.setBackground(new java.awt.Color(255, 244, 232));
        txt_nama1.setForeground(new java.awt.Color(0, 0, 0));
        txt_nama1.setBorder(null);
        form_ubah.add(txt_nama1, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 183, 410, 20));

        txt_beli.setText("Harga Beli"); txt_beli.setForeground(Color.GRAY);
        txt_beli1.setBackground(new java.awt.Color(255, 244, 232));
        txt_beli1.setForeground(new java.awt.Color(0, 0, 0));
        txt_beli1.setToolTipText("");
        txt_beli1.setBorder(null);
        form_ubah.add(txt_beli1, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 278, 410, 20));

        txt_harga.setText("Harga"); txt_harga.setForeground(Color.GRAY);
        txt_harga1.setBackground(new java.awt.Color(255, 244, 232));
        txt_harga1.setForeground(new java.awt.Color(0, 0, 0));
        txt_harga1.setToolTipText("");
        txt_harga1.setBorder(null);
        form_ubah.add(txt_harga1, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 230, 410, 20));

        txt_stok.setText("Stok"); txt_stok.setForeground(Color.GRAY);
        txt_stok1.setBackground(new java.awt.Color(255, 244, 232));
        txt_stok1.setForeground(new java.awt.Color(0, 0, 0));
        txt_stok1.setToolTipText("");
        txt_stok1.setBorder(null);
        form_ubah.add(txt_stok1, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 372, 410, 20));

        cmb_kategori1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_kategori1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_kategori1ActionPerformed(evt);
            }
        });
        form_ubah.add(cmb_kategori1, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 326, 410, 20));

        cmb_status1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_status1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_status1ActionPerformed(evt);
            }
        });
        form_ubah.add(cmb_status1, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 419, 410, 20));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/form tambah ubah.png"))); // NOI18N
        form_ubah.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 560, 490));

        page_ubah.add(form_ubah, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 580, 490));

        page_main.add(page_ubah, "card3");

        page_rusak.setBackground(new java.awt.Color(255, 244, 232));
        page_rusak.setForeground(new java.awt.Color(230, 230, 230));
        page_rusak.setPreferredSize(new java.awt.Dimension(836, 666));
        page_rusak.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/teks barang rusak.png"))); // NOI18N
        page_rusak.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 300, 37));

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_rusak.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        btn_search.setContentAreaFilled(false);

        btn_search.setBorderPainted(false);
        btn_search1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Search.png"))); // NOI18N
        btn_search1.setBorder(null);
        btn_search1.setContentAreaFilled(false);
        btn_search1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Search Select.png"))); // NOI18N
        btn_search1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_search1ActionPerformed(evt);
            }
        });
        page_rusak.add(btn_search1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 170, 50, 40));

        txt_search1.setBackground(new java.awt.Color(238, 236, 227));
        txt_search1.setBorder(null);
        page_rusak.add(txt_search1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 181, 290, 20));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Search.png"))); // NOI18N
        page_rusak.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 410, -1));

        btn_back.setContentAreaFilled(false);

        btn_back.setBorderPainted(false);
        btn_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Kembali.png"))); // NOI18N
        btn_back.setBorder(null);
        btn_back.setContentAreaFilled(false);
        btn_back.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Kembali Select.png"))); // NOI18N
        btn_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_backActionPerformed(evt);
            }
        });
        page_rusak.add(btn_back, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 590, 100, 40));

        btn_hapus.setContentAreaFilled(false);
        btn_hapus.setBorderPainted(false);
        btn_selesai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/selesai.png"))); // NOI18N
        btn_selesai.setBorder(null);
        btn_selesai.setContentAreaFilled(false);
        btn_selesai.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/barang/selesai select.png"))); // NOI18N
        btn_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_selesaiActionPerformed(evt);
            }
        });
        page_rusak.add(btn_selesai, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 175, 130, 40));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/BG Button.png"))); // NOI18N
        page_rusak.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 720, 65));

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_rusak.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        jLabel39.setText("Username");
        page_rusak.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        tbl_rusak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Barang", "Nama Barang", "Harga Beli", "Stok", "Status"
            }
        ));
        jScrollPane2.setViewportView(tbl_rusak);

        page_rusak.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, 730, 330));

        page_main.add(page_rusak, "card2");

        add(page_main, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed

    }//GEN-LAST:event_btn_searchActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        clearTambahBarang();

        page_main.removeAll();
        page_main.add(page_tambah);
        page_main.repaint();
        page_main.revalidate();
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        int selectedRow = tbl_barang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tbl_barang.getModel();
        String idBarang = (String) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            String sql = "DELETE FROM barang WHERE id_barang = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, idBarang);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_rusakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_rusakActionPerformed
        tambahbrgrusak();
    }//GEN-LAST:event_btn_rusakActionPerformed

    private void btn_barcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_barcodeActionPerformed
        int selectedRow = tbl_barang.getSelectedRow();
        if (selectedRow != -1) {
            String idBarang = tbl_barang.getValueAt(selectedRow, 0).toString();
            String namaBarang = tbl_barang.getValueAt(selectedRow, 1).toString();
            new DialogBarcodeGenerator(idBarang, namaBarang).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data barang terlebih dahulu!");
        }
    }//GEN-LAST:event_btn_barcodeActionPerformed

     private String idBarangYangSedangDiedit;
    
    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        int selectedRow = tbl_barang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tbl_barang.getModel();

        String selectedidBarang = String.valueOf(model.getValueAt(selectedRow, 0)); // Untuk disimpan
        String nama_barang = String.valueOf(model.getValueAt(selectedRow, 1));
        int harga_sewa = Integer.parseInt(String.valueOf(model.getValueAt(selectedRow, 2)));
        String kategori = String.valueOf(model.getValueAt(selectedRow, 3));
        int stok = Integer.parseInt(String.valueOf(model.getValueAt(selectedRow, 4)));
        String status = String.valueOf(model.getValueAt(selectedRow, 5));
        int harga_beli = Integer.parseInt(String.valueOf(model.getValueAt(selectedRow, 6)));

        page_main.removeAll();
        page_main.add(page_ubah);
        page_main.repaint();
        page_main.revalidate();

        txt_nama1.setText(nama_barang);
        txt_nama1.setForeground(Color.BLACK);
        txt_harga1.setText(String.valueOf(harga_sewa));
        txt_harga1.setForeground(Color.BLACK);
        txt_beli1.setText(String.valueOf(harga_beli));
        txt_beli1.setForeground(Color.BLACK);
        txt_stok1.setText(String.valueOf(stok));
        txt_stok1.setForeground(Color.BLACK);

        idBarangYangSedangDiedit = selectedidBarang;
    }//GEN-LAST:event_btn_ubahActionPerformed

    private void cmb_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_statusActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        String nama_barang, kategori, status, save;
        int harga_sewa, stok, harga_beli;

        String id_barang = generateID("barang", "id_barang", "BRG");
        nama_barang = txt_nama.getText();
        txt_nama.setForeground(Color.BLACK);
        String hargaStr = txt_harga.getText();
        String beliStr = txt_beli.getText();
        kategori = cmb_kategori.getSelectedItem().toString().toUpperCase();
        String stokStr = txt_stok.getText();
        status = cmb_status.getSelectedItem().toString().toUpperCase();

        if (nama_barang.isEmpty() || nama_barang.equals("Masukkan Nama Barang")
            || hargaStr.isEmpty() || hargaStr.equals("Masukkan Harga Sewa")
            || beliStr.isEmpty() || beliStr.equals("Masukkan Harga Beli")
            || cmb_kategori.getSelectedItem() == null || cmb_kategori.getSelectedItem().toString().isEmpty()
            || stokStr.isEmpty() || stokStr.equals("Masukkan Stok")
            || cmb_status.getSelectedItem() == null || cmb_status.getSelectedItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Cek apakah nama barang sudah ada di DB
            String cekNamaSQL = "SELECT COUNT(*) FROM barang WHERE nama_barang = ?";
            pst = con.prepareStatement(cekNamaSQL);
            pst.setString(1, nama_barang);
            ResultSet rs = pst.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Nama barang sudah ada, silakan ganti nama lain!", "Error", JOptionPane.ERROR_MESSAGE);
                pst.close();
                return;
            }
            pst.close();

            harga_sewa = Integer.parseInt(hargaStr.replaceAll("[^0-9]", ""));
            harga_beli = Integer.parseInt(beliStr.replaceAll("[^0-9]", ""));
            stok = Integer.parseInt(stokStr);

            if (harga_sewa <= 0 || harga_beli <= 0 || stok <= 0) {
                JOptionPane.showMessageDialog(this, "Harga, Beli, dan Stok harus lebih dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            save = "INSERT INTO barang (id_barang, nama_barang, harga_sewa, harga_beli, kategori, stok, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pst = con.prepareStatement(save);
            pst.setString(1, id_barang);
            pst.setString(2, nama_barang);
            pst.setInt(3, harga_sewa);
            pst.setInt(4, harga_beli);
            pst.setString(5, kategori);
            pst.setInt(6, stok);
            pst.setString(7, status);

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                page_main.removeAll();
                page_main.add(page_barang);
                page_main.repaint();
                page_main.revalidate();
                clearTambahBarang();
                load_table();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan barang!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga Sewa, Harga Beli, dan Stok harus berupa angka valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        String newNamaBarang = txt_nama1.getText().trim();
        String newKategori = cmb_kategori1.getSelectedItem().toString();
        String newStatus = cmb_status1.getSelectedItem().toString();

        String hargaStr = txt_harga1.getText().trim().replaceAll("[^\\d]", "");
        String beliStr = txt_beli1.getText().trim().replaceAll("[^\\d]", "");
        String stokStr = txt_stok1.getText().trim();

        if (newNamaBarang.isEmpty() || hargaStr.isEmpty() || beliStr.isEmpty() || newKategori.isEmpty() || stokStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int newHargaSewa = Integer.parseInt(hargaStr);
            int newBeli = Integer.parseInt(beliStr);
            int newStok = Integer.parseInt(stokStr);

            if (hargaStr.isEmpty() || beliStr.isEmpty() || stokStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Input tidak boleh kosong atau hanya berisi simbol!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int row = tbl_barang.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data barang yang ingin diupdate!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedIdBarang = tbl_barang.getValueAt(row, 0).toString();

            String cekQuery = "SELECT * FROM barang WHERE nama_barang = ? AND id_barang != ?";
            pst = con.prepareStatement(cekQuery);
            pst.setString(1, newNamaBarang);
            pst.setString(2, selectedIdBarang);
            rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Nama barang sudah digunakan, silakan pilih yang lain", "Error", JOptionPane.ERROR_MESSAGE);
                rs.close();
                pst.close();
                return;
            }

            String updateQuery = "UPDATE barang SET nama_barang = ?, harga_sewa = ?, harga_beli = ?, kategori = ?, stok = ?, status = ? WHERE id_barang = ?";
            pst = con.prepareStatement(updateQuery);
            pst.setString(1, newNamaBarang);
            pst.setInt(2, newHargaSewa);
            pst.setInt(3, newBeli);
            pst.setString(4, newKategori);
            pst.setInt(5, newStok);
            pst.setString(6, newStatus);
            pst.setString(7, selectedIdBarang);

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Data barang berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                load_table();
                page_main.removeAll();
                page_main.add(page_barang);
                page_main.repaint();
                page_main.revalidate();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data barang.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            rs.close();
            pst.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga Sewa, Harga Beli, dan Stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    private void cmb_kategori1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_kategori1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_kategori1ActionPerformed

    private void cmb_status1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_status1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_status1ActionPerformed

    private void btn_search1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_search1ActionPerformed

    }//GEN-LAST:event_btn_search1ActionPerformed

    private void btn_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_backActionPerformed
        // TODO add your handling code here:
        page_main.removeAll();
        page_main.add(page_barang);
        page_main.repaint();
        page_main.revalidate();
    }//GEN-LAST:event_btn_backActionPerformed

    private void btn_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_selesaiActionPerformed
        int selectedRow = tbl_rusak.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data barang rusak terlebih dahulu.");
            return;
        }

        DefaultTableModel modelRusak = (DefaultTableModel) tbl_rusak.getModel();
        DefaultTableModel modelBarang = (DefaultTableModel) tbl_barang.getModel();

        String idBarang = modelRusak.getValueAt(selectedRow, 0).toString();
        String namaBarang = modelRusak.getValueAt(selectedRow, 1).toString();
        int jumlahRusak = Integer.parseInt(modelRusak.getValueAt(selectedRow, 2).toString()); // stok rusak
        int hargaBeli = Integer.parseInt(modelRusak.getValueAt(selectedRow, 3).toString());
        String status = modelRusak.getValueAt(selectedRow, 4).toString();

        if (!status.equalsIgnoreCase("Maintenance")) {
            JOptionPane.showMessageDialog(this, "Hanya barang dengan status 'Maintenance' yang dapat dikembalikan.");
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Masukkan jumlah barang yang sudah diperbaiki:", "Input Jumlah Perbaikan", JOptionPane.PLAIN_MESSAGE);
        if (input == null) {
            // Cancel ditekan
            return;
        }

        int jumlahPerbaikan;
        try {
            jumlahPerbaikan = Integer.parseInt(input);
            if (jumlahPerbaikan <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah perbaikan harus lebih dari 0.");
                return;
            }
            if (jumlahPerbaikan > jumlahRusak) {
                JOptionPane.showMessageDialog(this, "Jumlah perbaikan tidak boleh melebihi jumlah barang rusak.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input jumlah perbaikan tidak valid.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin memproses perbaikan " + jumlahPerbaikan + " barang?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            con = Koneksi.getConnection();

            // Update stok di tabel barang
            boolean found = false;
            for (int i = 0; i < modelBarang.getRowCount(); i++) {
                if (modelBarang.getValueAt(i, 0).toString().equals(idBarang)) {
                    int stokLama = Integer.parseInt(modelBarang.getValueAt(i, 4).toString()); // stok di tbl_barang kolom 4
                    int stokBaru = stokLama + jumlahPerbaikan;
                    modelBarang.setValueAt(stokBaru, i, 4);

                    // Update stok di database
                    String sqlUpdate = "UPDATE barang SET stok = ? WHERE id_barang = ?";
                    PreparedStatement pstUpdate = con.prepareStatement(sqlUpdate);
                    pstUpdate.setInt(1, stokBaru);
                    pstUpdate.setString(2, idBarang);
                    pstUpdate.executeUpdate();
                    pstUpdate.close();

                    found = true;
                    break;
                }
            }

            if (!found) {
                // Jika barang belum ada di tbl_barang, insert baru
                String sqlInsert = "INSERT INTO barang (id_barang, nama_barang, harga_sewa, harga_beli, kategori, stok, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
                pstInsert.setString(1, idBarang);
                pstInsert.setString(2, namaBarang);
                pstInsert.setInt(3, 0); // harga sewa default
                pstInsert.setInt(4, hargaBeli);
                pstInsert.setString(5, ""); // kategori default
                pstInsert.setInt(6, jumlahPerbaikan);
                pstInsert.setString(7, "Ready");
                pstInsert.executeUpdate();
                pstInsert.close();

                modelBarang.addRow(new Object[]{idBarang, namaBarang, 0, hargaBeli, jumlahPerbaikan, "Ready"});
            }

            // Kurangi jumlah barang rusak di tbl_rusak dan database
            int sisaRusak = jumlahRusak - jumlahPerbaikan;

            if (sisaRusak > 0) {
                modelRusak.setValueAt(sisaRusak, selectedRow, 2); // update stok rusak di kolom 2

                // Update di database barang_bermasalah (stok)
                String sqlUpdateRusak = "UPDATE barang_bermasalah SET stok = ? WHERE id_barang = ? AND status = 'Maintenance'";
                PreparedStatement pstUpdateRusak = con.prepareStatement(sqlUpdateRusak);
                pstUpdateRusak.setInt(1, sisaRusak);
                pstUpdateRusak.setString(2, idBarang);
                pstUpdateRusak.executeUpdate();
                pstUpdateRusak.close();
            } else {
                // Jika habis, hapus data di database dan tabel rusak
                String sqlDelete = "DELETE FROM barang_bermasalah WHERE id_barang = ? AND status = 'Maintenance'";
                PreparedStatement pstDelete = con.prepareStatement(sqlDelete);
                pstDelete.setString(1, idBarang);
                pstDelete.executeUpdate();
                pstDelete.close();

                modelRusak.removeRow(selectedRow);
            }

            JOptionPane.showMessageDialog(this, "Barang berhasil diperbaiki dan stok diperbarui.");

            // Refresh tampilan halaman barang/rusak
            page_main.removeAll();
            page_main.add(page_barang);
            page_main.repaint();
            page_main.revalidate();

            load_table(); // reload data barang dan rusak
            tampilkanDataBarangRusak();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_selesaiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_back;
    private javax.swing.JButton btn_barcode;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_rusak;
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_search1;
    private javax.swing.JButton btn_selesai;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JButton btn_update;
    private javax.swing.JComboBox<String> cmb_kategori;
    private javax.swing.JComboBox<String> cmb_kategori1;
    private javax.swing.JComboBox<String> cmb_status;
    private javax.swing.JComboBox<String> cmb_status1;
    private javax.swing.JPanel form_tambah;
    private javax.swing.JPanel form_ubah;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel page_barang;
    private javax.swing.JPanel page_main;
    private javax.swing.JPanel page_rusak;
    private javax.swing.JPanel page_tambah;
    private javax.swing.JPanel page_ubah;
    private custom.JTable_custom tbl_barang;
    private custom.JTable_custom tbl_rusak;
    private javax.swing.JTextField txt_beli;
    private javax.swing.JTextField txt_beli1;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_harga1;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_nama1;
    private javax.swing.JTextField txt_search;
    private javax.swing.JTextField txt_search1;
    private javax.swing.JTextField txt_stok;
    private javax.swing.JTextField txt_stok1;
    // End of variables declaration//GEN-END:variables
}
