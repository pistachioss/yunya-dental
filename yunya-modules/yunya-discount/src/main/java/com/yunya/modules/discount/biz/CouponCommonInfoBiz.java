package com.yunya.modules.discount.biz;

import com.github.pagehelper.*;
import com.google.common.collect.Maps;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
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

    public PageInfo<VirtualProductVO> pageVirtual(VirtualProductQuery query) {
        Page<CouponCommonInfo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Example example = new Example(CouponCommonInfo.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("productTypeId", query.getProductCategoryId())
                .andEqualTo("isOnlineSale", true);
        if (StringUtils.isNotBlank(query.getKeyword())) {
            criteria.orEqualTo("name", "%" + query.getKeyword() + "%");
        }
        mapper.selectByExample(example);
        List<CouponCommonInfo> coupons = page.getResult();
        Map<Integer, CouponFileInfo> fileInfoMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(coupons)) {
            List<Integer> ids = coupons.stream().map(CouponCommonInfo::getId).collect(Collectors.toList());
            List<CouponFileInfo> files = fileInfoBiz.listByCouponIds(ids);
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
            virtualProductVO.setProductType(0);
            return virtualProductVO;
        }).collect(Collectors.toList());
        PageInfo<VirtualProductVO> pageInfo = new PageInfo<>(collect);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
    }

//  public int insertBackId(CouponCommonInfo couponCommonInfo){
//    return mapper.insertBackId(couponCommonInfo);
//  }

}
