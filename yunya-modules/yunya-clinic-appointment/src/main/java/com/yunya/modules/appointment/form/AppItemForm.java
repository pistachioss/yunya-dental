/**
 * Copyright (C), 2015-2019, 上海云牙医疗信息科技有限公司
 * FileName: AppItemForm
 * Author:   yzg
 * Date:     6/6/2019 2:34 PM
 * Description: 门诊预约项目Form类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.yunya.modules.appointment.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 〈一句话功能简述〉<br> 
 * 〈门诊预约项目Form类〉
 *
 * @author yzg
 * @create 6/6/2019
 * @since 1.0.0
 */
@Data
public class AppItemForm implements Serializable {
    /**
     * 诊所id
     */
    @NotBlank(message = "诊所id为空！")
    private String compClinId;

    /**
     * 预约项目类别id
     */
    @NotBlank(message = "项目分类id为空！")
    private String compAppitemId;

    /**
     * 项目id
     */
    @NotBlank(message = "项目编号为空！")
    private String itemNo;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 预约默认时长（分钟）
     */
    private String appDuration;

    /**
     * 是否有效、是否启用、是否可见
     */
    @NotNull(message = "是否启用状态为空！")
    private Boolean isvalid;
}