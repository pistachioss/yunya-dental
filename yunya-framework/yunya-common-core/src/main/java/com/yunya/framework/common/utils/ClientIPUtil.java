package com.yunya.framework.common.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 客户端ID获取工具类
 *
 * @author chow
 */
public class ClientIPUtil {

  /**
   * 获取客户端真实ip
   *
   * @param request HttpServletRequest
   * @return String
   */
  public static String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    String word = "unknown";
    if (ip == null || ip.length() == 0 || word.equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || word.equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || word.equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
