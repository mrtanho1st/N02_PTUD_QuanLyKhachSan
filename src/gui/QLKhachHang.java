package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.QLKhachHangController;

public class QLKhachHang extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(238, 243, 250);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_PRIMARY_BG = new Color(220, 235, 255);
    private static final Color BUTTON_GHOST_BG = new Color(238, 245, 255);
    private static final Color BUTTON_DANGER_BG = new Color(252, 230, 230);
    private static final Color BORDER_COLOR = new Color(196, 210, 230);

    private JTextField txtTimMaKH;
    private JTextField txtTimTenKH;
    private JComboBox<String> cboTimLoaiKH;

    private JTable tblKhachHang;
    private DefaultTableModel tableModel;

    private JTextField txtMaKH;
    private JTextField txtHoTen;
    private JTextField txtSDT;
    private JTextField txtCCCD;
    private JComboBox<String> cboLoaiKH;
    private JTextField txtDiemSo;

    private JButton btnTim;
    private JButton btnLamMoiTim;
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoiForm;

    public QLKhachHang() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(12, 14, 12, 14));
        add(createBody(), BorderLayout.CENTER);
    }

    public static JPanel createPanel() {
        QLKhachHang panel = new QLKhachHang();
        new QLKhachHangController(panel);
        return panel;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);

        body.add(createFilterPanel(), BorderLayout.NORTH);
        body.add(createCenterPanel(), BorderLayout.CENTER);

        return body;
    }

    private JPanel createFilterPanel() {
        JPanel filter = new RoundedPanel(20, CARD_BG);
        filter.setLayout(new GridBagLayout());
        filter.setBorder(new EmptyBorder(12, 14, 12, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblMaKH = createFilterLabel("Mã KH:");
        JLabel lblTenKH = createFilterLabel("Tên KH:");
        JLabel lblLoaiKH = createFilterLabel("Loại KH:");

        txtTimMaKH = createTextField(170, 36);
        txtTimTenKH = createTextField(220, 36);

        cboTimLoaiKH = new JComboBox<>(new String[] {
                "Tất cả", "Thường", "Thân thiết", "VIP"
        });
        styleComboBox(cboTimLoaiKH, 150, 36);

        btnTim = createPrimaryButton("Tìm kiếm", 120, 36);
        btnLamMoiTim = createGhostButton("Làm mới", 110, 36);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        filter.add(lblMaKH, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        filter.add(txtTimMaKH, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        filter.add(lblTenKH, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        filter.add(txtTimTenKH, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        filter.add(lblLoaiKH, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.6;
        filter.add(cboTimLoaiKH, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        filter.add(btnTim, gbc);

        gbc.gridx = 7;
        gbc.weightx = 0;
        filter.add(btnLamMoiTim, gbc);

        return filter;
    }

    private JPanel createCenterPanel() {
    	JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createTablePanel(),
                createDetailPanel());
        splitPane.setDividerLocation(760);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(APP_BG);
        wrapper.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Thông tin khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaKH = createTextField(220, 36);
        txtHoTen = createTextField(220, 36);
        txtSDT = createTextField(220, 36);
        txtCCCD = createTextField(220, 36);
        txtDiemSo = createTextField(220, 36);

        cboLoaiKH = new JComboBox<>(new String[] { "Thường", "Thân thiết", "VIP" });
        styleComboBox(cboLoaiKH, 220, 36);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã KH", txtMaKH);
        addFormRow(formPanel, gbc, "Họ tên", txtHoTen);
        addFormRow(formPanel, gbc, "Số điện thoại", txtSDT);
        addFormRow(formPanel, gbc, "CCCD", txtCCCD);
        addFormRow(formPanel, gbc, "Phân loại", cboLoaiKH);
        addFormRow(formPanel, gbc, "Điểm số", txtDiemSo);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(formScrollPane, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new Object[] { "Mã KH", "Họ tên", "SĐT", "CCCD", "Phân loại", "Điểm" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = new JTable(tableModel);
        tblKhachHang.setRowHeight(34);
        tblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblKhachHang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblKhachHang.getTableHeader().setBackground(new Color(231, 240, 252));
        tblKhachHang.getTableHeader().setForeground(TEXT);
        tblKhachHang.setGridColor(new Color(229, 236, 247));
        tblKhachHang.setShowVerticalLines(false);
        tblKhachHang.setSelectionBackground(new Color(210, 229, 255));
        tblKhachHang.setSelectionForeground(TEXT);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        tblKhachHang.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        tblKhachHang.getColumnModel().getColumn(2).setCellRenderer(centerAlign);
        tblKhachHang.getColumnModel().getColumn(4).setCellRenderer(centerAlign);
//        tblKhachHang.getColumnModel().getColumn(6).setCellRenderer(centerAlign);

        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 229, 244), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    private JPanel createActionPanel() {
        JPanel actions = new RoundedPanel(20, CARD_BG);
        actions.setLayout(new BorderLayout());
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        btnThem = createPrimaryButton("Thêm", 160, 46);
        btnCapNhat = createPrimaryButton("Cập nhật", 160, 46);
        btnXoa = createDangerButton("Xóa", 160, 46);
        btnLamMoiForm = createGhostButton("Làm mới", 160, 46);

        grid.add(btnThem);
        grid.add(btnCapNhat);
        grid.add(btnXoa);
        grid.add(btnLamMoiForm);

        actions.add(grid, BorderLayout.CENTER);
        return actions;
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        return label;
    }

    private JTextField createTextField(int width, int height) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(width, height));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 10, 6, 10)));
        return textField;
    }

    private void styleComboBox(JComboBox<?> comboBox, int width, int height) {
        comboBox.setPreferredSize(new Dimension(width, height));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void addFormRow(JPanel container, GridBagConstraints gbc, String labelText, Component input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(110, 36));

        gbc.gridx = 0;
        gbc.weightx = 0;
        container.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        container.add(input, gbc);

        gbc.gridy++;
    }

    private JButton createPrimaryButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_DANGER_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(235, 184, 184), 1));
        return button;
    }

    public JTextField getTxtTimMaKH() {
        return txtTimMaKH;
    }

    public JTextField getTxtTimTenKH() {
        return txtTimTenKH;
    }

    public JComboBox<String> getCboTimLoaiKH() {
        return cboTimLoaiKH;
    }

    public JTable getTblKhachHang() {
        return tblKhachHang;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtMaKH() {
        return txtMaKH;
    }

    public JTextField getTxtHoTen() {
        return txtHoTen;
    }

    public JTextField getTxtSDT() {
        return txtSDT;
    }
    public JTextField getTxtDiemSo() {
    	return txtDiemSo;
    }

    

    public JTextField getTxtCCCD() {
        return txtCCCD;
    }

    public JComboBox<String> getCboLoaiKH() {
        return cboLoaiKH;
    }

    public JButton getBtnTim() {
        return btnTim;
    }

    public JButton getBtnLamMoiTim() {
        return btnLamMoiTim;
    }

    public JButton getBtnThem() {
        return btnThem;
    }

    public JButton getBtnCapNhat() {
        return btnCapNhat;
    }

    public JButton getBtnXoa() {
        return btnXoa;
    }

    public JButton getBtnLamMoiForm() {
        return btnLamMoiForm;
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
}