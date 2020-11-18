package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.security.MessageDigest;
import java.util.Date;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "PhotoServiceQuery",description = "查询牙周期列表模型")
@Data
@ToString
public class PhotoServiceQuery {
    @ApiModelProperty(value = "是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第1页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量，默认显示10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;
    @ApiModelProperty(value = "患者id")
    @NotNull(message = "患者ID不能为空")
    private Integer patientId;
    @ApiModelProperty(value = "图片类型(0=照片,1=根尖片,2=全景片,3=正位片,4=侧位片,5=关节片,6=正畸片,7=其他片)")
    @NotNull(message = "图片类型不能为空")
    private Integer photoType;





}