package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.vo.web.PatientPrepaymentsInfoVo;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface PatientPrepaymentsInfoMapper extends Mapper<PatientPrepaymentsInfo> {
    /**
     * 根据患者id患者预付款基本信息查询
     * @param id 患者id
     * @return PatientPrepaymentRelationVo
     */
    PatientPrepaymentsInfoVo findPrepaymentInfo(@Param("id") Integer id);

    /**
     * 根据预付款卡号和患者id查询预付款信息
     * @param prepaidCard 预付款卡号
     * @return PatientPrepaymentsInfo
     */
    PatientPrepaymentsInfo selectOneByPrepaymentNumberAndPatientId(@Param("prepaymentNumber") String prepaidCard);

    /**
     * 根据主卡人id 查询副卡人信息
     * @param id 主卡人id
     * @return  List<PatientPrepaymentsInfoVo
     */
    List<PatientPrepaymentsInfoVo> selectPrepaymentRelationByMasterPatientId(@Param("id") Integer id);


    /**
     * 根据预付款卡号查询预付款信息
     * @param prepaidId 预付款卡号
     * @return PatientPrepaymentsInfo
     */
    PatientPrepaymentsInfo selectOneByCardNumber(@Param("prepaidId") String prepaidId);

}