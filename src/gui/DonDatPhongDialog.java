package gui;

import dao.KhachHang_Dao;
import entity.DichVu;
import entity.KhachHang;
import entity.NhanVien;
import entity.Phong;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class DonDatPhongDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(242, 246, 252);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER = new Color(214, 223, 235);
    private static final Color TITLE = new Color(36, 64, 102);
    private static final Color PRIMARY = new Color(91, 168, 217);

    private final List<Phong> dsPhongDaChon;
    private final NhanVien nhanVien;
    private final List<DichVu> dsDichVu;

    private JTextField txtTenKH;
    private JTextField txtCCCD;
    private JTextField txtSDT;
    private JComboBox<String> cboLoaiKH;
    private JTextField txtDiemSo;

    private JTextField txtTienCoc;
    private JCheckBox chkCheckInNgay;

    private JSpinner spnNgayNhan;
    private JSpinner spnNgayTra;

    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    private KhachHang_Dao khachHangDao;
    private boolean dangTuDongDienKhachHang = false;

    private boolean succeeded = false;

    public DonDatPhongDialog(Frame owner, List<Phong> dsPhongDaChon, NhanVien nhanVien, List<DichVu> dsDichVu) {
        super(owner, "Tạo đơn đặt phòng", true);
        this.dsPhongDaChon = dsPhongDaChon;
        this.nhanVien = nhanVien;
        this.dsDichVu = dsDichVu;
        this.khachHangDao = new KhachHang_Dao();
        initUI();
    }

    private void initUI() {
        setSize(900, 650);
        setMinimumSize(new Dimension(860, 650));
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        JLabel lblHeader = new JLabel("PHIẾU ĐẶT PHÒNG");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(TITLE);
        root.add(lblHeader, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(createNhanVienPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createThongTinPhongPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createKhachHangPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createThoiGianPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createDichVuPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createThanhToanPanel());

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);

        JButton btnDatPhong = createButton("Xác nhận đặt phòng", PRIMARY, Color.BLACK);
        JButton btnHuy = createButton("Hủy", new Color(232, 236, 242), Color.BLACK);

        btnDatPhong.addActionListener(e -> submit());
        btnHuy.addActionListener(e -> dispose());

        bottom.add(btnDatPhong);
        bottom.add(btnHuy);

        root.add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createNhanVienPanel() {
        JPanel panel = createSectionPanel("Nhân viên trực ca");

        addRow(panel, 0, "Mã nhân viên:", createValueLabel(nhanVien.getMaNV()),
                "Họ tên:", createValueLabel(nhanVien.getHoTen()));
        addRow(panel, 1, "Ca làm việc:", createValueLabel(nhanVien.getCaLamViec()),
                "Vị trí:", createValueLabel(nhanVien.getViTriCongViec()));

        return panel;
    }

    private JPanel createThongTinPhongPanel() {
        JPanel panel = createSectionPanel("Danh sách phòng đã chọn");

        String[] cols = {
                "Mã phòng", "Loại phòng", "Sức chứa", "Giá phòng", "Trạng thái"
        };

        DefaultTableModel modelPhong = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        for (Phong p : dsPhongDaChon) {
            modelPhong.addRow(new Object[] {
                    p.getMaPhong(),
                    p.getLoaiPhong(),
                    p.getSoNguoiToiDa() + " người",
                    nf.format(p.getGiaPhong()) + " VNĐ",
                    p.getTrangThai()
            });
        }

        JTable tblPhong = new JTable(modelPhong);
        tblPhong.setRowHeight(28);
        tblPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblPhong.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane sp = new JScrollPane(tblPhong);
        sp.setPreferredSize(new Dimension(760, 130));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 6, 6, 6);
        panel.add(sp, gbc);

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = createSectionPanel("Thông tin khách hàng");

        txtTenKH = createTextField();
        txtCCCD = createTextField();
        txtSDT = createTextField();

        cboLoaiKH = new JComboBox<>(new String[] {
                "Thường", "Thành viên", "VIP"
        });

        cboLoaiKH.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboLoaiKH.setPreferredSize(new Dimension(220, 34));
        cboLoaiKH.setBackground(Color.WHITE);
        cboLoaiKH.setBorder(BorderFactory.createLineBorder(BORDER));

        txtDiemSo = createTextField();
        txtDiemSo.setText("0");

        addGoiYKhachHangTheoSDT();

        addRow(panel, 0, "Tên khách hàng:", txtTenKH,
                "CCCD:", txtCCCD);
        addRow(panel, 1, "Số điện thoại:", txtSDT,
                "Loại KH:", cboLoaiKH);
        addRow(panel, 2, "Điểm số:", txtDiemSo,
                "", new JLabel());

        return panel;
    }

    private void addGoiYKhachHangTheoSDT() {
        txtSDT.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                goiYKhachHangTheoSDT();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                goiYKhachHangTheoSDT();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                goiYKhachHangTheoSDT();
            }
        });
    }

    private void goiYKhachHangTheoSDT() {
        if (dangTuDongDienKhachHang) {
            return;
        }

        String sdt = txtSDT.getText().trim();

        // Tránh query liên tục khi mới nhập vài số
        if (sdt.length() < 8) {
            return;
        }

        KhachHang kh = khachHangDao.findBySdt(sdt);

        if (kh == null) {
            return;
        }

        dangTuDongDienKhachHang = true;

        txtTenKH.setText(kh.getHoTen());
        txtCCCD.setText(kh.getCccd());

        if (kh.getLoaiKH() != null && !kh.getLoaiKH().isBlank()) {
            cboLoaiKH.setSelectedItem(kh.getLoaiKH());
        }

        txtDiemSo.setText(String.valueOf(kh.getDiem()));

        dangTuDongDienKhachHang = false;
    }

    private JPanel createThoiGianPanel() {
        JPanel panel = createSectionPanel("Thời gian dự kiến check-in / check-out");

        spnNgayNhan = createDateTimeSpinner(new Date());
        spnNgayTra = createDateTimeSpinner(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000L));

        addRow(panel, 0, "Check-in dự kiến:", spnNgayNhan,
                "Check-out dự kiến:", spnNgayTra);

        // JLabel note = new JLabel("Lưu ý: DB hiện tại của bạn đang dùng kiểu DATE cho
        // ngày nhận/ngày trả.");
        // note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        // note.setForeground(new Color(160, 80, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 6, 0, 6);
        // panel.add(note, gbc);

        return panel;
    }

    private JPanel createDichVuPanel() {
        JPanel panel = createSectionPanel("Dịch vụ đặt trước");

        String[] cols = { "Chọn", "Mã DV", "Tên dịch vụ", "Đơn giá", "Số lượng" };
        modelDichVu = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                if (columnIndex == 4) {
                    return Integer.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 4;
            }
        };

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        for (DichVu dv : dsDichVu) {
            modelDichVu.addRow(new Object[] {
                    false,
                    dv.getMaDV(),
                    dv.getTenDV(),
                    nf.format(dv.getGia()) + " VNĐ",
                    1
            });
        }

        tblDichVu = new JTable(modelDichVu);
        tblDichVu.setRowHeight(28);
        tblDichVu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane sp = new JScrollPane(tblDichVu);
        sp.setPreferredSize(new Dimension(760, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 6, 6, 6);
        panel.add(sp, gbc);

        return panel;
    }

    private JPanel createThanhToanPanel() {
        JPanel panel = createSectionPanel("Xác nhận");

        txtTienCoc = createTextField();
        chkCheckInNgay = new JCheckBox("Khách check-in ngay sau khi tạo đơn");
        chkCheckInNgay.setBackground(PANEL_BG);
        chkCheckInNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addRow(panel, 0, "Tiền cọc:", txtTienCoc,
                "", chkCheckInNgay);

        return panel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(TITLE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 6, 10, 6);
        panel.add(lbl, gbc);

        return panel;
    }

    private void addRow(JPanel panel, int row, String l1, Component c1, String l2, Component c2) {
        int y = row + 1;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (l1 != null && !l1.isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = y;
            gbc.weightx = 0;
            panel.add(createLabel(l1), gbc);
        }

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 1;
        panel.add(c1, gbc);

        if (l2 != null && !l2.isEmpty()) {
            gbc.gridx = 2;
            gbc.gridy = y;
            gbc.weightx = 0;
            panel.add(createLabel(l2), gbc);
        }

        gbc.gridx = 3;
        gbc.gridy = y;
        gbc.weightx = 1;
        panel.add(c2, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(248, 250, 253));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(220, 34));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 10, 6, 10)));
        return txt;
    }

    private JSpinner createDateTimeSpinner(Date date) {
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, java.util.Calendar.MINUTE);
        JSpinner sp = new JSpinner(model);
        sp.setEditor(new JSpinner.DateEditor(sp, "dd/MM/yyyy HH:mm"));
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sp.setPreferredSize(new Dimension(220, 34));

        JFormattedTextField tf = ((JSpinner.DefaultEditor) sp.getEditor()).getTextField();
        tf.setHorizontalAlignment(SwingConstants.LEFT);
        return sp;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return btn;
    }

    private void submit() {
        if (txtTenKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng.");
            return;
        }

        if (txtCCCD.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD.");
            return;
        }

        if (txtSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại.");
            return;
        }

        String diemSoText = txtDiemSo.getText().trim();

        if (diemSoText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm số.");
            return;
        }

        try {
            int diemSo = Integer.parseInt(diemSoText);

            if (diemSo < 0) {
                JOptionPane.showMessageDialog(this, "Điểm số không được âm.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm số phải là số nguyên.");
            return;
        }

        Date ngayNhan = getNgayNhan();
        Date ngayTra = getNgayTra();

        if (isNgayNhanTrongQuaKhu(ngayNhan)) {
            JOptionPane.showMessageDialog(this,
                    "Thời gian check-in không được nhỏ hơn thời gian hiện tại.");
            return;
        }

        if (!ngayTra.after(ngayNhan)) {
            JOptionPane.showMessageDialog(this, "Thời gian check-out phải sau check-in.");
            return;
        }

        long durationMinutes = (ngayTra.getTime() - ngayNhan.getTime()) / 60000L;
        if (durationMinutes < 60) {
            JOptionPane.showMessageDialog(
                    this,
                    "Thời gian từ ngày nhận phòng đến ngày trả phòng phải ít nhất 1 giờ.");
            return;
        }

        String tienCoc = txtTienCoc.getText().trim();
        double tienCocValue = 0;
        if (!tienCoc.isEmpty()) {
            try {
                tienCocValue = Double.parseDouble(tienCoc);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tiền cọc phải là số hợp lệ.");
                return;
            }
        }

        double tongGiaTriDon = tinhTongGiaTriDon();
        double minTienCoc = tongGiaTriDon * 0.3;

        if (tienCocValue < minTienCoc) {
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            JOptionPane.showMessageDialog(
                    this,
                    "Tiền cọc phải >= 30% tổng giá trị đơn đặt phòng. Số tiền tối thiểu là "
                            + nf.format(Math.ceil(minTienCoc)) + " VNĐ.");
            return;
        }

        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            Boolean chon = (Boolean) modelDichVu.getValueAt(i, 0);
            Integer soLuong = (Integer) modelDichVu.getValueAt(i, 4);
            if (Boolean.TRUE.equals(chon) && (soLuong == null || soLuong <= 0)) {
                JOptionPane.showMessageDialog(this, "Số lượng dịch vụ phải lớn hơn 0.");
                return;
            }
        }

        succeeded = true;
        dispose();
    }

    private double tinhTongGiaTriDon() { // M.Tân - Tính tổng giá trị đơn đặt phòng dựa trên phòng và dịch vụ đã chọn
        Date ngayNhan = getNgayNhan();
        Date ngayTra = getNgayTra();

        double tongPhong = 0;
        for (Phong phong : dsPhongDaChon) {
            tongPhong += tinhTienPhongTheoThoiGian(phong.getGiaPhong(), ngayNhan, ngayTra);
        }

        double tongDichVuMotPhong = 0;
        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            Boolean chon = (Boolean) modelDichVu.getValueAt(i, 0);
            if (!Boolean.TRUE.equals(chon)) {
                continue;
            }

            Object soLuongObj = modelDichVu.getValueAt(i, 4);
            int soLuong = soLuongObj == null ? 0 : (Integer) soLuongObj;
            if (soLuong <= 0) {
                continue;
            }

            String donGiaText = String.valueOf(modelDichVu.getValueAt(i, 3));
            double donGia = parseMoney(donGiaText);
            tongDichVuMotPhong += donGia * soLuong;
        }

        return tongPhong + tongDichVuMotPhong * dsPhongDaChon.size();
    }

    private double tinhTienPhongTheoThoiGian(double giaPhong, Date ngayNhan, Date ngayTra) {
        if (ngayNhan == null || ngayTra == null || ngayTra.before(ngayNhan)) {
            return 0;
        }

        long totalMinutes = (ngayTra.getTime() - ngayNhan.getTime()) / 60000L;

        if (totalMinutes <= 0) {
            return 0;
        }

        long fullDays = totalMinutes / 1440;
        long remainderMinutes = totalMinutes % 1440;
        double base = giaPhong / 24.0;
        double tongTien = fullDays * giaPhong;

        if (remainderMinutes > 0) {
            double hours = Math.ceil(remainderMinutes / 60.0);
            double multiplier;

            if (hours <= 2) {
                multiplier = 4.0;
            } else if (hours <= 6) {
                multiplier = 3.0;
            } else if (hours <= 12) {
                multiplier = 2.2;
            } else {
                multiplier = 1.5;
            }

            double tienPhanDu = hours * base * multiplier;
            tienPhanDu = Math.min(tienPhanDu, giaPhong);
            tongTien += tienPhanDu;
        }

        return tongTien;
    }

    private double parseMoney(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        String cleaned = value.replace("VNĐ", "").replace("VND", "").replace(".", "").replace(",", "").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isNgayNhanTrongQuaKhu(Date ngayNhan) {
        if (ngayNhan == null) {
            return true;
        }

        long phutNgayNhan = ngayNhan.getTime() / 60000L;
        long phutHienTai = System.currentTimeMillis() / 60000L;
        return phutNgayNhan < phutHienTai;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getTenKhachHang() {
        return txtTenKH.getText().trim();
    }

    public String getCccd() {
        return txtCCCD.getText().trim();
    }

    public String getSdt() {
        return txtSDT.getText().trim();
    }

    public Date getNgayNhan() {
        return (Date) spnNgayNhan.getValue();
    }

    public Date getNgayTra() {
        return (Date) spnNgayTra.getValue();
    }

    public String getTienCoc() {
        return txtTienCoc.getText().trim();
    }

    public boolean isCheckInNgay() {
        return chkCheckInNgay.isSelected();
    }

    public List<DichVuDatTruoc> getDichVuDaChon() {
        List<DichVuDatTruoc> ds = new ArrayList<>();
        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            Boolean chon = (Boolean) modelDichVu.getValueAt(i, 0);
            if (Boolean.TRUE.equals(chon)) {
                String maDV = modelDichVu.getValueAt(i, 1).toString();
                String tenDV = modelDichVu.getValueAt(i, 2).toString();
                Integer soLuong = (Integer) modelDichVu.getValueAt(i, 4);
                ds.add(new DichVuDatTruoc(maDV, tenDV, soLuong));
            }
        }
        return ds;
    }

    public List<Phong> getDsPhongDaChon() {
        return dsPhongDaChon;
    }

    public static class DichVuDatTruoc {

        private final String maDV;
        private final String tenDV;
        private final int soLuong;

        public DichVuDatTruoc(String maDV, String tenDV, int soLuong) {
            this.maDV = maDV;
            this.tenDV = tenDV;
            this.soLuong = soLuong;
        }

        public String getMaDV() {
            return maDV;
        }

        public String getTenDV() {
            return tenDV;
        }

        public int getSoLuong() {
            return soLuong;
        }

    }

    public String getLoaiKH() {
        Object value = cboLoaiKH.getSelectedItem();
        return value == null ? "Thường" : value.toString();
    }

    public int getDiemSo() {
        try {
            return Integer.parseInt(txtDiemSo.getText().trim());
        } catch (NumberFormatException e) {

            return 0;
        }
    }
}
