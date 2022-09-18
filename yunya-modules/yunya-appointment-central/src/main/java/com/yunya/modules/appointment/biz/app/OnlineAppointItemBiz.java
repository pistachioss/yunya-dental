package com.yunya.modules.appointment.biz.app;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.appointment.domain.form.OnlineAppointItemForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointItemModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointItemQuery;
import com.yunya.feign.appointment.domain.query.OnlineAppointmentQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.feign.appointment.vo.OnlineAppointmentVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.OnlineAppointItem;
import com.yunya.models.appointment.OnlineAppointItemSetting;
import com.yunya.models.appointment.OnlineAppointment;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.OnlineAppointItemMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @program: yunya-dental
 * @description: 预约项目设置业务
 * @author: LHB
 * @create: 2021-05-26 10:35
 **/
@Service
public class OnlineAppointItemBiz extends BaseBiz<OnlineAppointItemMapper, OnlineAppointItem> {

    @Autowired
    private OnlineAppointItemSettingBiz onlineAppointItemSettingBiz;
    @Autowired
    private OnlineAppointmentBiz onlineAppointmentBiz;

    /**
     * 查询线上预约项目
     * @param id id
     * @return 查询结果
     */
    public ResponseResult<OnlineAppointItemVo> findOnlineAppointItemById(Integer id) {
        OnlineAppointItemVo onlineAppointItemVo = mapper.findOnlineAppointItemById(id);
        return ResponseUtil.success(onlineAppointItemVo);
    }

    /**
     * 查询线上预约项目列表
     * @param query 查询条件
     * @return 查询结果
     */
    public List<OnlineAppointItemVo> findOnlineAppointItemByCondition(OnlineAppointItemQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        return mapper.findOnlineAppointItemByCondition(query);
    }

    /**
     * 新增线上预约项目
     * @param model 新增数据
     * @return 新增结果
     */
    public ResponseResult addItem(OnlineAppointItemModel model) {
        OnlineAppointItem example = new OnlineAppointItem();
        example.setName(model.getName());
        int count = mapper.selectCount(example);
        if (count > 0) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EXIST.getCode(),
                    AppointmentError.APPOINTMENT_ITEM_EXIST.getMessage(),null);
        }
        OnlineAppointItem build = EntityUtils.build(model, OnlineAppointItem.class);
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setCrtName(BaseContextHandler.getUsername());
        int status = mapper.insert(build);
        if (status <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getCode(),
                    AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 删除线上预约项目
     * @param itemId itemId
     * @return 删除结果
     */
    public ResponseResult deleteItemById(Integer itemId) {
        OnlineAppointItem onlineAppointItem = mapper.selectByPrimaryKey(itemId);
        if (onlineAppointItem == null) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EDIT_NOT_EXIST.getCode(),
                    AppointmentError.APPOINTMENT_ITEM_EDIT_NOT_EXIST.getMessage(),null);
        }
        if (!this.checkEnableDelete(itemId)) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_IS_GOING_ON.getCode(),
                    AppointmentError.APPOINTMENT_ITEM_IS_GOING_ON.getMessage(),null);
        }
        int status = mapper.deleteByPrimaryKey(itemId);
        if (status <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getCode(),
                    AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 修改线上预约项目
     * @param form 修改数据
     * @return 修改结果
     */
    public ResponseResult updateItem(OnlineAppointItemForm form) {
        OnlineAppointItem onlineAppointItem = mapper.selectByPrimaryKey(form.getItemId());
        if (onlineAppointItem == null) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EDIT_NOT_EXIST.getCode(),
                    AppointmentError.APPOINTMENT_ITEM_EDIT_NOT_EXIST.getMessage(),null);
        }
        onlineAppointItem.setName(form.getName());
        onlineAppointItem.setInservice(form.getInservice());
        onlineAppointItem.setDuration(form.getDuration());
        onlineAppointItem.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        onlineAppointItem.setUpdName(BaseContextHandler.getUsername());
        onlineAppointItem.setUpdTime(new Date(System.currentTimeMillis()));
        int status = mapper.updateByPrimaryKey(onlineAppointItem);
        if (status <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getCode(),
                    AppointmentError.APPOINTMENT_ITEM_EDIT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 判断是否可以删除
     * @param itemId 线上预约项目唯一标识 id
     * @return 可以删除-true；否则返回false
     */
    private boolean checkEnableDelete(Integer itemId) {
        Example example1 = new Example(OnlineAppointment.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("appointItemId",itemId);
        criteria1.andEqualTo("inservice",true);
        criteria1.andGreaterThan("appointDate",LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINESE)));
        int count1 = onlineAppointmentBiz.selectCountByExample(example1);

        if (count1 > 0) {
            return false;
        }
        Example example = new Example(OnlineAppointItemSetting.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("enableAppointItemIds",String.valueOf(itemId));
        criteria.andEqualTo("inservice",true);
        int count = onlineAppointItemSettingBiz.selectCountByExample(example);
        if (count > 0) {
            return false;
        }
        return true;
    }

}
