package com.yunya.feign.treatment_other.domain.query;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.framework.common.utils.StringHelper;
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
    private String verifyCode = StringHelper.EMPTY;

    /** 推荐类型：O-医嘱单，L-引导单 */
    @ApiModelProperty("推荐类型：O-医嘱单，L-引导单")
    private String type;
    
    /** 状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传） */
    @ApiModelProperty("状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传）")
    private Integer status;
}
