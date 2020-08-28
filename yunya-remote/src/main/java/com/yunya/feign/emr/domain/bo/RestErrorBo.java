package com.yunya.feign.emr.domain.bo;

import com.yunya.framework.common.model.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/13
 */
@Getter
@Setter
public class RestErrorBo {

    private RestError error;
    private Object[] msg;

    public void setMsg(Object...msg) {
        this.msg = msg;
    }

    public static RestErrorBo getInstance() {
        return new RestErrorBo();
    }
}
