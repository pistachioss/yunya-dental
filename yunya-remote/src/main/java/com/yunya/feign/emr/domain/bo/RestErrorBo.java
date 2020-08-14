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

    public static RestErrorBo getInstance() {
        RestErrorBo bo = new RestErrorBo();
        return bo;
    }
}
