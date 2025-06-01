package backend;

import java.sql.*;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import backend.RefreshListener; // Tambahkan ini di bagian import
import javax.swing.text.AbstractDocument;



public class MenuPemasok extends javax.swing.JPanel {

      public MenuPemasok(String namaUser) {
        initComponents();
        label_username.setText(namaUser);
ImageIcon icon = new ImageIcon(getClass().getResource("/gambar/icon.png"));

    }
      public void setUsername(String namaUser) {
        label_username.setText(namaUser); 

    }
    
    
    public MenuPemasok() {
        initComponents();
//        txt_search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
//    @Override
//    public void insertUpdate(javax.swing.event.DocumentEvent e) {
//        cariPemasok();
//    }
//    @Override
//    public void removeUpdate(javax.swing.event.DocumentEvent e) {
//        cariPemasok();
//    }
//    @Override
//    public void changedUpdate(javax.swing.event.DocumentEvent e) {
//        cariPemasok();
//    }
//});



        loadDataPemasok();
         label_username.setText(Login.Session.getUsername());
    }

    public void loadDataPemasok() {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("ID Pemasok");
    model.addColumn("Nama Pemasok");
    model.addColumn("No Telepon");
    model.addColumn("Alamat");

    try {
        Koneksi.config(); // WAJIB panggil dulu!
        Connection conn = Koneksi.getConnection();
        String sql = "SELECT * FROM pemasok";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("id_pemasok"),
                rs.getString("nama_pemasok"),
                rs.getString("no_telp"),
                rs.getString("alamat")
            });
        }

        tbl_pmasok.setModel(model);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Gagal load data: " + e.getMessage());
    }
}

  private void cariPemasok() {
    String keyword = txt_search.getText().trim();

    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("ID Pemasok");
    model.addColumn("Nama Pemasok");
    model.addColumn("No Telepon");
    model.addColumn("Alamat");

    try {
        Koneksi.config();
        Connection conn = Koneksi.getConnection();
        String sql = "SELECT id_pemasok, nama_pemasok, no_telp, alamat " +
                     "FROM pemasok " +
                     "WHERE nama_pemasok LIKE ? OR no_telp LIKE ? OR alamat LIKE ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%" + keyword + "%");
        pst.setString(2, "%" + keyword + "%");
        pst.setString(3, "%" + keyword + "%");
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("id_pemasok"),
                rs.getString("nama_pemasok"),
                rs.getString("no_telp"),
                rs.getString("alamat")
            });
        }

        tbl_pmasok.setModel(model);

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

        page_main = new javax.swing.JPanel();
        page_pemasok = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        btn_search = new javax.swing.JButton();
        btn_tambah1 = new javax.swing.JButton();
        txt_search = new javax.swing.JTextField();
        btn_tambah = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_hapus = new javax.swing.JButton();
        btn_ubah = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_username = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_pmasok = new custom.JTable_customAutoresize();

        setLayout(new java.awt.CardLayout());

        page_main.setBackground(new java.awt.Color(255, 244, 232));
        page_main.setLayout(new java.awt.CardLayout());

        page_pemasok.setBackground(new java.awt.Color(255, 244, 232));
        page_pemasok.setForeground(new java.awt.Color(230, 230, 230));
        page_pemasok.setPreferredSize(new java.awt.Dimension(836, 666));
        page_pemasok.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 74.png"))); // NOI18N
        page_pemasok.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 27, 41, 37));

        btn_search.setContentAreaFilled(false);

        btn_search.setBorderPainted(false);
        btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Search.png"))); // NOI18N
        btn_search.setContentAreaFilled(false);
        btn_search.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Search Select.png"))); // NOI18N
        btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchActionPerformed(evt);
            }
        });
        page_pemasok.add(btn_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 170, 50, 40));

        btn_tambah.setContentAreaFilled(false);

        btn_tambah.setBorderPainted(false);
        btn_tambah1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/pemasok/tambah pemasok.png"))); // NOI18N
        btn_tambah1.setContentAreaFilled(false);
        btn_tambah1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/pemasok/tambah pemasok (press).png"))); // NOI18N
        btn_tambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah1ActionPerformed(evt);
            }
        });
        page_pemasok.add(btn_tambah1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 210, 50));

        txt_search.setBackground(new java.awt.Color(238, 236, 227));
        txt_search.setBorder(null);
        page_pemasok.add(txt_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 181, 290, 20));

        btn_tambah.setContentAreaFilled(false);

        btn_tambah.setBorderPainted(false);
        btn_tambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/pemasok/tambah pemasok.png"))); // NOI18N
        btn_tambah.setContentAreaFilled(false);
        btn_tambah.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/pemasok/tambah pemasok (press).png"))); // NOI18N
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });
        page_pemasok.add(btn_tambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 210, 50));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/pemasok/Pemasok.png"))); // NOI18N
        page_pemasok.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 250, 37));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Search.png"))); // NOI18N
        page_pemasok.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 410, -1));

        btn_hapus.setContentAreaFilled(false);
        btn_hapus.setBorderPainted(false);
        btn_hapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Hapus.png"))); // NOI18N
        btn_hapus.setContentAreaFilled(false);
        btn_hapus.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/Button Hapus Select.png"))); // NOI18N
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });
        page_pemasok.add(btn_hapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 170, -1, 40));

        btn_ubah.setContentAreaFilled(false);

        btn_ubah.setBorderPainted(false);
        btn_ubah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/pemasok/ubah 1.png"))); // NOI18N
        btn_ubah.setContentAreaFilled(false);
        btn_ubah.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/pemasok/ubah.png"))); // NOI18N
        btn_ubah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ubahActionPerformed(evt);
            }
        });
        page_pemasok.add(btn_ubah, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 170, -1, 40));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/penyewaan/BG Button.png"))); // NOI18N
        page_pemasok.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 720, 65));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/dashpeg/Group 28.png"))); // NOI18N
        page_pemasok.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 69));

        label_username.setText("Username");
        page_pemasok.add(label_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, 20));
        page_pemasok.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 27, 250, 37));

        tbl_pmasok.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tbl_pmasok);

        page_pemasok.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, 740, 380));

        page_main.add(page_pemasok, "card2");

        add(page_main, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:

    int row = tbl_pmasok.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(null, "Pilih data yang ingin diedit!");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) tbl_pmasok.getModel();
    String id = (String) model.getValueAt(row, 0);
    String nama = (String) model.getValueAt(row, 1);
    String telp = (String) model.getValueAt(row, 2);
    String alamat = (String) model.getValueAt(row, 3);

    PanelPemasok frameEdit = new PanelPemasok(id, nama, telp, alamat);

    // Gunakan lambda untuk lebih sederhana
    frameEdit.setRefreshListener(() -> loadDataPemasok());

    frameEdit.setLocationRelativeTo(null);
    frameEdit.setVisible(true);



    }//GEN-LAST:event_btn_ubahActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        int row = tbl_pmasok.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(null, "Pilih data yang ingin dihapus!");
        return;
    }

    String id = tbl_pmasok.getValueAt(row, 0).toString();
    int konfirmasi = JOptionPane.showConfirmDialog(null, "Hapus data dengan ID " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

    if (konfirmasi == JOptionPane.YES_OPTION) {
        try {
            Koneksi.config();
            Connection conn = Koneksi.getConnection();
            String sql = "DELETE FROM pemasok WHERE id_pemasok=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
            loadDataPemasok();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal hapus data: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
                                                                                       
    PanelPemasok framePemasok = new PanelPemasok();
    framePemasok.setLocationRelativeTo(null);
    
    framePemasok.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            loadDataPemasok(); // Reload data setelah jendela ditutup
        }
    });

    framePemasok.setVisible(true);



    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_tambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah1ActionPerformed
                                            
    PanelPemasok framePemasok = new PanelPemasok();
    framePemasok.setLocationRelativeTo(null);

    framePemasok.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            loadDataPemasok();
        }
    });

    framePemasok.setVisible(true);



    }//GEN-LAST:event_btn_tambah1ActionPerformed

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed
        // TODO add your handling code here:
        cariPemasok();
    }//GEN-LAST:event_btn_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tambah1;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_username;
    private javax.swing.JPanel page_main;
    private javax.swing.JPanel page_pemasok;
    private custom.JTable_customAutoresize tbl_pmasok;
    private javax.swing.JTextField txt_search;
    // End of variables declaration//GEN-END:variables
}
