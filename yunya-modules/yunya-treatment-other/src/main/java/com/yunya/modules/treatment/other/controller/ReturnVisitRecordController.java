package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.treatment_other.domain.form.ReturnVisitingRecordForm;
import com.yunya.feign.treatment_other.domain.query.ReturnVisitQuery;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitVO;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitRecordVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.ReturnVisitRecordBiz;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: chenlin
 * @date: 2022/9/26 13:07
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("/returnVisit")
public class ReturnVisitRecordController {

    @Autowired
    private ReturnVisitRecordBiz returnVisitRecordBiz;

    /**
     * 根据条件查询回访管理列表
     *
     * @return
     */
    @ApiOperation("根据条件查询回访管理列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<ReturnVisitVO>> findReturnVisitList(@RequestBody @Validated ReturnVisitQuery query) {
        PageInfo<ReturnVisitVO> page = returnVisitRecordBiz.findReturnVisitList(query);
        return ResponseUtil.success(page);
    }

    /**
     * 保存回访
     * @param form 保存回访
     * @return
     */
    @ApiOperation("保存回访")
    @PostMapping("/save")
    @CurrentUser
    public ResponseResult save(@RequestBody @Validated ReturnVisitingRecordForm form){
        returnVisitRecordBiz.save(form);
        return ResponseUtil.success();
    }


    /**
     * 根据条件查询回访记录列表
     *
     * @return
     */
    @ApiOperation("根据条件查询回访记录列表")
    @PostMapping("/find")
    public ResponseResult<PageInfo<ReturnVisitRecordVO>> findReturnVisitRecordList(@RequestBody @Validated MultiClinicDateRangeQueryForm query) {
        PageInfo<ReturnVisitRecordVO> page = returnVisitRecordBiz.findReturnVisitRecordList(query);
        return ResponseUtil.success(page);
    }
}
