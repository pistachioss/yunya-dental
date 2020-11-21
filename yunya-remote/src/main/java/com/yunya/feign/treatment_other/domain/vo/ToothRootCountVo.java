package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 牙位根尖片数量(APP用)
 * @author: LHB
 * @create: 2020-11-20 14:11
 **/
@ApiModel(value = "ToothRootCountVo",description = "牙位根尖片数量(APP用)")
@Data
public class ToothRootCountVo implements Serializable {
    @ApiModelProperty("牙位编号")
    private Integer toothNo;
    @ApiModelProperty("牙位对应牙根尖片数量")
    private Integer count;
}
