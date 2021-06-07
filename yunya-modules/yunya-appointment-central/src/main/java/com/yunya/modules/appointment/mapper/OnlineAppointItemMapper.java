package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.OnlineAppointItemQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.models.appointment.OnlineAppointItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OnlineAppointItemMapper extends Mapper<OnlineAppointItem> {
    /**+
     * 查询线上预约项目
     * @param id id
     * @return OnlineAppointItemVo
     */
    OnlineAppointItemVo findOnlineAppointItemById(@Param("id") Integer id);

    /**
     * 查询线上预约项目列表
     * @param query 查询条件
     * @return 查询结果
     */
    List<OnlineAppointItemVo> findOnlineAppointItemByCondition(@Param("query") OnlineAppointItemQuery query);
}