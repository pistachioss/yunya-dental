/**
 * Copyright (C), 2015-2019, 上海云牙医疗信息科技有限公司
 * FileName: AppItemVo
 * Author:   yzg
 * Date:     6/6/2019 2:26 PM
 * Description: 预约项目VO类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.yunya.modules.appointment.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<br>
 * 〈预约项目VO类，所有VO类属性均与公司端Entity保持一致，便于返回值封装〉
 *
 * @author yzg
 * @create 6/6/2019
 * @since 1.0.0
 */
@Data
@ToString
public class AppItemVo implements Serializable {

    /**
     * 预约项目id
     */
    private Integer id;

    /**
     * 所属预约类型id
     */
    private Integer orderId;

    /**
     * 预约类型
     */
    private String type;

    /**
     * 预约项目名称
     */
    private String name;

    /**
     * 门诊端预约默认时长（分钟）
     */
    private String appDuration;

    /**
     * 门诊端项目启用状态
     */
    private Boolean isvalid;
}