package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.system.SysGateLog;
import com.yunya.modules.system.mapper.SysGateLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简单介绍:</br>
 *
 * @author: chow
 * @date: 2020/6/19 14:15
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysGateLogBiz extends BaseBiz<SysGateLogMapper, SysGateLog> {
    /***/
}