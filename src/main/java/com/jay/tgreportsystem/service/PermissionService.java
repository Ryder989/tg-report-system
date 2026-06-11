package com.jay.tgreportsystem.service;

import com.jay.tgreportsystem.entity.TelegramUser;
import com.jay.tgreportsystem.repository.TelegramUserRepository;
import org.springframework.stereotype.Service;
import com.jay.tgreportsystem.constant.AuditAction;

import java.util.List;

@Service
public class PermissionService {

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramMessageService telegramMessageService;
    private final MenuService menuService;
    private AuditLogService auditLogService;


    public PermissionService(
            TelegramUserRepository telegramUserRepository,
            TelegramMessageService telegramMessageService,
            MenuService menuService,
            AuditLogService auditLogService) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramMessageService = telegramMessageService;
        this.menuService = menuService;
        this.auditLogService = auditLogService;
    }

    public void openPermissionMenu(Long chatId, TelegramUser loginUser) {
        if (!hasPermission(loginUser, 1)) {
            noPermission(chatId);
            return;
        }

        menuService.sendPermissionMenu(chatId);
    }

    /// 2026/06/12 待審核帳號
    public void showPendingUsers(Long chatId, TelegramUser loginUser) {
        if (!hasPermission(loginUser, 1)) {
            noPermission(chatId);
            return;
        }

        List<TelegramUser> users = telegramUserRepository.findByEnabledFalse();

        if (users.isEmpty()) {
            telegramMessageService.sendText(chatId, "目前沒有待審核帳號");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📋 待審核帳號\n\n");

        for (TelegramUser user : users) {
            sb.append("TG ID：")
                    .append(user.getTelegramUserId())
                    .append("\n");

            sb.append("姓名：")
                    .append(user.getDisplayName())
                    .append("\n");

            sb.append("Username：")
                    .append(user.getUsername())
                    .append("\n\n");
        }

        sb.append("""
                開通方式：

                開通 TGID LEVEL

                範例：
                開通 123456789 6
                """);

        telegramMessageService.sendText(chatId, sb.toString());
    }

    /// 2026/06/12 開通權限
    public void approveUser(Long chatId, TelegramUser loginUser, String text) {
        if (!hasPermission(loginUser, 1)) {
            noPermission(chatId);
            return;
        }

        try {
            String[] parts = text.split("\\s+");

            if (parts.length != 3) {
                telegramMessageService.sendText(chatId, "格式錯誤：開通 TGID LEVEL");
                return;
            }

            Long tgId = Long.parseLong(parts[1]);
            Integer levelId = Integer.parseInt(parts[2]);

            if (!isValidLevel(levelId)) {
                telegramMessageService.sendText(chatId, "Level 錯誤，請輸入 1~7");
                return;
            }

            TelegramUser user = telegramUserRepository
                    .findByTelegramUserId(tgId)
                    .orElse(null);

            if (user == null) {
                telegramMessageService.sendText(chatId, "找不到使用者");
                return;
            }

            String oldRole = user.getRoleCode();
            Integer oldLevel = user.getLevel_id();

            user.setEnabled(true);
            user.setLevel_id(levelId);
            user.setRoleCode(getRoleCodeByLevel(levelId));

            telegramUserRepository.save(user);

            auditLogService.saveLog(loginUser,user,AuditAction.OPEN,oldRole, user.getRoleCode(),oldLevel,user.getLevel_id(),"開通權限");

            telegramMessageService.sendText(chatId, """
                    ✅ 開通成功

                    TG ID：%s
                    Level：%s
                    Role：%s
                    """.formatted(
                    tgId,
                    levelId,
                    getRoleCodeByLevel(levelId)
            ));

        } catch (Exception e) {
            telegramMessageService.sendText(chatId, """
                    格式錯誤

                    開通 TGID LEVEL

                    範例：
                    開通 123456789 6
                    """);
        }
    }

    /// 2026/06/12 修改權限
    public void modifyUser(Long chatId, TelegramUser loginUser, String text) {
        if (!hasPermission(loginUser, 1)) {
            noPermission(chatId);
            return;
        }

        try {
            String[] parts = text.split("\\s+");

            if (parts.length != 3) {
                telegramMessageService.sendText(chatId, "格式錯誤：修改 TGID LEVEL");
                return;
            }

            Long tgId = Long.parseLong(parts[1]);
            Integer levelId = Integer.parseInt(parts[2]);

            if (!isValidLevel(levelId)) {
                telegramMessageService.sendText(chatId, "Level 錯誤，請輸入 1~7");
                return;
            }

            TelegramUser user = telegramUserRepository
                    .findByTelegramUserId(tgId)
                    .orElse(null);

            if (user == null) {
                telegramMessageService.sendText(chatId, "找不到使用者");
                return;
            }

            if (user.getLevel_id() == 1) {
                telegramMessageService.sendText(chatId, "無法修改總監權限");
                return;
            }

            String oldRole = user.getRoleCode();
            Integer oldLevel = user.getLevel_id();


            user.setLevel_id(levelId);
            user.setRoleCode(getRoleCodeByLevel(levelId));

            telegramUserRepository.save(user);

            auditLogService.saveLog(loginUser,user,AuditAction.MODIFY,oldRole, user.getRoleCode(),oldLevel,user.getLevel_id(),"修改權限");

            telegramMessageService.sendText(chatId, """
                    ✅ 權限修改成功

                    TG ID：%s
                    Level：%s
                    Role：%s
                    """.formatted(
                    tgId,
                    levelId,
                    getRoleCodeByLevel(levelId)
            ));

        } catch (Exception e) {
            telegramMessageService.sendText(chatId, """
                    格式錯誤

                    修改 TGID LEVEL

                    範例：
                    修改 123456789 3
                    """);
        }
    }

    /// 2026/06/12 停用帳號
    public void disableUser(Long chatId, TelegramUser loginUser, String text) {
        if (!hasPermission(loginUser, 1)) {
            noPermission(chatId);
            return;
        }

        try {
            String[] parts = text.split("\\s+");

            if (parts.length != 2) {
                telegramMessageService.sendText(chatId, "格式錯誤：停用 TGID");
                return;
            }

            Long tgId = Long.parseLong(parts[1]);

            TelegramUser user = telegramUserRepository
                    .findByTelegramUserId(tgId)
                    .orElse(null);

            if (user == null) {
                telegramMessageService.sendText(chatId, "找不到使用者");
                return;
            }

            // 禁止停用總監
            if (user.getLevel_id() == 1){
                telegramMessageService.sendText(chatId,"無法停用總監");
                return;
            }

            String oldRole = user.getRoleCode();
            Integer oldLevel = user.getLevel_id();

            user.setEnabled(false);
            telegramUserRepository.save(user);

            auditLogService.saveLog(loginUser,user,AuditAction.DISABLE,oldRole, user.getRoleCode(),oldLevel,user.getLevel_id(),"帳號停用");

            telegramMessageService.sendText(chatId, """
                    🚫 帳號停用成功

                    TG ID：%s
                    姓名：%s
                    """.formatted(
                    user.getTelegramUserId(),
                    user.getDisplayName()
            ));

        } catch (Exception e) {
            telegramMessageService.sendText(chatId, """
                    格式錯誤

                    停用 TGID

                    範例：
                    停用 123456789
                    """);
        }
    }

    /// 2026/06/12 啟用帳號
    public void enableUser(Long chatId, TelegramUser loginUser, String text) {
        if (!hasPermission(loginUser, 1)) {
            noPermission(chatId);
            return;
        }

        try {
            String[] parts = text.split("\\s+");

            if (parts.length != 2) {
                telegramMessageService.sendText(chatId, "格式錯誤：啟用 TGID");
                return;
            }

            Long tgId = Long.parseLong(parts[1]);

            TelegramUser user = telegramUserRepository
                    .findByTelegramUserId(tgId)
                    .orElse(null);

            if (user == null) {
                telegramMessageService.sendText(chatId, "找不到使用者");
                return;
            }

            String oldRole = user.getRoleCode();
            Integer oldLevel = user.getLevel_id();

            user.setEnabled(true);
            telegramUserRepository.save(user);

            auditLogService.saveLog(loginUser,user,AuditAction.ENABLE,oldRole, user.getRoleCode(),oldLevel,user.getLevel_id(),"帳號啟用");

            telegramMessageService.sendText(chatId, """
                    🔓 帳號啟用成功

                    TG ID：%s
                    姓名：%s
                    """.formatted(
                    user.getTelegramUserId(),
                    user.getDisplayName()
            ));

        } catch (Exception e) {
            telegramMessageService.sendText(chatId, """
                    格式錯誤

                    啟用 TGID

                    範例：
                    啟用 123456789
                    """);
        }
    }

    /// 2026/06/12 查詢TG ID
    public void queryUser(Long chatId, TelegramUser loginUser, String text) {
        if (!hasPermission(loginUser, 1)) {
            noPermission(chatId);
            return;
        }

        try {
            String[] parts = text.split("\\s+");

            if (parts.length != 2) {
                telegramMessageService.sendText(chatId, "格式錯誤：查詢 TGID");
                return;
            }

            Long tgId = Long.parseLong(parts[1]);

            TelegramUser user = telegramUserRepository
                    .findByTelegramUserId(tgId)
                    .orElse(null);

            if (user == null) {
                telegramMessageService.sendText(chatId, "找不到使用者");
                return;
            }

            telegramMessageService.sendText(chatId, """
                    👤 使用者資訊

                    TG ID：%s
                    Username：%s
                    姓名：%s
                    Role：%s
                    Level：%s
                    啟用：%s
                    """.formatted(
                    user.getTelegramUserId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getRoleCode(),
                    user.getLevel_id(),
                    user.getEnabled()
            ));

        } catch (Exception e) {
            telegramMessageService.sendText(chatId, """
                    格式錯誤

                    查詢 TGID

                    範例：
                    查詢 123456789
                    """);
        }
    }

    /// 2026/06/12 是否有權限控管
    public boolean hasPermission(TelegramUser user, int maxLevel) {
        return user != null
                && user.getLevel_id() != null
                && user.getLevel_id() <= maxLevel;
    }

    /// 2026/06/12 無權限
    public void noPermission(Long chatId) {
        telegramMessageService.sendText(chatId, """
                ⛔ 權限不足

                請聯繫上層主管。
                """);
    }

    private boolean isValidLevel(Integer level) {
        return level != null && level >= 1 && level <= 7;
    }

    /// 2026/06/12 角色等級對應名
    private String getRoleCodeByLevel(Integer level) {
        return switch (level) {
            case 1 -> "DIRECTOR";
            case 2 -> "BIG_SHAREHOLDER";
            case 3 -> "SHAREHOLDER";
            case 4 -> "SUB_SHAREHOLDER";
            case 5 -> "MASTER_AGENT";
            case 6 -> "AGENT";
            case 7 -> "MEMBER";
            default -> "MEMBER";
        };
    }
}