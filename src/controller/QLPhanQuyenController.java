package controller;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.TaiKhoan_Dao;
import dao.TaiKhoan_Dao.NhanVienItem;
import gui.QLPhanQuyen;

public class QLPhanQuyenController {

    private final QLPhanQuyen view;
    private final TaiKhoan_Dao dao;

    public QLPhanQuyenController(QLPhanQuyen view) {
        this.view = view;
        this.dao = new TaiKhoan_Dao();

        init();
    }

    private void init() {
        loadNhanVien();
        loadTaiKhoan();
        addEvents();
    }

    private void addEvents() {
        view.getTxtTimKiem().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchTaiKhoanAuto();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchTaiKhoanAuto();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchTaiKhoanAuto();
            }
        });

        view.getCbTimTheo().addActionListener(e -> searchTaiKhoanAuto());

        view.getBtnLamMoiTimKiem().addActionListener(e -> {
            view.getTxtTimKiem().setText("");
            loadTaiKhoan();
        });

        view.getBtnLamMoiForm().addActionListener(e -> view.clearForm());

        view.getBtnThem().addActionListener(e -> insertTaiKhoan());

        view.getBtnCapNhat().addActionListener(e -> updateTaiKhoan());

        view.getBtnXoa().addActionListener(e -> deleteTaiKhoan());

        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getTable().getSelectedRow() >= 0) {
                fillFormFromSelectedRow();
            }
        });
    }

    private void loadTaiKhoan() {
        List<Object[]> list = dao.getAllTaiKhoan();
        fillTable(list);
    }

    private void searchTaiKhoanAuto() {
        String keyword = view.getTxtTimKiem().getText().trim();

        if (keyword.isEmpty()) {
            loadTaiKhoan();
            return;
        }

        String type = view.getCbTimTheo().getSelectedItem().toString();
        List<Object[]> list = dao.searchTaiKhoan(keyword, type);
        fillTable(list);
    }

    private void fillTable(List<Object[]> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (Object[] row : list) {
            model.addRow(row);
        }
    }

    private void loadNhanVien() {
        List<NhanVienItem> list = dao.getAllNhanVien();

        DefaultComboBoxModel<NhanVienItem> model = new DefaultComboBoxModel<>();

        for (NhanVienItem item : list) {
            model.addElement(item);
        }

        view.getCbNhanVien().setModel(model);
    }

    private void insertTaiKhoan() {
        String tenDangNhap = view.getTxtTenDangNhap().getText().trim();
        String matKhau = new String(view.getTxtMatKhau().getPassword()).trim();
        String vaiTro = view.getCbVaiTro().getSelectedItem().toString();
        NhanVienItem nv = (NhanVienItem) view.getCbNhanVien().getSelectedItem();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty() || nv == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        boolean result = dao.insertTaiKhoan(tenDangNhap, matKhau, vaiTro, nv.getMaNV());

        if (result) {
            JOptionPane.showMessageDialog(view, "Thêm tài khoản thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm tài khoản thất bại.");
        }
    }

    private void updateTaiKhoan() {
        String tenDangNhap = view.getTxtTenDangNhap().getText().trim();
        String matKhau = new String(view.getTxtMatKhau().getPassword()).trim();
        String vaiTroCbo = view.getCbVaiTro().getSelectedItem().toString();
        NhanVienItem nv = (NhanVienItem) view.getCbNhanVien().getSelectedItem();

        String vaiTro = null;
        if(vaiTroCbo.equals("Quản lý")) {
        	vaiTro = "QuanLy";
        }else {
        	vaiTro = "LeTan";
        }
        if (tenDangNhap.isEmpty() || nv == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn tài khoản cần cập nhật.");
            return;
        }

        boolean result = dao.updateTaiKhoan(tenDangNhap, vaiTro, nv.getMaNV());

        if (result) {
            JOptionPane.showMessageDialog(view, "Cập nhật tài khoản thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật tài khoản thất bại.");
        }
    }

    private void deleteTaiKhoan() {
        String tenDangNhap = view.getTxtTenDangNhap().getText().trim();

        if (tenDangNhap.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn tài khoản cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn xóa tài khoản này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean result = dao.deleteTaiKhoan(tenDangNhap);

        if (result) {
            JOptionPane.showMessageDialog(view, "Xóa tài khoản thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Xóa tài khoản thất bại.");
        }
    }

    private void refreshAfterAction() {
        if (view.getTxtTimKiem().getText().trim().isEmpty()) {
            loadTaiKhoan();
        } else {
            searchTaiKhoanAuto();
        }

        view.clearForm();
    }

    private void fillFormFromSelectedRow() {
        int row = view.getTable().getSelectedRow();
        String tenDangNhap = view.getTable().getValueAt(row, 0).toString();
        String maNV = view.getTable().getValueAt(row, 2).toString();
        String matKhau = view.getTable().getValueAt(row, 1).toString();
        String vaiTro = view.getTable().getValueAt(row, 4).toString();

        view.getTxtTenDangNhap().setText(tenDangNhap);
        view.getTxtMatKhau().setText(matKhau);
        view.getCbVaiTro().setSelectedItem(vaiTro);

        for (int i = 0; i < view.getCbNhanVien().getItemCount(); i++) {
            NhanVienItem item = view.getCbNhanVien().getItemAt(i);

            if (item.getMaNV().equals(maNV)) {
                view.getCbNhanVien().setSelectedIndex(i);
                break;
            }
        }
    }
}