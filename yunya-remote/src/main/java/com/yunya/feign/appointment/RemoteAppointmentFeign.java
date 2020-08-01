package com.yunya.feign.appointment;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.factory.RemoteAppointmentFeignBackFactory;
import com.yunya.feign.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.feign.appointment.vo.AppointmentItemVo;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约系统服务接口调用
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 16:58
 * @update yunya-lihuibin    2020-07-31    新建
 */
@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_CLINIC_APPOINTMENT,
        fallbackFactory = RemoteAppointmentFeignBackFactory.class)
public interface RemoteAppointmentFeign {

    /**
     * 根据条件查询门诊可预约项目
     *
     * @param form
     * @return
     * @description 查询公司端、门诊端的预约列表，Mock两端数据，返回vo对象列表
     */
    @PostMapping("/list")
    public PageInfo<AppointmentItemVo> findAppItemList(@RequestBody AppointItemQuery form);

    /**
     * 预约搜索
     *
     * @param baseQueryForm 查询条件
     * @return
     */
    @PostMapping("/search")
    public List<AppointmentItemVo> searchAppItem(@RequestBody AppointItemQuery baseQueryForm);

    /**
     * 获取门诊可预约的项目列表
     *
     * @param compClinId
     * @return
     */
    @GetMapping("/available/{compClinId}")
    public List<AppointmentItemEnableModelVo> findAvailableAppItem(@PathVariable("compClinId") String compClinId);

    /**
     * 根据id查询预约项目种类
     * @param id 预约项目id
     * @return
     */
    @GetMapping("/select/{id}")
    public ResponseResult selectAppointTypeById(@PathVariable("id") Integer id);
}
