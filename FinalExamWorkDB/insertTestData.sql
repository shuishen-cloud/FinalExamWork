-- 测试数据插入脚本
-- 为SimpleAlbumDB数据库插入测试数据

-- 清空现有测试数据（如果存在）
DELETE FROM Resource WHERE UserID >= 1;
DELETE FROM Users WHERE UserID >= 1;

-- 插入测试用户数据
INSERT INTO Users (UserName, Password, NickName, RemainingStorage, Role) VALUES
('admin', SHA2('admin123', 256), '管理员', 5368709120, 2),
('user1', SHA2('password1', 256), '张三', 5368709120, 1),
('user2', SHA2('password2', 256), '李四', 5368709120, 1),
('user3', SHA2('password3', 256), '王五', 5368709120, 1);

-- 为每个用户创建一些测试相册
-- 用户1 (admin) 的相册
INSERT INTO Resource (ResType, UserID, NameLink, Ext1, Ext2, Ext3) VALUES
(1, 1, '系统相册', '系统默认相册', NULL, 0),
(1, 1, '风景照片', '自然风景照片集合', NULL, 0),
(1, 1, '人物照片', '人物肖像照片', NULL, 0);

-- 用户2 (user1) 的相册
INSERT INTO Resource (ResType, UserID, NameLink, Ext1, Ext2, Ext3) VALUES
(1, 2, '旅行相册', '2024年春季旅行照片', NULL, 0),
(1, 2, '家庭照片', '家庭聚会照片', NULL, 0),
(1, 2, '工作项目', '工作相关图片', NULL, 0);

-- 用户3 (user2) 的相册
INSERT INTO Resource (ResType, UserID, NameLink, Ext1, Ext2, Ext3) VALUES
(1, 3, '个人写真', '个人肖像照片', NULL, 0),
(1, 3, '宠物相册', '我家的小猫', NULL, 0),
(1, 3, '美食照片', '各种美食', NULL, 0);

-- 用户4 (user3) 的相册
INSERT INTO Resource (ResType, UserID, NameLink, Ext1, Ext2, Ext3) VALUES
(1, 4, '学习资料', '课程学习图片', NULL, 0),
(1, 4, '运动照片', '运动时刻', NULL, 0),
(1, 4, '艺术创作', '个人艺术作品', NULL, 0);

-- 获取相册ID用于插入照片（使用子查询获取相册ID）
-- 用户1的照片
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1, Ext2, Ext3) VALUES
(2, 1, (SELECT ResID FROM Resource WHERE UserID = 1 AND NameLink = '风景照片' AND ResType = 1 LIMIT 1), '长城.jpg', '历史,风景,文化', 'upload/2024/01/changcheng.jpg', 1048576),
(2, 1, (SELECT ResID FROM Resource WHERE UserID = 1 AND NameLink = '风景照片' AND ResType = 1 LIMIT 1), '黄山.jpg', '自然,风景,山川', 'upload/2024/01/huangshan.jpg', 2097152),
(2, 1, (SELECT ResID FROM Resource WHERE UserID = 1 AND NameLink = '人物照片' AND ResType = 1 LIMIT 1), '会议照片.jpg', '会议,工作,同事', 'upload/2024/01/meeting.jpg', 1572864);

-- 用户2的照片
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1, Ext2, Ext3) VALUES
(2, 2, (SELECT ResID FROM Resource WHERE UserID = 2 AND NameLink = '旅行相册' AND ResType = 1 LIMIT 1), '巴黎铁塔.jpg', '旅行,欧洲,地标', 'upload/2024/02/eiffel.jpg', 3145728),
(2, 2, (SELECT ResID FROM Resource WHERE UserID = 2 AND NameLink = '旅行相册' AND ResType = 1 LIMIT 1), '海滩日落.jpg', '旅行,海滩,夕阳', 'upload/2024/02/sunset.jpg', 2621440),
(2, 2, (SELECT ResID FROM Resource WHERE UserID = 2 AND NameLink = '家庭照片' AND ResType = 1 LIMIT 1), '生日聚会.jpg', '家庭,生日,庆祝', 'upload/2024/02/birthday.jpg', 1396704),
(2, 2, (SELECT ResID FROM Resource WHERE UserID = 2 AND NameLink = '工作项目' AND ResType = 1 LIMIT 1), '项目会议.png', '工作,项目,会议', 'upload/2024/02/project.png', 524288);

-- 用户3的照片
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1, Ext2, Ext3) VALUES
(2, 3, (SELECT ResID FROM Resource WHERE UserID = 3 AND NameLink = '个人写真' AND ResType = 1 LIMIT 1), '春季写真.jpg', '个人,写真,春季', 'upload/2024/03/spring_portrait.jpg', 2097152),
(2, 3, (SELECT ResID FROM Resource WHERE UserID = 3 AND NameLink = '宠物相册' AND ResType = 1 LIMIT 1), '小猫1.jpg', '宠物,猫,可爱', 'upload/2024/03/kitten1.jpg', 1048576),
(2, 3, (SELECT ResID FROM Resource WHERE UserID = 3 AND NameLink = '宠物相册' AND ResType = 1 LIMIT 1), '小猫2.jpg', '宠物,猫,玩耍', 'upload/2024/03/kitten2.jpg', 1572864),
(2, 3, (SELECT ResID FROM Resource WHERE UserID = 3 AND NameLink = '美食照片' AND ResType = 1 LIMIT 1), '日式料理.jpg', '美食,日料,寿司', 'upload/2024/03/sushi.jpg', 2097152);

-- 用户4的照片
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1, Ext2, Ext3) VALUES
(2, 4, (SELECT ResID FROM Resource WHERE UserID = 4 AND NameLink = '学习资料' AND ResType = 1 LIMIT 1), '笔记截图.png', '学习,笔记,课程', 'upload/2024/04/notes.png', 786432),
(2, 4, (SELECT ResID FROM Resource WHERE UserID = 4 AND NameLink = '运动照片' AND ResType = 1 LIMIT 1), '篮球比赛.jpg', '运动,篮球,比赛', 'upload/2024/04/basketball.jpg', 1835008),
(2, 4, (SELECT ResID FROM Resource WHERE UserID = 4 AND NameLink = '艺术创作' AND ResType = 1 LIMIT 1), '绘画作品.jpg', '艺术,绘画,创作', 'upload/2024/04/painting.jpg', 2621440);

-- 创建一些分享链接
INSERT INTO Resource (ResType, UserID, RelateID, NameLink, Ext1) VALUES
(3, 1, (SELECT ResID FROM Resource WHERE UserID = 1 AND NameLink = '长城.jpg' AND ResType = 2 LIMIT 1), 'share_abc123', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 30 DAY), '%Y-%m-%d %H:%i:%s')),
(3, 2, (SELECT ResID FROM Resource WHERE UserID = 2 AND NameLink = '巴黎铁塔.jpg' AND ResType = 2 LIMIT 1), 'share_xyz789', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 15 DAY), '%Y-%m-%d %H:%i:%s')),
(3, 3, (SELECT ResID FROM Resource WHERE UserID = 3 AND NameLink = '小猫1.jpg' AND ResType = 2 LIMIT 1), 'share_kitten', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 7 DAY), '%Y-%m-%d %H:%i:%s'));

-- 显示插入的数据摘要
SELECT 'Users Table Data:' AS 'Summary';
SELECT UserID, UserName, NickName, Role FROM Users ORDER BY UserID;

SELECT 'Resource Table Summary:' AS 'Summary';
SELECT ResType, COUNT(*) AS Count FROM Resource GROUP BY ResType ORDER BY ResType;