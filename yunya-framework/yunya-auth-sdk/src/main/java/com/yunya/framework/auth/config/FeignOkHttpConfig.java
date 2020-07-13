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
 * feign请求拦截器
 *
 * @author chow
 */
@AutoConfigureBefore(FeignAutoConfiguration.class)
@Configuration
@ConditionalOnClass(Feign.class)
public class FeignOkHttpConfig {

  @Autowired OkHttpTokenInterceptor okHttpLoggingInterceptor;

  @Bean
  public okhttp3.OkHttpClient okHttpClient() {
    int feignOkHttpReadTimeout = 60;
    int feignConnectTimeout = 60;
    int feignWriteTimeout = 120;
    return new okhttp3.OkHttpClient.Builder()
        .readTimeout(feignOkHttpReadTimeout, TimeUnit.SECONDS)
        .connectTimeout(feignConnectTimeout, TimeUnit.SECONDS)
        .writeTimeout(feignWriteTimeout, TimeUnit.SECONDS)
        .connectionPool(new ConnectionPool())
        .addInterceptor(okHttpLoggingInterceptor)
        .build();
  }
}
