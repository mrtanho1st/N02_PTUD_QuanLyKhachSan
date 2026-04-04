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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.DichVuDAO;

public class QLDichVu extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(242, 242, 247);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(0, 122, 255);
    private static final Color PRIMARY_DARK = new Color(10, 132, 255);
    private static final Color TEXT = new Color(28, 28, 30);
    private static final Color BUTTON_TEXT = new Color(28, 28, 30);
    private static final Color BUTTON_PRIMARY_BG = new Color(0, 122, 255);
    private static final Color BUTTON_GHOST_BG = new Color(242, 242, 247);
    private static final Color BUTTON_DANGER_BG = new Color(255, 59, 48);

    private DefaultTableModel tableModel;
    private JTable table;
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private JTextField txtMaDV;
    private JTextField txtTenDV;
    private JTextField txtDonGia;
    private JComboBox<String> cbNhomDV;
    private JComboBox<String> cbDonViTinh;
    private JComboBox<String> cbTrangThai;
    private JTextField txtSearchFilter;
    private JComboBox<String> cbFilterType;

    public QLDichVu() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý dịch vụ khách sạn");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
                BorderFactory.createLineBorder(new Color(209, 209, 214), 1),
                new EmptyBorder(14, 18, 14, 18)));

        JLabel title = new JLabel("QUẢN LÝ DỊCH VỤ", SwingConstants.CENTER);
        title.setForeground(new Color(255, 255, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 33));

        JLabel subtitle = new JLabel("Khách sạn Imperial Vũng Tàu", SwingConstants.CENTER);
        subtitle.setForeground(new Color(232, 242, 255));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        title.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        textWrap.add(title);
        textWrap.add(Box.createVerticalStrut(4));
        textWrap.add(subtitle);

        JButton btnTrangChu = createGhostButton("Trang chủ", 120, 36);
        btnTrangChu.addActionListener(e -> goToHome());

        header.add(textWrap, BorderLayout.CENTER);
        header.add(btnTrangChu, BorderLayout.EAST);
        return header;
    }

    private void goToHome() {
        GiaoDienChinh home = new GiaoDienChinh();
        int currentState = getExtendedState();
        if ((currentState & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
            home.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            home.setBounds(getBounds());
        }
        home.setVisible(true);
        dispose();
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

        JLabel lbSearch = new JLabel("Tìm dịch vụ:");
        lbSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbSearch.setForeground(TEXT);

        txtSearchFilter = createInputField("");
        txtSearchFilter.setPreferredSize(new Dimension(320, 36));

        cbFilterType = new JComboBox<String>(
                new String[] { "Mã dịch vụ", "Tên dịch vụ", "Nhóm dịch vụ", "Trạng thái" });
        cbFilterType.setPreferredSize(new Dimension(180, 36));
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
        formCard.setPreferredSize(new Dimension(440, 0));

        JLabel title = new JLabel("Thông tin dịch vụ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 4, 6, 4);

        txtMaDV = createInputField("");
        txtTenDV = createInputField("");
        cbNhomDV = new JComboBox<String>(new String[] { "Ẩm thực", "Giặt ủi", "Đưa đón", "Giải trí", "Khác" });
        cbDonViTinh = new JComboBox<String>(new String[] { "Lần", "Suất", "Giờ", "Chai", "Phần" });
        txtDonGia = createInputField("0");
        cbTrangThai = new JComboBox<String>(new String[] { "Đang áp dụng", "Tạm ngưng" });

        addFormRow(form, gbc, "Mã dịch vụ", txtMaDV);
        addFormRow(form, gbc, "Tên dịch vụ", txtTenDV);
        addFormRow(form, gbc, "Nhóm dịch vụ", cbNhomDV);
        addFormRow(form, gbc, "Đơn vị tính", cbDonViTinh);
        addFormRow(form, gbc, "Đơn giá", txtDonGia);
        addFormRow(form, gbc, "Trạng thái", cbTrangThai);

        JLabel lbNote = new JLabel("Mô tả");
        lbNote.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbNote.setForeground(TEXT);
        lbNote.setPreferredSize(new Dimension(120, 36));

        JTextArea txtMoTa = new JTextArea(4, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 229, 234), 1),
                new EmptyBorder(6, 10, 6, 10)));

        JScrollPane moTaScroll = new JScrollPane(txtMoTa);
        moTaScroll.setPreferredSize(new Dimension(260, 90));

        gbc.gridx = 0;
        form.add(lbNote, gbc);
        gbc.gridx = 1;
        form.add(moTaScroll, gbc);

        formCard.add(title, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);
        return formCard;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách dịch vụ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new Object[] { "Mã dịch vụ", "Tên dịch vụ", "Nhóm dịch vụ", "Đơn vị tính", "Đơn giá", "Trạng thái" },
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
        table.setBackground(new Color(255, 255, 255));
        table.setForeground(TEXT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(245, 245, 247));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(new Color(229, 229, 234));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(230, 244, 255));
        table.setSelectionForeground(TEXT);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelectedRow();
            }
        });

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(2).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(3).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(4).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(5).setCellRenderer(centerAlign);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 229, 234), 1));
        scrollPane.getViewport().setBackground(new Color(255, 255, 255));

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
        btnThem.addActionListener(e -> addDichVu());
        btnCapNhat.addActionListener(e -> updateDichVu());
        btnXoa.addActionListener(e -> deleteDichVu());

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
                    BorderFactory.createLineBorder(new Color(229, 229, 234), 1),
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
        button.setForeground(new Color(255, 255, 255));
        button.setBackground(BUTTON_PRIMARY_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(209, 209, 214), 1));
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
        button.setBorder(BorderFactory.createLineBorder(new Color(209, 209, 214), 1));
        FormInputSupport.configureActionButton(button);
        return button;
    }

    private JButton createDangerButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(255, 255, 255));
        button.setBackground(BUTTON_DANGER_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 179, 171), 1));
        FormInputSupport.configureActionButton(button);
        return button;
    }

    private void seedData() {
        try {
            List<Object[]> rows = dichVuDAO.findAllRows();
            loadRows(rows);
        } catch (SQLException e) {
            loadFallbackData();
            System.err.println("Khong the tai du lieu DichVu tu SQL Server: " + e.getMessage());
        }
    }

    private void loadFallbackData() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[] { "DV001", "Bữa sáng buffet", "Ẩm thực", "Suất", "180,000", "Đang áp dụng" });
        tableModel.addRow(new Object[] { "DV002", "Giặt ủi nhanh", "Giặt ủi", "Lần", "85,000", "Đang áp dụng" });
        tableModel.addRow(new Object[] { "DV003", "Đưa đón sân bay", "Đưa đón", "Lần", "350,000", "Đang áp dụng" });
        tableModel.addRow(new Object[] { "DV004", "Karaoke phòng VIP", "Giải trí", "Giờ", "250,000", "Tạm ngưng" });
        tableModel.addRow(new Object[] { "DV005", "Minibar", "Ẩm thực", "Phần", "120,000", "Đang áp dụng" });
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
            List<Object[]> rows = dichVuDAO.search(keyword, filterBy);
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
        txtMaDV.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtTenDV.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        cbNhomDV.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 2)));
        cbDonViTinh.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 3)));
        txtDonGia.setText(String.valueOf(tableModel.getValueAt(row, 4)).replace(",", ""));
        cbTrangThai.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        txtMaDV.setText("");
        txtTenDV.setText("");
        txtDonGia.setText("0");
        cbNhomDV.setSelectedIndex(0);
        cbDonViTinh.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void addDichVu() {
        String maDV = txtMaDV.getText().trim();
        String tenDV = txtTenDV.getText().trim();
        String nhomDV = String.valueOf(cbNhomDV.getSelectedItem());
        String donViTinh = String.valueOf(cbDonViTinh.getSelectedItem());
        String trangThai = String.valueOf(cbTrangThai.getSelectedItem());

        if (maDV.isEmpty() || tenDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã dịch vụ và Tên dịch vụ.");
            return;
        }

        try {
            BigDecimal donGia = parseMoney(txtDonGia.getText().trim());
            boolean inserted = dichVuDAO.insert(maDV, tenDV, nhomDV, donViTinh, donGia, trangThai);
            if (inserted) {
                seedData();
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thất bại: " + ex.getMessage());
        }
    }

    private void updateDichVu() {
        String maDV = txtMaDV.getText().trim();
        if (maDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần cập nhật.");
            return;
        }

        String tenDV = txtTenDV.getText().trim();
        String nhomDV = String.valueOf(cbNhomDV.getSelectedItem());
        String donViTinh = String.valueOf(cbDonViTinh.getSelectedItem());
        String trangThai = String.valueOf(cbTrangThai.getSelectedItem());

        try {
            BigDecimal donGia = parseMoney(txtDonGia.getText().trim());
            boolean updated = dichVuDAO.update(maDV, tenDV, nhomDV, donViTinh, donGia, trangThai);
            if (updated) {
                seedData();
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ thất bại: " + ex.getMessage());
        }
    }

    private void deleteDichVu() {
        String maDV = txtMaDV.getText().trim();
        if (maDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa dịch vụ " + maDV + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = dichVuDAO.deleteById(maDV);
            if (deleted) {
                seedData();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Xóa dịch vụ thất bại: " + ex.getMessage());
        }
    }

    private BigDecimal parseMoney(String rawValue) {
        String normalized = rawValue.replace(",", "").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đơn giá.");
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Đơn giá không hợp lệ.");
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
            g2d.setColor(new Color(229, 229, 234));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applySystemLookAndFeel();
            new QLDichVu().setVisible(true);
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
