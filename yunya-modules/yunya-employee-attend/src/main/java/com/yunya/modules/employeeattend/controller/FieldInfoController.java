package com.yunya.modules.employeeattend.controller;

import com.yunya.feign.employee_attend.vo.FieldInfoListVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.employeeattend.biz.FieldInfoBiz;
import com.yunya.modules.employeeattend.form.FieldInfoForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@Api(tags = "外勤接口")
@RestController
@RequestMapping("/field_info")
@CrossOrigin
public class FieldInfoController {
    @Autowired
    private FieldInfoBiz fieldInfoBiz;

    /**
     * 新增外勤申请
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增外勤申请")
    @RepeatSubmit
    public ResponseResult create(@RequestBody @Validated FieldInfoForm fieldInfoForm) {
        return ResponseUtil.success(fieldInfoBiz.create(fieldInfoForm));
    }

    /**
     * 获取外勤申请列表
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("获取外勤申请列表")
    @CurrentUser
    public ResponseResult<List<FieldInfoListVO>> findList(@RequestBody @Validated FieldInfoForm fieldInfoForm) {
        return ResponseUtil.success(fieldInfoBiz.findList(fieldInfoForm));
    }

    /**
     * 审核外勤申请
     *
     * @param
     * @return
     */
    @PostMapping("/examine")
    @ApiOperation("审核外勤申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult examine(@RequestBody @Validated FieldInfoForm fieldInfoForm) {
        return ResponseUtil.success(fieldInfoBiz.examine(fieldInfoForm));
    }

    /**
     * 撤销外勤申请
     *
     * @param
     * @return
     */
    @PostMapping("/revoke")
    @ApiOperation("撤销外勤申请")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult revoke(@RequestBody @Validated FieldInfoForm fieldInfoForm) {
        return ResponseUtil.success(fieldInfoBiz.revoke(fieldInfoForm));
    }
}
