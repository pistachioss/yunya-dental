package com.yunya.modules.patient_central.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.patient_central.PatientExpInfo;
import com.yunya.modules.patient_central.mapper.PatientExpInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简单介绍:</br> 患者信息扩展 业务成
 *
 * @author: WY
 * @date 2020/7/28 13:24
 * @description: 患者信息扩展表（增删查改）
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientExpInfoBiz extends BaseBiz<PatientExpInfoMapper, PatientExpInfo> {

  @Autowired private PatientExpInfoMapper patientExpInfoMappers;
}
