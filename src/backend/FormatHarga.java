/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

/**
 *
 * @author Khafila Maulidiyah W
 */

import javax.swing.text.*;
import java.text.NumberFormat;
import java.util.Locale;


public class FormatHarga extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null) return;
        if (string.matches("\\d+")) {
            replace(fb, offset, 0, string, attr);
        }
        // else ignore input bukan angka
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text == null) return;

        // hanya izinkan digit, jika text ada karakter lain abaikan saja
        if (!text.matches("\\d*")) {
            return;
        }

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        // ambil hanya digit
        String angka = sb.toString().replaceAll("[^\\d]", "");

        if (angka.isEmpty()) {
            super.replace(fb, 0, doc.getLength(), "", attrs);
            return;
        }

        // format ke rupiah
        String formatted = formatRupiah(angka);

        // ganti semua isi dokumen dengan format rupiah
        super.replace(fb, 0, doc.getLength(), formatted, attrs);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

        String angka = sb.toString().replaceAll("[^\\d]", "");
        if (angka.isEmpty()) {
            super.replace(fb, 0, doc.getLength(), "", null);
            return;
        }
        String formatted = formatRupiah(angka);
        super.replace(fb, 0, doc.getLength(), formatted, null);
    }

    private String formatRupiah(String angka) {
        try {
            long nilai = Long.parseLong(angka);
            NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
            return "Rp " + formatter.format(nilai);
        } catch (NumberFormatException e) {
            return "Rp 0";
        }
    }
}