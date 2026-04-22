package entity;

import java.sql.Date;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private Date ngaySinh;
    private String sdt;
    private String email;
    private String gioiTinh;
    private Date ngayBatDauVaoLam;
    private String trangThaiLamViec;
    private String diaChi;
    private String caLamViec;
    private String viTriCongViec;

    public NhanVien() {
    }

    public NhanVien(String maNV) {
        this.maNV = maNV;
    }

    public NhanVien(String maNV, String hoTen, Date ngaySinh, String sdt, String email,
                    String gioiTinh, Date ngayBatDauVaoLam, String trangThaiLamViec,
                    String diaChi, String caLamViec, String viTriCongViec) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.ngayBatDauVaoLam = ngayBatDauVaoLam;
        this.trangThaiLamViec = trangThaiLamViec;
        this.diaChi = diaChi;
        this.caLamViec = caLamViec;
        this.viTriCongViec = viTriCongViec;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Date getNgayBatDauVaoLam() {
        return ngayBatDauVaoLam;
    }

    public void setNgayBatDauVaoLam(Date ngayBatDauVaoLam) {
        this.ngayBatDauVaoLam = ngayBatDauVaoLam;
    }

    public String getTrangThaiLamViec() {
        return trangThaiLamViec;
    }

    public void setTrangThaiLamViec(String trangThaiLamViec) {
        this.trangThaiLamViec = trangThaiLamViec;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(String caLamViec) {
        this.caLamViec = caLamViec;
    }

    public String getViTriCongViec() {
        return viTriCongViec;
    }

    public void setViTriCongViec(String viTriCongViec) {
        this.viTriCongViec = viTriCongViec;
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNV='" + maNV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", sdt='" + sdt + '\'' +
                ", email='" + email + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", ngayBatDauVaoLam=" + ngayBatDauVaoLam +
                ", trangThaiLamViec='" + trangThaiLamViec + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", caLamViec='" + caLamViec + '\'' +
                ", viTriCongViec='" + viTriCongViec + '\'' +
                '}';
    }
}