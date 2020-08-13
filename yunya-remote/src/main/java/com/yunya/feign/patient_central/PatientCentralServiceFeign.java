package com.yunya.feign.patient_central;


import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientExtendInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientTotalInfoVo;
import com.yunya.feign.patient_central.factory.PatientCentralServiceFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.Id;
import java.util.List;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_PATIENT,
        fallbackFactory = PatientCentralServiceFallBackFactory.class)
public interface PatientCentralServiceFeign {

    /**
     * 患者信息模糊查询暴露接口
     *
     * @param patientBaseInfoQueryForm 参数封装
     * @return
     */
    @RequestMapping(value = "/api/findPatientByNameAndMobile", method = RequestMethod.POST)
    List<PatientBaseInfoVo> findPatientByNameAndMobile(@RequestBody PatientLikeFinleQueryForm patientBaseInfoQueryForm);

    /**
     * 根据患者id查询患者信息
     * @param id
     * @return PatientBaseInfo
     */
    @RequestMapping (value = "/api/findPatientInfoById/{id}",method = RequestMethod.GET)
    PatientBaseInfo findPatientInfoById(@PathVariable(value = "id") Integer id);

    /**
     * 根据患者id集合查询患者list
     * @param ids
     * @return List<PatientBaseInfoVo>
     */
    @RequestMapping (value = "/api/findPatientInfoByIds",method = RequestMethod.POST)
    List<PatientBaseInfoVo> findPatientInfoByIds(@RequestBody List<Integer> ids);

    /**
     * 根据患者id查询患者资料
     * @param id
     * @return
     */
    @RequestMapping (value = "/findPatientData/{id}",method = RequestMethod.GET)
    PatientTotalInfoVo findPatientTotalInfo(@PathVariable("id") Integer id);

    /**
     * 根据患者id查询患者资料
     * @param patientMemberInfo
     * @return
     */
    @RequestMapping (value = "/findPatientMemberInfo",method = RequestMethod.POST)
    List<PatientMemberInfo> findPatientMemberInfo(@RequestBody PatientMemberInfo patientMemberInfo);

    /**
     * 修改患者信息
     * @param patientBaseInfo
     */
    @RequestMapping (value = "/updatePatientInfo",method = RequestMethod.POST)
    void updatePatientInfo(@RequestBody PatientBaseInfo patientBaseInfo);

    /**
     * 查询患者信息
     * @param patientBaseInfo
     * @return
     */
    @RequestMapping (value = "/findPatientInfo/{id}",method = RequestMethod.POST)
    PatientBaseInfo findPatientInfo(@RequestBody PatientBaseInfo patientBaseInfo);

    /**
     * 查询患者信息列表
     * @param patientBaseInfo
     * @return
     */
    @RequestMapping (value = "/findPatientInfoList/{id}",method = RequestMethod.POST)
    List<PatientBaseInfo> findPatientInfoList(@RequestBody PatientBaseInfo patientBaseInfo);
}
