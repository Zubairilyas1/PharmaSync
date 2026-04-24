package backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/pharmasync_db";
    private static final String USER = "root";
    private static String PASSWORD = null;

    // Static block to initialize the password from the .env file once when the class is loaded
    static {
        try {
            // Read lines from the src/backend/.env file
            List<String> lines = Files.readAllLines(Paths.get("src/backend/.env"));
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("DB_PASSWORD=")) {
                    PASSWORD = line.substring("DB_PASSWORD=".length()).trim();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not read .env file. " + e.getMessage());
        }
        
        // Fallback just in case
        if (PASSWORD == null) {
            PASSWORD = ""; 
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}