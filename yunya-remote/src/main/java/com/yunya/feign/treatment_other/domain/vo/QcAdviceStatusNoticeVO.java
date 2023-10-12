package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.feign.treatment_other.domain.common.QcAdviceItemStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/6 9:23
 * @description: 全程医疗-医嘱状态变更通知出参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱状态变更通知出参模型")
public class QcAdviceStatusNoticeVO extends QcResult {

    /** 医嘱项状态 */
    @ApiModelProperty("医嘱项状态")
    private List<QcAdviceItemStatus> order_infos;
}
