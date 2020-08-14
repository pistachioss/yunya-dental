package com.yunya.feign.patient_central.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 患者来源类型Vo
 *
 * @author: WY
 * @date 2020/8/14 17:01
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientOriginVo implements Serializable {
    /**
     * 患者来源ID
     */
    private Integer id;

    /**
     * 患者来源名称
     */
    private String name;

    /**
     * 患者来源类型
     */
    private Integer originType;

    }
