package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentInfo4ListVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseTreatmentProcessBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: yunya-dental
 * @description: 就诊信息
 * @author: LHB
 * @create: 2020-12-23 09:45
 **/
@Api(tags = "手机端就诊信息")
@RestController
@RequestMapping("/app/treatment")
public class TreatmentInfoFeign {
    @Autowired
    private BaseTreatmentProcessBiz baseTreatmentProcessBiz;

    /**
     * APP端就诊列表
     * @param query 查询参数
     * @return 分页实体
     */
    @ApiOperation("手机端就诊信息")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseResult<PageInfo<PatientTreatmentInfo4ListVO>> treatmentList4App(@RequestBody TreatmentList4AppQuery query) {
        PageInfo<PatientTreatmentInfo4ListVO> treatmentProcessVOPageInfo = baseTreatmentProcessBiz.treatmentList4App(query);
        return ResponseUtil.success(treatmentProcessVOPageInfo);
    }
}
