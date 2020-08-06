package com.yunya.feign.patient_central.domain.vo;

import com.yunya.framework.common.model.TreeNode;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 患者来来源数据返回Vo
 *
 * @author: WY
 * @date 2020/8/5 14:46
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者来来源数据返回Vo")
public class PatientOriginTreeVo extends TreeNode {
    /**
     * 患者来源ID
     */
    private Integer id;

    /**
     * 患者来源父ID（顶级为0）
     */
    private Integer parentId;

    /**
     * 患者来源名称
     */
    private String name;

    /**
     * 二维码地址
     */
    private String qrCodePath;

    /**
     * 是否允许操作（编辑、删除）
     */
    private Boolean allowOperate;

    /**
     * 是否有时间限制（0-否；1-是））
     */
    private Boolean timeLimit;

    /**
     * 限制开始时间
     */
    private Date limitStartDate;

    /**
     * 限制介绍时间
     */
    private Date limitEndDate;

    /**
     * 是否启用
     */
    private Boolean inservice;
}
