package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/8/31 15:22
 * @description: 全程医疗患者信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗患者信息数据模型")
public class QcPatientInfoVO implements Serializable {

    /** 证件编号 */
    @ApiModelProperty("证件号码")
    private String PAPMI_DVAnumber;
    
    /** 证件类型ID: 20-居民身份证, 23-军官证, 25-港澳居民来往内地通行证, 22-护照, 24-驾驶证, 26-台湾居民来往内地通行证 */
    @ApiModelProperty("证件类型ID: 20-居民身份证, 23-军官证, 25-港澳居民来往内地通行证, 22-护照, 24-驾驶证, 26-台湾居民来往内地通行证")
    private String PAPMI_CardType_DR;
    
    /** 证件类型描述 */
    @ApiModelProperty("证件类型描述")
    private String DVACardType_Desc;

    /** 医保卡卡号 */
    @ApiModelProperty("医保卡卡号")
    private String PAPMI_HealthFundNo;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String PAPMI_Name;

    /** 出生日期 */
    @ApiModelProperty("出生日期")
    private String DOB_Html;

    /** 出生时间 */
    @ApiModelProperty("出生时间")
    private String BirthTime_Html;

    /** 出生地 */
    @ApiModelProperty("出生地")
    private String PAPMI_BirthPlace;

    /** 年龄 */
    @ApiModelProperty("年龄")
    private String Age;

    /** 性别id: 1-男，2-女，3-未知性别，4-未说明性别 */
    @ApiModelProperty("性别id: 1-男，2-女，3-未知性别，4-未说明性别")
    private String PAPMI_Sex_DR;
    
    /** 性别代码 */
    @ApiModelProperty("性别代码")
    private String Sex_Code;
    
    /** 性别描述 */
    @ApiModelProperty("性别描述")
    private String Sex_Desc;

    /** 办公电话 */
    @ApiModelProperty("办公电话")
    private String PAPER_TelO;

    /** 家庭电话 */
    @ApiModelProperty("家庭电话")
    private String PAPER_TelH;

    /** 移动电话 */
    @ApiModelProperty("移动电话")
    private String PAPMI_MobPhone;
}
