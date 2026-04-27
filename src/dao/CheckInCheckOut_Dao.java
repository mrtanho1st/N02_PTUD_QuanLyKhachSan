package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.CheckInCheckOutItem;

public class CheckInCheckOut_Dao {

	public List<CheckInCheckOutItem> search(String maDDP, String cccdSdt, String trangThaiLoc) {
	    List<CheckInCheckOutItem> list = new ArrayList<>();

	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT ");
	    sql.append("    ddp.maDDP, ddp.tinhTrang, ddp.maKH, ");
	    sql.append("    kh.hoTen, kh.cccd, kh.sdt, ");
	    sql.append("    CONVERT(VARCHAR, ddp.ngayNhan, 120) AS ngayNhan, ");
	    sql.append("    CONVERT(VARCHAR, ddp.ngayTra, 120) AS ngayTra, ");
	    sql.append("    ddp.tienCoc ");
	    sql.append("FROM DonDatPhong ddp ");
	    sql.append("LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH ");
	    sql.append("WHERE 1 = 1 ");

	    if (maDDP != null && !maDDP.isBlank()) {
	        sql.append("AND ddp.maDDP LIKE ? ");
	    }

	    if (cccdSdt != null && !cccdSdt.isBlank()) {
	        sql.append("AND (kh.cccd LIKE ? OR kh.sdt LIKE ?) ");
	    }

	    if ("Chưa hoàn thành".equals(trangThaiLoc) || "Tất cả".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận') ");
	    } else if ("Đã đặt".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang = N'Đã đặt' ");
	    } else if ("Đã nhận".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang = N'Đã nhận' ");
	    } else if ("Hoàn thành".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang = N'Hoàn thành' ");
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
	                CheckInCheckOutItem item = new CheckInCheckOutItem(
	                        "",                 // maPhong không dùng ở danh sách đơn nữa
	                        "",                 // loaiPhong
	                        0,                  // giaPhong
	                        "",                 // trangThaiPhong
	                        rs.getString("maDDP"),
	                        rs.getString("tinhTrang"),
	                        rs.getString("maKH"),
	                        rs.getString("hoTen"),
	                        rs.getString("cccd"),
	                        rs.getString("sdt"),
	                        rs.getString("ngayNhan"),
	                        rs.getString("ngayTra"),
	                        rs.getObject("tienCoc") == null ? null : rs.getDouble("tienCoc")
	                );

	                list.add(item);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public List<Object[]> findPhongByMaDDP(String maDDP) {
	    List<Object[]> list = new ArrayList<>();

	    String sql = """
	            SELECT 
	                p.maPhong,
	                p.loaiPhong,
	                p.giaPhong,
	                p.trangThaiPhong
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
	                        rs.getDouble("giaPhong"),
	                        rs.getString("trangThaiPhong")
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
	                SUM(pdv.soLuong * pdv.donGia) AS thanhTien
	            FROM CTDonDatPhong ct
	            INNER JOIN PhieuDichVu pdv ON ct.maPhong = pdv.maPhong
	            INNER JOIN DichVu dv ON pdv.maDV = dv.maDV
	            WHERE ct.maDDP = ?
	            GROUP BY pdv.maDV, dv.tenDichVu
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
	                        rs.getDouble("thanhTien")
	                });
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	public boolean checkIn(String maDDP) {
	    Connection con = null;

	    try {
	        con = ConnectDB.getConnection();
	        con.setAutoCommit(false);

	        String sqlUpdateDon = """
	                UPDATE DonDatPhong
	                SET tinhTrang = N'Đã nhận'
	                WHERE maDDP = ?
	                """;

	        try (PreparedStatement ps = con.prepareStatement(sqlUpdateDon)) {
	            ps.setString(1, maDDP);
	            ps.executeUpdate();
	        }

	        String sqlUpdatePhong = """
	                UPDATE Phong
	                SET trangThaiPhong = N'Đang sử dụng'
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

	public boolean checkOut(String maDDP) {
	    Connection con = null;

	    try {
	        con = ConnectDB.getConnection();
	        con.setAutoCommit(false);

	        String sqlUpdateDon = """
	                UPDATE DonDatPhong
	                SET tinhTrang = N'Hoàn thành'
	                WHERE maDDP = ?
	                """;

	        try (PreparedStatement ps = con.prepareStatement(sqlUpdateDon)) {
	            ps.setString(1, maDDP);
	            ps.executeUpdate();
	        }

	        String sqlUpdatePhong = """
	                UPDATE Phong
	                SET trangThaiPhong = N'Trống'
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