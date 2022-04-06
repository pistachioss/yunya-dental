package com.yunya.feign.employee_attend.form;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：消息推送记录查询模型
 *
 * @author: chenlin
 * @Description: 消息推送记录查询模型
 * @Date: 2022/4/2 18:55
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("消息推送记录查询模型")
public class EmployeePushMessageRecordQueryForm extends PageQuery implements Serializable {

    @ApiModelProperty("员工id")
    private Integer userId;
}
