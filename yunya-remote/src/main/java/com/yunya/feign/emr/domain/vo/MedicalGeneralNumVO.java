package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
public class MedicalGeneralNumVO {

    /**
     * 常用词条ID
     */
    @ApiModelProperty("常用词条ID")
    private Integer generalId;

    /**
     * 使用次数
     */
    @ApiModelProperty("使用次数")
    private Integer number;

}