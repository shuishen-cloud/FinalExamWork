# 相册管理系统数据库设计

这是一个简单的相册管理系统的数据库脚本集合，包含数据库创建、表结构定义、触发器和备份功能。

## 文件说明

### 1. create.sql
数据库创建脚本
- 创建SimpleAlbumDB数据库
- 设置字符集为utf8mb4，排序规则为utf8mb4_general_ci

### 2. createTable.sql
数据库表结构定义
- `Users`表：存储用户信息，包括用户名、密码、存储容量等（已重命名以避免与系统保留字冲突）
- `Resource`表：存储资源信息（相册、照片、分享链接），使用LIST分区策略按资源类型分区
- 主键设计：Resource表主键包含(ResID, ResType)以满足分区表要求
- 已移除外键约束（分区表不支持外键），通过触发器维护数据完整性

### 3. createTrigger.sql
触发器脚本
- `TR_Photo_Storage`：在上传照片前检查并扣减用户存储容量
- `TR_Photo_DelStorage`：在删除照片后归还存储容量给用户
- 两个触发器都维护相册中的照片数量统计
- 添加了错误处理和数据验证逻辑

### 4. coreFindUpdate.sql
核心查询和操作示例脚本
- 包含用户登录验证查询
- 创建相册、上传照片的操作示例
- 查询用户相册和相册内照片的功能
- 生成分享链接和按标签检索照片的功能
- 提供了安全的参数化查询示例
- 包含存储过程定义以增强安全性

### 5. backup.sql
数据库备份脚本
- 创建了一个每日执行的定时事件 `EV_Album_Backup`
- 记录备份操作日志，实际备份由外部脚本完成
- 包含安全备份相关的存储过程

### 6. insertTestData.sql
测试数据插入脚本
- 用于快速插入用户、相册、照片和分享链接的测试数据
- 包含多个用户及其关联资源
- 提供数据关联的示例

## 数据库使用报告

### 表结构概览

**Users表（用户表）**
- UserID: INT，主键，自动递增
- UserName: VARCHAR(20)，用户名，唯一且非空
- Password: VARCHAR(255)，加密密码
- NickName: VARCHAR(30)，昵称，非空
- RemainingStorage: BIGINT，剩余存储容量，默认5GB
- Role: TINYINT，用户角色（1-普通用户，2-管理员）
- CreateTime: DATETIME，创建时间
- UpdateTime: TIMESTAMP，更新时间

**Resource表（资源表）**
- ResID: INT，主键，非自动递增（与ResType组成复合主键）
- ResType: TINYINT，资源类型（1-相册，2-照片，3-分享）
- UserID: INT，所属用户ID
- RelateID: INT，关联资源ID
- NameLink: VARCHAR(255)，资源名称或分享链接
- Ext1: VARCHAR(500)，扩展字段1（相册描述/照片标签/分享有效期）
- Ext2: VARCHAR(255)，扩展字段2（照片路径/照片格式）
- Ext3: BIGINT，扩展字段3（照片大小/相册照片数）
- CreateTime: DATETIME，创建时间
- UpdateTime: TIMESTAMP，更新时间

### 分区策略

Resource表采用LIST分区，按ResType字段分区：
- p_album: 值为1（相册）
- p_photo: 值为2（照片）
- p_share: 值为3（分享）

### 安全特性

1. 密码加密：使用SHA2函数对密码进行加密
2. 输入验证：通过CHECK约束验证数据
3. 触发器保护：自动管理存储容量和照片计数
4. 防SQL注入：提供参数化查询示例
5. 用户权限检查：在查询中加入用户权限验证

### 约束和限制

1. 用户名长度：4-20字符
2. 用户角色：只能是1（普通用户）或2（管理员）
3. 照片大小：不超过10MB（10485760字节）
4. 分享有效期：必须在创建时间之后

### 使用示例

#### 创建数据库和表
```sql
SOURCE create.sql
SOURCE createTable.sql
SOURCE createTrigger.sql
```

#### 插入测试数据
```sql
SOURCE insertTestData.sql
```

#### 常用查询
```sql
-- 查询某用户的所有相册
SELECT * FROM Resource WHERE UserID = ? AND ResType = 1;

-- 查询某相册下所有照片
SELECT * FROM Resource WHERE UserID = ? AND ResType = 2 AND RelateID = ?;

-- 按标签检索照片
SELECT * FROM Resource WHERE UserID = ? AND ResType = 2 AND Ext1 LIKE '%?%';
```

## 系统功能

整个系统设计为一个简单的相册管理系统，支持：
- 用户管理（注册、登录）
- 相册创建和管理
- 照片上传和存储（带容量限制）
- 照片分享功能
- 按标签检索照片
- 自动备份功能
- 数据完整性和安全性保障