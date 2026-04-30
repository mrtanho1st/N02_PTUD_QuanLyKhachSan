package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;

import controller.QLHoaDonController;

public class QLHoaDon extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);

    // Bộ lọc
    private JTextField txtTimMaHD;
    private JTextField txtTimMaDDP;
    private JTextField txtTimKhachHang;
    private JTextField txtTimNhanVien;
    private JTextField txtTimTongTien;
    private JDatePicker dateTuNgay;
    private JDatePicker dateDenNgay;
    private JComboBox<String> cboLocThue;
    private JButton btnLamMoiTim;

    // Bảng hóa đơn
    private JTable tblHoaDon;
    private DefaultTableModel tableModel;

    public QLHoaDon() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 18, 16, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblMaHD = createLabel("Mã HĐ:");
        JLabel lblMaDDP = createLabel("Mã ĐĐP:");
        JLabel lblKhachHang = createLabel("Khách hàng:");
        JLabel lblNhanVien = createLabel("Nhân viên:");
        JLabel lblTongTien = createLabel("Tổng tiền:");
        JLabel lblTuNgay = createLabel("Từ ngày:");
        JLabel lblDenNgay = createLabel("Đến ngày:");
        JLabel lblThue = createLabel("Thuế:");

        txtTimMaHD = new JTextField();
        txtTimMaDDP = new JTextField();
        txtTimKhachHang = new JTextField();
        txtTimNhanVien = new JTextField();
        txtTimTongTien = new JTextField();

        dateTuNgay = createDatePicker();
        dateDenNgay = createDatePicker();

        cboLocThue = new JComboBox<>();

        styleTextField(txtTimMaHD);
        styleTextField(txtTimMaDDP);
        styleTextField(txtTimKhachHang);
        styleTextField(txtTimNhanVien);
        styleTextField(txtTimTongTien);
        styleComboBox(cboLocThue);

        btnLamMoiTim = createButton("Làm mới");

        // Dòng 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaHD, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(txtTimMaHD, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblMaDDP, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.7;
        panel.add(txtTimMaDDP, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblKhachHang, gbc);

        gbc.gridx = 5;
        gbc.weightx = 1.0;
        panel.add(txtTimKhachHang, gbc);

        // Dòng 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(lblNhanVien, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(txtTimNhanVien, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblTongTien, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.7;
        panel.add(txtTimTongTien, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblThue, gbc);

        gbc.gridx = 5;
        gbc.weightx = 1.0;
        panel.add(cboLocThue, gbc);

        // Dòng 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(lblTuNgay, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(dateTuNgay, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblDenNgay, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.7;
        panel.add(dateDenNgay, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0;
        panel.add(btnLamMoiTim, gbc);

        return panel;
    }
    
    public void setDanhSachThue(java.util.List<String> dsThue) {
        cboLocThue.removeAllItems();

        cboLocThue.addItem("Tất cả");

        if (dsThue != null) {
            for (String thue : dsThue) {
                cboLocThue.addItem(thue);
            }
        }

        cboLocThue.setSelectedIndex(0);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblTitle = new JLabel("Danh sách hóa đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        String[] columns = {
                "Mã HĐ",
                "Mã ĐĐP",
                "Khách hàng",
                "NV lập hóa đơn",
                "Ngày lập",
                "Thuế",
                "Tổng tiền"
        };

        tableModel = new DefaultTableModel(new Object[][] {}, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = new JTable(tableModel);
        styleTable(tblHoaDon);

        tblHoaDon.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblHoaDon.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblHoaDon.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblHoaDon.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblHoaDon.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblHoaDon.getColumnModel().getColumn(5).setPreferredWidth(90);
        tblHoaDon.getColumnModel().getColumn(6).setPreferredWidth(130);

        tblHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblHoaDon.getSelectedRow() >= 0) {
                    moHoaDonDialogTuDongDangChon();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void moHoaDonDialogTuDongDangChon() {
        int selectedRow = tblHoaDon.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow = tblHoaDon.convertRowIndexToModel(selectedRow);
        String maHD = String.valueOf(tableModel.getValueAt(modelRow, 0));

        if (maHD == null || maHD.isBlank()) {
            return;
        }

        HoaDonDialog dialog = new HoaDonDialog(maHD);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
                        new EmptyBorder(6, 8, 6, 8)
                ));
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
                model.getDay()
        );
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
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(220, 38));
        comboBox.setMinimumSize(new Dimension(100, 38));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
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

    public void clearSearchForm() {
        txtTimMaHD.setText("");
        txtTimMaDDP.setText("");
        txtTimKhachHang.setText("");
        txtTimNhanVien.setText("");
        txtTimTongTien.setText("");

        setDatePickerValue(dateTuNgay, null);
        setDatePickerValue(dateDenNgay, null);

        if (cboLocThue.getItemCount() > 0) {
            cboLocThue.setSelectedIndex(0);
        }
    }

    public JTextField getTxtTimMaHD() {
        return txtTimMaHD;
    }

    public JTextField getTxtTimMaDDP() {
        return txtTimMaDDP;
    }

    public JTextField getTxtTimKhachHang() {
        return txtTimKhachHang;
    }

    public JTextField getTxtTimNhanVien() {
        return txtTimNhanVien;
    }

    public JTextField getTxtTimTongTien() {
        return txtTimTongTien;
    }

    public LocalDate getTuNgayValue() {
        return getDatePickerValue(dateTuNgay);
    }

    public LocalDate getDenNgayValue() {
        return getDatePickerValue(dateDenNgay);
    }

    public JDatePicker getDateTuNgay() {
        return dateTuNgay;
    }

    public JDatePicker getDateDenNgay() {
        return dateDenNgay;
    }

    public JComboBox<String> getCboLocThue() {
        return cboLocThue;
    }

    public JButton getBtnLamMoiTim() {
        return btnLamMoiTim;
    }

    public JTable getTblHoaDon() {
        return tblHoaDon;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public static JPanel createPanel() {
        QLHoaDon panel = new QLHoaDon();
        new QLHoaDonController(panel);
        return panel;
    }
}