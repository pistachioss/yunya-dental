package com.yunya.feign.treatment.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介: 门诊端转诊记录
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
public class ReferredInfoForm extends PageQuery implements Serializable {

    @ApiModelProperty("患者姓名或手机号")
    private String userName;

    /** 转诊开始时间 */
    @ApiModelProperty("转诊开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /** 转诊结束时间 */
    @ApiModelProperty("转诊结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("转诊医生id集合")
    private List<Integer> userIds;


    @ApiModelProperty("患者id集合(此字段后端使用 前端不用管)")
    private List<Integer> patIds;

    @ApiModelProperty("门诊ID")
    @NotNull(message = "门诊Id不能为空")
    private Integer orgId;


}
