package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：治疗计划详情核销添加模型
 *
 * @author: chenlin
 * @Description: 治疗计划详情核销添加模型
 * @Date: 2022/1/17 14:33
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划详情核销添加模型")
public class TreatPlanDetailWriteoffModel implements Serializable {

    @ApiModelProperty("需要删除的订单明细id列表")
    private List<Integer> deletedOrderDetailIds;

    private List<TreatPlanDetailWriteoffInfoModel> writeoffInfoModels;
}
