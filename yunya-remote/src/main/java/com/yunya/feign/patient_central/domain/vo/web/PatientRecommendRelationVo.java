package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 返回患者推荐关系信息模型
 *
 * @author: WY
 * @date 2020/7/29 14:13
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回患者推荐关系信息模型")
public class PatientRecommendRelationVo implements Serializable {

    /**
     * 被推荐患者id
     */
    private Integer id;

    /**
     * 推荐者id
     */
    private Integer patientId;

    /**
     * 被推荐患者名称
     */
    private String name;

    /**
     * 性别
     */
    private Byte gender;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 创建时间
     */
    private Date crtTime;


}
