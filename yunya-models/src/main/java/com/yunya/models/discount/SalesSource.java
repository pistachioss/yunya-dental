package com.yunya.models.discount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "sales_source")
@Data
public class SalesSource {
    /**
     * 主键
     */
    @Id
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 来源名称
     */
    @ApiModelProperty("来源名称")
    private String name;

    /**
     * 属性 0:非第三方来源,1:第三方来源
     */
    @ApiModelProperty("属性 0:非第三方来源,1:第三方来源")
    private Byte type;

    /**
     * 是否启用
     */
    @ApiModelProperty("名称")
    private Boolean inservice;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}