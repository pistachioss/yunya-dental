package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.report.domain.bo.BaseCardBo;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.CardMapper;
import com.yunya.middletable.dao.discount.CouponAllocateMapper;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.DiscountCouponMapper;
import com.yunya.middletable.dao.discount.PackageCouponMapper;
import com.yunya.middletable.dao.discount.RechargeCardMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponMapper;
import com.yunya.middletable.dao.discount.VoucheCouponMapper;
import com.yunya.middletable.dao.report.BaseCardMapper;
import com.yunya.middletable.dao.system.AccountItemMapper;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.PackageCoupon;
import com.yunya.models.discount.SpecialPackageCoupon;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.models.middletable.BaseCard;
import com.yunya.models.system.AccountItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.yunya.middletable.enums.CouponTypeEnum.*;
import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/10/20
 */
@Slf4j
@Service
public class BaseCardServiceImpl extends BaseBiz<BaseCardMapper, BaseCard> {
	@Resource
	private CardMapper cardMapper;
	@Resource
	private AccountItemMapper accountItemMapper;
	@Resource
	private CouponAllocateMapper allocateMapper;
	@Resource
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
	private RechargeCardMapper rechargeCardMapper;

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void operateBaseCard(MessageModel model) {
		Integer cardId = (Integer) model.getParamMap().get("id");
//		Integer operateType = model.getOperateType();
		operateData(cardId);
	}

	public RestErrorBo pullCard(String startDateStr, String endDateStr) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		if (!checkPullDate(startDateStr, endDateStr)) {
			return errorBo;
		}
		List<BaseCard> originData = getOriginDataByDate(startDateStr, endDateStr);
		if (CollectionUtils.isNotEmpty(originData)) {
			List<Integer> cardIds = originData.stream().map(BaseCard::getCardId).collect(toList());
			List<BaseCard> existData = getExistByCardIds(cardIds);
			//批量新增
			batchInsert(getAddCoupons(originData, existData));
			//批量更新
			batchUpdate(getUpdateCoupons(originData, existData));
		}
		return errorBo;
	}

	/**
	 * 新增
	 */
	private void operateData(Integer cardId) {
		Card card = cardMapper.selectByPrimaryKey(cardId);
		if (card == null) {
			deleteCard(cardId);
		} else {
			BaseCard baseCard = mapper.selectByPrimaryKey(cardId);
			Map<Integer, String> accountMap = getAccountMap();
			List<CouponAllocate> couponAllocates = getCouponAllocate(Lists.newArrayList(cardId));
			List<BaseCardBo> deadlineBo = getActiveDeadlineBo(Lists.newArrayList(cardId));
			if (baseCard != null) {

				mapper.updateByPrimaryKeySelective(singleEntityTransform(card, accountMap, couponAllocates, deadlineBo));
			} else {
				mapper.insertSelective(singleEntityTransform(card, accountMap, couponAllocates, deadlineBo));
			}
		}
	}


	private void deleteCard(Integer cardId) {
		Example example = new Example(BaseCard.class);
		example.createCriteria().andEqualTo("cardId", cardId);
		mapper.deleteByExample(example);
	}

	/**
	 * 批量插入基础数据
	 *
	 * @param list 原数据集合
	 */
	private void batchInsert(List<BaseCard> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			//对象转换
			mapper.insertList(list);
		}
	}

	/**
	 * @param list 原数据集合
	 */
	private void batchUpdate(List<BaseCard> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			mapper.updateList(list);
		}
	}

	/**
	 * 需要新增的卡券
	 *
	 * @param pullList  拉取数据
	 * @param existData 已存在基础数据
	 * @return list
	 */
	private List<BaseCard> getAddCoupons(List<BaseCard> pullList, List<BaseCard> existData) {
		if (CollectionUtils.isEmpty(existData)) {
			return pullList;
		}
		List<Integer> existIds = existData.stream().map(BaseCard::getCardId).collect(toList());
		List<BaseCard> list = pullList.stream().filter(obj -> !existIds.contains(obj.getCardId())).collect(toList());
		log.info("卡券基础表，需要新增的数据[{}]", list.size());
		return list;
	}

	/**
	 * 获取需要更新的卡券
	 *
	 * @param pullData  拉取数据
	 * @param existData 已存在基础数据
	 * @return List
	 */
	private List<BaseCard> getUpdateCoupons(List<BaseCard> pullData, List<BaseCard> existData) {
		List<BaseCard> updateCoupons = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(existData)) {
			//拉取的数据转换map
			Map<Integer, BaseCard> cardMap = pullData.stream().collect(toMap(BaseCard::getCardId, Function.identity()));
			//需要更新的产品集合
			updateCoupons = existData.stream().filter(obj -> cardMap.get(obj.getCardId()) != null
					&& !obj.equals(cardMap.get(obj.getCardId()))).collect(toList());
		}
		log.info("卡券基础表，需要更新的数据[{}]", updateCoupons.size());
		return updateCoupons;
	}

	/**
	 * 通过时间段查询原始数据
	 *
	 * @param startDateStr 开始时间
	 * @param endDateStr   结束时间
	 * @return list
	 */
	private List<BaseCard> getOriginDataByDate(String startDateStr, String endDateStr) {
		List<Card> originCards = getOriginCards(startDateStr, endDateStr);
		List<BaseCard> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(originCards)) {
			//查询优惠券分配信息
			Set<Integer> couponIds = originCards.stream().map(Card::getCouponId).collect(toSet());
			List<CouponAllocate> allocates = getCouponAllocate(Lists.newArrayList(couponIds));
			List<BaseCardBo> deadlineBo = getActiveDeadlineBo(Lists.newArrayList(couponIds));
			//入账方式查询
			Map<Integer, String> accountMap = getAccountMap();
			list = originCards.stream().map(card -> singleEntityTransform(card, accountMap, allocates, deadlineBo))
					.collect(toList());
		}
		return list;
	}

	private Map<Integer, String> getAccountMap() {
		//入账方式查询
		List<AccountItem> accountItems = accountItemMapper.selectAll();
		return accountItems.stream().collect(toMap(AccountItem::getId, AccountItem::getName));
	}

	private BaseCard singleEntityTransform(Card card, Map<Integer, String> accountMap, List<CouponAllocate> allocates,
	                                       List<BaseCardBo> deadlineBo) {
		BaseCard baseCard = BeanCopierUtils.generalCopyBean(card, BaseCard.class, getCardConvert());
		baseCard.setCardId(card.getId());
		baseCard.setAllocateOrgId(card.getOrgId());
		baseCard.setGenerateDate(card.getCrtTime());
		baseCard.setPayName(accountMap.get(baseCard.getPayId()));
		baseCard.setChargeStatus(card.getPay());
		baseCard.setAllocateDate(getAllocateDate(allocates, card.getCouponId(), card.getCrtTime()));
		//设置有效期
		baseCard.setActivationDeadline(getActiveDeadline(deadlineBo, card.getCouponId(), card.getActiveDate()));
		return baseCard;
	}

	private Converter getCardConvert() {
		return (s, tClazz, c) -> {
			if (s == null) {
				return null;
			}
			if (s.getClass() == tClazz) {
				return s;
			}
			if (s instanceof Byte) {
				return ((Byte) s).intValue();
			}
			if (s instanceof Date) {
				return ((Date) s).toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
			}
			return null;
		};
	}

	private List<Card> getOriginCards(String startDateStr, String endDateStr) {
		Example example = new Example(Card.class);
		example.createCriteria().andGreaterThanOrEqualTo("updTime", startDateStr)
				.andLessThan("updTime", endDateStr);
		return cardMapper.selectByExample(example);
	}

	private List<CouponAllocate> getCouponAllocate(List<Integer> couponIds) {
		Example example = new Example(CouponAllocate.class);
		example.createCriteria().andIn("couponId", couponIds);
		return allocateMapper.selectByExample(example);
	}

	/**
	 * 查询优惠券的分配时间
	 *
	 * @param allocates    优惠券源数据
	 * @param couponId     优惠券id
	 * @param generateDate 生成时间
	 */
	private LocalDateTime getAllocateDate(List<CouponAllocate> allocates, Integer couponId, LocalDateTime generateDate) {
		if (CollectionUtils.isNotEmpty(allocates)) {
			Optional<CouponAllocate> optional = allocates.stream().filter(obj -> couponId.equals(obj.getCouponId())
					&& generateDate.isEqual(obj.getAllocateDate().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime()))
					.findFirst();
			if (optional.isPresent()) {
				return optional.get().getCrtTime().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
			}
		}
		return null;
	}

	private LocalDateTime getActiveDeadline(List<BaseCardBo> deadlineBos, Integer couponId, LocalDateTime activeDate) {
		if (activeDate != null && CollectionUtils.isNotEmpty(deadlineBos)) {
			Optional<BaseCardBo> optional = deadlineBos.stream().filter(obj -> couponId.equals(obj.getCouponId()))
					.findFirst();
			if (optional.isPresent()) {
				BaseCardBo cardBo = optional.get();
				Integer effectiveDays = cardBo.getEffectiveDays();
				//优惠券截止时间
				LocalDateTime activationDeadline = cardBo.getActivationDeadline();
				if (activationDeadline == null) {
					if (effectiveDays != 0) {
						activeDate = activeDate.plusDays(effectiveDays);
						return activeDate;
					} else {
						return null;
					}
				} else {
					if (effectiveDays != 0) {
						activeDate = activeDate.plusDays(effectiveDays);
						return activationDeadline.isAfter(activeDate) ? activeDate : activationDeadline;
					} else {
						return activationDeadline;
					}
				}
			}
		}
		return null;
	}

	private List<BaseCardBo> getActiveDeadlineBo(List<Integer> couponIds) {
		//查询优惠券基础信息
		List<CouponCommonInfo> coupons = getCouponCommons(couponIds);
		Map<Integer, List<Integer>> couponMap = coupons.stream().collect(groupingBy(obj -> obj.getType().intValue(),
				mapping(CouponCommonInfo::getId, toList())));
		List<BaseCardBo> list = Lists.newArrayListWithCapacity(couponMap.size());
		couponMap.forEach((k, v) -> {
			List<BaseCardBo> cardBos = Lists.newArrayList();
			if (VOUCHER.equals(k)) {
				List<VoucheCoupon> voucherCoupons = getCouponDetail(v, VoucheCoupon.class, voucheCouponMapper);
				cardBos = voucherCoupons.stream().map(obj -> new BaseCardBo(obj.getCouponId(),
						obj.getActivationDeadline() == null ? null : obj.getActivationDeadline().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime()
						, obj.getEffectiveDays())).collect(toList());
			}
			if (DISCOUNT.equals(k)) {
				List<DiscountCoupon> discountCoupons = getCouponDetail(v, DiscountCoupon.class, discountCouponMapper);
				cardBos = discountCoupons.stream().map(obj -> new BaseCardBo(obj.getCouponId(),
						obj.getActivationDeadline() == null ? null : obj.getActivationDeadline().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime()
						, obj.getEffectiveDays())).collect(toList());
			}
			if (EXCHANGE.equals(k)) {
				List<PackageCoupon> packageCoupons = getCouponDetail(v, PackageCoupon.class, packageCouponMapper);
				cardBos = packageCoupons.stream().map(obj -> new BaseCardBo(obj.getCouponId(),
						obj.getActivationDeadline() == null ? null : obj.getActivationDeadline().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime()
						, obj.getEffectiveDays())).collect(toList());
			}
			if (SPECIAL_PACKAGE.equals(k)) {
				List<SpecialPackageCoupon> specialPackageCoupons = getCouponDetail(v, SpecialPackageCoupon.class, specialPackageCouponMapper);
				cardBos = specialPackageCoupons.stream().map(obj -> new BaseCardBo(obj.getCouponId(),
						obj.getActivationDeadline() == null ? null : obj.getActivationDeadline().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime()
						, obj.getEffectiveDays())).collect(toList());
			}
			if (CollectionUtils.isNotEmpty(cardBos)) {
				list.addAll(cardBos);
			}
		});
		return list;
	}

	private List getCouponDetail(List<Integer> couponIds, Class<?> clazz, Mapper mapper) {
		Example example = new Example(clazz);
		example.createCriteria().andIn("couponId", couponIds);
		return mapper.selectByExample(example);
	}

	private List<BaseCard> getExistByCardIds(List<Integer> cardIds) {
		Example example = new Example(BaseCard.class);
		example.createCriteria().andIn("couponId", cardIds);
		return mapper.selectByExample(example);
	}

	private List<CouponCommonInfo> getCouponCommons(List<Integer> couponIds) {
		Example example = new Example(CouponCommonInfo.class);
		example.createCriteria().andIn("id", couponIds);
		return couponMapper.selectByExample(example);
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
}
