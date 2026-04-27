package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;

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

import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;

import controller.QLKhuyenMaiController;

public class QLKhuyenMai extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    // Bộ lọc
    private JTextField txtTimMaKM;
    private JTextField txtTimTenKhuyenMai;
    private JTextField txtTimGiaTri;
    private JDatePicker dateTuNgay;
    private JDatePicker dateDenNgay;
    private JComboBox<String> cboLocTrangThai;
    private JButton btnLamMoiTim;

    // Bảng khuyến mãi
    private JTable tblKhuyenMai;
    private DefaultTableModel tableModel;

    // Form chi tiết
    private JTextField txtMaKM;
    private JTextField txtTenKhuyenMai;
    private JTextField txtGiaTri;
    private JDatePicker dateNgayBatDau;
    private JDatePicker dateNgayKetThuc;

    // Nút thao tác
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoiForm;

    public QLKhuyenMai() {
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

        JLabel lblMaKM = createLabel("Mã KM:");
        JLabel lblTenKM = createLabel("Tên KM:");
        JLabel lblGiaTri = createLabel("Giá trị:");
        JLabel lblTuNgay = createLabel("Từ ngày:");
        JLabel lblDenNgay = createLabel("Đến ngày:");
        JLabel lblTrangThai = createLabel("Trạng thái:");

        txtTimMaKM = new JTextField();
        txtTimTenKhuyenMai = new JTextField();
        txtTimGiaTri = new JTextField();

        dateTuNgay = createDatePicker();
        dateDenNgay = createDatePicker();

        cboLocTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Còn sử dụng", "Hết hạn"
        });

        styleTextField(txtTimMaKM);
        styleTextField(txtTimTenKhuyenMai);
        styleTextField(txtTimGiaTri);
        styleComboBox(cboLocTrangThai);

        btnLamMoiTim = createButton("Làm mới");

        // Dòng 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaKM, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(txtTimMaKM, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblTenKM, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(txtTimTenKhuyenMai, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblGiaTri, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.7;
        panel.add(txtTimGiaTri, gbc);

        // Dòng 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(lblTuNgay, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(dateTuNgay, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblDenNgay, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(dateDenNgay, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblTrangThai, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.7;
        panel.add(cboLocTrangThai, gbc);

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

        splitPane.setDividerLocation(650);
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

        JLabel lblTitle = new JLabel("Danh sách khuyến mãi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        String[] columns = {
                "Mã KM", "Tên khuyến mãi", "Giá trị",
                "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"
        };

        tableModel = new DefaultTableModel(new Object[][] {}, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhuyenMai = new JTable(tableModel);
        styleTable(tblKhuyenMai);

        tblKhuyenMai.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblKhuyenMai.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblKhuyenMai.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblKhuyenMai.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblKhuyenMai.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblKhuyenMai.getColumnModel().getColumn(5).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(tblKhuyenMai);
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

        JLabel lblTitle = new JLabel("Thông tin khuyến mãi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaKM = new JTextField();
        txtTenKhuyenMai = new JTextField();
        txtGiaTri = new JTextField();

        dateNgayBatDau = createDatePicker();
        dateNgayKetThuc = createDatePicker();

        styleTextField(txtMaKM);
        styleTextField(txtTenKhuyenMai);
        styleTextField(txtGiaTri);

        txtMaKM.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã KM", txtMaKM);
        addFormRow(formPanel, gbc, "Tên khuyến mãi", txtTenKhuyenMai);
        addFormRow(formPanel, gbc, "Giá trị", txtGiaTri);
        addFormRow(formPanel, gbc, "Ngày bắt đầu", dateNgayBatDau);
        addFormRow(formPanel, gbc, "Ngày kết thúc", dateNgayKetThuc);

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

    private JDatePicker createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        JDatePicker datePicker = new JDatePicker(model);

        datePicker.setPreferredSize(new Dimension(220, 38));
        datePicker.setMinimumSize(new Dimension(100, 38));
        datePicker.setBackground(Color.WHITE);
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        styleDatePickerChildren(datePicker);

        return datePicker;
    }

    private void styleDatePickerChildren(Container container) {
        for (Component comp : container.getComponents()) {
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (comp instanceof JTextField) {
                JTextField textField = (JTextField) comp;
                textField.setBackground(Color.WHITE);
                textField.setForeground(TEXT_DARK);
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(CARD_BORDER),
                        new EmptyBorder(6, 8, 6, 8)));
                textField.setPreferredSize(new Dimension(160, 34));
                textField.setMinimumSize(new Dimension(120, 34));
            }

            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setText("📅");
                button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                button.setFocusPainted(false);
                button.setBackground(BUTTON_BG);
                button.setForeground(TEXT_DARK);
                button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
                button.setPreferredSize(new Dimension(36, 34));
                button.setMinimumSize(new Dimension(36, 34));
                button.setMargin(new Insets(0, 0, 0, 0));
            }

            if (comp instanceof Container) {
                styleDatePickerChildren((Container) comp);
            }
        }
    }

    private LocalDate getDatePickerValue(JDatePicker datePicker) {
        UtilDateModel model = (UtilDateModel) datePicker.getModel();

        if (!model.isSelected()) {
            return null;
        }

        return LocalDate.of(
                model.getYear(),
                model.getMonth() + 1,
                model.getDay());
    }

    private void setDatePickerValue(JDatePicker datePicker, LocalDate date) {
        UtilDateModel model = (UtilDateModel) datePicker.getModel();

        if (date == null) {
            model.setSelected(false);
            clearDatePickerText(datePicker);
            return;
        }

        model.setDate(
                date.getYear(),
                date.getMonthValue() - 1,
                date.getDayOfMonth()
        );
        model.setSelected(true);
    }
    
    private void clearDatePickerText(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            }

            if (comp instanceof Container) {
                clearDatePickerText((Container) comp);
            }
        }
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

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(220, 38));
        comboBox.setMinimumSize(new Dimension(100, 38));
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
        txtMaKM.setText("");
        txtTenKhuyenMai.setText("");
        txtGiaTri.setText("");

        setDatePickerValue(dateNgayBatDau, null);
        setDatePickerValue(dateNgayKetThuc, null);

        tblKhuyenMai.clearSelection();
    }

    public void clearSearchForm() {
        txtTimMaKM.setText("");
        txtTimTenKhuyenMai.setText("");
        txtTimGiaTri.setText("");

        setDatePickerValue(dateTuNgay, null);
        setDatePickerValue(dateDenNgay, null);

        if (cboLocTrangThai.getItemCount() > 0) {
            cboLocTrangThai.setSelectedIndex(0);
        }
    }

    public JTextField getTxtTimMaKM() {
        return txtTimMaKM;
    }

    public JTextField getTxtTimTenKhuyenMai() {
        return txtTimTenKhuyenMai;
    }

    public JTextField getTxtTimGiaTri() {
        return txtTimGiaTri;
    }

    public LocalDate getTuNgayValue() {
        return getDatePickerValue(dateTuNgay);
    }

    public LocalDate getDenNgayValue() {
        return getDatePickerValue(dateDenNgay);
    }

    public JComboBox<String> getCboLocTrangThai() {
        return cboLocTrangThai;
    }

    public JButton getBtnLamMoiTim() {
        return btnLamMoiTim;
    }

    public JTable getTblKhuyenMai() {
        return tblKhuyenMai;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtMaKM() {
        return txtMaKM;
    }

    public JTextField getTxtTenKhuyenMai() {
        return txtTenKhuyenMai;
    }

    public JTextField getTxtGiaTri() {
        return txtGiaTri;
    }

    public LocalDate getNgayBatDauValue() {
        return getDatePickerValue(dateNgayBatDau);
    }

    public LocalDate getNgayKetThucValue() {
        return getDatePickerValue(dateNgayKetThuc);
    }

    public void setNgayBatDauValue(LocalDate date) {
        setDatePickerValue(dateNgayBatDau, date);
    }

    public void setNgayKetThucValue(LocalDate date) {
        setDatePickerValue(dateNgayKetThuc, date);
    }

    public JDatePicker getDateTuNgay() {
        return dateTuNgay;
    }

    public JDatePicker getDateDenNgay() {
        return dateDenNgay;
    }

    public JDatePicker getDateNgayBatDau() {
        return dateNgayBatDau;
    }

    public JDatePicker getDateNgayKetThuc() {
        return dateNgayKetThuc;
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
        QLKhuyenMai panel = new QLKhuyenMai();
        new QLKhuyenMaiController(panel);
        return panel;
    }
}