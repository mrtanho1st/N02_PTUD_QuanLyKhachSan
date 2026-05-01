package dao;

import connection.ConnectDB;
import entity.NhanVien;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_Dao {

    public List<NhanVien> findAll() {
        List<NhanVien> list = new ArrayList<>();

        String sql = "SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, "
                   + "trangThaiLamViec, diaChi, caLamViec, viTriCongViec "
                   + "FROM NhanVien";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(mapNhanVien(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public NhanVien findById(String maNV) {
        String sql = "SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, "
                   + "trangThaiLamViec, diaChi, caLamViec, viTriCongViec "
                   + "FROM NhanVien "
                   + "WHERE maNV = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maNV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien("
                   + "maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, "
                   + "trangThaiLamViec, diaChi, caLamViec, viTriCongViec"
                   + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setDate(3, nv.getNgaySinh());
            ps.setString(4, nv.getSdt());
            ps.setString(5, nv.getEmail());
            ps.setString(6, nv.getGioiTinh());
            ps.setDate(7, nv.getNgayBatDauVaoLam());
            ps.setString(8, nv.getTrangThaiLamViec());
            ps.setString(9, nv.getDiaChi());
            ps.setString(10, nv.getCaLamViec());
            ps.setString(11, nv.getViTriCongViec());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET "
                   + "hoTen = ?, ngaySinh = ?, sdt = ?, email = ?, gioiTinh = ?, "
                   + "ngayBatDauVaoLam = ?, trangThaiLamViec = ?, diaChi = ?, "
                   + "caLamViec = ?, viTriCongViec = ? "
                   + "WHERE maNV = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, nv.getHoTen());
            ps.setDate(2, nv.getNgaySinh());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getEmail());
            ps.setString(5, nv.getGioiTinh());
            ps.setDate(6, nv.getNgayBatDauVaoLam());
            ps.setString(7, nv.getTrangThaiLamViec());
            ps.setString(8, nv.getDiaChi());
            ps.setString(9, nv.getCaLamViec());
            ps.setString(10, nv.getViTriCongViec());
            ps.setString(11, nv.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Hàm search cũ cho các giao diện nhiều ô.
    // Giữ lại để không làm hư màn Quản lý nhân viên.
    public List<NhanVien> search(
            String maNV,
            String hoTen,
            String trangThai,
            String caLamViec,
            String viTriCongViec
    ) {
        List<NhanVien> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, ");
        sql.append("trangThaiLamViec, diaChi, caLamViec, viTriCongViec ");
        sql.append("FROM NhanVien ");
        sql.append("WHERE 1 = 1 ");

        if (maNV != null && !maNV.isBlank()) {
            sql.append("AND maNV LIKE ? ");
        }

        if (hoTen != null && !hoTen.isBlank()) {
            sql.append("AND hoTen LIKE ? ");
        }

        if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            sql.append("AND trangThaiLamViec = ? ");
        }

        if (caLamViec != null && !caLamViec.isBlank() && !"Tất cả".equalsIgnoreCase(caLamViec)) {
            sql.append("AND caLamViec = ? ");
        }

        if (viTriCongViec != null && !viTriCongViec.isBlank() && !"Tất cả".equalsIgnoreCase(viTriCongViec)) {
            sql.append("AND viTriCongViec = ? ");
        }

        sql.append("ORDER BY maNV");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maNV != null && !maNV.isBlank()) {
                ps.setString(index++, "%" + maNV.trim() + "%");
            }

            if (hoTen != null && !hoTen.isBlank()) {
                ps.setString(index++, "%" + hoTen.trim() + "%");
            }

            if (trangThai != null && !trangThai.isBlank() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                ps.setString(index++, trangThai.trim());
            }

            if (caLamViec != null && !caLamViec.isBlank() && !"Tất cả".equalsIgnoreCase(caLamViec)) {
                ps.setString(index++, caLamViec.trim());
            }

            if (viTriCongViec != null && !viTriCongViec.isBlank() && !"Tất cả".equalsIgnoreCase(viTriCongViec)) {
                ps.setString(index++, viTriCongViec.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapNhanVien(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Hàm mới cho Báo biểu Nhân viên.
    // 1 ô từ khóa: tìm theo mã NV hoặc họ tên.
    // 3 combobox: ca làm, trạng thái làm việc, vị trí công việc.
    public List<NhanVien> searchBaoBieu(
            String tuKhoa,
            String caLamViec,
            String trangThaiLamViec,
            String viTriCongViec
    ) {
        List<NhanVien> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, ");
        sql.append("trangThaiLamViec, diaChi, caLamViec, viTriCongViec ");
        sql.append("FROM NhanVien ");
        sql.append("WHERE 1 = 1 ");

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (");
            sql.append("maNV LIKE ? ");
            sql.append("OR hoTen LIKE ? ");
            sql.append(") ");
        }

        if (caLamViec != null && !caLamViec.isBlank() && !"Tất cả".equalsIgnoreCase(caLamViec)) {
            sql.append("AND caLamViec = ? ");
        }

        if (trangThaiLamViec != null
                && !trangThaiLamViec.isBlank()
                && !"Tất cả".equalsIgnoreCase(trangThaiLamViec)) {
            sql.append("AND trangThaiLamViec = ? ");
        }

        if (viTriCongViec != null
                && !viTriCongViec.isBlank()
                && !"Tất cả".equalsIgnoreCase(viTriCongViec)) {
            sql.append("AND viTriCongViec = ? ");
        }

        sql.append("ORDER BY maNV");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            if (caLamViec != null && !caLamViec.isBlank() && !"Tất cả".equalsIgnoreCase(caLamViec)) {
                ps.setString(index++, caLamViec.trim());
            }

            if (trangThaiLamViec != null
                    && !trangThaiLamViec.isBlank()
                    && !"Tất cả".equalsIgnoreCase(trangThaiLamViec)) {
                ps.setString(index++, trangThaiLamViec.trim());
            }

            if (viTriCongViec != null
                    && !viTriCongViec.isBlank()
                    && !"Tất cả".equalsIgnoreCase(viTriCongViec)) {
                ps.setString(index++, viTriCongViec.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapNhanVien(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private NhanVien mapNhanVien(ResultSet rs) throws Exception {
        return new NhanVien(
                rs.getString("maNV"),
                rs.getString("hoTen"),
                rs.getDate("ngaySinh"),
                rs.getString("sdt"),
                rs.getString("email"),
                rs.getString("gioiTinh"),
                rs.getDate("ngayBatDauVaoLam"),
                rs.getString("trangThaiLamViec"),
                rs.getString("diaChi"),
                rs.getString("caLamViec"),
                rs.getString("viTriCongViec")
        );
    }

    // Thống kê theo nhân viên với các bộ lọc (Tường)
    public List<Object[]> getThongKeTheoNhanVien(Date tuNgay, Date denNgay, String tuKhoa, String caLamViec, String trangThaiLamViec, String viTriCongViec) {
        List<Object[]> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ROW_NUMBER() OVER (ORDER BY COUNT(hd.maHD) DESC) AS xepHang, ");
        sql.append("       nv.maNV, nv.hoTen, nv.sdt, nv.email, nv.caLamViec, nv.viTriCongViec, ");
        sql.append("       COUNT(DISTINCT hd.maHD) AS soHoaDon, SUM(hd.tongTien) AS doanhThu ");
        sql.append("FROM NhanVien nv ");
        sql.append("LEFT JOIN HoaDon hd ON nv.maNV = hd.maNV ");
        sql.append("WHERE 1 = 1 ");

        if (tuNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD >= ?) ");
        }

        if (denNgay != null) {
            sql.append("AND (hd.ngayLapHD IS NULL OR hd.ngayLapHD <= ?) ");
        }

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND (nv.maNV LIKE ? OR nv.hoTen LIKE ? OR nv.sdt LIKE ?) ");
        }

        if (caLamViec != null && !caLamViec.isBlank()) {
            sql.append("AND nv.caLamViec = ? ");
        }

        if (trangThaiLamViec != null && !trangThaiLamViec.isBlank()) {
            sql.append("AND nv.trangThaiLamViec = ? ");
        }

        if (viTriCongViec != null && !viTriCongViec.isBlank()) {
            sql.append("AND nv.viTriCongViec = ? ");
        }

        sql.append("GROUP BY nv.maNV, nv.hoTen, nv.sdt, nv.email, nv.caLamViec, nv.viTriCongViec ");
        sql.append("ORDER BY COUNT(hd.maHD) DESC");

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (tuNgay != null) {
                ps.setDate(index++, tuNgay);
            }

            if (denNgay != null) {
                ps.setDate(index++, denNgay);
            }

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            if (caLamViec != null && !caLamViec.isBlank()) {
                ps.setString(index++, caLamViec);
            }

            if (trangThaiLamViec != null && !trangThaiLamViec.isBlank()) {
                ps.setString(index++, trangThaiLamViec);
            }

            if (viTriCongViec != null && !viTriCongViec.isBlank()) {
                ps.setString(index++, viTriCongViec);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[9];
                    row[0] = rs.getInt("xepHang");
                    row[1] = rs.getString("maNV");
                    row[2] = rs.getString("hoTen");
                    row[3] = rs.getString("sdt");
                    row[4] = rs.getString("email");
                    row[5] = rs.getString("caLamViec");
                    row[6] = rs.getString("viTriCongViec");
                    row[7] = rs.getInt("soHoaDon");
                    row[8] = rs.getDouble("doanhThu");
                    ds.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
}