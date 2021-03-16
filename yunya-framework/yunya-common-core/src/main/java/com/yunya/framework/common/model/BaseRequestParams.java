package com.yunya.framework.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 公共请求字段
 * @author: LHB
 * @create: 2021-03-15 13:23
 **/
@Data
public class BaseRequestParams implements Serializable {
    /**
     * APP端审核状态
     */
    private Boolean audit;

    public Boolean getAudit() {
        return audit;
    }

    public void setAudit(Boolean audit) {
        this.audit = audit;
    }
}
