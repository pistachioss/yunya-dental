package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordForm;
import com.yunya.feign.employee_attend.form.AttendanceStatisticsQueryForm;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.AttendancePunchRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 简介：考勤打卡管理
 *
 * @author: chenlin
 * @Description: 考勤打卡管理
 * @Date: 2020/11/9 14:13
 * @since: 1.0.0
 */
@Api(tags = "考勤打卡管理")
@RestController
@RequestMapping("attendancePunchRecord")
public class AttendancePunchRecordController {
    /** 注入对象 */
    @Autowired
    private AttendancePunchRecordBiz attendancePunchRecordBiz;

    /**
     * 查询打卡项目列表（班次、加班、请假、外勤等）以及即将打卡的项目
     *
     * @param queryForm 查询参数
     * @return ResponseResult<AttendanceInfoVO>
     */
    @ApiOperation(value = "查询打卡项目列表（班次、加班、请假、外勤等）以及即将打卡的项目")
    @ApiImplicitParam(value = "查询参数：经纬度或wifi的mac地址")
    @PostMapping("/punchInfo")
    @CurrentUser
    public ResponseResult<AttendancePunchInfoVO> punchInfo(@RequestBody AttendancePunchRecordQueryForm queryForm) {
        AttendancePunchInfoVO result = attendancePunchRecordBiz.punchInfo(queryForm);
        return ResponseUtil.success(result);
    }

    /**
     * 考勤打卡
     *
     * @param form 考勤打卡记录修改模型
     * @return ResponseResult
     */
    @ApiOperation("考勤打卡")
    @ApiImplicitParam(value = "考勤打卡记录修改模型")
    @PostMapping("/punch")
    @CurrentUser
    public ResponseResult punch(@RequestBody @Validated AttendancePunchRecordForm form) {
        attendancePunchRecordBiz.punch(form);
        return ResponseUtil.success();
    }

    /**
     * 根据年月查询员工考勤打卡日历。
     *
     * @param date 该月的某一天，例如2020-11-01
     * @return ResponseResult<List<AttendanceInfoVO>>
     */
    @ApiOperation("根据年月查询员工考勤打卡日历")
    @ApiImplicitParam(value = "该月的某一天，例如2020-11-01")
    @GetMapping("/punchRecordCalendarByMonth/{date}")
    @CurrentUser
    public ResponseResult<List<AttendanceCalendarInfoVO>> punchRecordCalendarByMonth(@PathVariable(value = "date") @NotBlank String date) {
        List<AttendanceCalendarInfoVO> result = attendancePunchRecordBiz.punchRecordCalendarByMonth(date);
        return ResponseUtil.success(result);
    }

    /**
     * 查询打卡日历中指定日期下的员工考勤打卡列表
     *
     * @param date 日期
     * @return ResponseResult<AttendancePunchInfoVO>
     */
    @ApiOperation("查询打卡日历中指定日期下的员工考勤打卡列表")
    @ApiImplicitParam(value = "该月的某一天，例如2020-11-01")
    @GetMapping("/punchRecordByDate/{date}")
    @CurrentUser
    public ResponseResult<AttendancePunchCalendarInfoVO> punchRecordByDate(@PathVariable(value = "date") @NotNull Date date) {
        AttendancePunchCalendarInfoVO attendancePunchInfoVO = attendancePunchRecordBiz.punchRecordByDate(date);
        return ResponseUtil.success(attendancePunchInfoVO);
    }

    /**
     * 根据年月查询员工考勤打卡月汇总。
     *
     * @param date 该月的某一天，例如2020-11-01
     * @return ResponseResult<AttendanceStatisticsVO>
     */
    @ApiOperation("根据年月查询员工考勤打卡月汇总")
    @ApiImplicitParam(value = "该月的某一天，例如2020-11-01")
    @GetMapping("/punchRecordByMonth/{date}")
    @CurrentUser
    public ResponseResult<AttendanceStatisticsVO> punchRecordByMonth(@PathVariable(value = "date") @NotBlank String date) {
        AttendanceStatisticsVO result = attendancePunchRecordBiz.punchRecordByMonth(date);
        return ResponseUtil.success(result);
    }

    /**
     * 根据条件分页查询考勤汇总
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceStatisticsVO>>
     */
    @ApiOperation("根据条件分页查询考勤汇总")
    @ApiImplicitParam(value = "查询参数", required = true)
    @PostMapping("/statisticsPunchRecord")
    public ResponseResult<PageInfo<AttendanceStatisticsVO>> statisticsPunchRecord(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceStatisticsVO> result = attendancePunchRecordBiz.statisticsPunchRecord(queryForm);
        PageInfo<AttendanceStatisticsVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件分页查询考勤汇总导出
     *
     * @param queryForm 查询参数
     * @return
     */
    @ApiOperation("根据条件分页查询考勤汇总导出")
    @ApiImplicitParam(value = "查询参数", required = true)
    @PostMapping("/statisticsPunchRecordExport")
    public ResponseResult statisticsPunchRecordExport(HttpServletResponse response, @RequestBody AttendanceStatisticsQueryForm queryForm) throws IOException {
        attendancePunchRecordBiz.statisticsPunchRecordExport(response, queryForm);
        return ResponseUtil.success(null);
    }

    /**
     * 根据统计次数类型的分页查询考勤汇总明细
     *
     * @param type 统计次数类型：0-缺卡，1-迟到，3-早退，4-无效卡，5-异常
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendancePunchCountVO>>
     */
    @ApiOperation("根据统计次数类型的分页查询考勤汇总明细")
    @ApiImplicitParam(value = "查询参数", required = true)
    @PostMapping("/statisticsPunchRecordByCount/{type}")
    public ResponseResult<PageInfo<AttendancePunchCountVO>> statisticsPunchRecordByCount(@PathVariable(value = "type") @NotNull byte type, @RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendancePunchCountVO> result = attendancePunchRecordBiz.statisticsPunchRecordByCount(type, queryForm);
        PageInfo<AttendancePunchCountVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据统计时长类型的分页查询考勤汇总明细
     *
     * @param type 统计次数类型：0-工作日时长，1-工作日加班时长，2-工作日加班超30分钟，3-休息日加班时长，4-请假时长，5-外勤时长
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendancePunchMinuteVO>>
     */
    @ApiOperation("根据统计时长类型的分页查询考勤汇总明细")
    @ApiImplicitParam(value = "查询参数", required = true)
    @PostMapping("/statisticsPunchRecordByMinute/{type}")
    public ResponseResult<PageInfo<AttendancePunchMinuteVO>> statisticsPunchRecordByMinute(@PathVariable(value = "countType") @NotNull byte type, @RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendancePunchMinuteVO> result = attendancePunchRecordBiz.statisticsPunchRecordByMinute(type, queryForm);
        PageInfo<AttendancePunchMinuteVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }
}
