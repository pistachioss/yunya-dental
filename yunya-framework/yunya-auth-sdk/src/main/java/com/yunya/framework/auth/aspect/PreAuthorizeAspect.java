package com.yunya.framework.auth.aspect;

import com.yunya.framework.auth.annotation.HasPermissions;
import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.auth.UserAuthException;
import com.yunya.framework.common.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 权限配置切面
 *
 * @author chow
 */
@Aspect
@Component
@Slf4j
public class PreAuthorizeAspect {
  // todo 补充feign调用
  // @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /**
   * 环绕通知
   *
   * @param point 切点
   * @return
   * @throws Throwable
   */
  @Around("@annotation(com.yunya.framework.auth.annotation.HasPermissions)")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    Signature signature = point.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;
    Method method = methodSignature.getMethod();
    HasPermissions annotation = method.getAnnotation(HasPermissions.class);
    if (annotation == null) {
      return point.proceed();
    }
    // todo 获取请求资源权限
    String authority = new StringBuilder(annotation.value()).toString();
    if (has(authority)) {
      return point.proceed();
    } else {
      throw new UserAuthException("操作权限不足");
    }
  }

  /**
   * 判断是否有操作权限
   *
   * @param authority 权限
   * @return
   */
  private boolean has(String authority) {
    // 用超管帐号方便测试，拥有所有权限
    HttpServletRequest request = ServletUtils.getRequest();
    String tmpUserKey = request.getHeader(CommonConstants.CONTEXT_KEY_USER_ID);
    if (Optional.ofNullable(tmpUserKey).isPresent()) {
      Integer userId = Integer.valueOf(tmpUserKey);
      log.debug("userId:{}", userId);
      if (userId == 1L) {
        return true;
      }
      // todo 完善权限匹配逻辑，判断用户是否具有该资源权限
      return true;
      // return systemServiceFeign.selectPermsByUserId(userId).stream().anyMatch(authority::equals);
    }
    return false;
  }
}
