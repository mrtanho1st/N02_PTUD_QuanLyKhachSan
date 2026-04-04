package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;

public class KhachHangDAO {

    public List<Object[]> findAllRows() throws SQLException {
        String sql = "SELECT MaKH, HoTen, SoDienThoai, Email, CCCD, PhanLoai, Diem FROM dbo.KhachHang ORDER BY MaKH";
        List<Object[]> rows = new ArrayList<Object[]>();

        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rows.add(toRow(resultSet));
            }
        }

        return rows;
    }

    public List<Object[]> search(String keyword, String phanLoai) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT MaKH, HoTen, SoDienThoai, Email, CCCD, PhanLoai, Diem FROM dbo.KhachHang WHERE 1 = 1");
        List<String> params = new ArrayList<String>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (MaKH LIKE ? OR HoTen LIKE ? OR SoDienThoai LIKE ? OR Email LIKE ? OR CCCD LIKE ?)");
            String keywordPattern = "%" + keyword.trim() + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
        }

        if (phanLoai != null && !phanLoai.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(phanLoai.trim())) {
            sql.append(" AND PhanLoai = ?");
            params.add(phanLoai.trim());
        }

        sql.append(" ORDER BY MaKH");

        List<Object[]> rows = new ArrayList<Object[]>();
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setString(i + 1, params.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(toRow(resultSet));
                }
            }
        }
        return rows;
    }

    public boolean insert(String maKH, String hoTen, String soDienThoai, String email, String cccd, String phanLoai, int diem)
            throws SQLException {
        String sql = "INSERT INTO dbo.KhachHang (MaKH, HoTen, SoDienThoai, Email, CCCD, PhanLoai, Diem) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKH);
            statement.setString(2, hoTen);
            statement.setString(3, soDienThoai);
            statement.setString(4, email);
            statement.setString(5, cccd);
            statement.setString(6, phanLoai);
            statement.setInt(7, diem);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(String maKH, String hoTen, String soDienThoai, String email, String cccd, String phanLoai, int diem)
            throws SQLException {
        String sql = "UPDATE dbo.KhachHang SET HoTen = ?, SoDienThoai = ?, Email = ?, CCCD = ?, PhanLoai = ?, Diem = ? WHERE MaKH = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hoTen);
            statement.setString(2, soDienThoai);
            statement.setString(3, email);
            statement.setString(4, cccd);
            statement.setString(5, phanLoai);
            statement.setInt(6, diem);
            statement.setString(7, maKH);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(String maKH) throws SQLException {
        String sql = "DELETE FROM dbo.KhachHang WHERE MaKH = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKH);
            return statement.executeUpdate() > 0;
        }
    }

    private Object[] toRow(ResultSet resultSet) throws SQLException {
        return new Object[] {
                resultSet.getString("MaKH"),
                resultSet.getString("HoTen"),
                resultSet.getString("SoDienThoai"),
                resultSet.getString("Email"),
                resultSet.getString("CCCD"),
                resultSet.getString("PhanLoai"),
                resultSet.getInt("Diem")
        };
    }
}
