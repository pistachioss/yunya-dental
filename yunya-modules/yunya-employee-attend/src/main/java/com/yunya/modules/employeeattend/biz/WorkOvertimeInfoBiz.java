package com.yunya.modules.employeeattend.biz;

import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.CopyInfo;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.models.employee_attend.WorkOvertimeInfo;

import com.yunya.modules.employeeattend.form.WorkOvertimeInfoForm;
import com.yunya.modules.employeeattend.mapper.CopyInfoMapper;
import com.yunya.modules.employeeattend.mapper.EmployeeScheduleMapper;
import com.yunya.modules.employeeattend.mapper.WorkOvertimeInfoMapper;
import com.yunya.modules.employeeattend.vo.EmListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_TRANSFORMATION_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.INSERT_MODEL;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkOvertimeInfoBiz extends BaseBiz<WorkOvertimeInfoMapper, WorkOvertimeInfo> {
    @Autowired
    private EmployeeScheduleMapper employeeScheduleMapper;
    @Autowired
    private CopyInfoMapper copyInfoMapper;
    /**
     * 根据日期和用户id列表查询加班列表
     *
     * @param userIds 用户id
     * @param date    日期
     * @return
     */
    public List<WorkOvertimeInfoVO> findWorkOvertimeInfosByUserIdsAndDate(List<Integer> userIds, Date date) {
        return mapper.findWorkOvertimeInfoByUserIdsAndDate(userIds, date);
    }

    /**
     * 新增加班申请
     *
     * @param workOvertimeInfoForm
     * @return
     */
    public int create(WorkOvertimeInfoForm workOvertimeInfoForm) {
        //判断是否有其他类型的申请
        if (true) {
            //判断是否与同类型其他申请时间冲突
            if (true) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = simpleDateFormat.format(workOvertimeInfoForm.getWorkDay());
                String nowString = simpleDateFormat.format(new Date());
                Date date = null;
                Date now = new Date();
                try {
                    date = simpleDateFormat.parse(dateString);
                    now = simpleDateFormat.parse(nowString);
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                }
                if (now.before(date)) {
                    EmployeeSchedule employeeSchedule = new EmployeeSchedule();
                    employeeSchedule.setId(workOvertimeInfoForm.getRestScheduleId());
                    //休息班的班次信息
                    List<EmListVO> relist = employeeScheduleMapper.findemList(employeeSchedule);
                    employeeSchedule.setId(workOvertimeInfoForm.getScheduleId());
                    //加班的班次信息
                    List<EmListVO> emlist = employeeScheduleMapper.findemList(employeeSchedule);
                    if (relist.size() > 0 && emlist.size() > 0) {
                        DateFormat df = DateFormat.getTimeInstance();
                        Date restartTime = null;
                        Date reendTime = null;
                        Date startTime = null;
                        Date endTime = null;
                        try {
                            startTime = df.parse(df.format(emlist.get(0).getStartTime()));
                            endTime = df.parse(df.format(emlist.get(0).getEndTime()));
                            restartTime = df.parse(df.format(relist.get(0).getStartTime()));
                            reendTime = df.parse(df.format(relist.get(0).getEndTime()));
                        } catch (ParseException e) {
                            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                        }
                        if (startTime.getTime() <= reendTime.getTime() && startTime.getTime() >= restartTime.getTime()
                                && endTime.getTime() <= reendTime.getTime() && endTime.getTime() >= restartTime.getTime()) {
                            WorkOvertimeInfo workOvertimeInfo = new WorkOvertimeInfo();
                            BeanUtils.copyProperties(workOvertimeInfoForm, workOvertimeInfo);
                            workOvertimeInfo.setCrtId(workOvertimeInfo.getUserId());
                            workOvertimeInfo.setCrtTime(new Date());
                            int num = mapper.insertSelective(workOvertimeInfo);
                            //生成抄送信息
                            if (workOvertimeInfoForm.getCopyList().size()>0){
                                List<CopyInfo>copyInfoList = new ArrayList<>();
                                for(Integer copyId:workOvertimeInfoForm.getCopyList()){
                                    CopyInfo copyInfo = new CopyInfo();
                                    copyInfo.setApplyId(num);
                                    copyInfo.setApplyType(1);
                                    copyInfo.setUserId(copyId);
                                    copyInfo.setCrtId(workOvertimeInfo.getUserId());
                                    copyInfo.setCrtTime(new Date());
                                }
                                copyInfoMapper.batchInsert(copyInfoList);
                            }
                            return num;
                        }
                        throw new ClientServiceException("加班班次时间应处于休息班次时间段之内", DATA_TRANSFORMATION_EXIST);
                    }
                    throw new ClientServiceException("班次信息错误", DATA_TRANSFORMATION_EXIST);
                }
                throw new ClientServiceException("不可以发起申请当天及以前的申请事项", INSERT_MODEL);
            }
            throw new ClientServiceException("该申请与其他外勤申请时间冲突", INSERT_MODEL);
        }
        throw new ClientServiceException("每天只能发起一种类型的申请", INSERT_MODEL);
    }
}
