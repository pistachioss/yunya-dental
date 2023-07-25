package com.yunya.modules.discount.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.bo.PatientCardBo;
import com.yunya.feign.discount.domain.query.DeductionOrderQuery;
import com.yunya.feign.discount.domain.query.DeductionPatientQuery;
import com.yunya.feign.discount.domain.vo.PatientDeductionBaseVO;
import com.yunya.feign.discount.domain.vo.PatientDeductionOrderVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.models.discount.ProductType;
import com.yunya.models.discount.SalesChannel;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.enums.UseWayEnum;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.yunya.framework.common.constant.BusinessConstants.MINI_CARD_REMARK;
import static com.yunya.modules.discount.enums.TrueFalseEnum.TRUE;
import static java.util.stream.Collectors.toList;

/**
 * @auther: xy
 * @date: 2023/6/26
 */
@Service
@Slf4j
public class DeductionPatientBiz {
    @Resource
    private CardMapper cardMapper;
    @Resource
    private SalesChannelMapper salesChannelMapper;
    @Resource
    private ProductTypeMapper productTypeMapper;
    @Resource
    private CouponOrderMapper couponOrderMapper;
    @Resource
    private CouponChangeRecordMapper changeRecordMapper;
    @Resource
    private CouponOrderVirtualMapper virtualMapper;
    @Resource
    private CouponBillPayMapper billPayMapper;
    @Resource
    private CardBiz cardBiz;
    @Resource
    private CouponCommonInfoBiz couponBiz;
    @Resource
    private DeductionPeriodBiz periodBiz;
    @Resource
    private SalesChannelBiz salesChannelBiz;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;

    public List<PatientDeductionBaseVO> deductionList(Integer patientId, DeductionPatientQuery query) {
        List<PatientCardBo> list = cardMapper.listPatientDeductionByParam(patientId, query.getCouponName());
        //对象转换
        return list.stream().map(this::patientCardBoConvertVo)
                .collect(toList());
    }

    private PatientDeductionBaseVO patientCardBoConvertVo(PatientCardBo bo) {
        PatientDeductionBaseVO ownCardVo = BeanCopierUtils.generalCopyBean(bo, PatientDeductionBaseVO.class);
        //查询销售渠道
        SalesChannel salesChannel = salesChannelMapper.selectByPrimaryKey(bo.getSaleChannelId());
        ownCardVo.setSaleChannelName(salesChannel == null ? null : salesChannel.getName());
        ownCardVo.setCouponTypeName(CouponTypeEnum.getValue(bo.getCouponType()));
        if (Objects.nonNull(bo.getBuyerId())) {
            ownCardVo.setBuyerName(systemServiceFeign.findSysUserEmployeeInfoByUserId(bo.getBuyerId()).getName());
        }
        if (Objects.nonNull(bo.getBuyerId())) {
            ownCardVo.setOwnName(systemServiceFeign.findSysUserEmployeeInfoByUserId(Integer.valueOf(bo.getSoldTarget())).getName());
        }
        ownCardVo.setActiveStatus(bo.getStatus() >= 2 ? 1 : 0);
        if (Objects.nonNull(bo.getCardOwner())) {
            ownCardVo.setActivePatient(patientFeign.findPatientInfoByIds(Lists.newArrayList(bo.getCardOwner())).get(0).getName());
        }
        ownCardVo.setUseStatus(bo.getStatus() >= 3 ? 1 : 0);
        //产品分类
        ProductType productType = productTypeMapper.selectByPrimaryKey(bo.getProductTypeId());
        ownCardVo.setProductTypeName(productType == null ? null : productType.getName());
        ownCardVo.setUseWayName(UseWayEnum.getValue(bo.getUseWay()));
        ownCardVo.setUseDeadline(bo.getUseDeadline() == null ? "永久有效" : bo.getUseDeadline());
        ownCardVo.setPayChannel(TRUE.getCode());
        String remark = bo.getRemark();
        if (StringUtils.isNotBlank(remark)) {
            ownCardVo.setPayChannel(Objects.equals(MINI_CARD_REMARK, remark) ? 0 : 1);
        }
        if (Objects.nonNull(bo.getBuyerId())) {
            ownCardVo.setPayChannel(2);
        }
        return ownCardVo;
    }

    public List<PatientDeductionOrderVO> orderList(Integer patientId, DeductionOrderQuery query) {
        List<PatientDeductionOrderVO> list = couponOrderMapper.listPatientDeductionByParam(patientId, query);
        return null;
    }
}
