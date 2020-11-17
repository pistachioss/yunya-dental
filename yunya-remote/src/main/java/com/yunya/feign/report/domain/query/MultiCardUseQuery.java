package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/11/16
 */
@Data
@ApiModel(value = "患者档案产品管理使用记录（兑换券、套餐券）参数")
public class MultiCardUseQuery extends PageQuery {
}
