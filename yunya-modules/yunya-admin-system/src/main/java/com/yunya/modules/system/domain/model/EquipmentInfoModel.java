package com.yunya.modules.system.domain.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 新增设备model
 *
 * @author: WY
 * @date 2020/9/2 10:37
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class EquipmentInfoModel implements Serializable {

    /**
     * 设备SN号(序列号)
     */
    private String serialNumber;
}
