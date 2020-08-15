package com.yunya.modules.patient_central.rpc;

import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientExtendInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientPublicInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientTotalInfoVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import com.yunya.modules.patient_central.biz.PatientMemberInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/3 15:22
 * @description:
 * @since: 1.0.0
 */
@Api("患者信息服务接口暴露")
@RestController
@RequestMapping("api")
public class PatientServiceRest {
    /** 患者 */
    @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;

    @Autowired private PatientMemberInfoBiz patientMemberInfoBiz;


    @ApiOperation("根据姓名/手机号/姓名拼音模糊查询患者")
    @RequestMapping (value = "/findPatientByNameAndMobile",method = RequestMethod.POST)
    public List<PatientBaseInfoVo> findPatientByNameAndMobile(@RequestBody PatientLikeFinleQueryForm patientBaseInfoQueryForm){
        return patientBaseInfoBiz.findPatientByNameAndMobile(patientBaseInfoQueryForm);
    }

    @ApiOperation("根据患者id查询患者信息")
    @RequestMapping (value = "/findPatientInfoById/{id}",method = RequestMethod.GET)
    public PatientBaseInfo findPatientInfoById(@PathVariable Integer id){
        return patientBaseInfoBiz.selectById(id);
    }

    @ApiOperation("根据患者id集合查询患者list")
    @RequestMapping (value = "/findPatientInfoByIds",method = RequestMethod.POST)
    public List<PatientBaseInfoVo> findPatientInfoByIds(@RequestBody List<Integer> ids){
        return patientBaseInfoBiz.findPatientInfoByIds(ids);
    }

    @ApiOperation("根据患者id查询患者资料")
    @RequestMapping (value = "/all/patientInfo/{id}",method = RequestMethod.GET)
    public PatientTotalInfoVo findPatientTotalInfo(@PathVariable(value = "id") Integer id){
        return patientBaseInfoBiz.findPatientTotalInfo(id);
    }

    @ApiOperation("根据患者id查询患者资料")
    @RequestMapping (value = "/findPatientMemberInfo",method = RequestMethod.POST)
    public List<PatientMemberInfo> findPatientMemberInfo(@RequestBody PatientMemberInfo patientMemberInfo){
        return patientMemberInfoBiz.selectList(patientMemberInfo);
    }

    @ApiOperation("修改患者信息")
    @RequestMapping (value = "/updatePatientInfo",method = RequestMethod.POST)
    public void updatePatientInfo(@RequestBody PatientBaseInfo patientBaseInfo){
        patientBaseInfoBiz.updateSelectiveById(patientBaseInfo);
    }

    @ApiOperation("查询患者信息")
    @RequestMapping (value = "/findPatientInfo/{id}",method = RequestMethod.POST)
    public PatientBaseInfo findPatientInfo(@RequestBody PatientBaseInfo patientBaseInfo){
        return patientBaseInfoBiz.selectOne(patientBaseInfo);
    }

    @ApiOperation("查询患者信息列表")
    @RequestMapping (value = "/findPatientInfoList/{id}",method = RequestMethod.POST)
    public List<PatientBaseInfo> findPatientInfoList(@RequestBody PatientBaseInfo patientBaseInfo){
        return patientBaseInfoBiz.selectList(patientBaseInfo);
    }

    @ApiOperation("根据门诊id获取病历号后六位")
    @RequestMapping (value = "/medical/{orgId}",method = RequestMethod.GET)
    public String findMedicalNumberByOrgId(@PathVariable(value = "orgId") Integer orgId){
        return patientBaseInfoBiz.findMedicalNumberByOrgId(orgId);
    }


}
