package com.yunya.feign.system.vo;

import com.yunya.feign.patient_central.domain.vo.web.MemberBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientPrepaymentsInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023-02-05 10:14
 * @description: 收费时的入账方式数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("收费时的入账方式数据模型")
public class ClinicChargeItemVO implements Serializable {
    
    /** 其他入账方式 */
    @ApiModelProperty("其他入账方式列表")
    private List<ClinicAccountItemVO> otherAccountItems;
    
    /** 预付款列表 */
    @ApiModelProperty("预付款列表")
    private List<PatientPrepaymentsInfoVo> prepaymentItems;
    
    /** 专项预付款列表 */
    @ApiModelProperty("专项预付款列表")
    private List<PatientPrepaymentsInfoVo> spPrepaymentItems;
    
    /** 会员卡列表 */
    @ApiModelProperty("会员卡列表")
    private List<MemberBaseInfoVo> memberItems;
}
