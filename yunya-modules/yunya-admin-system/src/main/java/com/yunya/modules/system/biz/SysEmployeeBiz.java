package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.system.mapper.SysEmployeeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  /***/
}
