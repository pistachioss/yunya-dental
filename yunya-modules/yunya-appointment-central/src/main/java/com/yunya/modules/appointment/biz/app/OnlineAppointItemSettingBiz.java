package com.yunya.modules.appointment.biz.app;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingVo;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
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
     * 删除预约项目
     * @param id 预约项目ID
     * @return 返回结果
     */
    @Transactional
    public ResponseResult<T> deleteOnlineAppointItemSettingById(Integer id) {
        OnlineAppointItemSetting onlineAppointItemSetting = mapper.selectByPrimaryKey(id);
        if (onlineAppointItemSetting != null) {
            onlineAppointItemSetting.setInservice(false);
            // 设置日期，修改人，修改时间
            setCommonProperties(onlineAppointItemSetting,"");
            int status = mapper.updateByPrimaryKeySelective(onlineAppointItemSetting);
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
    public ResponseResult<List<Integer>> findItemSettingByDentistId(Integer dentistId,Integer orgId) {
        OnlineAppointItemSettingVo itemSettingInfo = mapper.findItemSettingByDentistId(dentistId,orgId);
        List<OnlineAppointItemVo> lists = itemSettingInfo.getLists();
        List<Integer> result = null;
        if (StringHelper.isNotEmpty(lists)) {
            result = lists.stream().mapToInt(
                    OnlineAppointItemVo::getItemId).boxed().collect(Collectors.toList());
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        return ResponseUtil.success(result);
    }

    /**
     * 新增、更新线上预约设置
     * @param form 参数
     * @return 返回结果信息
     */
    public ResponseResult<T> addOrUpdateOnlineAppointItem(OnlineAppointItemSettingForm form) {
        Integer orgId = form.getOrgId();
        Integer dentistId = form.getDentistId();
        Integer itemSettingId = form.getItemSettingId();

        if (itemSettingId == null) {
            // 新增
            OnlineAppointItemSetting model = new OnlineAppointItemSetting();
            model.setOrgId(orgId);
            model.setDentistId(dentistId);
            String sb = listToStr(form.getLists());
            model.setEnableAppointItemIds(sb.toString());
            model.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            model.setCrtName(BaseContextHandler.getUsername());
            mapper.insertSelective(model);
        } else {
            // 更新
            OnlineAppointItemSetting onlineAppointItemSetting = mapper.selectByPrimaryKey(itemSettingId);
            onlineAppointItemSetting.setEnableAppointItemIds(listToStr(form.getLists()));
            onlineAppointItemSetting.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            onlineAppointItemSetting.setUpdName(BaseContextHandler.getUsername());
            onlineAppointItemSetting.setUpdTime(new Date(System.currentTimeMillis()));
            mapper.updateByPrimaryKey(onlineAppointItemSetting);
        }
        return ResponseUtil.success();
    }

    private String listToStr(List<Integer> lists) {
        StringBuilder sb = new StringBuilder();
        if (StringHelper.isNotEmpty(lists)) {
            lists.forEach(e->{
                sb.append(e);
                sb.append(",");
            });
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
