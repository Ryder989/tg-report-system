package com.jay.tgreportsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "telegram_user")
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    private String username;

    private String displayName;

    private String roleCode;

    private Boolean enabled;

    private Integer level_id;
}
