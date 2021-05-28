package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.OnlineAppointItemSettingQuery;
import com.yunya.feign.appointment.vo.EnableOnlineAppointDentistsVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingVo;
import com.yunya.models.appointment.OnlineAppointItemSetting;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OnlineAppointItemSettingMapper extends Mapper<OnlineAppointItemSetting> {

    /**
     * 根据医生ID查询线上可预约项目
     * @param dentistId 医生ID
     * @return 返回查询结果
     */
    OnlineAppointItemSettingVo findItemSettingByDentistId(@Param("dentistId") Integer dentistId, @Param("orgId") Integer orgId);

    /**
     * 查询可预约医生列表
     * @param orgId 门诊ID
     * @param itemId 可预约项目ID
     * @return 返回可预约医生列表
     */
    List<EnableOnlineAppointDentistsVo> findDentistsByAppointItem(@Param("orgId") Integer orgId, @Param("itemId") Integer itemId);

}