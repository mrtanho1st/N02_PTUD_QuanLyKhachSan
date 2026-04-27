package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;

public class ThanhToan_Dao {

    public List<Object[]> findDonChoThanhToan() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT 
                    ddp.maDDP,
                    kh.hoTen,
                    kh.cccd,
                    kh.sdt,
                    CONVERT(VARCHAR, ddp.ngayNhan, 120) AS ngayNhan,
                    CONVERT(VARCHAR, ddp.ngayTra, 120) AS ngayTra,
                    ddp.tienCoc
                FROM DonDatPhong ddp
                LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH
                WHERE ddp.tinhTrang = N'Đã nhận'
                ORDER BY ddp.ngayNhan DESC, ddp.maDDP DESC
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getString("maDDP"),
                        rs.getString("hoTen"),
                        rs.getString("cccd"),
                        rs.getString("sdt"),
                        rs.getString("ngayNhan"),
                        rs.getString("ngayTra"),
                        rs.getObject("tienCoc") == null ? 0.0 : rs.getDouble("tienCoc")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> searchDonChoThanhToan(String maDDP, String cccdSdt) {
        List<Object[]> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("    ddp.maDDP, ");
        sql.append("    kh.hoTen, ");
        sql.append("    kh.cccd, ");
        sql.append("    kh.sdt, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayNhan, 120) AS ngayNhan, ");
        sql.append("    CONVERT(VARCHAR, ddp.ngayTra, 120) AS ngayTra, ");
        sql.append("    ddp.tienCoc ");
        sql.append("FROM DonDatPhong ddp ");
        sql.append("LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH ");
        sql.append("WHERE ddp.tinhTrang = N'Đã nhận' ");

        if (maDDP != null && !maDDP.isBlank()) {
            sql.append("AND ddp.maDDP LIKE ? ");
        }

        if (cccdSdt != null && !cccdSdt.isBlank()) {
            sql.append("AND (kh.cccd LIKE ? OR kh.sdt LIKE ?) ");
        }

        sql.append("ORDER BY ddp.ngayNhan DESC, ddp.maDDP DESC ");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maDDP != null && !maDDP.isBlank()) {
                ps.setString(index++, "%" + maDDP.trim() + "%");
            }

            if (cccdSdt != null && !cccdSdt.isBlank()) {
                String kw = "%" + cccdSdt.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("maDDP"),
                            rs.getString("hoTen"),
                            rs.getString("cccd"),
                            rs.getString("sdt"),
                            rs.getString("ngayNhan"),
                            rs.getString("ngayTra"),
                            rs.getObject("tienCoc") == null ? 0.0 : rs.getDouble("tienCoc")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Object[] findDonThanhToanByMaDDP(String maDDP) {
        String sql = """
                SELECT 
                    ddp.maDDP,
                    kh.hoTen,
                    kh.cccd,
                    kh.sdt,
                    CONVERT(VARCHAR, ddp.ngayNhan, 120) AS ngayNhan,
                    CONVERT(VARCHAR, ddp.ngayTra, 120) AS ngayTra,
                    ddp.tienCoc
                FROM DonDatPhong ddp
                LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH
                WHERE ddp.maDDP = ?
                  AND ddp.tinhTrang = N'Đã nhận'
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getString("maDDP"),
                            rs.getString("hoTen"),
                            rs.getString("cccd"),
                            rs.getString("sdt"),
                            rs.getString("ngayNhan"),
                            rs.getString("ngayTra"),
                            rs.getObject("tienCoc") == null ? 0.0 : rs.getDouble("tienCoc")
                    };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Object[]> findPhongByMaDDP(String maDDP) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT 
                    p.maPhong,
                    p.loaiPhong,
                    ct.soNgay,
                    ct.donGia,
                    ct.soNgay * ct.donGia AS thanhTien
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

    public List<Object[]> findDichVuByMaDDP(String maDDP) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT 
                    pdv.maDV,
                    dv.tenDichVu,
                    SUM(pdv.soLuong) AS soLuong,
                    pdv.donGia,
                    SUM(pdv.soLuong * pdv.donGia) AS thanhTien
                FROM CTDonDatPhong ct
                INNER JOIN PhieuDichVu pdv ON ct.maPhong = pdv.maPhong
                INNER JOIN DichVu dv ON pdv.maDV = dv.maDV
                WHERE ct.maDDP = ?
                GROUP BY pdv.maDV, dv.tenDichVu, pdv.donGia
                ORDER BY pdv.maDV
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDDP);

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

    public double tinhTienPhong(String maDDP) {
        String sql = """
                SELECT SUM(soNgay * donGia) AS tongTienPhong
                FROM CTDonDatPhong
                WHERE maDDP = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("tongTienPhong");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double tinhTienDichVu(String maDDP) {
        String sql = """
                SELECT SUM(pdv.soLuong * pdv.donGia) AS tongTienDichVu
                FROM CTDonDatPhong ct
                INNER JOIN PhieuDichVu pdv ON ct.maPhong = pdv.maPhong
                WHERE ct.maDDP = ?
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maDDP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("tongTienDichVu");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean hoanTatThanhToan(String maDDP) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            String sqlUpdateDon = """
                    UPDATE DonDatPhong
                    SET tinhTrang = N'Hoàn thành'
                    WHERE maDDP = ?
                      AND tinhTrang = N'Đã nhận'
                    """;

            try (PreparedStatement ps = con.prepareStatement(sqlUpdateDon)) {
                ps.setString(1, maDDP);

                int rows = ps.executeUpdate();

                if (rows <= 0) {
                    con.rollback();
                    return false;
                }
            }

            String sqlUpdatePhong = """
                    UPDATE Phong
                    SET trangThaiPhong = N'Bảo trì'
                    WHERE maPhong IN (
                        SELECT maPhong
                        FROM CTDonDatPhong
                        WHERE maDDP = ?
                    )
                    """;

            try (PreparedStatement ps = con.prepareStatement(sqlUpdatePhong)) {
                ps.setString(1, maDDP);
                ps.executeUpdate();
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
}