package com.yunya.feign.treatment_other.domain.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunya.framework.common.utils.StringHelper;
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
    /** 平台就诊流水号，引导单模式下必填 */
    @ApiModelProperty("平台就诊流水号，引导单模式下必填")
    private String adm_no;

    /** 就诊日期 */
    @JSONField(name = "AdmDate_Html")
    @ApiModelProperty("就诊日期")
    private String AdmDate_Html = StringHelper.EMPTY;

    /** 就诊时间 */
    @JSONField(name = "AdmTime_Html")
    @ApiModelProperty("就诊时间")
    private String AdmTime_Html = StringHelper.EMPTY;

    /** 就诊类型：H-体检，O-门诊 */
    @JSONField(name = "PAADM_Type")
    @ApiModelProperty("就诊类型：H-体检，O-门诊")
    private String PAADM_Type = StringHelper.EMPTY;

    /** 模式：O-医嘱单，L-引导单 */
    @JSONField(name = "ModeType")
    @ApiModelProperty("模式：O-医嘱单，L-引导单")
    private String ModeType;

    /** 就诊类型描述 */
    @JSONField(name = "TypeDisplay")
    @ApiModelProperty("就诊类型描述")
    private String TypeDisplay;

    /** 就诊科室ID：2-全程医疗全科门诊，3-艾维口腔门诊 */
    @JSONField(name = "PAADM_DepCode_DR")
    @ApiModelProperty("就诊科室ID：2-全程医疗全科门诊，3-艾维口腔门诊")
    private String PAADM_DepCode_DR = StringHelper.EMPTY;

    /** 就诊科室描述 */
    @JSONField(name = "DepCode_Desc")
    @ApiModelProperty("就诊科室描述")
    private String DepCode_Desc = StringHelper.EMPTY;

    /** 就诊医生 */
    @JSONField(name = "AdmDocCodeDesc")
    @ApiModelProperty("就诊医生")
    private String AdmDocCodeDesc;

    /** 预约标志: Y, N */
    @JSONField(name = "AppFlag")
    @ApiModelProperty("预约标志：Y, N")
    private String AppFlag = StringHelper.EMPTY;

    /** 核销码 */
    @JSONField(name = "VerifCode")
    @ApiModelProperty("核销码")
    private String VerifCode = StringHelper.EMPTY;

    /** 诊断建议 */
    @JSONField(name = "Diagnos_Sugg")
    @ApiModelProperty("诊断建议")
    private String Diagnos_Sugg;

    /** 诊疗建议 */
    @JSONField(name = "Diagnos_Adm")
    @ApiModelProperty("诊疗建议")
    private String Diagnos_Adm;
    
    /** 引导单挂号备注 */
    @JSONField(name = "Reg_Notes")
    @ApiModelProperty("引导单挂号备注")
    private String Reg_Notes;

    /** 医疗机构代码：20-乾元门诊, 2-全程医疗 */
    @JSONField(name = "Org_Code")
    @ApiModelProperty("医疗机构代码：20-乾元门诊, 2-全程医疗")
    private String Org_Code;
}
