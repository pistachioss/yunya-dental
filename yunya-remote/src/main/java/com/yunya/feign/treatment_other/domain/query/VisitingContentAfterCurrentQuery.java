package com.yunya.feign.treatment_other.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 后续随访内容查询参数
 * @author: LHB
 * @create: 2020-08-24 14:59
 **/
@ApiModel(value = "后续随访内容查询参数")
@Data
@ToString
public class VisitingContentAfterCurrentQuery implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空！")
    private Integer patientId;

    /** 随访日期 */
    @ApiModelProperty(value = "随访日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "随访日期不能为空！")
    private Date date;

}
