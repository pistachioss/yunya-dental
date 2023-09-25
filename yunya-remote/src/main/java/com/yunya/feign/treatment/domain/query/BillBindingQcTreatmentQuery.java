package com.yunya.feign.treatment.domain.query;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/21 15:06
 * @description: 绑定全程就诊的账单查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("绑定全程就诊的账单查询模型")
public class BillBindingQcTreatmentQuery extends DateRangeQueryForm {
    
    /** 患者姓名、手机号 */
    @ApiModelProperty("患者姓名、手机号")
    private String search;
    
    /** 账单id列表 */
    @ApiModelProperty(value = "账单id列表", hidden = true)
    private List<Integer> billIds;
}
