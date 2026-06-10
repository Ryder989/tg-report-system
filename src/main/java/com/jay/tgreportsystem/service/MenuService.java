package com.jay.tgreportsystem.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Service
public class MenuService {

    private final TelegramMessageService telegramMessageService;

    public MenuService(TelegramMessageService telegramMessageService) {
        this.telegramMessageService = telegramMessageService;
    }

    public void sendMainMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("""
                歡迎使用文昌系統 Bot

                請選擇下方功能：
                """);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("👥 會員查詢");
        row1.add("💰 上下分紀錄");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("📊 今日報表");
        row2.add("🏆 代理排行");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("🚨 異常提醒");
        row3.add("🧍 流失會員");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("🧮 分潤計算");
        row4.add("🤖 AI客服");

        KeyboardRow row5 = new KeyboardRow();
        row5.add("⚙️ 系統設定");
        row5.add("🔐 權限管理");

        keyboard.setKeyboard(List.of(row1, row2, row3, row4, row5));
        message.setReplyMarkup(keyboard);

        telegramMessageService.executeMessage(message);
    }

    public void sendPermissionMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🔐 權限管理");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("✅ 開通帳號");
        row1.add("✏️ 修改權限");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("🚫 停用帳號");
        row2.add("🔓 啟用帳號");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("📋 待審核名單");
        row3.add("🔍 查詢使用者");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("⬅️ 返回主選單");

        keyboard.setKeyboard(List.of(row1, row2, row3, row4));
        message.setReplyMarkup(keyboard);

        telegramMessageService.executeMessage(message);
    }
}