package com.yunya.modules.patient_central.constant;

import lombok.Data;

/**
 * 简单介绍:</br> WO平台相关常量
 *
 * @author: WY
 * @date 2020/8/10 13:30
 * @description: WO平台相关常量
 * @since: 1.0.0
 */
@Data
public class WoPlatformConstants {

    /** ---------------------WO平台相关常量--------------------------------- */

    /** 应用id */
    public static final String APPID = "D40708B670E54D2DA06B1A3974A66EA4";

    /** 设备序列号 */
    public static final String DEVICEKEY = "84E0F4246B261501";

    /** 公钥 秘钥 */
    public static final String APPSECRET = "B496892726AC4D0BBCBC0A6575EC9365";
    public static final String APPKEY = "2CA42A1905B44CD18D8EE83049903306";

    /** 设备授权类型 1:本地库 2:云端库 */
    public static final byte TYPE = 1;

    /** ---------------------WO平台人脸识别硬件信息--------------------------------- */

    /** 人脸识别硬件设备密码 */
    public static final String PASS = "123456";

    /** 人脸识别硬件ip */
    public static String IP;

    /** 心跳方法条用url前缀 */
    public static final String URL = "http://" + IP + ":" + "8090";

    public static final String SN = "84E0F4246B261501";

    /** ---------------------患者信息--------------------------------- */

    /** 员工来源类型 */
    public static final Integer STAFF_TYPE = 1;

    /** 患者来源类型 */
    public static final Integer PATIENT_TYPE = 2;

    /** 活动来源类型 */
    public static final Integer EVENT_TYPE = 3;

    /** 患者来源类型 */
    public static final Integer PARTNERS_TYPE = 4;

}
