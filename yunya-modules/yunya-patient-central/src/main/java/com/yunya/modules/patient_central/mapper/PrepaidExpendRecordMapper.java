package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientPrepaymentsOwnerInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PrepaidExpendRecordVo;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
public interface PrepaidExpendRecordMapper extends Mapper<PrepaidExpendRecord> {

    /**
     * 查询预付款消费记录
     * @param queryForm 预付款消费QueryForm
     * @return List<PrepaidExpendRecordVo>
     */
    List<PrepaidExpendRecordVo> expendList(@Param("form") PrepaidExpendRecordQueryForm queryForm);

    /**
     * 查询共享帐户
     * @param patientId 患者id
     * @return 共享帐户信息
     */
    List<PatientPrepaymentsOwnerInfoVo> finishedAccount(@Param("patientId") Integer patientId);
}