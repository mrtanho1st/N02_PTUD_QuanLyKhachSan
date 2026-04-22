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
        return searchRoomViews("", "Tất cả", "Tất cả");
    }

    public List<DonDatPhong> searchRoomViews(String maPhong, String loaiPhong, String trangThai) {
        List<DonDatPhong> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.maPhong, p.loaiPhong, p.trangThaiPhong, ");
        sql.append("ddp.maDDP, ddp.tinhTrang, ddp.maKH, kh.hoTen, ");
        sql.append("CONVERT(VARCHAR, ddp.ngayNhan, 23) AS ngayNhan, ");
        sql.append("CONVERT(VARCHAR, ddp.ngayTra, 23) AS ngayTra, ");
        sql.append("ddp.tienCoc ");
        sql.append("FROM Phong p ");
        sql.append("LEFT JOIN CTDonDatPhong ct ON p.maPhong = ct.maPhong ");
        sql.append("LEFT JOIN DonDatPhong ddp ON ct.maDDP = ddp.maDDP ");
        sql.append("LEFT JOIN KhachHang kh ON ddp.maKH = kh.maKH ");
        sql.append("WHERE 1=1 ");

        if (maPhong != null && !maPhong.isBlank()) {
            sql.append(" AND p.maPhong LIKE ? ");
        }
        if (loaiPhong != null && !loaiPhong.equals("Tất cả")) {
            sql.append(" AND p.loaiPhong = ? ");
        }
        if (trangThai != null && !trangThai.equals("Tất cả")) {
            sql.append(" AND p.trangThaiPhong = ? ");
        }

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maPhong != null && !maPhong.isBlank()) {
                ps.setString(index++, "%" + maPhong + "%");
            }
            if (loaiPhong != null && !loaiPhong.equals("Tất cả")) {
                ps.setString(index++, loaiPhong);
            }
            if (trangThai != null && !trangThai.equals("Tất cả")) {
                ps.setString(index++, trangThai);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonDatPhong item = new DonDatPhong(
                            rs.getString("maPhong"),
                            rs.getString("loaiPhong"),
                            rs.getString("trangThaiPhong"),
                            rs.getString("maDDP"),
                            rs.getString("tinhTrang"),
                            rs.getString("maKH"),
                            rs.getString("hoTen"),
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

    public DonDatPhong findRoomDetailByMaPhong(String maPhong) {
        List<DonDatPhong> list = searchRoomViews(maPhong, "Tất cả", "Tất cả");
        return list.isEmpty() ? null : list.get(0);
    }
    public boolean datPhong(Phong phong, NhanVien nhanVien, String tenKH,
            String cccd, String sdt, java.sql.Timestamp ngayNhan,java.sql.Timestamp ngayTra,
            double tienCoc,boolean checkInNgay,List<DatPhongDialog.DichVuDatTruoc> dsDichVu) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            // 1. tìm khách theo CCCD
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

            // 2. nếu chưa có thì thêm khách mới
            if (maKH == null) {
                maKH = taoMa("KH");
                String sqlInsertKH = "INSERT INTO KhachHang(maKH, hoTen, cccd, sdt, loaiKH, diemSo) VALUES (?, ?, ?, ?, N'Thường', 0)";
                ps = con.prepareStatement(sqlInsertKH);
                ps.setString(1, maKH);
                ps.setString(2, tenKH);
                ps.setString(3, cccd);
                ps.setString(4, sdt);
                ps.executeUpdate();
                ps.close();
            }

            // 3. thêm đơn đặt phòng
            String maDDP = taoMa("DDP");
            String tinhTrang = checkInNgay ? "Đã nhận" : "Đã đặt";

            String sqlInsertDDP = "INSERT INTO DonDatPhong(maDDP, maKH, maNV, tinhTrang, ngayNhan, ngayTra, tienCoc) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sqlInsertDDP);
            ps.setString(1, maDDP);
            ps.setString(2, maKH);
            ps.setString(3, nhanVien.getMaNV());
            ps.setString(4, tinhTrang);
            ps.setTimestamp(5, ngayNhan);
            ps.setTimestamp(6, ngayTra);
            ps.setDouble(7, tienCoc);
            ps.executeUpdate();
            ps.close();

            // 4. thêm chi tiết đơn đặt phòng
            long millis = ngayTra.getTime() - ngayNhan.getTime();
            int soNgay = (int) Math.ceil(millis / (1000.0 * 60 * 60 * 24));
            if (soNgay <= 0) soNgay = 1;

            String sqlInsertCT = "INSERT INTO CTDonDatPhong(maDDP, maPhong, soNgay, donGia) VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(sqlInsertCT);
            ps.setString(1, maDDP);
            ps.setString(2, phong.getMaPhong());
            ps.setInt(3, soNgay);
            ps.setDouble(4, phong.getGiaPhong());
            ps.executeUpdate();
            ps.close();

            // 5. cập nhật trạng thái phòng
            String trangThaiPhong = checkInNgay ? "Đang sử dụng" : "Đã đặt";
            String sqlUpdatePhong = "UPDATE Phong SET trangThaiPhong = ? WHERE maPhong = ?";
            ps = con.prepareStatement(sqlUpdatePhong);
            ps.setString(1, trangThaiPhong);
            ps.setString(2, phong.getMaPhong());
            ps.executeUpdate();
            ps.close();

            // 6. lưu dịch vụ đặt trước
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
                String sqlInsertPDV = "INSERT INTO PhieuDichVu(maPDV, maPhong, maDV, soLuong, donGia) VALUES (?, ?, ?, ?, ?)";
                ps = con.prepareStatement(sqlInsertPDV);
                ps.setString(1, maPDV);
                ps.setString(2, phong.getMaPhong());
                ps.setString(3, dv.getMaDV());
                ps.setInt(4, dv.getSoLuong());
                ps.setDouble(5, donGia);
                ps.executeUpdate();
                ps.close();
            }

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
                if (rs != null) rs.close();
                if (ps != null) ps.close();
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
    private String taoMa(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}