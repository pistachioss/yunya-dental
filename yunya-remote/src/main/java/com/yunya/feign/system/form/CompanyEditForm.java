package com.yunya.feign.system.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyEditForm {

    private Integer companyId;

    /** 组织统一信用代码 */
    private String creditCode;
}
