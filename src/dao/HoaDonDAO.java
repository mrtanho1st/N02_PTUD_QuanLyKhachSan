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

public class HoaDonDAO {

    private final DecimalFormat moneyFormat;
    private final DateTimeFormatter dateFormat;

    public HoaDonDAO() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        moneyFormat = new DecimalFormat("#,##0", symbols);
        dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public List<Object[]> findAllRows() throws SQLException {
        String sql = "SELECT MaHoaDon, KhachHang, NhanVien, MaDon, MaThue, NgayLap, TongTien FROM dbo.HoaDon ORDER BY MaHoaDon";
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
        if ("CONVERT(varchar(10), NgayLap, 103)".equals(column)) {
            sql = "SELECT MaHoaDon, KhachHang, NhanVien, MaDon, MaThue, NgayLap, TongTien FROM dbo.HoaDon WHERE CONVERT(varchar(10), NgayLap, 103) LIKE ? ORDER BY MaHoaDon";
        } else {
            sql = "SELECT MaHoaDon, KhachHang, NhanVien, MaDon, MaThue, NgayLap, TongTien FROM dbo.HoaDon WHERE " + column
                    + " LIKE ? ORDER BY MaHoaDon";
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

    public boolean insert(String maHoaDon, String khachHang, String nhanVien, String maDon, String maThue, LocalDate ngayLap,
            BigDecimal tongTien) throws SQLException {
        String sql = "INSERT INTO dbo.HoaDon (MaHoaDon, KhachHang, NhanVien, MaDon, MaThue, NgayLap, TongTien) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maHoaDon);
            statement.setString(2, khachHang);
            statement.setString(3, nhanVien);
            statement.setString(4, maDon);
            statement.setString(5, maThue);
            statement.setDate(6, Date.valueOf(ngayLap));
            statement.setBigDecimal(7, tongTien);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(String maHoaDon, String khachHang, String nhanVien, String maDon, String maThue, LocalDate ngayLap,
            BigDecimal tongTien) throws SQLException {
        String sql = "UPDATE dbo.HoaDon SET KhachHang = ?, NhanVien = ?, MaDon = ?, MaThue = ?, NgayLap = ?, TongTien = ? WHERE MaHoaDon = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, khachHang);
            statement.setString(2, nhanVien);
            statement.setString(3, maDon);
            statement.setString(4, maThue);
            statement.setDate(5, Date.valueOf(ngayLap));
            statement.setBigDecimal(6, tongTien);
            statement.setString(7, maHoaDon);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(String maHoaDon) throws SQLException {
        String sql = "DELETE FROM dbo.HoaDon WHERE MaHoaDon = ?";
        Connection connection = ConnectDB.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maHoaDon);
            return statement.executeUpdate() > 0;
        }
    }

    private String mapFilterColumn(String filterBy) {
        if ("Khách hàng".equalsIgnoreCase(filterBy)) {
            return "KhachHang";
        }
        if ("Nhân viên".equalsIgnoreCase(filterBy)) {
            return "NhanVien";
        }
        if ("Mã thuế".equalsIgnoreCase(filterBy)) {
            return "MaThue";
        }
        if ("Ngày lập".equalsIgnoreCase(filterBy)) {
            return "CONVERT(varchar(10), NgayLap, 103)";
        }
        return "MaHoaDon";
    }

    private Object[] toRow(ResultSet resultSet) throws SQLException {
        Date ngayLap = resultSet.getDate("NgayLap");
        BigDecimal tongTien = resultSet.getBigDecimal("TongTien");
        return new Object[] {
                resultSet.getString("MaHoaDon"),
                resultSet.getString("KhachHang"),
                resultSet.getString("NhanVien"),
                resultSet.getString("MaDon"),
                resultSet.getString("MaThue"),
                ngayLap.toLocalDate().format(dateFormat),
                moneyFormat.format(tongTien)
        };
    }
}
