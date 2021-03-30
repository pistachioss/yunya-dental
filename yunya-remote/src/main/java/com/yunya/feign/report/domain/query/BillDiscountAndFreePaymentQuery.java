package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：折扣&免单支付查询参数
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/16 11:14
 * @since: 1.0.0
 */
@ApiModel("折扣&免单支付查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BillDiscountAndFreePaymentQuery extends PageQuery implements Serializable {
    /** 门诊ID列表 */
    @ApiModelProperty(value = "门诊ID列表")
    private Collection<Integer> orgIds;

    /** 查询收费开始日期 */
    @ApiModelProperty(value = "查询收费开始日期", required = true)
    @NotBlank(message = "查询收费开始日期不能为空！")
    private String startDate;

    /** 查询收费结束日期 */
    @ApiModelProperty(value = "查询收费结束日期", required = true)
    @NotBlank(message = "查询收费结束日期不能为空！")
    private String endDate;

    /** 折扣/免单 */
    @ApiModelProperty(value = "折扣/免单")
    private Integer accountType;

    /** 挂号医生ID列表*/
    @ApiModelProperty(value = "挂号医生ID列表")
    private Collection<Integer> employeeIds;
}
