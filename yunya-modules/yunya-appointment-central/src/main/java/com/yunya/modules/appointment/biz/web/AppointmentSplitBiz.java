package com.yunya.modules.appointment.biz.web;

import com.yunya.feign.appointment.domain.base.AppointmentSplitUpdateBaseInfo;
import com.yunya.feign.appointment.domain.form.AppointmentSplitForm;
import com.yunya.feign.appointment.vo.AppointConflictInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.AppointmentSplit;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.AppointmentSplitQuery;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.AppointmentSplitMapper;
import com.yunya.feign.appointment.vo.AppointmentSplitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预约拆分
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:02
 * @update yunya-lihuibin    2020-07-30    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointmentSplitBiz extends BaseBiz<AppointmentSplitMapper, AppointmentSplit> {

    /** 系统服务 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 新增预约分解
     * @param splitModel 时长分解列表
     * @return 插入成功的记录数
     * @throws ParseException 时间转换异常
     */
    public Integer insertAppointSplit(AppointmentSplitModel splitModel) {

        List<AppointmentSplitBaseInfo> splitList = splitModel.getSplitList();
        if (!StringHelper.isEmpty(splitList)) {
            // 检查时长分解是否符合条件，分解之后的时长和必须等于预约总时长
            List<AppointmentSplit> splits = this.checkSplit(splitModel.getAppointDate(),splitModel.getAppointmentId(),splitModel.getAppointDuration(),splitList);
            return mapper.insertAppointmentSplit(splits);
        }
        return 0;
    }

    /**
     * 根据条件查询分解预约
     * @param query
     * @return list
     */
    public List<AppointmentSplitVo> findAppointmentSplitByExample(AppointmentSplitQuery query){
        List<AppointmentSplitVo> splitVos = mapper.findAppointmentSplitByExample(query);
        if (!StringHelper.isEmpty(splitVos)) {
            splitVos.forEach(appointmentSplitVo -> {
                SysEmployee sysEmployee = this.remoteSystemServiceFeign.findSysEmployeeById(appointmentSplitVo.getAssistantId());
                if (null != sysEmployee) {
                    String name = sysEmployee.getName();
                    appointmentSplitVo.setAssistantName(name);
                }
            });
        }
        return splitVos;
    }

    /**
     * 根据条件批量查询分解预约
     * @param orgId 组织ID
     * @param appointIds 预约ID集合
     * @return list
     */
    public List<AppointmentSplitVo> findAppointmentSplitByExampleBatch(Integer orgId, List<Integer> appointIds){
        if(StringHelper.isNotEmpty(appointIds)) {
            List<AppointmentSplitVo> splitVos = mapper.findAppointmentSplitByExampleBatch(orgId, appointIds);
            if (!StringHelper.isEmpty(splitVos)) {
                List<Integer> assistantIds = splitVos.stream().map(AppointmentSplitVo::getAssistantId).collect(Collectors.toList());
                // 查询助手信息
                List<SysUserInfoDetail> sysUserEmployeeInfoByUserIds = this.remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(assistantIds);
                if (StringHelper.isNotEmpty(sysUserEmployeeInfoByUserIds)) {
                    splitVos.forEach(appointmentSplitVo -> {
                        boolean parallel = sysUserEmployeeInfoByUserIds.stream().anyMatch(entity -> entity.getUserId().equals(appointmentSplitVo.getAssistantId()));
                        if (parallel) {
                            SysUserInfoDetail userInfoDetail = sysUserEmployeeInfoByUserIds.stream().filter(entity -> entity.getUserId().equals(appointmentSplitVo.getAssistantId())).findAny().get();
                            String name = userInfoDetail.getName();
                            appointmentSplitVo.setAssistantName(name);
                        }
                    });
                }
            }
            return splitVos;
        }
        return null;
    }

    /**
     * 修改时长分解
     * @param form  时长分解表单
     * @return 修改成功的记录条数
     */
    public Integer updateAppointSplit(AppointmentSplitForm form){
        List<AppointmentSplitBaseInfo> splitList = form.getSplitList();
        if (StringHelper.isEmpty(splitList)){
            throw new ClientServiceException("时长分解列表不能为空！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        // 检查时长分解是否符合条件，分解之后的时长和必须等于预约总时长
        List<AppointmentSplit> splits = this.checkSplit(form.getAppointDate(),form.getAppointmentId(), form.getAppointDuration(), splitList);
        if (splits.isEmpty()){
            throw new ClientServiceException("时长分解有误！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        // 先删除该条预约相关的分解记录
        AppointmentSplitQuery splitQuery = new AppointmentSplitQuery();
        splitQuery.setOrgId(form.getOrgId());
        splitQuery.setAppointDate(form.getAppointDate());
        splitQuery.setAppointmentId(form.getAppointmentId());
        List<AppointmentSplitVo> appointmentSplitVoList = mapper.findAppointmentSplitByExample(splitQuery);
        appointmentSplitVoList.forEach(appointmentSplitVo -> {
            mapper.deleteByPrimaryKey(appointmentSplitVo.getId());
        });
        // 再重新插入修改之后的分解
        int result = mapper.insertAppointmentSplit(splits);
        return result;
    }

    /**
     * 时长分解检查（新增用）
     * @param appointDuration 预约时长
     * @param splitList 分解列表
     * @return  分解列表
     */
    public List<AppointmentSplit> appointSplitCheck(Date appointDate ,Integer appointDuration, List<AppointmentSplitBaseInfo> splitList){
        return this.checkSplit(appointDate,null, appointDuration, splitList);
    }


    /**
     * 将form参数封装转化为 AppointmentSplit实体 并检查分解开始时间是否早于结束分解时间
     * @param appointmentSplitBaseInfo   form参数
     * @return  成功返回AppointmentSplit;  失败返回null（分解开始时间不早于结束分解时间）
     * @throws ParseException
     */
    private AppointmentSplit formToEntity(Date appointDate,AppointmentSplitBaseInfo appointmentSplitBaseInfo) {
        String splitStartTimeStr = appointmentSplitBaseInfo.getSplitStartTime();
        String splitEndTimeStr = appointmentSplitBaseInfo.getSplitEndTime();
        // 获取当前年月日
        SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yearMonthDay = yearMonthDayFormat.format(appointDate);
        // 拼接开始分解时间 yyyy-MM-dd HH:mm
        String startTime = yearMonthDay + " " + splitStartTimeStr;
        // 拼接结束分解时间 yyyy-MM-dd HH:mm
        String endTime = yearMonthDay + " " + splitEndTimeStr;
        SimpleDateFormat compareDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 分解开始时间
        Date startTimeDate = null;
        // 分解结束时间
        Date endTimeDate = null;
        Date startParse = null;
        Date endparse = null;
        try{
            startParse = compareDateFormat.parse(startTime);
            endparse = compareDateFormat.parse(endTime);
        } catch (ParseException e){
            throw new ClientServiceException("[时间转化格式异常]："+e.getMessage(),OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }

        if (startParse.getTime() >= endparse.getTime()){
            return null;
        }
        startTimeDate = startParse;
        endTimeDate = endparse;
        // 如果没有错误，转化实体
        AppointmentSplit build = EntityUtils.build(appointmentSplitBaseInfo, AppointmentSplit.class);
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setSplitStartTime(startTimeDate);
        build.setSplitEndTime(endTimeDate);
        return build;
    }

    /**
     * 检测时长分解是否符合条件（分解之后的时长和必须等于预约总时长）
     * @param appointId  预约id
     * @param appointDuration  预约总时长
     * @param splitList  时长分解列表
     * @return  时长分解符合条件返回List<AppointmentSplit>实体列表；否则返回null
     */
    private List<AppointmentSplit> checkSplit(Date appointDate, Integer appointId,Integer appointDuration, List<? extends AppointmentSplitBaseInfo> splitList) {
        List<AppointmentSplit> splits = new ArrayList<>();
        if(splitList == null || splitList.isEmpty()){
            throw new ClientServiceException("时长分解列表不能为空！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }

        splitList.forEach(appointmentSplitBase -> {
            // 将appointmentSplitBase表单转化为AppointmentSplit实体
            AppointmentSplit appointmentSplit = formToEntity(appointDate,appointmentSplitBase);
            // 如果分解开始时间不早于结束分解时间 返回错误信息
            if (null == appointmentSplit){
                throw new ClientServiceException("分解开始时长不能大于分解结束时长！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            } else{
                // 否则将实体添加到集合中
                appointmentSplit.setAppointmentId(appointId);
                appointmentSplit.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
                appointmentSplit.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                splits.add(appointmentSplit);
            }
        });

        // 检测拆分的时间是否等于总预约时长
        Integer sumMinute = 0;
        for (AppointmentSplit split: splits ) {
            Calendar instance = Calendar.getInstance();
            // 预约开始时间
            instance.setTime(split.getSplitStartTime());
            int startHour = instance.get(Calendar.HOUR_OF_DAY);
            int startMinute = instance.get(Calendar.MINUTE);

            // 预约结束时间
            instance.setTime(split.getSplitEndTime());
            int endHour = instance.get(Calendar.HOUR_OF_DAY);
            int endMinute = instance.get(Calendar.MINUTE);
            sumMinute += (endHour - startHour) * 60 + (endMinute - startMinute);
        }

        if (!appointDuration.equals(sumMinute)){
            throw new ClientServiceException("预约分解时长错误，分解后的时长必须和预约总时长相等！请重新分解", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        return splits;
    }



}
