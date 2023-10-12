package com.yunya.feign.treatment_other.domain.form;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/5 17:39
 * @description: 全程医疗-医嘱项状态变更入参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱项状态变更入参模型")
public class QcAdviceItemStatusForm implements Serializable {

    /** 平台医嘱流水号 */
    @JSONField(name = "Mall_order_no")
    @ApiModelProperty("平台医嘱流水号")
    private String Mall_order_no;

    /** 医疗机构医嘱流水号 */
    @JSONField(name = "Org_order_no")
    @ApiModelProperty("医疗机构医嘱流水号")
    private String Org_order_no = StringHelper.EMPTY;

    /** 核销码 */
    @JSONField(name = "VerifCode")
    @ApiModelProperty("核销码")
    private String VerifCode;
    
    /** 备注 */
    @JSONField(name = "Remark")
    @ApiModelProperty("备注")
    private String Remark;
    
    /** 医嘱状态：1-核实，2-作废，4-停止，6-执行，12-撤销 */
    @JSONField(name = "Status")
    @ApiModelProperty("医嘱状态：1-核实，2-作废，4-停止，6-执行，12-撤销")
    private String Status;
    
    /** 强制标志：Y, N */
    @JSONField(name = "ForceFlag")
    @ApiModelProperty("强制标志：Y, N")
    private String ForceFlag = StringHelper.EMPTY;
    
    /** 医疗机构代码：20-乾元门诊, 2-全程医疗 */
    @JSONField(name = "Org_Code")
    @ApiModelProperty("医疗机构代码：20-乾元门诊, 2-全程医疗")
    private String Org_Code = "20";
}
