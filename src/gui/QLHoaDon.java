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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import dao.HoaDonDAO;

public class QLHoaDon extends JFrame {

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
    private JTable table;
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private JTextField txtMaHoaDon;
    private JTextField txtKhachHang;
    private JTextField txtNhanVien;
    private JTextField txtMaDon;
    private JTextField txtMaThue;
    private DatePicker dpNgayLap;
    private JTextField txtTongTien;
    private JTextField txtSearchFilter;
    private JComboBox<String> cbFilterType;

    public QLHoaDon() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý hóa đơn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 780);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new GradientPanel(PRIMARY_DARK, PRIMARY);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(138, 112, 70), 1),
                new EmptyBorder(14, 18, 14, 18)));

        JLabel title = new JLabel("QUẢN LÝ HÓA ĐƠN", SwingConstants.CENTER);
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

        JLabel lbSearch = new JLabel("Tìm hóa đơn:");
        lbSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbSearch.setForeground(TEXT);

        txtSearchFilter = createInputField("");
        txtSearchFilter.setPreferredSize(new Dimension(320, 36));

        cbFilterType = new JComboBox<String>(
                new String[] { "Mã hóa đơn", "Khách hàng", "Nhân viên", "Mã thuế", "Ngày lập" });
        cbFilterType.setPreferredSize(new Dimension(170, 36));
        cbFilterType.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        left.add(lbSearch);
        left.add(txtSearchFilter);
        left.add(cbFilterType);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton btnLoc = createPrimaryButton("Lọc", 100, 36);
        JButton btnShowAll = createGhostButton("Hiển thị tất cả", 140, 36);
        btnLoc.addActionListener(e -> applyFilter());
        btnShowAll.addActionListener(e -> {
            txtSearchFilter.setText("");
            cbFilterType.setSelectedIndex(0);
            seedData();
        });
        FormInputSupport.configureSearchField(txtSearchFilter, this::applyFilter, () -> {
            txtSearchFilter.setText("");
            cbFilterType.setSelectedIndex(0);
            seedData();
        });
        right.add(btnLoc);
        right.add(btnShowAll);

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
        formCard.setPreferredSize(new Dimension(430, 0));

        JLabel title = new JLabel("Thông tin hóa đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 4, 6, 4);

        txtMaHoaDon = createInputField("");
        txtKhachHang = createInputField("");
        txtNhanVien = createInputField("");
        txtMaDon = createInputField("");
        txtMaThue = createInputField("");
        DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dpNgayLap = new DatePicker(datePickerSettings);
        dpNgayLap.setDate(LocalDate.now());
        dpNgayLap.setPreferredSize(new Dimension(260, 36));
        txtTongTien = createInputField("0");

        addFormRow(form, gbc, "Mã hóa đơn", txtMaHoaDon);
        addFormRow(form, gbc, "Khách hàng", txtKhachHang);
        addFormRow(form, gbc, "Nhân viên", txtNhanVien);
        addFormRow(form, gbc, "Mã đơn", txtMaDon);
        addFormRow(form, gbc, "Mã thuế", txtMaThue);
        addFormRow(form, gbc, "Ngày lập", dpNgayLap);
        addFormRow(form, gbc, "Tổng tiền", txtTongTien);

        formCard.add(title, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);
        return formCard;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách hóa đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new Object[] { "Mã hóa đơn", "Khách hàng", "Nhân viên", "Mã đơn", "Mã thuế", "Ngày lập", "Tổng tiền" },
                0) {
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
        table.getTableHeader().setBackground(new Color(50, 45, 38));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(new Color(72, 66, 54));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(109, 88, 52));
        table.setSelectionForeground(TEXT);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelectedRow();
            }
        });

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(3).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(4).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(5).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(6).setCellRenderer(centerAlign);

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

        JButton btnLamMoi = createGhostButton("Làm mới", 120, 38);
        JButton btnThem = createPrimaryButton("Thêm", 110, 38);
        JButton btnCapNhat = createPrimaryButton("Cập nhật", 120, 38);
        JButton btnXoa = createDangerButton("Xóa", 110, 38);

        btnLamMoi.addActionListener(e -> clearForm());
        btnThem.addActionListener(e -> addHoaDon());
        btnCapNhat.addActionListener(e -> updateHoaDon());
        btnXoa.addActionListener(e -> deleteHoaDon());

        actions.add(btnLamMoi);
        actions.add(btnThem);
        actions.add(btnCapNhat);
        actions.add(btnXoa);

        return actions;
    }

    private void addFormRow(JPanel container, GridBagConstraints gbc, String labelText, java.awt.Component input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(120, 36));

        if (input instanceof JTextField) {
            JTextField textField = (JTextField) input;
            textField.setPreferredSize(new Dimension(260, 36));
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(86, 78, 62), 1),
                    new EmptyBorder(6, 10, 6, 10)));
            textField.setEditable(true);
            textField.setEnabled(true);
            textField.setFocusable(true);
        }

        if (input instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) input;
            combo.setPreferredSize(new Dimension(260, 36));
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        FormInputSupport.configureEditor(textField, true);
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
        button.setBorder(BorderFactory.createLineBorder(new Color(138, 112, 70), 1));
        FormInputSupport.configureActionButton(button);
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
        FormInputSupport.configureActionButton(button);
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
        FormInputSupport.configureActionButton(button);
        return button;
    }

    private void seedData() {
        try {
            List<Object[]> rows = hoaDonDAO.findAllRows();
            loadRows(rows);
        } catch (SQLException e) {
            loadFallbackData();
            System.err.println("Khong the tai du lieu HoaDon tu SQL Server: " + e.getMessage());
        }
    }

    private void loadFallbackData() {
        tableModel.setRowCount(0);
        tableModel.addRow(
                new Object[] { "HD001", "Nguyễn Văn An", "Trần Thu Hà", "DP001", "0312548899", "31/03/2026",
                        "3,250,000" });
        tableModel.addRow(
                new Object[] { "HD002", "Lê Hoàng Long", "Nguyễn Minh Quân", "DP002", "0312549900", "31/03/2026",
                        "1,850,000" });
        tableModel.addRow(
                new Object[] { "HD003", "Đỗ Gia Hân", "Phạm Ngọc Anh", "DP003", "0312550011", "30/03/2026",
                        "5,420,000" });
        tableModel.addRow(
                new Object[] { "HD004", "Trần Thị Bình", "Trần Thu Hà", "DP004", "0312551122", "30/03/2026",
                        "2,780,000" });
    }

    private void loadRows(List<Object[]> rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void applyFilter() {
        String keyword = txtSearchFilter.getText().trim();
        String filterBy = String.valueOf(cbFilterType.getSelectedItem());
        try {
            List<Object[]> rows = hoaDonDAO.search(keyword, filterBy);
            loadRows(rows);
            table.clearSelection();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lọc dữ liệu thất bại: " + ex.getMessage());
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtMaHoaDon.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtKhachHang.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtNhanVien.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtMaDon.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtMaThue.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        LocalDate ngayLap = LocalDate.parse(String.valueOf(tableModel.getValueAt(row, 5)), dateFormatter);
        dpNgayLap.setDate(ngayLap);
        txtTongTien.setText(String.valueOf(tableModel.getValueAt(row, 6)).replace(",", ""));
    }

    private void clearForm() {
        txtMaHoaDon.setText("");
        txtKhachHang.setText("");
        txtNhanVien.setText("");
        txtMaDon.setText("");
        txtMaThue.setText("");
        dpNgayLap.setDate(LocalDate.now());
        txtTongTien.setText("0");
        table.clearSelection();
    }

    private void addHoaDon() {
        try {
            String maHoaDon = txtMaHoaDon.getText().trim();
            String khachHang = txtKhachHang.getText().trim();
            String nhanVien = txtNhanVien.getText().trim();
            String maDon = txtMaDon.getText().trim();
            String maThue = txtMaThue.getText().trim();
            LocalDate ngayLap = readNgayLap();
            BigDecimal tongTien = parseMoney(txtTongTien.getText().trim());

            if (maHoaDon.isEmpty() || khachHang.isEmpty() || nhanVien.isEmpty() || maDon.isEmpty() || maThue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin hóa đơn.");
                return;
            }

            boolean inserted = hoaDonDAO.insert(maHoaDon, khachHang, nhanVien, maDon, maThue, ngayLap, tongTien);
            if (inserted) {
                seedData();
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Thêm hóa đơn thất bại: " + ex.getMessage());
        }
    }

    private void updateHoaDon() {
        String maHoaDon = txtMaHoaDon.getText().trim();
        if (maHoaDon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần cập nhật.");
            return;
        }
        try {
            String khachHang = txtKhachHang.getText().trim();
            String nhanVien = txtNhanVien.getText().trim();
            String maDon = txtMaDon.getText().trim();
            String maThue = txtMaThue.getText().trim();
            LocalDate ngayLap = readNgayLap();
            BigDecimal tongTien = parseMoney(txtTongTien.getText().trim());

            boolean updated = hoaDonDAO.update(maHoaDon, khachHang, nhanVien, maDon, maThue, ngayLap, tongTien);
            if (updated) {
                seedData();
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thất bại: " + ex.getMessage());
        }
    }

    private void deleteHoaDon() {
        String maHoaDon = txtMaHoaDon.getText().trim();
        if (maHoaDon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa hóa đơn " + maHoaDon + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean deleted = hoaDonDAO.deleteById(maHoaDon);
            if (deleted) {
                seedData();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại: " + ex.getMessage());
        }
    }

    private LocalDate readNgayLap() {
        LocalDate value = dpNgayLap.getDate();
        if (value == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày lập.");
        }
        return value;
    }

    private BigDecimal parseMoney(String rawValue) {
        String normalized = rawValue.replace(",", "").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tổng tiền.");
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Tổng tiền không hợp lệ.");
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
            new QLHoaDon().setVisible(true);
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
