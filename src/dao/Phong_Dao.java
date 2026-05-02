package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
//import entity.LoaiPhong;
import entity.Phong;

public class Phong_Dao {

    public List<Phong> findAll() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong FROM Phong";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(mapPhong(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Phong findById(String maPhong) {
        String sql = "SELECT maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong "
                   + "FROM Phong WHERE maPhong = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maPhong);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPhong(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insert(Phong phong) {
        String sql = "INSERT INTO Phong(maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, phong.getMaPhong());
            ps.setString(2, phong.getLoaiPhong());
            ps.setInt(3, phong.getSoNguoiToiDa());
            ps.setDouble(4, phong.getGiaPhong());
            ps.setString(5, phong.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Phong phong) {
        String sql = "UPDATE Phong "
                   + "SET loaiPhong = ?, soNguoiToiDa = ?, giaPhong = ?, trangThaiPhong = ? "
                   + "WHERE maPhong = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, phong.getLoaiPhong());
            ps.setInt(2, phong.getSoNguoiToiDa());
            ps.setDouble(3, phong.getGiaPhong());
            ps.setString(4, phong.getTrangThai());
            ps.setString(5, phong.getMaPhong());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maPhong) {
        String sql = "DELETE FROM Phong WHERE maPhong = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Hàm search cũ cho các giao diện nhiều ô.
    // Giữ lại để không làm hư màn Quản lý phòng.
    public List<Phong> search(
            String maPhong,
            Integer soNguoiCanTim,
            Double giaPhongCanTim,
            String loaiPhong,
            String trangThai
    ) {
        List<Phong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong ");
        sql.append("FROM Phong ");
        sql.append("WHERE 1 = 1 ");

        if (maPhong != null && !maPhong.isBlank()) {
            sql.append("AND maPhong LIKE ? ");
        }

        if (soNguoiCanTim != null) {
            sql.append("AND soNguoiToiDa = ? ");
        }

        if (giaPhongCanTim != null) {
            sql.append("AND giaPhong = ? ");
        }

        if (loaiPhong != null && !loaiPhong.isBlank() && !"Tất cả".equalsIgnoreCase(loaiPhong)) {
            sql.append("AND loaiPhong = ? ");
        }

        if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            sql.append("AND trangThaiPhong = ? ");
        }

        sql.append("ORDER BY maPhong");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maPhong != null && !maPhong.isBlank()) {
                ps.setString(index++, "%" + maPhong.trim() + "%");
            }

            if (soNguoiCanTim != null) {
                ps.setInt(index++, soNguoiCanTim);
            }

            if (giaPhongCanTim != null) {
                ps.setDouble(index++, giaPhongCanTim);
            }

            if (loaiPhong != null && !loaiPhong.isBlank() && !"Tất cả".equalsIgnoreCase(loaiPhong)) {
                ps.setString(index++, loaiPhong.trim());
            }

            if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                ps.setString(index++, trangThai.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhong(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Hàm mới cho Báo biểu Phòng.
    // Giao diện báo biểu chỉ có 1 ô từ khóa:
    // tìm theo mã phòng, loại phòng, giá phòng, số người tối đa.
    public List<Phong> searchBaoBieu(String tuKhoa, String loaiPhong, String trangThai) {
        List<Phong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong ");
        sql.append("FROM Phong ");
        sql.append("WHERE 1 = 1 ");

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (");
            sql.append("maPhong LIKE ? ");
            sql.append("OR loaiPhong LIKE ? ");
            sql.append("OR CAST(soNguoiToiDa AS NVARCHAR(20)) LIKE ? ");
            sql.append("OR CAST(giaPhong AS NVARCHAR(50)) LIKE ? ");
            sql.append(") ");
        }

        if (loaiPhong != null && !loaiPhong.isBlank() && !"Tất cả".equalsIgnoreCase(loaiPhong)) {
            sql.append("AND loaiPhong = ? ");
        }

        if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            sql.append("AND trangThaiPhong = ? ");
        }

        sql.append("ORDER BY maPhong");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            if (loaiPhong != null && !loaiPhong.isBlank() && !"Tất cả".equalsIgnoreCase(loaiPhong)) {
                ps.setString(index++, loaiPhong.trim());
            }

            if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                ps.setString(index++, trangThai.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhong(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<String> getAllLoaiPhongFromPhong() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT DISTINCT loaiPhong FROM Phong";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(rs.getString("loaiPhong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<Phong> findPhongTheoNgay(java.sql.Date tuNgay, java.sql.Date denNgay) {
        List<Phong> list = new ArrayList<>();

        String sql = """
            SELECT 
                p.*,
                CASE 
                    WHEN EXISTS (
                        SELECT 1 
                        FROM CTDonDatPhong ct
                        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP
                        WHERE ct.maPhong = p.maPhong
                        AND NOT (ddp.ngayTra < ? OR ddp.ngayNhan > ?)
                        AND ddp.tinhTrang = N'Đã nhận'
                    ) THEN N'Đang sử dụng'

                    WHEN EXISTS (
                        SELECT 1 
                        FROM CTDonDatPhong ct
                        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP
                        WHERE ct.maPhong = p.maPhong
                        AND NOT (ddp.ngayTra < ? OR ddp.ngayNhan > ?)
                        AND ddp.tinhTrang = N'Đã đặt'
                    ) THEN N'Đã đặt'

                    ELSE N'Trống'
                END AS trangThaiTheoNgay

            FROM Phong p
        """;

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            ps.setDate(3, tuNgay);
            ps.setDate(4, denNgay);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Phong p = new Phong(
                    rs.getString("maPhong"),
                    rs.getString("loaiPhong"),
                    rs.getInt("soNguoiToiDa"),
                    rs.getDouble("giaPhong"),
                    rs.getString("trangThaiTheoNgay") // 🔥 QUAN TRỌNG
                );

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Phong mapPhong(ResultSet rs) throws Exception {
        return new Phong(
                rs.getString("maPhong"),
                rs.getString("loaiPhong"),
                rs.getInt("soNguoiToiDa"),
                rs.getDouble("giaPhong"),
                rs.getString("trangThaiPhong")
        );
    }
}