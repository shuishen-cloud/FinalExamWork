-- 注意：此事件脚本存在安全问题，建议在生产环境中使用外部脚本进行备份
-- 以下是一个更安全的备份事件示例，仅记录备份时间，实际备份由外部脚本执行
CREATE EVENT EV_Album_Backup
ON SCHEDULE EVERY 1 DAY STARTS '2024-01-01 01:00:00'
DO
BEGIN
    -- 记录备份操作日志，实际备份由外部脚本完成
    INSERT INTO BackupLog (BackupDate, Status) VALUES (NOW(), 'Scheduled');
END;

-- 如果需要备份日志表，请在createTable.sql中添加以下表：
-- CREATE TABLE BackupLog (
--     LogID INT PRIMARY KEY AUTO_INCREMENT,
--     BackupDate DATETIME NOT NULL,
--     Status VARCHAR(50) NOT NULL
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份日志表';
