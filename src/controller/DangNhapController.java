package controller;

import javax.swing.JOptionPane;

import dao.DangNhap_Dao;
import dao.NhanVien_Dao;
import entity.NhanVien;
import entity.PhienDangNhap;
import entity.TaiKhoan;
import gui.DangNhap;
import gui.GiaoDienChinh;

public class DangNhapController {

    private final DangNhap view;
    private final DangNhap_Dao dangNhapDao;

    public DangNhapController(DangNhap view) {
        this.view = view;
        this.dangNhapDao = new DangNhap_Dao();
        registerEvents();
    }

    private void registerEvents() {
        view.getBtnDangNhap().addActionListener(e -> xuLyDangNhap());
        view.getBtnThoat().addActionListener(e -> System.exit(0));
        view.getChkHienMatKhau().addActionListener(e -> view.toggleHienMatKhau());
    }

    private void xuLyDangNhap() {
        String tenDangNhap = view.getTenDangNhap();
        String matKhau = view.getMatKhau();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            view.showMessage(
                    "Vui lòng nhập tên đăng nhập và mật khẩu!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);

            if (tenDangNhap.isEmpty()) {
                view.focusTenDangNhap();
            } else {
                view.focusMatKhau();
            }
            return;
        }

        TaiKhoan taiKhoan = dangNhapDao.dangNhap(tenDangNhap, matKhau);

        if (taiKhoan != null) {
            System.out.println("Dang nhap OK: " + taiKhoan.getTenDangNhap());
            System.out.println("maNV trong tai khoan = " + taiKhoan.getMaNV());

            NhanVien_Dao nhanVienDao = new NhanVien_Dao();
            NhanVien nhanVien = nhanVienDao.findById(taiKhoan.getMaNV());

            if (nhanVien == null) {
                view.showMessage(
                        "Đăng nhập thành công nhưng không tìm thấy nhân viên ứng với tài khoản này.",
                        "Lỗi dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            PhienDangNhap.setTaiKhoanDangNhap(taiKhoan);
            PhienDangNhap.setNhanVienDangNhap(nhanVien);

            System.out.println("Nhan vien dang nhap = " + nhanVien.getMaNV() + " - " + nhanVien.getHoTen());

            view.showMessage(
                    "Đăng nhập thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            GiaoDienChinh giaoDienChinh = new GiaoDienChinh();
            new GiaoDienChinhController(giaoDienChinh);
            giaoDienChinh.setVisible(true);

            view.dispose();
        } else {
            view.showMessage(
                    "Tên đăng nhập hoặc mật khẩu không chính xác!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            view.clearMatKhau();
            view.focusMatKhau();
        }
    }

}