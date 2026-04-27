package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.KhuyenMai;

public class KhuyenMai_Dao {

	public List<KhuyenMai> findAll() {
        List<KhuyenMai> list = new ArrayList<>();

        String sql = """
                SELECT maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc
                FROM KhuyenMai
                ORDER BY ngayBatDau DESC, maKM DESC
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(mapKhuyenMai(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

	public List<KhuyenMai> search(
	        java.sql.Date tuNgay,
	        java.sql.Date denNgay,
	        String maKM,
	        String tenKM,
	        Double giaTri,
	        String trangThai
	) {
	    List<KhuyenMai> list = new ArrayList<>();

	    StringBuilder sql = new StringBuilder();

	    sql.append("SELECT maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc ");
	    sql.append("FROM KhuyenMai ");
	    sql.append("WHERE 1 = 1 ");

	    if (tuNgay != null) {
	        sql.append("AND ngayBatDau >= ? ");
	    }

	    if (denNgay != null) {
	        sql.append("AND ngayBatDau <= ? ");
	    }

	    if (maKM != null && !maKM.isBlank()) {
	        sql.append("AND maKM LIKE ? ");
	    }

	    if (tenKM != null && !tenKM.isBlank()) {
	        sql.append("AND tenKhuyenMai LIKE ? ");
	    }

	    if (giaTri != null) {
	        sql.append("AND giaTri = ? ");
	    }

	    if ("Còn sử dụng".equalsIgnoreCase(trangThai)) {
	        sql.append("AND ngayKetThuc >= CAST(GETDATE() AS DATE) ");
	    } else if ("Hết hạn".equalsIgnoreCase(trangThai)) {
	        sql.append("AND ngayKetThuc < CAST(GETDATE() AS DATE) ");
	    }

	    sql.append("ORDER BY ngayBatDau DESC, maKM DESC ");

	    try (
	            Connection con = ConnectDB.getConnection();
	            PreparedStatement ps = con.prepareStatement(sql.toString())
	    ) {
	        int index = 1;

	        if (tuNgay != null) {
	            ps.setDate(index++, tuNgay);
	        }

	        if (denNgay != null) {
	            ps.setDate(index++, denNgay);
	        }

	        if (maKM != null && !maKM.isBlank()) {
	            ps.setString(index++, "%" + maKM.trim() + "%");
	        }

	        if (tenKM != null && !tenKM.isBlank()) {
	            ps.setString(index++, "%" + tenKM.trim() + "%");
	        }

	        if (giaTri != null) {
	            ps.setDouble(index++, giaTri);
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapKhuyenMai(rs));
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

    public boolean insert(KhuyenMai km) {
        String sql = """
                INSERT INTO KhuyenMai(maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setDouble(3, km.getGiaTri());
            ps.setDate(4, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(5, Date.valueOf(km.getNgayKetThuc()));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(KhuyenMai km) {
        String sql = """
                UPDATE KhuyenMai
                SET tenKhuyenMai = ?, giaTri = ?, ngayBatDau = ?, ngayKetThuc = ?
                WHERE maKM = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, km.getTenKM());
            ps.setDouble(2, km.getGiaTri());
            ps.setDate(3, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(4, Date.valueOf(km.getNgayKetThuc()));
            ps.setString(5, km.getMaKM());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maKM) {
        String sql = """
                DELETE FROM KhuyenMai
                WHERE maKM = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maKM);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByMaKM(String maKM) {
        String sql = """
                SELECT maKM
                FROM KhuyenMai
                WHERE maKM = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maKM);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    // Hàm mới cho Báo biểu Khuyến mãi.
    // Báo biểu có:
    // - 1 ô từ khóa: tìm theo mã KM hoặc tên khuyến mãi
    // - 2 ô ngày: lọc ngày bắt đầu và ngày kết thúc
    // - 1 combo khoảng giá trị: Dưới 10%, Từ 10% - 30%, Trên 30%
    public List<KhuyenMai> searchBaoBieu(
            Date tuNgay,
            Date denNgay,
            String tuKhoa,
            String khoangGiaTri
    ) {
        List<KhuyenMai> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc ");
        sql.append("FROM KhuyenMai ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND ngayBatDau >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND ngayKetThuc <= ? ");
        }

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (");
            sql.append("maKM LIKE ? ");
            sql.append("OR tenKhuyenMai LIKE ? ");
            sql.append(") ");
        }

        themDieuKienMucGiam(sql, khoangGiaTri);

        sql.append("ORDER BY ngayBatDau DESC, maKM DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (tuNgay != null) {
                ps.setDate(index++, tuNgay);
            }

            if (denNgay != null) {
                ps.setDate(index++, denNgay);
            }

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapKhuyenMai(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    private void themDieuKienTrangThai(StringBuilder sql, String trangThai) {
        if (trangThai == null || trangThai.isBlank() || "Tất cả".equalsIgnoreCase(trangThai)) {
            return;
        }

        if ("Đang áp dụng".equalsIgnoreCase(trangThai)) {
            sql.append("AND CAST(GETDATE() AS DATE) BETWEEN ngayBatDau AND ngayKetThuc ");
        } else if ("Hết hạn".equalsIgnoreCase(trangThai)) {
            sql.append("AND ngayKetThuc < CAST(GETDATE() AS DATE) ");
        } else if ("Sắp áp dụng".equalsIgnoreCase(trangThai)) {
            sql.append("AND ngayBatDau > CAST(GETDATE() AS DATE) ");
        }
    }

    private void themDieuKienMucGiam(StringBuilder sql, String mucGiam) {
        if (mucGiam == null || mucGiam.isBlank() || "Tất cả".equalsIgnoreCase(mucGiam)) {
            return;
        }

        if ("Dưới 10%".equalsIgnoreCase(mucGiam)) {
            sql.append("AND giaTri < 10 ");
        } else if ("Từ 10% - 30%".equalsIgnoreCase(mucGiam)) {
            sql.append("AND giaTri BETWEEN 10 AND 30 ");
        } else if ("Trên 30%".equalsIgnoreCase(mucGiam)) {
            sql.append("AND giaTri > 30 ");
        }
    }

    private KhuyenMai mapKhuyenMai(ResultSet rs) throws Exception {
        Date dbNgayBatDau = rs.getDate("ngayBatDau");
        Date dbNgayKetThuc = rs.getDate("ngayKetThuc");

        return new KhuyenMai(
                rs.getString("maKM"),
                rs.getString("tenKhuyenMai"),
                rs.getDouble("giaTri"),
                dbNgayBatDau == null ? null : dbNgayBatDau.toLocalDate(),
                dbNgayKetThuc == null ? null : dbNgayKetThuc.toLocalDate()
        );
    }
}