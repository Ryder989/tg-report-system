package com.jay.tgreportsystem.repository;

import com.jay.tgreportsystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
}
