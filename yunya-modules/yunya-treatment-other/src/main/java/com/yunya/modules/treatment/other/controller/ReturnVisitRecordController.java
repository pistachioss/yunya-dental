package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.query.PatientReturnVisitQuery;
import com.yunya.feign.treatment_other.domain.form.ReturnVisitRecordForm;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitRecordVO;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.ReturnVisitRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: chenlin
 * @date: 2022/9/26 13:07
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "回访记录控制器")
@RestController
@RequestMapping("/returnVisit")
public class ReturnVisitRecordController {

    @Autowired
    private ReturnVisitRecordBiz returnVisitRecordBiz;

    /**
     * 根据条件查询回访记录列表
     *
     * @return
     */
    @ApiOperation("根据条件查询回访记录列表")
    @PutMapping("/save")
    @CurrentUser
    public ResponseResult save(@RequestBody @Validated ReturnVisitRecordForm form) {
        returnVisitRecordBiz.save(form);
        return ResponseUtil.success(null);
    }

    /**
     * 根据就诊id获取回访明细
     *
     * @param treatmentId
     * @return
     */
    @ApiOperation("根据就诊id获取回访明细")
    @GetMapping("/list/{treatmentId}")
    public ResponseResult<ReturnVisitVO> findListByTreatmentId(@PathVariable(value = "treatmentId") Integer treatmentId){
        ReturnVisitVO result = returnVisitRecordBiz.findListByTreatmentId(treatmentId);
        return ResponseUtil.success(result);
    }

    /**
     * 根据条件查询回访记录列表
     *
     * @return
     */
    @ApiOperation("根据条件查询回访记录列表")
    @PostMapping("/find")
    public ResponseResult<PageInfo<ReturnVisitRecordVO>> findReturnVisitRecordList(@RequestBody PatientReturnVisitQuery query) {
        PageInfo<ReturnVisitRecordVO> page = returnVisitRecordBiz.findReturnVisitRecordList(query);
        return ResponseUtil.success(page);
    }
}
