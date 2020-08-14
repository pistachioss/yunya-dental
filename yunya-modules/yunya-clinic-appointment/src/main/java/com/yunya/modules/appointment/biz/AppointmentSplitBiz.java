package com.yunya.modules.appointment.biz;

import com.yunya.feign.appointment.domain.base.AppointmentSplitUpdateBaseInfo;
import com.yunya.feign.appointment.domain.form.AppointmentSplitForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.AppointmentSplit;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.AppointmentSplitQuery;
import com.yunya.modules.appointment.mapper.AppointmentSplitMapper;
import com.yunya.feign.appointment.vo.AppointmentSplitVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * 新增预约分解
     * @param splitModel 时长分解列表
     * @return
     * @throws ParseException
     */
    public Integer insertAppointSplit(AppointmentSplitModel splitModel) {

        List<AppointmentSplitBaseInfo> splitList = splitModel.getSplitList();
        if (splitList == null || splitList.isEmpty()){
            throw new ClientServiceException("时长分解列表不能为空！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        // 检查时长分解是否符合条件，分解之后的时长和必须等于预约总时长
        List<AppointmentSplit> splits = this.checkSplit(splitModel.getAppointmentId(),splitModel.getAppointDuration(),splitList);
        if (splits == null || splits.isEmpty()){
            throw new ClientServiceException("时长分解有误！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        return mapper.insertAppointmentSplit(splits);

    }

    /**
     * 根据条件查询分解预约
     * @param query
     * @return
     */
    public List<AppointmentSplitVo> findAppointmentSplitByExample(AppointmentSplitQuery query){
        List<AppointmentSplitVo> appointmentSplitByExample = mapper.findAppointmentSplitByExample(query);
        return appointmentSplitByExample;
    }

    /**
     * 修改时长分解
     * @param form  时长分解表单 TODO
     * @return
     */
    public Integer updateAppointSplit(AppointmentSplitForm form){
        List<AppointmentSplitUpdateBaseInfo> splitList = form.getSplitList();
        if (StringHelper.isEmpty(splitList)){
            throw new ClientServiceException("时长分解列表不能为空！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        // 检查时长分解是否符合条件，分解之后的时长和必须等于预约总时长
        List<AppointmentSplit> splits = this.checkSplit(form.getAppointmentId(), form.getAppointDuration(), splitList);
        if (splits == null || splits.isEmpty()){
            throw new ClientServiceException("时长分解有误！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        splits.forEach(appointmentSplit -> {
            AppointmentSplit hasAppointSplit = mapper.selectByPrimaryKey(appointmentSplit.getId());
            if (hasAppointSplit == null){
                throw new ClientServiceException("要修改的数据不存在！",OperationCodeConstants.DATA_NOT_EXIST);
            }
            appointmentSplit.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
            appointmentSplit.setUpdName(BaseContextHandler.getName());
            appointmentSplit.setUpdTime(new Date(System.currentTimeMillis()));
            int result = mapper.updateByPrimaryKeySelective(appointmentSplit);
            if (result <= 0){
                throw new ClientServiceException("修改时长分解失败！",OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        });
        return 1;
    }

    /**
     * 时长分解检查（新增用）
     * @param appointDuration
     * @param splitList
     * @return
     */
    public List<AppointmentSplit> appointSplitCheck(Integer appointDuration, List<AppointmentSplitBaseInfo> splitList){
        List<AppointmentSplit> splits = this.checkSplit(null, appointDuration, splitList);
        return splits;
    }


    /**
     * 将form参数封装转化为 AppointmentSplit实体 并检查分解开始时间是否早于结束分解时间
     * @param appointmentSplitBaseInfo   form参数
     * @return  成功返回AppointmentSplit;  失败返回null（分解开始时间不早于结束分解时间）
     * @throws ParseException
     */
    private AppointmentSplit formToEntity(AppointmentSplitBaseInfo appointmentSplitBaseInfo) {
        String splitStartTimeStr = appointmentSplitBaseInfo.getSplitStartTime();
        String splitEndTimeStr = appointmentSplitBaseInfo.getSplitEndTime();
        // 获取当前年月日
        SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yearMonthDay = yearMonthDayFormat.format(new Date(System.currentTimeMillis()));
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
     * @return  时长分解符合条件返回List<AppointmentSplit>实体列表；否则返回null TODO
     */
    private List<AppointmentSplit> checkSplit(Integer appointId,Integer appointDuration, List<? extends AppointmentSplitBaseInfo> splitList) {
        List<AppointmentSplit> splits = new ArrayList<>();
        if(splitList == null || splitList.isEmpty()){
            throw new ClientServiceException("时长分解列表不能为空！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }

        splitList.forEach(appointmentSplitBase -> {
            // 将appointmentSplitBase表单转化为AppointmentSplit实体
            AppointmentSplit appointmentSplit = formToEntity(appointmentSplitBase);
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

        if (appointDuration != sumMinute){
            throw new ClientServiceException("预约分解时长错误，分解后的时长必须和预约总时长相等！请重新分解。", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        return splits;
    }


}
