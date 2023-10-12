package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondDetailVO;
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
    @CurrentUser
    @ApiOperation("条件查询mall推荐列表")
    @PostMapping("/treatment/list")
    public ResponseResult<PageInfo<QcRecommondInfoVO>> findQcTreatmentList(@RequestBody @Validated QcRecommondInfoQuery query) {
        PageInfo<QcRecommondInfoVO> pageInfo = qcTreatmentRecordBiz.findMallRecommondList(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据全程就诊记录id查询详情
     *
     * @return
     */
    @ApiOperation("根据全程就诊记录id查询详情")
    @GetMapping("/treatment/{id}")
    public ResponseResult<QcRecommondDetailVO> findById(@PathVariable(value = "id") Integer id) {
        return ResponseUtil.success(qcTreatmentRecordBiz.findById(id));
    }



    /**
     * 医嘱单核销：根据核销码拉取全程就诊记录隐藏数据
     *
     * @param qcTreatmentId
     * @param query
     * @return
     */
    @CurrentUser
    @ApiOperation("医嘱单核销：根据核销码拉取全程就诊记录隐藏数据")
    @PutMapping("/treatment/verify/{qcTreatmentId}")
    public ResponseResult verify(@PathVariable(value = "qcTreatmentId") Integer qcTreatmentId, @RequestBody @Validated QcRecommondInfoQuery query) {
        qcTreatmentRecordBiz.verify(qcTreatmentId, query);
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
     * 同步医嘱上传
     *
     * @param qcTreatmentIds
     * @return
     */
    @CurrentUser
    @ApiOperation("同步医嘱上传")
    @PostMapping("/adviceUpload")
    public ResponseResult doctorAdviceUpload(@RequestBody @Validated List<Integer> qcTreatmentIds) {
        qcTreatmentRecordBiz.uploadAdviceItems(qcTreatmentIds);
        return ResponseUtil.success();
    }

    /**
     * 查询患者的可用全程医疗就诊记录列表
     *
     * @param patientId
     * @return
     */
    @ApiOperation("查询患者的可用全程医疗就诊记录列表")
    @GetMapping("/bindTreatment/{patientId}")
    public ResponseResult<List<QcRecommondInfoVO>> findPatientQcTreatmentRecord(@PathVariable(value = "patientId") Integer patientId) {
        List<QcRecommondInfoVO> pageInfo = qcTreatmentRecordBiz.findPatientEnableQcTreatmentRecord(patientId);
        return ResponseUtil.success(pageInfo);
    }
}
