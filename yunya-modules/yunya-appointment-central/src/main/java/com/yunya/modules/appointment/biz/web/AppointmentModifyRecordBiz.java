package com.yunya.modules.appointment.biz.web;

import com.yunya.feign.appointment.domain.form.AppointModifyRecordForm;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.model.AppointModifyRecordModel;
import com.yunya.feign.appointment.domain.query.AppointModifyRecordQuery;
import com.yunya.feign.appointment.vo.AppointModifyRecordVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.AppointmentModifyRecord;
import com.yunya.modules.appointment.mapper.AppointmentModifyRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 预约修改记录服务
 *
 * @author yunya-lihuibin
 * @create 2020-08-06 20:13
 * @update yunya-lihuibin    2020-08-06    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointmentModifyRecordBiz extends BaseBiz<AppointmentModifyRecordMapper, AppointmentModifyRecord> {

    /**
     * 新增预约修改记录
     * @param record  修改预约表单
     * @return 添加成功的条数；失败返回0
     */
    public Integer addAppointModifyRecord(AppointModifyRecordModel record){

        AppointmentModifyRecord build = EntityUtils.build(record, AppointmentModifyRecord.class);
        List<AppointmentModifyRecord> records = mapper.select(build);
        if (records != null && !records.isEmpty()){
            throw new ClientServiceException("已经存在相同的数据！", OperationCodeConstants.SAME_DATA_EXIST);
        }
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setCrtName(BaseContextHandler.getName());
        build.setCrtTime(new Date(System.currentTimeMillis()));
        return mapper.insertSelective(build);
    }

    /**
     * 保存预约更新被修改的日期、医生
     * @param appointmentForm 修改之后的内容
     * @param appointment  修改之前的内容
     */
    public void saveAppointModify(Appointment appointment, AppointmentBaseForm appointmentForm) {
        // 如果修改的内容未医生或者是预约日期，就将被修改的预约医生、预约时间保存
        if (appointment.getDentistId().equals(appointmentForm.getDentistId())
                && appointment.getAppointDate().equals(appointmentForm.getAppointDate())) {
            return;
        }
        AppointmentModifyRecord modify = new AppointmentModifyRecord();
        modify.setAppointmentId(appointmentForm.getId());
        modify.setOrgId(appointmentForm.getOrgId());
        modify.setDentistId(appointment.getDentistId());
        modify.setAppointDate(appointment.getAppointDate());
        modify.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        modify.setCrtName(BaseContextHandler.getName());
        modify.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(modify);
    }

    /**
     * 修改预约记录
     * @param form 修改记录表单
     * @return
     */
    public Integer updateAppointModify(AppointModifyRecordForm form){
        AppointmentModifyRecord build = EntityUtils.build(form, AppointmentModifyRecord.class);
        AppointmentModifyRecord appointmentModifyRecord = mapper.selectOne(build);
        if (appointmentModifyRecord != null){
            throw new ClientServiceException("记录已经存在！", OperationCodeConstants.SAME_DATA_EXIST);
        }
        int result = mapper.updateByPrimaryKeySelective(build);
        return result;
    }

    /**
     * 根据id修改预约记录
     * @param id 修改预约记录id
     * @return
     */
    public AppointModifyRecordVo findAppointModifyRecordById(Integer id){
        AppointmentModifyRecord appointmentModifyRecord = mapper.selectByPrimaryKey(id);
        return EntityUtils.build(appointmentModifyRecord,AppointModifyRecordVo.class);
    }

    /**
     * 根据条件查询预约修改记录
     * @param query
     * @return
     */
    public List<AppointModifyRecordVo> findAppointModifyRecordByExample(AppointModifyRecordQuery query){
        return mapper.findAppointModifyRecordByExample(query);
    }

    /**
     * 根据id删除预约修改记录
     * @param id
     * @return
     */
    public Integer deleteAppointModifyRecordById(Integer id){
        AppointModifyRecordVo recordVo = mapper.findAppointModifyRecordById(id);
        if (recordVo == null){
            throw new ClientServiceException("要删除的数据不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }
        return mapper.deleteByPrimaryKey(id);
    }

}
