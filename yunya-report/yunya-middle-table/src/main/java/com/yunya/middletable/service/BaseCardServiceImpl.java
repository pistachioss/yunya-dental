package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.bo.BaseCardBo;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.CardMapper;
import com.yunya.middletable.dao.discount.CouponAllocateMapper;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.DiscountCouponMapper;
import com.yunya.middletable.dao.discount.PackageCouponMapper;
import com.yunya.middletable.dao.discount.SalesChannelMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponMapper;
import com.yunya.middletable.dao.discount.VoucheCouponMapper;
import com.yunya.middletable.dao.report.BaseCardMapper;
import com.yunya.middletable.dao.system.AccountItemMapper;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.PackageCoupon;
import com.yunya.models.discount.SalesChannel;
import com.yunya.models.discount.SpecialPackageCoupon;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.models.report.BaseCard;
import com.yunya.models.system.AccountItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.yunya.middletable.enums.CouponTypeEnum.*;
import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/10/20
 */
@Slf4j
@Service
public class BaseCardServiceImpl{
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
	private SalesChannelMapper salesChannelMapper;
	@Resource(name = "customizeThreadPool")
	private ExecutorService cardThreadPool;
	@Resource
	private BaseCardMapper baseCardMapper;

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final int cutSlice = 100;
	private static final String REMARK = "小程序虚拟服务售卖";

	public void operateSingle(MessageModel model) {
		Integer cardId = (Integer) model.getParamMap().get("id");
//		Integer operateType = model.getOperateType();
		operateSingleData(cardId);
	}

	public void operateBatch(MessageModel model) throws InterruptedException {
		Integer cardId = (Integer) model.getParamMap().get("id");
		LocalDateTime submitDate = LocalDateTime.parse((String)model.getParamMap().get("submitDate"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//		Integer operateType = model.getOperateType();
		operateBatchDate(cardId, submitDate);
	}

	/**
	 * 操作单条数据
	 */
	private void operateSingleData(Integer cardId) {
		Card card = cardMapper.selectByPrimaryKey(cardId);
		log.info("卡券同步信息：{}", card);
		if (card == null) {
			deleteCard(cardId);
		} else {
			BaseCard baseCard = baseCardMapper.selectByPrimaryKey(cardId);
			List<BaseCard> originData = getOriginData(Collections.singletonList(card));
			if (baseCard == null) {
				baseCardMapper.insertSelective(originData.get(0));
			} else {
				List<BaseCard> updateCards = getUpdateCards(originData, Collections.singletonList(baseCard));
				if (CollectionUtils.isNotEmpty(updateCards)) {
					if(REMARK.equals(baseCard.getRemark())){
						baseCardMapper.updateByPrimaryKey(updateCards.get(0));
					}
					else
					{
						baseCardMapper.updateByPrimaryKey(updateCards.get(0));
					}
				}
			}
		}
	}

	/**
	 * 新增
	 */
	private void operateBatchDate(Integer couponId, LocalDateTime submitDate) throws InterruptedException {
		List<Card> cards = getCards(couponId, submitDate);
		if (CollectionUtils.isEmpty(cards)) {
			deleteBaseCards(couponId, submitDate);
		} else {
			List<BaseCard> baseCards = getBaseCards(couponId, submitDate);
			List<BaseCard> originData = getOriginData(cards);
			//批量新增
			batchInsert(getAddCards(originData, baseCards));
			//批量更新
			batchUpdate(getUpdateCards(originData, baseCards));
		}
	}

	private void deleteCard(Integer cardId) {
		Example example = new Example(BaseCard.class);
		example.createCriteria().andEqualTo("cardId", cardId);
		baseCardMapper.deleteByExample(example);
	}

	/**
	 * 批量插入基础数据
	 *
	 * @param list 原数据集合
	 */
	private void batchInsert(List<BaseCard> list) throws InterruptedException {
		if (CollectionUtils.isNotEmpty(list)) {
			//分割集合
			List<List<BaseCard>> partition = Lists.partition(list, cutSlice);
			CountDownLatch downLatch = new CountDownLatch(partition.size());
			for (List<BaseCard> baseCards : partition) {
				//多线程异步插入
				cardThreadPool.submit(() -> {
					try {
						baseCardMapper.insertList(baseCards);
						downLatch.countDown();
					} catch (Exception e) {
						downLatch.countDown();
						log.error("pull card batchInsert error",e);
					}
				});
			}
			downLatch.await();
		}
	}

	/**
	 * @param list 原数据集合
	 */
	private void batchUpdate(List<BaseCard> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			baseCardMapper.updateList(list);
		}
	}

	/**
	 * 需要新增的卡券
	 *
	 * @param pullList  拉取数据
	 * @param existData 已存在基础数据
	 * @return list
	 */
	private List<BaseCard> getAddCards(List<BaseCard> pullList, List<BaseCard> existData) {
		List<BaseCard> list = Lists.newArrayList();
		if (CollectionUtils.isEmpty(existData)) {
			list = pullList;
		} else {
			List<Integer> existIds = existData.stream().map(BaseCard::getCardId).collect(toList());
			list = pullList.stream().filter(obj -> !existIds.contains(obj.getCardId())).collect(toList());
		}
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
	private List<BaseCard> getUpdateCards(List<BaseCard> pullData, List<BaseCard> existData) {
		List<BaseCard> updateCards = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(existData)) {
			//拉取的数据转换map
			Map<Integer, BaseCard> cardMap = pullData.stream().collect(toMap(BaseCard::getCardId, Function.identity()));
			//需要更新的产品集合
			updateCards = existData.stream().filter(obj -> cardMap.get(obj.getCardId()) != null
					&& !obj.equals(cardMap.get(obj.getCardId()))).map(obj -> cardMap.get(obj.getCardId()))
					.collect(toList());
		}
		log.info("卡券基础表，需要更新的数据[{}]", updateCards.size());
		return updateCards;
	}

	private List<BaseCard> getOriginData(List<Card> originCards) {
		//查询优惠券分配信息
		Set<Integer> couponIds = originCards.stream().map(Card::getCouponId).collect(toSet());
		List<CouponAllocate> allocates = getCouponAllocate(Lists.newArrayList(couponIds));
		List<BaseCardBo> deadlineBo = getActiveDeadlineBo(Lists.newArrayList(couponIds));
		//入账方式查询
		Map<Integer, String> accountMap = getAccountMap();
		return originCards.stream().map(card -> singleEntityTransform(card, accountMap, allocates, deadlineBo))
				.collect(toList());
	}

	private Map<Integer, String> getAccountMap() {
		//入账方式查询
		List<AccountItem> accountItems = accountItemMapper.selectAll();
		return accountItems.stream().collect(toMap(AccountItem::getId, AccountItem::getName));
	}

	private BaseCard singleEntityTransform(Card card, Map<Integer, String> accountMap, List<CouponAllocate> allocates,
	                                       List<BaseCardBo> deadlineBo) {
		BaseCard baseCard = BeanCopierUtils.generalCopyBean(card, BaseCard.class, getCardConvert());
		baseCard.setCardNumber(card.getCardNumber() == null ? card.getThirdCardNumber() : card.getCardNumber());
		baseCard.setCardId(card.getId());
		baseCard.setAllocateOrgId(card.getOrgId());
		baseCard.setGenerateDate(card.getCrtTime());
		baseCard.setPayName(accountMap.get(baseCard.getPayId()));
		baseCard.setChargeStatus(card.getPay());
		baseCard.setAllocateDate(getAllocateDate(allocates, card.getCouponId(), card.getCrtTime()));
		if (card.getActiveDate() != null) {
			//设置有效期
			baseCard.setActivationDeadline(getActiveDeadline(deadlineBo, card.getCouponId(), card.getActiveDate()));
		}
		if (card.getSaleChannelId() != null) {
			SalesChannel salesChannel = salesChannelMapper.selectByPrimaryKey(card.getSaleChannelId());
			if (salesChannel != null) {
				baseCard.setSaleChannelName(salesChannel.getName());
			}
		}
        baseCard.setBuyerId(card.getBuyerId());
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

	private List<CouponAllocate> getCouponAllocate(List<Integer> couponIds) {
		Example example = new Example(CouponAllocate.class);
		example.createCriteria().andIn("couponId", couponIds)
				.andIsNotNull("allocateDate");
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

	private List<CouponCommonInfo> getCouponCommons(List<Integer> couponIds) {
		Example example = new Example(CouponCommonInfo.class);
		example.createCriteria().andIn("id", couponIds);
		return couponMapper.selectByExample(example);
	}

	private List<Card> getCards(Integer couponId, LocalDateTime submitDate) {
		Example example = new Example(Card.class);
		example.createCriteria().andEqualTo("couponId", couponId)
				.andEqualTo("crtTime", submitDate);
		return cardMapper.selectByExample(example);
	}

	private List<BaseCard> getBaseCards(Integer couponId, LocalDateTime submitDate) {
		Example example = new Example(BaseCard.class);
		example.createCriteria().andEqualTo("couponId", couponId)
				.andEqualTo("generateDate", submitDate);
		return baseCardMapper.selectByExample(example);
	}

	private void deleteBaseCards(Integer couponId, LocalDateTime submitDate) {
		Example example = new Example(BaseCard.class);
		example.createCriteria().andEqualTo("couponId", couponId)
				.andEqualTo("generateDate", submitDate);
		baseCardMapper.deleteByExample(example);
	}
}
