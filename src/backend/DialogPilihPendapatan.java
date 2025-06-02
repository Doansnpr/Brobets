
package backend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DialogPilihPendapatan extends JDialog {

    private int pilihan = -1; // 0 = penyewaan, 1 = pengembalian, -1 = batal

    public DialogPilihPendapatan(Frame parent) {
        super(parent, "Pilih Jenis Pendapatan", true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Pilih jenis pendapatan yang ingin ditampilkan:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton btnPenyewaan = new JButton("Pendapatan Penyewaan");
        btnPenyewaan.setBackground(new Color(0, 123, 255));
        btnPenyewaan.setForeground(Color.WHITE);
        btnPenyewaan.setFocusPainted(false);
        btnPenyewaan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPenyewaan.setPreferredSize(new Dimension(180, 40));
        btnPenyewaan.addActionListener(e -> {
            pilihan = 0;
            dispose();
        });

        JButton btnPengembalian = new JButton("Pendapatan Pengembalian");
        btnPengembalian.setBackground(new Color(40, 167, 69));
        btnPengembalian.setForeground(Color.WHITE);
        btnPengembalian.setFocusPainted(false);
        btnPengembalian.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPengembalian.setPreferredSize(new Dimension(180, 40));
        btnPengembalian.addActionListener(e -> {
            pilihan = 1;
            dispose();
        });

        btnPanel.add(btnPenyewaan);
        btnPanel.add(btnPengembalian);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> {
            pilihan = -1;
            dispose();
        });
        bottomPanel.add(btnBatal);

        panel.add(label, BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
        pack();
    }

    public int getPilihan() {
        return pilihan;
    }
}

