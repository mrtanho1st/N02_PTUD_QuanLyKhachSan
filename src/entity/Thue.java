package entity;

public class Thue {

    private String maThue;
    private String tenThue;
    private String moTa;
    private double tyLeThue;
    private String trangThai;

    public Thue() {
    }

    public Thue(String maThue, String tenThue, String moTa, double tyLeThue, String trangThai) {
        this.maThue = maThue;
        this.tenThue = tenThue;
        this.moTa = moTa;
        this.tyLeThue = tyLeThue;
        this.trangThai = trangThai;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public double getTyLeThue() {
        return tyLeThue;
    }

    public void setTyLeThue(double tyLeThue) {
        this.tyLeThue = tyLeThue;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

}
