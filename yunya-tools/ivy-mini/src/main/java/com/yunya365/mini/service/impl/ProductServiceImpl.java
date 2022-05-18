package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.service.IProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 产品服务
 * @author: xy
 **/
@Service
public class ProductServiceImpl implements IProductService {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private RemoteDiscountFeign discountFeign;

    @Override
    public List<HotSaleVO> hotSale() {
        return null;
    }

    @Override
    public PageInfo<GoodsVO> pageGoods(GoodsQuery query) {
        return treatmentServiceFeign.pageGoods(query);
    }

    @Override
    public PageInfo<VirtualProductVO> pageVirtual(VirtualProductQuery query) {
        return discountFeign.pageVirtual(query);
    }

    @Override
    public void goodsDetail(Integer itemId) {

    }

    @Override
    public void virtualDetail(Integer couponId) {

    }
}
