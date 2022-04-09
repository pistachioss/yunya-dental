package com.yunya.feign.report.domain.query.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2021/12/30
 * @description:
 */
@Data
@ToString
public class FuchaForm implements Serializable {
    @ApiModelProperty(value = "是否分页")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;
    @ApiModelProperty("距离末诊时长")
    private Integer days;
    @ApiModelProperty(value = "初复诊（0：初,1：复）", required = false)
    /** 出复诊类型 */
    private List<Integer> treatTypes;
    @ApiModelProperty(value = "末次接诊医生ID列表", required = false)
    /** 接诊医生 */
    private List<Integer> attendingDoctors;
    @ApiModelProperty(value = "患者条件", required = false)
    /** 患者条件 */
    private String combination;
    @ApiModelProperty(value = "门诊id", required = false)
    private List<Integer> orgIds;

    /** 距离末诊的查询方式：0-在范围之内，1-在范围之外*/
    @ApiModelProperty(value = "距离末诊的查询方式：0-在范围之内，1-在范围之外", required = true)
    private Integer dayOps = 0;
}
