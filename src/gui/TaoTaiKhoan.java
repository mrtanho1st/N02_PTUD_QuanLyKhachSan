package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import controller.TaoTaiKhoanController;
import dao.TaiKhoan_Dao.NhanVienItem;



public class TaoTaiKhoan extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(238, 243, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_PRIMARY_BG = new Color(220, 235, 255);
    private static final Color BUTTON_GHOST_BG = new Color(238, 245, 255);
    private static final Color BUTTON_DANGER_BG = new Color(252, 230, 230);

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JPasswordField txtNhapLaiMatKhau;
    private JComboBox<String> cbVaiTro;
    private JRadioButton rdoHoatDong;
    private JRadioButton rdoKhoa;

    private JComboBox<NhanVienItem> cbNhanVien;
    private JTextField txtHoTen;
    private JTextField txtGioiTinh;
    private JTextField txtNgaySinh;
    private JTextField txtSoDienThoai;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtViTriCongViec;

    private JButton btnTaoTaiKhoan;
    private JButton btnLamMoi;
    private JButton btnHuy;

    public TaoTaiKhoan() {
        setLayout(new BorderLayout(0, 12));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        add(createMainContent(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);

        JPanel formWrapper = new JPanel(new GridLayout(1, 2, 12, 0));
        formWrapper.setOpaque(false);

        JPanel accountPanel = createAccountPanel();
        JPanel userPanel = createUserPanel();

        accountPanel.setMinimumSize(new Dimension(360, 0));
        userPanel.setMinimumSize(new Dimension(360, 0));

        formWrapper.add(accountPanel);
        formWrapper.add(userPanel);

        JScrollPane scrollPane = new JScrollPane(formWrapper);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(APP_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        content.add(scrollPane, BorderLayout.CENTER);

        return content;
    }

    private JPanel createAccountPanel() {
        JPanel card = new RoundedPanel(20, CARD_BG);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = createSectionTitle("THÔNG TIN TÀI KHOẢN");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = createGbc();


        txtTenDangNhap = createInputField("");
        txtMatKhau = new JPasswordField();
        txtNhapLaiMatKhau = new JPasswordField();

        cbVaiTro = new JComboBox<>(new String[] {
                "Nhân viên quản lý",
                "Nhân viên lễ tân"
        });

        rdoHoatDong = new JRadioButton("Hoạt động");
        rdoKhoa = new JRadioButton("Khóa");

        rdoHoatDong.setSelected(true);
        rdoHoatDong.setOpaque(false);
        rdoKhoa.setOpaque(false);

        rdoHoatDong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rdoKhoa.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        ButtonGroup group = new ButtonGroup();
        group.add(rdoHoatDong);
        group.add(rdoKhoa);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(rdoHoatDong);
        statusPanel.add(rdoKhoa);

        addFormRow(form, gbc, 0, "Tên đăng nhập", txtTenDangNhap);
        addFormRow(form, gbc, 1, "Mật khẩu", txtMatKhau);
        addFormRow(form, gbc, 2, "Nhập lại MK", txtNhapLaiMatKhau);
        addFormRow(form, gbc, 3, "Vai trò", cbVaiTro);
        addFormRow(form, gbc, 4, "Trạng thái", statusPanel);

        card.add(title, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        return card;
    }

    private JPanel createUserPanel() {
        JPanel card = new RoundedPanel(20, CARD_BG);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = createSectionTitle("THÔNG TIN NGƯỜI DÙNG");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = createGbc();

        cbNhanVien = new JComboBox<>();
        txtHoTen = createInputField("");
        txtGioiTinh = createInputField("");
        txtNgaySinh = createInputField("");
        txtSoDienThoai = createInputField("");
        txtEmail = createInputField("");
        txtDiaChi = createInputField("");
        txtViTriCongViec = createInputField("");

        txtHoTen.setEditable(false);
        txtGioiTinh.setEditable(false);
        txtNgaySinh.setEditable(false);
        txtSoDienThoai.setEditable(false);
        txtEmail.setEditable(false);
        txtDiaChi.setEditable(false);
        txtViTriCongViec.setEditable(false);

        addFormRow(form, gbc, 0, "Nhân viên", cbNhanVien);
        addFormRow(form, gbc, 1, "Họ tên", txtHoTen);
        addFormRow(form, gbc, 2, "Giới tính", txtGioiTinh);
        addFormRow(form, gbc, 3, "Ngày sinh", txtNgaySinh);
        addFormRow(form, gbc, 4, "Số điện thoại", txtSoDienThoai);
        addFormRow(form, gbc, 5, "Email", txtEmail);
        addFormRow(form, gbc, 6, "Địa chỉ", txtDiaChi);
        addFormRow(form, gbc, 7, "Vị Trí", txtViTriCongViec);

        card.add(title, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        return card;
    }

    private JPanel createActionPanel() {
        JPanel actions = new RoundedPanel(20, CARD_BG);
        actions.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));
        actions.setBorder(new EmptyBorder(4, 10, 4, 10));

        btnTaoTaiKhoan = createPrimaryButton("Tạo tài khoản", 150, 40);
        btnLamMoi = createGhostButton("Làm mới", 120, 40);
        btnHuy = createDangerButton("Hủy", 100, 40);

        actions.add(btnTaoTaiKhoan);
        actions.add(btnLamMoi);
        actions.add(btnHuy);

        return actions;
    }

    private JLabel createSectionTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        return title;
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addFormRow(JPanel container, GridBagConstraints gbc, int row, String labelText, Component input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(150, 40));
        label.setMinimumSize(new Dimension(100, 40));

        input.setPreferredSize(new Dimension(360, 40));
        input.setMinimumSize(new Dimension(120, 40));

        if (input instanceof JTextField) {
            JTextField textField = (JTextField) input;
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 214, 235), 1),
                    new EmptyBorder(6, 10, 6, 10)));
            textField.setForeground(TEXT);
            textField.setBackground(Color.WHITE);
        }

        if (input instanceof JPasswordField) {
            JPasswordField passwordField = (JPasswordField) input;
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 214, 235), 1),
                    new EmptyBorder(6, 10, 6, 10)));
            passwordField.setForeground(TEXT);
            passwordField.setBackground(Color.WHITE);
        }

        if (input instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) input;
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        container.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(input, gbc);
    }

    private JTextField createInputField(String initialText) {
        JTextField textField = new JTextField(initialText);
        textField.setEditable(true);
        textField.setEnabled(true);
        textField.setFocusable(true);
        return textField;
    }

    private JButton createPrimaryButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_PRIMARY_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(188, 207, 233), 1));
        return button;
    }

    private JButton createGhostButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_GHOST_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(188, 207, 233), 1));
        return button;
    }

    private JButton createDangerButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_DANGER_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(235, 184, 184), 1));
        return button;
    }

    public void clearForm() {
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtNhapLaiMatKhau.setText("");
        rdoHoatDong.setSelected(true);

        if (cbVaiTro.getItemCount() > 0) {
            cbVaiTro.setSelectedIndex(0);
        }

        if (cbNhanVien.getItemCount() > 0) {
            cbNhanVien.setSelectedIndex(0);
        } else {
            clearNhanVienInfo();
        }
    }

    public void clearNhanVienInfo() {
        txtHoTen.setText("");
        txtGioiTinh.setText("");
        txtNgaySinh.setText("");
        txtSoDienThoai.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtViTriCongViec.setText("");
    }

    public void fillNhanVienInfo(NhanVienItem nv) {
        if (nv == null) {
            clearNhanVienInfo();
            return;
        }


        txtHoTen.setText(nullToEmpty(nv.getHoTen()));
        txtGioiTinh.setText(nullToEmpty(nv.getGioiTinh()));
        txtNgaySinh.setText(nullToEmpty(nv.getNgaySinh()));
        txtSoDienThoai.setText(nullToEmpty(nv.getSdt()));
        txtEmail.setText(nullToEmpty(nv.getEmail()));
        txtDiaChi.setText(nullToEmpty(nv.getDiaChi()));
        txtViTriCongViec.setText(nullToEmpty(nv.getViTriCongViec()));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public JTextField getTxtTenDangNhap() {
        return txtTenDangNhap;
    }

    public JPasswordField getTxtMatKhau() {
        return txtMatKhau;
    }

    public JPasswordField getTxtNhapLaiMatKhau() {
        return txtNhapLaiMatKhau;
    }

    public JComboBox<String> getCbVaiTro() {
        return cbVaiTro;
    }

    public JComboBox<NhanVienItem> getCbNhanVien() {
        return cbNhanVien;
    }

    public JButton getBtnTaoTaiKhoan() {
        return btnTaoTaiKhoan;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JButton getBtnHuy() {
        return btnHuy;
    }

    private static class RoundedPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color color;

        RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(color);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2d.setColor(new Color(214, 226, 243));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2d.dispose();
            super.paintComponent(g);
        }
    }

    public static JPanel createPanel() {
        TaoTaiKhoan view = new TaoTaiKhoan();
        new TaoTaiKhoanController(view);
        return view;
    }
}