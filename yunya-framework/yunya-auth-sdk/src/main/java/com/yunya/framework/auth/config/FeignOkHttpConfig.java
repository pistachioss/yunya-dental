package com.yunya.framework.auth.config;

import com.yunya.framework.auth.interceptor.OkHttpTokenInterceptor;
import feign.Feign;
import feign.codec.Decoder;
import okhttp3.ConnectionPool;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
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

  @Bean
  public Decoder feignDecoder() {
    return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
  }

  public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
    final HttpMessageConverters httpMessageConverters =
        new HttpMessageConverters(new JavaMappingJackson2HttpMessageConverter());
    return () -> httpMessageConverters;
  }

  public static class JavaMappingJackson2HttpMessageConverter
      extends MappingJackson2HttpMessageConverter {
    JavaMappingJackson2HttpMessageConverter() {
      List<MediaType> mediaTypes = new ArrayList<>();
      mediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")); // 关键
      setSupportedMediaTypes(mediaTypes);
    }
  }
}
