package com.yunya.modules.sms.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 简介：阿里云短信模板审核模型
 *
 * @author: chenlin
 * @Description: 阿里云短信模板审核模型
 * @Date: 2020/12/22 13:18
 * @since: 1.0.0
 */
@ToString
@Data
public class SmsTemplateReportVO {

    /*审核未通过原因。*/
    private String reason;

    /*模板名称。*/
    private String template_name;

    /*短信签名审核工单号。*/
    private String order_id;

    /*模板审核状态，包括：approving：审核中。approved：审核通过。rejected：审核未通过*/
    private String template_status;

    /*申请说明。*/
    private String remark;

    /*短信模板CODE。*/
    private String template_code;

    /*模板内容。*/
    private String template_content;

    /*模板类型。*/
    private String template_type;

    /*短信签名创建日期和时间。*/
    private String create_date;
}
