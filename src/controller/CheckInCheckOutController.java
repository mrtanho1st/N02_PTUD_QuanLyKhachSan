package controller;

import java.text.NumberFormat;
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
        view.getBtnThemDichVu().addActionListener(e -> xuLyThemDichVu());
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

        view.getTxtMaPhong().getDocument().addDocumentListener(docListener);
        view.getTxtMaDDP().getDocument().addDocumentListener(docListener);
        view.getTxtCccdSdt().getDocument().addDocumentListener(docListener);

        view.getCboTrangThai().addActionListener(e -> timKiemKhongThongBao());
    }

    private void loadDanhSachMacDinh() {
        List<CheckInCheckOutItem> ds = dao.search("", "", "", "Chưa hoàn thành");
        fillTable(ds);
        if (!ds.isEmpty()) {
            view.getTblDanhSach().setRowSelectionInterval(0, 0);
        } else {
            lamMoiChiTiet();
        }
    }

    private void timKiemKhongThongBao() {
        String maPhong = view.getTxtMaPhong().getText().trim();
        String maDDP = view.getTxtMaDDP().getText().trim();
        String cccdSdt = view.getTxtCccdSdt().getText().trim();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        if ("Tất cả".equals(trangThai)) {
            trangThai = "Chưa hoàn thành";
        }

        List<CheckInCheckOutItem> ds = dao.search(maPhong, maDDP, cccdSdt, trangThai);
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
                    item.getMaPhong(),
                    item.getTenKH(),
                    mapTinhTrang(item.getTinhTrang()),
                    item.getNgayNhan(),
                    item.getNgayTra()
            });
        }
    }

    private void hienThiChiTietDongDangChon() {
        int row = view.getTblDanhSach().getSelectedRow();
        if (row == -1) return;

        String maPhong = safeValue(row, 0);
        String maDDP = view.getTxtMaDDP().getText().trim();
        String cccdSdt = view.getTxtCccdSdt().getText().trim();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        if ("Tất cả".equals(trangThai)) {
            trangThai = "Chưa hoàn thành";
        }

        List<CheckInCheckOutItem> ds = dao.search(maPhong, maDDP, cccdSdt, trangThai);
        if (ds.isEmpty()) return;

        CheckInCheckOutItem item = ds.get(0);

        view.getLblMaPhong().setText(item.getMaPhong());
        view.getLblLoaiPhong().setText(item.getLoaiPhong());
        view.getLblGiaPhong().setText(formatMoney(item.getGiaPhong()) + " VNĐ");
        view.getLblTrangThaiPhong().setText(item.getTrangThaiPhong());

        view.getLblTenKH().setText(item.getTenKH());
        view.getLblCCCD().setText(item.getCccd());
        view.getLblSDT().setText(item.getSdt());
        view.getLblMaDDPChiTiet().setText(item.getMaDDP());

        view.getLblCheckInDuKien().setText(item.getNgayNhan());
        view.getLblCheckOutDuKien().setText(item.getNgayTra());
        view.getLblCheckInThucTe().setText("Đã nhận".equals(item.getTinhTrang()) ? item.getNgayNhan() : "");
        view.getLblCheckOutThucTe().setText("");

        loadDichVu(item.getMaPhong());

        double tienPhong = item.getGiaPhong();
        double tienDichVu = tinhTienDichVu(item.getMaPhong());
        double tienCoc = item.getTienCoc() == null ? 0 : item.getTienCoc();
        double conLai = tienPhong + tienDichVu - tienCoc;

        view.getLblTienPhong().setText(formatMoney(tienPhong) + " VNĐ");
        view.getLblTienDichVu().setText(formatMoney(tienDichVu) + " VNĐ");
        view.getLblTienCoc().setText(formatMoney(tienCoc) + " VNĐ");
        view.getLblConLai().setText(formatMoney(conLai) + " VNĐ");

        capNhatTrangThaiNut(item.getTinhTrang());
    }

    private void loadDichVu(String maPhong) {
        DefaultTableModel model = view.getModelDichVu();
        model.setRowCount(0);

        List<Object[]> ds = dao.findDichVuByMaPhong(maPhong);
        for (Object[] row : ds) {
            model.addRow(new Object[] {
                    row[0],
                    row[1],
                    row[2],
                    formatMoney((Double) row[3])
            });
        }
    }

    private double tinhTienDichVu(String maPhong) {
        double tong = 0;
        List<Object[]> ds = dao.findDichVuByMaPhong(maPhong);
        for (Object[] row : ds) {
            tong += (Double) row[3];
        }
        return tong;
    }

    private void capNhatTrangThaiNut(String tinhTrangDB) {
        boolean choCheckIn = "Đã đặt".equals(tinhTrangDB);
        boolean dangLuuTru = "Đã nhận".equals(tinhTrangDB);
        boolean daHoanThanh = "Hoàn thành".equals(tinhTrangDB);

        view.getBtnCheckIn().setEnabled(choCheckIn);
        view.getBtnThemDichVu().setEnabled(dangLuuTru);
        view.getBtnCapNhatThoiGian().setEnabled(choCheckIn || dangLuuTru);
        view.getBtnCheckOut().setEnabled(dangLuuTru);

        if (daHoanThanh) {
            view.getBtnCheckIn().setEnabled(false);
            view.getBtnThemDichVu().setEnabled(false);
            view.getBtnCapNhatThoiGian().setEnabled(false);
            view.getBtnCheckOut().setEnabled(false);
        }
    }

    private void xuLyCheckIn() {
        String maPhong = view.getLblMaPhong().getText().trim();
        String maDDP = view.getLblMaDDPChiTiet().getText().trim();

        if (maPhong.isEmpty() || maDDP.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng cần check-in.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xác nhận check-in cho phòng " + maPhong + "?",
                "Check-in",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.checkIn(maDDP, maPhong)) {
                JOptionPane.showMessageDialog(view, "Check-in thành công.");
                loadDanhSachMacDinh();
            } else {
                JOptionPane.showMessageDialog(view, "Check-in thất bại.");
            }
        }
    }

    private void xuLyCheckOut() {
        String maPhong = view.getLblMaPhong().getText().trim();
        String maDDP = view.getLblMaDDPChiTiet().getText().trim();

        if (maPhong.isEmpty() || maDDP.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng cần check-out.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xác nhận check-out cho phòng " + maPhong + "?",
                "Check-out",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.checkOut(maDDP, maPhong)) {
                JOptionPane.showMessageDialog(view, "Check-out thành công.");
                loadDanhSachMacDinh();
            } else {
                JOptionPane.showMessageDialog(view, "Check-out thất bại.");
            }
        }
    }

    private void xuLyThemDichVu() {
        JOptionPane.showMessageDialog(view, "Bước tiếp theo mình sẽ nối dialog thêm dịch vụ ở đây.");
    }

    private void xuLyCapNhatThoiGian() {
        JOptionPane.showMessageDialog(view, "Bước tiếp theo mình sẽ nối dialog cập nhật thời gian ở đây.");
    }

    private void lamMoiTatCa() {
        view.getTxtMaPhong().setText("");
        view.getTxtMaDDP().setText("");
        view.getTxtCccdSdt().setText("");
        view.getCboTrangThai().setSelectedIndex(0);
        loadDanhSachMacDinh();
    }

    private void lamMoiChiTiet() {
        view.getLblMaPhong().setText("");
        view.getLblLoaiPhong().setText("");
        view.getLblGiaPhong().setText("");
        view.getLblTrangThaiPhong().setText("");
        view.getLblTenKH().setText("");
        view.getLblCCCD().setText("");
        view.getLblSDT().setText("");
        view.getLblMaDDPChiTiet().setText("");
        view.getLblCheckInDuKien().setText("");
        view.getLblCheckOutDuKien().setText("");
        view.getLblCheckInThucTe().setText("");
        view.getLblCheckOutThucTe().setText("");
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
        if ("Đã đặt".equals(tinhTrangDB)) return "Chờ check-in";
        if ("Đã nhận".equals(tinhTrangDB)) return "Đang lưu trú";
        if ("Hoàn thành".equals(tinhTrangDB)) return "Đã hoàn thành";
        return tinhTrangDB;
    }

    private String formatMoney(double amount) {
        return moneyFormat.format(amount);
    }
}