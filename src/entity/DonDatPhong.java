package entity;

public class DonDatPhong {
    private String maPhong;
    private String loaiPhong;
    private int soNguoiToiDa;
    private double giaPhong;
    private String trangThaiPhong;

    private String maDDP;
    private String tinhTrangDat;
    private String maKH;
    private String tenKH;
    private String ngayNhan;
    private String ngayTra;
    private Double tienCoc;

    public DonDatPhong() {
    }

    public DonDatPhong(String maPhong, String loaiPhong, int soNguoiToiDa, double giaPhong,
                       String trangThaiPhong, String maDDP, String tinhTrangDat, String maKH,
                       String tenKH, String ngayNhan, String ngayTra, Double tienCoc) {
        this.maPhong = maPhong;
        this.loaiPhong = loaiPhong;
        this.soNguoiToiDa = soNguoiToiDa;
        this.giaPhong = giaPhong;
        this.trangThaiPhong = trangThaiPhong;
        this.maDDP = maDDP;
        this.tinhTrangDat = tinhTrangDat;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.ngayNhan = ngayNhan;
        this.ngayTra = ngayTra;
        this.tienCoc = tienCoc;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public int getSoNguoiToiDa() {
        return soNguoiToiDa;
    }

    public void setSoNguoiToiDa(int soNguoiToiDa) {
        this.soNguoiToiDa = soNguoiToiDa;
    }

    public double getGiaPhong() {
        return giaPhong;
    }

    public void setGiaPhong(double giaPhong) {
        this.giaPhong = giaPhong;
    }

    public String getTrangThaiPhong() {
        return trangThaiPhong;
    }

    public void setTrangThaiPhong(String trangThaiPhong) {
        this.trangThaiPhong = trangThaiPhong;
    }

    public String getMaDDP() {
        return maDDP;
    }

    public void setMaDDP(String maDDP) {
        this.maDDP = maDDP;
    }

    public String getTinhTrangDat() {
        return tinhTrangDat;
    }

    public void setTinhTrangDat(String tinhTrangDat) {
        this.tinhTrangDat = tinhTrangDat;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getNgayNhan() {
        return ngayNhan;
    }

    public void setNgayNhan(String ngayNhan) {
        this.ngayNhan = ngayNhan;
    }

    public String getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(String ngayTra) {
        this.ngayTra = ngayTra;
    }

    public Double getTienCoc() {
        return tienCoc;
    }

    public void setTienCoc(Double tienCoc) {
        this.tienCoc = tienCoc;
    }

    // Alias cho báo biểu/controller
    public String getHoTen() {
        return tenKH;
    }

    public String getTinhTrang() {
        return tinhTrangDat;
    }

    // Alias nếu chỗ nào trong code gọi tên khách hàng
    public String getTenKhachHang() {
        return tenKH;
    }

    public String getTrangThaiHienThi() {
        if (tinhTrangDat != null && !tinhTrangDat.isBlank()) {
            if ("Đã nhận".equalsIgnoreCase(tinhTrangDat)) {
                return "Đang sử dụng";
            }

            return tinhTrangDat;
        }

        return trangThaiPhong;
    }

    public boolean isPhongTrong() {
        return "Trống".equalsIgnoreCase(getTrangThaiHienThi());
    }

    public boolean isDaDat() {
        return !isPhongTrong();
    }
}