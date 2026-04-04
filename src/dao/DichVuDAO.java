package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;

public class DichVuDAO {

    private final DecimalFormat moneyFormat;

    public DichVuDAO() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        moneyFormat = new DecimalFormat("#,##0", symbols);
    }

    public List<Object[]> findAllRows() throws SQLException {
        String sql = "SELECT MaDV, TenDV, NhomDV, DonViTinh, DonGia, TrangThai FROM dbo.DichVu ORDER BY MaDV";
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

    public List<Object[]> search(String keyword, String filterBy) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllRows();
        }

        String column = mapFilterColumn(filterBy);
        String sql = "SELECT MaDV, TenDV, NhomDV, DonViTinh, DonGia, TrangThai FROM dbo.DichVu WHERE " + column
                + " LIKE ? ORDER BY MaDV";
        List<Object[]> rows = new ArrayList<Object[]>();

        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + keyword.trim() + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(toRow(resultSet));
                }
            }
        }

        return rows;
    }

    public boolean insert(String maDV, String tenDV, String nhomDV, String donViTinh, BigDecimal donGia, String trangThai)
            throws SQLException {
        String sql = "INSERT INTO dbo.DichVu (MaDV, TenDV, NhomDV, DonViTinh, DonGia, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maDV);
            statement.setString(2, tenDV);
            statement.setString(3, nhomDV);
            statement.setString(4, donViTinh);
            statement.setBigDecimal(5, donGia);
            statement.setString(6, trangThai);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(String maDV, String tenDV, String nhomDV, String donViTinh, BigDecimal donGia, String trangThai)
            throws SQLException {
        String sql = "UPDATE dbo.DichVu SET TenDV = ?, NhomDV = ?, DonViTinh = ?, DonGia = ?, TrangThai = ? WHERE MaDV = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tenDV);
            statement.setString(2, nhomDV);
            statement.setString(3, donViTinh);
            statement.setBigDecimal(4, donGia);
            statement.setString(5, trangThai);
            statement.setString(6, maDV);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(String maDV) throws SQLException {
        String sql = "DELETE FROM dbo.DichVu WHERE MaDV = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maDV);
            return statement.executeUpdate() > 0;
        }
    }

    private String mapFilterColumn(String filterBy) {
        if ("Tên dịch vụ".equalsIgnoreCase(filterBy)) {
            return "TenDV";
        }
        if ("Nhóm dịch vụ".equalsIgnoreCase(filterBy)) {
            return "NhomDV";
        }
        if ("Trạng thái".equalsIgnoreCase(filterBy)) {
            return "TrangThai";
        }
        return "MaDV";
    }

    private Object[] toRow(ResultSet resultSet) throws SQLException {
        BigDecimal donGia = resultSet.getBigDecimal("DonGia");
        return new Object[] {
                resultSet.getString("MaDV"),
                resultSet.getString("TenDV"),
                resultSet.getString("NhomDV"),
                resultSet.getString("DonViTinh"),
                moneyFormat.format(donGia),
                resultSet.getString("TrangThai")
        };
    }
}
