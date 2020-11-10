package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 订单处理参数模型(门诊管理 - 订单处理)
 * @author: LHB
 * @create: 2020-11-10 09:28
 **/
@ApiModel(value = "OrderProcessQuery",description = "订单处理参数模型(门诊管理 - 订单处理)")
@Data
public class OrderProcessQuery implements Serializable {
    @ApiModelProperty(value = "是否分页,默认true")
    private Boolean whetherPage = true;
    @ApiModelProperty("页码，默认第1页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;
    @ApiModelProperty("每页显示数量，默认显示10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;
    @ApiModelProperty(value = "订单编号")
    private String orderRecordNum;
    @ApiModelProperty(value = "检索关键字 患者姓名/手机号/拼音名字")
    private String search;
    @ApiModelProperty(value = "开单门诊ID集合")
    private Integer[] orgIds;
}
