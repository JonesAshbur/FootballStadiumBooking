/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80026 (8.0.26)
 Source Host           : localhost:3306
 Source Schema         : footballstadium_db

 Target Server Type    : MySQL
 Target Server Version : 80026 (8.0.26)
 File Encoding         : 65001

 Date: 26/02/2025 21:39:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announce
-- ----------------------------
DROP TABLE IF EXISTS `announce`;
CREATE TABLE `announce`  (
  `id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '公告id',
  `content` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '公告内容',
  `user_id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '公告所属用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announce
-- ----------------------------
INSERT INTO `announce` VALUES ('afalmFaE', '2025.4.20绿茵足球赛预选赛', 'leoKuuh4', '2025-02-22 23:17:47');
INSERT INTO `announce` VALUES ('JwSgRMRi', '测试', 'leoKuuh4', '2025-02-23 21:37:09');
INSERT INTO `announce` VALUES ('Yjk4Dg1r', '测试公告', 'leoKuuh4', '2025-02-23 19:25:15');

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '预约id',
  `hall_id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '预约所属体育场馆id',
  `hall_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '预约所属体育场馆名称',
  `hall_photo` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'common/no_image.jpg' COMMENT '预约所属体育场馆名称',
  `user_id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '预约所属用户id',
  `start_time` datetime NOT NULL COMMENT '预约体育场馆开始时间',
  `end_time` datetime NOT NULL COMMENT '预约体育场馆结束时间',
  `state` int NOT NULL DEFAULT 1 COMMENT '预约状态  1：待审核；2：审核通过；3：审核不通过；4：使用中；5：已完成；6：已取消',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '预约体育场馆费用',
  `fee_rule` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '收费标准',
  `remark` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '审核备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES ('GV5d8rsR', 'BtUo00tn', '场地2', '20250222/1740237328363.jpg', 'lU6MCGZj', '2025-02-26 10:00:00', '2025-02-26 12:00:00', 1, '2025-02-23 21:34:47', 300.00, '单个150元/时', '');
INSERT INTO `appointment` VALUES ('Iry9QJ6U', 'lsMOOaQj', '场地1', '20250222/1740237298613.jpg', 'pq26gyX1', '2025-02-27 12:00:00', '2025-02-27 14:00:00', 1, '2025-02-23 21:26:42', 300.00, '单个150元/时', '');
INSERT INTO `appointment` VALUES ('m8fxWWMF', 'BtUo00tn', '场地2', '20250222/1740237328363.jpg', 'NXEN9nfW', '2025-02-25 18:00:00', '2025-02-25 20:00:00', 1, '2025-02-23 20:54:44', 300.00, '单个150元/时', '');
INSERT INTO `appointment` VALUES ('nHKSi8Lz', 'lsMOOaQj', '场地1', '20250222/1740237298613.jpg', 'NXEN9nfW', '2025-02-27 14:00:00', '2025-02-27 16:00:00', 1, '2025-02-23 20:54:52', 300.00, '单个150元/时', '');
INSERT INTO `appointment` VALUES ('OUJdQSjm', 'OkIxI4Lt', '场地3', '20250222/1740237357893.jpg', 'lU6MCGZj', '2025-02-25 20:00:00', '2025-02-25 22:00:00', 1, '2025-02-25 12:44:28', 480.00, '单个240元/时', '');
INSERT INTO `appointment` VALUES ('YdfGNxo3', 'BtUo00tn', '场地2', '20250222/1740237328363.jpg', 'pq26gyX1', '2025-02-25 16:00:00', '2025-02-25 18:00:00', 2, '2025-02-25 12:38:11', 300.00, '单个150元/时', '');

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '体育器材id',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '体育器材名称',
  `num` int NOT NULL DEFAULT 0 COMMENT '体育器材数量',
  `brand` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '器材品牌',
  `fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '租借费用  元/小时',
  `photo` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'common/no_image.jpg' COMMENT '体育器材图片',
  `info` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '体育器材简介',
  `state` int NOT NULL DEFAULT 1 COMMENT '体育器材状态 1：已上架；2：已下架',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES ('vsVCmPpm', '口哨', 7, '卡尔美', 5.00, '20250222/1740237415092.jpg', '一次付费直接买断', 1);
INSERT INTO `equipment` VALUES ('WHGqm1m7', '足球', 1, '阿迪达斯', 20.00, '20250222/1740237383147.jpg', '新品上架', 1);

-- ----------------------------
-- Table structure for hall
-- ----------------------------
DROP TABLE IF EXISTS `hall`;
CREATE TABLE `hall`  (
  `id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '体育场馆id',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '体育场馆名称',
  `location` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '体育场馆位置',
  `fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '预约费用  元/小时',
  `state` int NOT NULL DEFAULT 1 COMMENT '体育场馆状态 1：空闲中；2：已预约',
  `info` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '体育场馆简介',
  `photo` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'common/no_image.jpg' COMMENT '体育场馆图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hall
-- ----------------------------
INSERT INTO `hall` VALUES ('BtUo00tn', '场地2', '体育中心二号院', 150.00, 1, '人工草', '20250222/1740237328363.jpg');
INSERT INTO `hall` VALUES ('lsMOOaQj', '场地1', '体育中心一号院', 150.00, 1, '天然草', '20250222/1740237298613.jpg');
INSERT INTO `hall` VALUES ('OkIxI4Lt', '场地3', '体育中心三号院', 240.00, 1, '室内人工草', '20250222/1740237357893.jpg');

-- ----------------------------
-- Table structure for rental
-- ----------------------------
DROP TABLE IF EXISTS `rental`;
CREATE TABLE `rental`  (
  `id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租借id',
  `start_time` datetime NOT NULL COMMENT '租借器材开始时间',
  `end_time` datetime NOT NULL COMMENT '租借器材结束时间',
  `fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '租借器材费用',
  `fee_rule` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '收费标准',
  `num` int NOT NULL DEFAULT 1 COMMENT '租借器材数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `user_id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租借器材所属用户id',
  `state` int NOT NULL DEFAULT 1 COMMENT '租借状态  1：待审核；2：审核通过；3：审核不通过；4：租借中；5：已完成；6：已取消',
  `equipment_id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租借所属体育器材id',
  `equipment_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租借所属体育器材名称',
  `equipment_photo` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'common/no_image.jpg' COMMENT ' 租借所属体育器材图片',
  `remark` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '审核备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rental
-- ----------------------------
INSERT INTO `rental` VALUES ('1jMu2uSI', '2025-02-25 12:38:20', '2025-02-25 12:38:21', 20.00, '单个20元/时', 1, '2025-02-25 12:38:22', 'pq26gyX1', 3, 'WHGqm1m7', '足球', '20250222/1740237383147.jpg', '');
INSERT INTO `rental` VALUES ('CGgzrIen', '2025-02-25 12:44:14', '2025-02-25 12:44:15', 20.00, '单个20元/时', 1, '2025-02-25 12:44:16', 'lU6MCGZj', 1, 'WHGqm1m7', '足球', '20250222/1740237383147.jpg', '');
INSERT INTO `rental` VALUES ('DSohzRfh', '2025-02-23 21:34:51', '2025-02-23 21:34:52', 5.00, '单个5元/时', 1, '2025-02-23 21:34:53', 'lU6MCGZj', 1, 'vsVCmPpm', '口哨', '20250222/1740237415092.jpg', '');
INSERT INTO `rental` VALUES ('eqv3aknp', '2025-02-23 21:26:08', '2025-02-23 21:26:09', 5.00, '单个5元/时', 1, '2025-02-23 21:26:10', 'pq26gyX1', 1, 'vsVCmPpm', '口哨', '20250222/1740237415092.jpg', '');
INSERT INTO `rental` VALUES ('gV9KoCkL', '2025-02-23 21:34:53', '2025-02-23 21:34:54', 60.00, '单个20元/时', 3, '2025-02-23 21:34:55', 'lU6MCGZj', 1, 'WHGqm1m7', '足球', '20250222/1740237383147.jpg', '');
INSERT INTO `rental` VALUES ('kooU5eaO', '2025-02-23 20:54:57', '2025-02-23 20:54:58', 20.00, '单个20元/时', 1, '2025-02-23 20:54:59', 'NXEN9nfW', 1, 'WHGqm1m7', '足球', '20250222/1740237383147.jpg', '');
INSERT INTO `rental` VALUES ('P2ZvVRKp', '2025-02-23 20:54:59', '2025-02-23 20:55:00', 5.00, '单个5元/时', 1, '2025-02-23 20:55:00', 'NXEN9nfW', 1, 'vsVCmPpm', '口哨', '20250222/1740237415092.jpg', '');
INSERT INTO `rental` VALUES ('spoKadFB', '2025-02-25 12:44:17', '2025-02-25 12:44:18', 20.00, '单个20元/时', 1, '2025-02-25 12:44:19', 'lU6MCGZj', 1, 'WHGqm1m7', '足球', '20250222/1740237383147.jpg', '');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户id',
  `username` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户密码',
  `role_id` int NOT NULL DEFAULT 1 COMMENT '用户所属角色 1：普通用户；2：管理员',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户手机号码',
  `head_pic` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'common/no_image.jpg' COMMENT '用户头像',
  `sex` int NOT NULL DEFAULT 3 COMMENT '用户性别 1：男；2：女；3：未知',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('leoKuuh4', 'admin1', '111111', 2, '11111111111', '20250222/1740237476962.jpg', 3);
INSERT INTO `user` VALUES ('lU6MCGZj', 'user4', '111111', 1, '55555555555', 'common/no_image.jpg', 3);
INSERT INTO `user` VALUES ('NXEN9nfW', 'user2', '111111', 1, '33333333333', '20250222/1740237537523.jpg', 2);
INSERT INTO `user` VALUES ('pq26gyX1', 'user1', '111111', 1, '00000000000', 'common/no_image.jpg', 2);
INSERT INTO `user` VALUES ('rmeIiJfU', 'user3', '111111', 1, '99999999999', 'common/no_image.jpg', 3);
INSERT INTO `user` VALUES ('xc6EytVp', 'admin2', '111111', 2, '44444444444', 'common/no_image.jpg', 1);

SET FOREIGN_KEY_CHECKS = 1;
