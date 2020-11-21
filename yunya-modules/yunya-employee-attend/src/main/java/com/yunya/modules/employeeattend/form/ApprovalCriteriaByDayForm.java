package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class ApprovalCriteriaByDayForm {
    /**
     * 请假开始日期
     */
    @ApiModelProperty("请假开始日期")
    @NotNull(message = "请假开始日期不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDay;

    /**
     * 请假结束日期
     */
    @ApiModelProperty("请假结束日期")
    @NotNull(message = "请假结束日期不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDay;
}
