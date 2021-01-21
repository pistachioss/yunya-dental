package com.yunya.models.system;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "dictionary_item")
public class DictionaryItem {
    @Id
    private Integer id;

    /**
     * 字典类型ID
     */
    @Column(name = "dictionary_type_id")
    private Integer dictionaryTypeId;

    /**
     * 字典选项
     */
    private String name;

    /**
     * 英文字典选项
     */
    @Column(name = "english_name")
    private String englishName;

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

}