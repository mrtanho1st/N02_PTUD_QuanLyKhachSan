package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import connection.ConnectDB;

public class QLNhanVien extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(20, 22, 26);
    private static final Color CARD_BG = new Color(28, 31, 36);
    private static final Color PRIMARY = new Color(201, 168, 106);
    private static final Color PRIMARY_DARK = new Color(94, 74, 43);
    private static final Color TEXT = new Color(231, 224, 206);
    private static final Color BUTTON_TEXT = new Color(231, 224, 206);
    private static final Color BUTTON_PRIMARY_BG = new Color(110, 89, 56);
    private static final Color BUTTON_GHOST_BG = new Color(52, 56, 64);
    private static final Color BUTTON_DANGER_BG = new Color(110, 64, 64);

    private DefaultTableModel tableModel;
    private JTextField txtMaNhanVien;

    public QLNhanVien() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý nhân viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 950);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);

        setContentPane(root);
        SwingUtilities.invokeLater(() -> {
            if (txtMaNhanVien != null) {
                txtMaNhanVien.requestFocusInWindow();
            }
        });
    }

    private JPanel createHeader() {
        JPanel header = new GradientPanel(PRIMARY_DARK, PRIMARY);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(138, 112, 70), 1),
                new EmptyBorder(14, 18, 14, 18)));

        JLabel title = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 33));

        JLabel subtitle = new JLabel("Khách sạn Imperial Vũng Tàu", SwingConstants.CENTER);
        subtitle.setForeground(new Color(223, 206, 170));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        title.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        textWrap.add(title);
        textWrap.add(Box.createVerticalStrut(4));
        textWrap.add(subtitle);

        header.add(textWrap, BorderLayout.CENTER);
        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);

        body.add(createFilterPanel(), BorderLayout.NORTH);
        body.add(createCenterPanel(), BorderLayout.CENTER);
        body.add(createActionPanel(), BorderLayout.SOUTH);

        return body;
    }

    private JPanel createFilterPanel() {
        JPanel filter = new RoundedPanel(20, CARD_BG);
        filter.setLayout(new BorderLayout(12, 0));
        filter.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel lbSearch = new JLabel("Tìm kiếm:");
        lbSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbSearch.setForeground(TEXT);

        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(320, 36));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(92, 84, 68), 1),
                new EmptyBorder(6, 10, 6, 10)));
        configureSafeTextInput(txtSearch);

        JComboBox<String> cbFilter = new JComboBox<String>(
                new String[] { "Mã NV", "Họ tên", "SĐT", "Vai trò", "Trạng thái" });
        cbFilter.setPreferredSize(new Dimension(170, 36));
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        left.add(lbSearch);
        left.add(txtSearch);
        left.add(cbFilter);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(createPrimaryButton("Lọc dữ liệu", 130, 36));

        filter.add(left, BorderLayout.WEST);
        filter.add(right, BorderLayout.EAST);
        return filter;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setOpaque(false);

        center.add(createFormPanel(), BorderLayout.WEST);
        center.add(createTablePanel(), BorderLayout.CENTER);

        return center;
    }

    private JPanel createFormPanel() {
        JPanel formCard = new RoundedPanel(20, CARD_BG);
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        formCard.setPreferredSize(new Dimension(470, 0));

        JLabel title = new JLabel("Thông tin nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 4, 6, 4);

        txtMaNhanVien = createInputField("");
        addFormRow(form, gbc, "Mã nhân viên", txtMaNhanVien);
        addFormRow(form, gbc, "Họ tên", createInputField(""));
        addFormRow(form, gbc, "Ngày sinh", createInputField("dd/MM/yyyy"));
        addFormRow(form, gbc, "Số điện thoại", createInputField(""));
        addFormRow(form, gbc, "Email", createInputField(""));
        addFormRow(form, gbc, "Giới tính", new JComboBox<String>(new String[] { "Nam", "Nữ", "Khác" }));
        addFormRow(form, gbc, "Địa chỉ", createInputField(""));
        addFormRow(form, gbc, "Vai trò", new JComboBox<String>(
                new String[] { "Nhân viên lễ tân", "Nhân viên quản lý" }));
        addFormRow(form, gbc, "Ca làm việc", new JComboBox<String>(
                new String[] { "Ca sáng", "Ca chiều", "Ca tối", "Ca xoay" }));
        addFormRow(form, gbc, "Ngày bắt đầu", createInputField("dd/MM/yyyy"));
        addFormRow(form, gbc, "Trạng thái", new JComboBox<String>(
                new String[] { "Đang làm việc", "Nghỉ" }));

        formCard.add(title, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);
        return formCard;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new Object[] { "Mã NV", "Họ tên", "Ngày sinh", "SĐT", "Email", "Giới tính", "Địa chỉ", "Vai trò",
                        "Ca làm việc", "Ngày bắt đầu", "Trạng thái" },
                0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(50, 45, 38));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(new Color(72, 66, 54));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(109, 88, 52));
        table.setSelectionForeground(TEXT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(170);
        table.getColumnModel().getColumn(7).setPreferredWidth(150);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);
        table.getColumnModel().getColumn(9).setPreferredWidth(110);
        table.getColumnModel().getColumn(10).setPreferredWidth(120);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(2).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(3).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(5).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(8).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(9).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(10).setCellRenderer(centerAlign);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(86, 78, 62), 1));
        scrollPane.getViewport().setBackground(new Color(28, 31, 36));

        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        seedData();
        return tableCard;
    }

    private JPanel createActionPanel() {
        JPanel actions = new RoundedPanel(20, CARD_BG);
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBorder(new EmptyBorder(4, 10, 4, 10));

        actions.add(createGhostButton("Làm mới", 120, 38));
        actions.add(createPrimaryButton("Thêm", 110, 38));
        actions.add(createPrimaryButton("Cập nhật", 120, 38));
        actions.add(createDangerButton("Xóa", 110, 38));

        return actions;
    }

    private void addFormRow(JPanel container, GridBagConstraints gbc, String labelText, java.awt.Component input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(130, 36));

        if (input instanceof JTextField) {
            JTextField textField = (JTextField) input;
            textField.setPreferredSize(new Dimension(280, 36));
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(86, 78, 62), 1),
                    new EmptyBorder(6, 10, 6, 10)));
            textField.setEditable(true);
            textField.setEnabled(true);
            textField.setFocusable(true);
            textField.setForeground(TEXT);
            textField.setBackground(new Color(28, 31, 36));
        }

        if (input instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) input;
            combo.setPreferredSize(new Dimension(280, 36));
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            combo.setEnabled(true);
            combo.setFocusable(true);
        }

        gbc.gridx = 0;
        container.add(label, gbc);
        gbc.gridx = 1;
        container.add(input, gbc);
        gbc.gridy++;
    }

    private JTextField createInputField(String initialText) {
        JTextField textField = new JTextField(initialText);
        textField.setEditable(true);
        textField.setEnabled(true);
        textField.setFocusable(true);
        configureSafeTextInput(textField);
        return textField;
    }

    private void configureSafeTextInput(JTextField textField) {
        textField.setDragEnabled(false);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                int caretPosition = field.getCaretPosition();
                field.select(caretPosition, caretPosition);
            }
        });
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    e.consume();
                }
            }
        });
    }

    private JButton createPrimaryButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_PRIMARY_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(138, 112, 70), 1));
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
        button.setBorder(BorderFactory.createLineBorder(new Color(138, 112, 70), 1));
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
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 90, 90), 1));
        return button;
    }

    private void seedData() {
        String sql = "SELECT MaNV, HoTen, NgaySinh, SoDienThoai, Email, GioiTinh, DiaChi, VaiTro, CaLamViec, NgayBatDau, TrangThai "
                + "FROM dbo.NhanVien ORDER BY MaNV";
        try {
            Connection connection = ConnectDB.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tableModel.addRow(new Object[] {
                            resultSet.getString("MaNV"),
                            resultSet.getString("HoTen"),
                            resultSet.getString("NgaySinh"),
                            resultSet.getString("SoDienThoai"),
                            resultSet.getString("Email"),
                            resultSet.getString("GioiTinh"),
                            resultSet.getString("DiaChi"),
                            resultSet.getString("VaiTro"),
                            resultSet.getString("CaLamViec"),
                            resultSet.getString("NgayBatDau"),
                            resultSet.getString("TrangThai")
                    });
                }
            }
        } catch (SQLException ignored) {
            // Keep table empty if DB is unavailable.
        }
    }

    private static class GradientPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final Color start;
        private final Color end;

        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
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
            g2d.setColor(new Color(86, 78, 62));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applySystemLookAndFeel();
            new QLNhanVien().setVisible(true);
        });
    }

    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Keep default look and feel.
        }
    }
}
