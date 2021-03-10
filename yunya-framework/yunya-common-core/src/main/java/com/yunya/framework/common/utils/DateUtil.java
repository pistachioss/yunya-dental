package com.yunya.framework.common.utils;

import cn.hutool.core.date.DateTime;
import com.yunya.framework.common.exception.ClientServiceException;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_TRANSFORMATION_EXIST;

/**
 * 描述: 日期处理工具类
 *
 * @author Gaoluding
 * @create 2019-08-11 10:26
 */
public class DateUtil {
  /** 最大秒*/
  public static final int MAX_SECOND = 59;
  private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

  private DateUtil() {}

  /**
   * 格式化指定格式字符串日期
   *
   * @param format
   * @param date
   * @return
   */
  public static String parseDateToStr(final String format, final Date date) {
    return new SimpleDateFormat(format).format(date);
  }

  /**
   * 获取上周星期一
   *
   * @param date
   * @return
   */
  public static Date getLastWeekMonday(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getThisWeekMonday(date));
    cal.add(Calendar.DATE, -7);
    return cal.getTime();
  }

  /**
   * 获取本周星期一
   *
   * @param date
   * @return
   */
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

  /**
   * 获取下周星期一
   *
   * @param date
   * @return
   */
  public static Date getNextWeekMonday(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getThisWeekMonday(date));
    cal.add(Calendar.DATE, 7);
    return cal.getTime();
  }

  /**
   * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
   *
   * @param nowTime 当前时间
   * @param startTime 开始时间
   * @param endTime 结束时间
   * @return
   * @author wy
   */
  public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
    if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
      return true;
    }

    Calendar date = Calendar.getInstance();
    date.setTime(nowTime);

    Calendar begin = Calendar.getInstance();
    begin.setTime(startTime);

    Calendar end = Calendar.getInstance();
    end.setTime(endTime);

    return date.after(begin) && date.before(end);
  }

  /**
   * date转LocalDateTime
   *
   * @param date
   * @return
   */
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
   *
   * @param date
   * @return
   */
  public static List<Date> getMonthFullDay(String date) {
    List<Date> fullDayList = new ArrayList<>(30);
    int year = Integer.parseInt(date.substring(0, 4));
    int month = Integer.parseInt(date.substring(5, 7));
    int day = 1; // 所有月份从1号开始
    Calendar cal = Calendar.getInstance(); // 获得当前日期对象
    cal.clear(); // 清除信息
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1); // 1月从0开始
    cal.set(Calendar.DAY_OF_MONTH, day);
    int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    for (int j = 0; j <= (count - 1); ) {
      if (SDF.format(cal.getTime()).equals(getLastDay(year, month))) {
        break;
      }
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
   *
   * @param year
   * @param month 1-12月
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
   *
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
   *
   * @param timeStamp
   * @return
   */
  public static Long micro2Min(Long timeStamp) {
    return timeStamp / 60000;
  }

  /**
   * 毫秒转小时分钟或者分钟
   *
   * @param timeStamp
   * @return
   */
  public static String micro2HourMin(Long timeStamp) {
    long min = micro2Min(timeStamp);
    long hours = (long) Math.floor(min / 60);
    long minute = min % 60;
    String result = "";
    if (hours > 0) {
      result = hours + "小时";
    }
    if (minute > 0) {
      result += minute + "分钟";
    }
    if (StringHelper.isEmpty(result)) {
      result = "0分钟";
    }
    return result;
  }

  /**
   * 获取两个日期内所有日期，包含给定日期
   *
   * @param start
   * @param end
   * @return
   */
  public static List<Date> getBetweenDate(Date start, Date end) {
    return getBetweenDate(start, end, -1, -1);
  }

  /**
   * 获取两个日期内所有日期，包含给定日期
   *
   * @param startDate
   * @param endDate
   * @param pageNum 分页参数，开始位置，如果start和limit同时等于-1，则不限制查询记录条数
   * @param pageSize 分页参数，结束位置
   * @return
   */
  public static List<Date> getBetweenDate(Date startDate, Date endDate, int pageNum, int pageSize) {
    List result = new ArrayList<Date>();
    Calendar tempStart = Calendar.getInstance();
    tempStart.setTime(startDate);
    tempStart.add(Calendar.DAY_OF_YEAR, 1);
    Calendar tempEnd = Calendar.getInstance();
    tempEnd.setTime(endDate);
    result.add(startDate);
    while (tempStart.before(tempEnd)) {
      result.add(tempStart.getTime());
      tempStart.add(Calendar.DAY_OF_YEAR, 1);
    }
    if (!result.contains(endDate)) {
      result.add(endDate);
    }
    if (pageNum != -1 && pageSize != -1) {
      result = pagination(result, pageNum, pageSize);
    }
    return result;
  }

  /**
   * 开始分页
   *
   * @param list 数据列表
   * @param pageNum 页码
   * @param pageSize 每页多少条数据
   * @return
   */
  public static List pagination(List list, Integer pageNum, Integer pageSize) {
    if (list == null || list.isEmpty()) {
      return null;
    }

    Integer count = list.size(); // 记录总数
    int pageCount; // 页数
    if (count % pageSize == 0) {
      pageCount = count / pageSize;
    } else {
      pageCount = count / pageSize + 1;
    }
    int fromIndex; // 开始索引
    int toIndex; // 结束索引
    if (!pageNum.equals(pageCount)) {
      fromIndex = (pageNum - 1) * pageSize;
      toIndex = fromIndex + pageSize;
    } else {
      fromIndex = (pageNum - 1) * pageSize;
      toIndex = count;
    }
    return list.subList(fromIndex, toIndex);
  }

  /**
   * 转换成1970-01-01当天的时间
   *
   * @param date
   * @return
   */
  public static Date dateTo19700101(Date date) throws ParseException {
    String dateStr = DateFormatUtils.format(date, "HH:mm:ss");
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1970-01-01 " + dateStr);
  }

  /**
   * 将第一个参数和第二个参数合成一个时间
   *
   * @param date
   * @param time
   * @return
   */
  public static Date timeToDate(Date date, Date time) throws ParseException {
    String dateStr = DateFormatUtils.format(date, "yyyy-MM-dd");
    String timeStr = DateFormatUtils.format(time, "HH:mm:ss");
    if (dateStr == null || timeStr == null) {
      return null;
    }
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr + " " + timeStr);
  }

  public static Date timeToDate(Date date, String time) throws ParseException {
    String dateStr = DateFormatUtils.format(date, "yyyy-MM-dd");
    String timeStr = time;
    int count = StringHelper.countChild(":",time);
    if (count == 1) {
      timeStr = time + ":00";
    } else if (count == 0) {
      timeStr = "00:00:00";
    }
    if (dateStr == null) {
      return null;
    }
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr + " " + timeStr);
  }

  /**
   * 计算两个日期之间的天数，包含两个日期
   *
   * @param smdate
   * @param bdate
   * @return
   * @throws ParseException
   */
  public static int daysBetween(Date smdate, Date bdate) throws ParseException {
    smdate = SDF.parse(SDF.format(smdate));
    bdate = SDF.parse(SDF.format(bdate));
    Calendar cal = Calendar.getInstance();
    cal.setTime(smdate);
    long time1 = cal.getTimeInMillis();
    cal.setTime(bdate);
    long time2 = cal.getTimeInMillis();
    long between_days = (time2 - time1) / (1000 * 3600 * 24);
    return Integer.parseInt(String.valueOf(between_days)) + 1;
  }

  /**
   * 计算指定两个日期之间的差值（可用于计算年龄）
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 返回差值（年）
   */
  public static Integer differFromDate(Date startDate, Date endDate) {
    int age = 0;
    Calendar born = Calendar.getInstance();
    Calendar now = Calendar.getInstance();
    if (startDate != null) {
      now.setTime(endDate);
      born.setTime(startDate);
      if (born.after(now)) {
        throw new IllegalArgumentException("开始日期不能超过结束日期");
      }
      age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
      int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
      int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
      if (nowDayOfYear < bornDayOfYear) {
        age -= 1;
      }
    }
    return age;
  }

  /**
   * 返回当天日期（yyyy-MM-dd）
   *
   * @return
   */
  public static Date getCurrentDate() {
    String nowStr = new DateTime().toDateStr();
    Date curDate;
    try {
      curDate = SDF.parse(nowStr);
    } catch (ParseException e) {
      throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
    }
    return curDate;
  }

  /**
   * 获取昨天
   *
   * @return
   */
  public static Date yesterday() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, -24);
    String yesterdayDate = SDF.format(calendar.getTime());
    Date yesterday;
    try {
      yesterday = SDF.parse(yesterdayDate);
    } catch (ParseException e) {
      throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
    }
    return yesterday;
  }

  /**
   * 格式成日期yyyy-MM-dd
   *
   * @param date
   * @return
   */
  public static Date toDate(Date date) {
    if (date == null) {
      return null;
    }
    try {
      date = SDF.parse(SDF.format(date));
    } catch (ParseException e) {
      throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
    }
    return date;
  }

  /** 年日期正则表达式 */
  public static String YEAR_REGEX = "^\\d{4}$";
  /** 月日期正则表达式 */
  public static String MONTH_REGEX = "^\\d{4}([-/.])\\d{1,2}$";
  /** 日日期正则表达式 */
  public static String DATE_REGEX = "^\\d{4}([-/.])\\d{1,2}\\1\\d{1,2}$";

  /**
   * 格式化日期 - yyyy-MM-dd HH:mm:ss
   *
   * @param date 日期
   * @param pattern 日期格式
   * @return 日期字符串
   */
  public static String format(Date date, String pattern) {
    SDF = new SimpleDateFormat(pattern);
    return SDF.format(date);
  }

  /**
   * 格式化日期 - yyyy-MM-dd HH:mm:ss
   *
   * @param date 日期字符串
   * @param pattern 日期格式
   * @return 日期
   * @throws ParseException 解析异常
   */
  public static Date parse(String date, String pattern) throws ParseException {
    SDF = new SimpleDateFormat(pattern);
    return SDF.parse(date);
  }

  /**
   * 日期范围 - 切片
   *
   * <pre>
   * -- eg:
   * 年 ----------------------- sliceUpDateRange("2018", "2020");
   * rs: [2018, 2019, 2020]
   *
   * 月 ----------------------- sliceUpDateRange("2018-06", "2018-08");
   * rs: [2018-06, 2018-07, 2018-08]
   *
   * 日 ----------------------- sliceUpDateRange("2018-06-30", "2018-07-02");
   * rs: [2018-06-30, 2018-07-01, 2018-07-02]
   * </pre>
   *
   * @param startDate 起始日期
   * @param endDate 结束日期
   * @return 切片日期
   */
  public static List<String> sliceUpDateRange(String startDate, String endDate) {
    List<String> rs = new ArrayList<>();
    try {
      int dt = Calendar.DATE;
      String pattern = "yyyy-MM-dd";
      if (startDate.matches(YEAR_REGEX)) {
        pattern = "yyyy";
        dt = Calendar.YEAR;
      } else if (startDate.matches(MONTH_REGEX)) {
        pattern = "yyyy-MM";
        dt = Calendar.MONTH;
      } else if (startDate.matches(DATE_REGEX)) {
        pattern = "yyyy-MM-dd";
        dt = Calendar.DATE;
      }
      Calendar sc = Calendar.getInstance();
      Calendar ec = Calendar.getInstance();
      sc.setTime(parse(startDate, pattern));
      ec.setTime(parse(endDate, pattern));
      while (sc.compareTo(ec) < 1) {
        rs.add(format(sc.getTime(), pattern));
        sc.add(dt, 1);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return rs;
  }
}
