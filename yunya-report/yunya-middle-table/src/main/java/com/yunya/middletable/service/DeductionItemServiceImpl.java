package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.report.domain.bo.BaseCouponItemBo;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.DeductionItemPeriodMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponItemMapper;
import com.yunya.middletable.dao.report.DeductionItemMapper;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DeductionItemPeriod;
import com.yunya.models.discount.SpecialPackageCouponItem;
import com.yunya.models.report.BaseCoupon;
import com.yunya.models.report.DeductionItem;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.yunya.framework.common.constant.BusinessConstants.ONE;
import static com.yunya.framework.common.constant.BusinessConstants.ZERO;
import static com.yunya.middletable.constant.SynConstant.CUT_SLICE_100;
import static com.yunya.middletable.enums.CouponTypeEnum.DEDUCTION_COUPON;
import static java.util.stream.Collectors.toList;


/**
 * @author xiangyang
 * @date 2020/10/16
 */
@Slf4j
@Service
public class DeductionItemServiceImpl extends BaseBiz<DeductionItemMapper, DeductionItem> {
	@Resource
	private CouponMapper couponMapper;
	@Resource
	private DeductionItemPeriodMapper deductionItemPeriodMapper;
	@Resource(name = "customizeThreadPool")
	private ExecutorService cardThreadPool;
    @Resource
    private SpecialPackageCouponItemMapper specialPackageCouponItemMapper;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public RestErrorBo operateBaseCouponItem(MessageModel model) throws InterruptedException {
		Integer couponId = (Integer) model.getParamMap().get("id");
//		Integer operateType = model.getOperateType();
		RestErrorBo errorBo = RestErrorBo.getInstance();
		operateData(couponId);
		return errorBo;
	}

    public void pullCouponItem(String startDateStr, String endDateStr) throws InterruptedException {
        long start = System.currentTimeMillis();
        if (!checkPullDate(startDateStr, endDateStr)) {
            throw new BaseException("结束时间不能小于开始时间", 500);
        }
        //查询原始数据
        List<DeductionItem> list = getOriginDataByDate(startDateStr, endDateStr);
        if (CollectionUtils.isNotEmpty(list)) {
            //查询已存在的基础数据
            deleteExistBaseCouponItem(startDateStr, endDateStr);
            //批量插入
            batchInsert(list);
        }
        long end = System.currentTimeMillis();
        log.info("【中间表同步】产品项目时长：[{}]分钟", (end - start) / 60000);
    }



    private void deleteExistBaseCouponItem(String startDateStr, String endDateStr) {
        Example example = new Example(DeductionItem.class);
        example.createCriteria().andGreaterThanOrEqualTo("crtTime", startDateStr)
                .andLessThan("crtTime", endDateStr);
        mapper.deleteByExample(example);
    }

    private List<DeductionItem> getOriginDataByDate(String startDateStr, String endDateStr) {
        BaseCouponItemBo itemBo = BaseCouponItemBo.getInstance();
        List<DeductionItemPeriod> packageItems = getCouponItem(startDateStr, endDateStr, DeductionItemPeriod.class, deductionItemPeriodMapper);
        log.info("本次查询划扣优惠券数据量：[{}]", packageItems.size());
        if (CollectionUtils.isNotEmpty(packageItems)) {
            itemBo.setPeriods(packageItems);
        }
        //原始数据转换
        return getTransformEntity(itemBo);
    }

    private List<DeductionItem> getTransformEntity(BaseCouponItemBo itemBo) {
        List<DeductionItemPeriod> packageItems = itemBo.getPeriods();
        List<DeductionItem> list = Lists.newArrayListWithCapacity(packageItems.size());
        if (CollectionUtils.isNotEmpty(packageItems)) {
            List<DeductionItem> packageList = packageItems.stream().map(obj -> {
                return BeanCopierUtils.generalCopyBean(obj, DeductionItem.class);
            }).collect(toList());
            list.addAll(packageList);
        }
        return list;
    }

    private List getCouponItem(String startDateStr, String endDateStr, Class<?> clazz, Mapper mapper) {
        Example example = new Example(clazz);
        example.createCriteria().andGreaterThanOrEqualTo("crtTime", startDateStr)
                .andLessThan("crtTime", endDateStr);
        return mapper.selectByExample(example);
    }

    private boolean checkPullDate(String startDateStr, String endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, df);
        LocalDate endDate = LocalDate.parse(endDateStr, df);
        return endDate.compareTo(startDate) > 0;
    }

	/**
	 * 批量插入基础数据
	 *
	 * @param list 优惠券项目
	 */
	private void batchInsert(List<DeductionItem> list) throws InterruptedException {
		if (CollectionUtils.isNotEmpty(list)) {
			List<List<DeductionItem>> partition = Lists.partition(list, CUT_SLICE_100);
			CountDownLatch downLatch = new CountDownLatch(partition.size());
			for (List<DeductionItem> baseItemList : partition) {
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





	private RestErrorBo operateData(Integer couponId) throws InterruptedException {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(couponId);
		if (coupon == null) {
			deleteByCouponId(couponId);
		} else {
			//查询报表中的优惠券项目
            batchInsert(getOriginItems(coupon));
		}
		return errorBo;
	}

	private List<DeductionItem> getOriginItems(CouponCommonInfo coupon) {
		int couponType = coupon.getType().intValue();
		Integer couponId = coupon.getId();
		List<DeductionItem> baseCouponItems = Lists.newArrayList();
        if (DEDUCTION_COUPON.equals(couponType)) {
            List<SpecialPackageCouponItem> packageItems = listCouponItems(couponId, SpecialPackageCouponItem.class,
                    specialPackageCouponItemMapper);
            baseCouponItems = packageItems.stream().map(obj -> {
                DeductionItem item = BeanCopierUtils.generalCopyBean(obj, DeductionItem.class, getCouponConvert());
                if (ZERO.equals(obj.getType())) {
                    BaseTariff tariff = treatmentServiceFeign.findBaseTariffById(obj.getItemId());
                    item.setUnitPrice(tariff.getPrice());
                    item.setPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(obj.getCount())));
                }
                if (ONE.equals(obj.getType())) {
                    BaseOralTariff tariff = treatmentServiceFeign.findBaseOralTariffById(obj.getItemId());
                    item.setUnitPrice(tariff.getPrice());
                    item.setPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(obj.getCount())));
                }
                return item;
            }).collect(toList());
        }
		return baseCouponItems;
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

	private List listCouponItems(Integer couponId, Class<?> clazz, Mapper mapper) {
		Example example = new Example(clazz);
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
