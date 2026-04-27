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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.QLDichVuController;

public class QLDichVu extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    // Bộ lọc
 // Bộ lọc
    private JTextField txtTimMaDV;
    private JTextField txtTimTenDV;
    private JTextField txtTimGia;
    private JButton btnLamMoiTim;

    // Bảng dịch vụ
    private JTable tblDichVu;
    private DefaultTableModel tableModel;

    // Form chi tiết
    private JTextField txtMaDV;
    private JTextField txtTenDV;
    private JTextField txtGia;

    // Nút thao tác
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoiForm;

    public QLDichVu() {
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

        JLabel lblMaDV = createLabel("Mã DV:");
        JLabel lblTenDV = createLabel("Tên DV:");
        JLabel lblGiaDV = createLabel("Giá DV:");

        txtTimMaDV = new JTextField();
        txtTimTenDV = new JTextField();
        txtTimGia = new JTextField();


        styleTextField(txtTimMaDV);
        styleTextField(txtTimTenDV);
        styleTextField(txtTimGia);


        btnLamMoiTim = createButton("Làm mới");

        // Dòng 1: Mã DV - Tên DV - Giá DV
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaDV, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(txtTimMaDV, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblTenDV, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(txtTimTenDV, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblGiaDV, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.8;
        panel.add(txtTimGia, gbc);

        


        gbc.gridx = 6;
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

        JLabel lblTitle = new JLabel("Danh sách dịch vụ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        String[] columns = {
                "Mã DV", "Tên dịch vụ", "Giá dịch vụ", "Số lượt dùng", "Doanh thu"
        };

        tableModel = new DefaultTableModel(new Object[][] {}, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDichVu = new JTable(tableModel);
        styleTable(tblDichVu);

        tblDichVu.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblDichVu.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblDichVu.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblDichVu.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblDichVu.getColumnModel().getColumn(4).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(tblDichVu);
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

        JLabel lblTitle = new JLabel("Thông tin dịch vụ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaDV = new JTextField();
        txtTenDV = new JTextField();
        txtGia = new JTextField();

        styleTextField(txtMaDV);
        styleTextField(txtTenDV);
        styleTextField(txtGia);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã DV", txtMaDV);
        addFormRow(formPanel, gbc, "Tên dịch vụ", txtTenDV);
        addFormRow(formPanel, gbc, "Giá dịch vụ", txtGia);

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

        Dimension btnSize = new Dimension(160, 48);
        btnThem.setPreferredSize(btnSize);
        btnCapNhat.setPreferredSize(btnSize);
        btnXoa.setPreferredSize(btnSize);
        btnLamMoiForm.setPreferredSize(btnSize);

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
        txtMaDV.setText("");
        txtTenDV.setText("");
        txtGia.setText("");
        tblDichVu.clearSelection();
    }
    public void clearSearchForm() {
        txtTimMaDV.setText("");
        txtTimTenDV.setText("");
        txtTimGia.setText("");
       
    }

    public JTextField getTxtTimMaDV() {
        return txtTimMaDV;
    }

    public JTextField getTxtTimTenDV() {
        return txtTimTenDV;
    }

    public JButton getBtnLamMoiTim() {
        return btnLamMoiTim;
    }

    public JTable getTblDichVu() {
        return tblDichVu;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtMaDV() {
        return txtMaDV;
    }

    public JTextField getTxtTenDV() {
        return txtTenDV;
    }

    public JTextField getTxtGia() {
        return txtGia;
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
    public JTextField getTxtTimGia() {
        return txtTimGia;
    }



    public static JPanel createPanel() {
        QLDichVu panel = new QLDichVu();
        new QLDichVuController(panel);
        return panel;
    }
}