package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.employee_attend.vo.PeriodontalRecordsVO;
import com.yunya.feign.treatment_other.domain.form.PeriodontalRecordsAddForm;
import com.yunya.feign.treatment_other.domain.form.PeriodontalRecordsForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.PeriodontalRecordsBiz;
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
@Api(tags = "牙周记表")
@RestController
@RequestMapping("periodontal/record")
public class PeriodontalRecordsController {

    @Autowired
    private PeriodontalRecordsBiz periodontalRecordsBiz;

    /**
     * 获取牙周记表
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("获取牙周记表")
    @RepeatSubmit
    public ResponseResult<List<PeriodontalRecordsVO>> findList(@RequestBody @Validated PeriodontalRecordsForm periodontalRecordsForm) {
        return ResponseUtil.success(periodontalRecordsBiz.findList(periodontalRecordsForm));
    }

    /**
     * 新增牙周记表
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增牙周记表")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult create(@RequestBody @Validated PeriodontalRecordsAddForm periodontalRecordsAddForm) {
        return ResponseUtil.success(periodontalRecordsBiz.create(periodontalRecordsAddForm));
    }

    /**
     * 修改牙周记表
     *
     * @param
     * @return
     */
    @PutMapping("")
    @ApiOperation("修改牙周记表")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated PeriodontalRecordsAddForm periodontalRecordsAddForm) {
        return ResponseUtil.success(periodontalRecordsBiz.update(periodontalRecordsAddForm));
    }

    /**
     *  删除牙周记表
     *
     * @param
     * @return
     */
    @DeleteMapping("")
    @ApiOperation("删除牙周记表")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult delete(@RequestBody @Validated PeriodontalRecordsForm periodontalRecordsForm) {
        return ResponseUtil.success(periodontalRecordsBiz.delete(periodontalRecordsForm));
    }
}
