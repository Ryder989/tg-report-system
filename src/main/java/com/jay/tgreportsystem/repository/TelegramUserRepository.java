package com.jay.tgreportsystem.repository;

import com.jay.tgreportsystem.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByTelegramUserIdAndEnabledTrue(Long telegramUserId);

    Optional<TelegramUser> findByTelegramUserId(Long telegramUserId);

    List<TelegramUser> findByEnabledFalse();
}