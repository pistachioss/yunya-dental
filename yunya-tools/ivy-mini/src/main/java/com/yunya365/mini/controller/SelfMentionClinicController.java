package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.form.SelfMentionClinicForm;
import com.yunya.feign.ivy_mini.domain.vo.SelfMentionClinicVO;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.SelfMentionClinicServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@RestController
@Api(tags = "后台-可自提门诊管理")
public class SelfMentionClinicController extends PcBaseController{

    @Resource
    private SelfMentionClinicServiceImpl selfMentionClinicService;

    @PostMapping("/selfMentionClinic/findlist")
    @ApiOperation("后台-可自提门诊管理-列表")
    public ResponseResult<List<SelfMentionClinicVO>> findList() {
        return ResponseUtil.success(selfMentionClinicService.findList());
    }

    @PostMapping("/selfMentionClinic/add")
    @ApiOperation("后台-可自提门诊管理-新增")
    @RepeatSubmit
    public ResponseResult add(@RequestBody @Valid SelfMentionClinicForm form) {
        selfMentionClinicService.add(form);
        return ResponseUtil.success(null);
    }

    @PutMapping("/selfMentionClinic/update")
    @ApiOperation("后台-可自提门诊管理-修改")
    @RepeatSubmit
    public ResponseResult update(@RequestBody @Valid SelfMentionClinicForm form) {
        selfMentionClinicService.update(form);
        return ResponseUtil.success(null);
    }

    @ApiOperation("后台-可自提门诊管理-删除")
    @DeleteMapping("/selfMentionClinic/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return ResponseUtil.success(selfMentionClinicService.deleteById(id));
    }
}
