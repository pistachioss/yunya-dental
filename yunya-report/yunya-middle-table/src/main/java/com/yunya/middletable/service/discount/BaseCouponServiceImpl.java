package com.yunya.middletable.service.discount;

import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.report.domain.bo.BaseCouponBo;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.CardMapper;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.DiscountCouponMapper;
import com.yunya.middletable.dao.discount.PackageCouponItemMapper;
import com.yunya.middletable.dao.discount.ProductTypeMapper;
import com.yunya.middletable.dao.discount.RechargeCardMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponItemMapper;
import com.yunya.middletable.dao.discount.VoucheCouponMapper;
import com.yunya.middletable.dao.discount.VoucherDiscountItemMapper;
import com.yunya.middletable.dao.report.BaseCouponItemMapper;
import com.yunya.middletable.dao.report.BaseCouponMapper;
import com.yunya.middletable.enums.MiddleError;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.ProductType;
import com.yunya.models.discount.RechargeCard;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.models.middletable.BaseCoupon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.yunya.middletable.enums.CouponTypeEnum.*;
import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Slf4j
@Service
public class BaseCouponServiceImpl extends BaseBiz<BaseCouponMapper, BaseCoupon> {
	@Resource
	private CouponMapper couponMapper;
	@Resource
	private CardMapper cardMapper;
	@Resource
	private PackageCouponItemMapper packageCouponItemMapper;
	@Resource
	private SpecialPackageCouponItemMapper specialPackageCouponItemMapper;
	@Resource
	private VoucheCouponMapper voucheCouponMapper;
	@Resource
	private DiscountCouponMapper discountCouponMapper;
	@Resource
	private VoucherDiscountItemMapper voucherDiscountItemMapper;
	@Resource
	private ProductTypeMapper productTypeMapper;
	@Resource
	private RechargeCardMapper rechargeCardMapper;
	@Resource
	private BaseCouponItemMapper baseCouponItemMapper;

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final Integer ADD = 0;
	private static final Integer UPDATE = 1;
	private static final Integer DELETE = 2;

	public RestErrorBo operateBaseCoupon(MessageModel model) {
		Integer couponId = (Integer)model.getParamMap().get("id");
		Integer operateType = model.getOperateType();
		BaseCoupon baseCoupon = mapper.selectByPrimaryKey(couponId);
		CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(couponId);
		RestErrorBo errorBo = RestErrorBo.getInstance();
		if (ADD.equals(operateType)) {
			if (baseCoupon != null) {
				log.info("优惠券[{}]数据已存在, 无法新增", couponId);
				errorBo.setError(MiddleError.COUPON_CANT_ADD);
			} else {
				if (coupon != null) {
					createCoupon(coupon);
				}
			}
		}
		if (UPDATE.equals(operateType)) {
			if (baseCoupon == null) {
				log.info("优惠券[{}]数据不存在, 无法更新", couponId);
				errorBo.setError(MiddleError.COUPON_CANT_UPDATE);
			} else {
				if (coupon != null) {
					updateCoupon(coupon);
				}
			}
		}
		if (DELETE.equals(operateType)) {
			if (baseCoupon == null) {
				log.info("优惠券[{}]数据不存在, 无法删除", couponId);
				errorBo.setError(MiddleError.COUPON_CANT_DELETE);
			} else {
				if (coupon != null && !coupon.getIsInservice()) {
					deleteCoupon(couponId);
				}
			}
		}
		return errorBo;
	}

	/**
	 * 通过时间段批量拉去基础数据
	 *
	 * @param startDateStr 开始时间
	 * @param endDateStr   结束时间
	 * @return bo
	 */
	public RestErrorBo pullCoupon(String startDateStr, String endDateStr) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		if (!checkPullDate(startDateStr, endDateStr)) {
			errorBo.setError(MiddleError.DATE_ERROR);
			return errorBo;
		}
		if (StringUtils.isBlank(startDateStr) || StringUtils.isBlank(endDateStr)) {
			return errorBo;
		}
		//查询原始数据
		List<CouponCommonInfo> list = getOriginDataByDate(startDateStr, endDateStr);
		if (CollectionUtils.isNotEmpty(list)) {
			List<Integer> couponIds = list.stream().map(CouponCommonInfo::getId).collect(toList());
			//查询已存在的基础数据
			List<BaseCoupon> existBaseCoupons = getExistBaseCoupon(couponIds);
			//list转map
			Map<Integer, BaseCoupon> existBaseCouponMap = existBaseCoupons.stream().collect(
					toMap(BaseCoupon::getCouponId, Function.identity()));
			//批量插入
			batchInsert(getAddCoupons(existBaseCoupons, list));
			//批量更新
			batchUpdateBase(getUpdateCoupons(list, existBaseCouponMap));
			//删除
			batchDeleteBase(getDeleteCoupons(list, existBaseCoupons));
		}
		return errorBo;
	}

	/**
	 * 对象转换为基础数据
	 *
	 * @param coupon       优惠券
	 * @param baseCouponBo 业务bo
	 * @return base
	 */
	private BaseCoupon singleEntityTransform(CouponCommonInfo coupon, BaseCouponBo baseCouponBo) {
		BaseCoupon baseCoupon = BeanCopierUtils.generalCopyBean(coupon, BaseCoupon.class, getCouponConvert());
		baseCoupon.setCouponId(coupon.getId());
		baseCoupon.setCouponName(coupon.getName());
		baseCoupon.setCouponType(coupon.getType().intValue());
		baseCoupon.setProductTypeName(baseCouponBo.getProductTypeMap().get(baseCoupon.getProductTypeId()));
		RechargeCard rechargeCard = baseCouponBo.getRechargeMap().get(coupon.getId());
		baseCoupon.setBonus(rechargeCard == null ? null : rechargeCard.getBonus());
		if (VOUCHER.equals(coupon.getType().intValue())) {
			VoucheCoupon voucheCoupon = baseCouponBo.getVoucherMap().get(coupon.getId());
			baseCoupon.setWorkloadRate(voucheCoupon == null ? null : voucheCoupon.getWorkloadRate());
		}
		if (DISCOUNT.equals(coupon.getType().intValue())) {
			DiscountCoupon discountCoupon = baseCouponBo.getDiscountMap().get(coupon.getId());
			baseCoupon.setWorkloadRate(discountCoupon == null ? null : discountCoupon.getWorkloadRate());
		}
		return baseCoupon;
	}

	/**
	 * 批量对象转换
	 *
	 * @param coupons      优惠券集合
	 * @param baseCouponBo 业务bo
	 * @return list
	 */
	private List<BaseCoupon> transformToEntity(List<CouponCommonInfo> coupons, BaseCouponBo baseCouponBo) {
		return coupons.stream().map(obj -> singleEntityTransform(obj, baseCouponBo)).collect(toList());
	}

	/**
	 * 新增
	 * @param coupon coupon
	 */
	private void createCoupon(CouponCommonInfo coupon) {
		BaseCouponBo baseCouponBo = getBaseCouponBo(Collections.singletonList(coupon));
		BaseCoupon baseCoupon = singleEntityTransform(coupon, baseCouponBo);
		mapper.insertSelective(baseCoupon);
	}

	/**
	 * 更新
	 * @param coupon coupon
	 */
	private void updateCoupon(CouponCommonInfo coupon) {
		BaseCouponBo baseCouponBo = getBaseCouponBo(Collections.singletonList(coupon));
		BaseCoupon baseCoupon = singleEntityTransform(coupon, baseCouponBo);
		mapper.updateByPrimaryKeySelective(baseCoupon);
	}

	/**
	 * 删除
	 * @param couponId 优惠券
	 */
	private void deleteCoupon(Integer couponId) {
		Example example = new Example(BaseCoupon.class);
		example.createCriteria().andEqualTo("couponId", couponId);
		mapper.deleteByExample(example);
	}

	/**
	 * 需要新增的优惠券
	 *
	 * @param existBaseCoupons 已存在基础数据
	 * @param list             拉取数据
	 * @return list
	 */
	private List<CouponCommonInfo> getAddCoupons(List<BaseCoupon> existBaseCoupons, List<CouponCommonInfo> list) {
		if (CollectionUtils.isEmpty(existBaseCoupons)) {
			return list;
		}
		List<Integer> existIds = existBaseCoupons.stream().map(BaseCoupon::getCouponId).collect(toList());
		return list.stream().filter(obj -> !existIds.contains(obj.getId())
				&& obj.getIsInservice()).collect(toList());
	}

	/**
	 * 获取需要更新的优惠券
	 * @param list 原数据
	 * @param existBaseCouponMap 已存在基础数据
	 * @return List
	 */
	private List<CouponCommonInfo> getUpdateCoupons(List<CouponCommonInfo> list, Map<Integer, BaseCoupon> existBaseCouponMap) {
		if (existBaseCouponMap != null && !existBaseCouponMap.isEmpty()) {
			//优惠券基础表需要的其他数据组合
			BaseCouponBo baseCouponBo = getBaseCouponBo(list);
			//需要更新的产品集合
			return list.stream().filter(obj -> existBaseCouponMap.get(obj.getId()) != null
					&& !existBaseCouponMap.get(obj.getId()).equals(obj, baseCouponBo.getRechargeMap(), baseCouponBo.getVoucherMap(),
						baseCouponBo.getDiscountMap(), baseCouponBo.getProductTypeMap())
					&& obj.getIsInservice()).collect(toList());
		}
		return null;
	}

	/**
	 * 获取需要删除的优惠券
	 * @param list 原数据
	 * @param existBaseCoupons 已存在基础数据
	 * @return ids
	 */
	private List<Integer> getDeleteCoupons(List<CouponCommonInfo> list, List<BaseCoupon> existBaseCoupons) {
		//原始数据需要删除的id
		Set<Integer> deleteIds = list.stream().filter(obj -> !obj.getIsInservice()).map(CouponCommonInfo::getId).collect(toSet());
		//已有数据ids
		Set<Integer> existIds = existBaseCoupons.stream().map(BaseCoupon::getCouponId).collect(toSet());
		Set<Integer> intersectionIds = SetUtils.intersection(deleteIds, existIds);
		if (CollectionUtils.isNotEmpty(intersectionIds)) {
			return Lists.newArrayList(intersectionIds);
		}
		return null;
	}

	/**
	 * 批量插入基础数据
	 *
	 * @param list 优惠券
	 */
	private void batchInsert(List<CouponCommonInfo> list) {
		//优惠券基础表需要的其他数据组合
		if (CollectionUtils.isNotEmpty(list)) {
			BaseCouponBo baseCouponBo = getBaseCouponBo(list);
			//对象转换
			List<BaseCoupon> addBaseCoupons = transformToEntity(list, baseCouponBo);
			mapper.insertList(addBaseCoupons);
		}
	}

	/**
	 * @param list 原数据集合
	 */
	private void batchUpdateBase(List<CouponCommonInfo> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			//优惠券基础表需要的其他数据组合
			BaseCouponBo baseCouponBo = getBaseCouponBo(list);
			List<BaseCoupon> updateBaseCoupons = transformToEntity(list, baseCouponBo);
			if (CollectionUtils.isNotEmpty(updateBaseCoupons)) {
				mapper.updateList(updateBaseCoupons);
			}
		}
	}

	/**
	 * 批量删除
	 *
	 * @param deleteIds 原数据集合
	 */
	private void batchDeleteBase(List<Integer> deleteIds) {
		if (CollectionUtils.isNotEmpty(deleteIds)) {
			Example deleteExample = new Example(BaseCoupon.class);
			deleteExample.createCriteria().andIn("couponId", deleteIds);
			mapper.deleteByExample(deleteExample);
		}
	}

	/**
	 * 查询已经存在的基础产品数据
	 *
	 * @param couponIds 查询的原始产品id集合
	 * @return map
	 */
	private List<BaseCoupon> getExistBaseCoupon(List<Integer> couponIds) {
		Example updateExample = new Example(BaseCoupon.class);
		updateExample.createCriteria().andIn("couponId", couponIds);
		return mapper.selectByExample(updateExample);
	}

	private Converter getCouponConvert() {
		return (s, tClazz, c) -> {
			if (s == null) {
				return s;
			}
			if (s.getClass() == tClazz) {
				return s;
			}
			if (s instanceof Date) {
				return ((Date) s).toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
			}
			return null;
		};
	}

	/**
	 * 校验参数
	 * @param startDateStr 开始时间
	 * @param endDateStr 结束时间
	 * @return boolean
	 */
	private boolean checkPullDate(String startDateStr, String endDateStr) {
		LocalDate startDate = LocalDate.parse(startDateStr, df);
		LocalDate endDate = LocalDate.parse(endDateStr, df);
		return endDate.compareTo(startDate) > 0;
	}

	/**
	 * 获取产品分类信息
	 *
	 * @return Map
	 */
	private Map<Integer, String> getProductTypeMap() {
		Example productExample = new Example(ProductType.class);
		productExample.createCriteria().andEqualTo("inservice", true);
		List<ProductType> productTypes = productTypeMapper.selectByExample(productExample);
		return productTypes.stream().collect(toMap(ProductType::getId, ProductType::getName));
	}

	/**
	 * 通过时间段查询原始数据
	 *
	 * @param startDateStr 开始时间
	 * @param endDateStr   结束时间
	 * @return list
	 */
	private List<CouponCommonInfo> getOriginDataByDate(String startDateStr, String endDateStr) {
		Example couponExample = new Example(CouponCommonInfo.class);
		couponExample.createCriteria().andGreaterThanOrEqualTo("updTime", startDateStr)
				.andLessThan("updTime", endDateStr);
		List<CouponCommonInfo> list = couponMapper.selectByExample(couponExample);
		log.info("本次查询优惠券数据量：[{}]", list.size());
		return list;
	}

	/**
	 * 获取基础表其他字段信息
	 *
	 * @param list 源数据
	 * @return bo
	 */
	private BaseCouponBo getBaseCouponBo(List<CouponCommonInfo> list) {
		BaseCouponBo baseCouponBo = new BaseCouponBo();
		List<Integer> rechargeIds = list.stream().filter(obj -> RECHARGE.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		List<Integer> voucherIds = list.stream().filter(obj -> VOUCHER.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		List<Integer> discountIds = list.stream().filter(obj -> DISCOUNT.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		//充值卡集合
		List<RechargeCard> rechargeCards = listByCouponIds(rechargeIds, RechargeCard.class, rechargeCardMapper);
		//代金集合
		List<VoucheCoupon> vouchers = listByCouponIds(voucherIds, VoucheCoupon.class, voucheCouponMapper);
		//折扣卡集合
		List<DiscountCoupon> discounts = listByCouponIds(discountIds, DiscountCoupon.class, discountCouponMapper);
		if (CollectionUtils.isNotEmpty(rechargeCards)) {
			Map<Integer, RechargeCard> rechargeMap = rechargeCards.stream().collect(toMap(RechargeCard::getCouponId,
					Function.identity()));
			baseCouponBo.setRechargeMap(rechargeMap);
		}
		if (CollectionUtils.isNotEmpty(vouchers)) {
			Map<Integer, VoucheCoupon> voucherMap = vouchers.stream().collect(toMap(VoucheCoupon::getCouponId,
					Function.identity()));
			baseCouponBo.setVoucherMap(voucherMap);
		}
		if (CollectionUtils.isNotEmpty(discounts)) {
			Map<Integer, DiscountCoupon> discountMap = discounts.stream().collect(toMap(DiscountCoupon::getCouponId,
					Function.identity()));
			baseCouponBo.setDiscountMap(discountMap);
		}
		//产品分类
		Map<Integer, String> productTypeMap = getProductTypeMap();
		baseCouponBo.setProductTypeMap(productTypeMap);
		return baseCouponBo;
	}

	private List listByCouponIds(List<Integer> couponIds, Class<?> clazz, Mapper mapper) {
		List list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(couponIds)) {
			Example example = new Example(clazz);
			example.createCriteria().andIn("couponId", couponIds);
			list = mapper.selectByExample(example);
		}
		return list;
	}
}
