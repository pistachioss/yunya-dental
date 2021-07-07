package com.yunya.modules.appointment.biz.web;

import com.yunya.feign.appointment.domain.form.AppointSettingForm;
import com.yunya.feign.appointment.domain.model.AppointSettingModel;
import com.yunya.feign.appointment.domain.query.AppointSettingQuery;
import com.yunya.feign.appointment.vo.AppointSettingVo;
import com.yunya.feign.appointment.vo.ClinicBusinessHoursVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.MedicalOrganizationInfoVO;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.MapUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.ClinicAppointmentSetting;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.ClinicAppointmentSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约显示设置
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 17:55
 * @update yunya-lihuibin    2020-08-03    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicAppointSettingBiz extends BaseBiz<ClinicAppointmentSettingMapper, ClinicAppointmentSetting> {
    /** 系统服务 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 编辑(添加)设置
     * @param form  设置数据表单
     * @return
     */
    public ResponseResult editOrAddSetting(AppointSettingForm form){

        if (form.getAppointUnit() > 30 || form.getAppointUnit() < 5) {
            return ResponseUtil.fail(AppointmentError.APPOINT_SETTING_UNIT.getCode(),AppointmentError.APPOINT_SETTING_UNIT.getMessage(),null);
        }
        ClinicAppointmentSetting build = EntityUtils.build(form, ClinicAppointmentSetting.class);
        Integer userId = form.getUserId();
        AppointSettingVo appointSettingVo = mapper.selectAppointSettingByExample(userId);
        // 如果已经存在用户设置，则进行修改设置操作，否则进行新增操作
        if (appointSettingVo != null) {
            build.setId(appointSettingVo.getId());
            build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
            build.setUpdName(BaseContextHandler.getName());
            build.setUpdTime(new Date(System.currentTimeMillis()));
            int result = mapper.updateByPrimaryKeySelective(build);
            if (result <= 0 ){
                return ResponseUtil.fail(AppointmentError.APPOINT_SETTING_FAIL.getCode(),AppointmentError.APPOINT_SETTING_FAIL.getMessage(),null);
            }
        } else {
            build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            int result = mapper.insertSelective(build);
            if (result <= 0 ){
                return ResponseUtil.fail(AppointmentError.APPOINT_SETTING_FAIL.getCode(),AppointmentError.APPOINT_SETTING_FAIL.getMessage(),null);
            }
        }
        return ResponseUtil.success(build);
    }

    /**
     * 根据条件查询预约设置
     * @param userId
     * @return
     */
    public AppointSettingVo findAppointSettingByUserId(Integer userId){
        AppointSettingVo appointSettingVo = mapper.selectAppointSettingByExample(userId);
        return appointSettingVo;
    }

    /**
     * 根据id查询预约设置
     * @param id 编辑设置id
     * @return
     */
    public AppointSettingVo selectAppointSettingById(Integer id){
        ClinicAppointmentSetting clinicAppointmentSetting = mapper.selectByPrimaryKey(id);
        AppointSettingVo build = EntityUtils.build(clinicAppointmentSetting, AppointSettingVo.class);
        return build;
    }

    /**
     * 新增预约设置
     * @param model  数据
     * @return
     */
    public ResponseResult addAppointSetting(AppointSettingModel model){
        if (model.getAppointUnit() > 30 || model.getAppointUnit() < 5){
            throw new ClientServiceException("预约单位设置错误！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        // 检测是否已经存在给定的数据
        ClinicAppointmentSetting build = EntityUtils.build(model, ClinicAppointmentSetting.class);
        List<ClinicAppointmentSetting> clinicAppointmentSettings = mapper.select(build);
        if (clinicAppointmentSettings != null && !clinicAppointmentSettings.isEmpty()){
            throw new ClientServiceException("预约设置已经存在！",OperationCodeConstants.DATA_EXIST);
        }
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setCrtName(BaseContextHandler.getName());
        build.setCrtTime(new Date(System.currentTimeMillis()));
        int result = mapper.insertSelective(build);
        if (result <= 0){
            throw new ClientServiceException("新增预约设置失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据设置id删除数据
     * @param id
     * @return
     */
    public ResponseResult delAppointSetting(Integer id){
        ClinicAppointmentSetting clinicAppointmentSetting = mapper.selectByPrimaryKey(id);
        if (clinicAppointmentSetting == null){
            throw new ClientServiceException("数据不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }
        int result = mapper.deleteByPrimaryKey(id);
        if (result <= 0){
            throw new ClientServiceException("删除失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
        return ResponseUtil.success();
    }

    /**
     * 门诊营业时间
     * @param orgId 门诊ID
     * @return 返回门诊营业时间
     */
    public ResponseResult<ClinicBusinessHoursVo> orgBusinessHours(Integer orgId) {
        MedicalOrganizationInfoVO orgExtInfo = remoteSystemServiceFeign.clinicExtInfoByCompanyId(orgId);
        ClinicBusinessHoursVo clinicBusinessHours = new ClinicBusinessHoursVo();
        if (orgExtInfo == null) {
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"数据不存在",null);
        }
        String businessStartTime = orgExtInfo.getBusinessStartTime();
        String businessEndTime = orgExtInfo.getBusinessEndTime();
        if (StringHelper.isNotEmpty(businessStartTime) && StringHelper.isNotEmpty(businessEndTime)) {
            clinicBusinessHours.setBusinessStartTime(businessStartTime);
            clinicBusinessHours.setBusinessEndTime(businessEndTime);
        }
        return ResponseUtil.success(clinicBusinessHours);
    }

}
