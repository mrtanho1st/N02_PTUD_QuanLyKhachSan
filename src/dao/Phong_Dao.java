package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
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
                Phong phong = new Phong(
                    rs.getString("maPhong"),
                    rs.getString("loaiPhong"),
                    rs.getInt("soNguoiToiDa"),
                    rs.getDouble("giaPhong"),
                    rs.getString("trangThaiPhong")
                );
                list.add(phong);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Phong findById(String maPhong) {
        String sql = "SELECT maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong FROM Phong WHERE maPhong = ?";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maPhong);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Phong(
                        rs.getString("maPhong"),
                        rs.getString("loaiPhong"),
                        rs.getInt("soNguoiToiDa"),
                        rs.getDouble("giaPhong"),
                        rs.getString("trangThaiPhong")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insert(Phong phong) {
        String sql = "INSERT INTO Phong(maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong) VALUES (?, ?, ?, ?, ?)";

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
        String sql = "UPDATE Phong SET loaiPhong = ?, soNguoiToiDa = ?, giaPhong = ?, trangThaiPhong = ? WHERE maPhong = ?";

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

    public List<Phong> search(String maPhong, Integer soNguoiCanTim, String loaiPhong, String trangThai) {
        List<Phong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong FROM Phong WHERE 1=1"
        );

        if (maPhong != null && !maPhong.isBlank()) {
            sql.append(" AND maPhong LIKE ?");
        }
        if (soNguoiCanTim != null) {
            sql.append(" AND soNguoiToiDa = ?");
        }
        if (loaiPhong != null && !loaiPhong.equals("Tất cả")) {
            sql.append(" AND loaiPhong = ?");
        }
        if (trangThai != null && !trangThai.equals("Tất cả")) {
            sql.append(" AND trangThaiPhong = ?");
        }

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maPhong != null && !maPhong.isBlank()) {
                ps.setString(index++, "%" + maPhong + "%");
            }
            if (soNguoiCanTim != null) {
                ps.setInt(index++, soNguoiCanTim);
            }
            if (loaiPhong != null && !loaiPhong.equals("Tất cả")) {
                ps.setString(index++, loaiPhong);
            }
            if (trangThai != null && !trangThai.equals("Tất cả")) {
                ps.setString(index++, trangThai);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Phong phong = new Phong(
                        rs.getString("maPhong"),
                        rs.getString("loaiPhong"),
                        rs.getInt("soNguoiToiDa"),
                        rs.getDouble("giaPhong"),
                        rs.getString("trangThaiPhong")
                    );
                    list.add(phong);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}