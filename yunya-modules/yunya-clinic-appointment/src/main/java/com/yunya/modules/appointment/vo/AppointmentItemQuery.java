package com.yunya.modules.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 查询门诊可预约项目参数封装
 *
 * @author yunya
 * @create 2020-07-21 17:42
 * @update yunya    2020-07-21    新建
 */
@Data
@ToString
@ApiModel(value = "查询门诊可预约项目参数封装")
public class AppointmentItemQuery implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "组织id")
    private String compClinId;

    @ApiModelProperty("预约类型ID")
    private Integer appointTypeId;
}
