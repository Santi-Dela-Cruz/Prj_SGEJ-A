package application.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class RunSeeder {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:src/main/resources/database/sgej_database.db"; // Ruta centralizada

        try (Connection conn = DriverManager.getConnection(url)) {
            ClienteSeeder.seed(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
