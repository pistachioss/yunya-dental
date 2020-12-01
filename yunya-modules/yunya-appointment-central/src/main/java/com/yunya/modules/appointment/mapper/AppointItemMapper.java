package com.yunya.modules.appointment.mapper;
import com.yunya.models.appointment.AppointItem;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.domain.query.AppointItemTypeQuery;
import com.yunya.feign.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.feign.appointment.vo.AppointmentItemVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AppointItemMapper extends Mapper<AppointItem> {
    /**
     * 根据门诊id、预约名称查询预约信息
     *
     * @param compClinId 门诊id
     * @param keyword    关键字
     * @return
     */
    List<AppointmentItemVo> selectAppitemByName(@Param("compClinId") String compClinId, @Param("keyword") String keyword);

    /**
     * 根据条件查询所有可预约项目
     *
     * @param form
     * @return
     */
    List<AppointmentItemEnableModelVo> selectAllAppointItemByOrgId(@Param("form") AppointItemTypeQuery form);

    /**
     * 根据条件查询可预约项目类型（公司端）
     *
     * @param form
     * @return
     */
    List<AppointmentItemVo> findAppointItemByExample(@Param("form") AppointItemQuery form);

    /**
     * 根据条件查询可预约项目类型(门诊端)
     *
     * @param form
     * @return
     */
    List<AppointmentItemVo> findAppointItemList(@Param("form") AppointItemQuery form);

    /**
     * 插入实体
     * @param model 实体
     * @return 返回影响行数
     */
    Integer insertEntity(AppointItem model);
}