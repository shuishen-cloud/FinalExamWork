-- 1. 上传照片扣减存储容量
DELIMITER //
CREATE TRIGGER TR_Photo_Storage BEFORE INSERT ON Resource
FOR EACH ROW
BEGIN
    DECLARE storage BIGINT DEFAULT 0;
    DECLARE user_count INT DEFAULT 0;
    DECLARE album_count INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    -- 检查用户是否存在（因为分区表不支持外键约束，需要在触发器中检查）
    SELECT COUNT(*), IFNULL(MAX(RemainingStorage), 0) INTO user_count, storage 
    FROM Users WHERE UserID = NEW.UserID;
    
    IF user_count = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '用户不存在';
    END IF;
    
    -- 仅对照片类型（ResType=2）校验存储
    IF NEW.ResType = 2 THEN
        IF storage < NEW.Ext3 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '存储容量不足';
        END IF;
        
        UPDATE Users SET RemainingStorage = RemainingStorage - NEW.Ext3 WHERE UserID = NEW.UserID;
        
        -- 检查关联相册是否存在且属于该用户
        IF NEW.RelateID IS NOT NULL THEN
            SELECT COUNT(*) INTO album_count
            FROM Resource 
            WHERE ResID = NEW.RelateID AND ResType = 1 AND UserID = NEW.UserID;
            
            IF album_count = 0 THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '关联相册不存在或不属于当前用户';
            END IF;
            
            -- 更新所属相册的照片数量（使用复合主键）
            UPDATE Resource SET Ext3 = IFNULL(Ext3, 0) + 1 
            WHERE ResID = NEW.RelateID AND ResType = 1 AND UserID = NEW.UserID;
        END IF;
    END IF;
END //

-- 2. 删除照片归还存储容量
CREATE TRIGGER TR_Photo_DelStorage AFTER DELETE ON Resource
FOR EACH ROW
BEGIN
    DECLARE user_count INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    -- 检查用户是否存在（因为分区表不支持外键约束，需要在触发器中检查）
    SELECT COUNT(*) INTO user_count
    FROM Users WHERE UserID = OLD.UserID;
    
    -- 仅对照片类型（ResType=2）进行处理
    IF OLD.ResType = 2 AND user_count > 0 THEN
        UPDATE Users SET RemainingStorage = RemainingStorage + OLD.Ext3 WHERE UserID = OLD.UserID;
        
        -- 检查关联相册是否存在且属于该用户，并且照片数量大于0
        IF OLD.RelateID IS NOT NULL THEN
            UPDATE Resource SET Ext3 = GREATEST(IFNULL(Ext3, 0) - 1, 0)
            WHERE ResID = OLD.RelateID AND ResType = 1 AND UserID = OLD.UserID;
        END IF;
    END IF;
END //
DELIMITER ;
