package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordForm;
import com.yunya.feign.employee_attend.form.AttendancePunchRecordQueryForm;
import com.yunya.feign.employee_attend.form.AttendanceStatisticsQueryForm;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.modules.employeeattend.biz.AttendancePunchRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

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
    @GetMapping("/punchRecordByDate/{date}")
    @CurrentUser
    public ResponseResult<AttendancePunchCalendarInfoVO> punchRecordByDate(@PathVariable(value = "date") @NotBlank String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(date);
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
        }
        AttendancePunchCalendarInfoVO attendancePunchInfoVO = attendancePunchRecordBiz.punchRecordByDate(dateTime);
        return ResponseUtil.success(attendancePunchInfoVO);
    }

    /**
     * 根据年月查询员工考勤打卡月汇总。
     *
     * @param date 该月的某一天，例如2020-11-01
     * @return ResponseResult<AttendanceStatisticsVO>
     */
    @ApiOperation("根据年月查询员工考勤打卡月汇总")
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
    @PostMapping("/statisticsPunchRecord")
    public ResponseResult<PageInfo<AttendanceStatisticsVO>> statisticsPunchRecord(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        PageInfo<AttendanceStatisticsVO> pageInfo = attendancePunchRecordBiz.statisticsPunchRecord(queryForm);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件分页查询考勤汇总导出
     *
     * @param queryForm 查询参数
     * @return
     */
    @ApiOperation("根据条件分页查询考勤汇总导出")
    @PostMapping("/statisticsPunchRecordExport")
    public void statisticsPunchRecordExport(HttpServletResponse response, @RequestBody AttendanceStatisticsQueryForm queryForm) throws IOException {
        queryForm.setWhetherPage(false);
        String date = queryForm.getDate();
        if (StringHelper.isEmpty(date)) {
            throw new ClientServiceException("没有选择日期导出条件！", PARAMETERS_IS_ILLEGAL);
        }
        PageInfo<AttendanceStatisticsVO> list = attendancePunchRecordBiz.statisticsPunchRecord(queryForm);
        List<AttendanceStatisticsVO> result = list.getList();
        if (result==null || result.isEmpty()) {
            throw new ClientServiceException("没有数据记录可以导出！", DATA_NOT_EXIST);
        }
        ExcelUtil<AttendanceStatisticsVO> excelUtil = new ExcelUtil<>(AttendanceStatisticsVO.class);
        excelUtil.exportExcel(response, result, "考勤汇总统计表","考勤汇总统计表");
    }

    /**
     * 分页查询工作时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<AttendancePunchPageInfoVO<AttendanceWorkDateMinuteVO>>
     */
    @ApiOperation("分页查询工作时长的考勤汇总明细")
    @PostMapping("/statisticsWorkDateByMinute")
    public ResponseResult<AttendancePunchPageInfoVO<AttendanceWorkDateMinuteVO>> statisticsWorkDateByMinute(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        AttendancePunchPageInfoVO<AttendanceWorkDateMinuteVO> result = attendancePunchRecordBiz.statisticsWorkDateByMinute(queryForm);
        return ResponseUtil.success(result);
    }

    /**
     * 分页查询工作时长的考勤汇总明细导出
     *
     * @param queryForm 查询参数
     * @return
     */
    @ApiOperation("分页查询工作时长的考勤汇总明细导出")
    @PostMapping("/statisticsWorkDateByMinuteExport")
    public void statisticsWorkDateByMinuteExport(HttpServletResponse response, @RequestBody AttendanceStatisticsQueryForm queryForm) throws IOException {
        queryForm.setWhetherPage(false);
        PageInfo<AttendanceWorkDateMinuteVO> pageInfo = attendancePunchRecordBiz.statisticsWorkDateByMinute(queryForm);
        List<AttendanceWorkDateMinuteVO> list = pageInfo.getList();
        if (list==null || list.isEmpty()) {
            throw new ClientServiceException("没有数据记录可以导出！", DATA_NOT_EXIST);
        }
        ExcelUtil<AttendanceWorkDateMinuteVO> excelUtil = new ExcelUtil<>(AttendanceWorkDateMinuteVO.class);
        excelUtil.exportExcel(response, list, "工作日时长统计表","工作日时长统计表");
    }

    /**
     * 分页查询工作日加班时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendancePunchMinuteVO>>
     */
    @ApiOperation("分页查询工作日加班时长的考勤汇总明细")
    @PostMapping("/statisticsWorkDateOvertimeByMinute")
    public ResponseResult<PageInfo<AttendanceWorkDateOvertimeMinuteVO>> statisticsWorkDateOvertimeByMinute(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceWorkDateOvertimeMinuteVO> result = attendancePunchRecordBiz.statisticsWorkDateOvertimeByMinute(1, queryForm);
        PageInfo<AttendanceWorkDateOvertimeMinuteVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询工作日加班时长超30分钟的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendancePunchMinuteVO>>
     */
    @ApiOperation("分页查询工作日加班时长超30分钟的考勤汇总明细")
    @PostMapping("/statisticsWorkDateOvertime30ByMinute")
    public ResponseResult<PageInfo<AttendanceWorkDateOvertimeMinuteVO>> statisticsWorkDateOvertime30ByMinute(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceWorkDateOvertimeMinuteVO> result = attendancePunchRecordBiz.statisticsWorkDateOvertimeByMinute(30, queryForm);
        PageInfo<AttendanceWorkDateOvertimeMinuteVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询休息日加班时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<AttendancePunchPageInfoVO<AttendanceWorkOvertimeMinuteVO>>
     */
    @ApiOperation("分页查询休息日加班时长的考勤汇总明细")
    @PostMapping("/statisticsWorkOvertimesByMinute")
    public ResponseResult<AttendancePunchPageInfoVO<AttendanceOvertimeMinuteVO>> statisticsWorkOvertimesByMinute(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        AttendancePunchPageInfoVO<AttendanceOvertimeMinuteVO> pageInfo = attendancePunchRecordBiz.statisticsWorkOvertimesByMinute(queryForm);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询请假时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceLeaveMinuteVO>>
     */
    @ApiOperation("分页查询请假时长的考勤汇总明细")
    @PostMapping("/statisticsLeavesByMinute")
    public ResponseResult<PageInfo<AttendanceLeaveMinuteVO>> statisticsLeavesByMinute(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceLeaveMinuteVO> result = attendancePunchRecordBiz.statisticsLeavesByMinute(queryForm);
        PageInfo<AttendanceLeaveMinuteVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询外勤时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceFieldMinuteVO>>
     */
    @ApiOperation("分页查询外勤时长的考勤汇总明细")
    @PostMapping("/statisticsFieldsByMinute")
    public ResponseResult<PageInfo<AttendanceFieldMinuteVO>> statisticsFieldsByMinute(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceFieldMinuteVO> result = attendancePunchRecordBiz.statisticsFieldsByMinute(queryForm);
        PageInfo<AttendanceFieldMinuteVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询迟到统计的明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceLaterCountVO>>
     */
    @ApiOperation("分页查询迟到统计的明细")
    @PostMapping("/statisticsPunchRecordByLaterCount")
    public ResponseResult<PageInfo<AttendanceLaterCountVO>> statisticsPunchRecordByLaterCount(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceLaterCountVO> result = attendancePunchRecordBiz.statisticsPunchRecordByLaterCount(queryForm);
        PageInfo<AttendanceLaterCountVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询早退统计的明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceEarlyCountVO>>
     */
    @ApiOperation("分页查询早退统计的明细")
    @PostMapping("/statisticsPunchRecordByEarlyCount")
    public ResponseResult<PageInfo<AttendanceEarlyCountVO>> statisticsPunchRecordByEarlyCount(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceEarlyCountVO> result = attendancePunchRecordBiz.statisticsPunchRecordByEarlyCount(queryForm);
        PageInfo<AttendanceEarlyCountVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询缺卡统计的明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceEarlyCountVO>>
     */
    @ApiOperation("分页查询缺卡统计的明细")
    @PostMapping("/statisticsPunchRecordByUnpunchCount")
    public ResponseResult<PageInfo<AttendanceUnpunchCountVO>> statisticsPunchRecordByUnpunchCount(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceUnpunchCountVO> result = attendancePunchRecordBiz.statisticsPunchRecordByUnpunchCount(queryForm);
        PageInfo<AttendanceUnpunchCountVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 分页查询无效卡统计的明细
     *
     * @param queryForm 查询参数
     * @return ResponseResult<PageInfo<AttendanceEarlyCountVO>>
     */
    @ApiOperation("分页查询无效卡统计的明细")
    @PostMapping("/statisticsPunchRecordByInvalidCount")
    public ResponseResult<PageInfo<AttendanceInvalidCountVO>> statisticsPunchRecordByInvalidCount(@RequestBody AttendanceStatisticsQueryForm queryForm) {
        List<AttendanceInvalidCountVO> result = attendancePunchRecordBiz.statisticsPunchRecordByInvalidCount(queryForm);
        PageInfo<AttendanceInvalidCountVO> pageInfo = new PageInfo<>(result);
        return ResponseUtil.success(pageInfo);
    }
}
