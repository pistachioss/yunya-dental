package com.yunya.modules.patient_central.rpc;

import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientPublicInfoVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
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
    public List<PatientBaseInfoVo> findPatientInfoByIds(@PathVariable List<Integer> ids){
        return patientBaseInfoBiz.findPatientInfoByIds(ids);
    }
}
