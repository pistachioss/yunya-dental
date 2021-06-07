package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@ApiModel("会员统计查询条件封装")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class VipLogoQueryForm extends PageQuery implements Serializable {

    @ApiModelProperty(value = "患者条件", required = true)
    private String combination;

    @ApiModelProperty(value = "末次就诊门诊ID", required = true)
    private Integer[] orgIds;

    @ApiModelProperty(value = "本次统计的会员级别", required = true)
    private String[] vipLogos;

    @ApiModelProperty(value = "上一次统计的会员级别", required = true)
    private String[] vipLogoOlds;

    @ApiModelProperty(value = "会员卡名称", required = true)
    private Integer[] memberLevelIds;
}
