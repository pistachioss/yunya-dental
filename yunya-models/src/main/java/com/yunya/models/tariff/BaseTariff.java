package com.yunya.models.tariff;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "base_tariff")
public class BaseTariff {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 父项目分类
     */
    @Column(name = "tariff_category_id")
    private Integer tariffCategoryId;

    /**
     * 项目编码
     */
    @Column(name = "item_number")
    private String itemNumber;

    /**
     * 项目名称
     */
    private String name;

    private String pinyin;

    /**
     * 项目英文名称
     */
    @Column(name = "english_name")
    private String englishName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 调价方式: 0, 手动调价; 1, 自动调价
     */
    private Boolean adjust;

    /**
     * 是否计算绩效:0否,1是
     */
    private Boolean achie;

    /**
     * 电子病历处理内容
     */
    private String emr;

    /**
     * 注意事项
     */
    private String attention;

    /**
     * 几天后随访
     */
    @Column(name = "fellow_up")
    private String fellowUp;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

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
     * 获取父项目分类
     *
     * @return tariff_category_id - 父项目分类
     */
    public Integer getTariffCategoryId() {
        return tariffCategoryId;
    }

    /**
     * 设置父项目分类
     *
     * @param tariffCategoryId 父项目分类
     */
    public void setTariffCategoryId(Integer tariffCategoryId) {
        this.tariffCategoryId = tariffCategoryId;
    }

    /**
     * 获取项目编码
     *
     * @return item_number - 项目编码
     */
    public String getItemNumber() {
        return itemNumber;
    }

    /**
     * 设置项目编码
     *
     * @param itemNumber 项目编码
     */
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * 获取项目名称
     *
     * @return name - 项目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置项目名称
     *
     * @param name 项目名称
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    /**
     * 获取项目英文名称
     *
     * @return english_name - 项目英文名称
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * 设置项目英文名称
     *
     * @param englishName 项目英文名称
     */
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    /**
     * 获取单位
     *
     * @return unit - 单位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置单位
     *
     * @param unit 单位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取价格
     *
     * @return price - 价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置价格
     *
     * @param price 价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取调价方式: 0, 手动调价; 1, 自动调价
     *
     * @return adjust - 调价方式: 0, 手动调价; 1, 自动调价
     */
    public Boolean getAdjust() {
        return adjust;
    }

    /**
     * 设置调价方式: 0, 手动调价; 1, 自动调价
     *
     * @param adjust 调价方式: 0, 手动调价; 1, 自动调价
     */
    public void setAdjust(Boolean adjust) {
        this.adjust = adjust;
    }

    /**
     * 获取是否计算绩效:0否,1是
     *
     * @return achie - 是否计算绩效:0否,1是
     */
    public Boolean getAchie() {
        return achie;
    }

    /**
     * 设置是否计算绩效:0否,1是
     *
     * @param achie 是否计算绩效:0否,1是
     */
    public void setAchie(Boolean achie) {
        this.achie = achie;
    }

    /**
     * 获取电子病历处理内容
     *
     * @return emr - 电子病历处理内容
     */
    public String getEmr() {
        return emr;
    }

    /**
     * 设置电子病历处理内容
     *
     * @param emr 电子病历处理内容
     */
    public void setEmr(String emr) {
        this.emr = emr;
    }

    /**
     * 获取注意事项
     *
     * @return attention - 注意事项
     */
    public String getAttention() {
        return attention;
    }

    /**
     * 设置注意事项
     *
     * @param attention 注意事项
     */
    public void setAttention(String attention) {
        this.attention = attention;
    }

    /**
     * 获取几天后随访
     *
     * @return fellow_up - 几天后随访
     */
    public String getFellowUp() {
        return fellowUp;
    }

    /**
     * 设置几天后随访
     *
     * @param fellowUp 几天后随访
     */
    public void setFellowUp(String fellowUp) {
        this.fellowUp = fellowUp;
    }

    /**
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
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
     * 获取创建人名称
     *
     * @return crt_name - 创建人名称
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtName 创建人名称
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取修改人名称
     *
     * @return upd_name - 修改人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置修改人名称
     *
     * @param updName 修改人名称
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}