package entity;

public enum ChucVu {
    QUAN_LY("Nhân Viên Quản Lý"),
    NHAN_VIEN("Nhân Viên Lễ Tân");

    private String tenChucVu;

    ChucVu(String tenChucVu) {
        this.tenChucVu = tenChucVu;
    }

    public String getTenChucVu() {
        return tenChucVu;
    }
}
