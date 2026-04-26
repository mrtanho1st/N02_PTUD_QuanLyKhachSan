package entity;

public class DichVu {
    private String maDV;
    private String tenDV;
    private double gia;
    private int soLuotDung;
    private double doanhThu;

    public DichVu() {
    }

    public DichVu(String maDV, String tenDV, double gia) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.gia = gia;
    }

    public DichVu(String maDV, String tenDV, double gia, int soLuotDung, double doanhThu) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.gia = gia;
        this.soLuotDung = soLuotDung;
        this.doanhThu = doanhThu;
    }

    public String getMaDV() {
        return maDV;
    }

    public void setMaDV(String maDV) {
        this.maDV = maDV;
    }

    public String getTenDV() {
        return tenDV;
    }

    public void setTenDV(String tenDV) {
        this.tenDV = tenDV;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public int getSoLuotDung() {
        return soLuotDung;
    }

    public void setSoLuotDung(int soLuotDung) {
        this.soLuotDung = soLuotDung;
    }

    public double getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    }
}