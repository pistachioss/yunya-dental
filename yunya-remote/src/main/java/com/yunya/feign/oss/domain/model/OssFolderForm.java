package com.yunya.feign.oss.domain.model;

import io.swagger.annotations.*;
import lombok.*;

import java.io.Serializable;


@Data
@ToString
@ApiModel("资源文件基本结构，需以下属性确定路径 参数模型")
public class OssFolderForm implements Serializable {

    @ApiModelProperty(value = "saas模式：注册公司ID，定制模式：为0或固定值", dataType = "int")
    public Integer companyId;

    @ApiModelProperty(value = "资源分类ossCategory", dataType = "int")
    public Integer ossCategory;

    @ApiModelProperty(value = "资源分类类别所对应对象ID", dataType = "int")
    public Integer objectId;
}
