package controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.KhuyenMai_Dao;
import entity.KhuyenMai;
import gui.QLKhuyenMai;

public class QLKhuyenMaiController {

    private final QLKhuyenMai view;
    private final KhuyenMai_Dao khuyenMaiDao;

    public QLKhuyenMaiController(QLKhuyenMai view) {
        this.view = view;
        this.khuyenMaiDao = new KhuyenMai_Dao();

        init();
    }

    private void init() {
        loadKhuyenMai();
        addEvents();
    }

    private void addEvents() {
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());
        view.getBtnLamMoiForm().addActionListener(e -> view.clearForm());

        view.getBtnThem().addActionListener(e -> themKhuyenMai());
        view.getBtnCapNhat().addActionListener(e -> capNhatKhuyenMai());
        view.getBtnXoa().addActionListener(e -> xoaKhuyenMai());

        view.getTblKhuyenMai().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getTblKhuyenMai().getSelectedRow() >= 0) {
                fillFormFromSelectedRow();
            }
        });

        DocumentListener searchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiemTuDong();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiemTuDong();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiemTuDong();
            }
        };

        view.getTxtTimMaKM().getDocument().addDocumentListener(searchListener);
        view.getTxtTimTenKhuyenMai().getDocument().addDocumentListener(searchListener);
        view.getTxtTimGiaTri().getDocument().addDocumentListener(searchListener);
        
        view.getCboLocTrangThai().addActionListener(e -> timKiemTuDong());
    }

    private void loadKhuyenMai() {
        List<KhuyenMai> list = khuyenMaiDao.findAll();
        fillTableKhuyenMai(list);
    }

    private void timKiemTuDong() {
        String maKM = view.getTxtTimMaKM().getText().trim();
        String tenKM = view.getTxtTimTenKhuyenMai().getText().trim();
        String giaTriText = view.getTxtTimGiaTri().getText().trim();
        String trangThai = view.getCboLocTrangThai().getSelectedItem().toString();

        Double giaTri = null;

        try {
            if (!giaTriText.isBlank()) {
                giaTri = Double.parseDouble(giaTriText);
            }
        } catch (NumberFormatException e) {
            return;
        }

        LocalDate tuNgayLocal = view.getTuNgayValue();
        LocalDate denNgayLocal = view.getDenNgayValue();

        java.sql.Date tuNgay = tuNgayLocal == null ? null : java.sql.Date.valueOf(tuNgayLocal);
        java.sql.Date denNgay = denNgayLocal == null ? null : java.sql.Date.valueOf(denNgayLocal);

        List<KhuyenMai> list = khuyenMaiDao.search(
                tuNgay,
                denNgay,
                maKM,
                tenKM,
                giaTri,
                trangThai
        );

        fillTableKhuyenMai(list);
    }

    private void fillTableKhuyenMai(List<KhuyenMai> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (KhuyenMai km : list) {
            model.addRow(new Object[] {
                    km.getMaKM(),
                    km.getTenKM(),
                    km.getGiaTri(),
                    km.getNgayBatDau(),
                    km.getNgayKetThuc(),
                    tinhTrangThai(km)
            });
        }
    }
    
    private String tinhTrangThai(KhuyenMai km) {
        if (km.getNgayKetThuc() == null) {
            return "";
        }

        if (km.getNgayKetThuc().isBefore(java.time.LocalDate.now())) {
            return "Hết hạn";
        }

        return "Còn sử dụng";
    }
    private void fillFormFromSelectedRow() {
        int row = view.getTblKhuyenMai().getSelectedRow();

        if (row < 0) {
            return;
        }

        view.getTxtMaKM().setText(getTableValue(row, 0));
        view.getTxtMaKM().setEditable(false);
        view.getTxtTenKhuyenMai().setText(getTableValue(row, 1));
        view.getTxtGiaTri().setText(getTableValue(row, 2));
        view.setNgayBatDauValue(LocalDate.parse(getTableValue(row, 3)));
        view.setNgayKetThucValue(LocalDate.parse(getTableValue(row, 4)));
    }

    private String getTableValue(int row, int col) {
        Object value = view.getTblKhuyenMai().getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private void themKhuyenMai() {
        KhuyenMai km = getKhuyenMaiFromForm();

        if (km == null) {
            return;
        }

        if (khuyenMaiDao.existsByMaKM(km.getMaKM())) {
            JOptionPane.showMessageDialog(view, "Mã khuyến mãi đã tồn tại.");
            return;
        }

        boolean ok = khuyenMaiDao.insert(km);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Thêm khuyến mãi thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm khuyến mãi thất bại.");
        }
    }

    private void capNhatKhuyenMai() {
        String maKM = view.getTxtMaKM().getText().trim();

        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khuyến mãi cần cập nhật.");
            return;
        }

        KhuyenMai km = getKhuyenMaiFromForm();

        if (km == null) {
            return;
        }

        boolean ok = khuyenMaiDao.update(km);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Cập nhật khuyến mãi thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật khuyến mãi thất bại.");
        }
    }

    private void xoaKhuyenMai() {
        String maKM = view.getTxtMaKM().getText().trim();

        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khuyến mãi cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn xóa khuyến mãi " + maKM + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = khuyenMaiDao.delete(maKM);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Xóa khuyến mãi thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Xóa khuyến mãi thất bại.");
        }
    }

    private KhuyenMai getKhuyenMaiFromForm() {
        String maKM = view.getTxtMaKM().getText().trim();
        String tenKM = view.getTxtTenKhuyenMai().getText().trim();
        String giaTriText = view.getTxtGiaTri().getText().trim();

        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Mã khuyến mãi không được để trống.");
            return null;
        }

        if (tenKM.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Tên khuyến mãi không được để trống.");
            return null;
        }

        double giaTri;

        try {
            giaTri = Double.parseDouble(giaTriText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Giá trị khuyến mãi phải là số.");
            return null;
        }

        if (giaTri <= 0) {
            JOptionPane.showMessageDialog(view, "Giá trị khuyến mãi phải lớn hơn 0.");
            return null;
        }

        LocalDate ngayBatDau = view.getNgayBatDauValue();
        LocalDate ngayKetThuc = view.getNgayKetThucValue();

        if (ngayBatDau == null || ngayKetThuc == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
            return null;
        }

        if (ngayKetThuc.isBefore(ngayBatDau)) {
            JOptionPane.showMessageDialog(view, "Ngày kết thúc không được trước ngày bắt đầu.");
            return null;
        }

        return new KhuyenMai(maKM, tenKM, giaTri, ngayBatDau, ngayKetThuc);
    }

    private void lamMoiTimKiem() {
    	view.clearSearchForm();
        loadKhuyenMai();
    }

    private void refreshAfterAction() {
        timKiemTuDong();
        view.clearForm();
    }
}
