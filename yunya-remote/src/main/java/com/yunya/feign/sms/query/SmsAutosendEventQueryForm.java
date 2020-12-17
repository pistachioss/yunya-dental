package com.yunya.feign.sms.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介：短信自动发送查询模型
 *
 * @author: chenlin
 * @Description: 短信自动发送查询模型
 * @Date: 2020/12/16 12:54
 * @since: 1.0.0
 */
@ApiModel("短信自动发送查询模型")
@ToString
@Data
public class SmsAutosendEventQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 组织id
     */
    @ApiModelProperty("组织id")
    private Integer orgId;

    /**
     * 事件编码
     */
    @ApiModelProperty("事件编码")
    private String eventCode;

    /**
     * 事件所对应的业务表的主键id
     */
    @ApiModelProperty("事件所对应的业务表的主键id")
    private Integer bizPid;

    /**
     * 短信模板id
     */
    @ApiModelProperty("短信模板id")
    private Integer templateId;

    /**
     * 状态：0-关闭，1-开启
     */
    @ApiModelProperty("状态：0-关闭，1-开启")
    private Byte status;
}
