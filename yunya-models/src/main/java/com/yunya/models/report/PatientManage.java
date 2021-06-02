package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "patient_manage")
@Data
public class PatientManage {
    @Id
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 患者类型
     */
    @Column(name = "patient_type")
    private String patientType;

    /**
     * 会员卡级别id
     */
    @Column(name = "member_level_id")
    private Integer memberLevelId;

    /**
     * 会员卡级别名称(金藤卡，银藤卡)
     */
    @Column(name = "member_level_name")
    private String memberLevelName;

    /**
     * 会员卡余额
     */
    @Column(name = "member_balance")
    private BigDecimal memberBalance;

    /**
     * 预付款余额
     */
    @Column(name = "principal_balance")
    private BigDecimal principalBalance;

    /**
     * 累计消费
     */
    @Column(name = "cumulative_consumption")
    private BigDecimal cumulativeConsumption;

    /**
     * 欠费总额
     */
    @Column(name = "total_arrears")
    private BigDecimal totalArrears;

    /**
     * 就诊次数
     */
    @Column(name = "number_of_visits")
    private Integer numberOfVisits;
}