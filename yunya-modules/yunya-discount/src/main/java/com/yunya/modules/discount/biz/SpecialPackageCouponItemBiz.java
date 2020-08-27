package com.yunya.modules.discount.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.discount.SpecialPackageCouponItem;
import com.yunya.modules.discount.mapper.SpecialPackageCouponItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 杨柳絮
 * @className SpecialPackageCouponItemBiz
 * @description
 * @date 2020/8/27 19:39
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SpecialPackageCouponItemBiz extends BaseBiz<SpecialPackageCouponItemMapper, SpecialPackageCouponItem> {
}
