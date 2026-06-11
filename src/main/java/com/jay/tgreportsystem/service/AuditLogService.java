package com.jay.tgreportsystem.service;

import com.jay.tgreportsystem.entity.AuditLog;
import com.jay.tgreportsystem.entity.TelegramUser;
import com.jay.tgreportsystem.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository){
        this.auditLogRepository = auditLogRepository;
    }

    public void saveLog(
            TelegramUser operator,
            TelegramUser target,
            String actionType,
            String oldRole,
            String newRole,
            Integer oldLevel,
            Integer newLevel,
            String actionremark
    ){
        AuditLog log = new AuditLog();

        log.setOperatorId(operator.getTelegramUserId());
        log.setOperatorName(operator.getDisplayName());

        log.setTargetUserId(target.getTelegramUserId());
        log.setTargetUsername(target.getDisplayName());

        log.setActionType(actionType);

        log.setOldRoleCode(oldRole);
        log.setNewRoleCode(newRole);

        log.setOldLevelId(oldLevel);
        log.setNewLevelId(newLevel);

        log.setActionTime(LocalDateTime.now());
        log.setActionRemark(actionremark);

        auditLogRepository.save(log);
    }
}
