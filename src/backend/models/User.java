package backend.models;

public class User {
    private String username;
    private String email;
    private String passwordHash;

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}