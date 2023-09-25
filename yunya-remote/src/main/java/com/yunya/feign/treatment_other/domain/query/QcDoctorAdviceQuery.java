package com.yunya.feign.treatment_other.domain.query;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/8/31 15:05
 * @description: 全程医疗mall平台医嘱查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗mall平台医嘱查询模型")
public class QcDoctorAdviceQuery implements Serializable {

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期", required = true)
    @NotEmpty(message = "开始日期不能为空")
    private String start_date;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期", required = true)
    @NotEmpty(message = "结束日期不能为空")
    private String end_date;

    /** 平台患者登记号 */
    @ApiModelProperty("平台患者登记号")
    private String patient_no = StringHelper.EMPTY;

    /** 平台就诊流水号 */
    @ApiModelProperty("平台就诊流水号")
    private String adm_no = StringHelper.EMPTY;

    /** 患者证件号码 */
    @ApiModelProperty("患者证件号码")
    private String cred_no = StringHelper.EMPTY;
    
    /** 核销码 */
    @ApiModelProperty("核销码")
    private String werif_code = StringHelper.EMPTY;

    /** 医疗机构代码：20-乾元门诊, 2-全程医疗 */
    @JSONField(name = "Org_Code")
    @ApiModelProperty("医疗机构代码：20-乾元门诊, 2-全程医疗")
    private String Org_Code = "20";
}
