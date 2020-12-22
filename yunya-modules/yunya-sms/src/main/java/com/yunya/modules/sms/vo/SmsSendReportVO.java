package com.yunya.modules.sms.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 简介：阿里云短信发送状态模型
 *
 * @author: chenlin
 * @Description: 阿里云短信发送状态模型
 * @Date: 2020/12/22 13:18
 * @since: 1.0.0
 */
@ToString
@Data
public class SmsSendReportVO {

    /*手机号码。*/
    private String phone_number;

    /*发送时间。*/
    private String send_time;

    /*状态报告时间。*/
    private String report_time;

    /*是否接收成功 */
    private Boolean success;

    /*状态报告编码。DELIVERED*/
    private String err_code;

    /*状态报告说明。用户接收成功*/
    private String err_msg;

    /*短信长度，140字节算一条短信，短信长度超过140字节时会拆分成多条短信发送。1，2，3*/
    private String sms_size;

    /*发送序列号	。*/
    private String biz_id;

    /*用户序列号。*/
    private String out_id;
}
