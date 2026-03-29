package entity;

public class HoaDon {
    private String maHoaDon;
    private String maPhong;
    private String tenKhachHang;
    private String ngayDen;
    private String ngayDi;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon, String maPhong, String tenKhachHang, String ngayDen, String ngayDi) {
        this.maHoaDon = maHoaDon;
        this.maPhong = maPhong;
        this.tenKhachHang = tenKhachHang;
        this.ngayDen = ngayDen;
        this.ngayDi = ngayDi;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaPhong() {
        return maPhong;
    }
    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }
    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }
    public String getNgayDen() {
        return ngayDen;
    }

    public void setNgayDen(String ngayDen) {
        this.ngayDen = ngayDen;
    }

    public String getNgayDi() {
        return ngayDi;
    }

    public void setNgayDi(String ngayDi) {
        this.ngayDi = ngayDi;
    }
}