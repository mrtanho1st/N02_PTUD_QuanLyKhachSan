package entity;

import java.sql.Date;

public class HoaDon {
    private String maHD;
    private String maDDP;
    private String tenKH;
    private String tenNV;
    private Date ngayLap;
    private String maThue;
    private double tongTien;
    
    public HoaDon() {
    	
    }

    public HoaDon(String maHD, String maDDP, String tenKH, String tenNV,
                         Date ngayLap, String maThue, double tongTien) {
        this.maHD = maHD;
        this.maDDP = maDDP;
        this.tenKH = tenKH;
        this.tenNV = tenNV;
        this.ngayLap = ngayLap;
        this.maThue = maThue;
        this.tongTien = tongTien;
    }

    public String getMaHD() { return maHD; }
    public String getMaDDP() { return maDDP; }
    public String getTenKH() { return tenKH; }
    public String getTenNV() { return tenNV; }
    public Date getNgayLap() { return ngayLap; }
    public String getMaThue() { return maThue; }
    public double getTongTien() { return tongTien; }
}