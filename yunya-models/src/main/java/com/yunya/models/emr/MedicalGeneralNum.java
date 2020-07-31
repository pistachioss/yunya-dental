package com.yunya.models.emr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "medical_general_num")
public class MedicalGeneralNum {
    /**
     * 主键ID
     */
    @Id
    private Integer id;
    /**
     * 常用词条ID
     */
    @Column(name = "general_id")
    @ApiModelProperty("常用词条ID")
    private Integer generalId;

    /**
     * 病历ID
     */
    @Column(name = "medical_id")
    @ApiModelProperty("病历ID")
    private Integer medicalId;

    /**
     * 使用次数
     */
    @ApiModelProperty("使用次数")
    private Integer number;

    /**
     * 使用时间
     */
    @Column(name = "crt_time")
    @ApiModelProperty("使用时间")
    private Date crtTime;
}