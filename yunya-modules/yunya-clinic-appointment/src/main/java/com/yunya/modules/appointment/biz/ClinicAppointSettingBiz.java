package com.yunya.modules.appointment.biz;

import com.yunya.feign.appointment.domain.form.AppointSettingForm;
import com.yunya.feign.appointment.domain.model.AppointSettingModel;
import com.yunya.feign.appointment.domain.query.AppointSettingQuery;
import com.yunya.feign.appointment.vo.AppointSettingVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.ClinicAppointmentSetting;
import com.yunya.modules.appointment.mapper.ClinicAppointmentSettingMapper;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

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

    /**
     * 编辑(添加)设置
     * @param form  设置数据表单
     * @return
     */
    public ResponseResult editOrAddSetting(AppointSettingForm form){
        ClinicAppointmentSetting build = EntityUtils.build(form, ClinicAppointmentSetting.class);

        AppointSettingQuery query = new AppointSettingQuery();
        query.setUserId(form.getUserId());
        AppointSettingVo appointSettingVo = mapper.selectAppointSettingByExample(query);
        // 如果已经存在用户设置，则进行修改设置操作，否则进行新增操作
        if (appointSettingVo != null){
            build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
            build.setUpdName(BaseContextHandler.getName());
            build.setUpdTime(new Date(System.currentTimeMillis()));
            int result = mapper.updateByPrimaryKeySelective(build);
            if (result <= 0 ){
                return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"编辑失败！",null);
            }
        } else {
            build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            int result = mapper.insertSelective(build);
            if (result <= 0 ){
                return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"编辑失败！",null);
            }
        }
        return ResponseUtil.success();
    }

    /**
     * 根据条件查询预约设置
     * @param query
     * @return
     */
    public AppointSettingVo selectAppointSettingByUserId(AppointSettingQuery query){
        AppointSettingVo appointSettingVo = mapper.selectAppointSettingByExample(query);
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
     * 添加预约显示设置
     * @param model  数据
     * @return
     */
    public ResponseResult addAppointSetting(AppointSettingModel model){
        // 检测是否已经存在给定的数据
        ClinicAppointmentSetting build = EntityUtils.build(model, ClinicAppointmentSetting.class);
        List<ClinicAppointmentSetting> clinicAppointmentSettings = mapper.selectByExample(build);
        if (clinicAppointmentSettings != null && !clinicAppointmentSettings.isEmpty()){
            return ResponseUtil.fail(OperationCodeConstants.SAME_DATA_EXIST, "添加的数据已经存在！",null);
        }
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        int result = mapper.insertSelective(build);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"添加设置失败！",null);
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
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"数据不存在！",null);
        }
        int result = mapper.deleteByPrimaryKey(id);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"删除失败！", null);
        }
        return ResponseUtil.success();
    }

}
