package com.yunya.modules.appointment.biz;

import com.yunya.feign.appointment.domain.form.AppointOperationForm;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.model.AppointOperationModel;
import com.yunya.feign.appointment.domain.query.AppointOperationQuery;
import com.yunya.feign.appointment.vo.AppointOperationRecordVo;
import com.yunya.feign.appointment.vo.DeviceItemVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.ClinicDepartmentRoomVO;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.AppointmentOperateRecord;
import com.yunya.modules.appointment.mapper.AppointmentOperateRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;

    @Autowired
    private ClinicDeviceItemBiz clinicDeviceItemBiz;

    /**
     * 插入预约记录
     * @param model  记录表单
     * @return 返回插入的记录条数
     */
    public Integer insertAppointmentOperateRecord(AppointOperationModel model){

        AppointmentOperateRecord appointOperateRecord = EntityUtils.build(model,AppointmentOperateRecord.class);
        appointOperateRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        appointOperateRecord.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        appointOperateRecord.setCrtName(BaseContextHandler.getName());
        appointOperateRecord.setCrtTime(new Date(System.currentTimeMillis()));
        return mapper.insertSelective(appointOperateRecord);
    }

    /**
     * 保存修改内容记录
     * 修改内容（包括：预约日期、预约时间、预约医生、预约助手、预约时长、科室、预约确认、设备、预约内容、预约备注）
     * @param appointmentBaseForm  修改之后表单
     * @param appointment 修改内容之前的数据
     */
    public void saveAppointOperationRecord(Appointment appointment,AppointmentBaseForm appointmentBaseForm){
        AppointmentOperateRecord record = new AppointmentOperateRecord();
        record.setAppointmentId(appointmentBaseForm.getId());
        record.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        record.setOperateType((byte) 1);
        record.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        record.setCrtName(BaseContextHandler.getName());
        record.setCrtTime(new Date(System.currentTimeMillis()));
        // 保存 预约日期 修改记录
        if (!appointment.getAppointDate().equals(appointmentBaseForm.getAppointDate())){
            record.setBeforeOperation(appointment.getAppointDate().toString());
            record.setAfterOperation(appointmentBaseForm.getAppointDate().toString());
            record.setRemarks("预约日期");
            mapper.insertSelective(record);
        }
        // 保存 预约时间 修改记录
        if (!appointment.getAppointTime().equals(appointmentBaseForm.getAppointTime())){
            record.setBeforeOperation(appointment.getAppointTime());
            record.setAfterOperation(appointmentBaseForm.getAppointTime());
            record.setRemarks("预约时间");
            mapper.insertSelective(record);
        }
        // 保存 预约医生 修改记录
        if (!appointment.getDentistId().equals(appointmentBaseForm.getDentistId())){
            SysUserInfoDetail beforeModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(appointment.getDentistId());
            SysUserInfoDetail afterModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(appointmentBaseForm.getDentistId());
            if (beforeModifyDentistInfo != null && afterModifyDentistInfo != null){
                record.setBeforeOperation(beforeModifyDentistInfo.getName());
                record.setAfterOperation(afterModifyDentistInfo.getName());
                record.setRemarks("预约医生");
                mapper.insertSelective(record);
            }
        }
        // 保存 预约助手 修改记录
        if (!appointment.getAssistantId().equals(appointmentBaseForm.getAssistantId())){
            SysUserInfoDetail beforeModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(appointment.getAssistantId());
            SysUserInfoDetail afterModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(appointmentBaseForm.getAssistantId());
            if (beforeModifyDentistInfo != null && afterModifyDentistInfo != null){
                record.setBeforeOperation(beforeModifyDentistInfo.getName());
                record.setAfterOperation(afterModifyDentistInfo.getName());
                record.setRemarks("预约助手");
                mapper.insertSelective(record);
            }
        }
        // 保存 预约时长 修改记录
        if (!appointment.getAppointDuration().equals(appointmentBaseForm.getAppointDuration())){
            record.setBeforeOperation(String.valueOf(appointment.getAppointDuration()));
            record.setAfterOperation(String.valueOf(appointmentBaseForm.getAppointDuration()));
            record.setRemarks("预约时长");
            mapper.insertSelective(record);
        }
        // 保存 预约科室 修改记录
        if (!appointment.getDeptRoomId().equals(appointmentBaseForm.getDeptRoomId())){
            ClinicDepartmentRoomVO beforeModifyDepartmentRoomInfo = systemServiceFeign.findClinicDepartmentRoomById(appointment.getDeptRoomId());
            ClinicDepartmentRoomVO afterModifyDepartmentRoomtInfo = systemServiceFeign.findClinicDepartmentRoomById(appointmentBaseForm.getDeptRoomId());
            if (beforeModifyDepartmentRoomInfo != null && afterModifyDepartmentRoomtInfo != null){
                record.setBeforeOperation(beforeModifyDepartmentRoomInfo.getDeptRoomName());
                record.setAfterOperation(afterModifyDepartmentRoomtInfo.getDeptRoomName());
                record.setRemarks("预约科室");
                mapper.insertSelective(record);
            }
        }
        // 保存 预约确认 修改记录
        if (!appointment.getConfirmStatus().equals(appointmentBaseForm.getConfirmStatus())){
            record.setBeforeOperation(appointment.getConfirmStatus() ? "确认" : "未确认");
            record.setAfterOperation(appointmentBaseForm.getConfirmStatus() ? "确认" : "未确认");
            record.setRemarks("预约确认");
            mapper.insertSelective(record);
        }
        // 保存 预约设备 修改记录
        if (!appointment.getClinicDeviceItemId().equals(appointmentBaseForm.getClinicDeviceItemId())){
            DeviceItemVo beforeModifyDeviceItemInfo = clinicDeviceItemBiz.selectDeviceItemById(appointment.getClinicDeviceItemId());
            DeviceItemVo afterModifyDeviceItemtInfo = clinicDeviceItemBiz.selectDeviceItemById(appointmentBaseForm.getClinicDeviceItemId());
            if (beforeModifyDeviceItemInfo != null && afterModifyDeviceItemtInfo != null){
                record.setBeforeOperation(beforeModifyDeviceItemInfo.getNumber());
                record.setAfterOperation(afterModifyDeviceItemtInfo.getNumber());
                record.setRemarks("预约设备");
            }
            mapper.insertSelective(record);
        }
        // 保存 预约内容 修改记录
        if (!appointment.getAppointContent().equals(appointmentBaseForm.getAppointContent())){
            record.setBeforeOperation(appointment.getAppointContent());
            record.setAfterOperation(appointmentBaseForm.getAppointContent());
            record.setRemarks("预约内容");
            mapper.insertSelective(record);
        }
        // 保存 预约备注 修改记录
        if (!appointment.getRemarks().equals(appointmentBaseForm.getRemarks())){
            record.setBeforeOperation(appointment.getRemarks());
            record.setAfterOperation(appointmentBaseForm.getRemarks());
            record.setRemarks("预约备注");
            mapper.insertSelective(record);
        }
    }

    /**
     * 根据条件查询所有操作记录
     * @param query  条件参数
     * @return
     */
    public List<AppointOperationRecordVo> findAppointOperationRecordByExample(AppointOperationQuery query){
        return mapper.findAppointOperationRecordByExample(query);
    }

    /**
     * 修改预约操作记录
     * @param form
     * @return
     */
    public Integer updateAppointOperationRecord(AppointOperationForm form){
        AppointmentOperateRecord build = EntityUtils.build(form, AppointmentOperateRecord.class);
        build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setUpdName(BaseContextHandler.getName());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        return mapper.updateByPrimaryKeySelective(build);
    }

    /**
     * 删除预约操作记录
     * @param id
     * @return
     */
    public Integer deleteAppointOperatioinById(Integer id){
        return mapper.deleteByPrimaryKey(id);
    }



}
