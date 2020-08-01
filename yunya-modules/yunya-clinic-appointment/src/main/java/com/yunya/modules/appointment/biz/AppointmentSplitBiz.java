package com.yunya.modules.appointment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.EntityUtils;
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
     * @param splitModel
     * @return
     */
    public Map<String,Object> insertSplit(AppointmentSplitModel splitModel) throws ParseException {
        List<AppointmentSplitBase> splitList = splitModel.getSplitList();
        List<AppointmentSplit> splits = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        if (!StringHelper.isEmpty(splitList)){
            splitList.forEach(appointmentSplitBase -> {
                try {
                    // 将appointmentSplitBase表单转化为AppointmentSplit实体
                    AppointmentSplit appointmentSplit = formToEntity(appointmentSplitBase);
                    // 如果分解开始时间不早于结束分解时间 返回错误信息
                    if (null == appointmentSplit){
                        resultMap.put("msg","开始时间不能大于结束时间！");
                    } else{
                        // 否则将实体添加到集合中
                        splits.add(appointmentSplit);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            // 如果有错误，将错误返回
            if (!StringHelper.isEmpty(resultMap)){
                return resultMap;
            }

            // 检测拆分的时间是否等于总预约时长
            // 总预约时长
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
                resultMap.put("msg","预约分解时间段错误！请重新分解。");
                return resultMap;
            }

            int result = mapper.insertAppointmentSplit(splits);
            // 返回插入数据失败信息
            if (result <= 0){
                resultMap.put("msg","预约分解失败！");
                return resultMap;
            }
            // 成功返回空结果集
            return resultMap;
        }
        resultMap.put("msg","分解列表为空");
        return resultMap;
    }

    /**
     * 删除预约分解
     * @param delForm
     * @return
     */
    public Map<String,Object> delSplit(AppointmentSplitDelForm delForm){
        Map<String,Object> resultMap = new HashMap<>();
        int result = mapper.delAppoointmentSplitByIds(delForm.getSplitIds());
        if (result <= 0){
            resultMap.put("msg","删除分解预约失败！");
            return resultMap;
        }
        resultMap.put("msg","删除" + result + "条分解预约");
        return resultMap;
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
    private AppointmentSplit formToEntity(AppointmentSplitBase appointmentSplitBase ) throws ParseException {
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
        Date startParse = compareDateFormat.parse(startTime);
        Date endparse = compareDateFormat.parse(endTime);
        if (compareDateFormat.parse(startTime).getTime() >= compareDateFormat.parse(endTime).getTime()){
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
