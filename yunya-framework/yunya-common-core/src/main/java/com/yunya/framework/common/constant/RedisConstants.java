package com.yunya.framework.common.constant;

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
  /** 当前用户ID */
  public static final String REDIS_KEY_USER_ID = "current_userId_";
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
  public static final String REDIS_KEY_APPOINTMENT_UN_DONE = "appointment_Un_Done_patientInfo_";

  public static final String REDIS_KEY_REGISTERED = "registered_patientInfo_";

  public static final String REDIS_KEY_TREATMENT_ING = "treatment_ing_patientInfo_";

  public static final String REDIS_KEY_TREATMENT_ED = "treatment_ed_patientInfo_";

  public static final String REDIS_KEY_TREATMENT_LEAVE = "treatment_leave_patientInfo_";

  /** ----------------------- 电子病例---------------------------------------- */
  public static final String LOCK_DRAFT_APPLY_NS = "lock:draft:apply";

  public static final String LOCK_CHANGE_APPLY_NS = "lock:change:apply";

  /** ------------------------------ 开单 -------------------------------------------- */
  /** 开单 */
  public static final String LOCK_ORDER_PROCESSING_CREATE = "lock:create:process";
  /** 解锁 */
  public static final String LOCK_ORDER_PROCESSING_UNLOCK = "lock:un:process";
  /** 收费 */
  public static final String LOCK_ORDER_PROCESSING_CHARGE = "lock:charge:process";

  /** -------------------------------- 系统用户(员工)信息 ------------------------------- */
  public static final String REDIS_KEY_EMPLOYEE_INFO = "sys_user_info_";
  public static final String REDIS_KEY_EMPLOYEE_LIST = "sys_user_info_list";

  /** ------------------------------ 随访管理 ------------------------------------------------- */
  public static final String LOCK_VISITING_RECORD = "lock:visiting:record";

  /** ------------------------------ 随访提醒管理 -----------------------------------------------*/
  public static final String LOCK_VISITING_REMIND = "lock:visiting:remind";

  /**--------------------------卡券明细-------------------------------------------*/
  public static final String LOCK_CARD_GENERATE = "lock:card:generate";
}
