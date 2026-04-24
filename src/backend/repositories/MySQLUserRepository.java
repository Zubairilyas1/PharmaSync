package backend.repositories;

import backend.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MySQLUserRepository implements UserRepository {

    private final Connection connection;

    public MySQLUserRepository(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }
    // creating table if not exists in the constructor so that we don't have to worry about it later. This way, the first time we create a MySQLUserRepository, it will ensure the table is there. If it already exists, the "IF NOT EXISTS" clause will prevent any errors.
    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL" +
                ");";
        try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
        }
    }

    @Override
    // to save data with handling of special characters and SQL injection prevention using PreparedStatement
    public void save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user to MySQL", e);
        }
    }

    @Override
    // to find user by email
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return findUserBySql(sql, email);
    }

    @Override
    // find by username
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return findUserBySql(sql, username);
    }

    @Override
    // update user password hash based on email as the unique identifier. We could also choose to update based on username, but email is often more unique and less likely to change than username.
    public void update(User user) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getPasswordHash());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user in MySQL", e);
        }
    }

    // Helper method to avoid code duplication when finding user by either email or username
    private Optional<User> findUserBySql(String sql, String parameter) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, parameter);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }
}