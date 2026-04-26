package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import entity.Phong;

public class PhongCardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int CARD_WIDTH = 300;
    private static final int CARD_HEIGHT = 180;

    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);

    private final Phong data;

    public PhongCardPanel(Phong data) {
        this.data = data;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 8));

        Dimension fixedSize = new Dimension(CARD_WIDTH, CARD_HEIGHT);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);

        String trangThai = data.getTrangThai();

        setBackground(getStatusColor(trangThai));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblMaPhong = new JLabel(data.getMaPhong());
        lblMaPhong.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMaPhong.setForeground(TEXT_DARK);

        JLabel lblTrangThai = new JLabel(trangThai);
        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTrangThai.setForeground(TEXT_DARK);
        lblTrangThai.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(lblMaPhong, BorderLayout.WEST);
        topPanel.add(lblTrangThai, BorderLayout.EAST);

        JLabel lblLoaiPhong = new JLabel(data.getLoaiPhong());
        lblLoaiPhong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLoaiPhong.setForeground(TEXT_DARK);

        JLabel lblGia = new JLabel("Giá: " + formatMoney(data.getGiaPhong()));
        lblGia.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblGia.setForeground(TEXT_DARK);

        JLabel lblSoNguoi = new JLabel("Tối đa: " + data.getSoNguoiToiDa() + " người");
        lblSoNguoi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSoNguoi.setForeground(TEXT_DARK);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        infoPanel.setOpaque(false);
        infoPanel.add(lblLoaiPhong);
        infoPanel.add(lblGia);
        infoPanel.add(lblSoNguoi);

        JLabel lblIconNguoi = new JLabel();
        lblIconNguoi.setOpaque(false);
        lblIconNguoi.setBorder(null);
        lblIconNguoi.setHorizontalAlignment(SwingConstants.RIGHT);
        lblIconNguoi.setIcon(getPeopleIcon(data.getSoNguoiToiDa(), 80, 80));

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.add(lblIconNguoi, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }

    public Phong getData() {
        return data;
    }

    private Color getStatusColor(String trangThai) {
        if (trangThai == null) {
            return Color.WHITE;
        }

        switch (trangThai) {
            case "Trống":
                return new Color(103, 215, 146, 30);

            case "Đã đặt":
                return new Color(233, 222, 178);

            case "Đang sử dụng":
            case "Đã nhận":
                return new Color(230, 205, 205);

            case "Bảo trì":
                return new Color(220, 220, 220);

            default:
                return Color.WHITE;
        }
    }

    private ImageIcon getPeopleIcon(int soNguoi, int width, int height) {
        String fileName;

        if (soNguoi <= 1) {
            fileName = "people_1_32.png";
        } else if (soNguoi == 2) {
            fileName = "people_2_32.png";
        } else {
            fileName = "people_3_32.png";
        }

        ImageIcon icon = null;

        // Cách 1: tìm trong src/resource/classpath
        java.net.URL url = getClass().getResource("/icon/" + fileName);

        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            // Cách 2: tìm theo đường dẫn folder icon ngoài src
            String path = "icon/" + fileName;
            icon = new ImageIcon(path);

            if (icon.getIconWidth() == -1) {
                System.err.println("Không tìm thấy icon:");
                System.err.println("Resource: /icon/" + fileName);
                System.err.println("File path: " + path);
                System.err.println("Project đang chạy tại: " + System.getProperty("user.dir"));
                return null;
            }
        }

        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private String formatMoney(double gia) {
        return String.format("%,.0f VNĐ", gia);
    }
}