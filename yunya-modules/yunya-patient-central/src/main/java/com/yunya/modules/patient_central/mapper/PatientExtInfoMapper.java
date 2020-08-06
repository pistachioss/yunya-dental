package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.PatientExtInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientExtInfoMapper extends Mapper<PatientExtInfo> {
    /**
     * 循环添加患者其他信息（标签,疾病史,过敏原）
     * @param addPatientExtInfoList
     */
    void insertPatientExtInfoList(List<PatientExtInfo> addPatientExtInfoList);

    /**
     * 循环更新患者其他信息（标签,疾病史,过敏原）
     * @param updatePatientExtInfoList
     */
    //void updatePatientExtInfoList(List<PatientExtInfo> updatePatientExtInfoList);

    /**
     * 通过患者id查询 其他信息（标签,疾病史,过敏原）
     * @param id
     * @return List<PatientExtInfo>
     */
    List<PatientExtInfo> patientExtInfoListByid(@Param("id") Integer id);

    /**
     * 根据id删除对于的 （标签,疾病史,过敏原） 信息
     *
     * @param id
     */
    int deletePatientExtInfoByPatientId(@Param("id") Integer id);
}