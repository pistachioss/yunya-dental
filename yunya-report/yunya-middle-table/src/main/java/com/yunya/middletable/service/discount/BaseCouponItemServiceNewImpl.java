package com.yunya.middletable.service.discount;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.bo.BaseCouponItemBo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.PackageCouponItemMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponItemMapper;
import com.yunya.middletable.dao.discount.VoucherDiscountItemMapper;
import com.yunya.middletable.dao.report.BaseCouponItemMapper;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.PackageCouponItem;
import com.yunya.models.discount.SpecialPackageCouponItem;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.models.report.BaseCoupon;
import com.yunya.models.report.BaseCouponItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.yunya.middletable.constant.SynConstant.*;
import static com.yunya.middletable.enums.CouponTypeEnum.*;
import static java.util.stream.Collectors.*;


/**
 * @author xiangyang
 * @date 2020/10/16
 */
@Slf4j
@Service
public class BaseCouponItemServiceNewImpl extends BaseBiz<BaseCouponItemMapper, BaseCouponItem> {
	@Resource
	private CouponMapper couponMapper;
	@Resource
	private PackageCouponItemMapper packageCouponItemMapper;
	@Resource
	private SpecialPackageCouponItemMapper specialPackageCouponItemMapper;
	@Resource
	private VoucherDiscountItemMapper voucherDiscountItemMapper;
	@Resource(name = "customizeThreadPool")
	private ExecutorService cardThreadPool;

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * 通过时间段批量拉去基础数据
	 *
	 * @param startDateStr 开始时间
	 * @param endDateStr   结束时间
	 * @return bo
	 */
	public void pullCouponItem(String startDateStr, String endDateStr) throws InterruptedException {
		long start = System.currentTimeMillis();
		if (!checkPullDate(startDateStr, endDateStr)) {
			throw new BaseException("结束时间不能小于开始时间", 500);
		}
		//查询原始数据
		List<BaseCouponItem> list = getOriginDataByDate(startDateStr, endDateStr);
		if (CollectionUtils.isNotEmpty(list)) {
			List<Integer> couponIds = list.stream().map(BaseCouponItem::getCouponId).collect(toList());
			//查询已存在的基础数据
			List<BaseCouponItem> existItems = getExistBaseCouponItem(couponIds);
			//批量插入
			batchInsert(getAddItem(list, existItems));
			//批量更新
			batchUpdateBase(getUpdateItem(list, existItems));
			//批量删除
			batchDeleteBase(getDeleteItems(list, existItems));
		}
		long end = System.currentTimeMillis();
		log.info("【中间表同步】产品项目时长：[{}]分钟", (end - start) / 60000);
	}

	/**
	 * 批量插入基础数据
	 *
	 * @param list 优惠券项目
	 */
	private void batchInsert(List<BaseCouponItem> list) throws InterruptedException {
		if (CollectionUtils.isNotEmpty(list)) {
			List<List<BaseCouponItem>> partition = Lists.partition(list, CUT_SLICE_100);
			CountDownLatch downLatch = new CountDownLatch(partition.size());
			for (List<BaseCouponItem> baseItemList : partition) {
				//多线程异步插入
				cardThreadPool.execute(() -> {
					try {
						mapper.insertList(baseItemList);
						downLatch.countDown();
					} catch (Exception e) {
						downLatch.countDown();
						log.error("pull couponItem batchInsert error",e);
					}
				});
			}
			downLatch.await();
		}
	}

	/**
	 * 批量更新基础数据
	 * @param list 原数据集合
	 */
	private void batchUpdateBase(List<BaseCouponItem> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			mapper.updateList(list);
		}
	}

	/**
	 * @param list 原数据集合
	 */
	private void batchDeleteBase(List<BaseCouponItem> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			mapper.deleteList(list);
		}
	}

	private List<BaseCouponItem> getOriginItems(CouponCommonInfo coupon) {
		int couponType = coupon.getType().intValue();
		Integer couponId = coupon.getId();
		List<BaseCouponItem> baseCouponItems = Lists.newArrayList();
		if (VOUCHER.equals(couponType) || DISCOUNT.equals(couponType)) {
			//获取优惠券项目源数据
			List<VoucherDiscountItem> voucherDiscountItems = listCouponItems(couponId, VoucherDiscountItem.class,
					voucherDiscountItemMapper);
			//对象转换
			baseCouponItems = BeanCopierUtils.listGeneralCopyBean(voucherDiscountItems, BaseCouponItem.class,
					getCouponConvert());
		}
		if (EXCHANGE.equals(couponType)) {
			List<PackageCouponItem> exchangeItems = listCouponItems(couponId, PackageCouponItem.class,
					packageCouponItemMapper);
			baseCouponItems = exchangeItems.stream().map(obj -> {
				BaseCouponItem item = BeanCopierUtils.generalCopyBean(obj, BaseCouponItem.class, getCouponConvert());
				item.setQuantity(obj.getCount());
				return item;
			}).collect(toList());
		}
		if (SPECIAL_PACKAGE.equals(couponType)) {
			List<SpecialPackageCouponItem> packageItems = listCouponItems(couponId, SpecialPackageCouponItem.class,
					specialPackageCouponItemMapper);
			baseCouponItems = packageItems.stream().map(obj -> {
				BaseCouponItem item = BeanCopierUtils.generalCopyBean(obj, BaseCouponItem.class, getCouponConvert());
				item.setQuantity(obj.getCount());
				item.setSaleUnitPrice(obj.getPackageUnitPrice());
				return item;
			}).collect(toList());
		}
		return baseCouponItems;
	}

	/**
	 * 需要新增的优惠券
	 *
	 * @param baseCouponItems 拉取数据
	 * @param existItems      已存在基础数据
	 * @return list
	 */
	private List<BaseCouponItem> getAddItem(List<BaseCouponItem> baseCouponItems, List<BaseCouponItem> existItems) {
		List<BaseCouponItem> addItems = Lists.newArrayList();
		if (CollectionUtils.isEmpty(existItems)) {
			addItems = baseCouponItems;
		} else {
			addItems = baseCouponItems.stream().filter(obj -> (obj.getQuantity() == null && !existItems.contains(obj)) ||
					(obj.getQuantity() != null && !judgeEqual(obj, existItems))).collect(toList());
		}
		log.info("优惠券项目基础表，需要新增的数据[{}]", addItems.size());
		return addItems;
	}

	/**
	 * 判断数据是否在基础表存在
	 *
	 * @param item  需判断数据
	 * @param items 集合
	 * @return bool
	 */
	private boolean judgeEqual(BaseCouponItem item, List<BaseCouponItem> items) {
		Optional<BaseCouponItem> optional = items.stream().filter(obj -> item.getCouponId().equals(obj.getCouponId())
				&& item.getType().equals(obj.getType()) && item.getItemId().equals(obj.getItemId())).findFirst();
		return optional.isPresent();
	}

	/**
	 * 获取需要更新的优惠券
	 *
	 * @param baseCouponItems 拉取数据
	 * @param existItems      已存在基础数据
	 * @return List
	 */
	private List<BaseCouponItem> getUpdateItem(List<BaseCouponItem> baseCouponItems, List<BaseCouponItem> existItems) {
		List<BaseCouponItem> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(existItems)) {
			Map<String, BaseCouponItem> itemMap = baseCouponItems.stream().filter(obj -> obj.getQuantity() != null)
					.collect(toMap(obj -> Joiner.on(":").join(obj.getCouponId(), obj.getItemId(), obj.getType()), Function.identity()));
			//需要更新的产品集合
			list = existItems.stream().filter(obj -> {
				if (obj.getQuantity() != null) {
					String key = Joiner.on(":").join(obj.getCouponId(), obj.getItemId(), obj.getType());
					return itemMap.get(key) != null && !obj.equals(itemMap.get(key));
				}
				return false;
			}).map(obj -> itemMap.get(Joiner.on(":").join(obj.getCouponId(), obj.getItemId(), obj.getType()))).collect(toList());
		}
		log.info("优惠券项目基础表，需要更新的数据[{}]", list.size());
		return list;
	}

	/**
	 * 获取需要更新的优惠券
	 *
	 * @param baseCouponItems 拉取数据
	 * @param existItems      已存在基础数据
	 * @return List
	 */
	private List<BaseCouponItem> getDeleteItems(List<BaseCouponItem> baseCouponItems, List<BaseCouponItem> existItems) {
		List<BaseCouponItem> deleteItems = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(existItems)) {
			//需要更新的产品集合
			deleteItems = existItems.stream().filter(obj -> (obj.getQuantity() == null && !baseCouponItems.contains(obj)) ||
					(obj.getQuantity() != null && !judgeEqual(obj, baseCouponItems))).collect(toList());
		}
		log.info("优惠券项目基础表，需要删除的数据[{}]", deleteItems.size());
		return deleteItems;
	}

//	private Predicate<BaseCouponItem> notContainsKey(Map<String, BaseCouponItem> itemMap, Map<String, BaseCouponItem> nullItemMap) {
//		return (obj) -> {
//			if (obj.getItemId() != null) {
//				String key = Joiner.on(":").join(obj.getCouponId(), obj.getItemId(), obj.getType());
//				return itemMap.get(key) == null;
//			}
//			String key = Joiner.on(":").join(obj.getCouponId(), obj.getType());
//			return nullItemMap.get(key) == null;
//		};
//	}

	private List<BaseCouponItem> getExistBaseCouponItem(List<Integer> couponIds) {
		Example example = new Example(BaseCouponItem.class);
		example.createCriteria().andIn("couponId", couponIds);
		return mapper.selectByExample(example);
	}

	/**
	 * 对象转换为报表数据格式
	 *
	 * @param itemBo 业务bo
	 * @return list
	 */
	private List<BaseCouponItem> getTransformEntity(BaseCouponItemBo itemBo) {
		List<VoucherDiscountItem> voucherDiscountItems = itemBo.getVoucherDiscountItems();
		List<PackageCouponItem> exchangeItems = itemBo.getExchangeItems();
		List<SpecialPackageCouponItem> packageItems = itemBo.getPackageItems();
		List<BaseCouponItem> list = Lists.newArrayListWithCapacity(voucherDiscountItems.size() + exchangeItems.size()
				+ packageItems.size());
		if (CollectionUtils.isNotEmpty(voucherDiscountItems)) {
			List<BaseCouponItem> voucherList = voucherDiscountItems.stream().map(obj -> {
				BaseCouponItem item = new BaseCouponItem();
				item.setCouponId(obj.getCouponId());
				item.setType(obj.getType());
				item.setChoiceRangType(obj.getChoiceRangType().intValue());
				item.setItemId(obj.getItemId());
				return item;
			}).collect(toList());
			list.addAll(voucherList);
		}
		if (CollectionUtils.isNotEmpty(exchangeItems)) {
			List<BaseCouponItem> exchangeList = exchangeItems.stream().map(obj -> {
				BaseCouponItem item = new BaseCouponItem();
				item.setCouponId(obj.getCouponId());
				item.setItemId(obj.getItemId());
				item.setType(obj.getType());
				item.setSaleUnitPrice(obj.getSaleUnitPrice());
				item.setQuantity(obj.getCount());
				item.setWorkloadLoad(obj.getWorkloadLoad());
				return item;
			}).collect(toList());
			list.addAll(exchangeList);
		}
		if (CollectionUtils.isNotEmpty(packageItems)) {
			List<BaseCouponItem> packageList = packageItems.stream().map(obj -> {
				BaseCouponItem item = new BaseCouponItem();
				item.setItemId(obj.getItemId());
				item.setCouponId(obj.getCouponId());
				item.setType(obj.getType());
				item.setSaleUnitPrice(obj.getPackageUnitPrice());
				item.setQuantity(obj.getCount());
				item.setWorkloadLoad(obj.getWorkloadLoad());
				return item;
			}).collect(toList());
			list.addAll(packageList);
		}
		return list;
	}

	/**
	 * 删除
	 *
	 * @param couponId 优惠券
	 */
	private void deleteByCouponId(Integer couponId) {
		Example example = new Example(BaseCoupon.class);
		example.createCriteria().andEqualTo("couponId", couponId);
		mapper.deleteByExample(example);
	}

	/**
	 * 通过时间段查询原始数据
	 *
	 * @param startDateStr 开始时间
	 * @param endDateStr   结束时间
	 * @return BaseCouponItemBo
	 */
	private List<BaseCouponItem> getOriginDataByDate(String startDateStr, String endDateStr) {
		BaseCouponItemBo itemBo = BaseCouponItemBo.getInstance();
		List<VoucherDiscountItem> voucherDiscountItems = getCouponItem(startDateStr, endDateStr, VoucherDiscountItem.class, voucherDiscountItemMapper);
		log.info("本次查询代金折扣优惠券数据量：[{}]", voucherDiscountItems.size());
		List<PackageCouponItem> exchangeItems = getCouponItem(startDateStr, endDateStr, PackageCouponItem.class, packageCouponItemMapper);
		log.info("本次查询兑换优惠券数据量：[{}]", exchangeItems.size());
		List<SpecialPackageCouponItem> packageItems = getCouponItem(startDateStr, endDateStr, SpecialPackageCouponItem.class, specialPackageCouponItemMapper);
		log.info("本次查询套餐优惠券数据量：[{}]", packageItems.size());
		if (CollectionUtils.isNotEmpty(voucherDiscountItems)) {
			itemBo.setVoucherDiscountItems(voucherDiscountItems);
		}
		if (CollectionUtils.isNotEmpty(exchangeItems)) {
			itemBo.setExchangeItems(exchangeItems);
		}
		if (CollectionUtils.isNotEmpty(packageItems)) {
			itemBo.setPackageItems(packageItems);
		}
		//原始数据转换
		return getTransformEntity(itemBo);
	}

	private List getCouponItem(String startDateStr, String endDateStr, Class<?> clazz, Mapper mapper) {
		Example example = new Example(clazz);
		example.createCriteria().andGreaterThanOrEqualTo("updTime", startDateStr)
				.andLessThan("updTime", endDateStr);
		return mapper.selectByExample(example);
	}

	private List listCouponItems(Integer couponId, Class<?> clazz, Mapper mapper) {
		Example example = new Example(clazz);
		example.createCriteria().andEqualTo("couponId", couponId);
		return mapper.selectByExample(example);
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

	private List<BaseCouponItem> getBaseItems(Integer couponId) {
		Example example = new Example(BaseCouponItem.class);
		example.createCriteria().andEqualTo("couponId", couponId);
		return mapper.selectByExample(example);
	}

	private Converter getCouponConvert() {
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
}
