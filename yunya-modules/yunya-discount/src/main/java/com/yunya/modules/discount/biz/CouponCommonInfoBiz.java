package com.yunya.modules.discount.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.query.CouponCommonInfoQuery;
import com.yunya.feign.discount.domain.vo.CouponCommonInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    /**
     * 条件查询卡券公用信息列表
     *
     * @param query
     * @return
     */
    public PageInfo<CouponCommonInfoVO> findList(CouponCommonInfoQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<CouponCommonInfoVO> result = mapper.selectCouponCommonList(query);
        return new PageInfo<>(result);
    }

//  public int insertBackId(CouponCommonInfo couponCommonInfo){
//    return mapper.insertBackId(couponCommonInfo);
//  }

}
