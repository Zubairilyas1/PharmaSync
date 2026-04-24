package backend.services;

import backend.models.User;
import backend.repositories.UserRepository;
import backend.exceptions.AuthenticationException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

// Service class for handling user authentication logic, including login, signup, and password reset. It interacts with the UserRepository to perform database operations and includes basic validation and error handling.
public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(String identifier, String rawPassword) throws AuthenticationException {
        Optional<User> userOpt = identifier.contains("@") 
            ? userRepository.findByEmail(identifier) 
            : userRepository.findByUsername(identifier);

        if (userOpt.isEmpty()) {
            throw new AuthenticationException("User not found with the provided identifier.");
        }

        User user = userOpt.get();
        if (!hashPassword(rawPassword).equals(user.getPasswordHash())) {
            throw new AuthenticationException("Incorrect password.");
        }
    }

    public void signup(String username, String email, String rawPassword) throws AuthenticationException {
        // Validate email
        if (email == null || !email.contains("@")) {
            throw new AuthenticationException("Invalid email format. Email must contain '@'.");
        }
        
        // Validate username and password
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters long.");
        }

        // Check for existing users
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthenticationException("An account with this email already exists.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new AuthenticationException("This username is already taken.");
        }
        
        User newUser = new User(username, email, hashPassword(rawPassword));
        userRepository.save(newUser);
    }
    // reset password with validation
    public void resetPassword(String email, String newPassword) throws AuthenticationException {
        if (newPassword == null || newPassword.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters long.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("User not found with this email.");
        }

        User user = userOpt.get();
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.update(user);
    }
    // Helper method to hash passwords using SHA-256.
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Helper method to convert byte array to hex string for storing hashed passwords.
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}