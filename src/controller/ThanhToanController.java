package controller;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.KhuyenMai_Dao;
import dao.ThanhToan_Dao;
import entity.KhuyenMai;
import gui.ThanhToan;
import gui.ThanhToanDialog;

import dao.HoaDon_Dao;
import gui.HoaDonDialog;
import dao.Thue_Dao;
import entity.Thue;
import entity.NhanVien;
import entity.PhienDangNhap;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ThanhToanController {

    private final ThanhToan view;
    private final ThanhToan_Dao dao;
    private final KhuyenMai_Dao khuyenMaiDao;
    private final HoaDon_Dao hoaDonDao;
    private final Thue_Dao thueDao;

    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private String maDDPDangChon = "";

    private double tienPhong = 0;
    private double tienDichVu = 0;
    private double tienCoc = 0;
    private double giamGia = 0;
    private double tienThue = 0;
    private double tienPhat = 0;
    private double tongTien = 0;
    private double canThanhToan = 0;
    private double tienThuaTienCoc = 0;

    private ThanhToanDialog dialog;

    private Object[] donDangChon;
    private List<Object[]> dsPhongDangChon;
    private List<Object[]> dsDichVuDangChon;

    private static final String BANK_ID = "970428"; // NamABank
    private static final String ACCOUNT_NO = "0787702900";
    private static final String ACCOUNT_NAME = "NGUYEN THI THANH THU";

    public ThanhToanController(ThanhToan view) {
        this.view = view;
        this.dao = new ThanhToan_Dao();
        this.khuyenMaiDao = new KhuyenMai_Dao();
        this.hoaDonDao = new HoaDon_Dao();
        this.thueDao = new Thue_Dao();

        addEvents();
    }

    private void addEvents() {
        view.getBtnLamMoi().addActionListener(e -> {
            view.clearSearch();
            maDDPDangChon = "";
            donDangChon = null;
            dsPhongDangChon = null;
            dsDichVuDangChon = null;
            loadDanhSachDonDaNhan();
        });

        view.getBtnThanhToan().addActionListener(e -> moDialogThanhToan());

        view.getTblDonChoThanhToan().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getTblDonChoThanhToan().getSelectedRow();

                if (row >= 0) {
                    Object value = view.getTblDonChoThanhToan().getValueAt(row, 0);
                    String maDDP = value == null ? "" : value.toString();

                    if (!maDDP.isBlank()) {
                        loadTheoMaDDP(maDDP);
                    }
                }
            }
        });

        DocumentListener searchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiemDonChoThanhToan();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiemDonChoThanhToan();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiemDonChoThanhToan();
            }
        };

        view.getTxtMaDDP().getDocument().addDocumentListener(searchListener);
        view.getTxtCccdSdt().getDocument().addDocumentListener(searchListener);
    }

    public void loadDanhSachDonDaNhan() {
        List<Object[]> list = dao.findDonChoThanhToan();
        fillTableDonChoThanhToan(list);

        if (!list.isEmpty()) {
            view.getTblDonChoThanhToan().setRowSelectionInterval(0, 0);
        } else {
            maDDPDangChon = "";
            donDangChon = null;
            view.clearDetail();
        }
    }

    private void timKiemDonChoThanhToan() {
        String maDDP = view.getTxtMaDDP().getText().trim();
        String cccdSdt = view.getTxtCccdSdt().getText().trim();

        List<Object[]> list = dao.searchDonChoThanhToan(maDDP, cccdSdt);
        fillTableDonChoThanhToan(list);

        if (!list.isEmpty()) {
            view.getTblDonChoThanhToan().setRowSelectionInterval(0, 0);
        } else {
            maDDPDangChon = "";
            donDangChon = null;
            view.clearDetail();
        }
    }

    private void fillTableDonChoThanhToan(List<Object[]> list) {
        DefaultTableModel model = view.getModelDonChoThanhToan();
        model.setRowCount(0);

        for (Object[] row : list) {
            double tienCocValue = toDouble(row[6]);

            model.addRow(new Object[] {
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    formatMoney(tienCocValue)
            });
        }
    }

    public void loadTheoMaDDP(String maDDP) {
        if (maDDP == null || maDDP.isBlank()) {
            return;
        }

        Object[] don = dao.findDonThanhToanByMaDDP(maDDP);

        if (don == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy đơn đang chờ thanh toán.");
            return;
        }

        maDDPDangChon = maDDP;
        donDangChon = don;

        tienCoc = toDouble(don[6]);

        loadPhong(maDDP);
        loadDichVu(maDDP);
        tinhTien();
    }

    private void loadPhong(String maDDP) {
        tienPhong = 0;
        dsPhongDangChon = dao.findPhongByMaDDP(maDDP);

        String ngayNhanText = toText(donDangChon[4]);
        String ngayTraText = toText(donDangChon[5]);

        for (Object[] row : dsPhongDangChon) {
            double donGia = toDouble(row[3]);
            double thanhTien = tinhTienPhongTheoThoiGian(donGia, ngayNhanText, ngayTraText);

            if (thanhTien <= 0 && row.length >= 5) {
                thanhTien = toDouble(row[4]);
            }

            if (thanhTien <= 0) {
                int soNgay = toInt(row[2]);
                thanhTien = soNgay * donGia;
            }

            tienPhong += thanhTien;
        }
    }

    private void loadDichVu(String maDDP) {
        tienDichVu = 0;
        dsDichVuDangChon = dao.findDichVuByMaDDP(maDDP);

        for (Object[] row : dsDichVuDangChon) {
            double thanhTien;

            if (row.length >= 5) {
                thanhTien = toDouble(row[4]);
            } else {
                thanhTien = toDouble(row[3]);
            }

            tienDichVu += thanhTien;
        }
    }

    private void tinhTien() {
        tongTien = tienPhong + tienDichVu + tienPhat;
        giamGia = 0;
        canThanhToan = tongTien - tienCoc;

        if (canThanhToan < 0) {
            canThanhToan = 0;
        }
    }

    private void moDialogThanhToan() {
        if (maDDPDangChon.isEmpty() || donDangChon == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần thanh toán.");
            return;
        }

        dialog = new ThanhToanDialog();

        loadMaGiamGiaLenDialog();
        loadThueLenDialog();

        dialog.getBtnDong().addActionListener(e -> {
            dialog.dispose();
            dialog = null;
        });

        dialog.getBtnThanhToan().addActionListener(e -> xuLyThanhToan());

        dialog.getTxtKhachDua().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                capNhatTienThua();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                capNhatTienThua();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                capNhatTienThua();
            }
        });

        dialog.getRdoTienMat().addActionListener(e -> {
            capNhatGiaoDienPhuongThuc();
            capNhatTienThua();
        });

        dialog.getRdoChuyenKhoan().addActionListener(e -> {
            capNhatGiaoDienPhuongThuc();
            capNhatTienThua();
        });

        dialog.getCboMaGiamGia().addActionListener(e -> {
            tinhTienTheoMaGiamGiaVaThue();
            hienThiTongTienLenDialog();
            capNhatTienThua();
            if ("Chuyển khoản".equalsIgnoreCase(dialog.getPhuongThucThanhToan())) {
                capNhatGiaoDienPhuongThuc();
            }
        });
        dialog.getCboThue().addActionListener(e -> {
            tinhTienTheoMaGiamGiaVaThue();
            hienThiTongTienLenDialog();
            capNhatTienThua();

            if ("Chuyển khoản".equalsIgnoreCase(dialog.getPhuongThucThanhToan())) {
                capNhatGiaoDienPhuongThuc();
            }
        });

        hienThiThongTinLenDialog();
        capNhatGiaoDienPhuongThuc();

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    private void loadThueLenDialog() {
        if (dialog == null) {
            return;
        }

        dialog.getCboThue().removeAllItems();

        dialog.getCboThue().addItem(
                new Thue("", "Không áp dụng", "Áp dụng", 0, "Không tính thuế"));

        List<Thue> list = thueDao.findThueDangApDung();

        for (Thue thue : list) {
            dialog.getCboThue().addItem(thue);
        }
    }

    private void loadMaGiamGiaLenDialog() {
        if (dialog == null) {
            return;
        }

        dialog.getCboMaGiamGia().removeAllItems();

        dialog.getCboMaGiamGia().addItem(
                new KhuyenMai("", "Không áp dụng", 0, null, null));

        List<KhuyenMai> list = khuyenMaiDao.findKhuyenMaiConHieuLuc();

        for (KhuyenMai km : list) {
            dialog.getCboMaGiamGia().addItem(km);
        }
    }

    private void hienThiThongTinLenDialog() {
        if (dialog == null || donDangChon == null) {
            return;
        }

        dialog.getLblMaDDP().setText(toText(donDangChon[0]));
        dialog.getLblTenKH().setText(toText(donDangChon[1]));
        dialog.getLblCCCD().setText(toText(donDangChon[2]));
        dialog.getLblSDT().setText(toText(donDangChon[3]));
        dialog.getLblNgayNhan().setText(toText(donDangChon[4]));
        dialog.getLblNgayTra().setText(toText(donDangChon[5]));

        fillPhongDialog();
        fillDichVuDialog();

        tinhTienTheoMaGiamGiaVaThue();
        hienThiTongTienLenDialog();
        capNhatTienThua();
    }

    private void fillPhongDialog() {
        DefaultTableModel model = dialog.getModelPhong();
        model.setRowCount(0);

        if (dsPhongDangChon == null) {
            return;
        }

        tienPhat = 0;

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime ngayTra = parseNgayGio(toText(donDangChon[5]));
        String ngayNhanText = toText(donDangChon[4]);
        String ngayTraText = toText(donDangChon[5]);

        for (Object[] row : dsPhongDangChon) {
            String maPhong = toText(row[0]);
            String loaiPhong = toText(row[1]);
            double donGia = toDouble(row[3]);
            String thoiGianLuuTru = formatThoiGianLuuTru();

            double thanhTien = tinhTienPhongTheoThoiGian(donGia, ngayNhanText, ngayTraText);

            if (thanhTien <= 0 && row.length >= 5) {
                thanhTien = toDouble(row[4]);
            }

            if (thanhTien <= 0) {
                int soNgay = toInt(row[2]);
                thanhTien = soNgay * donGia;
            }

            // Tính tiền phạt dựa trên thời gian quá hạn (so sánh ngayTra với now)
            double phiPhat = 0;

            if (ngayTra != null && now.isAfter(ngayTra)) {
                long minutesOver = java.time.Duration.between(ngayTra, now).toMinutes();

                if (minutesOver <= 30) {
                    phiPhat = 0;
                } else if (minutesOver <= 120) { // <= 2 giờ
                    phiPhat = 0.10 * thanhTien;
                } else if (minutesOver <= 240) { // <= 4 giờ
                    phiPhat = 0.30 * thanhTien;
                } else if (minutesOver <= 360) { // <= 6 giờ
                    phiPhat = 0.50 * thanhTien;
                } else { // > 6 giờ -> 100% tiền phòng (1 ngày)
                    phiPhat = donGia;
                }
            }

            tienPhat += phiPhat;

            model.addRow(new Object[] {
                    maPhong,
                    loaiPhong,
                    thoiGianLuuTru,
                    formatMoney(donGia) + " VNĐ",
                    phiPhat <= 0 ? "" : formatMoney(phiPhat) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }

        // Sau khi cập nhật các dòng phòng, cập nhật lại các phép tính
        tinhTien();
        hienThiTongTienLenDialog();
    }

    private String formatThoiGianLuuTru() {
        if (donDangChon == null || donDangChon.length < 6) {
            return "";
        }

        java.time.LocalDateTime ngayNhan = parseNgayGio(toText(donDangChon[4]));
        java.time.LocalDateTime ngayTra = parseNgayGio(toText(donDangChon[5]));

        if (ngayNhan == null || ngayTra == null || ngayTra.isBefore(ngayNhan)) {
            return "";
        }

        java.time.Duration duration = java.time.Duration.between(ngayNhan, ngayTra);
        long minutes = Math.max(0, duration.toMinutes());
        long totalHours = minutes / 60;

        if (minutes % 60 >= 30) {
            totalHours++;
        }

        long soNgay = totalHours / 24;
        long soGio = totalHours % 24;

        return soNgay + " ngày, " + soGio + " giờ";
    }

    private java.time.LocalDateTime parseNgayGio(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return java.time.LocalDateTime.parse(value.trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (java.time.format.DateTimeParseException ex) {
            return null;
        }
    }

    private double tinhTienPhongTheoThoiGian(double giaPhong, String ngayNhanText, String ngayTraText) {

        LocalDateTime ngayNhan = parseNgayGio(ngayNhanText);
        LocalDateTime ngayTra = parseNgayGio(ngayTraText);

        if (ngayNhan == null || ngayTra == null || ngayTra.isBefore(ngayNhan)) {
            return 0;
        }

        long totalMinutes = Duration.between(ngayNhan, ngayTra).toMinutes();

        if (totalMinutes <= 0) {
            return 0;
        }

        // Số ngày đầy đủ
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

    private void fillDichVuDialog() {
        DefaultTableModel model = dialog.getModelDichVu();
        model.setRowCount(0);

        if (dsDichVuDangChon == null) {
            return;
        }

        for (Object[] row : dsDichVuDangChon) {
            String maDV = toText(row[0]);
            String tenDV = toText(row[1]);
            int soLuong = toInt(row[2]);

            double donGia;
            double thanhTien;

            if (row.length >= 5) {
                donGia = toDouble(row[3]);
                thanhTien = toDouble(row[4]);
            } else {
                donGia = 0;
                thanhTien = toDouble(row[3]);
            }

            model.addRow(new Object[] {
                    maDV,
                    tenDV,
                    soLuong,
                    donGia <= 0 ? "" : formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }
    }

    private void tinhTienTheoMaGiamGiaVaThue() {
        giamGia = 0;
        tienThue = 0;
        tongTien = tienPhong + tienDichVu + tienPhat;

        Object selectedKM = null;

        if (dialog != null) {
            selectedKM = dialog.getCboMaGiamGia().getSelectedItem();
        }

        if (selectedKM instanceof KhuyenMai) {
            KhuyenMai km = (KhuyenMai) selectedKM;

            if (km.getMaKM() != null && !km.getMaKM().isBlank()) {
                giamGia = tongTien * km.getGiaTri() / 100.0;
            }
        }

        double tienSauGiam = tongTien - giamGia;

        if (tienSauGiam < 0) {
            tienSauGiam = 0;
        }

        Object selectedThue = null;

        if (dialog != null) {
            selectedThue = dialog.getCboThue().getSelectedItem();
        }

        if (selectedThue instanceof Thue) {
            Thue thue = (Thue) selectedThue;

            if (thue.getMaThue() != null && !thue.getMaThue().isBlank()) {
                tienThue = tienSauGiam * thue.getTyLeThue() / 100.0;
            }
        }

        double tongTienSauTienCoc = tongTien - giamGia + tienThue;
        tienThuaTienCoc = Math.max(0, tienCoc - tongTienSauTienCoc);
        canThanhToan = tongTienSauTienCoc - tienCoc;

        if (canThanhToan < 0) {
            canThanhToan = 0;
        }
    }

    private void hienThiTongTienLenDialog() {
        if (dialog == null) {
            return;
        }

        dialog.getLblTienPhong().setText(formatMoney(tienPhong) + " VNĐ");
        dialog.getLblTienDichVu().setText(formatMoney(tienDichVu) + " VNĐ");
        dialog.getLblTongTien().setText(formatMoney(tongTien) + " VNĐ");
        dialog.getLblPhiPhat().setText(formatMoney(tienPhat) + " VNĐ");
        dialog.getLblTienCoc().setText(formatMoney(tienCoc) + " VNĐ");
        dialog.getLblGiamGia().setText(formatMoney(giamGia) + " VNĐ");
        dialog.getLblTienThue().setText(formatMoney(tienThue) + " VNĐ");
        dialog.getLblCanThanhToan().setText(formatMoney(canThanhToan) + " VNĐ");
        dialog.hienThiTienThuaTheoTienCoc(tienThuaTienCoc);
    }

    private void capNhatGiaoDienPhuongThuc() {
        if (dialog == null) {
            return;
        }

        String phuongThuc = dialog.getPhuongThucThanhToan();

        if ("Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            dialog.hienThiTienMat();
            dialog.hienThiTienThuaTheoTienCoc(tienThuaTienCoc);
        } else {
            dialog.hienThiChuyenKhoan();

            dialog.getLblThongTinChuyenKhoan().setText(
                    "<html>"
                            + "Ngân hàng: Nam A Bank<br>"
                            + "Số TK: " + ACCOUNT_NO + "<br>"
                            + "Chủ TK: " + ACCOUNT_NAME + "<br>"
                            + "Số tiền: " + formatMoney(canThanhToan) + " VNĐ<br>"
                            + "Nội dung: Thanh toán đơn " + maDDPDangChon
                            + "</html>");

            capNhatQrChuyenKhoan();
        }
    }

    private void capNhatTienThua() {
        if (dialog == null) {
            return;
        }

        String phuongThuc = dialog.getPhuongThucThanhToan();

        if (!"Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            dialog.hienThiTienThuaTheoTienCoc(tienThuaTienCoc);
            return;
        }

        String khachDuaText = dialog.getTxtKhachDua().getText().trim();

        if (khachDuaText.isEmpty()) {
            dialog.hienThiTienThuaTheoTienCoc(tienThuaTienCoc);
            return;
        }

        try {
            double khachDua = Double.parseDouble(khachDuaText);
            double tienThua = tienThuaTienCoc + (khachDua - canThanhToan);

            if (tienThua < 0) {
                dialog.getLblTienThua().setText("Thiếu " + formatMoney(Math.abs(tienThua)) + " VNĐ");
            } else {
                dialog.getLblTienThua().setText("Thừa " + formatMoney(tienThua) + " VNĐ");
            }

        } catch (NumberFormatException e) {
            dialog.getLblTienThua().setText("Không hợp lệ");
        }
    }

    private void xuLyThanhToan() {
        if (maDDPDangChon.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần thanh toán.");
            return;
        }

        if (dialog == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng mở thông tin thanh toán.");
            return;
        }

        tinhTienTheoMaGiamGiaVaThue();
        hienThiTongTienLenDialog();

        String phuongThuc = dialog.getPhuongThucThanhToan();

        if ("Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            String khachDuaText = dialog.getTxtKhachDua().getText().trim();

            if (khachDuaText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền khách đưa.");
                return;
            }

            double khachDua;

            try {
                khachDua = Double.parseDouble(khachDuaText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Số tiền khách đưa không hợp lệ.");
                return;
            }

            if (khachDua < canThanhToan) {
                JOptionPane.showMessageDialog(view, "Khách đưa chưa đủ tiền thanh toán.");
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xác nhận thanh toán đơn " + maDDPDangChon + "?",
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String maHD = taoHoaDon(maDDPDangChon);

        if (maHD == null || maHD.isBlank()) {
            JOptionPane.showMessageDialog(view, "Tạo hóa đơn thất bại. Chưa hoàn tất thanh toán.");
            return;
        }

        boolean ok = dao.hoanTatThanhToan(maDDPDangChon);

        if (ok) {

            if (dialog != null) {
                dialog.dispose();
                dialog = null;
            }

            HoaDonDialog hoaDonDialog = new HoaDonDialog(maHD);
            hoaDonDialog.setLocationRelativeTo(view);
            hoaDonDialog.setVisible(true);

            view.clearDetail();
            loadDanhSachDonDaNhan();

        } else {
            JOptionPane.showMessageDialog(view, "Tạo hóa đơn thành công nhưng cập nhật trạng thái đơn thất bại.");
        }
    }

    private String toText(Object value) {
        return value == null ? "" : value.toString();
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            String text = value.toString()
                    .replace("VNĐ", "")
                    .replace(",", "")
                    .trim();

            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatMoney(double amount) {
        return moneyFormat.format(amount);
    }

    private String taoHoaDon(String maDDP) {
        tinhTienTheoMaGiamGiaVaThue();

        String maKM = null;

        Object selected = dialog.getCboMaGiamGia().getSelectedItem();

        if (selected instanceof KhuyenMai) {
            KhuyenMai km = (KhuyenMai) selected;

            if (km.getMaKM() != null && !km.getMaKM().isBlank()) {
                maKM = km.getMaKM();
            }
        }

        String maThue = null;

        Object selectedThue = dialog.getCboThue().getSelectedItem();

        if (selectedThue instanceof Thue) {
            Thue thue = (Thue) selectedThue;

            if (thue.getMaThue() != null && !thue.getMaThue().isBlank()) {
                maThue = thue.getMaThue();
            }
        }

        NhanVien nvDangNhap = PhienDangNhap.getNhanVienDangNhap();

        if (nvDangNhap == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy nhân viên đang đăng nhập.");
            return null;
        }

        String maNVLapHoaDon = nvDangNhap.getMaNV();

        return hoaDonDao.taoHoaDon(
                maDDP,
                maKM,
                maThue,
                maNVLapHoaDon,
                tongTien,
                dsPhongDangChon,
                dsDichVuDangChon);
    }

    private String taoLinkVietQR(double soTien, String noiDung) {
        String amount = String.valueOf((long) soTien);

        String cleanNoiDung = noiDung
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleanNoiDung.length() > 50) {
            cleanNoiDung = cleanNoiDung.substring(0, 50);
        }

        return "https://img.vietqr.io/image/"
                + BANK_ID + "-"
                + ACCOUNT_NO + "-compact2.png"
                + "?amount=" + amount
                + "&addInfo=" + cleanNoiDung.replace(" ", "%20")
                + "&accountName=" + ACCOUNT_NAME.replace(" ", "%20");
    }

    private void capNhatQrChuyenKhoan() {
        if (dialog == null) {
            return;
        }

        try {
            String noiDung = "Thanh toan " + maDDPDangChon;
            String qrUrl = taoLinkVietQR(canThanhToan, noiDung);

            System.out.println("QR URL: " + qrUrl);

            dialog.getLblQrChuyenKhoan().setText("Đang tải QR...");
            dialog.getLblQrChuyenKhoan().setIcon(null);

            BufferedImage bufferedImage = ImageIO.read(new URL(qrUrl));

            if (bufferedImage == null) {
                dialog.getLblQrChuyenKhoan().setText("<html>Không tải<br>được QR</html>");
                return;
            }

            Image img = bufferedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);

            dialog.getLblQrChuyenKhoan().setText("");
            dialog.getLblQrChuyenKhoan().setIcon(new ImageIcon(img));

            dialog.getLblQrChuyenKhoan().revalidate();
            dialog.getLblQrChuyenKhoan().repaint();

        } catch (Exception e) {
            e.printStackTrace();
            dialog.getLblQrChuyenKhoan().setIcon(null);
            dialog.getLblQrChuyenKhoan().setText("<html>Không tải<br>được QR</html>");
        }
    }
}
