DROP TABLE IF EXISTS `block`;
CREATE TABLE `block` (
  `block_id`      BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `block_name`    VARCHAR(256) NOT NULL,
  `status`        TINYINT(4)   NOT NULL
  COMMENT '0:inactive;1:active',
  `title`         VARCHAR(256)          DEFAULT NULL,
  `title_english` VARCHAR(256)          DEFAULT NULL,
  `subtitle`      VARCHAR(256)          DEFAULT NULL,
  `content`       BLOB,
  `bg_color`      CHAR(32)              DEFAULT NULL
  COMMENT 'background color',
  `sort_order`    INT(11)               DEFAULT NULL,
  `create_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at`   TIMESTAMP    NOT NULL DEFAULT '0000-00-00 00:00:00',
  `enabled`       BIT(1)       NOT NULL,
  `cover_img`     VARCHAR(1024)         DEFAULT NULL,
  PRIMARY KEY (`block_id`)
)
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `block_tag_rel`;
CREATE TABLE `block_tag_rel` (
  `block_tag_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tag_id`       BIGINT(20) NOT NULL,
  `block_id`     BIGINT(20)          DEFAULT NULL,
  `create_at`    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`block_tag_id`)

)
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand` (
  `brand_id`              BIGINT(64)    NOT NULL AUTO_INCREMENT
  COMMENT '商品品牌id',
  `english_name`          VARCHAR(256)  NOT NULL
  COMMENT '英文名称',
  `chinese_name`          VARCHAR(256)  NOT NULL
  COMMENT '中文名称',
  `logo`                  VARCHAR(1024) NOT NULL
  COMMENT 'logo url',
  `type`                  TINYINT(4)             DEFAULT '2'
  COMMENT '1: Pending 2:Active 3:Rejected',
  `description`           LONGTEXT COMMENT 'HTML介绍',
  `remark`                VARCHAR(256)           DEFAULT NULL,
  `creator`               VARCHAR(64)   NOT NULL
  COMMENT '创建者名称',
  `created_at`            DATETIME      NOT NULL,
  `updated_at`            DATETIME      NOT NULL,
  `enabled`               BIT(1)        NOT NULL,
  `status`                TINYINT(4)             DEFAULT '2'
  COMMENT '1: Pending 2:Active 3:Rejected',
  `vendor_application_id` BIGINT(64)             DEFAULT NULL
  COMMENT '创建品牌的供货商id',
  `hot_brand`             INT(11)       NOT NULL DEFAULT '0',
  `sort`                  INT(11)                DEFAULT NULL,
  PRIMARY KEY (`brand_id`)
)
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `category_id`   BIGINT(64) NOT NULL AUTO_INCREMENT
  COMMENT '商品类目id',
  `show_code`     VARCHAR(16)         DEFAULT '1000000000000'
  COMMENT '(已废弃,改用show_code_int)',
  `name`          VARCHAR(256)        DEFAULT NULL
  COMMENT '商品类目名称',
  `chinese_name`  VARCHAR(256)        DEFAULT NULL
  COMMENT '商品类目中文名称',
  `parent_id`     BIGINT(64)          DEFAULT NULL
  COMMENT '父级类目id,(-1为根类目,无父节点)',
  `level`         TINYINT(2)          DEFAULT NULL
  COMMENT '类目层数(根节点为第一层)',
  `sort_order`    INT(8)              DEFAULT NULL
  COMMENT '类目排序',
  `remark`        TEXT,
  `created_at`    DATETIME            DEFAULT NULL,
  `updated_at`    DATETIME            DEFAULT NULL,
  `enabled`       BIT(1)              DEFAULT NULL,
  `show_code_int` BIGINT(16)          DEFAULT '0'
  COMMENT '商品类目的显示码(包含层次信息,000,000,000,000 第一层,第二层,第三层,第四层)',
  `cover_img`     TEXT COMMENT '图片url',
  PRIMARY KEY (`category_id`)
)
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id`         BIGINT(64)     NOT NULL AUTO_INCREMENT,
  `category_id`        BIGINT(64)     NOT NULL
  COMMENT '商品类别ID',
  `vendor_id`          BIGINT(64)     NOT NULL
  COMMENT '供货商ID',
  `brand_id`           BIGINT(64)     NOT NULL
  COMMENT '品牌ID',
  `product_code`       VARCHAR(64)    NOT NULL
  COMMENT '商品代码',
  `cover_img`          TEXT           NOT NULL
  COMMENT '图片url',
  `name`               VARCHAR(256)   NOT NULL
  COMMENT '商品名',
  `description`        TEXT           NOT NULL
  COMMENT 'HTML介绍',
  `description_img`    TEXT           NOT NULL
  COMMENT '介绍图片',
  `remark`             TEXT           NOT NULL,
  `status`             TINYINT(4)     NOT NULL
  COMMENT '状态::1-NEW_PENDING,2-MODIFY_PENDING,3-ON_SALE,4-OFF,5-NEW_REJECTED,6-MODIFY_REJECTED,7-WAITING_SALE,8-UNAVAILABLE',
  `publish_at`         DATETIME       NOT NULL
  COMMENT '有效开始时间',
  `valid_at`           DATETIME       NOT NULL
  COMMENT '有效结束时间',
  `feature`            TINYINT(4)     NOT NULL
  COMMENT '特性',
  `rejected_reason`    TEXT COMMENT '拒绝理由',
  `rejected_by`        VARCHAR(256)            DEFAULT NULL,
  `rejected_at`        DATETIME                DEFAULT NULL,
  `created_at`         DATETIME       NOT NULL,
  `updated_at`         DATETIME       NOT NULL,
  `enabled`            BIT(1)         NOT NULL,
  `customer_rating`    DECIMAL(6, 4)  NOT NULL DEFAULT '0.0000'
  COMMENT '消费者评价',
  `shop_rating`        DECIMAL(6, 4)           DEFAULT '0.0000'
  COMMENT '商家评价',
  `score`              DECIMAL(6, 4)           DEFAULT '0.0000'
  COMMENT '商品综合评分',
  `product_group_id`   BIGINT(64)              DEFAULT NULL,
  `img_modified`       BIT(1)         NOT NULL DEFAULT 0
  COMMENT '实拍 图片是否编辑过(0未被修改 1 被修改过)',
  `season_code`        VARCHAR(128)            DEFAULT NULL
  COMMENT 'season_code',
  `last_check`         DATETIME                DEFAULT NULL,
  `min_retail_price`   DECIMAL(16, 4) NOT NULL
  COMMENT 'sku.price冗余字段',
  `max_retail_price`   DECIMAL(16, 4) NOT NULL
  COMMENT 'sku.price冗余字段',
  `color_code`         VARCHAR(128)            DEFAULT NULL
  COMMENT 'color',
  `designer_id`        VARCHAR(128)            DEFAULT NULL
  COMMENT 'designer_id',
  `preview_im_price`   DECIMAL(16, 4)          DEFAULT NULL
  COMMENT '预热售价',
  `min_boutique_price` DECIMAL(16, 4)          DEFAULT NULL
  COMMENT 'sku.in_price冗余字段',
  `max_boutique_price` DECIMAL(16, 4)          DEFAULT NULL
  COMMENT 'sku.in_price冗余字段',
  `max_im_price`       DECIMAL(16, 4)          DEFAULT NULL
  COMMENT 'sku.im_price冗余字段',
  `min_im_price`       DECIMAL(16, 4)          DEFAULT NULL
  COMMENT 'sku.im_price冗余字段',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `vendor_id` (`vendor_id`, `product_code`)
)
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `product_exception`
--

DROP TABLE IF EXISTS `product_exception`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_exception` (
  `id`                  BIGINT(64) NOT NULL AUTO_INCREMENT
  COMMENT '产品异常ID',
  `type_id`             INT(2)     NOT NULL
  COMMENT '异常类型ID',
  `product_id`          BIGINT(64) NOT NULL
  COMMENT 'Product ID',
  `sku_id`              BIGINT(64) NOT NULL
  COMMENT 'Sku ID',
  `note`                VARCHAR(200)        DEFAULT NULL
  COMMENT '备注',
  `status`              INT(2)     NOT NULL
  COMMENT '状态 0异常解决 1库存异常',
  `created_at`          DATETIME   NOT NULL
  COMMENT '创建时间',
  `created_by_user_id`  BIGINT(64)          DEFAULT NULL
  COMMENT '用户Id',
  `modified_at`         DATETIME            DEFAULT NULL
  COMMENT '审核时间',
  `modified_by_user_id` BIGINT(64)          DEFAULT NULL
  COMMENT '审核人Id',
  PRIMARY KEY (`id`)
)
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `shop_product`;
CREATE TABLE `shop_product` (
  `shop_product_id`  BIGINT(64)     NOT NULL AUTO_INCREMENT
  COMMENT '商家商品ID',
  `shop_id`          BIGINT(64)     NOT NULL
  COMMENT '商品ID',
  `shop_category_id` BIGINT(64)              DEFAULT NULL
  COMMENT '只有末端的category才出现商品',
  `product_id`       BIGINT(64)     NOT NULL
  COMMENT '商品ID',
  `name`             VARCHAR(256)            DEFAULT NULL
  COMMENT '商店编辑的名',
  `coverpic`         TEXT COMMENT '商店编辑的封面',
  `introduction`     LONGTEXT COMMENT '商店编辑的介绍',
  `sales_amount`     INT(11)                 DEFAULT '0'
  COMMENT '销售总量',
  `status`           TINYINT(2)     NOT NULL DEFAULT '-2'
  COMMENT '状态',
  `sale_at`          DATETIME                DEFAULT NULL
  COMMENT '起卖时间',
  `created_at`       DATETIME       NOT NULL,
  `updated_at`       DATETIME       NOT NULL,
  `enabled`          BIT(1)         NOT NULL,
  `product_group_id` BIGINT(64)              DEFAULT NULL,
  `min_sale_price`   DECIMAL(16, 4) NOT NULL
  COMMENT 'shop_product_sku.sku冗余字段',
  `max_sale_price`   DECIMAL(16, 4) NOT NULL
  COMMENT 'shop_product_sku.sku冗余字段',
  PRIMARY KEY (`shop_product_id`)
)
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `shop_product_sku`;
CREATE TABLE `shop_product_sku` (
  `shop_product_sku_id` BIGINT(64)     NOT NULL AUTO_INCREMENT
  COMMENT '商店售卖的商品 sku ID',
  `shop_id`             BIGINT(64)     NOT NULL
  COMMENT '商家ID',
  `shop_product_id`     BIGINT(64)     NOT NULL
  COMMENT '商家商品ID',
  `sku_id`              BIGINT(64)     NOT NULL,
  `name`                VARCHAR(256)            DEFAULT NULL,
  `coverpic`            TEXT,
  `introduction`        VARCHAR(2048)           DEFAULT NULL
  COMMENT '商店编辑的介绍',
  `sale_price`          DECIMAL(16, 4) NOT NULL
  COMMENT '售卖价钱',
  `created_at`          DATETIME       NOT NULL,
  `updated_at`          DATETIME       NOT NULL,
  `enabled`             BIT(1)         NOT NULL,
  PRIMARY KEY (`shop_product_sku_id`)
)
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `sku`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sku` (
  `sku_id`           BIGINT(64)     NOT NULL AUTO_INCREMENT
  COMMENT '商品sku ID',
  `product_id`       BIGINT(64)     NOT NULL
  COMMENT '商品ID product_id + group_id＋property_id 要唯一',
  `sku_code`         VARCHAR(64)    NOT NULL
  COMMENT 'sku显示编码',
  `name`             VARCHAR(256)   NOT NULL
  COMMENT '名称',
  `coverpic`         TEXT           NOT NULL
  COMMENT '封面图片url',
  `introduction`     LONGTEXT       NOT NULL
  COMMENT 'HTML介绍',
  `in_price`         DECIMAL(16, 4) NOT NULL
  COMMENT '商城进货价格',
  `price`            DECIMAL(16, 4) NOT NULL
  COMMENT '商城售价',
  `created_at`       DATETIME       NOT NULL,
  `updated_at`       DATETIME       NOT NULL,
  `enabled`          BIT(1)         NOT NULL,
  `retail_price`     DECIMAL(10, 2)          DEFAULT NULL
  COMMENT '品牌建议零售价',
  `im_price`         DECIMAL(16, 4)          DEFAULT '0.0000'
  COMMENT 'im price for admin',
  `full_modify_date` DATETIME                DEFAULT NULL
  COMMENT 'sku全量更新',
  `last_check`       DATETIME                DEFAULT NULL,
  `size`             VARCHAR(32)             DEFAULT NULL
  COMMENT 'size尺码',
  PRIMARY KEY (`sku_id`)

)
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `sku_property`;
CREATE TABLE `sku_property` (
  `sku_property_id`               BIGINT(64)   NOT NULL AUTO_INCREMENT
  COMMENT 'sku 属性 ID',
  `sku_id`                        BIGINT(64)   NOT NULL,
  `product_sku_property_key_id`   BIGINT(64)   NOT NULL
  COMMENT '商品sku属性的键',
  `product_sku_property_value_id` BIGINT(64)   NOT NULL
  COMMENT '商品sku属性的值',
  `name`                          VARCHAR(256) NOT NULL
  COMMENT '属性名',
  `remark`                        LONGTEXT     NOT NULL
  COMMENT 'HTML',
  `created_at`                    DATETIME     NOT NULL,
  `updated_at`                    DATETIME     NOT NULL,
  `enabled`                       BIT(1)       NOT NULL,
  PRIMARY KEY (`sku_property_id`)

)
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `sku_store`;
CREATE TABLE `sku_store` (
  `sku_store_id`      BIGINT(64) NOT NULL AUTO_INCREMENT
  COMMENT 'sku库存ID',
  `sku_id`            BIGINT(64)          DEFAULT NULL,
  `product_id`        BIGINT(64)          DEFAULT NULL
  COMMENT '商品ID',
  `store`             BIGINT(64)          DEFAULT NULL
  COMMENT '库存',
  `remind`            INT(32)             DEFAULT NULL
  COMMENT '库存提醒',
  `ordered`           INT(32)             DEFAULT NULL
  COMMENT '下单数量',
  `confirm`           INT(32)             DEFAULT NULL
  COMMENT '已确认订货数量',
  `ship`              INT(32)             DEFAULT NULL
  COMMENT '发货数量',
  `finished`          INT(32)             DEFAULT NULL
  COMMENT '完成交易数量',
  `returned`          INT(32)             DEFAULT NULL
  COMMENT '退货数量',
  `changed`           INT(32)             DEFAULT NULL
  COMMENT '换货数量',
  `clear`             INT(32)             DEFAULT NULL,
  `agree_return_rate` DECIMAL(16, 4)      DEFAULT NULL
  COMMENT '协议退货率',
  `created_at`        DATETIME            DEFAULT NULL,
  `updated_at`        DATETIME            DEFAULT NULL,
  `enabled`           BIT(1)              DEFAULT NULL,
  `reserved`          BIGINT(64) UNSIGNED DEFAULT '0'
  COMMENT '保留',
  `confirmed`         BIGINT(64) UNSIGNED DEFAULT '0'
  COMMENT '确认的库存',
  `last_check`        DATETIME            DEFAULT NULL,
  PRIMARY KEY (`sku_store_id`)

)
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `tag_id`     BIGINT(64)  NOT NULL AUTO_INCREMENT,
  `tag_name`   VARCHAR(64) NOT NULL
  COMMENT 'Tag名字',
  `created_at` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled`    BIT(1)      NOT NULL,
  PRIMARY KEY (`tag_id`)
)
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag_product_rel`
--

DROP TABLE IF EXISTS `tag_product_rel`;

CREATE TABLE `tag_product_rel` (
  `tag_product_id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `tag_id`         BIGINT(64) NOT NULL,
  `product_id`     BIGINT(64) NOT NULL,
  `created_at`     TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sort_num`       BIGINT(64) NOT NULL
  COMMENT '排序',
  PRIMARY KEY (`tag_product_id`)

)
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `vendor`;
CREATE TABLE `vendor` (
  `vendor_id`                      BIGINT(64)    NOT NULL AUTO_INCREMENT
  COMMENT '供货商ID',
  `vendor_code`                    VARCHAR(32)            DEFAULT NULL
  COMMENT 'vendor简码',
  `grade`                          TINYINT(4)    NOT NULL DEFAULT '0'
  COMMENT '供应商评级',
  `vendor_name`                    VARCHAR(256)  NOT NULL
  COMMENT '品牌名',
  `user_id`                        BIGINT(64)    NOT NULL
  COMMENT '用户ID',
  `vendor_applicant_id`            BIGINT(64)    NOT NULL
  COMMENT '申请表ID',
  `identity_card`                  VARCHAR(64)   NOT NULL
  COMMENT '身份证号码',
  `brand_type`                     TINYINT(4)    NOT NULL
  COMMENT '供货商类型(1.单品牌;2.多品牌;3.私营品牌)',
  `credit_card`                    VARCHAR(64)   NOT NULL
  COMMENT '银行卡信息',
  `status`                         TINYINT(4)    NOT NULL
  COMMENT '1:ACTIVED,2:PUNISHED,9:SIGN',
  `remark`                         TEXT          NOT NULL
  COMMENT '备注',
  `company_name`                   VARCHAR(256)  NOT NULL
  COMMENT '企业名',
  `registered_person`              VARCHAR(256)           DEFAULT NULL
  COMMENT '公司注册人',
  `electronic_identification_card` VARCHAR(1024) NOT NULL
  COMMENT ' 注册资本',
  `business_license_number`        VARCHAR(256)  NOT NULL
  COMMENT '营业执照号',
  `business_license_duration`      DATETIME               DEFAULT NULL
  COMMENT '营业执照有效开始时间',
  `valid_end_at`                   DATETIME               DEFAULT NULL
  COMMENT '营业执照失效时间',
  `electronic_business_license`    VARCHAR(1024) NOT NULL
  COMMENT '营业执照号电子版',
  `legal_management_scope`         VARCHAR(1024) NOT NULL
  COMMENT '法定经营范围',
  `business_license_location`      VARCHAR(1024) NOT NULL,
  `updated_at`                     DATETIME      NOT NULL,
  `created_at`                     DATETIME      NOT NULL,
  `enabled`                        BIT(1)        NOT NULL,
  `last_login`                     DATETIME               DEFAULT NULL
  COMMENT '最近一次登录vendor时间',
  `national_mark`                  VARCHAR(20)            DEFAULT ''
  COMMENT '供应商国家标识',
  `product_image_score`            TINYINT(4)             DEFAULT '5'
  COMMENT '产品图片评分',
  `vendor_score`                   TINYINT(4)             DEFAULT '5'
  COMMENT '供应商评分',
  `score`                          DECIMAL(6, 4)          DEFAULT '0.0000'
  COMMENT '综合分数',
  `address_country_id`             BIGINT(64)             DEFAULT NULL
  COMMENT '供货商国家',
  `contact`                        VARCHAR(256)           DEFAULT NULL
  COMMENT '联系人(邮箱)',
  `edd_title`                      VARCHAR(256)           DEFAULT NULL,
  `edd_desc`                       VARCHAR(256)           DEFAULT NULL,
  `skip_confirm`                   TINYINT(2)             DEFAULT NULL
  COMMENT '是否跳过弹出确认步骤 1:跳过, 其他:不跳过',
  PRIMARY KEY (`vendor_id`)

)
  DEFAULT CHARSET = utf8;


DROP VIEW IF EXISTS `view_prod_store_sum`;
CREATE VIEW `view_prod_store_sum` AS
  SELECT
    `t`.`product_id` AS `product_id`,
    sum(`t`.`store`) AS `store`
  FROM `sku_store` `t`
  WHERE ((`t`.`store` >= 0) AND (`t`.`enabled` = 1))
  GROUP BY `t`.`product_id`;

