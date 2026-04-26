package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.KhachHang;

public class KhachHang_Dao {

    public List<KhachHang> findAll() {
        List<KhachHang> list = new ArrayList<>();

        String sql = "SELECT maKH, hoTen, sdt, cccd, loaiKH, diemSo FROM KhachHang";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(mapKhachHang(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public KhachHang findById(String maKH) {
        String sql = "SELECT maKH, hoTen, sdt, cccd, loaiKH, diemSo "
                   + "FROM KhachHang WHERE maKH = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapKhachHang(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(maKH, hoTen, sdt, cccd, loaiKH, diemSo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getCccd());
            ps.setString(5, kh.getLoaiKH());
            ps.setInt(6, kh.getDiem());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(KhachHang kh) {
        String sql = "UPDATE KhachHang "
                   + "SET hoTen = ?, sdt = ?, cccd = ?, loaiKH = ?, diemSo = ? "
                   + "WHERE maKH = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getCccd());
            ps.setString(4, kh.getLoaiKH());
            ps.setInt(5, kh.getDiem());
            ps.setString(6, kh.getMaKH());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKH = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Hàm search cũ cho các giao diện nhiều ô.
    // Giữ lại để không làm hư màn Quản lý khách hàng.
    public List<KhachHang> search(String maKH, String tenKH, String loaiKH) {
        return search(maKH, tenKH, "", "", null, loaiKH);
    }

    public List<KhachHang> search(
            String maKH,
            String tenKH,
            String sdt,
            String cccd,
            Integer diemSo,
            String loaiKH
    ) {
        List<KhachHang> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maKH, hoTen, sdt, cccd, loaiKH, diemSo ");
        sql.append("FROM KhachHang ");
        sql.append("WHERE 1 = 1 ");

        if (maKH != null && !maKH.isBlank()) {
            sql.append("AND maKH LIKE ? ");
        }

        if (tenKH != null && !tenKH.isBlank()) {
            sql.append("AND hoTen LIKE ? ");
        }

        if (sdt != null && !sdt.isBlank()) {
            sql.append("AND sdt LIKE ? ");
        }

        if (cccd != null && !cccd.isBlank()) {
            sql.append("AND cccd LIKE ? ");
        }

        if (diemSo != null) {
            sql.append("AND diemSo = ? ");
        }

        if (loaiKH != null && !loaiKH.isBlank() && !"Tất cả".equalsIgnoreCase(loaiKH)) {
            sql.append("AND loaiKH = ? ");
        }

        sql.append("ORDER BY maKH");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maKH != null && !maKH.isBlank()) {
                ps.setString(index++, "%" + maKH.trim() + "%");
            }

            if (tenKH != null && !tenKH.isBlank()) {
                ps.setString(index++, "%" + tenKH.trim() + "%");
            }

            if (sdt != null && !sdt.isBlank()) {
                ps.setString(index++, "%" + sdt.trim() + "%");
            }

            if (cccd != null && !cccd.isBlank()) {
                ps.setString(index++, "%" + cccd.trim() + "%");
            }

            if (diemSo != null) {
                ps.setInt(index++, diemSo);
            }

            if (loaiKH != null && !loaiKH.isBlank() && !"Tất cả".equalsIgnoreCase(loaiKH)) {
                ps.setString(index++, loaiKH.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapKhachHang(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Hàm mới cho Báo biểu Khách hàng.
    // 1 ô từ khóa: tìm theo mã KH, họ tên, SĐT, CCCD, điểm số.
    // 1 combobox: loại khách hàng.
    public List<KhachHang> searchBaoBieu(String tuKhoa, String loaiKH) {
        List<KhachHang> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maKH, hoTen, sdt, cccd, loaiKH, diemSo ");
        sql.append("FROM KhachHang ");
        sql.append("WHERE 1 = 1 ");

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (");
            sql.append("maKH LIKE ? ");
            sql.append("OR hoTen LIKE ? ");
            sql.append("OR sdt LIKE ? ");
            sql.append("OR cccd LIKE ? ");
            sql.append("OR CAST(diemSo AS NVARCHAR(20)) LIKE ? ");
            sql.append(") ");
        }

        if (loaiKH != null && !loaiKH.isBlank() && !"Tất cả".equalsIgnoreCase(loaiKH)) {
            sql.append("AND loaiKH = ? ");
        }

        sql.append("ORDER BY maKH");

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
                ps.setString(index++, kw);
            }

            if (loaiKH != null && !loaiKH.isBlank() && !"Tất cả".equalsIgnoreCase(loaiKH)) {
                ps.setString(index++, loaiKH.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapKhachHang(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Các hàm count cũ nếu màn khác đang dùng thì vẫn giữ lại.
    public int countKhachHang(String maKH, String tenKH, String loaiKH) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM KhachHang ");
        sql.append("WHERE 1 = 1 ");

        if (maKH != null && !maKH.isBlank()) {
            sql.append("AND maKH LIKE ? ");
        }

        if (tenKH != null && !tenKH.isBlank()) {
            sql.append("AND hoTen LIKE ? ");
        }

        if (loaiKH != null && !loaiKH.isBlank() && !"Tất cả".equalsIgnoreCase(loaiKH)) {
            sql.append("AND loaiKH = ? ");
        }

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maKH != null && !maKH.isBlank()) {
                ps.setString(index++, "%" + maKH.trim() + "%");
            }

            if (tenKH != null && !tenKH.isBlank()) {
                ps.setString(index++, "%" + tenKH.trim() + "%");
            }

            if (loaiKH != null && !loaiKH.isBlank() && !"Tất cả".equalsIgnoreCase(loaiKH)) {
                ps.setString(index++, loaiKH.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int countByLoaiKH(String loaiKH) {
        String sql = "SELECT COUNT(*) FROM KhachHang WHERE loaiKH = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, loaiKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int countVip() {
        return countByLoaiKH("VIP");
    }

    public int countThanThiet() {
        return countByLoaiKH("Thân thiết");
    }

    private KhachHang mapKhachHang(ResultSet rs) throws Exception {
        return new KhachHang(
                rs.getString("maKH"),
                rs.getString("hoTen"),
                rs.getString("sdt"),
                rs.getString("cccd"),
                rs.getString("loaiKH"),
                rs.getInt("diemSo")
        );
    }
}