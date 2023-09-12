package com.yunya.feign.treatment_other.domain.query;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/9/11 17:41
 * @description: 全程医疗推荐信息查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗推荐信息查询模型")
public class QcRecommondInfoQuery extends DateRangeQueryForm {

    /** 姓名、手机号 */
    @ApiModelProperty("姓名、手机号")
    private String search;

    /** 核销码 */
    @ApiModelProperty("核销码")
    private String verifyCode;

    /** 推荐类型：1-医嘱单，2-引导单 */
    @ApiModelProperty("推荐类型：1-医嘱单，2-引导单")
    private Integer type;
    
    /** 就诊状态： */
    private Integer treatStatus;
}
