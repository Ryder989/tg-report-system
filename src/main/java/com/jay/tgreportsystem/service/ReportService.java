package com.jay.tgreportsystem.service;

import com.jay.tgreportsystem.entity.DailyReport;
import com.jay.tgreportsystem.repository.DailyReportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.time.temporal.ChronoUnit;

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

    public void sendCustomReport(Long chatId, String text) {

        try {

            String[] parts = text.split("\\s+");

            LocalDate startDate = LocalDate.parse(parts[1]);

            LocalDate endDate = LocalDate.parse(parts[2]);

            long dayDiff = ChronoUnit.DAYS.between(startDate,endDate);

            if(dayDiff < 0){telegramMessageService
                    .sendText(chatId,"結束日期不能小於開始日期");
                return;
            }
            if(dayDiff > 90){telegramMessageService
                    .sendText(chatId,"查詢區間不可超過90天");
                return;
            }

            List<DailyReport> reports = dailyReportRepository
                            .findByReportDateBetween(startDate, endDate);

            if(reports.isEmpty()){
                telegramMessageService.sendText(chatId, "查無資料");
                return;
            }

            Integer totalMembers = 0;
            Integer activeMembers = 0;
            Integer newMembers = 0;

            BigDecimal totalWinLose = BigDecimal.ZERO;
            BigDecimal rebateAmount = BigDecimal.ZERO;
            BigDecimal netProfit = BigDecimal.ZERO;

            for(DailyReport report : reports){

                totalMembers += report.getTotalMembers();
                activeMembers += report.getActiveMembers();
                newMembers += report.getNewMembers();

                totalWinLose = totalWinLose.add(report.getTotalWinLose());
                rebateAmount = rebateAmount.add(report.getRebateAmount());
                netProfit = netProfit.add(report.getNetProfit());
            }

            telegramMessageService.sendText(
                    chatId,
                    """
                    📅 區間報表
    
                    起始：%s
                    結束：%s
    
                    總會員數：%,d
                    活躍會員：%,d
                    新增會員：%,d
    
                    總輸贏：%,.0f
                    返水支出：%,.0f
                    淨利潤：%,.0f
                    """.formatted(
                            startDate,
                            endDate,
                            totalMembers,
                            activeMembers,
                            newMembers,
                            totalWinLose.doubleValue(),
                            rebateAmount.doubleValue(),
                            netProfit.doubleValue()
                    )
            );

        } catch (Exception e) {

            telegramMessageService.sendText(
                    chatId,
                    """
                    格式錯誤
    
                    報表 起始日期 結束日期
    
                    範例：
    
                    報表 2026-06-01 2026-06-11
                    """
            );
        }
    }
}