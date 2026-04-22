package entity;

public class CheckInCheckOutItem {
    private String maPhong;
    private String loaiPhong;
    private double giaPhong;
    private String trangThaiPhong;

    private String maDDP;
    private String tinhTrang;

    private String maKH;
    private String tenKH;
    private String cccd;
    private String sdt;

    private String ngayNhan;
    private String ngayTra;
    private Double tienCoc;

    public CheckInCheckOutItem() {
    }

    public CheckInCheckOutItem(String maPhong, String loaiPhong, double giaPhong, String trangThaiPhong,
            String maDDP, String tinhTrang, String maKH, String tenKH, String cccd, String sdt,
            String ngayNhan, String ngayTra, Double tienCoc) {
        this.maPhong = maPhong;
        this.loaiPhong = loaiPhong;
        this.giaPhong = giaPhong;
        this.trangThaiPhong = trangThaiPhong;
        this.maDDP = maDDP;
        this.tinhTrang = tinhTrang;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.cccd = cccd;
        this.sdt = sdt;
        this.ngayNhan = ngayNhan;
        this.ngayTra = ngayTra;
        this.tienCoc = tienCoc;
    }

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public String getLoaiPhong() { return loaiPhong; }
    public void setLoaiPhong(String loaiPhong) { this.loaiPhong = loaiPhong; }

    public double getGiaPhong() { return giaPhong; }
    public void setGiaPhong(double giaPhong) { this.giaPhong = giaPhong; }

    public String getTrangThaiPhong() { return trangThaiPhong; }
    public void setTrangThaiPhong(String trangThaiPhong) { this.trangThaiPhong = trangThaiPhong; }

    public String getMaDDP() { return maDDP; }
    public void setMaDDP(String maDDP) { this.maDDP = maDDP; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(String ngayNhan) { this.ngayNhan = ngayNhan; }

    public String getNgayTra() { return ngayTra; }
    public void setNgayTra(String ngayTra) { this.ngayTra = ngayTra; }

    public Double getTienCoc() { return tienCoc; }
    public void setTienCoc(Double tienCoc) { this.tienCoc = tienCoc; }
}