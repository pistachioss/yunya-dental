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
alter table yunya_patient.member_recharge_record modify column `type` int(5) NOT NULL DEFAULT '0' comment '操作类型：0 充值，1 撤销收费，2 订单退费，6.就诊账单返点 7.礼包账单返点';
alter table yunya_patient.prepaid_recharge_record modify column `type` int(5) NOT NULL DEFAULT '0' comment '操作类型：0 充值，1 撤销收费 2 订单退费 6.就诊账单返点 7.礼包账单返点';
create table yunya_patient.`patient_transfer_record` (
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
alter table yunya_patient.patient_member_relation modify column `org_id` int(11) DEFAULT NULL comment '诊所ID';
alter table yunya_patient.patient_member_relation modify column `crt_id` int(11) DEFAULT NULL comment '创建人ID';



-- yunya_report
update yunya_report.base_bill_detail set swipe_workload = 0, swipe_coupon_workload=0;
alter table yunya_report.base_bill_detail add column `free_amount` decimal(19,4) NOT NULL DEFAULT 0 comment '项目免单金额';
alter table yunya_report.base_bill_detail add column `swipe_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡核销工作量';
alter table yunya_report.base_bill_detail add column `swipe_coupon_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡补入工作量';
alter table yunya_report.stat_emp_pay add column `swipe_workload` decimal(19,4) NOT NULL DEFAULT 0 comment '划扣卡核销工作量';
alter table yunya_report.base_bill_detail drop column 'item_name';
alter table yunya_report.base_patient_member_occur_log modify column `occur_type` tinyint(8) NOT NULL comment '发生类型(1.充值 2.消费 3.退款 4.撤销收费 5.账单退费 6.就诊账单返点 7.礼包账单返点 8.转账转入 9.转账转出)';


