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
@ApiModel("返回患者推荐与亲属数量信息模型")
public class PatientKinRecomVo implements Serializable {

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 亲属数量
     */
    private Integer kinQty;

    /**
     * 患者推荐数量
     */
    private Integer RecomQty;

    /**
     * 合计数量
     */
    private Integer sumQty;

    public Integer getSumQty() {
        return kinQty + RecomQty;
    }
}
