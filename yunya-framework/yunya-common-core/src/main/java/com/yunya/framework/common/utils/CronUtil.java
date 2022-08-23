package com.yunya.framework.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.Date;

/**
 * 简介：cron表达式工具
 *
 * @author: chenlin
 * @Description: cron表达式工具
 * @Date: 2022/7/7 11:12
 * @since: 1.0.0
 */
@Slf4j
public class CronUtil {
    /**
     * 时间格式的cron
     */
    private static final String DATETIME_CRON = "ss mm HH dd MM ? yyyy";

    /**
     * 将给定时间转换成下次执行的cron表达式
     *
     * @param dateTime
     * @return
     */
    public static String nextExecCron(Date dateTime) {
        return DateUtil.format(dateTime, DATETIME_CRON);
    }

    public static String nextExecCron(String dateStr) {
        Date date = null;
        try {
            date = DateUtil.parse(dateStr, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            log.error("{}", e);
        }
        return nextExecCron(date);
    }
}
