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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.BaoBieuController;
import entity.LoaiBaoBieu;

public class BaoBieu extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);

    private final LoaiBaoBieu loaiBaoBieu;

    private JLabel lblTitle;

    private JLabel lblTuNgay;
    private JLabel lblDenNgay;
    private JLabel lblTuKhoa;
    private JLabel lblLoc1;
    private JLabel lblLoc2;

    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JTextField txtTuKhoa;

    private JComboBox<String> cboLoc1;
    private JComboBox<String> cboLoc2;

    private JButton btnXemBaoCao;
    private JButton btnLamMoi;
    private JButton btnXuatExcel;
    private JButton btnInPdf;

    private JLabel lblCard1Title;
    private JLabel lblCard2Title;
    private JLabel lblCard3Title;

    private JLabel lblCard1Value;
    private JLabel lblCard2Value;
    private JLabel lblCard3Value;

    private JTable tblBaoBieu;
    private DefaultTableModel tableModel;

    private JPanel pnlFilter;
    private JPanel pnlThongKe;

    public BaoBieu(LoaiBaoBieu loaiBaoBieu) {
        this.loaiBaoBieu = loaiBaoBieu;
        initUI();
        cauHinhMacDinhTheoLoai();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        pnlFilter = createFilterPanel();

        wrapper.add(pnlFilter, BorderLayout.CENTER);

        return wrapper;
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

        lblTuNgay = createLabel("Từ ngày:");
        lblDenNgay = createLabel("Đến ngày:");
        lblTuKhoa = createLabel("Từ khóa:");
        lblLoc1 = createLabel("Lọc 1:");
        lblLoc2 = createLabel("Lọc 2:");

        txtTuNgay = new JTextField();
        txtDenNgay = new JTextField();
        txtTuKhoa = new JTextField();

        cboLoc1 = new JComboBox<>(new String[] { "Tất cả" });
        cboLoc2 = new JComboBox<>(new String[] { "Tất cả" });

        styleTextField(txtTuNgay);
        styleTextField(txtDenNgay);
        styleTextField(txtTuKhoa);
        styleComboBox(cboLoc1);
        styleComboBox(cboLoc2);

        btnXemBaoCao = createButton("Xem báo cáo");
        btnLamMoi = createButton("Làm mới");
        btnXuatExcel = createButton("Xuất Excel");
        btnInPdf = createButton("In / PDF");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(lblTuNgay, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        panel.add(txtTuNgay, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblDenNgay, gbc);

        gbc.gridx = 3; gbc.weightx = 0.6;
        panel.add(txtDenNgay, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(lblTuKhoa, gbc);

        gbc.gridx = 5; gbc.weightx = 1.0;
        panel.add(txtTuKhoa, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(lblLoc1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        panel.add(cboLoc1, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lblLoc2, gbc);

        gbc.gridx = 3; gbc.weightx = 0.6;
        panel.add(cboLoc2, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(btnXemBaoCao, gbc);

        gbc.gridx = 5; gbc.weightx = 0;
        panel.add(btnLamMoi, gbc);

        gbc.gridx = 6; gbc.weightx = 0;
        panel.add(btnXuatExcel, gbc);

        gbc.gridx = 7; gbc.weightx = 0;
        panel.add(btnInPdf, gbc);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        pnlThongKe = createThongKePanel();
        panel.add(pnlThongKe, BorderLayout.NORTH);
        panel.add(createTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createThongKePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setOpaque(false);

        lblCard1Title = createCardTitle("Chỉ số 1");
        lblCard2Title = createCardTitle("Chỉ số 2");
        lblCard3Title = createCardTitle("Chỉ số 3");

        lblCard1Value = createCardValue("0");
        lblCard2Value = createCardValue("0");
        lblCard3Value = createCardValue("0");

        panel.add(createStatCard(lblCard1Title, lblCard1Value));
        panel.add(createStatCard(lblCard2Title, lblCard2Value));
        panel.add(createStatCard(lblCard3Title, lblCard3Value));

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = { "Cột 1", "Cột 2", "Cột 3" };

        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBaoBieu = new JTable(tableModel);
        tblBaoBieu.setRowHeight(28);
        tblBaoBieu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblBaoBieu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblBaoBieu.getTableHeader().setBackground(new Color(233, 239, 248));
        tblBaoBieu.getTableHeader().setForeground(TEXT_DARK);
        tblBaoBieu.setGridColor(CARD_BORDER);
        tblBaoBieu.setSelectionBackground(new Color(221, 232, 248));
        tblBaoBieu.setSelectionForeground(TEXT_DARK);

        JScrollPane scrollPane = new JScrollPane(tblBaoBieu);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(10, 10, 10, 10)));

        return scrollPane;
    }

    private JPanel createStatCard(JLabel title, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        panel.add(title, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createCardTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_DARK);
        return lbl;
    }

    private JLabel createCardValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(TEXT_DARK);
        return lbl;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(90, 34));
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(150, 36));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(150, 36));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(TEXT_DARK);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        button.setPreferredSize(new Dimension(120, 36));
        return button;
    }

    private void cauHinhMacDinhTheoLoai() {
        switch (loaiBaoBieu) {
            case HOA_DON:
//                setTitle("Báo biểu hóa đơn");
                setTableColumns(new String[] {
                        "Mã HD", "Mã ĐĐP", "Khách hàng", "Nhân viên", "Ngày lập", "Thuế", "Tổng tiền"
                });
                setCardTitles("Tổng hóa đơn", "Tổng doanh thu", "Hóa đơn cao nhất");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Từ khóa:", "Nhân viên:", "Thuế:");
                setComboBoxData(cboLoc1, new String[] { "Tất cả" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                break;

            case DON_DAT_PHONG:
//                setTitle("Báo biểu đơn đặt phòng");
                setTableColumns(new String[] {
                        "Mã ĐĐP", "Khách hàng", "Phòng", "Ngày nhận", "Ngày trả", "Trạng thái", "Tiền cọc"
                });
                setCardTitles("Tổng đơn", "Đã nhận", "Chờ check-in");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Từ khóa:", "Trạng thái:", "Nhân viên:");
                setComboBoxData(cboLoc1, new String[] { "Tất cả", "Đã đặt", "Đã nhận", "Hoàn thành" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                break;

            case PHONG:
//                setTitle("Báo biểu phòng");
                setTableColumns(new String[] {
                        "Mã phòng", "Loại phòng", "Số người", "Giá phòng", "Trạng thái"
                });
                setCardTitles("Tổng phòng", "Phòng trống", "Đang sử dụng");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Từ khóa:", "Loại phòng:", "Trạng thái:");
                setComboBoxData(cboLoc1, new String[] { "Tất cả", "Standard", "Superior", "Deluxe", "Suite", "Family" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả", "Trống", "Đang sử dụng", "Bảo trì" });
                break;

            case KHACH_HANG:
//                setTitle("Báo biểu khách hàng");
                setTableColumns(new String[] {
                        "Mã KH", "Họ tên", "SĐT", "CCCD", "Loại KH", "Điểm"
                });
                setCardTitles("Tổng khách", "Khách VIP", "Thân thiết");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Từ khóa:", "Loại KH:", "Xếp hạng:");
                setComboBoxData(cboLoc1, new String[] { "Tất cả", "Thường", "VIP", "Thân thiết" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                break;

            case NHAN_VIEN:
//                setTitle("Báo biểu nhân viên");
                setTableColumns(new String[] {
                        "Mã NV", "Họ tên", "SĐT", "Email", "Ca làm", "Vị trí", "Trạng thái"
                });
                setCardTitles("Tổng NV", "Đang làm", "Nghỉ việc");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Từ khóa:", "Ca làm:", "Trạng thái:");
                setComboBoxData(cboLoc1, new String[] { "Tất cả", "Ca sáng", "Ca chiều", "Ca tối" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả", "Đang làm", "Nghỉ việc" });
                break;

            case DICH_VU:
//                setTitle("Báo biểu dịch vụ");
                setTableColumns(new String[] {
                        "Mã DV", "Tên dịch vụ", "Đơn giá", "Số lượt dùng", "Doanh thu"
                });
                setCardTitles("Tổng dịch vụ", "Tổng lượt dùng", "Doanh thu DV");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Từ khóa:", "Nhóm DV:", "Trạng thái:");
                setComboBoxData(cboLoc1, new String[] { "Tất cả" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                break;

            case KHUYEN_MAI:
//                setTitle("Báo biểu khuyến mãi");
                setTableColumns(new String[] {
                        "Mã KM", "Tên khuyến mãi", "Giá trị", "Ngày bắt đầu", "Ngày kết thúc"
                });
                setCardTitles("Tổng KM", "Đang áp dụng", "Sắp hết hạn");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Từ khóa:", "Trạng thái:", "Mức giảm:");
                setComboBoxData(cboLoc1, new String[] { "Tất cả", "Đang áp dụng", "Ngưng áp dụng", "Hết hạn" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                break;
        }
    }

//    public void setTitle(String title) {
//        lblTitle.setText(title);
//    }

    public void setCardTitles(String c1, String c2, String c3) {
        lblCard1Title.setText(c1);
        lblCard2Title.setText(c2);
        lblCard3Title.setText(c3);
    }

    public void setCardValues(String v1, String v2, String v3) {
        lblCard1Value.setText(v1);
        lblCard2Value.setText(v2);
        lblCard3Value.setText(v3);
    }

    public void setFilterLabels(String tuNgay, String denNgay, String tuKhoa, String loc1, String loc2) {
        lblTuNgay.setText(tuNgay);
        lblDenNgay.setText(denNgay);
        lblTuKhoa.setText(tuKhoa);
        lblLoc1.setText(loc1);
        lblLoc2.setText(loc2);
    }

    public void setTableColumns(String[] columns) {
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
    }

    public void setComboBoxData(JComboBox<String> comboBox, String[] data) {
        comboBox.removeAllItems();
        for (String item : data) {
            comboBox.addItem(item);
        }
    }

    public void showDateFilters(boolean visible) {
        lblTuNgay.setVisible(visible);
        txtTuNgay.setVisible(visible);
        lblDenNgay.setVisible(visible);
        txtDenNgay.setVisible(visible);
    }

    public void showKeywordFilter(boolean visible) {
        lblTuKhoa.setVisible(visible);
        txtTuKhoa.setVisible(visible);
    }

    public void showLoc1(boolean visible) {
        lblLoc1.setVisible(visible);
        cboLoc1.setVisible(visible);
    }

    public void showLoc2(boolean visible) {
        lblLoc2.setVisible(visible);
        cboLoc2.setVisible(visible);
    }

    public JTextField getTxtTuNgay() {
        return txtTuNgay;
    }

    public JTextField getTxtDenNgay() {
        return txtDenNgay;
    }

    public JTextField getTxtTuKhoa() {
        return txtTuKhoa;
    }

    public JComboBox<String> getCboLoc1() {
        return cboLoc1;
    }

    public JComboBox<String> getCboLoc2() {
        return cboLoc2;
    }

    public JButton getBtnXemBaoCao() {
        return btnXemBaoCao;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JButton getBtnXuatExcel() {
        return btnXuatExcel;
    }

    public JButton getBtnInPdf() {
        return btnInPdf;
    }

    public JTable getTblBaoBieu() {
        return tblBaoBieu;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public LoaiBaoBieu getLoaiBaoBieu() {
        return loaiBaoBieu;
    }

    public static JPanel createPanel(LoaiBaoBieu loaiBaoBieu) {
    	BaoBieu panel = new BaoBieu(loaiBaoBieu);
        new BaoBieuController(panel);
        return panel;
    }
}