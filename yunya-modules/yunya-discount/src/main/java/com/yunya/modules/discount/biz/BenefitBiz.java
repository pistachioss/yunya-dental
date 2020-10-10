package com.yunya.modules.discount.biz;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.bo.ItemUseBenefitBo;
import com.yunya.feign.discount.domain.bo.OrderItemUseBo;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.AuthItemBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.discount.AuthDiscountBenefit;
import com.yunya.models.discount.CardBenefit;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.OrderBenefit;
import com.yunya.models.discount.PackageCouponItem;
import com.yunya.models.discount.SpecialPackageCouponItem;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.modules.discount.enums.DiscountError;
import com.yunya.modules.discount.mapper.AuthDiscountBenefitMapper;
import com.yunya.modules.discount.mapper.CardBenefitMapper;
import com.yunya.modules.discount.mapper.DiscountCouponMapper;
import com.yunya.modules.discount.mapper.OrderBenefitMapper;
import com.yunya.modules.discount.mapper.PackageCouponItemMapper;
import com.yunya.modules.discount.mapper.SpecialPackageCouponItemMapper;
import com.yunya.modules.discount.mapper.VoucheCouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.yunya.framework.common.constant.BusinessConstants.MEDICAL_APPLY_LOCK_SEC;
import static com.yunya.modules.discount.enums.BenefitOperateEnum.CHARGE;
import static com.yunya.modules.discount.enums.BenefitTypeEnum.COUPON_TYPE;
import static com.yunya.modules.discount.enums.ChoiceBenefitTypeEnum.AUTH_BENEFIT;
import static com.yunya.modules.discount.enums.ChoiceBenefitTypeEnum.CARD_BENEFIT;
import static com.yunya.modules.discount.enums.CouponTypeEnum.DISCOUNT;
import static com.yunya.modules.discount.enums.CouponTypeEnum.EXCHANGE;
import static com.yunya.modules.discount.enums.CouponTypeEnum.SPECIAL_PACKAGE;
import static com.yunya.modules.discount.enums.CouponTypeEnum.VOUCHER;
import static com.yunya.modules.discount.enums.TrueFalseEnum.FALSE;
import static java.util.stream.Collectors.toMap;

/**
 * @author xiangyang
 * @date 2020/10/9
 */
@Service
@Slf4j
public class BenefitBiz {

	@Resource
	private CardBiz cardBiz;
	@Resource
	private CardBenefitMapper cardBenefitMapper;
	@Resource
    private OrderBenefitMapper orderBenefitMapper;
	@Resource
	private AuthDiscountBenefitMapper authDiscountBenefitMapper;
	@Resource
	private VoucheCouponMapper voucherMapper;
	@Resource
	private DiscountCouponMapper discountCouponMapper;
	@Resource
	private PackageCouponItemMapper packageCouponItemMapper;
	@Resource
	private SpecialPackageCouponItemMapper specialPackageCouponItemMapper;
	@Resource
	private RemoteSystemServiceFeign systemServiceFeign;
	@Resource
	private RemoteTreatmentServiceFeign treatmentServiceFeign;
	@Resource
	private RedisUtils redisUtils;

	/**
	 * 收费 - 卡券保存优惠
	 *
	 * @param model model
	 * @return ResponseResult
	 */
	public ResponseResult<PatientOrderBenefitVo> saveCardBenefit(PatientOrderBenefitModel model) {
		boolean locked = false;
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		Integer orderId = model.getOrderId();
		String lockKey = Joiner.on(":").join(RedisConstants.LOCK_SUBMIT_BENEFIT, orderId);
		String lockVal = String.valueOf(loginUserId);
		try {
			// 1. 锁定产品
			locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
			if (!locked) {
				log.warn("【锁定失败】订单[{}]正在提交优惠，不能重复提交", orderId);
				return ResponseUtil.error(DiscountError.ORDER_ON_SUBMITTING);
			}
			log.info("【锁定成功】准备提交订单使用优惠...");

			List<CardBenefit> list = Lists.newArrayList();
			PatientChooseBenefitForm benefitForm = benefitTransformToForm(model);
			//查询订单项目对应的优惠
			ResponseResult result = cardBiz.choiceBenefitBo(benefitForm);
			if (!FALSE.getCode().equals(result.getStatus())) {
				return result;
			}
			List<OrderItemUseBo> data = (List<OrderItemUseBo>) result.getData();
			CardBenefit cardBenefit;
			for (OrderItemUseBo itemBenefitBo : data) {
				List<ItemUseBenefitBo> itemUseBenefitBos = itemBenefitBo.getItemUseBenefitBos();
				if (CollectionUtils.isNotEmpty(itemUseBenefitBos)) {
					for (ItemUseBenefitBo itemUseBenefitBo : itemUseBenefitBos) {
						cardBenefit = new CardBenefit();
						cardBenefit.setOrgId(benefitForm.getOrgId());
						cardBenefit.setOrderId(benefitForm.getOrderId());
						cardBenefit.setOrderDetailId(itemBenefitBo.getOrderDetailId());
						cardBenefit.setPatientId(benefitForm.getPatientId());
						cardBenefit.setCardId(itemUseBenefitBo.getBenefitId());
						cardBenefit.setItemId(itemBenefitBo.getItemId());
						cardBenefit.setItemType(itemBenefitBo.getType());
						cardBenefit.setBenefitType(itemUseBenefitBo.getBenefitType());
						cardBenefit.setItemIndex(itemUseBenefitBo.getItemIndex());
						cardBenefit.setBenefitAmount(itemUseBenefitBo.getBenefitAmount());
						cardBenefit.setOperateType(CHARGE.getCode());
						cardBenefit.setCrtId(loginUserId);
						cardBenefit.setUpdId(loginUserId);
						calculateWordLoad(itemUseBenefitBo, itemBenefitBo, cardBenefit);
						list.add(cardBenefit);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(list)) {
                BigDecimal totalBenefitAmount = data.stream().map(OrderItemUseBo::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                OrderBenefit orderBenefit = new OrderBenefit();
                orderBenefit.setOrderId(orderId);
                orderBenefit.setTotalAmount(totalBenefitAmount);
				orderBenefit.setBenefitType(CARD_BENEFIT.getCode());
                orderBenefit.setCrtId(loginUserId);
                orderBenefit.setUpdId(loginUserId);
                orderBenefitMapper.insertSelective(orderBenefit);
				cardBenefitMapper.insertList(list);
			}
			return ResponseUtil.success();
		} finally {
			if (locked) {
				log.info("【解锁成功】");
				redisUtils.unlock(lockKey, lockVal);
			}
		}
	}

	/**
	 * 收费 - 授权折扣优惠
	 *
	 * @param model model
	 * @return ResponseResult
	 */
	public ResponseResult saveAuthBenefit(AuthDiscountBenefitModel model) {
		boolean locked = false;
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		Integer orderId = model.getOrderId();
		String lockKey = Joiner.on(":").join(RedisConstants.LOCK_SUBMIT_BENEFIT, orderId);
		String lockVal = String.valueOf(loginUserId);
		try {
			// 1. 锁定产品
			locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
			if (!locked) {
				log.warn("【锁定失败】订单[{}]正在提交优惠，不能重复提交", orderId);
				return ResponseUtil.error(DiscountError.ORDER_ON_SUBMITTING);
			}
			log.info("【锁定成功】准备提交订单使用优惠...");

			SysEmployee employee = systemServiceFeign.findSysEmployeeById(model.getAuthorizedId());
			if (employee == null || employee.getDiscount()) {
				log.warn("【授权折扣优惠】授权人没有权限进行授权折扣");
				return ResponseUtil.error(DiscountError.EMPLOYEE_NO_AUTH_DISCOUNT);
			}
			//获取订单明细
			List<OrderDetail> orderDetails = treatmentServiceFeign.findOrderDetailByOrderRecordId(orderId);
			if (CollectionUtils.isNotEmpty(orderDetails)) {
				return ResponseUtil.error(DiscountError.ORDER_NOT_EXIST);
			}
			List<AuthItemBenefitModel> itemBenefits = model.getItemBenefits();
			AuthDiscountBenefit authDiscountBenefit;
			List<AuthDiscountBenefit> list = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(itemBenefits)) {
				RestErrorBo errorBo = checkAuthItem(orderDetails, itemBenefits);
				if (errorBo.getError() != null) {
					return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
				}
				for (AuthItemBenefitModel itemBenefit : itemBenefits) {
					authDiscountBenefit = new AuthDiscountBenefit();
					authDiscountBenefit.setOrgId(model.getOrgId());
					authDiscountBenefit.setOrderId(orderId);
					authDiscountBenefit.setOrderDetailId(itemBenefit.getOrderDetailId());
					authDiscountBenefit.setPatientId(model.getPatientId());
					authDiscountBenefit.setItemId(itemBenefit.getItemId());
					authDiscountBenefit.setItemType(itemBenefit.getType());
					authDiscountBenefit.setBenefitAmount(itemBenefit.getBenefitAmount());
					authDiscountBenefit.setOperateType(CHARGE.getCode());
					authDiscountBenefit.setCrtId(loginUserId);
					authDiscountBenefit.setUpdId(loginUserId);
					list.add(authDiscountBenefit);
				}
				if (CollectionUtils.isNotEmpty(list)) {
					BigDecimal totalBenefitAmount = itemBenefits.stream().map(AuthItemBenefitModel::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
					OrderBenefit orderBenefit = new OrderBenefit();
					orderBenefit.setOrderId(orderId);
					orderBenefit.setTotalAmount(totalBenefitAmount);
					orderBenefit.setBenefitType(AUTH_BENEFIT.getCode());
					orderBenefit.setAuthorizedId(model.getAuthorizedId());
					orderBenefit.setRemark(model.getRemark());
					orderBenefit.setCrtId(loginUserId);
					orderBenefit.setUpdId(loginUserId);
					orderBenefitMapper.insertSelective(orderBenefit);
					authDiscountBenefitMapper.insertList(list);
				}
			}
			return ResponseUtil.success();
		} finally {
			if (locked) {
				log.info("【解锁成功】");
				redisUtils.unlock(lockKey, lockVal);
			}
		}

	}

	/**
	 * 对象转换
	 *
	 * @param model model
	 * @return PatientChooseBenefitForm
	 */
	private PatientChooseBenefitForm benefitTransformToForm(PatientOrderBenefitModel model) {
		PatientChooseBenefitForm benefitForm = BeanCopierUtils.generalCopyBean(model, PatientChooseBenefitForm.class);
		return benefitForm;
	}

	/**
	 * 计算工作量
	 *
	 * @param itemUseBenefitBo itemUseBenefitBo
	 * @param itemBenefitBo    itemBenefitBo
	 * @param cardBenefit      cardBenefit
	 */
	private void calculateWordLoad(ItemUseBenefitBo itemUseBenefitBo, OrderItemUseBo itemBenefitBo, CardBenefit cardBenefit) {
		Example example;
		if (COUPON_TYPE.equals(itemUseBenefitBo.getBenefitType())) {
			Integer couponType = itemUseBenefitBo.getCouponType();
			Integer couponId = itemUseBenefitBo.getCouponId();
			cardBenefit.setCouponId(couponId);
			cardBenefit.setCouponType(couponType);
			if (VOUCHER.equals(couponType)) {
				example = new Example(VoucheCoupon.class);
				example.createCriteria().andEqualTo("couponId", couponId);
				VoucheCoupon voucheCoupon = voucherMapper.selectOneByExample(example);
				if (voucheCoupon != null) {
					cardBenefit.setSupplyWorkload(itemUseBenefitBo.getBenefitAmount().multiply(voucheCoupon.getWorkloadRate()));
				}
			}
			if (DISCOUNT.equals(couponType)) {
				example = new Example(DiscountCoupon.class);
				example.createCriteria().andEqualTo("couponId", couponId);
				DiscountCoupon discountCoupon = discountCouponMapper.selectOneByExample(example);
				if (discountCoupon != null) {
					cardBenefit.setSupplyWorkload(itemUseBenefitBo.getBenefitAmount().multiply(discountCoupon.getWorkloadRate()));
				}
			}
			if (EXCHANGE.equals(couponType)) {
				example = new Example(PackageCouponItem.class);
				example.createCriteria().andEqualTo("couponId", couponId).andEqualTo("itemId", itemBenefitBo.getItemId()).
						andEqualTo("type", itemBenefitBo.getType());
				PackageCouponItem packageCouponItem = packageCouponItemMapper.selectOneByExample(example);
				if (packageCouponItem != null) {
					cardBenefit.setSupplyWorkload(BigDecimal.ONE.multiply(packageCouponItem.getWorkloadLoad()));
				}
			}
			if (SPECIAL_PACKAGE.equals(couponType)) {
				example = new Example(SpecialPackageCouponItem.class);
				example.createCriteria().andEqualTo("couponId", couponId).andEqualTo("itemId", itemBenefitBo.getItemId()).
						andEqualTo("type", itemBenefitBo.getType());
				SpecialPackageCouponItem specialPackageCouponItem = specialPackageCouponItemMapper.selectOneByExample(example);
				if (specialPackageCouponItem != null) {
					cardBenefit.setSupplyWorkload(BigDecimal.ONE.multiply(specialPackageCouponItem.getWorkloadLoad()));
				}
			}
		}
	}

	private RestErrorBo checkAuthItem(List<OrderDetail> orderDetails, List<AuthItemBenefitModel> itemBenefits) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		Map<Integer, OrderDetail> orderDetailMap = orderDetails.stream().collect(toMap(OrderDetail::getId, Function.identity(), (v1, v2) -> v2));
		for (AuthItemBenefitModel item : itemBenefits) {
			OrderDetail detail = orderDetailMap.get(item.getOrderDetailId());
			if (detail == null) {
				errorBo.setError(DiscountError.ORDER_ITEM_NOT_EXIST);
				errorBo.setMsg(item.getItemId());
				return errorBo;
			}
			if (detail.getReceivableAmount().compareTo(item.getBenefitAmount()) < 0) {
				errorBo.setError(DiscountError.AUTH_BENEFIT_AMOUNT_ERROR);
				errorBo.setMsg(item.getItemId());
				return errorBo;
			}
		}
		return errorBo;
	}
}
