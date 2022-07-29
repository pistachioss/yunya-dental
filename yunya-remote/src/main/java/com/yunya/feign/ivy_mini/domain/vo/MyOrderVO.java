package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/6/30 10:41
 **/
@Data
@ApiModel(description = "我的订单列表")
public class MyOrderVO {
    private List<OrderFrontVO> list;
}
