package com.jay.tgreportsystem.service;

import com.jay.tgreportsystem.entity.TelegramUser;
import com.jay.tgreportsystem.repository.TelegramUserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UserService {

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramMessageService telegramMessageService;

    public UserService(
            TelegramUserRepository telegramUserRepository,
            TelegramMessageService telegramMessageService
    ) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramMessageService = telegramMessageService;
    }

    public TelegramUser loginOrRegister(Update update, Long chatId) {

        Long telegramUserId = update.getMessage().getFrom().getId();

        TelegramUser loginUser = telegramUserRepository
                .findByTelegramUserId(telegramUserId)
                .orElse(null);

        if (loginUser == null) {
            TelegramUser newUser = new TelegramUser();

            newUser.setTelegramUserId(telegramUserId);
            newUser.setUsername(update.getMessage().getFrom().getUserName());
            newUser.setDisplayName(update.getMessage().getFrom().getFirstName());
            newUser.setRoleCode("MEMBER");
            newUser.setLevel_id(7);
            newUser.setEnabled(false);

            telegramUserRepository.save(newUser);

            telegramMessageService.sendText(chatId, """
                    ✅ 帳號建立成功

                    帳號已送出審核。

                    請聯繫上層主管開通權限。
                    """);

            return null;
        }

        if (!Boolean.TRUE.equals(loginUser.getEnabled())) {
            telegramMessageService.sendText(chatId, """
                    ⏳ 帳號審核中

                    請聯繫上層主管開通權限。
                    """);

            return null;
        }

        return loginUser;
    }
}