package com.yunya.modules.employeeattend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jpush")
public class JPushConfig {

  private static String appKey;

  private static String appMasterSecret;

  private static Boolean production;

  public static Boolean getProduction() {
    return production;
  }

  @Value("${jpush.production}")
  public void setProduction(String production) {
    if ("prod".equals(production) || "true".equals(production)) {
      JPushConfig.production = true;
    } else {
      JPushConfig.production = false;
    }
  }

  public static String getAppKey() {
    return appKey;
  }

  @Value("${jpush.appKey}")
  public void setAppKey(String appKey) {
    JPushConfig.appKey = appKey;
  }

  public static String getAppMasterSecret() {
    return appMasterSecret;
  }

  @Value("${jpush.appMasterSecret}")
  public void setAppMasterSecret(String appMasterSecret) {
    JPushConfig.appMasterSecret = appMasterSecret;
  }
}
