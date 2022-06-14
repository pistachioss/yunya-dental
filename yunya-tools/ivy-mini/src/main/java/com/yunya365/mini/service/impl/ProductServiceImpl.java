package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.feign.ivy_mini.domain.bo.OrderItemBO;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseOralTariffCategory;
import com.yunya365.mini.service.IProductService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

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
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtils.zRevrangeWithScores(RedisConstants.HOT_SALE_PRODUCT, 0, 10);
        List<HotSaleVO> collect = Lists.newArrayListWithCapacity(10);
        if (CollectionUtils.isNotEmpty(typedTuples)) {
            Set<Integer> productIds = typedTuples.stream()
                    .map(t -> Integer.valueOf(Objects.requireNonNull(t.getValue()))).collect(toSet());
            //查询线上商品集合
            List<BaseOralTariff> baseOralTariffs = treatmentServiceFeign.listOnSaleOral(productIds);
            Map<Integer, BaseOralTariff> goodsMap = baseOralTariffs.stream()
                    .collect(toMap(BaseOralTariff::getId, Function.identity()));
            collect = typedTuples.stream().map(t -> {
                Integer id = Integer.valueOf(Objects.requireNonNull(t.getValue()));
                Double score = t.getScore();
                BaseOralTariff oralTariff = goodsMap.get(id);
                HotSaleVO vo = new HotSaleVO();
                if (Objects.nonNull(oralTariff)) {
                    String itemPic = oralTariff.getItemPic();
                    vo.setProductId(id);
                    vo.setProductName(oralTariff.getName());
                    vo.setProductPic(StringUtils.isNotBlank(itemPic) ? itemPic.substring(0, itemPic.indexOf(",")) : null);
                    vo.setProductPrice(oralTariff.getPrice());
                    vo.setStock(oralTariff.getStock());
                    vo.setSoldQuantity(oralTariff.getSale());
                }
                vo.setSoldQuantity(Objects.requireNonNull(score).intValue());
                vo.setProductType(0);
                return vo;
            }).collect(toList());
        }
        return collect;
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
    public GoodsDetailVO goodsDetail(Integer itemId) {
        BaseOralTariff tariff = treatmentServiceFeign.findBaseOralTariffById(itemId);
        GoodsDetailVO detailVO = BeanCopierUtils.generalCopyBean(tariff, GoodsDetailVO.class);
        detailVO.setProductId(tariff.getId());
        detailVO.setProductName(tariff.getName());
        String itemPic = tariff.getItemPic();
        detailVO.setProductPics(StringUtils.isNotBlank(itemPic) ? Lists.newArrayList(Splitter.on(",").split(itemPic)) : null);
        detailVO.setCategoryId(tariff.getOralTariffCategoryId());
        detailVO.setProductPrice(tariff.getPrice());
        return detailVO;
    }

    @Override
    public VirtualDetailVO virtualDetail(Integer couponId) {
        return discountFeign.couponDetail(couponId);
    }

    @Override
    public List<ProductTypeVO> cateGoryList(Integer type) {
        List<ProductTypeVO> list = Lists.newArrayList();
        // 产品类型（0-商品 1-虚拟服务）
        if (Objects.equals(0, type)) {
            BaseOralTariffCategory queryForm = new BaseOralTariffCategory();
            queryForm.setInservice(true);
            List<BaseOralTariffCategory> categoryList = treatmentServiceFeign.findBaseOralTariffCategoryList(queryForm);
            list = categoryList.stream().map(t -> BeanCopierUtils.generalCopyBean(t, ProductTypeVO.class)).collect(toList());
        }
        if (Objects.equals(1, type)) {
            ProductTypeQueryForm queryForm = new ProductTypeQueryForm();
            queryForm.setWhetherPage(false);
            ResponseResult<PageInfo<ProductTypeVO>> page = discountFeign.findList(queryForm);
            PageInfo<ProductTypeVO> data = page.getData();
            list = Objects.nonNull(data) ? data.getList() : list;

        }
        return list;
    }

    @Override
    public List<OrderItemBO> listGoodsOrderItem(Collection<Integer> ids) {
        //原始商品集合
        List<BaseOralTariff> itemList = treatmentServiceFeign.listOnSaleOral(ids);
        List<ProductTypeVO> cateGoryList = cateGoryList(0);
        List<OrderItemBO> orderItemBOS = itemList.stream().map(t -> {
            String itemPic = t.getItemPic();
            OrderItemBO bo = new OrderItemBO();
            bo.setProductId(t.getId());
            bo.setProductSn(t.getItemNumber());
            bo.setProductName(t.getName());
            bo.setProductPrice(t.getPrice());
            bo.setProductPic(StringUtils.isNotBlank(itemPic) ? itemPic.substring(0, itemPic.indexOf(",")) : null);
            bo.setStock(t.getStock());
            return bo;
        }).collect(toList());
        Map<Integer, ProductTypeVO> collect = cateGoryList.stream().collect(toMap(ProductTypeVO::getId, Function.identity()));
        orderItemBOS.stream()
                .filter(t -> collect.containsKey(t.getProductCategoryId()))
                .forEach(t -> {
                    ProductTypeVO category = collect.get(t.getProductCategoryId());
                    t.setProductCategoryId(category.getId());
                    t.setProductCategoryName(category.getName());
                });
        return orderItemBOS;
    }
}
