package entity;

public enum LoaiBaoBieu {
    KHACH_HANG("Báo biểu khách hàng"),
    NHAN_VIEN("Báo biểu nhân viên"),
    PHONG("Báo biểu phòng"),
    KHUYEN_MAI("Báo biểu khuyến mãi"),
    DICH_VU("Báo biểu dịch vụ"),
    DON_DAT_PHONG("Báo biểu đơn đặt phòng"),
    HOA_DON("Báo biểu hóa đơn");

    private final String tenHienThi;

    LoaiBaoBieu(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}