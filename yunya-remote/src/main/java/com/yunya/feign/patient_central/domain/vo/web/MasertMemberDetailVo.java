package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/14
 * @description:
 */
@ToString
@Data
@ApiModel("小程序返回会员信息参数模型")
public class MasertMemberDetailVo {
    @ApiModelProperty(value = "已绑定主卡信息")
    private List<PatientCardOwnerInfoVo> patientCardOwnerInfoVos;

    @ApiModelProperty(value = "患者会员卡关联关系")
    private MemberRelationVo memberRelationVo;

    @ApiModelProperty(value = "患者会员信息")
    private PatientPublicInfoVo patientPublicInfoVo;

    /** 患者累计信息 */
    @ApiModelProperty("患者累计信息")
    private PatientCumulativeInfoVO cumulativeInfo;
    /**
     *  积分
     */
    @ApiModelProperty(value = "积分")
    private Integer point;
    @ApiModelProperty("患者关系名称 用来判断是否为本人 区分微信拥有者")
    private String dictionaryName;
}
