package com.yunya.modules.patient_central.controller.app;

import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientExtendInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientArchivesVo;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientBaseInfoVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.util.List;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/21 10:29
 * @description:
 * @since: 1.0.0
 */
@Api(value = "app端患者信息", description = "患者信息（增删查改）")
@RestController
@RequestMapping("app/central")
public class AppPatientBaseInfoController {

    /** 注入对象 */
    private final PatientBaseInfoBiz patientBaseInfoBiz;

    public AppPatientBaseInfoController(PatientBaseInfoBiz patientBaseInfoBiz) {
        this.patientBaseInfoBiz = patientBaseInfoBiz;
    }

    /**
     * 添加患者基本信息信息
     * @param patientBaseInfoModel 新增患者信息
     * @return ResponseResult
     */
    @CurrentUser
    @ApiOperation("添加患者基本信息信息")
    @PostMapping("/add")
    public ResponseResult addPatient(
            @RequestBody @Validated PatientBaseInfoModel patientBaseInfoModel) {
        return ResponseUtil.success(this.patientBaseInfoBiz.addPatient(patientBaseInfoModel));
    }

    /**
     * 根据姓名和手机号判断是否已存在
     * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
     * @return ResponseResult
     */
    @ApiOperation("根据姓名和手机号判断是否已存在")
    @PostMapping("/userExistsFind")
    public ResponseResult findUserExists(
            @RequestBody PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
        return this.patientBaseInfoBiz.appFindUserExists(patientBaseInfoQueryForm);
    }

    /**
     * app端  姓名/病例编号/手机号/姓名拼音模糊查询患者
     * @param patientBaseInfoQueryForm 患者模糊查询模板
     * @return ResponseResult
     */
    @ApiOperation("根据姓名/病例编号/手机号/姓名拼音模糊查询患者")
    @PostMapping("/likePatient")
    public ResponseResult findPatientByNameAndMobile(@RequestBody PatientLikeFinleQueryForm patientBaseInfoQueryForm) {
        List<AppPatientBaseInfoVo> appPatientBaseInfoVos = this.patientBaseInfoBiz.appFindPatientByNameAndMobile(patientBaseInfoQueryForm);
        return ResponseUtil.success(appPatientBaseInfoVos);
    }

    /**
     * 患者档案
     * @param patientId 患者id
     * @return ResponseResult
     */
    @ApiOperation("app端患者档案")
    @GetMapping("patientArchives/{patientId}")
    public ResponseResult patientArchives(@PathVariable(value = "patientId") Integer patientId) {
        AppPatientArchivesVo appPatientArchivesVo = this.patientBaseInfoBiz.patientArchives(patientId);
        return ResponseUtil.success(appPatientArchivesVo);
    }


    /**
     * 修改患者信息
     * @param patientExtendInfoModel 基本信息+扩展信息+其他信息 参数模板
     * @return ResponseResult
     */
    @CurrentUser
    @ApiOperation("修改患者信息")
    @PostMapping("/updatePatientInfo")
    public ResponseResult updatePatientInfo(
            @RequestBody @Validated PatientExtendInfoModel patientExtendInfoModel) {
        this.patientBaseInfoBiz.updatePatientInfo(patientExtendInfoModel);
        return ResponseUtil.success();
    }


    @ApiOperation("员工推荐二维码")
    @GetMapping("staffQRCode/{id}")
    public ResponseResult staffQRCode(@PathVariable(value = "id") Integer id) {
        /** 注入患者来源Mapper */
        String staffQRCode = this.patientBaseInfoBiz.staffQRCode(id);
        return ResponseUtil.success(staffQRCode);
    }




}