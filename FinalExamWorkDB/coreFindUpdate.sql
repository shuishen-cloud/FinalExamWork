-- 1. 用户登录
SELECT UserID, Role FROM User 
WHERE UserName='test_user' AND Password=MD5(CONCAT('test_pwd','salt'));

-- 2. 创建相册（资源类型=1）
INSERT INTO Resource (ResType, UserID, NameLink, Ext1)
VALUES (1, 1, '旅行相册', '2024年旅行照片合集');

-- 3. 上传照片（资源类型=2，关联相册ID=1）
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1, Ext2, Ext3)
VALUES (2, 1, 1, '海边日落.jpg', '日落,海边,旅行', 'upload/2024/05/12/sunset.jpg', 204800);

-- 4. 查询某用户所有相册
SELECT * FROM Resource WHERE UserID=1 AND ResType=1;

-- 5. 查询某相册下所有照片
SELECT * FROM Resource WHERE UserID=1 AND ResType=2 AND RelateID=1 ORDER BY CreateTime DESC;

-- 6. 生成分享链接（资源类型=3，关联照片ID=2）
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1)
VALUES (3, 1, 2, 'share_89757abc', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 7 DAY), '%Y-%m-%d %H:%i:%s'));

-- 7. 按标签检索照片
SELECT * FROM Resource WHERE UserID=1 AND ResType=2 AND Ext1 LIKE '%日落%';
