package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;

public class ThueDAO {

    private final DecimalFormat percentFormat;
    private final DateTimeFormatter dateFormatter;

    public ThueDAO() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        percentFormat = new DecimalFormat("0.##", symbols);
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public List<Object[]> findAllRows() throws SQLException {
        String sql = "SELECT MaThue, TenThue, MoTa, TyLeThue, TrangThai, NgayHieuLuc FROM dbo.Thue ORDER BY MaThue";
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
        String sql;
        if ("CONVERT(varchar(10), NgayHieuLuc, 103)".equals(column)) {
            sql = "SELECT MaThue, TenThue, MoTa, TyLeThue, TrangThai, NgayHieuLuc FROM dbo.Thue WHERE CONVERT(varchar(10), NgayHieuLuc, 103) LIKE ? ORDER BY MaThue";
        } else {
            sql = "SELECT MaThue, TenThue, MoTa, TyLeThue, TrangThai, NgayHieuLuc FROM dbo.Thue WHERE " + column
                    + " LIKE ? ORDER BY MaThue";
        }

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

    public boolean insert(String maThue, String tenThue, String moTa, BigDecimal tyLeThue, String trangThai, LocalDate ngayHieuLuc)
            throws SQLException {
        String sql = "INSERT INTO dbo.Thue (MaThue, TenThue, MoTa, TyLeThue, TrangThai, NgayHieuLuc, NgayCapNhat) VALUES (?, ?, ?, ?, ?, ?, SYSUTCDATETIME())";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maThue);
            statement.setString(2, tenThue);
            statement.setString(3, moTa);
            statement.setBigDecimal(4, tyLeThue);
            statement.setString(5, trangThai);
            statement.setDate(6, Date.valueOf(ngayHieuLuc));
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateDefinition(String maThue, String tenThue, String moTa, BigDecimal tyLeThue, String trangThai,
            LocalDate ngayHieuLuc) throws SQLException {
        String sql = "UPDATE dbo.Thue SET TenThue = ?, MoTa = ?, TyLeThue = ?, TrangThai = ?, NgayHieuLuc = ?, NgayCapNhat = SYSUTCDATETIME() WHERE MaThue = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tenThue);
            statement.setString(2, moTa);
            statement.setBigDecimal(3, tyLeThue);
            statement.setString(4, trangThai);
            statement.setDate(5, Date.valueOf(ngayHieuLuc));
            statement.setString(6, maThue);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deactivate(String maThue) throws SQLException {
        String sql = "UPDATE dbo.Thue SET TrangThai = N'Tạm ngưng', NgayCapNhat = SYSUTCDATETIME() WHERE MaThue = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maThue);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean isUsedByHoaDon(String maThue) throws SQLException {
        String sql = "SELECT TOP 1 1 FROM dbo.HoaDon WHERE MaThue = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maThue);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private String mapFilterColumn(String filterBy) {
        if ("Tên thuế".equalsIgnoreCase(filterBy)) {
            return "TenThue";
        }
        if ("Trạng thái".equalsIgnoreCase(filterBy)) {
            return "TrangThai";
        }
        if ("Ngày hiệu lực".equalsIgnoreCase(filterBy)) {
            return "CONVERT(varchar(10), NgayHieuLuc, 103)";
        }
        return "MaThue";
    }

    private Object[] toRow(ResultSet resultSet) throws SQLException {
        BigDecimal tyLeThue = resultSet.getBigDecimal("TyLeThue");
        Date ngayHieuLuc = resultSet.getDate("NgayHieuLuc");
        return new Object[] {
                resultSet.getString("MaThue"),
                resultSet.getString("TenThue"),
                resultSet.getString("MoTa"),
                percentFormat.format(tyLeThue) + "%",
                resultSet.getString("TrangThai"),
                ngayHieuLuc.toLocalDate().format(dateFormatter)
        };
    }
}
