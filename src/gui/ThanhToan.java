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

import controller.ThanhToanController;

public class ThanhToan extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);
    private static final Color BUTTON_SUCCESS = new Color(220, 245, 230);

    private ThanhToanController controller;

    // Bộ lọc
    private JTextField txtMaDDP;
    private JTextField txtCccdSdt;
    private JButton btnLamMoi;

    // Danh sách đơn chờ thanh toán
    private JTable tblDonChoThanhToan;
    private DefaultTableModel modelDonChoThanhToan;

    // Thông tin đơn
    private JLabel lblMaDDP;
    private JLabel lblTenKH;
    private JLabel lblCCCD;
    private JLabel lblSDT;
    private JLabel lblNgayNhan;
    private JLabel lblNgayTra;
    private JLabel lblTienCoc;

    // Phòng
    private JTable tblPhong;
    private DefaultTableModel modelPhong;

    // Dịch vụ
    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    // Tổng tiền
    private JLabel lblTienPhong;
    private JLabel lblTienDichVu;
    private JLabel lblTongTien;
    private JLabel lblGiamGia;
    private JLabel lblCanThanhToan;

    private JComboBox<String> cboPhuongThuc;
    private JTextField txtKhachDua;
    private JLabel lblTienThua;


    private JButton btnThanhToan;
    private JButton btnXuatHoaDon;
    private JButton btnHuy;

    public ThanhToan() {
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

        JLabel lblMaDDPFilter = createLabel("Mã ĐĐP:");
        JLabel lblCccdSdtFilter = createLabel("CCCD / SĐT:");

        txtMaDDP = new JTextField();
        txtCccdSdt = new JTextField();

        styleTextField(txtMaDDP);
        styleTextField(txtCccdSdt);

        btnLamMoi = createButton("Làm mới");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaDDPFilter, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(txtMaDDP, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblCccdSdtFilter, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(txtCccdSdt, gbc);



        gbc.gridx = 4;
        panel.add(btnLamMoi, gbc);

        return panel;
    }

    private JPanel createBodyPanel() {
    	JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(createLeftPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Danh sách đơn chờ thanh toán");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        String[] cols = {
                "Mã ĐĐP", "Khách hàng", "CCCD", "SĐT", "Ngày nhận", "Ngày trả", "Tiền cọc"
        };

        modelDonChoThanhToan = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDonChoThanhToan = new JTable(modelDonChoThanhToan);
        styleTable(tblDonChoThanhToan);

        tblDonChoThanhToan.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblDonChoThanhToan.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblDonChoThanhToan.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblDonChoThanhToan.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblDonChoThanhToan.getColumnModel().getColumn(4).setPreferredWidth(130);
        tblDonChoThanhToan.getColumnModel().getColumn(5).setPreferredWidth(130);
        tblDonChoThanhToan.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblDonChoThanhToan);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        btnThanhToan = createSuccessButton("Thanh toán");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        bottomPanel.add(btnThanhToan, BorderLayout.EAST);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(APP_BG);
        wrapper.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBackground(PANEL_BG);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Thông tin thanh toán");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        lblMaDDP = createValueLabel();
        lblTenKH = createValueLabel();
        lblCCCD = createValueLabel();
        lblSDT = createValueLabel();
        lblNgayNhan = createValueLabel();
        lblNgayTra = createValueLabel();
        lblTienCoc = createValueLabel();

        lblTienPhong = createValueLabel();
        lblTienDichVu = createValueLabel();
        lblTongTien = createValueLabel();
        lblGiamGia = createValueLabel();
        lblCanThanhToan = createValueLabel();
        lblTienThua = createValueLabel();

        cboPhuongThuc = new JComboBox<>(new String[] {
                "Tiền mặt", "Chuyển khoản", "Thẻ"
        });
        styleComboBox(cboPhuongThuc);

        txtKhachDua = new JTextField();
        styleTextField(txtKhachDua);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        detailPanel.add(title, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        addRowPair(detailPanel, gbc, row++,
                "Mã ĐĐP", lblMaDDP,
                "Tên khách", lblTenKH);

        addRowPair(detailPanel, gbc, row++,
                "CCCD", lblCCCD,
                "SĐT", lblSDT);

        addRowPair(detailPanel, gbc, row++,
                "Ngày nhận", lblNgayNhan,
                "Ngày trả", lblNgayTra);

        addRowPair(detailPanel, gbc, row++,
                "Tiền cọc", lblTienCoc,
                "Phương thức", cboPhuongThuc);

        addSectionTitle(detailPanel, gbc, row++, "Danh sách phòng");

        modelPhong = new DefaultTableModel(
                new String[] { "Mã phòng", "Loại phòng", "Số ngày", "Đơn giá", "Thành tiền" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhong = new JTable(modelPhong);
        styleSmallTable(tblPhong);

        JScrollPane spPhong = new JScrollPane(tblPhong);
        spPhong.setPreferredSize(new Dimension(0, 120));
        spPhong.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        detailPanel.add(spPhong, gbc);

        addSectionTitle(detailPanel, gbc, row++, "Dịch vụ sử dụng");

        modelDichVu = new DefaultTableModel(
                new String[] { "Mã DV", "Tên dịch vụ", "Số lượng", "Đơn giá", "Thành tiền" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDichVu = new JTable(modelDichVu);
        styleSmallTable(tblDichVu);

        JScrollPane spDichVu = new JScrollPane(tblDichVu);
        spDichVu.setPreferredSize(new Dimension(0, 120));
        spDichVu.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        detailPanel.add(spDichVu, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRowPair(detailPanel, gbc, row++,
                "Tiền phòng", lblTienPhong,
                "Tiền dịch vụ", lblTienDichVu);

        addRowPair(detailPanel, gbc, row++,
                "Tổng tiền", lblTongTien,
                "Giảm giá", lblGiamGia);

        addRowPair(detailPanel, gbc, row++,
                "Cần thanh toán", lblCanThanhToan,
                "Khách đưa", txtKhachDua);

        addRowPair(detailPanel, gbc, row++,
                "Tiền thừa", lblTienThua,
                "", new JLabel());

        JScrollPane scrollPane = new JScrollPane(detailPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(PANEL_BG);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 14));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnThanhToan = createSuccessButton("Thanh toán");
        btnXuatHoaDon = createButton("Xuất hóa đơn");
        btnHuy = createDangerButton("Hủy");

        panel.add(btnThanhToan);
        panel.add(btnXuatHoaDon);
        panel.add(btnHuy);

        return panel;
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, int row, String title) {
        JLabel label = createLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(label, gbc);
    }

    private JPanel createFieldPair(String labelText, JComponent valueComp) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(110, 32));

        panel.add(label, BorderLayout.WEST);
        panel.add(valueComp, BorderLayout.CENTER);

        return panel;
    }

    private void addRowPair(
            JPanel parent,
            GridBagConstraints gbc,
            int row,
            String label1,
            JComponent value1,
            String label2,
            JComponent value2
    ) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        rowPanel.setOpaque(false);

        rowPanel.add(createFieldPair(label1, value1));

        if (label2 == null || label2.isBlank()) {
            rowPanel.add(new JPanel());
        } else {
            rowPanel.add(createFieldPair(label2, value2));
        }

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        parent.add(rowPanel, gbc);

        gbc.gridwidth = 1;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
        label.setPreferredSize(new Dimension(120, 32));
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(160, 34));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(160, 34));
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

    private void styleSmallTable(JTable table) {
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(233, 239, 248));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setGridColor(CARD_BORDER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT_DARK);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        button.setPreferredSize(new Dimension(140, 42));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_DANGER);
        return button;
    }

    private JButton createSuccessButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_SUCCESS);
        return button;
    }

    public void clearDetail() {
        
    }

    public void clearSearch() {
        txtMaDDP.setText("");
        txtCccdSdt.setText("");
    }

    public void loadMacDinh() {
        if (controller != null) {
            controller.loadDanhSachDonDaNhan();
        }
    }

    public void loadDonThanhToan(String maDDP) {
        if (controller != null) {
            controller.loadTheoMaDDP(maDDP);
        }
    }

    public void setController(ThanhToanController controller) {
        this.controller = controller;
    }

    public JTextField getTxtMaDDP() {
        return txtMaDDP;
    }

    public JTextField getTxtCccdSdt() {
        return txtCccdSdt;
    }



    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JTable getTblDonChoThanhToan() {
        return tblDonChoThanhToan;
    }

    public DefaultTableModel getModelDonChoThanhToan() {
        return modelDonChoThanhToan;
    }

    public JLabel getLblMaDDP() {
        return lblMaDDP;
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

    public JLabel getLblNgayNhan() {
        return lblNgayNhan;
    }

    public JLabel getLblNgayTra() {
        return lblNgayTra;
    }

    public JLabel getLblTienCoc() {
        return lblTienCoc;
    }

    public JTable getTblPhong() {
        return tblPhong;
    }

    public DefaultTableModel getModelPhong() {
        return modelPhong;
    }

    public JTable getTblDichVu() {
        return tblDichVu;
    }

    public DefaultTableModel getModelDichVu() {
        return modelDichVu;
    }

    public JLabel getLblTienPhong() {
        return lblTienPhong;
    }

    public JLabel getLblTienDichVu() {
        return lblTienDichVu;
    }

    public JLabel getLblTongTien() {
        return lblTongTien;
    }

    public JLabel getLblGiamGia() {
        return lblGiamGia;
    }

    public JLabel getLblCanThanhToan() {
        return lblCanThanhToan;
    }

    public JComboBox<String> getCboPhuongThuc() {
        return cboPhuongThuc;
    }

    public JTextField getTxtKhachDua() {
        return txtKhachDua;
    }

    public JLabel getLblTienThua() {
        return lblTienThua;
    }



    public JButton getBtnThanhToan() {
        return btnThanhToan;
    }

    public JButton getBtnXuatHoaDon() {
        return btnXuatHoaDon;
    }

    public JButton getBtnHuy() {
        return btnHuy;
    }

    public static JPanel createPanel() {
        ThanhToan panel = new ThanhToan();
        ThanhToanController controller = new ThanhToanController(panel);
        panel.setController(controller);
        panel.loadMacDinh();
        return panel;
    }
}