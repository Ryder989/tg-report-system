package com.jay.tgreportsystem.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
/// 使用者log TABLE
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "target_username")
    private String targetUsername;

    @Column(name = "old_role_code")
    private String oldRoleCode;

    @Column(name = "new_role_code")
    private String newRoleCode;

    @Column(name = "old_level_id")
    private Integer oldLevelId;

    @Column(name = "new_level_id")
    private Integer newLevelId;

    @Column(name = "action_remark")
    private String actionRemark;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

}
