package entity;

public enum LoaiPhong {
    PHONG_DON("Phòng đơn"),
    PHONG_DOI("Phòng đôi"),
    PHONG_GIA_DINH("Phòng gia đình"),
    PHONG_SUITE("Phòng suite");

    private final String tenLoai;

    LoaiPhong(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }
}
