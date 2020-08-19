package com.yunya.modules.discount.biz;

import com.github.pagehelper.*;
import com.yunya.feign.discount.domain.bo.*;
import com.yunya.feign.discount.domain.query.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.system.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.biz.*;
import com.yunya.framework.common.utils.*;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.enums.*;
import com.yunya.modules.discount.mapper.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.util.*;

import static java.util.stream.Collectors.*;
/**
 * 描述:
 *
 * @author xiangyang
 * @create 2020-08-17
 */
@Service
public class CardBiz extends BaseBiz<CardMapper, Card> {

    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private CouponCommonInfoMapper couponMapper;

    public List<GenerateAllocatePageVo> getCouponAllocatePage(CouponAllocateQuery query) {
        Page<GenerateAllocateBo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        //分页查询
        couponMapper.listBatchAllocateByParam(query.getKeyword(), query.getCouponTypeList());
        //属性转换
        List<GenerateAllocatePageVo> list = page.getResult().stream().map(this::allocateBoConvertVo).collect(toList());
        return list;
    }

    private void calculateNumber(int startIndex, int count) {

    }

    private GenerateAllocatePageVo allocateBoConvertVo(GenerateAllocateBo bo) {
        //属性复制
        GenerateAllocatePageVo vo = BeanCopierUtils.generalCopyBean(bo, GenerateAllocatePageVo.class);
        //提交人信息
        SysUserInfoDetail submitUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(bo.getSubmitUserId());
        //配给人信息
        SysUserInfoDetail allocateUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(bo.getAllocateUserId());
        vo.setSubmitterName(submitUser == null ?  null : submitUser.getName());
        vo.setAllocateUserName(allocateUser == null ? null : allocateUser.getName());
        vo.setCouponTypeName(CouponTypeEnum.getValue(bo.getCouponType()));
        return vo;
    }
}
