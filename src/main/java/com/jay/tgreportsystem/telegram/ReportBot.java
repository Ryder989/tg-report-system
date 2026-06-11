package com.jay.tgreportsystem.telegram;

import com.jay.tgreportsystem.entity.TelegramUser;
import com.jay.tgreportsystem.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ReportBot extends TelegramLongPollingBot {

    private final TelegramMessageService telegramMessageService;
    private final UserService userService;
    private final MenuService menuService;
    private final ReportService reportService;
    private final PermissionService permissionService;
    private final CommissionService commissionService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public ReportBot(
            @Value("${telegram.bot.token}") String botToken,
            TelegramMessageService telegramMessageService,
            UserService userService,
            MenuService menuService,
            ReportService reportService,
            PermissionService permissionService,
            CommissionService commissionService
    ) {
        super(botToken);
        this.telegramMessageService = telegramMessageService;
        this.userService = userService;
        this.menuService = menuService;
        this.reportService = reportService;
        this.permissionService = permissionService;
        this.commissionService = commissionService;

        this.telegramMessageService.setBot(this);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        TelegramUser loginUser = userService.loginOrRegister(update, chatId);

        if (loginUser == null) {
            return;
        }

        switch (text) {
            case "/start", "主選單", "⬅️ 返回主選單" -> {
                reportService.sendTodayReport(chatId);
                menuService.sendMainMenu(chatId);
            }

            case "📊 今日報表" -> reportService.sendTodayReport(chatId);

            case "📅 自訂報表" -> {telegramMessageService.sendText(
                        chatId,
                        """
                        請輸入：
            
                        報表 起始日期 結束日期
            
                        範例：
            
                        報表 2026-06-01 2026-06-11
                        """
                );
            }

            case "👥 會員查詢" -> telegramMessageService.sendText(chatId, """
                    👥 會員查詢

                    請輸入：
                    會員查詢 會員ID

                    例如：
                    會員查詢 88888
                    """);

            case "💰 上下分紀錄" -> telegramMessageService.sendText(chatId, "💰 上下分紀錄功能開發中");

            case "🏆 代理排行" -> telegramMessageService.sendText(chatId, "🏆 代理排行功能開發中");

            case "🚨 異常提醒" -> telegramMessageService.sendText(chatId, "🚨 異常提醒功能開發中");

            case "🧍 流失會員" -> telegramMessageService.sendText(chatId, "🧍 流失會員功能開發中");

            case "🧮 分潤計算" -> telegramMessageService.sendText(chatId, """
                    🧮 分潤計算

                    請輸入：
                    分潤計算 輸贏金額 比例

                    例如：
                    分潤計算 100000 30
                    """);

            case "🤖 AI客服" -> telegramMessageService.sendText(chatId, "🤖 AI客服功能開發中");

            case "⚙️ 系統設定" -> telegramMessageService.sendText(chatId, "⚙️ 系統設定功能開發中");

            case "🔐 權限管理" -> permissionService.openPermissionMenu(chatId, loginUser);

            case "✅ 開通帳號" -> telegramMessageService.sendText(chatId, """
                    請輸入：

                    開通 TGID LEVEL

                    範例：
                    開通 100000001 6
                    """);

            case "✏️ 修改權限" -> telegramMessageService.sendText(chatId, """
                    請輸入：

                    修改 TGID LEVEL

                    範例：
                    修改 100000001 3
                    """);

            case "🚫 停用帳號" -> telegramMessageService.sendText(chatId, """
                    請輸入：

                    停用 TGID

                    範例：
                    停用 100000001
                    """);

            case "🔓 啟用帳號" -> telegramMessageService.sendText(chatId, """
                    請輸入：

                    啟用 TGID

                    範例：
                    啟用 100000001
                    """);

            case "📋 待審核名單" -> permissionService.showPendingUsers(chatId, loginUser);

            case "🔍 查詢使用者" -> telegramMessageService.sendText(chatId, """
                    請輸入：

                    查詢 TGID

                    範例：
                    查詢 100000001
                    """);

            default -> handleTextCommand(chatId, text, loginUser);
        }
    }

    private void handleTextCommand(Long chatId, String text, TelegramUser loginUser) {

        if (text.startsWith("開通 ")) {
            permissionService.approveUser(chatId, loginUser, text);
            return;
        }

        if (text.startsWith("修改 ")) {
            permissionService.modifyUser(chatId, loginUser, text);
            return;
        }

        if (text.startsWith("停用 ")) {
            permissionService.disableUser(chatId, loginUser, text);
            return;
        }

        if (text.startsWith("啟用 ")) {
            permissionService.enableUser(chatId, loginUser, text);
            return;
        }

        if (text.startsWith("查詢 ")) {
            permissionService.queryUser(chatId, loginUser, text);
            return;
        }

        if (text.startsWith("會員查詢 ")) {
            String memberId = text.replace("會員查詢 ", "").trim();
            telegramMessageService.sendText(chatId, "會員查詢功能開發中，會員ID：" + memberId);
            return;
        }

        if (text.startsWith("分潤計算 ")) {
            commissionService.calculateCommission(chatId, text);
            return;
        }

        if(text.startsWith("報表 ")){
            reportService.sendCustomReport(chatId, text);
            return;
        }

        telegramMessageService.sendText(chatId, "查無此指令，請點選下方按鈕或輸入 /start。");
    }
}