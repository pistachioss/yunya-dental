package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.QcTreatmentRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseResult<PageInfo<QcRecommondInfoVO>> findMallRecommondList(QcRecommondInfoQuery query) {
        PageInfo<QcRecommondInfoVO> pageInfo = qcTreatmentRecordBiz.findMallRecommondList(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * mall平台医嘱核销
     *
     * @param id
     * @param verifyCode
     * @return
     */
    @CurrentUser
    @ApiOperation("mall平台医嘱核销")
    @PutMapping("/verify/{id}/{verifyCode}")
    public ResponseResult verify(@PathVariable(value = "id") Integer id, @PathVariable(value = "verifyCode") String verifyCode) {
        qcTreatmentRecordBiz.verify(id, verifyCode);
        return ResponseUtil.success();
    }

    /**
     * mall平台推荐绑定患者
     *
     * @param id
     * @param patientId
     * @return
     */
    @CurrentUser
    @ApiOperation("mall平台推荐绑定患者")
    @PutMapping("/bindPatient/{id}/{patientId}")
    public ResponseResult bindPatient(@PathVariable(value = "id") Integer id, @PathVariable(value = "patientId") Integer patientId) {
        qcTreatmentRecordBiz.bindPatient(id, patientId);
        return ResponseUtil.success();
    }

    /**
     * 医嘱执行
     *
     * @param id
     * @return
     */
    @CurrentUser
    @ApiOperation("医嘱执行")
    @PutMapping("/execute/{id}")
    public ResponseResult execute(@PathVariable(value = "id") Integer id) {
        qcTreatmentRecordBiz.execute(id);
        return ResponseUtil.success();
    }


    /**
     * 同步医嘱上传
     *
     * @param qcTreatmentIds
     * @return
     */
    @CurrentUser
    @ApiOperation("同步医嘱上传")
    @PostMapping("/adviceUpload")
    public ResponseResult doctorAdviceUpload(@RequestBody @Validated List<Integer> qcTreatmentIds) {
        qcTreatmentRecordBiz.doctorAdviceUpload(qcTreatmentIds);
        return ResponseUtil.success();
    }


}
