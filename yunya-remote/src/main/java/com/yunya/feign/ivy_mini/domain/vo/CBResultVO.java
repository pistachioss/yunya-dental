package com.yunya.feign.ivy_mini.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
public class CBResultVO {
    private boolean success;
    @JSONField(name = "error_code")
    private String errorCode;
    @JSONField(name = "error_msg")
    private String errorMsg;
}
