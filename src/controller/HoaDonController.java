package controller;

import java.awt.Desktop;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import dao.HoaDon_Dao;
import gui.HoaDonDialog;
import gui.HoaDonPdfExporter;

public class HoaDonController {

    private final HoaDonDialog view;
    private final HoaDon_Dao hoaDonDao;

    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private final String maHD;

    private Object[] thongTinHD;
    private List<Object[]> dsPhong;
    private List<Object[]> dsDichVu;

    private double tienPhong = 0;
    private double tienDichVu = 0;
    private double tienCoc = 0;
    private double giamGia = 0;
    private double thue = 0;
    private double tongTien = 0;
    private double tienPhat = 0;

    public HoaDonController(HoaDonDialog view, String maHD) {
        this.view = view;
        this.maHD = maHD;
        this.hoaDonDao = new HoaDon_Dao();
    }

    public void loadData() {
        thongTinHD = hoaDonDao.findThongTinInHoaDon(maHD);
        dsPhong = hoaDonDao.findDanhSachPhongInHoaDon(maHD);
        dsDichVu = hoaDonDao.findDanhSachDichVuInHoaDon(maHD);

        if (thongTinHD == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy hóa đơn.");
            view.dispose();
            return;
        }

        hienThiThongTinChung();
        hienThiDanhSachPhong();
        hienThiDanhSachDichVu();
        hienThiTongTien();
    }

    private void hienThiThongTinChung() {
        view.getLblMaHD().setText(toText(thongTinHD[0]));

        if (thongTinHD[1] == null) {
            view.getLblNgayLap().setText("");
        } else {
            view.getLblNgayLap().setText(dateFormat.format(thongTinHD[1]));
        }

        view.getLblTenKH().setText(toText(thongTinHD[2]));
        view.getLblCCCD().setText(toText(thongTinHD[3]));
        view.getLblSDT().setText(toText(thongTinHD[4]));

        String maNVLapDon = toText(thongTinHD[5]);
        String tenNVLapDon = toText(thongTinHD[6]);
        String maNVLapHoaDon = toText(thongTinHD[7]);
        String tenNVLapHoaDon = toText(thongTinHD[8]);

        // NV lập đơn removed from UI; only set NV lập hóa đơn
        view.getLblNVLapHoaDon().setText(formatNhanVien(maNVLapHoaDon, tenNVLapHoaDon));

        Date ngayNhan = toDate(thongTinHD[9]);
        Date ngayTra = toDate(thongTinHD[10]);

        if (ngayNhan == null) {
            view.getLblNgayNhan().setText("");
        } else {
            view.getLblNgayNhan().setText(dateFormat.format(ngayNhan));
        }

        if (ngayTra == null) {
            view.getLblNgayTra().setText("");
        } else {
            view.getLblNgayTra().setText(dateFormat.format(ngayTra));
        }

        String thoiGianLuuTru = formatThoiGianLuuTru(ngayNhan, ngayTra);

        view.getLblSoDem().setText(thoiGianLuuTru);

        tienCoc = toDouble(thongTinHD[12]);
        tongTien = toDouble(thongTinHD[13]);
    }

    private String formatNhanVien(String maNV, String tenNV) {
        if (maNV.isBlank() && tenNV.isBlank()) {
            return "";
        }

        if (maNV.isBlank()) {
            return tenNV;
        }

        if (tenNV.isBlank()) {
            return maNV;
        }

        return maNV + " - " + tenNV;
    }

    private void hienThiDanhSachPhong() {
        DefaultTableModel model = view.getModelPhong();
        model.setRowCount(0);

        tienPhong = 0;

        if (dsPhong == null) {
            return;
        }

        String thoiGianLuuTru = formatThoiGianLuuTru(toDate(thongTinHD[9]), toDate(thongTinHD[10]));

        for (int i = 0; i < dsPhong.size(); i++) {
            Object[] p = dsPhong.get(i);

            String maPhong = toText(p[0]);
            String loaiPhong = toText(p[1]);
            double donGia = toDouble(p[3]);
            double thanhTien = toDouble(p[4]);

            tienPhong += thanhTien;

            model.addRow(new Object[] {
                    i + 1,
                    maPhong + " - " + loaiPhong,
                    thoiGianLuuTru,
                    formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }
    }

    private void hienThiDanhSachDichVu() {
        DefaultTableModel model = view.getModelDichVu();
        model.setRowCount(0);

        tienDichVu = 0;

        if (dsDichVu == null) {
            return;
        }

        for (int i = 0; i < dsDichVu.size(); i++) {
            Object[] dv = dsDichVu.get(i);

            String tenDV = toText(dv[0]);
            int soLuong = toInt(dv[1]);
            double donGia = toDouble(dv[2]);
            double thanhTien = toDouble(dv[3]);

            tienDichVu += thanhTien;

            model.addRow(new Object[] {
                    i + 1,
                    tenDV,
                    soLuong,
                    formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }
    }

    private void hienThiTongTien() {
        giamGia = 0;

        double tyLeGiamGia = 0;

        if (thongTinHD != null && thongTinHD.length > 18) {
            tyLeGiamGia = toDouble(thongTinHD[18]);
        }

        double tyLeThue = 0;

        if (thongTinHD != null && thongTinHD.length > 15) {
            tyLeThue = toDouble(thongTinHD[15]);
        }

        double tongTruocGiam = tienPhong + tienDichVu;

        giamGia = tongTruocGiam * tyLeGiamGia / 100.0;

        double tienSauGiam = tongTruocGiam - giamGia;

        if (tienSauGiam < 0) {
            tienSauGiam = 0;
        }

        thue = tienSauGiam * tyLeThue / 100.0;
        // Tính tiền phạt dựa trên thời gian trả phòng và thời điểm lập hóa đơn
        tienPhat = 0;
        Date ngayLapHD = toDate(thongTinHD[1]);
        Date ngayTra = toDate(thongTinHD[10]);

        if (ngayTra != null && ngayLapHD != null && ngayLapHD.after(ngayTra) && dsPhong != null) {
            long minutesOver = (ngayLapHD.getTime() - ngayTra.getTime()) / 60000L;

            if (minutesOver < 0) {
                minutesOver = 0;
            }

            for (Object[] p : dsPhong) {
                double donGia = toDouble(p[3]);
                double thanhTien = toDouble(p[4]);

                double phiPhat = 0;

                if (minutesOver <= 30) {
                    phiPhat = 0;
                } else if (minutesOver <= 120) {
                    phiPhat = 0.10 * thanhTien;
                } else if (minutesOver <= 240) {
                    phiPhat = 0.30 * thanhTien;
                } else if (minutesOver <= 360) {
                    phiPhat = 0.50 * thanhTien;
                } else {
                    phiPhat = donGia; // 100% = 1 ngày
                }

                tienPhat += phiPhat;
            }
        }
        view.getLblTienPhong().setText(formatMoney(tienPhong) + " VNĐ");
        view.getLblTienDichVu().setText(formatMoney(tienDichVu) + " VNĐ");
        view.getLblPhiPhat().setText(formatMoney(tienPhat) + " VNĐ");
        view.getLblTienCoc().setText(formatMoney(tienCoc) + " VNĐ");
        view.getLblGiamGia().setText(formatMoney(giamGia) + " VNĐ");

        if (tyLeThue > 0) {
            view.getLblThue().setText(formatMoney(thue) + " VNĐ (" + formatMoney(tyLeThue) + "%)");
        } else {
            view.getLblThue().setText("0 VNĐ");
        }
        if (tyLeGiamGia > 0) {
            view.getLblGiamGia().setText(formatMoney(giamGia) + " VNĐ (" + formatMoney(tyLeGiamGia) + "%)");
        } else {
            view.getLblGiamGia().setText("0 VNĐ");
        }

        view.getLblTongTien().setText(formatMoney(tongTien) + " VNĐ");
    }

    public void xuatHoaDon() {
        try {
            java.io.File folder = new java.io.File("bill");

            if (!folder.exists()) {
                folder.mkdirs();
            }

            java.io.File file = new java.io.File(folder, "HoaDon_" + maHD + ".pdf");

            thongTinHD = hoaDonDao.findThongTinInHoaDon(maHD);
            dsPhong = hoaDonDao.findDanhSachPhongInHoaDon(maHD);
            dsDichVu = hoaDonDao.findDanhSachDichVuInHoaDon(maHD);

            if (thongTinHD == null) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy dữ liệu hóa đơn để xuất PDF.");
                return;
            }

            HoaDonPdfExporter.exportHoaDon(
                    file.getAbsolutePath(),
                    thongTinHD,
                    dsPhong,
                    dsDichVu);

            JOptionPane.showMessageDialog(
                    view,
                    "Xuất hóa đơn PDF thành công.\nFile được lưu tại: " + file.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Xuất hóa đơn PDF thất bại.");
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
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatMoney(double amount) {
        return moneyFormat.format(amount);
    }

    private Date toDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }

        return null;
    }

    private String formatThoiGianLuuTru(Date ngayNhan, Date ngayTra) {
        if (ngayNhan == null || ngayTra == null) {
            return "";
        }

        long minutes = Duration.between(ngayNhan.toInstant(), ngayTra.toInstant()).toMinutes();
        if (minutes < 0) {
            minutes = 0;
        }

        long totalHours = minutes / 60;
        long soNgay = totalHours / 24;
        long soGio = totalHours % 24;

        return soNgay + " ngày, " + soGio + " giờ";
    }
}