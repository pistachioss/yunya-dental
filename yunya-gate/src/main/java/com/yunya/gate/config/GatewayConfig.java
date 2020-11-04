package com.yunya.gate.config;

import com.yunya.gate.handler.RequestBodyRoutePredicateFactory;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关配置
 *
 * @author ace
 * @create 2019/1/27.
 */
@Configuration
public class GatewayConfig {

  @Bean
  RequestBodyRoutePredicateFactory requestBodyRoutePredicateFactory() {
    return new RequestBodyRoutePredicateFactory();
  }

  /**
   * feign解码器
   *
   * @return
   */
  @Bean
  public Decoder feignDecoder() {
    return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
  }

  /**
   * http消息转换器
   *
   * @return
   */
  public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
    final HttpMessageConverters httpMessageConverters =
        new HttpMessageConverters(new GateWayMappingJackson2HttpMessageConverter());
    return () -> httpMessageConverters;
  }

  /** json转http */
  public static class GateWayMappingJackson2HttpMessageConverter
      extends MappingJackson2HttpMessageConverter {
    GateWayMappingJackson2HttpMessageConverter() {
      List<MediaType> mediaTypes = new ArrayList<>();
      mediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
      setSupportedMediaTypes(mediaTypes);
    }
  }

  /** 负载均衡器 */
  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }
}
