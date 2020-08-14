package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 查询患者来源类型
 *
 * @author: WY
 * @date 2020/8/14 17:55
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OriginTypeQueryForm implements Serializable {

    /**
     * 患者来源类型
     */
    @ApiModelProperty(value = "患者来源类型 1.员工 2.老患者 3.活动 4.合作商",required = true)
    private Integer originType;

    /**
     * 患者原来类型id
     */
    @ApiModelProperty(value = "患者来源类型id", required = true)
    private Integer id;


}
