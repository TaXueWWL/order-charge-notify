/*
Navicat MySQL Data Transfer

Source Server         : localhost-3306
Source Server Version : 50505
Source Host           : 127.0.0.1:3306
Source Database       : demo

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-06-13 09:37:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_merchant_charge_record
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_charge_record`;
CREATE TABLE `t_merchant_charge_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `record_id` varchar(255) CHARACTER SET utf32 DEFAULT '-1' COMMENT '扣款流水号',
  `order_id` varchar(255) DEFAULT '-1' COMMENT '交易订单号',
  `purse_id` varchar(255) DEFAULT '-1' COMMENT '钱包(账户）id',
  `merchant_name` varchar(255) DEFAULT '-1' COMMENT '商户名',
  `charge_price` decimal(15,3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_merchant_info
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_info`;
CREATE TABLE `t_merchant_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `merchant_name` varchar(255) NOT NULL DEFAULT '-1' COMMENT '供应商名',
  `purse_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '钱包账户id',
  `notify_url` varchar(512) NOT NULL DEFAULT '-1' COMMENT '通知地址',
  `private_key` varchar(1024) NOT NULL DEFAULT '-1' COMMENT '签名密钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `purseid_index` (`purse_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_merchant_order
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_order`;
CREATE TABLE `t_merchant_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `order_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '平台订单号',
  `channel_order_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '渠道订单号',
  `order_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '订单状态，1 初始化 2 处理中 3 失败 0 成功',
  `notify_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '通知状态，1 初始化 2 通知处理中 3 失败 0 成功 -1 不需要通知',
  `pay_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '支付状态，1 初始化 2 处理中 3 失败 0 成功',
  `user_phoneno` varchar(11) NOT NULL DEFAULT '-1' COMMENT '用户手机号',
  `charge_price` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '订单金额',
  `purse_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '下单商户钱包id',
  `merchant_name` varchar(255) DEFAULT '-1' COMMENT '用户名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id_index` (`order_id`) USING BTREE,
  KEY `channel_orderid_index` (`channel_order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_merchant_renotify
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_renotify`;
CREATE TABLE `t_merchant_renotify` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `notify_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '通知id',
  `order_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '订单id',
  `channel_order_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '渠道订单号',
  `user_phoneno` varchar(11) NOT NULL DEFAULT '-1' COMMENT '用户手机号',
  `order_status` tinyint(1) NOT NULL DEFAULT '-1' COMMENT '订单状态 3 失败 0 成功',
  `order_status_desc` varchar(255) NOT NULL DEFAULT '-1' COMMENT '订单状态描述',
  `charge_price` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '交易金额',
  `notify_time` int(3) DEFAULT '0' COMMENT '通知次数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `notify_index` (`notify_id`) USING BTREE,
  KEY `notify_order_id_index` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户通知重发表，对于通知达到一定阈值的失败请求入该重发表。';

-- ----------------------------
-- Table structure for t_merchant_wallet
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_wallet`;
CREATE TABLE `t_merchant_wallet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `purse_id` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '用户钱包id',
  `merchant_name` varchar(255) DEFAULT '-1' COMMENT '商户名',
  `balance_account` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '账户余额',
  `account_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '账户状态 0 正常 1 异常',
  `version` int(11) NOT NULL DEFAULT '0',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for t_user_order3rd
-- ----------------------------
DROP TABLE IF EXISTS `t_user_order3rd`;
CREATE TABLE `t_user_order3rd` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `order_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '代理商订单号',
  `order_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '订单状态，1 初始化 2 处理中 3 失败 0 成功',
  `user_phoneno` varchar(11) NOT NULL DEFAULT '-1' COMMENT '用户手机号',
  `prod_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '商品id',
  `prod_name` varchar(255) NOT NULL DEFAULT '-1' COMMENT '商品名称',
  `out_prod_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '外部商品id',
  `out_prod_name` varchar(255) NOT NULL DEFAULT '-1' COMMENT '外部商品名称',
  `charge_money` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '交易金额',
  `finish_time` datetime DEFAULT NULL COMMENT '订单结束时间',
  `out_order_id` varchar(255) NOT NULL DEFAULT '-1' COMMENT '上游订单号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
