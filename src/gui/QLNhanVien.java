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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.QLNhanVienController;

public class QLNhanVien extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    private JTextField txtTimMaNV;
    private JTextField txtTimHoTen;
    private JComboBox<String> cboLocTrangThai;

    private JTable tblNhanVien;
    private DefaultTableModel tableModel;

    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtNgaySinh;
    private JTextField txtSDT;
    private JTextField txtEmail;
    private JComboBox<String> cboGioiTinh;
    private JTextField txtNgayBatDauVaoLam;
    private JComboBox<String> cboTrangThaiLamViec;
    private JTextField txtDiaChi;
    private JComboBox<String> cboCaLamViec;
    private JComboBox<String> cboViTriCongViec;

    private JButton btnLamMoiTim;
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoiForm;

    public QLNhanVien() {
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblMaNV = createLabel("Mã NV:");
        JLabel lblHoTen = createLabel("Họ tên:");
        JLabel lblTrangThai = createLabel("Trạng thái:");

        txtTimMaNV = new JTextField();
        txtTimHoTen = new JTextField();
        cboLocTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Đang làm", "Nghỉ việc", "Tạm nghỉ"
        });

        styleTextField(txtTimMaNV);
        styleTextField(txtTimHoTen);
        styleComboBox(cboLocTrangThai);

        btnLamMoiTim = createButton("Làm mới");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaNV, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(txtTimMaNV, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblHoTen, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(txtTimHoTen, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblTrangThai, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.8;
        panel.add(cboLocTrangThai, gbc);


        gbc.gridx = 6;
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
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lblTitle = new JLabel("Danh sách nhân viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        panel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {
                "Mã NV", "Họ tên", "Ngày sinh", "SĐT", "Email", "Giới tính",
                "Ngày bắt đầu", "Trạng thái", "Địa chỉ", "Ca làm", "Vị trí"
        };

        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNhanVien = new JTable(tableModel);
        tblNhanVien.setRowHeight(28);
        tblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblNhanVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblNhanVien.getTableHeader().setBackground(new Color(233, 239, 248));
        tblNhanVien.getTableHeader().setForeground(TEXT_DARK);
        tblNhanVien.setGridColor(CARD_BORDER);
        tblNhanVien.setSelectionBackground(new Color(221, 232, 248));
        tblNhanVien.setSelectionForeground(TEXT_DARK);

        JScrollPane scrollPane = new JScrollPane(tblNhanVien);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

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

        JLabel lblTitle = new JLabel("Thông tin nhân viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaNV = new JTextField();
        txtHoTen = new JTextField();
        txtNgaySinh = new JTextField();
        txtSDT = new JTextField();
        txtEmail = new JTextField();
        cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
        txtNgayBatDauVaoLam = new JTextField();
        cboTrangThaiLamViec = new JComboBox<>(new String[] { "Đang làm", "Nghỉ việc", "Tạm nghỉ" });
        txtDiaChi = new JTextField();
        cboCaLamViec = new JComboBox<>(new String[] { "Ca sáng", "Ca chiều", "Ca tối" });
        cboViTriCongViec = new JComboBox<>(new String[] { "Lễ tân", "Quản lý" });

        styleTextField(txtMaNV);
        styleTextField(txtHoTen);
        styleTextField(txtNgaySinh);
        styleTextField(txtSDT);
        styleTextField(txtEmail);
        styleComboBox(cboGioiTinh);
        styleTextField(txtNgayBatDauVaoLam);
        styleComboBox(cboTrangThaiLamViec);
        styleTextField(txtDiaChi);
        styleComboBox(cboCaLamViec);
        styleComboBox(cboViTriCongViec);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã NV", txtMaNV);
        addFormRow(formPanel, gbc, "Họ tên", txtHoTen);
        addFormRow(formPanel, gbc, "Ngày sinh", txtNgaySinh);
        addFormRow(formPanel, gbc, "SĐT", txtSDT);
        addFormRow(formPanel, gbc, "Email", txtEmail);
        addFormRow(formPanel, gbc, "Giới tính", cboGioiTinh);
        addFormRow(formPanel, gbc, "Ngày bắt đầu", txtNgayBatDauVaoLam);
        addFormRow(formPanel, gbc, "Trạng thái", cboTrangThaiLamViec);
        addFormRow(formPanel, gbc, "Địa chỉ", txtDiaChi);
        addFormRow(formPanel, gbc, "Ca làm", cboCaLamViec);
        addFormRow(formPanel, gbc, "Vị trí", cboViTriCongViec);

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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, java.awt.Component component) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(createLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
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
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(220, 38));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
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

    public JTextField getTxtTimMaNV() {
        return txtTimMaNV;
    }

    public JTextField getTxtTimHoTen() {
        return txtTimHoTen;
    }

    public JComboBox<String> getCboLocTrangThai() {
        return cboLocTrangThai;
    }

    public JTable getTblNhanVien() {
        return tblNhanVien;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtMaNV() {
        return txtMaNV;
    }

    public JTextField getTxtHoTen() {
        return txtHoTen;
    }

    public JTextField getTxtNgaySinh() {
        return txtNgaySinh;
    }

    public JTextField getTxtSDT() {
        return txtSDT;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JComboBox<String> getCboGioiTinh() {
        return cboGioiTinh;
    }

    public JTextField getTxtNgayBatDauVaoLam() {
        return txtNgayBatDauVaoLam;
    }

    public JComboBox<String> getCboTrangThaiLamViec() {
        return cboTrangThaiLamViec;
    }

    public JTextField getTxtDiaChi() {
        return txtDiaChi;
    }

    public JComboBox<String> getCboCaLamViec() {
        return cboCaLamViec;
    }

    public JComboBox<String> getCboViTriCongViec() {
        return cboViTriCongViec;
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

    public static JPanel createPanel() {
        QLNhanVien panel = new QLNhanVien();
        new QLNhanVienController(panel);
        return panel;
    }
}