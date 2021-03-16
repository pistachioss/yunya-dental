package com.yunya.modules.employeeattend;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordForm;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.form.AttendanceStatisticsQueryForm;
import com.yunya.feign.employee_attend.vo.AttendanceUnpunchCountVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.modules.employeeattend.controller.AttendancePunchRecordController;
import com.yunya.modules.employeeattend.controller.BaseScheduleController;
import com.yunya.modules.employeeattend.form.ScheduleForm;
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
@RunWith(SpringRunner.class)
public class AttendancePunchRecordControllerTest {
    @Autowired
    private AttendancePunchRecordController attendancePunchRecordController;
    @Autowired
    private BaseScheduleController baseScheduleController;
    @Autowired
    private RedisUtils redisUtils;

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
    public void testUnpunch() {
        String param = "{\"userName\":\"黄江英\",\"orgId\":26,\"num\":2,\"date\":\"2021-03\",\"userId\":632,\"orgName\":\"古墩路口腔门诊部\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
//        String param = "{\"userName\":\"邱诗惠\",\"orgId\":26,\"num\":1,\"date\":\"2021-03\",\"userId\":647,\"orgName\":\"古墩路口腔门诊部\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
        AttendanceStatisticsQueryForm query = JSONObject.parseObject(param,AttendanceStatisticsQueryForm.class);
        ResponseResult<PageInfo<AttendanceUnpunchCountVO>> result =attendancePunchRecordController.statisticsPunchRecordByUnpunchCount(query);
        System.out.println(JSONObject.toJSON(result));
    }
}
