package com.yunya.feign.patient_central.domain.query;

import com.yunya.feign.patient_central.domain.model.PrepaidMeturnRecordModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 退费记录QueryForm
 *
 * @author: WY
 * @date 2020/8/26 15:23
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "退费记录")
public class MemberReturnRecordQueryForm implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     *  会员卡
     */
    @ApiModelProperty(value = "会员卡号",required = true)
    private String cardNumber;

    /**
     *  门诊id
     */
    @ApiModelProperty(value = "门诊id",required = false)
    private Integer orgId;
}
