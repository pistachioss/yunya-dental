package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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
@ApiModel("个人初诊记录查询参数封装")
@Data
@ToString
public class FirstVisitPersonalQuery  extends PageQuery implements Serializable {

    /** 门诊ID */
    @ApiModelProperty(value = "门诊ID", required = true)
    private Integer orgId;

    /** 查询初诊开始时间 */
    @ApiModelProperty(value = "初诊开始时间", example = "2020-01-01", required = true)
    private String startDate;

    /** 查询就诊结束时间 */
    @ApiModelProperty(value = "初诊结束时间", example = "2021-01-01", required = true)
    private String endDate;
    /** 医生Id */
    @ApiModelProperty(value = "医生Id 后端使用 前端不传")
    private Integer userId;
}
