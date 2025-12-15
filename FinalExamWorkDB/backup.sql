-- 注意：为了安全起见，在生产环境中不要使用MySQL事件调度器执行系统命令
-- 以下是一个安全的备份事件示例，仅记录备份时间，实际备份由外部脚本执行
-- 建议使用外部备份脚本和系统cron作业来执行数据库备份

-- 启用事件调度器（如果尚未启用）
-- SET GLOBAL event_scheduler = ON;

CREATE EVENT IF NOT EXISTS EV_Album_Backup
ON SCHEDULE EVERY 1 DAY STARTS DATE_ADD(CURDATE(), INTERVAL 1 HOUR)
ON COMPLETION PRESERVE
ENABLE
DO
BEGIN
    -- 记录备份操作日志，实际备份由外部脚本完成
    INSERT INTO BackupLog (BackupDate, Status, Message) VALUES (NOW(), 'Scheduled', 'Event triggered');
END;

-- 如果需要备份日志表，请确保createTable.sql中已创建BackupLog表：
/*
CREATE TABLE BackupLog (
    LogID INT PRIMARY KEY AUTO_INCREMENT,
    BackupDate DATETIME NOT NULL,
    Status VARCHAR(50) NOT NULL,
    Message TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份日志表';
*/

-- 安全备份脚本相关存储过程
DELIMITER //
CREATE PROCEDURE BackupUsersData()
BEGIN
    -- 创建Users表的备份
    DROP TABLE IF EXISTS Users_Bak;
    CREATE TABLE Users_Bak AS SELECT * FROM Users;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE BackupResourceData()
BEGIN
    -- 创建Resource表的备份
    DROP TABLE IF EXISTS Resource_Bak;
    CREATE TABLE Resource_Bak AS SELECT * FROM Resource;
END //
DELIMITER ;
