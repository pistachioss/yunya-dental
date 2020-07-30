/**
 * Copyright (C), 2015-2019, XXX有限公司 FileName: AppAppItemController Author: Perter_Chou Date:
 * 2019/8/15 14:20 Description: APP端预约项目Controller History: <author> <time> <version> <desc>
 * Perter_Chou 14:20 Since 1.0 版权信息
 */
package com.yunya.modules.appointment.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.AppItemBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈APP端预约项目Controller〉
 *
 * @author Peter_Chou
 * @create 2019/8/15
 * @since 1.0.0
 */
@RestController
@RequestMapping("/app/item")
public class AppAppItemController {
    /**
     * 预约项目
     */
    @Autowired
    private AppItemBiz appItemBiz;

    /**
     * 查询门诊可用的预约项目
     *
     * @param compClinId
     * @return
     */
    @GetMapping("/list/{compClinId}")
    public ResponseResult findClinicAvailableItem(
            @PathVariable("compClinId") String compClinId) {
        return ResponseUtil.success(appItemBiz.findAvailableAppItemList(compClinId));
    }
}
