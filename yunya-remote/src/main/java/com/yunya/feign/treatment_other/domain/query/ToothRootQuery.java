package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 牙根尖片查询参数封装
 * @author: LHB
 * @create: 2020-11-20 10:55
 **/
@ApiModel(value = "ToothRootQuery",description = "牙根尖片查询参数封装")
@Data
public class ToothRootQuery implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty("牙位编号")
    private Integer toothNo;
}
