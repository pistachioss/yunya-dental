package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2023/9/20 10:19
 * @description: 全程医疗-客户基本信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-客户就诊信息数据模型")
public class QcCustomerTreatmentVO extends QcRecommondInfoVO {

    /** 性别id: 1-男，2-女，3-未知性别，4-未说明性别 */
    @ApiModelProperty("性别id: 1-男，2-女，3-未知性别，4-未说明性别")
    private Byte sex;

    /** 核销码 */
    @ApiModelProperty("核销码")
    private String verifyCode;

    /** 全程收费时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("全程收费时间")
    private Date admDate;

    /** 收费合计 */
    @ApiModelProperty("收费合计")
    private BigDecimal receivedTotalAmount;

    /** 医嘱执行 */
    @ApiModelProperty("医嘱执行")
    private BigDecimal adviceItemExecAmount;

    /** 艾维开单 */
    @ApiModelProperty("艾维开单")
    private BigDecimal orderItemExecAmount;

    /** 同步时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("同步时间")
    private Date syncTime;
}
