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

        String sql = "SELECT maKH, hoTen, sdt,cccd, loaiKH, diemSo FROM khachHang";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                    rs.getString("maKH"),
                    rs.getString("hoTen"),
                    rs.getString("sdt"),
                    rs.getString("cccd"),
                    rs.getString("loaiKH"),
                    rs.getInt("diemSo")
                );
                list.add(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public KhachHang findById(String maKH) {
        String sql = "SELECT maKH, hoTen, sdt,  cccd, loaiKH, diem FROM khachHang WHERE maKH = ?";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maKH);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO khachHang(maKH, hoTen, sdt, cccd, loaiKH, diemSo) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
        String sql = "UPDATE khachHang SET hoTen = ?, sdt = ?,  cccd = ?, loaiKH = ?, diemSo = ? WHERE maKH = ?";

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
        String sql = "DELETE FROM khachHang WHERE maKH = ?";

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

        StringBuilder sql = new StringBuilder(
            "SELECT maKH, hoTen, sdt, cccd, loaiKH, diemSo FROM khachHang WHERE 1=1"
        );

        if (maKH != null && !maKH.isBlank()) {
            sql.append(" AND maKH LIKE ?");
        }

        if (tenKH != null && !tenKH.isBlank()) {
            sql.append(" AND hoTen LIKE ?");
        }

        if (loaiKH != null && !loaiKH.equals("Tất cả")) {
            sql.append(" AND loaiKH = ?");
        }

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maKH != null && !maKH.isBlank()) {
                ps.setString(index++, "%" + maKH + "%");
            }

            if (tenKH != null && !tenKH.isBlank()) {
                ps.setString(index++, "%" + tenKH + "%");
            }

            if (loaiKH != null && !loaiKH.equals("Tất cả")) {
                ps.setString(index++, loaiKH);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getString("hoTen"),
                        rs.getString("sdt"),
                        rs.getString("cccd"),
                        rs.getString("loaiKH"),
                        rs.getInt("diemSo")
                    );
                    list.add(kh);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}