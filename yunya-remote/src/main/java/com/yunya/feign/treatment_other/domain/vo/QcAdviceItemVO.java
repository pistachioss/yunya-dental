package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/5 16:14
 * @description: 全程医疗医嘱信息
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗医嘱项信息")
public class QcAdviceItemVO implements Serializable {

    /** Mall平台唯一流水号 */
    @ApiModelProperty("Mall平台唯一流水号")
    private String Mall_order_no;

    /** 医嘱项代码 */
    @ApiModelProperty("医嘱项代码")
    private String ItmMast_Code;

    /** 医嘱项描述 */
    @ApiModelProperty("医嘱项描述")
    private String ItmMast_Desc;

    /** 数量 */
    @ApiModelProperty("数量")
    private Integer OEORI_QtyPackUOM;

    /** 数量单位描述 */
    @ApiModelProperty("数量单位描述")
    private String PackUOM_Desc;

    /** 单价 */
    @ApiModelProperty("单价")
    private String OEORI_UnitCost;

    /** 总金额 */
    @ApiModelProperty("总金额")
    private String OEORI_Price;

    /** 是否收费：P-已收费，流转类医嘱使用；B-未收费，本院项目 */
    @ApiModelProperty("是否收费：P-已收费，流转类医嘱使用；B-未收费，本院项目")
    private String OEORI_Billed;

    /** 备注 */
    @ApiModelProperty("备注")
    private String OEORI_DepProcNotes;

    /** 接收科室：2-全程医疗全科门诊，3-艾维口腔门诊 */
    @ApiModelProperty("接诊科室：2-全程医疗全科门诊，3-艾维口腔门诊")
    private String OEORI_RecDep_DR;

    /** 接收科室描述 */
    @ApiModelProperty("接收科室描述")
    private String RecDep_Desc;

    /** 处方号 */
    @ApiModelProperty("处方号")
    private String OEORI_PrescNo;

    /** 检验号 */
    @ApiModelProperty("检验号")
    private String OEORI_LabEpisodeNo;

    /** 标本名称 */
    @ApiModelProperty("标本名称")
    private String LabSpec_Name;

    /** 患者所在科室: 2-全程医疗全科门诊，3-艾维口腔门诊 */
    @ApiModelProperty("患者所在科室: 2-全程医疗全科门诊，3-艾维口腔门诊")
    private String OEORI_OrdDept_DR;

    /** 患者所在科室描述 */
    @ApiModelProperty("患者所在科室描述")
    private String OrdDept_Desc;

    /** 开医嘱人姓名 */
    @ApiModelProperty("开医嘱人姓名")
    private String Doctor_Name;

    /** 医嘱开始日期 */
    @ApiModelProperty("医嘱开始日期")
    private String SttDat_Html;

    /** 医嘱结束日期 */
    @ApiModelProperty("医嘱结束日期")
    private String SttTim_Html;

    /** 开立医嘱日期 */
    @ApiModelProperty("开立医嘱日期")
    private String Date_Html;

    /** 开立医嘱时间 */
    @ApiModelProperty("开立医嘱时间")
    private String TimeOrd_Html;

    /** 预约标志: Y, N */
    @ApiModelProperty("预约标志：Y, N")
    private String AppFlag;

    /** 核销码 */
    @ApiModelProperty("核销码")
    private String VerifCode;

    /** 医疗机构代码：HZAWQYKQMZBYXGS-乾元门诊, HZQCYL-全程医疗 */
    @ApiModelProperty("医疗机构代码：HZAWQYKQMZBYXGS-乾元门诊, HZQCYL-全程医疗")
    private String Org_Code;
}
