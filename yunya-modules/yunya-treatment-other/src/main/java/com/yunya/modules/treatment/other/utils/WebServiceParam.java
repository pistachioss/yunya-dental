package com.yunya.modules.treatment.other.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.XMLType;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/18 14:55
 * @description: WebService入参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("WebService入参模型")
public class WebServiceParam implements Serializable {

    /** 入参名称 */
    @ApiModelProperty("入参名称")
    private String inName;

    /** 入参类型 */
    @ApiModelProperty("入参名称")
    private QName inType = XMLType.XSD_STRING;

    /** 数据 */
    @ApiModelProperty("数据")
    private Object data;
}
