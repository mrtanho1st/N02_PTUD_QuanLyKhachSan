package entity;



import entity.NhanVien;
import entity.TaiKhoan;

public class PhienDangNhap {
    private static TaiKhoan taiKhoanDangNhap;
    private static NhanVien nhanVienDangNhap;

    public static TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    public static void setTaiKhoanDangNhap(TaiKhoan taiKhoan) {
    	PhienDangNhap.taiKhoanDangNhap = taiKhoan;
    }

    public static NhanVien getNhanVienDangNhap() {
        return nhanVienDangNhap;
    }

    public static void setNhanVienDangNhap(NhanVien nhanVien) {
    	PhienDangNhap.nhanVienDangNhap = nhanVien;
    }

    public static void clear() {
        taiKhoanDangNhap = null;
        nhanVienDangNhap = null;
    }

    public static boolean isLoggedIn() {
        return taiKhoanDangNhap != null;
    }
}