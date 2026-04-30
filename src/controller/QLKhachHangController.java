package controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.KhachHang_Dao;
import entity.KhachHang;
import gui.QLKhachHang;

public class QLKhachHangController {
    private static final String REGEX_CCCD = "\\d{12}";
    private static final String REGEX_SDT = "\\d{10,15}";

    private QLKhachHang view;
    private KhachHang_Dao khachHangDao;

    public QLKhachHangController(QLKhachHang view) {
        this.view = view;
        this.khachHangDao = new KhachHang_Dao();

        initController();
        loadDataToTable();
    }

    private void initController() {
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());

        ganSuKienTimKiemTuDong();

        view.getBtnThem().addActionListener(e -> themKhachHang());
        view.getBtnCapNhat().addActionListener(e -> capNhatKhachHang());
        view.getBtnXoa().addActionListener(e -> xoaKhachHang());
        view.getBtnLamMoiForm().addActionListener(e -> lamMoiForm());

        view.getTblKhachHang().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinLenForm();
            }
        });
    }

    private void ganSuKienTimKiemTuDong() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKhachHangKhongThongBao();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKhachHangKhongThongBao();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKhachHangKhongThongBao();
            }
        };

        view.getTxtTimMaKH().getDocument().addDocumentListener(docListener);
        view.getTxtTimTenKH().getDocument().addDocumentListener(docListener);
        view.getCboTimLoaiKH().addActionListener(e -> timKhachHangKhongThongBao());
    }

    private void loadDataToTable() {
        List<KhachHang> dsKhachHang = khachHangDao.findAll();
        fillTable(dsKhachHang);
    }

    private void fillTable(List<KhachHang> dsKhachHang) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (KhachHang kh : dsKhachHang) {
            model.addRow(new Object[] {
                kh.getMaKH(),
                kh.getHoTen(),
                kh.getCccd(),
                kh.getSdt(),
                kh.getLoaiKH(),
                kh.getDiem()
            });
        }
    }

    private void hienThiThongTinLenForm() {
        int row = view.getTblKhachHang().getSelectedRow();
        if (row == -1) return;

        view.getTxtMaKH().setText(view.getTblKhachHang().getValueAt(row, 0).toString());
        view.getTxtHoTen().setText(view.getTblKhachHang().getValueAt(row, 1).toString());
        view.getTxtSDT().setText(view.getTblKhachHang().getValueAt(row, 3).toString());
        view.getTxtCCCD().setText(view.getTblKhachHang().getValueAt(row, 2).toString());
        view.getCboLoaiKH().setSelectedItem(view.getTblKhachHang().getValueAt(row, 4).toString());
        view.getTxtDiemSo().setText(view.getTblKhachHang().getValueAt(row, 5).toString());

        view.getTxtMaKH().setEditable(false);
    }

    private void themKhachHang() {
        try {
            String maKH = view.getTxtMaKH().getText().trim();
            String hoTen = view.getTxtHoTen().getText().trim();
            String sdt = view.getTxtSDT().getText().trim();
            String cccd = view.getTxtCCCD().getText().trim();
            String loaiKH = view.getCboLoaiKH().getSelectedItem().toString();
            String diemSoText = view.getTxtDiemSo().getText().trim();

            Integer diemSo = parseDiemSo(diemSoText);
            if (diemSo == null || !validateThongTinKhachHang(maKH, hoTen, sdt, cccd, diemSo)) {
                return;
            }

            if (khachHangDao.findById(maKH) != null) {
                JOptionPane.showMessageDialog(view, "Mã khách hàng đã tồn tại");
                return;
            }

            if (khachHangDao.existsBySdt(sdt)) {
                JOptionPane.showMessageDialog(view, "Số điện thoại đã tồn tại");
                return;
            }

            if (khachHangDao.existsByCccd(cccd)) {
                JOptionPane.showMessageDialog(view, "CCCD đã tồn tại");
                return;
            }

            KhachHang kh = new KhachHang(maKH, hoTen, cccd, sdt, loaiKH, diemSo);

            if (khachHangDao.insert(kh)) {
                JOptionPane.showMessageDialog(view, "Thêm thành công");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống");
        }
    }

    private void capNhatKhachHang() {
        try {
            int row = view.getTblKhachHang().getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(view, "Chọn khách hàng cần cập nhật");
                return;
            }

            String maKH = view.getTxtMaKH().getText().trim();
            String hoTen = view.getTxtHoTen().getText().trim();
            String sdt = view.getTxtSDT().getText().trim();
            String cccd = view.getTxtCCCD().getText().trim();
            String loaiKH = view.getCboLoaiKH().getSelectedItem().toString();
            String diemSoText = view.getTxtDiemSo().getText().trim();

            Integer diem = parseDiemSo(diemSoText);
            if (diem == null || !validateThongTinKhachHang(maKH, hoTen, sdt, cccd, diem)) {
                return;
            }

            if (khachHangDao.existsBySdtExceptMaKH(sdt, maKH)) {
                JOptionPane.showMessageDialog(view, "Số điện thoại đã tồn tại");
                return;
            }

            if (khachHangDao.existsByCccdExceptMaKH(cccd, maKH)) {
                JOptionPane.showMessageDialog(view, "CCCD đã tồn tại");
                return;
            }

            KhachHang kh = new KhachHang(maKH, hoTen, cccd, sdt, loaiKH, diem);

            if (khachHangDao.update(kh)) {
                JOptionPane.showMessageDialog(view, "Cập nhật thành công");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống");
        }
    }

    private void xoaKhachHang() {
        int row = view.getTblKhachHang().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Chọn khách hàng cần xóa");
            return;
        }

        String maKH = view.getTblKhachHang().getValueAt(row, 0).toString();

        if (khachHangDao.coDuLieuLienQuan(maKH)) {
            JOptionPane.showMessageDialog(
                    view,
                    "Khách hàng này đã phát sinh đặt phòng hoặc hóa đơn, không thể xóa."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xóa khách hàng " + maKH + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangDao.delete(maKH)) {
                JOptionPane.showMessageDialog(view, "Xóa thành công");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại");
            }
        }
    }

    private void timKhachHangKhongThongBao() {
        String maKH = view.getTxtTimMaKH().getText().trim();
        String tenKH = view.getTxtTimTenKH().getText().trim();
        String loaiKH = view.getCboTimLoaiKH().getSelectedItem().toString();

        List<KhachHang> ds = khachHangDao.search(maKH, tenKH, loaiKH);
        fillTable(ds);

        if (!ds.isEmpty()) {
            view.getTblKhachHang().setRowSelectionInterval(0, 0);
        } else {
            view.getTblKhachHang().clearSelection();
        }
    }

    private void lamMoiTimKiem() {
        view.getTxtTimMaKH().setText("");
        view.getTxtTimTenKH().setText("");
        view.getCboTimLoaiKH().setSelectedIndex(0);
        loadDataToTable();
    }

    private void lamMoiForm() {
        view.getTxtMaKH().setText("");
        view.getTxtMaKH().setEditable(true);
        view.getTxtHoTen().setText("");
        view.getTxtSDT().setText("");
        view.getTxtCCCD().setText("");
        view.getTxtDiemSo().setText("");
        view.getTblKhachHang().clearSelection();

        if (view.getCboLoaiKH().getItemCount() > 0) {
            view.getCboLoaiKH().setSelectedIndex(0);
        }

        view.getTxtMaKH().requestFocus();
    }

    private Integer parseDiemSo(String diemSoText) {
        if (diemSoText.isEmpty()) {
            return 0;
        }

        try {
            int diemSo = Integer.parseInt(diemSoText);

            if (diemSo < 0) {
                JOptionPane.showMessageDialog(view, "Điểm số không được âm");
                return null;
            }

            return diemSo;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Điểm số phải là số nguyên");
            return null;
        }
    }

    private boolean validateThongTinKhachHang(
            String maKH,
            String hoTen,
            String sdt,
            String cccd,
            int diemSo
    ) {
        if (maKH.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Mã KH và họ tên không được rỗng");
            return false;
        }

        if (!sdt.isEmpty() && !sdt.matches(REGEX_SDT)) {
            JOptionPane.showMessageDialog(view, "Số điện thoại phải gồm 10 đến 15 chữ số");
            return false;
        }

        if (!cccd.isEmpty() && !cccd.matches(REGEX_CCCD)) {
            JOptionPane.showMessageDialog(view, "CCCD phải gồm đúng 12 chữ số");
            return false;
        }

        if (diemSo < 0) {
            JOptionPane.showMessageDialog(view, "Điểm số không được âm");
            return false;
        }

        return true;
    }
}
