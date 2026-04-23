package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.CheckInCheckOutItem;

public class CheckInCheckOut_Dao {

	public List<CheckInCheckOutItem> search(String maPhong, String maDDP, String cccdSdt, String trangThaiLoc) {
	    List<CheckInCheckOutItem> list = new ArrayList<>();

	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT ");
	    sql.append("    p.maPhong, p.loaiPhong, p.giaPhong, p.trangThaiPhong, ");
	    sql.append("    ddp.maDDP, ddp.tinhTrang, ddp.maKH, ");
	    sql.append("    kh.hoTen, kh.cccd, kh.sdt, ");
	    sql.append("    CONVERT(VARCHAR, ddp.ngayNhan, 120) AS ngayNhan, ");
	    sql.append("    CONVERT(VARCHAR, ddp.ngayTra, 120) AS ngayTra, ");
	    sql.append("    ddp.tienCoc ");
	    sql.append("FROM DonDatPhong ddp ");
	    sql.append("INNER JOIN CTDonDatPhong ct ON ddp.maDDP = ct.maDDP ");
	    sql.append("INNER JOIN Phong p ON ct.maPhong = p.maPhong ");
	    sql.append("LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH ");
	    sql.append("WHERE 1 = 1 ");

	    if (maPhong != null && !maPhong.isBlank()) {
	        sql.append("AND p.maPhong LIKE ? ");
	    }
	    if (maDDP != null && !maDDP.isBlank()) {
	        sql.append("AND ddp.maDDP LIKE ? ");
	    }
	    if (cccdSdt != null && !cccdSdt.isBlank()) {
	        sql.append("AND (kh.cccd LIKE ? OR kh.sdt LIKE ?) ");
	    }

	    if ("Chưa hoàn thành".equals(trangThaiLoc) || "Tất cả".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang IN (N'Đã đặt', N'Đã nhận') ");
	    } else if ("Chờ check-in".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang = N'Đã đặt' ");
	    } else if ("Đang lưu trú".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang = N'Đã nhận' ");
	    } else if ("Đã hoàn thành".equals(trangThaiLoc)) {
	        sql.append("AND ddp.tinhTrang = N'Hoàn thành' ");
	    }

	    sql.append("ORDER BY ddp.ngayNhan DESC, ddp.maDDP DESC");

	    try (
	        Connection con = ConnectDB.getConnection();
	        PreparedStatement ps = con.prepareStatement(sql.toString())
	    ) {
	        int index = 1;

	        if (maPhong != null && !maPhong.isBlank()) {
	            ps.setString(index++, "%" + maPhong + "%");
	        }
	        if (maDDP != null && !maDDP.isBlank()) {
	            ps.setString(index++, "%" + maDDP + "%");
	        }
	        if (cccdSdt != null && !cccdSdt.isBlank()) {
	            ps.setString(index++, "%" + cccdSdt + "%");
	            ps.setString(index++, "%" + cccdSdt + "%");
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                CheckInCheckOutItem item = new CheckInCheckOutItem(
	                        rs.getString("maPhong"),
	                        rs.getString("loaiPhong"),
	                        rs.getDouble("giaPhong"),
	                        rs.getString("trangThaiPhong"),
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

    public List<Object[]> findDichVuByMaPhong(String maPhong) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT pdv.maDV, dv.tenDichVu, pdv.soLuong, (pdv.soLuong * pdv.donGia) AS thanhTien " +
                     "FROM PhieuDichVu pdv " +
                     "INNER JOIN DichVu dv ON pdv.maDV = dv.maDV " +
                     "WHERE pdv.maPhong = ?";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maPhong);

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

    public boolean checkIn(String maDDP, String maPhong) {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            ps1 = con.prepareStatement("UPDATE DonDatPhong SET tinhTrang = N'Đã nhận' WHERE maDDP = ?");
            ps1.setString(1, maDDP);
            ps1.executeUpdate();

            ps2 = con.prepareStatement("UPDATE Phong SET trangThaiPhong = N'Đang sử dụng' WHERE maPhong = ?");
            ps2.setString(1, maPhong);
            ps2.executeUpdate();

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (ps1 != null) ps1.close();
                if (ps2 != null) ps2.close();
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

    public boolean checkOut(String maDDP, String maPhong) {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            ps1 = con.prepareStatement("UPDATE DonDatPhong SET tinhTrang = N'Hoàn thành' WHERE maDDP = ?");
            ps1.setString(1, maDDP);
            ps1.executeUpdate();

            ps2 = con.prepareStatement("UPDATE Phong SET trangThaiPhong = N'Trống' WHERE maPhong = ?");
            ps2.setString(1, maPhong);
            ps2.executeUpdate();

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (ps1 != null) ps1.close();
                if (ps2 != null) ps2.close();
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