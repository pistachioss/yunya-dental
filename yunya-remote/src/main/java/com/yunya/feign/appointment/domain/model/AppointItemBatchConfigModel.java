package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约项目统一配置(公司端)
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 11:17
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "预约项目统一配置(公司端)")
@Data
@ToString
public class AppointItemBatchConfigModel implements Serializable {
    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID", required = true)
    @NotNull(message = "预约项目ID")
    private Integer appointItemId;

    /**
     * 公司属性 0 :公司,1:区域管理,2:医疗机构,3:其他
     */
    @ApiModelProperty(value = "公司属性 0 :公司,1:区域管理,2:医疗机构,3:其他", required = true)
    @NotNull(message = "公司属性不能为空！")
    private Byte[] bytes;
}
