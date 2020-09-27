package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.PatientImg;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author YK
 */
@Repository
public interface PatientImgMapper extends Mapper<PatientImg> {
    /**
     * 根据患者id查询患者照片对象
     * @param takePhotosPatientId 患者id
     * @return PatientImg
     */
    PatientImg selectByPatient(@Param("patientId") int takePhotosPatientId);


    /**
     * 根据FaceImgId查询患者照片信息
     * @param faceId 照片id
     * @return PatientImg
     */
    PatientImg selectByFaceId(@Param("faceId") String faceId);

    /**
     * 根据照片id 清空照片信息
     * @param faceColumn 第一张照片
     * @param faceIdColumn 照片id
     * @param id 照片表主键
     */
    void updateFaceImg(@Param("faceColumn") String faceColumn,@Param("faceIdColumn") String faceIdColumn,@Param("id") Integer id);
}