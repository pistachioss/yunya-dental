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
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.AppointmentOperateRecord;
import com.yunya.modules.appointment.mapper.AppointmentOperateRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        List<AppointmentOperateRecord> operateRecords = new ArrayList<>();
        AppointmentOperateRecord record = new AppointmentOperateRecord();
        record.setAppointmentId(appointmentBaseForm.getId());
        record.setInservice(true);
        record.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        record.setOperateType((byte) 1);
        record.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        record.setCrtName(BaseContextHandler.getName());
        record.setCrtTime(new Date(System.currentTimeMillis()));
        // 保存 预约日期 修改记录
        Date appointDateBefore = appointment.getAppointDate();
        Date appointDateAfter = appointmentBaseForm.getAppointDate();
        if (null != appointDateBefore && null != appointDateAfter && !appointDateBefore.equals(appointDateAfter)){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            record.setBeforeOperation(dateFormat.format(appointment.getAppointDate()));
            record.setAfterOperation(dateFormat.format(appointmentBaseForm.getAppointDate()));
            record.setOperateItem("预约日期");
            operateRecords.add(record);
        }
        // 保存 预约时间 修改记录
        String appointTimeBefore = appointment.getAppointTime();
        String appointTimeAfter = appointmentBaseForm.getAppointTime();
        if (null != appointTimeBefore && null != appointTimeAfter && !appointTimeBefore.equals(appointTimeAfter)){
            record.setBeforeOperation(appointment.getAppointTime());
            record.setAfterOperation(appointmentBaseForm.getAppointTime());
            record.setOperateItem("预约时间");
            operateRecords.add(record);
        }
        // 保存 预约医生 修改记录
        Integer dentistIdBefore = appointment.getDentistId();
        Integer dentistIdAfter = appointmentBaseForm.getDentistId();
        if (null != dentistIdBefore && null != dentistIdAfter && !dentistIdBefore.equals(dentistIdAfter)){
            SysUserInfoDetail beforeModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(appointment.getDentistId());
            SysUserInfoDetail afterModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(appointmentBaseForm.getDentistId());
            if (beforeModifyDentistInfo != null && afterModifyDentistInfo != null){
                record.setBeforeOperation(beforeModifyDentistInfo.getName());
                record.setAfterOperation(afterModifyDentistInfo.getName());
                record.setOperateItem("预约医生");
                operateRecords.add(record);
            }
        }
        // 保存 预约助手 修改记录
        Integer assistantIdBefore = appointment.getAssistantId();
        Integer assistantIdAfter = appointmentBaseForm.getAssistantId();
        if (null != assistantIdBefore && null == assistantIdAfter && !assistantIdBefore.equals(assistantIdAfter)) {
            SysUserInfoDetail beforeModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(assistantIdBefore);
            if (beforeModifyDentistInfo != null) {
                record.setBeforeOperation(beforeModifyDentistInfo.getName());
            }
            record.setAfterOperation("");
            record.setOperateItem("预约助手");
            operateRecords.add(record);
        } else if (null == assistantIdBefore && null != assistantIdAfter && !assistantIdAfter.equals(assistantIdBefore)) {
            SysUserInfoDetail afterModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(assistantIdAfter);
            record.setBeforeOperation("");
            if (null != afterModifyDentistInfo) {
                record.setAfterOperation(afterModifyDentistInfo.getName());
            }
            record.setOperateItem("预约助手");
            operateRecords.add(record);
        } else if (null != assistantIdBefore && null != assistantIdAfter && !assistantIdBefore.equals(assistantIdAfter)) {
            SysUserInfoDetail beforeModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(assistantIdBefore);
            SysUserInfoDetail afterModifyDentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(assistantIdAfter);
            if (null != beforeModifyDentistInfo) {
                record.setBeforeOperation(beforeModifyDentistInfo.getName());
            }
            if (null != afterModifyDentistInfo) {
                record.setAfterOperation(afterModifyDentistInfo.getName());
            }
            record.setOperateItem("预约助手");
            operateRecords.add(record);
        }
        // 保存 预约时长 修改记录
        Integer appointDurationBefore = appointment.getAppointDuration();
        Integer appointDurationAfter = appointmentBaseForm.getAppointDuration();
        if (null != appointDurationBefore && null != appointDurationAfter && !appointDurationBefore.equals(appointDurationAfter)){
            record.setBeforeOperation(appointment.getAppointDuration() + "分钟");
            record.setAfterOperation(appointmentBaseForm.getAppointDuration() + "分钟");
            record.setOperateItem("预约时长");
            operateRecords.add(record);
        }
        // 保存 预约科室 修改记录
        Integer deptRoomIdBefore = appointment.getDeptRoomId();
        Integer deptRoomIdAfter = appointmentBaseForm.getDeptRoomId();
        ClinicDepartmentRoomVO beforeModifyDepartmentRoomInfo = null;
        ClinicDepartmentRoomVO afterModifyDepartmentRoomtInfo = null;
        if (null == deptRoomIdBefore && null != deptRoomIdAfter && !deptRoomIdAfter.equals(deptRoomIdBefore)) {
            afterModifyDepartmentRoomtInfo = systemServiceFeign.findClinicDepartmentRoomById(deptRoomIdAfter);
            record.setBeforeOperation("");
            if (null != afterModifyDepartmentRoomtInfo) {
                record.setAfterOperation(afterModifyDepartmentRoomtInfo.getDeptRoomName());
            }
            record.setOperateItem("预约科室");
            operateRecords.add(record);
        } else if (null != deptRoomIdBefore && null == deptRoomIdAfter && !deptRoomIdBefore.equals(deptRoomIdAfter)) {
            beforeModifyDepartmentRoomInfo = systemServiceFeign.findClinicDepartmentRoomById(deptRoomIdBefore);
            if (null != beforeModifyDepartmentRoomInfo) {
                record.setBeforeOperation(beforeModifyDepartmentRoomInfo.getDeptRoomName());
            }
            record.setAfterOperation("");
            record.setOperateItem("预约科室");
            operateRecords.add(record);
        } else if (null != deptRoomIdBefore && null != deptRoomIdAfter && !deptRoomIdBefore.equals(deptRoomIdAfter)) {
            afterModifyDepartmentRoomtInfo = systemServiceFeign.findClinicDepartmentRoomById(deptRoomIdAfter);
            beforeModifyDepartmentRoomInfo = systemServiceFeign.findClinicDepartmentRoomById(deptRoomIdBefore);
            if (null != beforeModifyDepartmentRoomInfo) {
                record.setBeforeOperation(beforeModifyDepartmentRoomInfo.getDeptRoomName());
            }
            if (null != afterModifyDepartmentRoomtInfo) {
                record.setAfterOperation(afterModifyDepartmentRoomtInfo.getDeptRoomName());
            }
            record.setOperateItem("预约科室");
            operateRecords.add(record);
        }
        // 保存 预约确认 修改记录
        Boolean confirmStatusBefore = appointment.getConfirmStatus();
        Boolean confirmStatusAfter = appointmentBaseForm.getConfirmStatus();
        if (null != confirmStatusBefore && null != confirmStatusAfter && !confirmStatusBefore.equals(confirmStatusAfter)){
            record.setBeforeOperation(appointment.getConfirmStatus() ? "确认" : "未确认");
            record.setAfterOperation(appointmentBaseForm.getConfirmStatus() ? "确认" : "未确认");
            record.setOperateItem("预约确认");
            operateRecords.add(record);
        }
        // 保存 预约设备 修改记录
        Integer clinicDeviceItemIdBefore = appointment.getClinicDeviceItemId();
        Integer clinicDeviceItemIdAfter = appointmentBaseForm.getClinicDeviceItemId();
        if (null == clinicDeviceItemIdBefore && null != clinicDeviceItemIdAfter && !clinicDeviceItemIdAfter.equals(clinicDeviceItemIdBefore)) {
            DeviceItemVo afterModifyDeviceItemtInfo = clinicDeviceItemBiz.selectDeviceItemById(appointmentBaseForm.getClinicDeviceItemId());
            record.setBeforeOperation("");
            if (null != afterModifyDeviceItemtInfo) {
                record.setAfterOperation(afterModifyDeviceItemtInfo.getNumber());
            }
            record.setOperateItem("预约设备");
            operateRecords.add(record);
        } else if (null != clinicDeviceItemIdBefore && null == clinicDeviceItemIdAfter && !clinicDeviceItemIdBefore.equals(clinicDeviceItemIdAfter)) {
            DeviceItemVo beforeModifyDeviceItemInfo = clinicDeviceItemBiz.selectDeviceItemById(appointment.getClinicDeviceItemId());
            if (null != beforeModifyDeviceItemInfo) {
                record.setBeforeOperation(beforeModifyDeviceItemInfo.getNumber());
            }
            record.setAfterOperation("");
            record.setOperateItem("预约设备");
            operateRecords.add(record);
        } else if (null != clinicDeviceItemIdBefore && null != clinicDeviceItemIdAfter && !clinicDeviceItemIdAfter.equals(clinicDeviceItemIdBefore)) {
            DeviceItemVo afterModifyDeviceItemtInfo = clinicDeviceItemBiz.selectDeviceItemById(appointmentBaseForm.getClinicDeviceItemId());
            DeviceItemVo beforeModifyDeviceItemInfo = clinicDeviceItemBiz.selectDeviceItemById(appointment.getClinicDeviceItemId());
            if (null != beforeModifyDepartmentRoomInfo) {
                record.setBeforeOperation(beforeModifyDeviceItemInfo.getNumber());
            }
            if (null != afterModifyDepartmentRoomtInfo) {
                record.setAfterOperation(afterModifyDeviceItemtInfo.getNumber());
            }
            record.setOperateItem("预约设备");
            operateRecords.add(record);
        }

        // 保存 预约内容 修改记录
        String appointContentBefore = appointment.getAppointContent();
        String appointContentAfter = appointmentBaseForm.getAppointContent();
        boolean appointContentEquels = true;
        if (null == appointContentBefore && null != appointContentAfter) {
            appointContentEquels = appointContentAfter.equals(appointContentBefore);
        } else if (null != appointContentBefore && null == appointContentAfter) {
            appointContentEquels = appointContentBefore.equals(appointContentAfter);
        } else if (null != appointContentBefore && null != appointContentAfter) {
            appointContentEquels = appointContentAfter.equals(appointContentBefore);
        }
        if (!appointContentEquels){
            record.setBeforeOperation(appointment.getAppointContent());
            record.setAfterOperation(appointmentBaseForm.getAppointContent());
            record.setOperateItem("预约内容");
            operateRecords.add(record);
        }
        // 保存 预约备注 修改记录
        String remarksBefore = appointment.getRemarks();
        String remarksAfter = appointmentBaseForm.getRemarks();
        boolean remarksEquels = true;
        if (null == remarksBefore && null != remarksAfter) {
            remarksEquels = remarksAfter.equals(remarksBefore);
        } else if (null != remarksBefore && null == remarksAfter) {
            remarksEquels = remarksBefore.equals(remarksAfter);
        } else if (null != remarksBefore && null != remarksAfter) {
            remarksEquels = remarksAfter.equals(remarksBefore);
        }
        if (!remarksEquels){
            record.setBeforeOperation(appointment.getRemarks());
            record.setAfterOperation(appointmentBaseForm.getRemarks());
            record.setOperateItem("预约备注");
            operateRecords.add(record);
        }
        if (StringHelper.isNotEmpty(operateRecords)) {
            mapper.inertBatch(operateRecords);
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
