package com.yunya.modules.appointment.biz.app;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointItemSettingModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointItemSettingQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemSettingModelVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.OnlineAppointItemSetting;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.OnlineAppointItemSettingMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 项目设置逻辑
 * @author: LHB
 * @create: 2021-05-18 16:58
 **/
@Service
public class OnlineAppointItemSettingBiz extends BaseBiz<OnlineAppointItemSettingMapper, OnlineAppointItemSetting> {

    /**
     * 根据id查询预约项目
     * @param id 预约项目ID
     * @return 返回结果
     */
    public ResponseResult<OnlineAppointItemSettingModelVo> findOnlineAppointItemById(Integer id) {
        OnlineAppointItemSetting onlineAppointItemSetting = mapper.selectByPrimaryKey(id);
        if (onlineAppointItemSetting == null) {
            return ResponseUtil.success();
        }
        OnlineAppointItemSettingModelVo build = EntityUtils.build(onlineAppointItemSetting, OnlineAppointItemSettingModelVo.class);
        return ResponseUtil.success(build);
    }


    /**
     *
     * 新增预约项目设置
     * @param model  预约项目参数
     * @return 返回设置结果
     */
    @Transactional
    public ResponseResult<T> addOnlineAppointItemSetting(OnlineAppointItemSettingModel model) {
        Example example = new Example(OnlineAppointItemSetting.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("appointItemName",model.getAppointItemName());
        List<OnlineAppointItemSetting> results = mapper.selectByExample(example);
        if (StringHelper.isEmpty(results)) {
            OnlineAppointItemSetting build = EntityUtils.build(model, OnlineAppointItemSetting.class);
            // 设置日期，创建人
            setCommonProperties(build,"add");
            int i = mapper.insertSelective(build);
            if (i > 0) {
                return ResponseUtil.success();
            }
        }
        return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EXIST.getCode(),AppointmentError.APPOINTMENT_ITEM_EXIST.getMessage(),null);
    }

    /**
     * 修改预约项目设置
     * @param form
     * @return
     */
    @Transactional
    public ResponseResult<T> updateOnlineAppointItemSetting(OnlineAppointItemSettingForm form) {
        Example example = new Example(OnlineAppointItemSetting.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",form.getId());
        criteria.andEqualTo("appoint_item_id",form.getAppointItemId());
        criteria.andEqualTo("appoint_item_name",form.getAppointItemName());
        List<OnlineAppointItemSetting> results = mapper.selectByExample(example);
        if (StringHelper.isNotEmpty(results)) {
            OnlineAppointItemSetting build = EntityUtils.build(form, OnlineAppointItemSetting.class);
            // 设置日期，修改人，修改时间
            setCommonProperties(build,"");
            int status = mapper.updateByPrimaryKeySelective(build);
            if (status > 0) {
                return ResponseUtil.success();
            } else {
                return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getCode(),
                        AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getMessage(),null);
            }
        }
        return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EDIT_NOT_EXIST.getCode(),
                AppointmentError.APPOINTMENT_ITEM_EDIT_NOT_EXIST.getMessage(),null);
    }

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
     * 根据条件批量查询预约项目
     * @param query 查询条件
     * @return 返回结果
     */
    public ResponseResult<PageInfo<OnlineAppointItemSettingModelVo>> findByCondition(OnlineAppointItemSettingQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<OnlineAppointItemSettingModelVo> result = mapper.selectByCondition(query);
        return ResponseUtil.success(new PageInfo<OnlineAppointItemSettingModelVo>(result));
    }
}
