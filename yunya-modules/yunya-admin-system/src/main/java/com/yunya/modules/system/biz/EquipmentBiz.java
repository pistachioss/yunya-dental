package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.system.EquipmentInfo;
import com.yunya.modules.system.mapper.EquipmentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/9/2 10:12
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EquipmentBiz extends BaseBiz<EquipmentInfoMapper, EquipmentInfo> {

    /** 注入对象 */
    @Autowired private EquipmentInfoMapper equipmentInfoMapper;
}
