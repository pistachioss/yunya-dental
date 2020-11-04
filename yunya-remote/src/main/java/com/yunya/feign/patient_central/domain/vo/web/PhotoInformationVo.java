package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 返回心跳版查询照片信息模型
 *
 * @author: WY
 * @date 2020/8/31 20:37
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回心跳版查询照片信息模型")
public class PhotoInformationVo implements Serializable {

    /**
     * 照片路径
     */
    //private String croplmgPath;

    /**
     * 照片id
     */
    private String faceId;

    /**
     * base64 的特征值
     */
    //private String feature;

    /**
     * 特征key
     */
    private String featureKey;

    /**
     * 照片路径
     */
    private String path;

    /**
     * 人员id
     */
    private String personId;
}
