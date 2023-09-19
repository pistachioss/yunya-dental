package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/18 17:45
 * @description: 全程医疗-就诊记录数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-就诊记录数据模型")
public class QcTreatmentVO implements Serializable {

    /** 全程就诊记录id */
    @ApiModelProperty("全程就诊记录id")
    private Integer qcTreatmentId;

    /** 登记号 */
    @ApiModelProperty("登记号")
    private String admNo;

    /** 类型：1-医嘱单，2-引导单 */
    @ApiModelProperty("类型：1-医嘱单，2-引导单")
    private Byte type;

    /** 原价总计 */
    @ApiModelProperty("原价总计")
    private BigDecimal originAmount = BigDecimal.ZERO;

    /** 实收总计 */
    @ApiModelProperty("实收总计")
    private BigDecimal receivedAmount = BigDecimal.ZERO;

    /** 全程医疗医嘱项明细列表 */
    @ApiModelProperty("全程医疗医嘱项明细列表")
    private List<OrderDetailChargeVO> qcAdviceItems;
}
