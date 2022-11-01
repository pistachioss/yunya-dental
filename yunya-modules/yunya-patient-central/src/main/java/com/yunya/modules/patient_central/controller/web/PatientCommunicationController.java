package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.model.PatientCommunicationModel;
import com.yunya.feign.patient_central.domain.query.PatientCommunicationForm;
import com.yunya.feign.patient_central.domain.query.PatientCommunicationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientCommunicationInfoVO;
import com.yunya.feign.patient_central.domain.vo.web.PatientCommunicationVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientCommunicationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: chenlin
 * @date: 2022/11/1 9:12
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "患者沟通记录")
@RestController
@RequestMapping("/communication")
public class PatientCommunicationController {

    @Autowired
    private PatientCommunicationBiz patientCommunicationBiz;


    /**
     * 根据条件查询患者沟通记录列表
     *
     * @param query
     * @return
     */
    @ApiOperation("根据条件查询患者沟通记录列表")
    @CurrentUser
    @PostMapping("/list")
    public ResponseResult<PageInfo<PatientCommunicationVO>> findList(@RequestBody @Validated PatientCommunicationQueryForm query) {
        PageInfo<PatientCommunicationVO> page =  patientCommunicationBiz.findList(query);
        return ResponseUtil.success(page);
    }

    /**
     * 根据id查询沟通详情
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id查询沟通详情")
    @CurrentUser
    @GetMapping("/one/{id}")
    public ResponseResult<PatientCommunicationInfoVO> findOneById(@PathVariable(value = "id") Integer id) {
        PatientCommunicationInfoVO info = patientCommunicationBiz.findOneById(id);
        return ResponseUtil.success(info);
    }

    /**
     * 添加患者沟通记录
     *
     * @param model
     * @return
     */
    @ApiOperation("添加患者沟通记录")
    @CurrentUser
    @PostMapping("/add")
    public ResponseResult add(@RequestBody @Validated PatientCommunicationModel model) {
        patientCommunicationBiz.add(model);
        return ResponseUtil.success();
    }

    /**
     * 修改患者沟通记录
     *
     * @param form
     * @return
     */
    @ApiOperation("修改患者沟通记录")
    @CurrentUser
    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody @Validated PatientCommunicationForm form) {
        patientCommunicationBiz.edit(form);
        return ResponseUtil.success();
    }

    /**
     * 删除患者沟通记录
     *
     * @param id
     * @return
     */
    @ApiOperation("删除患者沟通记录")
    @CurrentUser
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        patientCommunicationBiz.delete(id);
        return ResponseUtil.success();
    }
}
