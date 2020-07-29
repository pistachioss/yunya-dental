/**
 * Copyright (C), 2015-2019, 上海云牙医疗信息科技有限公司
 * FileName: AppItemController
 * Author:   yzg
 * Date:     6/6/2019 10:34 AM
 * Description: 预约项目控制层
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.yunya.modules.appointment.rpc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.controller.BaseController;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.AppItem;
import com.yunya.modules.appointment.biz.AppItemBiz;
import com.yunya.modules.appointment.form.AppItemForm;
import com.yunya.modules.appointment.form.AppointItemTypeQueryForm;
import com.yunya.modules.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.modules.appointment.vo.AppointmentItemTypeVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈预约项目控制层〉
 *
 * @author yzg
 * @create 6/6/2019
 * @since 1.0.0
 */
@RestController
@RequestMapping("/appoint")
@CrossOrigin
public class AppItemController extends BaseController<AppItemBiz, AppItem> {

    /**
     * 根据条件查询门诊可预约项目
     *
     * @param form
     * @return
     * @description 查询公司端、门诊端的预约列表，Mock两端数据，返回vo对象列表
     */
    @PostMapping("/list")
    public PageInfo<AppointmentItemTypeVo> findAppItemList(@RequestBody AppointItemTypeQueryForm form) {
        //查询门诊端的预约列表
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }

        List<AppointmentItemTypeVo> appItemList = baseBiz.findAppItemList(form);
        return new PageInfo<>(appItemList);
    }

    /**
     * 修改门诊预约项目
     *
     * @param appItemForm 预约项目Form
     */
/*    @PostMapping("/add")
    public ResponseResult saveAppItem(@RequestBody @Validated AppItemForm appItemForm) {
        Integer integer = baseBiz.save(appItemForm);
        if (integer > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.fail(30002, "修改失败", null);
    }*/

    /**
     * 预约搜索
     *
     * @param baseQueryForm 查询条件
     * @return
     */
    @PostMapping("/search")
    public List<AppointmentItemTypeVo> searchAppItem(@RequestBody AppointItemTypeQueryForm baseQueryForm) {
        return baseBiz.findByAppItemName(baseQueryForm);
    }

    /**
     * 修改门诊预约项目（不启用）
     *
     * @param id          门诊预约类id
     * @param appItemForm 预约项目Form
     */
    @PutMapping("/update/{id}")
    public ResponseResult update(@PathVariable("id") Integer id, @RequestBody @Validated AppItemForm appItemForm) {
        Integer integer = baseBiz.updateAppItem(id, appItemForm);
        if (integer > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.fail(30003, "修改失败", null);
    }

    /**
     * 获取门诊可预约的项目列表
     *
     * @param compClinId
     * @return
     */
    @GetMapping("/available/{compClinId}")
    public List<AppointmentItemEnableModelVo> findAvailableAppItem(@PathVariable("compClinId") String compClinId) {
        return baseBiz.findAvailableAppItemList(compClinId);
    }
}