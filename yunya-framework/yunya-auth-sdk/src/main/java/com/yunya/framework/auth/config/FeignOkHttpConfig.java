package com.yunya.framework.auth.config;

import com.yunya.framework.auth.interceptor.OkHttpTokenInterceptor;
import feign.Feign;
import okhttp3.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * feignOkHttp配置
 *
 * @author chow
 */
@AutoConfigureBefore(FeignAutoConfiguration.class)
@Configuration
@ConditionalOnClass(Feign.class)
public class FeignOkHttpConfig {

  @Autowired private OkHttpTokenInterceptor okHttpLoggingInterceptor;

  @Bean
  public okhttp3.OkHttpClient okHttpClient() {
    int feignOkHttpReadTimeout = 60;
    int feignConnectTimeout = 60;
    int feignWriteTimeout = 120;
    return new okhttp3.OkHttpClient.Builder()
        // 设置读取超时
        .readTimeout(feignOkHttpReadTimeout, TimeUnit.SECONDS)
        // 设置连接超时
        .connectTimeout(feignConnectTimeout, TimeUnit.SECONDS)
        // 设置写入超时
        .writeTimeout(feignWriteTimeout, TimeUnit.SECONDS)
        .connectionPool(new ConnectionPool())
        .addInterceptor(okHttpLoggingInterceptor)
        .build();
  }
}
