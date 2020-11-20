package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 牙根尖视图模型
 * @author: LHB
 * @create: 2020-11-19 20:40
 **/
@ApiModel(value = "ToothRootVo",description = "牙根尖视图模型")
@Data
public class ToothRootVo implements Serializable {
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;
    @ApiModelProperty(value = "X-光片类型",
            allowableValues = "0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片")
    private Byte type;
    @ApiModelProperty(value = "牙位编号")
    private Integer toothNo;
    @ApiModelProperty(value = "照片数量")
    private Integer count;

    @ApiModelProperty(value = "牙根尖图片详情列表")
    List<ToothRootDetailVo> toothRootDetails;
}
