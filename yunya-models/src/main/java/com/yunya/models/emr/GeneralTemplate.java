package com.yunya.models.emr;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author
 */
@Table(name="general_template")
@Data
public class GeneralTemplate implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 病历模板分类Id
     */
    @Column(name = "medical_template_category_id")
    private Integer medicalTemplateCategoryId;

    /**
     * 启用/禁用
     */
    private Integer enable;

    /**
     * 创建人Id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private LocalDateTime crtTime;

    /**
     * 更新人Id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private LocalDateTime updTime;

}