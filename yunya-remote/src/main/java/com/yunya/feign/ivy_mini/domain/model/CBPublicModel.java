package com.yunya.feign.ivy_mini.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
public class CBPublicModel {
    private String command;
    private String app;
    @JSONField(name = "operator_id")
    private String operatorId;
    private String version;
    @JSONField(name = "sign_type")
    private String signType;
    @JSONField(name = "request_id")
    private String requestId;
    @JSONField(name = "request_time")
    private String requestTime;
    private String sign;
}
