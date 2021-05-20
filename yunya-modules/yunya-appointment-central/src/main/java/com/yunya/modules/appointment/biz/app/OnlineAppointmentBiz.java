package com.yunya.modules.appointment.biz.app;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.OnlineAppointmentForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointmentModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointmentQuery;
import com.yunya.feign.appointment.vo.OnlineAppointmentVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.OnlineAppointment;
import com.yunya.modules.appointment.biz.web.AppointmentBiz;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.OnlineAppointmentMapper;
import io.swagger.annotations.ApiModelProperty;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 在线预约逻辑
 * @author: LHB
 * @create: 2021-05-18 16:57
 **/
@Service
public class OnlineAppointmentBiz extends BaseBiz<OnlineAppointmentMapper, OnlineAppointment> {

    @Autowired
    private AppointmentBiz appointmentBiz;

    /**
     * 根据id查询线上预约申请
     * @param id  预约申请ID
     * @return 返回预约申请信息
     */
    public ResponseResult<OnlineAppointmentVo> findOnlineAppointmentById(Integer id) {

        OnlineAppointmentVo onlineAppointment = mapper.selectEntityById(id);
        if (onlineAppointment == null) {
            return ResponseUtil.fail(AppointmentError.APPOINT_DATA_NOT_EXIST.getCode(),AppointmentError.APPOINT_DATA_NOT_EXIST.getMessage(),null);
        }
        return ResponseUtil.success(onlineAppointment);
    }

    /**
     * 新增在线预约申请
     * @param model 预约申请参数
     * @return 结果
     */
    public ResponseResult<T> addOnlineAppointment(OnlineAppointmentModel model) {

        OnlineAppointment build = EntityUtils.build(model, OnlineAppointment.class);
        build.setCrtName(build.getPatientName());
        // TODO 设置预约申请状态 build.setStatus()

        int status = mapper.insertSelective(build);
        if (status <= 0) {
            ResponseUtil.fail(AppointmentError.APPOINTMENT_FAIL.getCode(),AppointmentError.APPOINTMENT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 修改在线预约申请
     * @param form 预约申请参数
     * @return 结果
     */
    public ResponseResult<T> updateOnlineAppointment(OnlineAppointmentForm form) {
        Integer id = form.getId();
        Example example = new Example(OnlineAppointment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        int count = mapper.selectCountByExample(example);
        if (count <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINT_DATA_NOT_EXIST.getCode(),AppointmentError.APPOINT_DATA_NOT_EXIST.getMessage(),null);
        }
        OnlineAppointment build = EntityUtils.build(form, OnlineAppointment.class);
        build.setUpdName(build.getPatientName());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        int status = mapper.updateByPrimaryKey(build);
        if (status <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINT_EDIT_FAIL.getCode(),AppointmentError.APPOINT_EDIT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 删除预约申请
     * @param id 预约申请ID
     * @return 结果
     */
    public ResponseResult<T> deleteOnlineAppointmentById(Integer id) {
        Example example = new Example(OnlineAppointment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        int count = mapper.selectCountByExample(example);
        if (count <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINT_DATA_NOT_EXIST.getCode(),AppointmentError.APPOINT_DATA_NOT_EXIST.getMessage(),null);
        }
        int status = mapper.deleteByPrimaryKey(id);
        if (status <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINT_CANCEL_FAIL.getCode(),AppointmentError.APPOINT_CANCEL_FAIL.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据条件批量查询预约申请
     * @param query 查询参数
     * @return 结果列表
     */
    public ResponseResult<PageInfo<OnlineAppointmentVo>> findByCondition(OnlineAppointmentQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<OnlineAppointmentVo> results = mapper.findByCondition(query);
        return ResponseUtil.success(new PageInfo<OnlineAppointmentVo>(results));
    }
}
