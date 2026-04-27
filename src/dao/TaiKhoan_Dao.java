package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;

public class TaiKhoan_Dao {

    public List<Object[]> getAllTaiKhoan() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT tk.tenDangNhap, tk.matKhau, tk.maNV, nv.hoTen, tk.vaiTro
                FROM TaiKhoan tk
                INNER JOIN NhanVien nv ON tk.maNV = nv.maNV
                ORDER BY tk.tenDangNhap
                """;

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getString("tenDangNhap"),
                        "********",
                        rs.getString("maNV"),
                        rs.getString("hoTen"),
                        rs.getString("vaiTro")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> searchTaiKhoan(String keyword, String type) {
        List<Object[]> list = new ArrayList<>();

        String column;

        switch (type) {
            case "Mã nhân viên":
                column = "tk.maNV";
                break;
            case "Họ tên nhân viên":
                column = "nv.hoTen";
                break;
            case "Vai trò":
                column = "tk.vaiTro";
                break;
            default:
                column = "tk.tenDangNhap";
                break;
        }

        String sql = """
                SELECT tk.tenDangNhap, tk.matKhau, tk.maNV, nv.hoTen, tk.vaiTro
                FROM TaiKhoan tk
                INNER JOIN NhanVien nv ON tk.maNV = nv.maNV
                WHERE %s LIKE ?
                ORDER BY tk.tenDangNhap
                """.formatted(column);

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("tenDangNhap"),
                            "********",
                            rs.getString("maNV"),
                            rs.getString("hoTen"),
                            rs.getString("vaiTro")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<NhanVienItem> getAllNhanVien() {
        List<NhanVienItem> list = new ArrayList<>();

        String sql = """
                SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh,
                       diaChi, viTriCongViec, trangThaiLamViec
                FROM NhanVien
                ORDER BY hoTen
                """;

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(createNhanVienItem(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<NhanVienItem> getNhanVienChuaCoTaiKhoan() {
        List<NhanVienItem> list = new ArrayList<>();

        String sql = """
                SELECT nv.maNV, nv.hoTen, nv.ngaySinh, nv.sdt, nv.email, nv.gioiTinh,
                       nv.diaChi, nv.viTriCongViec, nv.trangThaiLamViec
                FROM NhanVien nv
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM TaiKhoan tk
                    WHERE tk.maNV = nv.maNV
                )
                ORDER BY nv.hoTen
                """;

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(createNhanVienItem(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean isTenDangNhapExists(String tenDangNhap) {
        String sql = """
                SELECT COUNT(*) AS soLuong
                FROM TaiKhoan
                WHERE tenDangNhap = ?
                """;

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("soLuong") > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertTaiKhoan(String tenDangNhap, String matKhau, String vaiTro, String maNV) {
        String sql = """
                INSERT INTO TaiKhoan(tenDangNhap, matKhau, vaiTro, maNV)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ps.setString(3, vaiTro);
            ps.setString(4, maNV);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateTaiKhoan(String tenDangNhap, String matKhau, String vaiTro, String maNV) {
        String sql;

        boolean coDoiMatKhau = matKhau != null && !matKhau.trim().isEmpty();

        if (coDoiMatKhau) {
            sql = """
                    UPDATE TaiKhoan
                    SET matKhau = ?, vaiTro = ?, maNV = ?
                    WHERE tenDangNhap = ?
                    """;
        } else {
            sql = """
                    UPDATE TaiKhoan
                    SET vaiTro = ?, maNV = ?
                    WHERE tenDangNhap = ?
                    """;
        }

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            if (coDoiMatKhau) {
                ps.setString(1, matKhau);
                ps.setString(2, vaiTro);
                ps.setString(3, maNV);
                ps.setString(4, tenDangNhap);
            } else {
                ps.setString(1, vaiTro);
                ps.setString(2, maNV);
                ps.setString(3, tenDangNhap);
            }

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteTaiKhoan(String tenDangNhap) {
        String sql = "DELETE FROM TaiKhoan WHERE tenDangNhap = ?";

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private NhanVienItem createNhanVienItem(ResultSet rs) throws Exception {
        return new NhanVienItem(
                rs.getString("maNV"),
                rs.getString("hoTen"),
                rs.getString("ngaySinh"),
                rs.getString("sdt"),
                rs.getString("email"),
                rs.getString("gioiTinh"),
                rs.getString("diaChi"),
                rs.getString("viTriCongViec"),
                rs.getString("trangThaiLamViec")
        );
    }

    public static class NhanVienItem {
        private final String maNV;
        private final String hoTen;
        private final String ngaySinh;
        private final String sdt;
        private final String email;
        private final String gioiTinh;
        private final String diaChi;
        private final String viTriCongViec;
        private final String trangThaiLamViec;

        public NhanVienItem(String maNV, String hoTen) {
            this(maNV, hoTen, "", "", "", "", "", "", "");
        }

        public NhanVienItem(
                String maNV,
                String hoTen,
                String ngaySinh,
                String sdt,
                String email,
                String gioiTinh,
                String diaChi,
                String viTriCongViec,
                String trangThaiLamViec
        ) {
            this.maNV = maNV;
            this.hoTen = hoTen;
            this.ngaySinh = ngaySinh;
            this.sdt = sdt;
            this.email = email;
            this.gioiTinh = gioiTinh;
            this.diaChi = diaChi;
            this.viTriCongViec = viTriCongViec;
            this.trangThaiLamViec = trangThaiLamViec;
        }

        public String getMaNV() {
            return maNV;
        }

        public String getHoTen() {
            return hoTen;
        }

        public String getNgaySinh() {
            return ngaySinh;
        }

        public String getSdt() {
            return sdt;
        }

        public String getEmail() {
            return email;
        }

        public String getGioiTinh() {
            return gioiTinh;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public String getViTriCongViec() {
            return viTriCongViec;
        }

        public String getTrangThaiLamViec() {
            return trangThaiLamViec;
        }

        @Override
        public String toString() {
            return maNV + " - " + hoTen;
        }
    }
}