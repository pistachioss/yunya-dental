-- yunya_treatment
alter table yunya_treatment.order_detail_pay_record add column `free_amount` decimal(19,4) NOT NULL DEFAULT 0 comment '项目免单金额';
alter table yunya_treatment.order_detail_pay_record add column  `swipe_workload` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '划扣卡工作量';
alter table yunya_treatment.order_detail_pay_record add column  `swipe_coupon_workload` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '划扣卡补入工作量';

create table yunya_treatment.`bill_pay_share_detail` (
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

alter table yunya_treatment.bill_pay_detail_record add column `bonus` decimal(19,4) NOT NULL DEFAULT '0.0000' comment '赠金';
update yunya_treatment.bill_pay_detail_record bdr left join yunya_report.base_bill_pay_detail bpd on bdr.id=bpd.bill_pay_detail_record_id set bdr.bonus = ifnull(bpd.bonus_amount,0);



-- yunya_patient
alter table yunya_patient_central.member_recharge_record modify column `type` int(5) NOT NULL DEFAULT '0' comment '操作类型：0 充值，1 撤销收费，2 订单退费，6.就诊账单返点 7.礼包账单返点，20 赠金转出，21 赠金转入';
alter table yunya_patient_central.prepaid_recharge_record modify column `type` int(5) NOT NULL DEFAULT '0' comment '操作类型：0 充值，1 撤销收费 2 订单退费 6.就诊账单返点 7.礼包账单返点';

create table yunya_patient_central.`patient_transfer_record` (
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

alter table yunya_patient_central.patient_member_relation modify column `org_id` int(11) DEFAULT NULL comment '诊所ID';
alter table yunya_patient_central.patient_member_relation modify column `crt_id` int(11) DEFAULT NULL comment '创建人ID';
ALTER TABLE `yunya_patient_central`.`patient_origin` ADD COLUMN `gift_rebate_rate`  decimal(19,4) NULL COMMENT '赠金返点比例' AFTER `qr_code_path`;
update yunya_patient_central.patient_member_info set inservice = 0;

ALTER TABLE `yunya_patient_central`.`patient_member_info`
ADD COLUMN `nonauto` BIT(1) NOT NULL DEFAULT b'0' COMMENT '非自动升级、降级会员等级' AFTER `v1_id`
ADD COLUMN `min_type_id` INT(11) NOT NULL DEFAULT 0 COMMENT '可降级的最低等级会员ID' AFTER `nonauto`
ADD UNIQUE INDEX `card_number_UNIQUE` (`card_number` ASC);

ALTER TABLE `yunya_patient_central`.`patient_kin_relation`
ADD COLUMN `type` TINYINT(5) NOT NULL DEFAULT 0 COMMENT '关系区分: 0 亲属关系；1 推荐关系；' AFTER `inservice`,
COMMENT = '患者亲属关系、推荐关系' ;



-- yunya_report
update yunya_report.base_bill_detail set swipe_workload = 0, swipe_coupon_workload=0;
alter table yunya_report.base_bill_detail add column `free_amount` decimal(19,4) NOT NULL DEFAULT 0 comment '项目免单金额';
alter table yunya_report.base_bill_detail add column `swipe_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡核销工作量';
alter table yunya_report.base_bill_detail add column `swipe_coupon_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡补入工作量';
alter table yunya_report.stat_emp_pay add column `swipe_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡核销工作量';
alter table yunya_report.base_bill_detail drop column 'item_name';
alter table yunya_report.base_patient_member_occur_log modify column `occur_type` tinyint(8) NOT NULL comment '发生类型(1.充值 2.消费 3.退款 4.撤销收费 5.账单退费 6.就诊账单返点 7.礼包账单返点 8.转账转入 9.转账转出，20 赠金转出，21 赠金转入)';



-- yunya_system
ALTER TABLE `yunya_system`.`member_type`
ADD COLUMN `old_name` VARCHAR(32) NULL COMMENT '未激活时的会员卡名称' AFTER `name`,
ADD COLUMN `next_level_id` INT(11) NULL COMMENT '下一级会员等级ID' AFTER `old_name`,
ADD COLUMN `recharge_max_amount` decimal(19, 4) NOT NULL COMMENT '充值达标获卡金额' AFTER `renewal_amount`,
ADD COLUMN `total_amount` decimal(19, 4) NOT NULL COMMENT '累计消费达标获卡金额' AFTER `recharge_max_amount`,
ADD COLUMN `recharge_sub_amount` decimal(19, 4) NOT NULL COMMENT '差额补齐获卡金额' AFTER `total_amount`,
ADD COLUMN `recharge_min_amount` decimal(19, 4) NOT NULL COMMENT '会员等级对应充值起充额' AFTER `recharge_sub_amount`
;
-- check type, id=4 (没有则需先创建)
select * from `yunya_system`.`member_type` where name = '普通会员';
-- INSERT INTO `yunya_system`.`member_type` (`id`, `name`, `old_name`, `renewal_amount`, `recharge_max_amount`, `total_amount`, `recharge_sub_amount`, `recharge_min_amount`, `age_limit`, `rate`, `type`, `icon`, `picture_code`, `inservice`, `crt_id`, `crt_name`, `crt_time`, `upd_time`) VALUES ('4', '普通会员', '', '111.0000', '0.0000', '0.0000', '0.0000', '0.0000', '11', '100.00', '0', '9f8ed073-432f-4101-afb5-ca2b7dd601b6.png', '0db1b655-1367-41f8-a865-9a28364b6667.png', b'1', '735', '测试-FL测测测测测测', '2023-06-15 10:46:50', '2023-06-15 10:46:50');

UPDATE `yunya_system`.`member_type` SET `name` = '金卡会员', `old_name` = '银藤会员' WHERE (`id` = '2');
UPDATE `yunya_system`.`member_type` SET `name` = '白金卡会员', `old_name` = '金藤会员' WHERE (`id` = '1');
UPDATE `yunya_system`.`member_type` SET `old_name` = '普通会员' WHERE (`id` = '4');