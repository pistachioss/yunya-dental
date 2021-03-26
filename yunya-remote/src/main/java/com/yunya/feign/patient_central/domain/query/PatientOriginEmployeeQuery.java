package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 员工推荐查询模板
 *
 * @author: WY
 * @date: 2021/3/19 17:15
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工推荐列表查询模板")
public class PatientOriginEmployeeQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间", example = "yyyy-MM-dd")
    private String startDate;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间", example = "yyyy-MM-dd")
    @NotNull(message = "结束时间不能为空！")
    private String endDate;

    /**
     * 推荐人姓名
     */
    @ApiModelProperty(value ="推荐人姓名",required = false)
    private String referrerName;

    /**
     * 就职状态
     */
    @ApiModelProperty(value ="就职状态",required = false)
    private String workStatus;

    /**
     * 门诊id
     */
    @ApiModelProperty(value = "门诊ID(可多选)")
    private List<Integer> orgIds;


}