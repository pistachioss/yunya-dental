package com.yunya.feign.report.domain.query.base;

import com.yunya.framework.common.model.PageQuery;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：两个日期范围查询参数模型
 *
 * @author: chenlin
 * @Description: 两个日期范围查询参数模型
 * @Date: 2021/12/10 10:12
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("两个日期范围查询参数模型")
public class DoubleDateRangeQueryForm extends PageQuery implements Serializable {
    /** 第一套时间类型 */
    @ApiModelProperty(value = "第一套时间类型:0-日；1-月；2-年", required = true)
    @NotNull(message = "第一个时间类型不能为空！")
    private Byte dateType;
    /** 第一套查询时间 */
    @ApiModelProperty(value = "第一套查询开始时间", required = true)
    @NotBlank(message = "第一个查询开始时间不能为空！")
    private String startDate1;
    /** 第一套查询结束时间 */
    @ApiModelProperty(value = "第一套查询结束时间", required = true)
    @NotBlank(message = "第一个查询结束时间不能为空！")
    private String endDate1;
    /** 第一套查询时间 */
    @ApiModelProperty(value = "第一套查询开始时间数字形式：yyyyMMdd")
    private Integer sDateInt1;
    /** 第一套查询结束时间 */
    @ApiModelProperty(value = "第一套查询结束时间数字形式：yyyyMMdd")
    private Integer eDateInt1;


    public void setStartDate1(String startDate1) {
        if (StringHelper.isNotEmpty(startDate1)) {
            this.sDateInt1 = DateUtil.startDate2Number(startDate1);
        }
        this.startDate1 = startDate1;
    }

    public void setEndDate1(String endDate1) {
        if (StringHelper.isNotEmpty(endDate1)) {
            this.eDateInt1 = DateUtil.endDate2Number(endDate1);
        }
        this.endDate1 = endDate1;
    }

    /** 第二套查询时间 */
    @ApiModelProperty(value = "第二套查询开始时间", required = true)
    @NotBlank(message = "第二个查询开始时间不能为空！")
    private String startDate2;
    /** 第二套查询结束时间 */
    @ApiModelProperty(value = "第二套查询结束时间", required = true)
    @NotBlank(message = "第二个查询结束时间不能为空！")
    private String endDate2;
    /** 第二套查询时间 */
    @ApiModelProperty(value = "第二套查询开始时间数字形式：yyyyMMdd")
    private Integer sDateInt2;
    /** 第二套查询结束时间 */
    @ApiModelProperty(value = "第二套查询结束时间数字形式：yyyyMMdd")
    private Integer eDateInt2;



    public void setStartDate2(String startDate2) {
        if (StringHelper.isNotEmpty(startDate2)) {
            this.sDateInt2 = DateUtil.startDate2Number(startDate2);
        }
        this.startDate2 = startDate2;
    }

    public void setEndDate2(String endDate2) {
        if (StringHelper.isNotEmpty(endDate2)) {
            this.eDateInt2 = DateUtil.endDate2Number(endDate2);
        }
        this.endDate2 = endDate2;
    }
}
