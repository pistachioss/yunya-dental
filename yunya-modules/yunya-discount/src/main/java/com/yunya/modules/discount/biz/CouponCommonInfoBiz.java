package com.yunya.modules.discount.biz;

import com.github.pagehelper.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.feign.ivy_mini.domain.bo.ProductBO;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.VirtualDetailVO;
import com.yunya.feign.ivy_mini.domain.vo.VirtualProductVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.CouponFileInfo;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.modules.discount.enums.TrueFalseEnum.*;
import static java.util.stream.Collectors.*;

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

    @Resource
    private CouponFileInfoBiz fileInfoBiz;
    @Resource
    private ProductTypeBiz productTypeBiz;

    public PageInfo<VirtualProductVO> pageVirtual(VirtualProductQuery query) {
        Page<CouponCommonInfo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Example example = new Example(CouponCommonInfo.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("isOnlineSale", true);
        if (Objects.nonNull(query.getProductCategoryId())) {
            criteria.andEqualTo("productTypeId", query.getProductCategoryId());
        }
        if (StringUtils.isNotBlank(query.getKeyword())) {
            criteria.andLike("name", "%" + query.getKeyword() + "%");
        }
        mapper.selectByExample(example);
        List<CouponCommonInfo> coupons = page.getResult();
        Map<Integer, CouponFileInfo> fileInfoMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(coupons)) {
            List<Integer> ids = coupons.stream().map(CouponCommonInfo::getId).collect(Collectors.toList());
            List<CouponFileInfo> files = fileInfoBiz.listByCouponIds(ids, 0);
            fileInfoMap = files.stream()
                    .collect(Collectors.toMap(CouponFileInfo::getCouponId, Function.identity(), (o, n) -> n));
        }
        Map<Integer, CouponFileInfo> finalFileInfoMap = fileInfoMap;
        List<VirtualProductVO> collect = coupons.stream().map(t -> {
            CouponFileInfo couponFileInfo = finalFileInfoMap.get(t.getId());
            String couponPic = Objects.nonNull(couponFileInfo) ? couponFileInfo.getPath() : null;
            VirtualProductVO virtualProductVO = new VirtualProductVO();
            virtualProductVO.setProductId(t.getId());
            virtualProductVO.setProductName(t.getName());
            virtualProductVO.setProductPic(couponPic);
            virtualProductVO.setProductPrice(t.getSoldAmount());
            virtualProductVO.setProductType(TRUE.getCode());
            return virtualProductVO;
        }).collect(Collectors.toList());
        PageInfo<VirtualProductVO> pageInfo = new PageInfo<>(collect);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
    }

    public VirtualDetailVO couponDetail(Integer couponId) {
        VirtualDetailVO vo = new VirtualDetailVO();
        CouponCommonInfo commonInfo = selectById(couponId);
        vo.setProductType(TRUE.getCode());
        if (Objects.nonNull(commonInfo)) {
            vo.setProductId(commonInfo.getId());
            vo.setProductName(commonInfo.getName());
            vo.setCategoryId(commonInfo.getProductTypeId());
            vo.setProductPrice(commonInfo.getSoldAmount());
            List<CouponFileInfo> couponFileInfos = fileInfoBiz.listByCouponIds(Lists.newArrayList(couponId), null);
            if (CollectionUtils.isNotEmpty(couponFileInfos)) {
                Optional<CouponFileInfo> fileInfo = couponFileInfos.stream()
                        .filter(t -> Objects.equals((byte) 2, t.getFileType())).findFirst();
                Optional<CouponFileInfo> fileInfo1 = couponFileInfos.stream()
                        .filter(t -> Objects.equals((byte) 0, t.getFileType())).findFirst();
                vo.setDetailHtml(fileInfo.map(CouponFileInfo::getPath).orElse(null));
                vo.setProductPics(fileInfo1.map(t -> {
                    String path = t.getPath();
                    return StringUtils.isNotBlank(path) ? Lists.newArrayList(Splitter.on(",").split(path)) : null;
                }).orElse(null));
            }
        }
        return vo;
    }

    public List<ProductBO> listOnSaleOral(Collection<Integer> ids) {
        Example example = new Example(CouponCommonInfo.class);
        example.selectProperties("id","type","name","couponCode","soldAmount");
        example.createCriteria().andIn("id", ids)
                .andEqualTo("isOnlineSale", true)
                .andEqualTo("inservice", true);
        List<CouponCommonInfo> couponCommonInfos = mapper.selectByExample(example);
        //产品图片
        List<CouponFileInfo> couponFileInfos = fileInfoBiz.listByCouponIds(ids, 0);
        //产品分类
        ProductTypeQueryForm queryForm = new ProductTypeQueryForm();
        queryForm.setWhetherPage(false);
        PageInfo<ProductTypeVO> data = productTypeBiz.findList(queryForm);
        return assembleProductBO(couponCommonInfos, couponFileInfos, data.getList());
    }

    private List<ProductBO> assembleProductBO(List<CouponCommonInfo> coupons, List<CouponFileInfo> files, List<ProductTypeVO> cateGoryList) {
        Map<Integer, CouponFileInfo> fileInfoMap = files.stream()
                .collect(Collectors.toMap(CouponFileInfo::getCouponId, Function.identity(), (o, n) -> n));
        List<ProductBO> collect = coupons.stream().map(t -> {
            CouponFileInfo couponFileInfo = fileInfoMap.get(t.getId());
            String couponPic = Objects.nonNull(couponFileInfo) ? couponFileInfo.getPath() : null;
            ProductBO bo = new ProductBO();
            bo.setProductId(t.getId());
            bo.setProductName(t.getName());
            bo.setProductPic(couponPic);
            bo.setProductCode(t.getCouponCode());
            bo.setProductPrice(t.getSoldAmount());
            bo.setProductType(TRUE.getCode());
            bo.setCategoryId(t.getProductTypeId());
            return bo;
        }).collect(Collectors.toList());
        Map<Integer, ProductTypeVO> categoryMap = cateGoryList.stream().collect(toMap(ProductTypeVO::getId, Function.identity()));
        collect.stream()
                .filter(t -> categoryMap.containsKey(t.getCategoryId()))
                .forEach(t -> {
                    ProductTypeVO category = categoryMap.get(t.getCategoryId());
                    t.setCategoryName(category.getName());
                });
        return collect;
    }

    public void lockVirtualStock(Integer productId, Integer quantity) {
        CouponCommonInfo commonInfo = mapper.selectByPrimaryKey(productId);
        commonInfo.setSale(commonInfo.getSale() + quantity);
        mapper.updateByPrimaryKeySelective(commonInfo);
    }
}
