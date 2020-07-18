package com.yunya.employee.common.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author bruce
 * @date 2020/7/15
 */
@Setter
@Getter
public class BaseEmployeeUpdateReq {

    private Integer employeeId;
    /**
     * 员工基本信息
     */
    private BaseBasicEmployeeReq basicEmployeeReq;
}
