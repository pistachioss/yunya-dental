package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/5 17:38
 * @description: 全程医疗-医嘱状态变更入参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱状态变更入参模型")
public class QcAdviceStatusForm implements Serializable {

    /** 医嘱项状态变更列表 */
    @ApiModelProperty("医嘱项状态变更列表")
    private List<QcAdviceItemStatusForm> order_infos;
}
