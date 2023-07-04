package com.yunya.feign.treatment.domain.query;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/4/26 9:34
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel
public class BillPayShareDetailQuery extends DateRangeQueryForm {

    /** 订单id */
    @ApiModelProperty(value = "订单id")
    private Integer orderRecordId;
    /** 收费id */
    @ApiModelProperty("收费id")
    private Integer billPayId;
}
