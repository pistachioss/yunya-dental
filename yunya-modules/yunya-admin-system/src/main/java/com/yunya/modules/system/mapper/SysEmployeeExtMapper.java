package com.yunya.modules.system.mapper;

import com.yunya.models.system.SysEmployeeExt;
import tk.mybatis.mapper.common.Mapper;

public interface SysEmployeeExtMapper extends Mapper<SysEmployeeExt> {
    void saveByPrimaryKeySelective(SysEmployeeExt entity);
}