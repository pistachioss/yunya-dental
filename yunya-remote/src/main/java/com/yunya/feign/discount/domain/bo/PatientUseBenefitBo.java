package com.yunya.feign.discount.domain.bo;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/9/15
 */
@Getter
@Setter
public class PatientUseBenefitBo implements Serializable {
	private Integer couponId;
	private Integer couponType;
	private BigDecimal discountRate;
	private List<Integer> usableClinic;
	private BigDecimal face;
	private BigDecimal balance;
	private Integer cardId;
	private String cardNumber;
	private String couponName;
	private LocalDate useDeadline;
	private Integer mixable;
	private Integer useWay;
	private Integer limitCount;
	/**
	 * 已经使用次数
	 */
	private Integer usedTimes;
	private List<ItemBenefitUseDetailBo> benefitUseDetail;

	public PatientUseBenefitBo() {
		this.usedTimes = 0;
		this.benefitUseDetail = Lists.newArrayList();
	}

	public void setExchangeCouponItemDetail(List<CouponItemUseBo> list) {
		int one = 1;
		if (CollectionUtils.isNotEmpty(list)) {
			List<ItemBenefitUseDetailBo> itemList = list.stream().filter(obj -> obj.getUsable() == one)
					.map(exchangeItem -> {
						return new ItemBenefitUseDetailBo(exchangeItem.getItemId(), exchangeItem.getType(),
								exchangeItem.getOriginCount() - exchangeItem.getUseCount(), BigDecimal.valueOf(0));

					}).collect(toList());
			this.setBenefitUseDetail(itemList);
		}
	}

	public void setPackageCouponItemDetail(List<CouponItemUseBo> list) {
		int one = 1;
		if (CollectionUtils.isNotEmpty(list)) {
			List<ItemBenefitUseDetailBo> itemList = list.stream().filter(obj -> obj.getUsable() == one)
					.map(packageItem -> {
						return new ItemBenefitUseDetailBo(packageItem.getItemId(), packageItem.getType(),
								packageItem.getOriginCount() - packageItem.getUseCount(), packageItem.getPackageUnitPrice());

					}).collect(toList());
			this.setBenefitUseDetail(itemList);
		}
	}

	public void setDiscountVoucherCouponItemDetail(Set<Integer> set, Integer type, Integer rangType) {
		if (rangType == 0) {
			int itemId = 0;
			List<ItemBenefitUseDetailBo> choiceAllList = Lists.newArrayList(new ItemBenefitUseDetailBo(itemId, type, null, null));
			addUseDetail(choiceAllList);
		} else {
			if (CollectionUtils.isNotEmpty(set)) {
				List<ItemBenefitUseDetailBo> itemList = set.stream().map(itemId -> {
					return new ItemBenefitUseDetailBo(itemId, type, null, null);
				}).collect(toList());
				addUseDetail(itemList);
			}
		}
	}

	private void addUseDetail(List<ItemBenefitUseDetailBo> itemList) {
		List<ItemBenefitUseDetailBo> useDetail = this.getBenefitUseDetail();
		if (CollectionUtils.isNotEmpty(useDetail)) {
			useDetail.addAll(itemList);
		} else {
			this.setBenefitUseDetail(itemList);
		}
	}

}
