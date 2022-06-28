package com.yunya365.mini.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.LockStockForm;
import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.feign.ivy_mini.domain.bo.*;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseOralTariffCategory;
import com.yunya365.mini.entity.FansPickUp;
import com.yunya365.mini.entity.FansReceiveAddress;
import com.yunya365.mini.service.IFansReceiveAddressService;
import com.yunya365.mini.service.IProductService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.framework.common.enums.TrueFalseEnum.*;
import static com.yunya365.mini.enums.IvyMiniError.*;
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
    @Resource
    private IFansReceiveAddressService fansReceiveAddressService;
    @Resource
    private FansPickUpServiceImpl pickUpService;

    @Override
    public List<HotSaleVO> hotSale() {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtils.zRevrangeWithScores(RedisConstants.HOT_SALE_PRODUCT, 0, 10);
        List<HotSaleVO> collect = Lists.newArrayListWithCapacity(10);
        if (CollectionUtils.isNotEmpty(typedTuples)) {
            Set<Integer> productIds = typedTuples.stream()
                    .map(t -> Integer.valueOf(Objects.requireNonNull(t.getValue()))).collect(toSet());
            //查询线上商品集合
            List<ProductBO> baseOralTariffs = treatmentServiceFeign.listOnSaleOral(productIds);
            Map<Integer, ProductBO> goodsMap = baseOralTariffs.stream()
                    .collect(toMap(ProductBO::getProductId, Function.identity()));
            collect = typedTuples.stream().map(t -> {
                Integer id = Integer.valueOf(Objects.requireNonNull(t.getValue()));
                Double score = t.getScore();
                ProductBO oralTariff = goodsMap.get(id);
                HotSaleVO vo = new HotSaleVO();
                if (Objects.nonNull(oralTariff)) {
                    String itemPic = oralTariff.getProductPic();
                    vo.setProductId(id);
                    vo.setProductName(oralTariff.getProductName());
                    vo.setProductPic(StringHelper.splitFirst(itemPic));
                    vo.setProductPrice(oralTariff.getProductPrice());
                    vo.setStock(oralTariff.getStock());
                    vo.setSoldQuantity(oralTariff.getSoldQuantity());
                }
                vo.setSoldQuantity(Objects.requireNonNull(score).intValue());
                vo.setProductType(FALSE.getCode());
                return vo;
            }).collect(toList());
        }
        return collect;
    }

    @Override
    public PageInfo<GoodsVO> pageGoods(GoodsQuery query) {
        Page<BaseOralTariff> page = treatmentServiceFeign.pageGoods(query);
        List<GoodsVO> collect = page.getResult().stream().map(t -> {
            String itemPic = t.getItemPic();
            GoodsVO goodsVO = new GoodsVO();
            goodsVO.setProductId(t.getId());
            goodsVO.setProductName(t.getName());
            goodsVO.setProductPic(StringHelper.splitFirst(itemPic));
            goodsVO.setProductPrice(t.getPrice());
            goodsVO.setProductType(FALSE.getCode());
            return goodsVO;
        }).collect(Collectors.toList());
        PageInfo<GoodsVO> pageInfo = new PageInfo<>(collect);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
    }

    @Override
    public PageInfo<VirtualProductVO> pageVirtual(VirtualProductQuery query) {
        return discountFeign.pageVirtual(query);
    }

    @Override
    public GoodsDetailVO goodsDetail(Integer itemId) {
        BaseOralTariff tariff = treatmentServiceFeign.findBaseOralTariffById(itemId);
        if (Objects.isNull(tariff)) {
            throw ClientServiceException.wrap(PRODUCT_LACK);
        }
        GoodsDetailVO detailVO = BeanCopierUtils.generalCopyBean(tariff, GoodsDetailVO.class);
        detailVO.setProductType(FALSE.getCode());
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
        if (Objects.equals(FALSE.getCode(), type)) {
            BaseOralTariffCategory queryForm = new BaseOralTariffCategory();
            queryForm.setInservice(true);
            List<BaseOralTariffCategory> categoryList = treatmentServiceFeign.findBaseOralTariffCategoryList(queryForm);
            list = categoryList.stream().map(t -> BeanCopierUtils.generalCopyBean(t, ProductTypeVO.class)).collect(toList());
        }
        if (Objects.equals(TRUE.getCode(), type)) {
            ProductTypeQueryForm queryForm = new ProductTypeQueryForm();
            queryForm.setWhetherPage(false);
            ResponseResult<PageInfo<ProductTypeVO>> page = discountFeign.findList(queryForm);
            PageInfo<ProductTypeVO> data = page.getData();
            list = Objects.nonNull(data) ? data.getList() : list;
        }
        return list;
    }

    @Override
    public List<OrderItemBO> listProductOrderItem(Collection<Integer> ids, Integer type) {
        List<ProductBO> itemBoList = Lists.newArrayList();
        if (FALSE.equals(type)) {
            itemBoList = treatmentServiceFeign.listOnSaleOral(ids);
        }
        //原始商品集合(虚拟服务)
        if (TRUE.equals(type)) {
            itemBoList = discountFeign.listOnSaleOral(ids);
        }
        return assembleOrderItemBO(itemBoList);
    }


    @Override
    public void lockProductStock(List<LockStockForm> form, Integer type) {
        if (FALSE.equals(type)) {
            treatmentServiceFeign.lockGoodsStock(form);
        }
        if (TRUE.equals(type)) {
            discountFeign.lockVirtualStock(form);
        }
    }

    @Override
    public FansAddressBO getAddress(Integer fansId, Integer addressId, Integer deliveryType) {
        FansAddressBO addressBO = new FansAddressBO();
        //配送方式（0->自提 1->配送）
        boolean b = Objects.isNull(addressId) && Objects.nonNull(fansId);
        if (Objects.equals(FALSE.getCode(), deliveryType)) {
            if (Objects.nonNull(addressId)) {
                FansPickUp pickUp = pickUpService.selectById(addressId);
                addressBO = BeanCopierUtils.generalCopyBean(pickUp, FansAddressBO.class);
            }
            if (b) {
                FansPickUp defaultAddress = pickUpService.getDefaultAddress(fansId);
                addressBO = Objects.isNull(defaultAddress) ? addressBO : BeanCopierUtils.generalCopyBean(defaultAddress, FansAddressBO.class);
            }
        }
        if (Objects.equals(TRUE.getCode(), deliveryType)) {
            if (Objects.nonNull(addressId)) {
                FansReceiveAddress address = fansReceiveAddressService.getById(addressId);
                addressBO = BeanCopierUtils.generalCopyBean(address, FansAddressBO.class);
            }
            if (b) {
                FansReceiveAddress defaultAddress = fansReceiveAddressService.getDefaultAddress(fansId);
                addressBO = Objects.isNull(defaultAddress) ? addressBO : BeanCopierUtils.generalCopyBean(defaultAddress, FansAddressBO.class);
            }
        }
        return addressBO;
    }


    private List<OrderItemBO> assembleOrderItemBO(List<ProductBO> itemList) {
        return itemList.stream().map(t -> {
            OrderItemBO orderItemBO = BeanCopierUtils.generalCopyBean(t, OrderItemBO.class);
            String itemPic = t.getProductPic();
            orderItemBO.setProductPic(StringHelper.splitFirst(itemPic));
            return orderItemBO;
        }).collect(toList());
    }
}
