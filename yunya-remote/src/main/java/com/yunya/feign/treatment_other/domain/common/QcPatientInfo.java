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
 * @date: 2023/9/11 10:32
 * @description: 全程医疗-患者信息模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-患者信息模型")
public class QcPatientInfo implements Serializable {
    /** 证件编号 */
    @JSONField(name = "PAPMI_DVAnumber")
    @ApiModelProperty("证件号码")
    private String PAPMI_DVAnumber = StringHelper.EMPTY;

    /** 证件类型ID: 20-居民身份证, 23-军官证, 25-港澳居民来往内地通行证, 22-护照, 24-驾驶证, 26-台湾居民来往内地通行证 */
    @JSONField(name = "PAPMI_CardType_DR")
    @ApiModelProperty("证件类型ID: 20-居民身份证, 23-军官证, 25-港澳居民来往内地通行证, 22-护照, 24-驾驶证, 26-台湾居民来往内地通行证")
    private String PAPMI_CardType_DR = StringHelper.EMPTY;

    /** 证件类型描述 */
    @JSONField(name = "DVACardType_Desc")
    @ApiModelProperty("证件类型描述")
    private String DVACardType_Desc = StringHelper.EMPTY;

    /** 医保卡卡号 */
    @JSONField(name = "PAPMI_HealthFundNo")
    @ApiModelProperty("医保卡卡号")
    private String PAPMI_HealthFundNo;

    /** 患者姓名 */
    @JSONField(name = "PAPMI_Name")
    @ApiModelProperty("患者姓名")
    private String PAPMI_Name = StringHelper.EMPTY;

    /** 出生日期 */
    @JSONField(name = "DOB_Html")
    @ApiModelProperty("出生日期")
    private String DOB_Html = StringHelper.EMPTY;

    /** 出生时间 */
    @JSONField(name = "BirthTime_Html")
    @ApiModelProperty("出生时间")
    private String BirthTime_Html;

    /** 出生地 */
    @JSONField(name = "PAPMI_BirthPlace")
    @ApiModelProperty("出生地")
    private String PAPMI_BirthPlace;

    /** 年龄 */
    @JSONField(name = "Age")
    @ApiModelProperty("年龄")
    private String Age = StringHelper.EMPTY;

    /** 性别id: 1-男，2-女，3-未知性别，4-未说明性别 */
    @JSONField(name = "PAPMI_Sex_DR")
    @ApiModelProperty("性别id: 1-男，2-女，3-未知性别，4-未说明性别")
    private String PAPMI_Sex_DR = StringHelper.EMPTY;

    /** 性别代码 */
    @JSONField(name = "Sex_Code")
    @ApiModelProperty("性别代码")
    private String Sex_Code = StringHelper.EMPTY;

    /** 性别描述 */
    @JSONField(name = "Sex_Desc")
    @ApiModelProperty("性别描述")
    private String Sex_Desc = StringHelper.EMPTY;

    /** 办公电话 */
    @JSONField(name = "PAPER_TelO")
    @ApiModelProperty("办公电话")
    private String PAPER_TelO;

    /** 家庭电话 */
    @JSONField(name = "PAPER_TelH")
    @ApiModelProperty("家庭电话")
    private String PAPER_TelH = StringHelper.EMPTY;

    /** 移动电话 */
    @JSONField(name = "PAPMI_MobPhone")
    @ApiModelProperty("移动电话")
    private String PAPMI_MobPhone;

    /** 电子邮箱 */
    @JSONField(name = "PAPMI_Email")
    @ApiModelProperty("电子邮箱")
    private String PAPMI_Email;

    /** 工作单位 */
    @JSONField(name = "PAPMI_SecondPhone")
    @ApiModelProperty("工作单位")
    private String PAPMI_SecondPhone;

    /** 婚姻状况：21-未婚, 22-已婚 */
    @JSONField(name = "PAPER_Marital_DR")
    @ApiModelProperty("婚姻状况：21-未婚, 22-已婚")
    private String PAPER_Marital_DR;

    /** 婚姻状况描述 */
    @JSONField(name = "Marital_Desc")
    @ApiModelProperty("婚姻状况描述")
    private String Marital_Desc;

    /** 民族：1-汉族 */
    @JSONField(name = "PAPER_Nation_DR")
    @ApiModelProperty("民族：1-汉族")
    private String PAPER_Nation_DR = StringHelper.EMPTY;

    /** 民族描述 */
    @JSONField(name = "Nation_Desc")
    @ApiModelProperty("民族描述")
    private String Nation_Desc = StringHelper.EMPTY;

    /** 职业描述 */
    @JSONField(name = "Occupation_Desc")
    @ApiModelProperty("职业描述")
    private String Occupation_Desc;

    /** 国籍or地区：1-汉族 */
    @JSONField(name = "PAPER_Country_DR")
    @ApiModelProperty("国籍or地区：1-汉族")
    private String PAPER_Country_DR;

    /** 国籍or地区描述 */
    @JSONField(name = "Country_Desc")
    @ApiModelProperty("国籍or地区描述")
    private String Country_Desc;

    /** 现住国籍or地区：1-汉族 */
    @JSONField(name = "PAPER_Country_Birth_DR")
    @ApiModelProperty("现住国籍or地区：1-汉族")
    private String PAPER_Country_Birth_DR;

    /** 现住国籍or地区描述 */
    @JSONField(name = "Country_Birth_Desc")
    @ApiModelProperty("现住国籍or地区描述")
    private String Country_Birth_Desc;

    /** 现住省份 */
    @JSONField(name = "PAPMI_CT_Province_DR")
    @ApiModelProperty("现住省份")
    private String PAPMI_CT_Province_DR;

    /** 现住省份描述 */
    @JSONField(name = "CT_Province_Desc")
    @ApiModelProperty("现住省份描述")
    private String CT_Province_Desc;

    /** 现住城市 */
    @JSONField(name = "PAPER_CityCode_DR")
    @ApiModelProperty("现住城市")
    private String PAPER_CityCode_DR;

    /** 现住城市描述 */
    @JSONField(name = "CityCode_Desc")
    @ApiModelProperty("现住城市描述")
    private String CityCode_Desc;

    /** 现住完整地址描述 */
    @JSONField(name = "Address")
    @ApiModelProperty("现住完整地址描述")
    private String Address = StringHelper.EMPTY;

    /** 现住邮编 */
    @JSONField(name = "PostCode")
    @ApiModelProperty("现住邮编")
    private String PostCode;
}
