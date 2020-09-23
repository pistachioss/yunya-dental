package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.web.PatientOriginInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginVo;
import com.yunya.models.patient_central.PatientOrigin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface PatientOriginMapper extends Mapper<PatientOrigin> {
    /**
     * 根据父ID查询患者来源
     * @param parentId 患者id
     * @return PatientOrigin
     */
    PatientOrigin findPatientOriginByParentId(@Param("parentId") Integer parentId);

    /**
     * 查询患者来源集合
     * @return  List<PatientOriginInfoVo>
     */
    List<PatientOriginInfoVo> findAll();

    /**
     * 根据患者来源名称查询是否已经存在
     * @param name 来源名称
     * @return PatientOrigin
     */
    PatientOrigin findPatientOriginByName(@Param("name") String name);

    /**
     * 根据患者来源type和 name 模糊查询
     * @param patientOrigin 患者来源信息
     * @return List<PatientOrigin>
     */
    List<PatientOrigin> findPatientOriginByTypt(@Param("from") PatientOrigin patientOrigin);

    /**
     * 查询父级患者来源
     * @return List<PatientOriginVo>
     */
    List<PatientOriginVo> originalType();

    /**
     * 查询患者来源类型
     * @return List<PatientOriginInfoVo>
     */
    List<PatientOriginInfoVo> selectOriginalType();

    /**
     * 查询患者来源type最大值（id最大的）
     * @return
     */
    Integer selectTypeMaximum();

    /**
     * 根据来源type查询
     * @param originType 患者来源type
     * @return PatientOrigin
     */
    PatientOrigin getTypeName(@Param("originType") Integer originType);
}