CREATE TABLE `sys_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '支持手机号登录',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '支持账号登录',
  `pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码(md5)',
  `status` smallint(1) NOT NULL DEFAULT '0' COMMENT '用户状态,0正常，1创建中，2已禁用',
  `user_type` smallint(1) NOT NULL DEFAULT '0' COMMENT '用户类型 0消费者，1商家，9管理员',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表，买家和卖家都在这里。暂缺卖家信息和买家信息表';

CREATE TABLE `bas_goods` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `seller_id` bigint(20) NOT NULL COMMENT '商家id',
  `name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '商品名称',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '一口价',
  `outer_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '外部编码',
  `pic_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '商品主图url',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '商品描述，支持html',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `record_version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号（乐观锁）',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品';

CREATE TABLE `bas_goods_sku` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `seller_id` bigint(20) NOT NULL COMMENT '商家id',
  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'sku名称',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'sku价格',
  `pic_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'sku大图',
  `pic_thumbnail_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'sku缩略图（为空就用sku大图）',
  `sort_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '排序编号，数字小的在前面',
  `is_publish` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否发布可见',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `record_version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号（乐观锁）',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品sku';

CREATE TABLE `om_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `seller_id` bigint(20) unsigned NOT NULL COMMENT '商家id',
  `consumer_id` bigint(20) unsigned NOT NULL COMMENT '商家id',
  `order_sn` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `order_status` int(11) NOT NULL DEFAULT '0' COMMENT '订单状态，枚举值：1、2、3.。。。（各状态值略）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单备注',
  `pay_amount` double NOT NULL DEFAULT '0' COMMENT '支付金额',
  `receiver_phone` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收件人电话',
  `receiver_address` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '详细地址',
  `receiver_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收件人姓名',
  `pay_time` datetime(6) DEFAULT NULL COMMENT '付款时间',
  `receive_time` datetime(6) DEFAULT NULL COMMENT '确认收货时间',
  `goods_count` int(11) NOT NULL DEFAULT '0' COMMENT '商品数量',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `record_version` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_order_sn` (`order_sn`),
  KEY `idx_seller` (`seller_id`,`order_status`,`pay_time`) USING BTREE,
  KEY `idx_consumer` (`consumer_id`,`order_status`,`pay_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

CREATE TABLE `om_order_goods` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `seller_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `order_sn` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `goods_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商品id',
  `goods_count` int(11) NOT NULL DEFAULT '0' COMMENT '商品数量',
  `goods_img` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商品图片',
  `goods_name` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商品名称',
  `goods_price` double NOT NULL DEFAULT '0' COMMENT '商品单件 单价：元',
  `sku_id` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商品skuid',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `record_version` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_order_sn_goods_id` (`order_sn`,`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品';