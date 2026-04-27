package controller;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import dao.TaiKhoan_Dao;
import dao.TaiKhoan_Dao.NhanVienItem;
import gui.TaoTaiKhoan;

public class TaoTaiKhoanController {

    private final TaoTaiKhoan view;
    private final TaiKhoan_Dao dao;

    public TaoTaiKhoanController(TaoTaiKhoan view) {
        this.view = view;
        this.dao = new TaiKhoan_Dao();

        init();
    }

    private void init() {
        loadNhanVienChuaCoTaiKhoan();
        addEvents();
    }

    private void addEvents() {
        view.getCbNhanVien().addActionListener(e -> fillNhanVienInfo());

        view.getBtnTaoTaiKhoan().addActionListener(e -> taoTaiKhoan());

        view.getBtnLamMoi().addActionListener(e -> view.clearForm());

        view.getBtnHuy().addActionListener(e -> view.clearForm());
    }

    private void loadNhanVienChuaCoTaiKhoan() {
        List<NhanVienItem> list = dao.getNhanVienChuaCoTaiKhoan();

        DefaultComboBoxModel<NhanVienItem> model = new DefaultComboBoxModel<>();

        for (NhanVienItem item : list) {
            model.addElement(item);
        }

        view.getCbNhanVien().setModel(model);

        if (model.getSize() > 0) {
            view.getCbNhanVien().setSelectedIndex(0);
            view.fillNhanVienInfo(model.getElementAt(0));
        } else {
            view.clearNhanVienInfo();
        }
    }

    private void fillNhanVienInfo() {
        NhanVienItem nv = (NhanVienItem) view.getCbNhanVien().getSelectedItem();
        view.fillNhanVienInfo(nv);
    }

    private void taoTaiKhoan() {
        String tenDangNhap = view.getTxtTenDangNhap().getText().trim();
        String matKhau = new String(view.getTxtMatKhau().getPassword()).trim();
        String nhapLaiMatKhau = new String(view.getTxtNhapLaiMatKhau().getPassword()).trim();
        String vaiTro = view.getCbVaiTro().getSelectedItem().toString();

        NhanVienItem nv = (NhanVienItem) view.getCbNhanVien().getSelectedItem();

        if (nv == null) {
            JOptionPane.showMessageDialog(view, "Không còn nhân viên nào chưa có tài khoản.");
            return;
        }

        if (tenDangNhap.isEmpty() || matKhau.isEmpty() || nhapLaiMatKhau.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin tài khoản.");
            return;
        }

        if (tenDangNhap.length() < 4) {
            JOptionPane.showMessageDialog(view, "Tên đăng nhập phải có ít nhất 4 ký tự.");
            return;
        }

        if (matKhau.length() < 6) {
            JOptionPane.showMessageDialog(view, "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!matKhau.equals(nhapLaiMatKhau)) {
            JOptionPane.showMessageDialog(view, "Mật khẩu nhập lại không khớp.");
            return;
        }

        if (dao.isTenDangNhapExists(tenDangNhap)) {
            JOptionPane.showMessageDialog(view, "Tên đăng nhập đã tồn tại.");
            return;
        }

        boolean result = dao.insertTaiKhoan(
                tenDangNhap,
                matKhau,
                vaiTro,
                nv.getMaNV()
        );

        if (result) {
            JOptionPane.showMessageDialog(view, "Tạo tài khoản thành công.");
            loadNhanVienChuaCoTaiKhoan();
            view.clearForm();
        } else {
            JOptionPane.showMessageDialog(view, "Tạo tài khoản thất bại.");
        }
    }
}