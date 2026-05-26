package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.HoaDonController;

public class HoaDonDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color WRAPPER_BG = new Color(235, 238, 245);
    private static final Color HEADER_BG = new Color(233, 239, 248);
    private static final Color BORDER_COLOR = new Color(205, 210, 220);

    private String maHD;

    private JPanel pnlHoaDon;

    private JLabel lblMaHD;
    private JLabel lblNgayLap;

    private JLabel lblNVLapDon;
    private JLabel lblNVLapHoaDon;
    private JLabel lblTenKH;
    private JLabel lblCCCD;
    private JLabel lblSDT;
    private JLabel lblNgayNhan;
    private JLabel lblNgayTra;
    private JLabel lblSoDem;

    private JTable tblPhong;
    private DefaultTableModel modelPhong;

    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    private JLabel lblTienPhong;
    private JLabel lblTienDichVu;
    private JLabel lblPhiPhat;
    private JLabel lblTienCoc;
    private JLabel lblGiamGia;
    private JLabel lblThue;
    private JLabel lblTongTien;
    private JLabel lblTienPhaiTra;

    private JButton btnXuatHoaDon;
    private JButton btnDong;

    private HoaDonController controller;

    public HoaDonDialog(String maHD) {
        this.maHD = maHD;

        setTitle("Chi tiết hóa đơn");
        setModal(true);
        setSize(920, 620);

        initUI();

        setLocationRelativeTo(null);

        controller = new HoaDonController(this, maHD);
        controller.loadData();
    }

    private void initUI() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(WRAPPER_BG);
        wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));

        pnlHoaDon = new JPanel(new BorderLayout(0, 14));
        pnlHoaDon.setBackground(Color.WHITE);
        pnlHoaDon.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(22, 28, 22, 28)));

        pnlHoaDon.add(createHeaderPanel(), BorderLayout.NORTH);
        pnlHoaDon.add(createCenterPanel(), BorderLayout.CENTER);
        pnlHoaDon.add(createFooterPanel(), BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(pnlHoaDon);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        setContentPane(wrapper);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);

        JLabel lblHotel = new JLabel("KHÁCH SẠN IMPERIAL", JLabel.CENTER);
        lblHotel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel lblAddress = new JLabel(
                "Địa chỉ: 12 Nguyễn Huệ, Quận 1, TP.HCM",
                JLabel.CENTER);
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblHotline = new JLabel(
                "Hotline: 0909 999 999",
                JLabel.CENTER);
        lblHotline.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblTitle = new JLabel("HÓA ĐƠN THANH TOÁN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel titlePanel = new JPanel(new GridLayout(4, 1, 0, 4));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblHotel);
        titlePanel.add(lblAddress);
        titlePanel.add(lblHotline);
        titlePanel.add(lblTitle);

        lblMaHD = createBoldValueLabel();
        lblNgayLap = createBoldValueLabel();

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(createLine("Mã hóa đơn:", lblMaHD));
        infoPanel.add(createLine("Ngày thanh toán:", lblNgayLap));

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new GridLayout(2, 1, 0, 12));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(createPhongTablePanel());
        tablePanel.add(createDichVuTablePanel());

        panel.add(createInfoPanel(), BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(createTotalPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 24, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));

        // removed lblNVLapDon per UI change
        lblNVLapHoaDon = createValueLabel();
        lblTenKH = createValueLabel();
        lblCCCD = createValueLabel();
        lblSDT = createValueLabel();
        lblNgayNhan = createValueLabel();
        lblNgayTra = createValueLabel();
        lblSoDem = createValueLabel();

        panel.add(createLine("Tên khách hàng:", lblTenKH));
        panel.add(createLine("NV lập hóa đơn:", lblNVLapHoaDon));

        panel.add(createLine("CCCD:", lblCCCD));
        panel.add(createLine("SĐT:", lblSDT));

        panel.add(createLine("Ngày nhận phòng:", lblNgayNhan));
        panel.add(createLine("Ngày trả phòng:", lblNgayTra));

        panel.add(createLine("Thời gian lưu trú:", lblSoDem));
        panel.add(createLine("", createValueLabel()));

        return panel;
    }

    private JScrollPane createPhongTablePanel() {
        modelPhong = new DefaultTableModel(
                new String[] { "STT", "Tên phòng", "Thời gian lưu trú", "Đơn giá", "Thành tiền" },
                0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhong = new JTable(modelPhong);
        styleTable(tblPhong);

        JScrollPane scrollPane = new JScrollPane(tblPhong);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Thông tin phòng sử dụng"));

        return scrollPane;
    }

    private JScrollPane createDichVuTablePanel() {
        modelDichVu = new DefaultTableModel(
                new String[] { "STT", "Tên dịch vụ", "Số lượng", "Đơn giá", "Thành tiền" },
                0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDichVu = new JTable(modelDichVu);
        styleTable(tblDichVu);

        JScrollPane scrollPane = new JScrollPane(tblDichVu);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Dịch vụ thuê thêm"));

        return scrollPane;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(HEADER_BG);
        table.setGridColor(new Color(210, 220, 235));
    }

    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new BorderLayout(24, 0));
        panel.setBackground(Color.WHITE);

        JTextArea note = new JTextArea(
                "Ghi chú:\n"
                        + "- Hóa đơn được lập sau khi khách hoàn tất thanh toán.\n"
                        + "- Vui lòng kiểm tra thông tin trước khi rời quầy.\n"
                        + "- Cảm ơn quý khách đã sử dụng dịch vụ của Khách sạn Imperial.");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        note.setEditable(false);
        note.setOpaque(false);

        JPanel totalPanel = new JPanel(new GridLayout(0, 1, 0, 7));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setPreferredSize(new Dimension(360, 240));

        lblTienPhong = createValueLabel();
        lblTienDichVu = createValueLabel();
        lblPhiPhat = createValueLabel();
        lblTienCoc = createValueLabel();
        lblGiamGia = createValueLabel();
        lblThue = createValueLabel();
        lblTongTien = createBoldValueLabel();
        lblTienPhaiTra = createBoldValueLabel();

        totalPanel.add(createLine("Tiền phòng:", lblTienPhong));
        totalPanel.add(createLine("Tiền dịch vụ:", lblTienDichVu));
        totalPanel.add(createLine("Phí phạt:", lblPhiPhat));
        totalPanel.add(createLine("Tiền cọc:", lblTienCoc));
        totalPanel.add(createLine("Giảm giá:", lblGiamGia));
        totalPanel.add(createLine("Thuế:", lblThue));
        totalPanel.add(createLine("Tổng tiền:", lblTongTien));
        totalPanel.add(createLine("Tổng khách phải trả thêm:", lblTienPhaiTra));
        

        panel.add(note, BorderLayout.CENTER);
        panel.add(totalPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 80, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(22, 0, 0, 0));

        JLabel lblKhach = new JLabel(
                "<html><center>Khách hàng<br><br><br><br>________________</center></html>",
                JLabel.CENTER);

        JLabel lblNhanVien = new JLabel(
                "<html><center>Nhân viên lập hóa đơn<br><br><br><br>________________</center></html>",
                JLabel.CENTER);

        lblKhach.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(lblKhach);
        panel.add(lblNhanVien);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setOpaque(false);

        btnXuatHoaDon = new JButton("Xuất PDF");
        btnDong = new JButton("Đóng");

        btnXuatHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnXuatHoaDon.addActionListener(e -> controller.xuatHoaDon());
        btnDong.addActionListener(e -> dispose());

        panel.add(btnXuatHoaDon);
        panel.add(btnDong);

        return panel;
    }

    private JPanel createLine(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setPreferredSize(new Dimension(135, 28));

        panel.add(label, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JLabel createBoldValueLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return label;
    }

    public String getMaHD() {
        return maHD;
    }

    public JPanel getPnlHoaDon() {
        return pnlHoaDon;
    }

    public JLabel getLblMaHD() {
        return lblMaHD;
    }

    public JLabel getLblNgayLap() {
        return lblNgayLap;
    }

    public JLabel getLblNVLapHoaDon() {
        return lblNVLapHoaDon;
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

    public JLabel getLblSoDem() {
        return lblSoDem;
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

    public JLabel getLblPhiPhat() {
        return lblPhiPhat;
    }

    public JLabel getLblTienCoc() {
        return lblTienCoc;
    }

    public JLabel getLblGiamGia() {
        return lblGiamGia;
    }

    public JLabel getLblThue() {
        return lblThue;
    }

    public JLabel getLblTongTien() {
        return lblTongTien;
    }
    public JLabel getLblTienPhaiTra() {
    	return lblTienPhaiTra;
    }
    
}