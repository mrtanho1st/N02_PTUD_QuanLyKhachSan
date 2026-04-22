package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.DichVu;

public class DichVu_Dao {

    public List<DichVu> findAll() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT maDV, tenDichVu, giaDichVu FROM DichVu";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new DichVu(
                        rs.getString("maDV"),
                        rs.getString("tenDichVu"),
                        rs.getDouble("giaDichVu")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}