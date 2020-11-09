package com.yunya.modules.discount.biz;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.domain.bo.ItemUseBenefitBo;
import com.yunya.feign.discount.domain.bo.OrderItemUseBo;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.AuthItemBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
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
import com.yunya.models.discount.CouponCommonInfo;
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
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.DiscountCouponMapper;
import com.yunya.modules.discount.mapper.OrderBenefitMapper;
import com.yunya.modules.discount.mapper.PackageCouponItemMapper;
import com.yunya.modules.discount.mapper.SpecialPackageCouponItemMapper;
import com.yunya.modules.discount.mapper.VoucheCouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.modules.discount.enums.BenefitOperateEnum.*;
import static com.yunya.modules.discount.enums.BenefitTypeEnum.*;
import static com.yunya.modules.discount.enums.ChoiceBenefitTypeEnum.*;
import static com.yunya.modules.discount.enums.CouponTypeEnum.*;
import static com.yunya.modules.discount.enums.TrueFalseEnum.*;
import static java.util.stream.Collectors.*;

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
	private CouponCommonInfoMapper couponMapper;
	@Resource
	private RemoteSystemServiceFeign systemServiceFeign;
	@Resource
	private RemoteTreatmentServiceFeign treatmentServiceFeign;
	@Resource
	private RedisUtils redisUtils;

	private static final Integer AUTH_BENEFIT_TYPE = 2;

	/**
	 * 收费 - 卡券保存优惠
	 *
	 * @param model model
	 * @return ResponseResult
	 */
	@Transactional
	public ResponseResult<PatientOrderBenefitVo> saveCardBenefit(PatientOrderBenefitModel model) {
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		Integer orderId = model.getOrderId();
		try {
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
						cardBenefit.setSort(itemUseBenefitBo.getId());
						calculateWordLoad(itemUseBenefitBo, itemBenefitBo, cardBenefit);
						list.add(cardBenefit);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(list)) {
                BigDecimal totalBenefitAmount = data.stream()
		                .filter(obj -> obj.getBenefitAmount() != null).map(OrderItemUseBo::getBenefitAmount)
		                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
			//解锁卡券
			cardBiz.manualUnLock(model.getPatientId(), RedisConstants.LOCK_CHOICE_CARD);
			log.info("【保存卡券优惠解锁成功】");
		}
	}

	/**
	 * 收费 - 授权折扣优惠
	 *
	 * @param model model
	 * @return ResponseResult
	 */
	@Transactional
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
	 * 查询订单优惠明细
	 * @param orderId 订单id
	 * @return list
	 */
	public List<OrderBenefitDetailVo> getOrderBenefit(Integer orderId) {
		List<OrderBenefitDetailVo> resultList = Lists.newArrayList();
		//查询订单优惠汇总信息
		OrderBenefit summary = getOrderBenefitSummary(orderId);
		if (summary == null) {
			return resultList;
		}
		if (CARD_BENEFIT.equals(summary.getBenefitType())) {
			List<CardBenefit> cardBenefits = getOrderBenefitDetail(orderId, CardBenefit.class, cardBenefitMapper);
			if (CollectionUtils.isNotEmpty(cardBenefits)) {
				Map<Integer, List<CardBenefit>> listMap = cardBenefits.stream().collect(groupingBy(CardBenefit::getOrderDetailId));
				listMap.forEach((k,v) -> {
					OrderBenefitDetailVo vo = new OrderBenefitDetailVo();
					BigDecimal itemBenefitAmount = v.stream().map(CardBenefit::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
					vo.setOrderDetailId(k);
					vo.setItemBenefitAmount(itemBenefitAmount);
					//按照优惠提交顺序排序
					v.sort(Comparator.comparing(CardBenefit::getSort));
					List<ItemUseBenefitVo> itemBenefits = v.stream().map(obj -> {
						ItemUseBenefitVo benefitVo = new ItemUseBenefitVo();
						benefitVo.setBenefitId(obj.getCardId());
						benefitVo.setBenefitType(obj.getBenefitType());
						benefitVo.setCouponType(obj.getCouponType());
						CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(obj.getCouponId());
						benefitVo.setBenefitName(coupon == null ? null : coupon.getName());
						benefitVo.setBenefitAmount(obj.getBenefitAmount());
						return benefitVo;
					}).collect(toList());
					vo.setItemBenefitList(itemBenefits);
					resultList.add(vo);
				});
			}
		}
		if (AUTH_BENEFIT.equals(summary.getBenefitType())) {
			List<AuthDiscountBenefit> authBenefit = getOrderBenefitDetail(orderId, AuthDiscountBenefit.class, authDiscountBenefitMapper);
			if (CollectionUtils.isNotEmpty(authBenefit)) {
				Map<Integer, List<AuthDiscountBenefit>> listMap = authBenefit.stream().collect(groupingBy(AuthDiscountBenefit::getOrderDetailId));
				listMap.forEach((k,v) -> {
					OrderBenefitDetailVo vo = new OrderBenefitDetailVo();
					BigDecimal itemBenefitAmount = v.stream().map(AuthDiscountBenefit::getBenefitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
					vo.setOrderDetailId(k);
					vo.setItemBenefitAmount(itemBenefitAmount);
					List<ItemUseBenefitVo> itemBenefits = v.stream().map(obj -> {
						ItemUseBenefitVo benefitVo = new ItemUseBenefitVo();
						benefitVo.setBenefitType(AUTH_BENEFIT_TYPE);
						benefitVo.setBenefitAmount(obj.getBenefitAmount());
						return benefitVo;
					}).collect(toList());
					vo.setItemBenefitList(itemBenefits);
					resultList.add(vo);
				});
			}
		}
		return resultList;
	}

	@Transactional
	public RestErrorBo revokeBenefit(Integer orderId) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		//查询订单优惠汇总信息
		OrderBenefit summary = getOrderBenefitSummary(orderId);
		if (summary == null) {
			errorBo.setError(DiscountError.ORDER_NO_BENEFIT);
			return errorBo;
		}
		//更新优惠券
		if (CARD_BENEFIT.equals(summary.getBenefitType())) {
			CardBenefit cardBenefit = new CardBenefit();
			cardBenefit.setDeleted(TRUE.getCode());
			cardBenefit.setOperateType(MODIFY_BILL.getCode());
			cardBenefit.setUpdId(loginUserId);
			updateOrderBenefit(cardBenefit, orderId, CardBenefit.class, cardBenefitMapper);
		}
		//更新授权
		if (AUTH_BENEFIT.equals(summary.getBenefitType())) {
			AuthDiscountBenefit authDiscountBenefit = new AuthDiscountBenefit();
			authDiscountBenefit.setDeleted(TRUE.getCode());
			authDiscountBenefit.setOperateType(MODIFY_BILL.getCode());
			authDiscountBenefit.setUpdId(loginUserId);
			updateOrderBenefit(authDiscountBenefit, orderId, AuthDiscountBenefit.class, authDiscountBenefitMapper);
		}
		//更新订单优惠总信息
		OrderBenefit orderBenefit = new OrderBenefit();
		orderBenefit.setDeleted(TRUE.getCode());
		orderBenefit.setUpdId(loginUserId);
		updateOrderBenefit(orderBenefit, orderId, OrderBenefit.class, orderBenefitMapper);
		return errorBo;
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

	/**
	 * 解锁卡券资源
	 * @param patientId 患者
	 */
	private void unlockCard(Integer patientId) {
		Set<String> keys = redisUtils.keys(RedisConstants.LOCK_CHOICE_CARD + "*");
		log.info("【收费-优惠】收费使用优惠完成，开始释放卡券资源");
		//需要删除的key
		if (CollectionUtils.isNotEmpty(keys)) {
			for (String delCardId : keys) {
				String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHOICE_CARD, String.valueOf(delCardId));
				String lockVal = String.valueOf(patientId);
				// 释放患者取消选择的卡券的锁
				redisUtils.unlock(lockKey, lockVal);
			}
			log.info("【收费-优惠】解锁完成");
		}
	}

	/**
	 * 查询订单总优惠信息
	 * @param orderId
	 * @return
	 */
	private OrderBenefit getOrderBenefitSummary(Integer orderId) {
		Example example = new Example(OrderBenefit.class);
		example.createCriteria().andEqualTo("deleted", ZERO)
				.andEqualTo("orderId", orderId);
		return orderBenefitMapper.selectOneByExample(example);
	}

	/**
	 * 获取订单优惠明细（优惠券或授权折扣）
	 * @param orderId
	 * @param clazz
	 * @param mapper
	 */
	private List getOrderBenefitDetail(Integer orderId, Class<?> clazz, Mapper mapper) {
		Example example = new Example(clazz);
		example.createCriteria().andEqualTo("deleted", ZERO)
				.andEqualTo("orderId", orderId);
		return mapper.selectByExample(example);
	}

	private <T> void updateOrderBenefit(T t, Integer orderId, Class<?> clazz, Mapper<T> mapper) {
		Example example = new Example(clazz);
		example.createCriteria().andEqualTo("orderId", orderId)
				.andEqualTo("deleted", FALSE.getCode());
		mapper.updateByExampleSelective(t, example);
	}

	/**
	 * 组合优惠信息
	 *
	 * @param model 患者选择优惠信息
	 * @return set
	 */
	protected List<Integer> assembleCardIds(PatientOrderBenefitModel model) {
		List<Integer> cardIds = Lists.newArrayList();
		Integer discountId = model.getDiscountId();
		if (discountId != null) {
			cardIds.add(discountId);
		}
		List<Integer> exchangeIds = model.getExchangeIds();
		if (CollectionUtils.isNotEmpty(exchangeIds)) {
			exchangeIds.forEach(exchangeId -> cardIds.add(exchangeId));
		}
		List<Integer> packageIds = model.getPackageIds();
		if (CollectionUtils.isNotEmpty(packageIds)) {
			packageIds.forEach(packageId -> cardIds.add(packageId));
		}
		List<Integer> voucherIds = model.getVoucherIds();
		if (CollectionUtils.isNotEmpty(voucherIds)) {
			voucherIds.forEach(voucherId -> cardIds.add(voucherId));
		}
		return cardIds;
	}
}
