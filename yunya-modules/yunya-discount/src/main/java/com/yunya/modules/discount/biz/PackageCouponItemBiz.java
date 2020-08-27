package com.yunya.modules.discount.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.discount.PackageCouponItem;
import com.yunya.modules.discount.mapper.PackageCouponItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 杨柳絮
 * @className PackageCouponItemBiz
 * @description
 * @date 2020/8/26 16:13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PackageCouponItemBiz extends BaseBiz<PackageCouponItemMapper, PackageCouponItem> {
}
