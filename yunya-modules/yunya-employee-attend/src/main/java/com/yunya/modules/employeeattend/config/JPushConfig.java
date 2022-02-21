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