package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/5 17:39
 * @description: 全程医疗-医嘱信息状态变更
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱信息状态变更")
public class QcAdviceItemStatusForm implements Serializable {

    /** 平台医嘱流水号 */
    @ApiModelProperty("平台医嘱流水号")
    private String Mall_order_no;

    /** 医疗机构医嘱流水号 */
    private String Org_order_no;

    /** 核销码 */
    @ApiModelProperty("核销码")
    private String VerifCode;
    
    /** 备注 */
    @ApiModelProperty("备注")
    private String Remark;
    
    /** 医嘱状态：1-核实，2-作废，4-停止，6-执行，12-撤销 */
    @ApiModelProperty("医嘱状态：1-核实，2-作废，4-停止，6-执行，12-撤销")
    private String status;
    
    /** 强制标志：Y, N */
    @ApiModelProperty("强制标志：Y, N")
    private String ForceFlag;
}
