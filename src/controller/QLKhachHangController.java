package controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import dao.KhachHang_Dao;
import entity.KhachHang;
import gui.QLKhachHang;

public class QLKhachHangController {

    private QLKhachHang view;
    private KhachHang_Dao khachHangDao;

    public QLKhachHangController(QLKhachHang view) {
        this.view = view;
        this.khachHangDao = new KhachHang_Dao();

        initController();
        loadDataToTable();
    }

    private void initController() {
        view.getBtnTim().addActionListener(e -> timKhachHang());
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());

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

    private void loadDataToTable() {
        List<KhachHang> dsKhachHang = khachHangDao.findAll();
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (KhachHang kh : dsKhachHang) {
            model.addRow(new Object[] {
                kh.getMaKH(),
                kh.getHoTen(),
                kh.getSdt(),
                kh.getCccd(),
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
        view.getTxtSDT().setText(view.getTblKhachHang().getValueAt(row, 2).toString());
        view.getTxtCCCD().setText(view.getTblKhachHang().getValueAt(row, 3).toString());
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

            if (maKH.isEmpty() || hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã KH và họ tên không được rỗng");
                return;
            }

            int diemSo = 0;
            if (!diemSoText.isEmpty()) {
                try {
                    diemSo = Integer.parseInt(diemSoText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(view, "Điểm số phải là số nguyên");
                    return;
                }
            }

            if (khachHangDao.findById(maKH) != null) {
                JOptionPane.showMessageDialog(view, "Mã khách hàng đã tồn tại");
                return;
            }

            KhachHang kh = new KhachHang(maKH, hoTen, sdt, cccd, loaiKH, diemSo);

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

            if (maKH.isEmpty() || hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã KH và họ tên không được rỗng");
                return;
            }

            int diem = Integer.parseInt(view.getTblKhachHang().getValueAt(row, 6).toString());

            KhachHang kh = new KhachHang(maKH, hoTen, sdt, cccd, loaiKH, diem);

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

    private void timKhachHang() {
        String maKH = view.getTxtTimMaKH().getText().trim();
        String tenKH = view.getTxtTimTenKH().getText().trim();
        String loaiKH = view.getCboTimLoaiKH().getSelectedItem().toString();

        List<KhachHang> ds = khachHangDao.search(maKH, tenKH, loaiKH);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (KhachHang kh : ds) {
            model.addRow(new Object[] {
                kh.getMaKH(),
                kh.getHoTen(),
                kh.getSdt(),
                kh.getCccd(),
                kh.getLoaiKH(),
                kh.getDiem()
            });
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
}