package entity;

public class DonDatPhong {
    private String maDDP;
    private String maKH;
    private String maNV;
    private String ngayDat;
    private String ngayNhanPhong;
    private String ngayTraPhong;

    public DonDatPhong() {
    }

    public DonDatPhong(String maDDP, String maKH, String maNV, String ngayDat, String ngayNhanPhong, String ngayTraPhong) {
        this.maDDP = maDDP;
        this.maKH = maKH;
        this.maNV = maNV;
        this.ngayDat = ngayDat;
        this.ngayNhanPhong = ngayNhanPhong;
        this.ngayTraPhong = ngayTraPhong;
    }

    public String getMaDDP() {
        return maDDP;
    }

    public void setMaDDP(String maDDP) {
        this.maDDP = maDDP;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(String ngayDat) {
        this.ngayDat = ngayDat;
    }

    public String getNgayNhanPhong() {
        return ngayNhanPhong;
    }

    public void setNgayNhanPhong(String ngayNhanPhong) {
        this.ngayNhanPhong = ngayNhanPhong;
    }

    public String getNgayTraPhong() {
        return ngayTraPhong;
    }

    public void setNgayTraPhong(String ngayTraPhong) {
        this.ngayTraPhong = ngayTraPhong;
    }
}
