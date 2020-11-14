package com.yunya.framework.common.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * 描述:
 *
 * @author Gaoluding
 * @create 2019-08-11 10:26
 */
public class DateUtil {

    private DateUtil(){};

    public static Date geLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 000);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public static Date getNextWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author wy
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /*public static Boolean isEffectiveDate1(String dangqian,String kaishi,String end) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(dangqian);
        System.out.println(kaishi);
        System.out.println(end);

        System.out.println(dateFormat.parse(kaishi).getTime()+"<"+dateFormat.parse(dangqian).getTime());
        System.out.println(dateFormat.parse(end).getTime()+">"+dateFormat.parse(dangqian).getTime());
        if(dateFormat.parse(kaishi).getTime() < dateFormat.parse(dangqian).getTime()){
            System.out.println("true;");
        }
        if(dateFormat.parse(end).getTime() > dateFormat.parse(dangqian).getTime()){
            System.out.println("false;");
        }
        if(dateFormat.parse(kaishi).getTime() < dateFormat.parse(dangqian).getTime() && dateFormat.parse(end).getTime() > dateFormat.parse(dangqian).getTime()){
            System.out.println("true;");
            System.out.println("------------------------------------------;");
            return true;
        }else {
            System.out.println("false;");
            System.out.println("------------------------------------------;");
            return false;
        }
    }*/

    /**
     * 获取给定日期所属月份的所有日期列表
     * @param date
     * @return
     */
    public static List<Date> getMonthFullDay(String date) {
        List<Date> fullDayList = new ArrayList<>(30);
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int day = 1;// 所有月份从1号开始
        Calendar cal = Calendar.getInstance();// 获得当前日期对象
        cal.clear();// 清除信息
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);// 1月从0开始
        cal.set(Calendar.DAY_OF_MONTH, day);
        int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int j = 0; j <= (count - 1); ) {
            if (sdf.format(cal.getTime()).equals(getLastDay(year, month)))
                break;
            cal.add(Calendar.DAY_OF_MONTH, j == 0 ? +0 : +1);
            j++;
            fullDayList.add(cal.getTime());
        }
        return fullDayList;
    }

    public static String getLastDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    /**
     * 获取指定年月的开始日期
     * @param year
     * @param month
     * @return
     */
    public static Date getBeginTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate localDate = yearMonth.atDay(1);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.of("Asia/Shanghai"));
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 获取指定年月的结束日期
     * @param year
     * @param month
     * @return
     */
    public static Date getEndTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        LocalDateTime localDateTime = endOfMonth.atTime(23, 59, 59, 999);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 毫秒转分钟
     * @param restMinute
     * @return
     */
    public static Long micro2Min(Long restMinute) {
        return restMinute/60000;
    }
}
