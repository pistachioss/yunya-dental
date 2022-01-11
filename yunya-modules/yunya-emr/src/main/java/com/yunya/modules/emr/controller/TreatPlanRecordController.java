package com.yunya.modules.emr.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.model.TreatPlanRecordModel;
import com.yunya.feign.emr.domain.vo.CheckConfigVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.PageQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.TreatPlanRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author chenlin
 * @description: 治疗计划控制器
 * @date 2022/1/07
 */
@Api(tags = {"治疗计划控制器"})
@RestController
@RequestMapping("treatPlanRecord")
public class TreatPlanRecordController {

    @Autowired
    private TreatPlanRecordBiz treatPlanRecordBiz;

    @ApiOperation(value = "保存治疗计划")
    @PostMapping("/save")
    @CurrentUser
    public ResponseResult save(@Valid @RequestBody TreatPlanRecordModel model) {
        treatPlanRecordBiz.save(model);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "根据id查询")
    @GetMapping("/one/{id}")
    public ResponseResult<CheckConfigVO> findOneById(@PathVariable(value = "id") Integer id) {
//        return ResponseUtil.success(treatPlanRecordBiz.findOneById(id));
        return null;
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    @CurrentUser
    public ResponseResult<PageInfo<CheckConfigVO>> findList(@Valid @RequestBody PageQuery query) {
//        PageInfo<CheckConfigVO> page = treatPlanRecordBiz.findCheckConfigList(query);
//        return ResponseUtil.success(page);
        return null;
    }
}
