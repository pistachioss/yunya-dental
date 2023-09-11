package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.QcTreatmentRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: chenlin
 * @date: 2023/9/11 15:12
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "全程医疗就诊控制器")
@RestController
@RequestMapping("/qc")
public class QcTreatmentRecordController {

    @Autowired
    private QcTreatmentRecordBiz qcTreatmentRecordBiz;

    /**
     * 条件查询mall推荐列表
     *
     * @return
     */
    @ApiOperation("条件查询mall推荐列表")
    @PostMapping("/recommond/list")
    public ResponseResult<PageInfo<QcRecommondInfoVO>> findMallRecommondList() {
        PageInfo<QcRecommondInfoVO> pageInfo = qcTreatmentRecordBiz.findMallRecommondList();
        return ResponseUtil.success(pageInfo);
    }
}
