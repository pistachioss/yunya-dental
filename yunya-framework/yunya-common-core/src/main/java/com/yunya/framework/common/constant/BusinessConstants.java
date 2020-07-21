package com.yunya.framework.common.constant;

import java.util.regex.Pattern;

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
  public static final Byte USER_RESIGNATION_STATUS = 3;

  /** -------------------------组织（公司）信息相关常量--------------------------- */

  /** 公司类型 */
  public static final Byte COMPANY_TYPE = 0;
  /** 大区类型 */
  public static final Byte DISTRICT_TYPE = 1;
  /** 医疗机构类型 */
  public static final Byte MEDICAL_TYPE = 2;
  /** 其他类型 */
  public static final Byte OTHER_TYPE = 3;

  /** -------------------------用户、员工信息相关常量--------------------------- */

  /** ------------------------ 其他 -----------------------------------------*/
  public static final String CLINIC_BUSINESS_PATTER = "HH:mm";
}
