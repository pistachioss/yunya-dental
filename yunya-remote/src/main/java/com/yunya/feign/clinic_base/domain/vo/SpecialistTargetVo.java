package com.yunya.feign.clinic_base.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "返回专科数量目标分页查询模型")
public class SpecialistTargetVo {

    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "专科项目id",required = true)
    private Integer sspId;
    @ApiModelProperty(value = "目标",required = true)
    private BigDecimal target;
    @ApiModelProperty(value = "完成",required = true)
    private Integer complete;
    @ApiModelProperty(value = "日期",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    //分页查询条件
    @ApiModelProperty(required = true)
    private Integer pageNum;
    @ApiModelProperty(required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

}
