package com.yunya.feign.patient_central.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author: chenlin
 * @date: 2023/7/18 16:30
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者转账记录查询模型")
public class PatientTransferRecordQuery extends PageQuery {

    /** 查询类型：0-预付款转账会员，1-预付款转账预付款 */
    @ApiModelProperty(value = "查询类型：0-预付款转账会员，1-预付款转账预付款", required = true)
    @NotNull(message = "查询类型不能为空")
    private Integer queryType;

    /** 卡号 */
    @ApiModelProperty(value = "卡号", required = true)
    @NotEmpty(message = "卡号不能为空")
    private String cardNumber;

    /** 类型：1-转入，2-转出 */
    @ApiModelProperty(value = "类型：1-转入，2-转出，查询充值到会员卡需要传2")
    private Byte type;
}
