package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Class BaseTariffFellowUpRelationVO
 * @Description 基础项目-随访信息返回参数
 * @Author lihuibin
 * @Date 2022/8/20 10:23
 * @Version 1.0
 */
@Data
@ApiModel("基础项目-随访信息返回参数")
public class BaseTariffFellowUpRelationVO implements Serializable {
    /** 主键 */
    @ApiModelProperty("主键")
    private Integer id;

    /** 基础项目ID */
    @ApiModelProperty("基础项目ID")
    private Integer baseTariffId;

    /**
     * 几天后随访
     */
    @ApiModelProperty("几天后随访")
    private Integer fellowUp;

    /** 随访原因 */
    @ApiModelProperty("随访原因")
    private String fellowUpCase;
}

