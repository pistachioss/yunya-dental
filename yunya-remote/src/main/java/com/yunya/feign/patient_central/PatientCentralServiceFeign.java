package com.yunya.feign.patient_central;


import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.feign.patient_central.factory.PatientCentralServiceFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.patient_central.PatientBaseInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    PatientBaseInfo findPatientInfoById(@PathVariable Integer id);
}
