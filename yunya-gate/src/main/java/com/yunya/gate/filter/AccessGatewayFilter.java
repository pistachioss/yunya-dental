package com.yunya.gate.filter;

import com.alibaba.fastjson.JSON;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.LogInfo;
import com.yunya.feign.system.vo.PermissionInfo;
import com.yunya.framework.auth.config.UserAuthConfig;
import com.yunya.framework.auth.utils.UserAuthUtil;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.jwt.IJWTInfo;
import com.yunya.gate.handler.RequestBodyRoutePredicateFactory;
import com.yunya.gate.utils.DBLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 网关过滤器
 *
 * @author ace
 * @create 2018/3/12.
 */
@Configuration
@Slf4j
public class AccessGatewayFilter implements GlobalFilter {

  /** 注入对象 */
  @Autowired @Lazy private RemoteSystemServiceFeign systemServiceFeign;

  @Autowired private UserAuthUtil userAuthUtil;

  @Autowired private UserAuthConfig userAuthConfig;

  @Resource(name = "stringRedisTemplate")
  private ValueOperations<String, String> valueOperations;

  /** 忽略网关鉴权的路径 */
  @Value("${gate.ignore.startWith}")
  private String startWith;

  /** 网关前缀 */
  private static final String GATE_WAY_PREFIX = "/api";

  /**
   * 过滤器 1、判断用户请求是否忽略鉴权（配置文件中 startWith后的地址都无需鉴权）； 2、对用户token合法性进行判断（是否过期、签名是否合法等）；
   * 3、比较当前用户token与redis中的token是否一致，保证登陆的用户只有一个;
   *
   * @param serverWebExchange 服务网络交换器
   * @param gatewayFilterChain 过滤链
   * @return
   */
  @Override
  public Mono<Void> filter(
      ServerWebExchange serverWebExchange, GatewayFilterChain gatewayFilterChain) {
    log.info("check user token....");
    // 获取请求属性（该属性已做非空判断）
    LinkedHashSet<Object> requiredAttribute =
        serverWebExchange.getRequiredAttribute(
            ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
    ServerHttpRequest request = serverWebExchange.getRequest();
    // 获取请求uri
    String requestUri = request.getPath().pathWithinApplication().value();
    for (Object o : requiredAttribute) {
      URI next = (URI) o;
      if (next.getPath().startsWith(GATE_WAY_PREFIX)) {
        // 截取api后的uri
        requestUri = next.getPath().substring(GATE_WAY_PREFIX.length());
      }
    }
    BaseContextHandler.setToken(null);
    ServerHttpRequest.Builder mutate = request.mutate();
    // 不需要拦截的路径
    if (isStartWith(requestUri)) {
      ServerHttpRequest build = mutate.build();
      return gatewayFilterChain.filter(serverWebExchange.mutate().request(build).build());
    }

    // 获取请求上头携带的token
    String authToken = getToken(request);
    if (StringUtils.isBlank(authToken)) {
      return setUnauthorizedResponse(serverWebExchange, "Token Can't Null Or Empty String!");
    }

    // 对用户token合法性进行鉴权
    IJWTInfo jwtInfo;
    try {
      // 获取当前用户信息
      jwtInfo = userAuthUtil.getInfoFromToken(authToken);
    } catch (Exception e) {
      log.error("用户Token过期异常", e);
      return setUnauthorizedResponse(serverWebExchange, "User Token Forbidden or Expired!");
    }

    // 获取该用户在redis中存储的的token
    String currentUserIdKey = RedisConstants.setKey(RedisConstants.REDIS_KEY_USER_ID, jwtInfo.getDeviceType(), jwtInfo.getId());
    String redisToken = valueOperations.get(currentUserIdKey);
    if (StringUtils.isBlank(redisToken) || !authToken.equals(redisToken)) {
      return setUnauthorizedResponse(serverWebExchange, "Account Has Been Logged In Other Place!");
    }

    // 获取redis中存储的用户信息
    String userInfoStr = valueOperations.get(RedisConstants.REDIS_KEY_USER_TOKEN + redisToken);
    if (StringUtils.isBlank(userInfoStr)) {
      return setUnauthorizedResponse(serverWebExchange, "User Token Verify Failed!");
    }

    // 将token设置到请求头和线程局部变量RouteLocatorBuilder
    mutate.header(userAuthConfig.getTokenHeader(), redisToken);
    BaseContextHandler.setToken(redisToken);

    // 获取请求方法
    /*    final String method = Objects.requireNonNull(request.getMethod()).toString();
    List<PermissionInfo> permissionIfs = userService.getAllPermissionInfo();
    // 判断资源是否启用权限约束
    Stream<PermissionInfo> stream = getPermissionIfs(requestUri, method, permissionIfs);
    List<PermissionInfo> result = stream.collect(Collectors.toList());
    PermissionInfo[] permissions = result.toArray(new PermissionInfo[] {});
    if (permissions.length > 0) {
      if (checkUserPermission(permissions, serverWebExchange, jwtInfo)) {
        return setUnauthorizedResponse(
            serverWebExchange, "User Access Forbidden!Does not has Permission!");
      }
    }*/
    ServerHttpRequest reqBuild = mutate.build();
    return gatewayFilterChain.filter(serverWebExchange.mutate().request(reqBuild).build());
  }

  /**
   * 抛出网关异常
   *
   * @param exchange 交换器
   * @param msg 提示信息
   * @return
   */
  @NotNull
  private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, String msg) {
    ServerHttpResponse originalResponse = exchange.getResponse();
    originalResponse.setStatusCode(HttpStatus.OK);
    originalResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());

    return originalResponse.writeWith(
        Mono.fromSupplier(
            () -> {
              DataBufferFactory bufferFactory = originalResponse.bufferFactory();
              return bufferFactory.wrap(JSON.toJSONBytes(ResponseUtil.fail(401, msg, null)));
            }));
  }

  /**
   * 获取目标权限资源
   *
   * @param requestUri uri
   * @param method 请求方法
   * @param serviceInfo 权限列表
   * @return
   */
  private Stream<PermissionInfo> getPermissionIfs(
      final String requestUri, final String method, List<PermissionInfo> serviceInfo) {
    return serviceInfo.parallelStream()
        .filter(
            permissionInfo -> {
              String uri = permissionInfo.getUri();
              if (uri.indexOf("{") > 0) {
                uri = uri.replaceAll("\\{\\*}", "[a-zA-Z\\\\d]+");
              }
              String regEx = "^" + uri + "$";
              return (Pattern.compile(regEx).matcher(requestUri).find())
                  && method.equals(permissionInfo.getMethod());
            });
  }

  /**
   * 设置用户信息和日志信息
   *
   * @param serverWebExchange 交换机
   * @param user 用户信息
   * @param pm 权限
   */
  private void setCurrentUserInfoAndLog(
      ServerWebExchange serverWebExchange, IJWTInfo user, PermissionInfo pm) {
    String host =
        Objects.requireNonNull(serverWebExchange.getRequest().getRemoteAddress()).toString();
    LogInfo logInfo =
        new LogInfo(
            pm.getMenu(),
            pm.getName(),
            pm.getUri(),
            new Date(),
            user.getId(),
            user.getName(),
            host,
            String.valueOf(
                serverWebExchange
                    .getAttributes()
                    .get(RequestBodyRoutePredicateFactory.REQUEST_BODY_ATTR)));
    DBLog.getInstance().setLogService(systemServiceFeign).offerQueue(logInfo);
  }

  /**
   * 返回session中的用户信息，并对用户token进行权限校验
   *
   * @param request 请求
   * @return
   */
  private String getToken(ServerHttpRequest request) {
    HttpHeaders httpHeaders = request.getHeaders();
    // 获取请求携带token
    List<String> strings = httpHeaders.get(userAuthConfig.getTokenHeader());
    String authToken = null;
    if (null != strings) {
      authToken = strings.get(0);
    }
    if (StringUtils.isBlank(authToken)) {
      authToken = httpHeaders.getFirst("token");
    }
    return authToken;
  }

  /**
   * 检查用户权限
   *
   * @param permissions 权限
   * @param ctx 交换机
   * @param user 用户信息
   * @return
   */
  private boolean checkUserPermission(
      PermissionInfo[] permissions, ServerWebExchange ctx, IJWTInfo user) {
    List<PermissionInfo> permissionInfos =
        systemServiceFeign.getPermissionByUsername(user.getUniqueName());
    PermissionInfo current = null;
    for (PermissionInfo info : permissions) {
      boolean anyMatch =
          permissionInfos.parallelStream()
              .anyMatch(permissionInfo -> permissionInfo.getCode().equals(info.getCode()));
      if (anyMatch) {
        current = info;
        break;
      }
    }
    if (current == null) {
      return true;
    } else {
      if (!RequestMethod.GET.toString().equals(current.getMethod())) {
        setCurrentUserInfoAndLog(ctx, user, current);
      }
      return false;
    }
  }

  /**
   * URI是否以什么打头
   *
   * @param requestUri 请求路径
   * @return
   */
  private boolean isStartWith(String requestUri) {
    return Arrays.stream(startWith.split(",")).anyMatch(str -> requestUri.contains(str.trim()));
  }

  /**
   * 网关抛异常
   *
   * @param body 交换器
   * @param code 响应状态码
   */
  private Mono<Void> setFailedRequest(ServerWebExchange serverWebExchange, String body, int code) {
    serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);
    return serverWebExchange.getResponse().setComplete();
  }
}
