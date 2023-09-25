package com.yunya.feign.treatment_other.domain.form;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunya.feign.treatment_other.domain.common.QcAdviceItemInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/9/11 10:51
 * @description: 全程医疗-医嘱上传-医嘱项信息模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱上传-医嘱项信息模型")
public class QcAdviceUploadItemForm extends QcAdviceItemInfo {

    /** 医疗机构医嘱流水号 */
    @JSONField(name = "Org_order_no")
    @ApiModelProperty("医疗机构医嘱流水号")
    private String Org_order_no;
}
