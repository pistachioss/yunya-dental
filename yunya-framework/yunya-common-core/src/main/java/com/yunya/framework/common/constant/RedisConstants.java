package com.yunya.framework.common.constant;

import com.google.common.base.Joiner;
import com.yunya.framework.common.utils.StringHelper;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: redis通用常量
 *
 * @author: chow
 * @date: 2020/7/9 13:16
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class RedisConstants implements Serializable {

  /** ---------------------------用户信息缓存通用key------------------------------- */
  /** 当前用户ID current_userId_{当前登录设备名称}_{用户ID} */
  public static final String REDIS_KEY_USER_ID = "current_userId_{}_{}";
  /** 当前用户登陆名 */
  public static final String REDIS_KEY_USERNAME = "current_userName_";
  /** 用户真实姓名 */
  public static final String REDIS_KEY_USER_NAME = "current_Name_";
  /** 当前用户token */
  public static final String REDIS_KEY_USER_TOKEN = "current_userToken_";

  /** ---------------------------组织（公司、门诊）信息缓存通用key------------------------ */
  /** 组织（公司、门诊）ID */
  public static final String REDIS_KEY_ORG_ID = "orgId_";
  /** 组织（公司、门诊）信息列表 */
  public static final String REDIS_KEY_ORG_LIST = "orgList_";

  /** ------------------------- 诊疗列表（预约未到、候诊中、就诊中、接诊完成、离店）缓存通用key------------------------------- */
  /** 预约未到 appointment_Un_Done_patientInfo_{当前日期}_{预约ID}_{医生ID}_{患者ID} */
  public static final String REDIS_KEY_APPOINTMENT_UN_DONE =
      "appointment_Un_Done_patientInfo_{}_{}_{}_{}";

  public static final String REDIS_KEY_REGISTERED = "registered_patientInfo_";

  public static final String REDIS_KEY_TREATMENT_ING = "treatment_ing_patientInfo_";

  public static final String REDIS_KEY_TREATMENT_ED = "treatment_ed_patientInfo_";

  public static final String REDIS_KEY_TREATMENT_LEAVE = "treatment_leave_patientInfo_";

  /** ----------------------- 电子病例---------------------------------------- */
  public static final String LOCK_DRAFT_APPLY_NS = "lock:draft:apply";

  public static final String LOCK_CHANGE_APPLY_NS = "lock:change:apply";
  /** 病例临时草稿 */
  public static final String DRAFT_TEMP =  "draft:temp";

  /** ------------------------------ 开单、收费、账单处理-------------------------------------------- */
  /** 价目表/商品表 */
  public static final String REDIS_KEY_ITEM_INFO = "billing_item_info_";
  /** 开单 */
  public static final String LOCK_ORDER_PROCESSING_CREATE = "lock:create:process";
  /** 解锁 */
  public static final String LOCK_ORDER_PROCESSING_UNLOCK = "lock:un:process";
  /** 收费 */
  public static final String LOCK_ORDER_PROCESSING_CHARGE = "lock:charge:process";
  /** 收费记录 */
  public static final String LOCK_BILL_PAY_RECORD = "bill_pay_record_";

  /** -------------------------------- 系统用户(员工)信息 ------------------------------- */
  public static final String REDIS_KEY_EMPLOYEE_INFO = "sys_user_info_";
  public static final String REDIS_KEY_USER_INFO = "sys_employee_info_";

  public static final String REDIS_KEY_EMPLOYEE_LIST = "sys_user_info_list";
  /** 入账方式列表 */
  public static final String REDIS_KEY_ACCOUNT_ITEM_LIST = "account_item_list";

  /** --------------------------------- 预约中心 ---------------------------------------------- */
  public static final String REDIS_KEY_APPOINT_INFO = "appoint_info_";

  public static final String REDIS_KEY_APPOINT_DENTIST_DIMENSION = "appoint_dentist_dimension_";
  public static final String REDIS_KEY_APPOINT_PATIENT_DIMENSION = "appoint_patient_dimension_";
  public static final String REDIS_KEY_APPOINT_LIST = "appoint_list_";
  public static final String LOCK_LIMIT_MODIFY = "lock:limit:modify";

  /** ------------------------------ 随访管理 ------------------------------------------------- */
  public static final String LOCK_VISITING_RECORD = "lock:visiting:record";

  /** ------------------------------ 随访提醒管理 ----------------------------------------------- */
  public static final String LOCK_VISITING_REMIND = "lock:visiting:remind";

  /** --------------------------卡券明细------------------------------------------- */
  public static final String LOCK_CARD_GENERATE = "lock:card:generate";

  public static final String LOCK_CARD_SOLD = "lock:card:sold";
  public static final String LOCK_CARD_ACTIVE = "lock:card:active";

  /** -------------------------优惠------------------------------------------------ */
  public static final String LOCK_CHOICE_CARD = "lock:choice:card";

  public static final String LOCK_SUBMIT_BENEFIT = "lock:submit:benefit";

  /** ----------------------------------------------------------------------------- */
  public static final String LOCK_CASHIER_PAY = "LOCK:CASHIER:PAY";

  /** ---------------------------用户短信验证码----------------------------------------------- */
  public static final String FORGET_PWD_AUTHORIZATION = "forget_pwd_authorization_";
  /** 考勤设备绑定短信验证码 */
  public static final String ATTENDANCE_DEVICE_BINDING_AUTHORIZATION =
      "attendance_device_binding_authorization_";

  /** ---------------------------短信管理----------------------------------------------- */
  /** 短信余额 */
  public static final String SMS_STATISTICS_SURPLUS_ORG = "sms:statistics:surplus:org:";
  /** 短信统计锁 */
  public static final String LOCK_SMS_ORG_STATISTICS = "lock:sms:statistics:org:";
  /** 短信发送的消息队列 */
  public static final String SMS_SEND_MESSAGE_QUEUE = "sms:send:message:queue:";
  /** 短信验证码发送的消息队列 */
  public static final String SMS_SEND_VERIFYCODE_QUEUE = "sms:send:verifycode:queue:";

  /** ---------------------------考勤管理----------------------------------------------- */
  /** 考勤锁 */
  public static final String LOCK_ATTENDANCE_PUNCH = "lock:attendance:punch";
  /** 考勤锁时长 */
  public static final Long ATTENDANCE_PUNCH_LOCK_SEC = 600L;

  /** -----------------------------------患者信息--------------------------------------- */
  /** 患者信息 patient_base_info_{患者ID} */
  public static final String PATIENT_BASE_INFO = "patient_base_info_{}";

  /** 会员卡卡号生成锁 */
  public static final String MEMBER_GENERAT_LOCK = "lock:member:generate:";
  /** 预付款卡号生成锁 */
  public static final String PREPAYMENT_GENERAT_LOCK = "lock:prepayment:generate:";

  /** ---------------------------------积分商城------------------------------------------ */
  public static final String CREDITS_SHOP_ORDER = "credits:shop:order:";


  /** ---------------------------------报表统计------------------------------------------ */
  /** 员工就诊统计锁 */
  public static final String LOCK_STATISTICS_EMP_TREAT = "lock:statistics:emp:treat:";
  /** 员工收费时统计锁 */
  public static final String LOCK_STATISTICS_EMP_PAY = "lock:statistics:emp:pay:";
  /** 员工账单时统计锁 */
  public static final String LOCK_STATISTICS_EMP_BILL = "lock:statistics:emp:bill:";

  /** ---------------------------------艾维小程序------------------------------------------ */
  /** mini登录token */
  public static final String MINI_TOKEN = "mini:token";
  /** mini登录id */
  public static final String MINI_USER_ID =  "mini:userId";
  /** 登录session_key */
  public static final String MINI_SESSION_KEY = "mini:session_key";
  /** 微信小程序access_token_key 用于保存在redis中的key */
  public static final String MINI_ACCESS_TOKEN_KEY = "wechat:mini:accessToken";
  /** 小程序热销产品 */
  public static final String HOT_SALE_PRODUCT = "hot:sale:product";
  /** 下单key */
  public static final String CREATE_ORDER_LOCK = "create:order:lock";
  /** 生成订单id key */
  public static final String ORDER_ID_GENERATE = "order:id:generate";
  /** 退款 key */
  public static final String REFUND_ORDER_LOCK = "refund:order:lock";
  /** 确认收货 key */
  public static final String CONFIRM_ORDER_LOCK = "confirm:order:lock";
  /** 小程序验证码 */
  public static final String MINI_CAPTCHA =  "mini:captcha";

  /** ---------------------------全诊通----------------------------------------------- */
  public static final String QZT_TOKEN =  "qzt:token";
  /** ---------------------------滨江----------------------------------------------- */
  public static final String BJ_TOKEN =  "bj:token";

  /**
   * 设置key中的占位符
   *
   * @param keyPrefix key前缀
   * @param params 占位符参数
   * @return 返回设置之后的key
   */
  public static String setKey(String keyPrefix, String... params) {
    StringBuilder sb = new StringBuilder();
    if (params != null && params.length > 0) {
      String[] s = keyPrefix.split("_");
      boolean b = false;
      for (int i = 0, index = 0; i < s.length; i++) {
        b = false;
        if (index < params.length) {
          if ("{}".equals(s[i])) {
            if (!StringHelper.isBlank(params[index]) && !"null".equals(params[index])) {
              sb.append(params[index]);
            } else {
              b = true;
            }
            index++;
          } else {
            sb.append(s[i]);
          }
          if (!b) {
            sb.append("_");
          }
        }
      }
    }
    if (sb.toString().endsWith("_")) {
      sb = sb.deleteCharAt(sb.length() - 1);
    }

    return sb.toString();
  }

  public static String buildLockCacheKey(String lockPrefix, Object suffix)
  {
    return Joiner.on(":").join(lockPrefix, suffix);
  }
}
