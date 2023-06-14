package com.yunya.framework.common.utils;

import java.security.InvalidParameterException;

/**
 * @author: chenlin
 * @date: 2023/5/15 13:39
 * @description: 数值工具
 * @since: 1.0.0
 */
public class NumberUtil {

    public static void main(String[] args) {
        System.out.println(betweenAnd(0, new Integer[]{1, 2, 3}));
    }


    /**
     * 判断给定值是否在指定范围内闭区间[lower, upper]
     *
     * @param val
     * @param range 为两个元素的数组，则表示上、下限范围
     * @return
     */
    public static boolean betweenAnd(Integer val, Integer[] range) {
        if (StringHelper.isEmpty(range)) {
            throw new NullPointerException();
        }
        if (range.length != 2) {
            throw new InvalidParameterException("parameter: range length must is 2");
        }
        return betweenAnd(val, range[0], range[1]);
    }

    /**
     * 判断给定值是否在指定范围内闭区间[lower, upper]
     *
     * @param val
     * @param lowwer 为null，则表示无下限
     * @param upper 为null，则表示无上限
     * @return
     */
    public static boolean betweenAnd(Integer val, Integer lowwer, Integer upper) {
        if (StringHelper.isNull(val)) {
            return false;
        }
        boolean between = false;
        boolean and = false;
        if (StringHelper.isNull(lowwer)) {
            between = true;
        } else if (val >= lowwer) {
            between = true;
        }

        if (StringHelper.isNull(upper)) {
            and = true;
        } else if (val <= upper) {
            and = true;
        }
        return between && and;
    }
}
