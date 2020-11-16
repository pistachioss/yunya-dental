package com.yunya.modules.employeeattend.controller;

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
}
