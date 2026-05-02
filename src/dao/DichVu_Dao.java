package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.DichVu;

public class DichVu_Dao {

    public List<DichVu> findAll() {
//        List<DichVu> list = new ArrayList<>();
//
//        String sql = "SELECT maDV, tenDichVu, giaDichVu FROM DichVu";
//
//        try (
//                Connection con = ConnectDB.getConnection();
//                PreparedStatement ps = con.prepareStatement(sql);
//                ResultSet rs = ps.executeQuery()
//        ) {
//            while (rs.next()) {
//                list.add(new DichVu(
//                        rs.getString("maDV"),
//                        rs.getString("tenDichVu"),
//                        rs.getDouble("giaDichVu")
//                ));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
    	
    	
    	  return searchBaoBieu(null, null, null);
   
    }

    // Hàm search cũ cho các giao diện nhiều ô.
    // Giữ lại để không làm hư màn khác.
    public List<DichVu> search(
            Date tuNgay,
            Date denNgay,
            String maDV,
            String tenDichVu,
            Double giaDichVu
    ) {
        List<DichVu> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    dv.maDV, ");
        sql.append("    dv.tenDichVu, ");
        sql.append("    dv.giaDichVu, ");
        sql.append("    ISNULL(SUM(pdv.soLuong), 0) AS soLuotDung, ");
        sql.append("    ISNULL(SUM(pdv.soLuong * pdv.donGia), 0) AS doanhThu ");
        sql.append("FROM DichVu dv ");

        sql.append("LEFT JOIN PhieuDichVu pdv ON dv.maDV = pdv.maDV ");
        sql.append("LEFT JOIN CTHoaDonDichVu ctdv ON pdv.maPDV = ctdv.maPDV ");
        sql.append("LEFT JOIN HoaDon hd ON ctdv.maHD = hd.maHD ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD >= ?) ");
        }

        if (denNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD <= ?) ");
        }

        if (maDV != null && !maDV.isBlank()) {
            sql.append("AND dv.maDV LIKE ? ");
        }

        if (tenDichVu != null && !tenDichVu.isBlank()) {
            sql.append("AND dv.tenDichVu LIKE ? ");
        }

        if (giaDichVu != null) {
            sql.append("AND dv.giaDichVu = ? ");
        }

        sql.append("GROUP BY dv.maDV, dv.tenDichVu, dv.giaDichVu ");
        sql.append("ORDER BY doanhThu DESC, dv.maDV");

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

            if (maDV != null && !maDV.isBlank()) {
                ps.setString(index++, "%" + maDV.trim() + "%");
            }

            if (tenDichVu != null && !tenDichVu.isBlank()) {
                ps.setString(index++, "%" + tenDichVu.trim() + "%");
            }

            if (giaDichVu != null) {
                ps.setDouble(index++, giaDichVu);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDichVuBaoBieu(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean insert(DichVu dv) {
        String sql = """
                INSERT INTO DichVu(maDV, tenDichVu, giaDichVu)
                VALUES (?, ?, ?)
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, dv.getMaDV());
            ps.setString(2, dv.getTenDV());
            ps.setDouble(3, dv.getGia());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(DichVu dv) {
        String sql = """
                UPDATE DichVu
                SET tenDichVu = ?, giaDichVu = ?
                WHERE maDV = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, dv.getTenDV());
            ps.setDouble(2, dv.getGia());
            ps.setString(3, dv.getMaDV());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maDV) {
        String sql = """
                DELETE FROM DichVu
                WHERE maDV = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDV);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByMaDV(String maDV) {
        String sql = """
                SELECT maDV
                FROM DichVu
                WHERE maDV = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDV);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean dangDuocSuDung(String maDV) {
        String sql = """
                SELECT maDV
                FROM PhieuDichVu
                WHERE maDV = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDV);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Hàm mới cho Báo biểu Dịch vụ.
    // 1 ô từ khóa: tìm theo mã DV, tên dịch vụ, giá dịch vụ.
    // 2 ô ngày: lọc theo ngày lập hóa đơn.
    public List<DichVu> searchBaoBieu(Date tuNgay, Date denNgay, String tuKhoa) {
        List<DichVu> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    dv.maDV, ");
        sql.append("    dv.tenDichVu, ");
        sql.append("    dv.giaDichVu, ");
        sql.append("    ISNULL(SUM(pdv.soLuong), 0) AS soLuotDung, ");
        sql.append("    ISNULL(SUM(pdv.soLuong * pdv.donGia), 0) AS doanhThu ");
        sql.append("FROM DichVu dv ");

        sql.append("LEFT JOIN PhieuDichVu pdv ON dv.maDV = pdv.maDV ");
        sql.append("LEFT JOIN CTHoaDonDichVu ctdv ON pdv.maPDV = ctdv.maPDV ");
        sql.append("LEFT JOIN HoaDon hd ON ctdv.maHD = hd.maHD ");

        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD >= ?) ");
        }

        if (denNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD <= ?) ");
        }

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (");
            sql.append("dv.maDV LIKE ? ");
            sql.append("OR dv.tenDichVu LIKE ? ");
            sql.append("OR CAST(dv.giaDichVu AS NVARCHAR(50)) LIKE ? ");
            sql.append(") ");
        }

        sql.append("GROUP BY dv.maDV, dv.tenDichVu, dv.giaDichVu ");
        sql.append("ORDER BY doanhThu DESC, dv.maDV ");

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
                ps.setString(index++, kw); // maDV
                ps.setString(index++, kw); // tenDichVu
                ps.setString(index++, kw); // giaDichVu
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDichVuBaoBieu(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private DichVu mapDichVuBaoBieu(ResultSet rs) throws Exception {
        DichVu dv = new DichVu(
                rs.getString("maDV"),
                rs.getString("tenDichVu"),
                rs.getDouble("giaDichVu")
        );

        dv.setSoLuotDung(rs.getInt("soLuotDung"));
        dv.setDoanhThu(rs.getDouble("doanhThu"));

        return dv;
    }

    public List<Object[]> getThongKeDichVu(Date tuNgay, Date denNgay) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dv.maDV, dv.tenDichVu, dv.giaDichVu, ");
        sql.append("       ISNULL(SUM(pdv.soLuong), 0) AS soLuotSuDung, ");
        sql.append("       ISNULL(SUM(ctdv.thanhTien), 0) AS doanhThu ");
        sql.append("FROM DichVu dv ");
        sql.append("LEFT JOIN PhieuDichVu pdv ON dv.maDV = pdv.maDV ");
        sql.append("LEFT JOIN CTHoaDonDichVu ctdv ON pdv.maPDV = ctdv.maPDV ");
        sql.append("LEFT JOIN HoaDon hd ON ctdv.maHD = hd.maHD ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD >= ?) ");
        }

        if (denNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD <= ?) ");
        }

        sql.append("GROUP BY dv.maDV, dv.tenDichVu, dv.giaDichVu ");
        sql.append("ORDER BY doanhThu DESC, dv.maDV");

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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getString("maDV");
                    row[1] = rs.getString("tenDichVu");
                    row[2] = rs.getDouble("giaDichVu");
                    row[3] = rs.getInt("soLuotSuDung");
                    row[4] = rs.getDouble("doanhThu");
                    ds.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
}
