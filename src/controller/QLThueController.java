package controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.Thue_Dao;
import entity.Thue;
import gui.QLThue;

public class QLThueController {

    private final QLThue view;
    private final Thue_Dao thueDao;

    public QLThueController(QLThue view) {
        this.view = view;
        this.thueDao = new Thue_Dao();

        init();
    }

    private void init() {
        loadThue();
        addEvents();
    }

    private void addEvents() {
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());
        view.getBtnLamMoiForm().addActionListener(e -> lamMoiForm());

        view.getBtnThem().addActionListener(e -> themThue());
        view.getBtnCapNhat().addActionListener(e -> capNhatThue());
        view.getBtnXoa().addActionListener(e -> xoaThue());

        view.getTblThue().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getTblThue().getSelectedRow() >= 0) {
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

        view.getTxtTimMaThue().getDocument().addDocumentListener(searchListener);
        view.getTxtTimTenThue().getDocument().addDocumentListener(searchListener);
        view.getTxtTimTyLe().getDocument().addDocumentListener(searchListener);
        view.getCboTimTrangThai().addActionListener(e -> timKiemTuDong());
    }

    private void loadThue() {
        fillTable(thueDao.findAll());
    }

    private void timKiemTuDong() {
        String maThue = view.getTxtTimMaThue().getText().trim();
        String tenThue = view.getTxtTimTenThue().getText().trim();
        String trangThai = view.getCboTimTrangThai().getSelectedItem().toString();
        String tyLeText = view.getTxtTimTyLe().getText().trim();

        Double tyLeThue = null;

        try {
            if (!tyLeText.isBlank()) {
                tyLeThue = Double.parseDouble(tyLeText);
            }
        } catch (NumberFormatException e) {
            return;
        }

        fillTable(thueDao.search(maThue, tenThue, trangThai, tyLeThue));
    }

    private void fillTable(List<Thue> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (Thue thue : list) {
            model.addRow(new Object[] {
                    thue.getMaThue(),
                    thue.getTenThue(),
                    thue.getTrangThai(),
                    thue.getTyLeThue(),
                    thue.getMoTa()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = view.getTblThue().getSelectedRow();

        if (row < 0) {
            return;
        }

        view.getTxtMaThue().setText(getTableValue(row, 0));
        view.getTxtTenThue().setText(getTableValue(row, 1));
        view.getCboTrangThai().setSelectedItem(getTableValue(row, 2));
        view.getTxtTyLeThue().setText(getTableValue(row, 3));
        view.getTxtMoTa().setText(getTableValue(row, 4));
        view.getTxtMaThue().setEditable(false);
    }

    private String getTableValue(int row, int col) {
        Object value = view.getTblThue().getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private void themThue() {
        Thue thue = getThueFromForm();

        if (thue == null) {
            return;
        }

        if (thueDao.existsByMaThue(thue.getMaThue())) {
            JOptionPane.showMessageDialog(view, "Mã thuế đã tồn tại.");
            return;
        }

        if (thueDao.insert(thue)) {
            JOptionPane.showMessageDialog(view, "Thêm thuế thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm thuế thất bại.");
        }
    }

    private void capNhatThue() {
        if (view.getTblThue().getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thuế cần cập nhật.");
            return;
        }

        Thue thue = getThueFromForm();

        if (thue == null) {
            return;
        }

        if (thueDao.update(thue)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thuế thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thuế thất bại.");
        }
    }

    private void xoaThue() {
        if (view.getTblThue().getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thuế cần xóa.");
            return;
        }

        String maThue = view.getTxtMaThue().getText().trim();

        if (thueDao.dangDuocSuDung(maThue)) {
            JOptionPane.showMessageDialog(
                    view,
                    "Thuế này đang được sử dụng trong hóa đơn, không thể xóa."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn xóa thuế " + maThue + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (thueDao.delete(maThue)) {
            JOptionPane.showMessageDialog(view, "Xóa thuế thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Xóa thuế thất bại.");
        }
    }

    private Thue getThueFromForm() {
        String maThue = view.getTxtMaThue().getText().trim();
        String tenThue = view.getTxtTenThue().getText().trim();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();
        String tyLeText = view.getTxtTyLeThue().getText().trim();
        String moTa = view.getTxtMoTa().getText().trim();

        if (maThue.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Mã thuế không được để trống.");
            return null;
        }

        if (tenThue.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Tên thuế không được để trống.");
            return null;
        }

        double tyLeThue;

        try {
            tyLeThue = Double.parseDouble(tyLeText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Tỷ lệ thuế phải là số.");
            return null;
        }

        if (tyLeThue < 0 || tyLeThue > 100) {
            JOptionPane.showMessageDialog(view, "Tỷ lệ thuế phải nằm trong khoảng từ 0 đến 100.");
            return null;
        }

        return new Thue(maThue, tenThue, trangThai, tyLeThue, moTa);
    }

    private void lamMoiTimKiem() {
        view.clearSearchForm();
        loadThue();
    }

    private void lamMoiForm() {
        view.clearForm();
        view.getTxtMaThue().requestFocus();
    }

    private void refreshAfterAction() {
        timKiemTuDong();
        view.clearForm();
    }
}
