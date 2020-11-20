package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class LeaveInfoForm {
    @Id
    private Integer id;
    @ApiModelProperty("user_id")
    private Integer userId;

    @ApiModelProperty("门诊ID")
    private Integer companyId;
    /**
     * 假期类型id
     */
    @ApiModelProperty("假期类型id")
    private Integer vacationId;

    /**
     * 开始时间
     */
    @ApiModelProperty("start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    /**
     * 请假原因
     */
    @ApiModelProperty("请假原因")
    private String leaveReason;

    /**
     * 图片（用逗号隔开）
     */
    @ApiModelProperty("图片（用逗号隔开）")
    private String leavePicture;

    /**
     * 审批条件表id
     */
    @ApiModelProperty("审批条件表id")
    private Integer approvalCriteriaId;

    /**
     * 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    @ApiModelProperty("审批状态 0 审批中 1通过 2拒绝 3撤回")
    private Integer apprpvalStatus;

    /**
     * 创建人
     */
    @ApiModelProperty("crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty("crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @ApiModelProperty("upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @ApiModelProperty("upd_time")
    private Date updTime;
}
