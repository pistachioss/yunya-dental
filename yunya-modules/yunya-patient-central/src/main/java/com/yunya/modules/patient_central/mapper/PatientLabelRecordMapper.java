package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.web.PatientLabelRecordVo;
import com.yunya.models.patient_central.PatientLabelRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author YK
 */
@Repository
public interface PatientLabelRecordMapper extends Mapper<PatientLabelRecord> {
    /**
     * 根据患者id 查询标签信息
     * @param patientId 患者id
     * @return List<PatientLabelRecordVo>
     */
    List<PatientLabelRecordVo> selectLabelList(@Param("patientId") Integer patientId);
}