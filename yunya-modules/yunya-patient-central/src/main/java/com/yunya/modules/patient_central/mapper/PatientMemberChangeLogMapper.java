package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.PatientMemberChangeLogVo;
import com.yunya.models.patient_central.PatientMemberChangeLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientMemberChangeLogMapper extends Mapper<PatientMemberChangeLog> {

    /**
     * 变更记录
     * @param cardNumber
     * @return List<PatientMemberChangeLogVo>
     */
    List<PatientMemberChangeLogVo> changeLog(String cardNumber);

    }
