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
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.employee_attend.AttendancePunchRecord;
import com.yunya.modules.employeeattend.enums.AttendanceSourceEnum;
import com.yunya.modules.employeeattend.enums.AttendanceStateEnum;
import com.yunya.modules.employeeattend.enums.AttendanceStatusEnum;
import com.yunya.modules.employeeattend.enums.AttendanceTypeEnum;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.mapper.AttendancePunchRecordMapper;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    /** 注入对象 */
    @Autowired
    private FieldInfoBiz fieldInfoBiz;
    /** 注入对象 */
    @Autowired
    private AttendanceDeviceBindingBiz attendanceDeviceBindingBiz;
    /** 注入对象 */
    @Autowired
    private WorkOvertimeInfoBiz workOvertimeInfoBiz;

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
        if (onDutyPunchRecord.getIsPunch() == 1) {//已打卡
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
            onDutyPunchItem.setPunchType((byte) 0);
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
        if (offDutyPunchRecord.getIsPunch() == 1) {//已打卡
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
            offDutyPunchItem.setPunchType((byte) 1);
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
        Byte punchType = punchItem.getPunchStatus();
        if (AttendanceStatusEnum.UNVALID_PUNCH.getCode() != punchType) {
            if (isPunch == 0) {
                if (now.after(startTime)) {
                    punchType = 1;//迟到打卡
                } else {
                    punchType = 0;//上班打卡
                }
            } else {
                punchItem = attendancePunchRecordVOS.get(attendancePunchRecordVOS.size()-1);
                if (now.after(endTime)) {
                    punchType = 2;//早退打卡
                } else {
                    punchType = 3;//下班打卡
                }
            }
        }
        result.setSource(punchItem.getSource());
        result.setId(punchItem.getId());
        result.setOrgId(punchItem.getOrgId());
        result.setPunchStatus(punchType);
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
        if (onDutyIspunch==0 && oldPunchRecord.getPunchType()==AttendanceTypeEnum.OFFDUTY.getCode()) {
            throw new ClientServiceException("未打上班卡，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        Byte oldPunchStatus = oldPunchRecord.getPunchStatus();
        if ((AttendanceStatusEnum.ONDUTY_PUNCH.getCode()==punchStatus||AttendanceStatusEnum.LATER_PUNCH.getCode()==punchStatus)
                && (AttendanceStatusEnum.ONDUTY_PUNCH.getCode()==oldPunchStatus||AttendanceStatusEnum.LATER_PUNCH.getCode()==oldPunchStatus)) {// 上班更新不允许
            throw new ClientServiceException("上班卡已打，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        attendancePunchRecord.setIsPunch((byte) 1);
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
            Byte punchType = attendancePunchRecordVO.getPunchType();
            if (punchType == 0) {
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
        if (firstIsPunch == 0) {// 上班未打卡，则0;
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
        if (lastIsPunch == 0) {// 下班未打卡，待确认
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
        // 打卡记录
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setUserId(userId);
//        recordQueryForm.setPunchStatusList(new Byte[]{0,1,2,3});
        recordQueryForm.setBetweenDate(firstDate);
        recordQueryForm.setAndDate(endDate);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordList(recordQueryForm);
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
            if (isPunch == 0) {
                Integer count = unPunchCount.get(date);
                if (count == null) {
                    count = 0;
                }
                unPunchCount.put(date, ++count);
                temp = "缺卡";
            } else {
                if (punchStatus==0 || punchStatus==2) {//正常
                    temp = "正常";
                } else if (punchStatus == 1) {
                    temp = "迟到";
                } else if (punchStatus == 3) {
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
            /*Integer count = unPunchCount.get(date);
            if (count!=null && 2==count) {
                attendanceCalendarInfoVO.setName("旷工");
                attendanceCalendarInfoVO.setType(exception);
            }*/
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
//    public AttendanceStatisticsVO punchRecordByMonth(String dateStr) {
//        AttendanceStatisticsVO result = new AttendanceStatisticsVO();
//        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
//        List<Date> dateList = DateUtil.getMonthFullDay(dateStr);
//        Date firstDate = dateList.get(0);
//        Date endDate = dateList.get(dateList.size()-1);
//        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
//        employeeScheduleQueryForm.setBetweenWorkDate(firstDate);
//        employeeScheduleQueryForm.setAndWorkDate(endDate);
//        employeeScheduleQueryForm.setUserId(userId);
//        // 班次列表
//        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
//        Map<Integer, EmployeeScheduleVO> restMap = new LinkedHashMap<>(employeeScheduleVOS.size());
//        Map<Integer, EmployeeScheduleVO> workMap = new LinkedHashMap<>(employeeScheduleVOS.size());
//        List<Integer> orgIds = new ArrayList<>();
//        employeeScheduleVOS.forEach(employeeScheduleVO->{
//            Integer orgId = employeeScheduleVO.getClinicId();
//            if (!orgIds.contains(orgId)) {
//                orgIds.add(orgId);
//            }
//            if (REST.equals(employeeScheduleVO.getType())) {
//                restMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
//            } else {
//                workMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
//            }
//        });
//        List<OrganizationInfoDetail> orgList = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
//        Map<Integer, String> orgMap = new HashMap<>(orgList.size());
//        orgList.forEach(organizationInfo -> {
//            orgMap.put(organizationInfo.getId(), organizationInfo.getName());
//        });
//
//        // 考勤列表（包含按天请假）
//        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
//        queryForm.setUserId(userId);
//        queryForm.setBetweenDate(firstDate);
//        queryForm.setAndDate(endDate);
//        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordWithScheduleIdList(queryForm);
//
//        // 按班次请假
//        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
//        leaveQueryForm.setUserId(userId);
//        leaveQueryForm.setBetweenStartDate(firstDate);
//        leaveQueryForm.setAndStartDate(endDate);
//        leaveQueryForm.setVacationStatus(0);
//        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
//        List<AttendancePunchRecordVO> leaveStatisticsList = new ArrayList<>(leaveInfoVOS.size());
//        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
//            AttendancePunchRecordVO leaveStatistics = new AttendancePunchRecordVO();
//            Date startTime = leaveInfoVO.getStartTime();
//            Date endTime = leaveInfoVO.getEndTime();
//            leaveStatistics.setPunchDate(startTime);
//            EmployeeScheduleVO employeeScheduleVO = workMap.get(leaveInfoVO.getScheduleId());
//            if (employeeScheduleVO != null) {
//                leaveStatistics.setName("按班次请假");
//                Integer orgId = employeeScheduleVO.getClinicId();
//                leaveStatistics.setOrgName(orgMap.get(orgId));
//            }
//            long diff = endTime.getTime()-startTime.getTime();
//            leaveStatistics.setMinutes(DateUtil.micro2HourMin(diff));
//            leaveStatisticsList.add(leaveStatistics);
//        }
//
//        // 外勤
//        FieldInfoQueryForm fieldQueryForm = new FieldInfoQueryForm();
//        fieldQueryForm.setUserId(userId);
//        fieldQueryForm.setBetweenDate(firstDate);
//        fieldQueryForm.setAndDate(endDate);
//        fieldQueryForm.setApprpvalStatus(1);
//        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldQueryForm);
//        List<AttendancePunchRecordVO> fieldStatisticsList = new ArrayList<>(fieldInfoVOS.size());
//        Map<Integer, FieldInfoVO> map = new HashMap<>(fieldInfoVOS.size());
//        fieldInfoVOS.forEach(fieldInfoVO -> map.put(fieldInfoVO.getId(), fieldInfoVO));
//
//        int attendanceNum = 0;
//        List<AttendancePunchRecordVO> lateStatisticsList = new ArrayList<>(30);
//        List<AttendancePunchRecordVO> earlyStatisticsList = new ArrayList<>(30);
//        List<AttendancePunchRecordVO> workOvertimeStatisticsList = new ArrayList<>(30);
//        List<AttendancePunchRecordVO> unpunchStatisticsList = new ArrayList<>(30);
//        List<AttendancePunchRecordVO> invalidStatisticsList = new ArrayList<>(30);
//        List<AttendancePunchDateVO> attendancePunchRecordVOList = punchRecord2PunchDate(attendancePunchRecordVOS);
//
//        for (AttendancePunchDateVO attendancePunchDateVO : attendancePunchRecordVOList) {
//            AttendancePunchRecordVO attendancePunchRecordVO = new AttendancePunchRecordVO();
//            Byte onDutyStatus = attendancePunchDateVO.getOnDutyStatus();
//            Byte offDutyStatus = attendancePunchDateVO.getOffDutyStatus();
//            Byte leave = attendancePunchDateVO.getLeave();
//            Date punchDate = attendancePunchDateVO.getPunchDate();
//            attendancePunchRecordVO.setPunchDate(punchDate);
//            Date onDutyStartTime = attendancePunchDateVO.getOnDutyStartTime();
//            Date onDutyEndTime = attendancePunchDateVO.getOffDutyEndTime();
//            Date onDutyPunchTime = attendancePunchDateVO.getOnDutyPunchTime();
//            Integer onDutyorgId = attendancePunchDateVO.getOnDutyOrgId();
//            String onOrgName = orgMap.get(onDutyorgId);
//            if (onDutyStatus != null) {
//                if (onDutyStatus == 0) { // 上午正常打卡
//                    attendanceNum++;
//                } else if (onDutyStatus == 1) {// 上午迟到
//                    attendanceNum++;
//                    long diff = onDutyPunchTime.getTime() - onDutyStartTime.getTime();
//                    attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                    attendancePunchRecordVO.setOrgName(onOrgName);
//                    attendancePunchRecordVO.setPunchTime(onDutyPunchTime);
//                    lateStatisticsList.add(attendancePunchRecordVO);
//                } else if (onDutyStatus == 4) { // 上午无效卡
//                    attendancePunchRecordVO.setOrgName(onOrgName);
//                    attendancePunchRecordVO.setPunchType(AttendanceTypeEnum.ONDUTY.getCode());
//                    invalidStatisticsList.add(attendancePunchRecordVO);
//                } else if (onDutyStatus == 5) { // 上午未打卡
//                    attendancePunchRecordVO.setName(attendancePunchDateVO.getOnDutyName());
//                    attendancePunchRecordVO.setPunchType(AttendanceTypeEnum.ONDUTY.getCode());
//                    attendancePunchRecordVO.setOrgName(onOrgName);
//                    attendancePunchRecordVO.setStartTime(onDutyStartTime);
//                    unpunchStatisticsList.add(attendancePunchRecordVO);
//                }
//            }
//
//            Date offDutyStartTime = attendancePunchDateVO.getOffDutyStartTime();
//            Date offDutyEndTime = attendancePunchDateVO.getOffDutyEndTime();
//            Date offDutyPunchTime = attendancePunchDateVO.getOffDutyPunchTime();
//            Integer offDutyorgId = attendancePunchDateVO.getOffDutyOrgId();
//            String offOrgName = orgMap.get(offDutyorgId);
//            if (offDutyStatus != null) {
//                if (offDutyStatus == 2) { // 下午正常打卡
//                    attendanceNum++;
//                } else if (offDutyStatus == 3) {// 下午迟到
//                    attendanceNum++;
//                    long diff = offDutyEndTime.getTime() - offDutyPunchTime.getTime();
//                    attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                    attendancePunchRecordVO.setOrgName(offOrgName);
//                    attendancePunchRecordVO.setPunchTime(offDutyPunchTime);
//                    earlyStatisticsList.add(attendancePunchRecordVO);
//                } else if (offDutyStatus == 4) { // 下午无效卡
//                    attendancePunchRecordVO.setOrgName(offOrgName);
//                    attendancePunchRecordVO.setPunchType(AttendanceTypeEnum.OFFDUTY.getCode());
//                    invalidStatisticsList.add(attendancePunchRecordVO);
//                } else if (offDutyStatus == 5) { // 下午未打卡
//                    attendancePunchRecordVO.setName(attendancePunchDateVO.getOffDutyName());
//                    attendancePunchRecordVO.setPunchType(AttendanceTypeEnum.OFFDUTY.getCode());
//                    attendancePunchRecordVO.setOrgName(offOrgName);
//                    attendancePunchRecordVO.setEndTime(offDutyEndTime);
//                    unpunchStatisticsList.add(attendancePunchRecordVO);
//                }
//            }
//
//
//            if (offDutyStatus!=null && offDutyStatus!=5) {
//                Byte onDutySource = attendancePunchDateVO.getOnDutySource();
//                Byte offDutySource = attendancePunchDateVO.getOffDutySource();
//                //加班
//                Byte workDateOvertime = attendancePunchDateVO.getWorkDateOvertime();
//                if (workDateOvertime!=null) {
//                    long diff = onDutyEndTime.getTime() - onDutyStartTime.getTime() + offDutyEndTime.getTime() - offDutyStartTime.getTime();
//                    if (onDutySource == 4 && onDutySource == offDutySource) {// 上班卡是加班、下班卡是加班
//                        diff = onDutyEndTime.getTime() - onDutyPunchTime.getTime() + offDutyPunchTime.getTime() - offDutyStartTime.getTime();
//                    }
//                    if (workDateOvertime == 2) {// 全天加班
//                        attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                        attendancePunchRecordVO.setStartTime(onDutyStartTime);
//                        attendancePunchRecordVO.setEndTime(offDutyEndTime);
//                        attendancePunchRecordVO.setOrgName(offOrgName);
//                        workOvertimeStatisticsList.add(attendancePunchRecordVO);
//                    } else {
//                        if (workDateOvertime == 0) {//上午加班
//                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                            attendancePunchRecordVO.setStartTime(onDutyStartTime);
//                            attendancePunchRecordVO.setEndTime(onDutyEndTime);
//                            attendancePunchRecordVO.setOrgName(onOrgName);
//                            workOvertimeStatisticsList.add(attendancePunchRecordVO);
//                        }
//                        if (workDateOvertime == 1) {// 下午加班
//                            attendancePunchRecordVO = new AttendancePunchRecordVO();
//                            attendancePunchRecordVO.setPunchDate(punchDate);
//                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                            attendancePunchRecordVO.setStartTime(offDutyStartTime);
//                            attendancePunchRecordVO.setEndTime(offDutyEndTime);
//                            attendancePunchRecordVO.setOrgName(offOrgName);
//                            workOvertimeStatisticsList.add(attendancePunchRecordVO);
//                        }
//                    }
//                }
//                //外勤
//                Byte field = attendancePunchDateVO.getField();
//                if (field != null) {
//                    Integer onDutySourceId = attendancePunchDateVO.getOnDutySourceId();
//                    Integer offDutySourceId = attendancePunchDateVO.getOffDutySourceId();
//                    FieldInfoVO field1 = map.get(onDutySourceId);
//                    FieldInfoVO field2 = map.get(offDutySourceId);
//                    long diff = 0;
//                    if (field1 != null) {
//                        diff = field1.getEndTime().getTime() - field1.getStartTime().getTime();
//                        if (onDutySource == 5 && onDutySource == offDutySource) {
//                            diff = field1.getEndTime().getTime() - onDutyPunchTime.getTime() + offDutyPunchTime.getTime() - field2.getStartTime().getTime();
//                        }
//                    }
//                    if (onDutySourceId != offDutySourceId && field2 != null) {
//                        diff += field2.getEndTime().getTime() - field2.getStartTime().getTime();
//                        if (onDutySource == 5 && onDutySource == offDutySource) {
//                            diff += offDutyPunchTime.getTime() - field2.getStartTime().getTime();
//                        }
//                    }
//                    if (field == 2) {//全天外勤
//                        attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                        attendancePunchRecordVO.setStartTime(onDutyStartTime);
//                        attendancePunchRecordVO.setEndTime(offDutyEndTime);
//                        attendancePunchRecordVO.setOrgName(onOrgName);
//                        fieldStatisticsList.add(attendancePunchRecordVO);
//                    } else {
//                        if (field == 0) {//上午外勤
//                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                            attendancePunchRecordVO.setStartTime(onDutyStartTime);
//                            attendancePunchRecordVO.setEndTime(onDutyEndTime);
//                            attendancePunchRecordVO.setOrgName(onOrgName);
//                            fieldStatisticsList.add(attendancePunchRecordVO);
//                        }
//                        if (field == 1) {//下午外勤
//                            attendancePunchRecordVO = new AttendancePunchRecordVO();
//                            attendancePunchRecordVO.setPunchDate(punchDate);
//                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
//                            attendancePunchRecordVO.setStartTime(offDutyStartTime);
//                            attendancePunchRecordVO.setEndTime(offDutyEndTime);
//                            attendancePunchRecordVO.setOrgName(offOrgName);
//                            fieldStatisticsList.add(attendancePunchRecordVO);
//                        }
//                    }
//                }
//            }
//
//            if (leave!=null && leave==2) {// 全天请假
//                attendancePunchRecordVO.setOrgName(onOrgName);
//                attendancePunchRecordVO.setName("按天请假");
//                attendancePunchRecordVO.setPunchDate(attendancePunchDateVO.getPunchDate());
//                attendancePunchRecordVO.setMinutes(DAY_LEAVE_MINUTE);
//                leaveStatisticsList.add(attendancePunchRecordVO);
//            }
//        }
//
//        List<AttendancePunchRecordVO> restStatisticeList = new ArrayList<>(restMap.size());
//        restMap.forEach((id, employeeScheduleVO) ->{
//            AttendancePunchRecordVO restStatistics = new AttendancePunchRecordVO();
//            Integer orgId = employeeScheduleVO.getClinicId();
//            String orgName = orgMap.get(orgId);
//            restStatistics.setOrgName(orgName);
//            restStatistics.setPunchDate(employeeScheduleVO.getWorkDate());
//            restStatisticeList.add(restStatistics);
//        });
//        Collections.sort(leaveStatisticsList, Comparator.comparing(AttendancePunchRecordVO::getPunchDate));
//        result.setWorkOvertimeNum(workOvertimeStatisticsList.size());
//        result.setUnpunchNum(unpunchStatisticsList.size());
//        result.setRestNum(restStatisticeList.size());
//        result.setFieldNum(fieldStatisticsList.size());
//        result.setLeaveNum(leaveStatisticsList.size());
//        result.setLateNum(lateStatisticsList.size());
//        result.setAttendanceNum(attendanceNum);
//        result.setEarlyNum(earlyStatisticsList.size());
//        result.setInvalidNum(invalidStatisticsList.size());
//        result.setRestStatisticsList(restStatisticeList);
//        result.setLateStatisticsList(lateStatisticsList);
//        result.setEarlyStatisticsList(earlyStatisticsList);
//        result.setLeaveStatisticsList(leaveStatisticsList);
//        result.setWorkOvertimeStatisticsList(workOvertimeStatisticsList);
//        result.setInvalidStatisticsList(invalidStatisticsList);
//        result.setUnpunchStatisticsList(unpunchStatisticsList);
//        result.setFieldStatisticsList(fieldStatisticsList);
//        return result;
//    }

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

        // 考勤列表（包含按天请假）
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setUserId(userId);
        queryForm.setBetweenDate(firstDate);
        queryForm.setAndDate(endDate);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordWithScheduleIdList(queryForm);

        // 按班次请假
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setUserId(userId);
        leaveQueryForm.setBetweenStartDate(firstDate);
        leaveQueryForm.setAndStartDate(endDate);
        leaveQueryForm.setVacationStatus(0);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        List<AttendancePunchRecordVO> leaveStatisticsList = new ArrayList<>(leaveInfoVOS.size());
        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
            AttendancePunchRecordVO leaveStatistics = new AttendancePunchRecordVO();
            Date startTime = leaveInfoVO.getStartTime();
            Date endTime = leaveInfoVO.getEndTime();
            leaveStatistics.setPunchDate(startTime);
            EmployeeScheduleVO employeeScheduleVO = workMap.get(leaveInfoVO.getScheduleId());
            if (employeeScheduleVO != null) {
                leaveStatistics.setName("按班次请假");
                Integer orgId = employeeScheduleVO.getClinicId();
                leaveStatistics.setOrgName(orgMap.get(orgId));
            }
            long diff = endTime.getTime()-startTime.getTime();
            leaveStatistics.setMinutes(DateUtil.micro2HourMin(diff));
            leaveStatisticsList.add(leaveStatistics);
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
            if (isPunch == 0) {
                if (source == AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {// 按天请假
                    AttendancePunchRecordVO leaveStatistics = new AttendancePunchRecordVO();
                    leaveStatistics.setOrgName(orgName);
                    leaveStatistics.setName("按天请假");
                    leaveStatistics.setPunchDate(attendancePunchRecordVO.getPunchDate());
                    leaveStatistics.setMinutes(DAY_LEAVE_MINUTE);
                    leaveStatisticsList.add(leaveStatistics);
                } else {
                    attendancePunchRecordVO.setOrgName(orgName);
                    unpunchStatisticsList.add(attendancePunchRecordVO);
                }
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
                if (isPunch == 0) {
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
        setQueryFormDate(queryForm);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.selectAttendanceStatisticsPunchRecord(queryForm);
        List<AttendancePunchDateVO> attendancePunchDateVOS = punchRecord2PunchDate(attendancePunchRecordVOS);
        Table<Integer,Integer, Long> workDateMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> workDateOvertimeMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> workDateOvertime30MinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> restDateOvertimeMap = HashBasedTable.create();
        Table<Integer,Integer, Long> fieldMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> leaveMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> laterNumMap = HashBasedTable.create();
        Table<Integer,Integer, Integer> earlyNumMap = HashBasedTable.create();
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
            long diff = DAY_LEAVE_MILLSEC;
            if (vacationStatus == 1) { // 按班次请假
                diff = leaveInfoVO.getEndTime().getTime() - leaveInfoVO.getStartTime().getTime();
                List<LeaveInfoVO> list = leaveInfoMap.get(userId, orgId);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(leaveInfoVO);
                leaveInfoMap.put(userId, orgId, list);
            }

            Long length = leaveMinuteMap.get(userId, orgId);
            if (length == null) {
                length = 0L;
            }
            leaveMinuteMap.put(userId, orgId, length+diff);
            isFullMap.put(userId, orgId, false);
        }

        Set<Integer> fieldIds = new HashSet<>();
        for (AttendancePunchDateVO attendancePunchDateVO : attendancePunchDateVOS) {
            Integer userId = attendancePunchDateVO.getUserId();
            Date date = attendancePunchDateVO.getPunchDate();
            Integer onDutyOrgId = attendancePunchDateVO.getOnDutyOrgId();
            Byte onDutyStatus = attendancePunchDateVO.getOnDutyStatus();
            Date onPunchTime = attendancePunchDateVO.getOnDutyPunchTime();
            Date onStartTime = attendancePunchDateVO.getOnDutyStartTime();
            Date onEndTime = attendancePunchDateVO.getOnDutyEndTime();
            Date offStartTime = attendancePunchDateVO.getOffDutyStartTime();
            Date offPunchTime = attendancePunchDateVO.getOffDutyPunchTime();
            Date offEndTime = attendancePunchDateVO.getOffDutyEndTime();
            // 上班打卡
            boolean hasAttendance = false;
            switch (onDutyStatus) {
                case 0: {
                    hasAttendance = true;
                    Long length = workDateMinuteMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = computeWithoutLeave(date, onEndTime, onStartTime, userId, onDutyOrgId, leaveInfoMap);
                    workDateMinuteMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 1: {
                    hasAttendance = true;
                    incrNum(laterNumMap, userId, onDutyOrgId);
                    isFullMap.put(userId, onDutyOrgId, false);
                    Long length = workDateMinuteMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = computeWithoutLeave(date, onEndTime, onPunchTime, userId, onDutyOrgId, leaveInfoMap);
                    workDateMinuteMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 4: {
                    incrNum(invalidNumMap, userId, onDutyOrgId);
                    isFullMap.put(userId, onDutyOrgId, false);
                    break;
                }
                case 5: {
                    incrNum(unpunchNumMap, userId, onDutyOrgId);
                    isFullMap.put(userId, onDutyOrgId, false);
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
            Integer offDutyOrgId = attendancePunchDateVO.getOffDutyOrgId();
            switch (offDutyStatus) {
                case 2:{
                    hasAttendance = true;
                    Long length = workDateMinuteMap.get(userId, offDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = computeWithoutLeave(date, offEndTime, offStartTime, userId, offDutyOrgId, leaveInfoMap);
                    workDateMinuteMap.put(userId, offDutyOrgId, length+diff);
                    if (offPunchTime.after(offEndTime)) {
                        length = workDateOvertimeMinuteMap.get(userId, offDutyOrgId);
                        if (length == null) {
                            length = 0L;
                        }
                        diff = offPunchTime.getTime() - offEndTime.getTime();
                        workDateOvertimeMinuteMap.put(userId, offDutyOrgId, length+diff);
                        if (DateUtil.micro2Min(diff) >= 30) {
                            length = workDateOvertime30MinuteMap.get(userId, offDutyOrgId);
                            if (length == null) {
                                length = 0L;
                            }
                            workDateOvertime30MinuteMap.put(userId, offDutyOrgId, length+diff);
                        }
                    }
                    break;
                }
                case 3:{
                    incrNum(earlyNumMap, userId, offDutyOrgId);
                    hasAttendance = true;
                    isFullMap.put(userId, offDutyOrgId, false);
                    Long length = workDateMinuteMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = computeWithoutLeave(date, offPunchTime, offStartTime, userId, offDutyOrgId, leaveInfoMap);
                    workDateMinuteMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 4:{
                    incrNum(invalidNumMap, userId, offDutyOrgId);
                    isFullMap.put(userId, offDutyOrgId, false);
                    break;
                }
                case 5:{
                    incrNum(unpunchNumMap, userId, offDutyOrgId);
                    isFullMap.put(userId, offDutyOrgId, false);
                    break;
                }
                default:
            }
            if (hasAttendance) {
                incrNum(attendancNumMap, userId, offDutyOrgId);
            }
            Byte workOvertime = attendancePunchDateVO.getWorkDateOvertime();
            if (workOvertime != null) {
                switch (workOvertime) {
                    case 0: {
                        Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                        if (length == null) {
                            length = 0L;
                        }
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        long diff = onEndTime.getTime() - startTime.getTime();
                        restDateOvertimeMap.put(userId, onDutyOrgId, length + diff);
                        break;
                    }
                    case 1: {
                        Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                        if (length == null) {
                            length = 0L;
                        }
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - offStartTime.getTime();
                        restDateOvertimeMap.put(userId, onDutyOrgId, length + diff);
                        break;
                    }
                    case 2: {
                        Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                        if (length == null) {
                            length = 0L;
                        }
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - startTime.getTime();
                        restDateOvertimeMap.put(userId, onDutyOrgId, length + diff);
                        break;
                    }
                }
            }
            Byte field = attendancePunchDateVO.getField();
            Integer onSourceId = attendancePunchDateVO.getOnDutySourceId();
            Integer offSourceId = attendancePunchDateVO.getOffDutySourceId();
            employeeScheduleVOMap.remove(onSourceId);
            employeeScheduleVOMap.remove(offSourceId);
            if (field != null) {
                fieldIds.add(onSourceId);
                fieldIds.add(offSourceId);
                switch (field) {
                    case 0: {
                        Long length = fieldMinuteMap.get(userId, onDutyOrgId);
                        if (length == null) {
                            length = 0L;
                        }
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        long diff = onEndTime.getTime() - startTime.getTime();
                        fieldMinuteMap.put(userId, onDutyOrgId, length + diff);
                        break;
                    }
                    case 1: {
                        Long length = fieldMinuteMap.get(userId, onDutyOrgId);
                        if (length == null) {
                            length = 0L;
                        }
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - offStartTime.getTime();
                        fieldMinuteMap.put(userId, onDutyOrgId, length + diff);
                        break;
                    }
                    case 2: {
                        Long length = fieldMinuteMap.get(userId, onDutyOrgId);
                        if (length == null) {
                            length = 0L;
                        }
                        Date startTime = onPunchTime;
                        if (startTime.before(onStartTime)) {
                            startTime = onStartTime;
                        }
                        Date endTime = offPunchTime;
                        if (endTime.after(offEndTime)) {
                            endTime = offEndTime;
                        }
                        long diff = endTime.getTime() - startTime.getTime();
                        fieldMinuteMap.put(userId, onDutyOrgId, length + diff);
                        break;
                    }
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
                Long length = fieldMinuteMap.get(userId, orgId);
                if (length == null) {
                    length = 0L;
                }
                long diff = fieldInfoVO.getEndTime().getTime() - fieldInfoVO.getStartTime().getTime();
                fieldMinuteMap.put(userId, orgId, length + diff);
            }
        });
        //外勤覆盖的上班班次（覆盖全部、只覆盖上班卡、只覆盖下班卡）
        employeeScheduleVOMap.forEach((id, employeeScheduleVO)->{
            Integer userId = employeeScheduleVO.getEmployeeId();
            Integer orgId = employeeScheduleVO.getClinicId();
            Long length = workDateMinuteMap.get(userId, orgId);
            if (length == 0) {
                length = 0L;
            }
            long diff = employeeScheduleVO.getFirstStartTime().getTime()-employeeScheduleVO.getFirstEndTime().getTime();
            workDateMinuteMap.put(userId, orgId, length + diff);
        });

        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(queryForm.getWhetherPage());
        model.setPageNum(queryForm.getPageNum());
        model.setPageSize(queryForm.getPageSize());
        if (queryForm.getOrgId() != null) {
            model.setOrgIds(Arrays.asList(queryForm.getOrgId()));
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
            Long leaveMinute = leaveMinuteMap.get(userId, orgId);
            if (leaveMinute == null) {
                leaveMinute = 0L;
            }
            statistics.setLeaveMinute(DateUtil.micro2Min(leaveMinute));
            Long fieldMinute = fieldMinuteMap.get(userId, orgId);
            if (fieldMinute == null) {
                fieldMinute = 0L;
            }
            statistics.setFieldMinute(DateUtil.micro2Min(fieldMinute));
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
            Long workDateMinute = workDateMinuteMap.get(userId, orgId);
            if (workDateMinute == null) {
                workDateMinute = 0L;
            }
            statistics.setWorkDateMinute(DateUtil.micro2Min(workDateMinute));
            Boolean isFull = isFullMap.get(userId, orgId);
            statistics.setIsFull(isFull==null?true:isFull);
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
            Integer earlyNum = earlyNumMap.get(userId, orgId);
            if (earlyNum == null) {
                earlyNum = 0;
            }
            statistics.setEarlyNum(earlyNum);
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
        Date sDateTime = DateUtil.timetoDate(date, startTime);
        Date eDateTime = DateUtil.timetoDate(date, endTime);
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
                    throw new ClientServiceException("日期参数格式转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
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
        HashBasedTable<Date, String, List<AttendancePunchRecordVO>> dateMap = HashBasedTable.create();
        attendancePunchRecordVOS.forEach(attendancePunchRecordVO -> {
            Date punchDate = attendancePunchRecordVO.getPunchDate();
            String key = attendancePunchRecordVO.getUserId() + "," + attendancePunchRecordVO.getOrgId();
            List<AttendancePunchRecordVO> list = dateMap.get(punchDate, key);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(attendancePunchRecordVO);
            dateMap.put(punchDate, key, list);
        });
        Map<String, Map<Date, List<AttendancePunchRecordVO>>> map = dateMap.columnMap();
        map.forEach((key, value)->{
            value.forEach((date, list)->{
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
                    punchDate.setPunchDate(date);
                    if (punchType == 0) {
                        punchDate.setOnDutyPunchTime(punchTime);
                        punchDate.setOnDutyOrgId(orgId);
                        punchDate.setOnDutyName(name);
                        if (source==AttendanceSourceEnum.LEAVE_BYDAY.getCode()) {
                            punchDate.setLeave((byte) 3);
                        } else if (source==AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode()) {
                            byte leave = 0;
                            if (punchDate.getLeave() != null) {
                                leave = 2;
                            }
                            punchDate.setLeave(leave);
                        } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                            byte workDateOvertime = 0;
                            if (punchDate.getWorkDateOvertime() != null) {
                                workDateOvertime = 2;
                            }
                            punchDate.setWorkDateOvertime(workDateOvertime);
                        } else if (source==AttendanceSourceEnum.FIELD.getCode()) {
                            byte field = 0;
                            if (punchDate.getField()!=null) {
                                field = 2;
                            }
                            punchDate.setField(field);
                        }
                        punchDate.setOnDutySourceId(sourceId);
                        punchDate.setOnDutyStartTime(vo.getStartTime());
                        punchDate.setOnDutyEndTime(vo.getEndTime());
                        Byte status = 5;
                        if (isPunch != 0) {
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
                            byte leave = 0;
                            if (punchDate.getLeave() != null) {
                                leave = 2;
                            }
                            punchDate.setLeave(leave);
                        } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                            byte workDateOvertime = 1;
                            if (punchDate.getWorkDateOvertime()!=null) {
                                workDateOvertime = 2;
                            }
                            punchDate.setWorkDateOvertime(workDateOvertime);
                        } else if (source==AttendanceSourceEnum.FIELD.getCode()) {
                            byte field = 1;
                            if (punchDate.getField()!=null) {
                                field = 2;
                            }
                            punchDate.setField(field);
                        }
                        punchDate.setOffDutySourceId(sourceId);
                        punchDate.setOffDutyEndTime(vo.getEndTime());
                        punchDate.setOffDutyStartTime(vo.getStartTime());
                        Byte status = 5;
                        if (isPunch != 0) {
                            status = punchStatus;
                        }
                        punchDate.setOffDutyStatus(status);
                    }
                });
                result.add(punchDate);
            });
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

    @Override
    public void insertSelective(AttendancePunchRecord entity) {
        mapper.insertSelective(entity);
    }

    /**
     * 根据条件分页查询考勤汇总导出
     *
     * @param queryForm 查询参数
     */
    public void statisticsPunchRecordExport(HttpServletResponse response, AttendanceStatisticsQueryForm queryForm) throws IOException {
        queryForm.setWhetherPage(false);
        List<AttendanceStatisticsVO> list = statisticsPunchRecord(queryForm);
        ExcelUtil<AttendanceStatisticsVO> excelUtil = new ExcelUtil<>(AttendanceStatisticsVO.class);
        excelUtil.exportExcel(response, list, "考勤汇总表");
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
            recordQueryForm.setIsPunch((byte) 0);
        } else {
            recordQueryForm.setIsPunch((byte) 1);
            recordQueryForm.setPunchStatus(type);
        }
        recordQueryForm.setPunchType((byte) 0);
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
            if (onIsPunch == 0) {
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
            if (offIsPunch == 0) {
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
            String tail = "【请假】";
            if (source==AttendanceSourceEnum.WORK_SCHEDULE.getCode()) {
                tail = "【上班】";
            } else if (source==AttendanceSourceEnum.REST_SCHEDULE.getCode()) {
                tail = "【休息】";
            } else if (source==AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                tail = "【加班】";
            } else if (source==AttendanceSourceEnum.FIELD.getCode()) {
                tail = "【外勤】";
            }
            Date firstStartTime = employeeScheduleVO.getFirstStartTime();
            Date firstEndTime = employeeScheduleVO.getFirstEndTime();
            result.append(employeeScheduleVO.getName()).append("(")
                    .append(sdf.format(firstStartTime)).append("-")
                    .append(sdf.format(firstEndTime)).append(")").append(tail);
        }
        return result;
    }

    /**
     * 根据统计时长类型的分页查询考勤汇总明细
     *
     * @param type 统计次数类型：0-工作日时长，1-工作日加班时长，2-工作日加班超30分钟，3-休息日加班时长，4-请假时长，5-外勤时长
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendancePunchMinuteVO> statisticsPunchRecordByMinute(byte type, AttendanceStatisticsQueryForm queryForm) {
        EmployeeScheduleQueryForm scheduleQueryForm = new EmployeeScheduleQueryForm();
        scheduleQueryForm.setWhetherPage(queryForm.getWhetherPage());
        scheduleQueryForm.setPage(queryForm.getPageNum());
        scheduleQueryForm.setSize(queryForm.getPageSize());
        scheduleQueryForm.setUserId(queryForm.getUserId());
        scheduleQueryForm.setClinicId(queryForm.getOrgId());
        setQueryFormDate(queryForm);
        scheduleQueryForm.setBetweenWorkDate(queryForm.getBetweenDate());
        scheduleQueryForm.setAndWorkDate(queryForm.getAndDate());
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(scheduleQueryForm);
        List<AttendancePunchMinuteVO> result = new ArrayList<>();

        switch (type) {

        }
        return null;
    }
}
