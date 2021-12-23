package com.yunya.feign.treatment.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2021/12/6
 * @description:
 */
@Data
public class BasePeizhenCentreVO implements Serializable {
    /**
     * 诊疗记录ID
     */

    private Integer treatmentId;

    /**
     * 组织ID
     */

    private Integer orgId;

    /**
     * 接诊开始时间
     */

    private Date treatStartTime;

    /**
     * 接诊结束时间
     */

    private Date treatEndTime;

    /**
     * 助手1
     */

    private String assistant1;

    /**
     * 助手2
     */

    private String assistant2;

    /**
     * 助手3
     */

    private String assistant3;

    /**
     * 应收工作量
     */

    private String actualWorkload;

    /**
     * 退费金额
     */

    private String refundWorkload;
}
