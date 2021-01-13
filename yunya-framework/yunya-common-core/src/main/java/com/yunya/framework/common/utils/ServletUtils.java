package com.yunya.framework.common.utils;

import com.yunya.framework.common.utils.text.Convert;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet工具类
 *
 * @author chow
 */
@Slf4j
public class ServletUtils {

  /**
   * 获取String参数
   *
   * @param name
   * @return
   */
  public static String getParameter(String name) {
    return getRequest().getParameter(name);
  }

  /**
   * 获取String参数
   *
   * @param name
   * @param defaultValue
   * @return
   */
  public static String getParameter(String name, String defaultValue) {
    return Convert.toStr(getRequest().getParameter(name), defaultValue);
  }

  /**
   * 获取Integer参数
   *
   * @param name
   * @return
   */
  public static Integer getParameterToInt(String name) {
    return Convert.toInt(getRequest().getParameter(name));
  }

  /**
   * 获取Integer参数
   *
   * @param name
   * @param defaultValue
   * @return
   */
  public static Integer getParameterToInt(String name, Integer defaultValue) {
    return Convert.toInt(getRequest().getParameter(name), defaultValue);
  }

  /**
   * 获取request
   *
   * @return
   */
  public static HttpServletRequest getRequest() {
    return getRequestAttributes().getRequest();
  }

  /**
   * 获取response
   *
   * @return
   */
  public static HttpServletResponse getResponse() {
    return getRequestAttributes().getResponse();
  }

  /**
   * 获取session
   *
   * @return
   */
  public static HttpSession getSession() {
    return getRequest().getSession();
  }

  public static ServletRequestAttributes getRequestAttributes() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    return (ServletRequestAttributes) attributes;
  }

  /**
   * 将字符串渲染到客户端
   *
   * @param response 渲染对象
   * @param string 待渲染的字符串
   * @return null
   */
  public static String renderString(HttpServletResponse response, String string) {
    try {
      response.setContentType("application/json");
      response.setCharacterEncoding("utf-8");
      response.getWriter().print(string);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 是否是Ajax异步请求
   *
   * @param request
   */
  public static boolean isAjaxRequest(HttpServletRequest request) {
    String accept = request.getHeader("accept");
    if (accept != null && accept.contains("application/json")) {
      return true;
    }

    String xRequestedWith = request.getHeader("X-Requested-With");
    if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
      return true;
    }

    String uri = request.getRequestURI();
    if (StringHelper.inStringIgnoreCase(uri, ".json", ".xml")) {
      return true;
    }

    String ajax = request.getParameter("__ajax");
    return StringHelper.inStringIgnoreCase(ajax, "json", "xml");
  }

  public static DeviceType getCurrentDevice(HttpServletRequest request) {
    String userAgentStr = request.getHeader("User-Agent");
    if (userAgentStr.contains("okhttp")) {
      return OperatingSystem.ANDROID_MOBILE.getDeviceType();
    }
    UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
    OperatingSystem operatingSystem = userAgent.getOperatingSystem();
//    log.info("=============================登录获取当前设备========================");
//    log.info("==> 【User-Agent】:{}", userAgentStr);
//    log.info("==> 【系统】: {}", operatingSystem.getDeviceType());
//    log.info("==> 【系统名称】: {}", operatingSystem.getName());
//
//    if (operatingSystem.getName().equalsIgnoreCase(OperatingSystem.UNKNOWN.getName())) {
//      if (userAgentStr.contains("iPhone")) {
//        return OperatingSystem.MAC_OS_X_IPHONE.getDeviceType();
//      } else if (userAgentStr.contains("iPad")) {
//        return OperatingSystem.MAC_OS_X_IPAD.getDeviceType();
//      } else if (userAgentStr.contains("Mac OS")) {
//        return OperatingSystem.MAC_OS.getDeviceType();
//      } else if (userAgentStr.contains("Android")) {
//        return OperatingSystem.ANDROID.getDeviceType();
//      } else {
//        return OperatingSystem.UNKNOWN.getDeviceType();
//      }
//    }
    return operatingSystem.getDeviceType();
  }
}
