package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectDB;
import entity.Thue;

public class Thue_Dao {

    public List<Thue> findThueDangApDung() {
        List<Thue> list = new ArrayList<>();

        String sql = """
                SELECT maThue, tenThue, trangThai, tyLeThue, moTa
                FROM Thue
                WHERE trangThai = N'Áp dụng'
                ORDER BY tyLeThue ASC
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Thue thue = new Thue(
                        rs.getString("maThue"),
                        rs.getString("tenThue"),
                        rs.getString("trangThai"),
                        rs.getDouble("tyLeThue"),
                        rs.getString("moTa")
                );

                list.add(thue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<String> findAllThueForCombo() {
        List<String> ds = new ArrayList<>();

        String sql = """
                SELECT maThue, tenThue
                FROM Thue
                ORDER BY maThue
                """;

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                String maThue = rs.getString("maThue");
                String tenThue = rs.getString("tenThue");

                ds.add(maThue + " - " + tenThue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
}