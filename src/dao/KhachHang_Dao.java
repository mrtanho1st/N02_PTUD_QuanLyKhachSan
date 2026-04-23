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
        String sql = "SELECT maKH, hoTen, sdt, cccd, loaiKH, diemSo FROM KhachHang WHERE maKH = ?";

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
        String sql = "INSERT INTO KhachHang(maKH, hoTen, sdt, cccd, loaiKH, diemSo) VALUES (?, ?, ?, ?, ?, ?)";

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
        String sql = "UPDATE KhachHang SET hoTen = ?, sdt = ?, cccd = ?, loaiKH = ?, diemSo = ? WHERE maKH = ?";

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

    public List<KhachHang> search(String maKH, String tenKH, String loaiKH) {
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

    // ===== Dùng cho Báo biểu =====

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