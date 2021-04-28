package com.yunya.feign.wechat.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/4/19 12:43
 **/
@Data
public class WxTemplateDataVo {
    private String value;
    private String color;

    public WxTemplateDataVo() {
    }

    public WxTemplateDataVo(String value, String color) {
        this.value = value;
        this.color = color;
    }
}
