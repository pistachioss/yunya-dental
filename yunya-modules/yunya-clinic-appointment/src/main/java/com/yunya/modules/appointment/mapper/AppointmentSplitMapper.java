package com.yunya.modules.appointment.mapper;

import com.yunya.models.appointment.AppointmentSplit;
import com.yunya.feign.appointment.domain.query.AppointmentSplitQuery;
import com.yunya.feign.appointment.vo.AppointmentSplitVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AppointmentSplitMapper extends Mapper<AppointmentSplit> {

    /**
     * 批量插入预约分解
     * @param splitList
     * @return
     */
    int insertAppointmentSplit(@Param("splitList") List<AppointmentSplit> splitList);

    /**
     * 通过id集合批量删除分解预约
     * @param ids  id集合
     * @return
     */
    int delAppoointmentSplitByIds(@Param("ids") Integer[] ids);

    /**
     * 根据条件查询预约分解列表
     * @param query  条件集合
     * @return
     */
    List<AppointmentSplitVo> findAppointmentSplitByExample(@Param("query") AppointmentSplitQuery query);


}