package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：开单项目统计查询参数
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/16 11:14
 * @since: 1.0.0
 */
@ApiModel("开单项目统计查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BillItemInfoQuery extends PageQuery implements Serializable {
    /** 门诊ID列表 */
    @ApiModelProperty(value = "门诊ID列表")
    private Collection<Integer> orgIds;
    /** 门诊ID */
    @ApiModelProperty(value = "门诊ID")
    private Integer orgId;
    /** 时间类型 */
    @ApiModelProperty(value = "时间类型:0-日；1-月；2-年", required = true)
    @NotNull(message = "时间类型不能为空！")
    private Byte dateType;
    /** 查询时间 */
    @ApiModelProperty(value = "查询开始时间", required = true)
    @NotBlank(message = "查询开始时间不能为空！")
    private String startDate;
    /** 查询结束时间 */
    @ApiModelProperty(value = "查询结束时间", required = true)
    @NotBlank(message = "查询结束时间不能为空！")
    private String endDate;
    /** 项目分类ID列表 */
    private Collection<Integer> categoryIds;
    /** 项目类型（0-价目表；1-商品） */
    @ApiModelProperty(value = "项目类型（0-价目表；1-商品）", required = true)
    @NotNull(message = "项目类型不能为空！")
    private Integer itemType;
    /** 项目ID列表 */
    private Collection<Integer> itemIds;
    /** 员工ID列表*/
    @ApiModelProperty(value = "员工ID列表")
    private Collection<Integer> employeeIds;
    /** 就职状态列表*/
    @ApiModelProperty(value = "就职状态列表")
    private Collection<Integer> workStatus;
    /** 分类及项目列表 */
    @ApiModelProperty(value = "分类及项目列表")
    private Collection<Integer[]> categoryItems;
}
