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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.KhachHangDAO;

public class QLKhachHang extends JFrame {

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
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private JTextField txtMaKH;
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JTextField txtEmail;
    private JTextField txtCCCD;
    private JComboBox<String> cbPhanLoai;
    private JTextField txtSearchFilter;
    private JComboBox<String> cbFilterPhanLoai;

    public QLKhachHang() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý khách hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 950);
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

        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
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

        JLabel lbSearch = new JLabel("Tìm kiếm:");
        lbSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbSearch.setForeground(TEXT);

        txtSearchFilter = new JTextField();
        txtSearchFilter.setPreferredSize(new Dimension(320, 36));
        txtSearchFilter.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearchFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(92, 84, 68), 1),
                new EmptyBorder(6, 10, 6, 10)));
        FormInputSupport.configureSearchField(txtSearchFilter, this::applyFilter, () -> {
            txtSearchFilter.setText("");
            cbFilterPhanLoai.setSelectedIndex(0);
            seedData();
        });

        cbFilterPhanLoai = new JComboBox<String>(new String[] { "Tất cả", "VIP", "Thân thiết", "Mới" });
        cbFilterPhanLoai.setPreferredSize(new Dimension(150, 36));
        cbFilterPhanLoai.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        left.add(lbSearch);
        left.add(txtSearchFilter);
        left.add(cbFilterPhanLoai);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton btnLoc = createPrimaryButton("Lọc dữ liệu", 130, 36);
        btnLoc.addActionListener(e -> applyFilter());
        right.add(btnLoc);

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
        formCard.setPreferredSize(new Dimension(380, 0));

        JLabel title = new JLabel("Thông tin khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 4, 6, 4);

        txtMaKH = new JTextField();
        txtHoTen = new JTextField();
        txtSoDienThoai = new JTextField();
        txtEmail = new JTextField();
        txtCCCD = new JTextField();
        cbPhanLoai = new JComboBox<String>(new String[] { "Mới", "Thân thiết", "VIP" });

        addFormRow(form, gbc, "Mã KH", txtMaKH);
        addFormRow(form, gbc, "Họ tên", txtHoTen);
        addFormRow(form, gbc, "Số điện thoại", txtSoDienThoai);
        addFormRow(form, gbc, "Email", txtEmail);
        addFormRow(form, gbc, "CCCD", txtCCCD);
        addFormRow(form, gbc, "Phân loại", cbPhanLoai);

        formCard.add(title, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);
        return formCard;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new Object[] { "Mã KH", "Họ tên", "SĐT", "Email", "CCCD", "Phân loại", "Điểm" }, 0) {
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
        table.getColumnModel().getColumn(5).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(6).setCellRenderer(centerAlign);

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
        btnThem.addActionListener(e -> addKhachHang());
        btnCapNhat.addActionListener(e -> updateKhachHang());
        btnXoa.addActionListener(e -> deleteKhachHang());

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
        label.setPreferredSize(new Dimension(110, 36));

        if (input instanceof JTextField) {
            JTextField textField = (JTextField) input;
            textField.setPreferredSize(new Dimension(220, 36));
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 229, 234), 1),
                    new EmptyBorder(6, 10, 6, 10)));
            FormInputSupport.configureEditor(textField, true);
        }

        if (input instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) input;
            combo.setPreferredSize(new Dimension(220, 36));
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        gbc.gridx = 0;
        container.add(label, gbc);
        gbc.gridx = 1;
        container.add(input, gbc);
        gbc.gridy++;
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
            List<Object[]> rows = khachHangDAO.findAllRows();
            loadRows(rows);
        } catch (SQLException e) {
            loadFallbackData();
            System.err.println("Khong the tai du lieu KhachHang tu SQL Server: " + e.getMessage());
        }
    }

    private void loadFallbackData() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[] { "KH001", "Nguyễn Văn An", "0901234567", "an.nguyen@gmail.com", "079204001111",
                "VIP", 2450 });
        tableModel.addRow(new Object[] { "KH002", "Trần Thị Bình", "0912345678", "binh.tran@gmail.com", "079204002222",
                "Thân thiết", 1280 });
        tableModel.addRow(new Object[] { "KH003", "Lê Hoàng Long", "0933456789", "long.le@gmail.com", "079204003333",
                "Mới", 120 });
        tableModel.addRow(new Object[] { "KH004", "Phạm Minh Khoa", "0945566778", "khoa.pham@gmail.com", "079204004444",
                "Thân thiết", 860 });
        tableModel.addRow(
                new Object[] { "KH005", "Đỗ Gia Hân", "0967788990", "han.do@gmail.com", "079204005555", "VIP", 3010 });
    }

    private void loadRows(List<Object[]> rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void applyFilter() {
        String keyword = txtSearchFilter.getText().trim();
        String phanLoai = String.valueOf(cbFilterPhanLoai.getSelectedItem());
        try {
            List<Object[]> rows = khachHangDAO.search(keyword, phanLoai);
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
        txtMaKH.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtHoTen.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtSoDienThoai.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtEmail.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtCCCD.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        cbPhanLoai.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        txtMaKH.setText("");
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
        txtEmail.setText("");
        txtCCCD.setText("");
        cbPhanLoai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void addKhachHang() {
        String maKH = txtMaKH.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String phanLoai = String.valueOf(cbPhanLoai.getSelectedItem());

        if (maKH.isEmpty() || hoTen.isEmpty() || soDienThoai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã KH, Họ tên và Số điện thoại.");
            return;
        }

        try {
            boolean inserted = khachHangDAO.insert(maKH, hoTen, soDienThoai, email, cccd, phanLoai, 0);
            if (inserted) {
                seedData();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại: " + ex.getMessage());
        }
    }

    private void updateKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật.");
            return;
        }

        String hoTen = txtHoTen.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String phanLoai = String.valueOf(cbPhanLoai.getSelectedItem());

        try {
            boolean updated = khachHangDAO.update(maKH, hoTen, soDienThoai, email, cccd, phanLoai, 0);
            if (updated) {
                seedData();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại: " + ex.getMessage());
        }
    }

    private void deleteKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khách hàng " + maKH + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = khachHangDAO.deleteById(maKH);
            if (deleted) {
                seedData();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại: " + ex.getMessage());
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
            new QLKhachHang().setVisible(true);
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
