package backend.models;

import java.time.LocalDateTime;

public class AuditLog {
    private int id;
    private String action;
    private String username;
    private LocalDateTime timestamp;
    private String details;

    public AuditLog(int id, String action, String username, LocalDateTime timestamp, String details) {
        this.id = id;
        this.action = action;
        this.username = username;
        this.timestamp = timestamp;
        this.details = details;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}