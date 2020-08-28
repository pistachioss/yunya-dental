package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 消费记录查询QueryForm
 *
 * @author: WY
 * @date 2020/8/28 9:12
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class MemberExpendRecordQueryForm implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 会员卡号
     */
    @ApiModelProperty(value = "会员卡号",required = true)
    private String memberId;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;
}
