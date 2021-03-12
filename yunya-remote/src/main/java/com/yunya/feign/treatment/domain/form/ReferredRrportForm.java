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
public class ReferredRrportForm extends PageQuery implements Serializable {

    @ApiModelProperty("门诊ID")
    private Integer orgId;

    @ApiModelProperty("时间参数类型 0:月 ,1:年")
    @NotNull(message = "时间不能为空！")
    private Integer timeType;

    @ApiModelProperty("月份或年份 月份传yyyy-dd 年份传yyyy")
    @NotNull(message = "时间不能为空！")
    private String time;

    @ApiModelProperty("转诊医生id集合")
    private List<Integer> userIds;

    @ApiModelProperty("转诊医生id集合(此字段后端使用 前端不用管)")
    private List<Integer> userIdsAfter;

    @ApiModelProperty("被转诊医生id集合")
    private List<Integer> referredIds;

    @ApiModelProperty("员工就职状态")
    private Byte[]userStatus;

}
