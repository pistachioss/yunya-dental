package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.VacationSet;
import com.yunya.modules.employeeattend.biz.VacationSetBiz;
import com.yunya.modules.employeeattend.form.VacationDeleteForm;
import com.yunya.modules.employeeattend.form.VacationSetForm;
import com.yunya.modules.employeeattend.form.VacationSetQuery;
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
@Api(tags = "假期设置接口")
@RestController
@RequestMapping("/vacation_set")
@CrossOrigin
public class VacationSetController {
    @Autowired
    private VacationSetBiz vacationSetBiz;

    /**
         * 查看假期设置列表
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("查看假期设置列表")
    public ResponseResult<PageInfo<VacationSet>> findList(@RequestBody @Validated VacationSetQuery vacationSetQuery) {
        return ResponseUtil.success(vacationSetBiz.findlist(vacationSetQuery));
    }

    /**
     * 新增假期设置
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增假期设置")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult create(@RequestBody @Validated VacationSetForm vacationSetForm) {
        return ResponseUtil.success(vacationSetBiz.create(vacationSetForm));
    }

    /**
     * 修改假期设置
     * @param
     * @return
     */
    @PutMapping
    @ApiOperation("修改假期设置")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated VacationSetForm vacationSetForm) {
        return ResponseUtil.success(vacationSetBiz.update(vacationSetForm));
    }

    /**
     * 删除假期设置
     * @param
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除假期设置")
    @RepeatSubmit
    public ResponseResult delete(@RequestBody @Validated VacationDeleteForm vacationDeleteForm) {
        return ResponseUtil.success(vacationSetBiz.delete(vacationDeleteForm));
    }

}
