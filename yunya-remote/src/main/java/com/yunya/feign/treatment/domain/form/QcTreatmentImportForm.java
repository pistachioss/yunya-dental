package com.yunya.feign.treatment.domain.form;

import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/18 16:30
 * @description: 全程医疗-登记单导入查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-登记单导入查询模型")
public class QcTreatmentImportForm implements Serializable {
    /** 全程就诊记录id列表 */
    @ApiModelProperty(value = "全程就诊记录id列表")
    private List<Integer> qcTreatmentIds;

    /** 订单id */
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull(message = "订单id不能为空")
    private Integer orderRecordId;

    /** 账单id */
    @ApiModelProperty("账单id")
    private Integer billRecordId;

    /** 订单明细列表 */
    @ApiModelProperty(value = "订单明细列表")
    private List<OrderDetailChargeVO> orderDetails;
}
