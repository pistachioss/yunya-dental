package com.yunya.models.emr;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author 
 * 病历模板分类
 */
@Table(name="medical_template_category")
@Data
public class MedicalTemplateCategory implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 上级分类Id
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 创建人Id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    @Column(name = "sort")
    private Integer sort;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private LocalDateTime crtTime;

    /**
     * 更新Id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private LocalDateTime updTime;
}