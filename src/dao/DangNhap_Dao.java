package dao;

import entity.TaiKhoan;
import connection.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DangNhap_Dao {
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Lấy kết nối từ ConnectDB
            con = ConnectDB.getConnection();

            // Truy vấn SQL để kiểm tra tài khoản
            String sql = "SELECT tenDangNhap, matKhau FROM TaiKhoan WHERE tenDangNhap = ? AND matKhau = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, tenDangNhap);
            pstmt.setString(2, matKhau);

            // Thực hiện truy vấn
            rs = pstmt.executeQuery();

            // Nếu tìm thấy tài khoản
            if (rs.next()) {
                taiKhoan = new TaiKhoan();
                taiKhoan.setTenDangNhap(rs.getString("tenDangNhap"));
                taiKhoan.setMatKhau(rs.getString("matKhau"));
                System.out.println("Đăng nhập thành công!");
            } else {
                System.out.println("Tên đăng nhập hoặc mật khẩu không chính xác!");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng các tài nguyên
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return taiKhoan;
    }
}
