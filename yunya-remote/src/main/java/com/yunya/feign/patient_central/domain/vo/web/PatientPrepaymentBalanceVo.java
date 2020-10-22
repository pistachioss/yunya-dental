package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/10/17 10:52
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class PatientPrepaymentBalanceVo implements Serializable {

    /** 预付款信息 */
    private PatientPrepaymentsInfoVo patientPrepaymentsInfoVo;

    /** 关联预付款账户 */
    private List<PatientPrepaymentsInfoVo>  prepaymentsInfoVoList;
}