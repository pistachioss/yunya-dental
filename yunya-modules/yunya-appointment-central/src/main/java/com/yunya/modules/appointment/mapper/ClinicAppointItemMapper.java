package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.ClinicAppointItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicAppointItemMapper extends Mapper<ClinicAppointItem> {

    /**
     * 根据门诊id和预约项目id删除记录
     * @param orgId   门诊id
     * @param appointItemId  预约项目记录
     * @return
     */
    Integer delClinicAppointItemByIdAndAppointItemId(@Param("orgId") Integer orgId, @Param("appointItemId") Integer appointItemId);

    /**
     * 根据门诊id和预约项目id 查询记录
     * @param orgId
     * @param appointItemId
     * @return
     */
    ClinicAppointItem findClinicAppointItemByOrgIdAndClinicAppointItemId(@Param("orgId") Integer orgId, @Param("appointItemId") Integer appointItemId);

    /**
     * 根据预约项目id 查询记录
     * @param appointItemId
     * @return
     */
    List<ClinicAppointItem> findClinicAppointItemByAppointItemId(@Param("appointItemId") Integer appointItemId);

    /**
     * 批量查询门使用项目
     * @param list
     */
    void insertClinicAppointItem(@Param("list") List<ClinicAppointItem> list);

}