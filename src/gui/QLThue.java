package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.QLThueController;

public class QLThue extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    private JTextField txtTimMaThue;
    private JTextField txtTimTenThue;
    private JComboBox<String> cboTimTrangThai;
    private JTextField txtTimTyLe;
    private JButton btnLamMoiTim;

    private JTable tblThue;
    private DefaultTableModel tableModel;

    private JTextField txtMaThue;
    private JTextField txtTenThue;
    private JComboBox<String> cboTrangThai;
    private JTextField txtTyLeThue;
    private JTextArea txtMoTa;

    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoiForm;

    public QLThue() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 18, 16, 18)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtTimMaThue = new JTextField();
        txtTimTenThue = new JTextField();
        txtTimTyLe = new JTextField();
        cboTimTrangThai = new JComboBox<>(new String[] { "Tất cả", "Áp dụng", "Ngưng áp dụng" });
        btnLamMoiTim = createButton("Làm mới");

        styleTextField(txtTimMaThue);
        styleTextField(txtTimTenThue);
        styleTextField(txtTimTyLe);
        styleComboBox(cboTimTrangThai);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(createLabel("Mã thuế:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(txtTimMaThue, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(createLabel("Tên thuế:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(txtTimTenThue, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(createLabel("Trạng thái:"), gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.7;
        panel.add(cboTimTrangThai, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        panel.add(createLabel("Tỷ lệ (%):"), gbc);

        gbc.gridx = 7;
        gbc.weightx = 0.4;
        panel.add(txtTimTyLe, gbc);

        gbc.gridx = 8;
        gbc.weightx = 0;
        panel.add(btnLamMoiTim, gbc);

        return panel;
    }

    private JPanel createBodyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
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
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lblTitle = new JLabel("Danh sách thuế");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        tableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Mã thuế", "Tên thuế", "Trạng thái", "Tỷ lệ (%)", "Mô tả" }) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThue = new JTable(tableModel);
        styleTable(tblThue);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblThue.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblThue.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblThue.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        tblThue.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblThue.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblThue.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblThue.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblThue.getColumnModel().getColumn(4).setPreferredWidth(260);

        JScrollPane scrollPane = new JScrollPane(tblThue);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(APP_BG);
        wrapper.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lblTitle = new JLabel("Thông tin thuế");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        txtMaThue = new JTextField();
        txtTenThue = new JTextField();
        txtTyLeThue = new JTextField();
        txtMoTa = new JTextArea(5, 20);
        cboTrangThai = new JComboBox<>(new String[] { "Áp dụng", "Ngưng áp dụng" });

        styleTextField(txtMaThue);
        styleTextField(txtTenThue);
        styleTextField(txtTyLeThue);
        styleComboBox(cboTrangThai);
        styleTextArea(txtMoTa);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã thuế", txtMaThue);
        addFormRow(formPanel, gbc, "Tên thuế", txtTenThue);
        addFormRow(formPanel, gbc, "Trạng thái", cboTrangThai);
        addFormRow(formPanel, gbc, "Tỷ lệ (%)", txtTyLeThue);
        addTextAreaRow(formPanel, gbc, "Mô tả", txtMoTa);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(formScrollPane, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 16, 16));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnThem = createButton("Thêm");
        btnCapNhat = createButton("Cập nhật");
        btnXoa = createDangerButton("Xóa");
        btnLamMoiForm = createButton("Làm mới");

        panel.add(btnThem);
        panel.add(btnCapNhat);
        panel.add(btnXoa);
        panel.add(btnLamMoiForm);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, Component component) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);

        gbc.gridy++;
    }

    private void addTextAreaRow(JPanel panel, GridBagConstraints gbc, String label, JTextArea textArea) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createLabel(label), gbc);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollPane.setPreferredSize(new Dimension(220, 120));

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(130, 38));
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(220, 38));
        textField.setMinimumSize(new Dimension(100, 38));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(8, 10, 8, 10));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(220, 38));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(233, 239, 248));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setGridColor(CARD_BORDER);
        table.setSelectionBackground(new Color(221, 232, 248));
        table.setSelectionForeground(TEXT_DARK);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(TEXT_DARK);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        button.setPreferredSize(new Dimension(160, 48));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_DANGER);
        return button;
    }

    public void clearForm() {
        txtMaThue.setText("");
        txtMaThue.setEditable(true);
        txtTenThue.setText("");
        txtTyLeThue.setText("");
        txtMoTa.setText("");
        cboTrangThai.setSelectedIndex(0);
        tblThue.clearSelection();
    }

    public void clearSearchForm() {
        txtTimMaThue.setText("");
        txtTimTenThue.setText("");
        txtTimTyLe.setText("");
        cboTimTrangThai.setSelectedIndex(0);
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

    public JTextField getTxtTimTyLe() {
        return txtTimTyLe;
    }

    public JButton getBtnLamMoiTim() {
        return btnLamMoiTim;
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

    public JComboBox<String> getCboTrangThai() {
        return cboTrangThai;
    }

    public JTextField getTxtTyLeThue() {
        return txtTyLeThue;
    }

    public JTextArea getTxtMoTa() {
        return txtMoTa;
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

    public static JPanel createPanel() {
        QLThue panel = new QLThue();
        new QLThueController(panel);
        return panel;
    }
}
