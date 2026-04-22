package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import entity.DonDatPhong;



public class PhongCardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);

    private final DonDatPhong data;

    public PhongCardPanel(DonDatPhong data) {
        this.data = data;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 8));
        setPreferredSize(new Dimension(160, 95));
        setBackground(getStatusColor(data.getTrangThaiPhong()));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblMaPhong = new JLabel(data.getMaPhong());
        lblMaPhong.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblMaPhong.setForeground(TEXT_DARK);

        JLabel lblLoaiPhong = new JLabel(data.getLoaiPhong(), SwingConstants.RIGHT);
        lblLoaiPhong.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoaiPhong.setForeground(TEXT_DARK);

        JLabel lblTrangThai = new JLabel(data.getTrangThaiPhong());
        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTrangThai.setForeground(TEXT_DARK);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblMaPhong, BorderLayout.WEST);
        top.add(lblLoaiPhong, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(lblTrangThai, BorderLayout.CENTER);
    }

    public DonDatPhong getData() {
        return data;
    }

    private Color getStatusColor(String trangThai) {
        switch (trangThai) {
            case "Trống":
                return new Color(103, 215, 146);
            case "Đã đặt":
                return new Color(233, 222, 178);
            case "Đang sử dụng":
                return new Color(230, 205, 205);
            case "Bảo trì":
                return new Color(220, 220, 220);
            default:
                return Color.WHITE;
        }
    }
}