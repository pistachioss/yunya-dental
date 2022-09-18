package com.yunya.feign.ivy_mini.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CBMiniQueryModel extends CBPublicModel{
    @JSONField(name = "cb_order_no")
    private String cbOrderNo;
}
