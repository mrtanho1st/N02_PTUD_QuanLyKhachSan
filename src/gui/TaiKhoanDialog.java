package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import controller.TaiKhoanController;

public class TaiKhoanDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color WRAPPER_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    private JLabel lblTenDangNhap;
    private JLabel lblVaiTro;
    private JLabel lblMaNV;
    private JLabel lblTenNV;
    private JLabel lblEmail;
    private JLabel lblSDT;
    private JLabel lblViTri;

    private JPanel pnlDoiMatKhau;

    private JPasswordField txtMatKhauHienTai;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhanMatKhauMoi;

    private JButton btnHienDoiMatKhau;
    private JButton btnLuuMatKhau;
    private JButton btnHuyDoiMatKhau;
    private JButton btnDong;

    private TaiKhoanController controller;

    public TaiKhoanDialog() {
        setTitle("Thông tin tài khoản");
        setModal(true);
        setSize(560, 520);
        setResizable(false);

        initUI();

        controller = new TaiKhoanController(this);
        controller.loadThongTinTaiKhoan();

        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(WRAPPER_BG);
        wrapper.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JLabel lblTitle = new JLabel("Thông tin tài khoản");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_DARK);

        panel.add(lblTitle, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(createBottomPanel(), BorderLayout.SOUTH);

        wrapper.add(panel, BorderLayout.CENTER);
        setContentPane(wrapper);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(PANEL_BG);

        panel.add(createInfoPanel(), BorderLayout.NORTH);
        panel.add(createDoiMatKhauPanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        lblTenDangNhap = createValueLabel();
        lblVaiTro = createValueLabel();
        lblMaNV = createValueLabel();
        lblTenNV = createValueLabel();
        lblEmail = createValueLabel();
        lblSDT = createValueLabel();
        lblViTri = createValueLabel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addInfoRow(panel, gbc, 0, "Tên đăng nhập:", lblTenDangNhap);
        addInfoRow(panel, gbc, 1, "Vai trò:", lblVaiTro);
        addInfoRow(panel, gbc, 2, "Mã nhân viên:", lblMaNV);
        addInfoRow(panel, gbc, 3, "Tên nhân viên:", lblTenNV);
        addInfoRow(panel, gbc, 4, "Email:", lblEmail);
        addInfoRow(panel, gbc, 5, "SĐT:", lblSDT);
        addInfoRow(panel, gbc, 6, "Vị trí:", lblViTri);

        return panel;
    }

    private JPanel createDoiMatKhauPanel() {
        pnlDoiMatKhau = new JPanel(new GridBagLayout());
        pnlDoiMatKhau.setBackground(Color.WHITE);
        pnlDoiMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTitle = new JLabel("Đổi mật khẩu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_DARK);

        txtMatKhauHienTai = new JPasswordField();
        txtMatKhauMoi = new JPasswordField();
        txtXacNhanMatKhauMoi = new JPasswordField();

        stylePasswordField(txtMatKhauHienTai);
        stylePasswordField(txtMatKhauMoi);
        stylePasswordField(txtXacNhanMatKhauMoi);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        pnlDoiMatKhau.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        addPasswordRow(pnlDoiMatKhau, gbc, 1, "Mật khẩu hiện tại:", txtMatKhauHienTai);
        addPasswordRow(pnlDoiMatKhau, gbc, 2, "Mật khẩu mới:", txtMatKhauMoi);
        addPasswordRow(pnlDoiMatKhau, gbc, 3, "Xác nhận mật khẩu:", txtXacNhanMatKhauMoi);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnLuuMatKhau = createButton("Lưu mật khẩu");
        btnHuyDoiMatKhau = createDangerButton("Hủy");

        buttonPanel.add(btnLuuMatKhau);
        buttonPanel.add(btnHuyDoiMatKhau);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        pnlDoiMatKhau.add(buttonPanel, gbc);

        pnlDoiMatKhau.setVisible(false);

        return pnlDoiMatKhau;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setBackground(PANEL_BG);

        btnHienDoiMatKhau = createButton("Đổi mật khẩu");
        btnDong = createDangerButton("Đóng");

        panel.add(btnHienDoiMatKhau);
        panel.add(btnDong);

        return panel;
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JLabel valueLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        JLabel label = createLabel(labelText);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(valueLabel, gbc);
    }

    private void addPasswordRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JPasswordField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        JLabel label = createLabel(labelText);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(150, 32));
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(240, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT_DARK);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        button.setPreferredSize(new Dimension(150, 42));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_DANGER);
        return button;
    }

    public void hienThiFormDoiMatKhau(boolean hienThi) {
        pnlDoiMatKhau.setVisible(hienThi);
        btnHienDoiMatKhau.setVisible(!hienThi);

        if (hienThi) {
            setSize(560, 620);
            txtMatKhauHienTai.requestFocus();
        } else {
            setSize(560, 520);
            clearMatKhauForm();
        }

        revalidate();
        repaint();
        setLocationRelativeTo(getOwner());
    }

    public void clearMatKhauForm() {
        txtMatKhauHienTai.setText("");
        txtMatKhauMoi.setText("");
        txtXacNhanMatKhauMoi.setText("");
    }

    public String getMatKhauHienTai() {
        return new String(txtMatKhauHienTai.getPassword());
    }

    public String getMatKhauMoi() {
        return new String(txtMatKhauMoi.getPassword());
    }

    public String getXacNhanMatKhauMoi() {
        return new String(txtXacNhanMatKhauMoi.getPassword());
    }

    public JLabel getLblTenDangNhap() {
        return lblTenDangNhap;
    }

    public JLabel getLblVaiTro() {
        return lblVaiTro;
    }

    public JLabel getLblMaNV() {
        return lblMaNV;
    }

    public JLabel getLblTenNV() {
        return lblTenNV;
    }

    public JLabel getLblEmail() {
        return lblEmail;
    }

    public JLabel getLblSDT() {
        return lblSDT;
    }

    public JLabel getLblViTri() {
        return lblViTri;
    }

    public JButton getBtnHienDoiMatKhau() {
        return btnHienDoiMatKhau;
    }

    public JButton getBtnLuuMatKhau() {
        return btnLuuMatKhau;
    }

    public JButton getBtnHuyDoiMatKhau() {
        return btnHuyDoiMatKhau;
    }

    public JButton getBtnDong() {
        return btnDong;
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}