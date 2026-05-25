package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

//import javax.swing.ImageIcon;
//import java.awt.Image;

import entity.KhuyenMai;
import entity.Thue;

public class ThanhToanDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);
    private static final Color BUTTON_SUCCESS = new Color(220, 245, 230);

    private JLabel lblMaDDP;
    private JLabel lblTenKH;
    private JLabel lblCCCD;
    private JLabel lblSDT;
    private JLabel lblNgayNhan;
    private JLabel lblNgayTra;

    private JTable tblPhong;
    private DefaultTableModel modelPhong;

    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    private JLabel lblTienPhong;
    private JLabel lblTienDichVu;
    private JLabel lblTongTien;
    private JLabel lblPhiPhat;
    private JLabel lblTienCoc;
    private JLabel lblGiamGia;
    private JLabel lblCanThanhToan;

    private JComboBox<KhuyenMai> cboMaGiamGia;
    private JComboBox<Thue> cboThue;
    private JLabel lblTienThue;

    private JRadioButton rdoTienMat;
    private JRadioButton rdoChuyenKhoan;
    private ButtonGroup groupPhuongThuc;

    private JPanel pnlNoiDungThanhToan;
    private CardLayout cardThanhToan;

    private JTextField txtKhachDua;
    private JLabel lblTienThua;
    private JLabel lblThongTinChuyenKhoan;
    private JLabel lblQrChuyenKhoan;

    private JButton btnThanhToan;
    private JButton btnDong;

    public ThanhToanDialog() {
        setTitle("Thông tin thanh toán");
        setModal(true);
        setSize(920, 620);

        initUI();

        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBackground(PANEL_BG);
        detailPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Thông tin thanh toán", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);

        lblMaDDP = createValueLabel();
        lblTenKH = createValueLabel();
        lblCCCD = createValueLabel();
        lblSDT = createValueLabel();
        lblNgayNhan = createValueLabel();
        lblNgayTra = createValueLabel();

        lblTienPhong = createValueLabel();
        lblTienDichVu = createValueLabel();
        lblTongTien = createValueLabel();
        lblPhiPhat = createValueLabel();
        lblTienCoc = createValueLabel();
        lblGiamGia = createValueLabel();
        lblCanThanhToan = createValueLabel();

        cboMaGiamGia = new JComboBox<>();
        styleComboBox(cboMaGiamGia);

        cboThue = new JComboBox<>();
        styleComboBox(cboThue);

        lblTienThue = createValueLabel();

        rdoTienMat = new JRadioButton("Tiền mặt");
        rdoChuyenKhoan = new JRadioButton("Chuyển khoản");

        styleRadioButton(rdoTienMat);
        styleRadioButton(rdoChuyenKhoan);

        groupPhuongThuc = new ButtonGroup();
        groupPhuongThuc.add(rdoTienMat);
        groupPhuongThuc.add(rdoChuyenKhoan);

        rdoTienMat.setSelected(true);

        txtKhachDua = new JTextField();
        styleTextField(txtKhachDua);

        lblTienThua = createValueLabel();

        lblThongTinChuyenKhoan = createValueLabel();
        lblThongTinChuyenKhoan.setPreferredSize(new Dimension(420, 86));
        lblThongTinChuyenKhoan.setText(
                "<html>"
                        + "Ngân hàng: MB Bank<br>"
                        + "Số TK: 0123456789<br>"
                        + "Chủ TK: HOTEL IMPERIAL<br>"
                        + "Nội dung: Thanh toán đặt phòng"
                        + "</html>");

        lblQrChuyenKhoan = new JLabel("QR");
        lblQrChuyenKhoan.setHorizontalAlignment(JLabel.CENTER);
        lblQrChuyenKhoan.setVerticalAlignment(JLabel.CENTER);
        lblQrChuyenKhoan.setOpaque(true);
        lblQrChuyenKhoan.setBackground(Color.WHITE);
        lblQrChuyenKhoan.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        lblQrChuyenKhoan.setPreferredSize(new Dimension(220, 220));

        cardThanhToan = new CardLayout();
        pnlNoiDungThanhToan = new JPanel(cardThanhToan);
        pnlNoiDungThanhToan.setOpaque(false);

        pnlNoiDungThanhToan.add(createTienMatPanel(), "TIEN_MAT");
        pnlNoiDungThanhToan.add(createChuyenKhoanPanel(), "CHUYEN_KHOAN");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        detailPanel.add(title, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        addSectionTitle(detailPanel, gbc, row++, "Thông tin khách hàng");

        addRowPair(detailPanel, gbc, row++, "Mã đặt phòng", lblMaDDP, "Tên khách hàng", lblTenKH);
        addRowPair(detailPanel, gbc, row++, "CCCD", lblCCCD, "Số điện thoại", lblSDT);
        addRowPair(detailPanel, gbc, row++, "Ngày nhận", lblNgayNhan, "Ngày trả", lblNgayTra);

        addSectionTitle(detailPanel, gbc, row++, "Danh sách phòng");

        modelPhong = new DefaultTableModel(
                new String[] { "Mã phòng", "Loại phòng", "Thời gian lưu trú", "Đơn giá", "Phí phạt", "Thành tiền" },
                0) {
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
        gbc.fill = GridBagConstraints.BOTH;
        detailPanel.add(spDichVu, gbc);

        // addSectionTitle(detailPanel, gbc, row++, "Thanh toán");
        //
        // addRowPair(detailPanel, gbc, row++, "Tiền phòng", lblTienPhong, "Tiền dịch
        // vụ", lblTienDichVu);
        // addRowPair(detailPanel, gbc, row++, "Tổng tiền", lblTongTien, "Tiền cọc",
        // lblTienCoc);
        // addRowPair(detailPanel, gbc, row++, "Mã giảm giá", cboMaGiamGia, "Giảm giá",
        // lblGiamGia);
        // addRowPair(detailPanel, gbc, row++, "Cần thanh toán", lblCanThanhToan,
        // "Phương thức", createPhuongThucPanel());
        addSectionTitle(detailPanel, gbc, row++, "Chi tiết thanh toán");

        addRowPair(detailPanel, gbc, row++, "Tiền phòng", lblTienPhong, "Tiền dịch vụ", lblTienDichVu);
        addRowPair(detailPanel, gbc, row++, "Tổng tiền", lblTongTien, "Tiền cọc", lblTienCoc);
        addRowPair(detailPanel, gbc, row++, "Phí phạt", lblPhiPhat, null, new JPanel());
        addRowPair(detailPanel, gbc, row++, "Mã giảm giá", cboMaGiamGia, "Giảm giá", lblGiamGia);
        addRowPair(detailPanel, gbc, row++, "Thuế", cboThue, "Tiền thuế", lblTienThue);
        addRowPair(detailPanel, gbc, row++, "Cần thanh toán", lblCanThanhToan, "Phương thức", createPhuongThucPanel());

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailPanel.add(pnlNoiDungThanhToan, gbc);

        JScrollPane scrollPane = new JScrollPane(detailPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        hienThiTienMat();
    }

    private JPanel createPhuongThucPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);

        panel.add(rdoTienMat);
        panel.add(rdoChuyenKhoan);

        return panel;
    }

    private JPanel createTienMatPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(12, 12, 12, 12)));

        panel.add(createFieldPair("Khách đưa", txtKhachDua));
        panel.add(createFieldPair("Tiền thừa", lblTienThua));

        return panel;
    }

    private JPanel createChuyenKhoanPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(12, 12, 12, 12)));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(createFieldPair("Thông tin CK", lblThongTinChuyenKhoan), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(lblQrChuyenKhoan, BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 16, 16, 16));

        btnThanhToan = createSuccessButton("Thanh toán");
        btnDong = createDangerButton("Đóng");

        panel.add(btnThanhToan);

        panel.add(btnDong);

        return panel;
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, int row, String title) {
        JLabel label = createLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(label, gbc);
        gbc.gridwidth = 1;
    }

    private JPanel createFieldPair(String labelText, JComponent valueComp) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(115, 32));

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
            JComponent value2) {
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
        label.setPreferredSize(new Dimension(120, 34));
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
        comboBox.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
    }

    private void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radioButton.setForeground(TEXT_DARK);
        radioButton.setBackground(PANEL_BG);
        radioButton.setFocusPainted(false);
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

    public void hienThiTienMat() {
        cardThanhToan.show(pnlNoiDungThanhToan, "TIEN_MAT");
        txtKhachDua.setEnabled(true);
        txtKhachDua.setEditable(true);
    }

    public void hienThiChuyenKhoan() {
        cardThanhToan.show(pnlNoiDungThanhToan, "CHUYEN_KHOAN");
        txtKhachDua.setText("");
        txtKhachDua.setEnabled(false);
        txtKhachDua.setEditable(false);
        lblTienThua.setText("");
    }

    public String getPhuongThucThanhToan() {
        if (rdoTienMat.isSelected()) {
            return "Tiền mặt";
        }

        return "Chuyển khoản";
    }

    public void clearDetail() {
        lblMaDDP.setText("");
        lblTenKH.setText("");
        lblCCCD.setText("");
        lblSDT.setText("");
        lblNgayNhan.setText("");
        lblNgayTra.setText("");

        lblTienPhong.setText("");
        lblTienDichVu.setText("");
        lblTongTien.setText("");
        lblTienCoc.setText("");
        lblGiamGia.setText("");
        lblTienThue.setText("");
        lblCanThanhToan.setText("");

        txtKhachDua.setText("");
        lblTienThua.setText("");

        modelPhong.setRowCount(0);
        modelDichVu.setRowCount(0);
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

    public DefaultTableModel getModelPhong() {
        return modelPhong;
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

    public JLabel getLblTienCoc() {
        return lblTienCoc;
    }

    public JLabel getLblGiamGia() {
        return lblGiamGia;
    }

    public JLabel getLblCanThanhToan() {
        return lblCanThanhToan;
    }

    public JComboBox<KhuyenMai> getCboMaGiamGia() {
        return cboMaGiamGia;
    }

    public JComboBox<Thue> getCboThue() {
        return cboThue;
    }

    public JLabel getLblTienThue() {
        return lblTienThue;
    }

    public JLabel getLblPhiPhat() {
        return lblPhiPhat;
    }

    public JRadioButton getRdoTienMat() {
        return rdoTienMat;
    }

    public JRadioButton getRdoChuyenKhoan() {
        return rdoChuyenKhoan;
    }

    public JTextField getTxtKhachDua() {
        return txtKhachDua;
    }

    public JLabel getLblTienThua() {
        return lblTienThua;
    }

    public JLabel getLblThongTinChuyenKhoan() {
        return lblThongTinChuyenKhoan;
    }

    public JButton getBtnThanhToan() {
        return btnThanhToan;
    }

    public JButton getBtnDong() {
        return btnDong;
    }

    public JLabel getLblQrChuyenKhoan() {
        return lblQrChuyenKhoan;
    }
}