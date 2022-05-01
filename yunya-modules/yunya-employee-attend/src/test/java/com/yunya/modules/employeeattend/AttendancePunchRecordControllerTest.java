package com.yunya.modules.employeeattend;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.*;
import com.yunya.feign.employee_attend.vo.AttendanceInvalidCountVO;
import com.yunya.feign.employee_attend.vo.AttendanceUnpunchCountVO;
import com.yunya.feign.employee_attend.vo.EmpPushMsgUnReadCountVO;
import com.yunya.feign.employee_attend.vo.EmployeePushMessageRecordVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.modules.employeeattend.config.JPushConfig;
import com.yunya.modules.employeeattend.controller.AttendancePunchRecordController;
import com.yunya.modules.employeeattend.controller.BaseScheduleController;
import com.yunya.modules.employeeattend.controller.EmployeePushMessageRecordController;
import com.yunya.modules.employeeattend.form.ScheduleForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 14:26
 * @since: 1.0.0
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class AttendancePunchRecordControllerTest {
    @Autowired
    private AttendancePunchRecordController attendancePunchRecordController;
    @Autowired
    private BaseScheduleController baseScheduleController;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private EmployeePushMessageRecordController employeePushMessageRecordController;

    @Test
    public void clear() {
        redisUtils.setLock(RedisConstants.LOCK_ATTENDANCE_PUNCH,DateUtil.getCurrentDate().toString(),RedisConstants.ATTENDANCE_PUNCH_LOCK_SEC, TimeUnit.SECONDS);
        redisUtils.unlock(RedisConstants.LOCK_ATTENDANCE_PUNCH, DateUtil.getCurrentDate().toString());
    }

    @Test
    public void testPunch() {
        Date now = new Date(System.currentTimeMillis());
        BaseContextHandler.setUserID("554");
        AttendancePunchRecordForm form = new AttendancePunchRecordForm();
        form.setId(7);
        form.setWifiMacAddress("9C:3D:CF:B0:7F:5D");
        form.setPunchAddress("杭州wifi");
        form.setPunchTime(now);
        form.setPunchStatus((byte) 1);
        ResponseResult result = attendancePunchRecordController.punch(form);
        System.out.println(result);
    }

    @Test
    public void testPunchInfo() {
        BaseContextHandler.setUserID("558");
        AttendancePunchRecordQueryForm queryForm = new AttendancePunchRecordQueryForm();
        queryForm.setWifiMacAddress("9C:3D:CF:B0:7F:5D");
        queryForm.setLongitude("");
        queryForm.setLatitude("");
        ResponseResult result = attendancePunchRecordController.punchInfo(queryForm);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testPunchRecordByDate() throws ParseException {
        BaseContextHandler.setUserID("569");
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-12");
//        ResponseResult result = attendancePunchRecordController.punchRecordByDate(date);
//        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testPunchRecordCalendarByMonth() {
        BaseContextHandler.setUserID("569");
        ResponseResult result = attendancePunchRecordController.punchRecordCalendarByMonth("2020-11-12");
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testPunchRecordByMonth() {
        BaseContextHandler.setUserID("569");
        ResponseResult result = attendancePunchRecordController.punchRecordByMonth("2020-11-12");
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testStatisticsPunchRecord() {
        BaseContextHandler.setUserID("569");
        AttendanceStatisticsQueryForm queryForm = new AttendanceStatisticsQueryForm();
        queryForm.setDate("2020-11-16");
        ResponseResult result = attendancePunchRecordController.statisticsPunchRecord(queryForm);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testSearch() {
        ScheduleForm queryForm = new ScheduleForm();
        ResponseResult result = baseScheduleController.search(queryForm);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testUnpunchCount() {
        String param = "{\"userName\":\"张芳萍\",\"orgId\":34,\"num\":5,\"date\":\"2021-03\",\"userId\":600,\"orgName\":\"杭州艾维雅文口腔门诊部有限公司\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
//        String param = "{\"userName\":\"邱诗惠\",\"orgId\":26,\"num\":1,\"date\":\"2021-03\",\"userId\":647,\"orgName\":\"古墩路口腔门诊部\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
        AttendanceStatisticsQueryForm query = JSONObject.parseObject(param,AttendanceStatisticsQueryForm.class);
        ResponseResult<PageInfo<AttendanceUnpunchCountVO>> result =attendancePunchRecordController.statisticsPunchRecordByUnpunchCount(query);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void testInvalidCount() {
        String param = "{\"userName\":\"周佳芸\",\"orgId\":28,\"num\":2,\"date\":\"2021-03\",\"userId\":311,\"orgName\":\"杭州艾维乾元口腔门诊部有限公司\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
//        String param = "{\"userName\":\"邱诗惠\",\"orgId\":26,\"num\":1,\"date\":\"2021-03\",\"userId\":647,\"orgName\":\"古墩路口腔门诊部\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
        AttendanceStatisticsQueryForm query = JSONObject.parseObject(param,AttendanceStatisticsQueryForm.class);
        ResponseResult<PageInfo<AttendanceInvalidCountVO>> result =attendancePunchRecordController.statisticsPunchRecordByInvalidCount(query);
        System.out.println(JSONObject.toJSON(result));
    }

//    @Test
//    public void testTask() {
//        AttendancePunchRecordScheduledTask task = new AttendancePunchRecordScheduledTask();
//        task.produceAttendancePunchTemplateData();
//    }

    @Test
    public void testConfig() {
      JPushConfig jPushConfig = new JPushConfig();
      log.info("jPushConfig: " + jPushConfig.getAppKey() + jPushConfig.getAppMasterSecret());
    }

    @Test
    public void testPushMessageFind() {
        EmployeePushMessageRecordQueryForm query = new EmployeePushMessageRecordQueryForm();
        query.setUserId(735);
        query.setPreDateTime("2022-04-13");
        PageInfo<EmployeePushMessageRecordVO> data = employeePushMessageRecordController.findList(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }

    @Test
    public void testFindCountUnRead() {
        EmployeePushMessageRecordQueryForm query = new EmployeePushMessageRecordQueryForm();
        query.setUserId(742);
        EmpPushMsgUnReadCountVO data = employeePushMessageRecordController.findCountUnRead(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }

    @Test
    public void testUptPushMessageHaveRead() {
        BaseContextHandler.setUserID("635");
        EmployeePushMessageRecordForm form = new EmployeePushMessageRecordForm();
        form.setDataId(37);
        form.setMessageType(30);
        employeePushMessageRecordController.uptPushMessageHaveRead(form);
    }
}
