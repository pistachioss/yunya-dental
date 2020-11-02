package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 返回患者照片信息模型
 *
 * @author: WY
 * @date 2020/8/11 10:19
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回患者照片信息模型")
public class PictureVo implements Serializable {

    /**
     * 照片所有者（人员）guid
     */
    private String personGuid;

    /**
     * 人员照片
     */
    private String faceUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 照片id
     */
    private String faceGuid;
}
