package backend.repositories;

import backend.models.Sale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLSaleRepository implements SaleRepository {
    private Connection connection;

    public MySQLSaleRepository(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS sales (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "medicine_id INT NOT NULL," +
                     "quantity INT NOT NULL," +
                     "total_price DECIMAL(10,2) NOT NULL," +
                     "timestamp DATETIME NOT NULL," +
                     "completed_by VARCHAR(100)," +
                     "FOREIGN KEY (medicine_id) REFERENCES medicines(id)" +
                     ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Sale sale) {
        String sql = "INSERT INTO sales (medicine_id, quantity, total_price, timestamp, completed_by) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, sale.getMedicineId());
            pstmt.setInt(2, sale.getQuantity());
            pstmt.setDouble(3, sale.getTotalPrice());
            pstmt.setTimestamp(4, Timestamp.valueOf(sale.getTimestamp()));
            pstmt.setString(5, sale.getCompletedBy());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sale.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY timestamp DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sales.add(new Sale(
                    rs.getInt("id"),
                    rs.getInt("medicine_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getString("completed_by")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }
}