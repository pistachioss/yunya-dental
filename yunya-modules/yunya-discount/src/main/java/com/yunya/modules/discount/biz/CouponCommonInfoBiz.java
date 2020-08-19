package com.yunya.modules.discount.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 卡券公共信息业务层
 *
 * @author: chow
 * @date: 2020/7/30 19:53
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CouponCommonInfoBiz extends BaseBiz<CouponCommonInfoMapper, CouponCommonInfo> {

//  public int insertBackId(CouponCommonInfo couponCommonInfo){
//    return mapper.insertBackId(couponCommonInfo);
//  }

}
