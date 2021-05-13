package com.yunya.middletable.dao.report.credits_shop;

import lombok.ToString;

import java.util.Date;
import javax.persistence.*;

@Table(name = "credits_shop")
@ToString
public class CreditsShop {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 积分来源: game(游戏), sign(签到),task(pk赛), reSign(补签)。 hdtool(加积分活动),alipay(支付宝), qb(Q币), coupon(优惠券), object(实物), phonebill(话费), phoneflow(流量), virtual(虚拟商品),offlineConsume(门诊消费), refund(门店退费)
     */
    private String type;

    /**
     * 渠道 0-艾维自有；1-兑吧
     */
    private Byte channel;

    /**
     * 兑吧订单号
     */
    @Column(name = "order_num")
    private String orderNum;

    /**
     * 当前总积分
     */
    @Column(name = "credits_account")
    private Long creditsAccount;

    /**
     * 本次兑换、扣除的积分
     */
    private Long credits;

    /**
     * 积分操作：0-增加积分；1-减少积分
     */
    @Column(name = "credits_option")
    private Byte creditsOption;

    /**
     * 此次兑换实际扣除开发者账户费用，单位为分
     */
    @Column(name = "actual_price")
    private Integer actualPrice;

    /**
     * 自有商品编码
     */
    @Column(name = "item_code")
    private String itemCode;

    /**
     * 本次消耗积分描述
     */
    private String description;

    /**
     * 备注 详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号、门店消费信息(支付ID)、门店退费(退费记录ID)
     */
    private String remarks;

    /**
     * 是否删除，是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新日期
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取患者ID
     *
     * @return patient_id - 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID
     *
     * @param patientId 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取积分来源: game(游戏), sign(签到),task(pk赛), reSign(补签)。 hdtool(加积分活动),alipay(支付宝), qb(Q币), coupon(优惠券), object(实物), phonebill(话费), phoneflow(流量), virtual(虚拟商品),offlineConsume(门诊消费), refund(门店退费)
     *
     * @return type - 积分来源: game(游戏), sign(签到),task(pk赛), reSign(补签)。 hdtool(加积分活动),alipay(支付宝), qb(Q币), coupon(优惠券), object(实物), phonebill(话费), phoneflow(流量), virtual(虚拟商品),offlineConsume(门诊消费), refund(门店退费)
     */
    public String getType() {
        return type;
    }

    /**
     * 设置积分来源: game(游戏), sign(签到),task(pk赛), reSign(补签)。 hdtool(加积分活动),alipay(支付宝), qb(Q币), coupon(优惠券), object(实物), phonebill(话费), phoneflow(流量), virtual(虚拟商品),offlineConsume(门诊消费), refund(门店退费)
     *
     * @param type 积分来源: game(游戏), sign(签到),task(pk赛), reSign(补签)。 hdtool(加积分活动),alipay(支付宝), qb(Q币), coupon(优惠券), object(实物), phonebill(话费), phoneflow(流量), virtual(虚拟商品),offlineConsume(门诊消费), refund(门店退费)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取渠道 0-艾维自有；1-兑吧
     *
     * @return channel - 渠道 0-艾维自有；1-兑吧
     */
    public Byte getChannel() {
        return channel;
    }

    /**
     * 设置渠道 0-艾维自有；1-兑吧
     *
     * @param channel 渠道 0-艾维自有；1-兑吧
     */
    public void setChannel(Byte channel) {
        this.channel = channel;
    }

    /**
     * 获取兑吧订单号
     *
     * @return order_num - 兑吧订单号
     */
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * 设置兑吧订单号
     *
     * @param orderNum 兑吧订单号
     */
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 获取当前总积分
     *
     * @return credits_account - 当前总积分
     */
    public Long getCreditsAccount() {
        return creditsAccount;
    }

    /**
     * 设置当前总积分
     *
     * @param creditsAccount 当前总积分
     */
    public void setCreditsAccount(Long creditsAccount) {
        this.creditsAccount = creditsAccount;
    }

    /**
     * 获取本次兑换、扣除的积分
     *
     * @return credits - 本次兑换、扣除的积分
     */
    public Long getCredits() {
        return credits;
    }

    /**
     * 设置本次兑换、扣除的积分
     *
     * @param credits 本次兑换、扣除的积分
     */
    public void setCredits(Long credits) {
        this.credits = credits;
    }

    /**
     * 获取积分操作：0-增加积分；1-减少积分
     *
     * @return credits_option - 积分操作：0-增加积分；1-减少积分
     */
    public Byte getCreditsOption() {
        return creditsOption;
    }

    /**
     * 设置积分操作：0-增加积分；1-减少积分
     *
     * @param creditsOption 积分操作：0-增加积分；1-减少积分
     */
    public void setCreditsOption(Byte creditsOption) {
        this.creditsOption = creditsOption;
    }

    /**
     * 获取此次兑换实际扣除开发者账户费用，单位为分
     *
     * @return actual_price - 此次兑换实际扣除开发者账户费用，单位为分
     */
    public Integer getActualPrice() {
        return actualPrice;
    }

    /**
     * 设置此次兑换实际扣除开发者账户费用，单位为分
     *
     * @param actualPrice 此次兑换实际扣除开发者账户费用，单位为分
     */
    public void setActualPrice(Integer actualPrice) {
        this.actualPrice = actualPrice;
    }

    /**
     * 获取自有商品编码
     *
     * @return item_code - 自有商品编码
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * 设置自有商品编码
     *
     * @param itemCode 自有商品编码
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * 获取本次消耗积分描述
     *
     * @return description - 本次消耗积分描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置本次消耗积分描述
     *
     * @param description 本次消耗积分描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取备注 详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号、门店消费信息(支付ID)、门店退费(退费记录ID)
     *
     * @return remarks - 备注 详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号、门店消费信息(支付ID)、门店退费(退费记录ID)
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注 详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号、门店消费信息(支付ID)、门店退费(退费记录ID)
     *
     * @param remarks 备注 详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号、门店消费信息(支付ID)、门店退费(退费记录ID)
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取是否删除，是否有效
     *
     * @return inservice - 是否删除，是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否删除，是否有效
     *
     * @param inservice 是否删除，是否有效
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
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
     * 获取更新人ID
     *
     * @return upd_id - 更新人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人ID
     *
     * @param updId 更新人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新日期
     *
     * @return upd_time - 更新日期
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新日期
     *
     * @param updTime 更新日期
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}