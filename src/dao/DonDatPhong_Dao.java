package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import connection.ConnectDB;
import entity.DonDatPhong;
import entity.NhanVien;
import entity.Phong;
import gui.DatPhongDialog;

public class DonDatPhong_Dao {

    public List<DonDatPhong> findAllRoomViews() {
        return search("", "", "Tất cả", "", "Tất cả", null, null, null);
    }

    // Hàm search cũ cho các giao diện nhiều ô.
    // Giữ lại để không làm hư màn đặt phòng / quản lý phòng.
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

        // Lọc theo khoảng giao nhau:
        // Đơn có ngày trả >= ngày bắt đầu tìm kiếm
        // và ngày nhận <= ngày kết thúc tìm kiếm.
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

    // Hàm mới cho Báo biểu Đơn đặt phòng.
    // 1 ô từ khóa: mã ĐĐP, mã phòng, loại phòng, tên nhân viên,
    // tên khách hàng, tiền cọc.
    // 1 combobox: tình trạng.
    // 2 ô ngày: lọc theo khoảng giao nhau giữa ngày nhận / ngày trả.
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
                ps.setString(index++, kw); // maDDP
                ps.setString(index++, kw); // maPhong
                ps.setString(index++, kw); // loaiPhong
                ps.setString(index++, kw); // tenKH
                ps.setString(index++, kw); // tenNV
                ps.setString(index++, kw); // tienCoc
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
            Phong phong,
            NhanVien nhanVien,
            String tenKH,
            String cccd,
            String sdt,
            java.sql.Timestamp ngayNhan,
            java.sql.Timestamp ngayTra,
            double tienCoc,
            boolean checkInNgay,
            List<DatPhongDialog.DichVuDatTruoc> dsDichVu
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            String maKH = null;

            String sqlFindKH = "SELECT maKH FROM KhachHang WHERE cccd = ?";
            ps = con.prepareStatement(sqlFindKH);
            ps.setString(1, cccd);
            rs = ps.executeQuery();

            if (rs.next()) {
                maKH = rs.getString("maKH");
            }

            rs.close();
            ps.close();

            if (maKH == null) {
                maKH = taoMa("KH");

                String sqlInsertKH = """
                        INSERT INTO KhachHang(maKH, hoTen, cccd, sdt, loaiKH, diemSo)
                        VALUES (?, ?, ?, ?, N'Thường', 0)
                        """;

                ps = con.prepareStatement(sqlInsertKH);
                ps.setString(1, maKH);
                ps.setString(2, tenKH);
                ps.setString(3, cccd);
                ps.setString(4, sdt);
                ps.executeUpdate();
                ps.close();
            }

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

            long millis = ngayTra.getTime() - ngayNhan.getTime();
            int soNgay = (int) Math.ceil(millis / (1000.0 * 60 * 60 * 24));

            if (soNgay <= 0) {
                soNgay = 1;
            }

            String sqlInsertCT = """
                    INSERT INTO CTDonDatPhong(maDDP, maPhong, soNgay, donGia)
                    VALUES (?, ?, ?, ?)
                    """;

            ps = con.prepareStatement(sqlInsertCT);
            ps.setString(1, maDDP);
            ps.setString(2, phong.getMaPhong());
            ps.setInt(3, soNgay);
            ps.setDouble(4, phong.getGiaPhong());
            ps.executeUpdate();
            ps.close();

            String trangThaiPhong = checkInNgay ? "Đang sử dụng" : "Đã đặt";

            String sqlUpdatePhong = "UPDATE Phong SET trangThaiPhong = ? WHERE maPhong = ?";
            ps = con.prepareStatement(sqlUpdatePhong);
            ps.setString(1, trangThaiPhong);
            ps.setString(2, phong.getMaPhong());
            ps.executeUpdate();
            ps.close();

            if (dsDichVu != null) {
                for (DatPhongDialog.DichVuDatTruoc dv : dsDichVu) {
                    String sqlGiaDV = "SELECT giaDichVu FROM DichVu WHERE maDV = ?";
                    ps = con.prepareStatement(sqlGiaDV);
                    ps.setString(1, dv.getMaDV());
                    rs = ps.executeQuery();

                    double donGia = 0;

                    if (rs.next()) {
                        donGia = rs.getDouble("giaDichVu");
                    }

                    rs.close();
                    ps.close();

                    String maPDV = taoMa("PDV");

                    String sqlInsertPDV = """
                            INSERT INTO PhieuDichVu(maPDV, maPhong, maDV, soLuong, donGia)
                            VALUES (?, ?, ?, ?, ?)
                            """;

                    ps = con.prepareStatement(sqlInsertPDV);
                    ps.setString(1, maPDV);
                    ps.setString(2, phong.getMaPhong());
                    ps.setString(3, dv.getMaDV());
                    ps.setInt(4, dv.getSoLuong());
                    ps.setDouble(5, donGia);
                    ps.executeUpdate();
                    ps.close();
                }
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
        return prefix + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
    }
}