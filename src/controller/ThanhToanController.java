package controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.ThanhToan_Dao;
import gui.ThanhToan;

public class ThanhToanController {

    private final ThanhToan view;
    private final ThanhToan_Dao dao;
    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private String maDDPDangChon = "";

    private double tienPhong = 0;
    private double tienDichVu = 0;
    private double tienCoc = 0;
    private double giamGia = 0;
    private double tongTien = 0;
    private double canThanhToan = 0;

    public ThanhToanController(ThanhToan view) {
        this.view = view;
        this.dao = new ThanhToan_Dao();

        addEvents();
    }

    private void addEvents() {
        view.getBtnTimKiem().addActionListener(e -> timKiemDonChoThanhToan());

        view.getBtnLamMoi().addActionListener(e -> {
            view.clearSearch();
            view.clearDetail();
            loadDanhSachDonDaNhan();
        });

        view.getBtnTinhTien().addActionListener(e -> tinhTienVaHienThi());

        view.getBtnThanhToan().addActionListener(e -> xuLyThanhToan());

        view.getBtnXuatHoaDon().addActionListener(e -> {
            if (maDDPDangChon.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần xuất hóa đơn.");
                return;
            }

            xuatHoaDon(maDDPDangChon);
        });

        view.getBtnHuy().addActionListener(e -> view.clearDetail());

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
            view.clearDetail();
        }
    }

    private void fillTableDonChoThanhToan(List<Object[]> list) {
        DefaultTableModel model = view.getModelDonChoThanhToan();
        model.setRowCount(0);

        for (Object[] row : list) {
            double tienCocValue = toDouble(row[6]);

            model.addRow(new Object[] {
                    row[0],                    // maDDP
                    row[1],                    // hoTen
                    row[2],                    // cccd
                    row[3],                    // sdt
                    row[4],                    // ngayNhan
                    row[5],                    // ngayTra
                    formatMoney(tienCocValue)  // tienCoc
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

        view.getLblMaDDP().setText(toText(don[0]));
        view.getLblTenKH().setText(toText(don[1]));
        view.getLblCCCD().setText(toText(don[2]));
        view.getLblSDT().setText(toText(don[3]));
        view.getLblNgayNhan().setText(toText(don[4]));
        view.getLblNgayTra().setText(toText(don[5]));

        tienCoc = toDouble(don[6]);
        view.getLblTienCoc().setText(formatMoney(tienCoc) + " VNĐ");

        loadPhong(maDDP);
        loadDichVu(maDDP);
        tinhTienVaHienThi();
    }

    private void loadPhong(String maDDP) {
        DefaultTableModel model = view.getModelPhong();
        model.setRowCount(0);

        tienPhong = 0;

        List<Object[]> list = dao.findPhongByMaDDP(maDDP);

        for (Object[] row : list) {
            String maPhong = toText(row[0]);
            String loaiPhong = toText(row[1]);
            int soNgay = toInt(row[2]);
            double donGia = toDouble(row[3]);

            double thanhTien;

            if (row.length >= 5) {
                thanhTien = toDouble(row[4]);
            } else {
                thanhTien = soNgay * donGia;
            }

            tienPhong += thanhTien;

            model.addRow(new Object[] {
                    maPhong,
                    loaiPhong,
                    soNgay,
                    formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }
    }

    private void loadDichVu(String maDDP) {
        DefaultTableModel model = view.getModelDichVu();
        model.setRowCount(0);

        tienDichVu = 0;

        List<Object[]> list = dao.findDichVuByMaDDP(maDDP);

        for (Object[] row : list) {
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

            tienDichVu += thanhTien;

            model.addRow(new Object[] {
                    maDV,
                    tenDV,
                    soLuong,
                    donGia <= 0 ? "" : formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }
    }

    private void tinhTienVaHienThi() {
        tongTien = tienPhong + tienDichVu;
        giamGia = 0;
        canThanhToan = tongTien - tienCoc - giamGia;

        if (canThanhToan < 0) {
            canThanhToan = 0;
        }

        view.getLblTienPhong().setText(formatMoney(tienPhong) + " VNĐ");
        view.getLblTienDichVu().setText(formatMoney(tienDichVu) + " VNĐ");
        view.getLblTongTien().setText(formatMoney(tongTien) + " VNĐ");
        view.getLblGiamGia().setText(formatMoney(giamGia) + " VNĐ");
        view.getLblCanThanhToan().setText(formatMoney(canThanhToan) + " VNĐ");

        capNhatTienThua();
    }

    private void capNhatTienThua() {
        String phuongThuc = view.getCboPhuongThuc().getSelectedItem().toString();

        if (!"Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            view.getLblTienThua().setText("0 VNĐ");
            return;
        }

        String khachDuaText = view.getTxtKhachDua().getText().trim();

        if (khachDuaText.isEmpty()) {
            view.getLblTienThua().setText("");
            return;
        }

        try {
            double khachDua = Double.parseDouble(khachDuaText);
            double tienThua = khachDua - canThanhToan;

            if (tienThua < 0) {
                view.getLblTienThua().setText("Thiếu " + formatMoney(Math.abs(tienThua)) + " VNĐ");
            } else {
                view.getLblTienThua().setText(formatMoney(tienThua) + " VNĐ");
            }

        } catch (NumberFormatException e) {
            view.getLblTienThua().setText("Không hợp lệ");
        }
    }

    private void xuLyThanhToan() {
        if (maDDPDangChon.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần thanh toán.");
            return;
        }

        tinhTienVaHienThi();

        String phuongThuc = view.getCboPhuongThuc().getSelectedItem().toString();

        if ("Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            String khachDuaText = view.getTxtKhachDua().getText().trim();

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
                "Xác nhận thanh toán đơn " + maDDPDangChon + "?\n"
                        + "Sau khi thanh toán, hóa đơn sẽ được xuất và đơn sẽ hoàn thành.",
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean daXuatHoaDon = xuatHoaDon(maDDPDangChon);

        if (!daXuatHoaDon) {
            JOptionPane.showMessageDialog(view, "Xuất hóa đơn thất bại. Chưa hoàn tất thanh toán.");
            return;
        }

        boolean ok = dao.hoanTatThanhToan(maDDPDangChon);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Thanh toán thành công. Đơn đã hoàn thành.");

            view.clearDetail();
            loadDanhSachDonDaNhan();

        } else {
            JOptionPane.showMessageDialog(view, "Xuất hóa đơn thành công nhưng cập nhật trạng thái thất bại.");
        }
    }

    private boolean xuatHoaDon(String maDDP) {
        /*
         * Tạm thời return true để hoàn thiện flow.
         * Nếu bé đã có HoaDon_Dao hoặc QLHoaDonController thì thay đoạn này bằng:
         * - tạo hóa đơn
         * - lưu chi tiết hóa đơn
         * - xuất file/hiển thị hóa đơn
         */

        System.out.println("Xuất hóa đơn cho đơn: " + maDDP);
        System.out.println("Tiền phòng: " + tienPhong);
        System.out.println("Tiền dịch vụ: " + tienDichVu);
        System.out.println("Tổng tiền: " + tongTien);
        System.out.println("Tiền cọc: " + tienCoc);
        System.out.println("Cần thanh toán: " + canThanhToan);

        return true;
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
}