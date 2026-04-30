package entity;

public class Thue {
    private String maThue;
    private String tenThue;
    private String trangThai;
    private double tyLeThue;
    private String moTa;

    public Thue() {
    }

    public Thue(String maThue, String tenThue, String trangThai, double tyLeThue, String moTa) {
        this.maThue = maThue;
        this.tenThue = tenThue;
        this.trangThai = trangThai;
        this.tyLeThue = tyLeThue;
        this.moTa = moTa;
    }

    public String getMaThue() {
        return maThue;
    }

    public void setMaThue(String maThue) {
        this.maThue = maThue;
    }

    public String getTenThue() {
        return tenThue;
    }

    public void setTenThue(String tenThue) {
        this.tenThue = tenThue;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public double getTyLeThue() {
        return tyLeThue;
    }

    public void setTyLeThue(double tyLeThue) {
        this.tyLeThue = tyLeThue;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
