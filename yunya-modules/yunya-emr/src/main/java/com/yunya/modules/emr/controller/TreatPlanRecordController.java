package com.yunya.modules.emr.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.TreatPlanRecordChangeForm;
import com.yunya.feign.emr.domain.model.TreatPlanRecordModel;
import com.yunya.feign.emr.domain.query.PlanTypeDetailQuery;
import com.yunya.feign.emr.domain.query.PlanTypeStatisticsQuery;
import com.yunya.feign.emr.domain.query.TreatPlanRecordQuery;
import com.yunya.feign.emr.domain.vo.TreatPlanRecordInfoVO;
import com.yunya.feign.emr.domain.vo.TreatPlanRecordVO;
import com.yunya.feign.emr.domain.vo.TreatPlanTypeDetailVO;
import com.yunya.feign.emr.domain.vo.TreatPlanTypeStatisticsVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.TreatPlanRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
        treatPlanRecordBiz.save(model, (byte) 0, true);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "根据治疗计划id查询治疗计划（打印）")
    @GetMapping("/one/{planId}")
    public ResponseResult<TreatPlanRecordVO> findOneById(@PathVariable(value = "planId") Integer planId) {
        return ResponseUtil.success(treatPlanRecordBiz.findOneById(planId));
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public ResponseResult<PageInfo<TreatPlanRecordInfoVO>> findList(@Valid @RequestBody TreatPlanRecordQuery query) {
        PageInfo<TreatPlanRecordInfoVO> page = treatPlanRecordBiz.findTreatPlanRecordInfoList(query);
        return ResponseUtil.success(page);
    }

    @ApiOperation("查询患者所有已确认、进行中的治疗计划列表")
    @PostMapping("/patientList")
    public ResponseResult<PageInfo<TreatPlanRecordVO>> findPatientTreatPlanList(
            @RequestParam("orgId") @Valid @NotNull(message = "门诊id不能为空") Integer orgId,
            @Valid @RequestBody TreatPlanRecordQuery query) {
        PageInfo<TreatPlanRecordVO> page = treatPlanRecordBiz.findPatientTreatPlanList(orgId, query);
        return ResponseUtil.success(page);
    }

    @ApiOperation("治疗计划变更：方案确认，方案变更，提前终止，撤销终止")
    @PutMapping("/change")
    @CurrentUser
    public ResponseResult<TreatPlanRecordVO> treatPlanChange(@RequestBody @Valid TreatPlanRecordChangeForm form) {
        return ResponseUtil.success(treatPlanRecordBiz.treatPlanChange(form));
    }

    @ApiOperation("条件查询治疗类型统计列表")
    @PostMapping("/planType/statistics")
    public ResponseResult<PageInfo<TreatPlanTypeStatisticsVO>> findTreatPlanTypeStatistics(@Valid @RequestBody PlanTypeStatisticsQuery query) {
        PageInfo<TreatPlanTypeStatisticsVO> page = treatPlanRecordBiz.findTreatPlanTypeStatistics(query);
        return ResponseUtil.success(page);
    }

    @ApiOperation("条件查询治疗类型统计明细列表")
    @PostMapping("/planType/detail")
    public ResponseResult<PageInfo<TreatPlanTypeDetailVO>> findTreatPlanTypeDetail(@Valid @RequestBody PlanTypeDetailQuery query) {
        PageInfo<TreatPlanTypeDetailVO> page = treatPlanRecordBiz.findTreatPlanTypeDetail(query);
        return ResponseUtil.success(page);
    }

    @ApiOperation("条件导出治疗类型统计明细列表")
    @PostMapping("/planType/detail/export")
    public ResponseResult<T> exportTreatPlanTypeDetail(@Valid @RequestBody PlanTypeDetailQuery query, HttpServletResponse response) throws Exception {
        treatPlanRecordBiz.exportTreatPlanTypeDetail(query, response);
        return ResponseUtil.success(null);
    }
}
