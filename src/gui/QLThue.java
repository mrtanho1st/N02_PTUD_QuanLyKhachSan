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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.Thue_Dao;
import entity.Thue;

public class QLThue extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(238, 243, 250);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_PRIMARY_BG = new Color(220, 235, 255);
    private static final Color BUTTON_GHOST_BG = new Color(238, 245, 255);
    private static final Color BUTTON_DANGER_BG = new Color(252, 230, 230);
    private static final Color BORDER_COLOR = new Color(196, 210, 230);

    private final Thue_Dao thueDao = new Thue_Dao();

    private JTextField txtTimMaThue;
    private JTextField txtTimTenThue;
    private JComboBox<String> cboTimTrangThai;

    private JTable tblThue;
    private DefaultTableModel tableModel;

    private JTextField txtMaThue;
    private JTextField txtTenThue;
    private JTextField txtTyLeThue;
    private JComboBox<String> cboTrangThai;
    private JTextField txtMoTa;

    private JButton btnLamMoiTim;
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoiForm;
    private boolean cheDoTimKiem;

    private String selectedMaThue;

    public QLThue(boolean cheDoTimKiem) {
        this.cheDoTimKiem = cheDoTimKiem;
        initUI();
        loadData();
        addEvents();

        phanQuyenGiaoDien();

        // Nếu là Lễ tân thì vô hiệu hoá các nút CRUD liên quan đến thuế
        try {
            if (!controller.GiaoDienChinhController.coQuyen("", "Thuế")) {
                btnThem.setEnabled(false);
                btnCapNhat.setEnabled(false);
                btnXoa.setEnabled(false);
                btnLamMoiForm.setEnabled(false);
            }
        } catch (Exception e) {
            // ignore if permission helper not available at UI init
        }
    }

    public static JPanel createPanel(boolean cheDoTimKiem) {
        return new QLThue(cheDoTimKiem);
    }

    private void phanQuyenGiaoDien() {

        if (cheDoTimKiem) {

            btnThem.setEnabled(false);

            btnCapNhat.setEnabled(false);

            btnXoa.setEnabled(false);

            btnLamMoiForm.setEnabled(false);
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel filter = new RoundedPanel(20, CARD_BG);
        filter.setLayout(new GridBagLayout());
        filter.setBorder(new EmptyBorder(12, 14, 12, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblMaThue = createFilterLabel("Mã thuế:");
        JLabel lblTenThue = createFilterLabel("Tên thuế:");
        JLabel lblTrangThai = createFilterLabel("Trạng thái:");

        txtTimMaThue = createTextField(170, 36);
        txtTimTenThue = createTextField(220, 36);
        cboTimTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Áp dụng", "Ngừng áp dụng"
        });
        styleComboBox(cboTimTrangThai, 150, 36);

        btnLamMoiTim = createGhostButton("Làm mới", 110, 36);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        filter.add(lblMaThue, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        filter.add(txtTimMaThue, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        filter.add(lblTenThue, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        filter.add(txtTimTenThue, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        filter.add(lblTrangThai, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.6;
        filter.add(cboTimTrangThai, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        filter.add(btnLamMoiTim, gbc);

        return filter;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createTablePanel(),
                createDetailPanel());

        splitPane.setDividerLocation(760);
        splitPane.setResizeWeight(0.62);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setContinuousLayout(true);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách thuế");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new Object[] { "Mã thuế", "Tên thuế", "Trạng thái", "Tỷ lệ", "Mô tả" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThue = new JTable(tableModel);
        tblThue.setRowHeight(34);
        tblThue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblThue.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblThue.getTableHeader().setBackground(new Color(231, 240, 252));
        tblThue.getTableHeader().setForeground(TEXT);
        tblThue.setGridColor(new Color(229, 236, 247));
        tblThue.setShowVerticalLines(false);
        tblThue.setSelectionBackground(new Color(210, 229, 255));
        tblThue.setSelectionForeground(TEXT);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        tblThue.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        tblThue.getColumnModel().getColumn(2).setCellRenderer(centerAlign);
        tblThue.getColumnModel().getColumn(3).setCellRenderer(centerAlign);

        JScrollPane scrollPane = new JScrollPane(tblThue);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 229, 244), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
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

        JLabel title = new JLabel("Thông tin thuế");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaThue = createTextField(220, 36);
        txtTenThue = createTextField(220, 36);
        txtTyLeThue = createTextField(220, 36);
        txtMoTa = createTextField(220, 36);
        cboTrangThai = new JComboBox<>(new String[] { "Áp dụng", "Ngừng áp dụng" });
        styleComboBox(cboTrangThai, 220, 36);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã thuế", txtMaThue);
        addFormRow(formPanel, gbc, "Tên thuế", txtTenThue);
        addFormRow(formPanel, gbc, "Tỷ lệ (%)", txtTyLeThue);
        addFormRow(formPanel, gbc, "Trạng thái", cboTrangThai);
        addFormRow(formPanel, gbc, "Mô tả", txtMoTa);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(formScrollPane, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        return wrapper;
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

    private void addEvents() {
        btnLamMoiTim.addActionListener(e -> {
            txtTimMaThue.setText("");
            txtTimTenThue.setText("");
            cboTimTrangThai.setSelectedIndex(0);
            loadData();
        });

        txtTimMaThue.addActionListener(e -> timKiem());
        txtTimTenThue.addActionListener(e -> timKiem());
        cboTimTrangThai.addActionListener(e -> timKiem());

        btnThem.addActionListener(e -> themThue());
        btnCapNhat.addActionListener(e -> capNhatThue());
        btnXoa.addActionListener(e -> xoaThue());
        btnLamMoiForm.addActionListener(e -> clearForm());

        tblThue.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedToForm();
            }
        });
    }

    private void loadData() {
        fillTable(thueDao.findAllThue());
    }

    private void timKiem() {
        List<Thue> ds = thueDao.searchThue(
                txtTimMaThue.getText().trim(),
                txtTimTenThue.getText().trim(),
                cboTimTrangThai.getSelectedItem() == null ? "Tất cả" : cboTimTrangThai.getSelectedItem().toString());
        fillTable(ds);
    }

    private void fillTable(List<Thue> list) {
        tableModel.setRowCount(0);

        for (Thue thue : list) {
            tableModel.addRow(new Object[] {
                    thue.getMaThue(),
                    thue.getTenThue(),
                    thue.getTrangThai(),
                    thue.getTyLeThue(),
                    thue.getMoTa()
            });
        }
    }

    private void loadSelectedToForm() {
        int row = tblThue.getSelectedRow();
        if (row < 0) {
            selectedMaThue = null;
            txtMaThue.setEditable(true);
            return;
        }

        selectedMaThue = valueAt(row, 0);
        txtMaThue.setText(selectedMaThue);
        txtTenThue.setText(valueAt(row, 1));
        cboTrangThai.setSelectedItem(valueAt(row, 2));
        txtTyLeThue.setText(valueAt(row, 3));
        txtMoTa.setText(valueAt(row, 4));
        txtMaThue.setEditable(false);
    }

    private void clearForm() {
        txtMaThue.setText("");
        txtTenThue.setText("");
        txtTyLeThue.setText("");
        txtMoTa.setText("");
        cboTrangThai.setSelectedIndex(0);
        selectedMaThue = null;
        txtMaThue.setEditable(true);
        tblThue.clearSelection();
    }

    private void themThue() {
        Thue thue = readForm();
        if (thue == null) {
            return;
        }

        if (thueDao.existsByMaThue(thue.getMaThue())) {
            showMessage("Mã thuế đã tồn tại.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (thueDao.insert(thue)) {
            showMessage("Thêm thuế thành công.", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } else {
            showMessage("Thêm thuế thất bại.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatThue() {
        String maCu = selectedMaThue != null && !selectedMaThue.isBlank()
                ? selectedMaThue
                : txtMaThue.getText().trim();

        if (maCu.isBlank()) {
            showMessage("Vui lòng chọn một dòng cần cập nhật.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Thue thue = readForm();
        if (thue == null) {
            return;
        }

        thue.setMaThue(maCu);

        if (thueDao.update(thue)) {
            showMessage("Cập nhật thuế thành công.", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } else {
            showMessage("Cập nhật thuế thất bại.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaThue() {
        String maThue = selectedMaThue != null && !selectedMaThue.isBlank()
                ? selectedMaThue
                : txtMaThue.getText().trim();

        if (maThue.isBlank()) {
            showMessage("Vui lòng chọn một dòng cần xóa.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa thuế " + maThue + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (thueDao.delete(maThue)) {
            showMessage("Xóa thuế thành công.", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } else {
            showMessage("Xóa thuế thất bại.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Thue readForm() {
        String maThue = txtMaThue.getText().trim();
        String tenThue = txtTenThue.getText().trim();
        String tyLeText = txtTyLeThue.getText().trim();
        String trangThai = cboTrangThai.getSelectedItem() == null ? "" : cboTrangThai.getSelectedItem().toString();
        String moTa = txtMoTa.getText().trim();

        if (maThue.isBlank()) {
            showMessage("Mã thuế không được để trống.", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (tenThue.isBlank()) {
            showMessage("Tên thuế không được để trống.", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        double tyLe;
        try {
            tyLe = Double.parseDouble(tyLeText);
        } catch (NumberFormatException ex) {
            showMessage("Tỷ lệ thuế phải là số.", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (tyLe < 0 || tyLe > 100) {
            showMessage("Tỷ lệ thuế phải nằm trong khoảng 0 đến 100.", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (trangThai.isBlank()) {
            showMessage("Trạng thái không được để trống.", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Thue(maThue, tenThue, trangThai, tyLe, moTa);
    }

    private void showMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, "Quản lý thuế", messageType);
    }

    private String valueAt(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    public JTextField getTxtTimMaThue() {
        return txtTimMaThue;
    }

    public JTextField getTxtTimTenThue() {
        return txtTimTenThue;
    }

    public JComboBox<String> getCboTimTrangThai() {
        return cboTimTrangThai;
    }

    public JTable getTblThue() {
        return tblThue;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtMaThue() {
        return txtMaThue;
    }

    public JTextField getTxtTenThue() {
        return txtTenThue;
    }

    public JTextField getTxtTyLeThue() {
        return txtTyLeThue;
    }

    public JComboBox<String> getCboTrangThai() {
        return cboTrangThai;
    }

    public JTextField getTxtMoTa() {
        return txtMoTa;
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
