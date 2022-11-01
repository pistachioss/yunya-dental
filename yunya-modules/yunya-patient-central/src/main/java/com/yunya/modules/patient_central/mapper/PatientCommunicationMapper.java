package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PatientCommunicationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientCommunicationVO;
import com.yunya.models.patient_central.PatientCommunication;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientCommunicationMapper extends Mapper<PatientCommunication> {
    /**
     * 根据条件查询患者沟通记录列表
     *
     * @param query
     * @return
     */
    List<PatientCommunicationVO> selectPatientCommunicationList(@Param("query") PatientCommunicationQueryForm query);
}