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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.QLPhanQuyenController;
import dao.TaiKhoan_Dao.NhanVienItem;

public class QLPhanQuyen extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(238, 243, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_PRIMARY_BG = new Color(220, 235, 255);
    private static final Color BUTTON_GHOST_BG = new Color(238, 245, 255);
    private static final Color BUTTON_DANGER_BG = new Color(252, 230, 230);

    private JTextField txtTimKiem;
    private JComboBox<String> cbTimTheo;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JComboBox<NhanVienItem> cbNhanVien;
    private JComboBox<String> cbVaiTro;


    private JButton btnLamMoiTimKiem;
    
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoiForm;

    public QLPhanQuyen() {
        setLayout(new BorderLayout(0, 12));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel filter = new RoundedPanel(20, CARD_BG);
        filter.setLayout(new BorderLayout(12, 0));
        filter.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel lbSearch = new JLabel("Tìm tài khoản:");
        lbSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbSearch.setForeground(TEXT);

        txtTimKiem = createInputField("");
        txtTimKiem.setPreferredSize(new Dimension(320, 36));

        cbTimTheo = new JComboBox<>(new String[] {
                "Tên đăng nhập", "Mã nhân viên", "Họ tên nhân viên", "Vai trò"
        });
        cbTimTheo.setPreferredSize(new Dimension(180, 36));
        cbTimTheo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        left.add(lbSearch);
        left.add(txtTimKiem);
        left.add(cbTimTheo);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);


        btnLamMoiTimKiem = createGhostButton("Làm mới", 120, 36);

        
        right.add(btnLamMoiTimKiem);

        filter.add(left, BorderLayout.WEST);
        filter.add(right, BorderLayout.EAST);

        return filter;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setOpaque(false);

        JPanel tablePanel = createTablePanel();
        JPanel rightPanel = createRightPanel();

        tablePanel.setMinimumSize(new Dimension(560, 0));
        rightPanel.setMinimumSize(new Dimension(480, 0));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tablePanel,
                rightPanel
        );

        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.58);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setContinuousLayout(true);

        center.add(splitPane, BorderLayout.CENTER);

        return center;
    }
    
    private JPanel createRightPanel() {
        JPanel right = new JPanel(new BorderLayout(0, 12));
        right.setOpaque(false);

        JPanel formPanel = createFormPanel();

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(APP_BG);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.getVerticalScrollBar().setBlockIncrement(80);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        right.add(formScroll, BorderLayout.CENTER);
        right.add(createActionPanel(), BorderLayout.SOUTH);

        return right;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách tài khoản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new Object[] { "Tên đăng nhập", "Mật khẩu", "Mã NV", "Họ tên", "Vai trò" }, 0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(231, 240, 252));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(new Color(229, 236, 247));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(210, 229, 255));
        table.setSelectionForeground(TEXT);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(3).setCellRenderer(centerAlign);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 229, 244), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    private JPanel createFormPanel() {
        JPanel formCard = new RoundedPanel(20, CARD_BG);
        formCard.setLayout(new BorderLayout(0, 16));
        formCard.setBorder(new EmptyBorder(24, 28, 24, 28));
        formCard.setPreferredSize(new Dimension(500, 620));
        formCard.setMinimumSize(new Dimension(460, 520));

        JLabel title = new JLabel("Thông tin phân quyền");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTenDangNhap = createInputField("");
        txtMatKhau = new JPasswordField();
        txtMatKhau.setEditable(false);
        txtMatKhau.setEnabled(false);
        txtMatKhau.setFocusable(false);
        cbNhanVien = new JComboBox<>();
        cbVaiTro = new JComboBox<>(new String[] {
                "Nhân viên quản lý",
                "Nhân viên lễ tân"
        });

        addFormRow(form, gbc, 0, "Tên đăng nhập", txtTenDangNhap);
        addFormRow(form, gbc, 1, "Mật khẩu", txtMatKhau);
        addFormRow(form, gbc, 2, "Nhân viên", cbNhanVien);
        addFormRow(form, gbc, 3, "Vai trò", cbVaiTro);

        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setOpaque(false);
        notePanel.setPreferredSize(new Dimension(0, 150));
        notePanel.setMinimumSize(new Dimension(0, 150));
        notePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(218, 229, 244), 1),
                "Mô tả quyền"
        ));

        javax.swing.JTextArea note = new javax.swing.JTextArea();
        note.setText(
                "Nhân viên quản lý: Toàn diện hệ thống\n\n"
              + "Nhân viên lễ tân: xử lý nghiệp vụ, tìm kiếm và cập nhật thông tin."
        );
        note.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        note.setForeground(TEXT);
        note.setBackground(Color.WHITE);
        note.setEditable(false);
        note.setFocusable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane noteScroll = new JScrollPane(note);
        noteScroll.setBorder(null);
        noteScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noteScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        notePanel.add(noteScroll, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(form, BorderLayout.NORTH);
        content.add(notePanel, BorderLayout.CENTER);

        formCard.add(title, BorderLayout.NORTH);
        formCard.add(content, BorderLayout.CENTER);

        return formCard;
    }

    private JPanel createActionPanel() {
        JPanel actions = new RoundedPanel(20, CARD_BG);
        actions.setLayout(new GridLayout(2, 2, 12, 12));
        actions.setBorder(new EmptyBorder(14, 14, 14, 14));
        actions.setPreferredSize(new Dimension(0, 130));

        btnThem = createPrimaryButton("Thêm", 150, 42);
        btnCapNhat = createPrimaryButton("Cập nhật", 150, 42);
        btnXoa = createDangerButton("Xóa", 150, 42);
        btnLamMoiForm = createGhostButton("Làm mới", 150, 42);

        actions.add(btnThem);
        actions.add(btnCapNhat);
        actions.add(btnXoa);
        actions.add(btnLamMoiForm);

        return actions;
    }

    private void addFormRow(JPanel container, GridBagConstraints gbc, int row, String labelText, Component input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);

        Dimension labelSize = new Dimension(130, 38);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);

        Dimension inputSize = new Dimension(220, 38);
        input.setPreferredSize(inputSize);
//        input.setMinimumSize(inputSize);

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
        gbc.weightx = 1;
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

        if (cbNhanVien.getItemCount() > 0) {
            cbNhanVien.setSelectedIndex(0);
        }

        if (cbVaiTro.getItemCount() > 0) {
            cbVaiTro.setSelectedIndex(0);
        }

        table.clearSelection();
    }

    public JTextField getTxtTimKiem() {
        return txtTimKiem;
    }

    public JComboBox<String> getCbTimTheo() {
        return cbTimTheo;
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtTenDangNhap() {
        return txtTenDangNhap;
    }

    public JPasswordField getTxtMatKhau() {
        return txtMatKhau;
    }

    public JComboBox<NhanVienItem> getCbNhanVien() {
        return cbNhanVien;
    }

    public JComboBox<String> getCbVaiTro() {
        return cbVaiTro;
    }

    

    public JButton getBtnLamMoiTimKiem() {
        return btnLamMoiTimKiem;
    }
    public JButton getBtnLamMoiForm() {
    	return btnLamMoiForm;
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
        QLPhanQuyen view = new QLPhanQuyen();
        new QLPhanQuyenController(view);
        return view;
    }
}