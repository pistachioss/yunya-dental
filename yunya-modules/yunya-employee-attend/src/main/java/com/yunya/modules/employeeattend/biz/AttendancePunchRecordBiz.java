package com.yunya.modules.employeeattend.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.yunya.feign.employee_attend.form.*;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
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

import java.math.BigDecimal;
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
        String deviceNumber = null;
        if (attendanceDeviceBindingVO != null) {
            deviceNumber = attendanceDeviceBindingVO.getDeviceNumber();
        }
        result.setDeviceNumber(deviceNumber);
        // 根据考勤地址或Wifi的mac地址抽取用户当天的打卡项目（上班班次、加班、请假、外勤）
        String macAddress = queryForm.getWifiMacAddress();
        String longitude = queryForm.getLongitude();
        String latitude = queryForm.getLatitude();
        if (StringHelper.isEmpty(macAddress) && (StringHelper.isEmpty(longitude) || StringHelper.isEmpty(latitude))) {
            throw new ClientServiceException("考勤地址或Wifi不能都为空！", PARAMETERS_IS_ILLEGAL);
        }
        Map<Integer, JSONObject> orgMap = getOrgMapByPosition(longitude, latitude, macAddress);
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setWhetherPage(false);
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
        JSONObject object = orgMap.get(result.getOrgId());
        if (AttendanceSourceEnum.FIELD.getCode().equals(source)) {
            result.setAttendanceAddressId(-999);
            result.setPunchMode(0);
        } else if (object==null) {//不在考勤范围内
            result.setPunchStatus((byte) 5);
        } else if (object != null) {
            result.setPunchName(object.getString("punchName"));
            Integer addressId = object.getInteger("addressId");
            int punchMode = 1;
            if (addressId != null) {
                result.setAttendanceAddressId(addressId);
                punchMode = 0;
            }
            result.setPunchMode(punchMode);
        }
        return result;
    }

    /**
     * 根据位置获取对应门诊的考勤地址设置
     * @param longitude
     * @param latitude
     * @param macAddress
     * @return
     */
    private Map<Integer, JSONObject> getOrgMapByPosition(String longitude, String latitude, String macAddress) {
        Map<Integer, JSONObject> orgMap = new HashMap<>(16);
        if (StringHelper.isNotEmpty(latitude) && StringHelper.isNotEmpty(longitude)) {
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
                double distance = distanceByLongNLat(lon1,lat1,lon2,lat2);
                if (distance-attendanceRange <= 0) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (attendanceAddressSetVOS!=null && !attendanceAddressSetVOS.isEmpty()) {
                attendanceAddressSetVOS.forEach(addressSetVO -> {
                    JSONObject object = new JSONObject();
                    object.put("punchName", addressSetVO.getAttendanceAddress());
                    object.put("addressId", addressSetVO.getId());
                    orgMap.put(addressSetVO.getOrgId(), object);
                });
            }
        }
        if (StringHelper.isNotEmpty(macAddress)) {
            AttendanceWifiSetQueryForm wifiQUeryForm = new AttendanceWifiSetQueryForm();
            wifiQUeryForm.setWhetherPage(true);
            wifiQUeryForm.setPageNum(1);
            wifiQUeryForm.setPageSize(1000);
            wifiQUeryForm.setMacAddress(macAddress);
            List<AttendanceWifiSetVO> attendanceWifiSetVOS = attendanceWifiSetBiz.findAttendanceWifiSets(wifiQUeryForm);
            if (attendanceWifiSetVOS!=null && !attendanceWifiSetVOS.isEmpty()) {//不在考勤范围内
                attendanceWifiSetVOS.forEach(wifiSetVO -> {
                    JSONObject object = new JSONObject();
                    object.put("punchName", wifiSetVO.getWifiName());
                    orgMap.put(wifiSetVO.getOrgId(), object);
                });
            }
        }
        return orgMap;
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
                    String orgName = organizationInfoDetail.getAbbreviation();
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
        Byte condition = onDutyPunchRecord.getSource();
        if (condition.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())
            || condition.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())) {
            condition = 2;
        }
        onDutyPunchItem.setCondition(condition);
        boolean isNext = false;
        if (onDutyPunchRecord.getIsPunch().equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {//已打卡
            onDutyPunchItem.setPunchType(onDutyPunchRecord.getPunchType());
            byte punchMode = 0;
            if (StringHelper.isNotEmpty(onDutyPunchRecord.getWifiMacAddress())) {
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
        if (condition.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())
            || condition.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())) {
            condition = 2;
        }
        offDutyPunchItem.setCondition(condition);
        if (offDutyPunchRecord.getIsPunch().equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {//已打卡
            byte punchMode = 0;
            if (offDutyPunchRecord.getWifiMacAddress()!=null) {
                punchMode = 1;
            }
            offDutyPunchItem.setPunchMode(punchMode);
            offDutyPunchItem.setPunchType(offDutyPunchRecord.getPunchType());
            offDutyPunchItem.setPunchName(offDutyPunchRecord.getPunchAddress());
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
        Date now = null;
        try {
            now = DateUtil.dateTo19700101(new Date(System.currentTimeMillis()));
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
        }
        AttendancePunchRecordVO punchItem = attendancePunchRecordVOS.get(0);
        Date startTime = punchItem.getStartTime();
        Byte isPunch = punchItem.getIsPunch();
        Byte punchStatus = punchItem.getPunchStatus();
        if (!AttendanceStatusEnum.INVALID_PUNCH.getCode().equals(punchStatus)) {
            if (isPunch.equals(AttendanceIsPunchEnum.UNPUNCH.getCode())) {
                if (now.after(startTime)) {
                    punchStatus = AttendanceStatusEnum.LATER_PUNCH.getCode();//迟到打卡
                } else {
                    punchStatus = AttendanceStatusEnum.ONDUTY_PUNCH.getCode();//上班打卡
                }
            } else {
                punchItem = attendancePunchRecordVOS.get(attendancePunchRecordVOS.size()-1);
                if (now.before(punchItem.getEndTime())) {
                    punchStatus = AttendanceStatusEnum.EARLY_PUNCH.getCode();//早退打卡
                } else {
                    punchStatus = AttendanceStatusEnum.OFFDUTY_PUNCH.getCode();//下班打卡
                }
            }
        } else {
            if (isPunch.equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {
                punchItem = attendancePunchRecordVOS.get(attendancePunchRecordVOS.size()-1);
            }
        }
        result.setPunchType(punchItem.getPunchType());
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
     * @param longitude1 第一点经度
     * @param latitude1  第一点纬度
     * @param longitude2 第二点经度
     * @param latitude2  第二点纬度
     * @return 返回距离 单位：米
     */
    public static double distanceByLongNLat(double longitude1, double latitude1, double longitude2, double latitude2) {
        Double EARTH_RADIUS = 6370.996; // 地球半径系数
        Double PI = 3.1415926;

        Double radLat1 = latitude1 * PI / 180.0;
        Double radLat2 = latitude2 * PI / 180.0;

        Double radLng1 = longitude1 * PI / 180.0;
        Double radLng2 = longitude2 * PI /180.0;

        Double a =  radLat1 -  radLat2;
        Double b =  radLng1 -  radLng2;

        Double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b/2),2)));
        distance = distance * EARTH_RADIUS * 1000;
        BigDecimal bg = new BigDecimal(distance);
        double d3 = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return d3;

    }

    /**
     * 考勤打卡
     * @param attendancePunchRecordForm 考勤打卡记录修改模型
     */
    public void punch(AttendancePunchRecordForm attendancePunchRecordForm) {
        Date now = new Date(System.currentTimeMillis());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        if (attendanceDeviceBindingBiz.findEmployeeBindingDevice(userId) == null) {
            throw new ClientServiceException("设备未绑定，请先绑定", OPERATION_NOT_ALLOW);
        }
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setPunchDate(now);
        queryForm.setUserId(userId);
        queryForm.setWhetherPage(false);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(queryForm);
        if (attendancePunchRecordVOS==null || attendancePunchRecordVOS.isEmpty()) {
            throw new ClientServiceException("打卡失败，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        AttendancePunchRecordVO onPunchRecord = null;
        AttendancePunchRecordVO offPunchRecord = null;
        AttendancePunchRecordVO dbPunchRecord = null;
        Byte onDutyIspunch = null;
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            if (attendancePunchRecordVO.getPunchType().equals(AttendanceTypeEnum.ONDUTY.getCode())) {
                onDutyIspunch = attendancePunchRecordVO.getIsPunch();
                onPunchRecord = attendancePunchRecordVO;
            } else {
                offPunchRecord = attendancePunchRecordVO;
            }
            if (attendancePunchRecordVO.getId().equals(attendancePunchRecordForm.getId())) {
                dbPunchRecord = attendancePunchRecordVO;
            }
        }
        if (onPunchRecord==null || offPunchRecord == null || dbPunchRecord==null) {
            throw new ClientServiceException("打卡失败，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        if (onDutyIspunch.equals(AttendanceIsPunchEnum.UNPUNCH.getCode()) && offPunchRecord.getPunchType().equals(AttendanceTypeEnum.ONDUTY.getCode())) {
            throw new ClientServiceException("未打上班卡，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        AttendancePunchRecord attendancePunchRecord = new AttendancePunchRecord();
        Byte source = dbPunchRecord.getSource();
        if (!AttendanceSourceEnum.FIELD.getCode().equals(source)) {
            Integer addressId = checkPosition(attendancePunchRecordForm, dbPunchRecord.getOrgId());
            attendancePunchRecord.setAttendanceAddressId(addressId);
            attendancePunchRecord.setWifiMacAddress(attendancePunchRecordForm.getWifiMacAddress());
        }
//        checkPosition(attendancePunchRecordForm,dbPunchRecord);
        Byte punchStatus = attendancePunchRecordForm.getPunchStatus();
        Byte punchType = dbPunchRecord.getPunchType();
        Byte isInScope = AttendanceIsInScopeEnum.UNBELONG.getCode();
        if (!punchStatus.equals(AttendanceStatusEnum.INVALID_PUNCH.getCode())) {
            Date time = null;
            try {
                time = DateUtil.dateTo19700101(now);
            } catch (ParseException e) {
                throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
            }
            if (punchType.equals(AttendanceTypeEnum.ONDUTY.getCode())) {//上班卡
                if (time.after(dbPunchRecord.getStartTime())) {
                    punchStatus = AttendanceStatusEnum.LATER_PUNCH.getCode();//迟到打卡
                } else {
                    punchStatus = AttendanceStatusEnum.ONDUTY_PUNCH.getCode();//上班打卡
                }
            } else {//下班卡
                if (time.before(dbPunchRecord.getEndTime())) {
                    punchStatus = AttendanceStatusEnum.EARLY_PUNCH.getCode();//早退打卡
                } else {
                    punchStatus = AttendanceStatusEnum.OFFDUTY_PUNCH.getCode();//下班打卡
                }
                Date onPunchTime = onPunchRecord.getPunchTime();
                Date offPunchTime = null;
                try {
                    offPunchTime = DateUtil.dateTo19700101(now);
                } catch (ParseException e) {
                    throw new ClientServiceException("日期转换错误", DATA_TRANSFORMATION_EXIST);
                }
                Date onStartTime = onPunchRecord.getStartTime();
                Date onEndTime = onPunchRecord.getEndTime();
                Date offStartTime = offPunchRecord.getStartTime();
                Date offEndtTime = offPunchRecord.getEndTime();
                if (onPunchRecord.getSource().equals(offPunchRecord.getSource())
                    && onPunchRecord.getSourceId().equals(offPunchRecord.getSourceId())) {//全天两个班次
                    if (!(offPunchTime.before(onStartTime) || onPunchTime.after(offEndtTime)
                            || (onPunchTime.after(onEndTime)&&offPunchTime.before(offStartTime)))) {
                        isInScope = AttendanceIsInScopeEnum.BELONG.getCode();
                    }
                } else {//全天一个班次
                    if (!(onPunchTime.after(offEndtTime) || offPunchTime.before(onStartTime))) {
                        isInScope = AttendanceIsInScopeEnum.BELONG.getCode();
                    }
                }
            }
        }
        Byte oldPunchStatus = dbPunchRecord.getPunchStatus();
        if ((AttendanceStatusEnum.ONDUTY_PUNCH.getCode().equals(punchStatus)
                ||AttendanceStatusEnum.LATER_PUNCH.getCode().equals(punchStatus))
                && (AttendanceStatusEnum.ONDUTY_PUNCH.getCode().equals(oldPunchStatus)
                ||AttendanceStatusEnum.LATER_PUNCH.getCode().equals(oldPunchStatus))) {// 上班更新不允许
            throw new ClientServiceException("上班卡已打，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        attendancePunchRecord.setLongitude(attendancePunchRecordForm.getLongitude());
        attendancePunchRecord.setLatitude(attendancePunchRecordForm.getLatitude());
        attendancePunchRecord.setId(attendancePunchRecordForm.getId());
        attendancePunchRecord.setPunchTime(now);
        attendancePunchRecord.setPunchAddress(attendancePunchRecordForm.getPunchAddress());
        attendancePunchRecord.setPunchStatus(punchStatus);
        attendancePunchRecord.setIsPunch(AttendanceIsPunchEnum.PUNCHED.getCode());
        attendancePunchRecord.setIsInScope(isInScope);
        attendancePunchRecord.setUptTime(now);
        attendancePunchRecord.setUptId(userId);
        mapper.updateByPrimaryKeySelective(attendancePunchRecord);
        if (isInScope.equals(AttendanceIsInScopeEnum.BELONG.getCode())) {
            AttendancePunchRecord onEntity = new AttendancePunchRecord();
            onEntity.setId(onPunchRecord.getId());
            onEntity.setIsInScope(AttendanceIsInScopeEnum.BELONG.getCode());
            mapper.updateByPrimaryKeySelective(onEntity);
        }
    }

    private Integer checkPosition(AttendancePunchRecordForm queryForm, Integer orgId) {
        Map<Integer, JSONObject> orgMap = getOrgMapByPosition(queryForm.getLongitude(), queryForm.getLatitude(), queryForm.getWifiMacAddress());
        JSONObject object = orgMap.get(orgId);
        if (orgMap.isEmpty() || object==null) {
            throw new ClientServiceException("不在考勤范围，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        return object.getInteger("addressId");
    }

    private void checkPosition(AttendancePunchRecordForm attendancePunchRecordForm, AttendancePunchRecordVO dbPunchRecord) {
        Integer addressId = attendancePunchRecordForm.getAttendanceAddressId();
        String macAddresss = attendancePunchRecordForm.getWifiMacAddress();
        if (!AttendanceSourceEnum.FIELD.getCode().equals(dbPunchRecord.getSource())) {
            Integer addOrgId = null;
            if (addressId==null && StringHelper.isEmpty(macAddresss)) {
                throw new ClientServiceException("不在考勤范围，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            } else if (addressId != null) {
                AttendanceAddressSetVO attendanceAddressSetVO = attendanceAddressSetBiz.findAttendanceAddressSetById(addressId);
                if (attendanceAddressSetVO == null) {
                    throw new ClientServiceException("不在考勤范围，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
                }
                addOrgId = attendanceAddressSetVO.getOrgId();
            } else if (StringHelper.isNotEmpty(macAddresss)) {
                AttendanceWifiSetVO attendanceWifiSetVO = attendanceWifiSetBiz.findAttendanceWifiSetByMac(macAddresss);
                if (attendanceWifiSetVO == null) {
                    throw new ClientServiceException("不在考勤范围，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
                }
                addOrgId = attendanceWifiSetVO.getOrgId();
            }
            if (addOrgId == null) {
                throw new ClientServiceException("考勤地址或考勤WIFI没有设置，请先设置考勤地址", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            }
            if (!dbPunchRecord.getOrgId().equals(addOrgId)) {
                throw new ClientServiceException("不在考勤范围，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            }
        } else {
            if (addressId==null && StringHelper.isEmpty(macAddresss)) {
                throw new ClientServiceException("不在考勤范围，请刷新页面", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            }
        }
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
        queryForm.setWhetherPage(false);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(queryForm);
        setOrgName(attendancePunchRecordVOS);

        AttendancePunchRecordVO firstPunchRecord = null;
        AttendancePunchRecordVO lastPunchRecord = null;
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            if (attendancePunchRecordVO.getPunchType().equals(AttendanceTypeEnum.ONDUTY.getCode())) {
                firstPunchRecord = attendancePunchRecordVO;
            } else {
                lastPunchRecord = attendancePunchRecordVO;
            }
        }
        List<AttendancePunchItemVO> attendancePunchItemVOS = new ArrayList<>(2);
        List<AttendancePunchRecordVO> attendancePunchRecordVOList = new ArrayList<>(2);
        EmployeeScheduleQueryForm scheduleQueryForm = new EmployeeScheduleQueryForm();
        scheduleQueryForm.setUserId(userId);
        scheduleQueryForm.setWorkDate(date);
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(scheduleQueryForm);
        if (employeeScheduleVOS!=null && !employeeScheduleVOS.isEmpty()) {
            List<Integer> orgIds = new ArrayList<>();
            employeeScheduleVOS.forEach(employeeScheduleVO -> {
                Integer orgId = employeeScheduleVO.getClinicId();
                if (!orgIds.contains(orgId)) {
                    orgIds.add(orgId);
                }
            });
            List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
            employeeScheduleVOS.forEach(employeeScheduleVO -> {
                AttendancePunchRecordVO punchRecordVO = new AttendancePunchRecordVO();
                String name = employeeScheduleVO.getName();
                String type = employeeScheduleVO.getType();
                name += "（" + type + "）";
                punchRecordVO.setName(name);
                punchRecordVO.setStartTime(employeeScheduleVO.getFirstStartTime());
                Date endTime = employeeScheduleVO.getFirstEndTime();
                if (employeeScheduleVO.getSecondEndTime() != null) {
                    endTime = employeeScheduleVO.getSecondEndTime();
                }
                punchRecordVO.setEndTime(endTime);
                Integer clinicId = employeeScheduleVO.getClinicId();
                String orgName = "";
                for (OrganizationInfoDetail organizationInfoDetail : organizationInfoDetails) {
                    if (organizationInfoDetail.getId().equals(clinicId)) {
                        orgName = organizationInfoDetail.getAbbreviation();
                        break;
                    }
                }
                punchRecordVO.setOrgName(orgName);
                attendancePunchRecordVOList.add(punchRecordVO);
            });
        }
        if (attendancePunchRecordVOS!=null && !attendancePunchRecordVOS.isEmpty()) {
            attendancePunchItemVOS.add(createPunchItem(firstPunchRecord));
            attendancePunchItemVOS.add(createPunchItem(lastPunchRecord));
        }
        result.setAttendancePunchRecordVOS(attendancePunchRecordVOList);
        result.setAttendancePunchItemVOS(attendancePunchItemVOS);
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
        Byte punchStatus = attendancePunchRecordVO.getPunchStatus();
        Byte isPunch = attendancePunchRecordVO.getIsPunch();
        Byte source = attendancePunchRecordVO.getSource();
        Byte status = null; //休息
        if (!(AttendanceSourceEnum.REST_SCHEDULE.getCode().equals(source)
                && AttendanceIsPunchEnum.UNPUNCH.getCode().equals(isPunch))) {
            if (!AttendanceStatusEnum.INVALID_PUNCH.getCode().equals(punchStatus)
                    && AttendanceIsPunchEnum.UNPUNCH.getCode().equals(isPunch)) {
                status = 5; //缺卡
            } else {
                status = punchStatus;
            }
        }
        Date date = DateUtil.getCurrentDate();
        if (AttendanceIsPunchEnum.UNPUNCH.getCode().equals(isPunch)
                && date.equals(attendancePunchRecordVO.getPunchDate())) {
            status = null;
        }
        punchItem.setPunchStatus(status);
        if (StringHelper.isNotEmpty(attendancePunchRecordVO.getWifiMacAddress())) {
            punchItem.setPunchMode((byte) 1);
        } else if (StringHelper.isNotEmpty(attendancePunchRecordVO.getLongitude())
                && StringHelper.isNotEmpty(attendancePunchRecordVO.getLatitude())) {
            punchItem.setPunchMode((byte) 0);
        }
        punchItem.setOrgName(attendancePunchRecordVO.getOrgName());
        if (source.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())
                || source.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())) {
            source = 2;
        }
        punchItem.setCondition(source);
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

        Date now = DateUtil.getCurrentDate();
        // 打卡记录
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setUserId(userId);
        recordQueryForm.setBetweenDate(firstDate);
        recordQueryForm.setAndDate(endDate);
        recordQueryForm.setWhetherPage(false);
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
        Map<Date, Integer> unPunchCount = new HashMap<>();
        Map<Date, String> stateMap = new HashMap<>();
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            Byte isPunch = attendancePunchRecordVO.getIsPunch();
            Date date = attendancePunchRecordVO.getPunchDate();
            Byte punchStatus = attendancePunchRecordVO.getPunchStatus();
            Byte source = attendancePunchRecordVO.getSource();
            Date punchDate = attendancePunchRecordVO.getPunchDate();
            String state = stateMap.get(date);
            String temp = null;
            if (isPunch.equals(AttendanceIsPunchEnum.UNPUNCH.getCode())) {
                if (!punchDate.equals(now)) {
                    Integer count = unPunchCount.get(date);
                    if (count == null) {
                        count = 0;
                    }
                    unPunchCount.put(date, ++count);
                    temp = "缺卡";
                }
            } else {
                if (punchStatus.equals(AttendanceStatusEnum.ONDUTY_PUNCH.getCode())
                        ||punchStatus.equals(AttendanceStatusEnum.OFFDUTY_PUNCH.getCode())) {//正常
                    temp = "正常";
                } else if (punchStatus.equals(AttendanceStatusEnum.LATER_PUNCH.getCode())) {
                    temp = "迟到";
                } else if (punchStatus.equals(AttendanceStatusEnum.EARLY_PUNCH.getCode())) {
                    temp = "早退";
                }
            }
            if (source.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())
                    || source.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())) {
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
        }

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
        userId = 1604;
        List<Date> dateList = DateUtil.getMonthFullDay(dateStr);
        Date firstDate = dateList.get(0);
        Date endDate = dateList.get(dateList.size()-1);
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setBetweenWorkDate(firstDate);
        employeeScheduleQueryForm.setAndWorkDate(endDate);
        employeeScheduleQueryForm.setUserId(userId);
        // 班次列表
        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
        Map<Date, Set<Integer>> restMap = new LinkedHashMap<>(employeeScheduleVOS.size());
        Map<Integer, EmployeeScheduleVO> workMap = new LinkedHashMap<>(employeeScheduleVOS.size());
        List<Date> workDates = new ArrayList<>();
        employeeScheduleVOS.forEach(employeeScheduleVO->{
            Integer orgId = employeeScheduleVO.getClinicId();
            Date workDate = employeeScheduleVO.getWorkDate();
            if (REST.equals(employeeScheduleVO.getType())) {
                Set<Integer> set = restMap.get(workDate);
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(orgId);
                restMap.put(workDate, set);
            } else {
                workMap.put(employeeScheduleVO.getId(), employeeScheduleVO);
                workDates.add(workDate);
            }
        });
        Map<Integer, String> orgMap = new HashMap<>(16);
        OrganizationModel model = new OrganizationModel();
        model.setWhetherPage(false);
        List<OrganizationInfoDetail> orgList = remoteSystemServiceFeign.findOrgInfoList(model);
        orgList.forEach(organizationInfo->orgMap.put(organizationInfo.getId(),organizationInfo.getName()));

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
        leaveQueryForm.setApprovalStatus(1);
        leaveQueryForm.setWhetherPage(false);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        List<AttendancePunchRecordVO> leaveStatisticsList = new ArrayList<>(leaveInfoVOS.size());
        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
            AttendancePunchRecordVO leaveStatistics = new AttendancePunchRecordVO();
            Date startTime = leaveInfoVO.getStartTime();
            Date endTime = leaveInfoVO.getEndTime();
            if (leaveInfoVO.getVacationStatus().equals(0)) {// 按班次请假
                leaveStatistics.setPunchDate(leaveInfoVO.getStartDate());
                EmployeeScheduleVO employeeScheduleVO = workMap.get(leaveInfoVO.getScheduleId());
                if (employeeScheduleVO != null) {
                    leaveStatistics.setName("按班次请假");
                    Integer orgId = employeeScheduleVO.getClinicId();
                    leaveStatistics.setOrgName(orgMap.get(orgId));
                }
                long diff = endTime.getTime()-startTime.getTime();
                leaveStatistics.setSourceId(leaveInfoVO.getId());
                leaveStatistics.setSource(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode());
                leaveStatistics.setMinutes(DateUtil.micro2HourMin(diff));
                leaveStatistics.setStartTime(leaveInfoVO.getStartTime());
                leaveStatistics.setEndTime(leaveInfoVO.getEndTime());
                leaveStatisticsList.add(leaveStatistics);
            } else {// 按天请假
                startTime = leaveInfoVO.getStartDate();
                endTime = leaveInfoVO.getEndDate();
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
                    leaveStatistics.setSource(AttendanceSourceEnum.LEAVE_BYDAY.getCode());
                    leaveStatistics.setSourceId(leaveInfoVO.getId());
                    leaveStatisticsList.add(leaveStatistics);
                });
            }
        }

        Date curDate = DateUtil.getCurrentDate();
        Set<Integer> esIds = new HashSet<>();
        List<AttendancePunchRecordVO> lateStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> earlyStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> workOvertimeStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> unpunchStatisticsList = new ArrayList<>(30);
        List<AttendancePunchRecordVO> invalidStatisticsList = new ArrayList<>(30);
        Map<Integer, List<AttendancePunchRecordVO>> workOvertimeMap = new HashMap<>();
        Map<Integer, List<AttendancePunchRecordVO>> fieldMap = new HashMap<>();
        for (AttendancePunchRecordVO attendancePunchRecordVO : attendancePunchRecordVOS) {
            Byte isPunch = attendancePunchRecordVO.getIsPunch();
            Date punchDate = attendancePunchRecordVO.getPunchDate();
            Date startTime = attendancePunchRecordVO.getStartTime();
            Date endTime = attendancePunchRecordVO.getEndTime();
            Integer orgId = attendancePunchRecordVO.getOrgId();
            String orgName = orgMap.get(orgId);
            Byte source = attendancePunchRecordVO.getSource();
            Integer sourceId = attendancePunchRecordVO.getSourceId();
            if (isPunch.equals(AttendanceIsPunchEnum.UNPUNCH.getCode())) {
               if (!source.equals(AttendanceSourceEnum.REST_SCHEDULE.getCode())
                   && !source.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())
                   && !source.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())
                   && !curDate.equals(punchDate)) {
                   attendancePunchRecordVO.setOrgName(orgName);
                   unpunchStatisticsList.add(attendancePunchRecordVO);
               }
            } else {
                Date punchTime = attendancePunchRecordVO.getPunchTime();
                Byte punchStatus = attendancePunchRecordVO.getPunchStatus();
                if (AttendanceStatusEnum.INVALID_PUNCH.getCode().equals(punchStatus)) {
                    attendancePunchRecordVO.setOrgName(orgName);
                    invalidStatisticsList.add(attendancePunchRecordVO);
                }
                if (source.equals(AttendanceSourceEnum.WORK_SCHEDULE.getCode())) {
                    switch (punchStatus) {
                        case 0: {
                            esIds.add(attendancePunchRecordVO.getEsId());
                            break;
                        }
                        case 1: {
                            esIds.add(attendancePunchRecordVO.getEsId());
                            long diff = punchTime.getTime() - startTime.getTime();
                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
                            attendancePunchRecordVO.setOrgName(orgName);
                            lateStatisticsList.add(attendancePunchRecordVO);
                            break;
                        }
                        case 2: {
                            esIds.add(attendancePunchRecordVO.getEsId());
                            break;
                        }
                        case 3: {
                            esIds.add(attendancePunchRecordVO.getEsId());
                            long diff = endTime.getTime()-punchTime.getTime();
                            attendancePunchRecordVO.setMinutes(DateUtil.micro2HourMin(diff));
                            attendancePunchRecordVO.setOrgName(orgName);
                            earlyStatisticsList.add(attendancePunchRecordVO);
                            break;
                        }
                        default:
                    }
                } /*else if (source.equals(AttendanceSourceEnum.REST_SCHEDULE.getCode())
                    && !curDate.equals(punchDate)) {
                    attendancePunchRecordVO.setOrgName(orgName);
                    invalidStatisticsList.add(attendancePunchRecordVO);
                } */else if (source.equals(AttendanceSourceEnum.WORK_OVERTIME.getCode())) {
                    List<AttendancePunchRecordVO> list = workOvertimeMap.get(sourceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(attendancePunchRecordVO);
                    workOvertimeMap.put(sourceId,list);
                }
            }
            if (source.equals(AttendanceSourceEnum.FIELD.getCode())) {
                List<AttendancePunchRecordVO> list = fieldMap.get(sourceId);
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (isPunch.equals(AttendanceIsPunchEnum.UNPUNCH.getCode())) {
                    Byte punchType = attendancePunchRecordVO.getPunchType();
                    attendancePunchRecordVO = new AttendancePunchRecordVO();
                    attendancePunchRecordVO.setStartTime(now);
                    attendancePunchRecordVO.setPunchTime(now);
                    attendancePunchRecordVO.setEndTime(now);
                    attendancePunchRecordVO.setPunchType(punchType);
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
        workOvertimeQueryForm.setApprovalStatus(1);
        workOvertimeQueryForm.setWhetherPage(false);
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
                if (list.size() > 1) {//全天班
                    AttendancePunchRecordVO last = list.get(1);
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = last.getPunchTime();
                    if (endTime.after(last.getEndTime())) {
                        endTime = last.getEndTime();
                    }
                    diff = endTime.getTime() - startTime.getTime();
                } else if (list.size() == 1) {//半天班
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = first.getEndTime();
                    if (first.getPunchType().equals(AttendanceTypeEnum.OFFDUTY.getCode())) {
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
        fieldQueryForm.setApprovalStatus(1);
        fieldQueryForm.setWhetherPage(false);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldQueryForm);
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
                if (list.size() == 2) {//全天班
                    AttendancePunchRecordVO last = list.get(1);
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = last.getPunchTime();
                    if (endTime.after(last.getEndTime())) {
                        endTime = last.getEndTime();
                    }
                    diff = endTime.getTime() - startTime.getTime();
                } else if (list.size() == 1) {//半天班
                    Date startTime = first.getPunchTime();
                    if (startTime.before(first.getStartTime())) {
                        startTime = first.getStartTime();
                    }
                    Date endTime = first.getEndTime();
                    if (first.getPunchType().equals(AttendanceTypeEnum.OFFDUTY.getCode())) {
                        endTime = first.getStartTime();
                    }
                    diff = Math.abs(endTime.getTime()-startTime.getTime());
                }
            }
            item.setMinutes(DateUtil.micro2HourMin(diff));
            fieldStatisticsList.add(item);
        });

        //休息天数
        List<AttendancePunchRecordVO> restStatisticeList = new ArrayList<>(restMap.size());
        restMap.forEach((workDate, list) ->{
            if (!workDates.contains(workDate)) {
                AttendancePunchRecordVO restStatistics = new AttendancePunchRecordVO();
                StringBuilder orgName = new StringBuilder();
                for (Integer orgId : list) {
                    if (orgName.length() > 0) {
                        orgName.append("、");
                    }
                    orgName.append(orgMap.get(orgId));
                }
                restStatistics.setOrgName(orgName.toString());
                restStatistics.setPunchDate(workDate);
                restStatisticeList.add(restStatistics);
            }
        });
        Collections.sort(leaveStatisticsList, Comparator.comparing(AttendancePunchRecordVO::getPunchDate));
        result.setWorkOvertimeNum(workOvertimeStatisticsList.size());
        result.setUnpunchNum(unpunchStatisticsList.size());
        result.setRestNum(restStatisticeList.size());
        result.setFieldNum(fieldStatisticsList.size());
        result.setLeaveNum(leaveStatisticsList.size());
        result.setLateNum(lateStatisticsList.size());
        result.setAttendanceNum(esIds.size());
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
    public PageInfo<AttendanceStatisticsVO> statisticsPunchRecord(AttendanceStatisticsQueryForm queryForm) {
        String name = queryForm.getEmployeeName();
        Byte type = queryForm.getType();
        if (type == null) {
            throw new ClientServiceException("请选择查询年月", PARAM_NOT_ALLOW_EMPTY);
        }
        setQueryFormDate(queryForm);
        Set<Integer> userIds = new HashSet<>();
        Set<String> userOrgIds = new HashSet<>();
        //补入时长
        List<AttendanceManualMakeupVO> makeupVOS = makeupMinuteGroupByUserIdAndOrgId(queryForm, null);
        Table<Integer,Integer, Long> workDateMakeupMinutes = HashBasedTable.create();
        Table<Integer,Integer, Long> workOvertimeMakeupMinutes = HashBasedTable.create();
        if (StringHelper.isNotEmpty(makeupVOS)) {
            makeupVOS.forEach(makeupVO-> {
                if (MakeupTypeEnum.WORKDATE.getCode().equals(makeupVO.getType())) {
                    incrMinute(workDateMakeupMinutes,makeupVO.getUserId(),makeupVO.getOrgId(),makeupVO.getMinute());
                } else {
                    incrMinute(workOvertimeMakeupMinutes,makeupVO.getUserId(),makeupVO.getOrgId(),makeupVO.getMinute());
                }
            });
        }

        // 打卡记录
        List<AttendancePunchRecordVO> attendancePunchRecordVOS = getPunchRecordByQuery(queryForm);
        Table<Integer,Integer, Long> workDateOvertimeMinuteMap = HashBasedTable.create();
        Table<Integer,Integer, Long> workDateOvertime30MinuteMap = HashBasedTable.create();
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
        Date curDate = DateUtil.getCurrentDate();
        Map<Integer, List<AttendancePunchRecordVO>> punchRecordMap = new HashMap<>(attendancePunchRecordVOS.size());
        Map<Integer, List<AttendancePunchRecordVO>> workOvertimeRecordMap = new HashMap<>(attendancePunchRecordVOS.size());
        Map<Integer, List<AttendancePunchRecordVO>> fieldRecordMap = new HashMap<>(attendancePunchRecordVOS.size());
        List<Date> remDups = new ArrayList<>(attendancePunchRecordVOS.size());
        attendancePunchRecordVOS.forEach(record->{
            Integer esId = record.getEsId();
            Byte source = record.getSource();
            Integer userId = record.getUserId();
            userIds.add(userId);
            Integer orgId = record.getOrgId();
            userOrgIds.add(userId + "," + orgId);
            Byte isInScope = record.getIsInScope();
            if (AttendanceSourceEnum.WORK_SCHEDULE.getCode().equals(source)) {//上班班次
                if (isInScope.equals(AttendanceIsInScopeEnum.BELONG.getCode())) {
                    List<AttendancePunchRecordVO> list = punchRecordMap.get(esId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(record);
                    punchRecordMap.put(esId, list);
                }
                if (!curDate.equals(record.getPunchDate()) && AttendanceIsPunchEnum.UNPUNCH.getCode().equals(record.getIsPunch())) {
                    incrNum(unpunchNumMap, userId, orgId);
                }
            } else if (AttendanceSourceEnum.WORK_OVERTIME.getCode().equals(source)) {// 加班
                if (isInScope.equals(AttendanceIsInScopeEnum.BELONG.getCode())) {
                    Integer sourceId = record.getSourceId();
                    List<AttendancePunchRecordVO> list = workOvertimeRecordMap.get(sourceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(record);
                    workOvertimeRecordMap.put(sourceId, list);
                }
            } else if (AttendanceSourceEnum.FIELD.getCode().equals(source)) {// 外勤
                if (isInScope.equals(AttendanceIsInScopeEnum.BELONG.getCode())) {
                    Integer sourceId = record.getSourceId();
                    List<AttendancePunchRecordVO> list = fieldRecordMap.get(sourceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(record);
                    fieldRecordMap.put(sourceId, list);
                }
            }
            if (record.getIsPunch().equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {
                if (record.getPunchStatus().equals(AttendanceStatusEnum.INVALID_PUNCH.getCode())) {//无效卡
                    incrNum(invalidNumMap,userId,orgId);
                } else {//已打卡且不是无效卡的
                    if (!remDups.contains(record.getPunchDate())) {
                        incrNum(attendancNumMap, userId, orgId);
                        remDups.add(record.getPunchDate());
                    }
                }
            } else {
                isFullMap.put(userId, orgId, false);
            }
        });
        //请假
        List<LeaveInfoVO> leaveInfoVOS = getLeaveInfoByQuery(queryForm);
        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
            Integer userId = leaveInfoVO.getUserId();
            Integer orgId = leaveInfoVO.getOrgId();
            long diff;
            if (leaveInfoVO.getVacationStatus().equals(0)) { // 按班次请假
                diff = leaveInfoVO.getEndTime().getTime() - leaveInfoVO.getStartTime().getTime();
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

        //工作时长：考勤范围内的迟到和早退将影响关联的上班班次的时长，请假扣除关联的上班班次的时长；加班和外勤不影响上班班次的时长
        Table<Integer,Integer, Long> workDateMinuteMap = sumWorkDateInfo(queryForm, punchRecordMap, laterMinuteMap,
                laterNumMap, isFullMap, earlyMinuteMap, earlyNumMap, workDateOvertimeMinuteMap, workDateOvertime30MinuteMap, leaveInfoVOS);

        //外勤
        FieldInfoQueryForm fieldInfoQueryForm = new FieldInfoQueryForm();
        fieldInfoQueryForm.setBetweenDate(queryForm.getBetweenDate());
        fieldInfoQueryForm.setAndDate(queryForm.getAndDate());
        fieldInfoQueryForm.setApprovalStatus(1);
        fieldInfoQueryForm.setWhetherPage(false);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldInfoQueryForm);
        fieldInfoVOS.forEach(fieldInfoVO -> {
            Integer id = fieldInfoVO.getId();
            Integer userId = fieldInfoVO.getUserId();
            Integer orgId = fieldInfoVO.getCompanyId();
            Date sTime = fieldInfoVO.getStartTime();
            Date eTime = fieldInfoVO.getEndTime();
            long diff = eTime.getTime() - sTime.getTime();
            List<AttendancePunchRecordVO> list = fieldRecordMap.get(id);
            if (list!=null && !list.isEmpty()) {
                AttendancePunchRecordVO first = list.get(0);
                if (list.size() > 1) {
                    AttendancePunchRecordVO last = list.get(1);
                    if (AttendanceTypeEnum.OFFDUTY.getCode().equals(first.getPunchType())) {
                        first = last;
                        last = list.get(0);
                    }
                    Date onPunchTime = first.getPunchTime();
                    Date offPunchTime = last.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {
                        diff -= onPunchTime.getTime() - sTime.getTime();
                    }
                    if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(last.getPunchStatus())) {
                        diff -= eTime.getTime() - offPunchTime.getTime();
                    }
                } else {
                    Date punchTime = first.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                        diff -= punchTime.getTime() - sTime.getTime();
                    } else if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(first.getPunchStatus())) {//早退
                        diff -= eTime.getTime() - punchTime.getTime();
                    }
                }
            } else {
                diff = 0;
            }
            incrMinute(fieldMinuteMap, userId, orgId, diff);
            incrNum(fieldCounts, userId, orgId);
        });

        Table<Integer,Integer, Integer> restDateOverCounts = HashBasedTable.create();
        List<WorkOvertimeInfoVO> workOvertimeInfoVOS = getRestWorkOvertimeByQuery(queryForm);
        Table<Integer,Integer, Long> restDateOvertimeMap = sumRestWorkOvertimeInfo(workOvertimeInfoVOS, workOvertimeRecordMap, restDateOverCounts);

        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(queryForm.getWhetherPage());
        model.setPageNum(queryForm.getPageNum());
        model.setPageSize(queryForm.getPageSize());
        if (queryForm.getOrgId() != null) {
            model.setOrgIds(Arrays.asList(queryForm.getOrgId()));
            if (userIds!=null && !userIds.isEmpty()) {
                model.setUserIds(userIds);
            }
        } else {
            if (userOrgIds!=null && !userOrgIds.isEmpty()) {
                model.setUserOrgIds(userOrgIds);
            }
        }
        model.setUserName(name);
        model.setWorkStatus(new Byte[]{0, 1, 3});
        PageInfo<SysUserInfoDetail> userPage = remoteSystemServiceFeign.findSysUserEmployeeWithOrgList(model);
        List<AttendanceStatisticsVO> result = new ArrayList<>();
        List<SysUserInfoDetail> userList = userPage.getList();
        if (userList!=null && !userList.isEmpty()) {
            userList.forEach(user -> {
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
                Long workOvertimeMakeupMinute = workOvertimeMakeupMinutes.get(userId, orgId);
                if (workOvertimeMakeupMinute == null) {
                    workOvertimeMakeupMinute = 0L;
                }
                statistics.setRestDateOvertimeMinute(DateUtil.micro2Min(restMinute) + workOvertimeMakeupMinute);
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
                Long workDateMinute = workDateMinuteMap.get(userId, orgId);
                if (workDateMinute == null) {
                    workDateMinute = 0L;
                }
                Long workDateMakeupMinute = workDateMakeupMinutes.get(userId, orgId);
                if (workDateMakeupMinute == null) {
                    workDateMakeupMinute = 0L;
                }
                statistics.setWorkDateMinute(DateUtil.micro2Min(workDateMinute) + workDateMakeupMinute);
                Boolean isFull = isFullMap.get(userId, orgId);
                String isFullStr = "--";
                if (type == 0) {
                    if (isFull == null || isFull) {
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
        }
        PageInfo<AttendanceStatisticsVO> pageInfo = new PageInfo();
        pageInfo.setList(result);
        pageInfo.setPageNum(queryForm.getPageNum());
        pageInfo.setPageSize(queryForm.getPageSize());
        pageInfo.setTotal(userPage.getTotal());
        return pageInfo;
    }

    private Table<Integer, Integer, Long> sumRestWorkOvertimeInfo(List<WorkOvertimeInfoVO> workOvertimeInfoVOS, Map<Integer, List<AttendancePunchRecordVO>> workOvertimeRecordMap, Table<Integer, Integer, Integer> restDateOverCounts) {
        Table<Integer, Integer, Long> restDateOvertimeMap = HashBasedTable.create();
        workOvertimeInfoVOS.forEach(workOvertimeInfoVO -> {
            Integer id = workOvertimeInfoVO.getId();
            Integer userId = workOvertimeInfoVO.getUserId();
            Integer orgId = workOvertimeInfoVO.getCompanyId();
            Date sTime = workOvertimeInfoVO.getStartTime();
            Date eTime = workOvertimeInfoVO.getEndTime();
            long diff = eTime.getTime() - sTime.getTime();
            List<AttendancePunchRecordVO> list = workOvertimeRecordMap.get(id);
            if (list!=null && !list.isEmpty()) {
                AttendancePunchRecordVO first = list.get(0);
                if (list.size() > 1) {
                    AttendancePunchRecordVO last = list.get(1);
                    if (AttendanceTypeEnum.OFFDUTY.getCode().equals(first.getPunchType())) {
                        first = last;
                        last = list.get(0);
                    }
                    Date onPunchTime = first.getPunchTime();
                    Date offPunchTime = last.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {
                        diff -= onPunchTime.getTime() - sTime.getTime();
                    }
                    if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(last.getPunchStatus())) {
                        diff -= eTime.getTime() - offPunchTime.getTime();
                    }
                } else {
                    Date punchTime = first.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                        diff -= punchTime.getTime() - sTime.getTime();
                    } else if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(first.getPunchStatus())) {//早退
                        diff -= eTime.getTime() - punchTime.getTime();
                    }
                }
            } else {
                diff = 0;
            }
            incrMinute(restDateOvertimeMap,userId,orgId,diff);
            incrNum(restDateOverCounts,userId, orgId);
        });
        return restDateOvertimeMap;
    }

    /**
     * 统计休息日加班的加班时长
     *
     * @param queryForm
     * @return
     */
    private long sumRestWorkOvertimeInfo(AttendanceStatisticsQueryForm queryForm) {
        long minute = 0;
        List<WorkOvertimeInfoVO> workOvertimeInfoVOS = getRestWorkOvertimeByQuery(queryForm);
        List<AttendancePunchRecordVO> punchRecordVOS = getPunchRecordByQuery(queryForm);
        Map<Integer, List<AttendancePunchRecordVO>> workOvertimeRecordMap = new HashMap<>(16);
        punchRecordVOS.forEach(record-> {
            Byte isInScope = record.getIsInScope();
            if (AttendanceSourceEnum.WORK_OVERTIME.getCode().equals(record.getSource())) {// 加班
                if (isInScope.equals(AttendanceIsInScopeEnum.BELONG.getCode())) {
                    Integer sourceId = record.getSourceId();
                    List<AttendancePunchRecordVO> list = workOvertimeRecordMap.get(sourceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(record);
                    workOvertimeRecordMap.put(sourceId, list);
                }
            }
        });
        for (WorkOvertimeInfoVO workOvertimeInfoVO : workOvertimeInfoVOS) {
            Integer id = workOvertimeInfoVO.getId();
            Date sTime = workOvertimeInfoVO.getStartTime();
            Date eTime = workOvertimeInfoVO.getEndTime();
            long diff = eTime.getTime() - sTime.getTime();
            List<AttendancePunchRecordVO> list = workOvertimeRecordMap.get(id);
            if (list!=null && !list.isEmpty()) {
                AttendancePunchRecordVO first = list.get(0);
                if (list.size() > 1) {
                    AttendancePunchRecordVO last = list.get(1);
                    if (AttendanceTypeEnum.OFFDUTY.getCode().equals(first.getPunchType())) {
                        first = last;
                        last = list.get(0);
                    }
                    Date onPunchTime = first.getPunchTime();
                    Date offPunchTime = last.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {
                        diff -= onPunchTime.getTime() - sTime.getTime();
                    }
                    if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(last.getPunchStatus())) {
                        diff -= eTime.getTime() - offPunchTime.getTime();
                    }
                } else {
                    Date punchTime = first.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                        diff -= punchTime.getTime() - sTime.getTime();
                    } else if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(first.getPunchStatus())) {//早退
                        diff -= eTime.getTime() - punchTime.getTime();
                    }
                }
            } else {
                diff = 0;
            }
            minute += diff;
        }
        return DateUtil.micro2Min(minute);
    }

    private List<WorkOvertimeInfoVO> getRestWorkOvertimeByQuery(AttendanceStatisticsQueryForm queryForm) {
        WorkOvertimeInfoQueryForm workOvertimeQuery = new WorkOvertimeInfoQueryForm();
        workOvertimeQuery.setStartTime(queryForm.getBetweenDate());
        workOvertimeQuery.setEndTime(queryForm.getAndDate());
        workOvertimeQuery.setApprovalStatus(1);
        workOvertimeQuery.setWhetherPage(false);
        workOvertimeQuery.setUserId(queryForm.getUserId());
        return workOvertimeInfoBiz.findWorkOvertimeInfoList(workOvertimeQuery);
    }

    private List<LeaveInfoVO> getLeaveInfoByQuery(AttendanceStatisticsQueryForm queryForm) {
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setBetweenStartDate(queryForm.getBetweenDate());
        leaveQueryForm.setAndStartDate(queryForm.getAndDate());
        leaveQueryForm.setApprovalStatus(1);
        leaveQueryForm.setUserId(queryForm.getUserId());
        leaveQueryForm.setWhetherPage(false);
        return leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
    }

    private List<AttendancePunchRecordVO> getPunchRecordByQuery(AttendanceStatisticsQueryForm queryForm) {
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setWhetherPage(false);
        recordQueryForm.setBetweenDate(queryForm.getBetweenDate());
        recordQueryForm.setAndDate(queryForm.getAndDate());
        recordQueryForm.setOrgId(queryForm.getOrgId());
        recordQueryForm.setUserId(queryForm.getUserId());
        return mapper.findAttendancePunchRecordWithScheduleIdList(recordQueryForm);
    }

    private List<EmployeeScheduleVO> getEmployeeScheduleByQuery(AttendanceStatisticsQueryForm queryForm) {
        //上班班次
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setBetweenWorkDate(queryForm.getBetweenDate());
        employeeScheduleQueryForm.setAndWorkDate(queryForm.getAndDate());
        employeeScheduleQueryForm.setType("上班");
        employeeScheduleQueryForm.setUserId(queryForm.getUserId());
        return employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
    }

    private Table<Integer,Integer, Long> sumWorkDateInfo(AttendanceStatisticsQueryForm queryForm, Map<Integer, List<AttendancePunchRecordVO>> punchRecordMap, Table<Integer, Integer, Long> laterMinuteMap, Table<Integer, Integer, Integer> laterNumMap, Table<Integer, Integer, Boolean> isFullMap, Table<Integer, Integer, Long> earlyMinuteMap, Table<Integer, Integer, Integer> earlyNumMap, Table<Integer, Integer, Long> workDateOvertimeMinuteMap, Table<Integer, Integer, Long> workDateOvertime30MinuteMap, List<LeaveInfoVO> leaveInfoVOS) {
        List<EmployeeScheduleVO> employeeScheduleVOS = getEmployeeScheduleByQuery(queryForm);
        Table<Integer,Integer, Long> workDateMinuteMap = HashBasedTable.create();
        for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
            Integer userId = employeeScheduleVO.getEmployeeId();
            Integer orgId = employeeScheduleVO.getClinicId();
            Integer esId = employeeScheduleVO.getId();
            Date startTime = employeeScheduleVO.getFirstStartTime();
            Date endTime = employeeScheduleVO.getSecondEndTime();
            if (endTime == null) {
                endTime = employeeScheduleVO.getFirstEndTime();
            }
            Date workDate = employeeScheduleVO.getWorkDate();
            long diff = endTime.getTime() - startTime.getTime();//班次时长
            List<AttendancePunchRecordVO> list = punchRecordMap.get(esId);
            if (list!=null && !list.isEmpty()) {
                AttendancePunchRecordVO first = list.get(0);
                if (list.size() == 2) {
                    AttendancePunchRecordVO last = list.get(1);
                    if (AttendanceTypeEnum.OFFDUTY.getCode().equals(first.getPunchType())) {
                        last = first;
                        first = list.get(1);
                    }
                    Date sTime = first.getStartTime();
                    Date eTime = first.getEndTime();
                    Date onTime = first.getPunchTime();
                    Date offTime = last.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                        long lateDiff = onTime.getTime() - sTime.getTime();
//                        System.out.println(DateUtil.micro2Min(lateDiff));
                        incrMinute(laterMinuteMap,userId,orgId,lateDiff);
                        incrNum(laterNumMap,userId,orgId);
                        diff -= lateDiff;
                        isFullMap.put(userId, orgId, false);
                    }
                    if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(last.getPunchStatus())) {//早退
                        long earlyDiff = eTime.getTime() - offTime.getTime();
//                        System.out.println(DateUtil.micro2Min(earlyDiff));
                        incrMinute(earlyMinuteMap,userId,orgId,earlyDiff);
                        incrNum(earlyNumMap,userId,orgId);
                        diff -= earlyDiff;
                        isFullMap.put(userId, orgId, false);
                    } else if (AttendanceStatusEnum.OFFDUTY_PUNCH.getCode().equals(last.getPunchStatus())) {
                        long workOvertimeDiff = offTime.getTime()-eTime.getTime();
//                        System.out.println(DateUtil.micro2Min(workOvertimeDiff));
                        if (DateUtil.micro2Min(workOvertimeDiff) >= 1) {
                            incrMinute(workDateOvertimeMinuteMap, userId, orgId, workOvertimeDiff);
                            if (DateUtil.micro2Min(workOvertimeDiff) >= 30) {
                                incrMinute(workDateOvertime30MinuteMap, userId, orgId, workOvertimeDiff);
                            }
                        }
                    }
                    for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
                        Date lSTime = leaveInfoVO.getStartTime();
                        Date lETime = leaveInfoVO.getEndTime();
                        if (leaveInfoVO.getVacationStatus().equals(0) && leaveInfoVO.getScheduleId().equals(esId)
                                && sTime.before(lSTime) && eTime.after(lETime)) {
                            diff -= lETime.getTime() - lSTime.getTime();
                        }
                    }
                } else {
                    Date sTime = first.getStartTime();
                    Date eTime = first.getEndTime();
                    Date punchTime = first.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                        long lateDiff = punchTime.getTime() - sTime.getTime();
//                        System.out.println(DateUtil.micro2Min(lateDiff));
                        incrMinute(laterMinuteMap,userId,orgId,lateDiff);
                        incrNum(laterNumMap,userId,orgId);
                        diff -= lateDiff;
                        isFullMap.put(userId, orgId, false);
                    } else if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(first.getPunchStatus())) {//早退
                        long earlyDiff = eTime.getTime() - punchTime.getTime();
//                        System.out.println(DateUtil.micro2Min(earlyDiff));
                        incrMinute(earlyMinuteMap,userId,orgId,earlyDiff);
                        incrNum(earlyNumMap,userId,orgId);
                        diff -= earlyDiff;
                        isFullMap.put(userId, orgId, false);
                    }
                    if (AttendanceStatusEnum.OFFDUTY_PUNCH.getCode().equals(first.getPunchStatus())) {
                        long workOvertimeDiff = punchTime.getTime()-eTime.getTime();
//                        System.out.println(DateUtil.micro2Min(workOvertimeDiff));
                        if (DateUtil.micro2Min(workOvertimeDiff) >= 1) {
                            incrMinute(workDateOvertimeMinuteMap, userId, orgId, workOvertimeDiff);
                            if (DateUtil.micro2Min(workOvertimeDiff) >= 30) {
                                incrMinute(workDateOvertime30MinuteMap, userId, orgId, workOvertimeDiff);
                            }
                        }
                    }
                    for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
                        Date lSTime = leaveInfoVO.getStartTime();
                        Date lETime = leaveInfoVO.getEndTime();
                        if (leaveInfoVO.getVacationStatus().equals(0) && leaveInfoVO.getScheduleId().equals(esId)
                                && sTime.before(lSTime) && eTime.after(lETime)) {
                            diff -= lETime.getTime() - lSTime.getTime();
                        }
                    }
                }
            } else {//有效时间范围外打卡
                diff = 0;
                isFullMap.put(userId, orgId, false);
            }
            System.out.println(DateUtil.micro2Min(diff));
            incrMinute(workDateMinuteMap,userId,orgId,diff);
        }
        return workDateMinuteMap;
    }

    private long sumWorkDateInfo(AttendanceStatisticsQueryForm queryForm) {
        List<LeaveInfoVO> leaveInfoVOS = getLeaveInfoByQuery(queryForm);
        List<AttendancePunchRecordVO> punchRecordVOS = getPunchRecordByQuery(queryForm);
        Map<Integer, List<AttendancePunchRecordVO>> punchRecordMap = new HashMap<>();
        punchRecordVOS.forEach(record->{
            Integer esId = record.getEsId();
            Byte source = record.getSource();
            Byte isInScope = record.getIsInScope();
            if (AttendanceSourceEnum.WORK_SCHEDULE.getCode().equals(source)) {//上班班次
                if (isInScope.equals(AttendanceIsInScopeEnum.BELONG.getCode())) {
                    List<AttendancePunchRecordVO> list = punchRecordMap.get(esId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(record);
                    punchRecordMap.put(esId, list);
                }
            }
        });
        List<EmployeeScheduleVO> employeeScheduleVOS = getEmployeeScheduleByQuery(queryForm);
        long result = 0;
        for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
            Integer esId = employeeScheduleVO.getId();
            Date startTime = employeeScheduleVO.getFirstStartTime();
            Date endTime = employeeScheduleVO.getSecondEndTime();
            if (endTime == null) {
                endTime = employeeScheduleVO.getFirstEndTime();
            }
            Date workDate = employeeScheduleVO.getWorkDate();
            long diff = endTime.getTime() - startTime.getTime();//班次时长
            List<AttendancePunchRecordVO> list = punchRecordMap.get(esId);
            if (list!=null && !list.isEmpty()) {
                AttendancePunchRecordVO first = list.get(0);
                if (list.size() == 2) {
                    AttendancePunchRecordVO last = list.get(1);
                    if (AttendanceTypeEnum.OFFDUTY.getCode().equals(first.getPunchType())) {
                        last = first;
                        first = list.get(1);
                    }
                    Date sTime = first.getStartTime();
                    Date eTime = first.getEndTime();
                    Date onTime = first.getPunchTime();
                    Date offTime = last.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                        long lateDiff = onTime.getTime() - sTime.getTime();
//                        System.out.println(DateUtil.micro2Min(lateDiff));
                        diff -= lateDiff;
                    }
                    if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(last.getPunchStatus())) {//早退
                        long earlyDiff = eTime.getTime() - offTime.getTime();
//                        System.out.println(DateUtil.micro2Min(earlyDiff));
                        diff -= earlyDiff;
                    }
                    for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
                        Date lSTime = leaveInfoVO.getStartTime();
                        Date lETime = leaveInfoVO.getEndTime();
                        if (leaveInfoVO.getVacationStatus().equals(0) && leaveInfoVO.getScheduleId().equals(esId)
                                && sTime.before(lSTime) && eTime.after(lETime)) {
                            diff -= lETime.getTime() - lSTime.getTime();
                        }
                    }
                } else {
                    Date sTime = first.getStartTime();
                    Date eTime = first.getEndTime();
                    Date punchTime = first.getPunchTime();
                    if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                        long lateDiff = punchTime.getTime() - sTime.getTime();
//                        System.out.println(DateUtil.micro2Min(lateDiff));
                        diff -= lateDiff;
                    } else if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(first.getPunchStatus())) {//早退
                        long earlyDiff = eTime.getTime() - punchTime.getTime();
//                        System.out.println(DateUtil.micro2Min(earlyDiff));
                        diff -= earlyDiff;
                    }
                    for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
                        Date lSTime = leaveInfoVO.getStartTime();
                        Date lETime = leaveInfoVO.getEndTime();
                        if (leaveInfoVO.getVacationStatus().equals(0) && leaveInfoVO.getScheduleId().equals(esId)
                                && sTime.before(lSTime) && eTime.after(lETime)) {
                            diff -= lETime.getTime() - lSTime.getTime();
                        }
                    }
                }
            } else {//有效时间范围外打卡
                diff = 0;
            }
            result += diff;
        }
        return DateUtil.micro2Min(result);
    }

    /**
     * 根据查询条件统计补入时长
     *
     * @param queryForm
     * @return
     */
    private List<AttendanceManualMakeupVO> makeupMinuteGroupByUserIdAndOrgId(AttendanceStatisticsQueryForm queryForm, Byte type) {
        AttendanceManualMakeupQueryForm makeupQueryForm = new AttendanceManualMakeupQueryForm();
        makeupQueryForm.setWhetherPage(false);
        makeupQueryForm.setBetweenDate(queryForm.getBetweenDate());
        makeupQueryForm.setAndDate(queryForm.getAndDate());
        makeupQueryForm.setOrgId(queryForm.getOrgId());
        makeupQueryForm.setType(type);
        makeupQueryForm.setUserId(queryForm.getUserId());
        return attendanceManualMakeupBiz.sumAttendanceMakeupMinute(makeupQueryForm);
    }

    private boolean inScope(Date sTime, Date eTime, Date target, byte onDuty) {
        boolean inScope = true;
        if (target == null) {
            return false;
        }
        if (AttendanceTypeEnum.ONDUTY.getCode().equals(onDuty)) {
            if (target.after(eTime)) {
                inScope = false;
            }
        } else {
            if (target.before(sTime)) {
                inScope = false;
            }
        }
        return inScope;
    }

//    public PageInfo<AttendanceStatisticsVO> statisticsPunchRecord(AttendanceStatisticsQueryForm queryForm) {
//        String name = queryForm.getEmployeeName();
//        Byte type = queryForm.getType();
//        if (type == null) {
//            throw new ClientServiceException("请选择查询年月", PARAM_NOT_ALLOW_EMPTY);
//        }
//        setQueryFormDate(queryForm);
//        Set<Integer> userIds = new HashSet<>();
//        Set<String> userOrgIds = new HashSet<>();
//        // 打卡记录
//        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
//        recordQueryForm.setBetweenDate(queryForm.getBetweenDate());
//        recordQueryForm.setAndDate(queryForm.getAndDate());
//        recordQueryForm.setOrgId(queryForm.getOrgId());
//
//
//        recordQueryForm.setUserId(queryForm.getUserId());
//
//
//        List<AttendancePunchRecordVO> attendancePunchRecordVOS = mapper.findAttendancePunchRecordWithScheduleIdList(recordQueryForm);
//        List<AttendancePunchDateVO> attendancePunchDateVOS = punchRecord2PunchDate(attendancePunchRecordVOS);
//        Table<Integer,Integer, Long> workDateMinuteMap = HashBasedTable.create();
//        Table<Integer,Integer, Long> workDateOvertimeMinuteMap = HashBasedTable.create();
//        Table<Integer,Integer, Long> workDateOvertime30MinuteMap = HashBasedTable.create();
//        Table<Integer,Integer, Long> restDateOvertimeMap = HashBasedTable.create();
//        Table<Integer,Integer, Long> fieldMinuteMap = HashBasedTable.create();
//        Table<Integer,Integer, Integer> fieldCounts = HashBasedTable.create();
//        Table<Integer,Integer, Long> leaveMinuteMap = HashBasedTable.create();
//        Table<Integer,Integer, Integer> leaveCounts = HashBasedTable.create();
//        Table<Integer,Integer, Integer> laterNumMap = HashBasedTable.create();
//        Table<Integer,Integer, Long> laterMinuteMap = HashBasedTable.create();
//        Table<Integer,Integer, Integer> earlyNumMap = HashBasedTable.create();
//        Table<Integer,Integer, Long> earlyMinuteMap = HashBasedTable.create();
//        Table<Integer,Integer, Integer> unpunchNumMap = HashBasedTable.create();
//        Table<Integer,Integer, Integer> invalidNumMap = HashBasedTable.create();
//        Table<Integer,Integer, Integer> attendancNumMap = HashBasedTable.create();
//        Table<Integer,Integer, Boolean> isFullMap = HashBasedTable.create();
//        Table<Integer, Integer, Long> overrideField = HashBasedTable.create();
//        //上班班次
//        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
//        employeeScheduleQueryForm.setBetweenWorkDate(queryForm.getBetweenDate());
//        employeeScheduleQueryForm.setAndWorkDate(queryForm.getAndDate());
//        employeeScheduleQueryForm.setType("上班");
//        List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleBiz.findEmployeeScheduleList(employeeScheduleQueryForm);
//        Map<Integer, EmployeeScheduleVO> employeeScheduleVOMap = new HashMap<>(employeeScheduleVOS.size());
//        employeeScheduleVOS.forEach(employeeScheduleVO -> employeeScheduleVOMap.put(employeeScheduleVO.getId(), employeeScheduleVO));
//        //请假
//        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
//        leaveQueryForm.setBetweenStartDate(queryForm.getBetweenDate());
//        leaveQueryForm.setAndStartDate(queryForm.getAndDate());
//        leaveQueryForm.setApprovalStatus(1);
//        leaveQueryForm.setWhetherPage(false);
//        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
//        Table<Integer,Integer, List<LeaveInfoVO>> leaveInfoMap = HashBasedTable.create();
//        for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
//            Integer userId = leaveInfoVO.getUserId();
//            Integer orgId = leaveInfoVO.getOrgId();
//            long diff;
//            if (leaveInfoVO.getVacationStatus().equals(0)) { // 按班次请假
//                diff = leaveInfoVO.getEndTime().getTime() - leaveInfoVO.getStartTime().getTime();
//                List<LeaveInfoVO> list = leaveInfoMap.get(userId, orgId);
//                if (list == null) {
//                    list = new ArrayList<>();
//                }
//                list.add(leaveInfoVO);
//                leaveInfoMap.put(userId, orgId, list);
//            } else { // 按天请假
//                int days;
//                try {
//                    days = DateUtil.daysBetween(leaveInfoVO.getStartDate(), leaveInfoVO.getEndDate());
//                } catch (ParseException e) {
//                    throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
//                }
//                diff = DAY_LEAVE_MILLSEC * days;
//            }
//            userIds.add(userId);
//            userOrgIds.add(userId + "," + orgId);
//            incrMinute(leaveMinuteMap, userId, orgId, diff);
//            incrNum(leaveCounts, userId, orgId);
//            isFullMap.put(userId, orgId, false);
//        }
//        Date curDate = DateUtil.getCurrentDate();
//        Set<Integer> fieldIds = new HashSet<>();
//        for (AttendancePunchDateVO attendancePunchDateVO : attendancePunchDateVOS) {
//            Integer userId = attendancePunchDateVO.getUserId();
//            userIds.add(userId);
//            Date date = attendancePunchDateVO.getPunchDate();
//            Integer onDutyOrgId = attendancePunchDateVO.getOnDutyOrgId();
//            Byte onDutyStatus = attendancePunchDateVO.getOnDutyStatus();
//            Date onPunchTime = attendancePunchDateVO.getOnDutyPunchTime();
//            Date onStartTime = attendancePunchDateVO.getOnDutyStartTime();
//            Date onEndTime = attendancePunchDateVO.getOnDutyEndTime();
//            Integer offDutyOrgId = attendancePunchDateVO.getOffDutyOrgId();
//            Date offStartTime = attendancePunchDateVO.getOffDutyStartTime();
//            Date offPunchTime = attendancePunchDateVO.getOffDutyPunchTime();
//            Date offEndTime = attendancePunchDateVO.getOffDutyEndTime();
//            if (onDutyOrgId != null) {
//                userOrgIds.add(userId + "," + onDutyOrgId);
//            }
//            if (offDutyOrgId != null) {
//                userOrgIds.add(userId + "," + offDutyOrgId);
//            }
//            Byte workOvertime = attendancePunchDateVO.getWorkOvertime();
//            Byte field = attendancePunchDateVO.getField();
//            // 上班打卡
//            boolean hasAttendance = false;
//            switch (onDutyStatus) {
//                case 0: {
//                    hasAttendance = true;
//                    long diff = 0;
//                    if (inScope(onStartTime,onEndTime,onPunchTime,offPunchTime)) {
//                        diff = computeWithoutLeave(date, onEndTime, onStartTime, userId, onDutyOrgId, leaveInfoMap);
//                    }
//                    if (userId == 1597) {
//                        System.out.println("");
//                    }
//                    incrMinute(workDateMinuteMap, userId, onDutyOrgId, diff);
//                    break;
//                }
//                case 1: {
//                    hasAttendance = true;
//                    if (workOvertime==0 && field==0) {
//                        incrNum(laterNumMap, userId, onDutyOrgId);
//                        incrMinute(laterMinuteMap, userId, onDutyOrgId, onPunchTime.getTime()-onStartTime.getTime());
//                        isFullMap.put(userId, onDutyOrgId, false);
//                    }
//                    long diff = 0;
//                    if (inScope(onStartTime,onEndTime,onPunchTime,offPunchTime)) {
//                        diff = computeWithoutLeave(date, onEndTime, onPunchTime, userId, onDutyOrgId, leaveInfoMap);
//                    }
//                    if (userId == 1597) {
//                        System.out.println("");
//                    }
//                    incrMinute(workDateMinuteMap, userId, onDutyOrgId, diff);
//                    break;
//                }
//                case 4: {
//                    incrNum(invalidNumMap, userId, onDutyOrgId);
//                    isFullMap.put(userId, onDutyOrgId, false);
//                    break;
//                }
//                case 5: {
//                    if (!curDate.equals(date) && workOvertime==0 && field==0) {
//                        incrNum(unpunchNumMap, userId, onDutyOrgId);
//                        isFullMap.put(userId, onDutyOrgId, false);
//                    }
//                    break;
//                }
//                default:
//            }
//            if (hasAttendance) {
//                incrNum(attendancNumMap, userId, onDutyOrgId);
//                hasAttendance = false;
//            }
//            // 下班打卡
//            Byte offDutyStatus = attendancePunchDateVO.getOffDutyStatus();
//            switch (offDutyStatus) {
//                case 2:{
//                    hasAttendance = true;
//                    long diff = 0;
//                    if (userId == 1597) {
//                        System.out.println("");
//                    }
//                    if (inScope(offStartTime,offEndTime,onPunchTime,offPunchTime)) {
//                        diff = computeWithoutLeave(date, offEndTime, offStartTime, userId, offDutyOrgId, leaveInfoMap);
//                        incrMinute(workDateMinuteMap, userId, offDutyOrgId, diff);
//                        diff = offPunchTime.getTime() - offEndTime.getTime();
//                        if (workOvertime == 0 && field == 0 && DateUtil.micro2Min(diff) >= 1) {
//                            incrMinute(workDateOvertimeMinuteMap, userId, offDutyOrgId, diff);
//                            if (DateUtil.micro2Min(diff) >= 30) {
//                                incrMinute(workDateOvertime30MinuteMap, userId, offDutyOrgId, diff);
//                            }
//                        }
//                    }
//                    break;
//                }
//                case 3:{
//                    hasAttendance = true;
//                    if (workOvertime==0 && field==0) {
//                        isFullMap.put(userId, offDutyOrgId, false);
//                        incrNum(earlyNumMap, userId, offDutyOrgId);
//                        incrMinute(earlyMinuteMap, userId, offDutyOrgId, offEndTime.getTime()-offPunchTime.getTime());
//                    }
//                    long diff = 0;
//                    if (inScope(offStartTime,offEndTime,onPunchTime,offPunchTime)) {
//                        diff = computeWithoutLeave(date, offPunchTime, offStartTime, userId, offDutyOrgId, leaveInfoMap);
//                    }
//                    if (userId == 1597) {
//                        System.out.println("");
//                    }
//                    incrMinute(workDateMinuteMap, userId, offDutyOrgId, diff);
//                    break;
//                }
//                case 4:{
//                    incrNum(invalidNumMap, userId, offDutyOrgId);
//                    isFullMap.put(userId, offDutyOrgId, false);
//                    break;
//                }
//                case 5:{
//                    if (!curDate.equals(date) && workOvertime==0 && field==0) {
//                        incrNum(unpunchNumMap, userId, offDutyOrgId);
//                        isFullMap.put(userId, offDutyOrgId, false);
//                    }
//                    break;
//                }
//                default:
//            }
//            if (hasAttendance) {
//                incrNum(attendancNumMap, userId, offDutyOrgId);
//            }
//            switch (workOvertime) {
//                case 1: {// 上班加班
//                    if (onDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, offEndTime, onPunchTime, offPunchTime)) {
//                            Date startTime = onPunchTime;
//                            if (startTime.before(onStartTime)) {
//                                startTime = onStartTime;
//                            }
//                            diff = onEndTime.getTime() - startTime.getTime();
//                        }
//                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
//                    }
//                    break;
//                }
//                case 2: {// 下班加班
//                    if (offDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, offEndTime, onPunchTime, offPunchTime)) {
//                            Date endTime = offPunchTime;
//                            if (endTime.after(offEndTime)) {
//                                endTime = offEndTime;
//                            }
//                            diff = endTime.getTime() - offStartTime.getTime();
//                        }
//                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
//                    }
//                    break;
//                }
//                case 3: {// 上班下班同一加班
//                    if (onDutyStatus<5 && offDutyStatus<5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, offEndTime, onPunchTime, offPunchTime)){
//                            Date startTime = onPunchTime;
//                            if (startTime.before(onStartTime)) {
//                                startTime = onStartTime;
//                            }
//                            Date endTime = offPunchTime;
//                            if (endTime.after(offEndTime)) {
//                                endTime = offEndTime;
//                            }
//                            diff = endTime.getTime() - startTime.getTime();
//                        }
//                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
//                    }
//                    break;
//                }
//                case 4: {// 上班下班两个不同加班
//                    if (onDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, offEndTime, onPunchTime, offPunchTime)) {
//                            Date startTime = onPunchTime;
//                            if (startTime.before(onStartTime)) {
//                                startTime = onStartTime;
//                            }
//                            diff = onEndTime.getTime() - startTime.getTime();
//                        }
//                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
//                    }
//                    if (offDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, offEndTime, onPunchTime, offPunchTime)) {
//                            Date endTime = offPunchTime;
//                            if (endTime.after(offEndTime)) {
//                                endTime = offEndTime;
//                            }
//                            diff = endTime.getTime() - offStartTime.getTime();
//                        }
//                        incrMinute(restDateOvertimeMap, userId, onDutyOrgId, diff);
//                    }
//                    break;
//                }
//            }
//            Integer onSourceId = attendancePunchDateVO.getOnDutySourceId();
//            Integer offSourceId = attendancePunchDateVO.getOffDutySourceId();
//            switch (field) {
//                case 1: {// 仅上班外勤
//                    if (onDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, onEndTime, onPunchTime, offPunchTime)) {
//                            Date startTime = onPunchTime;
//                            if (startTime.before(onStartTime)) {
//                                startTime = onStartTime;
//                            }
//                            diff = onEndTime.getTime() - startTime.getTime();
//                        }
//                        incrMinute(fieldMinuteMap, userId, onDutyOrgId, diff);
//                        incrMinute(overrideField, userId, onDutyOrgId, diff);
//                        incrNum(fieldCounts, userId, onDutyOrgId);
//                        fieldIds.add(onSourceId);
//                        fieldIds.add(offSourceId);
//                    }
//                    break;
//                }
//                case 2: {// 仅下班外勤
//                    if (offDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(offStartTime, offEndTime, onPunchTime, offPunchTime)) {
//                            Date endTime = offPunchTime;
//                            if (endTime.after(offEndTime)) {
//                                endTime = offEndTime;
//                            }
//                            diff = endTime.getTime() - offStartTime.getTime();
//                        }
//                        incrMinute(fieldMinuteMap, userId, offDutyOrgId, diff);
//                        incrMinute(overrideField, userId, offDutyOrgId, diff);
//                        incrNum(fieldCounts, userId, offDutyOrgId);
//                        fieldIds.add(onSourceId);
//                        fieldIds.add(offSourceId);
//                    }
//                    break;
//                }
//                case 3: {// 上班下班同一个外勤
//                    if (onDutyStatus<5 && offDutyStatus<5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, offEndTime, onPunchTime, offPunchTime)) {
//                            Date startTime = onPunchTime;
//                            if (startTime.before(onStartTime)) {
//                                startTime = onStartTime;
//                            }
//                            Date endTime = offPunchTime;
//                            if (endTime.after(offEndTime)) {
//                                endTime = offEndTime;
//                            }
//                            diff = endTime.getTime() - startTime.getTime();
//                        }
//                        incrMinute(fieldMinuteMap, userId, onDutyOrgId, diff);
//                        incrMinute(overrideField, userId, onDutyOrgId, diff);
//                        incrNum(fieldCounts, userId, onDutyOrgId);
//                        fieldIds.add(onSourceId);
//                        fieldIds.add(offSourceId);
//                    }
//                    break;
//                }
//                case 4: {// 上班下班两个不同的外勤
//                    if (onDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(onStartTime, onEndTime, onPunchTime, offPunchTime)) {
//                            Date startTime = onPunchTime;
//                            if (startTime.before(onStartTime)) {
//                                startTime = onStartTime;
//                            }
//                            diff = onEndTime.getTime() - startTime.getTime();
//                        }
//                        incrMinute(fieldMinuteMap, userId, onDutyOrgId, diff);
//                        incrMinute(overrideField, userId, onDutyOrgId, diff);
//                        incrNum(fieldCounts, userId, onDutyOrgId);
//                    }
//                    if (offDutyStatus < 5) {
//                        long diff = 0;
//                        if (inScope(offStartTime, offEndTime, onPunchTime, offPunchTime)) {
//                            Date endTime = offPunchTime;
//                            if (endTime.after(offEndTime)) {
//                                endTime = offEndTime;
//                            }
//                            diff = endTime.getTime() - offStartTime.getTime();
//                        }
//                        incrMinute(fieldMinuteMap, userId, offDutyOrgId, diff);
//                        incrMinute(overrideField, userId, offDutyOrgId, diff);
//                        incrNum(fieldCounts, userId, offDutyOrgId);
//                        fieldIds.add(onSourceId);
//                        fieldIds.add(offSourceId);
//                    }
//                    break;
//                }
//            }
//        }
//        //被覆盖的外勤（排除已打卡和未打卡的）
//        FieldInfoQueryForm fieldInfoQueryForm = new FieldInfoQueryForm();
//        fieldInfoQueryForm.setBetweenDate(queryForm.getBetweenDate());
//        fieldInfoQueryForm.setAndDate(queryForm.getAndDate());
//        fieldInfoQueryForm.setApprovalStatus(1);
//        fieldInfoQueryForm.setWhetherPage(false);
//        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldInfoQueryForm);
//        fieldInfoVOS.forEach(fieldInfoVO -> {
//            Integer id = fieldInfoVO.getId();
//            if (!fieldIds.contains(id)) {
//                Integer userId = fieldInfoVO.getUserId();
//                Integer orgId = fieldInfoVO.getCompanyId();
//                long diff = fieldInfoVO.getEndTime().getTime() - fieldInfoVO.getStartTime().getTime();
//                incrMinute(fieldMinuteMap, userId, orgId, diff);
//                incrNum(fieldCounts, userId, orgId);
//            }
//        });
//
//        Table<Integer,Integer, Integer> restDateOverCounts = HashBasedTable.create();
//        WorkOvertimeInfoQueryForm workOvertimeQuery = new WorkOvertimeInfoQueryForm();
//        workOvertimeQuery.setStartTime(queryForm.getBetweenDate());
//        workOvertimeQuery.setEndTime(queryForm.getAndDate());
//        workOvertimeQuery.setApprovalStatus(1);
//        workOvertimeQuery.setWhetherPage(false);
//        List<WorkOvertimeInfoVO> workOvertimeInfoVOS = workOvertimeInfoBiz.findWorkOvertimeInfoList(workOvertimeQuery);
//        workOvertimeInfoVOS.forEach(workOvertimeInfoVO -> {
//            Integer userId = workOvertimeInfoVO.getUserId();
//            Integer orgId = workOvertimeInfoVO.getCompanyId();
//            incrNum(restDateOverCounts,userId, orgId);
//        });
//        SysUserEmployeeModel model = new SysUserEmployeeModel();
//        model.setWhetherPage(queryForm.getWhetherPage());
//        model.setPageNum(queryForm.getPageNum());
//        model.setPageSize(queryForm.getPageSize());
//        if (queryForm.getOrgId() != null) {
//            model.setOrgIds(Arrays.asList(queryForm.getOrgId()));
//            if (userIds != null && !userIds.isEmpty()) {
//                model.setUserIds(userIds);
//            }
//        } else {
//            if (userOrgIds != null && !userOrgIds.isEmpty()) {
//                model.setUserOrgIds(userOrgIds);
//            }
//        }
//        model.setUserName(name);
//        model.setWorkStatus(new Byte[]{0, 1, 3});
//        PageInfo<SysUserInfoDetail> userPage = remoteSystemServiceFeign.findSysUserEmployeeWithOrgList(model);
//        List<AttendanceStatisticsVO> result = new ArrayList<>();
//        List<SysUserInfoDetail> userList = userPage.getList();
//        if (userList!=null && !userList.isEmpty()) {
//            userList.forEach(user -> {
//                Integer userId = user.getUserId();
//                String companyIds = user.getCompanyIds();
//                Integer orgId = null;
//                if (StringHelper.isNotEmpty(companyIds)) {
//                    orgId = Integer.parseInt(companyIds);
//                }
//                AttendanceStatisticsVO statistics = new AttendanceStatisticsVO();
//                statistics.setUserId(userId);
//                statistics.setEmployeeName(user.getName());
//                statistics.setOrgId(orgId);
//                statistics.setOrgName(user.getCompanys());
//                Long restMinute = restDateOvertimeMap.get(userId, orgId);
//                if (restMinute == null) {
//                    restMinute = 0L;
//                }
//                statistics.setRestDateOvertimeMinute(DateUtil.micro2Min(restMinute));
//                Integer restCount = restDateOverCounts.get(userId, orgId);
//                if (restCount == null) {
//                    restCount = 0;
//                }
//                statistics.setWorkOvertimeNum(restCount);
//                Long leaveMinute = leaveMinuteMap.get(userId, orgId);
//                if (leaveMinute == null) {
//                    leaveMinute = 0L;
//                }
//                statistics.setLeaveMinute(DateUtil.micro2Min(leaveMinute));
//                Integer leaveCount = leaveCounts.get(userId, orgId);
//                if (leaveCount == null) {
//                    leaveCount = 0;
//                }
//                statistics.setLeaveNum(leaveCount);
//                Long fieldMinute = fieldMinuteMap.get(userId, orgId);
//                if (fieldMinute == null) {
//                    fieldMinute = 0L;
//                }
//                statistics.setFieldMinute(DateUtil.micro2Min(fieldMinute));
//                Integer fieldCount = fieldCounts.get(userId, orgId);
//                if (fieldCount == null) {
//                    fieldCount = 0;
//                }
//                statistics.setFieldNum(fieldCount);
//                Long workDateOvertimeMinute = workDateOvertimeMinuteMap.get(userId, orgId);
//                if (workDateOvertimeMinute == null) {
//                    workDateOvertimeMinute = 0L;
//                }
//                statistics.setWorkDateOvertimeMinute(DateUtil.micro2Min(workDateOvertimeMinute));
//                Long workDate30Minute = workDateOvertime30MinuteMap.get(userId, orgId);
//                if (workDate30Minute == null) {
//                    workDate30Minute = 0L;
//                }
//                if (userId == 1597) {
//                    System.out.println("");
//                }
//                statistics.setWorkDateOvertime30Minute(DateUtil.micro2Min(workDate30Minute));
//                Long overrideMinute = overrideField.get(userId, orgId);
//                if (overrideMinute == null) {
//                    overrideMinute = 0L;
//                }
//                incrMinute(workDateMinuteMap, userId, orgId, overrideMinute);// 外勤覆盖上班班次的（覆盖上班卡、覆盖下班卡）
//                incrMinute(workDateMinuteMap, userId, orgId, restMinute);// 加班覆盖上班班次的（覆盖上班卡、覆盖下班卡）
//                Long workDateMinute = workDateMinuteMap.get(userId, orgId);
//                if (workDateMinute == null) {
//                    workDateMinute = 0L;
//                }
//                statistics.setWorkDateMinute(DateUtil.micro2Min(workDateMinute));
//                Boolean isFull = isFullMap.get(userId, orgId);
//                String isFullStr = "--";
//                if (type == 0) {
//                    if (isFull == null || isFull) {
//                        isFullStr = "是";
//                    } else {
//                        isFullStr = "否";
//                    }
//                }
//                statistics.setIsFull(isFullStr);
//                Integer attendanceNum = attendancNumMap.get(userId, orgId);
//                if (attendanceNum == null) {
//                    attendanceNum = 0;
//                }
//                statistics.setAttendanceNum(attendanceNum);
//                Integer lateNum = laterNumMap.get(userId, orgId);
//                if (lateNum == null) {
//                    lateNum = 0;
//                }
//                statistics.setLateNum(lateNum);
//                Long lateMinute = laterMinuteMap.get(userId, orgId);
//                if (lateMinute == null) {
//                    lateMinute = 0L;
//                }
//                statistics.setLateMinute(DateUtil.micro2Min(lateMinute));
//                Integer earlyNum = earlyNumMap.get(userId, orgId);
//                if (earlyNum == null) {
//                    earlyNum = 0;
//                }
//                statistics.setEarlyNum(earlyNum);
//                Long earlyMinute = earlyMinuteMap.get(userId, orgId);
//                if (earlyMinute == null) {
//                    earlyMinute = 0L;
//                }
//                statistics.setEarlyMinute(DateUtil.micro2Min(earlyMinute));
//                Integer unpunchNum = unpunchNumMap.get(userId, orgId);
//                if (unpunchNum == null) {
//                    unpunchNum = 0;
//                }
//                statistics.setUnpunchNum(unpunchNum);
//                Integer invalidNum = invalidNumMap.get(userId, orgId);
//                if (invalidNum == null) {
//                    invalidNum = 0;
//                }
//                statistics.setInvalidNum(invalidNum);
//                result.add(statistics);
//            });
//        }
//        PageInfo<AttendanceStatisticsVO> pageInfo = new PageInfo();
//        pageInfo.setList(result);
//        pageInfo.setPageNum(queryForm.getPageNum());
//        pageInfo.setPageSize(queryForm.getPageSize());
//        pageInfo.setTotal(userPage.getTotal());
//        return pageInfo;
//    }

    /**
     * 校验是否在考勤时间范围内
     *
     * @param sTime 左边界
     * @param eTime 右边界
     * @param onPunchTime
     * @param offPunchTime
     * @return
     */
    private boolean inScope(Date sTime, Date eTime, Date onPunchTime, Date offPunchTime) {
        boolean inScope = true;
        if (onPunchTime==null || offPunchTime==null) {
            return false;
        }
        if (onPunchTime.after(eTime)) {
            inScope = false;
        }
        if (offPunchTime.before(sTime)) {
            inScope = false;
        }
        return inScope;
    }

    /*private boolean inScope(Date sTime, Date eTime, Date onPunchTime, Date offPunchTime) {
        boolean inScope = true;
        if (onPunchTime!=null && onPunchTime.after(eTime)) {
            inScope = false;
        }
        if (offPunchTime!=null && offPunchTime.before(sTime)) {
            inScope = false;
        }
        return inScope;
    }*/

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
        if (list!=null && !list.isEmpty()) {
            Date sDateTime;
            Date eDateTime;
            try {
                sDateTime = DateUtil.timeToDate(date, startTime);
                eDateTime = DateUtil.timeToDate(date, endTime);
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
                String endTimeStr = DateUtil.getLastDay(year,12);
                try {
                    startTime = sdf.parse(sdf.format(startTime));
                    endTime = sdf.parse(endTimeStr);
                } catch (ParseException e) {
                    throw new ClientServiceException("日期参数格式转换错误", DATA_TRANSFORMATION_EXIST);
                }
            } else if (dateStr.length() == 7) {
                String[] dateTmp = dateStr.split("-");
                int year = Integer.parseInt(dateTmp[0]);
                int month = Integer.parseInt(dateTmp[1]);
                startTime = DateUtil.getBeginTime(year,month);
                String endTimeStr = DateUtil.getLastDay(year,month);
                try {
                    startTime = sdf.parse(sdf.format(startTime));
                    endTime = sdf.parse(endTimeStr);
                } catch (ParseException e) {
                    throw new ClientServiceException("日期参数格式转换错误", DATA_TRANSFORMATION_EXIST);
                }
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
                if (punchType.equals(AttendanceTypeEnum.ONDUTY.getCode())) {
                    punchDate.setOnDutyPunchTime(punchTime);
                    punchDate.setOnDutyOrgId(orgId);
                    punchDate.setOnDutyName(name);
                    punchDate.setOnPunchAddress(vo.getPunchAddress());
                    if (source.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())) {
                        punchDate.setLeave((byte) 5);
                    } else if (source.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())) {
                        leave = 1;
                        if (punchDate.getLeave()!=null && punchDate.getLeave()!=0 && !punchDate.getOffDutySourceId().equals(sourceId)) {
                            leave = 4;
                        } else if (punchDate.getLeave()!=null && punchDate.getLeave()!=0 && punchDate.getOffDutySourceId().equals(sourceId)) {
                            leave = 3;
                        }
                    } else if (source.equals(AttendanceSourceEnum.WORK_OVERTIME.getCode())) {
                        workDateOvertime = 1;
                        if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && !punchDate.getOffDutySourceId().equals(sourceId)) {
                            workDateOvertime = 4;
                        } else if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && punchDate.getOffDutySourceId().equals(sourceId)) {
                            workDateOvertime = 3;
                        }
                    } else if (source.equals(AttendanceSourceEnum.FIELD.getCode())) {
                        field = 1;
                        if (punchDate.getField()!=null && punchDate.getField()!=0 && !punchDate.getOffDutySourceId().equals(sourceId)) {
                            field = 4;
                        } else if (punchDate.getField()!=null && punchDate.getField()!=0 && punchDate.getOffDutySourceId().equals(sourceId)) {
                            field = 3;
                        }
                    }
                    punchDate.setOnDutySourceId(sourceId);
                    punchDate.setOnDutyStartTime(vo.getStartTime());
                    punchDate.setOnDutyEndTime(vo.getEndTime());
                    Byte status = 5;
                    if (isPunch.equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {
                        status = punchStatus;
                    } else if (!AttendanceStatusEnum.INVALID_PUNCH.getCode().equals(punchStatus)) {//未打卡且休息
                        status = 6;
                    }
                    punchDate.setOnDutyStatus(status);
                } else {
                    punchDate.setOffDutyPunchTime(punchTime);
                    punchDate.setOffDutyOrgId(orgId);
                    punchDate.setOffDutyName(name);
                    punchDate.setOffPunchAddress(vo.getPunchAddress());
                    if (source.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())) {
                        punchDate.setLeave((byte) 3);
                    } else if (source.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())) {
                        leave = 2;
                        if (punchDate.getLeave()!=null && punchDate.getLeave()!=0 && !sourceId.equals(punchDate.getOnDutySourceId())) {
                            leave = 4;
                        } else if (punchDate.getLeave()!=null && punchDate.getLeave()!=0&& sourceId.equals(punchDate.getOnDutySourceId())) {
                            leave = 3;
                        }
                    } else if (source.equals(AttendanceSourceEnum.WORK_OVERTIME.getCode())) {
                        workDateOvertime = 2;
                        if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && !sourceId.equals(punchDate.getOnDutySourceId())) {
                            workDateOvertime = 4;
                        } else if (punchDate.getWorkOvertime()!=null && punchDate.getWorkOvertime()!=0 && sourceId.equals(punchDate.getOnDutySourceId())) {
                            workDateOvertime = 3;
                        }
                    } else if (source.equals(AttendanceSourceEnum.FIELD.getCode())) {
                        field = 2;
                        if (punchDate.getField()!=null && punchDate.getField()!=0 && !sourceId.equals(punchDate.getOnDutySourceId())) {
                            field = 4;
                        } else if (punchDate.getField()!=null && punchDate.getField()!=0 && sourceId.equals(punchDate.getOnDutySourceId())) {
                            field = 3;
                        }
                    }
                    punchDate.setOffDutySourceId(sourceId);
                    punchDate.setOffDutyEndTime(vo.getEndTime());
                    punchDate.setOffDutyStartTime(vo.getStartTime());
                    Byte status = 5;
                    if (isPunch.equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {
                        status = punchStatus;
                    } else if (!AttendanceStatusEnum.INVALID_PUNCH.getCode().equals(punchStatus)) {//未打卡且休息
                        status = 6;
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
     * 根据工作时长的分页查询考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public AttendancePunchPageInfoVO<AttendanceWorkDateMinuteVO> statisticsWorkDateByMinute(AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        List<Date> dateList = DateUtil.getBetweenDate(betweenDate, andDate);
        int total = dateList.size();
        if (queryForm.getWhetherPage()) {
            dateList = DateUtil.pagination(dateList, queryForm.getPageNum(), queryForm.getPageSize());
        }
        betweenDate = dateList.get(0);
        andDate = dateList.get(dateList.size()-1);
        // 排班
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        // 打卡
        Map<Integer, List<AttendancePunchRecordVO>> punchRecords = new HashMap<>();
        Map<Date, List<AttendancePunchRecordVO>> punchRecordMap = getPunchRecordMapGroupByDate(userId, null, betweenDate, andDate, null, punchRecords);
        // 手动补入时长
        Map<Date, AttendanceManualMakeupVO> manualMakeupMap = getManualMakeupMapGroupByDate(userId, orgId, MakeupTypeEnum.WORKDATE.getCode(), betweenDate, andDate);
        // 请假
        Map<Date, List<LeaveInfoVO>> leaveInfoMap = getLeaveInfoMapGroupByDate(userId, orgId, betweenDate, andDate);
        // 外勤
        FieldInfoQueryForm fieldQueryForm = new FieldInfoQueryForm();
        fieldQueryForm.setBetweenDate(betweenDate);
        fieldQueryForm.setAndDate(andDate);
        fieldQueryForm.setCompanyId(orgId);
        fieldQueryForm.setApprovalStatus(1);
        fieldQueryForm.setUserId(userId);
        fieldQueryForm.setWhetherPage(false);
        List<FieldInfoVO> fieldInfoVOS = fieldInfoBiz.findFieldInfoList(fieldQueryForm);
        List<AttendanceWorkDateMinuteVO> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hSdf = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        long minute = sumWorkDateInfo(queryForm);
        List<AttendanceManualMakeupVO> makeupVOS = makeupMinuteGroupByUserIdAndOrgId(queryForm, MakeupTypeEnum.WORKDATE.getCode());
        if (StringHelper.isNotEmpty(makeupVOS)) {
            minute += makeupVOS.get(0).getMinute();
        }
        dateList.forEach(date -> {
            boolean notCurDate;
            try {
                notCurDate = DateUtil.daysBetween(now, date)!=1;
            } catch (ParseException e) {
                throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
            }
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
            StringBuilder punchResult = new StringBuilder();
            StringBuilder employeeScheduleName = new StringBuilder();
            AttendanceManualMakeupVO manualMakeupVO = manualMakeupMap.get(date);
            if (manualMakeupVO != null) {
                workDateMinuteVO.setId(manualMakeupVO.getId());
                workDateMinuteVO.setMakeupMinutes(manualMakeupVO.getMinute());
                workDateMinuteVO.setMakeupDesc(manualMakeupVO.getMakeupDesc());
            } else {
                workDateMinuteVO.setId(0);
            }
            List<LeaveInfoVO> leaveInfoVOS = leaveInfoMap.get(date);
            if (leaveInfoVOS!=null && !leaveInfoVOS.isEmpty()) {
                hasApply = 1;
            }
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            if (employeeScheduleVOS==null || employeeScheduleVOS.isEmpty()) {
                employeeScheduleName.append("未排班");
            } else {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName, hSdf);
                    Integer esId = employeeScheduleVO.getId();
                    List<AttendancePunchRecordVO> list = punchRecords.get(esId);
                    if (list!=null && !list.isEmpty()) {
                        Date startTime = employeeScheduleVO.getFirstStartTime();
                        Date endTime = employeeScheduleVO.getSecondEndTime();
                        if (endTime == null) {
                            endTime = employeeScheduleVO.getFirstEndTime();
                        }
                        diff += endTime.getTime() - startTime.getTime();
                        AttendancePunchRecordVO first = list.get(0);
                        if (list.size() == 2) {
                            AttendancePunchRecordVO last = list.get(1);
                            if (AttendanceTypeEnum.OFFDUTY.getCode().equals(first.getPunchType())) {
                                last = first;
                                first = list.get(1);
                            }
                            Date sTime = first.getStartTime();
                            Date eTime = first.getEndTime();
                            Date onTime = first.getPunchTime();
                            Date offTime = last.getPunchTime();
                            if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                                long lateDiff = onTime.getTime() - sTime.getTime();
                                System.out.println(DateUtil.micro2Min(lateDiff));
                                diff -= lateDiff;
                            }
                            if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(last.getPunchStatus())) {//早退
                                long earlyDiff = eTime.getTime() - offTime.getTime();
                                System.out.println(DateUtil.micro2Min(earlyDiff));
                                diff -= earlyDiff;
                            } else if (AttendanceStatusEnum.OFFDUTY_PUNCH.getCode().equals(last.getPunchStatus())) {
                                long workOvertimeDiff = offTime.getTime()-eTime.getTime();
                                System.out.println(DateUtil.micro2Min(workOvertimeDiff));
                            }
                            if (leaveInfoVOS!=null && !leaveInfoVOS.isEmpty()) {
                                for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
                                    Date lSTime = leaveInfoVO.getStartTime();
                                    Date lETime = leaveInfoVO.getEndTime();
                                    if (leaveInfoVO.getVacationStatus().equals(0) && leaveInfoVO.getScheduleId().equals(esId)
                                            && sTime.before(lSTime) && eTime.after(lETime)) {
                                        diff -= lETime.getTime() - lSTime.getTime();
                                    }
                                }
                            }
                        } else {
                            Date sTime = first.getStartTime();
                            Date eTime = first.getEndTime();
                            Date punchTime = first.getPunchTime();
                            if (AttendanceStatusEnum.LATER_PUNCH.getCode().equals(first.getPunchStatus())) {//迟到
                                long lateDiff = punchTime.getTime() - sTime.getTime();
                                System.out.println(DateUtil.micro2Min(lateDiff));
                                diff -= lateDiff;
                            } else if (AttendanceStatusEnum.EARLY_PUNCH.getCode().equals(first.getPunchStatus())) {//早退
                                long earlyDiff = eTime.getTime() - punchTime.getTime();
                                System.out.println(DateUtil.micro2Min(earlyDiff));
                                diff -= earlyDiff;
                            }
                            if (leaveInfoVOS!=null && !leaveInfoVOS.isEmpty()) {
                                for (LeaveInfoVO leaveInfoVO : leaveInfoVOS) {
                                    Date lSTime = leaveInfoVO.getStartTime();
                                    Date lETime = leaveInfoVO.getEndTime();
                                    if (leaveInfoVO.getVacationStatus().equals(0) && leaveInfoVO.getScheduleId().equals(esId)
                                            && sTime.before(lSTime) && eTime.after(lETime)) {
                                        diff -= lETime.getTime() - lSTime.getTime();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            boolean hasException = false;
            List<AttendancePunchRecordVO> punchRecordVOS = punchRecordMap.get(date);
            if (punchRecordVOS!=null && !punchRecordVOS.isEmpty()) {
                AttendancePunchRecordVO onPunchItem = punchRecordVOS.get(0);
                AttendancePunchRecordVO offPunchItem = punchRecordVOS.get(1);
                if (onPunchItem.getPunchType().equals(AttendanceTypeEnum.OFFDUTY.getCode())) {
                    offPunchItem = onPunchItem;
                    onPunchItem = punchRecordVOS.get(1);
                }
                Byte onSource = onPunchItem.getSource();
                Byte isPunch = onPunchItem.getIsPunch();
                Byte punchType = onPunchItem.getPunchType();
                onPunchTime = onPunchItem.getPunchTime();
                String tag = "work";
                Byte offSource = offPunchItem.getSource();
                offPunchTime = offPunchItem.getPunchTime();
                if (onSource.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())
                        ||onSource.equals(AttendanceSourceEnum.REST_SCHEDULE.getCode())
                        ||onSource.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())) {
                    // 休息、按班次请假、按天请假
                    tag = "rest";
                } else if (onSource.equals(AttendanceSourceEnum.WORK_OVERTIME.getCode())) {
                    // 加班
                    tag = "exception";
                    hasApply = 2; //加班必定存在于打卡记录中
                } else if (onSource.equals(AttendanceSourceEnum.FIELD.getCode())) {
                    // 外勤
                    tag = "exception";
                    hasApply = 3;
                }
                if (tag == "rest") {
                    if (isPunch.equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {//未打卡
                        punchResult.append(AttendanceTypeEnum.getValue(punchType)).append("无效");
                        onPunchTime = onPunchItem.getPunchTime();
                        hasException = true;
                    }
                } else {
                    if (tag == "work") {
                        punchResult.append(AttendanceTypeEnum.getValue(punchType));
                        if (isPunch.equals(AttendanceIsPunchEnum.UNPUNCH.getCode())) {//未打卡
                            punchResult.append("缺卡");
                            hasException = true;
                        } else {
                            Byte punchStatus = onPunchItem.getPunchStatus();
                            if (punchStatus.equals(AttendanceStatusEnum.ONDUTY_PUNCH.getCode())) {
                                punchResult.append("正常");
                            } else {
                                punchResult.append(AttendanceStatusEnum.getValue(punchStatus));
                                hasException = true;
                            }
                        }
                    }

                }

                isPunch = offPunchItem.getIsPunch();
                punchType = offPunchItem.getPunchType();
                tag = "work";
                if (offSource.equals(AttendanceSourceEnum.LEAVE_BYDAY.getCode())
                        || offSource.equals(AttendanceSourceEnum.REST_SCHEDULE.getCode())
                        || offSource.equals(AttendanceSourceEnum.LEAVE_BYSCHEDULE.getCode())) {
                    // 休息、按班次请假、按天请假
                    tag = "rest";
                } else if (offSource.equals(AttendanceSourceEnum.WORK_OVERTIME.getCode())) {
                    // 加班
                    tag = "exception";
                    hasApply = 2; //加班必定存在于打卡记录中
                } else if (offSource.equals(AttendanceSourceEnum.FIELD.getCode())) {
                    // 外勤
                    tag = "exception";
                    hasApply = 3;
                }
                if (tag == "rest") {
                    if (isPunch.equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {//未打卡
                        if (punchResult.length() > 0) {
                            punchResult.append("、");
                        }
                        punchResult.append(AttendanceTypeEnum.getValue(punchType)).append("无效");
                        offPunchTime = offPunchItem.getPunchTime();
                        hasException = true;
                    }
                } else {
                    if (tag == "work") {
                        if (punchResult.length() > 0) {
                            punchResult.append("、");
                        }
                        punchResult.append(AttendanceTypeEnum.getValue(punchType));
                        if (isPunch.equals(AttendanceIsPunchEnum.UNPUNCH.getCode())) {//未打卡
                            punchResult.append("缺卡");
                            hasException = true;
                        } else {
                            Byte punchStatus = offPunchItem.getPunchStatus();
                            if (punchStatus.equals(AttendanceStatusEnum.OFFDUTY_PUNCH.getCode())) {
                                punchResult.append("正常");
                            } else {
                                punchResult.append(AttendanceStatusEnum.getValue(punchStatus));
                                hasException = true;
                            }
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
            workDateMinuteVO.setPunchResult(notCurDate?punchResult.toString():"");
            workDateMinuteVO.setDate(date);
            result.add(workDateMinuteVO);
        });
        AttendancePunchPageInfoVO<AttendanceWorkDateMinuteVO> pageInfo = new AttendancePunchPageInfoVO<>();
        pageInfo.setTotal(total);
        pageInfo.setList(result);
        pageInfo.setPageNum(queryForm.getPageNum());
        pageInfo.setPageSize(queryForm.getPageSize());
        pageInfo.setMinute(minute);
        return pageInfo;
    }

    /**
     * 根据日期分组请假列表
     * @param userId
     * @param orgId
     * @param betweenDate
     * @param andDate
     * @return
     */
    private Map<Date, List<LeaveInfoVO>> getLeaveInfoMapGroupByDate(Integer userId, Integer orgId, Date betweenDate, Date andDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        LeaveInfoQueryForm leaveQueryForm = new LeaveInfoQueryForm();
        leaveQueryForm.setUserId(userId);
        leaveQueryForm.setBetweenStartDate(betweenDate);
        leaveQueryForm.setAndStartDate(andDate);
        leaveQueryForm.setOrgId(orgId);
        leaveQueryForm.setWhetherPage(false);
        List<LeaveInfoVO> leaveInfoVOS = leaveInfoBiz.findLeaveInfoList(leaveQueryForm);
        Map<Date, List<LeaveInfoVO>> result = new HashMap<>();
        leaveInfoVOS.forEach(leaveInfoVO -> {
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
            if (leaveInfoVO.getVacationStatus().equals(0)) {// 按班次请假
                List<LeaveInfoVO> list = result.get(startDate);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(leaveInfoVO);
                result.put(startDate, list);
            } else {// 按天请假
                List<Date> dates = DateUtil.getBetweenDate(startDate,endDate);
                dates.forEach(date -> {
                    LeaveInfoVO leaveByDay = new LeaveInfoVO();
                    BeanUtils.copyProperties(leaveInfoVO, leaveByDay);
                    leaveByDay.setStartDate(date);
                    leaveByDay.setEndDate(date);
                    List<LeaveInfoVO> list = result.get(date);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(leaveByDay);
                    result.put(date, list);
                });
            }
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
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        result.forEach(workDateOvertimeMinute->{
            Date date = workDateOvertimeMinute.getDate();
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            StringBuilder employeeScheduleName = new StringBuilder();
            if (employeeScheduleVOS!=null && !employeeScheduleMap.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName, sdf);
                }
            }
            workDateOvertimeMinute.setEmployeeScheduleName(employeeScheduleName.toString());
        });
        return result;
    }

    /**
     * 分页查询休息日加班时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public AttendancePunchPageInfoVO<AttendanceOvertimeMinuteVO> statisticsWorkOvertimesByMinute(AttendanceStatisticsQueryForm queryForm) {
        SimpleDateFormat hSdf = new SimpleDateFormat("HH:mm");
        setQueryFormDate(queryForm);
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        long minute = sumRestWorkOvertimeInfo(queryForm);
        List<AttendanceManualMakeupVO> makeupVOS = makeupMinuteGroupByUserIdAndOrgId(queryForm, MakeupTypeEnum.OVERTIME.getCode());
        if (StringHelper.isNotEmpty(makeupVOS)) {
            minute += makeupVOS.get(0).getMinute();
        }
        //加班申请
        WorkOvertimeInfoQueryForm workQueryForm = new WorkOvertimeInfoQueryForm();
        workQueryForm.setWhetherPage(queryForm.getWhetherPage());
        workQueryForm.setPageNum(queryForm.getPageNum());
        workQueryForm.setPageSize(queryForm.getPageSize());
        workQueryForm.setUserId(userId);
        workQueryForm.setCompanyId(orgId);
        workQueryForm.setStartTime(betweenDate);
        workQueryForm.setEndTime(andDate);
        workQueryForm.setApprovalStatus(1);
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
        Map<Date, AttendanceManualMakeupVO> manualMakeupVOMap = getManualMakeupMapGroupByDate(userId, orgId, MakeupTypeEnum.OVERTIME.getCode(),betweenDate, andDate);
        // 排班
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        // 打卡
        Map<Date, List<AttendancePunchRecordVO>> punchRecordMap = getPunchRecordMapGroupByDate(userId, orgId, betweenDate, andDate);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        workOvertimeVOS.forEach(workOvertimeMinuteVO -> {
            long diff = 0;
            Date onPunchTime = null;
            Date offPunchTime = null;
            StringBuilder sb = new StringBuilder();
            Date date = workOvertimeMinuteVO.getWorkDate();
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            if (employeeScheduleVOS!=null && !employeeScheduleVOS.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, sb, sdf);
                }
            }
            List<AttendancePunchRecordVO> punchRecords = punchRecordMap.get(date);
            if (punchRecords!=null && !punchRecords.isEmpty()) {
                for (AttendancePunchRecordVO punchRecord : punchRecords) {
                    if (punchRecord.getIsInScope().equals(AttendanceIsInScopeEnum.BELONG.getCode())
                    && punchRecord.getSource().equals(AttendanceSourceEnum.WORK_OVERTIME.getCode())) {
                        if (punchRecord.getIsPunch().equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {
                            Date sTime = punchRecord.getStartTime();
                            Date eTime = punchRecord.getEndTime();
                            if (punchRecord.getPunchType().equals(AttendanceTypeEnum.ONDUTY.getCode())) {//上班
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
            }
            AttendanceManualMakeupVO manualMakeupVO = manualMakeupVOMap.get(date);
            if (manualMakeupVO != null) {
                workOvertimeMinuteVO.setId(manualMakeupVO.getId());
                workOvertimeMinuteVO.setMakeupMinute(manualMakeupVO.getMinute());
                workOvertimeMinuteVO.setMakeupDesc(manualMakeupVO.getMakeupDesc());
            } else {
                workOvertimeMinuteVO.setId(0);
            }
            Date firstStartTime = workOvertimeMinuteVO.getOnPunchTime();
            Date firstEndTime = workOvertimeMinuteVO.getOffPunchTime();
            StringBuilder scheduleName = new StringBuilder();
            String schedule = workOvertimeMinuteVO.getScheduleName();
            if (schedule != null) {
                scheduleName.append(schedule);
                scheduleName.append("（").append(hSdf.format(firstStartTime)).append("-").append(hSdf.format(firstEndTime)).append("）");
            }
            String approvalName = userNameMap.get(workOvertimeMinuteVO.getApprovalPeopleId());
            workOvertimeMinuteVO.setApprovalUserName(approvalName);
            workOvertimeMinuteVO.setEmployeeScheduleName(sb.toString());
            workOvertimeMinuteVO.setMinutes(DateUtil.micro2Min(diff));
            workOvertimeMinuteVO.setOnPunchTime(onPunchTime);
            workOvertimeMinuteVO.setOffPunchTime(offPunchTime);
            workOvertimeMinuteVO.setScheduleName(scheduleName.toString());
        });
        return new AttendancePunchPageInfoVO<>(workOvertimeVOS,minute);
    }

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
        leaveQueryForm.setApprovalStatus(1);
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
            Date startDate = leaveInfoVO.getStartDate();
            Date endDate = leaveInfoVO.getEndDate();
            if (endDate.after(queryForm.getAndDate())) {
                endDate = queryForm.getAndDate();
            }
            long diff = 0;
            StringBuilder sb = new StringBuilder(leaveInfoVO.getVacationName());
            StringBuilder approvalNames = new StringBuilder();
            if (leaveInfoVO.getVacationStatus().equals(0)) {
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

        // 外勤
        FieldInfoQueryForm fieldQueryForm = new FieldInfoQueryForm();
        fieldQueryForm.setWhetherPage(queryForm.getWhetherPage());
        fieldQueryForm.setPageNum(queryForm.getPageNum());
        fieldQueryForm.setPageSize(queryForm.getPageSize());
        fieldQueryForm.setUserId(userId);
        fieldQueryForm.setCompanyId(orgId);
        fieldQueryForm.setBetweenDate(betweenDate);
        fieldQueryForm.setAndDate(andDate);
        fieldQueryForm.setApprovalStatus(1);
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
        SimpleDateFormat hSdf = new SimpleDateFormat("HH:mm");
        fieldInfoVOS.forEach(fieldInfoVO -> {
            AttendanceFieldMinuteVO fieldMinuteVO = new AttendanceFieldMinuteVO();
            Date startTime = fieldInfoVO.getStartTime();
            Date endTime = fieldInfoVO.getEndTime();
            Date date;
            try {
                date = sdf.parse(sdf.format(startTime));
            } catch (ParseException e) {
                throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
            }
            StringBuilder sb = new StringBuilder();
            List<EmployeeScheduleVO> employeeSchedules = employeeScheduleMap.get(date);
            if (employeeSchedules!=null && !employeeSchedules.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeSchedules) {
                    putEmployeeScheduleName(employeeScheduleVO, sb, hSdf);
                }
            }
            List<AttendancePunchRecordVO> punchRecords = attendancePunchRecordMap.get(date);
            Date onPunchTime = null;
            Date offPunchTime = null;
            StringBuilder address = new StringBuilder();
            long diff = endTime.getTime() - startTime.getTime();
            if (punchRecords!=null && !punchRecords.isEmpty()) {
//                List<AttendancePunchDateVO> punchDateVOS = punchRecord2PunchDate(punchRecords);
//                for (AttendancePunchDateVO punchDateVO : punchDateVOS) {
//                    Date onStartTime = punchDateVO.getOnDutyStartTime();
//                    Date onEndTime = punchDateVO.getOnDutyEndTime();
//                    onPunchTime = punchDateVO.getOnDutyPunchTime();
//                    switch (punchDateVO.getOnDutyStatus()) {
//                        case 0:{
//                            diff += onEndTime.getTime() - onStartTime.getTime();
//                            break;
//                        }
//                        case 1:{
//                            diff += onEndTime.getTime() - onPunchTime.getTime();
//                            break;
//                        }
//                    }
//                    String punchAddress = punchDateVO.getOnPunchAddress();
//                    if (StringHelper.isNotEmpty(punchAddress)) {
//                        address.append(punchAddress);
//                    }
//                    punchAddress = punchDateVO.getOffPunchAddress();
//                    if (StringHelper.isNotEmpty(punchAddress)) {
//                        if (address.length() > 0) {
//                            address.append("、");
//                        }
//                        address.append(punchAddress);
//                    }
//                    Date offStartTime = punchDateVO.getOffDutyStartTime();
//                    Date offEndTime = punchDateVO.getOffDutyEndTime();
//                    offPunchTime = punchDateVO.getOffDutyPunchTime();
//                    switch (punchDateVO.getOffDutyStatus()) {
//                        case 2:{
//                            diff += offEndTime.getTime() - offStartTime.getTime();
//                            break;
//                        }
//                        case 3:{
//                            diff += offPunchTime.getTime() - offStartTime.getTime();
//                            break;
//                        }
//                    }
//                }
                for (AttendancePunchRecordVO punchRecord : punchRecords) {
                    if (punchRecord.getIsPunch().equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {
                        Date sTime;
                        Date eTime;
                        try {
                            sTime = hSdf.parse(hSdf.format(punchRecord.getStartTime()));
                            eTime = hSdf.parse(hSdf.format(punchRecord.getEndTime()));
                        } catch (ParseException e) {
                            throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                        }
                        Byte punchType = punchRecord.getPunchType();
                        if (punchType.equals(AttendanceTypeEnum.ONDUTY.getCode())) {
                            // 迟到时长
                            onPunchTime = punchRecord.getPunchTime();
                            if (onPunchTime.after(sTime)) {
                                diff += sTime.getTime() - onPunchTime.getTime();
                            }
                        } else {
                            // 早退时长
                            offPunchTime = punchRecord.getPunchTime();
                            if (offPunchTime.before(eTime)) {
                                diff += offPunchTime.getTime() - eTime.getTime();
                            }
                        }
                        String punchAddress = punchRecord.getPunchAddress();
                        if (StringHelper.isNotEmpty(punchAddress)) {
                            if (address.length() > 0) {
                                address.append("、");
                            }
                            address.append(punchAddress);
                        }
                    }
                }
            }
            String approvalName = userNameMap.get(fieldInfoVO.getApprovalPeopleId());
            fieldMinuteVO.setApprovalUserName(approvalName);
            Date sTime;
            Date eTime;
            try {
                sTime = hSdf.parse(hSdf.format(startTime));
                eTime = hSdf.parse(hSdf.format(endTime));
            } catch (ParseException e) {
                throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
            }
            if (!inScope(sTime,eTime,onPunchTime,offPunchTime)) {
                diff = 0;
            }
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
            result.add(fieldMinuteVO);
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
        return getPunchRecordMapGroupByDate(userId, orgId, betweenDate, andDate, null, null);
    }

    private Map<Date, List<AttendancePunchRecordVO>> getPunchRecordMapGroupByDate(Integer userId, Integer orgId, Date betweenDate, Date andDate,
                              List<Integer> notInIds, Map<Integer, List<AttendancePunchRecordVO>> punchRecords) {
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setBetweenDate(betweenDate);
        recordQueryForm.setAndDate(andDate);
        recordQueryForm.setUserId(userId);
        recordQueryForm.setOrgId(orgId);
        recordQueryForm.setNotInIds(notInIds);
        recordQueryForm.setWhetherPage(false);
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
            Integer esId = attendancePunchRecordVO.getEsId();
            if (punchRecords!=null && AttendanceSourceEnum.WORK_SCHEDULE.getCode().equals(attendancePunchRecordVO.getSource())
                && AttendanceIsInScopeEnum.BELONG.getCode().equals(attendancePunchRecordVO.getIsInScope())) {
                List<AttendancePunchRecordVO> recordVOS = punchRecords.get(esId);
                if (recordVOS == null) {
                    recordVOS = new ArrayList<>();
                }
                recordVOS.add(attendancePunchRecordVO);
                punchRecords.put(esId, recordVOS);
            }


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
     * 拼接排班名称
     *
     * @param employeeScheduleVO
     * @param employeeScheduleName
     */
    private void putEmployeeScheduleName(EmployeeScheduleVO employeeScheduleVO, StringBuilder employeeScheduleName, SimpleDateFormat sdf) {
        if (employeeScheduleName.length() > 0) {
            employeeScheduleName.append(" ");
        }
        Date firstStartTime = employeeScheduleVO.getFirstStartTime();
        Date firstEndTime = employeeScheduleVO.getFirstEndTime();
        Date secondStartTime = employeeScheduleVO.getSecondStartTime();
        Date secondEndTime = employeeScheduleVO.getSecondEndTime();

        employeeScheduleName.append(employeeScheduleVO.getName())
                .append("（").append(sdf.format(firstStartTime))
                .append("-").append(sdf.format(firstEndTime));
        if (secondStartTime!=null && secondEndTime!=null) {
            employeeScheduleName.append("、")
                    .append(sdf.format(secondStartTime))
                    .append("-")
                    .append(sdf.format(secondEndTime));
        }
        employeeScheduleName.append("）【")
                .append(employeeScheduleVO.getType())
                .append("】");
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
        queryForm.setWhetherPage(false);
        List<AttendanceManualMakeupVO> attendanceManualMakeupVOS = attendanceManualMakeupBiz.findAttendanceManualMakeupList(queryForm);
        Map<Date, AttendanceManualMakeupVO> result = new HashMap<>(attendanceManualMakeupVOS.size());
        attendanceManualMakeupVOS.forEach(makeupVO -> result.put(makeupVO.getMakeupDate(), makeupVO));
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
        model.setWhetherPage(false);
        model.setUserIds(userIds);
        model.setWorkStatus(new Byte[]{0, 1, 3});
        List<SysUserInfoDetail> users = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        Map<Integer, String> result = new HashMap<>(users.size());
        users.forEach(sysUserInfoDetail -> result.put(sysUserInfoDetail.getUserId(), sysUserInfoDetail.getName()));
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
        if (source.equals(AttendanceSourceEnum.WORK_SCHEDULE.getCode())) {
            result = "【上班】";
        } else if (source.equals(AttendanceSourceEnum.REST_SCHEDULE.getCode())) {
            result = "【休息】";
        } else if (source.equals(AttendanceSourceEnum.WORK_OVERTIME.getCode())) {
            result = "【加班】";
        } else if (source.equals(AttendanceSourceEnum.FIELD.getCode())) {
            result = "【外勤】";
        }
        return result;
    }

    /**
     * 分页查询迟到统计的明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceLaterCountVO> statisticsPunchRecordByLaterCount(AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setWhetherPage(queryForm.getWhetherPage());
        recordQueryForm.setPageNum(queryForm.getPageNum());
        recordQueryForm.setPageSize(queryForm.getPageSize());
        recordQueryForm.setUserId(userId);
        recordQueryForm.setOrgId(orgId);
        recordQueryForm.setBetweenDate(betweenDate);
        recordQueryForm.setAndDate(andDate);
        recordQueryForm.setSource(AttendanceSourceEnum.WORK_SCHEDULE.getCode());
        recordQueryForm.setPunchStatus(AttendanceStatusEnum.LATER_PUNCH.getCode());
        List<AttendancePunchRecordVO> punchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
        List<AttendanceLaterCountVO> result = new ArrayList<>(punchRecordVOS.size());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        punchRecordVOS.forEach(punchRecord->{
            AttendanceLaterCountVO laterCountVO = new AttendanceLaterCountVO();
            Date date = punchRecord.getPunchDate();
            StringBuilder employeeScheduleName = new StringBuilder();
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            if (employeeScheduleVOS!=null && !employeeScheduleVOS.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName, sdf);
                }
            }
            Date onPunchTime = punchRecord.getPunchTime();
            Date startTime = punchRecord.getStartTime();
            long diff = onPunchTime.getTime()-startTime.getTime();
            laterCountVO.setDate(date);
            laterCountVO.setStartTime(startTime);
            laterCountVO.setOnPunchTime(onPunchTime);
            laterCountVO.setPunchAddress(punchRecord.getPunchAddress());
            laterCountVO.setEmployeeScheduleName(employeeScheduleName.toString());
            laterCountVO.setMinutes(DateUtil.micro2Min(diff));
            result.add(laterCountVO);
        });
        return result;
    }

    /**
     * 分页查询早退统计的明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceEarlyCountVO> statisticsPunchRecordByEarlyCount(AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setWhetherPage(queryForm.getWhetherPage());
        recordQueryForm.setPageNum(queryForm.getPageNum());
        recordQueryForm.setPageSize(queryForm.getPageSize());
        recordQueryForm.setUserId(userId);
        recordQueryForm.setOrgId(orgId);
        recordQueryForm.setBetweenDate(betweenDate);
        recordQueryForm.setAndDate(andDate);
        recordQueryForm.setSource(AttendanceSourceEnum.WORK_SCHEDULE.getCode());
        recordQueryForm.setPunchStatus(AttendanceStatusEnum.EARLY_PUNCH.getCode());
        List<AttendancePunchRecordVO> punchRecordVOS = findAttendancePunchRecordList(recordQueryForm);
        List<AttendanceEarlyCountVO> result = new ArrayList<>(punchRecordVOS.size());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        punchRecordVOS.forEach(punchRecord->{
            AttendanceEarlyCountVO earlyCountVO = new AttendanceEarlyCountVO();
            Date date = punchRecord.getPunchDate();
            StringBuilder employeeScheduleName = new StringBuilder();
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            if (employeeScheduleVOS!=null && !employeeScheduleVOS.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName, sdf);
                }
            }
            Date offPunchTime = punchRecord.getPunchTime();
            Date endTime = punchRecord.getEndTime();
            long diff = endTime.getTime()-offPunchTime.getTime();
            earlyCountVO.setDate(date);
            earlyCountVO.setEndTime(endTime);
            earlyCountVO.setOffPunchTime(offPunchTime);
            earlyCountVO.setPunchAddress(punchRecord.getPunchAddress());
            earlyCountVO.setEmployeeScheduleName(employeeScheduleName.toString());
            earlyCountVO.setMinutes(DateUtil.micro2Min(diff));
            result.add(earlyCountVO);
        });
        return result;
    }

    /**
     * 分页查询缺卡统计的明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceUnpunchCountVO> statisticsPunchRecordByUnpunchCount(AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setWhetherPage(queryForm.getWhetherPage());
        recordQueryForm.setPageNum(queryForm.getPageNum());
        recordQueryForm.setPageSize(recordQueryForm.getPageSize());
        recordQueryForm.setUserId(userId);
        recordQueryForm.setOrgId(orgId);
        recordQueryForm.setBetweenDate(betweenDate);
        recordQueryForm.setAndDate(andDate);
        recordQueryForm.setIsPunch(AttendanceIsPunchEnum.UNPUNCH.getCode());
        recordQueryForm.setSource((byte) 0);
        List<AttendancePunchRecordVO> masterRecordVOS = findAttendancePunchRecordListGroupByDate(recordQueryForm);
        List<Integer> notInIds = new ArrayList<>(masterRecordVOS.size());
        masterRecordVOS.forEach(masterRecordVO->notInIds.add(masterRecordVO.getId()));
        Map<Date, List<AttendancePunchRecordVO>> slaveRecordMap = getPunchRecordMapGroupByDate(userId, orgId, betweenDate, andDate, notInIds, null);
        List<AttendanceUnpunchCountVO> result = new ArrayList<>(masterRecordVOS.size());
        Date curDate = DateUtil.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        masterRecordVOS.forEach(punchRecord->{
            Date date = punchRecord.getPunchDate();
            if (!date.equals(curDate)) {
                AttendanceUnpunchCountVO unpunchCountVO = new AttendanceUnpunchCountVO();
                StringBuilder employeeScheduleName = new StringBuilder();
                List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
                if (employeeScheduleVOS != null && !employeeScheduleVOS.isEmpty()) {
                    for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                        putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName, sdf);
                    }
                }
                int count = 1;
                Date onPunchTime = punchRecord.getPunchTime();
                Date offPunchTime = null;
                if (punchRecord.getPunchType().equals(AttendanceTypeEnum.OFFDUTY.getCode())) {
                    onPunchTime = null;
                    offPunchTime = punchRecord.getPunchTime();
                }
                List<AttendancePunchRecordVO> list = slaveRecordMap.get(date);
                if (list != null && !list.isEmpty()) {
                    for (AttendancePunchRecordVO slaveRecord : list) {
                        if (AttendanceSourceEnum.WORK_SCHEDULE.getCode().equals(slaveRecord.getSource())) {
                            if (slaveRecord.getPunchType().equals(AttendanceTypeEnum.ONDUTY.getCode())) {
                                onPunchTime = slaveRecord.getPunchTime();
                            } else {
                                offPunchTime = slaveRecord.getPunchTime();
                            }
                            if (AttendanceIsPunchEnum.UNPUNCH.getCode().equals(slaveRecord.getIsPunch())) {
                                count++;
                            }
                        }
                    }
                }
                unpunchCountVO.setDate(date);
                unpunchCountVO.setOnPunchTime(onPunchTime);
                unpunchCountVO.setOffPunchTime(offPunchTime);
                unpunchCountVO.setPunchResult(count == 1 ? "下班卡缺卡" : "上班卡缺卡、下班卡缺卡");
                unpunchCountVO.setEmployeeScheduleName(employeeScheduleName.toString());
                unpunchCountVO.setCount(count);
                result.add(unpunchCountVO);
            }
        });
        return result;
    }

    /**
     * 分页查询无效卡统计的明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceInvalidCountVO> statisticsPunchRecordByInvalidCount(AttendanceStatisticsQueryForm queryForm) {
        Integer userId = queryForm.getUserId();
        Integer orgId = queryForm.getOrgId();
        setQueryFormDate(queryForm);
        Date betweenDate = queryForm.getBetweenDate();
        Date andDate = queryForm.getAndDate();
        Map<Date, List<EmployeeScheduleVO>> employeeScheduleMap = getEmployeeScheduleMapGroupByDate(userId, orgId, betweenDate, andDate);
        AttendancePunchRecordQueryForm recordQueryForm = new AttendancePunchRecordQueryForm();
        recordQueryForm.setWhetherPage(queryForm.getWhetherPage());
        recordQueryForm.setPageNum(queryForm.getPageNum());
        recordQueryForm.setPageSize(recordQueryForm.getPageSize());
        recordQueryForm.setUserId(userId);
        recordQueryForm.setOrgId(orgId);
        recordQueryForm.setBetweenDate(betweenDate);
        recordQueryForm.setAndDate(andDate);
        recordQueryForm.setIsPunch(AttendanceIsPunchEnum.PUNCHED.getCode());
        recordQueryForm.setPunchStatus(AttendanceStatusEnum.INVALID_PUNCH.getCode());
        List<AttendancePunchRecordVO> masterRecordVOS = findAttendancePunchRecordListGroupByDate(recordQueryForm);
        List<Integer> notInIds = new ArrayList<>(masterRecordVOS.size());
        masterRecordVOS.forEach(masterRecordVO->notInIds.add(masterRecordVO.getId()));
        Map<Date, List<AttendancePunchRecordVO>> slaveRecordMap = getPunchRecordMapGroupByDate(userId, orgId, betweenDate, andDate, notInIds, null);
        List<AttendanceInvalidCountVO> result = new ArrayList<>(masterRecordVOS.size());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        masterRecordVOS.forEach(punchRecord->{
            AttendanceInvalidCountVO invalidCountVO = new AttendanceInvalidCountVO();
            Date date = punchRecord.getPunchDate();
            StringBuilder employeeScheduleName = new StringBuilder();
            List<EmployeeScheduleVO> employeeScheduleVOS = employeeScheduleMap.get(date);
            if (employeeScheduleVOS!=null && !employeeScheduleMap.isEmpty()) {
                for (EmployeeScheduleVO employeeScheduleVO : employeeScheduleVOS) {
                    putEmployeeScheduleName(employeeScheduleVO, employeeScheduleName, sdf);
                }
            }
            StringBuilder punchAddress = new StringBuilder(punchRecord.getPunchAddress());
            int count = 1;
            Date onPunchTime = punchRecord.getPunchTime();
            Date offPunchTime = null;
            if (punchRecord.getPunchType().equals(AttendanceTypeEnum.OFFDUTY.getCode())) {
                onPunchTime = null;
                offPunchTime = punchRecord.getPunchTime();
            }
            List<AttendancePunchRecordVO> list = slaveRecordMap.get(date);
            if (list!=null && !list.isEmpty()) {
                for (AttendancePunchRecordVO slaveRecord : list) {
                    if (slaveRecord.getIsPunch().equals(AttendanceIsPunchEnum.PUNCHED.getCode())) {
                        if (slaveRecord.getPunchType().equals(AttendanceTypeEnum.ONDUTY.getCode())) {
                            onPunchTime = slaveRecord.getPunchTime();
                        } else {
                            offPunchTime = slaveRecord.getPunchTime();
                        }
                        if (punchAddress.length() > 0) {
                            punchAddress.append("、");
                        }
                        punchAddress.append(slaveRecord.getPunchAddress());
                        count++;
                    }
                }
            }
            invalidCountVO.setDate(date);
            invalidCountVO.setOnPunchTime(onPunchTime);
            invalidCountVO.setOffPunchTime(offPunchTime);
            invalidCountVO.setPunchResult(count==1?"上班卡无效":"上班卡无效、下班卡无效");
            invalidCountVO.setEmployeeScheduleName(employeeScheduleName.toString());
            invalidCountVO.setCount(count);
            invalidCountVO.setPunchAddress(punchAddress.toString());
            result.add(invalidCountVO);
        });
        return result;
    }

    /**
     * 根据打卡日期分组，分页查询打卡记录列表
     * @param queryForm 查询参数
     * @return
     */
    private List<AttendancePunchRecordVO> findAttendancePunchRecordListGroupByDate(AttendancePunchRecordQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(),queryForm.getPageSize());
        }
        return mapper.findAttendancePunchRecordListGroupByDate(queryForm);
    }
}
