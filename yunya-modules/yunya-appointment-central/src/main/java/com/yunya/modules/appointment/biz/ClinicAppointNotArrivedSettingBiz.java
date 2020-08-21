package com.yunya.modules.appointment.biz;

import com.yunya.feign.appointment.domain.form.AppointNotArrivedSettingForm;
import com.yunya.feign.appointment.domain.model.AppointNotArrivedSettingModel;
import com.yunya.feign.appointment.vo.AppointNotArrivedSettingVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.ClinicAppointNotArrivedSetting;
import com.yunya.modules.appointment.mapper.ClinicAppointNotArrivedSettingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.Date;

/**
 * 预约未到表头设置业务
 *
 * @author yunya-lihuibin
 * @create 2020-08-11 11:00
 * @update yunya-lihuibin    2020-08-11    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicAppointNotArrivedSettingBiz extends BaseBiz<ClinicAppointNotArrivedSettingMapper, ClinicAppointNotArrivedSetting> {


    /**
     * 新增设置
     * @param model
     * @return
     */
    public Integer addAppointNotArrivedSetting(AppointNotArrivedSettingModel model){
        AppointNotArrivedSettingVo notArrivedSettingVo = mapper.findAppointNotArrivedSettingByUserId(model.getUserId());
        if (notArrivedSettingVo != null){
            throw new ClientServiceException("设置已经存在！", OperationCodeConstants.SAME_DATA_EXIST);
        }
        ClinicAppointNotArrivedSetting build = EntityUtils.build(model, ClinicAppointNotArrivedSetting.class);
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setCrtName(BaseContextHandler.getName());
        build.setCrtTime(new Date(System.currentTimeMillis()));
        return mapper.insertSelective(build);
    }

    /**
     * 删除用户设置
     * @param id
     * @return
     */
    public Integer deleteAppointNotArrivedSetting(Integer id){
        ClinicAppointNotArrivedSetting clinicAppointNotArrivedSetting = mapper.selectByPrimaryKey(id);
        if (clinicAppointNotArrivedSetting == null){
            throw new ClientServiceException("用户设置不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }
        return mapper.deleteByPrimaryKey(id);
    }

    /**
     * 跟新用户设置
     * @param form
     * @return
     */
    public Integer updateAppointNotArrivedSetting(AppointNotArrivedSettingForm form){
        ClinicAppointNotArrivedSetting clinicAppointNotArrivedSetting = mapper.selectByPrimaryKey(form.getId());
        if (clinicAppointNotArrivedSetting == null){
            throw new ClientServiceException("用户设置不存在！",OperationCodeConstants.DATA_NOT_EXIST);
        }
        ClinicAppointNotArrivedSetting build = EntityUtils.build(form, ClinicAppointNotArrivedSetting.class);
        build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setUpdName(BaseContextHandler.getName());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        return mapper.updateByPrimaryKeySelective(build);
    }

    /**
     * 根据id查询用户设置
     * @param id
     * @return
     */
    public AppointNotArrivedSettingVo findAppointNotArrivedSettingById(Integer id){
        ClinicAppointNotArrivedSetting clinicAppointNotArrivedSetting = mapper.selectByPrimaryKey(id);
        AppointNotArrivedSettingVo build = EntityUtils.build(clinicAppointNotArrivedSetting, AppointNotArrivedSettingVo.class);
        return build;
    }

    /**
     * 根据用户id查询用户设置
     * @param userId  用户id
     * @return
     */
    public AppointNotArrivedSettingVo findAppointNotArrivedSettingByUserId(Integer userId){
        return mapper.findAppointNotArrivedSettingByUserId(userId);
    }

}
