package com.yunya.middletable.dao.patient;

import com.yunya.feign.patient_central.domain.model.PatientExtInfoModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientExtInfoVo;
import com.yunya.models.patient_central.PatientExtInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface PatientExtInfoMapper extends Mapper<PatientExtInfo> {

}