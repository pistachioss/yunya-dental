package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：产品售卖激活数据统计查询模型
 *
 * @author: chenlin
 * @Description: 产品售卖激活数据统计查询模型
 * @Date: 2022/7/6 10:05
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("产品售卖激活数据统计查询模型")
public class CouponSoldActivedStatisticsQuery extends PageQuery implements Serializable {

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

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String couponName;

    /** 产品类型id */
    @ApiModelProperty("产品类型id")
    private Integer productTypeId;

    /** 销售渠道id */
    @ApiModelProperty("销售渠道id")
    private Integer soldChannelId;
}
