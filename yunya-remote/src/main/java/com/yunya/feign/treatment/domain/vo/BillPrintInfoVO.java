package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 账单打印信息VO
 * @author: LHB
 * @create: 2020-11-24 12:46
 **/
@ApiModel(value = "BillPrintInfoVO",description = "账单打印信息VO")
@Data
public class BillPrintInfoVO implements Serializable {
    @ApiModelProperty("本单优惠总额")
    private BigDecimal totalPrivilegeAmount;
    @ApiModelProperty("应收金额（消费总额）")
    private BigDecimal totalReceivableAmount;
    @ApiModelProperty("实际应收金额")
    private BigDecimal totalActualReceivableAmount;
    @ApiModelProperty("已收金额（本单收费总额）")
    private BigDecimal totalReceivedAmount;
    @ApiModelProperty("欠费金额（本单欠费）")
    private BigDecimal totalDebtAmount;
    @ApiModelProperty("账单明细打印列表")
    List<BillDetailPrintInfoVO> billDetail;
}
