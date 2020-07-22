package com.yunya.employee.expand.constant;

import java.util.regex.Pattern;

/**
 * 简单介绍:</br> 公司端全局常量
 *
 * @author: chow
 * @date: 2020/5/28 17:29
 * @description:
 * @since: 1.0.0
 */
public class EmployeeCommonConstant {

    public static final String CLINIC_BUSINESS_PATTER = "HH:mm";

    public static final Integer FALSE = 0;

    public static final Integer TRUE = 1;

    public static final Pattern ID_CARD_PATTER = Pattern.compile("(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)");

    /** 数据名称已存在 */
    public static final Integer NAME_IS_OCCUPIED = 30001;

    /** 请求参数为空 */
    public static final Integer QUERY_RESULT_INVALID = 30002;


}
