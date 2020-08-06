package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.PatientOriginInfoVo;
import com.yunya.models.patient_central.PatientOrigin;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientOriginMapper extends Mapper<PatientOrigin> {
    /**
     * 根据父ID查询患者来源
     * @param parentId
     * @return PatientOrigin
     */
    PatientOrigin findPatientOriginByParentId(@Param("parentId") Integer parentId);

    /**
     * 查询患者来源集合
     * @return
     */
    List<PatientOriginInfoVo> findAll();

    /**
     * 根据患者来源名称查询是否已经存在
     * @param name
     * @return
     */
    PatientOrigin findPatientOriginByName(@Param("name") String name);
}