package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/11 15:22
 * @description: 全程医疗-mall推荐信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-mall推荐信息数据模型")
public class QcRecommondInfoVO implements Serializable {

    /** 登记号 */
    @ApiModelProperty("登记号")
    private String admNo;
    
    /** 姓名 */
    @ApiModelProperty("姓名")
    private String customerName;

    /** 身份证号 */
    @ApiModelProperty("身份证号")
    private String idCard;

    /** 手机号 */
    @ApiModelProperty("手机号")
    private String mobile;

    /** 推荐类型：1-医嘱单，2-引导单 */
    @ApiModelProperty("推荐类型：1-医嘱单，2-引导单")
    private Integer type;

    /** 全程收费时间 */
    @ApiModelProperty("全程收费时间")
    private String qcAdmDate;

    /** 医嘱备注 */
    @ApiModelProperty("医嘱备注")
    private String admRemark;
    
    /** 绑定患者id */
    @ApiModelProperty("绑定患者id")
    private Integer patientId;
    
    /** 绑定患者姓名 */
    @ApiModelProperty("绑定患者姓名")
    private String patientName;

    /** 绑定订单 */
    private String billNum;
}
