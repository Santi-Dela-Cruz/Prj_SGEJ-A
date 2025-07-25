package application.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class RunSeeder {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:sgej_database.db"; // Cambia esta ruta si usas otro archivo .db

        try (Connection conn = DriverManager.getConnection(url)) {
            ClienteSeeder.seed(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
