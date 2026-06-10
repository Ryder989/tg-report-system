package com.jay.tgreportsystem.service;

import com.jay.tgreportsystem.entity.DailyReport;
import com.jay.tgreportsystem.repository.DailyReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReportService {

    private final DailyReportRepository dailyReportRepository;
    private final TelegramMessageService telegramMessageService;

    public ReportService(
            DailyReportRepository dailyReportRepository,
            TelegramMessageService telegramMessageService
    ) {
        this.dailyReportRepository = dailyReportRepository;
        this.telegramMessageService = telegramMessageService;
    }

    public void sendTodayReport(Long chatId) {
        LocalDate today = LocalDate.now();

        DailyReport report = dailyReportRepository
                .findByReportDate(today)
                .orElse(null);

        if (report == null) {
            telegramMessageService.sendText(chatId, "今日尚無報表資料：" + today);
            return;
        }

        String message = """
                📊 今日總覽 %s

                總會員數：%s
                活躍會員：%s
                新增會員：%s

                總輸贏：%s
                返水支出：%s
                淨利潤：%s
                """.formatted(
                report.getReportDate(),
                report.getTotalMembers(),
                report.getActiveMembers(),
                report.getNewMembers(),
                report.getTotalWinLose(),
                report.getRebateAmount(),
                report.getNetProfit()
        );

        telegramMessageService.sendText(chatId, message);
    }
}