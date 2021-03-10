package com.yunya.modules.system.mapper;

import com.yunya.feign.system.form.AppVersionCheckForm;
import com.yunya.feign.system.form.AppVersionAddForm;
import com.yunya.feign.system.vo.AppVersionVO;
import com.yunya.models.system.AppVersion;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface AppVersionMapper extends Mapper<AppVersion> {

    /**
     * 添加APP版本信息
     * @param form
     * @return
     */
    int addAppVersion(@Param("form") AppVersionAddForm form);

    /**
     * 获取最新发布的APP版本信息
     * @param form 查询表单
     * @return AppVersionVO
     */
    AppVersionVO lastReleaseApp(@Param("form") AppVersionCheckForm form);
}