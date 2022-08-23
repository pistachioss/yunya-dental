package com.yunya.feign.ivy_mini.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
public class CBWxPayVO {
    private CBWxPayDataVO data;
    private CBResultVO result;
    private String sign;
}
