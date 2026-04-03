package entity;

public class CTHD {
    private String maHD;
    private String maPhong;
    private int soNgayThue;

    public CTHD() {
    }

    public CTHD(String maHD, String maPhong, int soNgayThue) {
        this.maHD = maHD;
        this.maPhong = maPhong;
        this.soNgayThue = soNgayThue;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public int getSoNgayThue() {
        return soNgayThue;
    }

    public void setSoNgayThue(int soNgayThue) {
        this.soNgayThue = soNgayThue;
    }
}
