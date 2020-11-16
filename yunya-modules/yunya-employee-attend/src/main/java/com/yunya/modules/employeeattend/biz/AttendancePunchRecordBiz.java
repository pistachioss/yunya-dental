package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import com.yunya.modules.employeeattend.enums.AttendanceSourceEnum;
import com.yunya.modules.employeeattend.enums.AttendanceStateEnum;
import com.yunya.modules.employeeattend.enums.AttendanceStatusEnum;
import com.yunya.modules.employeeattend.enums.AttendanceTypeEnum;
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

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

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
    private WorkOvertimeInfoBiz workOvertimeInfoBiz;
    /** 注入对象 */
    @Autowired
    private AttendanceDeviceBindingBiz attendanceDeviceBindingBiz;

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
        String macAddress = queryForm.getMacAddress();
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
            }
        }
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setUserId(userId);
        recordQueryForm.setPunchDate(new Date(System.currentTimeMillis()));
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
        if (attendancePunchRecordVOS!=null && !attendancePunchRecordVOS.isEmpty()) {
            setOrgName(attendancePunchRecordVOS);
            setCurItemInfo(attendancePunchRecordVOS, result);
            result.setAttendancePunchItemVOS(findPunchItem(attendancePunchRecordVOS));
        } else {

        }
        result.setPunchName(punchName);
        result.setPunchMode(punchMode);
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

        AttendancePunchItemVO onDutyPunchItem = new AttendancePunchItemVO();
        AttendancePunchItemVO offDutyPunchItem = new AttendancePunchItemVO();
        onDutyPunchItem.setOrgName(onDutyPunchRecord.getOrgName());
        onDutyPunchItem.setId(onDutyPunchRecord.getId());
        onDutyPunchItem.setStartTime(onDutyPunchRecord.getStartTime());
        onDutyPunchItem.setEndTime(onDutyPunchRecord.getEndTime());
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
    public AttendancePunchInfoVO punchRecordByDate(Date date) {
        AttendancePunchInfoVO result = new AttendancePunchInfoVO();
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setPunchDate(date);
        queryForm.setUserId(userId);
//        queryForm.setPunchStatusList(new Byte[]{0,1,2,3});
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordList(queryForm);
        List<AttendancePunchItemVO> attendancePunchItemVOS = new ArrayList<>(5);
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
        if (attendancePunchRecordVOS!=null && !attendancePunchRecordVOS.isEmpty()) {
            attendancePunchItemVOS.add(createPunchItem(firstPunchRecord));
            attendancePunchItemVOS.add(createPunchItem(lastPunchRecord));
        }
        long[] computeValue = compute(firstPunchRecord,lastPunchRecord);
        result.setWorkLength(computeValue[0]);
        result.setPunchCount((int) computeValue[1]);
        result.setAttendancePunchItemVOS(attendancePunchItemVOS);
        return result;
    }

    /**
     * 计算工作时长和打卡次数
     * @param firstPunchRecord
     * @param lastPunchRecord
     * @return
     */
    private long[] compute(AttendancePunchRecordVO firstPunchRecord, AttendancePunchRecordVO lastPunchRecord) {
        long[] result = new long[2];
        if (firstPunchRecord == null || lastPunchRecord == null) {
            return result;
        }
        Byte firstIsPunch = firstPunchRecord.getIsPunch();
        Byte lastIsPunch = lastPunchRecord.getIsPunch();
        if (firstIsPunch == 0) {// 上班未打卡，则0;
            return result;
        }
        long workLength = 0;
        Date punchTime = lastPunchRecord.getPunchTime();
        long startTime = 0;
        if (punchTime != null) {
            startTime = punchTime.getTime();
        }
        long length = startTime - firstPunchRecord.getPunchTime().getTime();
        long punchCount = 2;
        punchTime = firstPunchRecord.getPunchTime();
        long endTime = 0;
        if (punchTime != null) {
            endTime = punchTime.getTime();
        }
        if (lastIsPunch == 0) {// 下班未打卡，待确认
            length = firstPunchRecord.getEndTime().getTime() - endTime;
            punchCount = 1;
        }
        workLength = DateUtil.micro2Min(length);
        result[0] = workLength;
        result[1] = punchCount;
        return result;
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
        punchItem.setPunchMode(punchMode);
        Date startTime = attendancePunchRecordVO.getStartTime();
        Date endTime = attendancePunchRecordVO.getEndTime();
        String itemName = attendancePunchRecordVO.getName();
        punchItem.setStartTime(startTime);
        punchItem.setEndTime(endTime);
        punchItem.setItemName(itemName);
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
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findRestEmployeeScheduleListInDate(userId, firstDate, endDate);
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
            if (source == 1) {
                temp = "请假";
            } else if (source == 3) {
                temp = "外勤";
            }
            if (state==null|| !"正常".equals(state)) {
                state = temp;
            }
            if (state != null) {
                stateMap.put(date, state);
            }
        });

        dateList.forEach(date->{
            byte normal = AttendanceStateEnum.NORMAL.getCode();
            byte exception = AttendanceStateEnum.EXCEPTION.getCode();
            byte unknown = AttendanceStateEnum.UNKNOWN.getCode();
            AttendanceCalendarInfoVO attendanceCalendarInfoVO = new AttendanceCalendarInfoVO();
            attendanceCalendarInfoVO.setDate(date);
            if (date.compareTo(now) > 0) {
                attendanceCalendarInfoVO.setName("未开始");
                attendanceCalendarInfoVO.setType(unknown);
            } else {
                attendanceCalendarInfoVO.setName("缺卡");
                attendanceCalendarInfoVO.setType(exception);
                employeeScheduleVOS.forEach(restVO -> {
                    Date workDate = restVO.getWorkDate();
                    if (date.compareTo(workDate) == 0) {
                        attendanceCalendarInfoVO.setName("休息");
                        attendanceCalendarInfoVO.setType(normal);
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
                    case "外勤": {
                        attendanceCalendarInfoVO.setName("外勤");
                        attendanceCalendarInfoVO.setType(exception);
                        break;
                    }
                    default:
                }
            }
            Integer count = unPunchCount.get(date);
            if (count!=null && 2==count) {
                attendanceCalendarInfoVO.setName("旷工");
                attendanceCalendarInfoVO.setType(exception);
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
        AttendanceStatisticsVO result = new AttendanceStatisticsVO();
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        List<Date> dateList = DateUtil.getMonthFullDay(dateStr);
        Date firstDate = dateList.get(0);
        Date endDate = dateList.get(dateList.size()-1);
        // 休息班次列表
        List<EmployeeScheduleVO> restVOS = employeeScheduleBiz.findRestEmployeeScheduleListInDate(userId,firstDate,endDate);
        Map<Integer, EmployeeScheduleVO> restMap = new LinkedHashMap<>(restVOS.size());
        List<Integer> orgIds = new ArrayList<>();
        restVOS.forEach(restVO->{
            Integer orgId = restVO.getClinicId();
            if (!orgIds.contains(orgId)) {
                orgIds.add(orgId);
            }
            restMap.put(restVO.getId(), restVO);
        });
        List<OrganizationInfoDetail> orgList = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
        Map<Integer, String> orgMap = new HashMap<>(orgList.size());
        orgList.forEach(organizationInfo -> {
            orgMap.put(organizationInfo.getId(), organizationInfo.getName());
        });

        // 考勤列表
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setUserId(userId);
        queryForm.setBetweenDate(firstDate);
        queryForm.setAndDate(endDate);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordWithScheduleIdList(queryForm);

        // 请假，跨月份了算哪个月的？
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setUserId(userId);
        leaveQueryForm.setBetweenStartDate(firstDate);
        leaveQueryForm.setAndStartDate(endDate);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        List<AttendancePunchRecordVO> leaveStatisticsList = new ArrayList<>(leaveInfoVOS.size());
        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
            AttendancePunchRecordVO leaveStatistics = new AttendancePunchRecordVO();
//            leaveStatistics.setOrgName();
            Date startTime = leaveInfoVO.getStartTime();
            Date endTime = leaveInfoVO.getEndTime();
            leaveStatistics.setPunchDate(startTime);
//            leaveStatistics.setName();
            long diff = endTime.getTime()-startTime.getTime();
            leaveStatistics.setMinutes(DateUtil.micro2Min(diff));
        }

        // 外勤
        FieldInfoQueryForm fieldQueryForm = new FieldInfoQueryForm();
        fieldQueryForm.setUserId(userId);
        fieldQueryForm.setBetweenDate(firstDate);
        fieldQueryForm.setAndDate(endDate);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldQueryForm);
        Set<Integer> workOvertime = new HashSet<>();
        List<AttendancePunchRecordVO> fieldStatisticsList = new ArrayList<>(fieldInfoVOS.size());
        fieldInfoVOS.forEach(fieldInfoVO -> {
            Date startTime = fieldInfoVO.getStartTime();
            Date endTime = fieldInfoVO.getEndTime();
            long diff = endTime.getTime()-startTime.getTime();
            AttendancePunchRecordVO fieldStatistics = new AttendancePunchRecordVO();
            BeanUtils.copyProperties(fieldInfoVO, fieldStatistics);
            fieldStatistics.setMinutes(DateUtil.micro2Min(diff));
            fieldStatisticsList.add(fieldStatistics);
        });
        int attendanceNum = 0;
        List<AttendancePunchRecordVO> lateStatisticsList = new ArrayList<>(16);
        List<AttendancePunchRecordVO> earlyStatisticsList = new ArrayList<>(16);
        List<AttendancePunchRecordVO> workOvertimeStatisticsList = new ArrayList<>(16);
        List<AttendancePunchRecordVO> unpunchStatisticsList = new ArrayList<>(16);
        List<AttendancePunchRecordVO> invalidStatisticsList = new ArrayList<>(16);
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            Byte isPunch = attendancePunchRecordVO.getIsPunch();
            Date startTime = attendancePunchRecordVO.getStartTime();
            Date endTime = attendancePunchRecordVO.getEndTime();
            Integer orgId = attendancePunchRecordVO.getOrgId();
            String orgName = orgMap.get(orgId);
            if (isPunch == 0) {
                attendancePunchRecordVO.setOrgName(orgName);
                unpunchStatisticsList.add(attendancePunchRecordVO);
            } else {
                Date punchTime = attendancePunchRecordVO.getPunchTime();
                Byte source = attendancePunchRecordVO.getSource();
                Integer sourceId = attendancePunchRecordVO.getSourceId();
                if (source == AttendanceSourceEnum.EMPLOYEE_SCHEDULE.getCode()) {
                    Byte punchStatus = attendancePunchRecordVO.getPunchStatus();
                    switch (punchStatus) {
                        case 0: {
                            attendanceNum++;
                            break;
                        }
                        case 1: {
                            long diff = punchTime.getTime() - startTime.getTime();
                            attendancePunchRecordVO.setMinutes(DateUtil.micro2Min(diff));
                            attendancePunchRecordVO.setOrgName(orgName);
                            lateStatisticsList.add(attendancePunchRecordVO);
                            break;
                        }
                        case 2: {
                            attendanceNum++;
                            break;
                        }
                        case 3: {
                            long diff = endTime.getTime()-punchTime.getTime();
                            attendancePunchRecordVO.setMinutes(DateUtil.micro2Min(diff));
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
                } else if (source == AttendanceSourceEnum.WORK_OVERTIME.getCode()) {
                    Integer scheduleId = attendancePunchRecordVO.getScheduleId();
                    if (scheduleId!=null) {
                        restMap.remove(scheduleId);
                    }
                    workOvertime.add(sourceId);
                    long diff = endTime.getTime()-startTime.getTime();
                    attendancePunchRecordVO.setMinutes(DateUtil.micro2Min(diff));
                    attendancePunchRecordVO.setOrgName(orgName);
                    workOvertimeStatisticsList.add(attendancePunchRecordVO);
                }
            }
        }

        List<AttendancePunchRecordVO> restStatisticeList = new ArrayList<>(restMap.size());
        restMap.forEach((id, employeeScheduleVO) ->{
            AttendancePunchRecordVO restStatistics = new AttendancePunchRecordVO();
            Integer orgId = employeeScheduleVO.getClinicId();
            String orgName = orgMap.get(orgId);
            restStatistics.setOrgName(orgName);
            restStatistics.setPunchDate(employeeScheduleVO.getWorkDate());
            restStatisticeList.add(restStatistics);
        });
        int leaveNum = leaveInfoVOS==null?0:leaveInfoVOS.size();
        result.setWorkOvertimeNum(workOvertime.size());
        result.setUnpunchNum(unpunchStatisticsList.size());
        result.setRestNum(restStatisticeList.size());
        result.setFieldNum(fieldStatisticsList.size());
        result.setLeaveNum(leaveNum);
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
     * 根据条件分页查询考勤汇总
     *
     * @param queryForm 查询参数
     * @return
     */
    public PageInfo<AttendanceStatisticsVO> statisticsPunchRecord(AttendanceStatisticsQueryForm queryForm) {
        String name = queryForm.getEmployeeName();
        String dateStr = queryForm.getDate();
        Date startTime = null;
        Date endTime = null;
        if (StringHelper.isNotEmpty(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            if (dateStr.length() == 4) {//按年查
                int year = Integer.parseInt(dateStr);
                startTime = DateUtil.getBeginTime(year,0);
                endTime = DateUtil.getEndTime(year,11);
            } else if (dateStr.length() == 7) {
                String[] dateTmp = dateStr.split("-");
                int year = Integer.parseInt(dateTmp[0]);
                int month = Integer.parseInt(dateTmp[1]) - 1;
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

        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setBetweenStartDate(startTime);
        leaveQueryForm.setAndStartDate(endTime);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        List<Integer> employeeScheduleIds = new ArrayList<>();
        leaveInfoVOS.forEach(leaveInfoVO -> {
            Integer employeeScheduleId = leaveInfoVO.getScheduleId();
            if (employeeScheduleId!=null && !employeeScheduleIds.contains(employeeScheduleId)) {
                employeeScheduleIds.add(employeeScheduleId);
            }
        });
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.selectInIds(employeeScheduleIds);
        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
            Integer userId = leaveInfoVO.getUserId();
            Integer orgId = null;
            for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                if (employeeScheduleVO.getId().equals(leaveInfoVO.getScheduleId())) {
                    orgId = employeeScheduleVO.getClinicId();
                }
            }
            Long length = leaveMinuteMap.get(userId, orgId);
            if (length == null) {
                length = 0L;
            }
            long diff = leaveInfoVO.getEndTime().getTime() - leaveInfoVO.getStartTime().getTime();
            leaveMinuteMap.put(userId, orgId, length+diff);
            isFullMap.put(userId, orgId, false);
        }


        for (AttendancePunchDateVO attendancePunchDateVO : attendancePunchDateVOS) {
            Integer userId = attendancePunchDateVO.getUserId();
            Integer onDutyOrgId = attendancePunchDateVO.getOnDutyOrgId();
            Byte onDutyStatus = attendancePunchDateVO.getOnDutyStatus();
            Date onPunchTime = attendancePunchDateVO.getOnDutyPunchTime();
            Date onEndTime = attendancePunchDateVO.getOnDutyEndTime();
            Date offStartTime = attendancePunchDateVO.getOffDutyStartTime();
            Date offPunchTime = attendancePunchDateVO.getOffDutyPunchTime();
            Date offEndTime = attendancePunchDateVO.getOffDutyEndTime();
            boolean hasAttendance = false;
            switch (onDutyStatus) {
                case 0: {
                    hasAttendance = true;
                    Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = onEndTime.getTime() - onPunchTime.getTime();
                    workDateMinuteMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 1: {
                    hasAttendance = true;
                    incrNum(laterNumMap, userId, onDutyOrgId);
                    isFullMap.put(userId, onDutyOrgId, false);
                    Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = onEndTime.getTime() - onPunchTime.getTime();
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
            Byte offDutyStatus = attendancePunchDateVO.getOffDutyStatus();
            Integer offDutyOrgId = attendancePunchDateVO.getOffDutyOrgId();
            switch (offDutyStatus) {
                case 2:{
                    hasAttendance = true;
                    Long length = restDateOvertimeMap.get(userId, offDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = offPunchTime.getTime() - offStartTime.getTime();
                    workDateMinuteMap.put(userId, offDutyOrgId, length+diff);
                    if (offPunchTime.getTime()-offEndTime.getTime() > 3600) {//至少工作日加班1小时
                        Long overtime = workDateOvertimeMinuteMap.get(userId, offDutyOrgId);
                        if (overtime == null) {
                            overtime = 0L;
                        }
                        diff = onEndTime.getTime() - offStartTime.getTime();
                        workDateOvertimeMinuteMap.put(userId, offDutyOrgId, overtime+diff);
                        if (DateUtil.micro2Min(diff) >= 30) {
                            overtime = workDateOvertime30MinuteMap.get(userId, offDutyOrgId);
                            if (overtime == null) {
                                overtime = 0L;
                            }
                            workDateOvertime30MinuteMap.put(userId, offDutyOrgId, overtime+diff);
                        }
                    }
                    break;
                }
                case 3:{
                    incrNum(earlyNumMap, userId, offDutyOrgId);
                    hasAttendance = true;
                    isFullMap.put(userId, offDutyOrgId, false);
                    Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = offPunchTime.getTime() - offStartTime.getTime();
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
            Byte workOvertime = attendancePunchDateVO.getWorkOvertime();
            switch (workOvertime) {
                case 0:{
                    Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = onEndTime.getTime() - onPunchTime.getTime();
                    restDateOvertimeMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 1:{
                    Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = offPunchTime.getTime() - offStartTime.getTime();
                    restDateOvertimeMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 2:{
                    Long length = restDateOvertimeMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = offPunchTime.getTime() - onPunchTime.getTime();
                    restDateOvertimeMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
            }

            Byte field = attendancePunchDateVO.getField();
            switch (field) {
                case 0:{
                    Long length = fieldMinuteMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = onEndTime.getTime() - onPunchTime.getTime();
                    fieldMinuteMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 1:{
                    Long length = fieldMinuteMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = offPunchTime.getTime() - offStartTime.getTime();
                    fieldMinuteMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
                case 2:{
                    Long length = fieldMinuteMap.get(userId, onDutyOrgId);
                    if (length == null) {
                        length = 0L;
                    }
                    long diff = offPunchTime.getTime() - onPunchTime.getTime();
                    fieldMinuteMap.put(userId, onDutyOrgId, length+diff);
                    break;
                }
            }
        }

        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(queryForm.getWhetherPage());
        model.setPageNum(queryForm.getPageNum());
        model.setPageSize(queryForm.getPageSize());
        model.setOrgIds(Arrays.asList(queryForm.getOrgId()));
        model.setKeyWord(name);
        model.setWorkStatus(new Byte[]{0, 1, 3});
        List<SysUserInfoDetail> userList = remoteSystemServiceFeign.findSysUserEmployeeWithOrgList(model);
        List<AttendanceStatisticsVO> result = new ArrayList<>();
        userList.forEach(user->{
            Integer userId = user.getUserId();
            Integer orgId = Integer.parseInt(user.getCompanyIds());
            AttendanceStatisticsVO statistics = new AttendanceStatisticsVO();
            statistics.setUserId(userId);
            statistics.setEmployeeName(user.getName());
            statistics.setOrgId(orgId);
            statistics.setOrgName(user.getCompanys());

            Long restMinute = restDateOvertimeMap.get(userId, orgId);
            if (restMinute != null) {
                statistics.setRestDateOvertimeMinute(DateUtil.micro2Min(restMinute));
            }
            Long leaveMinute = leaveMinuteMap.get(userId, orgId);
            if (leaveMinute != null) {
                statistics.setLeaveMinute(DateUtil.micro2Min(leaveMinute));
            }
            Long fieldMinute = fieldMinuteMap.get(userId, orgId);
            if (fieldMinute != null) {
                statistics.setFieldMinute(DateUtil.micro2Min(fieldMinute));
            }
            Boolean isFull = isFullMap.get(userId, orgId);
            statistics.setIsFull(isFull==null?true:isFull);
            statistics.setWorkDateOvertimeMinute(workDateOvertimeMinuteMap.get(userId, orgId));
            statistics.setWorkDateOvertime30Minute(workDateOvertime30MinuteMap.get(userId, orgId));
            statistics.setWorkDateMinute(workDateMinuteMap.get(userId, orgId));
            statistics.setAttendanceNum(attendancNumMap.get(userId, orgId));
            statistics.setLateNum(laterNumMap.get(userId, orgId));
            statistics.setEarlyNum(earlyNumMap.get(userId, orgId));
            statistics.setUnpunchNum(unpunchNumMap.get(userId, orgId));
            statistics.setInvalidNum(invalidNumMap.get(userId, orgId));
        });
        return new PageInfo<>(result);
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
                    punchDate.setUserId(vo.getUserId());
                    punchDate.setPunchDate(date);
                    if (punchType == 0) {
                        punchDate.setOnDutyPunchTime(punchTime);
                        punchDate.setOnDutyOrgId(orgId);
                        punchDate.setOnDutyName(name);
                        switch (source) {
                            case 1:{
                                punchDate.setLeave((byte) 2);
                                break;
                            }
                            case 2:{
                                byte wokOvertime = 0;
                                if (punchDate.getWorkOvertime()!=null) {
                                    wokOvertime = 2;
                                }
                                punchDate.setWorkOvertime(wokOvertime);
                                break;
                            }
                            case 3:{
                                byte field = 0;
                                if (punchDate.getField()!=null) {
                                    field = 2;
                                }
                                punchDate.setField(field);
                                break;
                            }
                        }
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
                        switch (source) {
                            case 1:{
                                punchDate.setLeave((byte) 2);
                                break;
                            }
                            case 2:{
                                byte wokOvertime = 1;
                                if (punchDate.getWorkOvertime()!=null) {
                                    wokOvertime = 2;
                                }
                                punchDate.setWorkOvertime(wokOvertime);
                                break;
                            }
                            case 3:{
                                byte field = 1;
                                if (punchDate.getField()!=null) {
                                    field = 2;
                                }
                                punchDate.setField(field);
                                break;
                            }
                        }
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
}
