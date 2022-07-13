package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：365产品售卖明细查询模型
 *
 * @author: chenlin
 * @Description: 365产品售卖明细查询模型
 * @Date: 2022/7/6 10:05
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("365产品售卖明细查询模型")
public class Coupon365SoldDetailQuery extends PageQuery implements Serializable {

    /** 时间类型 */
    @ApiModelProperty(value = "时间类型:0-日；1-月；2-年", required = true)
    @NotNull(message = "时间类型不能为空")
    private Byte dateType = 0;

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期", required = true)
    @NotEmpty(message = "开始日期不能为空")
    private String startDate;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期", required = true)
    @NotEmpty(message = "结束日期不能为空")
    private String endDate;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;

    /** 售卖产品类型id */
    @ApiModelProperty(value = "售卖产品类型id", required = true)
    @NotNull(message = "售卖产品类型id不能为空")
    private Integer productTypeId = 14;

    /** 卡券id列表 */
    @ApiModelProperty(value = "卡券id列表")
    private Collection<Integer> couponIds;

    /** 商品id列表 */
    @ApiModelProperty("商品id列表")
    private Collection<Integer> oralIds;

    /** 售卖类型：1-门诊开单，2-卡券售出 */
    @ApiModelProperty("售卖类型：1-门诊开单，2-卡券售出 ")
    private Byte soldType;

    /** 是否初诊 */
    @ApiModelProperty("是否初诊")
    private Boolean firstVisit;
}
