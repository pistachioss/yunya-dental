package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：365卡销售激活统计VO
 *
 * @author: chenlin
 * @Description:365卡销售激活统计VO
 * @Date: 2021/4/8 15:40
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("365卡销售激活统计VO")
public class SaleActivited365CardVO implements Serializable {



    /** 门诊 */
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 销售数量 */
    @Excel(name = "销售数量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("销售数量")
    private Integer saleNum;

    /** 365kids（含第三方渠道售卖） */
    @Excel(name = "365kids（含第三方渠道售卖）")
    @ApiModelProperty("365kids（含第三方渠道售卖）")
    private Long kids365Card;

    /** 356Youngs（含第三方渠道售卖） */
    @Excel(name = "356Youngs（含第三方渠道售卖）")
    @ApiModelProperty("356Youngs（含第三方渠道售卖）")
    private Long youngs365Card;

    /** 356Adults（含第三方渠道售卖） */
    @Excel(name = "356Adults（含第三方渠道售卖）")
    @ApiModelProperty("356Adults（含第三方渠道售卖）")
    private Long adults365Card;
}
