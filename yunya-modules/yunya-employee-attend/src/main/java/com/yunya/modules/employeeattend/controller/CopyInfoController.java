package com.yunya.modules.employeeattend.controller;

import com.yunya.feign.employee_attend.form.AttendanceItemCountQuery;
import com.yunya.feign.employee_attend.vo.AttendanceItemCountVO;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.CopyInfoBiz;
import com.yunya.modules.employeeattend.form.CopyInfoForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(tags = "抄送接口")
@RestController
@RequestMapping("/copy_info")
@CrossOrigin
public class CopyInfoController {

    @Autowired
    private CopyInfoBiz copyInfoBiz;

    /**
     * 查看假期设置列表
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("查看抄送人列表")
    public ResponseResult findList(@RequestBody @Validated CopyInfoForm copyInfoForm) {
        return ResponseUtil.success(copyInfoBiz.findList(copyInfoForm));
    }

    /**
     * 获取待审批or抄送相关项目的未处理数量
     *
     * @param
     * @return
     */
    @PostMapping("/attendance/count")
    @ApiOperation("获取待审批or抄送相关项目的未处理数量")
    @RepeatSubmit
    public ResponseResult<AttendanceItemCountVO> attendanceItemCount(@RequestBody @Validated AttendanceItemCountQuery query) {
        return ResponseUtil.success(copyInfoBiz.attendanceItemCount(query));
    }
}
