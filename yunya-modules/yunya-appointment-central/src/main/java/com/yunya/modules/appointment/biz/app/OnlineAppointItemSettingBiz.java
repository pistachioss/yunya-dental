package com.yunya.modules.appointment.biz.app;

import com.alibaba.csp.sentinel.init.InitExecutor;
import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.vo.EnableOnlineAppointDentistsVo;
import com.yunya.feign.appointment.vo.EnableOnlineAppointItemVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.OnlineAppointItemSetting;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.OnlineAppointItemSettingMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description: 项目设置逻辑
 * @author: LHB
 * @create: 2021-05-18 16:58
 **/
@Service
public class OnlineAppointItemSettingBiz extends BaseBiz<OnlineAppointItemSettingMapper, OnlineAppointItemSetting> {

    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    @Autowired
    private OnlineAppointItemBiz onlineAppointItemBiz;


    /**
     * 删除预约项目配置
     * @param dentistId 医生ID
     * @param orgId  门诊ID
     * @return
     */
    @Transactional
    public ResponseResult<T> deleteOnlineAppointItemSetting(Integer dentistId,Integer orgId) {
        OnlineAppointItemSetting query = new OnlineAppointItemSetting();
        query.setDentistId(dentistId);
        query.setOrgId(orgId);
        OnlineAppointItemSetting onlineAppointItemSetting = mapper.selectOne(query);
        if (onlineAppointItemSetting != null) {
            int status = mapper.delete(onlineAppointItemSetting);
            if (status > 0) {
                return ResponseUtil.success();
            }
        }
        return ResponseUtil.fail(AppointmentError.APPOINT_SETTING_FAIL.getCode(),AppointmentError.APPOINT_SETTING_FAIL.getMessage(),null);
    }




    private void setCommonProperties(OnlineAppointItemSetting onlineAppointItemSetting,String type) {
        String option = "add";
        String userID = BaseContextHandler.getUserID();
        String username = BaseContextHandler.getUsername();
        if (option.equals(type)) {
            onlineAppointItemSetting.setCrtId(Integer.valueOf(userID));
            onlineAppointItemSetting.setCrtName(username);
        } else {
            onlineAppointItemSetting.setUpdId(Integer.valueOf(userID));
            onlineAppointItemSetting.setUpdName(username);
            onlineAppointItemSetting.setUpdTime(new Date(System.currentTimeMillis()));
        }
    }

    /**
     * 根据医生ID查询线上可预约项目
     * @param dentistId 医生ID
     * @return 返回查询结果
     */
    public OnlineAppointItemSettingVo findItemSettingByDentistId(Integer dentistId, Integer orgId) {
        OnlineAppointItemSettingVo itemSettingInfo = mapper.findItemSettingByDentistId(dentistId,orgId);
        return itemSettingInfo;
    }

    /**
     * 新增、更新线上预约设置
     * @param form 参数
     * @return 返回结果信息
     */
    public ResponseResult<T> addOrUpdateOnlineAppointItem(OnlineAppointItemSettingForm form) {
        Integer itemSettingId = form.getItemSettingId();

        OnlineAppointItemSetting query = new OnlineAppointItemSetting();
        query.setOrgId(form.getOrgId());
        query.setDentistId(form.getDentistId());
        OnlineAppointItemSetting onlineAppointItemSetting = mapper.selectOne(query);

        if (itemSettingId == null && onlineAppointItemSetting != null) {
            onlineAppointItemSetting.setEnableAppointItemIds(listToStr(form.getLists()));
            onlineAppointItemSetting.setUpdTime(new Date(System.currentTimeMillis()));
            mapper.updateByPrimaryKey(onlineAppointItemSetting);
            return ResponseUtil.success();
        }

        if (itemSettingId == null) {
            // 新增
            OnlineAppointItemSetting model = new OnlineAppointItemSetting();
            model.setOrgId(form.getOrgId());
            model.setDentistId(form.getDentistId());
            String sb = listToStr(form.getLists());
            model.setEnableAppointItemIds(sb.toString());
            mapper.insertSelective(model);
        } else {
            // 更新
            OnlineAppointItemSetting entity = mapper.selectByPrimaryKey(itemSettingId);
            entity.setEnableAppointItemIds(listToStr(form.getLists()));
            entity.setUpdTime(new Date(System.currentTimeMillis()));
            mapper.updateByPrimaryKey(entity);
        }
        return ResponseUtil.success();
    }

    private String listToStr(List<Integer> lists) {
        StringBuilder sb = new StringBuilder("");
        if (StringHelper.isNotEmpty(lists)) {
            lists.forEach(e->{
                sb.append(e);
                sb.append(",");
            });
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 查询可预约医生列表
     * @param orgId 门诊ID
     * @param itemId 可预约项目ID
     * @return 返回可预约医生列表
     */
    public ResponseResult<List<EnableOnlineAppointDentistsVo>> findDentistsByAppointItem(Integer orgId, Integer itemId) {
        List<EnableOnlineAppointDentistsVo> dentistsVos = mapper.findDentistsByAppointItem(orgId,itemId);
        if (StringHelper.isNotEmpty(dentistsVos)) {
            List<Integer> dentistIds = dentistsVos.stream().mapToInt(
                    EnableOnlineAppointDentistsVo::getDentistId).boxed().collect(Collectors.toList());
            List<SysUserInfoDetail> dentistInfos = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(dentistIds);
            if (StringHelper.isNotEmpty(dentistInfos)) {
                dentistsVos.forEach(dentistObj->{
                    dentistInfos.stream().filter(
                            employee -> employee.getUserId().equals(dentistObj.getDentistId()))
                            .findAny()
                            .ifPresent(sysUserInfoDetail -> dentistObj.setDentistName(sysUserInfoDetail.getUsername()));

                });
            }

        }
        return ResponseUtil.success(dentistsVos);
    }
}
