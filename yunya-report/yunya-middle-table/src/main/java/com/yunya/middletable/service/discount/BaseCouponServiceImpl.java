package com.yunya.middletable.service.discount;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.CardMapper;
import com.yunya.middletable.dao.discount.CouponMapper;
import com.yunya.middletable.dao.discount.PackageCouponItemMapper;
import com.yunya.middletable.dao.discount.ProductTypeMapper;
import com.yunya.middletable.dao.discount.SpecialPackageCouponItemMapper;
import com.yunya.middletable.dao.discount.VoucherDiscountItemMapper;
import com.yunya.middletable.dao.report.BaseCouponItemMapper;
import com.yunya.middletable.dao.report.BaseCouponMapper;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.ProductType;
import com.yunya.models.middletable.BaseCoupon;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yunya.middletable.enums.TrueFalseEnum.*;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
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
	private VoucherDiscountItemMapper voucherDiscountItemMapper;
	@Resource
	private ProductTypeMapper productTypeMapper;
	@Resource
	private BaseCouponItemMapper baseCouponItemMapper;

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public void operateBaseCoupon(MessageModel model) {
		//Integer couponId = model.getId();
		//CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(couponId);
		//createCoupon(coupon);
	}

	public void pullCoupon(String startDate, String endDate) {

		Example productExample = new Example(ProductType.class);
		productExample.createCriteria().andEqualTo("inservice", TRUE.getCode());
		List<ProductType> productTypes = productTypeMapper.selectByExample(productExample);
		Map<Integer, String> productMap = productTypes.stream().collect(Collectors.toMap(ProductType::getId, ProductType::getName));

		Example couponExample = new Example(CouponCommonInfo.class);
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			couponExample.createCriteria().andGreaterThanOrEqualTo("updTime", startDate)
					.andLessThan("updTime", endDate);
		}
		List<CouponCommonInfo> list = couponMapper.selectByExample(couponExample);
		if (CollectionUtils.isNotEmpty(list)) {
			list.forEach(obj -> {
				BaseCoupon baseCoupon = mapper.selectOne(selectOneCondition(obj.getId()));
				if (baseCoupon == null) {
					createCoupon(obj);
				} else {
					if (!obj.getIsInservice()) {
						deleteCoupon(baseCoupon.getCouponId());
					} else {
						updateCoupon(obj);
					}
				}
			});
		}
	}

	private void createCoupon(CouponCommonInfo coupon) {
		BaseCoupon baseCoupon = BeanCopierUtils.generalCopyBean(coupon, BaseCoupon.class, getCouponConvert());
		baseCoupon.setCouponId(coupon.getId());
		baseCoupon.setCouponName(coupon.getName());
		baseCoupon.setCouponType(coupon.getType().intValue());
		mapper.insertSelective(baseCoupon);
	}

	private void updateCoupon(CouponCommonInfo coupon) {
		Example example = new Example(BaseCoupon.class);
		example.createCriteria().andEqualTo("couponId", coupon.getId());
		BaseCoupon baseCoupon = BeanCopierUtils.generalCopyBean(coupon, BaseCoupon.class, getCouponConvert());
		baseCoupon.setCouponId(coupon.getId());
		baseCoupon.setCouponName(coupon.getName());
		baseCoupon.setCouponType(coupon.getType().intValue());
		mapper.updateByExample(baseCoupon, example);
	}

	private void deleteCoupon(Integer couponId) {
		Example example = new Example(BaseCoupon.class);
		example.createCriteria().andEqualTo("couponId", couponId);
		mapper.deleteByExample(example);
	}


	private Converter getCouponConvert() {
		Converter converter = (s, tClazz, c) -> {
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
		return converter;
	}

	private BaseCoupon selectOneCondition(Integer couponId) {
		BaseCoupon baseCoupon = new BaseCoupon();
		baseCoupon.setCouponId(couponId);
		return baseCoupon;
	}
}
