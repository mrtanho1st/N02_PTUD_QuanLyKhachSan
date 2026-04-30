package controller;

import javax.swing.JOptionPane;

import dao.TaiKhoan_Dao;
import entity.NhanVien;
import entity.PhienDangNhap;
import entity.TaiKhoan;
import gui.TaiKhoanDialog;

public class TaiKhoanController {

    private final TaiKhoanDialog view;
    private final TaiKhoan_Dao taiKhoanDao;

    public TaiKhoanController(TaiKhoanDialog view) {
        this.view = view;
        this.taiKhoanDao = new TaiKhoan_Dao();

        registerEvents();
    }

    private void registerEvents() {
        view.getBtnHienDoiMatKhau().addActionListener(e -> view.hienThiFormDoiMatKhau(true));

        view.getBtnHuyDoiMatKhau().addActionListener(e -> view.hienThiFormDoiMatKhau(false));

        view.getBtnLuuMatKhau().addActionListener(e -> xuLyDoiMatKhau());

        view.getBtnDong().addActionListener(e -> view.dispose());
    }

    public void loadThongTinTaiKhoan() {
        TaiKhoan taiKhoan = PhienDangNhap.getTaiKhoanDangNhap();
        NhanVien nhanVien = PhienDangNhap.getNhanVienDangNhap();

        if (taiKhoan == null) {
            view.showMessage(
                    "Không tìm thấy tài khoản đang đăng nhập.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            view.dispose();
            return;
        }

        view.getLblTenDangNhap().setText(toText(taiKhoan.getTenDangNhap()));
        view.getLblVaiTro().setText(toText(taiKhoan.getVaiTro()));
        view.getLblMaNV().setText(toText(taiKhoan.getMaNV()));

        if (nhanVien != null) {
            view.getLblTenNV().setText(toText(nhanVien.getHoTen()));
            view.getLblEmail().setText(toText(nhanVien.getEmail()));
            view.getLblSDT().setText(toText(nhanVien.getSdt()));
            view.getLblViTri().setText(toText(nhanVien.getViTriCongViec()));
        } else {
            view.getLblTenNV().setText("");
            view.getLblEmail().setText("");
            view.getLblSDT().setText("");
            view.getLblViTri().setText("");
        }
    }

    private void xuLyDoiMatKhau() {
        TaiKhoan taiKhoanDangNhap = PhienDangNhap.getTaiKhoanDangNhap();

        if (taiKhoanDangNhap == null) {
            view.showMessage(
                    "Không tìm thấy tài khoản đang đăng nhập.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String tenDangNhap = taiKhoanDangNhap.getTenDangNhap();

        String matKhauHienTai = view.getMatKhauHienTai();
        String matKhauMoi = view.getMatKhauMoi();
        String xacNhanMatKhauMoi = view.getXacNhanMatKhauMoi();

        if (matKhauHienTai.isBlank() || matKhauMoi.isBlank() || xacNhanMatKhauMoi.isBlank()) {
            view.showMessage(
                    "Vui lòng nhập đầy đủ thông tin.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (matKhauMoi.length() < 6) {
            view.showMessage(
                    "Mật khẩu mới phải có ít nhất 6 ký tự.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
            view.showMessage(
                    "Mật khẩu xác nhận không khớp.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (matKhauMoi.equals(matKhauHienTai)) {
            view.showMessage(
                    "Mật khẩu mới không được trùng mật khẩu hiện tại.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean matKhauCuDung = taiKhoanDao.kiemTraMatKhau(tenDangNhap, matKhauHienTai);

        if (!matKhauCuDung) {
            view.showMessage(
                    "Mật khẩu hiện tại không chính xác.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean thanhCong = taiKhoanDao.doiMatKhau(tenDangNhap, matKhauMoi);

        if (thanhCong) {
            taiKhoanDangNhap.setMatKhau(matKhauMoi);
            PhienDangNhap.setTaiKhoanDangNhap(taiKhoanDangNhap);

            view.showMessage(
                    "Đổi mật khẩu thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            view.hienThiFormDoiMatKhau(false);
        } else {
            view.showMessage(
                    "Đổi mật khẩu thất bại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String toText(Object value) {
        return value == null ? "" : value.toString();
    }
}