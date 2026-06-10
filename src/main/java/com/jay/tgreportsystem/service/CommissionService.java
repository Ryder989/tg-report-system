package com.jay.tgreportsystem.service;

import org.springframework.stereotype.Service;

@Service
public class CommissionService {

    private final TelegramMessageService telegramMessageService;

    public CommissionService(TelegramMessageService telegramMessageService) {
        this.telegramMessageService = telegramMessageService;
    }

    public void calculateCommission(Long chatId, String text) {
        try {
            String[] parts = text.split("\\s+");

            if (parts.length != 3) {
                telegramMessageService.sendText(chatId, "格式錯誤，請輸入：分潤計算 100000 30");
                return;
            }

            double amount = Double.parseDouble(parts[1]);
            double rate = Double.parseDouble(parts[2]);
            double result = amount * rate / 100;

            telegramMessageService.sendText(chatId, """
                    🧮 分潤計算結果

                    輸贏金額：%,.0f
                    分潤比例：%.2f%%
                    分潤金額：%,.0f
                    """.formatted(amount, rate, result));

        } catch (Exception e) {
            telegramMessageService.sendText(chatId, "格式錯誤，請輸入：分潤計算 100000 30");
        }
    }
}