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

    // Hàm search cũ cho các giao diện nhiều ô.
    // Giữ lại để không làm hư màn khác.
    public List<HoaDon> search(
            Date tuNgay,
            Date denNgay,
            String maHD,
            String khachHang,
            String dichVu,
            Double tongTien,
            String nhanVien,
            String thue
    ) {
        List<HoaDon> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT hd.maHD, hd.maDDP, kh.hoTen AS tenKH, nv.hoTen AS tenNV, ");
        sql.append("       hd.ngayLapHD, hd.maThue, hd.tongTien ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("LEFT JOIN Thue t ON hd.maThue = t.maThue ");
        sql.append("LEFT JOIN CTHoaDon cthd ON hd.maHD = cthd.maHD ");
        sql.append("LEFT JOIN DichVu dv ON cthd.maDV = dv.maDV ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }

        if (maHD != null && !maHD.isBlank()) {
            sql.append("AND hd.maHD LIKE ? ");
        }

        if (khachHang != null && !khachHang.isBlank()) {
            sql.append("AND kh.hoTen LIKE ? ");
        }

        if (dichVu != null && !dichVu.isBlank()) {
            sql.append("AND (dv.maDV LIKE ? OR dv.tenDichVu LIKE ?) ");
        }

        if (tongTien != null) {
            sql.append("AND hd.tongTien = ? ");
        }

        if (nhanVien != null && !nhanVien.isBlank() && !"Tất cả".equalsIgnoreCase(nhanVien)) {
            sql.append("AND nv.hoTen = ? ");
        }

        if (thue != null && !thue.isBlank() && !"Tất cả".equalsIgnoreCase(thue)) {
            sql.append("AND hd.maThue = ? ");
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

            if (maHD != null && !maHD.isBlank()) {
                ps.setString(index++, "%" + maHD.trim() + "%");
            }

            if (khachHang != null && !khachHang.isBlank()) {
                ps.setString(index++, "%" + khachHang.trim() + "%");
            }

            if (dichVu != null && !dichVu.isBlank()) {
                String dvKeyword = "%" + dichVu.trim() + "%";
                ps.setString(index++, dvKeyword);
                ps.setString(index++, dvKeyword);
            }

            if (tongTien != null) {
                ps.setDouble(index++, tongTien);
            }

            if (nhanVien != null && !nhanVien.isBlank() && !"Tất cả".equalsIgnoreCase(nhanVien)) {
                ps.setString(index++, nhanVien.trim());
            }

            if (thue != null && !thue.isBlank() && !"Tất cả".equalsIgnoreCase(thue)) {
                ps.setString(index++, thue.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapHoaDon(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // Hàm mới cho Báo biểu Hóa đơn.
    // 1 ô từ khóa: tìm theo mã HD, mã ĐĐP, khách hàng, dịch vụ, tổng tiền, nhân viên, thuế.
    // 2 ô ngày: lọc theo ngày lập hóa đơn.
    public List<HoaDon> searchBaoBieu(Date tuNgay, Date denNgay, String tuKhoa) {
        List<HoaDon> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT hd.maHD, hd.maDDP, kh.hoTen AS tenKH, nv.hoTen AS tenNV, ");
        sql.append("       hd.ngayLapHD, hd.maThue, hd.tongTien ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("LEFT JOIN Thue t ON hd.maThue = t.maThue ");
        sql.append("LEFT JOIN CTHoaDon cthd ON hd.maHD = cthd.maHD ");
        sql.append("LEFT JOIN DichVu dv ON cthd.maDV = dv.maDV ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (");
            sql.append("hd.maHD LIKE ? ");
            sql.append("OR hd.maDDP LIKE ? ");
            sql.append("OR kh.hoTen LIKE ? ");
            sql.append("OR nv.hoTen LIKE ? ");
            sql.append("OR hd.maThue LIKE ? ");
            sql.append("OR t.tenThue LIKE ? ");
            sql.append("OR dv.maDV LIKE ? ");
            sql.append("OR dv.tenDichVu LIKE ? ");
            sql.append("OR CAST(hd.tongTien AS NVARCHAR(50)) LIKE ? ");
            sql.append(") ");
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
                ps.setString(index++, kw); // maHD
                ps.setString(index++, kw); // maDDP
                ps.setString(index++, kw); // tenKH
                ps.setString(index++, kw); // tenNV
                ps.setString(index++, kw); // maThue
                ps.setString(index++, kw); // tenThue
                ps.setString(index++, kw); // maDV
                ps.setString(index++, kw); // tenDichVu
                ps.setString(index++, kw); // tongTien
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapHoaDon(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public int countHoaDon(Date tuNgay, Date denNgay, String tuKhoa) {
        return searchBaoBieu(tuNgay, denNgay, tuKhoa).size();
    }

    public double sumTongTien(Date tuNgay, Date denNgay, String tuKhoa) {
        double sum = 0;

        for (HoaDon hd : searchBaoBieu(tuNgay, denNgay, tuKhoa)) {
            sum += hd.getTongTien();
        }

        return sum;
    }

    public double maxTongTien(Date tuNgay, Date denNgay, String tuKhoa) {
        double max = 0;

        for (HoaDon hd : searchBaoBieu(tuNgay, denNgay, tuKhoa)) {
            if (hd.getTongTien() > max) {
                max = hd.getTongTien();
            }
        }

        return max;
    }

    private HoaDon mapHoaDon(ResultSet rs) throws Exception {
        return new HoaDon(
                rs.getString("maHD"),
                rs.getString("maDDP"),
                rs.getString("tenKH"),
                rs.getString("tenNV"),
                rs.getDate("ngayLapHD"),
                rs.getString("maThue"),
                rs.getDouble("tongTien")
        );
    }
}