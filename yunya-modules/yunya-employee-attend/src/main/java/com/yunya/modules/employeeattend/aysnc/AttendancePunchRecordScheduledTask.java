package com.yunya.modules.employeeattend.aysnc;

import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.vo.AttendancePunchRecordVO;
import com.yunya.feign.employee_attend.vo.FieldInfoVO;
import com.yunya.feign.employee_attend.vo.LeaveInfoVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.employee_attend.AttendancePunchRecord;
import com.yunya.modules.employeeattend.biz.*;
import com.yunya.modules.employeeattend.enums.AttendanceStatusEnum;
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
    private static final String START_TIME = "08:00";
    private static final String END_TIME = "18:00";
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
            EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
            employeeScheduleQueryForm.setWorkDate(now);
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
            Map<Integer, EmployeeScheduleVO> employeeScheduleMap = new HashMap<>();
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
                employeeScheduleMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
            });

            //当天请假列表追加
            Map<Integer, List<Object>> leaveByDays = appendLeaveList(punchItemMap, restItemMap);
            //当天加班列表追加
            appendWorkOvertimeList(punchItemMap, employeeScheduleMap);
            //当天外勤列表追加
            appendFieldList(punchItemMap);
            if (!punchItemMap.isEmpty() || !restItemMap.isEmpty() || !leaveByDays.isEmpty()) {
                insertDefaultPunchRecord(punchItemMap, restItemMap, leaveByDays);
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
     * @param restItemMap
     * @return
     */
    private Map<Integer, List<Object>> appendLeaveList(Map<Integer, List<EmployeeScheduleVO>> punchItemMap, Map<Integer, List<EmployeeScheduleVO>> restItemMap) {
        Map<Integer, List<Object>> leaveByDays = new HashMap<>(5);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfosByUserIdsAndDate(null, new Date(System.currentTimeMillis()));
        leaveInfoVOS.forEach(leaveInfoVO -> {
            Integer userId = leaveInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            int vacationStatus = leaveInfoVO.getVacationStatus();
            if (USEDAY.equals(vacationStatus)) {//按天请假
                if (list.isEmpty()) {
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
                        Date startTime = DateUtil.dateTo19700101(leaveInfoVO.getStartTime());
                        Date endTime = DateUtil.dateTo19700101(leaveInfoVO.getEndTime());
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
                            restItemMap.put(userId, list);
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
        return leaveByDays;
    }

    /**
     * 追加加班列表
     * @param punchItemMap
     * @param employeeScheduleVOMap
     */
    private void appendWorkOvertimeList(Map<Integer, List<EmployeeScheduleVO>> punchItemMap, Map<Integer, EmployeeScheduleVO> employeeScheduleVOMap) {
        List<WorkOvertimeInfoVO> workOvertimeInfoVOS = workOvertimeInfoBiz.findWorkOvertimeInfosByUserIdsAndDate(null, new Date(System.currentTimeMillis()));
        workOvertimeInfoVOS.forEach(workOvertimeInfoVO -> {
            Integer userId = workOvertimeInfoVO.getUserId();
            List<EmployeeScheduleVO> list = punchItemMap.get(userId);
            if (list == null) {
                list = new ArrayList<>();
            }
            Integer restScheduleId = workOvertimeInfoVO.getRestScheduleId();
            EmployeeScheduleVO restVO = employeeScheduleVOMap.get(restScheduleId);
            if (restVO != null) {
                Date startTime = restVO.getFirstStartTime();
                Date endTime = restVO.getFirstEndTime();
                List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
                if (list.isEmpty()) {
                    EmployeeScheduleVO punchItem = new EmployeeScheduleVO();
                    punchItem.setType(WORKOVERTIME);
                    punchItem.setFirstStartTime(startTime);
                    punchItem.setFirstEndTime(endTime);
                    punchItem.setId(workOvertimeInfoVO.getId());
                    punchItem.setEmployeeId(userId);
                    punchItem.setClinicId(restVO.getClinicId());
                    punchItem.setName(restVO.getName());
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
                            punchItem.setClinicId(restVO.getClinicId());
                            punchItem.setName(restVO.getName());
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
                            punchItem.setClinicId(restVO.getClinicId());
                            punchItem.setName(restVO.getName());
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
            Date startTime = DateUtil.dateTo19700101(fieldInfoVO.getStartTime());
            Date endTime = DateUtil.dateTo19700101(fieldInfoVO.getEndTime());
            List<EmployeeScheduleVO> punchItemList = new ArrayList<>();
            if (list.isEmpty()) {
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
        byte unvalid = AttendanceStatusEnum.UNVALID_PUNCH.getCode();
        for (Map.Entry<Integer, List<EmployeeScheduleVO>> entry : punchItemMap.entrySet()) {
            Integer userId = entry.getKey();
            restItemMap.remove(userId);
            List<EmployeeScheduleVO> list = entry.getValue();
            if (list.size() == 1) {//一分为二
                EmployeeScheduleVO employeeScheduleVO = list.get(0);
                AttendancePunchRecord attendancePunchRecord = createRecord(employeeScheduleVO, now, userId, unvalid, (byte)0);
                attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                attendancePunchRecord = createRecord(employeeScheduleVO, now, userId, unvalid, (byte)1);
                attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
            } else if (list.size() > 1) {//取首尾
                EmployeeScheduleVO firstItem = list.get(0);
                AttendancePunchRecord attendancePunchRecord = createRecord(firstItem, now, userId, unvalid, (byte)0);
                attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                EmployeeScheduleVO lastItem = list.get(list.size()-1);
                attendancePunchRecord = createRecord(lastItem, now, userId, unvalid, (byte)1);
                attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
            }
        }

        // 当天只包含休息班次或按班次请假
        for (Map.Entry<Integer, List<EmployeeScheduleVO>> entry : restItemMap.entrySet()) {
            Integer userId = entry.getKey();
            List<EmployeeScheduleVO> list = entry.getValue();
            List<Object> employeeScheduleVOS = leaveByDays.get(userId);
            if (employeeScheduleVOS!=null && !employeeScheduleVOS.isEmpty()) {
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
                    punchItem.setType(LEAVE_BYDAY);
                    punchItem.setFirstStartTime(employeeScheduleVO.getFirstStartTime());
                    punchItem.setFirstEndTime(employeeScheduleVO.getFirstEndTime());
                    punchItem.setId(id);
                    punchItem.setEmployeeId(userId);
                    punchItem.setClinicId(employeeScheduleVO.getClinicId());
                    punchItem.setName("按天请假");
                    employeeScheduleVOS.add(punchItem);
                    leaveByDays.put(userId, employeeScheduleVOS);
                }
            } else {
                if (list.size() == 1) {//一分为二
                    EmployeeScheduleVO employeeScheduleVO = list.get(0);
                    AttendancePunchRecord attendancePunchRecord = createRecord(employeeScheduleVO, now, userId, unvalid, (byte) 0);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                    attendancePunchRecord = createRecord(employeeScheduleVO, now, userId, unvalid, (byte) 1);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                } else if (list.size() > 1) {//取首尾
                    EmployeeScheduleVO firstItem = list.get(0);
                    AttendancePunchRecord attendancePunchRecord = createRecord(firstItem, now, userId, unvalid, (byte) 0);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                    EmployeeScheduleVO lastItem = list.get(list.size() - 1);
                    attendancePunchRecord = createRecord(lastItem, now, userId, unvalid, (byte) 1);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                }
            }
        }

        if (!leaveByDays.isEmpty()) {
            for (Map.Entry<Integer, List<Object>> entry : leaveByDays.entrySet()) {
                Integer userId = entry.getKey();
                List<Object> list = entry.getValue();
                list.stream().sorted((o1, o2)->{
                    EmployeeScheduleVO evo1 = (EmployeeScheduleVO) o1;
                    EmployeeScheduleVO evo2 = (EmployeeScheduleVO) o2;
                    return evo1.getFirstStartTime().compareTo(evo2.getFirstStartTime());
                });
                if (list.size() == 1) {//一分为二
                    Object obj = list.get(0);
                    if (obj instanceof Integer) {
                        continue;
                    }
                    EmployeeScheduleVO employeeScheduleVO = (EmployeeScheduleVO)obj;
                    AttendancePunchRecord attendancePunchRecord = createRecord(employeeScheduleVO, now, userId, unvalid, (byte) 0);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                    attendancePunchRecord = createRecord(employeeScheduleVO, now, userId, unvalid, (byte) 1);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                } else if (list.size() > 1) {//取首尾
                    Object obj = list.get(0);
                    if (obj instanceof Integer) {
                        continue;
                    }
                    EmployeeScheduleVO firstItem = (EmployeeScheduleVO) obj;
                    AttendancePunchRecord attendancePunchRecord = createRecord(firstItem, now, userId, unvalid, (byte) 0);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                    obj = list.get(list.size() - 1);
                    if (obj instanceof Integer) {
                        continue;
                    }
                    EmployeeScheduleVO lastItem = (EmployeeScheduleVO) obj;
                    attendancePunchRecord = createRecord(lastItem, now, userId, unvalid, (byte) 1);
                    attendancePunchRecordBiz.insertSelective(attendancePunchRecord);
                }
            }
        }
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
        punchRecord.setIsPunch((byte) 0);
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
