package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.yunya.feign.employee_attend.form.*;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.employee_attend.AttendancePunchRecord;
import com.yunya.modules.employeeattend.enums.*;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.mapper.AttendancePunchRecordMapper;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_TRANSFORMATION_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介：考勤打卡业务层
 *
 * @author: chenlin
 * @Description: 考勤打卡业务层
 * @Date: 2020/11/9 14:29
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttendancePunchRecordBiz extends BaseBiz<AttendancePunchRecordMapper, AttendancePunchRecord> {
    /** 按天请假，时长固定8小时 */
    private static final String DAY_LEAVE_MINUTE = "8小时";
    /** 班次类型：休息 */
    private static final String REST = "休息";
    /** 班次类型：上班 */
    private static final String WORK = "上班";
    /** 按天请假：时长8小时以毫秒表示 */
    private static final long DAY_LEAVE_MILLSEC = 28800000;
    /** 注入对象 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    /** 注入对象 */
    @Autowired
    private AttendanceAddressSetBiz attendanceAddressSetBiz;
    /** 注入对象 */
    @Autowired
    private AttendanceWifiSetBiz attendanceWifiSetBiz;
    /** 注入对象 */
    @Autowired
    private EmployeeScheduleBiz employeeScheduleBiz;
    /** 注入对象 */
    @Autowired
    private LeaveInfoBiz leaveInfoBiz;
    @Autowired
    private LeaveScheduleBiz leaveScheduleBiz;
    /** 注入对象 */
    @Autowired
    private FieldInfoBiz fieldInfoBiz;
    /** 注入对象 */
    @Autowired
    private AttendanceDeviceBindingBiz attendanceDeviceBindingBiz;
    /** 注入对象 */
    @Autowired
    private WorkOvertimeInfoBiz workOvertimeInfoBiz;
    /** 注入对象 */
    @Autowired
    private AttendanceManualMakeupBiz attendanceManualMakeupBiz;

    /**
     * 分页查询员工的考勤打卡记录列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendancePunchRecordVO> findAttendancePunchRecordList(AttendancePunchRecordQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findAttendancePunchRecordList(queryForm);
    }

    /**
     * 查询打卡项目列表（班次、加班、请假、外勤等）以及即将打卡的项目
     *
     * @param queryForm 查询参数
     * @return
     */
    public AttendancePunchInfoVO punchInfo(AttendancePunchRecordQueryForm queryForm) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        AttendancePunchInfoVO result = new AttendancePunchInfoVO();
        AttendanceDeviceBindingVO attendanceDeviceBindingVO = attendanceDeviceBindingBiz.findEmployeeBindingDevice(userId);
        result.setHasDeviceBinding(true);
        if (attendanceDeviceBindingVO == null) {
            result.setHasDeviceBinding(false);
            return result;
        }

        // 根据考勤地址或Wifi的mac地址抽取用户当天的打卡项目（上班班次、加班、请假、外勤）
        String macAddress = queryForm.getWifiMacAddress();
        String longitude = queryForm.getLongitude();
        String latitude = queryForm.getLatitude();
        if (StringHelper.isEmpty(macAddress) && (StringHelper.isEmpty(longitude) || StringHelper.isEmpty(latitude))) {
            throw new ClientServiceException("考勤地址或Wifi不能都为空！", PARAMETERS_IS_ILLEGAL);
        }

        Integer orgId = null;
        String punchName = null;
        int punchMode = 0;
        if (StringHelper.isNotEmpty(macAddress)) {
            punchMode = 1;
            AttendanceWifiSetQueryForm wifiQUeryForm = new AttendanceWifiSetQueryForm();
            wifiQUeryForm.setWhetherPage(true);
            wifiQUeryForm.setPageNum(1);
            wifiQUeryForm.setPageSize(1000);
            wifiQUeryForm.setMacAddress(macAddress);
            List<AttendanceWifiSetVO> attendanceWifiSetVOS = attendanceWifiSetBiz.findAttendanceWifiSets(wifiQUeryForm);
            if (attendanceWifiSetVOS!=null && !attendanceWifiSetVOS.isEmpty()) {//不在考勤范围内
                AttendanceWifiSetVO attendanceWifiSetVO = attendanceWifiSetVOS.get(0);
                orgId = attendanceWifiSetVO.getOrgId();
                punchName = attendanceWifiSetVO.getWifiName();
            }
        } else {
            AttendanceAddressSetQueryForm addressQueryForm = new AttendanceAddressSetQueryForm();
            addressQueryForm.setWhetherPage(true);
            addressQueryForm.setPageNum(1);
            addressQueryForm.setPageSize(1000);
            List<AttendanceAddressSetVO> attendanceAddressSetVOS = attendanceAddressSetBiz.findAttendanceAddressSets(addressQueryForm);
            double lon1 = Double.parseDouble(longitude);
            double lat1 = Double.parseDouble(latitude);
            attendanceAddressSetVOS = attendanceAddressSetVOS.stream().filter(attendanceAddressSetVO -> {
                double lon2 = Double.parseDouble(attendanceAddressSetVO.getLongitude());
                double lat2 = Double.parseDouble(attendanceAddressSetVO.getLatitude());
                double attendanceRange = attendanceAddressSetVO.getAttendanceRange();
                double distance =distanceByLongNLat(lon1,lat1,lon2,lat2);
                if (distance-attendanceRange > 0) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (attendanceAddressSetVOS!=null && !attendanceAddressSetVOS.isEmpty()) {
                AttendanceAddressSetVO attendanceAddressSetVO = attendanceAddressSetVOS.get(0);
                orgId = attendanceAddressSetVO.getOrgId();
                punchName = attendanceAddressSetVO.getAttendanceAddress();
                // 考勤地址
                result.setAttendanceAddressId(attendanceAddressSetVO.getId());
            }
        }
        result.setPunchName(punchName);
        result.setPunchMode(punchMode);
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setUserId(userId);
        recordQueryForm.setPunchDate(new Date(System.currentTimeMillis()));
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
        if (attendancePunchRecordVOS!=null && !attendancePunchRecordVOS.isEmpty()) {
            setOrgName(attendancePunchRecordVOS);
            setCurItemInfo(attendancePunchRecordVOS, result);
            result.setAttendancePunchItemVOS(findPunchItem(attendancePunchRecordVOS));
        } else {//未排班
            result.setPunchStatus((byte) 6);
            return result;
        }
        Byte source = result.getSource();
        if (orgId==null || (AttendanceSourceEnum.FIELD.getCode()!=source&&!orgId.equals(result.getOrgId()))) {//不在考勤范围内
            result.setPunchStatus((byte) 5);
        }
        return result;
    }

    /**
     * 装配组织名称
     * @param attendancePunchRecordVOS
     */
    private void setOrgName(List<AttendancePunchRecordVO> attendancePunchRecordVOS) {
        List<Integer> orgIds = new ArrayList<>(2);
        attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
            Integer orgId = attendancePunchRecordVO.getOrgId();
            if (orgId!=null && !orgIds.contains(orgId)) {
                orgIds.add(orgId);
            }
        });
        if (!orgIds.isEmpty()) {
            List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
            organizationInfoDetails.forEach(organizationInfoDetail->{
                attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
                    String orgName = organizationInfoDetail.getName();
                    if (organizationInfoDetail.getId().equals(attendancePunchRecordVO.getOrgId())) {
                        attendancePunchRecordVO.setOrgName(orgName);
                        return;
                    }
                });
            });
        }
    }

    /**
     * 查找给定列表中上班打卡和下班打卡的打卡项目
     * @param attendancePunchRecordVOS
     * @return
     */
    private List<AttendancePunchItemVO> findPunchItem(List<AttendancePunchRecordVO> attendancePunchRecordVOS) {
        List<AttendancePunchItemVO> result = new ArrayList<>();
        AttendancePunchRecordVO onDutyPunchRecord = attendancePunchRecordVOS.get(0);
        AttendancePunchRecordVO offDutyPunchRecord = attendancePunchRecordVOS.get(1);
        Byte source = onDutyPunchRecord.getSource();

        AttendancePunchItemVO onDutyPunchItem = new AttendancePunchItemVO();
        AttendancePunchItemVO offDutyPunchItem = new AttendancePunchItemVO();
        onDutyPunchItem.setOrgName(onDutyPunchRecord.getOrgName());
        onDutyPunchItem.setId(onDutyPunchRecord.getId());
        onDutyPunchItem.setStartTime(onDutyPunchRecord.getStartTime());
        onDutyPunchItem.setEndTime(onDutyPunchRecord.getEndTime());
        Byte condition = onDutyPunchRecord.getSource();
        if (source==AttendanceSourceEnum.LEAVE_BYDAY.getCode() || source==AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode()) {
            condition = 2;
        }
        onDutyPunchItem.setCondition(condition);
        boolean isNext = false;
        if (onDutyPunchRecord.getIsPunch()==AttendanceIsPunchEnum.PUNCHED.getCode()) {//已打卡
            onDutyPunchItem.setPunchType(onDutyPunchRecord.getPunchType());
            byte punchMode = 0;
            if (onDutyPunchRecord.getWifiMacAddress()!=null) {
                punchMode = 1;
            }
            onDutyPunchItem.setPunchMode(punchMode);
            onDutyPunchItem.setPunchType(onDutyPunchRecord.getPunchType());
            onDutyPunchItem.setPunchName(onDutyPunchRecord.getPunchAddress());
            onDutyPunchItem.setPunchStatus(onDutyPunchRecord.getPunchStatus());
            onDutyPunchItem.setPunchTime(onDutyPunchRecord.getPunchTime());
        } else {// 未打卡
            isNext = true;
            onDutyPunchItem.setPunchType(AttendanceTypeEnum.ONDUTY.getCode());
        }
        onDutyPunchItem.setIsNext(isNext);

        offDutyPunchItem.setOrgName(offDutyPunchRecord.getOrgName());
        offDutyPunchItem.setId(offDutyPunchRecord.getId());
        offDutyPunchItem.setStartTime(offDutyPunchRecord.getStartTime());
        offDutyPunchItem.setEndTime(offDutyPunchRecord.getEndTime());
        offDutyPunchItem.setIsNext(!isNext);
        condition = offDutyPunchRecord.getSource();
        if (source==AttendanceSourceEnum.LEAVE_BYDAY.getCode() || source==AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode()) {
            condition = 2;
        }
        offDutyPunchItem.setCondition(condition);
        if (offDutyPunchRecord.getIsPunch()==AttendanceIsPunchEnum.PUNCHED.getCode()) {//已打卡
            byte punchMode = 0;
            if (offDutyPunchRecord.getWifiMacAddress()!=null) {
                punchMode = 1;
            }
            offDutyPunchItem.setPunchMode(punchMode);
            offDutyPunchItem.setPunchType(offDutyPunchRecord.getPunchType());
            onDutyPunchItem.setPunchName(onDutyPunchRecord.getPunchAddress());
            offDutyPunchItem.setPunchStatus(offDutyPunchRecord.getPunchStatus());
            offDutyPunchItem.setPunchTime(offDutyPunchRecord.getPunchTime());
        } else {// 未打卡
            offDutyPunchItem.setPunchType(AttendanceTypeEnum.OFFDUTY.getCode());
        }
        result.add(onDutyPunchItem); //上班打卡项目
        result.add(offDutyPunchItem); // 下班打卡项目
        return result;
    }

    /**
     * 装配即将打卡或正在打卡的项目信息
     * @param attendancePunchRecordVOS
     * @param result
     */
    private void setCurItemInfo(List<AttendancePunchRecordVO> attendancePunchRecordVOS, AttendancePunchInfoVO result) {
        Date now = new Date(System.currentTimeMillis());
        AttendancePunchRecordVO punchItem = attendancePunchRecordVOS.get(0);
        Date startTime = punchItem.getStartTime();
        Date endTime = punchItem.getEndTime();
        Byte isPunch = punchItem.getIsPunch();
        Byte punchStatus = punchItem.getPunchStatus();
        if (AttendanceStatusEnum.UNVALID_PUNCH.getCode() != punchStatus) {
            if (isPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {
                if (now.after(startTime)) {
                    punchStatus = AttendanceStatusEnum.LATER_PUNCH.getCode();//迟到打卡
                } else {
                    punchStatus = AttendanceStatusEnum.ONDUTY_PUNCH.getCode();//上班打卡
                }
            } else {
                punchItem = attendancePunchRecordVOS.get(attendancePunchRecordVOS.size()-1);
                if (now.before(endTime)) {
                    punchStatus = AttendanceStatusEnum.EARLY_PUNCH.getCode();//早退打卡
                } else {
                    punchStatus = AttendanceStatusEnum.OFFDUTY_PUNCH.getCode();//下班打卡
                }
            }
        }
        result.setSource(punchItem.getSource());
        result.setId(punchItem.getId());
        result.setOrgId(punchItem.getOrgId());
        result.setPunchStatus(punchStatus);
        result.setOrgName(punchItem.getOrgName());
        result.setStartTime(punchItem.getStartTime());
        result.setEndTime(punchItem.getEndTime());
        result.setName(punchItem.getName());
    }

    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @param long2 第二点经度
     * @param lat2  第二点纬度
     * @return 返回距离 单位：米
     */
    public static double distanceByLongNLat(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137;//地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    /**
     * 考勤打卡
     * @param attendancePunchRecordForm 考勤打卡记录修改模型
     */
    public void punch(AttendancePunchRecordForm attendancePunchRecordForm) {
        Date now = new Date(System.currentTimeMillis());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        // 打卡：1、无效卡，2-打卡
        AttendancePunchRecord attendancePunchRecord = new AttendancePunchRecord();
        BeanUtils.copyProperties(attendancePunchRecordForm, attendancePunchRecord);
        Byte punchStatus = attendancePunchRecord.getPunchStatus();
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setPunchDate(now);
        queryForm.setUserId(userId);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(queryForm);
        if (attendancePunchRecordVOS==null || attendancePunchRecordVOS.isEmpty()) {
            throw new ClientServiceException("打卡失败，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        AttendancePunchRecordVO oldPunchRecord = null;
        Byte onDutyIspunch = null;
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            if (attendancePunchRecordVO.getPunchType()==AttendanceTypeEnum.ONDUTY.getCode()) {
                onDutyIspunch = attendancePunchRecordVO.getIsPunch();
            }
            if (attendancePunchRecordVO.getId().equals(attendancePunchRecord.getId())) {
                oldPunchRecord = attendancePunchRecordVO;
            }
        }
        if (oldPunchRecord == null) {
            throw new ClientServiceException("打卡失败，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        if (onDutyIspunch==AttendanceIsPunchEnum.UNPUNCH.getCode() && oldPunchRecord.getPunchType()==AttendanceTypeEnum.OFFDUTY.getCode()) {
            throw new ClientServiceException("未打上班卡，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        Byte oldPunchStatus = oldPunchRecord.getPunchStatus();
        if ((AttendanceStatusEnum.ONDUTY_PUNCH.getCode()==punchStatus||AttendanceStatusEnum.LATER_PUNCH.getCode()==punchStatus)
                && (AttendanceStatusEnum.ONDUTY_PUNCH.getCode()==oldPunchStatus||AttendanceStatusEnum.LATER_PUNCH.getCode()==oldPunchStatus)) {// 上班更新不允许
            throw new ClientServiceException("上班卡已打，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        attendancePunchRecord.setIsPunch(AttendanceIsPunchEnum.PUNCHED.getCode());
        attendancePunchRecord.setUptTime(now);
        attendancePunchRecord.setUptId(userId);
        mapper.updateByPrimaryKeySelective(attendancePunchRecord);
    }

    /**
     * 查询打卡日历中指定日期下的员工考勤打卡列表
     *
     * @param date 日期
     * @return
     */
    public AttendancePunchCalendarInfoVO punchRecordByDate(Date date) {
        AttendancePunchCalendarInfoVO result = new AttendancePunchCalendarInfoVO();
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        // 打卡记录
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setPunchDate(date);
        queryForm.setUserId(userId);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordList(queryForm);
        setOrgName(attendancePunchRecordVOS);

        AttendancePunchRecordVO firstPunchRecord = null;
        AttendancePunchRecordVO lastPunchRecord = null;
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            if (attendancePunchRecordVO.getPunchType()==AttendanceTypeEnum.ONDUTY.getCode()) {
                firstPunchRecord = attendancePunchRecordVO;
            } else {
                lastPunchRecord = attendancePunchRecordVO;
            }
        }
        List<AttendancePunchItemVO> attendancePunchItemVOS = new ArrayList<>(2);
        List<AttendancePunchRecordVO> attendancePunchRecordVOList = new ArrayList<>(2);
        if (attendancePunchRecordVOS!=null && !attendancePunchRecordVOS.isEmpty()) {
            attendancePunchRecordVOList = employeeScheduleList(firstPunchRecord, lastPunchRecord);
            attendancePunchItemVOS.add(createPunchItem(firstPunchRecord));
            attendancePunchItemVOS.add(createPunchItem(lastPunchRecord));
        }
//        long workLen = computeWorkLen(firstPunchRecord,lastPunchRecord);
//        result.setWorkLength(workLen);
        result.setAttendancePunchRecordVOS(attendancePunchRecordVOList);
        result.setAttendancePunchItemVOS(attendancePunchItemVOS);
        return result;
    }

    /**
     * 排班信息列表
     * @param firstPunchRecord
     * @param lastPunchRecord
     * @return
     */
    private List<AttendancePunchRecordVO> employeeScheduleList(AttendancePunchRecordVO firstPunchRecord, AttendancePunchRecordVO lastPunchRecord) {
        List<AttendancePunchRecordVO> result = new ArrayList<>();
        if (firstPunchRecord!=null && lastPunchRecord!=null) {
            if (firstPunchRecord.getSourceId().equals(lastPunchRecord.getSourceId())) {// 全天班
                AttendancePunchRecordVO attendancePunchRecordVO = new AttendancePunchRecordVO();
                attendancePunchRecordVO.setName(firstPunchRecord.getName());

                attendancePunchRecordVO.setOrgName(firstPunchRecord.getOrgName());
                attendancePunchRecordVO.setStartTime(firstPunchRecord.getStartTime());
                attendancePunchRecordVO.setEndTime(firstPunchRecord.getEndTime());
                result.add(attendancePunchRecordVO);
            } else {
                AttendancePunchRecordVO firstRecord = new AttendancePunchRecordVO();
                firstRecord.setName(firstPunchRecord.getName());
                firstRecord.setOrgName(firstPunchRecord.getOrgName());
                firstRecord.setStartTime(firstPunchRecord.getStartTime());
                firstRecord.setEndTime(firstPunchRecord.getEndTime());
                result.add(firstRecord);
                AttendancePunchRecordVO lastRecord = new AttendancePunchRecordVO();
                lastRecord.setName(lastPunchRecord.getName());
                lastRecord.setOrgName(lastPunchRecord.getOrgName());
                lastRecord.setStartTime(lastPunchRecord.getStartTime());
                lastRecord.setEndTime(lastPunchRecord.getEndTime());
                result.add(lastRecord);
            }
        } else if (firstPunchRecord != null) {
            AttendancePunchRecordVO firstRecord = new AttendancePunchRecordVO();
            firstRecord.setName(firstPunchRecord.getName());
            firstRecord.setOrgName(firstPunchRecord.getOrgName());
            firstRecord.setStartTime(firstPunchRecord.getStartTime());
            firstRecord.setEndTime(firstPunchRecord.getEndTime());
            result.add(firstRecord);
        } else if (lastPunchRecord != null) {
            AttendancePunchRecordVO lastRecord = new AttendancePunchRecordVO();
            lastRecord.setName(lastPunchRecord.getName());
            lastRecord.setOrgName(lastPunchRecord.getOrgName());
            lastRecord.setStartTime(lastPunchRecord.getStartTime());
            lastRecord.setEndTime(lastPunchRecord.getEndTime());
            result.add(lastRecord);
        }
        return result;
    }

    /**
     * 计算工作时长
     * @param firstPunchRecord
     * @param lastPunchRecord
     * @return
     */
    private long computeWorkLen(AttendancePunchRecordVO firstPunchRecord, AttendancePunchRecordVO lastPunchRecord) {
        if (firstPunchRecord == null || lastPunchRecord == null) {
            return 0;
        }
        Byte firstIsPunch = firstPunchRecord.getIsPunch();
        Byte lastIsPunch = lastPunchRecord.getIsPunch();
        if (firstIsPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {// 上班未打卡，则0;
            return 0;
        }
        Date punchTime = lastPunchRecord.getPunchTime();
        long startTime = 0;
        if (punchTime != null) {
            startTime = punchTime.getTime();
        }
        long length = startTime - firstPunchRecord.getPunchTime().getTime();
        punchTime = firstPunchRecord.getPunchTime();
        long endTime = 0;
        if (punchTime != null) {
            endTime = punchTime.getTime();
        }
        if (lastIsPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {// 下班未打卡，待确认
            length = firstPunchRecord.getEndTime().getTime() - endTime;
        }
        return DateUtil.micro2Min(length);
    }

    /**
     * 生成打卡记录
     * @param attendancePunchRecordVO
     * @return
     */
    private AttendancePunchItemVO createPunchItem(AttendancePunchRecordVO attendancePunchRecordVO) {
        AttendancePunchItemVO punchItem = new AttendancePunchItemVO();
        punchItem.setPunchTime(attendancePunchRecordVO.getPunchTime());
        punchItem.setPunchType(attendancePunchRecordVO.getPunchType());
        punchItem.setPunchName(attendancePunchRecordVO.getPunchAddress());
        punchItem.setPunchStatus(attendancePunchRecordVO.getPunchStatus());
        byte punchMode = 0;
        if (StringHelper.isNotEmpty(attendancePunchRecordVO.getWifiMacAddress())) {
            punchMode = 1;
        }
        punchItem.setOrgName(attendancePunchRecordVO.getOrgName());
        punchItem.setPunchMode(punchMode);
        Byte source = attendancePunchRecordVO.getSource();
        if (source==AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode()
                || source==AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {
            source = 2;
        }
        punchItem.setSource(source);
        punchItem.setStartTime(attendancePunchRecordVO.getStartTime());
        punchItem.setEndTime(attendancePunchRecordVO.getEndTime());
        punchItem.setItemName(attendancePunchRecordVO.getName());
        return punchItem;
    }

    /**
     * 根据年月查询员工考勤打卡日历。
     *
     * @param dateStr 该月的某一天，例如2020-11-01
     * @return
     */
    public List<AttendanceCalendarInfoVO> punchRecordCalendarByMonth(String dateStr) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        List<Date> dateList = DateUtil.getMonthFullDay(dateStr);
        List<AttendanceCalendarInfoVO> result = new ArrayList<>(dateList.size());
        Date firstDate = dateList.get(0);
        Date endDate = dateList.get(dateList.size()-1);
        // 休息班次
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setUserId(userId);
        employeeScheduleQueryForm.setType(REST);
        employeeScheduleQueryForm.setBetweenWorkDate(firstDate);
        employeeScheduleQueryForm.setAndWorkDate(endDate);
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);

        // 请假记录
        LeaveInfoQueryForm leaveInfoQueryForm = new LeaveInfoQueryForm();
        leaveInfoQueryForm.setBetweenStartDate(firstDate);
        leaveInfoQueryForm.setAndStartDate(endDate);
        leaveInfoQueryForm.setUserId(userId);


        // 打卡记录
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setUserId(userId);
        recordQueryForm.setBetweenDate(firstDate);
        recordQueryForm.setAndDate(endDate);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
        Date now = new Date(System.currentTimeMillis());
        Map<Date, Integer> unPunchCount = new HashMap<>();
        Map<Date, String> stateMap = new HashMap<>();
        attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
            Byte isPunch = attendancePunchRecordVO.getIsPunch();
            Date date = attendancePunchRecordVO.getPunchDate();
            Byte punchStatus = attendancePunchRecordVO.getPunchStatus();
            Byte source = attendancePunchRecordVO.getSource();
            String state = stateMap.get(date);
            String temp = null;
            if (isPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {
                Integer count = unPunchCount.get(date);
                if (count == null) {
                    count = 0;
                }
                unPunchCount.put(date, ++count);
                temp = "缺卡";
            } else {
                if (punchStatus==AttendanceStatusEnum.ONDUTY_PUNCH.getCode()
                        ||punchStatus==AttendanceStatusEnum.OFFDUTY_PUNCH.getCode()) {//正常
                    temp = "正常";
                } else if (punchStatus==AttendanceStatusEnum.LATER_PUNCH.getCode()) {
                    temp = "迟到";
                } else if (punchStatus==AttendanceStatusEnum.EARLY_PUNCH.getCode()) {
                    temp = "早退";
                }
            }
            if (source==AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode()
                    || source==AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {
                temp = "请假";
            }/* else if (source == AttendanceSourceEnum.FIELD.getCode()) {
                temp = "外勤";
            }*/
            if (state==null|| !"正常".equals(state)) {
                state = temp;
            }
            if (state != null) {
                stateMap.put(date, state);
            }
        });

        dateList.forEach(date->{
            byte rest = AttendanceStateEnum.REST.getCode();
            byte normal = AttendanceStateEnum.NORMAL.getCode();
            byte exception = AttendanceStateEnum.EXCEPTION.getCode();
            byte unknown = AttendanceStateEnum.UNKNOWN.getCode();
            AttendanceCalendarInfoVO attendanceCalendarInfoVO = new AttendanceCalendarInfoVO();
            attendanceCalendarInfoVO.setDate(date);
            if (date.compareTo(now) > 0) {
                attendanceCalendarInfoVO.setName("未开始");
                attendanceCalendarInfoVO.setType(unknown);
            } else {
                attendanceCalendarInfoVO.setName("未排班");
                attendanceCalendarInfoVO.setType(exception);
                employeeScheduleVOS.forEach(restVO -> {
                    Date workDate = restVO.getWorkDate();
                    if (date.compareTo(workDate) == 0) {
                        attendanceCalendarInfoVO.setName("休息");
                        attendanceCalendarInfoVO.setType(rest);
                    }
                });
            }
            String state = stateMap.get(date);
            if (StringHelper.isNotEmpty(state)) {
                switch (state) {
                    case "正常": {
                        attendanceCalendarInfoVO.setName("正常");
                        attendanceCalendarInfoVO.setType(normal);
                        break;
                    }
                    case "迟到": {
                        attendanceCalendarInfoVO.setName("迟到");
                        attendanceCalendarInfoVO.setType(exception);
                        break;
                    }
                    case "早退": {
                        attendanceCalendarInfoVO.setName("早退");
                        attendanceCalendarInfoVO.setType(exception);
                        break;
                    }
                    case "缺卡": {
                        attendanceCalendarInfoVO.setName("缺卡");
                        attendanceCalendarInfoVO.setType(exception);
                        break;
                    }
                    case "请假": {
                        attendanceCalendarInfoVO.setName("请假");
                        attendanceCalendarInfoVO.setType(exception);
                        break;
                    }
                    /*case "外勤": {
                        attendanceCalendarInfoVO.setName("外勤");
                        attendanceCalendarInfoVO.setType(exception);
                        break;
                    }*/
                    default:
                }
            }
            result.add(attendanceCalendarInfoVO);
        });
        return result;
    }

    /**
     * 根据年月查询员工考勤打卡月汇总。
     *
     * @param dateStr 该月的某一天，例如2020-11-01
     * @return
     */
    public AttendanceStatisticsVO punchRecordByMonth(String dateStr) {
        Date now = new Date(System.currentTimeMillis());
        AttendanceStatisticsVO result = new AttendanceStatisticsVO();
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        List<Date> dateList = DateUtil.getMonthFullDay(dateStr);
        Date firstDate = dateList.get(0);
        Date endDate = dateList.get(dateList.size()-1);
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setBetweenWorkDate(firstDate);
        employeeScheduleQueryForm.setAndWorkDate(endDate);
        employeeScheduleQueryForm.setUserId(userId);
        // 班次列表
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
        Map<Integer, EmployeeScheduleVO> restMap = new LinkedHashMap<>(employeeScheduleVOS.size());
        Map<Integer, EmployeeScheduleVO> workMap = new LinkedHashMap<>(employeeScheduleVOS.size());
        List<Integer> orgIds = new ArrayList<>();
        employeeScheduleVOS.forEach(employeeScheduleVO->{
            Integer orgId = employeeScheduleVO.getClinicId();
            if (!orgIds.contains(orgId)) {
                orgIds.add(orgId);
            }
            if (REST.equals(employeeScheduleVO.getType())) {
                restMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
            } else {
                workMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
            }
        });
        List<OrganizationInfoDetail> orgList = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
        Map<Integer, String> orgMap = new HashMap<>(orgList.size());
        orgList.forEach(organizationInfo -> {
            orgMap.put(organizationInfo.getId(), organizationInfo.getName());
        });

        // 打卡列表
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setUserId(userId);
        queryForm.setBetweenDate(firstDate);
        queryForm.setAndDate(endDate);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordWithScheduleIdList(queryForm);

        // 请假
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setUserId(userId);
        leaveQueryForm.setBetweenStartDate(firstDate);
        leaveQueryForm.setAndStartDate(endDate);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        List<AttendancePunchRecordVO> leaveStatisticsList = new ArrayList<>(leaveInfoVOS.size());
        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
            AttendancePunchRecordVO leaveStatistics = new AttendancePunchRecordVO();
            Date startTime = leaveInfoVO.getStartTime();
            Date endTime = leaveInfoVO.getEndTime();
            Integer vacationStatus = leaveInfoVO.getVacationStatus();
            if (vacationStatus == 0) {// 按班次请假
                leaveStatistics.setPunchDate(leaveInfoVO.getStartDate());
                EmployeeScheduleVO employeeScheduleVO = workMap.get(leaveInfoVO.getScheduleId());
                if (employeeScheduleVO != null) {
                    leaveStatistics.setName("按班次请假");
                    Integer orgId = employeeScheduleVO.getClinicId();
                    leaveStatistics.setOrgName(orgMap.get(orgId));
                }
                long diff = endTime.getTime()-startTime.getTime();
                leaveStatistics.setMinutes(DateUtil.micro2HourMin(diff));
                leaveStatisticsList.add(leaveStatistics);
            } else {// 按天请假
                if (endTime.after(endDate)) {
                    endTime = endDate;
                }
                Integer orgId = leaveInfoVO.getOrgId();
                List<Date> dates = DateUtil.getBetweenDate(startTime, endTime);
                dates.forEach(date -> {
                    leaveStatistics.setOrgName(orgMap.get(orgId));
                    leaveStatistics.setName("按天请假");
                    leaveStatistics.setPunchDate(date);
                    leaveStatistics.setMinutes(DAY_LEAVE_MINUTE);
                    leaveStatisticsList.add(leaveStatistics);
                });
            }
        }

        int attendanceNum = 0;
        List<AttendancePunchRecordVO> lateStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> earlyStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> workOvertimeStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> unpunchStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> invalidStatisticsList = new ArrayList<>(30);
        Map<Integer, List<AttendancePunchRecordVO>> workOvertimeMap = new HashMap<>();
        Map<Integer, List<AttendancePunchRecordVO>> fieldMap = new HashMap<>();
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            Byte isPunch = attendancePunchRecordVO.getIsPunch();
            Date startTime = attendancePunchRecordVO.getStartTime();
            Date endTime = attendancePunchRecordVO.getEndTime();
            Integer orgId = attendancePunchRecordVO.getOrgId();
            String orgName = orgMap.get(orgId);
            Byte source = attendancePunchRecordVO.getSource();
            Integer sourceId = attendancePunchRecordVO.getSourceId();
            if (isPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()
                    && source!=AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {
                attendancePunchRecordVO.setOrgName(orgName);
                unpunchStatisticsList.add(attendancePunchRecordVO);
            } else {
                Date punchTime = attendancePunchRecordVO.getPunchTime();
                Byte punchStatus = attendancePunchRecordVO.getPunchStatus();
                if (source==AttendanceSourceEnum.WORK_SCHEDULE.getCode()) {
                    switch (punchStatus) {
                        case 0: {
                            attendanceNum++;
                            break;
                        }
                        case 1: {
                            attendanceNum++;
                            long diff = punchTime.getTime() - startTime.getTime();
                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
                            attendancePunchRecordVO.setOrgName(orgName);
                            lateStatisticsList.add(attendancePunchRecordVO);
                            break;
                        }
                        case 2: {
                            attendanceNum++;
                            break;
                        }
                        case 3: {
                            attendanceNum++;
                            long diff = endTime.getTime()-punchTime.getTime();
                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
                            attendancePunchRecordVO.setOrgName(orgName);
                            earlyStatisticsList.add(attendancePunchRecordVO);
                            break;
                        }
                        case 4: {
                            attendancePunchRecordVO.setOrgName(orgName);
                            invalidStatisticsList.add(attendancePunchRecordVO);
                            break;
                        }
                        default:
                    }
                } else if (source==AttendanceSourceEnum.REST_SCHEDULE.getCode()) {
                    attendancePunchRecordVO.setOrgName(orgName);
                    unpunchStatisticsList.add(attendancePunchRecordVO);
                } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                    List<AttendancePunchRecordVO> list = workOvertimeMap.get(sourceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(attendancePunchRecordVO);
                    workOvertimeMap.put(sourceId,list);
                }
            }
            if (source==AttendanceSourceEnum.FIELD.getCode()) {
                List<AttendancePunchRecordVO> list = fieldMap.get(sourceId);
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (isPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {
                    attendancePunchRecordVO = new AttendancePunchRecordVO();
                    attendancePunchRecordVO.setStartTime(now);
                    attendancePunchRecordVO.setPunchTime(now);
                    attendancePunchRecordVO.setEndTime(now);
                }
                list.add(attendancePunchRecordVO);
                fieldMap.put(sourceId, list);
            }
        }

        //加班
        WorkOvertimeInfoQueryForm workOvertimeQueryForm = new WorkOvertimeInfoQueryForm();
        workOvertimeQueryForm.setStartTime(firstDate);
        workOvertimeQueryForm.setEndTime(endDate);
        workOvertimeQueryForm.setUserId(userId);
        workOvertimeQueryForm.setApprpvalStatus(1);
        List<WorkOvertimeInfoVO> workOvertimeInfoVOS = workOvertimeInfoBiz.findWorkOvertimeInfoList(workOvertimeQueryForm);
        workOvertimeInfoVOS.forEach(workOvertimeInfoVO -> {
            Integer id = workOvertimeInfoVO.getId();
            Integer orgId = workOvertimeInfoVO.getCompanyId();
            AttendancePunchRecordVO item = new AttendancePunchRecordVO();
            item.setStartTime(workOvertimeInfoVO.getStartTime());
            item.setEndTime(workOvertimeInfoVO.getEndTime());
            item.setOrgName(orgMap.get(orgId));
            item.setPunchDate(workOvertimeInfoVO.getWorkDate());
            List<AttendancePunchRecordVO> list = workOvertimeMap.get(id);
            long diff = 0;
            if (list!=null && !list.isEmpty()) {
                AttendancePunchRecordVO first = list.get(0);
                AttendancePunchRecordVO last = list.get(1);
                if (first!=null && last!=null) {//全天班
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = last.getPunchTime();
                    if (endTime.after(last.getEndTime())) {
                        endTime = last.getEndTime();
                    }
                    diff = endTime.getTime() - startTime.getTime();
                } else if (first != null) {//半天班
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = first.getEndTime();
                    if (first.getPunchType()==AttendanceTypeEnum.OFFDUTY.getCode()) {
                        endTime = first.getStartTime();
                    }
                    diff = Math.abs(endTime.getTime()-startTime.getTime());
                }
            }
            item.setMinutes(DateUtil.micro2HourMin(diff));
            workOvertimeStatisticsList.add(item);
        });

        // 外勤
        FieldInfoQueryForm fieldQueryForm = new FieldInfoQueryForm();
        fieldQueryForm.setUserId(userId);
        fieldQueryForm.setBetweenDate(firstDate);
        fieldQueryForm.setAndDate(endDate);
        fieldQueryForm.setApprpvalStatus(1);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldQueryForm);
        List<Integer> workOvertime = new ArrayList<>();
        List<AttendancePunchRecordVO> fieldStatisticsList = new ArrayList<>(fieldInfoVOS.size());
        fieldInfoVOS.forEach(fieldInfoVO -> {
            AttendancePunchRecordVO item = new AttendancePunchRecordVO();
            Integer id = fieldInfoVO.getId();
            Integer orgId = fieldInfoVO.getCompanyId();
            Date sTime = fieldInfoVO.getStartTime();
            Date eTime = fieldInfoVO.getEndTime();
            item.setStartTime(sTime);
            item.setEndTime(eTime);
            item.setOrgName(orgMap.get(orgId));
            item.setPunchDate(fieldInfoVO.getStartTime());
            List<AttendancePunchRecordVO> list = fieldMap.get(id);
            long diff = eTime.getTime()-sTime.getTime();
            if (list!=null && !list.isEmpty()) {
                AttendancePunchRecordVO first = list.get(0);
                AttendancePunchRecordVO last = list.get(1);
                if (first!=null && last!=null) {//全天班
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = last.getPunchTime();
                    if (endTime.after(last.getEndTime())) {
                        endTime = last.getEndTime();
                    }
                    diff = endTime.getTime() - startTime.getTime();
                } else if (first != null) {//半天班
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = first.getEndTime();
                    if (first.getPunchType()==AttendanceTypeEnum.OFFDUTY.getCode()) {
                        endTime = first.getStartTime();
                    }
                    diff = Math.abs(endTime.getTime()-startTime.getTime());
                }
            }
            item.setMinutes(DateUtil.micro2HourMin(diff));
            fieldStatisticsList.add(item);
        });

        List<AttendancePunchRecordVO> restStatisticeList = new ArrayList<>(restMap.size());
        restMap.forEach((id, employeeScheduleVO) ->{
            AttendancePunchRecordVO restStatistics = new AttendancePunchRecordVO();
            Integer orgId = employeeScheduleVO.getClinicId();
            String orgName = orgMap.get(orgId);
            restStatistics.setOrgName(orgName);
            restStatistics.setPunchDate(employeeScheduleVO.getWorkDate());
            restStatisticeList.add(restStatistics);
        });
        Collections.sort(leaveStatisticsList, Comparator.comparing(AttendancePunchRecordVO::getPunchDate));
        result.setWorkOvertimeNum(workOvertime.size());
        result.setUnpunchNum(unpunchStatisticsList.size());
        result.setRestNum(restStatisticeList.size());
        result.setFieldNum(fieldStatisticsList.size());
        result.setLeaveNum(leaveStatisticsList.size());
        result.setLateNum(lateStatisticsList.size());
        result.setAttendanceNum(attendanceNum);
        result.setEarlyNum(earlyStatisticsList.size());
        result.setInvalidNum(invalidStatisticsList.size());
        result.setRestStatisticsList(restStatisticeList);
        result.setLateStatisticsList(lateStatisticsList);
        result.setEarlyStatisticsList(earlyStatisticsList);
        result.setLeaveStatisticsList(leaveStatisticsList);
        result.setWorkOvertimeStatisticsList(workOvertimeStatisticsList);
        result.setInvalidStatisticsList(invalidStatisticsList);
        result.setUnpunchStatisticsList(unpunchStatisticsList);
        result.setFieldStatisticsList(fieldStatisticsList);
        return result;
    }

    /**
     * 根据条件查询考勤汇总
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceStatisticsVO> statisticsPunchRecord(AttendanceStatisticsQueryForm queryForm) {
        String name = queryForm.getEmployeeName();
        Byte type = queryForm.getType();
        setQueryFormDate(queryForm);
        Set<Integer> userIds = new HashSet<>();
        Set<String> userOrgIds = new HashSet<>();
        // 打卡记录
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setBetweenDate(queryForm.getBetweenDate());
        recordQueryForm.setAndDate(queryForm.getAndDate());
        recordQueryForm.setOrgId(queryForm.getOrgId());
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordWithScheduleIdList(recordQueryForm);
        List<AttendancePunchDateVO> attendancePunchDateVOS = punchRecord2PunchDate(attendancePunchRecordVOS);
        Table<Integer,Integer, Long> workDateMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> workDateOvertimeMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> workDateOvertime30MinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> restDateOvertimeMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> restDateOverCounts = HashBasedTable.create();
        Table<Integer,Integer, Long> fieldMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> fieldCounts = HashBasedTable.create();
        Table<Integer,Integer, Long> leaveMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> leaveCounts = HashBasedTable.create();
        Table<Integer,Integer, Integer> laterNumMap = HashBasedTable.create();
        Table<Integer,Integer, Long> laterMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> earlyNumMap = HashBasedTable.create();
        Table<Integer,Integer, Long> earlyMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> unpunchNumMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> invalidNumMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> attendancNumMap = HashBasedTable.create();
        Table<Integer,Integer, Boolean> isFullMap = HashBasedTable.create();

        //上班班次
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setBetweenWorkDate(queryForm.getBetweenDate());
        employeeScheduleQueryForm.setAndWorkDate(queryForm.getAndDate());
        employeeScheduleQueryForm.setType("上班");
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
        Map<Integer, EmployeeScheduleVO> employeeScheduleVOMap = new HashMap<>(employeeScheduleVOS.size());
        employeeScheduleVOS.forEach(employeeScheduleVO -> employeeScheduleVOMap.put(employeeScheduleVO.getId(), employeeScheduleVO));
        //请假
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setBetweenStartDate(queryForm.getBetweenDate());
        leaveQueryForm.setAndStartDate(queryForm.getAndDate());
        leaveQueryForm.setApprpvalStatus(1);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        Table<Integer,Integer, List<LeaveInfoVO>> leaveInfoMap = HashBasedTable.create();
        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
            Integer vacationStatus = leaveInfoVO.getVacationStatus();
            Integer userId = leaveInfoVO.getUserId();
            Integer orgId = leaveInfoVO.getOrgId();
            if (orgId == null) {
                EmployeeScheduleVO employeeScheduleVO = employeeScheduleVOMap.get(leaveInfoVO.getScheduleId());
                if (employeeScheduleVO != null) {
                    orgId = employeeScheduleVO.getClinicId();
                }
            }

            long diff;
            if (vacationStatus == 1) { // 按班次请假
                diff = leaveInfoVO.getEndTime().getTime() - leaveInfoVO.getStartTime().getTime();
                List<LeaveInfoVO> list = leaveInfoMap.get(userId, orgId);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(leaveInfoVO);
                leaveInfoMap.put(userId, orgId, list);
            } else { // 按天请假
                int days;
                try {
                    days = DateUtil.daysBetween(leaveInfoVO.getStartDate(), leaveInfoVO.getEndDate());
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                }
                diff = DAY_LEAVE_MILLSEC * days;
            }
            userIds.add(userId);
            userOrgIds.add(userId + "," + orgId);
            incrMinute(leaveMinuteMap, userId, orgId, diff);
            incrNum(leaveCounts, userId, orgId);
            isFullMap.put(userId, orgId, false);
        }

        Set<Integer> fieldIds = new HashSet<>();
        for (AttendancePunchDateVO attendancePunchDateVO : attendancePunchDateVOS) {
            Integer userId = attendancePunchDateVO.getUserId();
            userIds.add(userId);
            Date date = attendancePunchDateVO.getPunchDate();
            Integer onDutyOrgId = attendancePunchDateVO.getOnDutyOrgId();
            Byte onDutyStatus = attendancePunchDateVO.getOnDutyStatus();
            Date onPunchTime = attendancePunchDateVO.getOnDutyPunchTime();
            Date onStartTime = attendancePunchDateVO.getOnDutyStartTime();
            Date onEndTime = attendancePunchDateVO.getOnDutyEndTime();
            Integer offDutyOrgId = attendancePunchDateVO.getOffDutyOrgId();
            Date offStartTime = attendancePunchDateVO.getOffDutyStartTime();
            Date offPunchTime = attendancePunchDateVO.getOffDutyPunchTime();
            Date offEndTime = attendancePunchDateVO.getOffDutyEndTime();
            if (onDutyOrgId != null) {
                userOrgIds.add(userId + "," + onDutyOrgId);
            }
            if (offDutyOrgId != null) {
                userOrgIds.add(userId + "," + offDutyOrgId);
            }
            Byte workOvertime = attendancePunchDateVO.getWorkOvertime();
            Byte field = attendancePunchDateVO.getField();
            // 上班打卡
            boolean hasAttendance = false;
            switch (onDutyStatus) {
                case 0: {
                    hasAttendance = true;
                    long diff = computeWithoutLeave(date, onEndTime, onStartTime, userId, onDutyOrgId, leaveInfoMap);
                    incrMinute(workDateMinuteMap, userId, onDutyOrgId, diff);
                    break;
                }
                case 1: {
                    hasAttendance = true;
                    if (workOvertime==0 && field==0) {
                        incrNum(laterNumMap, userId, onDutyOrgId);
                        incrMinute(laterMinuteMap, userId, onDutyOrgId, onPunchTime.getTime()-onStartTime.getTime());
                        isFullMap.put(userId, onDutyOrgId, false);
                    }
                    long diff = computeWithoutLeave(date, onEndTime, onPunchTime, userId, onDutyOrgId, leaveInfoMap);
                    incrMinute(workDateMinuteMap, userId, onDutyOrgId, diff);
                    break;
                }
                case 4: {
                    incrNum(invalidNumMap, userId, onDutyOrgId);
                    isFullMap.put(userId, onDutyOrgId, false);
                    break;
                }
                case 5: {
                    if (workOvertime==0 && field==0) {
                        incrNum(unpunchNumMap, userId, onDutyOrgId);
                        isFullMap.put(userId, onDutyOrgId, false);
                    }
                    break;
                }
                default:
            }
            if (hasAttendance) {
                incrNum(attendancNumMap, userId, onDutyOrgId);
                hasAttendance = false;
            }
            // 下班打卡
            Byte offDutyStatus = attendancePunchDateVO.getOffDutyStatus();
            switch (offDutyStatus) {
                case 2:{
                    hasAttendance = true;
                    long diff = computeWithoutLeave(date, offEndTime, offStartTime, userId, offDutyOrgId, leaveInfoMap);
                    incrMinute(workDateMinuteMap, userId, offDutyOrgId, diff);
                    diff = offPunchTime.getTime() - offEndTime.getTime();
                    if (workOvertime==0 && field==0 && DateUtil.micro2Min(diff)>=1) {
                        incrMinute(workDateOvertimeMinuteMap, userId, offDutyOrgId, diff);
                        if (DateUtil.micro2Min(diff) >= 30) {
                            incrMinute(workDateOvertime30MinuteMap, userId, offDutyOrgId, diff);
                        }
                    }
                    break;
                }
                case 3:{
                    hasAttendance = true;
                    if (workOvertime==0 && field==0) {
                        isFullMap.put(userId, offDutyOrgId, false);
                        incrNum(earlyNumMap, userId, offDutyOrgId);
                        incrMinute(earlyMinuteMap, userId, offDutyOrgId, offEndTime.getTime()-offPunchTime.getTime());
                    }
                    long diff = computeWithoutLeave(date, offPunchTime, offStartTime, userId, offDutyOrgId, leaveInfoMap);
                    incrMinute(workDateMinuteMap, userId, offDutyOrgId, diff);
                    break;
                }
                case 4:{
                    incrNum(invalidNumMap, userId, offDutyOrgId);
                    isFullMap.put(userId, offDutyOrgId, false);
                    break;
                }
                case 5:{
                    if (workOvertime==0 && field==0) {
                        incrNum(unpunchNumMap, userId, offDutyOrgId);
                        isFullMap.put(userId, offDutyOrgId, false);
                    }
                    break;
                }
                default:
            }
            if (hasAttendance) {
                incrNum(attendancNumMap, userId, offDutyOrgId);
            }
            switch (workOvertime) {
                case 1: {// 上班加班
                    if (onDutyStatus != 5) {
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        long diff = onEndTime.getTime() - startTime.getTime();
                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
                        incrNum(restDateOverCounts, userId, onDutyOrgId);
                    }
                    break;
                }
                case 2: {// 下班加班
                    if (offDutyStatus != 5) {
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - offStartTime.getTime();
                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
                        incrNum(restDateOverCounts, userId, onDutyOrgId);
                    }
                    break;
                }
                case 3: {// 上班下班同一加班
                    if (onDutyStatus!=5 && offDutyStatus!=5) {
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - startTime.getTime();
                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
                        incrNum(restDateOverCounts, userId, onDutyOrgId);
                    }
                    break;
                }
                case 4: {// 上班下班两个不同加班
                    if (onDutyStatus != 5) {
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        long diff = onEndTime.getTime() - startTime.getTime();
                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
                        incrNum(restDateOverCounts, userId, onDutyOrgId);
                    }
                    if (offDutyStatus != 5) {
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - offStartTime.getTime();
                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
                        incrNum(restDateOverCounts, userId, onDutyOrgId);
                    }
                    break;
                }
            }
            Integer onSourceId = attendancePunchDateVO.getOnDutySourceId();
            Integer offSourceId = attendancePunchDateVO.getOffDutySourceId();
            switch (field) {
                case 1: {// 仅上班外勤
                    if (onDutyStatus != 5) {
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        long diff = onEndTime.getTime() - startTime.getTime();
                        incrMinute(fieldMinuteMap, userId, onDutyOrgId, diff);
                        incrNum(fieldCounts, userId, onDutyOrgId);
                        fieldIds.add(onSourceId);
                        fieldIds.add(offSourceId);
                    }
                    break;
                }
                case 2: {// 仅下班外勤
                    if (offDutyStatus != 5) {
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - offStartTime.getTime();
                        incrMinute(fieldMinuteMap, userId, offDutyOrgId, diff);
                        incrNum(fieldCounts, userId, offDutyOrgId);
                        fieldIds.add(onSourceId);
                        fieldIds.add(offSourceId);
                    }
                    break;
                }
                case 3: {// 上班下班同一个外勤
                    if (onDutyStatus!=5 && offDutyStatus!=5) {
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - startTime.getTime();
                        incrMinute(fieldMinuteMap, userId, onDutyOrgId, diff);
                        incrNum(fieldCounts, userId, onDutyOrgId);
                        fieldIds.add(onSourceId);
                        fieldIds.add(offSourceId);
                    }
                    break;
                }
                case 4: {// 上班下班两个不同的外勤
                    if (onDutyStatus != 5) {
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        long diff = onEndTime.getTime() - startTime.getTime();
                        incrMinute(fieldMinuteMap, userId, onDutyOrgId, diff);
                        incrNum(fieldCounts, userId, onDutyOrgId);
                    }
                    if (offDutyStatus != 5) {
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - offStartTime.getTime();
                        incrMinute(fieldMinuteMap, userId, offDutyOrgId, diff);
                        incrNum(fieldCounts, userId, offDutyOrgId);
                        fieldIds.add(onSourceId);
                        fieldIds.add(offSourceId);
                    }
                    break;
                }
            }
        }
        //被覆盖的外勤（排除已打卡和未打卡的）
        FieldInfoQueryForm fieldInfoQueryForm = new FieldInfoQueryForm();
        fieldInfoQueryForm.setBetweenDate(queryForm.getBetweenDate());
        fieldInfoQueryForm.setAndDate(queryForm.getAndDate());
        fieldInfoQueryForm.setApprpvalStatus(1);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldInfoQueryForm);
        fieldInfoVOS.forEach(fieldInfoVO -> {
            Integer id = fieldInfoVO.getId();
            if (!fieldIds.contains(id)) {
                Integer userId = fieldInfoVO.getUserId();
                Integer orgId = fieldInfoVO.getCompanyId();
                long diff = fieldInfoVO.getEndTime().getTime() - fieldInfoVO.getStartTime().getTime();
                incrMinute(fieldMinuteMap, userId, orgId, diff);
                incrNum(fieldCounts, userId, orgId);
            }
        });

        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(queryForm.getWhetherPage());
        model.setPageNum(queryForm.getPageNum());
        model.setPageSize(queryForm.getPageSize());
        if (queryForm.getOrgId() != null) {
            model.setOrgIds(Arrays.asList(queryForm.getOrgId()));
            if (userIds != null && !userIds.isEmpty()) {
                model.setUserIds(userIds);
            }
        } else {
            if (userOrgIds != null && !userOrgIds.isEmpty()) {
                model.setUserOrgIds(userOrgIds);
            }
        }
        model.setKeyWord(name);
        model.setWorkStatus(new Byte[]{0, 1, 3});
        List<SysUserInfoDetail> userList = remoteSystemServiceFeign.findSysUserEmployeeWithOrgList(model);
        List<AttendanceStatisticsVO> result = new ArrayList<>();
        userList.forEach(user->{
            Integer userId = user.getUserId();
            String companyIds = user.getCompanyIds();
            Integer orgId = null;
            if (StringHelper.isNotEmpty(companyIds)) {
                orgId = Integer.parseInt(companyIds);
            }
            AttendanceStatisticsVO statistics = new AttendanceStatisticsVO();
            statistics.setUserId(userId);
            statistics.setEmployeeName(user.getName());
            statistics.setOrgId(orgId);
            statistics.setOrgName(user.getCompanys());
            Long restMinute = restDateOvertimeMap.get(userId, orgId);
            if (restMinute == null) {
                restMinute = 0L;
            }
            statistics.setRestDateOvertimeMinute(DateUtil.micro2Min(restMinute));
            Integer restCount = restDateOverCounts.get(userId, orgId);
            if (restCount == null) {
                restCount = 0;
            }
            statistics.setWorkOvertimeNum(restCount);
            Long leaveMinute = leaveMinuteMap.get(userId, orgId);
            if (leaveMinute == null) {
                leaveMinute = 0L;
            }
            statistics.setLeaveMinute(DateUtil.micro2Min(leaveMinute));
            Integer leaveCount = leaveCounts.get(userId, orgId);
            if (leaveCount == null) {
                leaveCount = 0;
            }
            statistics.setLeaveNum(leaveCount);
            Long fieldMinute = fieldMinuteMap.get(userId, orgId);
            if (fieldMinute == null) {
                fieldMinute = 0L;
            }
            statistics.setFieldMinute(DateUtil.micro2Min(fieldMinute));
            Integer fieldCount = fieldCounts.get(userId, orgId);
            if (fieldCount == null) {
                fieldCount = 0;
            }
            statistics.setFieldNum(fieldCount);
            Long workDateOvertimeMinute = workDateOvertimeMinuteMap.get(userId, orgId);
            if (workDateOvertimeMinute == null) {
                workDateOvertimeMinute = 0L;
            }
            statistics.setWorkDateOvertimeMinute(DateUtil.micro2Min(workDateOvertimeMinute));
            Long workDate30Minute = workDateOvertime30MinuteMap.get(userId, orgId);
            if (workDate30Minute == null) {
                workDate30Minute = 0L;
            }
            statistics.setWorkDateOvertime30Minute(DateUtil.micro2Min(workDate30Minute));
            incrMinute(workDateMinuteMap, userId, orgId, fieldMinute);// 外勤覆盖上班班次的（覆盖上班卡、覆盖下班卡）
            incrMinute(workDateMinuteMap, userId, orgId, restMinute);// 加班覆盖上班班次的（覆盖上班卡、覆盖下班卡）
            Long workDateMinute = workDateMinuteMap.get(userId, orgId);
            if (workDateMinute == null) {
                workDateMinute = 0L;
            }
            statistics.setWorkDateMinute(DateUtil.micro2Min(workDateMinute));
            Boolean isFull = isFullMap.get(userId, orgId);
            String isFullStr = "--";
            if (type == 0) {
                if (isFull==null || isFull) {
                    isFullStr = "是";
                } else {
                    isFullStr = "否";
                }
            }
            statistics.setIsFull(isFullStr);
            Integer attendanceNum = attendancNumMap.get(userId, orgId);
            if (attendanceNum == null) {
                attendanceNum = 0;
            }
            statistics.setAttendanceNum(attendanceNum);
            Integer lateNum = laterNumMap.get(userId, orgId);
            if (lateNum == null) {
                lateNum = 0;
            }
            statistics.setLateNum(lateNum);
            Long lateMinute = laterMinuteMap.get(userId, orgId);
            if (lateMinute == null) {
                lateMinute = 0L;
            }
            statistics.setLateMinute(DateUtil.micro2Min(lateMinute));
            Integer earlyNum = earlyNumMap.get(userId, orgId);
            if (earlyNum == null) {
                earlyNum = 0;
            }
            statistics.setEarlyNum(earlyNum);
            Long earlyMinute = earlyMinuteMap.get(userId, orgId);
            if (earlyMinute == null) {
                earlyMinute = 0L;
            }
            statistics.setEarlyMinute(DateUtil.micro2Min(earlyMinute));
            Integer unpunchNum = unpunchNumMap.get(userId, orgId);
            if (unpunchNum == null) {
                unpunchNum = 0;
            }
            statistics.setUnpunchNum(unpunchNum);
            Integer invalidNum = invalidNumMap.get(userId, orgId);
            if (invalidNum == null) {
                invalidNum = 0;
            }
            statistics.setInvalidNum(invalidNum);
            result.add(statistics);
        });
        return result;
    }

    /**
     * 计算工作时长，其中需要剔除工作班次内的请假（请假被完全覆盖的情况）
     *
     * @param date
     * @param endTime
     * @param startTime
     * @param userId
     * @param orgId
     * @param leaveInfoMap
     * @return
     */
    private long computeWithoutLeave(Date date, Date endTime, Date startTime, Integer userId, Integer orgId, Table<Integer, Integer, List<LeaveInfoVO>> leaveInfoMap) {
        long diff = endTime.getTime() - startTime.getTime();
        List<LeaveInfoVO> list = leaveInfoMap.get(userId, orgId);
        Date sDateTime;
        Date eDateTime;
        try {
            sDateTime = DateUtil.timetoDate(date, startTime);
            eDateTime = DateUtil.timetoDate(date, endTime);
        } catch (Exception e) {
            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
        }
        for (LeaveInfoVO leaveInfoVO : list) {
            Date leaveStartTime = leaveInfoVO.getStartTime();
            Date leaveEndTime = leaveInfoVO.getEndTime();
            if (sDateTime.before(leaveStartTime) && eDateTime.after(leaveEndTime)) {
                diff -= leaveEndTime.getTime() - leaveStartTime.getTime();
            }
        }
        return diff;
    }

    /**
     * 将按年查询或按月查询或按天查询的参数转换成按日期范围的查询参数。
     *
     * @param queryForm
     */
    private void setQueryFormDate(AttendanceStatisticsQueryForm queryForm) {
        String dateStr = queryForm.getDate();
        if (StringHelper.isNotEmpty(dateStr)) {
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (dateStr.length() == 4) {//按年查
                int year = Integer.parseInt(dateStr);
                startTime = DateUtil.getBeginTime(year,1);
                endTime = DateUtil.getEndTime(year,12);
            } else if (dateStr.length() == 7) {
                String[] dateTmp = dateStr.split("-");
                int year = Integer.parseInt(dateTmp[0]);
                int month = Integer.parseInt(dateTmp[1]);
                startTime = DateUtil.getBeginTime(year,month);
                endTime = DateUtil.getEndTime(year,month);
            } else if (dateStr.length() == 10) {
                try {
                    endTime = startTime = sdf.parse(dateStr);
                } catch (ParseException e) {
                    throw new ClientServiceException("日期参数格式转换错误", DATA_TRANSFORMATION_EXIST);
                }
            } else {
                throw new ClientServiceException("日期参数格式错误", PARAMETERS_IS_ILLEGAL);
            }
            queryForm.setBetweenDate(startTime);
            queryForm.setAndDate(endTime);
        }
    }

    /**
     * 将考勤打卡记录转换成每日打卡
     *
     * @param attendancePunchRecordVOS
     * @return
     */
    private List<AttendancePunchDateVO> punchRecord2PunchDate(List<AttendancePunchRecordVO> attendancePunchRecordVOS) {
        List<AttendancePunchDateVO> result = new ArrayList<>();
        Map<String, List<AttendancePunchRecordVO>> dataMap = new HashMap<>(attendancePunchRecordVOS.size());
        attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
            String key = attendancePunchRecordVO.getUserId() + "," + attendancePunchRecordVO.getOrgId() + "," + attendancePunchRecordVO.getPunchDate();
            List<AttendancePunchRecordVO> list = dataMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(attendancePunchRecordVO);
            dataMap.put(key, list);
        });
        dataMap.forEach((key, list)->{
            AttendancePunchDateVO punchDate = new AttendancePunchDateVO();
            list.forEach(vo->{
                Byte punchType = vo.getPunchType();
                Byte isPunch = vo.getIsPunch();
                Date punchTime = vo.getPunchTime();
                String name = vo.getName();
                Integer orgId = vo.getOrgId();
                Byte punchStatus = vo.getPunchStatus();
                Byte source = vo.getSource();
                Integer sourceId = vo.getSourceId();
                punchDate.setUserId(vo.getUserId());
                punchDate.setPunchDate(vo.getPunchDate());
                byte leave = 0;
                byte workDateOvertime = 0;
                byte field = 0;
                if (punchType==AttendanceTypeEnum.ONDUTY.getCode()) {
                    punchDate.setOnDutyPunchTime(punchTime);
                    punchDate.setOnDutyOrgId(orgId);
                    punchDate.setOnDutyName(name);
                    if (source==AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {
                        punchDate.setLeave((byte) 5);
                    } else if (source==AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode()) {
                        leave = 1;
                        if (punchDate.getLeave()!=null && punchDate.getLeave()!=0&& punchDate.getOnDutySourceId()!=sourceId) {
                            leave = 4;
                        } else if (punchDate.getLeave()!=null && punchDate.getLeave()!=0 && punchDate.getOnDutySourceId()==sourceId) {
                            leave = 3;
                        }
                    } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                        workDateOvertime = 1;
                        if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && punchDate.getOnDutySourceId()!=sourceId) {
                            workDateOvertime = 4;
                        } else if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && punchDate.getOnDutySourceId()==sourceId) {
                            workDateOvertime = 3;
                        }
                    } else if (source==AttendanceSourceEnum.FIELD.getCode()) {
                        field = 1;
                        if (punchDate.getField()!=null && punchDate.getField()!=0 && punchDate.getOnDutySourceId()!=sourceId) {
                            field = 4;
                        } else if (punchDate.getField()!=null && punchDate.getField()!=0 && punchDate.getOnDutySourceId()==sourceId) {
                            field = 3;
                        }
                    }
                    punchDate.setOnDutySourceId(sourceId);
                    punchDate.setOnDutyStartTime(vo.getStartTime());
                    punchDate.setOnDutyEndTime(vo.getEndTime());
                    Byte status = 5;
                    if (isPunch==AttendanceIsPunchEnum.PUNCHED.getCode()) {
                        status = punchStatus;
                    }
                    punchDate.setOnDutyStatus(status);
                } else {
                    punchDate.setOffDutyPunchTime(punchTime);
                    punchDate.setOffDutyOrgId(orgId);
                    punchDate.setOffDutyName(name);
                    if (source==AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {
                        punchDate.setLeave((byte) 3);
                    } else if (source==AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {
                        leave = 2;
                        if (punchDate.getLeave()!=null && punchDate.getLeave()!=0 && punchDate.getOffDutySourceId()!=sourceId) {
                            leave = 4;
                        } else if (punchDate.getLeave()!=null && punchDate.getLeave()!=0&& punchDate.getOffDutySourceId()==sourceId) {
                            leave = 3;
                        }
                    } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                        workDateOvertime = 2;
                        if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && punchDate.getOffDutySourceId()!=sourceId) {
                            workDateOvertime = 4;
                        } else if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && punchDate.getOffDutySourceId()==sourceId) {
                            workDateOvertime = 3;
                        }
                    } else if (source==AttendanceSourceEnum.FIELD.getCode()) {
                        field = 2;
                        if (punchDate.getField()!=null && punchDate.getField()!=0 && punchDate.getOffDutySourceId()!=sourceId) {
                            field = 4;
                        } else if (punchDate.getField()!=null && punchDate.getField()!=0 && punchDate.getOffDutySourceId()==sourceId) {
                            field = 3;
                        }
                    }
                    punchDate.setOffDutySourceId(sourceId);
                    punchDate.setOffDutyEndTime(vo.getEndTime());
                    punchDate.setOffDutyStartTime(vo.getStartTime());
                    Byte status = 5;
                    if (isPunch==AttendanceIsPunchEnum.PUNCHED.getCode()) {
                        status = punchStatus;
                    }
                    punchDate.setOffDutyStatus(status);
                }
                punchDate.setField(field);
                punchDate.setWorkOvertime(workDateOvertime);
                punchDate.setLeave(leave);
            });
            result.add(punchDate);
        });
        return result;
    }

    /**
     * 对员工+组织的指标进行计数
     * @param nums
     * @param userId
     * @param orgId
     */
    private void incrNum(Table<Integer, Integer, Integer> nums, Integer userId, Integer orgId) {
        Integer count = nums.get(userId, orgId);
        if (count == null) {
            count = 0;
        }
        nums.put(userId, orgId, ++count);
    }

    private void incrMinute(Table<Integer, Integer, Long> minutes, Integer userId, Integer orgId, long diff) {
        Long length = minutes.get(userId, orgId);
        if (length == null) {
            length = 0L;
        }
        minutes.put(userId, orgId, length + diff);
    }

    @Override
    public void insertSelective(AttendancePunchRecord entity) {
        mapper.insertSelective(entity);
    }

    /**
     * 根据统计次数类型的分页查询考勤汇总明细
     *
     * @param type 统计次数类型：0-缺卡，1-迟到，3-早退，4-无效卡
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendancePunchCountVO> statisticsPunchRecordByCount(byte type, AttendanceStatisticsQueryForm queryForm) {
        setQueryFormDate(queryForm);
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setWhetherPage(queryForm.getWhetherPage());
        recordQueryForm.setPageNum(queryForm.getPageNum());
        recordQueryForm.setPageSize(queryForm.getPageSize() * 2);
        recordQueryForm.setUserId(queryForm.getUserId());
        recordQueryForm.setOrgId(queryForm.getOrgId());
        recordQueryForm.setBetweenDate(queryForm.getBetweenDate());
        recordQueryForm.setAndDate(queryForm.getAndDate());
        if (type == 0) {
            recordQueryForm.setIsPunch(AttendanceIsPunchEnum.UNPUNCH.getCode());
        } else {
            recordQueryForm.setIsPunch(AttendanceIsPunchEnum.PUNCHED.getCode());
            recordQueryForm.setPunchStatus(type);
        }
        recordQueryForm.setPunchType(AttendanceTypeEnum.ONDUTY.getCode());
        List<AttendancePunchRecordVO> masterRecords = findAttendancePunchRecordList(recordQueryForm);
        List<Integer> ids = new ArrayList<>(masterRecords.size());
        masterRecords.forEach(attendancePunchRecordVO -> ids.add(attendancePunchRecordVO.getId()));
        recordQueryForm.setNotInIds(ids);
        List<AttendancePunchRecordVO> otherRecords = findAttendancePunchRecordList(recordQueryForm);
        Map<Date, AttendancePunchRecordVO> otherRecordMap = new HashMap<>();
        otherRecords.forEach(attendancePunchRecordVO -> otherRecordMap.put(attendancePunchRecordVO.getPunchDate(), attendancePunchRecordVO));
        Map<Integer, EmployeeScheduleVO> employeeScheduleMap = new HashMap<>();
        if (masterRecords!=null && !masterRecords.isEmpty()) {
            EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
            employeeScheduleQueryForm.setUserId(queryForm.getUserId());
            employeeScheduleQueryForm.setType(WORK);
            employeeScheduleQueryForm.setBetweenWorkDate(queryForm.getBetweenDate());
            employeeScheduleQueryForm.setAndWorkDate(queryForm.getAndDate());
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
            employeeScheduleVOS.forEach(employeeScheduleVO -> {
                if (employeeScheduleVO.getSecondEndTime() != null) {
                    employeeScheduleVO.setFirstEndTime(employeeScheduleVO.getSecondEndTime());
                }
                employeeScheduleMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
            });
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        List<AttendancePunchCountVO> result = new ArrayList<>();
        masterRecords.forEach(masterRecord -> {
            AttendancePunchCountVO attendancePunchCountVO = new AttendancePunchCountVO();
            Date punchDate = masterRecord.getPunchDate();
            AttendancePunchRecordVO slaveRecord = otherRecordMap.get(punchDate);
            StringBuilder name = getBaseScheduleName(masterRecord,employeeScheduleMap,sdf);
            if (slaveRecord.getSource()!=masterRecord.getSource() || slaveRecord.getSourceId()!=masterRecord.getSourceId()) {
                name = new StringBuilder();
                StringBuilder onName = getBaseScheduleName(masterRecord,employeeScheduleMap,sdf);
                name.append(onName);
                StringBuilder offName = getBaseScheduleName(slaveRecord,employeeScheduleMap,sdf);
                if (offName.length() > 0) {
                    name.append(" ").append(offName);
                }
            }
            attendancePunchCountVO.setDate(punchDate);
            Byte onIsPunch = masterRecord.getIsPunch();
            Byte onPunchStatus = masterRecord.getPunchStatus();
            Date onPunchTime = masterRecord.getPunchTime();
            String punchResult = "";
            int count = 0;
            long diff = 0;
            String onPunchAddress = masterRecord.getPunchAddress();
            if (onIsPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {
                punchResult = "上班卡缺卡";
                count++;
            } else {
                if (onPunchStatus == AttendanceStatusEnum.LATER_PUNCH.getCode()) {
                    diff = onPunchTime.getTime() - masterRecord.getStartTime().getTime();
                    attendancePunchCountVO.setMinutes(DateUtil.micro2Min(diff));
                    attendancePunchCountVO.setOnAttendanceTime(masterRecord.getStartTime());
                } else if (onPunchStatus == AttendanceStatusEnum.UNVALID_PUNCH.getCode()) {
                    punchResult = "上班卡无效";
                    count++;
                }
                attendancePunchCountVO.setPunchAddress(onPunchAddress);
                attendancePunchCountVO.setOnPunchTime(onPunchTime);
            }
            Byte offIsPunch = slaveRecord.getIsPunch();
            Byte offPunchStatus = slaveRecord.getPunchStatus();
            String offPunchAddress = slaveRecord.getPunchAddress();
            Date offPunchTime = slaveRecord.getPunchTime();
            if (offIsPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {
                if (punchResult.length() > 0) {
                    punchResult += "、";
                }
                punchResult += "下班卡缺卡";
                count++;
            } else {
                if (offPunchStatus == AttendanceStatusEnum.EARLY_PUNCH.getCode()) {
                    attendancePunchCountVO.setPunchAddress(offPunchAddress);
                    diff = offPunchTime.getTime() - slaveRecord.getEndTime().getTime();
                    attendancePunchCountVO.setMinutes(DateUtil.micro2Min(diff));
                    attendancePunchCountVO.setOffAttendanceTime(slaveRecord.getEndTime());
                } else if (offPunchStatus == AttendanceStatusEnum.UNVALID_PUNCH.getCode()) {
                    if (punchResult.length() > 0) {
                        punchResult += "、";
                    }
                    punchResult += "下班卡无效";
                    if (onPunchAddress.length() > 0) {
                        onPunchAddress += "、";
                    }
                    onPunchAddress += offPunchAddress;
                    attendancePunchCountVO.setPunchAddress(onPunchAddress);
                    count++;
                }
                attendancePunchCountVO.setOffPunchTime(offPunchTime);
            }
            attendancePunchCountVO.setName(name.toString());
            attendancePunchCountVO.setPunchResult(punchResult);
            attendancePunchCountVO.setCount(count);
            result.add(attendancePunchCountVO);
        });
        return result;
    }

    /**
     * 获取排班名称
     *
     * @param attendancePunchRecordVO
     * @param employeeScheduleMap
     * @param sdf
     * @return
     */
    private StringBuilder getBaseScheduleName(AttendancePunchRecordVO attendancePunchRecordVO, Map<Integer, EmployeeScheduleVO> employeeScheduleMap, SimpleDateFormat sdf) {
        Integer sourceId = attendancePunchRecordVO.getSourceId();
        EmployeeScheduleVO employeeScheduleVO = employeeScheduleMap.get(sourceId);
        StringBuilder result = new StringBuilder();
        if (employeeScheduleVO != null) {
            Byte source = attendancePunchRecordVO.getSource();
            String tail = getSourceName(source);
            Date firstStartTime = employeeScheduleVO.getFirstStartTime();
            Date firstEndTime = employeeScheduleVO.getFirstEndTime();
            result.append(employeeScheduleVO.getName()).append("(")
                    .append(sdf.format(firstStartTime)).append("-")
                    .append(sdf.format(firstEndTime)).append(")").append(tail);
        }
        return result;
    }

    /**
     * 根据工作时长的分页查询考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceWorkDateMinuteVO> statisticsWorkDateByMinute(AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        List<Date> dateList = DateUtil.getBetweenDate(betweenDate, andDate, queryForm.getPageNum(), queryForm.getPageSize());
        betweenDate = dateList.get(0);
        andDate = dateList.get(dateList.size()-1);
        // 排班
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        // 打卡
        Map<Date, List<AttendancePunchRecordVO>> punchRecordMap = getPunchRecordMapGroupByDate(userId, orgId, betweenDate, andDate);
        // 手动补入时长
        Map<Date, AttendanceManualMakeupVO> manualMakeupMap = getManualMakeupMapGroupByDate(userId, orgId, MakeupTypeEnum.WORKDATE.getCode(), betweenDate, andDate);
        // 请假
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setUserId(userId);
        leaveQueryForm.setBetweenStartDate(betweenDate);
        leaveQueryForm.setAndStartDate(andDate);
        leaveQueryForm.setOrgId(orgId);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        // 外勤
        FieldInfoQueryForm fieldQueryForm = new FieldInfoQueryForm();
        fieldQueryForm.setBetweenDate(betweenDate);
        fieldQueryForm.setAndDate(andDate);
        fieldQueryForm.setCompanyId(orgId);
        fieldQueryForm.setApprpvalStatus(1);
        fieldQueryForm.setUserId(userId);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldQueryForm);
        List<AttendanceWorkDateMinuteVO> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateList.forEach(date -> {
            AttendanceWorkDateMinuteVO workDateMinuteVO = new AttendanceWorkDateMinuteVO();
            Date onPunchTime = null;
            Date offPunchTime = null;
            long diff = 0;
            Byte hasApply = 0; // 0-无，1-请假，2-加班，3外勤
            for (FieldInfoVO fieldInfoVO : fieldInfoVOS) {// 外勤可能被覆盖
                String fieldDateStr = sdf.format(fieldInfoVO.getStartTime());
                Date fieldDate;
                try {
                    fieldDate = sdf.parse(fieldDateStr);
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                }
                if (date.compareTo(fieldDate)==0) {
                    hasApply = 3;
                    break;
                }
            }
            for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
                String startDateStr = sdf.format(leaveInfoVO.getStartDate());
                String endDateStr = sdf.format(leaveInfoVO.getEndDate());
                Date startDate;
                Date endDate;
                try {
                    startDate = sdf.parse(startDateStr);
                    endDate = sdf.parse(endDateStr);
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                }
                if (leaveInfoVO.getVacationStatus()==0) {// 按班次请假
                    if (date.compareTo(startDate) == 0) {
                        hasApply = 1;
                        break;
                    }
                } else {// 按天请假
                    if (date.compareTo(startDate)>=0 && date.compareTo(endDate)<=0) {
                        hasApply = 1;
                        break;
                    }
                }
            }
            StringBuilder punchResult = new StringBuilder();
            StringBuilder employeeScheduleName = new StringBuilder();
            AttendanceManualMakeupVO manualMakeupVO = manualMakeupMap.get(date);
            if (manualMakeupVO != null) {
                long interpolationDiff = manualMakeupVO.getMinute();
                workDateMinuteVO.setMakeupMinutes(DateUtil.micro2Min(interpolationDiff));
                workDateMinuteVO.setMakeupDesc(manualMakeupVO.getMakeupDesc());
            }
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            if (employeeScheduleVOS==null || employeeScheduleVOS.isEmpty()) {
                employeeScheduleName.append("未排班");
            } else {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName);
                }
            }
            boolean hasException = false;
            List<AttendancePunchRecordVO> punchRecordVOS = punchRecordMap.get(date);
            if (punchRecordVOS!=null && !punchRecordVOS.isEmpty()) {
                for (AttendancePunchRecordVO punchRecordVO : punchRecordVOS) {
                    if (punchResult.length() > 0) {
                        punchResult.append("、");
                    }
                    Byte source = punchRecordVO.getSource();
                    Byte isPunch = punchRecordVO.getIsPunch();
                    Byte punchType = punchRecordVO.getPunchType();
                    String tag = "work";
                    if (source==AttendanceSourceEnum.LEAVE_BYDAY.getCode()
                            ||source==AttendanceSourceEnum.REST_SCHEDULE.getCode()
                            ||source==AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode()) {
                        // 休息、按班次请假、按天请假
                        tag = "rest";
                    } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                        // 加班
                        tag = "exception";
                        hasApply = 2; //加班必定存在于打卡记录中
                    } else if (source==AttendanceSourceEnum.FIELD.getCode()) {
                        // 外勤
                        tag = "exception";
                    }
                    if (tag == "rest") {
                        punchResult.append(AttendanceTypeEnum.getValue(punchType));
                        if (isPunch==AttendanceIsPunchEnum.UNPUNCH.getCode()) {//未打卡
                            punchResult.append("无效");
                            if (punchType==AttendanceTypeEnum.ONDUTY.getCode()) {
                                onPunchTime = punchRecordVO.getPunchTime();
                            } else {
                                offPunchTime = punchRecordVO.getPunchTime();
                            }
                        }
                    } else {
                        if (tag == "work") {
                            punchResult.append(AttendanceTypeEnum.getValue(punchType));
                            if (isPunch == AttendanceIsPunchEnum.UNPUNCH.getCode()) {//未打卡
                                punchResult.append("缺卡");
                                hasException = true;
                            } else {
                                Byte punchStatus = punchRecordVO.getPunchStatus();
                                if (punchStatus == AttendanceStatusEnum.ONDUTY_PUNCH.getCode()
                                        || punchStatus == AttendanceStatusEnum.OFFDUTY_PUNCH.getCode()) {
                                    punchResult.append("正常");
                                } else {
                                    punchResult.append(AttendanceStatusEnum.getValue(punchStatus));
                                    hasException = true;
                                }
                                if (punchType == AttendanceTypeEnum.ONDUTY.getCode()) {
                                    onPunchTime = punchRecordVO.getPunchTime();
                                } else {
                                    offPunchTime = punchRecordVO.getPunchTime();
                                }
                            }
                        }
                        if (punchType == AttendanceTypeEnum.ONDUTY.getCode()) {
                            Date onStartTime = punchRecordVO.getStartTime();
                            if (onPunchTime!=null && onPunchTime.after(onStartTime)) {
                                onStartTime = onPunchTime;
                            }
                            diff = punchRecordVO.getEndTime().getTime() - onStartTime.getTime();
                        } else {
                            Date offEndTime = punchRecordVO.getEndTime();
                            if (offPunchTime!=null && offPunchTime.before(offEndTime)) {
                                offEndTime = offPunchTime;
                            }
                            diff = offEndTime.getTime() - punchRecordVO.getStartTime().getTime();
                        }
                    }
                }
            }
            workDateMinuteVO.setHasException(hasException);
            workDateMinuteVO.setHasApply(hasApply);
            workDateMinuteVO.setOnPunchTime(onPunchTime);
            workDateMinuteVO.setOffPunchTime(offPunchTime);
            workDateMinuteVO.setMinutes(DateUtil.micro2Min(diff));
            workDateMinuteVO.setEmployeeScheduleName(employeeScheduleName.toString());
            workDateMinuteVO.setPunchResult(punchResult.toString());
            workDateMinuteVO.setDate(date);
            result.add(workDateMinuteVO);
        });
        return result;
    }

    /**
     * 分页查询工作日加班时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceWorkDateOvertimeMinuteVO> statisticsWorkDateOvertimeByMinute(int minute, AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        recordQueryForm.setPunchStatus(AttendanceStatusEnum.OFFDUTY_PUNCH.getCode());
        recordQueryForm.setSource(AttendanceSourceEnum.WORK_SCHEDULE.getCode());
        recordQueryForm.setIsPunch(AttendanceIsPunchEnum.PUNCHED.getCode());
        recordQueryForm.setOrgId(orgId);
        recordQueryForm.setUserId(userId);
        recordQueryForm.setBetweenDate(betweenDate);
        recordQueryForm.setAndDate(andDate);
        List<AttendanceWorkDateOvertimeMinuteVO> result = mapper.statisticsWorkDateOvertimeByMinute(minute, recordQueryForm);
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        result.forEach(workDateOvertimeMinute->{
            Date date = workDateOvertimeMinute.getDate();
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            StringBuilder employeeScheduleName = new StringBuilder();
            if (employeeScheduleVOS!=null && !employeeScheduleMap.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName);
                }
            }
            workDateOvertimeMinute.setEmployeeScheduleName(employeeScheduleName.toString());
        });
        return result;
    }

    /**
     * 拼接排班名称
     *
     * @param employeeScheduleVO
     * @param employeeScheduleName
     */
    private void putEmployeeScheduleName(EmployeeScheduleVO employeeScheduleVO, StringBuilder employeeScheduleName) {
        if (employeeScheduleName.length() > 0) {
            employeeScheduleName.append(" ");
        }
        Date firstStartTime = employeeScheduleVO.getFirstStartTime();
        Date firstEndTime = employeeScheduleVO.getFirstEndTime();
        Date secondStartTime = employeeScheduleVO.getSecondStartTime();
        Date secondEndTime = employeeScheduleVO.getSecondEndTime();
        employeeScheduleName.append(employeeScheduleVO.getName())
                .append("（").append(firstStartTime)
                .append("-").append(firstEndTime);
        if (secondStartTime!=null && secondEndTime!=null) {
            employeeScheduleName.append("、").append(secondStartTime).append("-").append(secondEndTime);
        }
        employeeScheduleName.append("）【").append(employeeScheduleVO.getType()).append("】");
    }

    /**
     * 根据日期分组手动补入时长
     *
     * @param userId
     * @param orgId
     * @param type
     * @param betweenDate
     * @param andDate
     * @return
     */
    private Map<Date, AttendanceManualMakeupVO> getManualMakeupMapGroupByDate(Integer userId, Integer orgId, Byte type, Date betweenDate, Date andDate) {
        AttendanceManualMakeupQueryForm queryForm = new AttendanceManualMakeupQueryForm();
        queryForm.setUserId(userId);
        queryForm.setOrgId(orgId);
        queryForm.setBetweenDate(betweenDate);
        queryForm.setAndDate(andDate);
        queryForm.setType(type);
        List<AttendanceManualMakeupVO> attendanceManualMakeupVOS = attendanceManualMakeupBiz.findAttendanceManualMakeupList(queryForm);
        Map<Date, AttendanceManualMakeupVO> result = new HashMap<>(attendanceManualMakeupVOS.size());
        attendanceManualMakeupVOS.forEach(makeupVO -> result.put(makeupVO.getMakeupDate(), makeupVO));
        return result;
    }

    /**
     * 分页查询休息日加班时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceOvertimeMinuteVO> statisticsWorkOvertimesByMinute(AttendanceStatisticsQueryForm queryForm) {
        SimpleDateFormat hSdf = new SimpleDateFormat("HH:mm");
        setQueryFormDate(queryForm);
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        //加班申请
        WorkOvertimeInfoQueryForm workQueryForm = new WorkOvertimeInfoQueryForm();
        workQueryForm.setWhetherPage(queryForm.getWhetherPage());
        workQueryForm.setPageNum(queryForm.getPageNum());
        workQueryForm.setPageSize(queryForm.getPageSize());
        workQueryForm.setUserId(userId);
        workQueryForm.setCompanyId(orgId);
        workQueryForm.setStartTime(betweenDate);
        workQueryForm.setEndTime(andDate);
        workQueryForm.setApprpvalStatus(1);
        List<AttendanceOvertimeMinuteVO> workOvertimeVOS = workOvertimeInfoBiz.statisticsWorkOvertimesByMinute(workQueryForm);
        //审批人
        List<Integer> userIds = new ArrayList<>();
        workOvertimeVOS.forEach(workOvertimeMinuteVO -> {
            Integer approveUserId = workOvertimeMinuteVO.getApprovalPeopleId();
            if (!userIds.contains(approveUserId)) {
                userIds.add(approveUserId);
            }
        });
        Map<Integer, String> userNameMap = getApproveUserMap(userIds);
        // 排班
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        // 打卡
        Map<Date, List<AttendancePunchRecordVO>> punchRecordMap = getPunchRecordMapGroupByDate(userId, orgId, betweenDate, andDate);
        workOvertimeVOS.forEach(workOvertimeMinuteVO -> {
            long diff = 0;
            Date onPunchTime = null;
            Date offPunchTime = null;
            StringBuilder sb = new StringBuilder();
            Date date = workOvertimeMinuteVO.getWorkDate();
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            if (employeeScheduleVOS!=null && !employeeScheduleVOS.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, sb);
                }
            }
            List<AttendancePunchRecordVO> punchRecords = punchRecordMap.get(date);
            if (punchRecords!=null && !punchRecords.isEmpty()) {
                for (AttendancePunchRecordVO punchRecord : punchRecords) {
                    if (punchRecord.getIsPunch()==AttendanceIsPunchEnum.PUNCHED.getCode()) {
                        Date sTime = punchRecord.getStartTime();
                        Date eTime = punchRecord.getEndTime();
                        if (punchRecord.getPunchType()==AttendanceTypeEnum.ONDUTY.getCode()) {//上班
                            onPunchTime = punchRecord.getPunchTime();
                            if (onPunchTime.after(sTime)) {
                                sTime = onPunchTime;
                            }
                        } else {
                            offPunchTime = punchRecord.getPunchTime();
                            if (offPunchTime.before(eTime)) {
                                eTime = offPunchTime;
                            }
                        }
                        diff += eTime.getTime() - sTime.getTime();
                    }
                }
            }
            Date firstStartTime = workOvertimeMinuteVO.getOnPunchTime();
            Date firstEndTime = workOvertimeMinuteVO.getOnPunchTime();
            StringBuilder scheduleName = new StringBuilder(workOvertimeMinuteVO.getScheduleName());
            scheduleName.append("（").append(hSdf.format(firstStartTime)).append("-").append(hSdf.format(firstEndTime)).append("）");
            String approvalName = userNameMap.get(workOvertimeMinuteVO.getApprovalPeopleId());
            workOvertimeMinuteVO.setApprovalUserName(approvalName);
            workOvertimeMinuteVO.setEmployeeScheduleName(sb.toString());
            workOvertimeMinuteVO.setMinutes(DateUtil.micro2Min(diff));
            workOvertimeMinuteVO.setOnPunchTime(onPunchTime);
            workOvertimeMinuteVO.setOffPunchTime(offPunchTime);
            workOvertimeMinuteVO.setScheduleName(scheduleName.toString());
        });
        return workOvertimeVOS;
    }

    /**
     * 分页查询休息日加班时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
//    public List<AttendanceWorkOvertimeMinuteVO> statisticsWorkOvertimesByMinute1(AttendanceStatisticsQueryForm queryForm) {
//        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
//        recordQueryForm.setUserId(queryForm.getUserId());
//        recordQueryForm.setOrgId(queryForm.getOrgId());
//        setQueryFormDate(queryForm);
//        recordQueryForm.setBetweenDate(queryForm.getBetweenDate());
//        recordQueryForm.setAndDate(queryForm.getAndDate());
//        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
//        Table<Date, String, List<AttendancePunchRecordVO>> attendancePunchRecordVOMap = HashBasedTable.create();
//        attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
//            Date punchDate = attendancePunchRecordVO.getPunchDate();
//            String key = attendancePunchRecordVO.getUserId() + "," + attendancePunchRecordVO.getOrgId();
//            List<AttendancePunchRecordVO> list = attendancePunchRecordVOMap.get(punchDate, key);
//            if (list == null) {
//                list = new ArrayList<>();
//            }
//            list.add(attendancePunchRecordVO);
//            attendancePunchRecordVOMap.put(punchDate, key,list);
//        });
//
//        WorkOvertimeInfoQueryForm workQueryForm = new WorkOvertimeInfoQueryForm();
//        workQueryForm.setWhetherPage(queryForm.getWhetherPage());
//        workQueryForm.setPageNum(queryForm.getPageNum());
//        workQueryForm.setPageSize(queryForm.getPageSize());
//        workQueryForm.setUserId(queryForm.getUserId());
//        workQueryForm.setCompanyId(queryForm.getOrgId());
//        setQueryFormDate(queryForm);
//        workQueryForm.setStartTime(queryForm.getBetweenDate());
//        workQueryForm.setEndTime(queryForm.getAndDate());
//        workQueryForm.setApprpvalStatus(1);
//        List<AttendanceWorkOvertimeMinuteVO> workOvertimeMinutes = workOvertimeInfoBiz.statisticsWorkOvertimesByMinute(workQueryForm);
//        List<Integer> notIds = new ArrayList<>();
//        workOvertimeMinutes.forEach(workOvertime-> {
//            Integer id = workOvertime.getId();
//            if (!notIds.contains(id)) {
//                notIds.add(id);
//            }
//        });
//        workQueryForm.setNotIds(notIds);
//        List<WorkOvertimeInfoVO> otherWorkOvertimeInfos = workOvertimeInfoBiz.findWorkOvertimeInfoList(workQueryForm);
//        Map<Date, WorkOvertimeInfoVO> workOvertimeInfoVOMap = new HashMap<>(otherWorkOvertimeInfos.size());
//        otherWorkOvertimeInfos.forEach(workOvertimeInfoVO -> {
//            Date date = workOvertimeInfoVO.getWorkDate();
//            workOvertimeInfoVOMap.put(date, workOvertimeInfoVO);
//        });
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        StringBuilder scheduleName = new StringBuilder();
//        StringBuilder employeeScheduleName = new StringBuilder();
//        workOvertimeMinutes.forEach(workOvertime->{
//            Date workDate = workOvertime.getWorkDate();
//            Integer id = workOvertime.getId();
//            Date startTime = workOvertime.getOnPunchTime();
//            Date endTime = workOvertime.getOffPunchTime();
//            scheduleName.append(workOvertime.getScheduleName()).append("（").append(startTime).append("-").append(endTime).append("）");
//            Date onPunchTime = null;
//            Date offPunchTime = null;
//            WorkOvertimeInfoVO workOvertimeInfoVO = workOvertimeInfoVOMap.get(workDate);
//            long diff = 0;
//            String key = workOvertime.getUserId() + "," + workOvertime.getCompanyId();
//            List<AttendancePunchRecordVO> list = attendancePunchRecordVOMap.get(workDate, key);
//            AttendancePunchRecordVO first = list.get(0);
//            AttendancePunchRecordVO last = list.get(1);
//            Integer onSourceId = first.getSourceId();
//            Integer offSourceId = last.getSourceId();
//            if (workOvertimeInfoVO != null) {
//                if (scheduleName.length() > 0) {
//                    scheduleName.append(" ");
//                }
//                scheduleName.append(workOvertimeInfoVO.getRestScheduleName()).append("（").append(workOvertimeInfoVO.getStartTime()).append("-").append(workOvertimeInfoVO.getEndTime()).append("）");
//                Date sTime = first.getStartTime();
//                Date eTime = first.getEndTime();
//                onPunchTime = first.getPunchTime();
//                if (onPunchTime != null) {
//                    if (sTime.before(onPunchTime)) {
//                        sTime = onPunchTime;
//                    }
//                } else {
//                    sTime = eTime;
//                }
//                long onDiff = eTime.getTime() - sTime.getTime();
//                sTime = last.getStartTime();
//                eTime = last.getEndTime();
//                offPunchTime = last.getPunchTime();
//                if (offPunchTime != null) {
//                    if (eTime.after(offPunchTime)) {
//                        eTime = offPunchTime;
//                    }
//                } else {
//                    eTime = sTime;
//                }
//                long offDiff = eTime.getTime() - sTime.getTime();
//                diff = onDiff + offDiff;
//                employeeScheduleName.append(first.getName()).append("（").append(sdf.format(first.getStartTime())).append("-").append(sdf.format(first.getEndTime())).append("）【加班】")
//                        .append(last.getName()).append("（").append(sdf.format(last.getStartTime())).append("-").append(sdf.format(last.getEndTime())).append("）").append(getSourceName(last.getSource()));
//            } else {
//                Date sTime = first.getStartTime();
//                Date eTime = last.getEndTime();
//                if (onSourceId==id && offSourceId==id) {
//                    onPunchTime = first.getPunchTime();
//                    offPunchTime = last.getPunchTime();
//                    if (onPunchTime != null) {
//                        if (sTime.before(onPunchTime)) {
//                            sTime = onPunchTime;
//                        }
//                    } else {
//                        sTime = eTime;
//                    }
//                    if (offPunchTime != null) {
//                        if (eTime.after(offPunchTime)) {
//                            eTime = offPunchTime;
//                        }
//                    } else {
//                        eTime = sTime;
//                    }
//                    employeeScheduleName.append(first.getName()).append("（").append(sdf.format(first.getStartTime())).append("-").append(sdf.format(last.getEndTime())).append("）【加班】");
//                } else if (onSourceId == id) {
//                    onPunchTime = first.getPunchTime();
//                    eTime = first.getEndTime();
//                    if (onPunchTime != null) {
//                        if (sTime.before(onPunchTime)) {
//                            sTime = onPunchTime;
//                        }
//                    } else {
//                        sTime = eTime;
//                    }
//                    scheduleName.append(first.getName()).append("（").append(sdf.format(first.getStartTime())).append("-").append(sdf.format(first.getEndTime())).append("）");
//                    employeeScheduleName.append(first.getName()).append("（").append(sdf.format(first.getStartTime())).append("-").append(sdf.format(first.getEndTime())).append("）【加班】")
//                        .append(last.getName()).append("（").append(sdf.format(last.getStartTime())).append("-").append(sdf.format(last.getEndTime())).append("）").append(getSourceName(last.getSource()));
//                } else if (offSourceId == id) {
//                    sTime = last.getStartTime();
//                    offPunchTime = last.getPunchTime();
//                    if (offPunchTime != null) {
//                        if (eTime.after(offPunchTime)) {
//                            eTime = offPunchTime;
//                        }
//                    } else {
//                        eTime = sTime;
//                    }
//                    scheduleName.append(last.getName()).append("（").append(sdf.format(last.getStartTime())).append("-").append(sdf.format(last.getEndTime())).append("）");
//                    employeeScheduleName.append(first.getName()).append("（").append(sdf.format(first.getStartTime())).append("-").append(sdf.format(first.getEndTime())).append("）").append(getSourceName(first.getSource()))
//                            .append(last.getName()).append("（").append(sdf.format(last.getStartTime())).append("-").append(sdf.format(last.getEndTime())).append("）【加班】");
//                }
//                diff = eTime.getTime() - sTime.getTime();
//            }
//            workOvertime.setScheduleName(scheduleName.toString());
//            workOvertime.setOnPunchTime(onPunchTime);
//            workOvertime.setOffPunchTime(offPunchTime);
//            workOvertime.setMinutes(DateUtil.micro2Min(diff));
//            workOvertime.setEmployeeScheduleName(employeeScheduleName.toString());
//        });
//        return workOvertimeMinutes;
//    }

    /**
     * 分页查询请假时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceLeaveMinuteVO> statisticsLeavesByMinute(AttendanceStatisticsQueryForm queryForm) {
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setWhetherPage(queryForm.getWhetherPage());
        leaveQueryForm.setPageNum(queryForm.getPageNum());
        leaveQueryForm.setPageSize(queryForm.getPageSize());
        leaveQueryForm.setUserId(queryForm.getUserId());
        leaveQueryForm.setOrgId(queryForm.getOrgId());
        setQueryFormDate(queryForm);
        leaveQueryForm.setBetweenStartDate(queryForm.getBetweenDate());
        leaveQueryForm.setAndStartDate(queryForm.getAndDate());
        leaveQueryForm.setApprpvalStatus(1);
        // 排班
        List<LeaveScheduleVO> leaveScheduleVOS = leaveScheduleBiz.findLeaveScheduleByStatisticsQuery(queryForm);
        Map<Integer, List<LeaveScheduleVO>> leaveScheduleMap = new HashMap<>(16);
        Set<Integer> userIds = new HashSet<>();
        leaveScheduleVOS.forEach(leaveScheduleVO -> {
            Integer leaveId = leaveScheduleVO.getLeaveId();
            Integer approvalPeopleId = leaveScheduleVO.getApprovalPeopleId();
            userIds.add(approvalPeopleId);
            List<LeaveScheduleVO> list = leaveScheduleMap.get(leaveId);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(leaveScheduleVO);
            leaveScheduleMap.put(leaveId, list);
        });
        //审批人
        Map<Integer, String> userNameMap = getApproveUserMap(userIds);
        //请假
        leaveQueryForm.setIds(leaveScheduleMap.keySet());
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.statisticsLeavesByMinute(leaveQueryForm);
        List<AttendanceLeaveMinuteVO> result = new ArrayList<>(leaveInfoVOS.size());
        leaveInfoVOS.forEach(leaveInfoVO -> {
            AttendanceLeaveMinuteVO leaveMinuteVO = new AttendanceLeaveMinuteVO();
            Date startDate = leaveInfoVO.getStartTime();
            Date endDate = leaveInfoVO.getEndTime();
            if (endDate.after(queryForm.getAndDate())) {
                endDate = queryForm.getAndDate();
            }
            long diff = 0;
            StringBuilder sb = new StringBuilder(leaveInfoVO.getVacationName());
            StringBuilder approvalNames = new StringBuilder();
            Integer vacationStatus = leaveInfoVO.getVacationStatus();
            if (vacationStatus == 0) {
                List<LeaveScheduleVO> list = leaveScheduleMap.get(leaveInfoVO.getId());
                for (LeaveScheduleVO leaveScheduleVO : list) {
                    Integer approvalPeopleId = leaveScheduleVO.getApprovalPeopleId();
                    String userName = userNameMap.get(approvalPeopleId);
                    if (StringHelper.isNotEmpty(userName)) {
                        if (approvalNames.length() > 0) {
                            approvalNames.append("、");
                        }
                        approvalNames.append(userName);
                    }
                    diff += leaveScheduleVO.getEndTime().getTime() - leaveScheduleVO.getStartTime().getTime();
                }
                sb.append("（").append("按班次请假").append("）");
            } else {
                try {
                    diff = DAY_LEAVE_MILLSEC * (DateUtil.daysBetween(startDate,endDate));
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                }
                sb.append("（").append("按天请假").append("）");
            }
            leaveMinuteVO.setMinutes(DateUtil.micro2Min(diff));
            leaveMinuteVO.setApplyDate(leaveInfoVO.getCrtTime());
            leaveMinuteVO.setLeaveStartTime(startDate);
            leaveMinuteVO.setLeaveEndTime(endDate);
            leaveMinuteVO.setLeaveType(sb.toString());
            leaveMinuteVO.setApprovalUserName(approvalNames.toString());
            result.add(leaveMinuteVO);
        });
        return result;
    }

    /**
     * 分页查询外勤时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceFieldMinuteVO> statisticsFieldsByMinute(AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();

        // 排班
        FieldInfoQueryForm fieldQueryForm = new FieldInfoQueryForm();
        fieldQueryForm.setWhetherPage(queryForm.getWhetherPage());
        fieldQueryForm.setPageNum(queryForm.getPageNum());
        fieldQueryForm.setPageSize(queryForm.getPageSize());
        fieldQueryForm.setUserId(userId);
        fieldQueryForm.setCompanyId(orgId);
        fieldQueryForm.setBetweenDate(betweenDate);
        fieldQueryForm.setAndDate(andDate);
        fieldQueryForm.setApprpvalStatus(1);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldQueryForm);
        // 审批人
        List<Integer> userIds = new ArrayList<>();
        fieldInfoVOS.forEach(fieldInfoVO -> {
            Integer approveUserId = fieldInfoVO.getApprovalPeopleId();
            if (!userIds.contains(approveUserId)) {
                userIds.add(approveUserId);
            }
        });
        Map<Integer, String> userNameMap = getApproveUserMap(userIds);
        // 排班
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        // 打卡
        Map<Date, List<AttendancePunchRecordVO>> attendancePunchRecordMap = getPunchRecordMapGroupByDate(userId, orgId, betweenDate, andDate);

        List<AttendanceFieldMinuteVO> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hsSdf = new SimpleDateFormat("HH:mm");

        fieldInfoVOS.forEach(fieldInfoVO -> {
            AttendanceFieldMinuteVO fieldMinuteVO = new AttendanceFieldMinuteVO();
            Date startTime = fieldInfoVO.getStartTime();
            Date endTime = fieldInfoVO.getEndTime();
            String dateStr = sdf.format(startTime);
            Date date;
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
            }
            StringBuilder sb = new StringBuilder();
            List<EmployeeScheduleVO> employeeSchedules = employeeScheduleMap.get(date);
            if (employeeSchedules!=null && !employeeSchedules.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeSchedules) {
                    putEmployeeScheduleName(employeeScheduleVO, sb);
                }
            }
            List<AttendancePunchRecordVO> punchRecords = attendancePunchRecordMap.get(date);
            long diff = 0;
            Date onPunchTime = null;
            Date offPunchTime = null;
            StringBuilder address = new StringBuilder();
            if (punchRecords==null || punchRecords.isEmpty()) {// 被覆盖的外勤
                diff = endTime.getTime() - startTime.getTime();
            } else {
                for (AttendancePunchRecordVO punchRecord : punchRecords) {
                    if (punchRecord.getIsPunch()== AttendanceIsPunchEnum.PUNCHED.getCode()) {
                        Date sTime = punchRecord.getStartTime();
                        Date eTime = punchRecord.getEndTime();
                        Byte punchType = punchRecord.getPunchType();
                        if (punchType == AttendanceTypeEnum.ONDUTY.getCode()) {
                            onPunchTime = punchRecord.getPunchTime();
                            if (onPunchTime.after(sTime)) {
                                sTime = onPunchTime;
                            }
                        } else {
                            offPunchTime = punchRecord.getPunchTime();
                            if (offPunchTime.before(eTime)) {
                                eTime = offPunchTime;
                            }
                        }
                        if (address.length() > 0) {
                            address.append("、");
                        }
                        address.append(punchRecord.getPunchAddress());
                        diff += eTime.getTime() - sTime.getTime();
                    }
                }
            }
            String approvalName = userNameMap.get(fieldInfoVO.getApprovalPeopleId());
            fieldMinuteVO.setApprovalUserName(approvalName);
            fieldMinuteVO.setMinutes(DateUtil.micro2Min(diff));
            fieldMinuteVO.setPunchAddresss(address.toString());
            fieldMinuteVO.setOnPunchTime(onPunchTime);
            fieldMinuteVO.setOffPunchTime(offPunchTime);
            fieldMinuteVO.setEmployeeScheduleName(sb.toString());
            fieldMinuteVO.setApplyDate(fieldInfoVO.getCrtTime());
            fieldMinuteVO.setStartTime(startTime);
            fieldMinuteVO.setEndTime(endTime);
            fieldMinuteVO.setFieldDate(date);
            fieldMinuteVO.setFieldAddress(fieldInfoVO.getFieldAddress());

        });
        return result;
    }

    /**
     * 根据日期分组打卡记录列表
     *
     * @param userId
     * @param orgId
     * @param betweenDate
     * @param andDate
     * @return
     */
    private Map<Date, List<AttendancePunchRecordVO>> getPunchRecordMapGroupByDate(Integer userId, Integer orgId, Date betweenDate, Date andDate) {
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setBetweenDate(betweenDate);
        recordQueryForm.setAndDate(andDate);
        recordQueryForm.setUserId(userId);
        recordQueryForm.setOrgId(orgId);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
        Map<Date, List<AttendancePunchRecordVO>> attendancePunchRecordMap = new HashMap<>(16);
        attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
            Date date = attendancePunchRecordVO.getPunchDate();
            List<AttendancePunchRecordVO> list = attendancePunchRecordMap.get(date);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(attendancePunchRecordVO);
            attendancePunchRecordMap.put(date, list);
        });
        return attendancePunchRecordMap;
    }

    /**
     * 根据日期分组排班列表
     *
     * @param userId
     * @param orgId
     * @param betweenDate
     * @param andDate
     * @return
     */
    private Map<Date, List<EmployeeScheduleVO>> getEmployeeScheduleMapGroupByDate(Integer userId, Integer orgId, Date betweenDate, Date andDate) {
        Map<Date, List<EmployeeScheduleVO>> result = new HashMap<>(16);
        EmployeeScheduleQueryForm scheduleQueryForm = new EmployeeScheduleQueryForm();
        scheduleQueryForm.setUserId(userId);
        scheduleQueryForm.setBetweenWorkDate(betweenDate);
        scheduleQueryForm.setAndWorkDate(andDate);
//        scheduleQueryForm.setClinicId(orgId);
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(scheduleQueryForm);
        employeeScheduleVOS.forEach(employeeScheduleVO -> {
            Date date = employeeScheduleVO.getWorkDate();
            List<EmployeeScheduleVO> list = result.get(date);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(employeeScheduleVO);
            result.put(date, list);
        });
        return result;
    }

    /**
     * 获取用户名
     *
     * @param userIds
     * @return
     */
    private Map<Integer, String> getApproveUserMap(Collection<Integer> userIds) {
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setUserIds(userIds);
        model.setWorkStatus(new Byte[]{0, 1, 3});
        List<SysUserInfoDetail> users = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        Map<Integer, String> result = new HashMap<>(users.size());
        users.forEach(sysUserInfoDetail -> {
            result.put(sysUserInfoDetail.getUserId(), sysUserInfoDetail.getUsername());
        });
        return result;
    }

    /**
     * 获取标签名称
     *
     * @param source
     * @return
     */
    private String getSourceName(Byte source) {
        String result = "";
        if (source == null) {
            return result;
        }
        result = "【请假】";
        if (source==AttendanceSourceEnum.WORK_SCHEDULE.getCode()) {
            result = "【上班】";
        } else if (source==AttendanceSourceEnum.REST_SCHEDULE.getCode()) {
            result = "【休息】";
        } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
            result = "【加班】";
        } else if (source==AttendanceSourceEnum.FIELD.getCode()) {
            result = "【外勤】";
        }
        return result;
    }
}
