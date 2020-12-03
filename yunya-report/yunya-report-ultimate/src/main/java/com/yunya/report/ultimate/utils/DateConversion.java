package com.yunya.report.ultimate.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 简介: 日期格式化
 *
 * @author: WY
 * @date: 2020/12/3 10:53
 * @description:
 * @since: 1.0.0
 */
public class DateConversion {

    /**
     * 时间天数+1
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static String getEndDate(String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sDate = sdf.parse(endDate);
        //要实现日期+1 需要String转成Date类型
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        f.format(sDate);
        Calendar c = Calendar.getInstance();
        c.setTime(sDate);
        //利用Calendar 实现 Date日期+1天
        c.add(Calendar.DAY_OF_MONTH, 1);
        sDate = c.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdf1.format(sDate);
        return endDate;
    }
}