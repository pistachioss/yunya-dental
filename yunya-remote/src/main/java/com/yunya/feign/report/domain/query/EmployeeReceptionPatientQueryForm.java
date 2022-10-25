package com.yunya.feign.report.domain.query;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2022/10/25 10:46
 * @description: 个人接诊患者查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("个人接诊患者查询模型")
public class EmployeeReceptionPatientQueryForm extends MultiClinicDateRangeQueryForm implements Serializable {
    
    /** 员工id列表 */
    @ApiModelProperty("员工id列表")
    @NotEmpty(message = "员工id列表不能为空")
    private List<Integer> employeeIds;
}
