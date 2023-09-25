package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/11 11:29
 * @description: 全程医疗-医嘱状态变更通知-医嘱项信息出参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱状态变更通知-医嘱项信息出参模型")
public class QcAdviceItemStatusNoticeVO implements Serializable {
    /** 平台医嘱流水号 */
    @ApiModelProperty("平台医嘱流水号")
    private String Mall_order_no;

    /** 医疗机构医嘱流水号*/
    @ApiModelProperty("医疗机构医嘱流水号")
    private String Org_order_no;
}
