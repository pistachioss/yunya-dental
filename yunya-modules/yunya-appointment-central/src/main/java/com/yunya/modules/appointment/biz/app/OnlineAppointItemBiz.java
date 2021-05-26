package com.yunya.modules.appointment.biz.app;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointmentModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointItemQuery;
import com.yunya.feign.appointment.vo.OnlineAppointItemVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.appointment.OnlineAppointItem;
import com.yunya.modules.appointment.mapper.OnlineAppointItemMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

/**
 * @program: yunya-dental
 * @description: 预约项目设置业务
 * @author: LHB
 * @create: 2021-05-26 10:35
 **/
@Service
public class OnlineAppointItemBiz extends BaseBiz<OnlineAppointItemMapper, OnlineAppointItem> {
    /**
     * 查询线上预约项目
     * @param id id
     * @return 查询结果
     */
    public ResponseResult<T> findOnlineAppointItemById(Integer id) {
        return null;
    }

    /**
     * 查询线上预约项目列表
     * @param query 查询条件
     * @return 查询结果
     */
    public ResponseResult<OnlineAppointItemVo> findOnlineAppointItemByCondition(OnlineAppointItemQuery query) {
        return null;
    }

    /**
     * 新增线上预约项目
     * @param model 新增数据
     * @return 新增结果
     */
    public ResponseResult<T> addItem(OnlineAppointmentModel model) {
        return null;
    }

    /**
     * 删除线上预约项目
     * @param id id
     * @return 删除结果
     */
    public ResponseResult<T> deleteItemById(Integer id) {
        return null;
    }

    /**
     * 修改线上预约项目
     * @param form 修改数据
     * @return 修改结果
     */
    public ResponseResult<T> updateItem(OnlineAppointItemForm form) {
        return null;
    }
}
