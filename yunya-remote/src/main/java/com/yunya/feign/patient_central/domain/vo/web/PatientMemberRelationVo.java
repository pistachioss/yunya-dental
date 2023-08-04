package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 返回会员绑定关系模型
 *
 * @author: WY
 * @date 2020/7/30 15:16
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回会员绑定关系模型")
public class PatientMemberRelationVo implements Serializable {

    /**
     * 绑定关系id
     */
    @ApiModelProperty(value = "绑定关系id")
    private Integer bindId;

    /**
     * 诊所Id
     */
    @ApiModelProperty(value = "诊所Id")
    private Integer orgId;

    /**
     * 关联人名称
     */
    @ApiModelProperty(value = "关联人名称")
    private String name;

    /**
     * 主卡会员人ID
     */
    @ApiModelProperty(value = "主卡会员人ID")
    private Integer masterCardId;
    
    /** 亲密付患者手机号 */
    @ApiModelProperty("亲密付患者手机号")
    private String masterMobile;

    /**
     * 副卡会员人ID
     */
    @ApiModelProperty(value = "副卡会员人ID")
    private Integer secondaryCardId;

    /**
     * 关联类型
     */
    @ApiModelProperty(value = "关联类型")
    private Integer bindType;

    @ApiModelProperty("头像")
    private String faceUrl;

}
