package com.yunya.feign.emr.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/8/6
 */
@Setter
@Getter
@ApiModel(value = "病例审核查询模型")
public class MedicalApproveQuery {

    @ApiModelProperty(value = "查询关键字")
    private String keyword;

    @ApiModelProperty(value = "草稿提交时间")
    private String submitTime;

    private Integer pageNum;

    private Integer pageSize;
}
