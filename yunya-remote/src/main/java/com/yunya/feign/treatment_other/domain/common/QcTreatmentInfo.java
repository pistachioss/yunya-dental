package com.yunya.feign.treatment_other.domain.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/11 10:43
 * @description: 全程医疗-就诊信息模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-就诊信息模型")
public class QcTreatmentInfo implements Serializable {

    /** 就诊日期 */
    @ApiModelProperty("就诊日期")
    private String AdmDate_Html;

    /** 就诊时间 */
    @ApiModelProperty("就诊时间")
    private String AdmTime_Html;

    /** 就诊类型：H-体检，O-门诊 */
    @ApiModelProperty("就诊类型：H-体检，O-门诊")
    private String PAADM_Type;

    /** 就诊类型描述 */
    private String TypeDisplay;

    /** 就诊科室ID：2-全程医疗全科门诊，3-艾维口腔门诊 */
    @ApiModelProperty("就诊科室ID：2-全程医疗全科门诊，3-艾维口腔门诊")
    private String PAADM_DepCode_DR;

    /** 就诊科室描述 */
    @ApiModelProperty("就诊科室描述")
    private String DepCode_Desc;

    /** 就诊医生 */
    @ApiModelProperty("就诊医生")
    private String AdmDocCodeDesc;

    /** 预约标志: Y, N */
    @ApiModelProperty("预约标志：Y, N")
    private String AppFlag;

    /** 核销码 */
    @ApiModelProperty("核销码")
    private String VerifCode;

    /** 医疗机构代码：HZAWQYKQMZBYXGS-乾元门诊, HZQCYL-全程医疗 */
    @ApiModelProperty("医疗机构代码：HZAWQYKQMZBYXGS-乾元门诊, HZQCYL-全程医疗")
    private String Org_Code = "HZAWQYKQMZBYXGS";
}
