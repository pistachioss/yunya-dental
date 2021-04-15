package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@ApiModel("粉丝绑定患者修改备注")
@Data
public class WxFansUpdateForm {

    @ApiModelProperty("粉丝表Id")
    private Integer id;
    @ApiModelProperty("备注")
    private String remark;

}
