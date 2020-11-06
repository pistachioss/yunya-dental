package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 19:24
 * @since: 1.0.0
 */
@ApiModel("考勤设备绑定查询参数模型")
@Data
@ToString
public class AttendanceDeviceBindingQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 绑定时间 */
    @ApiModelProperty(value = "绑定时间")
    private Date bindingTime;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    /** 绑定状态：0：已解绑 1：已绑定 */
    @ApiModelProperty("绑定状态：0：已解绑 1：已绑定")
    private Integer bindingStatus;

    /** 绑定用户id */
    @ApiModelProperty(value = "绑定用户id")
    private Integer userId;

    /** 绑定用户名 */
    @ApiModelProperty(value = "绑定用户名")
    private String userName;

    /** 绑定次数 */
    @ApiModelProperty(value = "绑定次数")
    private Integer bindingCount;
}
