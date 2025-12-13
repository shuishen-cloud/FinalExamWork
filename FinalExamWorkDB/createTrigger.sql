-- 1. 上传照片扣减存储容量
DELIMITER //
CREATE TRIGGER TR_Photo_Storage BEFORE INSERT ON Resource
FOR EACH ROW
BEGIN
    DECLARE storage BIGINT;
    -- 仅对照片类型（ResType=2）校验存储
    IF NEW.ResType = 2 THEN
        SELECT RemainingStorage INTO storage FROM User WHERE UserID=NEW.UserID;
        IF storage < NEW.Ext3 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='存储容量不足';
        END IF;
        UPDATE User SET RemainingStorage=RemainingStorage-NEW.Ext3 WHERE UserID=NEW.UserID;
        -- 更新所属相册的照片数量
        UPDATE Resource SET Ext3=IFNULL(Ext3,0)+1 
        WHERE ResID=NEW.RelateID AND ResType=1 AND UserID=NEW.UserID;
    END IF;
END //

-- 2. 删除照片归还存储容量
CREATE TRIGGER TR_Photo_DelStorage AFTER DELETE ON Resource
FOR EACH ROW
BEGIN
    IF OLD.ResType = 2 THEN
        UPDATE User SET RemainingStorage=RemainingStorage+OLD.Ext3 WHERE UserID=OLD.UserID;
        -- 更新所属相册的照片数量
        UPDATE Resource SET Ext3=IFNULL(Ext3,0)-1 
        WHERE ResID=OLD.RelateID AND ResType=1 AND UserID=OLD.UserID;
    END IF;
END //
DELIMITER ;
