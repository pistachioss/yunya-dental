-- yunya_treatment
alter table `test_yunya_treatment`.`order_detail_pay_record` add column `free_amount` decimal(19,4) NOT NULL DEFAULT 0 comment '项目免单金额';
alter table `test_yunya_treatment`.`order_detail_pay_record` add column  `swipe_workload` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '划扣卡工作量';
alter table `test_yunya_treatment`.`order_detail_pay_record` add column  `swipe_coupon_workload` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '划扣卡补入工作量';

create table `test_yunya_treatment`.`bill_pay_share_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT comment '分摊记录id',
  `order_record_id` int(11) NOT NULL comment '订单id',
  `bill_pay_id` int(11) NOT NULL comment '账单收费记录id',
  `order_detail_id` int(11) NOT NULL comment '订单明细ID',
  `item_type` tinyint(2) DEFAULT NULL comment '项目类型：0-价目，1-商品',
  `item_id` int(11) DEFAULT NULL comment '项目id',
  `free_amount` decimal(16,4) NOT NULL comment '项目免单金额（分摊）',
  `received_amount` decimal(16,4) NOT NULL DEFAULT '0.0000' comment '项目实收金额（分摊）',
  `executor_id` int(11) DEFAULT NULL comment '执行人id',
  `consulter_id` int(11) DEFAULT NULL comment '咨询师ID',
  `pay_date` datetime DEFAULT NULL comment '收费时间',
  `swipe_workload` decimal(16,4) NOT NULL DEFAULT '0.0000' comment '划扣卡核销工作量',
  `inservice` bit(1) NOT NULL DEFAULT b'1' comment '是否有效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=831768 DEFAULT CHARSET=utf8mb4 comment='账单收费分摊明细表';

create table `test_yunya_treatment`.`bill_pay_record_log` (
  `bill_pay_record_id` int(11) NOT NULL COMMENT '收费记录id',
  `org_id` int(11) NOT NULL COMMENT '门诊id',
  `order_record_id` int(11) NOT NULL COMMENT '订单记录id',
  `total_charge` decimal(19,4) NOT NULL COMMENT '总收费',
  `total_principal` decimal(19,4) NOT NULL COMMENT '总收费中的本金',
  `param` varchar(2048) NOT NULL COMMENT '收费参数json',
  `type` tinyint(2) NOT NULL DEFAULT '2' COMMENT '类型：1-挂账，2-确认收费，3-收欠费',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '收费状态：1-主流程完成，2-次流程完成',
  `err_msg` mediumtext COMMENT '错误信息',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `crt_id` int(11) NOT NULL COMMENT '创建人id',
  `crt_name` varchar(255) DEFAULT NULL COMMENT '创建人姓名',
  `upt_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`bill_pay_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费日志表';

alter table `test_yunya_treatment`.`bill_pay_detail_record` add column `bonus` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '赠金';
update `test_yunya_treatment`.`bill_pay_detail_record` bdr
left join `test_yunya_report`.`base_bill_pay_detail` bpd on bdr.id=bpd.bill_pay_detail_record_id set bdr.bonus = ifnull(bpd.bonus_amount,0);



-- yunya_patient
alter table `test_yunya_patient_central`.`member_recharge_record` modify column `type` int(5) NOT NULL DEFAULT '0' comment '操作类型：0 充值，1 撤销收费，2 订单退费，6.就诊账单返点 7.礼包账单返点，20 赠金转出，21 赠金转入';
alter table `test_yunya_patient_central`.`prepaid_recharge_record` modify column `type` int(5) NOT NULL DEFAULT '0' comment '操作类型：0 充值，1 撤销收费 2 订单退费 6.就诊账单返点 7.礼包账单返点';

create table `test_yunya_patient_central`.`patient_transfer_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT comment '转账记录id',
  `org_id` int(11) DEFAULT NULL comment '门诊id',
  `transferor_number` varchar(64) NOT NULL comment '转账人卡号',
  `acceptor_number` varchar(64) NOT NULL comment '接收人卡号',
  `principal` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '本金',
  `bonus` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '赠金',
  `inservice` bit(1) NOT NULL DEFAULT b'1' comment '是否启用',
  `remark` varchar(255) DEFAULT NULL comment '备注',
  `crt_time` datetime DEFAULT NULL comment '创建时间',
  `crt_id` int(11) DEFAULT NULL comment '创建人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment='账户转账记录';

alter table `test_yunya_patient_central`.`patient_member_relation` modify column `org_id` int(11) DEFAULT NULL comment '诊所ID';
alter table `test_yunya_patient_central`.`patient_member_relation` modify column `crt_id` int(11) DEFAULT NULL comment '创建人ID';
ALTER TABLE `test_yunya_patient_central`.`patient_origin` ADD COLUMN `gift_rebate_rate`  decimal(19,4) NULL COMMENT '赠金返点比例' AFTER `qr_code_path`;

-- 默认全部会员卡未激活
update `test_yunya_patient_central`.`patient_member_info` set inservice = 0;
-- 更新之前异常会员卡与诊所ID的关联
update test_yunya_patient_central.patient_member_info m
left join test_yunya_system.clinic_ext_info c on substr(m.card_number, 2, 3) = c.clinic_number
set m.org_id = c.company_id
where m.org_id != c.company_id
;
update test_yunya_patient_central.patient_member_info m
set org_id = 21
where substr(m.card_number, 2, 3) = '000'
;

ALTER TABLE `test_yunya_patient_central`.`patient_member_info`
ADD COLUMN `nonauto` BIT(1) NOT NULL DEFAULT b'0' COMMENT '非自动升级、降级会员等级' AFTER `v1_id`,
ADD COLUMN `min_type_id` INT(11) NOT NULL DEFAULT 0 COMMENT '可降级的最低等级会员ID' AFTER `nonauto`,
ADD UNIQUE INDEX `card_number_UNIQUE` (`card_number` ASC);

ALTER TABLE `test_yunya_patient_central`.`patient_kin_relation`
ADD COLUMN `type` TINYINT(5) NOT NULL DEFAULT 0 COMMENT '关系区分: 0 亲属关系；1 推荐关系；' AFTER `inservice`,
COMMENT = '患者亲属关系、推荐关系' ;


-- yunya_report
alter table `test_yunya_report`.`base_bill_detail` add column `free_amount` decimal(19,4) NOT NULL DEFAULT 0 comment '项目免单金额';
alter table `test_yunya_report`.`base_bill_detail` add column `swipe_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡核销工作量';
alter table `test_yunya_report`.`base_bill_detail` add column `swipe_coupon_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡补入工作量';
update `test_yunya_report`.`base_bill_detail` set swipe_workload = 0, swipe_coupon_workload = 0;
alter table `test_yunya_report`.`stat_emp_pay` add column `swipe_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡核销工作量';
alter table `test_yunya_report`.`base_bill_detail` drop column `item_name`;
alter table `test_yunya_report`.`base_patient_member_occur_log` modify column `occur_type` tinyint(8) NOT NULL comment '发生类型(1.充值 2.消费 3.退款 4.撤销收费 5.账单退费 6.就诊账单返点 7.礼包账单返点 8.转账转入 9.转账转出，20 赠金转出，21 赠金转入)';



-- yunya_system
ALTER TABLE `test_yunya_system`.`member_type`
ADD COLUMN `old_name` VARCHAR(32) NULL COMMENT '未激活时的会员卡名称' AFTER `name`,
ADD COLUMN `next_level_id` INT(11) NULL COMMENT '下一级会员等级ID' AFTER `old_name`,
ADD COLUMN `recharge_max_amount` decimal(19, 4) NOT NULL COMMENT '充值达标获卡金额' AFTER `renewal_amount`,
ADD COLUMN `total_amount` decimal(19, 4) NOT NULL COMMENT '累计消费达标获卡金额' AFTER `recharge_max_amount`,
ADD COLUMN `recharge_sub_amount` decimal(19, 4) NOT NULL COMMENT '差额补齐获卡金额' AFTER `total_amount`,
ADD COLUMN `recharge_min_amount` decimal(19, 4) NOT NULL COMMENT '会员等级对应充值起充额' AFTER `recharge_sub_amount`
;
-- check type, id=4 (没有则需先创建)
select * from `test_yunya_system`.`member_type` where name = '普通会员';
-- INSERT INTO `test_yunya_system`.`member_type` (`id`, `name`, `old_name`, `renewal_amount`, `recharge_max_amount`, `total_amount`, `recharge_sub_amount`, `recharge_min_amount`, `age_limit`, `rate`, `type`, `icon`, `picture_code`, `inservice`, `crt_id`, `crt_name`, `crt_time`, `upd_time`) VALUES ('4', '普通会员', '', '111.0000', '0.0000', '0.0000', '0.0000', '0.0000', '11', '100.00', '0', '9f8ed073-432f-4101-afb5-ca2b7dd601b6.png', '0db1b655-1367-41f8-a865-9a28364b6667.png', b'1', '735', '测试-FL测测测测测测', '2023-06-15 10:46:50', '2023-06-15 10:46:50');

UPDATE `test_yunya_system`.`member_type` SET `name` = '金卡会员', `old_name` = '银藤会员' WHERE (`id` = '2');
UPDATE `test_yunya_system`.`member_type` SET `name` = '白金卡会员', `old_name` = '金藤会员' WHERE (`id` = '1');
UPDATE `test_yunya_system`.`member_type` SET `old_name` = '普通会员' WHERE (`id` = '4');

-- 向阳
CREATE TABLE `test_yunya_discount`.`coupon_bill` (
     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '账单ID',
     `org_id` int(11) NOT NULL COMMENT '组织（门诊）id',
     `patient_id` int(11) NOT NULL COMMENT '患者id',
     `order_record_id` int(11) NOT NULL COMMENT '开单记录id',
     `bill_number` varchar(32) NOT NULL COMMENT '账单编号（ZD+门诊ID+时间戳）',
     `price` decimal(19,4) NOT NULL COMMENT '原价',
     `receivable_amount` decimal(19,4) NOT NULL COMMENT '应收金额',
     `received_amount` decimal(19,4) NOT NULL COMMENT '已收金额',
     `debt_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '欠费金额（本单欠费）',
     `invoice` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开发票',
     `invoice_number` varchar(32) DEFAULT NULL COMMENT '发票编号',
     `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
     `crt_id` int(11) NOT NULL COMMENT '创建人id',
     `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `upd_id` int(11) NOT NULL COMMENT '更新人',
     `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`),
     KEY `idx_org_id` (`org_id`) USING BTREE,
     KEY `idx_patient_id` (`patient_id`) USING BTREE,
     KEY `idx_order_record_id` (`order_record_id`) USING BTREE,
     KEY `idx_crt_time` (`crt_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8mb4 COMMENT='划扣账单';

CREATE TABLE `test_yunya_discount`.`coupon_bill_pay` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `org_id` int(11) NOT NULL COMMENT '组织id',
     `patient_id` int(11) NOT NULL COMMENT '患者id',
     `order_id` int(11) NOT NULL COMMENT '开单记录id',
     `bill_id` int(11) NOT NULL COMMENT '账单记录ID',
     `received_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '本次收费金额',
     `owe_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '欠费金额',
     `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
     `crt_id` int(11) NOT NULL COMMENT '收款人id',
     `crt_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收款时间',
     `upd_id` int(11) NOT NULL COMMENT '更新人',
     `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`),
     KEY `idx_org_id` (`org_id`) USING BTREE,
     KEY `idx_order_record_id` (`order_id`) USING BTREE,
     KEY `idx_bill_record_id` (`bill_id`) USING BTREE,
     KEY `idx_ctt_time` (`crt_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=191 DEFAULT CHARSET=utf8mb4 COMMENT='划扣收费记录';



CREATE TABLE `test_yunya_discount`.`coupon_bill_pay_detail` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `org_id` int(11) NOT NULL COMMENT '组织（诊所）id',
    `patient_id` int(11) NOT NULL COMMENT '患者id',
    `order_id` int(11) NOT NULL COMMENT '订单记录id',
    `bill_id` int(11) NOT NULL COMMENT '账单ID',
    `bill_pay_id` int(11) NOT NULL COMMENT '账单收费记录ID',
    `type` tinyint(5) NOT NULL COMMENT '入账方式类型（0-预付款；1-会员卡；2-其他支付方式）',
    `account_item_id` int(11) NOT NULL COMMENT '入账方式明细ID',
    `account_item_name` varchar(20) NOT NULL COMMENT '入账方式明细',
    `amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '入账金额',
    `principal_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '本金',
    `bonus_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '赠金',
    `patient_num` varchar(20) DEFAULT NULL COMMENT '会员卡号或预付款卡号',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
    `crt_id` int(11) NOT NULL COMMENT '创建人ID',
    `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建人姓名',
    `upd_id` int(11) NOT NULL COMMENT '更新人ID',
    `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idk_org_id` (`org_id`) USING BTREE,
    KEY `idk_patient_id` (`patient_id`) USING BTREE,
    KEY `idk_order_record_id` (`order_id`) USING BTREE,
    KEY `idk_bill_record_id` (`bill_id`) USING BTREE,
    KEY `bill_pay_record_id` (`bill_pay_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8mb4 COMMENT='yunya_treatment.008(账单收费详情记录)';


CREATE TABLE `test_yunya_discount`.`coupon_change_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `org_id` int(11) NOT NULL COMMENT '门诊id',
  `patient_id` int(11) NOT NULL COMMENT '患者ID(产生)',
  `order_id` int(11) NOT NULL COMMENT '订单id',
  `card_id` int(11) NOT NULL COMMENT '卡券id',
  `coupon_id` int(11) NOT NULL COMMENT '礼包id',
  `occur_type` tinyint(8) NOT NULL COMMENT '发生类型(1.购买 2.消费 3.退款)',
  `occur_amount` decimal(19,2) NOT NULL DEFAULT '0.00' COMMENT '发生金额',
  `current_amount` decimal(19,2) NOT NULL DEFAULT '0.00' COMMENT '当前金额',
  `payment_id` int(11) DEFAULT NULL COMMENT '发生方式字典',
  `payment_manner` varchar(32) DEFAULT NULL COMMENT '发生方式名称',
  `operator_user_id` int(11) DEFAULT NULL COMMENT '操作人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注/原因',
  `card_number` varchar(64) DEFAULT NULL COMMENT '卡号',
  `occur_date` datetime DEFAULT NULL COMMENT '发生日期',
  `inservice` bit(1) DEFAULT b'1' COMMENT '是否启用 是否有效',
  PRIMARY KEY (`id`),
  KEY `indx_patient_id` (`patient_id`) USING BTREE,
  KEY `indx_occur_date` (`occur_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COMMENT='划扣卡变动记录';


CREATE TABLE `test_yunya_discount`.`coupon_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `org_id` int(11) NOT NULL COMMENT '组织（门诊）ID',
  `patient_id` int(11) NOT NULL COMMENT '患者ID',
  `order_record_num` varchar(32) DEFAULT NULL COMMENT '订单编号',
  `status` tinyint(5) NOT NULL DEFAULT '0' COMMENT '状态（0-已下单；1-已收费；2-已退款）',
  `total_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '订单总额',
  `receivable_amount` decimal(19,4) NOT NULL COMMENT '应收金额（实际支付金额）',
  `received_amount` decimal(19,4) DEFAULT NULL COMMENT '已收金额（本单收费总额）',
  `remarks` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
  `crt_id` int(11) NOT NULL COMMENT '创建人ID',
  `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `upd_id` int(11) NOT NULL COMMENT '更新人',
  `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_org_id` (`org_id`) USING BTREE,
  KEY `idx_patient_id` (`patient_id`) USING BTREE,
  KEY `idx_order_record_num` (`order_record_num`) USING BTREE,
  KEY `idx_crt_time` (`crt_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8mb4 COMMENT='卡券订单';


CREATE TABLE `test_yunya_discount`.`coupon_order_detail` (
     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `org_id` int(11) NOT NULL COMMENT '组织（门诊）ID',
     `order_id` int(11) NOT NULL COMMENT '订单ID',
     `type` tinyint(5) NOT NULL COMMENT '卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券; 5-划扣券）',
     `coupon_id` int(11) NOT NULL COMMENT '开单项目ID',
     `coupon_number` varchar(12) NOT NULL COMMENT '礼包编码',
     `coupon_name` varchar(255) DEFAULT NULL COMMENT '开单项目名称',
     `price` decimal(19,4) NOT NULL COMMENT '售出单价',
     `quantity` int(11) NOT NULL COMMENT '数量',
     `receivable_amount` decimal(19,4) NOT NULL COMMENT '应收金额',
     `consulter_id` int(11) DEFAULT NULL COMMENT '咨询师ID',
     `executor_id` int(11) DEFAULT NULL COMMENT '执行人ID',
     `remarks` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
     `sale_channel_id` int(11) NOT NULL COMMENT '销售渠道(艾维门诊)',
     `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
     `crt_id` int(11) NOT NULL COMMENT '创建人ID',
     `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `upd_id` int(11) NOT NULL COMMENT '更新人',
     `upd_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`),
     KEY `idk_org_id` (`org_id`) USING BTREE,
     KEY `idk_order_record_id` (`order_id`) USING BTREE,
     KEY `idx_coupon_id` (`coupon_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=utf8mb4 COMMENT='卡券订单明细';


CREATE TABLE `test_yunya_discount`.`coupon_order_virtual` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `order_id` int(11) NOT NULL COMMENT '订单id',
    `order_sn` varchar(64) NOT NULL COMMENT '订单号',
    `card_id` int(11) NOT NULL COMMENT '卡券id',
    `coupon_id` int(11) NOT NULL COMMENT '礼包id',
    `patient_id` int(11) NOT NULL COMMENT '患者',
    `coupon_name` varchar(255) NOT NULL COMMENT '礼包名称',
    `card_number` varchar(12) NOT NULL COMMENT '卡号',
    `sold_date` datetime NOT NULL COMMENT '售卖时间',
    `package_unit_price` decimal(11,2) NOT NULL COMMENT '套餐单价',
    `price` decimal(11,2) NOT NULL COMMENT '原价',
    `inservice` bit(1) DEFAULT b'1' COMMENT '是否有效',
    `crt_id` int(11) NOT NULL COMMENT '创建人',
    `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `upd_id` int(11) DEFAULT NULL COMMENT '更新人',
                                                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=295 DEFAULT CHARSET=utf8mb4 COMMENT='虚拟卡券';


CREATE TABLE `test_yunya_discount`.`coupon_refund` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '退费记录ID',
    `org_id` int(11) NOT NULL COMMENT '组织ID',
    `patient_id` int(11) NOT NULL COMMENT '患者ID',
    `order_id` int(11) NOT NULL COMMENT '开单记录ID',
    `refund_amount` decimal(19,4) NOT NULL COMMENT '退款总额',
    `reason` varchar(1024) DEFAULT NULL COMMENT '退费原因',
    `refund_certificate` varchar(1024) DEFAULT NULL COMMENT '退费凭证(多个用法逗号隔开)',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
    `crt_id` int(11) NOT NULL COMMENT '创建人ID',
    `crt_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_id` int(11) DEFAULT NULL COMMENT '更新人ID',
    `upd_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_org_id` (`org_id`) USING BTREE,
    KEY `idx_patient_id` (`patient_id`) USING BTREE,
    KEY `idx_order_record_id` (`order_id`) USING BTREE,
    KEY `idx_crt_time` (`crt_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COMMENT='划扣退费';

CREATE TABLE `test_yunya_discount`.`coupon_refund_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '退费项目明细ID',
  `org_id` int(11) NOT NULL COMMENT '组织ID',
  `order_detail_id` int(11) NOT NULL COMMENT '订单明细id',
  `order_virtual_id` int(11) NOT NULL COMMENT '订单卡券id',
  `card_id` int(11) NOT NULL COMMENT '卡券id',
  `refund_id` int(11) DEFAULT NULL COMMENT '退费记录ID',
  `refund_amount` decimal(19,4) DEFAULT '0.0000' COMMENT '退费金额',
  `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
  `crt_id` int(11) NOT NULL COMMENT '创建人ID',
  `crt_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `upd_id` int(11) DEFAULT NULL COMMENT '更新人ID',
  `upd_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idk_org_id` (`org_id`) USING BTREE,
  KEY `idk_order_detail_id` (`card_id`) USING BTREE,
  KEY `idk_bill_refund_record_id` (`refund_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COMMENT='退款项目明细';


CREATE TABLE `test_yunya_discount`.`coupon_refund_pay` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `refund_id` int(11) NOT NULL COMMENT '退费记录ID',
   `account_item_id` int(11) NOT NULL COMMENT '退费支付方式ID',
   `account_item_name` varchar(20) NOT NULL,
   `refund_pay_amount` decimal(19,4) NOT NULL COMMENT '退费付款合计金额',
   `total_amount` decimal(19,4) NOT NULL COMMENT '该明细退款总额',
   `principal_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '退款本金',
   `gift_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '退款赠金',
   `patient_number` varchar(255) DEFAULT NULL COMMENT '预付款号或会员卡号或支付方式id',
   `inservice` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
   `crt_id` int(11) NOT NULL COMMENT '创建人ID',
   `crt_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `upd_id` int(11) DEFAULT NULL COMMENT '更新人ID',
   `upd_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`),
   KEY `idk_bill_refund_record_id` (`refund_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COMMENT='退费支付';


CREATE TABLE `test_yunya_discount`.`deduction_coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `coupon_id` int(11) NOT NULL COMMENT '优惠券id',
  `activation_deadline` date DEFAULT NULL COMMENT '激活截至日期(产品有效期)',
  `effective_days` int(11) DEFAULT '0' COMMENT '卡券激活后有效期',
  `mixable` int(11) NOT NULL COMMENT '是否可混合使用优惠 0.不可以共用 1.可以共用',
  `useable_clinic` varchar(1024) CHARACTER SET utf8 NOT NULL COMMENT '可使用门诊',
  `use_way` tinyint(4) NOT NULL COMMENT '使用方式 0:一次使用 1:多次使用',
  `limit_count` int(11) NOT NULL COMMENT '账单单次使用限制数量',
  `is_share` tinyint(1) NOT NULL COMMENT '是否可与他人共享',
  `workload_rate` decimal(11,0) DEFAULT NULL COMMENT '工作量比例',
  `remark` varchar(1000) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `crt_id` int(11) NOT NULL COMMENT '创建人',
  `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `upd_id` int(11) DEFAULT NULL COMMENT '更新人',
  `upd_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_coupon_id` (`coupon_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COMMENT='划扣券';

CREATE TABLE `test_yunya_discount`.`deduction_item_period` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `coupon_id` int(11) NOT NULL COMMENT '卡券ID',
   `type` int(11) NOT NULL COMMENT '类型 0:基础价目表,1:基础商品表',
   `item_id` int(11) NOT NULL COMMENT '明细ID',
   `count` int(11) NOT NULL COMMENT '数量',
   `sale_amount` decimal(11,2) NOT NULL COMMENT '套餐价',
   `package_unit_price` decimal(11,2) NOT NULL COMMENT '套餐单价',
   `workload_load` decimal(11,2) DEFAULT NULL COMMENT '单个数量补入工作量',
   `unit_price` decimal(11,2) NOT NULL COMMENT '单价',
   `price` decimal(11,2) NOT NULL COMMENT '原价',
   `crt_id` int(11) DEFAULT NULL COMMENT '创建人',
   `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
   `is_show_app` int(2) DEFAULT '1' COMMENT '套餐券匹配项目小程序是否显示 0否 1是',
   PRIMARY KEY (`id`),
   KEY `idx_coupon_id` (`coupon_id`) USING BTREE,
   KEY `idx_date` (`crt_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8462 DEFAULT CHARSET=utf8mb4 COMMENT='划扣项目变化';

CREATE TABLE `test_yunya_report`.`base_coupon_bill` (
    `order_id` int(11) NOT NULL,
    `bill_id` int(11) DEFAULT NULL COMMENT '订单记录ID(order_record表ID)',
    `org_id` int(11) NOT NULL COMMENT '组织ID',
    `patient_id` int(11) NOT NULL COMMENT '患者ID',
    `order_status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '状态（0-已下单；1-已收费；2-已退款）',
    `order_num` varchar(32) NOT NULL COMMENT '订单编号',
    `bill_num` varchar(32) DEFAULT NULL COMMENT '账单编号',
    `order_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '订单总额',
    `received_amount` decimal(19,4) DEFAULT '0.0000' COMMENT '已收总额',
    `receivable_amount` decimal(19,4) DEFAULT '0.0000' COMMENT '应收金额',
    `debt_amount` decimal(19,4) DEFAULT '0.0000' COMMENT '欠费总额',
    `order_date` datetime NOT NULL COMMENT '开单日期',
    `biller_id` int(11) DEFAULT NULL COMMENT '开单人ID',
    `bill_date` datetime DEFAULT NULL COMMENT '账单日期',
    `checker_id` int(11) DEFAULT NULL COMMENT '结账人ID',
    PRIMARY KEY (`order_id`),
    KEY `idx_org_id` (`org_id`) USING BTREE,
    KEY `idx_patient_id` (`patient_id`) USING BTREE,
    KEY `idx_orde_date` (`order_date`) USING BTREE,
    KEY `idx_bill_date` (`bill_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单记录';

CREATE TABLE `test_yunya_report`.`base_coupon_bill_detail` (
    `order_detail_id` int(11) NOT NULL COMMENT '订单明细ID',
    `org_id` int(11) DEFAULT NULL COMMENT '组织ID',
    `order_id` int(11) NOT NULL COMMENT '订单ID',
    `coupon_id` int(11) NOT NULL COMMENT '礼包id',
    `type` int(11) NOT NULL COMMENT '卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券; 5-划扣券）',
    `coupon_number` varchar(12) NOT NULL COMMENT '礼包编码',
    `coupon_name` varchar(255) NOT NULL COMMENT '开单项目名称',
    `price` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '售出单价',
    `receivable_amount` decimal(19,4) NOT NULL COMMENT '应收金额',
    `quantity` int(11) NOT NULL COMMENT '数量',
    `executor_id` int(11) DEFAULT NULL COMMENT '执行人ID',
    `consulter_id` int(11) DEFAULT NULL COMMENT '咨询师ID',
    `sale_channel_id` int(11) NOT NULL COMMENT '销售渠道(艾维门诊)',
    `received_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '项目收费金额',
    `remark` varchar(128) DEFAULT '' COMMENT '开单备注',
    PRIMARY KEY (`order_detail_id`),
    KEY `idx_org_id` (`org_id`) USING BTREE,
    KEY `idx_executor_id` (`executor_id`) USING BTREE,
    KEY `idx_bill_id` (`order_id`) USING BTREE,
    KEY `idx_cmp` (`org_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单明细';

CREATE TABLE `test_yunya_report`.`base_coupon_bill_pay` (
    `bill_pay_id` int(11) NOT NULL COMMENT '账单收费记录ID',
    `order_id` int(11) NOT NULL COMMENT '订单ID',
    `org_id` int(11) NOT NULL COMMENT '组织ID',
    `patient_id` int(11) NOT NULL COMMENT '患者id',
    `payee_user_id` int(11) NOT NULL COMMENT '收款人ID',
    `payee_date` datetime NOT NULL COMMENT '收款日期',
    `received_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '本次收费总额',
    `owe_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '欠费总额',
    PRIMARY KEY (`bill_pay_id`),
    KEY `idx_bill_id` (`order_id`) USING BTREE,
    KEY `idx_org_id` (`org_id`) USING BTREE,
    KEY `idx_payee_user_id` (`payee_user_id`) USING BTREE,
    KEY `idx_payee_date` (`payee_date`) USING BTREE,
    KEY `idx_cmp` (`order_id`,`org_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单收费记录';

CREATE TABLE `test_yunya_report`.`base_coupon_bill_pay_detail` (
    `bill_pay_detail_id` int(11) NOT NULL COMMENT '账单支付明细记录ID',
    `order_id` int(11) NOT NULL COMMENT '订单id',
    `bill_pay_id` int(11) NOT NULL COMMENT '账单收费记录ID',
    `account_item_id` int(11) NOT NULL COMMENT '入账方式明细ID',
    `account_item_name` varchar(20) NOT NULL COMMENT '入账方式明细',
    `type` tinyint(5) NOT NULL COMMENT '入账方式类型（0-预付款；1-会员卡；2-其他支付方式）',
    `amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '入账金额',
    `principal_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '消费本金',
    `bonus_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '消费赠金',
    `patient_num` varchar(20) DEFAULT NULL COMMENT '会员卡号或预付款卡号',
    PRIMARY KEY (`bill_pay_detail_id`),
    KEY `idx_bill_id` (`order_id`),
    KEY `idx_bill_pay_id` (`bill_pay_id`),
    KEY `idx_account_item_id` (`account_item_id`),
    KEY `idx_cmp` (`type`,`account_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='账单付款明细记录';

CREATE TABLE `test_yunya_report`.`base_coupon_order_virtual` (
    `virtual_id` int(11) NOT NULL,
    `order_id` int(11) NOT NULL COMMENT '订单id',
    `order_sn` varchar(64) NOT NULL COMMENT '订单号',
    `card_id` int(11) NOT NULL COMMENT '卡券id',
    `coupon_id` int(11) NOT NULL COMMENT '礼包id',
    `patient_id` int(11) NOT NULL COMMENT '患者',
    `coupon_name` varchar(255) NOT NULL COMMENT '礼包名称',
    `card_number` varchar(12) NOT NULL COMMENT '卡号',
    `sold_date` datetime NOT NULL COMMENT '售卖时间',
    `package_unit_price` decimal(11,2) NOT NULL COMMENT '套餐单价',
    `inservice` bit(1) DEFAULT b'1' COMMENT '是否有效',
    `price` decimal(11,2) NOT NULL COMMENT '原价',
    `crt_id` int(11) NOT NULL COMMENT '创建人',
    `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `upd_id` int(11) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`virtual_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟卡券';

CREATE TABLE `test_yunya_report`.`base_coupon_refund` (
    `refund_id` int(11) NOT NULL COMMENT '退费记录ID',
    `org_id` int(11) NOT NULL COMMENT '组织ID',
    `patient_id` int(11) NOT NULL COMMENT '患者ID',
    `order_id` int(11) NOT NULL COMMENT '订单id',
    `refund_amount` decimal(19,4) NOT NULL COMMENT '退费总额',
    `refund_operator_id` int(11) DEFAULT NULL COMMENT '退费人ID',
    `refund_date` datetime DEFAULT NULL COMMENT '退费日期',
    `refund_reason` varchar(255) DEFAULT NULL COMMENT '退费原因',
    `bill_num` varchar(64) NOT NULL COMMENT '账单编号',
    `bill_date` datetime DEFAULT NULL COMMENT '账单日期',
    `order_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '订单总额',
    `receivable_amount` decimal(19,4) DEFAULT '0.0000' COMMENT '应收总额',
    `received_amount` decimal(19,4) DEFAULT '0.0000' COMMENT '账单已收总额',
    PRIMARY KEY (`refund_id`),
    KEY `idx_org_id` (`org_id`) USING BTREE,
    KEY `idx_patient_id` (`patient_id`) USING BTREE,
    KEY `idx_bill_id` (`order_id`) USING BTREE,
    KEY `idx_refund_date` (`refund_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退费记录';

CREATE TABLE `test_yunya_report`.`base_coupon_refund_detail` (
    `refund_detail_id` int(11) NOT NULL COMMENT '退费明细记录ID',
    `refund_id` int(11) NOT NULL COMMENT '退费记录ID',
    `order_detail_id` int(11) NOT NULL COMMENT '订单明细ID',
    `order_virtual_id` int(11) DEFAULT NULL COMMENT '订单卡券id',
    `card_id` int(11) NOT NULL COMMENT '卡券id',
    `refund_amount` decimal(19,4) NOT NULL COMMENT '退费金额',
    `executor_id` int(11) DEFAULT NULL COMMENT '执行人id',
    `consulter_id` int(11) DEFAULT NULL COMMENT '咨询死ID',
    `type` tinyint(3) NOT NULL COMMENT '卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券; 5-划扣券）',
    `card_number` varchar(100) DEFAULT NULL COMMENT '卡号',
    `coupon_name` varchar(200) DEFAULT NULL COMMENT '礼包名称',
    PRIMARY KEY (`refund_detail_id`),
    KEY `idx_refund_id` (`refund_id`) USING BTREE,
    KEY `idx_bill_detail_id` (`order_detail_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退费明细';

CREATE TABLE `test_yunya_report`.`base_coupon_refund_pay` (
    `bill_refund_pay_id` int(11) NOT NULL COMMENT '账单退费支付明细ID',
    `refund_id` int(11) NOT NULL COMMENT '账单退费记录ID',
    `account_item_id` int(11) NOT NULL COMMENT '支付方式ID',
    `account_item_name` varchar(20) NOT NULL,
    `patient_number` varchar(255) DEFAULT NULL COMMENT '预付款号或会员卡号或支付方式id',
    `total_amount` decimal(19,4) NOT NULL COMMENT '该明细退款总额',
    `principal_amount` decimal(19,4) NOT NULL COMMENT '退款本金',
    `bonus_amount` decimal(19,4) NOT NULL DEFAULT '0.0000' COMMENT '退款赠金',
    PRIMARY KEY (`bill_refund_pay_id`),
    KEY `idx_refund_id` (`refund_id`) USING BTREE,
    KEY `idx_account_item_id` (`account_item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单退费付款明细';

CREATE TABLE `test_yunya_report`.`coupon_change_record` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `org_id` int(11) NOT NULL COMMENT '门诊id',
    `patient_id` int(11) NOT NULL COMMENT '患者ID(产生)',
    `order_id` int(11) NOT NULL COMMENT '订单id',
    `card_id` int(11) NOT NULL COMMENT '卡券id',
    `coupon_id` int(11) NOT NULL COMMENT '礼包id',
    `occur_type` tinyint(8) NOT NULL COMMENT '发生类型(1.购买 2.消费 3.退款)',
    `occur_amount` decimal(19,2) NOT NULL DEFAULT '0.00' COMMENT '发生金额',
    `current_amount` decimal(19,2) NOT NULL DEFAULT '0.00' COMMENT '当前金额',
    `payment_id` int(11) DEFAULT NULL COMMENT '发生方式字典',
    `payment_manner` varchar(32) DEFAULT NULL COMMENT '发生方式名称',
    `operator_user_id` int(11) DEFAULT NULL COMMENT '操作人',
    `remarks` varchar(255) DEFAULT NULL COMMENT '备注/原因',
    `card_number` varchar(64) DEFAULT NULL COMMENT '卡号',
    `occur_date` datetime DEFAULT NULL COMMENT '发生日期',
    `inservice` bit(1) DEFAULT b'1' COMMENT '是否启用 是否有效',
    PRIMARY KEY (`id`),
    KEY `indx_patient_id` (`patient_id`) USING BTREE,
    KEY `indx_occur_date` (`occur_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COMMENT='划扣卡变动记录';

CREATE TABLE `test_yunya_report`.`deduction_item` (
    `coupon_id` int(11) NOT NULL COMMENT '卡券ID',
    `type` int(11) NOT NULL COMMENT '类型 0:基础价目表,1:基础商品表',
    `item_id` int(11) NOT NULL COMMENT '明细ID',
    `count` int(11) NOT NULL COMMENT '数量',
    `sale_amount` decimal(11,2) NOT NULL COMMENT '套餐价',
    `package_unit_price` decimal(11,2) NOT NULL COMMENT '套餐单价',
    `workload_load` decimal(11,2) DEFAULT NULL COMMENT '单个数量补入工作量',
    `unit_price` decimal(11,2) NOT NULL COMMENT '单价',
    `price` decimal(11,2) NOT NULL COMMENT '原价',
    `crt_id` int(11) DEFAULT NULL COMMENT '创建人',
    `crt_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_coupon_id` (`coupon_id`) USING BTREE,
    KEY `idx_date` (`crt_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='划扣项目变化';



-- 更新表格sql
ALTER TABLE `test_yunya_discount`.`card`
    ADD COLUMN `buyer_id` INT(11) NULL AFTER `seller_user_id`;

ALTER TABLE `test_yunya_discount`.`coupon_common_info`
    CHANGE COLUMN `type` `type` TINYINT(10) NOT NULL COMMENT '卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券; 5-划扣券）' ;

ALTER TABLE `test_yunya_report`.`base_coupon`
    CHANGE COLUMN `coupon_type` `coupon_type` TINYINT(6) NULL DEFAULT NULL COMMENT '卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券；5-划扣卡）' ;
