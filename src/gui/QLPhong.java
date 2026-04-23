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

import controller.QLPhongController;
import entity.LoaiPhong;

public class QLPhong extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    private JTextField txtTimMaPhong;
    private JTextField txtTimSoNguoiToiDa;
    private JComboBox<String> cboLocLoaiPhong;
    private JComboBox<String> cboLocTrangThai;

    private JTable tblPhong;
    private DefaultTableModel tableModel;

    private JTextField txtMaPhong;
    private JComboBox<LoaiPhong> cboLoaiPhong;
    private JTextField txtSoNguoiToiDa;
    private JTextField txtGiaPhong;
    private JComboBox<String> cboTrangThaiPhong;


    private JButton btnLamMoiTim;
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoiForm;

    public QLPhong() {
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

        JLabel lblTim = createLabel("Tìm mã phòng:");
        JLabel lblSoNguoi = createLabel("Số người:");
        JLabel lblLoai = createLabel("Loại phòng:");
        JLabel lblTrangThai = createLabel("Trạng thái:");

        txtTimMaPhong = new JTextField();
        txtTimSoNguoiToiDa = new JTextField();

        cboLocLoaiPhong = new JComboBox<>(new String[] {
                "Tất cả", "Standard", "Superior", "Deluxe", "Suite"
        });

        cboLocTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Trống", "Đang sử dụng", "Bảo trì"
        });
        styleTextField(txtTimSoNguoiToiDa);
        styleTextField(txtTimMaPhong);
        styleComboBox(cboLocLoaiPhong);
        styleComboBox(cboLocTrangThai);

        btnLamMoiTim = createButton("Làm mới");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblTim, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtTimMaPhong, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblSoNguoi, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.6;
        panel.add(txtTimSoNguoiToiDa, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblLoai, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.6;
        panel.add(cboLocLoaiPhong, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        panel.add(lblTrangThai, gbc);

        gbc.gridx = 7;
        gbc.weightx = 0.6;
        panel.add(cboLocTrangThai, gbc);



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
        splitPane.setDividerLocation(600);
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

        JLabel lblTitle = new JLabel("Danh sách phòng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        panel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {
                "Mã phòng", "Loại phòng", "Số người tối đa", "Giá phòng", "Trạng thái"
        };

        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhong = new JTable(tableModel);
        tblPhong.setRowHeight(28);
        tblPhong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblPhong.getTableHeader().setBackground(new Color(233, 239, 248));
        tblPhong.getTableHeader().setForeground(TEXT_DARK);
        tblPhong.setGridColor(CARD_BORDER);
        tblPhong.setSelectionBackground(new Color(221, 232, 248));
        tblPhong.setSelectionForeground(TEXT_DARK);

        JScrollPane scrollPane = new JScrollPane(tblPhong);
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

        JLabel lblTitle = new JLabel("Thông tin phòng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaPhong = new JTextField();

        cboLoaiPhong = new JComboBox<>();
        for (LoaiPhong lp : LoaiPhong.values()) {
            cboLoaiPhong.addItem(lp);
        }

        txtSoNguoiToiDa = new JTextField();
        txtGiaPhong = new JTextField();

        cboTrangThaiPhong = new JComboBox<>(new String[] {
                "Trống", "Đang sử dụng", "Bảo trì"
        });

        styleTextField(txtMaPhong);
        styleComboBox(cboLoaiPhong);
        styleTextField(txtSoNguoiToiDa);
        styleTextField(txtGiaPhong);
        styleComboBox(cboTrangThaiPhong);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã phòng", txtMaPhong);
        addFormRow(formPanel, gbc, "Loại phòng", cboLoaiPhong);
        addFormRow(formPanel, gbc, "Số người tối đa", txtSoNguoiToiDa);
        addFormRow(formPanel, gbc, "Giá phòng", txtGiaPhong);
        addFormRow(formPanel, gbc, "Trạng thái phòng", cboTrangThaiPhong);

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBackground(APP_BG);

        btnThem = createButton("Thêm");
        btnSua = createButton("Cập nhật");
        btnXoa = createDangerButton("Xóa");
        btnLamMoiForm = createButton("Làm mới");

        panel.add(btnThem);
        panel.add(btnSua);
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
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT_DARK);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_DANGER);
        return button;
    }

    public JTextField getTxtTimMaPhong() {
        return txtTimMaPhong;
    }
    
    public JTextField getTxtTimSoNguoi() {
        return txtTimSoNguoiToiDa;
    }

    public JComboBox<String> getCboLocLoaiPhong() {
        return cboLocLoaiPhong;
    }

    public JComboBox<String> getCboLocTrangThai() {
        return cboLocTrangThai;
    }

    public JTable getTblPhong() {
        return tblPhong;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtMaPhong() {
        return txtMaPhong;
    }

    public JComboBox<LoaiPhong> getCboLoaiPhong() {
        return cboLoaiPhong;
    }

    public JTextField getTxtSoNguoiToiDa() {
        return txtSoNguoiToiDa;
    }

    public JTextField getTxtGiaPhong() {
        return txtGiaPhong;
    }

    public JComboBox<String> getCboTrangThaiPhong() {
        return cboTrangThaiPhong;
    }

   

    public JButton getBtnLamMoiTim() {
        return btnLamMoiTim;
    }

    public JButton getBtnThem() {
        return btnThem;
    }

    public JButton getBtnSua() {
        return btnSua;
    }

    public JButton getBtnXoa() {
        return btnXoa;
    }

    public JButton getBtnLamMoiForm() {
        return btnLamMoiForm;
    }

    public static JPanel createPanel() {
        QLPhong panel = new QLPhong();
        new QLPhongController(panel);
        return panel;
    }
    
   
}