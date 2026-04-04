package connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnectDB {
    public static void main(String[] args) {
        ConnectDB db = ConnectDB.getInstance();
        try {
            Connection connection = db.getConnection();
            System.out.println("Connected to SQL Server successfully.");
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT @@VERSION AS version")) {
                if (resultSet.next()) {
                    System.out.println("Server version: " + resultSet.getString("version"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect SQL Server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }
}
