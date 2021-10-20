package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.model.TreatPlanModel;
import com.yunya.feign.treatment_other.domain.query.TreatPlanQuery;
import com.yunya.feign.treatment_other.domain.vo.TreatPlanVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.TreatPlanBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介：治疗计划控制层
 *
 * @author: chenlin
 * @Description: 治疗计划控制层
 * @Date: 2021/10/19 16:02
 * @since: 1.0.0
 */
@Api(tags = "治疗计划控制器")
@RestController
@RequestMapping("/treatPlan")
public class TreatPlanController {

    @Autowired
    private TreatPlanBiz treatPlanBiz;

    @ApiOperation("查询治疗计划列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<TreatPlanVO>> findTreatPlanList(@RequestBody @Validated TreatPlanQuery query){
        PageInfo<TreatPlanVO> data = treatPlanBiz.findTreatPlanList(query);
        return ResponseUtil.success(data);
    }

    @ApiOperation("添加治疗计划（批量添加文件）")
    @PostMapping("/add/batch")
    @CurrentUser
    public ResponseResult<T> addBatch(@Validated @RequestBody TreatPlanModel model){
        return treatPlanBiz.addBatch(model);
    }
}
