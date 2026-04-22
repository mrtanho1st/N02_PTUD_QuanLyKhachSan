package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.NhanVien;

public class NhanVien_Dao {

    public List<NhanVien> findAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, " +
                     "trangThaiLamViec, diaChi, caLamViec, viTriCongViec FROM NhanVien";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                NhanVien nv = new NhanVien(
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
                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public NhanVien findById(String maNV) {
        String sql = "SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, " +
                     "trangThaiLamViec, diaChi, caLamViec, viTriCongViec FROM NhanVien WHERE maNV = ?";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, maNV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien(maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, " +
                     "trangThaiLamViec, diaChi, caLamViec, viTriCongViec) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        String sql = "UPDATE NhanVien SET hoTen = ?, ngaySinh = ?, sdt = ?, email = ?, gioiTinh = ?, " +
                     "ngayBatDauVaoLam = ?, trangThaiLamViec = ?, diaChi = ?, caLamViec = ?, viTriCongViec = ? " +
                     "WHERE maNV = ?";

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

    public List<NhanVien> search(String maNV, String hoTen, String trangThai) {
        List<NhanVien> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, " +
            "trangThaiLamViec, diaChi, caLamViec, viTriCongViec FROM NhanVien WHERE 1=1"
        );

        if (maNV != null && !maNV.isBlank()) {
            sql.append(" AND maNV LIKE ?");
        }
        if (hoTen != null && !hoTen.isBlank()) {
            sql.append(" AND hoTen LIKE ?");
        }
        if (trangThai != null && !"Tất cả".equals(trangThai)) {
            sql.append(" AND trangThaiLamViec = ?");
        }

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if (maNV != null && !maNV.isBlank()) {
                ps.setString(index++, "%" + maNV + "%");
            }
            if (hoTen != null && !hoTen.isBlank()) {
                ps.setString(index++, "%" + hoTen + "%");
            }
            if (trangThai != null && !"Tất cả".equals(trangThai)) {
                ps.setString(index++, trangThai);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien(
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
                    list.add(nv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}