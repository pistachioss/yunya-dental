package com.yunya.modules.appointment.mapper;

import com.yunya.feign.appointment.domain.query.OnlineAppointmentQuery;
import com.yunya.feign.appointment.vo.CountOnlineAppointVo;
import com.yunya.feign.appointment.vo.OnlineAppointmentVo;
import com.yunya.models.appointment.OnlineAppointment;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

public interface OnlineAppointmentMapper extends Mapper<OnlineAppointment> {

    /**
     * 根据预约申请ID查询预约详情
     * @param id 预约申请ID
     * @return 预约信息
     */
    OnlineAppointmentVo selectEntityById(@Param("id") Integer id);

    /**
     * 根据条件批量查询预约申请
     * @param query 查询条件
     * @return 返回查询信息列表
     */
    List<OnlineAppointmentVo> findByCondition(@Param("query") OnlineAppointmentQuery query);

    /**
     * 获取消息通知
     * @param orgId
     * @return
     */
    int countNewMessageNotice(@Param("orgId") Integer orgId);

    /**
     * 获取同一时间预约数量
     * @param orgId
     * @param date
     * @return
     */
    List<CountOnlineAppointVo> countOnlineAppointSameTime(@Param("orgId") Integer orgId, @Param("date") String date);
}