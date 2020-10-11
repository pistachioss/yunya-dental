package com.yunya.feign.emr.domain.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/10/10
 */
@Getter
@Setter
public class ChangeCountBo {
    private Integer pendingCount;
    private Integer passCount;
}
