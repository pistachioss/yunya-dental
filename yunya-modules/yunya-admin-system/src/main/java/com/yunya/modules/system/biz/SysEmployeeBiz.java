package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.system.mapper.SysEmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_EMPLOYEE_INFO;

/**
 * 简介: 员工信息业务层
 *
 * @author: chow
 * @date: 2020/9/11 14:45
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysEmployeeBiz extends BaseBiz<SysEmployeeMapper, SysEmployee> {

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;

  /**
   * 根据员工ID查询员工信息
   *
   * @param userId 员工ID
   * @return SysEmployee
   */
  public SysEmployee findSysEmployeeById(Integer userId) {
    String empKey = REDIS_KEY_EMPLOYEE_INFO + userId;
    SysEmployee sysEmployee = redisUtils.get(empKey, SysEmployee.class);
    if (null != sysEmployee) {
      sysEmployee = mapper.selectByUserId(userId);
      if (null != sysEmployee) {
        redisUtils.set(empKey, sysEmployee);
      }
    }
    return sysEmployee;
  }
}
