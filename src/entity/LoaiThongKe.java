package entity;

public enum LoaiThongKe {
    DOANH_THU_THEO_THOI_GIAN("Doanh thu theo thời gian"),
    DOANH_THU_THEO_PHONG("Doanh thu theo phòng"),
    DOANH_THU_THEO_KHACH_HANG("Doanh thu theo khách hàng"),
    THONG_KE_DON_DAT_PHONG("Thống kê đơn đặt phòng"),
    THONG_KE_HOA_DON("Thống kê hóa đơn"),
    THONG_KE_DICH_VU("Thống kê dịch vụ"),
    PHONG_DAT_NHIEU_NHAT("Phòng đặt nhiều nhất"),
    KHACH_HANG_DIEM_CAO_NHAT("Khách hàng điểm cao nhất"),
    THONG_KE_THEO_NHAN_VIEN("Thống kê theo nhân viên");

    private final String tenHienThi;

    LoaiThongKe(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
