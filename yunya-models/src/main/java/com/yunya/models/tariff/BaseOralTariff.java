package com.yunya.models.tariff;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Table(name = "base_oral_tariff")
public class BaseOralTariff {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 父项目分类
     */
    @Column(name = "oral_tariff_category_id")
    private Integer oralTariffCategoryId;

    /**
     * 项目编码
     */
    @Column(name = "item_number")
    private String itemNumber;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 拼音
     */
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
     * 数量
     */
    private Integer quantity;

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
     * 是否线上售卖(0:否 1:是)
     */
    @Column(name = "is_online_sale")
    private Boolean isOnlineSale;
    /**
     * 销量
     */
    private Integer sale;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * 商品图片(限制为3张，以逗号分割)
     */
    @Column(name = "item_pic")
    private String itemPic;
    /**
     * 商品详情网页内容
     */
    @Column(name = "detail_html")
    private String detailHtml;

}