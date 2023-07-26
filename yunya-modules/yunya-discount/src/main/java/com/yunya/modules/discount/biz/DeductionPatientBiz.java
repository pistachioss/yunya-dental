package com.yunya.modules.discount.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.bo.PatientCardBo;
import com.yunya.feign.discount.domain.form.DeductionActiveForm;
import com.yunya.feign.discount.domain.form.DeductionChangeForm;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.query.CouponRefundQuery;
import com.yunya.feign.discount.domain.query.DeductionOrderQuery;
import com.yunya.feign.discount.domain.query.DeductionPatientQuery;
import com.yunya.feign.discount.domain.vo.PatientDeductionBaseVO;
import com.yunya.feign.discount.domain.vo.PatientDeductionOrderVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.ProductType;
import com.yunya.models.discount.SalesChannel;
import com.yunya.modules.discount.enums.CouponOrderError;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.enums.DiscountError;
import com.yunya.modules.discount.enums.UseWayEnum;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Collection;
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
    private CardBenefitMapper cardBenefitMapper;
    @Resource
    private CouponOrderBiz couponOrderBiz;
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
        ownCardVo.setChangeStatus(1);
        String remark = bo.getRemark();
        if (StringUtils.isNotBlank(remark)) {
            ownCardVo.setPayChannel(Objects.equals(MINI_CARD_REMARK, remark) ? 0 : 1);
            ownCardVo.setChangeStatus(Objects.equals(MINI_CARD_REMARK, remark) ? 0 : 1);
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

    @Transactional(rollbackFor = Exception.class)
    public void active(DeductionActiveForm form) {
        OwnCardActiveForm activeForm = new OwnCardActiveForm();
        activeForm.setCardId(form.getCardId());
        ResponseResult responseResult = cardBiz.ownActiveCard(form.getPatientId(), activeForm);
        if (!Objects.equals(0, responseResult.getStatus())) {
            throw ClientServiceException.wrap(responseResult.getStatus(), responseResult.getMsg());
        }
    }

    public void cancelActive(DeductionActiveForm form) {
        Integer patientId = form.getPatientId();
        Integer cardId = form.getCardId();
        Card card = cardMapper.selectByPrimaryKey(cardId);
        if (card == null) {
            throw ClientServiceException.wrap(DiscountError.CARD_NOT_EXIST);
        }
        if (!patientId.equals(card.getPatientId())) {
            throw ClientServiceException.wrap(DiscountError.OTHER_CARD_NOT_ALLOW_DELETE);
        }
        int useCount = cardBenefitMapper.countCardUsed(cardId);
        if (useCount > 0) {
            throw ClientServiceException.wrap(DiscountError.CARD_IS_USED);
        }
        cancel(card);
    }

    public void refundDetail(CouponRefundQuery query) {

    }

    private List<Card> listCard(Collection<Integer> cardIds) {
        Example example = new Example(Card.class);
        example.createCriteria().andIn("id", cardIds);
        return cardMapper.selectByExample(example);
    }

    public void cancel(Card card ) {
        card.setStatus(1);
        card.setPatientId(null);
        card.setActiveOrgId(null);
        card.setActiveUserId(null);
        card.setSharer(null);
        card.setActiveDate(null);
        cardMapper.updateByPrimaryKey(card);
    }

    public void change(DeductionChangeForm form) {
        Integer cardId = form.getCardId();
        Card card = cardMapper.selectByPrimaryKey(cardId);
        if (card == null) {
            throw ClientServiceException.wrap(DiscountError.CARD_NOT_EXIST);
        }
        Integer status = card.getStatus();
        if (Objects.equals(MINI_CARD_REMARK, card.getRemark())) {
            throw ClientServiceException.wrap(CouponOrderError.CHANGE_ERROR);
        }
        if (status >= 2) {
            throw ClientServiceException.wrap(CouponOrderError.CARD_ACTIVED);
        }
        int useCount = cardBenefitMapper.countCardUsed(cardId);
        if (useCount > 0) {
            throw ClientServiceException.wrap(DiscountError.CARD_IS_USED);
        }
        change(card, form);
    }

    public void change(Card card,DeductionChangeForm form) {
        card.setSoldTarget(form.getPatientId().toString());
        card.setSoldPhoneNumber(form.getMobile());
        cardMapper.updateByPrimaryKey(card);
    }
}
