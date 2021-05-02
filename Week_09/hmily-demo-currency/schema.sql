CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `balance` decimal(10,0) NOT NULL DEFAULT '0',
  `currency` varchar(10) NOT NULL COMMENT '币种：usd、cny',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id_currency` (`user_id`,`currency`)
) ENGINE=InnoDB;

CREATE TABLE `account_trade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_id` varchar(50) NOT NULL COMMENT '请求id，唯一',
  `user_id` bigint(20) NOT NULL,
  `from_account_freeze` decimal(10,0) NOT NULL COMMENT '来源账户冻结金额',
  `from_currency` varchar(10) NOT NULL COMMENT '来源账户币种',
  `to_account_freeze` decimal(10,0) NOT NULL COMMENT '目标账户冻结金额',
  `to_currency` varchar(10) NOT NULL COMMENT '目标账户币种',
  `status` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_trade_sn` (`request_id`)
) ENGINE=InnoDB;