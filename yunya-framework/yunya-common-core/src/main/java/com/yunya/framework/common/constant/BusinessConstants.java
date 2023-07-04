package com.yunya.framework.common.constant;

import java.util.Arrays;
import java.util.List;

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
  public static final Integer ADMIN_ID = 1;

  public static final String ADMIN_ACCOUNT = "admin";
  public static final String ADMIN_NAME = "系统管理员";
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
  /** 公司端的组织id */
  public static final Integer COMPANY_ORGID = 21;
  /*************************** 默认支付方式 ****************************/
  /** 现金支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_CASH = "现金";
  /** 会员卡支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_MEMBER = "会员卡";
  /** 预付款支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_PREPARE = "预付款";
  /** 预付款支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_ORTHADANTIC_PREPARE = "正畸预付款";
  /** 预付款支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_WHITENING_PREPARE = "美白预付款";
  /** 支付宝支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_ALIPAY = "支付宝";
  /** 微信支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_WECHAT = "微信";
  /** 银行账户支付方式名称 */
  public static final String ACCOUNT_ITEM_OF_BANK = "银行账户";
  /** 免单支付入账方式id */
  public static final List<Integer> FREE_PAYMENT_ID = Arrays.asList(23, 26);
  /*************************** 岗位组系统初始化参数 ***********************/
  /** 医生岗位组 */
  public static final Integer DENTIST_GROUP_ID = 3;
  /** 助手岗位组 */
  public static final Integer ASSISTANT_GROUP_ID = 4;
  /** 门诊经理岗位组 */
  public static final Integer CLINIC_MANAGER_ID = 7;
  /** 前台岗位组 */
  public static final Integer RECEPTIONIST_ID = 8;
  /** 门诊主任岗位组 */
  public static final Integer CLINIC_DIRECTOR_ID = 12;

  /** -------------------------用户、员工信息相关常量--------------------------- */

  /** ---------------------------患者信息相关常量------------------------------ */
  /** 患者来源类型：未知来源 */
  public static final Integer UNKNOWN_ORIGIN_TYPE = 12;

  /** ------------------------ 其他 ----------------------------------------- */
  public static final String CLINIC_BUSINESS_PATTER = "HH:mm";

  public static final Integer DISABLE_NUM = 0;

  public static final Integer ENABLE_NUM = 1;

  public static final Long HOUR_GAP = 24L;

  public static final Long MEDICAL_APPLY_LOCK_SEC = 600L;
  /** 免单项目ID */
  public static final Integer FREE_TARIFF_ITEM_ID = 679;
  public static final String DOT = ",";

    /** 电子待审批状态 */
  public static Integer MEDICAL_AUDIT_PENDING_STATUS = 1;
  /** 电子待主治医生提交状态 */
  public static Integer NORMAL_MEDICAL_STATUS = 0;

  public static Integer ZERO = 0;
  public static Integer ONE = 1;

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

  /** 本次免单支付 */
  public static Integer PAYMENT_BY_CUSTOMER_FREE = 23;
  /** 艾维员工免单 */
  public static Integer PAYMENT_BY_EMPLOYEE_FREE = 26;

  /********************************report********************************/
  public static final Integer ADD = 0;

  public static final Integer UPDATE = 1;
  public static final Integer DELETE = 2;

  /********************************预约中心正则*****************************/
  /** 手机号正则表达式 */
  public static final String MOBILE_REGEXP =
      "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
  /** 名字正则表达式 */
  public static final String NAME_REGEXP = "^[\\u4e00-\\u9fa5]{0,}$";
  public static final String CN_EN_NAME_REGEXP = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$";
  /** 拼音名字正则表达式 */
  public static final String PINYIN_REGEXP = "^[A-Za-z]+$";
  /** mini token 前缀 */
  public static final String MINI_TOKEN_PREFIX = "mini ";

  /********************************mini wechat*****************************/
  public static final String MINI_CARD_REMARK = "小程序虚拟服务售卖";
}
