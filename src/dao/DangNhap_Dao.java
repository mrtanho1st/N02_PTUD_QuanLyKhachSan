package dao;

import entity.TaiKhoan;
import connection.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DangNhap_Dao {
	public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
	    String sql = "SELECT tenDangNhap, matKhau, vaiTro, maNV FROM TaiKhoan WHERE tenDangNhap = ? AND matKhau = ?";

	    try (
	        Connection con = ConnectDB.getConnection();
	        PreparedStatement ps = con.prepareStatement(sql)
	    ) {
	        ps.setString(1, tenDangNhap);
	        ps.setString(2, matKhau);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                TaiKhoan tk = new TaiKhoan();
	                tk.setTenDangNhap(rs.getString("tenDangNhap"));
	                tk.setMatKhau(rs.getString("matKhau"));
	                tk.setVaiTro(rs.getString("vaiTro"));
	                tk.setMaNV(rs.getString("maNV"));
	                return tk;
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
}
