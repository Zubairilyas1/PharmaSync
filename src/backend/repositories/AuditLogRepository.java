package backend.repositories;

import backend.models.AuditLog;
import java.util.List;

public interface AuditLogRepository {
    void add(AuditLog log);
    List<AuditLog> findAll();
}