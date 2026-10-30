
package backend;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        try {
            Connection conn = Database.getConnection();
            if (conn != null) {
                System.out.println("Conexión exitosa!");
                conn.close();
            } else {
                System.out.println("Conexión fallida!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}