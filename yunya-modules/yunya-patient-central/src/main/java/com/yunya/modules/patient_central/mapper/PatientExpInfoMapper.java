package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.PatientExpInfoVo;
import com.yunya.models.patient_central.PatientExpInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface PatientExpInfoMapper extends Mapper<PatientExpInfo> {
    /**
     * 根据患者id查询患者扩展信息
     * @param id
     * @return PatientExpInfo
     */
    PatientExpInfoVo selectIdByPatientId(@Param ("id") Integer id);
}