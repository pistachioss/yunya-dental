package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.WorkOvertimeInfoBiz;
import com.yunya.modules.employeeattend.form.WorkOvertimeInfoForm;
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
@Api(tags = "加班接口")
@RestController
@RequestMapping("/work_overtime_info")
@CrossOrigin
public class WorkOvertimeInfoController {
    @Autowired
    private WorkOvertimeInfoBiz workOvertimeInfoBiz;

    /**
     * 新增加班申请
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增加班申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult create(@RequestBody @Validated WorkOvertimeInfoForm workOvertimeInfoForm) {
//        return ResponseUtil.success(workOvertimeInfoBiz.create(workOvertimeInfoForm));
        return null;
    }


}
