package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private static final ConnectDB INSTANCE = new ConnectDB();
    private Connection connection;

    private ConnectDB() {
    }

    public static ConnectDB getInstance() {
        return INSTANCE;
    }

    public synchronized void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = DriverManager.getConnection(buildJdbcUrl(), getDbUser(), getDbPassword());
    }

    public synchronized Connection getConnection() throws SQLException {
        connect();
        return connection;
    }

    public synchronized void disconnect() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
            // Close quietly, this is best-effort cleanup.
        } finally {
            connection = null;
        }
    }

    private String buildJdbcUrl() {
        String host = getEnvOrDefault("DB_HOST", "127.0.0.1");
        String port = getEnvOrDefault("DB_PORT", "1433");
        String database = getEnvOrDefault("DB_NAME", "HotelManagement");
        String encrypt = getEnvOrDefault("DB_ENCRYPT", "true");
        String trustServerCert = getEnvOrDefault("DB_TRUST_SERVER_CERT", "true");
        String loginTimeout = getEnvOrDefault("DB_LOGIN_TIMEOUT", "10");
        return "jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=" + database
                + ";encrypt=" + encrypt
                + ";trustServerCertificate=" + trustServerCert
                + ";loginTimeout=" + loginTimeout;
    }

    private String getDbUser() {
        return getEnvOrDefault("DB_USER", "sa");
    }

    private String getDbPassword() {
        return getEnvOrDefault("DB_PASSWORD", "");
    }

    private String getEnvOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}
