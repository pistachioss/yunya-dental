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
 * @date: 2023/9/11 10:48
 * @description: 全程医疗-医嘱项信息模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱项信息模型")
public class QcAdviceItemInfo implements Serializable {

    /** Mall平台唯一流水号 */
    @JSONField(name = "Mall_order_no")
    @ApiModelProperty("Mall平台唯一流水号")
    private String Mall_order_no = StringHelper.EMPTY;

    /** 医嘱项代码 */
    @JSONField(name = "ItmMast_Code")
    @ApiModelProperty("医嘱项代码")
    private String ItmMast_Code;

    /** 医嘱项描述 */
    @JSONField(name = "ItmMast_Desc")
    @ApiModelProperty("医嘱项描述")
    private String ItmMast_Desc;

    /** 数量 */
    @JSONField(name = "OEORI_QtyPackUOM")
    @ApiModelProperty("数量")
    private Integer OEORI_QtyPackUOM;

    /** 数量单位描述 */
    @JSONField(name = "PackUOM_Desc")
    @ApiModelProperty("数量单位描述")
    private String PackUOM_Desc;

    /** 单价 */
    @JSONField(name = "OEORI_UnitCost")
    @ApiModelProperty("单价")
    private String OEORI_UnitCost;

    /** 总金额 */
    @JSONField(name = "OEORI_Price")
    @ApiModelProperty("总金额")
    private String OEORI_Price;

    /** 是否收费：P-已收费，流转类医嘱使用；B-未收费，本院项目 */
    @JSONField(name = "OEORI_Billed")
    @ApiModelProperty("是否收费：P-已收费，流转类医嘱使用；B-未收费，本院项目")
    private String OEORI_Billed = "B";

    /** 备注 */
    @JSONField(name = "OEORI_DepProcNotes")
    @ApiModelProperty("备注")
    private String OEORI_DepProcNotes;

    /** 接收科室：2-全程医疗全科门诊，3-艾维口腔门诊 */
    @JSONField(name = "OEORI_RecDep_DR")
    @ApiModelProperty("接收科室：2-全程医疗全科门诊，3-艾维口腔门诊")
    private String OEORI_RecDep_DR = StringHelper.EMPTY;

    /** 接收科室描述 */
    @JSONField(name = "RecDep_Desc")
    @ApiModelProperty("接收科室描述")
    private String RecDep_Desc = StringHelper.EMPTY;

    /** 处方号 */
    @JSONField(name = "OEORI_PrescNo")
    @ApiModelProperty("处方号")
    private String OEORI_PrescNo;

    /** 检验号 */
    @JSONField(name = "OEORI_LabEpisodeNo")
    @ApiModelProperty("检验号")
    private String OEORI_LabEpisodeNo;

    /** 标本名称, 检验项目必填 */
    @JSONField(name = "LabSpec_Name")
    @ApiModelProperty("标本名称, 检验项目必填")
    private String LabSpec_Name;

    /** 患者所在科室描述 */
    @JSONField(name = "OrdDept_Desc")
    @ApiModelProperty("患者所在科室描述")
    private String OrdDept_Desc;

    /** 开医嘱人姓名 */
    @JSONField(name = "Doctor_Name")
    @ApiModelProperty("开医嘱人姓名")
    private String Doctor_Name;

    /** 医嘱开始日期 */
    @JSONField(name = "SttDat_Html")
    @ApiModelProperty("医嘱开始日期")
    private String SttDat_Html;

    /** 医嘱结束日期 */
    @JSONField(name = "SttTim_Html")
    @ApiModelProperty("医嘱结束日期")
    private String SttTim_Html;

    /** 开立医嘱日期 */
    @JSONField(name = "Date_Html")
    @ApiModelProperty("开立医嘱日期")
    private String Date_Html;

    /** 开立医嘱时间 */
    @JSONField(name = "TimeOrd_Html")
    @ApiModelProperty("开立医嘱时间")
    private String TimeOrd_Html;

    /** 预约标志: Y, N */
    @JSONField(name = "AppFlag")
    @ApiModelProperty("预约标志：Y, N")
    private String AppFlag;

    /** 核销码 */
    @JSONField(name = "VerifCode")
    @ApiModelProperty("核销码")
    private String VerifCode;

    /** 医疗机构代码：20-乾元门诊, 2-全程医疗 */
    @JSONField(name = "Org_Code")
    @ApiModelProperty("医疗机构代码：20-乾元门诊, 2-全程医疗")
    private String Org_Code = "20";
}
