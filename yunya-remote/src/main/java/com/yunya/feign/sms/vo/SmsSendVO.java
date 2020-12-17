package com.yunya.feign.sms.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：短信发送响应模型
 *
 * @author: chenlin
 * @Description: 短信发送响应模型
 * @Date: 2020/12/14 16:13
 * @since: 1.0.0
 */
@ApiModel("短信发送响应模型")
@ToString
@Data
public class SmsSendVO implements Serializable {

    /**
     * 发送对象合计
     */
    @ApiModelProperty("发送对象合计")
    private Integer sendTotal;

    /**
     * 发送成功
     */
    @ApiModelProperty("发送成功")
    private Integer sendSuccess;

    /**
     * 发送失败
     */
    @ApiModelProperty("发送失败")
    private Integer sendFailure;

    /**
     * 发送记录列表
     */
    @ApiModelProperty("发送记录列表")
    private PageInfo<SmsSendRecordVO> smsSendRecordPageInfo;
}
