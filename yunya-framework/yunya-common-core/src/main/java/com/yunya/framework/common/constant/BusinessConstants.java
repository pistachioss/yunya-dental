package com.yunya.framework.common.constant;

/**
 * 简介: 业务相关常量
 *
 * @author: chow
 * @date: 2020/7/12 17:29
 * @description:
 * @since: 1.0.0
 */
public class BusinessConstants {

  /** ---------------------菜单权限相关常量--------------------------------- */
  /** 系统管理员默认账号 */
  public static final String ADMIN_ACCOUNT = "admin";
  /** 默认顶级父ID */
  public static final Integer DEFAULT_PARENT_ID = 0;
  /** 目录 */
  public static final String RESOURCE_TYPE_DIRT = "dirt";
  /** 菜单 */
  public static final String RESOURCE_TYPE_MENU = "menu";
  /** 按钮 */
  public static final String RESOURCE_TYPE_BTN = "button";
  /** 页面 */
  public static final String RESOURCE_TYPE_URI = "uri";

  /** -------------------------用户、员工信息相关常量--------------------------- */
  /** 用户离职状态 */
  public static final Byte USER_RESIGNATION_STATUS = 2;

  /** -------------------------组织（公司）信息相关常量--------------------------- */
  /** 公司类型 */
  public static final Byte COMPANY_TYPE = 0;
  /** 大区类型 */
  public static final Byte DISTRICT_TYPE = 1;
  /** 医疗机构类型 */
  public static final Byte MEDICAL_TYPE = 2;
  /** 其他类型 */
  public static final Byte OTHER_TYPE = 3;
  /*************************** 默认支付方式 ****************************/
  /** 会员卡支付方式ID */
  public static final Integer ACCOUNT_ITEM_OF_MEMBER = 60;
  /** 预付款支付方式ID */
  public static final Integer ACCOUNT_ITEM_OF_PREPARE = 61;

  /** -------------------------用户、员工信息相关常量--------------------------- */

  /** ------------------------ 其他 ----------------------------------------- */
  public static final String CLINIC_BUSINESS_PATTER = "HH:mm";

  public static final Integer DISABLE_NUM = 0;

  public static final Integer ENABLE_NUM = 1;

  public static final Long HOUR_GAP = 24L;

  public static final Long MEDICAL_APPLY_LOCK_SEC = 600L;

  /** 电子待审批状态 */
  public static Integer MEDICAL_AUDIT_PENDING_STATUS = 1;
  /** 电子待主治医生提交状态 */
  public static Integer NORMAL_MEDICAL_STATUS = 0;

  /**************************** 开单记录状态 *******************************/
  /** 账单未锁定 */
  public static final Byte ORDER_UN_LOCK_STATUS = 0;
  /** 账单锁定 */
  public static final Byte ORDER_LOCK_STATUS = 1;
  /** 账单已结算 */
  public static final Byte ORDER_FINISH_STATUS = 2;
  /** 收费中 */
  public static final Byte ORDER_CHARGING_STATUS = 3;

  /*******************************就诊记录状态*****************************/
  /** 就诊中 */
  public static final Byte TREATMENT_PROCESSING_STATUS = 0;
  /** 就诊已开单（接诊未完成） */
  public static final Byte TREATMENT_PROCESS_ORDER_STATUS = 1;
  /** 就诊已完成 */
  public static final Byte TREATMENT_PROCESSED_STATUS = 2;
  /** 就诊已结账 */
  public static final Byte TREATMENT_PROCESS_FINISH_STATUS = 3;
  /** 卡券密码位数 */
  public static Integer CARD_PASS_BIT = 6;
  /** 卡券导出文件名 */
  public static String EXPORT_CARD_FILENAME = "产品生成分配";
  /** 优惠券有效期 */
  public static String COUPON_ALWAYS_EFFECT = "永久有效";

  /********************************report********************************/
  public static final Integer ADD = 0;
  public static final Integer UPDATE = 1;
  public static final Integer DELETE = 2;
}
