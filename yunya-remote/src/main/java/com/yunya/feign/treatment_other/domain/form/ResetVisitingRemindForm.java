package com.yunya.feign.treatment_other.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Program: yunya-dental
 * @Description: 批量修改提醒表单参数
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2022-02-17 17:55
 **/
@ApiModel("批量修改提醒表单参数")
@Data
public class ResetVisitingRemindForm implements Serializable {
    /**
     * 提醒日期
     */
    @ApiModelProperty(value = "提醒日期", required = true)
    @NotNull(message = "提醒日期不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date remindDate;

    /**
     * 提醒时间
     */
    @ApiModelProperty(value = "提醒时间", required = true)
    @NotNull(message = "提醒时间不能为空！")
    private String remindTime;

    /**
     * 提醒ID
     */
    @ApiModelProperty(value = "提醒ID", required = true)
    @NotNull(message = "请选中要修改的项目")
    private List<Integer> ids;
}
