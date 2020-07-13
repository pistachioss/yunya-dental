package com.yunya.feign.system;

import com.yunya.feign.system.factory.RemoteSystemServiceFallBackFactory;
import com.yunya.feign.system.domain.LogInfo;
import com.yunya.feign.system.domain.PermissionInfo;
import com.yunya.feign.system.domain.UserInfo;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 简单介绍: 云牙系统基础服务接口调用
 *
 * @author: chow
 * @date: 2020/7/5 15:47
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_SYSTEM_SERVICE,
    fallbackFactory = RemoteSystemServiceFallBackFactory.class)
public interface RemoteSystemServiceFeign {

  /**
   * 校验用户合法性
   *
   * @param params 参数封装
   * @return
   */
  @RequestMapping(value = "/api/user/validate", method = RequestMethod.POST)
  UserInfo validate(@RequestParam Map<String, Object> params);

  /**
   * 根据用户ID获取用户的权限列表
   *
   * @param userId 用户ID
   * @return
   */
  @RequestMapping(value = "/api/user/one", method = RequestMethod.GET)
  Set<String> selectPermsByUserId(Integer userId);

  /**
   * 获取所有权限列表
   *
   * @return
   */
  @RequestMapping(value = "/api/user/all", method = RequestMethod.GET)
  List<PermissionInfo> getAllPermissionInfo();

  /**
   * 根据用户名获取权限列表
   *
   * @param uniqueName
   * @return
   */
  @RequestMapping(value = "/api/user/get", method = RequestMethod.GET)
  List<PermissionInfo> getPermissionByUsername(String uniqueName);

  /**
   * 保存日志信息
   *
   * @param log
   */
  @RequestMapping(value = "/api/user/save", method = RequestMethod.POST)
  void saveLog(LogInfo log);
}
