package entity;

public enum LoaiPhong {
	STANDARD("Standard", "Phòng đơn"),
	SUPERIOR("Superior", "Phòng đôi"),
	DELUXE("Deluxe", "Phòng gia đình"),
	SUITE("Suite", "Phòng suite"),
	FAMILY("Family", "Phòng gia đình");

    private final String giaTriTrongDB;
    private final String tenLoai;

    LoaiPhong(String giaTriTrongDB, String tenLoai) {
        this.giaTriTrongDB = giaTriTrongDB;
        this.tenLoai = tenLoai;
    }

    public String getGiaTriTrongDB() {
        return giaTriTrongDB;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public static LoaiPhong fromDatabaseValue(String dbValue) {
        for (LoaiPhong loai : LoaiPhong.values()) {
            if (loai.giaTriTrongDB.equalsIgnoreCase(dbValue)) {
                return loai;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại phòng tương ứng: " + dbValue);
    }

    @Override
    public String toString() {
        return tenLoai;
    }
}