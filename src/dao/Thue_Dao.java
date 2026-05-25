package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.Thue;

public class Thue_Dao {

    public List<Thue> findAllThue() {
        List<Thue> list = new ArrayList<>();

        String sql = """
                SELECT maThue, tenThue, trangThai, tyLeThue, moTa
                FROM Thue
                ORDER BY maThue
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Thue(
                        rs.getString("maThue"),
                        rs.getString("tenThue"),
                        rs.getString("trangThai"),
                        rs.getDouble("tyLeThue"),
                        rs.getString("moTa")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Thue findById(String maThue) {
        String sql = """
                SELECT maThue, tenThue, trangThai, tyLeThue, moTa
                FROM Thue
                WHERE maThue = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maThue);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Thue(
                            rs.getString("maThue"),
                            rs.getString("tenThue"),
                            rs.getString("trangThai"),
                            rs.getDouble("tyLeThue"),
                            rs.getString("moTa"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Thue> searchThue(String maThue, String tenThue, String trangThai) {
        List<Thue> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maThue, tenThue, trangThai, tyLeThue, moTa ");
        sql.append("FROM Thue WHERE 1 = 1 ");

        if (maThue != null && !maThue.isBlank()) {
            sql.append("AND maThue LIKE ? ");
        }

        if (tenThue != null && !tenThue.isBlank()) {
            sql.append("AND tenThue LIKE ? ");
        }

        if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            sql.append("AND trangThai = ? ");
        }

        sql.append("ORDER BY maThue");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;

            if (maThue != null && !maThue.isBlank()) {
                ps.setString(index++, "%" + maThue.trim() + "%");
            }

            if (tenThue != null && !tenThue.isBlank()) {
                ps.setString(index++, "%" + tenThue.trim() + "%");
            }

            if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                ps.setString(index++, trangThai.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Thue(
                            rs.getString("maThue"),
                            rs.getString("tenThue"),
                            rs.getString("trangThai"),
                            rs.getDouble("tyLeThue"),
                            rs.getString("moTa")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Thue> findThueDangApDung() {
        List<Thue> list = new ArrayList<>();

        String sql = """
                SELECT maThue, tenThue, trangThai, tyLeThue, moTa
                FROM Thue
                WHERE trangThai = N'Áp dụng'
                ORDER BY tyLeThue ASC
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Thue thue = new Thue(
                        rs.getString("maThue"),
                        rs.getString("tenThue"),
                        rs.getString("trangThai"),
                        rs.getDouble("tyLeThue"),
                        rs.getString("moTa"));

                list.add(thue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> findAllThueForCombo() {
        List<String> ds = new ArrayList<>();

        String sql = """
                SELECT maThue, tenThue
                FROM Thue
                ORDER BY maThue
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String maThue = rs.getString("maThue");
                String tenThue = rs.getString("tenThue");

                ds.add(maThue + " - " + tenThue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public boolean insert(Thue thue) {
        String sql = """
                INSERT INTO Thue(maThue, tenThue, trangThai, tyLeThue, moTa)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, thue.getMaThue());
            ps.setString(2, thue.getTenThue());
            ps.setString(3, thue.getTrangThai());
            ps.setDouble(4, thue.getTyLeThue());
            ps.setString(5, thue.getMoTa());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Thue thue) {
        String sql = """
                UPDATE Thue
                SET tenThue = ?, trangThai = ?, tyLeThue = ?, moTa = ?
                WHERE maThue = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, thue.getTenThue());
            ps.setString(2, thue.getTrangThai());
            ps.setDouble(3, thue.getTyLeThue());
            ps.setString(4, thue.getMoTa());
            ps.setString(5, thue.getMaThue());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maThue) {
        String sql = """
                DELETE FROM Thue
                WHERE maThue = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maThue);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByMaThue(String maThue) {
        String sql = """
                SELECT maThue
                FROM Thue
                WHERE maThue = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maThue);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}