package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Properties;

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

    private JLabel lblTuNgay;
    private JLabel lblDenNgay;
    private JLabel lblTuKhoa;
    private JLabel lblLoc1;
    private JLabel lblLoc2;
    private JLabel lblLoc3;

    private JDatePicker dateTuNgay;
    private JDatePicker dateDenNgay;
    private JTextField txtTuKhoa;

    private JComboBox<String> cboLoc1;
    private JComboBox<String> cboLoc2;
    private JComboBox<String> cboLoc3;

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
        lblTuKhoa = createLabel("Tìm kiếm:");
        lblLoc1 = createLabel("Lọc 1:");
        lblLoc2 = createLabel("Lọc 2:");
        lblLoc3 = createLabel("Lọc 3:");

        dateTuNgay = createDatePicker();
        dateDenNgay = createDatePicker();
        txtTuKhoa = new JTextField();

        cboLoc1 = new JComboBox<>(new String[] { "Tất cả" });
        cboLoc2 = new JComboBox<>(new String[] { "Tất cả" });
        cboLoc3 = new JComboBox<>(new String[] { "Tất cả" });

        styleTextField(txtTuKhoa);
        styleComboBox(cboLoc1);
        styleComboBox(cboLoc2);
        styleComboBox(cboLoc3);

        btnLamMoi = createButton("Làm mới");
        btnXuatExcel = createButton("Xuất Excel");
        btnInPdf = createButton("In / PDF");

        // Hàng 1: Tìm kiếm | Từ ngày | Đến ngày
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblTuKhoa, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtTuKhoa, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblTuNgay, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        panel.add(dateTuNgay, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblDenNgay, gbc);

        gbc.gridx = 5;
        gbc.weightx = 1;
        panel.add(dateDenNgay, gbc);

        // Hàng 2: Các combobox
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(lblLoc1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(cboLoc1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblLoc2, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.6;
        panel.add(cboLoc2, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblLoc3, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.6;
        panel.add(cboLoc3, gbc);

        // Hàng 3: Các nút, bỏ Xem báo cáo
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXuatExcel);
        pnlButtons.add(btnInPdf);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pnlButtons, gbc);

        return panel;
    }

    private JDatePicker createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        JDatePicker datePicker = new JDatePicker(model);

        datePicker.setPreferredSize(new Dimension(180, 36));
        datePicker.setMinimumSize(new Dimension(180, 36));
        datePicker.setBackground(Color.WHITE);
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        styleDatePickerChildren(datePicker);

        return datePicker;
    }

    private void styleDatePickerChildren(java.awt.Container container) {
        for (java.awt.Component comp : container.getComponents()) {
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (comp instanceof JTextField) {
                JTextField textField = (JTextField) comp;
                textField.setBackground(Color.WHITE);
                textField.setForeground(TEXT_DARK);
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(CARD_BORDER),
                        new EmptyBorder(6, 8, 6, 8)));
                textField.setPreferredSize(new Dimension(140, 34));
                textField.setMinimumSize(new Dimension(140, 34));
            }

            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setText("📅");
                button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                button.setFocusPainted(false);
                button.setBackground(BUTTON_BG);
                button.setForeground(TEXT_DARK);
                button.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, CARD_BORDER));
                button.setPreferredSize(new Dimension(30, 34));
                button.setMinimumSize(new Dimension(30, 34));
                button.setMargin(new Insets(0, 0, 0, 0));
            }

            if (comp instanceof java.awt.Container) {
                styleDatePickerChildren((java.awt.Container) comp);
            }
        }
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
                setTableColumns(new String[] {
                        "Mã HD", "Mã ĐĐP", "Khách hàng", "Nhân viên", "Ngày lập", "Thuế", "Tổng tiền"
                });
                setCardTitles("Tổng hóa đơn", "Tổng doanh thu", "Hóa đơn cao nhất");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Tìm kiếm:", "", "", "");

                setComboBoxData(cboLoc1, new String[] { "Tất cả" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                setComboBoxData(cboLoc3, new String[] { "Tất cả" });

                showDateFilters(true);
                showLoc1(false);
                showLoc2(false);
                showLoc3(false);
                break;

            case DON_DAT_PHONG:
                setTableColumns(new String[] {
                        "Mã ĐĐP", "Khách hàng", "Phòng", "Ngày nhận", "Ngày trả", "Trạng thái", "Tiền cọc"
                });
                setCardTitles("Tổng đơn", "Đã nhận", "Đã đặt");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Tìm kiếm:", "Tình trạng:", "", "");

                setComboBoxData(cboLoc1, new String[] {
                        "Tất cả", "Hoàn thành", "Đã nhận", "Đã đặt"
                });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                setComboBoxData(cboLoc3, new String[] { "Tất cả" });

                showDateFilters(true);
                showLoc1(true);
                showLoc2(false);
                showLoc3(false);
                break;

            case PHONG:
                setTableColumns(new String[] {
                        "Mã phòng", "Loại phòng", "Số người", "Giá phòng", "Trạng thái"
                });
                setCardTitles("Tổng phòng", "Phòng trống", "Đang sử dụng");
                setFilterLabels("", "", "Tìm kiếm:", "Loại phòng:", "Trạng thái:", "");

                setComboBoxData(cboLoc1, new String[] {
                        "Tất cả", "Phòng Tiêu chuẩn", "Phòng Cao cấp", "Phòng Sang trọng", "Phòng Gia đình",
                        "Phòng Thượng hạng"
                });
                setComboBoxData(cboLoc2, new String[] {
                        "Tất cả", "Trống", "Đang sử dụng", "Bảo trì"
                });
                setComboBoxData(cboLoc3, new String[] { "Tất cả" });

                showDateFilters(false);
                showLoc1(true);
                showLoc2(true);
                showLoc3(false);
                break;

            case KHACH_HANG:
                setTableColumns(new String[] {
                        "Mã KH", "Họ tên", "SĐT", "CCCD", "Loại KH", "Điểm"
                });
                setCardTitles("Tổng khách", "Khách VIP", "Thân thiết");
                setFilterLabels("", "", "Tìm kiếm:", "Loại KH:", "", "");

                setComboBoxData(cboLoc1, new String[] {
                        "Tất cả", "Thường", "VIP", "Thân thiết"
                });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                setComboBoxData(cboLoc3, new String[] { "Tất cả" });

                showDateFilters(false);
                showLoc1(true);
                showLoc2(false);
                showLoc3(false);
                break;

            case NHAN_VIEN:
                setTableColumns(new String[] {
                        "Mã NV", "Họ tên", "SĐT", "Email", "Ca làm", "Vị trí", "Trạng thái"
                });
                setCardTitles("Tổng NV", "Đang làm", "Nghỉ việc");
                setFilterLabels("", "", "Tìm kiếm:", "Ca làm:", "Trạng thái:", "Vị trí:");

                setComboBoxData(cboLoc1, new String[] {
                        "Tất cả", "Ca sáng", "Ca chiều", "Ca tối"
                });
                setComboBoxData(cboLoc2, new String[] {
                        "Tất cả", "Đang làm", "Nghỉ việc"
                });
                setComboBoxData(cboLoc3, new String[] {
                        "Tất cả", "Lễ tân", "Lễ Tân", "Quản lý"
                });

                showDateFilters(false);
                showLoc1(true);
                showLoc2(true);
                showLoc3(true);
                break;

            case DICH_VU:
                setTableColumns(new String[] {
                        "Mã DV", "Tên dịch vụ", "Đơn giá", "Số lượt dùng", "Doanh thu"
                });
                setCardTitles("Tổng dịch vụ", "Tổng lượt dùng", "Doanh thu DV");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Tìm kiếm:", "", "", "");

                setComboBoxData(cboLoc1, new String[] { "Tất cả" });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                setComboBoxData(cboLoc3, new String[] { "Tất cả" });

                showDateFilters(true);
                showLoc1(false);
                showLoc2(false);
                showLoc3(false);
                break;

            case KHUYEN_MAI:
                setTableColumns(new String[] {
                        "Mã KM", "Tên khuyến mãi", "Giá trị", "Ngày bắt đầu", "Ngày kết thúc"
                });
                setCardTitles("Tổng KM", "Đang áp dụng", "Sắp hết hạn");
                setFilterLabels("Từ ngày:", "Đến ngày:", "Tìm kiếm:", "Khoảng giá trị:", "", "");

                setComboBoxData(cboLoc1, new String[] {
                        "Tất cả", "Dưới 10%", "Từ 10% - 30%", "Trên 30%"
                });
                setComboBoxData(cboLoc2, new String[] { "Tất cả" });
                setComboBoxData(cboLoc3, new String[] { "Tất cả" });

                showDateFilters(true);
                showLoc1(true);
                showLoc2(false);
                showLoc3(false);
                break;
        }
    }

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

    public void setFilterLabels(String tuNgay, String denNgay, String tuKhoa, String loc1, String loc2, String loc3) {
        lblTuNgay.setText(tuNgay);
        lblDenNgay.setText(denNgay);
        lblTuKhoa.setText(tuKhoa);
        lblLoc1.setText(loc1);
        lblLoc2.setText(loc2);
        lblLoc3.setText(loc3);
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
        dateTuNgay.setVisible(visible);
        lblDenNgay.setVisible(visible);
        dateDenNgay.setVisible(visible);
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

    public void showLoc3(boolean visible) {
        lblLoc3.setVisible(visible);
        cboLoc3.setVisible(visible);
    }

    public JComboBox<String> getCboLoc3() {
        return cboLoc3;
    }

    public JDatePicker getDateTuNgay() {
        return dateTuNgay;
    }

    public JDatePicker getDateDenNgay() {
        return dateDenNgay;
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