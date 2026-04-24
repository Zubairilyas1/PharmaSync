package backend.repositories;

import backend.models.User;
import java.util.Optional;

// UserRepository interface for user-related database operations
public interface UserRepository {
    void save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    void update(User user);
}