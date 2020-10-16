package com.yunya.models.middletable;

import com.google.common.base.Objects;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.RechargeCard;
import com.yunya.models.discount.VoucheCoupon;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
@Table(name = "base_coupon")
public class BaseCoupon {
	/**
	 * 产品ID
	 */
	@Id
	@Column(name = "coupon_id")
	private Integer couponId;

	/**
	 * 产品名称
	 */
	@Column(name = "coupon_name")
	private String couponName;

	/**
	 * 产品分类ID
	 */
	@Column(name = "product_type_id")
	private Integer productTypeId;

	/**
	 * 产品分类名称
	 */
	@Column(name = "product_type_name")
	private String productTypeName;

	/**
	 * 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
	 */
	@Column(name = "coupon_type")
	private Integer couponType;

	/**
	 * 创建人ID
	 */
	@Column(name = "crt_id")
	private Integer crtId;

	/**
	 * 创建时间
	 */
	@Column(name = "crt_time")
	private LocalDateTime crtTime;

	/**
	 * 售出金额（本金）
	 */
	@Column(name = "sold_amount")
	private BigDecimal soldAmount;

	/**
	 * 赠金
	 */
	private BigDecimal bonus;

	/**
	 * 补入工作量折扣率
	 */
	@Column(name = "workload_rate")
	private BigDecimal workloadRate;

	@Override
	public int hashCode() {
		return Objects.hashCode(couponId, couponName, productTypeId, productTypeName, couponType, crtId, crtTime, soldAmount, bonus, workloadRate);
	}

	public boolean equals(CouponCommonInfo coupon, Map<Integer, RechargeCard> rechargeMap, Map<Integer, VoucheCoupon> voucherMap,
	                      Map<Integer, DiscountCoupon> discountMap, Map<Integer, String> productTypeMap) {

		Object this$couponId = this.getCouponId();
		Object other$couponId = coupon.getId();
		if (this$couponId == null) {
			if (other$couponId != null) {
				return false;
			}
		} else if (!this$couponId.equals(other$couponId)) {
			return false;
		}

		Object this$couponName = this.getCouponName();
		Object other$couponName = coupon.getName();
		if (this$couponName == null) {
			if (other$couponName != null) {
				return false;
			}
		} else if (!this$couponName.equals(other$couponName)) {
			return false;
		}

		Object this$productTypeId = this.getProductTypeId();
		Object other$productTypeId = coupon.getProductTypeId();
		if (this$productTypeId == null) {
			if (other$productTypeId != null) {
				return false;
			}
		} else if (!this$productTypeId.equals(other$productTypeId)) {
			return false;
		}

		label110:
		{
			Object this$productTypeName = this.getProductTypeName();
			String productName = productTypeMap.get(this$productTypeId);
			if (this$productTypeName == null) {
				if (productName == null) {
					break label110;
				}
			} else if (this$productTypeName.equals(productName)) {
				break label110;
			}

			return false;
		}

		label103:
		{
			Object this$couponType = this.getCouponType();
			Byte other$couponType = coupon.getType();
			if (this$couponType == null) {
				if (other$couponType == null) {
					break label103;
				}
			} else if (this$couponType.equals(other$couponType.intValue())) {
				break label103;
			}

			return false;
		}

		Object this$crtId = this.getCrtId();
		Object other$crtId = coupon.getCrtId();
		if (this$crtId == null) {
			if (other$crtId != null) {
				return false;
			}
		} else if (!this$crtId.equals(other$crtId)) {
			return false;
		}

		label89:
		{
			Object this$crtTime = this.getCrtTime();
			Date other$crtTime = coupon.getCrtTime();
			if (this$crtTime == null) {
				if (other$crtTime == null) {
					break label89;
				}
			} else if (this$crtTime.equals(other$crtTime.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime())) {
				break label89;
			}

			return false;
		}

		label82:
		{
			Object this$soldAmount = this.getSoldAmount();
			Object other$soldAmount = coupon.getSoldAmount();
			if (this$soldAmount == null) {
				if (other$soldAmount == null) {
					break label82;
				}
			} else if (this$soldAmount.equals(other$soldAmount)) {
				break label82;
			}

			return false;
		}

		Object this$bonus = this.getBonus();
		RechargeCard rechargeCard = rechargeMap.get(this$couponId);
		BigDecimal other$bonus = rechargeCard == null ? null : rechargeCard.getBonus();
		if (this$bonus == null) {
			if (other$bonus != null) {
				return false;
			}
		} else if (!this$bonus.equals(other$bonus)) {
			return false;
		}

		Object this$workloadRate = this.getWorkloadRate();
        BigDecimal other$workloadRate = null;
		if (this.getCouponType() == 0) {
			VoucheCoupon voucheCoupon = voucherMap.get(this$couponId);
			other$workloadRate = voucheCoupon == null ? null : voucheCoupon.getWorkloadRate();
		}
		if (this.getCouponType() == 1) {
            DiscountCoupon discountCoupon = discountMap.get(this$couponId);
            other$workloadRate = discountCoupon == null ? null : discountCoupon.getWorkloadRate();
		}
        if (this$workloadRate == null) {
            if (other$workloadRate != null) {
                return false;
            }
        } else if (!this$workloadRate.equals(other$workloadRate)) {
            return false;
        }
		return true;
	}
}