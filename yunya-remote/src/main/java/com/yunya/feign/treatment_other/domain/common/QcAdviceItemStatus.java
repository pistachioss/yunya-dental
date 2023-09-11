package com.yunya.feign.treatment_other.domain.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/5 17:14
 * @description: 全程医疗-医嘱项状态信息
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱项状态信息")
public class QcAdviceItemStatus implements Serializable {

    /** 平台医嘱流水号 */
    @ApiModelProperty("平台医嘱流水号")
    private String Mall_order_no;
    
    /** 医疗机构医嘱流水号*/
    @ApiModelProperty("医疗机构医嘱流水号")
    private String Org_order_no;

    /** 平台项目id */
    @ApiModelProperty("平台项目id")
    private String ItmMast_rowid;

    /** 医嘱状态：1-核实，2-作废，4-停止，6-执行，12-撤销*/
    @ApiModelProperty("医嘱状态：1-核实，2-作废，4-停止，6-执行，12-撤销")
    private String order_status;
}
