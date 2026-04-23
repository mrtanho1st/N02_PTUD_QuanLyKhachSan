package dao;



import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.HoaDon;

public class HoaDon_Dao {

    public List<HoaDon> search(Date tuNgay, Date denNgay, String tuKhoa) {
        List<HoaDon> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT hd.maHD, hd.maDDP, kh.hoTen AS tenKH, nv.hoTen AS tenNV, ");
        sql.append("       hd.ngayLapHD, hd.maThue, hd.tongTien ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }
        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (hd.maHD LIKE ? OR hd.maDDP LIKE ? OR kh.hoTen LIKE ? OR nv.hoTen LIKE ?) ");
        }

        sql.append("ORDER BY hd.ngayLapHD DESC, hd.maHD DESC");

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
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new HoaDon(
                            rs.getString("maHD"),
                            rs.getString("maDDP"),
                            rs.getString("tenKH"),
                            rs.getString("tenNV"),
                            rs.getDate("ngayLapHD"),
                            rs.getString("maThue"),
                            rs.getDouble("tongTien")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public int countHoaDon(Date tuNgay, Date denNgay, String tuKhoa) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) sql.append("AND hd.ngayLapHD >= ? ");
        if (denNgay != null) sql.append("AND hd.ngayLapHD <= ? ");
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (hd.maHD LIKE ? OR hd.maDDP LIKE ? OR kh.hoTen LIKE ? OR nv.hoTen LIKE ?) ");
        }

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;
            if (tuNgay != null) ps.setDate(index++, tuNgay);
            if (denNgay != null) ps.setDate(index++, denNgay);

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double sumTongTien(Date tuNgay, Date denNgay, String tuKhoa) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ISNULL(SUM(hd.tongTien), 0) ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) sql.append("AND hd.ngayLapHD >= ? ");
        if (denNgay != null) sql.append("AND hd.ngayLapHD <= ? ");
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (hd.maHD LIKE ? OR hd.maDDP LIKE ? OR kh.hoTen LIKE ? OR nv.hoTen LIKE ?) ");
        }

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;
            if (tuNgay != null) ps.setDate(index++, tuNgay);
            if (denNgay != null) ps.setDate(index++, denNgay);

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double maxTongTien(Date tuNgay, Date denNgay, String tuKhoa) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ISNULL(MAX(hd.tongTien), 0) ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) sql.append("AND hd.ngayLapHD >= ? ");
        if (denNgay != null) sql.append("AND hd.ngayLapHD <= ? ");
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (hd.maHD LIKE ? OR hd.maDDP LIKE ? OR kh.hoTen LIKE ? OR nv.hoTen LIKE ?) ");
        }

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;
            if (tuNgay != null) ps.setDate(index++, tuNgay);
            if (denNgay != null) ps.setDate(index++, denNgay);

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}