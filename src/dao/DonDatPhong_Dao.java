package dao;

import connection.ConnectDB;
import entity.DonDatPhong;
import entity.NhanVien;
import entity.Phong;
import gui.DonDatPhongDialog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DonDatPhong_Dao {

    public List<DonDatPhong> findAllRoomViews() {
        return search("", "", "Tất cả", "", "Tất cả", null, null, null);
    }

    public List<DonDatPhong> search(
            String phong,
            String khachHang,
            String nhanVien,
            String maDDP,
            String tinhTrang,
            Double tienCoc,
            java.sql.Date ngayBatDau,
            java.sql.Date ngayKetThuc
    ) {
        List<DonDatPhong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("    p.maPhong, p.loaiPhong, p.soNguoiToiDa, p.giaPhong, p.trangThaiPhong, ");
        sql.append("    ddp.maDDP, ddp.tinhTrang, ");
        sql.append("    kh.maKH, kh.hoTen AS tenKH, ");
        sql.append("    nv.maNV, nv.hoTen AS tenNV, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayNhan, 23) AS ngayNhan, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayTra, 23) AS ngayTra, ");
        sql.append("    ddp.tienCoc ");
        sql.append("FROM DonDatPhong ddp ");
        sql.append("LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON ddp.maNV = nv.maNV ");
        sql.append("LEFT JOIN CTDonDatPhong ct ON ddp.maDDP = ct.maDDP ");
        sql.append("LEFT JOIN Phong p ON ct.maPhong = p.maPhong ");
        sql.append("WHERE 1 = 1 ");

        if (phong != null && !phong.isBlank()) {
            sql.append("AND (p.maPhong LIKE ? OR p.loaiPhong LIKE ?) ");
        }

        if (khachHang != null && !khachHang.isBlank()) {
            sql.append("AND (kh.maKH LIKE ? OR kh.hoTen LIKE ? OR kh.sdt LIKE ? OR kh.cccd LIKE ?) ");
        }

        if (nhanVien != null && !nhanVien.isBlank() && !"Tất cả".equalsIgnoreCase(nhanVien)) {
            sql.append("AND (nv.maNV LIKE ? OR nv.hoTen LIKE ?) ");
        }

        if (maDDP != null && !maDDP.isBlank()) {
            sql.append("AND ddp.maDDP LIKE ? ");
        }

        if (tinhTrang != null && !tinhTrang.isBlank() && !"Tất cả".equalsIgnoreCase(tinhTrang)) {
            sql.append("AND ddp.tinhTrang = ? ");
        }

        if (tienCoc != null) {
            sql.append("AND ddp.tienCoc = ? ");
        }

        if (ngayBatDau != null) {
            sql.append("AND CAST(ddp.ngayTra AS DATE) >= ? ");
        }

        if (ngayKetThuc != null) {
            sql.append("AND CAST(ddp.ngayNhan AS DATE) <= ? ");
        }

        sql.append("ORDER BY ddp.ngayNhan DESC, ddp.maDDP DESC, p.maPhong ");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (phong != null && !phong.isBlank()) {
                String kw = "%" + phong.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            if (khachHang != null && !khachHang.isBlank()) {
                String kw = "%" + khachHang.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            if (nhanVien != null && !nhanVien.isBlank() && !"Tất cả".equalsIgnoreCase(nhanVien)) {
                String kw = "%" + nhanVien.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            if (maDDP != null && !maDDP.isBlank()) {
                ps.setString(index++, "%" + maDDP.trim() + "%");
            }

            if (tinhTrang != null && !tinhTrang.isBlank() && !"Tất cả".equalsIgnoreCase(tinhTrang)) {
                ps.setString(index++, tinhTrang.trim());
            }

            if (tienCoc != null) {
                ps.setDouble(index++, tienCoc);
            }

            if (ngayBatDau != null) {
                ps.setDate(index++, ngayBatDau);
            }

            if (ngayKetThuc != null) {
                ps.setDate(index++, ngayKetThuc);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDonDatPhong(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<DonDatPhong> findDonDangXuLy() {
        List<DonDatPhong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("    p.maPhong, p.loaiPhong, p.soNguoiToiDa, p.giaPhong, p.trangThaiPhong, ");
        sql.append("    ddp.maDDP, ddp.tinhTrang, ");
        sql.append("    kh.maKH, kh.hoTen AS tenKH, ");
//        sql.append("    kh.soDienThoai, kh.cccd, ");
        sql.append("    nv.maNV, nv.hoTen AS tenNV, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayNhan, 23) AS ngayNhan, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayTra, 23) AS ngayTra, ");
        sql.append("    ddp.tienCoc ");
        sql.append("FROM DonDatPhong ddp ");
        sql.append("LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON ddp.maNV = nv.maNV ");
        sql.append("LEFT JOIN CTDonDatPhong ct ON ddp.maDDP = ct.maDDP ");
        sql.append("LEFT JOIN Phong p ON ct.maPhong = p.maPhong ");
        sql.append("WHERE ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận', N'Đã hủy', N'Hoàn thành') ");
        sql.append("ORDER BY ddp.ngayNhan DESC, ddp.maDDP DESC, p.maPhong ");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString());
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(mapDonDatPhong(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<DonDatPhong> searchBaoBieu(
            String tuKhoa,
            String tinhTrang,
            java.sql.Date tuNgay,
            java.sql.Date denNgay
    ) {
        List<DonDatPhong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("    p.maPhong, p.loaiPhong, p.soNguoiToiDa, p.giaPhong, p.trangThaiPhong, ");
        sql.append("    ddp.maDDP, ddp.tinhTrang, ");
        sql.append("    kh.maKH, kh.hoTen AS tenKH, ");
        sql.append("    nv.maNV, nv.hoTen AS tenNV, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayNhan, 23) AS ngayNhan, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayTra, 23) AS ngayTra, ");
        sql.append("    ddp.tienCoc ");
        sql.append("FROM DonDatPhong ddp ");
        sql.append("LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH ");
        sql.append("LEFT JOIN NhanVien nv ON ddp.maNV = nv.maNV ");
        sql.append("LEFT JOIN CTDonDatPhong ct ON ddp.maDDP = ct.maDDP ");
        sql.append("LEFT JOIN Phong p ON ct.maPhong = p.maPhong ");
        sql.append("WHERE 1 = 1 ");

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (");
            sql.append("ddp.maDDP LIKE ? ");
            sql.append("OR p.maPhong LIKE ? ");
            sql.append("OR p.loaiPhong LIKE ? ");
            sql.append("OR kh.hoTen LIKE ? ");
            sql.append("OR nv.hoTen LIKE ? ");
            sql.append("OR CAST(ddp.tienCoc AS NVARCHAR(50)) LIKE ? ");
            sql.append(") ");
        }

        if (tinhTrang != null && !tinhTrang.isBlank() && !"Tất cả".equalsIgnoreCase(tinhTrang)) {
            sql.append("AND ddp.tinhTrang = ? ");
        }

        if (tuNgay != null) {
            sql.append("AND CAST(ddp.ngayTra AS DATE) >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND CAST(ddp.ngayNhan AS DATE) <= ? ");
        }

        sql.append("ORDER BY ddp.ngayNhan DESC, ddp.maDDP DESC, p.maPhong ");

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
                ps.setString(index++, kw);
            }

            if (tinhTrang != null && !tinhTrang.isBlank() && !"Tất cả".equalsIgnoreCase(tinhTrang)) {
                ps.setString(index++, tinhTrang.trim());
            }

            if (tuNgay != null) {
                ps.setDate(index++, tuNgay);
            }

            if (denNgay != null) {
                ps.setDate(index++, denNgay);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDonDatPhong(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    
    
    public DonDatPhong findRoomDetailByMaPhong(String maPhong) {
        List<DonDatPhong> list = search(maPhong, "", "Tất cả", "", "Tất cả", null, null, null);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean datPhong(
            List<Phong> dsPhong,
            NhanVien nhanVien,
            String tenKH,
            String cccd,
            String sdt,
            String loaiKH,
            int diemSo,
            java.sql.Timestamp ngayNhan,
            java.sql.Timestamp ngayTra,
            double tienCoc,
            boolean checkInNgay,
            List<DonDatPhongDialog.DichVuDatTruoc> dsDichVu
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (dsPhong == null || dsPhong.isEmpty()) {
                return false;
            }

            if (nhanVien == null) {
                return false;
            }

            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            String maKH = timHoacTaoKhachHang(con, tenKH, cccd, sdt, loaiKH, diemSo);

            String maDDP = taoMa("DDP");
            String tinhTrangMoi = checkInNgay ? "Đã nhận" : "Đã đặt";

            String sqlInsertDDP = """
                    INSERT INTO DonDatPhong(maDDP, maKH, maNV, tinhTrang, ngayNhan, ngayTra, tienCoc)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            ps = con.prepareStatement(sqlInsertDDP);
            ps.setString(1, maDDP);
            ps.setString(2, maKH);
            ps.setString(3, nhanVien.getMaNV());
            ps.setString(4, tinhTrangMoi);
            ps.setTimestamp(5, ngayNhan);
            ps.setTimestamp(6, ngayTra);
            ps.setDouble(7, tienCoc);
            ps.executeUpdate();
            ps.close();

            int soNgay = tinhSoNgay(ngayNhan, ngayTra);

            String sqlInsertCT = """
                    INSERT INTO CTDonDatPhong(maDDP, maPhong, soNgay, donGia)
                    VALUES (?, ?, ?, ?)
                    """;

            String trangThaiPhong = checkInNgay ? "Đang sử dụng" : "Đã đặt";
            String sqlUpdatePhong = "UPDATE Phong SET trangThaiPhong = ? WHERE maPhong = ?";

            for (Phong phong : dsPhong) {
                ps = con.prepareStatement(sqlInsertCT);
                ps.setString(1, maDDP);
                ps.setString(2, phong.getMaPhong());
                ps.setInt(3, soNgay);
                ps.setDouble(4, phong.getGiaPhong());
                ps.executeUpdate();
                ps.close();

                ps = con.prepareStatement(sqlUpdatePhong);
                ps.setString(1, trangThaiPhong);
                ps.setString(2, phong.getMaPhong());
                ps.executeUpdate();
                ps.close();
            }

            themDichVuChoNhieuPhong(con, maDDP, dsPhong, dsDichVu);
            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }

                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private String timHoacTaoKhachHang(
            Connection con,
            String tenKH,
            String cccd,
            String sdt,
            String loaiKH,
            int diemSo
    ) throws Exception {
        String maKH = null;

        String sqlFindKH = "SELECT maKH FROM KhachHang WHERE cccd = ?";

        try (PreparedStatement ps = con.prepareStatement(sqlFindKH)) {
            ps.setString(1, cccd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    maKH = rs.getString("maKH");
                }
            }
        }

        if (maKH != null) {
            return maKH;
        }

        maKH = taoMa("KH");

        String sqlInsertKH = """
                INSERT INTO KhachHang(maKH, hoTen, cccd, sdt, loaiKH, diemSo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sqlInsertKH)) {
            ps.setString(1, maKH);
            ps.setString(2, tenKH);
            ps.setString(3, cccd);
            ps.setString(4, sdt);
            ps.setString(5, loaiKH);
            ps.setInt(6, diemSo);
            ps.executeUpdate();
        }

        return maKH;
    }

    private int tinhSoNgay(
            java.sql.Timestamp ngayNhan,
            java.sql.Timestamp ngayTra
    ) {
        long millis = ngayTra.getTime() - ngayNhan.getTime();
        int soNgay = (int) Math.ceil(millis / (1000.0 * 60 * 60 * 24));

        if (soNgay <= 0) {
            soNgay = 1;
        }

        return soNgay;
    }

    private void themDichVuChoNhieuPhong(
            Connection con,
            String maDDP,
            List<Phong> dsPhong,
            List<DonDatPhongDialog.DichVuDatTruoc> dsDichVu
    ) throws Exception {
        if (dsDichVu == null || dsDichVu.isEmpty()) {
            return;
        }

        String sqlGiaDV = "SELECT giaDichVu FROM DichVu WHERE maDV = ?";

        String sqlInsertPDV = """
        	    INSERT INTO PhieuDichVu(maPDV, maDDP, maPhong, maDV, soLuong, donGia)
        	    VALUES (?, ?, ?, ?, ?, ?)
        	""";

        for (Phong phong : dsPhong) {
            for (DonDatPhongDialog.DichVuDatTruoc dv : dsDichVu) {
                double donGia = 0;

                try (PreparedStatement psGia = con.prepareStatement(sqlGiaDV)) {
                    psGia.setString(1, dv.getMaDV());

                    try (ResultSet rs = psGia.executeQuery()) {
                        if (rs.next()) {
                            donGia = rs.getDouble("giaDichVu");
                        }
                    }
                }

                try (PreparedStatement psInsert = con.prepareStatement(sqlInsertPDV)) {
                    psInsert.setString(1, taoMa("PDV"));
                    psInsert.setString(2, maDDP);
                    psInsert.setString(3, phong.getMaPhong());
                    psInsert.setString(4, dv.getMaDV());
                    psInsert.setInt(5, dv.getSoLuong());
                    psInsert.setDouble(6, donGia);
                    psInsert.executeUpdate();
                }
            }
        }
    }

    private DonDatPhong mapDonDatPhong(ResultSet rs) throws Exception {
        return new DonDatPhong(
                rs.getString("maPhong"),
                rs.getString("loaiPhong"),
                rs.getInt("soNguoiToiDa"),
                rs.getDouble("giaPhong"),
                rs.getString("trangThaiPhong"),
                rs.getString("maDDP"),
                rs.getString("tinhTrang"),
                rs.getString("maKH"),
                rs.getString("tenKH"),
                rs.getString("ngayNhan"),
                rs.getString("ngayTra"),
                rs.getObject("tienCoc") == null ? null : rs.getDouble("tienCoc")
        );
    }

    private String taoMa(String prefix) {
        int length = (int)(Math.random() * 4) + 2; // 2 -> 5 chữ số

        int min = (int)Math.pow(10, length - 1);
        int max = (int)Math.pow(10, length) - 1;

        int number = (int)(Math.random() * (max - min + 1)) + min;

        return prefix + number;
    }
//    public List<Object[]> getDichVuByMaPhong(String maPhong) {
//        List<Object[]> list = new ArrayList<>();
//
//        String sql = """
//                SELECT 
//                    pdv.maDV,
//                    dv.tenDichVu,
//                    pdv.soLuong,
//                    pdv.donGia,
//                    pdv.soLuong * pdv.donGia AS thanhTien
//                FROM PhieuDichVu pdv
//                INNER JOIN DichVu dv ON pdv.maDV = dv.maDV
//                WHERE pdv.maPhong = ?
//                ORDER BY pdv.maPDV DESC
//                """;
//
//        try (
//                Connection con = ConnectDB.getConnection();
//                PreparedStatement ps = con.prepareStatement(sql)
//        ) {
//            ps.setString(1, maPhong);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    list.add(new Object[] {
//                            rs.getString("maDV"),
//                            rs.getString("tenDichVu"),
//                            rs.getInt("soLuong"),
//                            rs.getDouble("donGia"),
//                            rs.getDouble("thanhTien")
//                    });
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
    
    
    
    public List<Object[]> getPhongTrongDon(String maDDP) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT 
                    p.maPhong,
                    p.loaiPhong,
                    ct.soNgay,
                    ct.donGia
                FROM CTDonDatPhong ct
                INNER JOIN Phong p ON ct.maPhong = p.maPhong
                WHERE ct.maDDP = ?
                ORDER BY p.maPhong
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("maPhong"),
                            rs.getString("loaiPhong"),
                            rs.getInt("soNgay"),
                            rs.getDouble("donGia")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean capNhatThongTinDon(
            String maDDP,
            String ngayNhan,
            String ngayTra,
            int soNguoi,
            String tinhTrang
    ) {
        String sqlUpdateDon = """
                UPDATE DonDatPhong
                SET ngayNhan = ?, ngayTra = ?, tinhTrang = ?
                WHERE maDDP = ?
                """;

        /*
         * Bảng CTDonDatPhong của bé hiện chưa thấy cột số người.
         * Nên biến soNguoi hiện chỉ validate ở controller.
         * Nếu sau này thêm cột soNguoi vào CTDonDatPhong thì update thêm ở đây.
         */

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sqlUpdateDon)
        ) {
            ps.setString(1, ngayNhan);
            ps.setString(2, ngayTra);
            ps.setString(3, tinhTrang);
            ps.setString(4, maDDP);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean themPhongVaoDon(String maDDP, String maPhong) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            int soNgay = getSoNgayCuaDon(con, maDDP);
            double donGia = getGiaPhong(con, maPhong);

            String sqlInsert = """
                    INSERT INTO CTDonDatPhong(maDDP, maPhong, soNgay, donGia)
                    VALUES (?, ?, ?, ?)
                    """;

            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setString(1, maDDP);
                ps.setString(2, maPhong);
                ps.setInt(3, soNgay);
                ps.setDouble(4, donGia);
                ps.executeUpdate();
            }

            String tinhTrangDon = getTinhTrangDon(con, maDDP);
            String trangThaiPhong = "Đã nhận".equalsIgnoreCase(tinhTrangDon) ? "Đang sử dụng" : "Đã đặt";

            updateTrangThaiPhong(con, maPhong, trangThaiPhong);

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

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

        return false;
    }

    public boolean doiPhongTrongDon(String maDDP, String maPhongCu, String maPhongMoi) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            double donGiaMoi = getGiaPhong(con, maPhongMoi);

            String sqlUpdateCT = """
                    UPDATE CTDonDatPhong
                    SET maPhong = ?, donGia = ?
                    WHERE maDDP = ? AND maPhong = ?
                    """;

            try (PreparedStatement ps = con.prepareStatement(sqlUpdateCT)) {
                ps.setString(1, maPhongMoi);
                ps.setDouble(2, donGiaMoi);
                ps.setString(3, maDDP);
                ps.setString(4, maPhongCu);

                if (ps.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }

            updateTrangThaiPhong(con, maPhongCu, "Trống");

            String tinhTrangDon = getTinhTrangDon(con, maDDP);
            String trangThaiPhongMoi = "Đã nhận".equalsIgnoreCase(tinhTrangDon) ? "Đang sử dụng" : "Đã đặt";

            updateTrangThaiPhong(con, maPhongMoi, trangThaiPhongMoi);

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

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

        return false;
    }

    public boolean themDichVuChoPhong(String maDDP, String maPhong, String maDV, int soLuong) {
        String sqlCheck = "SELECT maPDV FROM PhieuDichVu WHERE maDDP = ? AND maPhong = ? AND maDV = ?";
        String sqlUpdate = "UPDATE PhieuDichVu SET soLuong = soLuong + ? WHERE maDDP = ? AND maPhong = ? AND maDV = ?";
        String sqlGiaDV = "SELECT giaDichVu FROM DichVu WHERE maDV = ?";
        String sqlInsertPDV = """
                INSERT INTO PhieuDichVu(maPDV, maDDP, maPhong, maDV, soLuong, donGia)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConnectDB.getConnection()) {
            // 1. Kiểm tra tồn tại để UPDATE
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
                psCheck.setString(1, maDDP);
                psCheck.setString(2, maPhong);
                psCheck.setString(3, maDV);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                            psUpdate.setInt(1, soLuong);
                            psUpdate.setString(2, maDDP);
                            psUpdate.setString(3, maPhong);
                            psUpdate.setString(4, maDV);
                            return psUpdate.executeUpdate() > 0;
                        }
                    }
                }
            }

            // 2. Nếu chưa có thì INSERT
            double donGia = 0;
            try (PreparedStatement psGia = con.prepareStatement(sqlGiaDV)) {
                psGia.setString(1, maDV);
                try (ResultSet rs = psGia.executeQuery()) {
                    if (rs.next()) donGia = rs.getDouble("giaDichVu");
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsertPDV)) {
                ps.setString(1, taoMa("PDV"));
                ps.setString(2, maDDP);   // Đảm bảo lấy giá trị từ Controller truyền xuống
                ps.setString(3, maPhong);
                ps.setString(4, maDV);
                ps.setInt(5, soLuong);
                ps.setDouble(6, donGia);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean huyDonDatPhong(String maDDP) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            List<String> dsMaPhong = new ArrayList<>();

            String sqlPhong = "SELECT maPhong FROM CTDonDatPhong WHERE maDDP = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlPhong)) {
                ps.setString(1, maDDP);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dsMaPhong.add(rs.getString("maPhong"));
                    }
                }
            }

            String sqlUpdateDon = """
                    UPDATE DonDatPhong
                    SET tinhTrang = N'Đã hủy'
                    WHERE maDDP = ?
                    """;

            try (PreparedStatement ps = con.prepareStatement(sqlUpdateDon)) {
                ps.setString(1, maDDP);
                ps.executeUpdate();
            }

            for (String maPhong : dsMaPhong) {
                updateTrangThaiPhong(con, maPhong, "Trống");
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

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

        return false;
    }
    
    private int getSoNgayCuaDon(Connection con, String maDDP) throws Exception {
        String sql = """
                SELECT DATEDIFF(DAY, ngayNhan, ngayTra) AS soNgay
                FROM DonDatPhong
                WHERE maDDP = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int soNgay = rs.getInt("soNgay");
                    return soNgay <= 0 ? 1 : soNgay;
                }
            }
        }

        return 1;
    }

    private double getGiaPhong(Connection con, String maPhong) throws Exception {
        String sql = "SELECT giaPhong FROM Phong WHERE maPhong = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhong);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("giaPhong");
                }
            }
        }

        return 0;
    }

    private String getTinhTrangDon(Connection con, String maDDP) throws Exception {
        String sql = "SELECT tinhTrang FROM DonDatPhong WHERE maDDP = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tinhTrang");
                }
            }
        }

        return "Đã đặt";
    }

    private void updateTrangThaiPhong(Connection con, String maPhong, String trangThai) throws Exception {
        String sql = "UPDATE Phong SET trangThaiPhong = ? WHERE maPhong = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maPhong);
            ps.executeUpdate();
        }
    }
    
    public List<Object[]> getDichVuTrongDon(String maDDP) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT 
                    pdv.maPhong,
                    pdv.maDV,
                    dv.tenDichVu,
                    pdv.donGia,
                    pdv.soLuong,
                    pdv.soLuong * pdv.donGia AS thanhTien
                FROM CTDonDatPhong ct
                INNER JOIN PhieuDichVu pdv 
					ON ct.maPhong = pdv.maPhong 
					AND ct.maDDP = pdv.maDDP
                INNER JOIN DichVu dv ON pdv.maDV = dv.maDV
                WHERE ct.maDDP = ?
                ORDER BY pdv.maPhong, pdv.maPDV DESC
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("maPhong"),
                            rs.getString("maDV"),
                            rs.getString("tenDichVu"),
                            rs.getDouble("donGia"),
                            rs.getInt("soLuong"),
                            rs.getDouble("thanhTien")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean xoaDichVuChoPhong(String maPhong, String maDV) {
        String sql = """
                DELETE FROM PhieuDichVu
        			WHERE maDDP = ? AND maPhong = ? AND maDV = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maPhong);
            ps.setString(2, maDV);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Thống kê đơn đặt phòng theo thời gian ( Tường )
    public List<Object[]> getThongKeDonDatPhong(java.sql.Date tuNgay, java.sql.Date denNgay) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CAST(ddp.ngayNhan AS DATE) AS theoNgay, ");
        sql.append("       COUNT(DISTINCT ddp.maDDP) AS tongDon, ");
        sql.append("       SUM(CASE WHEN ddp.tinhTrang = N'Đã đặt' THEN 1 ELSE 0 END) AS daDat, ");
        sql.append("       SUM(CASE WHEN ddp.tinhTrang = N'Đã nhận' THEN 1 ELSE 0 END) AS daNhan, ");
        sql.append("       SUM(CASE WHEN ddp.tinhTrang = N'Hoàn thành' THEN 1 ELSE 0 END) AS hoanThanh, ");
        sql.append("       '' AS cacPhong, ");
        sql.append("       ISNULL(SUM(ddp.tienCoc), 0) AS tongTienCoc ");
        sql.append("FROM DonDatPhong ddp ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND ddp.ngayNhan >= ? ");
        }

        if (denNgay != null) {
            sql.append("AND ddp.ngayTra <= ? ");
        }

        sql.append("GROUP BY CAST(ddp.ngayNhan AS DATE) ");
        sql.append("ORDER BY theoNgay ASC");

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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[7];
                    row[0] = rs.getDate("theoNgay").toString();
                    row[1] = rs.getInt("tongDon");
                    row[2] = rs.getInt("daDat");
                    row[3] = rs.getInt("daNhan");
                    row[4] = rs.getInt("hoanThanh");
                    row[5] = "";
                    row[6] = rs.getDouble("tongTienCoc");
                    ds.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
    //dùng cho trang chủ
    public int demPhongSapTraHomNay() {

        int count = 0;

        String sql = """
            SELECT COUNT(*)
            FROM DonDatPhong
            WHERE CAST(ngayTra AS DATE) = CAST(GETDATE() AS DATE)
            AND tinhTrang = N'Đã nhận'
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
    
    public int demKhachHomNay() {

        int count = 0;

        String sql = """
            SELECT COUNT(DISTINCT maKH)
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