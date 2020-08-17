package com.yunya.feign.patient_central.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/14 15:08
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("会员卡关系删除")
public class CardRelationForm implements Serializable {

    /**
     * 关系id
     */
    @ApiModelProperty(value = "关系id",required = true)
    private Integer id;

    /**
     * 关系类型 会员卡：0 卡余额：1
     */
    private Byte bindType;
}
