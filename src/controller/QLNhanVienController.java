package controller;

import java.sql.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.NhanVien_Dao;
import entity.NhanVien;
import gui.QLNhanVien;

public class QLNhanVienController {

    private QLNhanVien view;
    private NhanVien_Dao nhanVienDao;

    public QLNhanVienController(QLNhanVien view) {
        this.view = view;
        this.nhanVienDao = new NhanVien_Dao();

        // Quyền truy cập: chặn Lễ tân
        if (!GiaoDienChinhController.coQuyen("", "Nhân viên")) {
            JOptionPane.showMessageDialog(view, "Bạn không có quyền sử dụng chức năng này!");
            view.getBtnThem().setEnabled(false);
            view.getBtnCapNhat().setEnabled(false);
            view.getBtnXoa().setEnabled(false);
            loadDataToTable();
            return;
        }

        initController();
        loadDataToTable();
    }

    private void initController() {
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());

        ganSuKienTimKiemTuDong();

        view.getBtnThem().addActionListener(e -> themNhanVien());
        view.getBtnCapNhat().addActionListener(e -> capNhatNhanVien());
        view.getBtnXoa().addActionListener(e -> xoaNhanVien());
        view.getBtnLamMoiForm().addActionListener(e -> lamMoiForm());

        view.getTblNhanVien().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinLenForm();
            }
        });
    }

    private void ganSuKienTimKiemTuDong() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timNhanVienKhongThongBao();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timNhanVienKhongThongBao();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timNhanVienKhongThongBao();
            }
        };

        view.getTxtTimMaNV().getDocument().addDocumentListener(docListener);
        view.getTxtTimHoTen().getDocument().addDocumentListener(docListener);
        view.getCboLocTrangThai().addActionListener(e -> timNhanVienKhongThongBao());
    }

    private void loadDataToTable() {
        List<NhanVien> dsNhanVien = nhanVienDao.findAll();
        fillTable(dsNhanVien);
    }

    private void fillTable(List<NhanVien> dsNhanVien) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (NhanVien nv : dsNhanVien) {
            model.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getNgaySinh(),
                    nv.getSdt(),
                    nv.getEmail(),
                    nv.getGioiTinh(),
                    nv.getNgayBatDauVaoLam(),
                    nv.getTrangThaiLamViec(),
                    nv.getDiaChi(),
                    nv.getCaLamViec(),
                    nv.getViTriCongViec()
            });
        }
    }

    private void hienThiThongTinLenForm() {
        int row = view.getTblNhanVien().getSelectedRow();
        if (row == -1)
            return;

        view.getTxtMaNV().setText(view.getTblNhanVien().getValueAt(row, 0).toString());
        view.getTxtHoTen().setText(view.getTblNhanVien().getValueAt(row, 1).toString());
        view.getTxtNgaySinh().setText(view.getTblNhanVien().getValueAt(row, 2).toString());
        view.getTxtSDT().setText(view.getTblNhanVien().getValueAt(row, 3).toString());
        view.getTxtEmail().setText(view.getTblNhanVien().getValueAt(row, 4).toString());
        view.getCboGioiTinh().setSelectedItem(view.getTblNhanVien().getValueAt(row, 5).toString());
        view.getTxtNgayBatDauVaoLam().setText(view.getTblNhanVien().getValueAt(row, 6).toString());
        view.getCboTrangThaiLamViec().setSelectedItem(view.getTblNhanVien().getValueAt(row, 7).toString());
        view.getTxtDiaChi().setText(view.getTblNhanVien().getValueAt(row, 8).toString());
        view.getCboCaLamViec().setSelectedItem(view.getTblNhanVien().getValueAt(row, 9).toString());
        view.getCboViTriCongViec().setSelectedItem(view.getTblNhanVien().getValueAt(row, 10).toString());

        view.getTxtMaNV().setEditable(false);
    }

    private void themNhanVien() {
        try {
            String maNV = view.getTxtMaNV().getText().trim();
            String hoTen = view.getTxtHoTen().getText().trim();
            String ngaySinhText = view.getTxtNgaySinh().getText().trim();
            String sdt = view.getTxtSDT().getText().trim();
            String email = view.getTxtEmail().getText().trim();
            String gioiTinh = view.getCboGioiTinh().getSelectedItem().toString();
            String ngayBatDauText = view.getTxtNgayBatDauVaoLam().getText().trim();
            String trangThai = view.getCboTrangThaiLamViec().getSelectedItem().toString();
            String diaChi = view.getTxtDiaChi().getText().trim();
            String caLam = view.getCboCaLamViec().getSelectedItem().toString();
            String viTri = view.getCboViTriCongViec().getSelectedItem().toString();

            if (maNV.isEmpty() || hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã NV và họ tên không được rỗng");
                return;
            }

            Date ngaySinh = Date.valueOf(ngaySinhText);
            Date ngayBatDau = Date.valueOf(ngayBatDauText);

            if (nhanVienDao.findById(maNV) != null) {
                JOptionPane.showMessageDialog(view, "Mã nhân viên đã tồn tại");
                return;
            }

            NhanVien nv = new NhanVien(
                    maNV, hoTen, ngaySinh, sdt, email, gioiTinh,
                    ngayBatDau, trangThai, diaChi, caLam, viTri);

            if (nhanVienDao.insert(nv)) {
                JOptionPane.showMessageDialog(view, "Thêm thành công");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại");
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Ngày phải đúng định dạng yyyy-mm-dd");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống");
        }
    }

    private void capNhatNhanVien() {
        try {
            int row = view.getTblNhanVien().getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(view, "Chọn nhân viên cần cập nhật");
                return;
            }

            String maNV = view.getTxtMaNV().getText().trim();
            String hoTen = view.getTxtHoTen().getText().trim();
            String ngaySinhText = view.getTxtNgaySinh().getText().trim();
            String sdt = view.getTxtSDT().getText().trim();
            String email = view.getTxtEmail().getText().trim();
            String gioiTinh = view.getCboGioiTinh().getSelectedItem().toString();
            String ngayBatDauText = view.getTxtNgayBatDauVaoLam().getText().trim();
            String trangThai = view.getCboTrangThaiLamViec().getSelectedItem().toString();
            String diaChi = view.getTxtDiaChi().getText().trim();
            String caLam = view.getCboCaLamViec().getSelectedItem().toString();
            String viTri = view.getCboViTriCongViec().getSelectedItem().toString();

            if (maNV.isEmpty() || hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Mã NV và họ tên không được rỗng");
                return;
            }

            Date ngaySinh = Date.valueOf(ngaySinhText);
            Date ngayBatDau = Date.valueOf(ngayBatDauText);

            NhanVien nv = new NhanVien(
                    maNV, hoTen, ngaySinh, sdt, email, gioiTinh,
                    ngayBatDau, trangThai, diaChi, caLam, viTri);

            if (nhanVienDao.update(nv)) {
                JOptionPane.showMessageDialog(view, "Cập nhật thành công");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại");
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Ngày phải đúng định dạng yyyy-mm-dd");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống");
        }
    }

    private void xoaNhanVien() {
        int row = view.getTblNhanVien().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Chọn nhân viên cần xóa");
            return;
        }

        String maNV = view.getTblNhanVien().getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xóa nhân viên " + maNV + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienDao.delete(maNV)) {
                JOptionPane.showMessageDialog(view, "Xóa thành công");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại");
            }
        }
    }

    private void timNhanVienKhongThongBao() {
        String maNV = view.getTxtTimMaNV().getText().trim();
        String hoTen = view.getTxtTimHoTen().getText().trim();
        String trangThai = view.getCboLocTrangThai().getSelectedItem().toString();

        List<NhanVien> ds = nhanVienDao.search(maNV, hoTen, trangThai, "Tất cả", "Tất cả");
        fillTable(ds);

        if (!ds.isEmpty()) {
            view.getTblNhanVien().setRowSelectionInterval(0, 0);
        } else {
            view.getTblNhanVien().clearSelection();
        }
    }

    private void lamMoiTimKiem() {
        view.getTxtTimMaNV().setText("");
        view.getTxtTimHoTen().setText("");
        view.getCboLocTrangThai().setSelectedIndex(0);
        loadDataToTable();
    }

    private void lamMoiForm() {
        view.getTxtMaNV().setText("");
        view.getTxtMaNV().setEditable(true);
        view.getTxtHoTen().setText("");
        view.getTxtNgaySinh().setText("");
        view.getTxtSDT().setText("");
        view.getTxtEmail().setText("");
        view.getTxtNgayBatDauVaoLam().setText("");
        view.getTxtDiaChi().setText("");
        view.getTblNhanVien().clearSelection();

        view.getCboGioiTinh().setSelectedIndex(0);
        view.getCboTrangThaiLamViec().setSelectedIndex(0);
        view.getCboCaLamViec().setSelectedIndex(0);
        view.getCboViTriCongViec().setSelectedIndex(0);
    }
}