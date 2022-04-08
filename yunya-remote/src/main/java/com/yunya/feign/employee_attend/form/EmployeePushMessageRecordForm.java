package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：消息推送记录修改模型
 *
 * @author: chenlin
 * @Description: 消息推送记录修改模型
 * @Date: 2022/4/2 18:55
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("消息推送记录修改模型")
public class EmployeePushMessageRecordForm implements Serializable {

    /** 数据业务id */
    @ApiModelProperty(value = "数据业务id", required = true)
    @NotNull(message = "数据业务id不能为空")
    private Integer dataId;

    /** 消息类型: 1-考勤打卡，10-请假审批，20-加班审批，30-外勤审批*/
    @ApiModelProperty(value = "消息类型: 1-考勤打卡，10-请假审批，20-加班审批，30-外勤审批", required = true)
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;
}
