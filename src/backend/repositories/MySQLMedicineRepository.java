package backend.repositories;

import backend.models.Medicine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// MySQL implementation of MedicineRepository
public class MySQLMedicineRepository implements MedicineRepository {
    private final Connection connection;

    public MySQLMedicineRepository(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS medicines (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) UNIQUE NOT NULL, " +
                "description TEXT, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "stock_quantity INT NOT NULL DEFAULT 0, " +
                "batch_id VARCHAR(50), " +
                "expiry_date DATE, " +
                "status VARCHAR(20) DEFAULT 'Active'" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            
            // Add columns if they do not exist for backwards compatibility with existing table
            try { stmt.execute("ALTER TABLE medicines ADD COLUMN batch_id VARCHAR(50) DEFAULT 'UNKNOWN'"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE medicines ADD COLUMN expiry_date DATE DEFAULT (CURRENT_DATE + INTERVAL 1 YEAR)"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE medicines ADD COLUMN status VARCHAR(20) DEFAULT 'Active'"); } catch (SQLException ignored) {}
            
        } catch (SQLException e) {
            System.err.println("Error creating medicines table: " + e.getMessage());
        }
    }

    @Override
    public void add(Medicine medicine) {
        String sql = "INSERT INTO medicines (name, description, price, stock_quantity, batch_id, expiry_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getDescription());
            stmt.setDouble(3, medicine.getPrice());
            stmt.setInt(4, medicine.getStockQuantity());
            stmt.setString(5, medicine.getBatchId());
            if (medicine.getExpiryDate() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(medicine.getExpiryDate()));
            } else {
                stmt.setDate(6, null);
            }
            stmt.setString(7, medicine.getStatus());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    medicine.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding medicine", e);
        }
    }

    @Override
    public void update(Medicine medicine) {
        String sql = "UPDATE medicines SET name=?, description=?, price=?, stock_quantity=?, batch_id=?, expiry_date=?, status=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getDescription());
            stmt.setDouble(3, medicine.getPrice());
            stmt.setInt(4, medicine.getStockQuantity());
            stmt.setString(5, medicine.getBatchId());
            if (medicine.getExpiryDate() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(medicine.getExpiryDate()));
            } else {
                stmt.setDate(6, null);
            }
            stmt.setString(7, medicine.getStatus());
            stmt.setInt(8, medicine.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating medicine", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM medicines WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting medicine", e);
        }
    }

    @Override
    public Optional<Medicine> findById(int id) {
        return findOne("SELECT * FROM medicines WHERE id=?", id);
    }

    @Override
    public Optional<Medicine> findByName(String name) {
        return findOne("SELECT * FROM medicines WHERE name=?", name);
    }

    @Override
    public List<Medicine> findAll() {
        return findList("SELECT * FROM medicines");
    }

    @Override
    public List<Medicine> search(String keyword) {
        String sql = "SELECT * FROM medicines WHERE name LIKE ? OR description LIKE ?";
        String wildCard = "%" + keyword + "%";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, wildCard);
            stmt.setString(2, wildCard);
            ResultSet rs = stmt.executeQuery();
            List<Medicine> medicines = new ArrayList<>();
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
            return medicines;
        } catch (SQLException e) {
            throw new RuntimeException("Error searching medicines", e);
        }
    }

    // Helper methods
    private Optional<Medicine> findOne(String sql, Object param) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (param instanceof Integer) stmt.setInt(1, (Integer) param);
            else if (param instanceof String) stmt.setString(1, (String) param);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMedicine(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding medicine: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private List<Medicine> findList(String sql) {
        List<Medicine> medicines = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error listing medicines: " + e.getMessage(), e);
        }
        return medicines;
    }

    private Medicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        java.sql.Date sqlDate = rs.getDate("expiry_date");
        java.time.LocalDate expiryDate = sqlDate != null ? sqlDate.toLocalDate() : java.time.LocalDate.now().plusYears(1);
        String batchId = rs.getString("batch_id");
        if (batchId == null) batchId = "UNKNOWN";
        String status = rs.getString("status");
        if (status == null) status = "Active";

        return new Medicine(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("stock_quantity"),
                batchId,
                expiryDate,
                status
        );
    }
}