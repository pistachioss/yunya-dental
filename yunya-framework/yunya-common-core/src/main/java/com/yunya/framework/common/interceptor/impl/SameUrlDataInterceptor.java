package com.yunya.framework.common.interceptor.impl;

import cn.hutool.json.JSONUtil;
import com.yunya.framework.common.interceptor.RepeatSubmitInterceptor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 判断请求url和数据是否和上一次相同， 如果和上次相同，则是重复提交表单。 有效时间为5秒内。
 *
 * @author chow
 */
@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {

  public final String REPEAT_PARAMS = "repeatParams";

  public final String REPEAT_TIME = "repeatTime";

  public final String SESSION_REPEAT_KEY = "repeatData";

  /**
   * 间隔时间，单位:秒 默认5秒
   *
   * <p>两次相同参数的请求，如果间隔时间大于该参数，系统不会认定为重复提交的数据
   */
  private int intervalTime = 1;

  /**
   * 设置间隔时间
   *
   * @param intervalTime 间隔时间
   */
  public void setIntervalTime(int intervalTime) {
    this.intervalTime = intervalTime;
  }

  /**
   * 是否重复提交
   *
   * @param request 请求
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean isRepeatSubmit(HttpServletRequest request) {
    // 本次参数及系统时间
    String nowParams = JSONUtil.toJsonPrettyStr(request.getParameterMap());
    Map<String, Object> nowDataMap = new HashMap<>(16);
    nowDataMap.put(REPEAT_PARAMS, nowParams);
    nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

    // 请求地址（作为存放session的key值）
    String url = request.getRequestURI();

    HttpSession session = request.getSession();
    Object sessionObj = session.getAttribute(SESSION_REPEAT_KEY);
    if (sessionObj != null) {
      Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
      if (sessionMap.containsKey(url)) {
        Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
        if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap)) {
          return true;
        }
      }
    }
    Map<String, Object> sessionMap = new HashMap<>(16);
    sessionMap.put(url, nowDataMap);
    session.setAttribute(SESSION_REPEAT_KEY, sessionMap);
    return false;
  }

  /**
   * 判断参数是否相同
   *
   * @param nowMap 当前参数
   * @param preMap 上次请求参数
   * @return
   */
  private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
    String nowParams = (String) nowMap.get(REPEAT_PARAMS);
    String preParams = (String) preMap.get(REPEAT_PARAMS);
    return nowParams.equals(preParams);
  }

  /**
   * 判断两次间隔时间
   *
   * @param nowMap 当前请求时间
   * @param preMap 上次请求时间
   * @return
   */
  private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap) {
    long time1 = (Long) nowMap.get(REPEAT_TIME);
    long time2 = (Long) preMap.get(REPEAT_TIME);
    return (time1 - time2) < (this.intervalTime * 1000);
  }
}
