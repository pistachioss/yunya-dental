package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: App端个人就诊详细信息视图模型
 * @author: LHB
 * @create: 2020-12-16 09:54
 **/
@Data
@ApiModel(value = "TreatmentInfo4App", description = "App端个人就诊详细信息视图模型")
public class TreatmentInfo4AppVO implements Serializable {

    /** 预约信息 */
    @ApiModelProperty("预约信息")
    private TreatmentAppointInfo4AppVO appintInfo;
    /** 挂号信息 */
    @ApiModelProperty("挂号信息")
    private TreatmentRegInfo4AppVO regInfo;
    /** 账单信息 */
    @ApiModelProperty("账单信息")
    private TreatmentOrderInfo4AppVO orderInfo;

}
