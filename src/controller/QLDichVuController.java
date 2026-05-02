package controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.DichVu_Dao;
import entity.DichVu;
import gui.QLDichVu;

public class QLDichVuController {

    private final QLDichVu view;
    private final DichVu_Dao dichVuDao;

    public QLDichVuController(QLDichVu view) {
        this.view = view;
        this.dichVuDao = new DichVu_Dao();

        init();
    }

    private void init() {
        loadDichVu();
        addEvents();
    }

    private void addEvents() {
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());
        view.getBtnLamMoiForm().addActionListener(e -> view.clearForm());

        view.getBtnThem().addActionListener(e -> themDichVu());
        view.getBtnCapNhat().addActionListener(e -> capNhatDichVu());
        view.getBtnXoa().addActionListener(e -> xoaDichVu());

        view.getTblDichVu().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getTblDichVu().getSelectedRow() >= 0) {
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

        view.getTxtTimMaDV().getDocument().addDocumentListener(searchListener);
        view.getTxtTimTenDV().getDocument().addDocumentListener(searchListener);
    }

    private void loadDichVu() {
        List<DichVu> list = dichVuDao.findAll();
        fillTableDichVu(list);
    }

    private void timKiemTuDong() {
        String maDV = view.getTxtTimMaDV().getText().trim();
        String tenDichVu = view.getTxtTimTenDV().getText().trim();
        String giaText = view.getTxtTimGia().getText().trim();

        Double giaDichVu = null;
        java.sql.Date tuNgay = null;
        java.sql.Date denNgay = null;

        try {
            if (!giaText.isBlank()) {
                giaDichVu = Double.parseDouble(giaText);
            }

        } catch (Exception e) {
            return;
        }

        List<DichVu> list = dichVuDao.search(
                tuNgay,
                denNgay,
                maDV,
                tenDichVu,
                giaDichVu
        );

        fillTableDichVu(list);
    }

    private void fillTableDichVu(List<DichVu> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (DichVu dv : list) {
            model.addRow(new Object[] {
            		dv.getMaDV(),
                    dv.getTenDV(),
                    dv.getGia(),
                    dv.getSoLuotDung(),
                    dv.getDoanhThu()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = view.getTblDichVu().getSelectedRow();

        if (row < 0) {
            return;
        }

        view.getTxtMaDV().setText(getTableValue(row, 0));
        view.getTxtTenDV().setText(getTableValue(row, 1));
        view.getTxtGia().setText(getTableValue(row, 2));
    }

    private String getTableValue(int row, int col) {
        Object value = view.getTblDichVu().getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private void themDichVu() {
        DichVu dv = getDichVuFromForm();

        if (dv == null) {
            return;
        }

        if (dichVuDao.existsByMaDV(dv.getMaDV())) {
            JOptionPane.showMessageDialog(view, "Mã dịch vụ đã tồn tại.");
            return;
        }

        boolean ok = dichVuDao.insert(dv);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Thêm dịch vụ thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm dịch vụ thất bại.");
        }
    }

    private void capNhatDichVu() {
        String maDV = view.getTxtMaDV().getText().trim();

        if (maDV.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ cần cập nhật.");
            return;
        }

        DichVu dv = getDichVuFromForm();

        if (dv == null) {
            return;
        }

        boolean ok = dichVuDao.update(dv);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Cập nhật dịch vụ thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật dịch vụ thất bại.");
        }
    }

    private void xoaDichVu() {
        String maDV = view.getTxtMaDV().getText().trim();

        if (maDV.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ cần xóa.");
            return;
        }

        if (dichVuDao.dangDuocSuDung(maDV)) {
            JOptionPane.showMessageDialog(
                    view,
                    "Dịch vụ này đã được sử dụng trong phiếu dịch vụ, không thể xóa."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn xóa dịch vụ " + maDV + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = dichVuDao.delete(maDV);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Xóa dịch vụ thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Xóa dịch vụ thất bại.");
        }
    }

    private DichVu getDichVuFromForm() {
        String maDV = view.getTxtMaDV().getText().trim();
        String tenDV = view.getTxtTenDV().getText().trim();
        String giaText = view.getTxtGia().getText().trim();

        if (maDV.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Mã dịch vụ không được để trống.");
            return null;
        }

        if (tenDV.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Tên dịch vụ không được để trống.");
            return null;
        }

        double gia;

        try {
            gia = Double.parseDouble(giaText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Giá dịch vụ phải là số.");
            return null;
        }

        if (gia <= 0) {
            JOptionPane.showMessageDialog(view, "Giá dịch vụ phải lớn hơn 0.");
            return null;
        }

        return new DichVu(maDV, tenDV, gia);
    }

    private void lamMoiTimKiem() {
    	view.clearSearchForm();

        loadDichVu();
    }

    private void refreshAfterAction() {
        timKiemTuDong();
        view.clearForm();
    }
}
