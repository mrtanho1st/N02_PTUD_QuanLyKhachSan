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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import dao.CheckInCheckOut_Dao;
import entity.CheckInCheckOutItem;
import gui.CheckInCheckOut;

public class CheckInCheckOutController {

    private final CheckInCheckOut view;
    private final CheckInCheckOut_Dao dao;
    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public CheckInCheckOutController(CheckInCheckOut view) {
        this.view = view;
        this.dao = new CheckInCheckOut_Dao();
        initController();
        loadDanhSachMacDinh();
    }

    private void initController() {
        view.getBtnTaiLai().addActionListener(e -> lamMoiTatCa());
        ganSuKienTimKiemTuDong();

        view.getBtnCheckIn().addActionListener(e -> xuLyCheckIn());
        view.getBtnCapNhatThoiGian().addActionListener(e -> xuLyCapNhatThoiGian());
        view.getBtnCheckOut().addActionListener(e -> xuLyCheckOut());
        view.getBtnLamMoi().addActionListener(e -> lamMoiChiTiet());

        view.getTblDanhSach().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    hienThiChiTietDongDangChon();
                }
            }
        });
    }

    private void ganSuKienTimKiemTuDong() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiemKhongThongBao();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiemKhongThongBao();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiemKhongThongBao();
            }
        };

        view.getTxtMaDDP().getDocument().addDocumentListener(docListener);
        view.getTxtCccdSdt().getDocument().addDocumentListener(docListener);

        view.getCboTrangThai().addActionListener(e -> timKiemKhongThongBao());
    }

    private void loadDanhSachMacDinh() {
        List<CheckInCheckOutItem> ds = dao.search("", "", "Chưa hoàn thành");
        fillTable(ds);
        if (!ds.isEmpty()) {
            view.getTblDanhSach().setRowSelectionInterval(0, 0);
        } else {
            lamMoiChiTiet();
        }
    }

    private void timKiemKhongThongBao() {
        String maDDP = view.getTxtMaDDP().getText().trim();
        String cccdSdt = view.getTxtCccdSdt().getText().trim();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        if ("Tất cả".equals(trangThai)) {
            trangThai = "Chưa hoàn thành";
        }

        List<CheckInCheckOutItem> ds = dao.search(maDDP, cccdSdt, trangThai);
        fillTable(ds);

        if (!ds.isEmpty()) {
            view.getTblDanhSach().setRowSelectionInterval(0, 0);
        } else {
            lamMoiChiTiet();
        }
    }

    private void fillTable(List<CheckInCheckOutItem> ds) {
        DefaultTableModel model = view.getModelDanhSach();
        model.setRowCount(0);

        for (CheckInCheckOutItem item : ds) {
            model.addRow(new Object[] {
                    item.getMaDDP(),
                    item.getTenKH(),
                    mapTinhTrang(item.getTinhTrang()),
                    item.getCccd(),
                    item.getSdt(),
                    item.getNgayNhan(),
                    item.getNgayTra()
            });
        }
    }

    private void hienThiChiTietDongDangChon() {
        int row = view.getTblDanhSach().getSelectedRow();
        if (row == -1) {
            return;
        }

        String maDDP = safeValue(row, 0);
        String cccdSdt = view.getTxtCccdSdt().getText().trim();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        if ("Tất cả".equals(trangThai)) {
            trangThai = "Chưa hoàn thành";
        }

        List<CheckInCheckOutItem> ds = dao.search(maDDP, cccdSdt, trangThai);
        if (ds.isEmpty()) {
            return;
        }

        CheckInCheckOutItem item = ds.get(0);

        loadPhongChiTiet(maDDP);

        view.getLblTenKH().setText(item.getTenKH());
        view.getLblCCCD().setText(item.getCccd());
        view.getLblSDT().setText(item.getSdt());
        view.getLblMaDDPChiTiet().setText(item.getMaDDP());

        view.getLblCheckInDuKien().setText(item.getNgayNhan());
        view.getLblCheckOutDuKien().setText(item.getNgayTra());

        loadDichVuTheoDon(maDDP);

        double tienPhong = tinhTienPhongTheoDon(item, maDDP);
        double tienDichVu = tinhTienDichVuTheoDon(maDDP);
        double tienCoc = item.getTienCoc() == null ? 0 : item.getTienCoc();
        double conLai = tienPhong + tienDichVu - tienCoc;

        view.getLblTienPhong().setText(formatMoney(tienPhong) + " VNĐ");
        view.getLblTienDichVu().setText(formatMoney(tienDichVu) + " VNĐ");
        view.getLblTienCoc().setText(formatMoney(tienCoc) + " VNĐ");
        view.getLblConLai().setText(formatMoney(conLai) + " VNĐ");

        capNhatTrangThaiNut(item.getTinhTrang());
    }

    private double tinhTienDichVuTheoDon(String maDDP) {
        double tong = 0;

        List<Object[]> ds = dao.findDichVuByMaDDP(maDDP);

        for (Object[] row : ds) {
            tong += (Double) row[3];
        }

        return tong;
    }

    private double tinhTienPhongTheoDon(CheckInCheckOutItem item, String maDDP) {
        double tong = 0;

        List<Object[]> dsPhong = dao.findPhongByMaDDP(maDDP);
        String ngayNhanText = item.getNgayNhan();
        String ngayTraText = item.getNgayTra();

        for (Object[] row : dsPhong) {
            Object giaObj = row[2];
            double giaPhong = 0;

            if (giaObj instanceof Number) {
                giaPhong = ((Number) giaObj).doubleValue();
            } else if (giaObj != null) {
                try {
                    giaPhong = Double.parseDouble(giaObj.toString());
                } catch (NumberFormatException e) {
                    // bỏ qua dòng không parse được
                }
            }

            tong += tinhTienPhongTheoThoiGian(giaPhong, ngayNhanText, ngayTraText);
        }

        return tong;
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

        long fullDays = totalMinutes / 1440;
        long remainderMinutes = totalMinutes % 1440;

        double tongTien = fullDays * giaPhong;
        double base = giaPhong / 24.0;

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

    private LocalDateTime parseNgayGio(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }

    private void loadDichVuTheoDon(String maDDP) {
        DefaultTableModel model = view.getModelDichVu();
        model.setRowCount(0);

        List<Object[]> ds = dao.findDichVuByMaDDP(maDDP);

        for (Object[] row : ds) {
            model.addRow(new Object[] {
                    row[0],
                    row[1],
                    row[2],
                    formatMoney((Double) row[3])
            });
        }
    }

    private void loadPhongChiTiet(String maDDP) {
        DefaultTableModel model = view.getModelPhongChiTiet();
        model.setRowCount(0);

        List<Object[]> dsPhong = dao.findPhongByMaDDP(maDDP);

        for (Object[] row : dsPhong) {
            model.addRow(row);
        }
    }

    private void capNhatTrangThaiNut(String tinhTrangDB) {
        boolean choCheckIn = "Đã đặt".equals(tinhTrangDB);
        boolean dangLuuTru = "Đã nhận".equals(tinhTrangDB);
        boolean daHoanThanh = "Hoàn thành".equals(tinhTrangDB);

        view.getBtnCheckIn().setEnabled(choCheckIn);
        view.getBtnCapNhatThoiGian().setEnabled(choCheckIn || dangLuuTru);
        view.getBtnCheckOut().setEnabled(dangLuuTru);

        if (daHoanThanh) {
            view.getBtnCheckIn().setEnabled(false);
            view.getBtnCapNhatThoiGian().setEnabled(false);
            view.getBtnCheckOut().setEnabled(false);
        }
    }

    private void xuLyCheckIn() {
        String maDDP = view.getLblMaDDPChiTiet().getText().trim();

        if (maDDP.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần check-in.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xác nhận check-in cho đơn " + maDDP + "?",
                "Check-in",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.checkIn(maDDP)) {
                JOptionPane.showMessageDialog(view, "Check-in thành công.");
                loadDanhSachMacDinh();
            } else {
                JOptionPane.showMessageDialog(view, "Check-in thất bại.");
            }
        }
    }

    private void xuLyCheckOut() {
        String maDDP = view.getLblMaDDPChiTiet().getText().trim();

        if (maDDP.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần thanh toán.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Chuyển sang màn hình thanh toán cho đơn " + maDDP + "?",
                "Thanh toán",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(view);

        if (window instanceof gui.GiaoDienChinh) {
            gui.GiaoDienChinh main = (gui.GiaoDienChinh) window;
            main.moThanhToanTheoDon(maDDP);
        } else {
            JOptionPane.showMessageDialog(view, "Không tìm thấy giao diện chính để chuyển sang thanh toán.");
        }
    }

//    private void xuLyCapNhatThoiGian() {
//        JOptionPane.showMessageDialog(view, "Bước tiếp theo mình sẽ nối dialog cập nhật thời gian ở đây.");
//    }
    private void xuLyCapNhatThoiGian() {
        String maDDP = view.getLblMaDDPChiTiet().getText().trim();

        if (maDDP.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn đơn.");
            return;
        }

        String ngayMoi = JOptionPane.showInputDialog(
                view,
                "Nhập thời gian checkout mới (yyyy-MM-dd HH:mm:ss):");

        if (ngayMoi == null || ngayMoi.trim().isEmpty()) {
            return;
        }

        boolean success = dao.capNhatNgayTra(maDDP, ngayMoi);

        if (success) {
            JOptionPane.showMessageDialog(view,
                    "Cập nhật thành công.");

            timKiemKhongThongBao();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Cập nhật thất bại.");
        }
    }

    private void lamMoiTatCa() {
        view.getTxtMaDDP().setText("");
        view.getTxtCccdSdt().setText("");
        view.getCboTrangThai().setSelectedIndex(0);
        loadDanhSachMacDinh();
    }

    private void lamMoiChiTiet() {
        view.getModelPhongChiTiet().setRowCount(0);
        view.getLblTenKH().setText("");
        view.getLblCCCD().setText("");
        view.getLblSDT().setText("");
        view.getLblMaDDPChiTiet().setText("");
        view.getLblCheckInDuKien().setText("");
        view.getLblCheckOutDuKien().setText("");

        view.getLblTienPhong().setText("");
        view.getLblTienDichVu().setText("");
        view.getLblTienCoc().setText("");
        view.getLblConLai().setText("");
        view.getModelDichVu().setRowCount(0);
    }

    private String safeValue(int row, int col) {
        Object value = view.getTblDanhSach().getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private String mapTinhTrang(String tinhTrangDB) {
        if ("Đã đặt".equals(tinhTrangDB))
            return "Đã đặt";
        if ("Đã nhận".equals(tinhTrangDB))
            return "Đã nhận";
        if ("Hoàn thành".equals(tinhTrangDB))
            return "Hoàn thành";
        return tinhTrangDB;
    }

    private String formatMoney(double amount) {
        return moneyFormat.format(amount);
    }
}