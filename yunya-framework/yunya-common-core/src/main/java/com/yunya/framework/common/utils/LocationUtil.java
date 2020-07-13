package com.yunya.framework.common.utils;

import lombok.extern.slf4j.Slf4j;

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

  private static double rad(double d) {
    return d * Math.PI / 180.0;
  }
}
