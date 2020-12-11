package com.yunya.modules.employeeattend.aysnc;

import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.vo.AttendancePunchRecordVO;
import com.yunya.feign.employee_attend.vo.FieldInfoVO;
import com.yunya.feign.employee_attend.vo.LeaveInfoVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.employee_attend.AttendancePunchRecord;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.modules.employeeattend.biz.*;
import com.yunya.modules.employeeattend.enums.AttendanceIsPunchEnum;
import com.yunya.modules.employeeattend.enums.AttendanceStatusEnum;
import com.yunya.modules.employeeattend.enums.AttendanceTypeEnum;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    /** 打卡项目类型：上班 */
    private static final String ONDUTY = "0";
    /** 打卡项目类型：休息 */
    private static final String REST = "1";
    /** 打卡项目类型：按天请假 */
    private static final String LEAVE_BYDAY = "2";
    /** 打卡项目类型：按班次请假 */
    private static final String LEAVE_BYSCHEDULE = "3";
    /** 班次类型：上班 */
    private static final String WORK = "上班";
    /** 打卡项目类型：加班 */
    private static final String WORKOVERTIME = "4";
    /** 打卡项目类型：外勤 */
    private static final String FIELD = "5";
    /** 按天请假 */
    private static final Integer USEDAY = 1;

    /** 注入日志对象 */
    private final Logger logger = LoggerFactory.getLogger(AttendancePunchRecordScheduledTask.class);
    /** 注入对象 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
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
    /** 注入对象 */
    @Autowired
    private BaseScheduleBiz baseScheduleBiz;

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
            queryForm.setWhetherPage(false);
            List<AttendancePunchRecordVO> attendancePunchRecordVOS = attendancePunchRecordBiz.findAttendancePunchRecordList(queryForm);
            List<Integer> userIds = new ArrayList<>();
            if (attendancePunchRecordVOS!=null && !attendancePunchRecordVOS.isEmpty()) {
                SysUserEmployeeModel model = new SysUserEmployeeModel();
                model.setWorkStatus(new Byte[]{0, 1, 3});
                model.setWhetherPage(false);
                List<SysUserInfoDetail> userList = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
                userList.forEach(user->{
                    Integer userId = user.getUserId();
                    if (!userIds.contains(userId)) {
                        userIds.add(userId);
                    }
                });
                attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
                    Integer userId = attendancePunchRecordVO.getUserId();
                    userIds.remove(userId);
                });
                if (userIds.isEmpty()) {
                    return;
                }
            }
            produceAttendancePunchTemplateData(userIds);
        } catch (Exception e) {
            logger.error("<======AttendancePunchRecordScheduledTask Error: {}=======>",e);
        } finally {
            logger.info("结束生成考勤打卡模板数据");
        }
    }

    public void produceAttendancePunchTemplateData(List<Integer> userIds) {
        Date now = new Date(System.currentTimeMillis());
        //当天班次列表
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setWorkDate(now);
        employeeScheduleQueryForm.setUserIds(userIds);
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
        Map<Integer, List<EmployeeScheduleVO>> restItemMap = new LinkedHashMap<>();
        Map<Integer, List<EmployeeScheduleVO>> punchItemMap = new LinkedHashMap<>();
        employeeScheduleVOS.forEach(employeeScheduleVO -> {
            Integer userId = employeeScheduleVO.getEmployeeId();
            if (employeeScheduleVO.getSecondEndTime() != null) {
                employeeScheduleVO.setFirstEndTime(employeeScheduleVO.getSecondEndTime());
            }
            if (WORK.equals(employeeScheduleVO.getType())) {
                employeeScheduleVO.setType(ONDUTY);
                List<EmployeeScheduleVO> list = punchItemMap.get(userId);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(employeeScheduleVO);
                punchItemMap.put(userId, list);
            } else {
                employeeScheduleVO.setType(REST);
                List<EmployeeScheduleVO> list = restItemMap.get(userId);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(employeeScheduleVO);
                restItemMap.put(userId, list);
            }
        });

        //当天请假列表追加
        Map<Integer, List<Object>> leaveByDays = appendLeaveList(userIds, punchItemMap, restItemMap);
        //当天加班列表追加
        appendWorkOvertimeList(userIds, punchItemMap);
        //当天外勤列表追加
        appendFieldList(userIds, punchItemMap);
        if (!punchItemMap.isEmpty() || !restItemMap.isEmpty() || !leaveByDays.isEmpty()) {
            insertDefaultPunchRecord(punchItemMap, restItemMap, leaveByDays);
        }
    }

    /**
     * 追加请假列表
     *
     * @param userIds
     * @param punchItemMap
     * @param restItemMap
     * @return
     */
    private Map<Integer, List<Object>> appendLeaveList(List<Integer> userIds, Map<Integer, List<EmployeeScheduleVO>> punchItemMap, Map<Integer, List<EmployeeScheduleVO>> restItemMap) {
        Map<Integer, List<Object>> leaveByDays = new HashMap<>(5);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfosByUserIdsAndDate(userIds, new Date(System.currentTimeMillis()));
        leaveInfoVOS.forEach(leaveInfoVO -> {
            Integer userId = leaveInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            int vacationStatus = leaveInfoVO.getVacationStatus();
            if (USEDAY.equals(vacationStatus)) {//按天请假
                if (list.isEmpty()) {//按天请假的当天不存在上班班次
                    leaveByDays.put(userId, Arrays.asList(leaveInfoVO.getId()));
                } else {
                    for (EmployeeScheduleVO employeeScheduleVO : list) {
                        EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                        punchItem.setType(LEAVE_BYDAY);
                        punchItem.setFirstStartTime(employeeScheduleVO.getFirstStartTime());
                        punchItem.setFirstEndTime(employeeScheduleVO.getFirstEndTime());
                        punchItem.setId(leaveInfoVO.getId());
                        punchItem.setEmployeeId(userId);
                        punchItem.setClinicId(employeeScheduleVO.getClinicId());
                        punchItem.setName("按天请假");
                        List<Object> es = leaveByDays.get(userId);
                        if (es == null) {
                            es = new ArrayList<>();
                        }
                        es.add(punchItem);
                        leaveByDays.put(userId, es);
                    }
                }
                punchItemMap.remove(userId);
            } else {//按班次请假
                Integer scheduleId = leaveInfoVO.getScheduleId();
                List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
                for (EmployeeScheduleVO employeeScheduleVO : list) {
                    Integer id = employeeScheduleVO.getId();
                    if (id.equals(scheduleId)) {
                        Date startTime;
                        Date endTime;
                        try {
                            startTime = leaveInfoVO.getStartTime();
                            endTime = leaveInfoVO.getEndTime();
                        } catch (Exception e) {
                            throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                        }
                        Date firstTime = employeeScheduleVO.getFirstStartTime();
                        Date lastTime = employeeScheduleVO.getFirstEndTime();
                        if (startTime.compareTo(firstTime)==0 && endTime.compareTo(lastTime)==0) {//请假覆盖，则剔除当前班次
                            list = restItemMap.get(userId);
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                            EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                            punchItem.setType(LEAVE_BYSCHEDULE);
                            punchItem.setFirstStartTime(startTime);
                            punchItem.setFirstEndTime(endTime);
                            punchItem.setId(leaveInfoVO.getId());
                            punchItem.setEmployeeId(userId);
                            punchItem.setClinicId(leaveInfoVO.getOrgId());
                            punchItem.setName("按班次请假");
                            list.add(punchItem);
                            restItemMap.put(userId, list);
                        } else {
                            if (startTime.compareTo(firstTime)==0 && endTime.before(lastTime)) {
                                employeeScheduleVO.setFirstStartTime(endTime);
                            } else if (startTime.after(firstTime) && endTime.compareTo(lastTime)==0) {
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
        return leaveByDays;
    }

    /**
     * 追加加班列表
     * @param userIds
     * @param punchItemMap
     */
    private void appendWorkOvertimeList(List<Integer> userIds, Map<Integer, List<EmployeeScheduleVO>> punchItemMap) {
        List<WorkOvertimeInfoVO> workOvertimeInfoVOS = workOvertimeInfoBiz.findWorkOvertimeInfosByUserIdsAndDate(userIds, new Date(System.currentTimeMillis()));
        workOvertimeInfoVOS.forEach(workOvertimeInfoVO -> {
            Integer userId = workOvertimeInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            Integer scheduleId = workOvertimeInfoVO.getScheduleId();
            BaseSchedule baseSchedule = baseScheduleBiz.selectById(scheduleId);
            if (baseSchedule != null) {
                Date startTime = baseSchedule.getFirstStartTime();
                Date endTime = baseSchedule.getFirstEndTime();
                String name = baseSchedule.getName();
                List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
                if (list.isEmpty()) {
                    EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                    punchItem.setType(WORKOVERTIME);
                    punchItem.setFirstStartTime(startTime);
                    punchItem.setFirstEndTime(endTime);
                    punchItem.setId(workOvertimeInfoVO.getId());
                    punchItem.setEmployeeId(userId);
                    punchItem.setClinicId(workOvertimeInfoVO.getCompanyId());
                    punchItem.setName(name);
                    punchItemList.add(punchItem);
                } else {
                    for (EmployeeScheduleVO employeeScheduleVO : list) {
                        Date firstTime = employeeScheduleVO.getFirstStartTime();
                        if (startTime.before(firstTime)) {// 加班班次超前于上班班次
                            EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                            punchItem.setType(WORKOVERTIME);
                            punchItem.setFirstStartTime(startTime);
                            punchItem.setFirstEndTime(endTime);
                            punchItem.setId(workOvertimeInfoVO.getId());
                            punchItem.setEmployeeId(userId);
                            punchItem.setClinicId(workOvertimeInfoVO.getCompanyId());
                            punchItem.setName(name);
                            punchItemList.add(punchItem);
                            punchItemList.add(employeeScheduleVO);
                        } else {
                            punchItemList.add(employeeScheduleVO);
                            EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                            punchItem.setType(WORKOVERTIME);
                            punchItem.setFirstStartTime(startTime);
                            punchItem.setFirstEndTime(endTime);
                            punchItem.setId(workOvertimeInfoVO.getId());
                            punchItem.setEmployeeId(userId);
                            punchItem.setClinicId(workOvertimeInfoVO.getCompanyId());
                            punchItem.setName(name);
                            punchItemList.add(punchItem);
                        }
                    }
                }
                punchItemMap.put(userId, punchItemList);
            }
        });
    }

    /**
     * 追加外勤列表
     * @param userIds
     * @param punchItemMap
     */
    private void appendFieldList(List<Integer> userIds, Map<Integer, List<EmployeeScheduleVO>> punchItemMap) {
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfosByUserIdAndDate(userIds, new Date(System.currentTimeMillis()));
        fieldInfoVOS.forEach(fieldInfoVO -> {
            Integer userId = fieldInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            Date startTime;
            Date endTime;
            try {
                startTime = DateUtil.dateTo19700101(fieldInfoVO.getStartTime());
                endTime = DateUtil.dateTo19700101(fieldInfoVO.getEndTime());
            } catch (Exception e) {
                throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
            }
            List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
            if (!list.isEmpty()) {
                for (EmployeeScheduleVO scheduleVO : list) {
                    Date firstTime = scheduleVO.getFirstStartTime();
                    Date lastTime = scheduleVO.getFirstEndTime();
                    if (startTime.compareTo(firstTime)<=0 && endTime.compareTo(lastTime)>=0) {//外勤覆盖
                        EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                        punchItem.setType(FIELD);
                        punchItem.setFirstStartTime(startTime);
                        punchItem.setFirstEndTime(endTime);
                        punchItem.setId(fieldInfoVO.getId());
                        punchItem.setEmployeeId(userId);
                        punchItem.setName("外勤");
                        punchItem.setClinicId(fieldInfoVO.getCompanyId());
                        punchItemList.add(punchItem);
                    } else {
                        if (startTime.compareTo(firstTime)<=0) {
                            EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                            punchItem.setType(FIELD);
                            punchItem.setFirstStartTime(startTime);
                            punchItem.setFirstEndTime(endTime);
                            punchItem.setId(fieldInfoVO.getId());
                            punchItem.setEmployeeId(userId);
                            punchItem.setName("外勤");
                            punchItem.setClinicId(fieldInfoVO.getCompanyId());
                            punchItemList.add(punchItem);
                            punchItemList.add(scheduleVO);
                        } else if (endTime.compareTo(lastTime) >= 0) {
                            punchItemList.add(scheduleVO);
                            EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                            punchItem.setType(FIELD);
                            punchItem.setFirstStartTime(startTime);
                            punchItem.setFirstEndTime(endTime);
                            punchItem.setId(fieldInfoVO.getId());
                            punchItem.setEmployeeId(userId);
                            punchItem.setName("外勤");
                            punchItem.setClinicId(fieldInfoVO.getCompanyId());
                            punchItemList.add(punchItem);
                        } else {// 覆盖外勤
                            punchItemList.add(scheduleVO);
                        }
                    }
                }
            }
            punchItemMap.put(userId, punchItemList);
        });
    }

    /**
     * 生成模板打卡数据
     * @param punchItemMap
     * @param restItemMap
     * @param leaveByDays
     */
    @Transactional
    public void insertDefaultPunchRecord(Map<Integer, List<EmployeeScheduleVO>> punchItemMap, Map<Integer, List<EmployeeScheduleVO>> restItemMap, Map<Integer, List<Object>> leaveByDays) {
        Date now = new Date(System.currentTimeMillis());
        for (Map.Entry<Integer, List<EmployeeScheduleVO>> entry : punchItemMap.entrySet()) {
            Integer userId = entry.getKey();
            List<EmployeeScheduleVO> list = entry.getValue();
            if (list.size() > 0) {//取首尾
                restItemMap.remove(userId);
            }
        }

        // 当天只包含休息班次或按班次请假
        for (Map.Entry<Integer, List<EmployeeScheduleVO>> entry : restItemMap.entrySet()) {
            Integer userId = entry.getKey();
            List<EmployeeScheduleVO> list = entry.getValue();
            List<Object> employeeScheduleVOS = leaveByDays.get(userId);
            if (employeeScheduleVOS!=null && !employeeScheduleVOS.isEmpty()) { //按天请假当天存在休息班次
                Object object = employeeScheduleVOS.get(0);
                Integer id = null;
                if (object instanceof Integer) {
                    id = (Integer) object;
                    employeeScheduleVOS = new ArrayList<>(5);
                } else {
                    EmployeeScheduleVO leaveInfoVO = (EmployeeScheduleVO) object;
                    id = leaveInfoVO.getId();
                }
                for (EmployeeScheduleVO employeeScheduleVO : list) {
                    EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                    punchItem.setType(REST);
                    punchItem.setFirstStartTime(employeeScheduleVO.getFirstStartTime());
                    punchItem.setFirstEndTime(employeeScheduleVO.getFirstEndTime());
                    punchItem.setId(id);
                    punchItem.setEmployeeId(userId);
                    punchItem.setClinicId(employeeScheduleVO.getClinicId());
                    punchItem.setName(employeeScheduleVO.getName());
                    employeeScheduleVOS.add(punchItem);
                    leaveByDays.put(userId, employeeScheduleVOS);
                }
            } else {
                List<EmployeeScheduleVO> punchItems = punchItemMap.get(userId);
                if (punchItems == null) {
                    punchItems = new ArrayList<>();
                }
                punchItems.addAll(list);
                punchItemMap.put(userId, punchItems);
            }
        }

        for (Map.Entry<Integer, List<Object>> entry : leaveByDays.entrySet()) {
            Integer userId = entry.getKey();
            List<Object> list = entry.getValue();
            List<EmployeeScheduleVO> punchItems = punchItemMap.get(userId);
            if (punchItems == null) {
                punchItems = new ArrayList<>();
            }
            for (Object obj : list) {
                if (obj instanceof EmployeeScheduleVO) {
                    punchItems.add((EmployeeScheduleVO) obj);
                    punchItemMap.put(userId, punchItems);
                }
            }
        }

        byte unvalid = AttendanceStatusEnum.INVALID_PUNCH.getCode();
        punchItemMap.forEach((userId, employeeScheduleVOS)->{
            employeeScheduleVOS = employeeScheduleVOS.stream().sorted(Comparator.comparing(EmployeeScheduleVO::getFirstStartTime)).collect(Collectors.toList());
            int index = employeeScheduleVOS.size()-1;
            if (index == 0) {
                EmployeeScheduleVO employeeScheduleVO = employeeScheduleVOS.get(0);
                AttendancePunchRecord firstItem = createRecord(employeeScheduleVO, now, userId, unvalid, AttendanceTypeEnum.ONDUTY.getCode());
                attendancePunchRecordBiz.insertSelective(firstItem);
                AttendancePunchRecord lastItem = createRecord(employeeScheduleVO, now, userId, unvalid, AttendanceTypeEnum.OFFDUTY.getCode());
                attendancePunchRecordBiz.insertSelective(lastItem);
            } else if (index > 0) {
                EmployeeScheduleVO employeeScheduleVO = employeeScheduleVOS.get(0);
                AttendancePunchRecord firstItem = createRecord(employeeScheduleVO, now, userId, unvalid, AttendanceTypeEnum.ONDUTY.getCode());
                attendancePunchRecordBiz.insertSelective(firstItem);
                EmployeeScheduleVO lastEmployeeScheduleVO = employeeScheduleVOS.get(index);
                AttendancePunchRecord lastItem = createRecord(lastEmployeeScheduleVO, now, userId, unvalid, AttendanceTypeEnum.OFFDUTY.getCode());
                attendancePunchRecordBiz.insertSelective(lastItem);
            }
        });
    }

    /**
     * 生成数据
     * @param employeeScheduleVO
     * @param now
     * @param userId
     * @param unvalid
     * @param punchType
     * @return
     */
    private AttendancePunchRecord createRecord(EmployeeScheduleVO employeeScheduleVO, Date now, Integer userId, Byte unvalid, Byte punchType) {
        AttendancePunchRecord punchRecord = new AttendancePunchRecord();
        punchRecord.setPunchDate(now);
        punchRecord.setUptTime(now);
        punchRecord.setIsPunch(AttendanceIsPunchEnum.UNPUNCH.getCode());
        punchRecord.setCrtTime(now);
        punchRecord.setCrtId(-999);
        punchRecord.setUptId(-999);
        punchRecord.setPunchType(punchType);//上班
        punchRecord.setUserId(userId);
        String typeStr = employeeScheduleVO.getType();
        if (LEAVE_BYDAY.equals(typeStr)||LEAVE_BYSCHEDULE.equals(typeStr)||REST.equals(typeStr)) {
            punchRecord.setPunchStatus(unvalid);
        }
        punchRecord.setSource(Byte.valueOf(typeStr));
        punchRecord.setSourceId(employeeScheduleVO.getId());
        punchRecord.setStartTime(employeeScheduleVO.getFirstStartTime());
        punchRecord.setEndTime(employeeScheduleVO.getFirstEndTime());
        punchRecord.setOrgId(employeeScheduleVO.getClinicId());
        punchRecord.setName(employeeScheduleVO.getName());
        return punchRecord;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        produceAttendancePunchTemplateData();
    }
}
