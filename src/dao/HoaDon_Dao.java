package dao;

import connection.ConnectDB;
import entity.HoaDon;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            String thue) {
        List<HoaDon> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT hd.maHD, hd.maDDP, kh.hoTen AS tenKH, nv.hoTen AS tenNV, ");
        sql.append("       hd.ngayLapHD, hd.maThue, hd.tongTien ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("LEFT JOIN Thue t ON hd.maThue = t.maThue ");
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
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
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
    // 1 ô từ khóa: tìm theo mã HD, mã ĐĐP, khách hàng, tổng tiền, nhân viên, thuế.
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

        // ✅ join dịch vụ
        sql.append("LEFT JOIN CTHoaDonDichVu ctdv ON hd.maHD = ctdv.maHD ");
        sql.append("LEFT JOIN PhieuDichVu pdv ON ctdv.maPDV = pdv.maPDV ");
        sql.append("LEFT JOIN DichVu dv ON pdv.maDV = dv.maDV ");

        // ✅ join phòng (nếu cần tìm theo phòng)
        sql.append("LEFT JOIN CTHoaDonPhong ctp ON hd.maHD = ctp.maHD ");

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
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
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

    public String findMaHDByMaDDP(String maDDP) {
        String sql = """
                SELECT maHD
                FROM HoaDon
                WHERE maDDP = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("maHD");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String taoHoaDon(
            String maDDP,
            String maKM,
            String maThue,
            String maNVLapHoaDon,
            double tongTienThanhToan,
            List<Object[]> dsPhong,
            List<Object[]> dsDichVu) {
        String maHDDaCo = findMaHDByMaDDP(maDDP);

        if (maHDDaCo != null && !maHDDaCo.isBlank()) {
            return maHDDaCo;
        }

        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            String maHD = taoMaHDTuDong(con);

            Object[] thongTin = findThongTinTaoHoaDon(con, maDDP);

            if (thongTin == null) {
                con.rollback();
                return null;
            }

            String maKH = toText(thongTin[0]);

            if (maNVLapHoaDon == null || maNVLapHoaDon.isBlank()) {
                con.rollback();
                return null;
            }

            insertHoaDon(con, maHD, maDDP, maKH, maNVLapHoaDon, maThue, tongTienThanhToan);
            insertChiTietPhong(con, maHD, maKM, dsPhong);
            insertChiTietDichVu(con, maHD, maKM, dsDichVu);

            con.commit();
            return maHD;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;

        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String taoMaHDTuDong(Connection con) throws Exception {
        String sql = """
                SELECT ISNULL(MAX(CAST(SUBSTRING(maHD, 3, LEN(maHD) - 2) AS INT)), 0) + 1 AS nextID
                FROM HoaDon
                WHERE maHD LIKE 'HD%'
                """;

        try (
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int nextID = rs.getInt("nextID");
                return String.format("HD%03d", nextID);
            }
        }

        return "HD001";
    }

    private Object[] findThongTinTaoHoaDon(Connection con, String maDDP) throws Exception {
        String sql = """
                SELECT maKH
                FROM DonDatPhong
                WHERE maDDP = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getString("maKH")
                    };
                }
            }
        }

        return null;
    }

    private void insertHoaDon(
            Connection con,
            String maHD,
            String maDDP,
            String maKH,
            String maNV,
            String maThue,
            double tongTien) throws Exception {
        String sql = """
                INSERT INTO HoaDon(maHD, maDDP, maKH, maNV, maThue, ngayLapHD, tongTien)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, maDDP);
            ps.setString(3, maKH);
            ps.setString(4, maNV);

            if (maThue == null || maThue.isBlank()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, maThue);
            }

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setDouble(7, tongTien);

            ps.executeUpdate();
        }
    }

    private void insertChiTietPhong(
            Connection con,
            String maHD,
            String maKM,
            List<Object[]> dsPhong) throws Exception {
        if (dsPhong == null) {
            return;
        }

        String sql = """
                INSERT INTO CTHoaDonPhong(maHD, maPhong, maKM, soNgay, donGia, thanhTien)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Object[] row : dsPhong) {
                String maPhong = toText(row[0]);
                double soNgay = toDouble(row[2]);
                double donGia = toDouble(row[3]);

                double thanhTien;

                if (row.length >= 5) {
                    thanhTien = toDouble(row[4]);
                } else {
                    thanhTien = soNgay * donGia;
                }

                ps.setString(1, maHD);
                ps.setString(2, maPhong);

                if (maKM == null || maKM.isBlank()) {
                    ps.setNull(3, Types.VARCHAR);
                } else {
                    ps.setString(3, maKM);
                }

                ps.setDouble(4, soNgay);
                ps.setDouble(5, donGia);
                ps.setDouble(6, thanhTien);

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void insertChiTietDichVu(
            Connection con,
            String maHD,
            String maKM,
            List<Object[]> dsDichVu) throws Exception {
        if (dsDichVu == null) {
            return;
        }

        String sql = """
                INSERT INTO CTHoaDonDichVu(maHD, maPDV, maKM, soLuong, donGia, thanhTien)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Object[] row : dsDichVu) {
                String maDV = toText(row[0]);
                int soLuong = toInt(row[2]);

                double donGia;
                double thanhTien;

                if (row.length >= 5) {
                    donGia = toDouble(row[3]);
                    thanhTien = toDouble(row[4]);
                } else {
                    donGia = 0;
                    thanhTien = toDouble(row[3]);
                }

                String maPDV = findMaPDVTheoHoaDonVaDichVu(con, maHD, maDV);

                if (maPDV == null || maPDV.isBlank()) {
                    continue;
                }

                ps.setString(1, maHD);
                ps.setString(2, maPDV);

                if (maKM == null || maKM.isBlank()) {
                    ps.setNull(3, Types.VARCHAR);
                } else {
                    ps.setString(3, maKM);
                }

                ps.setInt(4, soLuong);
                ps.setDouble(5, donGia);

                if (thanhTien > 0) {
                    ps.setDouble(6, thanhTien);
                } else {
                    ps.setDouble(6, donGia * soLuong);
                }

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private String findMaPDVTheoHoaDonVaDichVu(Connection con, String maHD, String maDV) throws Exception {
        String sql = """
                SELECT TOP 1 pdv.maPDV
                FROM HoaDon hd
                JOIN PhieuDichVu pdv ON hd.maDDP = pdv.maDDP
                WHERE hd.maHD = ?
                  AND pdv.maDV = ?
                ORDER BY pdv.maPDV
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, maDV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("maPDV");
                }
            }
        }

        return null;
    }

    public Object[] findThongTinHoaDonDialog(String maHD) {
        String sql = """
                SELECT
                    hd.maHD,
                    hd.maDDP,
                    hd.maKH,
                    kh.hoTen AS tenKhachHang,
                    kh.cccd,
                    kh.sdt AS sdtKhachHang,
                    hd.maNV,
                    nv.hoTen AS tenNhanVien,
                    hd.maThue,
                    hd.maPDV,
                    hd.ngayLapHD,
                    hd.tongTien,
                    ddp.tienCoc
                FROM HoaDon hd
                LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH
                LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV
                LEFT JOIN DonDatPhong ddp ON hd.maDDP = ddp.maDDP
                WHERE hd.maHD = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getString("maHD"),
                            rs.getString("maDDP"),
                            rs.getString("maKH"),
                            rs.getString("tenKhachHang"),
                            rs.getString("cccd"),
                            rs.getString("sdtKhachHang"),
                            rs.getString("maNV"),
                            rs.getString("tenNhanVien"),
                            rs.getString("maThue"),
                            rs.getString("maPDV"),
                            rs.getDate("ngayLapHD"),
                            rs.getDouble("tongTien"),
                            rs.getDouble("tienCoc")
                    };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Object[]> findChiTietHoaDonDialog(String maHD) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT
                    cthd.maHD,
                    cthd.maKM,
                    km.tenKhuyenMai,
                    km.giaTri,
                    cthd.maPhong,
                    p.loaiPhong,
                    cthd.maDV,
                    dv.tenDichVu,
                    cthd.chiTiet,
                    cthd.donGia
                FROM CTHoaDon cthd
                LEFT JOIN KhuyenMai km ON cthd.maKM = km.maKM
                LEFT JOIN Phong p ON cthd.maPhong = p.maPhong
                LEFT JOIN DichVu dv ON cthd.maDV = dv.maDV
                WHERE cthd.maHD = ?
                ORDER BY cthd.maPhong, cthd.maDV
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("maHD"),
                            rs.getString("maKM"),
                            rs.getString("tenKhuyenMai"),
                            rs.getDouble("giaTri"),
                            rs.getString("maPhong"),
                            rs.getString("loaiPhong"),
                            rs.getString("maDV"),
                            rs.getString("tenDichVu"),
                            rs.getString("chiTiet"),
                            rs.getDouble("donGia")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private String toText(Object value) {
        return value == null ? "" : value.toString();
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            String text = value.toString()
                    .replace("VNĐ", "")
                    .replace(",", "")
                    .trim();

            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private HoaDon mapHoaDon(ResultSet rs) throws Exception {
        return new HoaDon(
                rs.getString("maHD"),
                rs.getString("maDDP"),
                rs.getString("tenKH"),
                rs.getString("tenNV"),
                rs.getDate("ngayLapHD"),
                rs.getString("maThue"),
                rs.getDouble("tongTien"));
    }

    // lấy để cho hóa đơn xuất ra
    public Object[] findThongTinInHoaDon(String maHD) {
        String sql = """
                    SELECT
                        hd.maHD,
                        hd.ngayLapHD,
                        kh.hoTen AS tenKH,
                        kh.cccd,
                        kh.sdt,

                        nvLapDon.maNV AS maNVLapDon,
                        nvLapDon.hoTen AS tenNVLapDon,

                        nvLapHoaDon.maNV AS maNVLapHoaDon,
                        nvLapHoaDon.hoTen AS tenNVLapHoaDon,

                        ddp.ngayNhan,
                        ddp.ngayTra,
                        DATEDIFF(DAY, ddp.ngayNhan, ddp.ngayTra) AS soDem,
                        ddp.tienCoc,
                        hd.tongTien,

                        hd.maThue,
                        ISNULL(t.tyLeThue, 0) AS tyLeThue,

                        km.maKM,
                        km.tenKhuyenMai,
                        ISNULL(km.giaTri, 0) AS tyLeGiamGia
                    FROM HoaDon hd
                    JOIN DonDatPhong ddp ON hd.maDDP = ddp.maDDP
                    JOIN KhachHang kh ON hd.maKH = kh.maKH

                    LEFT JOIN NhanVien nvLapDon
                        ON ddp.maNV = nvLapDon.maNV

                    LEFT JOIN NhanVien nvLapHoaDon
                        ON hd.maNV = nvLapHoaDon.maNV

                    LEFT JOIN Thue t
                        ON hd.maThue = t.maThue

                    OUTER APPLY (
                        SELECT TOP 1 x.maKM
                        FROM (
                            SELECT maKM
                            FROM CTHoaDonPhong
                            WHERE maHD = hd.maHD
                              AND maKM IS NOT NULL

                            UNION

                            SELECT maKM
                            FROM CTHoaDonDichVu
                            WHERE maHD = hd.maHD
                              AND maKM IS NOT NULL
                        ) x
                    ) kmHD

                    LEFT JOIN KhuyenMai km
                        ON kmHD.maKM = km.maKM

                    WHERE hd.maHD = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getString("maHD"), // 0
                            rs.getTimestamp("ngayLapHD"), // 1
                            rs.getString("tenKH"), // 2
                            rs.getString("cccd"), // 3
                            rs.getString("sdt"), // 4

                            rs.getString("maNVLapDon"), // 5
                            rs.getString("tenNVLapDon"), // 6

                            rs.getString("maNVLapHoaDon"), // 7
                            rs.getString("tenNVLapHoaDon"), // 8

                            rs.getTimestamp("ngayNhan"), // 9
                            rs.getTimestamp("ngayTra"), // 10
                            rs.getInt("soDem"), // 11
                            rs.getDouble("tienCoc"), // 12
                            rs.getDouble("tongTien"), // 13

                            rs.getString("maThue"), // 14
                            rs.getDouble("tyLeThue"), // 15

                            rs.getString("maKM"), // 16
                            rs.getString("tenKhuyenMai"), // 17
                            rs.getDouble("tyLeGiamGia") // 18
                    };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Object[]> findDanhSachPhongInHoaDon(String maHD) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT
                    p.maPhong,
                    p.loaiPhong,
                    ctp.soNgay,
                    ctp.donGia,
                    ctp.thanhTien
                FROM CTHoaDonPhong ctp
                JOIN Phong p ON ctp.maPhong = p.maPhong
                WHERE ctp.maHD = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("maPhong"),
                            rs.getString("loaiPhong"),
                            rs.getInt("soNgay"),
                            rs.getDouble("donGia"),
                            rs.getDouble("thanhTien")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> findDanhSachDichVuInHoaDon(String maHD) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT
                    dv.maDV,
                    dv.tenDichVu,
                    ctdv.soLuong,
                    ctdv.donGia,
                    ctdv.thanhTien
                FROM CTHoaDonDichVu ctdv
                JOIN PhieuDichVu pdv ON ctdv.maPDV = pdv.maPDV
                JOIN DichVu dv ON pdv.maDV = dv.maDV
                WHERE ctdv.maHD = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("maDV"),
                            rs.getString("tenDichVu"),
                            rs.getInt("soLuong"),
                            rs.getDouble("donGia"),
                            rs.getDouble("thanhTien")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<HoaDon> searchQuanLyHoaDon(
            Date tuNgay,
            Date denNgay,
            String maHD,
            String maDDP,
            String khachHang,
            String nhanVien,
            Double tongTien,
            String thue) {
        List<HoaDon> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT ");
        sql.append("hd.maHD, ");
        sql.append("hd.maDDP, ");
        sql.append("kh.hoTen AS tenKH, ");
        sql.append("nv.hoTen AS tenNV, ");
        sql.append("hd.ngayLapHD, ");
        sql.append("hd.maThue, ");
        sql.append("hd.tongTien ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("LEFT JOIN Thue t ON hd.maThue = t.maThue ");
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

        if (maDDP != null && !maDDP.isBlank()) {
            sql.append("AND hd.maDDP LIKE ? ");
        }

        if (khachHang != null && !khachHang.isBlank()) {
            sql.append("AND kh.hoTen LIKE ? ");
        }

        if (nhanVien != null && !nhanVien.isBlank()) {
            sql.append("AND nv.hoTen LIKE ? ");
        }

        if (tongTien != null) {
            sql.append("AND hd.tongTien = ? ");
        }

        if (thue != null && !thue.isBlank() && !"Tất cả".equalsIgnoreCase(thue)) {
            sql.append("AND hd.maThue = ? ");
        }

        sql.append("ORDER BY hd.ngayLapHD DESC, hd.maHD DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
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

            if (maDDP != null && !maDDP.isBlank()) {
                ps.setString(index++, "%" + maDDP.trim() + "%");
            }

            if (khachHang != null && !khachHang.isBlank()) {
                ps.setString(index++, "%" + khachHang.trim() + "%");
            }

            if (nhanVien != null && !nhanVien.isBlank()) {
                ps.setString(index++, "%" + nhanVien.trim() + "%");
            }

            if (tongTien != null) {
                ps.setDouble(index++, tongTien);
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

    // Lấy doanh thu tách riêng phòng và dịch vụ theo thời gian ( Tường )
    public List<Object[]> getDoanhThuTheoThoiGianChiTiet(Date tuNgay, Date denNgay) {
        List<Object[]> ds = new ArrayList<>();

        // Query được tối ưu để tránh Cartesian product
        // Bước 1: Tính toán chi tiết cho từng hóa đơn
        // Bước 2: Nhóm theo ngày
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    A.ngayLapHD, ");
        sql.append("    COUNT(A.maHD) AS SoHoaDon, ");
        sql.append("    SUM(A.TienPhong) AS TienPhong, ");
        sql.append("    SUM(A.TienDichVu) AS TienDichVu, ");
        sql.append("    SUM(A.TongTien) AS TongTien, ");
        sql.append("    SUM(A.SoPhong) AS TongSoPhongDat, ");
        sql.append("    SUM(A.TienThue) AS TienGiam ");
        sql.append("FROM ( ");
        sql.append("    SELECT ");
        sql.append("        HD.maHD, ");
        sql.append("        HD.ngayLapHD, ");
        sql.append("        HD.tongTien, ");
        sql.append("        ISNULL(P.TienPhong, 0) AS TienPhong, ");
        sql.append("        ISNULL(P.SoPhong, 0) AS SoPhong, ");
        sql.append("        ISNULL(DV.TienDichVu, 0) AS TienDichVu, ");
        sql.append("        ISNULL(HD.tongTien * T.tyLeThue / 100, 0) AS TienThue ");
        sql.append("    FROM HoaDon HD ");
        sql.append("    LEFT JOIN ( ");
        sql.append("        SELECT maHD, SUM(thanhTien) AS TienPhong, ");
        sql.append("               COUNT(DISTINCT maPhong) AS SoPhong ");
        sql.append("        FROM CTHoaDonPhong ");
        sql.append("        GROUP BY maHD ");
        sql.append("    ) P ON HD.maHD = P.maHD ");
        sql.append("    LEFT JOIN ( ");
        sql.append("        SELECT maHD, SUM(thanhTien) AS TienDichVu ");
        sql.append("        FROM CTHoaDonDichVu ");
        sql.append("        GROUP BY maHD ");
        sql.append("    ) DV ON HD.maHD = DV.maHD ");
        sql.append("    LEFT JOIN Thue T ON HD.maThue = T.maThue ");
        sql.append("    WHERE 1=1 ");
        if (tuNgay != null && denNgay != null) {
            sql.append("    AND HD.ngayLapHD >= ? AND HD.ngayLapHD <= ? ");
        }
        sql.append(") A ");
        sql.append("GROUP BY A.ngayLapHD ");
        sql.append("ORDER BY A.ngayLapHD ASC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;
            if (tuNgay != null && denNgay != null) {
                ps.setDate(index++, tuNgay);
                ps.setDate(index++, denNgay);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[7];
                    row[0] = rs.getDate("ngayLapHD");
                    row[1] = rs.getInt("SoHoaDon");
                    row[2] = rs.getDouble("TienPhong");
                    row[3] = rs.getDouble("TienDichVu");
                    row[4] = rs.getDouble("TongTien");
                    row[5] = rs.getInt("TongSoPhongDat");
                    row[6] = rs.getDouble("TienGiam");
                    ds.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<Object[]> getDoanhThuTheoThoiGianChiTiet(Date tuNgay, Date denNgay, String giaTriHoaDon) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    A.ngayLapHD, ");
        sql.append("    COUNT(A.maHD) AS SoHoaDon, ");
        sql.append("    SUM(A.TienPhong) AS TienPhong, ");
        sql.append("    SUM(A.TienDichVu) AS TienDichVu, ");
        sql.append("    SUM(A.TongTien) AS TongTien, ");
        sql.append("    SUM(A.SoPhong) AS TongSoPhongDat, ");
        sql.append("    SUM(A.TienThue) AS TienGiam ");
        sql.append("FROM ( ");
        sql.append("    SELECT ");
        sql.append("        HD.maHD, ");
        sql.append("        HD.ngayLapHD, ");
        sql.append("        HD.tongTien, ");
        sql.append("        ISNULL(P.TienPhong, 0) AS TienPhong, ");
        sql.append("        ISNULL(P.SoPhong, 0) AS SoPhong, ");
        sql.append("        ISNULL(DV.TienDichVu, 0) AS TienDichVu, ");
        sql.append("        ISNULL(HD.tongTien * T.tyLeThue / 100, 0) AS TienThue ");
        sql.append("    FROM HoaDon HD ");
        sql.append("    LEFT JOIN ( ");
        sql.append("        SELECT maHD, SUM(thanhTien) AS TienPhong, ");
        sql.append("               COUNT(DISTINCT maPhong) AS SoPhong ");
        sql.append("        FROM CTHoaDonPhong ");
        sql.append("        GROUP BY maHD ");
        sql.append("    ) P ON HD.maHD = P.maHD ");
        sql.append("    LEFT JOIN ( ");
        sql.append("        SELECT maHD, SUM(thanhTien) AS TienDichVu ");
        sql.append("        FROM CTHoaDonDichVu ");
        sql.append("        GROUP BY maHD ");
        sql.append("    ) DV ON HD.maHD = DV.maHD ");
        sql.append("    LEFT JOIN Thue T ON HD.maThue = T.maThue ");
        sql.append("    WHERE 1=1 ");

        if (tuNgay != null && denNgay != null) {
            sql.append("    AND HD.ngayLapHD >= ? AND HD.ngayLapHD <= ? ");
        }

        // Thêm điều kiện lọc theo giá trị hóa đơn
        if (giaTriHoaDon != null && !"Tất cả".equals(giaTriHoaDon)) {
            if ("Dưới 500 nghìn".equals(giaTriHoaDon)) {
                sql.append("    AND HD.tongTien < 500000 ");
            } else if ("500 nghìn - 1 triệu".equals(giaTriHoaDon)) {
                sql.append("    AND HD.tongTien >= 500000 AND HD.tongTien < 1000000 ");
            } else if ("1 triệu - 5 triệu".equals(giaTriHoaDon)) {
                sql.append("    AND HD.tongTien >= 1000000 AND HD.tongTien < 5000000 ");
            } else if ("Trên 5 triệu".equals(giaTriHoaDon)) {
                sql.append("    AND HD.tongTien >= 5000000 ");
            }
        }

        sql.append(") A ");
        sql.append("GROUP BY A.ngayLapHD ");
        sql.append("ORDER BY A.ngayLapHD ASC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;
            if (tuNgay != null && denNgay != null) {
                ps.setDate(index++, tuNgay);
                ps.setDate(index++, denNgay);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[7];
                    row[0] = rs.getDate("ngayLapHD");
                    row[1] = rs.getInt("SoHoaDon");
                    row[2] = rs.getDouble("TienPhong");
                    row[3] = rs.getDouble("TienDichVu");
                    row[4] = rs.getDouble("TongTien");
                    row[5] = rs.getInt("TongSoPhongDat");
                    row[6] = rs.getDouble("TienGiam");
                    ds.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Doanh thu theo khách hàng ( Tường )
    public List<Object[]> getDoanhThuTheoKhachHang(Date tuNgay, Date denNgay) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT kh.maKH, kh.hoTen, kh.loaiKH, COUNT(DISTINCT hd.maHD) AS soLanLuuTru, ");
        sql.append("       SUM(hd.tongTien) AS tongChiTieu ");
        sql.append("FROM KhachHang kh ");
        sql.append("LEFT JOIN HoaDon hd ON kh.maKH = hd.maKH ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null || denNgay != null) {
            sql.append("AND hd.maHD IS NOT NULL ");
        }

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }

        sql.append("GROUP BY kh.maKH, kh.hoTen, kh.loaiKH ");
        sql.append("ORDER BY SUM(hd.tongTien) DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
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
                    row[0] = rs.getString("maKH");
                    row[1] = rs.getString("hoTen");
                    row[2] = rs.getString("loaiKH");
                    row[3] = rs.getInt("soLanLuuTru");
                    row[4] = rs.getDouble("tongChiTieu");
                    ds.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // Doanh thu theo khách hàng với lọc theo loại khách hàng
    public List<Object[]> getDoanhThuTheoKhachHang(Date tuNgay, Date denNgay, String loaiKH) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT kh.maKH, kh.hoTen, kh.loaiKH, COUNT(DISTINCT hd.maHD) AS soLanLuuTru, ");
        sql.append("       SUM(hd.tongTien) AS tongChiTieu ");
        sql.append("FROM KhachHang kh ");
        sql.append("LEFT JOIN HoaDon hd ON kh.maKH = hd.maKH ");
        sql.append("WHERE 1 = 1 ");

        if (loaiKH != null && !"Tất cả".equals(loaiKH)) {
            sql.append("AND kh.loaiKH = ? ");
        }

        if (tuNgay != null || denNgay != null) {
            sql.append("AND hd.maHD IS NOT NULL ");
        }

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }

        sql.append("GROUP BY kh.maKH, kh.hoTen, kh.loaiKH ");
        sql.append("ORDER BY SUM(hd.tongTien) DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;

            if (loaiKH != null && !"Tất cả".equals(loaiKH)) {
                ps.setString(index++, loaiKH);
            }

            if (tuNgay != null) {
                ps.setDate(index++, tuNgay);
            }

            if (denNgay != null) {
                ps.setDate(index++, denNgay);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getString("maKH");
                    row[1] = rs.getString("hoTen");
                    row[2] = rs.getString("loaiKH");
                    row[3] = rs.getInt("soLanLuuTru");
                    row[4] = rs.getDouble("tongChiTieu");
                    ds.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // Doanh thu theo phòng ( Tường )
    public List<Object[]> getDoanhThuTheoPhong(Date tuNgay, Date denNgay) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.maPhong, p.loaiPhong, COUNT(DISTINCT hd.maHD) AS soLuotThue, ");
        sql.append("       COUNT(ctp.soNgay) AS tongSoNgay, SUM(ctp.thanhTien) AS tongDoanhThu ");
        sql.append("FROM Phong p ");
        sql.append("LEFT JOIN CTHoaDonPhong ctp ON p.maPhong = ctp.maPhong ");
        sql.append("LEFT JOIN HoaDon hd ON ctp.maHD = hd.maHD ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }

        sql.append("GROUP BY p.maPhong, p.loaiPhong ");
        sql.append("ORDER BY SUM(ctp.thanhTien) DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;

            if (tuNgay != null) {
                ps.setDate(index++, tuNgay);
                System.out.println("🔍 DAO: Lọc từ ngày = " + tuNgay);
            }

            if (denNgay != null) {
                ps.setDate(index++, denNgay);
                System.out.println("🔍 DAO: Lọc đến ngày = " + denNgay);
            }

            System.out.println("🔍 DAO SQL: " + sql.toString());

            try (ResultSet rs = ps.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getString("maPhong");
                    row[1] = rs.getString("loaiPhong");
                    row[2] = rs.getInt("soLuotThue");
                    row[3] = rs.getInt("tongSoNgay");
                    row[4] = rs.getDouble("tongDoanhThu");
                    ds.add(row);
                    rowCount++;
                    System.out.println(
                            "🔍 DAO Row " + rowCount + ": " + row[0] + " | " + row[1] + " | DoanhThu=" + row[4]);
                }
                System.out.println("🔍 DAO: Tổng cộng " + rowCount + " phòng");
            }

        } catch (Exception e) {
            System.err.println("❌ DAO Error: " + e.getMessage());
            e.printStackTrace();
        }

        return ds;
    }

    // Doanh thu theo phòng với lọc theo loại phòng
    public List<Object[]> getDoanhThuTheoPhong(Date tuNgay, Date denNgay, String loaiPhong) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.maPhong, p.loaiPhong, COUNT(DISTINCT hd.maHD) AS soLuotThue, ");
        sql.append("       COUNT(ctp.soNgay) AS tongSoNgay, SUM(ctp.thanhTien) AS tongDoanhThu ");
        sql.append("FROM Phong p ");
        sql.append("LEFT JOIN CTHoaDonPhong ctp ON p.maPhong = ctp.maPhong ");
        sql.append("LEFT JOIN HoaDon hd ON ctp.maHD = hd.maHD ");
        sql.append("WHERE 1 = 1 ");

        if (loaiPhong != null && !"Tất cả".equals(loaiPhong)) {
            sql.append("AND p.loaiPhong = ? ");
        }

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }

        sql.append("GROUP BY p.maPhong, p.loaiPhong ");
        sql.append("ORDER BY SUM(ctp.thanhTien) DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;

            if (loaiPhong != null && !"Tất cả".equals(loaiPhong)) {
                ps.setString(index++, loaiPhong);
            }

            if (tuNgay != null) {
                ps.setDate(index++, tuNgay);
            }

            if (denNgay != null) {
                ps.setDate(index++, denNgay);
            }

            try (ResultSet rs = ps.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getString("maPhong");
                    row[1] = rs.getString("loaiPhong");
                    row[2] = rs.getInt("soLuotThue");
                    row[3] = rs.getInt("tongSoNgay");
                    row[4] = rs.getDouble("tongDoanhThu");
                    ds.add(row);
                    rowCount++;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ DAO Error: " + e.getMessage());
            e.printStackTrace();
        }

        return ds;
    }

    // Khách hàng có điểm số cao nhất ( Tường )
    public List<Object[]> getKhachHangDiemCaoNhat() {
        List<Object[]> ds = new ArrayList<>();

        String sql = """
                SELECT ROW_NUMBER() OVER (ORDER BY kh.diemSo DESC) AS xepHang,
                       kh.maKH, kh.hoTen, kh.sdt, kh.loaiKH, kh.diemSo
                FROM KhachHang kh
                ORDER BY kh.diemSo DESC
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("xepHang");
                row[1] = rs.getString("maKH");
                row[2] = rs.getString("hoTen");
                row[3] = rs.getString("sdt");
                row[4] = rs.getString("loaiKH");
                row[5] = rs.getDouble("diemSo");
                ds.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // Khách hàng có điểm số cao nhất với lọc theo loại khách hàng
    public List<Object[]> getKhachHangDiemCaoNhat(String loaiKH) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ROW_NUMBER() OVER (ORDER BY kh.diemSo DESC) AS xepHang, ");
        sql.append("       kh.maKH, kh.hoTen, kh.sdt, kh.loaiKH, kh.diemSo ");
        sql.append("FROM KhachHang kh ");
        sql.append("WHERE 1 = 1 ");

        if (loaiKH != null && !"Tất cả".equals(loaiKH)) {
            sql.append("AND kh.loaiKH = ? ");
        }

        sql.append("ORDER BY kh.diemSo DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;

            if (loaiKH != null && !"Tất cả".equals(loaiKH)) {
                ps.setString(index++, loaiKH);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[6];
                    row[0] = rs.getInt("xepHang");
                    row[1] = rs.getString("maKH");
                    row[2] = rs.getString("hoTen");
                    row[3] = rs.getString("sdt");
                    row[4] = rs.getString("loaiKH");
                    row[5] = rs.getDouble("diemSo");
                    ds.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // Thống kê hóa đơn ( Tường )
    public List<Object[]> getThongKeHoaDon(Date tuNgay, Date denNgay) {
        return getThongKeHoaDon(tuNgay, denNgay, "Tất cả", "Tất cả");
    }

    public List<Object[]> getThongKeHoaDon(Date tuNgay, Date denNgay, String giaTriHD, String nhanVien) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT hd.maHD, hd.ngayLapHD, kh.hoTen, nv.hoTen AS tenNV, ");
        sql.append(
                "       ISNULL(SUM(CASE WHEN ctp.maHD IS NOT NULL THEN ctp.thanhTien ELSE 0 END), 0) AS tienPhong, ");
        sql.append(
                "       ISNULL(SUM(CASE WHEN ctdv.maHD IS NOT NULL THEN ctdv.thanhTien ELSE 0 END), 0) AS tienDichVu, ");
        sql.append("       t.tenThue, hd.tongTien ");
        sql.append("FROM HoaDon hd ");
        sql.append("LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV ");
        sql.append("LEFT JOIN Thue t ON hd.maThue = t.maThue ");
        sql.append("LEFT JOIN CTHoaDonPhong ctp ON hd.maHD = ctp.maHD ");
        sql.append("LEFT JOIN CTHoaDonDichVu ctdv ON hd.maHD = ctdv.maHD ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND hd.ngayLapHD >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND hd.ngayLapHD <= ? ");
        }

        // Lọc theo giá trị hóa đơn
        if (giaTriHD != null && !"Tất cả".equalsIgnoreCase(giaTriHD)) {
            if ("Dưới 1 triệu".equals(giaTriHD)) {
                sql.append("AND hd.tongTien < 1000000 ");
            } else if ("Từ 1 - 3 triệu".equals(giaTriHD)) {
                sql.append("AND hd.tongTien >= 1000000 AND hd.tongTien <= 3000000 ");
            } else if ("Trên 3 triệu".equals(giaTriHD)) {
                sql.append("AND hd.tongTien > 3000000 ");
            }
        }

        // Lọc theo nhân viên lập
        if (nhanVien != null && !"Tất cả".equalsIgnoreCase(nhanVien) && !nhanVien.isEmpty()) {
            sql.append("AND nv.hoTen = ? ");
        }

        sql.append("GROUP BY hd.maHD, hd.ngayLapHD, kh.hoTen, nv.hoTen, t.tenThue, hd.tongTien ");
        sql.append("ORDER BY hd.ngayLapHD DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;

            if (tuNgay != null) {
                ps.setDate(index++, tuNgay);
            }

            if (denNgay != null) {
                ps.setDate(index++, denNgay);
            }

            if (nhanVien != null && !"Tất cả".equalsIgnoreCase(nhanVien) && !nhanVien.isEmpty()) {
                ps.setString(index++, nhanVien.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[8];
                    row[0] = rs.getString("maHD");
                    row[1] = rs.getDate("ngayLapHD");
                    row[2] = rs.getString("hoTen");
                    row[3] = rs.getString("tenNV");
                    row[4] = rs.getDouble("tienPhong");
                    row[5] = rs.getDouble("tienDichVu");
                    row[6] = rs.getString("tenThue");
                    row[7] = rs.getDouble("tongTien");
                    ds.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
    
    //dùng cho trang chủ
    public double tinhDoanhThuHomNay() {

        double tong = 0;

        String sql = """
            SELECT ISNULL(SUM(tongTien), 0)
            FROM HoaDon
            WHERE CAST(ngayLapHD AS DATE) = CAST(GETDATE() AS DATE)
        """;

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            if (rs.next()) {
                tong = rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tong;
    }
    public int demDonDatHomNay() {

        int count = 0;

        String sql = """
            SELECT COUNT(*)
            FROM DonDatPhong
            WHERE CAST(ngayNhan AS DATE) = CAST(GETDATE() AS DATE)
        """;

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}
