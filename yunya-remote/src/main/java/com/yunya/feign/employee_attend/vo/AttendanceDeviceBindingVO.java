package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤设备绑定响应参数模型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 19:09
 * @since: 1.0.0
 */
@ApiModel("考勤设备绑定响应参数模型")
@Data
@ToString
public class AttendanceDeviceBindingVO implements Serializable {
    /** 主键id */
    @ApiModelProperty("主键id")
    private Integer id;

    /** 绑定用户id */
    @ApiModelProperty("绑定用户id")
    private Integer userId;

    /** 绑定用户名 */
    @ApiModelProperty("绑定用户名")
    private String userName;

    /** 绑定打卡账号 */
    @ApiModelProperty("绑定打卡账号")
    private String mobile;

    /** 绑定状态：0：已解绑 1：已绑定 */
    @ApiModelProperty("绑定状态：0：已解绑 1：已绑定")
    private int bindingStatus;

    /** 初始化设备号 */
    @ApiModelProperty("初始化设备号")
    private String firstNumber;

    /** 绑定时间 */
    @ApiModelProperty("绑定时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date bindingTime;

    /** 绑定次数 */
    @ApiModelProperty("绑定次数")
    private Integer bindingCount;

    /** 设备号 */
    @ApiModelProperty("设备号")
    private String deviceNumber;

    /** 设备号 */
    @ApiModelProperty("上次绑定设备号")
    private String oldNumber;
}
