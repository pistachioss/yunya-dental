package com.yunya.modules.sms.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 简介：阿里云短信签名审核模型
 *
 * @author: chenlin
 * @Description: 阿里云短信签名审核模型
 * @Date: 2020/12/22 13:18
 * @since: 1.0.0
 */
@ToString
@Data
public class SmsSignatureReportVO {
    /*签名来源。*/
    private String sign_source;

    /*审核未通过原因。*/
    private String reason;

    /*签名名称。*/
    private String sign_name;

    /*短信签名审核工单号。*/
    private String order_id;

    /*签名审核状态，包括：approving：审核中。approved：审核通过。rejected：审核未通过*/
    private String sign_status;

    /*申请说明。*/
    private String remark;

    /*适用场景。*/
    private String sign_scene;

    /*短信签名创建日期和时间。*/
    private String create_date;
}
