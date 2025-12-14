-- 这些是示例查询，实际使用时应该使用参数化查询来避免SQL注入
-- 1. 用户登录 (参数化查询示例)
-- SELECT UserID, Role FROM User 
-- WHERE UserName = ? AND Password = SHA2(CONCAT(?, (SELECT salt FROM UserSalt WHERE UserName = ?)), 256);

-- 2. 创建相册（资源类型=1）(参数化查询示例)
-- INSERT INTO Resource (ResType, UserID, NameLink, Ext1)
-- VALUES (?, ?, ?, ?);

-- 3. 上传照片（资源类型=2）(参数化查询示例)
-- INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1, Ext2, Ext3)
-- VALUES (?, ?, ?, ?, ?, ?, ?);

-- 4. 查询某用户所有相册 (参数化查询示例)
-- SELECT * FROM Resource WHERE UserID = ? AND ResType = 1;

-- 5. 查询某相册下所有照片 (参数化查询示例)
-- SELECT * FROM Resource WHERE UserID = ? AND ResType = 2 AND RelateID = ? ORDER BY CreateTime DESC;

-- 6. 生成分享链接（资源类型=3）(参数化查询示例)
-- INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1)
-- VALUES (3, ?, ?, ?, DATE_FORMAT(DATE_ADD(NOW(), INTERVAL ? DAY), '%Y-%m-%d %H:%i:%s'));

-- 7. 按标签检索照片 (参数化查询示例)
-- SELECT * FROM Resource WHERE UserID = ? AND ResType = 2 AND Ext1 LIKE CONCAT('%', ?, '%');

-- 以下是修复后的安全示例查询（使用变量代替硬编码值）
SET @test_user_id = 1;
SET @test_album_id = 1;
SET @test_photo_id = 2;
SET @test_username = 'test_user';
SET @test_tag = '日落';

-- 1. 用户登录 (安全示例)
SELECT UserID, Role FROM User 
WHERE UserName = @test_username AND Password = SHA2(CONCAT('test_pwd', 'salt'), 256);

-- 2. 创建相册（资源类型=1）(安全示例)
INSERT INTO Resource (ResType, UserID, NameLink, Ext1)
VALUES (1, @test_user_id, '旅行相册', '2024年旅行照片合集');

-- 3. 上传照片（资源类型=2，关联相册ID=1）(安全示例)
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1, Ext2, Ext3)
VALUES (2, @test_user_id, @test_album_id, '海边日落.jpg', '日落,海边,旅行', 'upload/2024/05/12/sunset.jpg', 204800);

-- 4. 查询某用户所有相册 (安全示例)
SELECT * FROM Resource WHERE UserID = @test_user_id AND ResType = 1;

-- 5. 查询某相册下所有照片 (安全示例)
SELECT * FROM Resource WHERE UserID = @test_user_id AND ResType = 2 AND RelateID = @test_album_id ORDER BY CreateTime DESC;

-- 6. 生成分享链接（资源类型=3，关联照片ID=2）(安全示例)
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1)
VALUES (3, @test_user_id, @test_photo_id, 'share_89757abc', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 7 DAY), '%Y-%m-%d %H:%i:%s'));

-- 7. 按标签检索照片 (安全示例)
SELECT * FROM Resource WHERE UserID = @test_user_id AND ResType = 2 AND Ext1 LIKE CONCAT('%', @test_tag, '%');

-- 安全查询：检查用户权限（推荐在所有查询中加入权限检查）
SELECT r.* FROM Resource r
JOIN User u ON r.UserID = u.UserID
WHERE r.UserID = @test_user_id 
  AND u.UserID = @test_user_id  -- 验证操作用户与资源用户一致
  AND r.ResType = 1;
