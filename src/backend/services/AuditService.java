package backend.services;

import backend.models.AuditLog;
import backend.repositories.AuditLogRepository;
import java.time.LocalDateTime;
import java.util.List;

public class AuditService {
    private AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void logAction(String action, String username, String details) {
        AuditLog log = new AuditLog(0, action, username, LocalDateTime.now(), details);
        repository.add(log);
    }

    public List<AuditLog> getAllLogs() {
        return repository.findAll();
    }
}