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
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.AppointType;
import com.yunya.modules.appointment.biz.AppointItemBiz;
import com.yunya.feign.appointment.domain.model.AppointmentItemModel;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.modules.appointment.biz.AppointTypeBiz;
import com.yunya.modules.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.modules.appointment.vo.AppointmentItemVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/appoint")
@CrossOrigin
@Api(tags = "患者预约服务调用API")
public class AppointItemRest {

    @Autowired
    private AppointItemBiz baseBiz;

    @Autowired
    private AppointTypeBiz appointTypeBiz;

    /**
     * 根据条件查询门诊可预约项目
     *
     * @param form
     * @return
     * @description 查询公司端、门诊端的预约列表，Mock两端数据，返回vo对象列表
     */
    @PostMapping("/list")
    public PageInfo<AppointmentItemVo> findAppItemList(@RequestBody AppointItemQuery form) {
        //查询门诊端的预约列表
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }

        List<AppointmentItemVo> appItemList = baseBiz.findAppItemList(form);
        return new PageInfo<>(appItemList);
    }

    /**
     * 预约搜索
     *
     * @param baseQueryForm 查询条件
     * @return
     */
    @PostMapping("/search")
    public List<AppointmentItemVo> searchAppItem(@RequestBody AppointItemQuery baseQueryForm) {
        return baseBiz.findByAppItemName(baseQueryForm);
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

    /**
     * 根据id查询预约项目种类
     * @param id 预约项目id
     * @return
     */
    @GetMapping("/select/{id}")
    public AppointType selectAppointTypeById(@PathVariable("id") Integer id){
        AppointType appointType = appointTypeBiz.selectAppointTypeById(id);
        return appointType;
    }
}