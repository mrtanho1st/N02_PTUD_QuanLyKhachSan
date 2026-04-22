package entity;

public class DonDatPhong {
    private String maPhong;
    private String loaiPhong;
    private String trangThaiPhong;

    private String maDDP;
    private String tinhTrangDat;
    private String maKH;
    private String tenKH;
    private String ngayNhan;
    private String ngayTra;
    private Double tienCoc;

    public DonDatPhong(String maPhong, String loaiPhong, String trangThaiPhong,
                        String maDDP, String tinhTrangDat, String maKH,
                        String tenKH, String ngayNhan, String ngayTra, Double tienCoc) {
        this.maPhong = maPhong;
        this.loaiPhong = loaiPhong;
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

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public String getTrangThaiPhong() {
        return trangThaiPhong;
    }

    public String getMaDDP() {
        return maDDP;
    }

    public String getTinhTrangDat() {
        return tinhTrangDat;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public String getNgayNhan() {
        return ngayNhan;
    }

    public String getNgayTra() {
        return ngayTra;
    }

    public Double getTienCoc() {
        return tienCoc;
    }

    public boolean isPhongTrong() {
        return "Trống".equalsIgnoreCase(trangThaiPhong);
    }

    public boolean isDaDat() {
        return !isPhongTrong();
    }
}