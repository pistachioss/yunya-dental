package com.yunya.framework.common.utils;

import com.yunya.framework.common.enums.LengthUnitEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.yunya.framework.common.enums.LengthUnitEnum.*;

/**
 * 地理位置相关工具类
 *
 * @author mwj
 * @date 2018-10-30
 */
@Slf4j
public class LocationUtil {

  private static final double EARTH_RADIUS = 6378137.0;

  /**
   * 计算两个经纬度之间的距离
   *
   * @param longitude1 坐标经度1
   * @param latitude1 坐标纬度1
   * @param longitude2 坐标经度2
   * @param latitude2 坐标纬度2
   * @return
   */
  public static double getDistance(
      double longitude1, double latitude1, double longitude2, double latitude2) {
    double lat1 = rad(latitude1);
    double lat2 = rad(latitude2);
    double a = lat1 - lat2;
    double b = rad(longitude1) - rad(longitude2);
    double s =
        2
            * Math.asin(
                Math.sqrt(
                    Math.pow(Math.sin(a / 2), 2)
                        + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
    s = s * EARTH_RADIUS;
    s = Math.round(10000 * s) / 10000;
    return s;
  }

  public static double distanceByLongNLat(String lon1, String lat1, String lon2, String lat2) {
    return distanceByLongNLat(Double.parseDouble(lon1), Double.parseDouble(lat1), Double.parseDouble(lon2), Double.parseDouble(lat2));
  }

  /**
   * 计算地球上任意两点(经纬度)距离
   *
   * @param longitude1 第一点经度
   * @param latitude1  第一点纬度
   * @param longitude2 第二点经度
   * @param latitude2  第二点纬度
   * @return 返回距离 单位：米
   */
  public static double distanceByLongNLat(double longitude1, double latitude1, double longitude2, double latitude2) {
    return distanceByLongNLat(longitude1, latitude1, longitude2, latitude2, METER);
  }

  public static double distanceKilometer(String lon1, String lat1, String lon2, String lat2) {
    return distanceKilometer(Double.parseDouble(lon1), Double.parseDouble(lat1), Double.parseDouble(lon2), Double.parseDouble(lat2));
  }

  /**
   * 计算地球上任意两点(经纬度)距离
   *
   * @param longitude1 第一点经度
   * @param latitude1  第一点纬度
   * @param longitude2 第二点经度
   * @param latitude2  第二点纬度
   * @return 返回距离 单位：千米米
   */
  public static double distanceKilometer(double longitude1, double latitude1, double longitude2, double latitude2) {
    return distanceByLongNLat(longitude1, latitude1, longitude2, latitude2, KILOMETER);
  }


  public static double distanceByLongNLat(double longitude1, double latitude1, double longitude2, double latitude2, LengthUnitEnum unit) {
    Double EARTH_RADIUS = 6370.996; // 地球半径系数
    Double PI = 3.1415926;

    Double radLat1 = latitude1 * PI / 180.0;
    Double radLat2 = latitude2 * PI / 180.0;

    Double radLng1 = longitude1 * PI / 180.0;
    Double radLng2 = longitude2 * PI /180.0;

    Double a =  radLat1 -  radLat2;
    Double b =  radLng1 -  radLng2;

    Double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b/2),2)));
    distance = distance * EARTH_RADIUS * 1000;
    BigDecimal bg = new BigDecimal(distance);
    if (KILOMETER.equals(unit)) {// km
      bg = bg.divide(new BigDecimal(1000));
    } else if (DECIMETER.equals(unit)) {// dm
      bg = bg.multiply(new BigDecimal(10));
    } else if (CENTIMETER.equals(unit)) {// cm
      bg = bg.multiply(new BigDecimal(100));
    }
    return bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  private static double rad(double d) {
    return d * Math.PI / 180.0;
  }
}
