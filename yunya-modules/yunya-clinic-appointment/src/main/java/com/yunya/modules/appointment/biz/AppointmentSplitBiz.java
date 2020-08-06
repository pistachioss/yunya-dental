package com.yunya.modules.appointment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.AppointmentSplit;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBase;
import com.yunya.feign.appointment.domain.form.AppointmentSplitDelForm;
import com.yunya.feign.appointment.domain.model.AppointmentSplitModel;
import com.yunya.feign.appointment.domain.query.AppointmentSplitQuery;
import com.yunya.modules.appointment.mapper.AppointmentSplitMapper;
import com.yunya.modules.appointment.vo.AppointmentSplitVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
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
    public Integer insertSplit(AppointmentSplitModel splitModel) throws ParseException {
        List<AppointmentSplitBase> splitList = splitModel.getSplitList();
        List<AppointmentSplit> splits = new ArrayList<>();
        Map<String, Object> errtMap = new HashMap<>();

        if (splitList == null || splitList.isEmpty()){
            return -1;
        }

        splitList.forEach(appointmentSplitBase -> {
            // 将appointmentSplitBase表单转化为AppointmentSplit实体
            AppointmentSplit appointmentSplit = formToEntity(appointmentSplitBase);
            // 如果分解开始时间不早于结束分解时间 返回错误信息
            if (null == appointmentSplit){
                throw new ClientServiceException("分解开始时长不能大于分解结束时长！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            } else{
                // 否则将实体添加到集合中
                appointmentSplit.setAppointmentId(splitModel.getAppointmentId());
                appointmentSplit.setOrgId(splitModel.getOrgId());
                appointmentSplit.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                splits.add(appointmentSplit);
            }
        });

        // 检测拆分的时间是否等于总预约时长
        Integer appointDuration = splitModel.getAppointDuration();
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

        int result = mapper.insertAppointmentSplit(splits);
        return result;

    }

    /**
     * 删除预约分解
     * @param delForm
     * @return
     */
    public Integer delSplit(AppointmentSplitDelForm delForm){
        int result = mapper.delAppoointmentSplitByIds(delForm.getSplitIds());
        return result;
    }

    /**
     * 根据条件查询分解预约
     * @param query
     * @return
     */
    public List<AppointmentSplitVo> findAppointmentSplitByExample(AppointmentSplitQuery query){
        return mapper.findAppointmentSplitByExample(query);
    }


    /**
     * 将form参数封装转化为 AppointmentSplit实体 并检查分解开始时间是否早于结束分解时间
     * @param appointmentSplitBase   form参数
     * @return  成功返回AppointmentSplit;  失败返回null（分解开始时间不早于结束分解时间）
     * @throws ParseException
     */
    private AppointmentSplit formToEntity(AppointmentSplitBase appointmentSplitBase ) {
        String splitStartTimeStr = appointmentSplitBase.getSplitStartTime();
        String splitEndTimeStr = appointmentSplitBase.getSplitEndTime();
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
        AppointmentSplit build = EntityUtils.build(appointmentSplitBase, AppointmentSplit.class);
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setSplitStartTime(startTimeDate);
        build.setSplitEndTime(endTimeDate);
        return build;

    }


}
