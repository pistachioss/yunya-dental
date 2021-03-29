package com.yunya.report.ultimate.mapper;

import com.yunya.feign.patient_central.domain.query.PatientOriginActivityQuery;
import com.yunya.feign.patient_central.domain.query.PatientOriginEmployeeQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginActivityVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginEmployeeVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedTotalWorkloadVo;
import com.yunya.models.report.BasePatientOriginLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author YK
 */
public interface BasePatientOriginLogMapper extends Mapper<BasePatientOriginLog> {


    /**
     * 查询员工推荐患者人数信息
     * @param query 条件
     * @return  查询推荐人信息以及推荐数量
     */
    List<PatientOriginEmployeeVo> findEmployeeVoLists(@Param("query") PatientOriginEmployeeQuery query);

    /**
     * 查询订单项目实收计算
     * @param originId 条件
     * @return 订单项目实收计算信息列表
     */
    List<ReceivedTotalWorkloadVo> findReceivedTotalWorkload(@Param("originId") Integer originId);

    /**
     * 查询活动推荐患者人数信息
     * @param query 条件
     * @return  查询活动信息以及推荐数量
     */
    List<PatientOriginActivityVo> findActivityVoLists(@Param("query") PatientOriginActivityQuery query);
}