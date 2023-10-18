package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bill_pay_record_log")
public class BillPayRecordLog {


    /**
     * 收费记录id
     */
    @Id
    @Column(name = "bill_pay_record_id")
    private Integer billPayRecordId;


    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 订单记录id
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 使用优惠的会员卡id
     */
    @Column(name = "member_id")
    private Integer memberId;

    /**
     * 会员卡优惠类型：99-本人会员优惠，100、推荐人次一级会员优惠，101-亲密付会员优惠
     */
    @Column(name = "member_discount_type")
    private Integer memberDiscountType;

    /**
     * 总收费
     */
    @Column(name = "total_charge")
    private BigDecimal totalCharge;

    /**
     * 总收费中的本金
     */
    @Column(name = "total_principal")
    private BigDecimal totalPrincipal;

    /**
     * 收费参数json
     */
    private String param;

    /**
     * 类型：1-挂账，2-确认收费，3-收欠费
     */
    private Byte type;

    /**
     * 收费状态：1-主流程完成，2-次流程完成
     */
    private Byte status;

    /**
     * 错误信息
     */
    @Column(name = "err_msg")
    private String errMsg;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

    /**
     * 获取收费记录id
     *
     * @return bill_pay_record_id - 收费记录id
     */
    public Integer getBillPayRecordId() {
        return billPayRecordId;
    }

    /**
     * 设置收费记录id
     *
     * @param billPayRecordId 收费记录id
     */
    public void setBillPayRecordId(Integer billPayRecordId) {
        this.billPayRecordId = billPayRecordId;
    }

    /**
     * 获取门诊id
     *
     * @return org_id - 门诊id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置门诊id
     *
     * @param orgId 门诊id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取订单记录id
     *
     * @return order_record_id - 订单记录id
     */
    public Integer getOrderRecordId() {
        return orderRecordId;
    }

    /**
     * 设置订单记录id
     *
     * @param orderRecordId 订单记录id
     */
    public void setOrderRecordId(Integer orderRecordId) {
        this.orderRecordId = orderRecordId;
    }

    /**
     * 获取患者id
     *
     * @return
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id
     *
     * @param patientId
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取使用优惠的会员卡id
     *
     * @return
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * 设置使用优惠的会员卡id
     *
     * @param memberId
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * 获取会员卡优惠类型：99-本人会员优惠，100、推荐人次一级会员优惠，101-亲密付会员优惠
     *
     * @return
     */
    public Integer getMemberDiscountType() {
        return memberDiscountType;
    }

    /**
     * 设置会员卡优惠类型：99-本人会员优惠，100、推荐人次一级会员优惠，101-亲密付会员优惠
     *
     * @param memberDiscountType
     */
    public void setMemberDiscountType(Integer memberDiscountType) {
        this.memberDiscountType = memberDiscountType;
    }

    /**
     * 获取总收费
     *
     * @return total_charge - 总收费
     */
    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    /**
     * 设置总收费
     *
     * @param totalCharge 总收费
     */
    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    /**
     * 获取总收费中的本金
     *
     * @return total_principal - 总收费中的本金
     */
    public BigDecimal getTotalPrincipal() {
        return totalPrincipal;
    }

    /**
     * 设置总收费中的本金
     *
     * @param totalPrincipal 总收费中的本金
     */
    public void setTotalPrincipal(BigDecimal totalPrincipal) {
        this.totalPrincipal = totalPrincipal;
    }

    /**
     * 获取收费参数json
     *
     * @return param - 收费参数json
     */
    public String getParam() {
        return param;
    }

    /**
     * 设置收费参数json
     *
     * @param param 收费参数json
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * 获取类型：1-挂账，2-确认收费，3-收欠费
     *
     * @return type - 类型：1-挂账，2-确认收费，3-收欠费
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置类型：1-挂账，2-确认收费，3-收欠费
     *
     * @param type 类型：1-挂账，2-确认收费，3-收欠费
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取收费状态：1-主流程完成，2-次流程完成
     *
     * @return status - 收费状态：1-主流程完成，2-次流程完成
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置收费状态：1-主流程完成，2-次流程完成
     *
     * @param status 收费状态：1-主流程完成，2-次流程完成
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取错误信息
     *
     * @return
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * 设置错误信息
     *
     * @param errMsg
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
    }

    /**
     * 获取更新时间
     *
     * @return upt_time - 更新时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置更新时间
     *
     * @param uptTime 更新时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}