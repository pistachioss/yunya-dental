package com.yunya.modules.appointment.biz.app;

import com.yunya.feign.appointment.domain.form.OnlineAppointmentForm;
import com.yunya.feign.appointment.domain.model.OnlineAppointmentModel;
import com.yunya.feign.appointment.domain.query.OnlineAppointmentQuery;
import com.yunya.feign.appointment.vo.CountOnlineAppointVo;
import com.yunya.feign.appointment.vo.OnlineAppointNewMessageNoticeVo;
import com.yunya.feign.appointment.vo.OnlineAppointmentVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.MedicalOrganizationInfoVO;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.appointment.OnlineAppointItem;
import com.yunya.models.appointment.OnlineAppointment;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.OnlineAppointmentMapper;
import com.yunya.modules.appointment.service.AppointmentLifecycle;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private OnlineAppointItemBiz onlineAppointItemBiz;

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
        Integer dentistId = onlineAppointment.getDentistId();
        SysUserInfoDetail dentistInfo = systemServiceFeign.findSysUserEmployeeInfoByUserId(dentistId);
        if (dentistInfo != null) {
            onlineAppointment.setDentistName(dentistInfo.getName());
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

        // 判断预约申请是否已满
        boolean allowApply = checkApplyRules(model.getOrgId(), model.getAppointItemId(), model.getAppointDate(), model.getAppointTime());
        if (!allowApply) {
            return ResponseUtil.fail(AppointmentError.ONLINE_APPOINT_OUT_OF_CAPACITY.getCode(),
                    AppointmentError.ONLINE_APPOINT_OUT_OF_CAPACITY.getMessage(),null);
        }
        build.setCrtName(build.getPatientName());
        OnlineAppointItem item = new OnlineAppointItem();
        item.setItemId(model.getAppointItemId());
        OnlineAppointItem onlineAppointItem = onlineAppointItemBiz.selectOne(item);
        if (onlineAppointItem != null) {
            build.setDuration(onlineAppointItem.getDuration());
        }
        int status = mapper.insertSelective(build);
        if (status <= 0) {
            ResponseUtil.fail(AppointmentError.APPOINTMENT_FAIL.getCode(),AppointmentError.APPOINTMENT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success();
    }

    /**
     * 判断预约申请是否已满,默认同一时间之能有一个患者
     * @param orgId
     * @param itemId
     * @param date
     * @param time
     * @return
     */
    private boolean checkApplyRules(Integer orgId, Integer itemId, Date date, Date time) {
        String dateStr = date.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Map<String, List<CountOnlineAppointVo>> timeListObj = appointTimeList(orgId, itemId, dateStr,null);
        List<CountOnlineAppointVo> timeList = timeListObj.get("timeList");
        if (StringHelper.isNotEmpty(timeList)) {
            // 判断预约申请是否已满,默认同一时间之能有一个患者
            String timeStr = time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toString();
            return timeList.stream().anyMatch(vo -> vo.getTime().equals(timeStr.trim()) && vo.getCount().equals(0));
        }
        return true;
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
        OnlineAppointItem item = new OnlineAppointItem();
        item.setItemId(form.getAppointItemId());
        OnlineAppointItem onlineAppointItem = onlineAppointItemBiz.selectOne(item);
        if (onlineAppointItem != null) {
            build.setDuration(onlineAppointItem.getDuration());
        }
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
    public List<OnlineAppointmentVo> findByCondition(OnlineAppointmentQuery query) {

        String searchStr = query.getSearchStr();
        if (searchStr != null && searchStr.matches(BusinessConstants.MOBILE_REGEXP)) {
            query.setPhone(searchStr);
        } else {
            query.setPatientName(searchStr);
        }
        List<OnlineAppointmentVo> results = mapper.findByCondition(query);
        if (StringHelper.isNotEmpty(results)) {
            setDentistInfo(results);
            setOrgInfo(results);
            setPatientInfo(results);
        }
        return results;
    }

    /**
     * 设置患者信息
     * @param results 预约申请列表
     */
    private void setPatientInfo(List<OnlineAppointmentVo> results) {
        log.info("====> 患者信息\n{}",results);
        List<Integer> patientIds =  results.stream().filter(entity-> null != entity.getPatientId())
                .mapToInt(OnlineAppointmentVo::getPatientId)
                .boxed()
                .collect(Collectors.toList());
        if (StringHelper.isNotEmpty(patientIds)) {
            List<PatientBaseInfoVo> patientInfos = remotePatientCentralServiceFeign.findPatientInfoByIds(patientIds);
            if (StringHelper.isNotEmpty(patientInfos)) {
                results.forEach(onlineAppointmentVo -> {
                    patientInfos.stream().filter(
                            e -> e.getId().equals(onlineAppointmentVo.getPatientId()))
                            .findAny()
                            .ifPresent(entity -> onlineAppointmentVo.setMedicalNumber(entity.getMedicalNumber()));
                });
            }
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
                onlineAppointmentVo.setOrgName(orgInfo.getBrandName() + "(" + orgInfo.getAbbreviation() + ")");
            });
        }
    }

    /**
     * 设置医生信息
     * @param results 预约申请列表
     */
    private void setDentistInfo(List<OnlineAppointmentVo> results) {
        log.info("线上预约导出===>\n{}",results);
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
        List<OnlineAppointmentVo> data = this.findByCondition(query);
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
    public void onlineAppointStatus(Integer onlineAppointmentId,Integer patientId) {
        log.info("===》预约ID:{}",onlineAppointmentId);
        OnlineAppointment onlineAppointment = mapper.selectByPrimaryKey(onlineAppointmentId);
        if (onlineAppointment != null) {
            onlineAppointment.setStatus((byte) 1);
            onlineAppointment.setUpdName(onlineAppointment.getPatientName());
            onlineAppointment.setUpdTime(new Date(System.currentTimeMillis()));
            mapper.updateByPrimaryKeySelective(onlineAppointment);
        }

    }

    /**
     * 预约消息通知
     * @param orgId  门诊ID
     * @return 返回结果
     */
    public OnlineAppointNewMessageNoticeVo newMessageNotice(Integer orgId) {
        int count = mapper.countNewMessageNotice(orgId);
        System.out.println(count);
        OnlineAppointNewMessageNoticeVo result = new OnlineAppointNewMessageNoticeVo();
        result.setLastTimeStamp(System.currentTimeMillis());
        result.setCount(count);
        return result;
    }

    /**
     * 查询预约时间列表
     * @param orgId 门诊ID
     * @param itemId 预约项目ID
     * @return
     */
    public Map<String,List<CountOnlineAppointVo>> appointTimeList(Integer orgId, Integer itemId, String date,String time) {
        List<CountOnlineAppointVo> timeList = new ArrayList<>(16);
        OnlineAppointItem itemQuery = new OnlineAppointItem();
        itemQuery.setItemId(itemId);
        OnlineAppointItem onlineAppointItem = onlineAppointItemBiz.selectOne(itemQuery);
        Integer duration = onlineAppointItem.getDuration();
        MedicalOrganizationInfoVO clinicInfoDetail = systemServiceFeign.clinicExtInfoByCompanyId(orgId);
        String startTime = clinicInfoDetail.getBusinessStartTime();
        String endTime = clinicInfoDetail.getBusinessEndTime();
        CountOnlineAppointVo countOnlineAppointVo = new CountOnlineAppointVo();
        countOnlineAppointVo.setTime(startTime);
        countOnlineAppointVo.setDuration(duration);
        countOnlineAppointVo.setCount(0);
        timeList.add(countOnlineAppointVo);
        while(true) {
            startTime = DateUtil.timeAdd(startTime, duration, DateUtil.MINUTE);
            int result = DateUtil.compareTime(startTime, endTime);
            if (result > 0) {
                break;
            }
            CountOnlineAppointVo item = new CountOnlineAppointVo();
            item.setTime(startTime);
            item.setCount(0);
            item.setDuration(duration);
            timeList.add(item);
        }
        countOnlineAppointSameTime(timeList,orgId,date);
        if (StringHelper.isNotBlank(time)) {
            // 根据当前时间渲染时间列表中相应的时间块
            this.drawGreenBlock(timeList,time);
        }
        Map<String,List<CountOnlineAppointVo>> result = new HashMap<>();
        result.put("timeList",timeList);
        return result;
    }

    /**
     * 根据时间渲染时间列表
     * @param timeVoList
     * @param time
     */
    private void drawGreenBlock(List<CountOnlineAppointVo> timeVoList, String time) {
        if (StringHelper.isNotEmpty(timeVoList)) {
            LocalTime appointStartLocalTime = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
            for (CountOnlineAppointVo vo: timeVoList) {
                LocalTime appointEndlocalTime = appointStartLocalTime.plusMinutes(vo.getDuration());
                LocalTime startLocalTime = LocalTime.parse(vo.getTime(), DateTimeFormatter.ISO_LOCAL_TIME);
                LocalTime endLocalTime = LocalTime.parse(vo.getTime(), DateTimeFormatter.ISO_LOCAL_TIME).plusMinutes(vo.getDuration());
                if ((appointStartLocalTime.isAfter(startLocalTime) || appointStartLocalTime.equals(startLocalTime)) && appointStartLocalTime.isBefore(endLocalTime)) {
                    vo.setSelected(true);
                }
                if ((appointEndlocalTime.isBefore(endLocalTime) || appointEndlocalTime.equals(endLocalTime)) && appointEndlocalTime.isAfter(startLocalTime)) {
                    vo.setSelected(true);
                    break;
                }
            }
        }
    }

    /**
     * 设置同一时间内预约人数
     * @param params  预约时间表
     * @param orgId 门诊ID
     * @param date 预约日期
     */
    private void countOnlineAppointSameTime(List<CountOnlineAppointVo> params, Integer orgId, String date) {
        List<CountOnlineAppointVo> result = mapper.countOnlineAppointSameTime(orgId,date);
        if (StringHelper.isNotEmpty(result)) {
            result.forEach (vo-> {
                Optional<CountOnlineAppointVo> onlineAppointOptional = params.stream().filter(e -> e.getTime().equals(vo.getTime())).findAny();
                onlineAppointOptional.ifPresent(countOnlineAppointVo -> vo.setCount(countOnlineAppointVo.getCount()));
            });
        }
    }

    /**
     * PC端新建预约完成时，通知微信预约申请服务更新预约状态
     * @param appointment 预约信息
     */
    @Override
    public void build(Appointment appointment) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=================预约生命周期=============\n");
        sb.append("==> 新建预约完成\n");
        sb.append(appointment.toString());
        sb.append("==========================================");
        log.info(sb.toString());
        onlineAppointStatus(appointment.getOnlineAppointmentId(),appointment.getPatientId());
    }
}
