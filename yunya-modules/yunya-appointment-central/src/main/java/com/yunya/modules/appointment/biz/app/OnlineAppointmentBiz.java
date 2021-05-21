package com.yunya.modules.appointment.biz.app;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.OnlineAppointmentForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointmentModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointmentQuery;
import com.yunya.feign.appointment.vo.CancelAppointmentVO;
import com.yunya.feign.appointment.vo.OnlineAppointmentVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.OnlineAppointment;
import com.yunya.modules.appointment.biz.web.AppointmentBiz;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.OnlineAppointmentMapper;
import com.yunya.modules.appointment.service.AppointmentLifecycle;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @program: yunya-dental
 * @description: 在线预约逻辑
 * @author: LHB
 * @create: 2021-05-18 16:57
 **/
@Service
@Slf4j
public class OnlineAppointmentBiz extends BaseBiz<OnlineAppointmentMapper, OnlineAppointment> implements AppointmentLifecycle {

    @Autowired
    private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;

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
        if (StringHelper.isNotEmpty(results)) {
            setDentistInfo(results);
            setOrgInfo(results);
            setPatientInfo(results);
        }
        return ResponseUtil.success(new PageInfo<OnlineAppointmentVo>(results));
    }

    /**
     * 设置门诊信息
     * @param results 预约申请列表
     */
    private void setPatientInfo(List<OnlineAppointmentVo> results) {
        List<Integer> patientIds = results.stream().mapToInt(
                OnlineAppointmentVo::getPatientId).boxed().collect(Collectors.toList());
        List<PatientBaseInfoVo> patientInfos = remotePatientCentralServiceFeign.findPatientInfoByIds(patientIds);
        if (StringHelper.isNotEmpty(patientInfos)) {
            results.stream().forEach(onlineAppointmentVo -> {
                PatientBaseInfoVo patientBaseInfoVo = patientInfos.stream().filter(e -> e.getId().equals(onlineAppointmentVo.getPatientId())).findFirst().get();
                onlineAppointmentVo.setMedicalNumber(patientBaseInfoVo.getMedicalNumber());
            });
        }
    }

    /**
     * 设置门诊信息
     * @param results 预约申请列表
     */
    private void setOrgInfo(List<OnlineAppointmentVo> results) {
        List<Integer> orgIds = results.stream().mapToInt(OnlineAppointmentVo::getOrgId).boxed().collect(Collectors.toList());
        List<OrganizationInfoDetail> orgInfos = systemServiceFeign.findOrgInfoInIds(orgIds);
        if (StringHelper.isNotEmpty(orgInfos)) {
            results.stream().forEach(onlineAppointmentVo -> {
                OrganizationInfoDetail orgInfo = orgInfos.stream().filter(e -> e.getId().equals(onlineAppointmentVo.getOrgId())).findFirst().get();
                onlineAppointmentVo.setOrgName(orgInfo.getName());
            });
        }
    }

    /**
     * 设置医生信息
     * @param results 预约申请列表
     */
    private void setDentistInfo(List<OnlineAppointmentVo> results) {
        List<Integer> dentistIds = results.stream().mapToInt(OnlineAppointmentVo::getDentistId).boxed().collect(Collectors.toList());
        List<SysUserInfoDetail> dentistInfos = systemServiceFeign.findSysUserEmployeeInfoByUserIds(dentistIds);
        if (StringHelper.isNotEmpty(dentistInfos)) {
            results.stream().forEach(onlineAppointmentVo -> {
                SysUserInfoDetail userInfoDetail = dentistInfos.stream().filter(e -> e.getUserId().equals(onlineAppointmentVo.getDentistId())).findFirst().get();
                onlineAppointmentVo.setDentistName(userInfoDetail.getName());
            });
        }
    }

    /**
     * 导出预约申请
     * @param response
     * @param query
     */
    public void export(HttpServletResponse response, @Validated OnlineAppointmentQuery query) throws IOException {
        query.setWhetherPage(false);
        ExcelUtil<OnlineAppointmentVo> excelUtil = new ExcelUtil<>(OnlineAppointmentVo.class);
        ResponseResult<PageInfo<OnlineAppointmentVo>> responseResult = this.findByCondition(query);
        List<OnlineAppointmentVo> data = responseResult.getData().getList();
        Integer orgId = query.getOrgId();
        String orgName = "";
        if (orgId != null) {
            OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
            orgName = orgInfo.getName();
        }
        String fileName = excelUtil.getFileName(query.getAppointStartDate(), query.getAppointEndDate(), orgName, "在线预约申请表");
        excelUtil.exportExcel(response,data,"在线预约申请表",fileName);
    }

    /**
     * 设置预约状态
     * @param onlineAppointmentId
     */
    public void onlineAppointStatus(Integer onlineAppointmentId) {
        OnlineAppointment onlineAppointment = mapper.selectByPrimaryKey(onlineAppointmentId);
        if (onlineAppointment != null) {
            onlineAppointment.setStatus((byte) 1);
            onlineAppointment.setUpdName(onlineAppointment.getPatientName());
            onlineAppointment.setUpdTime(new Date(System.currentTimeMillis()));
            mapper.updateByPrimaryKeySelective(onlineAppointment);
        }
    }

    @Override
    public void build(Appointment appointment) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=================预约生命周期=============\n");
        sb.append("==> 新建预约完成\n");
        sb.append("==========================================");
        log.info(sb.toString());
        onlineAppointStatus(appointment.getOnlineAppointmentId());
    }
}
