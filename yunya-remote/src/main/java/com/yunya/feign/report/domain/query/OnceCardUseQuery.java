package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2020/11/16
 */
@Data
@ApiModel(value = "患者档案产品管理使用记录（代金券、折扣券）参数")
public class OnceCardUseQuery extends PageQuery {
}
