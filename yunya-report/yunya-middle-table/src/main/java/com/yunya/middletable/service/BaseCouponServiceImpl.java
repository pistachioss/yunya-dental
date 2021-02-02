package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.report.domain.bo.BaseCouponBo;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.DiscountCouponMapper;
import com.yunya.middletable.dao.discount.PackageCouponMapper;
import com.yunya.middletable.dao.discount.ProductTypeMapper;
import com.yunya.middletable.dao.discount.RechargeCardMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponMapper;
import com.yunya.middletable.dao.discount.VoucheCouponMapper;
import com.yunya.middletable.dao.report.BaseCouponMapper;
import com.yunya.middletable.enums.MiddleError;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.PackageCoupon;
import com.yunya.models.discount.ProductType;
import com.yunya.models.discount.RechargeCard;
import com.yunya.models.discount.SpecialPackageCoupon;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.models.report.BaseCoupon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.yunya.middletable.constant.SynConstant.*;
import static com.yunya.middletable.enums.CouponTypeEnum.*;
import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Slf4j
@Service
public class BaseCouponServiceImpl extends BaseBiz<BaseCouponMapper, BaseCoupon> {
	@Autowired
	private CouponMapper couponMapper;
	@Resource
	private VoucheCouponMapper voucheCouponMapper;
	@Resource
	private DiscountCouponMapper discountCouponMapper;
	@Resource
	private PackageCouponMapper packageCouponMapper;
	@Resource
	private SpecialPackageCouponMapper specialPackageCouponMapper;
	@Resource
	private ProductTypeMapper productTypeMapper;
	@Resource
	private RechargeCardMapper rechargeCardMapper;
	@Resource(name = "customizeThreadPool")
	private ExecutorService cardThreadPool;

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void operateBaseCoupon(MessageModel model) {
		Integer couponId = (Integer) model.getParamMap().get("id");
//		Integer operateType = model.getOperateType();
		operateData(couponId);
	}

	/**
	 * 通过时间段批量拉去基础数据
	 *
	 * @param startDateStr 开始时间
	 * @param endDateStr   结束时间
	 * @return bo
	 */
	@Transactional
	public RestErrorBo pullCoupon(String startDateStr, String endDateStr) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		if (!checkPullDate(startDateStr, endDateStr)) {
			errorBo.setError(MiddleError.DATE_ERROR);
			return errorBo;
		}
		//查询原始数据
		List<BaseCoupon> list = getOriginDataByDate(startDateStr, endDateStr);
		if (CollectionUtils.isNotEmpty(list)) {
			List<Integer> couponIds = list.stream().map(BaseCoupon::getCouponId).collect(toList());
			//查询已存在的基础数据
			List<BaseCoupon> existBaseCoupons = getExistBaseCoupon(couponIds);
			//批量插入
			batchInsert(getAddCoupons(existBaseCoupons, list));
			//批量更新
			batchUpdateBase(getUpdateCoupons(existBaseCoupons, list));
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
			Date activationDeadline = voucheCoupon.getActivationDeadline();
			baseCoupon.setActivationDeadline(activationDeadline == null ? null
					: activationDeadline.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime());
			baseCoupon.setEffectiveDays(voucheCoupon.getEffectiveDays());
		}
		if (DISCOUNT.equals(coupon.getType().intValue())) {
			DiscountCoupon discountCoupon = baseCouponBo.getDiscountMap().get(coupon.getId());
			baseCoupon.setWorkloadRate(discountCoupon == null ? null : discountCoupon.getWorkloadRate());
			Date activationDeadline = discountCoupon.getActivationDeadline();
			baseCoupon.setActivationDeadline(activationDeadline == null ? null
					: activationDeadline.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime());
			baseCoupon.setEffectiveDays(discountCoupon.getEffectiveDays());
		}
		if (EXCHANGE.equals(coupon.getType().intValue())) {
			PackageCoupon packageCoupon = baseCouponBo.getPackageCouponMap().get(coupon.getId());
			Date activationDeadline = packageCoupon.getActivationDeadline();
			baseCoupon.setActivationDeadline(activationDeadline == null ? null
					: activationDeadline.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime());
			baseCoupon.setEffectiveDays(packageCoupon.getEffectiveDays());
		}
		if (SPECIAL_PACKAGE.equals(coupon.getType().intValue())) {
			SpecialPackageCoupon specialPackageCoupon = baseCouponBo.getSpecialPackageCouponMap().get(coupon.getId());
			Date activationDeadline = specialPackageCoupon.getActivationDeadline();
			baseCoupon.setActivationDeadline(activationDeadline == null ? null
					: activationDeadline.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime());
			baseCoupon.setEffectiveDays(specialPackageCoupon.getEffectiveDays());
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
	 *
	 * @param couponId 优惠券id
	 * @return error
	 */
	private RestErrorBo operateData(Integer couponId) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(couponId);
		if (coupon == null) {
			deleteCoupon(couponId);
		} else {
			BaseCoupon baseCoupon = mapper.selectByPrimaryKey(couponId);
			//数据转换（原始数据）
			List<BaseCoupon> originData = getOriginData(Collections.singletonList(coupon));
			if (baseCoupon != null) {
				List<BaseCoupon> updateCoupons = getUpdateCoupons(Collections.singletonList(baseCoupon), originData);
				if (CollectionUtils.isNotEmpty(updateCoupons)) {
					mapper.updateByPrimaryKeySelective(updateCoupons.get(0));
				}
			} else {
				mapper.insertSelective(originData.get(0));
			}
		}
		return errorBo;
	}

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
	private List<BaseCoupon> getAddCoupons(List<BaseCoupon> existBaseCoupons, List<BaseCoupon> list) {
		List<BaseCoupon> addCoupons = Lists.newArrayList();
		if (CollectionUtils.isEmpty(existBaseCoupons)) {
			addCoupons = list;
		} else {
			List<Integer> existIds = existBaseCoupons.stream().map(BaseCoupon::getCouponId).collect(toList());
			addCoupons = list.stream().filter(obj -> !existIds.contains(obj.getCouponId())).collect(toList());
		}
		log.info("优惠券基础表，需要新增的数据[{}]", addCoupons.size());
		return addCoupons;
	}

	/**
	 * 获取需要更新的优惠券
	 *
	 * @param existBaseCoupons 已存在基础数据
	 * @param list             原数据
	 * @return List
	 */
	private List<BaseCoupon> getUpdateCoupons(List<BaseCoupon> existBaseCoupons, List<BaseCoupon> list) {
		List<BaseCoupon> updateCoupons = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(existBaseCoupons)) {
			//拉取的数据转换map
			Map<Integer, BaseCoupon> couponMap = list.stream().collect(toMap(BaseCoupon::getCouponId, Function.identity()));
			//需要更新的产品集合
			updateCoupons = existBaseCoupons.stream().filter(obj -> couponMap.get(obj.getCouponId()) != null
					&& !obj.equals(couponMap.get(obj.getCouponId()))).map(obj ->couponMap.get(obj.getCouponId()))
					.collect(toList());
		}
		log.info("优惠券基础表，需要更新的数据[{}]", updateCoupons.size());
		return updateCoupons;
	}

//	/**
//	 * 获取需要删除的优惠券
//	 *
//	 * @param list             原数据
//	 * @param existBaseCoupons 已存在基础数据
//	 * @return ids
//	 */
//	private List<Integer> getDeleteCoupons(List<CouponCommonInfo> list, List<BaseCoupon> existBaseCoupons) {
//		//原始数据需要删除的id
//		Set<Integer> deleteIds = list.stream().filter(obj -> !obj.getIsInservice()).map(CouponCommonInfo::getId).collect(toSet());
//		//已有数据ids
//		Set<Integer> existIds = existBaseCoupons.stream().map(BaseCoupon::getCouponId).collect(toSet());
//		Set<Integer> intersectionIds = SetUtils.intersection(deleteIds, existIds);
//		if (CollectionUtils.isNotEmpty(intersectionIds)) {
//			return Lists.newArrayList(intersectionIds);
//		}
//		return null;
//	}

	/**
	 * 批量插入基础数据
	 *
	 * @param list 优惠券
	 */
	private void batchInsert(List<BaseCoupon> list) {
		//优惠券基础表需要的其他数据组合
		if (CollectionUtils.isNotEmpty(list)) {
			List<List<BaseCoupon>> partition = Lists.partition(list, CUT_SLICE_100);
			for (List<BaseCoupon> couponList : partition) {
				//多线程异步插入
				cardThreadPool.execute(() -> {
					try {
						mapper.insertList(couponList);
					} catch (Exception e) {
						log.error("pull coupon batchInsert error", e);
					}
				});
			}
		}
	}

	/**
	 * @param list 原数据集合
	 */
	private void batchUpdateBase(List<BaseCoupon> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			mapper.updateList(list);
		}
	}

	/**
	 * 查询已经存在的基础产品数据
	 *
	 * @param couponIds 查询的原始产品id集合
	 * @return map
	 */
	private List<BaseCoupon> getExistBaseCoupon(List<Integer> couponIds) {
		Example example = new Example(BaseCoupon.class);
		example.createCriteria().andIn("couponId", couponIds);
		return mapper.selectByExample(example);
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
	 *
	 * @param startDateStr 开始时间
	 * @param endDateStr   结束时间
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
	private List<BaseCoupon> getOriginDataByDate(String startDateStr, String endDateStr) {
		Example couponExample = new Example(CouponCommonInfo.class);
		couponExample.createCriteria().andGreaterThanOrEqualTo("updTime", startDateStr)
				.andLessThan("updTime", endDateStr);
		List<CouponCommonInfo> list = couponMapper.selectByExample(couponExample);
		List<BaseCoupon> result = Lists.newArrayList();
		log.info("本次查询优惠券数据量：[{}]", list.size());
		if (CollectionUtils.isNotEmpty(list)) {
			result = getOriginData(list);
		}
		return result;
	}

	private List<BaseCoupon> getOriginData(List<CouponCommonInfo> list) {
		//获取其他字段
		BaseCouponBo baseCouponBo = getBaseCouponBo(list);
		//对象转换
		return transformToEntity(list, baseCouponBo);
	}

	/**
	 * 获取基础表其他字段信息
	 *
	 * @param list 源数据
	 * @return bo
	 */
	private BaseCouponBo getBaseCouponBo(List<CouponCommonInfo> list) {
		BaseCouponBo baseCouponBo = BaseCouponBo.getInstance();
		List<Integer> rechargeIds = list.stream().filter(obj -> RECHARGE.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		List<Integer> voucherIds = list.stream().filter(obj -> VOUCHER.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		List<Integer> discountIds = list.stream().filter(obj -> DISCOUNT.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		List<Integer> exchangeIds = list.stream().filter(obj -> EXCHANGE.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		List<Integer> specialIds = list.stream().filter(obj -> SPECIAL_PACKAGE.equals(obj.getType().intValue()))
				.map(CouponCommonInfo::getId).collect(toList());
		//充值卡集合
		List<RechargeCard> rechargeCards = listByCouponIds(rechargeIds, RechargeCard.class, rechargeCardMapper);
		//代金集合
		List<VoucheCoupon> vouchers = listByCouponIds(voucherIds, VoucheCoupon.class, voucheCouponMapper);
		//折扣卡集合
		List<DiscountCoupon> discounts = listByCouponIds(discountIds, DiscountCoupon.class, discountCouponMapper);
		//兑换券集合
		List<PackageCoupon> packageCoupons = listByCouponIds(exchangeIds, PackageCoupon.class, packageCouponMapper);
		//套餐券集合
		List<SpecialPackageCoupon> specialPackageCoupons = listByCouponIds(specialIds, SpecialPackageCoupon.class, specialPackageCouponMapper);
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
		if (CollectionUtils.isNotEmpty(packageCoupons)) {
			Map<Integer, PackageCoupon> packageCouponMap = packageCoupons.stream().collect(toMap(PackageCoupon::getCouponId,
					Function.identity()));
			baseCouponBo.setPackageCouponMap(packageCouponMap);
		}
		if (CollectionUtils.isNotEmpty(specialPackageCoupons)) {
			Map<Integer, SpecialPackageCoupon> specialPackageCouponMap = specialPackageCoupons.stream().collect(toMap(SpecialPackageCoupon::getCouponId,
					Function.identity()));
			baseCouponBo.setSpecialPackageCouponMap(specialPackageCouponMap);
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
