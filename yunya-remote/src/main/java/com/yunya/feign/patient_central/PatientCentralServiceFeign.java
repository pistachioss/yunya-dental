package com.yunya.feign.patient_central;


import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.MemberExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.MemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.SecondaryMemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientTotalInfoVo;
import com.yunya.feign.patient_central.factory.PatientCentralServiceFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import io.swagger.annotations.ApiOperation;
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
    @RequestMapping (value = "/api/total/patientInfo/{id}", method = RequestMethod.GET)
    PatientTotalInfoVo findPatientTotalInfo(@PathVariable(value = "id") Integer id);

    /**
     * 根据患者id查询患者资料
     * @param patientMemberInfo
     * @return
     */
    @RequestMapping (value = "/api/findPatientMemberInfo",method = RequestMethod.POST)
    List<PatientMemberInfo> findPatientMemberInfo(@RequestBody PatientMemberInfo patientMemberInfo);

    /**
     * 修改患者信息
     * @param patientBaseInfo
     */
    @RequestMapping (value = "/api/updatePatientInfo",method = RequestMethod.POST)
    void updatePatientInfo(@RequestBody PatientBaseInfo patientBaseInfo);

    /**
     * 查询患者信息
     * @param patientBaseInfo
     * @return
     */
    @RequestMapping (value = "/api/findPatientInfo",method = RequestMethod.POST)
    PatientBaseInfo findPatientInfo(@RequestBody PatientBaseInfo patientBaseInfo);

    /**
     * 查询患者信息列表
     * @param patientBaseInfo
     * @return
     */
    @RequestMapping (value = "/api/findPatientInfoList",method = RequestMethod.POST)
    List<PatientBaseInfo> findPatientInfoList(@RequestBody PatientBaseInfo patientBaseInfo);

    /**
     * 根据门诊id获取病历号后六位
     * @param orgId
     * @return
     */
    @RequestMapping (value = "/api/medical/{orgId}",method = RequestMethod.GET)
    String findMedicalNumberByOrgId(@PathVariable(value = "orgId") Integer orgId);

    /**
     * 会员卡消费
     * @param model
     * @return ResponseResult
     */
    @RequestMapping(value = "/api/member/expend",method = RequestMethod.POST)
    ResponseResult expend(@RequestBody MemberExpendRecordModel model );

    /**
     * 预付款消费
     * @param model
     * @return
     */
    @RequestMapping(value = "/api/prepaid/expend",method = RequestMethod.POST)
    ResponseResult expend(@RequestBody PrepaidExpendRecordModel model );

    /**
     * 修改硬件设备密码
     * @param form
     */
    @RequestMapping(value = "/api/updPass",method = RequestMethod.POST)
    void updPass(@RequestBody UpdPassForm form );

    /**
     *  查询会员卡绑定信息
     * @param form
     * @return List<MemberInfoVo>
     */
    @RequestMapping(value = "/api/findMemberInfo",method = RequestMethod.POST)
    MemberInfoVo findMemberInfo(@RequestBody PatientMemberInfoQueryForm form);

}
