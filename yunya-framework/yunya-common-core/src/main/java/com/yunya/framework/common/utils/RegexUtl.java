package com.yunya.framework.common.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static com.yunya.framework.common.constant.BusinessConstants.ID_CARD_REGEXP;
import static com.yunya.framework.common.constant.BusinessConstants.MOBILE_REGEXP;

/**
 * @author: chenlin
 * @date: 2023/10/10 19:46
 * @description:
 * @since: 1.0.0
 */
public class RegexUtl {

    /**
     * 是否是有效的手机号
     *
     * @param mobile
     * @return
     */
    public static boolean matchMobile(String mobile) {
        if (!StringUtils.isEmpty(mobile)) {
            Pattern compile = Pattern.compile(MOBILE_REGEXP);
            return compile.matcher(mobile).find();
        }
        return false;
    }

    /**
     * 是否是有效的身份证号
     *
     * @param idCard
     * @return
     */
    public static boolean matchIdCard(String idCard) {
        if (!StringUtils.isEmpty(idCard)) {
            Pattern compile = Pattern.compile(ID_CARD_REGEXP);
            return compile.matcher(idCard).find();
        }
        return false;
    }
}
