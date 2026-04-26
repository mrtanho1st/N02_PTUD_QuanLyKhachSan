package controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.Phong_Dao;
import entity.Phong;
import gui.QLPhong;

public class QLPhongController {

    private final QLPhong view;
    private final Phong_Dao phongDao;

    public QLPhongController(QLPhong view) {
        this.view = view;
        this.phongDao = new Phong_Dao();

        initController();
        loadDataToTable();
    }

    private void initController() {
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());

        ganSuKienTimKiemTuDong();

        view.getBtnThem().addActionListener(e -> themPhong());
        view.getBtnSua().addActionListener(e -> suaPhong());
        view.getBtnXoa().addActionListener(e -> xoaPhong());
        view.getBtnLamMoiForm().addActionListener(e -> lamMoiForm());

        view.getTblPhong().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinLenForm();
            }
        });
    }

    private void ganSuKienTimKiemTuDong() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timPhongKhongThongBao();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timPhongKhongThongBao();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timPhongKhongThongBao();
            }
        };

        view.getTxtTimMaPhong().getDocument().addDocumentListener(docListener);
        view.getTxtTimSoNguoi().getDocument().addDocumentListener(docListener);

        view.getCboLocLoaiPhong().addActionListener(e -> timPhongKhongThongBao());
        view.getCboLocTrangThai().addActionListener(e -> timPhongKhongThongBao());
    }

    private void loadDataToTable() {
        List<Phong> dsPhong = phongDao.findAll();
        fillTable(dsPhong);
    }

    private void fillTable(List<Phong> dsPhong) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (Phong p : dsPhong) {
            model.addRow(new Object[] {
                p.getMaPhong(),
                p.getLoaiPhong(),
                p.getSoNguoiToiDa(),
                p.getGiaPhong(),
                p.getTrangThai()
            });
        }
    }

    private void hienThiThongTinLenForm() {
        int row = view.getTblPhong().getSelectedRow();
        if (row == -1) return;

        view.getTxtMaPhong().setText(view.getTblPhong().getValueAt(row, 0).toString());
        view.getCboLoaiPhong().setSelectedItem(view.getTblPhong().getValueAt(row, 1).toString());
        view.getTxtSoNguoiToiDa().setText(view.getTblPhong().getValueAt(row, 2).toString());
        view.getTxtGiaPhong().setText(view.getTblPhong().getValueAt(row, 3).toString());
        view.getCboTrangThaiPhong().setSelectedItem(view.getTblPhong().getValueAt(row, 4).toString());

        view.getTxtMaPhong().setEditable(false);
    }

    private void themPhong() {
        try {
            Phong phong = layThongTinPhongTuForm();
            if (phong == null) return;

            if (phongDao.findById(phong.getMaPhong()) != null) {
                JOptionPane.showMessageDialog(view, "Mã phòng đã tồn tại.");
                return;
            }

            if (phongDao.insert(phong)) {
                JOptionPane.showMessageDialog(view, "Thêm phòng thành công.");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm phòng thất bại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống khi thêm phòng.");
        }
    }

    private void suaPhong() {
        try {
            int row = view.getTblPhong().getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần cập nhật.");
                return;
            }

            Phong phong = layThongTinPhongTuForm();
            if (phong == null) return;

            if (phongDao.update(phong)) {
                JOptionPane.showMessageDialog(view, "Cập nhật phòng thành công.");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật phòng thất bại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống khi cập nhật phòng.");
        }
    }

    private void xoaPhong() {
        int row = view.getTblPhong().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần xóa.");
            return;
        }

        String maPhong = view.getTblPhong().getValueAt(row, 0).toString();
        String trangThai = view.getTblPhong().getValueAt(row, 4).toString();

        if ("Đã đặt".equalsIgnoreCase(trangThai) || "Đang sử dụng".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(view,
                    "Không thể xóa phòng đang ở trạng thái '" + trangThai + "'.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn xóa phòng " + maPhong + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (phongDao.delete(maPhong)) {
                JOptionPane.showMessageDialog(view, "Xóa phòng thành công.");
                loadDataToTable();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại.");
            }
        }
    }

    private void timPhongKhongThongBao() {
        String maPhong = view.getTxtTimMaPhong().getText().trim();
        String soNguoiText = view.getTxtTimSoNguoi().getText().trim();
        String loaiPhong = view.getCboLocLoaiPhong().getSelectedItem().toString();
        String trangThai = view.getCboLocTrangThai().getSelectedItem().toString();

        Integer soNguoiCanTim = null;
        Double giaPhongCanTim = null; // chưa có ô giá thì để null

        if (!soNguoiText.isEmpty()) {
            try {
                soNguoiCanTim = Integer.parseInt(soNguoiText);

                if (soNguoiCanTim <= 0) {
                    fillTable(phongDao.search(maPhong, null, giaPhongCanTim, loaiPhong, trangThai));
                    return;
                }

            } catch (NumberFormatException e) {
                fillTable(phongDao.search(maPhong, null, giaPhongCanTim, loaiPhong, trangThai));
                return;
            }
        }

        List<Phong> ds = phongDao.search(maPhong, soNguoiCanTim, giaPhongCanTim, loaiPhong, trangThai);
        fillTable(ds);
    }

    private void lamMoiTimKiem() {
        view.getTxtTimMaPhong().setText("");
        view.getTxtTimSoNguoi().setText("");
        view.getCboLocLoaiPhong().setSelectedIndex(0);
        view.getCboLocTrangThai().setSelectedIndex(0);
        loadDataToTable();
    }

    private void lamMoiForm() {
        view.getTxtMaPhong().setText("");
        view.getTxtMaPhong().setEditable(true);
        view.getTxtSoNguoiToiDa().setText("");
        view.getTxtGiaPhong().setText("");
        view.getTblPhong().clearSelection();

        if (view.getCboLoaiPhong().getItemCount() > 0) {
            view.getCboLoaiPhong().setSelectedIndex(0);
        }

        if (view.getCboTrangThaiPhong().getItemCount() > 0) {
            view.getCboTrangThaiPhong().setSelectedIndex(0);
        }
    }

    private Phong layThongTinPhongTuForm() {
        String maPhong = view.getTxtMaPhong().getText().trim();
        String loaiPhong = view.getCboLoaiPhong().getSelectedItem().toString();
        String soNguoiText = view.getTxtSoNguoiToiDa().getText().trim();
        String giaText = view.getTxtGiaPhong().getText().trim();
        String trangThai = view.getCboTrangThaiPhong().getSelectedItem().toString();

        if (maPhong.isEmpty() || soNguoiText.isEmpty() || giaText.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin phòng.");
            return null;
        }

        if (!maPhong.matches("^P\\d{3}$")) {
            JOptionPane.showMessageDialog(view, "Mã phòng phải đúng định dạng, ví dụ: P101.");
            return null;
        }

        int soNguoi;
        double giaPhong;

        try {
            soNguoi = Integer.parseInt(soNguoiText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Số người tối đa phải là số nguyên.");
            return null;
        }

        try {
            giaPhong = Double.parseDouble(giaText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Giá phòng phải là số.");
            return null;
        }

        if (soNguoi <= 0) {
            JOptionPane.showMessageDialog(view, "Số người tối đa phải lớn hơn 0.");
            return null;
        }

        if (giaPhong <= 0) {
            JOptionPane.showMessageDialog(view, "Giá phòng phải lớn hơn 0.");
            return null;
        }

        return new Phong(maPhong, loaiPhong, soNguoi, giaPhong, trangThai);
    }
}