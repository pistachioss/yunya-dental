package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 设备信息QueryForm
 *
 * @author: WY
 * @date 2020/9/2 16:34
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class EquipmentInfoQueryForm implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * id主键
     */
    private String id;

    /**
     * 设备ip地址
     */
    private String ip;

    /**
     * 设备SN号(序列号)
     */
    private String serialNumber;

    /**
     * 设备密码
     */
    private String pass;
}
