package com.yunya.modules.appointment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.appointment.ClinicDeviceItem;
import com.yunya.modules.appointment.mapper.ClinicDeviceItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 预约设备服务
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 20:58
 * @update yunya-lihuibin    2020-07-31    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicDeviceItemBiz extends BaseBiz<ClinicDeviceItemMapper, ClinicDeviceItem> {
}
