package entity;

public class NhanVien {
    private String maNV;
    private String tenNV;
    private String sdt;
    private String diaChi;
    private ChucVu chucVu;

    public NhanVien(String maNV, String tenNV, String sdt, String diaChi, ChucVu chucVu) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
    }

    public NhanVien() {
    }

}
