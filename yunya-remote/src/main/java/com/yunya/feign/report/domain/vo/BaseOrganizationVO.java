package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：组织信息VO
 *
 * @author: chenlin
 * @Description: 组织信息VO
 * @Date: 2021/12/17 10:29
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("组织信息VO")
public class BaseOrganizationVO implements Serializable {
    /**
     * 组织ID
     */
    @ApiModelProperty("组织ID")
    private Integer orgId;

    /**
     * 组织简称
     */
    @ApiModelProperty("组织简称")
    private String abbreviation;

    /**
     * 上级组织ID
     */
    @ApiModelProperty("上级组织ID")
    private Integer parentId;

    /**
     * 上级组织简称
     */
    @ApiModelProperty("上级组织简称")
    private String parentName;

    /**
     * 组织类型（0-公司；1-区域；2-医疗机构；3-其他）
     */
    @ApiModelProperty("组织类型（0-公司；1-区域；2-医疗机构；3-其他）")
    private String orgType;
}
