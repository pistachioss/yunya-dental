package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 设备信息Vo
 *
 * @author: WY
 * @date 2020/9/2 16:09
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class EquipmentInfoVO implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 设备IP
     */
    private String ip;

    /**
     * 设备密码
     */
    private String pass;

    /**
     * 门诊id
     */
    private Integer orgId;

    /**
     * 创建人名称
     */
    private String crtName;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 修改人id
     */
    private Integer crtId;

    /**
     * 修改人id
     */
    private Integer updId;

    /**
     * 修改人名称
     */
    private String updName;

    /**
     * 修改时间
     */
    private Date updTime;
}
