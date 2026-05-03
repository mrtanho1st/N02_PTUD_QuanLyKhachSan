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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import controller.QLThongKeController;
import entity.LoaiThongKe;

public class QLThongke extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);

    private JLabel lblTuNgay;
    private JLabel lblDenNgay;
    private JLabel lblLoc1;
    private JLabel lblLoc2;
    private JLabel lblLoc3;

    private JDatePicker dateTuNgay;
    private JDatePicker dateDenNgay;

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
    private JPanel pnlChartContainer;

    private LoaiThongKe loaiThongKe;

    public QLThongke(LoaiThongKe loaiThongKe) {
        this.loaiThongKe = loaiThongKe;
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

    // top
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
        lblLoc1 = createLabel("Lọc 1:");
        lblLoc2 = createLabel("Lọc 2:");
        lblLoc3 = createLabel("Lọc 3:");

        dateTuNgay = createDatePicker();
        dateDenNgay = createDatePicker();

        cboLoc1 = new JComboBox<>(new String[] { "Tất cả" });
        cboLoc2 = new JComboBox<>(new String[] { "Tất cả" });
        cboLoc3 = new JComboBox<>(new String[] { "Tất cả" });


        styleComboBox(cboLoc1);
        styleComboBox(cboLoc2);
        styleComboBox(cboLoc3);

        btnLamMoi = createButton("Làm mới");
        btnXuatExcel = createButton("Xuất Excel");
        btnInPdf = createButton("In / PDF");

        // Hàng 2: Từ khóa | Từ ngày | Đến ngày

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(lblTuNgay, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(dateTuNgay, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblDenNgay, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        panel.add(dateDenNgay, gbc);

        // Hàng 1: Các combobox
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblLoc1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
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

    // center
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createBodyWestPanel(),
                createBodyEastPanel());
        splitPane.setDividerLocation(760);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBodyEastPanel() {
        // Container chứa biểu đồ, có viền đồng bộ với phần còn lại
        pnlChartContainer = new JPanel(new BorderLayout());
        pnlChartContainer.setBackground(Color.WHITE);
        pnlChartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(10, 10, 10, 10)));

        // Gắn biểu đồ mặc định (trống) khi mới mở màn hình
        DefaultCategoryDataset emptyDataset = new DefaultCategoryDataset();
        JFreeChart barChart = createBarChart(emptyDataset, "Biểu đồ thống kê", "Danh mục", "Giá trị");
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setOpaque(false);
        
        pnlChartContainer.add(chartPanel, BorderLayout.CENTER);

        // Panel bọc ngoài cùng để chỉnh kích thước và margin
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 16, 0, 0)); // Cách bảng bên trái 16px
        wrapper.setPreferredSize(new Dimension(500, 0));
        wrapper.add(pnlChartContainer, BorderLayout.CENTER);

        return wrapper;
    }

    // Hàm hỗ trợ cấu hình giao diện cho biểu đồ cột
    private JFreeChart createBarChart(DefaultCategoryDataset dataset, String title, String xLabel, String yLabel) {
        JFreeChart chart = ChartFactory.createBarChart(
                title, xLabel, yLabel, dataset,
                PlotOrientation.VERTICAL, // Cột dọc
                true, true, false);

        // Tùy chỉnh màu sắc, font chữ cho đẹp và hợp tông với App của bạn
        chart.setBackgroundPaint(Color.WHITE);
        // chỉnh màu cột thành màu xanh dương nhạt
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(70, 130, 180));
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        chart.getTitle().setPaint(TEXT_DARK);
        
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(PANEL_BG);
        plot.setRangeGridlinePaint(CARD_BORDER);
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));

        return chart;
    }

    // Cung cấp hàm public để Controller gọi tới mỗi khi muốn vẽ lại biểu đồ
    public void updateChart(DefaultCategoryDataset dataset, String title, String xLabel, String yLabel) {
        JFreeChart chart = createBarChart(dataset, title, xLabel, yLabel);
        ChartPanel chartPanel = new ChartPanel(chart);
        
        pnlChartContainer.removeAll();
        pnlChartContainer.add(chartPanel, BorderLayout.CENTER);
        pnlChartContainer.revalidate();
        pnlChartContainer.repaint();
    }

    private JPanel createBodyWestPanel() {
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

    // Các phương thức hỗ trợ tạo giao diện
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

    // Cấu hình mặc định theo loại báo biểu

    private void cauHinhMacDinhTheoLoai() {
    switch (loaiThongKe) {
        case DOANH_THU_THEO_THOI_GIAN:
            setTableColumns(new String[] {
                    "Thời gian", "Số lượng hóa đơn", "Doanh thu phòng", "Doanh thu dịch vụ", "Tổng doanh thu"
            });
            // Card: Thể hiện các con số tổng quan về tài chính
            setCardTitles("Tổng số hóa đơn", "Tổng doanh thu", "Ngày doanh thu cao nhất");
            setFilterLabels("Từ ngày:", "Đến ngày:", "Tháng", "", "");

            setComboBoxData(cboLoc1, new String[] { "Tất cả", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12" });
            
            showDateFilters(true);
            showLoc1(true); showLoc2(false); showLoc3(false);
            break;

        case DOANH_THU_THEO_KHACH_HANG:
            setTableColumns(new String[] {
                    "Mã khách hàng", "Tên khách hàng", "Hạng thành viên", "Số lần lưu trú", "Tổng chi tiêu"
            });
            // Card: Đánh giá khách hàng mang lại dòng tiền
            setCardTitles("Tổng số khách hàng", "Tổng lượt lưu trú", "Chi tiêu cao nhất");
            // Lọc theo Loại khách hàng thay vì Tình trạng đơn
            setFilterLabels("", "", "Loại KH:", "", "");

            setComboBoxData(cboLoc1, new String[] {
                    "Tất cả", "Thường", "VIP", "Thân thiết"
            });

            showDateFilters(false);
            showLoc1(true); showLoc2(false); showLoc3(false);
            break;

        case DOANH_THU_THEO_PHONG:
            setTableColumns(new String[] {
                    "Mã phòng", "Loại phòng", "Số lượt thuê", "Tổng số ngày thuê", "Tổng doanh thu"
            });
            // Card: Hiệu suất sinh lời của phòng
            setCardTitles("Tổng lượt thuê", "Tổng doanh thu phòng", "Phòng doanh thu cao nhất");
            // Lọc theo Loại phòng thay vì Tình trạng đơn
            setFilterLabels("", "", "Loại phòng:", "", "");

            setComboBoxData(cboLoc1, new String[] {
                    "Tất cả", "Phòng Tiêu chuẩn", "Phòng Cao cấp", "Phòng Sang trọng", "Phòng Gia đình", "Phòng Thượng hạng"
            });

            showDateFilters(false); // Báo cáo theo phòng thường không cần Từ ngày - Đến ngày
            showLoc1(true); showLoc2(false); showLoc3(false);
            break;

        case KHACH_HANG_DIEM_CAO_NHAT:
            setTableColumns(new String[] {
                    "Xếp hạng", "Mã KH", "Tên khách hàng", "Số điện thoại", "Loại khách hàng", "Tổng điểm số"
            });
            // Card: Focus vào điểm số tích lũy
            setCardTitles("Tổng số khách", "Khách điểm cao nhất", "Điểm trung bình/Khách");
            setFilterLabels("", "", "Loại KH:", "", "");

            setComboBoxData(cboLoc1, new String[] {
                    "Tất cả", "Thường", "VIP", "Thân thiết"
            });

            showDateFilters(false); // Báo cáo Top không cần Từ ngày - Đến ngày
            showLoc1(true); showLoc2(false); showLoc3(false);
            break;

        case THONG_KE_DICH_VU:
            setTableColumns(new String[] {
                    "Mã dịch vụ", "Tên dịch vụ", "Đơn giá", "Số lượng bán ra", "Tổng doanh thu"
            });
            // Card: Hiệu suất bán chéo dịch vụ
            setCardTitles("Tổng lượt sử dụng", "Tổng doanh thu DV", "Dịch vụ hot nhất");
            setFilterLabels("Từ ngày:", "Đến ngày:", "", "", "");

            showDateFilters(true);
            showLoc1(false); showLoc2(false); showLoc3(false);
            break;

        case THONG_KE_HOA_DON:
            setTableColumns(new String[] {
                    "Mã hóa đơn", "Ngày lập", "Tên khách hàng", "Nhân viên lập", "Tiền phòng", "Tiền dịch vụ", "Thuế áp dụng", "Tổng thanh toán"
            });
            // Card: Dữ liệu sổ sách
            setCardTitles("Tổng số hóa đơn", "Tiền phòng thu được", "Tiền dịch vụ thu được");
            // Có thể lọc theo các mốc giá trị hóa đơn
            setFilterLabels("Từ ngày:", "Đến ngày:", "Giá trị HĐ:", "", "");

            setComboBoxData(cboLoc1, new String[] {
                    "Tất cả", "Dưới 1 triệu", "Từ 1 - 3 triệu", "Trên 3 triệu"
            });

            showDateFilters(true);
            showLoc1(true); showLoc2(false); showLoc3(false);
            break;

        case THONG_KE_DON_DAT_PHONG:
            setTableColumns(new String[] {
                    "Thời gian", "Tổng số đơn", "Số đơn đã đặt", "Số đơn đã nhận", "Số đơn hoàn thành", "Tổng tiền cọc"
            });
            // Bạn đã làm phần này rất chuẩn rồi
            setCardTitles("Tổng đơn đặt", "Đơn hoàn thành", "Tiền cọc thu được");
            setFilterLabels("Từ ngày:", "Đến ngày:", "Tháng:", "", "");

            setComboBoxData(cboLoc1, new String[] {
                    "Tất cả", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            });

            showDateFilters(true);
            showLoc1(true); showLoc2(false); showLoc3(false);
            break;

        case THONG_KE_THEO_NHAN_VIEN:
            setTableColumns(new String[] {
                    "Xếp hạng", "Mã NV", "Họ tên", "SĐT", "Email", "Ca làm", "Vị trí", "Số hóa đơn", "Tổng doanh thu","Tháng"
            });
            // Card: Đánh giá KPI nhân viên
            setCardTitles("Tổng nhân sự", "Số hóa đơn đã lập", "Nhân viên xuất sắc nhất");
            setFilterLabels("Từ ngày:", "Đến ngày:",  "", "Tháng:", "Vị trí:");

            setComboBoxData(cboLoc2, new String[] {
                    "Tất cả", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            });
            setComboBoxData(cboLoc3, new String[] {
                    "Tất cả", "Lễ tân", "Quản lý"
            });

            showDateFilters(true); // Mở Date để xem KPI theo tháng
            showLoc1(false); showLoc2(true); showLoc3(true);
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

    public void setFilterLabels(String tuNgay, String denNgay, String loc1, String loc2, String loc3) {
        lblTuNgay.setText(tuNgay);
        lblDenNgay.setText(denNgay);
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

    // Reset date picker một cách đúng đắn (clear cả model và textfield)
    public void resetDatePicker(JDatePicker datePicker) {
        // Reset model
        datePicker.getModel().setValue(null);
        
        // Clear textfield bên trong date picker
        for (java.awt.Component comp : datePicker.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof java.awt.Container) {
                clearTextFieldsInContainer((java.awt.Container) comp);
            }
        }
    }

    private void clearTextFieldsInContainer(java.awt.Container container) {
        for (java.awt.Component comp : container.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof java.awt.Container) {
                clearTextFieldsInContainer((java.awt.Container) comp);
            }
        }
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

    public LoaiThongKe getLoaiThongKe() {
        return loaiThongKe;
    }

    public static JPanel createPanel(LoaiThongKe loaiThongKe) {
        QLThongke panel = new QLThongke(loaiThongKe);
        new QLThongKeController(panel);
        return panel;
    }

}