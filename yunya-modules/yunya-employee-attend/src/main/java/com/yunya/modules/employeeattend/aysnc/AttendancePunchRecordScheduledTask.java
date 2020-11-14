package com.yunya.modules.employeeattend.aysnc;

import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.vo.AttendancePunchRecordVO;
import com.yunya.feign.employee_attend.vo.FieldInfoVO;
import com.yunya.feign.employee_attend.vo.LeaveInfoVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.models.employee_attend.AttendancePunchRecord;
import com.yunya.modules.employeeattend.biz.*;
import com.yunya.modules.employeeattend.enums.AttendanceStatusEnum;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleVO;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 简介：定时任务：考勤打卡生成今天待打卡记录模板数据，当该服务集群部署需要加分布式锁
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/11 10:23
 * @since: 1.0.0
 */
@Component
@EnableScheduling
public class AttendancePunchRecordScheduledTask implements InitializingBean {
    /** 打卡项目类型：请假 */
    private static final String LEAVE = "1";
    /** 打卡项目类型：上班 */
    private static final String WORK = "上班";
    /** 打卡项目类型：加班 */
    private static final String WORKOVERTIME = "2";
    /** 打卡项目类型：外勤 */
    private static final String FIELD = "3";
    /** 按天请假 */
    private static final Integer USEDAY = 1;
    /** 注入日志对象 */
    private final Logger logger = LoggerFactory.getLogger(AttendancePunchRecordScheduledTask.class);
    /** 注入对象 */
    @Autowired
    private AttendancePunchRecordBiz attendancePunchRecordBiz;
    /** 注入对象 */
    @Autowired
    private EmployeeScheduleBiz employeeScheduleBiz;
    /** 注入对象 */
    @Autowired
    private LeaveInfoBiz leaveInfoBiz;
    /** 注入对象 */
    @Autowired
    private FieldInfoBiz fieldInfoBiz;
    /** 注入对象 */
    @Autowired
    private WorkOvertimeInfoBiz workOvertimeInfoBiz;

    /**
     * 生成今天待打卡记录模板数据.定时任务每天01：00：00执行 00 00 01 * * ?
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "00 00 01 * * ?")
    public void produceAttendancePunchTemplateData(){
        logger.info("开始生成考勤打卡模板数据");
        try {
            Date now = new Date(System.currentTimeMillis());
            //查询当天是否已经生成过
            AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
            queryForm.setPunchDate(now);
            List<AttendancePunchRecordVO> attendancePunchRecordVOS = attendancePunchRecordBiz.findAttendancePunchRecordList(queryForm);
            if (attendancePunchRecordVOS!=null && !attendancePunchRecordVOS.isEmpty()) {
                return;
            }
            /*SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWorkStatus(new Byte[]{0, 1, 3});
            List<SysUserInfoDetail> userList = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            List<Integer> userIds = new ArrayList<>();
            userList.forEach(user->{
                Integer userId = user.getUserId();
                if (userIds.contains(userId)) {
                    userIds.add(userId);
                }
            });*/

            //当天班次列表
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeSchedulesByDateAndEmpId(null,Arrays.asList(now));
            Map<Integer, EmployeeScheduleVO> restMap = new HashMap<>();
            Map<Integer, List<EmployeeScheduleVO>> punchItemMap = new LinkedHashMap<>();
            employeeScheduleVOS.forEach(employeeScheduleVO -> {
                Integer userId = employeeScheduleVO.getEmployeeId();
                employeeScheduleVO.getClinicId();
                if (employeeScheduleVO.getSecondEndTime() != null) {
                    employeeScheduleVO.setFirstEndTime(employeeScheduleVO.getSecondEndTime());
                }
                if (WORK.equals(employeeScheduleVO.getType())) {
                    employeeScheduleVO.setType("0");
                    List<EmployeeScheduleVO> list = punchItemMap.get(userId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(employeeScheduleVO);
                    punchItemMap.put(userId, list);
                }
                restMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
            });

            //当天请假列表追加
            appendLeaveList(punchItemMap, restMap);
            //当天加班列表追加
            appendWorkOvertimeList(punchItemMap, restMap);
            //当天外勤列表追加
            appendFieldList(punchItemMap);
            if (!punchItemMap.isEmpty()) {
                insertDefaultPunchRecord(punchItemMap);
            }
        } catch (Exception e) {
            logger.error("<======AttendancePunchRecordScheduledTask Error: {}=======>",e);
        } finally {
            logger.info("结束生成考勤打卡模板数据");
        }
    }

    /**
     * 追加请假列表
     * @param punchItemMap
     * @param restMap
     */
    private void appendLeaveList(Map<Integer, List<EmployeeScheduleVO>> punchItemMap, Map<Integer, EmployeeScheduleVO> restMap) {
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfosByUserIdsAndDate(null, new Date(System.currentTimeMillis()));
        leaveInfoVOS.forEach(leaveInfoVO -> {
            Integer userId = leaveInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            int vacationStatus = leaveInfoVO.getVacationStatus();
            if (USEDAY.equals(vacationStatus)) {//按天请假
                EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                punchItem.setType(LEAVE);
                punchItem.setFirstStartTime(leaveInfoVO.getStartTime());
                punchItem.setFirstEndTime(leaveInfoVO.getEndTime());
                punchItem.setId(leaveInfoVO.getId());
                punchItem.setEmployeeId(userId);
                punchItem.setName("请假");
                punchItemMap.put(userId, Arrays.asList(punchItem));
            } else {//按班次请假
                Integer scheduleId = leaveInfoVO.getScheduleId();
                List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
                for (EmployeeScheduleVO employeeScheduleVO : list) {
                    Integer id = employeeScheduleVO.getId();
                    if (id.equals(scheduleId)) {
                        Date startTime = leaveInfoVO.getStartTime();
                        Date endTime = leaveInfoVO.getEndTime();
                        Date firstTime = employeeScheduleVO.getFirstStartTime();
                        Date lastTime = employeeScheduleVO.getFirstEndTime();
                        if (startTime.compareTo(firstTime)==0 && endTime.compareTo(lastTime)==0) {//请假优先，则剔除当前班次
//                            EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
//                            punchItem.setType(LEAVE);
//                            punchItem.setFirstStartTime(leaveInfoVO.getStartTime());
//                            punchItem.setFirstEndTime(leaveInfoVO.getEndTime());
//                            punchItem.setId(leaveInfoVO.getId());
//                            punchItem.setEmployeeId(userId);
//                            EmployeeScheduleVO rest = restMap.get(userId);
//                            punchItem.setClinicId(rest.getClinicId());
//                            punchItem.setName(rest.getName());
//                            punchItemList.add(punchItem);
                        } else {
                            if (startTime.compareTo(firstTime) == 0 && endTime.before(lastTime)) {
                                employeeScheduleVO.setFirstStartTime(endTime);
                            } else if (startTime.after(firstTime) && endTime.compareTo(lastTime) == 0) {
                                employeeScheduleVO.setFirstEndTime(startTime);
                            }
                            punchItemList.add(employeeScheduleVO);
                        }
                    } else {
                        punchItemList.add(employeeScheduleVO);
                    }
                }
                punchItemMap.put(userId, punchItemList);
            }
        });
    }

    /**
     * 追加加班列表
     * @param punchItemMap
     * @param restMap
     */
    private void appendWorkOvertimeList(Map<Integer, List<EmployeeScheduleVO>> punchItemMap, Map<Integer, EmployeeScheduleVO> restMap) {
        List<WorkOvertimeInfoVO> workOvertimeInfoVOS = workOvertimeInfoBiz.findWorkOvertimeInfosByUserIdsAndDate(null, new Date(System.currentTimeMillis()));
        workOvertimeInfoVOS.forEach(workOvertimeInfoVO -> {
            Integer userId = workOvertimeInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            Integer scheduleId = workOvertimeInfoVO.getScheduleId();
            EmployeeScheduleVO restVO = restMap.get(scheduleId);
            Date startTime = restVO.getFirstEndTime();
            Date endTime = restVO.getFirstEndTime();
            List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
            for (EmployeeScheduleVO employeeScheduleVO : list) {
                Date firstTime = employeeScheduleVO.getFirstStartTime();
                if (startTime.before(firstTime)) {// 加班班次超前于上班班次
                    EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                    punchItem.setType(WORKOVERTIME);
                    punchItem.setFirstStartTime(startTime);
                    punchItem.setFirstEndTime(endTime);
                    punchItem.setId(workOvertimeInfoVO.getId());
                    punchItem.setEmployeeId(userId);
                    punchItem.setClinicId(restVO.getClinicId());
                    punchItem.setName(restVO.getName());
                    punchItemList.add(punchItem);
                }
                punchItemList.add(employeeScheduleVO);
            }
            punchItemMap.put(userId, punchItemList);
        });
    }

    /**
     * 追加外勤列表
     * @param punchItemMap
     */
    private void appendFieldList(Map<Integer, List<EmployeeScheduleVO>> punchItemMap) {
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfosByUserIdAndDate(null, new Date(System.currentTimeMillis()));
        fieldInfoVOS.forEach(fieldInfoVO -> {
            Integer userId = fieldInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            Date startTime = fieldInfoVO.getStartTime();
            Date endTime = fieldInfoVO.getEndTime();
            List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
            for (EmployeeScheduleVO scheduleVO : list) {
                Date firstTime = scheduleVO.getFirstStartTime();
                Date lastTime = scheduleVO.getFirstEndTime();
                if (startTime.before(firstTime) || endTime.after(lastTime)) {
                    EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                    punchItem.setType(FIELD);
                    punchItem.setFirstStartTime(startTime);
                    punchItem.setFirstEndTime(endTime);
                    punchItem.setId(fieldInfoVO.getId());
                    punchItem.setEmployeeId(userId);
                    punchItem.setName("外勤");
                    punchItemList.add(punchItem);
                }
                punchItemList.add(scheduleVO);
            }
            punchItemMap.put(userId, punchItemList);
        });
    }

    /**
     * 生成模板打卡数据
     * @param punchItemMap
     */
    @Transactional
    public void insertDefaultPunchRecord(Map<Integer, List<EmployeeScheduleVO>> punchItemMap) {
        Date now = new Date(System.currentTimeMillis());
        byte unvalid = AttendanceStatusEnum.UNVALID_PUNCH.getCode();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Map.Entry<Integer, List<EmployeeScheduleVO>> entry : punchItemMap.entrySet()) {
            Integer userId = entry.getKey();
            List<EmployeeScheduleVO> list = entry.getValue();
            if (list.size() == 1) {//一分为二
                EmployeeScheduleVO employeeScheduleVO = list.get(0);
                Date startTime = employeeScheduleVO.getFirstStartTime();
                Date endTime = employeeScheduleVO.getFirstEndTime();
                AttendancePunchRecord punchRecord1 = new AttendancePunchRecord();
                punchRecord1.setPunchDate(now);
                punchRecord1.setUptTime(now);
                punchRecord1.setIsPunch((byte) 0);
                punchRecord1.setCrtTime(now);
                punchRecord1.setCrtId(-999);
                punchRecord1.setUptId(-999);
                punchRecord1.setPunchType((byte) 0);//上班
                punchRecord1.setUserId(userId);
                String typeStr = employeeScheduleVO.getType();
                if (LEAVE.equals(typeStr)) {
                    punchRecord1.setPunchStatus(unvalid);
                }
                punchRecord1.setSource(Byte.valueOf(typeStr));
                punchRecord1.setSourceId(employeeScheduleVO.getId());
                punchRecord1.setStartTime(startTime);
                punchRecord1.setEndTime(endTime);
                punchRecord1.setOrgId(employeeScheduleVO.getClinicId());
                punchRecord1.setName(employeeScheduleVO.getName());
                attendancePunchRecordBiz.insertSelective(punchRecord1);

                String dateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
                AttendancePunchRecord punchRecord2 = new AttendancePunchRecord();
                punchRecord2.setPunchDate(now);
                punchRecord2.setUptTime(now);
                punchRecord2.setIsPunch((byte) 0);
                punchRecord2.setCrtTime(now);
                punchRecord2.setCrtId(-999);
                punchRecord2.setUptId(-999);
                punchRecord2.setPunchType((byte) 1);//下班
                punchRecord2.setUserId(userId);
                typeStr = employeeScheduleVO.getType();
                if (LEAVE.equals(typeStr)) {
                    punchRecord2.setPunchStatus(unvalid);
                }
                punchRecord1.setSource(Byte.valueOf(typeStr));
                punchRecord2.setSourceId(employeeScheduleVO.getId());
                punchRecord2.setStartTime(endTime);
                punchRecord2.setOrgId(employeeScheduleVO.getClinicId());
                punchRecord2.setName(employeeScheduleVO.getName());
                try {
                    punchRecord2.setEndTime(sdf.parse(dateStr));
                } catch (ParseException e) {
                    logger.error("<====== AttendancePunchRecordScheduledTask.insertDefaultPunchRecord Error: {} ======>",e);
                }
                attendancePunchRecordBiz.insertSelective(punchRecord2);
            } else if (list.size() > 1) {//取首尾
                EmployeeScheduleVO firstItem = list.get(0);
                AttendancePunchRecord punchRecord1 = new AttendancePunchRecord();
                punchRecord1.setPunchDate(now);
                punchRecord1.setUptTime(now);
                punchRecord1.setIsPunch((byte) 0);
                punchRecord1.setCrtTime(now);
                punchRecord1.setPunchType((byte) 0);//上班
                punchRecord1.setUserId(userId);
                punchRecord1.setCrtId(-999);
                punchRecord1.setUptId(-999);
                String typeStr = firstItem.getType();
                if (LEAVE.equals(typeStr)) {
                    punchRecord1.setPunchStatus(unvalid);
                }
                punchRecord1.setSource(Byte.valueOf(typeStr));
                punchRecord1.setSourceId(firstItem.getId());
                punchRecord1.setStartTime(firstItem.getFirstStartTime());
                punchRecord1.setEndTime(firstItem.getFirstEndTime());
                punchRecord1.setOrgId(firstItem.getClinicId());
                punchRecord1.setName(firstItem.getName());
                attendancePunchRecordBiz.insertSelective(punchRecord1);

                EmployeeScheduleVO lastItem = list.get(list.size()-1);
                AttendancePunchRecord punchRecord2 = new AttendancePunchRecord();
                punchRecord2.setPunchDate(now);
                punchRecord2.setUptTime(now);
                punchRecord2.setIsPunch((byte) 0);
                punchRecord2.setCrtTime(now);
                punchRecord2.setPunchType((byte) 1);//下班
                punchRecord2.setUserId(userId);
                punchRecord2.setCrtId(-999);
                punchRecord2.setUptId(-999);
                typeStr = lastItem.getType();
                if (LEAVE.equals(typeStr)) {
                    punchRecord2.setPunchStatus(unvalid);
                }
                punchRecord1.setSource(Byte.valueOf(typeStr));
                punchRecord2.setSourceId(lastItem.getId());
                punchRecord2.setStartTime(lastItem.getFirstStartTime());
                punchRecord2.setEndTime(lastItem.getFirstEndTime());
                punchRecord2.setOrgId(lastItem.getClinicId());
                punchRecord2.setName(lastItem.getName());
                attendancePunchRecordBiz.insertSelective(punchRecord2);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        produceAttendancePunchTemplateData();
    }
}
