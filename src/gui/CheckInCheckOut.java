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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.CheckInCheckOutController;

public class CheckInCheckOut extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    private JTextField txtMaPhong;
    private JTextField txtMaDDP;
    private JTextField txtCccdSdt;
    private JComboBox<String> cboTrangThai;
    private JButton btnTaiLai;

    private JTable tblDanhSach;
    private DefaultTableModel modelDanhSach;

    private JLabel lblMaPhong;
    private JLabel lblLoaiPhong;
    private JLabel lblGiaPhong;
    private JLabel lblTrangThaiPhong;

    private JLabel lblTenKH;
    private JLabel lblCCCD;
    private JLabel lblSDT;
    private JLabel lblMaDDPChiTiet;

    private JLabel lblCheckInDuKien;
    private JLabel lblCheckOutDuKien;
    private JLabel lblCheckInThucTe;
    private JLabel lblCheckOutThucTe;

    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    private JLabel lblTienPhong;
    private JLabel lblTienDichVu;
    private JLabel lblTienCoc;
    private JLabel lblConLai;

    private JButton btnCheckIn;
    private JButton btnThemDichVu;
    private JButton btnCapNhatThoiGian;
    private JButton btnCheckOut;
    private JButton btnLamMoi;

    public CheckInCheckOut() {
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

        JLabel lblMaPhong = createLabel("Mã phòng:");
        JLabel lblMaDDP = createLabel("Mã ĐĐP:");
        JLabel lblCccdSdt = createLabel("CCCD / SĐT:");
        JLabel lblTrangThai = createLabel("Trạng thái:");

        txtMaPhong = new JTextField();
        txtMaDDP = new JTextField();
        txtCccdSdt = new JTextField();

        cboTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Chờ check-in", "Đang lưu trú"
        });

        styleTextField(txtMaPhong);
        styleTextField(txtMaDDP);
        styleTextField(txtCccdSdt);
        styleComboBox(cboTrangThai);

        btnTaiLai = createButton("Làm mới");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaPhong, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(txtMaPhong, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblMaDDP, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.8;
        panel.add(txtMaDDP, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblCccdSdt, gbc);

        gbc.gridx = 5;
        gbc.weightx = 1.0;
        panel.add(txtCccdSdt, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        panel.add(lblTrangThai, gbc);

        gbc.gridx = 7;
        gbc.weightx = 0.8;
        panel.add(cboTrangThai, gbc);


        gbc.gridx = 8;
        gbc.weightx = 0;
        panel.add(btnTaiLai, gbc);

        return panel;
    }

    private JPanel createBodyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createTablePanel(),
                createDetailPanel());
        splitPane.setDividerLocation(620);
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

        JLabel lblTitle = new JLabel("Danh sách check-in / check-out");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        panel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {
                "Mã phòng", "Khách hàng", "Trạng thái", "Ngày nhận", "Ngày trả"
        };

        modelDanhSach = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDanhSach = new JTable(modelDanhSach);
        tblDanhSach.setRowHeight(28);
        tblDanhSach.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblDanhSach.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblDanhSach.getTableHeader().setBackground(new Color(233, 239, 248));
        tblDanhSach.getTableHeader().setForeground(TEXT_DARK);
        tblDanhSach.setGridColor(CARD_BORDER);
        tblDanhSach.setSelectionBackground(new Color(221, 232, 248));
        tblDanhSach.setSelectionForeground(TEXT_DARK);

        JScrollPane scrollPane = new JScrollPane(tblDanhSach);
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

        JLabel lblTitle = new JLabel("Thông tin check-in / check-out");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        lblMaPhong = createValueLabel();
        lblLoaiPhong = createValueLabel();
        lblGiaPhong = createValueLabel();
        lblTrangThaiPhong = createValueLabel();

        lblTenKH = createValueLabel();
        lblCCCD = createValueLabel();
        lblSDT = createValueLabel();
        lblMaDDPChiTiet = createValueLabel();

        lblCheckInDuKien = createValueLabel();
        lblCheckOutDuKien = createValueLabel();
        lblCheckInThucTe = createValueLabel();
        lblCheckOutThucTe = createValueLabel();

        lblTienPhong = createValueLabel();
        lblTienDichVu = createValueLabel();
        lblTienCoc = createValueLabel();
        lblConLai = createValueLabel();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        int row = gbc.gridy;

        addRowPair(formPanel, gbc, row++,
                "Mã phòng", lblMaPhong,
                "Loại phòng", lblLoaiPhong);

        addRowPair(formPanel, gbc, row++,
                "Giá phòng", lblGiaPhong,
                "Trạng thái phòng", lblTrangThaiPhong);

        addRowPair(formPanel, gbc, row++,
                "Tên khách", lblTenKH,
                "CCCD", lblCCCD);

        addRowPair(formPanel, gbc, row++,
                "SĐT", lblSDT,
                "Mã ĐĐP", lblMaDDPChiTiet);

        addRowPair(formPanel, gbc, row++,
                "Check-in dự kiến", lblCheckInDuKien,
                "Check-out dự kiến", lblCheckOutDuKien);

        addRowPair(formPanel, gbc, row++,
                "Check-in thực tế", lblCheckInThucTe,
                "Check-out thực tế", lblCheckOutThucTe);

        addRowPair(formPanel, gbc, row++,
                "Tiền phòng", lblTienPhong,
                "Tiền dịch vụ", lblTienDichVu);

        addRowPair(formPanel, gbc, row++,
                "Tiền cọc", lblTienCoc,
                "Còn lại", lblConLai);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 4; // chiếm toàn bộ form
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(createLabel("Dịch vụ"), gbc);

        row++;

        modelDichVu = new DefaultTableModel(
                new String[] { "Mã DV", "Tên dịch vụ", "SL", "Thành tiền" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDichVu = new JTable(modelDichVu);
        tblDichVu.setRowHeight(26);
        tblDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblDichVu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblDichVu.getTableHeader().setBackground(new Color(233, 239, 248));
        tblDichVu.getTableHeader().setForeground(TEXT_DARK);
        tblDichVu.setGridColor(CARD_BORDER);

        JScrollPane spDichVu = new JScrollPane(tblDichVu);
        spDichVu.setPreferredSize(new Dimension(0, 140)); // chiều ngang tự giãn hết khung
        spDichVu.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 4; // chiếm toàn bộ form
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(spDichVu, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(PANEL_BG);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        return wrapper;
    }
    private JPanel createFieldPair(String labelText, JComponent valueComp) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(130, 32));

        panel.add(label, BorderLayout.WEST);
        panel.add(valueComp, BorderLayout.CENTER);

        return panel;
    }
    private void addRowPair(JPanel parent, GridBagConstraints gbc, int row,
            String label1, JComponent value1,
            String label2, JComponent value2) {

        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        rowPanel.setOpaque(false);

        rowPanel.add(createFieldPair(label1, value1));
        rowPanel.add(createFieldPair(label2, value2));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        parent.add(rowPanel, gbc);

        gbc.gridwidth = 1;
    }
    

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 12, 12));
        panel.setBackground(APP_BG);

        btnCheckIn = createButton("Xác nhận Check-in");
        btnThemDichVu = createButton("Thêm dịch vụ");
        btnCapNhatThoiGian = createButton("Cập nhật thời gian");
        btnCheckOut = createDangerButton("Check-out");
        btnLamMoi = createButton("Làm mới");

        panel.add(btnCheckIn);
        panel.add(btnThemDichVu);
        panel.add(btnCapNhatThoiGian);
        panel.add(btnCheckOut);
        panel.add(btnLamMoi);
        panel.add(new JPanel());

        return panel;
    }




    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(150, 32));
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

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(100, 32));
        return label;
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
        lbl.setPreferredSize(new Dimension(120, 32));
        lbl.setMinimumSize(new Dimension(120, 32));
        lbl.setMaximumSize(new Dimension(120, 32));
        return lbl;
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

    public JTextField getTxtMaPhong() {
        return txtMaPhong;
    }

    public JTextField getTxtMaDDP() {
        return txtMaDDP;
    }

    public JTextField getTxtCccdSdt() {
        return txtCccdSdt;
    }

    public JComboBox<String> getCboTrangThai() {
        return cboTrangThai;
    }



    public JButton getBtnTaiLai() {
        return btnTaiLai;
    }

    public JTable getTblDanhSach() {
        return tblDanhSach;
    }

    public DefaultTableModel getModelDanhSach() {
        return modelDanhSach;
    }

    public JTable getTblDichVu() {
        return tblDichVu;
    }

    public DefaultTableModel getModelDichVu() {
        return modelDichVu;
    }

    public JLabel getLblMaPhong() {
        return lblMaPhong;
    }

    public JLabel getLblLoaiPhong() {
        return lblLoaiPhong;
    }

    public JLabel getLblGiaPhong() {
        return lblGiaPhong;
    }

    public JLabel getLblTrangThaiPhong() {
        return lblTrangThaiPhong;
    }

    public JLabel getLblTenKH() {
        return lblTenKH;
    }

    public JLabel getLblCCCD() {
        return lblCCCD;
    }

    public JLabel getLblSDT() {
        return lblSDT;
    }

    public JLabel getLblMaDDPChiTiet() {
        return lblMaDDPChiTiet;
    }

    public JLabel getLblCheckInDuKien() {
        return lblCheckInDuKien;
    }

    public JLabel getLblCheckOutDuKien() {
        return lblCheckOutDuKien;
    }

    public JLabel getLblCheckInThucTe() {
        return lblCheckInThucTe;
    }

    public JLabel getLblCheckOutThucTe() {
        return lblCheckOutThucTe;
    }

    public JLabel getLblTienPhong() {
        return lblTienPhong;
    }

    public JLabel getLblTienDichVu() {
        return lblTienDichVu;
    }

    public JLabel getLblTienCoc() {
        return lblTienCoc;
    }

    public JLabel getLblConLai() {
        return lblConLai;
    }

    public JButton getBtnCheckIn() {
        return btnCheckIn;
    }

    public JButton getBtnThemDichVu() {
        return btnThemDichVu;
    }

    public JButton getBtnCapNhatThoiGian() {
        return btnCapNhatThoiGian;
    }

    public JButton getBtnCheckOut() {
        return btnCheckOut;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public static JPanel createPanel() {
        CheckInCheckOut panel = new CheckInCheckOut();
        new CheckInCheckOutController(panel);
        return panel;
    }
}