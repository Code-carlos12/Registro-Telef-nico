package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = env("DB_URL",
            "jdbc:mysql://localhost:3306/registrotelefonico?serverTimezone=UTC");
    private static final String USER = env("DB_USER", "root");
    private static final String PASSWORD = env("DB_PASSWORD", "");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }
}
