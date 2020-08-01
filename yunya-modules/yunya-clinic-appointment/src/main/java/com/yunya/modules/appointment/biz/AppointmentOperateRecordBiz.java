package com.yunya.modules.appointment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.appointment.AppointmentOperateRecord;
import com.yunya.modules.appointment.mapper.AppointmentOperateRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 预约操作记录表
 *
 * @author yunya-lihuibin
 * @create 2020-07-29 10:00
 * @update yunya-lihuibin    2020-07-29    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointmentOperateRecordBiz extends BaseBiz<AppointmentOperateRecordMapper, AppointmentOperateRecord> {

    /**
     * 插入预约记录
     * @param appointmentId  预约id
     * @param orgId    门诊id
     * @param operateType    操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)
     * @return
     */
    public Integer insertAppointmentOperateRecord(Integer appointmentId, Integer orgId, Byte operateType){

        AppointmentOperateRecord appointOperateRecord = new AppointmentOperateRecord();
        appointOperateRecord.setOrgId(orgId);
        appointOperateRecord.setAppointmentId(appointmentId);
        appointOperateRecord.setOperateType(operateType);
        // TODO
//        appointOperateRecord.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointOperateRecord.setCrtId(10001);
        appointOperateRecord.setCrtName(BaseContextHandler.getName());
        appointOperateRecord.setCrtTime(new Date(System.currentTimeMillis()));
        appointOperateRecord.setInservice(true);

        int insert = mapper.insert(appointOperateRecord);
        return insert;
    }


}
