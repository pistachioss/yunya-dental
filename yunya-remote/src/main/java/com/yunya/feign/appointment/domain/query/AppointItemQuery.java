package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.annotation.Target;

/**
 * 预约项目检索
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 11:53
 * @update yunya-lihuibin    2020-07-31    新建
 */
@ApiModel(value = "预约项目检索")
@Data
@ToString
public class AppointItemQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 开启按名字模糊查询（默认开启） */
    @ApiModelProperty(value = "开启按名字模糊查询（默认开启）")
    private Boolean fuzzyQuery = true;

    /** 预约项目类型id */
    @ApiModelProperty(value = "预约项目类型id")
    private Integer appointTypeId;

    /** 预约项目名称（模糊查询用） */
    @ApiModelProperty(value = "预约项目名称（模糊查询用）")
    private String name;

    @ApiModelProperty(value = "是否启用、是否有效，默认有效；1-有效，0-无效")
    private Byte inservice = 1;

    @ApiModelProperty(value = "组织ID")
    private Integer orgId;
}
