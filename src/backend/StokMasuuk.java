
package backend;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.AbstractDocument;

public class StokMasuuk extends javax.swing.JPanel {
private Map<String, String> barangMap = new HashMap<>();

private Map<String, String> pemasokMap = new HashMap<>();



//private String formatRupiah(String angka) {
//    try {
//        long nilai = Long.parseLong(angka);
//        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
//        return "Rp" + formatter.format(nilai);
//    } catch (NumberFormatException e) {
//        return "Rp0";
//    }
//}


    public StokMasuuk() {
        
        initComponents();
        loadDataStokMasuk();
        label_username.setText(Login.Session.getUsername());
        label_username1.setText(Login.Session.getUsername());
        label_username.setText( Login.Session.getUsername());
        label_username1.setText( Login.Session.getUsername());

        
        
        
        
txt_search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        cariBarang();
    }
    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        cariBarang();
    }
    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        cariBarang();
    }
});



        
        // Hanya izinkan input angka untuk jumlah dan harga
((AbstractDocument) txt_jumlahbarang.getDocument()).setDocumentFilter(new filter());
((AbstractDocument) txt_harga.getDocument()).setDocumentFilter(new FormatHarga());








        loadPemasokToComboBox();
        // Buat combobox transparan
    cmb_pilihbarang.setOpaque(false);
    cmb_pilihbarang.setBackground(new Color(0, 0, 0, 0));
    cmb_pilihbarang.setForeground(Color.WHITE);
    cmb_pilihbarang.setBorder(null);
    //buat dropdown transparan
    cmb_pilihbarang.setRenderer(new DefaultListCellRenderer() {
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

    
      cmb_idpemasok.setOpaque(false);
    cmb_idpemasok.setBackground(new Color(0, 0, 0, 0));
    cmb_idpemasok.setForeground(Color.WHITE);
    cmb_idpemasok.setBorder(null);
    //buat dropdown transparan
   cmb_idpemasok.setRenderer(new DefaultListCellRenderer() {
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
    

    private void loadBarangKeComboBox() {
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    barangMap.clear();

    try {
        Koneksi.config();
        Connection conn = Koneksi.getConnection();
        String sql = "SELECT id_barang, nama_barang FROM barang";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String idBarang = rs.getString("id_barang");
            String namaBarang = rs.getString("nama_barang");
            barangMap.put(namaBarang, idBarang);
            model.addElement(namaBarang);
        }

        cmb_pilihbarang.setModel(model);
        cmb_pilihbarang.setSelectedIndex(-1); 

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data barang: " + e.getMessage());
    }
}
    
    private void loadPemasokToComboBox() {
    cmb_idpemasok.removeAllItems();
    pemasokMap.clear(); // hapus data sebelumnya

    try {
        Koneksi.config();
        Connection conn = Koneksi.getConnection();

        String sql = "SELECT id_pemasok, nama_pemasok FROM pemasok";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String id = rs.getString("id_pemasok");
            String nama = rs.getString("nama_pemasok");

            pemasokMap.put(nama, id); // simpan relasi nama -> id
            cmb_idpemasok.addItem(nama); // tampilkan nama ke comboBox
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data pemasok: " + e.getMessage());
    }
}
    
    
    



private void formWindowOpened(java.awt.event.WindowEvent evt) {
    loadBarangKeComboBox();
}
private void loadDataStokMasuk() {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("ID Stok Masuk");
    model.addColumn("Nama Barang");
    model.addColumn("Kategori");
    model.addColumn("Qty");
    model.addColumn("Harga");
    model.addColumn("Nama Pemasok");

    try {
        Koneksi.config();
        Connection conn = Koneksi.getConnection();

        String sql = "SELECT sm.id_stok_masuk, b.nama_barang, sm.kategori, sm.qty, sm.harga, p.nama_pemasok " +
                     "FROM stok_masuk sm " +
                     "JOIN barang b ON sm.id_barang = b.id_barang " +
                     "JOIN pemasok p ON sm.id_pemasok = p.id_pemasok";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("id_stok_masuk"),
                rs.getString("nama_barang"),
                rs.getString("kategori"),
                rs.getInt("qty"),
                rs.getInt("harga"),
                rs.getString("nama_pemasok")
            });
        }

        tbl_stok.setModel(model);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data stok masuk: " + e.getMessage());
    }
    
    
//    txt_search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
//    @Override
//    public void insertUpdate(javax.swing.event.DocumentEvent e) {
//        cariBarang();
//    }
//    @Override
//    public void removeUpdate(javax.swing.event.DocumentEvent e) {
//        cariBarang();
//    }
//    @Override
//    public void changedUpdate(javax.swing.event.DocumentEvent e) {
//        cariBarang();
//    }
//});

}

private void cariBarang() {
    String keyword = txt_search.getText().trim();

    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("ID Stok Masuk");
    model.addColumn("Nama Barang");
    model.addColumn("Kategori");
    model.addColumn("Qty");
    model.addColumn("Harga");
    model.addColumn("Nama Pemasok");

    try {
        Koneksi.config();
        Connection conn = Koneksi.getConnection();
        String sql = "SELECT sm.id_stok_masuk, b.nama_barang, sm.kategori, sm.qty, sm.harga, p.nama_pemasok " +
                     "FROM stok_masuk sm " +
                     "JOIN barang b ON sm.id_barang = b.id_barang " +
                     "JOIN pemasok p ON sm.id_pemasok = p.id_pemasok " +
                     "WHERE b.nama_barang LIKE ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%" + keyword + "%");
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("id_stok_masuk"),
                rs.getString("nama_barang"),
                rs.getString("kategori"),
                rs.getInt("qty"),
                rs.getInt("harga"),
                rs.getString("nama_pemasok")
            });
        }

        tbl_stok.setModel(model);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mencari data: " + e.getMessage());
    }
}


public String generateIdStokMasuk(Connection conn) throws SQLException {
    String prefix = "SM";
    String sql = "SELECT id_stok_masuk FROM stok_masuk ORDER BY id_stok_masuk DESC LIMIT 1";
    PreparedStatement pst = conn.prepareStatement(sql);
    ResultSet rs = pst.executeQuery();

    int nextId = 1;
    if (rs.next()) {
        String lastId = rs.getString("id_stok_masuk").replace(prefix, "");
        try {
            nextId = Integer.parseInt(lastId) + 1;
        } catch (NumberFormatException e) {
            nextId = 1; // fallback
        }
    }

    return String.format("%s%03d", prefix, nextId); // hasil: SM001, SM002, dst
}


 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        page_mainn = new javax.swing.JPanel();
        page_stokmasuk = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        btn_search = new javax.swing.JButton();
        txt_search = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_username = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_stok = new custom.JTable_customAutoresize();
        page_tambahstokmasuk = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        form_tambah = new javax.swing.JPanel();
        txt_jumlahbarang = new javax.swing.JTextField();
        txt_harga = new javax.swing.JTextField();
        txt_kategori = new javax.swing.JTextField();
        cmb_pilihbarang = new javax.swing.JComboBox<>();
        cmb_idpemasok = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        btn_simpan = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        label_username1 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        page_mainn.setLayout(new java.awt.CardLayout());

        page_stokmasuk.setBackground(new java.awt.Color(255, 244, 232));
        page_stokmasuk.setForeground(new java.awt.Color(230, 230, 230));
        page_stokmasuk.setPreferredSize(new java.awt.Dimension(836, 666));
        page_stokmasuk.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_stokmasuk.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

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
        page_stokmasuk.add(btn_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 50, 40));

        txt_search.setBackground(new java.awt.Color(238, 236, 227));
        txt_search.setBorder(null);
        txt_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_searchActionPerformed(evt);
            }
        });
        page_stokmasuk.add(txt_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 180, 280, 20));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Search.png"))); // NOI18N
        page_stokmasuk.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 170, 400, -1));

        btn_tambah.setContentAreaFilled(false);

        btn_tambah.setBorderPainted(false);
        btn_tambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/stokmasuk/Group 49.png"))); // NOI18N
        btn_tambah.setBorder(null);
        btn_tambah.setContentAreaFilled(false);
        btn_tambah.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/stokmasuk/Group 102.png"))); // NOI18N
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });
        page_stokmasuk.add(btn_tambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 210, 50));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/BG Button.png"))); // NOI18N
        page_stokmasuk.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 710, 65));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_stokmasuk.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        label_username.setText("Username");
        page_stokmasuk.add(label_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/stokmasuk/Stok Masuk.png"))); // NOI18N
        page_stokmasuk.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 320, 40));

        tbl_stok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tbl_stok);

        page_stokmasuk.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, 740, 380));

        page_mainn.add(page_stokmasuk, "card2");

        page_tambahstokmasuk.setBackground(new java.awt.Color(255, 244, 232));
        page_tambahstokmasuk.setPreferredSize(new java.awt.Dimension(836, 666));
        page_tambahstokmasuk.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/stokmasuk/Stok Masuk.png"))); // NOI18N
        page_tambahstokmasuk.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 312, 37));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_tambahstokmasuk.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        form_tambah.setBackground(new java.awt.Color(255, 244, 232));
        form_tambah.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_jumlahbarang.setBorder(null);
        txt_jumlahbarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_jumlahbarangActionPerformed(evt);
            }
        });
        form_tambah.add(txt_jumlahbarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, 420, 30));

        txt_harga.setBorder(null);
        txt_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_hargaActionPerformed(evt);
            }
        });
        txt_harga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_hargaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_hargaKeyTyped(evt);
            }
        });
        form_tambah.add(txt_harga, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, 430, 30));

        txt_kategori.setBackground(new java.awt.Color(248, 232, 215));
        txt_kategori.setBorder(null);
        form_tambah.add(txt_kategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 180, 430, 30));

        cmb_pilihbarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_pilihbarang.setBorder(null);
        form_tambah.add(cmb_pilihbarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 430, 30));

        cmb_idpemasok.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_idpemasok.setBorder(null);
        cmb_idpemasok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_idpemasokActionPerformed(evt);
            }
        });
        form_tambah.add(cmb_idpemasok, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 430, 30));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/stokmasuk/Group 128.png"))); // NOI18N
        form_tambah.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 630, 490));

        page_tambahstokmasuk.add(form_tambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 560, 490));

        btn_simpan.setContentAreaFilled(false);

        btn_simpan.setBorderPainted(false);
        btn_simpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/stokmasuk/Group 19.png"))); // NOI18N
        btn_simpan.setBorder(null);
        btn_simpan.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/stokmasuk/Group 51.png"))); // NOI18N
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });
        page_tambahstokmasuk.add(btn_simpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 580, 120, 40));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_tambahstokmasuk.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        label_username1.setText("Username");
        page_tambahstokmasuk.add(label_username1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));

        page_mainn.add(page_tambahstokmasuk, "card3");

        add(page_mainn, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
          loadBarangKeComboBox();
        page_mainn.removeAll();
        page_mainn.add(page_tambahstokmasuk);
        page_mainn.repaint();
        page_mainn.revalidate();
        
                loadDataStokMasuk();

    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        label_username.setText(Login.Session.getUsername());

        
        String selectedNamaBarang = (String) cmb_pilihbarang.getSelectedItem();
if (selectedNamaBarang == null || selectedNamaBarang.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
    return;
}

String idBarang = barangMap.get(selectedNamaBarang);
if (idBarang == null) {
    JOptionPane.showMessageDialog(this, "Barang tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
    return;
}

String jumlahStr = txt_jumlahbarang.getText().trim();
String hargaStr = txt_harga.getText().replaceAll("[^\\d]", ""); // Hapus "Rp" dan titik
String kategori = txt_kategori.getText().trim();
String namaPemasok = (String) cmb_idpemasok.getSelectedItem();
String idPemasok = pemasokMap.get(namaPemasok); // Ambil id dari map

if (jumlahStr.isEmpty() || hargaStr.isEmpty() || kategori.isEmpty() || idPemasok == null || idPemasok.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
    return;
}

try {
    int qty = Integer.parseInt(jumlahStr);
    int harga = Integer.parseInt(hargaStr);
    


    Koneksi.config();
    Connection conn = Koneksi.getConnection();
    String idStokMasuk = generateIdStokMasuk(conn);


    // 1. INSERT ke stok_masuk
    String sqlInsert = "INSERT INTO stok_masuk (id_stok_masuk, id_barang, kategori, qty, harga, id_pemasok) VALUES (?, ?, ?, ?, ?, ?)";
    PreparedStatement pstInsert = conn.prepareStatement(sqlInsert);
    pstInsert.setString(1, idStokMasuk);
    pstInsert.setString(2, idBarang);
    pstInsert.setString(3, kategori);
    pstInsert.setInt(4, qty);
    pstInsert.setInt(5, harga);
    pstInsert.setString(6, idPemasok);
    pstInsert.executeUpdate();

    // 2. UPDATE stok barang
    String sqlUpdate = "UPDATE barang SET stok = stok + ? WHERE id_barang = ?";
    PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate);
    pstUpdate.setInt(1, qty);
    pstUpdate.setString(2, idBarang);
    pstUpdate.executeUpdate();

    JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");

    // Reset form
    cmb_pilihbarang.setSelectedIndex(-1);
    txt_jumlahbarang.setText("");
    txt_harga.setText("");
    txt_kategori.setText("");
    cmb_idpemasok.setSelectedIndex(-1);

    // Kembali ke halaman stok masuk
    loadDataStokMasuk();
    page_mainn.removeAll();
    page_mainn.add(page_stokmasuk);
    page_mainn.repaint();
    page_mainn.revalidate();

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Jumlah dan harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Terjadi kesalahan database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}


    }//GEN-LAST:event_btn_simpanActionPerformed

    private void txt_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_searchActionPerformed
         cariBarang();
    }//GEN-LAST:event_txt_searchActionPerformed

    private void txt_jumlahbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_jumlahbarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_jumlahbarangActionPerformed

    private void cmb_idpemasokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_idpemasokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_idpemasokActionPerformed

    private void txt_hargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_hargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_hargaActionPerformed

    private void txt_hargaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hargaKeyReleased
 

    }//GEN-LAST:event_txt_hargaKeyReleased

    private void txt_hargaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hargaKeyTyped
   

    }//GEN-LAST:event_txt_hargaKeyTyped

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed
        // TODO add your handling code here:
        cariBarang();
    }//GEN-LAST:event_btn_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JComboBox<String> cmb_idpemasok;
    private javax.swing.JComboBox<String> cmb_pilihbarang;
    private javax.swing.JPanel form_tambah;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_username;
    private javax.swing.JLabel label_username1;
    private javax.swing.JPanel page_mainn;
    private javax.swing.JPanel page_stokmasuk;
    private javax.swing.JPanel page_tambahstokmasuk;
    private custom.JTable_customAutoresize tbl_stok;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_jumlahbarang;
    private javax.swing.JTextField txt_kategori;
    private javax.swing.JTextField txt_search;
    // End of variables declaration//GEN-END:variables
}
