CREATE EVENT EV_Album_Backup
ON SCHEDULE EVERY 1 DAY STARTS '2024-01-01 01:00:00'
DO
BEGIN
    SET @backup_file = CONCAT('/backup/SimpleAlbumDB_', DATE_FORMAT(NOW(), '%Y%m%d'), '.sql');
    SET @backup_sql = CONCAT('mysqldump -uroot -p123456 SimpleAlbumDB > ', @backup_file);
    PREPARE stmt FROM @backup_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    -- 删除15天前的备份
    SET @del_sql = CONCAT('rm -f /backup/SimpleAlbumDB_', DATE_FORMAT(NOW()-INTERVAL 15 DAY, '%Y%m%d'), '.sql');
    PREPARE stmt FROM @del_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END;
