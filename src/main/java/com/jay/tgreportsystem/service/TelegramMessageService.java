package com.jay.tgreportsystem.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramMessageService {

    private TelegramLongPollingBot bot;

    public void setBot(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendText(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        executeMessage(message);
    }

    public void executeMessage(SendMessage message) {
        try {
            if (bot == null) {
                throw new IllegalStateException("Telegram bot 尚未初始化");
            }

            bot.execute(message);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}