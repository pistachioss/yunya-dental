package com.yunya.framework.common.utils;

import cn.hutool.core.date.DateTime;
import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
  /** 最大秒 */
  public static final int MAX_SECOND = 59;

  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

  private static final SimpleDateFormat NUMBER_DATESDF = new SimpleDateFormat("yyyyMMdd");
  private static final SimpleDateFormat NUMBER_YEARSDF = new SimpleDateFormat("yyyy");
  private static final SimpleDateFormat NUMBER_MONTHSDF = new SimpleDateFormat("yyyyMM");
  /** 年 */
  public static final int YEAR = 1;
  /** 月 */
  public static final int MONTH = 2;
  /** 日 */
  public static final int DAY = 3;
  /** 时 */
  public static final int HOURS = 4;
  /** 分 */
  public static final int MINUTE = 5;
  /** 秒 */
  public static final int SECONDS = 6;

  private DateUtil() {}

  public static String parseObjectToStr(final String format, final Object dateObj)
      throws ParseException {
    Date date = null;
    if (dateObj instanceof Date) {
      date = (Date) dateObj;
    } else if (dateObj instanceof String) {
      String dateStr = (String) dateObj;
      date = parse(dateStr, format);
    } else {
      throw new ClientServiceException("时间日期数据类型错误", CommonConstants.ILLEGAL_PARAMETERS_CODE);
    }
    return format(date, format);
  }

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

  public static String getEndDate(String date) {
    int year = Integer.parseInt(date.substring(0, 4));
    int month = 12;
    if (!date.matches(YEAR_REGEX)) {
      month = Integer.parseInt(date.substring(5,7));
    }
    YearMonth yearMonth = YearMonth.of(year, month);
    return yearMonth.atEndOfMonth().toString();
  }

  public static String getStartDate(String date) {
    int year = Integer.parseInt(date.substring(0, 4));
    int month = 01;
    if (!date.matches(YEAR_REGEX)) {
      month = Integer.parseInt(date.substring(5,7));
    }
    YearMonth yearMonth = YearMonth.of(year, month);
    return yearMonth.atDay(1).toString();
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
    int count = StringHelper.countChild(":", time);
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

  public static String date2Number(String dateStr, String suffix) {
    if (StringHelper.isEmpty(dateStr)) {
      return null;
    }
    String date = StringHelper.remove(dateStr, "-");
    if (StringHelper.isEmpty(suffix)) {
      if (date.matches(YEAR_REGEX)) {// 年
        suffix = "0101";
      } else if (date.matches(MONTH_REGEX)){// 月
        suffix = "01";
      } else {
        suffix = "";
      }
    }
    return date + suffix;
  }

  /**
   * 日期格式成数字形式：yyyyMMdd
   *
   * @param date
   * @return
   */
  public static Integer date2Number(Date date) {
    return date2Number(date, NUMBER_DATESDF);
  }

  public static Integer date2Number(Date date, SimpleDateFormat sdf) {
    Integer dateNumber = null;
    if (date == null) {
      return dateNumber;
    }
    return Integer.parseInt(sdf.format(date));
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
    return new SimpleDateFormat(pattern).format(date);
  }

  /**
   * 格式化日期 - yyyy-MM-dd HH:mm:ss
   *
   * @param date 日期
   * @param sdf 日期解析器
   * @return 日期字符串
   */
  public static String format(Date date, SimpleDateFormat sdf) {
    return sdf.format(date);
  }

  /**
   * 解析成日期
   *  支持格式：yyyy-MM-dd, yyyy-M-d, yyyyMMdd, yyyy/MM/dd, yyyy/M/d
   *
   * @param dateStr 日期字符串
   * @return 日期
   * @throws ParseException 解析异常
   */
  public static Date parse2Date(String dateStr) {
    Date date = null;
    try {
      date = SDF.parse(dateStr);
    } catch (Exception e) {
    }
    if (ObjectUtils.isEmpty(date)) {
      try {
        date = new SimpleDateFormat("yyyy-M-d").parse(dateStr);
      } catch (ParseException e) {
      }
    }
    if (ObjectUtils.isEmpty(date)) {
      try {
        date = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
      } catch (ParseException e) {
      }
    }
    if (ObjectUtils.isEmpty(date)) {
      try {
        date = new SimpleDateFormat("yyyy/MM/dd").parse(dateStr);
      } catch (ParseException e) {
      }
    }
    if (ObjectUtils.isEmpty(date)) {
      try {
        date = new SimpleDateFormat("yyyy/M/d").parse(dateStr);
      } catch (ParseException e) {
      }
    }
    if (ObjectUtils.isEmpty(date)) {
      throw new ClientServiceException("date parse error param：" + dateStr + " format not supported！", 1);
    }
    return date;
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
    return new SimpleDateFormat(pattern).parse(date);
  }

  /**
   * 格式化日期 - yyyy-MM-dd HH:mm:ss
   *
   * @param date 日期字符串
   * @param sdf 日期解析器
   * @return 日期
   * @throws ParseException 解析异常
   */
  public static Date parse(String date, SimpleDateFormat sdf) throws ParseException {
    return sdf.parse(date);
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

  /**
   * 上一年
   *
   * @param year
   * @return
   */
  public static String preYear(String year) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    Calendar c = Calendar.getInstance();
    try {
      c.setTime(sdf.parse(year));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    c.add(Calendar.YEAR, -1);
    return sdf.format(c.getTime());
  }

  /**
   * 环比日期，即上一个日期（年、月、日）
   *
   * @param date 日期
   * @param diff 差值
   * @return
   */
  public static String preDate(String date, int diff) {
    String[] dates = date.split("-");
    if (date.matches(MONTH_REGEX)) { // 月
      return preDate(date, diff, "yyyy-MM", Calendar.MONTH);
    } else if (date.matches(YEAR_REGEX)) { // 年
      return preDate(date, diff, "yyyy", Calendar.YEAR);
    }
    return preDate(date, diff, "yyyy-MM-dd", Calendar.DATE);
  }

  /**
   * 环比日期，即上一个日期（年、月、日）
   *
   * @param date 日期
   * @param diff 差值
   * @param format 年月日的格式
   * @param dateField 年月日的增加字段
   * @return
   */
  public static String preDate(String date, int diff, String format, int dateField) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    Calendar c = Calendar.getInstance();
    try {
      c.setTime(sdf.parse(date));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    c.add(dateField, -diff);
    return sdf.format(c.getTime());
  }

  /**
   * 日期字段差值：
   *
   * @param startDate
   * @param endDate
   * @return
   */
  public static int dateFieldDiff(String startDate, String endDate) {
    String[] sDates = startDate.split("-");
    String[] eDates = endDate.split("-");
    if (startDate.matches(MONTH_REGEX)) { // 月
      return Integer.parseInt(sDates[1]) - Integer.parseInt(eDates[1]);
    } else if (startDate.matches(YEAR_REGEX)) { // 年
      return Integer.parseInt(sDates[1]) - Integer.parseInt(eDates[1]);
    }
    return compareDate(startDate, endDate) - 1; // 日
  }

  public static int compareDate(String firstDate, String secondDate) {
    int count = StringHelper.countChild("-", firstDate);
    String pattern = "yyyy-MM-dd";
    if (count == 1) { // 月
      pattern = "yyyy-MM";
    } else if (count == 0) {
      pattern = "yyyy";
    }
    return compareDate(firstDate, secondDate, pattern);
  }

  public static int compareDate(String firstMonth, String secondMonth, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    Date d1 = null;
    Date d2 = null;
    try {
      d1 = sdf.parse(firstMonth);
      d2 = sdf.parse(secondMonth);
      return daysBetween(d1, d2);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * 时间加法计算
   *
   * @param time 时间 HH:mm:ss 或 HH:mm
   * @param expr 加数
   * @param unit 单位
   * @return 返回结果
   */
  public static String timeAdd(String time, int expr, int unit) {
    checkTimeFormat(time);
    LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    if (unit == DateUtil.HOURS) {
      return localTime.plusHours(expr).format(DateTimeFormatter.ofPattern("HH:mm"));
    } else if (unit == DateUtil.MINUTE) {
      return localTime.plusMinutes(expr).format(DateTimeFormatter.ofPattern("HH:mm"));
    } else if (unit == DateUtil.SECONDS) {
      return localTime.plusSeconds(expr).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    return "";
  }

  /**
   * 比较时间
   *
   * @param time1
   * @param time2
   * @return the comparator value, negative if less, positive if greater
   */
  public static int compareTime(String time1, String time2) {
    checkTimeFormat(time1);
    checkTimeFormat(time2);
    LocalTime localTime1 = LocalTime.parse(time1, DateTimeFormatter.ISO_LOCAL_TIME);
    LocalTime localTime2 = LocalTime.parse(time2, DateTimeFormatter.ISO_LOCAL_TIME);
    return localTime1.compareTo(localTime2);
  }

  /**
   * 校验时间格式
   *
   * @param time 时间
   */
  private static void checkTimeFormat(String time) {
    try {
      if (time == null || time.equals("")) {
        throw new ClientServiceException("时间格式不正确" + time, CommonConstants.ILLEGAL_PARAMETERS_CODE);
      }
      String[] times = time.split(":");
      for (int i = 0; i < times.length; i++) {
        if (i == 0) {
          Integer hours = Integer.valueOf(times[0]);
          if (hours < 0 || hours >= 24) {
            throw new ClientServiceException(
                "时间格式不正确" + time, CommonConstants.ILLEGAL_PARAMETERS_CODE);
          }
        } else {
          if (Integer.parseInt(times[1]) < 0 || Integer.parseInt(times[1]) > 59) {
            throw new ClientServiceException(
                "时间格式不正确" + time, CommonConstants.ILLEGAL_PARAMETERS_CODE);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 同比日期 即去年时的这个日期（日、月、年）
   *
   * @param dateStr
   * @return
   */
  public static String chainDate(String dateStr) {
    String[] dates = dateStr.split("-");
    String pattern = "yyyy-MM-dd";
    if (dateStr.matches(MONTH_REGEX)) {
      pattern = "yyyy-MM";
    } else if (dateStr.matches(YEAR_REGEX)) {
      pattern = "yyyy";
    }
    Date date = null;
    try {
      date = parse(dateStr, pattern);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.YEAR, -1);
    return format(c.getTime(), pattern);
  }

  public static String yearStart(String dateStr) {
    String[] dates = dateStr.split("-");
    if (dateStr.matches(MONTH_REGEX)) { // 月
      return dates[0] + "-01";
    } else if (dateStr.matches(YEAR_REGEX)) { // 年
      return dates[0];
    }
    return dates[0] + "-01-01";
  }

  public static String yearEnd(String dateStr) {
    String[] dates = dateStr.split("-");
    if (dateStr.matches(MONTH_REGEX)) { // 月
      return dates[0] + "-12";
    } else if (dateStr.matches(YEAR_REGEX)) { // 年
      return dates[0];
    }
    return dates[0] + "-12-31";
  }

  // -计算日期 start------------------------------------------

  /**
   * 计算结束时间与当前时间间隔的天数
   *
   * @param endDate 结束日期
   * @return 计算结束时间与当前时间间隔的天数
   */
  public static long until(Date endDate) {
    return LocalDateTime.now().until(dateToLocalDateTime(endDate), ChronoUnit.DAYS);
  }

  /**
   * 计算结束时间与开始时间间隔的天数
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 计算结束时间与开始时间间隔的天数
   */
  public static long until(Date startDate, Date endDate) {
    return dateToLocalDateTime(startDate).until(dateToLocalDateTime(endDate), ChronoUnit.DAYS);
  }

  /**
   * 计算结束时间与开始时间间隔的天数
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 计算结束时间与开始时间间隔的天数
   */
  public static long until(LocalDateTime startDate, LocalDateTime endDate) {
    return startDate.until(endDate, ChronoUnit.DAYS);
  }

  public static long until(LocalDate startDate, LocalDate endDate) {
    return startDate.until(endDate, ChronoUnit.DAYS);
  }

  public static Integer startDate2Number(String date) {
    if (!date.matches(DATE_REGEX)) {
      date = getStartDate(date);
    }
    return Integer.parseInt(date2Number(date, null));
  }

  public static Integer endDate2Number(String date) {
    if (!date.matches(DATE_REGEX)) {
      date = getEndDate(date);
    }
    return Integer.parseInt(date2Number(date, null));
  }

  /**
   * 怀孕月份转成孕周数
   * @param pregnancyMonth
   * @return
   */
  public static Integer pregancyMonth2Week(Integer pregnancyMonth) {
    return pregnancyMonth * 4;
  }

  /**
   * 怀孕孕周数转成孕月份
   * @param pregnancyWeek
   * @return
   */
  public static Integer pregancyWeek2Month(Integer pregnancyWeek) {
    return pregnancyWeek / 4;
  }
}
