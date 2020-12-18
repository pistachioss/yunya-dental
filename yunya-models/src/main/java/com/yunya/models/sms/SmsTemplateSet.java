package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sms_template_set")
public class SmsTemplateSet {
    @Id
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 签名id
     */
    @Column(name = "signature_id")
    private Integer signatureId;

    /**
     * 模板名称
     */
    @Column(name = "template_name")
    private String templateName;

    /**
     * 模板内容
     */
    @Column(name = "template_content")
    private String templateContent;

    /**
     * 模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）
     */
    @Column(name = "template_item")
    private String templateItem;

    /**
     * 申请说明
     */
    private String remark;

    /**
     * 审核状态：0：审核中。1：审核通过。2：审核失败。
     */
    @Column(name = "template_status")
    private Byte templateStatus;

    /**
     * 阿里云短信模板code
     */
    @Column(name = "template_code")
    private String templateCode;

    /**
     * 短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    @Column(name = "template_type")
    private Byte templateType;

    /**
     * 模板有效字数（包含头部的签名，不包含模板变量及其占位符）
     */
    @Column(name = "template_length")
    private Integer templateLength;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 提交人
     */
    @Column(name = "crt_user")
    private String crtUser;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

    /**
     * 适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     */
    private Byte sense;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取组织id（门诊、公司）
     *
     * @return org_id - 组织id（门诊、公司）
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id（门诊、公司）
     *
     * @param orgId 组织id（门诊、公司）
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取签名id
     *
     * @return signature_id - 签名id
     */
    public Integer getSignatureId() {
        return signatureId;
    }

    /**
     * 设置签名id
     *
     * @param signatureId 签名id
     */
    public void setSignatureId(Integer signatureId) {
        this.signatureId = signatureId;
    }

    /**
     * 获取模板名称
     *
     * @return template_name - 模板名称
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * 设置模板名称
     *
     * @param templateName 模板名称
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * 获取模板内容
     *
     * @return template_content - 模板内容
     */
    public String getTemplateContent() {
        return templateContent;
    }

    /**
     * 设置模板内容
     *
     * @param templateContent 模板内容
     */
    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    /**
     * 获取模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）
     *
     * @return template_item - 模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）
     */
    public String getTemplateItem() {
        return templateItem;
    }

    /**
     * 设置模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）
     *
     * @param templateItem 模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）
     */
    public void setTemplateItem(String templateItem) {
        this.templateItem = templateItem;
    }

    /**
     * 设置模板有效字数（包含头部的签名，不包含模板变量及其占位符）
     *
     * @param templateLength 模板有效字数（包含头部的签名，不包含模板变量及其占位符）
     */
    public void setTemplateLength(Integer templateLength) {
        this.templateLength = templateLength;
    }

    /**
     * 获取模板有效字数（包含头部的签名，不包含模板变量及其占位符）
     *
     * @return
     */
    public Integer getTemplateLength() {
        return templateLength;
    }

    /**
     * 获取申请说明
     *
     * @return remark - 申请说明
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置申请说明
     *
     * @param remark 申请说明
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取审核状态：0：审核中。1：审核通过。2：审核失败。
     *
     * @return template_status - 审核状态：0：审核中。1：审核通过。2：审核失败。
     */
    public Byte getTemplateStatus() {
        return templateStatus;
    }

    /**
     * 设置审核状态：0：审核中。1：审核通过。2：审核失败。
     *
     * @param templateStatus 审核状态：0：审核中。1：审核通过。2：审核失败。
     */
    public void setTemplateStatus(Byte templateStatus) {
        this.templateStatus = templateStatus;
    }

    /**
     * 获取阿里云短信模板code
     *
     * @return template_code - 阿里云短信模板code
     */
    public String getTemplateCode() {
        return templateCode;
    }

    /**
     * 设置阿里云短信模板code
     *
     * @param templateCode 阿里云短信模板code
     */
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    /**
     * 获取短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     *
     * @return template_type - 短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    public Byte getTemplateType() {
        return templateType;
    }

    /**
     * 设置短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     *
     * @param templateType 短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    public void setTemplateType(Byte templateType) {
        this.templateType = templateType;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取提交人
     *
     * @return crt_user - 提交人
     */
    public String getCrtUser() {
        return crtUser;
    }

    /**
     * 设置提交人
     *
     * @param crtUser 提交人
     */
    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取修改人id
     *
     * @return upt_id - 修改人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人id
     *
     * @param uptId 修改人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upt_time - 修改时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改时间
     *
     * @param uptTime 修改时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }

    /**
     * 获取适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     *
     * @return sense - 适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     */
    public Byte getSense() {
        return sense;
    }

    /**
     * 设置适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     *
     * @param sense 适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     */
    public void setSense(Byte sense) {
        this.sense = sense;
    }
}