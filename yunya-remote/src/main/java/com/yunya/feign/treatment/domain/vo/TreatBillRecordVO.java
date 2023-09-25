package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2023/9/21 15:30
 * @description: 就诊账单数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("就诊账单数据模型")
public class TreatBillRecordVO implements Serializable {

    /** 账单id */
    @ApiModelProperty("账单id")
    private Integer billId;

    /** 账单编号 */
    @ApiModelProperty("账单编号")
    private String billNum;

    /** 账单日期 */
    @ApiModelProperty("账单日期")
    private Date billDate;

    /** 收费日期 */
    @ApiModelProperty("收费日期")
    private Date payDate;
}
