package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/4/16 13:25
 **/
@Data
@ApiModel(value = "礼包详情")
public class WxCardUsageVo {
    @ApiModelProperty(value = "产品类型")
    private Integer couponType;
    @ApiModelProperty(value = "有效时间")
    private String effectiveDate;
    @ApiModelProperty(value = "文件类型（0-图片 1-文档）")
    private Integer fileType;
    @ApiModelProperty(value = "文件地址")
    private String path;
    @ApiModelProperty(value = "卡券项目使用情况")
    private List<BenefitItemVo> cardUsageList;
}
