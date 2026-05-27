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
        String sql = "SELECT p.maPhong, p.loaiPhong, p.soNguoiToiDa, p.giaPhong, "
                + "CASE "
                + "    WHEN p.trangThaiPhong = N'Bảo trì' THEN N'Bảo trì' "
                + "    WHEN EXISTS ( "
                + "        SELECT 1 "
                + "        FROM CTDonDatPhong ct "
                + "        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP "
                + "        WHERE ct.maPhong = p.maPhong "
                + "        AND ddp.ngayTra < GETDATE() "
                + "        AND ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận') "
                + "    ) THEN N'Quá hạn' "
                + "    WHEN EXISTS ( "
                + "        SELECT 1 "
                + "        FROM CTDonDatPhong ct "
                + "        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP "
                + "        WHERE ct.maPhong = p.maPhong "
                + "        AND ddp.tinhTrang = N'Đã nhận' "
                + "    ) THEN N'Đang sử dụng' "
                + "    WHEN EXISTS ( "
                + "        SELECT 1 "
                + "        FROM CTDonDatPhong ct "
                + "        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP "
                + "        WHERE ct.maPhong = p.maPhong "
                + "        AND ddp.tinhTrang = N'Đã đặt' "
                + "    ) THEN N'Đã đặt' "
                + "    ELSE N'Trống' "
                + "END AS trangThaiPhong "
                + "FROM Phong p";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapPhong(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Phong findById(String maPhong) {
        String sql = "SELECT p.maPhong, p.loaiPhong, p.soNguoiToiDa, p.giaPhong, "
                + "CASE "
                + "    WHEN p.trangThaiPhong = N'Bảo trì' THEN N'Bảo trì' "
                + "    WHEN EXISTS ( "
                + "        SELECT 1 "
                + "        FROM CTDonDatPhong ct "
                + "        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP "
                + "        WHERE ct.maPhong = p.maPhong "
                + "        AND ddp.ngayTra < GETDATE() "
                + "        AND ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận') "
                + "    ) THEN N'Quá hạn' "
                + "    WHEN EXISTS ( "
                + "        SELECT 1 "
                + "        FROM CTDonDatPhong ct "
                + "        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP "
                + "        WHERE ct.maPhong = p.maPhong "
                + "        AND ddp.tinhTrang = N'Đã nhận' "
                + "    ) THEN N'Đang sử dụng' "
                + "    WHEN EXISTS ( "
                + "        SELECT 1 "
                + "        FROM CTDonDatPhong ct "
                + "        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP "
                + "        WHERE ct.maPhong = p.maPhong "
                + "        AND ddp.tinhTrang = N'Đã đặt' "
                + "    ) THEN N'Đã đặt' "
                + "    ELSE N'Trống' "
                + "END AS trangThaiPhong "
                + "FROM Phong p WHERE p.maPhong = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
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
                PreparedStatement ps = con.prepareStatement(sql)) {
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
                PreparedStatement ps = con.prepareStatement(sql)) {
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
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
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
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("loaiPhong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Phong> search(
            String maPhong,
            Integer soNguoiCanTim,
            Double giaPhongCanTim,
            String loaiPhong,
            String trangThai,
            java.sql.Date tuNgay,
            java.sql.Date denNgay) {
        List<Phong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("""
                    SELECT
                        p.maPhong, p.loaiPhong, p.soNguoiToiDa, p.giaPhong,

                        CASE
                            WHEN p.trangThaiPhong = N'Bảo trì' THEN N'Bảo trì'
                            WHEN EXISTS (
                                SELECT 1
                                FROM CTDonDatPhong ct
                                JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP
                                WHERE ct.maPhong = p.maPhong
                                AND ddp.ngayTra < GETDATE()
                                AND ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận')
                            ) THEN N'Quá hạn'

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
                        END AS trangThaiPhong

                    FROM Phong p
                    WHERE 1=1
                """);

        // 🔎 FILTER
        if (maPhong != null && !maPhong.isBlank()) {
            sql.append(" AND p.maPhong LIKE ? ");
        }

        if (soNguoiCanTim != null) {
            sql.append(" AND p.soNguoiToiDa = ? ");
        }

        if (giaPhongCanTim != null) {
            sql.append(" AND p.giaPhong = ? ");
        }

        if (loaiPhong != null && !loaiPhong.isBlank() && !"Tất cả".equalsIgnoreCase(loaiPhong)) {
            sql.append(" AND p.loaiPhong = ? ");
        }

        // ⚠️ filter theo trạng thái tính theo ngày
        if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            sql.append(" AND (");
            sql.append(" CASE ");
            sql.append(" WHEN EXISTS (SELECT 1 FROM CTDonDatPhong ct JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP ");
            sql.append(
                    " WHERE ct.maPhong = p.maPhong AND ddp.ngayTra < GETDATE() AND ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận')) THEN N'Quá hạn' ");
            sql.append(" WHEN EXISTS (SELECT 1 FROM CTDonDatPhong ct JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP ");
            sql.append(
                    " WHERE ct.maPhong = p.maPhong AND NOT (ddp.ngayTra < ? OR ddp.ngayNhan > ?) AND ddp.tinhTrang = N'Đã nhận') THEN N'Đang sử dụng' ");
            sql.append(" WHEN EXISTS (SELECT 1 FROM CTDonDatPhong ct JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP ");
            sql.append(
                    " WHERE ct.maPhong = p.maPhong AND NOT (ddp.ngayTra < ? OR ddp.ngayNhan > ?) AND ddp.tinhTrang = N'Đã đặt') THEN N'Đã đặt' ");
            sql.append(" ELSE N'Trống' END = ? ) ");
        }

        sql.append(" ORDER BY p.maPhong ");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;

            // CASE chính
            ps.setDate(i++, tuNgay);
            ps.setDate(i++, denNgay);
            ps.setDate(i++, tuNgay);
            ps.setDate(i++, denNgay);

            // FILTER
            if (maPhong != null && !maPhong.isBlank()) {
                ps.setString(i++, "%" + maPhong + "%");
            }

            if (soNguoiCanTim != null) {
                ps.setInt(i++, soNguoiCanTim);
            }

            if (giaPhongCanTim != null) {
                ps.setDouble(i++, giaPhongCanTim);
            }

            if (loaiPhong != null && !loaiPhong.isBlank() && !"Tất cả".equalsIgnoreCase(loaiPhong)) {
                ps.setString(i++, loaiPhong);
            }

            if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                // CASE filter cần thêm 4 date nữa
                ps.setDate(i++, tuNgay);
                ps.setDate(i++, denNgay);
                ps.setDate(i++, tuNgay);
                ps.setDate(i++, denNgay);

                ps.setString(i++, trangThai);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Phong p = new Phong(
                        rs.getString("maPhong"),
                        rs.getString("loaiPhong"),
                        rs.getInt("soNguoiToiDa"),
                        rs.getDouble("giaPhong"),
                        rs.getString("trangThaiPhong") // 🔥 dùng trạng thái theo ngày
                );

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Dùng trong tìm kiếm của quản lý phòng
    public List<Phong> searchKhongTheoNgay(
            String maPhong,
            Integer soNguoiCanTim,
            Double giaPhongCanTim,
            String loaiPhong,
            String trangThai) {
        List<Phong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.maPhong, p.loaiPhong, p.soNguoiToiDa, p.giaPhong, ");
        sql.append("CASE ");
        sql.append("    WHEN p.trangThaiPhong = N'Bảo trì' THEN N'Bảo trì' ");
        sql.append("    WHEN EXISTS ( ");
        sql.append("        SELECT 1 ");
        sql.append("        FROM CTDonDatPhong ct ");
        sql.append("        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP ");
        sql.append("        WHERE ct.maPhong = p.maPhong ");
        sql.append("        AND ddp.ngayTra < GETDATE() ");
        sql.append("        AND ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận') ");
        sql.append("    ) THEN N'Quá hạn' ");
        sql.append("    WHEN EXISTS ( ");
        sql.append("        SELECT 1 ");
        sql.append("        FROM CTDonDatPhong ct ");
        sql.append("        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP ");
        sql.append("        WHERE ct.maPhong = p.maPhong ");
        sql.append("        AND ddp.tinhTrang = N'Đã nhận' ");
        sql.append("    ) THEN N'Đang sử dụng' ");
        sql.append("    WHEN EXISTS ( ");
        sql.append("        SELECT 1 ");
        sql.append("        FROM CTDonDatPhong ct ");
        sql.append("        JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP ");
        sql.append("        WHERE ct.maPhong = p.maPhong ");
        sql.append("        AND ddp.tinhTrang = N'Đã đặt' ");
        sql.append("    ) THEN N'Đã đặt' ");
        sql.append("    ELSE N'Trống' ");
        sql.append("END AS trangThaiPhong ");
        sql.append("FROM Phong p WHERE 1=1 ");

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

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;

            if (maPhong != null && !maPhong.isBlank()) {
                ps.setString(i++, "%" + maPhong + "%");
            }

            if (soNguoiCanTim != null) {
                ps.setInt(i++, soNguoiCanTim);
            }

            if (giaPhongCanTim != null) {
                ps.setDouble(i++, giaPhongCanTim);
            }

            if (loaiPhong != null && !loaiPhong.isBlank() && !"Tất cả".equalsIgnoreCase(loaiPhong)) {
                ps.setString(i++, loaiPhong);
            }

            if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                ps.setString(i++, trangThai);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapPhong(rs));
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
                rs.getString("trangThaiPhong"));
    }

    // dùng cho trang chủ
    public int demPhongDangSuDung() {

        int count = 0;

        String sql = """
                    SELECT COUNT(*)
                    FROM Phong
                    WHERE trangThaiPhong = N'Đang sử dụng'
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public int demPhongBaoTri() {

        int count = 0;

        String sql = """
                    SELECT COUNT(*)
                    FROM Phong
                    WHERE trangThaiPhong = N'Bảo trì'
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}