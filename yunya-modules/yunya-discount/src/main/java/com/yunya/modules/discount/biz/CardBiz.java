package com.yunya.modules.discount.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunya.feign.discount.domain.bo.AllocateNumBo;
import com.yunya.feign.discount.domain.bo.BenefitUseBo;
import com.yunya.feign.discount.domain.bo.CouponItemUseBo;
import com.yunya.feign.discount.domain.bo.CouponSaleBo;
import com.yunya.feign.discount.domain.bo.GenerateAllocatePageBo;
import com.yunya.feign.discount.domain.bo.ItemBenefitUseDetailBo;
import com.yunya.feign.discount.domain.bo.ItemUseBenefitBo;
import com.yunya.feign.discount.domain.bo.OrderItemUseBo;
import com.yunya.feign.discount.domain.bo.OrgCouponAllocateBo;
import com.yunya.feign.discount.domain.bo.PatientBenefitBo;
import com.yunya.feign.discount.domain.bo.PatientCardBo;
import com.yunya.feign.discount.domain.bo.PatientUseBenefitBo;
import com.yunya.feign.discount.domain.bo.UseClinicBo;
import com.yunya.feign.discount.domain.bo.ViewAllocateBo;
import com.yunya.feign.discount.domain.form.CancelCardSoldForm;
import com.yunya.feign.discount.domain.form.CardSoldForm;
import com.yunya.feign.discount.domain.form.ConfigSharerForm;
import com.yunya.feign.discount.domain.form.OtherCardActiveForm;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.ClinicAllocateModel;
import com.yunya.feign.discount.domain.model.GenerateAllocateModel;
import com.yunya.feign.discount.domain.query.CardActiveQuery;
import com.yunya.feign.discount.domain.query.CardSaleQuery;
import com.yunya.feign.discount.domain.query.CouponAllocateQuery;
import com.yunya.feign.discount.domain.query.CouponSaleQuery;
import com.yunya.feign.discount.domain.query.GenerateAllocateCardQuery;
import com.yunya.feign.discount.domain.query.GenerateAllocateDetailQuery;
import com.yunya.feign.discount.domain.query.PatientBenefitQuery;
import com.yunya.feign.discount.domain.query.PatientCardQuery;
import com.yunya.feign.discount.domain.vo.CardActiveDetailVo;
import com.yunya.feign.discount.domain.vo.CardQrCodeVo;
import com.yunya.feign.discount.domain.vo.CardSalePageVo;
import com.yunya.feign.discount.domain.vo.CouponSalePageVo;
import com.yunya.feign.discount.domain.vo.ExportCardAllocateVo;
import com.yunya.feign.discount.domain.vo.GenerateAllocateDetailVo;
import com.yunya.feign.discount.domain.vo.GenerateAllocatePageVo;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientCardBaseVo;
import com.yunya.feign.discount.domain.vo.PatientCardSharerVo;
import com.yunya.feign.discount.domain.vo.PatientDiscountVo;
import com.yunya.feign.discount.domain.vo.PatientExchangeVo;
import com.yunya.feign.discount.domain.vo.PatientItemBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientMemberCardVo;
import com.yunya.feign.discount.domain.vo.PatientOptionalBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientOwnCardVo;
import com.yunya.feign.discount.domain.vo.PatientPackageVo;
import com.yunya.feign.discount.domain.vo.PatientShareCardVo;
import com.yunya.feign.discount.domain.vo.PatientVoucherVo;
import com.yunya.feign.discount.domain.vo.ViewAllocateVo;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.MasertMemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.MemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.SecondaryMemberInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.constant.UserConstant;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.models.discount.PackageCoupon;
import com.yunya.models.discount.PackageCouponItem;
import com.yunya.models.discount.ProductType;
import com.yunya.models.discount.RechargeCard;
import com.yunya.models.discount.SalesChannel;
import com.yunya.models.discount.SpecialPackageCoupon;
import com.yunya.models.discount.SpecialPackageCouponItem;
import com.yunya.models.discount.VoucheCoupon;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.modules.discount.enums.CardStatusEnum;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.enums.DiscountError;
import com.yunya.modules.discount.enums.SoldTypeEnum;
import com.yunya.modules.discount.enums.SoldWayEnum;
import com.yunya.modules.discount.enums.TrueFalseEnum;
import com.yunya.modules.discount.enums.UseWayEnum;
import com.yunya.modules.discount.mapper.CardMapper;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.DiscountCouponMapper;
import com.yunya.modules.discount.mapper.PackageCouponItemMapper;
import com.yunya.modules.discount.mapper.PackageCouponMapper;
import com.yunya.modules.discount.mapper.ProductTypeMapper;
import com.yunya.modules.discount.mapper.RechargeCardMapper;
import com.yunya.modules.discount.mapper.SalesChannelMapper;
import com.yunya.modules.discount.mapper.SpecialPackageCouponItemMapper;
import com.yunya.modules.discount.mapper.SpecialPackageCouponMapper;
import com.yunya.modules.discount.mapper.VoucheCouponMapper;
import com.yunya.modules.discount.mapper.VoucherDiscountItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.*;
import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.modules.discount.enums.BenefitTypeEnum.*;
import static com.yunya.modules.discount.enums.CardQrCodeEnum.*;
import static com.yunya.modules.discount.enums.CardStatusEnum.*;
import static com.yunya.modules.discount.enums.CouponTypeEnum.*;
import static com.yunya.modules.discount.enums.RangTypeEnum.*;
import static com.yunya.modules.discount.enums.TrueFalseEnum.*;
import static java.util.stream.Collectors.*;

/**
 * 描述:
 *
 * @author xiangyang
 * @create 2020-08-17
 */
@Service
@Slf4j
public class CardBiz extends BaseBiz<CardMapper, Card> {

	@Resource
	private RemoteSystemServiceFeign systemServiceFeign;
	@Resource
	private PatientCentralServiceFeign patientFeign;
	@Resource
	private CouponCommonInfoMapper couponMapper;
	@Resource
	private CouponAllocateMapper allocateMapper;
	@Resource
	private VoucheCouponMapper voucherMapper;
	@Resource
	private VoucherDiscountItemMapper voucherDiscountItemMapper;
	@Resource
	private DiscountCouponMapper discountMapper;
	@Resource
	private PackageCouponMapper packageMapper;
	@Resource
	private PackageCouponItemMapper packageCouponItemMapper;
	@Resource
	private SpecialPackageCouponItemMapper specialPackageCouponItemMapper;
	@Resource
	private SpecialPackageCouponMapper specialPackageMapper;
	@Resource
	private SalesChannelMapper salesChannelMapper;
	@Resource
	private ProductTypeMapper productTypeMapper;
	@Resource
	private RechargeCardMapper rechargeCardMapper;
	@Resource
	private RedisUtils redisUtils;
	@Resource(name = "customizeThreadPool")
	private ExecutorService cardThreadPool;
	@Resource
	private RemoteTreatmentServiceFeign treatmentServiceFeign;
	@Resource
	private RemoteRabbitMqServiceFeign mqServiceFeign;

	/**
	 * 产品生成分配分页查询
	 *
	 * @param query 查询参数
	 * @return 分页结果
	 */
	public PageInfo<GenerateAllocatePageVo> getCouponAllocatePage(CouponAllocateQuery query) {
		Page<GenerateAllocatePageBo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		//分页查询
		couponMapper.listBatchAllocateByParam(query.getCouponName(), query.getCouponTypeList());
		//属性转换
		List<GenerateAllocatePageVo> list = page.getResult().stream().map(this::allocateBoConvertVo).collect(toList());
		PageInfo<GenerateAllocatePageVo> pageInfo = new PageInfo<>(list);
		pageInfo.setTotal(page.getTotal());
		pageInfo.setPageNum(page.getPageNum());
		return pageInfo;
	}

	public List<GenerateAllocateDetailVo> getGenerateAllocateList(GenerateAllocateDetailQuery query) {
		Example example = new Example(CouponAllocate.class);
		example.createCriteria().andEqualTo("couponId", query.getCouponId())
				.andEqualTo("crtTime", query.getSubmitDate());
		List<CouponAllocate> allocateList = allocateMapper.selectByExample(example);
		List<GenerateAllocateDetailVo> list = allocateList.stream().map(obj -> {
			GenerateAllocateDetailVo vo = new GenerateAllocateDetailVo();
			OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(obj.getOrgId());
			vo.setOrgId(obj.getOrgId());
			vo.setAllocateNum(obj.getAllocateNum());
			vo.setCouponAllocateId(obj.getId());
			vo.setOrgName(orgInfo == null ? null : orgInfo.getName());
			return vo;
		}).collect(toList());
		return list;
	}

	/**
	 * 生成分配
	 *
	 * @param allocateModel model
	 * @return res
	 * @throws Exception ex
	 */
	@Transactional
	public ResponseResult generateAllocate(GenerateAllocateModel allocateModel) throws Exception {
		boolean locked = false;
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		Integer couponId = allocateModel.getCouponId();
		LocalDateTime submitDate = allocateModel.getSubmitDate();
		String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CARD_GENERATE, String.valueOf(couponId), submitDate.toEpochSecond(ZoneOffset.of("+8")));
		String lockVal = String.valueOf(loginUserId);
		long start = System.currentTimeMillis();
		RestErrorBo errorBo;
		log.info("卡券生成分配开始提交：[{}]，提交日期：[{}]", couponId, submitDate);
		try {
			// 1. 锁定产品
			locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
			if (!locked) {
				log.warn("【锁定失败】卡券[{}]正在分配：[{}]，无法提交", couponId, submitDate);
				return ResponseUtil.error(DiscountError.COUPON_IS_LOCKED);
			}
			log.info("【锁定成功】准备提交卡券生成分配...");

			CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(couponId);
			if (coupon == null || !coupon.getIsInservice()) {
				return ResponseUtil.error(DiscountError.COUPON_NOT_EXIST);
			}
			//1. 校验优惠券分配信息
			int count = allocateMapper.countGeneratedByParam(couponId, submitDate);
			if (count > 0) {
				log.warn("【卡券生成失败】：[{}]该批次[{}]已有组织生成卡券", couponId, submitDate);
				return ResponseUtil.error(DiscountError.CARD_IS_GENERATED);
			}
			List<ClinicAllocateModel> allocateList = allocateModel.getAllocateList();
			List<Integer> couponAllocateIds = allocateList.stream().map(ClinicAllocateModel::getCouponAllocateId)
					.collect(toList());
			int allocateCount = mapper.countByAllocateId(couponAllocateIds);
			if (allocateCount > 0) {
				log.warn("【卡券生成失败】：[{}]该批次[{}]已有组织生成卡券", couponId, submitDate);
				return ResponseUtil.error(DiscountError.CARD_IS_GENERATED);
			}
			//2. 校验组织优惠券分配明细
			errorBo = checkCouponAllocate(allocateList, couponId, submitDate);
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
			}
			//3. 提交生成分配
			LocalDateTime generateDate = LocalDateTime.now().withNano(0);
			errorBo = generateAllocateDetail(allocateList, couponId, submitDate, coupon.getCouponCode(), generateDate);
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError());
			}
			long end = System.currentTimeMillis();
			log.info("卡券[{}]生成分配完成，执行时间[{}]秒[{}]毫秒", couponId, (end - start) / 1000, (end - start) % 1000);
			mqServiceFeign.sendMessage(buildMessage(couponId, generateDate));
			log.info("【生成卡券发送消息成功】：优惠券id[{}]，批次[{}]", couponId, generateDate);
			return ResponseUtil.success();
		} finally {
			if (locked) {
				log.info("【解锁成功】");
				redisUtils.unlock(lockKey, lockVal);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public RestErrorBo generateAllocateDetail(List<ClinicAllocateModel> allocateList, Integer couponId,
	                                          LocalDateTime submitDate, String couponCode, LocalDateTime generateDate) throws Exception {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		//所有组织的卡券分配信息
		List<Integer> couponAllocateIds = allocateList.stream().map(ClinicAllocateModel::getCouponAllocateId)
				.collect(toList());
		//所有组织卡券总数
		int sumAllocate = allocateList.stream().mapToInt(ClinicAllocateModel::getAllocateNum).sum();
		//该产品卡券已生成数量
		int sumGenerateNum = mapper.getSumNumByCouponId(couponId);
		CountDownLatch boLatch = new CountDownLatch(allocateList.size());
		//1. 计算每个组织卡券信息
		List<Future<AllocateNumBo>> boFutureList = calculateNumber(allocateList, sumGenerateNum, boLatch);
		boLatch.await();
		//组织优惠券结果转Bo
		List<AllocateNumBo> numBoList = getCalculateBoFutureResult(boFutureList);
		if (numBoList.size() != allocateList.size()) {
			log.warn("【卡券生成失败】");
			errorBo.setError(DiscountError.FAIL_TO_GENERATE);
			return errorBo;
		}
		CountDownLatch cardLatch = new CountDownLatch(sumAllocate);
		//2. 计算每个卡券的生成信息
		List<Future<Card>> cardFutureList = createCardEntity(couponId, couponCode, numBoList, loginUserId,
				generateDate, cardLatch, sumAllocate);
		cardLatch.await();
		//取出卡券明细任务的执行结果
		List<Card> cardList = getAllocateFutureResult(cardFutureList);
		if (CollectionUtils.isEmpty(cardList) || sumAllocate != cardList.size()) {
			log.warn("【卡券生成失败】");
			errorBo.setError(DiscountError.FAIL_TO_GENERATE);
			return errorBo;
		}
		log.info("【卡券明细任务执行结束】卡券数量count：[{}]", cardList.size());
		//3. 生成卡券信息
		mapper.insertList(cardList);
		//4. 更新优惠券分配记录
		allocateMapper.updateAllocateByIds(couponAllocateIds, submitDate, loginUserId, generateDate);
		return errorBo;
	}

	/**
	 * 查看配给详情
	 *
	 * @param query query
	 * @return list
	 */
	public List<ViewAllocateVo> getAllocateDetail(GenerateAllocateCardQuery query) {
		//查询优惠券分配ids
		List<Integer> allocateIds = listIdsBySubmitParam(query);
		List<ViewAllocateVo> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(allocateIds)) {
			List<ViewAllocateBo> boList = mapper.listViewVosByParam(query.getCouponId(), allocateIds);
			list = boList.stream().map(obj -> {
				ViewAllocateVo vo = new ViewAllocateVo();
				OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(obj.getAllocateOrgId());
				vo.setNumberSegment(obj.getNumberSegment());
				vo.setAllocateOrgName(orgInfo == null ? null : orgInfo.getName());
				return vo;
			}).collect(toList());
		}
		return list;
	}

	/**
	 * 查询导出集合
	 *
	 * @param query query
	 * @return list
	 */
	public List<ExportCardAllocateVo> getExportCardAllocateList(GenerateAllocateCardQuery query) {
		//查询优惠券分配ids
		List<Integer> allocateIds = listIdsBySubmitParam(query);
		List<ExportCardAllocateVo> list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(allocateIds)) {
			List<Card> cardList = mapper.listExportVosByParam(query.getCouponId(), allocateIds);
			List<Integer> orgIds = Lists.newArrayList(Sets.newHashSet(cardList.stream().map(Card::getOrgId).collect(toList())));
			log.info("卡券导出，组织数量[{}]", orgIds.size());
			//卡券组织映射
			Map<Integer, String> orgMap = Maps.newHashMapWithExpectedSize(orgIds.size());
			orgIds.forEach(orgId -> {
				OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
				orgMap.put(orgId, orgInfo == null ? null : orgInfo.getName());
			});
			list = cardList.stream().map(obj -> {
				ExportCardAllocateVo vo = new ExportCardAllocateVo();
				vo.setCardNumber(obj.getCardNumber());
				vo.setAllocateOrgName(orgMap.get(obj.getOrgId()));
				vo.setCardPassword(new String(Base64.getDecoder().decode(obj.getCardPassword())));
				return vo;
			}).collect(toList());
		}
		log.info("查询导出卡券分配信息完成");
		return list;
	}

	/**
	 * 产品售卖分页查询
	 *
	 * @param query query
	 * @return page
	 */
	public PageInfo<CouponSalePageVo> getCouponSalePage(CouponSaleQuery query) {
		Page<CouponSaleBo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		//查询优惠券售卖信息
		mapper.listSaleInfoByParam(query.getCouponTypeList(), query.getCouponName(), query.getOrgId());
		List<CouponSalePageVo> list = page.getResult().stream().map(obj -> {
			CouponSalePageVo vo = BeanCopierUtils.generalCopyBean(obj, CouponSalePageVo.class);
			vo.setCouponTypeName(CouponTypeEnum.getValue(obj.getCouponType()));
			return vo;
		}).collect(toList());
		PageInfo<CouponSalePageVo> pageInfo = new PageInfo<>(list);
		pageInfo.setTotal(page.getTotal());
		pageInfo.setPageNum(page.getPageNum());
		return pageInfo;
	}

	public PageInfo<CardSalePageVo> getCardSalePage(CardSaleQuery query) {
		Page<Card> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		mapper.listCardInfosByParam(query.getCardNumber(), query.getSoldTypeList(), query.getCardStatsList(), query.getPhoneNumber(),
				query.getCouponId(), query.getOrgId());
		//实体转换为pageVo
		List<CardSalePageVo> list = page.getResult().stream().map(this::cardConvertPageVo).collect(toList());
		PageInfo<CardSalePageVo> pageInfo = new PageInfo<>(list);
		pageInfo.setTotal(page.getTotal());
		pageInfo.setPageNum(page.getPageNum());
		return pageInfo;
	}

	/**
	 * 卡券售出
	 *
	 * @param cardId cardId
	 * @param form   form
	 * @return res
	 */
	public ResponseResult soldCard(Integer cardId, CardSoldForm form) {
		boolean locked = false;
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CARD_SOLD, String.valueOf(cardId));
		String lockVal = String.valueOf(loginUserId);
		log.info("卡券售卖开始提交：[{}]", cardId);
		try {
			// 1. 锁定售卖卡券
			locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
			if (!locked) {
				log.warn("【锁定失败】卡券[{}]正在售卖中，无法提交", cardId);
				return ResponseUtil.error(DiscountError.CARD_SOLD_IS_LOCKED);
			}
			log.info("【锁定成功】准备提交卡券售卖...");

			RestErrorBo errorBo;
			Integer orgId = form.getOrgId();
			Integer couponId = form.getCouponId();
			//获取组织名
			String orgName = getOrgName(orgId);
			//2. 检查卡券
			Card card = mapper.selectByPrimaryKey(cardId);
			errorBo = checkCardForSale(cardId, card, couponId, orgId, orgName);
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
			}
			//3. 检查优惠券
			CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
			errorBo = checkCouponForSale(couponId, couponInfo);
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError());
			}
			//4. 检查优惠券分配
			OrgCouponAllocateBo orgAllocateBo = allocateMapper.getOrgAllocateByParam(couponId, orgId);
			if (orgAllocateBo == null) {
				log.warn("【售卖失败】[{}]，[{}]未生成分配", couponInfo.getName(), orgName);
				return ResponseUtil.error(DiscountError.ORG_COUPON_NOT_ALLOCATE, orgName, couponInfo.getName());
			}
			//检查该组织该优惠券售卖数量
			int forSaleCount = mapper.getOrgCardSoldInfoByParam(couponId, orgId);
			if (forSaleCount <= 0) {
				log.warn("【售卖失败】[{}]，的[{}]已全部售出，", orgName, couponInfo.getName());
				return ResponseUtil.error(DiscountError.CARD_SOLD_OUT, orgName, couponInfo.getName());
			}
			//5. 卡券售卖
			updateCardForSold(card, form, loginUserId);
			// TODO: 2020/8/26 发短信
			mqServiceFeign.sendMessage(cardId, UPDATE, BaseCardSingle);
			log.info("【售卖卡券发送消息成功】：卡券id[{}]", cardId);
			return ResponseUtil.success();
		} finally {
			if (locked) {
				log.info("【解锁成功】");
				redisUtils.unlock(lockKey, lockVal);
			}
		}
	}

	/**
	 * 校验卡券二维码信息
	 *
	 * @param cardQrData 卡券二维码信息
	 * @return vo
	 */
	public CardQrCodeVo cardQrCodeCheck(String cardQrData) {
		CardQrCodeVo vo = new CardQrCodeVo();
		vo.setCardQrCodeType(QR_CODE_NORMAL.getCode());
		String qrCodeData = new String(Base64.getDecoder().decode(cardQrData));
		List<String> data = Lists.newArrayList(Splitter.on(":").trimResults().omitEmptyStrings().split(qrCodeData));
		if (data.size() != 2) {
			log.warn("卡券二维码数据异常");
			vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
			vo.setErrorMsg("卡券二维码数据异常");
			return vo;
		}
		Card card = mapper.selectByPrimaryKey(Integer.valueOf(data.get(1)));
		if (card == null) {
			vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
			vo.setErrorMsg("卡券不存在");
			return vo;
		}
		//已失效
		if (!cardQrData.equals(card.getLink())) {
			vo.setCardQrCodeType(QR_CODE_INVALID.getCode());
			return vo;
		}
		//已核销
		if (ACTIVATED.equals(card.getStatus())) {
			vo.setCardQrCodeType(QR_CODE_DESTROY.getCode());
			return vo;
		}
		if (!ACTIVE_PENDING.equals(card.getStatus())) {
			vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
			vo.setErrorMsg("卡券售出状态异常");
			return vo;
		}
		CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(card.getCouponId());
		if (coupon == null) {
			log.warn("优惠券不存在");
			vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
			vo.setErrorMsg("优惠券不存在");
			return vo;
		}
		int couponType = coupon.getType().intValue();
		//查询优惠券过期信息
		vo = checkCouponDeadline(card.getCouponId(), couponType);
		if (QR_CODE_NORMAL.equals(vo.getCardQrCodeType())) {
			vo.setCouponName(coupon.getName());
		}
		return vo;
	}

	/**
	 * 卡券取消售出
	 *
	 * @param cardId cardId
	 * @param form   form
	 * @return res
	 */
	public ResponseResult cancelCardSold(Integer cardId, CancelCardSoldForm form) {
		Integer couponId = form.getCouponId();
		Integer orgId = form.getOrgId();
		RestErrorBo errorBo;

		//1. 检查卡券
		Card card = mapper.selectByPrimaryKey(cardId);
		errorBo = checkCardForCancelSale(cardId, card, couponId, orgId);
		if (errorBo.getError() != null) {
			return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
		}
		//2. 检查优惠券
		CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
		if (couponInfo == null || !couponInfo.getIsInservice()) {
			log.warn("【售卖失败】优惠券[{}]不存在", couponId);
			return ResponseUtil.error(DiscountError.COUPON_NOT_EXIST);
		}
		//更新取消卡券售出
		updateCardForCancel(card);
		mqServiceFeign.sendMessage(cardId, UPDATE, BaseCardSingle);
		log.info("【取消售出卡券发送消息成功】：卡券id[{}]", couponId);
		return ResponseUtil.success();
	}

	public CardActiveDetailVo getCardDetailByManual(CardActiveQuery query) {
		String cardPassEncode = Base64.getEncoder().encodeToString(query.getCardPassword().getBytes());
		Example example = new Example(Card.class);
		example.createCriteria().andEqualTo("cardNumber", query.getCardNumber())
				.andEqualTo("cardPassword", cardPassEncode);
		Card card = mapper.selectOneByExample(example);
		if (card == null) {
			return null;
		}
		if (!ACTIVE_PENDING.equals(card.getStatus())) {
			return null;
		}
		return mapper.findByCardNumAndPass(query.getCardNumber(), cardPassEncode);

	}

	public CardActiveDetailVo getCardDetailByMachine(String qrCode) {
		String qrCodeData = new String(Base64.getDecoder().decode(qrCode));
		List<String> data = Lists.newArrayList(Splitter.on(":").trimResults().omitEmptyStrings().split(qrCodeData));
		Card card = mapper.selectByPrimaryKey(Integer.valueOf(data.get(1)));
		if (card == null || !ACTIVE_PENDING.equals(card.getStatus())) {
			return null;
		}
		return mapper.findByCardNumAndPass(card.getCardNumber(), card.getCardPassword());
	}

	/**
	 * 自有平台激活
	 *
	 * @param patientId patientId
	 * @param form      form
	 * @return res
	 */
	public ResponseResult ownActiveCard(Integer patientId, OwnCardActiveForm form) {
		boolean locked = false;
		Integer cardId = form.getCardId();
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CARD_ACTIVE, String.valueOf(cardId));
		String lockVal = String.valueOf(loginUserId);
		log.info("卡券激活开始提交：[{}]", cardId);
		try {
			// 1. 锁定激活卡券
			locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
			if (!locked) {
				log.warn("【锁定失败】卡券[{}]正在激活中，无法提交", cardId);
				return ResponseUtil.error(DiscountError.CARD_ACTIVE_IS_LOCKED);
			}
			log.info("【锁定成功】准备提交卡券激活...");

			RestErrorBo errorBo;
			//2. 检查卡券
			Card card = mapper.selectByPrimaryKey(cardId);
			errorBo = checkCardForOwnActive(form.getPayId(), card);
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
			}
			//3. 检查优惠券
			errorBo = checkCouponForActive(card.getCouponId());
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError());
			}
			//4. 卡券激活
			updateOwnActiveCard(patientId, form, loginUserId);
			mqServiceFeign.sendMessage(cardId, UPDATE, BaseCardSingle);
			log.info("【自有平台激活卡券发送消息成功】：卡券id[{}]", cardId);
			return ResponseUtil.success();
		} finally {
			if (locked) {
				log.info("【解锁成功】");
				redisUtils.unlock(lockKey, lockVal);
			}
		}
	}

	/**
	 * 第三方平台激活卡券
	 *
	 * @param patientId patientId
	 * @param form      form
	 * @return res
	 */
	public ResponseResult otherActiveCard(Integer patientId, OtherCardActiveForm form) {
		boolean locked = false;
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		String cardNumber = form.getThirdCardNumber();
		String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CARD_ACTIVE, cardNumber);
		String lockVal = String.valueOf(loginUserId);
		log.info("第三方平台卡券激活开始提交：[{}]", cardNumber);
		try {
			// 1. 锁定激活第三方平台卡券
			locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
			if (!locked) {
				log.warn("【锁定失败】第三方平台卡券[{}]正在激活中，无法提交", cardNumber);
				return ResponseUtil.error(DiscountError.CARD_ACTIVE_IS_LOCKED);
			}
			log.info("【锁定成功】准备提交第三方平台卡券激活...");

			RestErrorBo errorBo;
			//1. 检查优惠券
			errorBo = checkCouponForActive(form.getCouponId());
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError());
			}
			//2. 检查卡券
			errorBo = checkCardForOtherActive(cardNumber);
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError());
			}
			//3. 第三方平台卡券激活
			Card activeCard = insertOtherActiveCard(patientId, form, loginUserId);
			mqServiceFeign.sendMessage(activeCard.getId(), ADD, BaseCardSingle);
			log.info("【第三方激活发送消息成功】：卡券id[{}]", activeCard.getId());
			return ResponseUtil.success();
		} finally {
			if (locked) {
				log.info("【解锁成功】");
				redisUtils.unlock(lockKey, lockVal);
			}
		}
	}

	/**
	 * 配置共享人
	 *
	 * @param patientId patientId
	 * @param form      form
	 * @return res
	 */
	public ResponseResult configSharer(Integer patientId, Integer cardId, ConfigSharerForm form) {
		//1. 校验卡券
		Card card = mapper.selectByPrimaryKey(cardId);
		if (card == null || !patientId.equals(card.getPatientId())) {
			log.warn("卡券[{}]不存在", cardId);
			return ResponseUtil.error(DiscountError.CARD_NOT_EXIST);
		}
		if (!ACTIVATED.equals(card.getStatus())) {
			log.warn("卡券[{}]未激活", cardId);
			return ResponseUtil.error(DiscountError.CARD_NOT_ACTIVATED);
		}
		//配置共享人不能是自己
		List<String> shareIds = Lists.newArrayList(Splitter.on(",").trimResults().omitEmptyStrings().split(form.getSharerIdStr()));
		if (shareIds.contains(String.valueOf(patientId))) {
			log.warn("【配置共享人失败】：共享人不能是患者自己");
			return ResponseUtil.error(DiscountError.SHARER_NOT_ALLOW_OWNER);
		}
		//2. 校验优惠券
		CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(card.getCouponId());
		if (coupon == null) {
			return ResponseUtil.error(DiscountError.COUPON_NOT_EXIST);
		}
		//优惠券是否与人共享使用
		Boolean shareStatus = getShareStatus(coupon.getType().intValue(), coupon.getId());
		if (!shareStatus) {
			log.warn("{}不能与他人共享", coupon.getName());
			return ResponseUtil.error(DiscountError.COUPON_NOT_ALLOW_SHARE);
		}
		Card shareCard = configShareVoConvertCard(cardId, form);
		mapper.updateByPrimaryKeySelective(shareCard);
		return ResponseUtil.success();
	}

	public List<PatientCardSharerVo> getConfiguredSharer(Integer cardId) {
		Card card = mapper.selectByPrimaryKey(cardId);
		if (card == null) {
			return null;
		}
		if (StringUtils.isNotBlank(card.getSharer())) {
			//共享人id
			List<Integer> sharerIds = Arrays.stream(card.getSharer().split(",")).map(Integer::parseInt).collect(toList());
			List<PatientCardSharerVo> list = Lists.newArrayListWithCapacity(sharerIds.size());
			//查询患者信息
			List<PatientBaseInfoVo> patients = patientFeign.findPatientInfoByIds(sharerIds);
			Map<Integer, PatientBaseInfoVo> patientMap = patients.stream().collect(toMap(PatientBaseInfoVo::getId, Function.identity()));
			PatientCardSharerVo vo;
			for (Integer sharerId : sharerIds) {
				PatientBaseInfoVo patientInfo = patientMap.get(sharerId);
				vo = new PatientCardSharerVo();
				vo.setSharerId(sharerId);
				vo.setSharerName(patientInfo == null ? null : patientInfo.getName());
				vo.setSharerPhoneNumber(patientInfo == null ? null : patientInfo.getMobile());
				list.add(vo);
			}
			return list;
		}
		return null;
	}

	public PageInfo<PatientCardBaseVo> getPatientCardPage(Integer patientId, PatientCardQuery query) {
		Page<PatientCardBo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
		mapper.listPatientCardsByParam(patientId, query.getCouponName(), query.getCouponTypeList(), query.getQueryType());
		//对象转换
		List<PatientCardBaseVo> list = page.getResult().stream().map(obj -> this.patientCardBoConvertVo(query.getQueryType(), obj))
				.collect(toList());
		PageInfo<PatientCardBaseVo> pageInfo = new PageInfo<>(list);
		pageInfo.setTotal(page.getTotal());
		pageInfo.setPageNum(page.getPageNum());
		return pageInfo;
	}

	/**
	 * 选择优惠
	 *
	 * @param form form
	 * @return res
	 */
	public ResponseResult<List<OrderItemUseBo>> choiceBenefitBo(PatientChooseBenefitForm form) {
		Integer orderId = form.getOrderId();
		Integer patientId = form.getPatientId();
		Integer orgId = form.getOrgId();
		RestErrorBo errorBo;
		//查询患者可用优惠
		PatientOptionalBenefitVo benefitVo = this.getPatientBenefit(patientId, orderId, orgId);
		if (benefitVo == null) {
			log.info("【选择优惠】，患者没有可使用优惠券信息");
			return ResponseUtil.error(DiscountError.CANT_USE_BENEFIT);
		}
		errorBo = checkChoiceBenefitForItem(benefitVo, form);
		if (errorBo.getError() != null) {
			return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
		}
		try {
			//锁定患者选择的优惠信息
			errorBo = lockedBenefitByPatient(form);
			if (errorBo.getError() != null) {
				return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
			}
			//获取订单明细
			List<OrderDetail> orderDetails = treatmentServiceFeign.findOrderDetailByOrderRecordId(orderId);
			if (CollectionUtils.isEmpty(orderDetails)) {
				return ResponseUtil.error(DiscountError.ORDER_NOT_EXIST);
			}

			//订单项目集合转换bo对象
			List<OrderItemUseBo> orderItemBos = this.assignedItemVos(orderDetails);
			calculateBenefit(form, benefitVo, orderItemBos);
			return ResponseUtil.success(orderItemBos);
		} catch (Exception e) {
			Set<String> keys = redisUtils.keys(RedisConstants.LOCK_CHOICE_CARD + "*");
			log.warn("【选择优惠】优惠选择发生异常，解除卡券锁定{}", keys);
			if (CollectionUtils.isNotEmpty(keys)) {
				for (String lockKey : keys) {
					String lockVal = String.valueOf(patientId);
					// 释放患者取消选择的卡券的锁
					redisUtils.unlock(lockKey, lockVal);
				}
			}
			throw e;
		}
	}

	/**
	 * 选择优惠
	 *
	 * @param form form
	 * @return res
	 */
	public ResponseResult<PatientOrderBenefitVo> choiceBenefit(PatientChooseBenefitForm form) {
		ResponseResult<List<OrderItemUseBo>> responseResult = choiceBenefitBo(form);
		if (!FALSE.equals(responseResult.getStatus())) {
			return ResponseUtil.error(responseResult.getStatus(), responseResult.getMsg());
		}
		PatientOrderBenefitVo result = transformBenefitInfo(responseResult.getData());
		return ResponseUtil.success(result);
	}

	/**
	 * 计算订单项目的优惠
	 *
	 * @param form         form
	 * @param benefitVo    benefitVo
	 * @param orderItemBos orderItemBos
	 */
	private void calculateBenefit(PatientChooseBenefitForm form, PatientOptionalBenefitVo benefitVo, List<OrderItemUseBo> orderItemBos) {
		Integer orgId = form.getOrgId();
		BenefitUseBo benefitUseBo = BenefitUseBo.getInstance();
		//设置患者选择优惠券的相关信息
		this.assignedBenefitBos(benefitUseBo, benefitVo, form);
		//设置优惠券的优惠项目明细
		this.assignedCouponItemDetail(benefitUseBo);
		//计算订单项目优惠信息
		for (OrderItemUseBo orderItem : orderItemBos) {
			Integer quantity = orderItem.getQuantity();
			//单个个体优惠
			if (quantity == 1) {
				singleItemUseBenefit(benefitUseBo, orderItem, orgId);
			}
			//多个数量项目优惠
			if (quantity > 1) {
				for (int i = 1; i <= quantity; i++) {
					multiItemUseBenefit(benefitUseBo, orderItem, orgId, i);
				}
			}
		}
	}

	/**
	 * 单个项目使用优惠
	 *
	 * @param benefitUseBo benefitUseBo
	 * @param orderItem    项目
	 * @param orgId        组织
	 */
	private void singleItemUseBenefit(BenefitUseBo benefitUseBo, OrderItemUseBo orderItem, Integer orgId) {
		//兑换券项目优惠
		List<PatientUseBenefitBo> exchangeBenefitBos = benefitUseBo.getExchangeBenefitBos();
		setUpSingleBenefitInfoForOrder(orderItem, orgId, exchangeBenefitBos);

		//套餐券项目优惠
		//订单明细对应的项目是否被优惠过，对于个体项目，除代金券如果项目已被优惠则不能叠加使用其他优惠券
		if (orderItem.getBenefitAmount() == null) {
			List<PatientUseBenefitBo> packageBenefitBos = benefitUseBo.getPackageBenefitBos();
			//按售出套餐单价正序排序
			sortPackageBySoldUnit(packageBenefitBos, orderItem);
			setUpSingleBenefitInfoForOrder(orderItem, orgId, exchangeBenefitBos);
		}
		//会员卡项目
		PatientUseBenefitBo memberBenefitBo = benefitUseBo.getMemberBenefitBo();
		PatientUseBenefitBo discountBenefitBos = benefitUseBo.getDiscountBenefitBos();
		//折扣券券项目优惠
		if (orderItem.getBenefitAmount() == null && memberBenefitBo == null && discountBenefitBos != null) {
			setUpSingleBenefitInfoForOrder(orderItem, orgId, Collections.singletonList(discountBenefitBos));
		}
		//会员卡优惠
		if (orderItem.getBenefitAmount() == null && memberBenefitBo != null) {
			setUpSingleBenefitInfoForOrder(orderItem, orgId, Collections.singletonList(discountBenefitBos));
		}
		//代金券项目优惠
		List<PatientUseBenefitBo> voucherBenefitBos = benefitUseBo.getVoucherBenefitBos();
		setUpSingleBenefitInfoForOrder(orderItem, orgId, voucherBenefitBos);
	}

	/**
	 * 多个数量项目使用优惠
	 *
	 * @param benefitUseBo benefitUseBo
	 * @param orderItem    项目
	 * @param orgId        组织
	 * @param i            项目的哪一次数量
	 */
	private void multiItemUseBenefit(BenefitUseBo benefitUseBo, OrderItemUseBo orderItem, Integer orgId, Integer i) {
		int mark = 0;
		//兑换券项目优惠
		List<PatientUseBenefitBo> exchangeBenefitBos = benefitUseBo.getExchangeBenefitBos();
		//兑换券项目优惠
		if (FALSE.equals(mark) && CollectionUtils.isNotEmpty(exchangeBenefitBos)) {
			exchangeBenefitBos = getBenefitBosByItem(exchangeBenefitBos, orderItem);
			mark = setUpMultiItemForOrder(exchangeBenefitBos, orderItem, orgId, i);
		}
		//套餐券项目优惠
		List<PatientUseBenefitBo> packageBenefitBos = benefitUseBo.getPackageBenefitBos();
		if (FALSE.equals(mark) && CollectionUtils.isNotEmpty(packageBenefitBos)) {
			//按售出套餐单价正序排序
			sortPackageBySoldUnit(packageBenefitBos, orderItem);
			mark = setUpMultiItemForOrder(packageBenefitBos, orderItem, orgId, i);
		}
		//折扣券项目优惠
		PatientUseBenefitBo discountBenefitBo = benefitUseBo.getDiscountBenefitBos();
		//会员卡项目
		PatientUseBenefitBo memberBenefitBo = benefitUseBo.getMemberBenefitBo();
		if (FALSE.equals(mark) && memberBenefitBo == null && discountBenefitBo != null) {
			setUpMultiItemForOrder(Collections.singletonList(discountBenefitBo), orderItem, orgId, i);
		}
		//会员卡项目优惠
		if (FALSE.equals(mark) && memberBenefitBo != null) {
			setUpMultiItemForOrder(Collections.singletonList(memberBenefitBo), orderItem, orgId, i);
		}
		//代金券优惠
		List<PatientUseBenefitBo> voucherBenefitBos = benefitUseBo.getVoucherBenefitBos();
		if (CollectionUtils.isNotEmpty(voucherBenefitBos)) {
			setUpMultiItemForOrder(voucherBenefitBos, orderItem, orgId, i);
		}
	}

	/**
	 * 转换bo为vo对象
	 *
	 * @param orderItemBos orderItemBos
	 * @return PatientOrderBenefitVo
	 */
	private PatientOrderBenefitVo transformBenefitInfo(List<OrderItemUseBo> orderItemBos) {
		PatientOrderBenefitVo result = new PatientOrderBenefitVo();
		BigDecimal totalBenefitAmount = orderItemBos.stream().filter(obj -> obj.getBenefitAmount() != null).map(OrderItemUseBo::getBenefitAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		result.setBenefitTotalAmount(totalBenefitAmount);
		List<PatientItemBenefitVo> itemList = orderItemBos.stream().filter(obj -> CollectionUtils.isNotEmpty(obj.getItemUseBenefitBos()))
				.map(obj -> {
					BigDecimal benefitAmount = obj.getBenefitAmount();
					benefitAmount = benefitAmount == null ? BigDecimal.ZERO : benefitAmount;
					PatientItemBenefitVo vo = new PatientItemBenefitVo();
					vo.setOrderDetailId(obj.getOrderDetailId());
					vo.setType(obj.getType());
					vo.setItemId(obj.getItemId());
					vo.setBenefitDiscountRate(obj.getBenefitDiscountRate());
					vo.setActualAmount(obj.getReceivableAmount().subtract(benefitAmount));
					List<ItemUseBenefitVo> itemUseBenefitVos = BeanCopierUtils.listGeneralCopyBean(obj.getItemUseBenefitBos(), ItemUseBenefitVo.class);
					vo.setItemBenefitList(itemUseBenefitVos);
					return vo;
				}).collect(toList());
		result.setItemList(itemList);
		return result;
	}

	/**
	 * 查找订单项目对应的优惠券信息
	 *
	 * @param orderItem  订单项目
	 * @param orgId      组织id
	 * @param benefitBos 优惠券信息
	 */
	private void setUpSingleBenefitInfoForOrder(OrderItemUseBo orderItem, Integer orgId, List<PatientUseBenefitBo> benefitBos) {
		for (PatientUseBenefitBo benefitBo : benefitBos) {
			//订单项目id对应的可用的优惠券信息
			ItemBenefitUseDetailBo benefitUseDetailBo = findBenefitForOrderItem(orgId, benefitBo, orderItem);
			if (benefitUseDetailBo != null) {
				Integer couponType = benefitBo.getCouponType();
				//订单项目原价
				BigDecimal originalPrice = orderItem.getReceivableAmount();
				//订单项目已优惠金额
				BigDecimal oldBenefitAmount = orderItem.getBenefitAmount() == null ? BigDecimal.ZERO : orderItem.getBenefitAmount();
				//订单项目应收金额（原价 - 已优惠金额）
				BigDecimal receivableAmount = originalPrice.subtract(oldBenefitAmount);
				BigDecimal benefitAmount;
				if (receivableAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
					if (EXCHANGE.equals(couponType) || SPECIAL_PACKAGE.equals(couponType)) {
						BigDecimal packageUnitPrice = benefitUseDetailBo.getPackageUnitPrice();
						benefitAmount = receivableAmount.compareTo(packageUnitPrice) > 0 ? receivableAmount.subtract(packageUnitPrice) : BigDecimal.valueOf(0);
						//个体项目设置优惠相关信息
						buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, TRUE.getCode());
						break;
					}
					if (DISCOUNT.equals(couponType)) {
						benefitAmount = receivableAmount.multiply(BigDecimal.valueOf(1).subtract(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						//个体项目设置优惠相关信息
						buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, TRUE.getCode());
						break;
					}
					if (MEMBER_CARD.equals(couponType)) {
						//订单项目id对应的可用的优惠券信息
						benefitAmount = receivableAmount.multiply(BigDecimal.valueOf(1).subtract(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						buildOrderProperty(benefitAmount, orderItem, benefitBo, null, MEMBER_TYPE.getCode(), null, TRUE.getCode());
					}
					if (VOUCHER.equals(couponType)) {
						if (benefitBo.getFace().compareTo(BigDecimal.valueOf(0)) > 0) {
							benefitAmount = receivableAmount.compareTo(benefitBo.getFace()) >= 0 ? benefitBo.getFace() : receivableAmount;
							//个体项目设置优惠相关信息
							buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, TRUE.getCode());
						}
					}
				}
			}
		}
	}

	/**
	 * 订单项目设置相关属性
	 *
	 * @param benefitAmount      优惠金额
	 * @param order              订单
	 * @param benefitBo          优惠信息
	 * @param benefitUseDetailBo 优惠项目明细
	 * @param couponType         优惠券类型
	 */
	private void buildOrderProperty(BigDecimal benefitAmount, OrderItemUseBo order, PatientUseBenefitBo benefitBo, ItemBenefitUseDetailBo benefitUseDetailBo,
	                                Integer benefitType, Integer couponType, Integer itemIndex) {
		BigDecimal oldBenefitAmount = order.getBenefitAmount();
		order.setBenefitAmount(oldBenefitAmount == null ? benefitAmount : oldBenefitAmount.add(benefitAmount));
		BigDecimal receivableAmount = order.getReceivableAmount();
		order.setBenefitDiscountRate(order.getBenefitAmount().divide(receivableAmount, 4, RoundingMode.HALF_UP));
		order.setQuantity(order.getQuantity() - 1);
		//是否可共用
		order.setPreMixAble(benefitBo.getMixable());
		if (VOUCHER.equals(couponType)) {
			BigDecimal face = benefitBo.getFace();
			benefitBo.setFace(face.subtract(benefitAmount));
		}
		Integer preId = 0;
		List<ItemUseBenefitBo> itemUseBenefitBos = order.getItemUseBenefitBos();
		ItemUseBenefitBo itemUseBenefitBo = new ItemUseBenefitBo();
		if (CollectionUtils.isNotEmpty(itemUseBenefitBos)) {
			//上一个id顺序
			preId = itemUseBenefitBos.stream().max(Comparator.comparing(ItemUseBenefitBo::getId)).get().getId();
		}
		//优惠券使用顺序
		itemUseBenefitBo.setId(preId + 1);
		itemUseBenefitBo.setBenefitId(benefitBo.getCardId());
		itemUseBenefitBo.setCouponId(benefitBo.getCouponId());
		itemUseBenefitBo.setBenefitType(benefitType);
		itemUseBenefitBo.setCouponType(couponType);
		itemUseBenefitBo.setBenefitName(benefitBo.getCouponName());
		itemUseBenefitBo.setItemIndex(itemIndex);
		//单个个体优惠金额
		BigDecimal oldCouponBenefitAmount = itemUseBenefitBo.getBenefitAmount();
		itemUseBenefitBo.setBenefitAmount(oldCouponBenefitAmount == null ? benefitAmount : oldCouponBenefitAmount.add(benefitAmount));
		itemUseBenefitBos.add(itemUseBenefitBo);
		order.setItemUseBenefitBos(itemUseBenefitBos);
		if (EXCHANGE.equals(couponType) || SPECIAL_PACKAGE.equals(couponType)) {
			benefitUseDetailBo.setCount(benefitUseDetailBo.getCount() - 1);
		}
	}

	private int setUpMultiItemForOrder(List<PatientUseBenefitBo> benefitBos, OrderItemUseBo orderItem, Integer orgId,
	                                   Integer itemIndex) {
		for (PatientUseBenefitBo benefitBo : benefitBos) {
			if (TRUE.equals(orderItem.getPreMixAble()) && (TRUE.equals(benefitBo.getMixable()) || itemIndex == 1)) {
				//订单项目id对应的可用的优惠券信息
				ItemBenefitUseDetailBo benefitUseDetailBo = findBenefitForOrderItem(orgId, benefitBo, orderItem);
				if (benefitUseDetailBo != null) {
					Integer couponType = benefitBo.getCouponType();
					//订单项目原价
					BigDecimal originalPrice = orderItem.getReceivableAmount();
					////订单项目已优惠金额
					BigDecimal oldBenefitAmount = orderItem.getBenefitAmount() == null ? BigDecimal.ZERO : orderItem.getBenefitAmount();
					//订单项目应收金额（原价 - 已优惠金额）
					BigDecimal receivableAmount = originalPrice.subtract(oldBenefitAmount);
					BigDecimal benefitAmount;
					if (receivableAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
						if (EXCHANGE.equals(couponType) || SPECIAL_PACKAGE.equals(couponType)) {
							BigDecimal packageUnitPrice = benefitUseDetailBo.getPackageUnitPrice();
							benefitAmount = receivableAmount.compareTo(packageUnitPrice) > 0 ? benefitAmount = receivableAmount.subtract(packageUnitPrice)
									: BigDecimal.valueOf(0);
							buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, itemIndex);
							return TRUE.getCode();
						}
						if (DISCOUNT.equals(couponType)) {
							benefitAmount = receivableAmount.multiply(BigDecimal.valueOf(1).subtract(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
									.setScale(2, BigDecimal.ROUND_HALF_UP);
							buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), DISCOUNT.getCode(), itemIndex);
							return TRUE.getCode();
						}
						if (MEMBER_CARD.equals(couponType)) {
							//订单项目id对应的可用的优惠券信息
							benefitAmount = receivableAmount.multiply(BigDecimal.valueOf(1).subtract(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
									.setScale(2, BigDecimal.ROUND_HALF_UP);
							buildOrderProperty(benefitAmount, orderItem, benefitBo, null, MEMBER_TYPE.getCode(), null, itemIndex);
							return TRUE.getCode();
						}
						if (VOUCHER.equals(couponType)) {
							if (benefitBo.getFace().compareTo(BigDecimal.valueOf(0)) > 0) {
								benefitAmount = receivableAmount.compareTo(benefitBo.getFace()) >= 0 ? benefitBo.getFace() : receivableAmount;
								buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, itemIndex);
								return TRUE.getCode();
							}
						}
					}
				}
			}
		}
		return FALSE.getCode();
	}

	/**
	 * 查询患者可选择优惠信息
	 *
	 * @param query query
	 * @return PatientOptionalBenefitVo
	 */
	public PatientOptionalBenefitVo initBenefit(PatientBenefitQuery query) {
		int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
		return getPatientBenefit(query.getPatientId(), query.getOrderId(), orgId);
	}

	private PatientOptionalBenefitVo getPatientBenefit(Integer patientId, Integer orderId, Integer orgId) {
		//查询患者可用优惠
		List<PatientBenefitBo> benefitBos = mapper.listBenefitByPatientId(patientId, orgId);
		//获取订单明细
		List<OrderDetail> orderDetail = treatmentServiceFeign.findOrderDetailByOrderRecordId(orderId);
		if (CollectionUtils.isEmpty(orderDetail)) {
			return null;
		}
		//订单的项目明细映射
		Map<Integer, Set<Integer>> itemMap = orderDetail.stream().collect(groupingBy(obj -> obj.getType().intValue(),
				mapping(OrderDetail::getBillingItemId, toSet())));
		for (PatientBenefitBo benefitBo : benefitBos) {
			if (VOUCHER.equals(benefitBo.getCouponType()) || DISCOUNT.equals(benefitBo.getCouponType())) {
				//订单中项目分类集合
				List<Integer> itemTypes = orderDetail.stream().map(obj -> obj.getType().intValue()).collect(toList());
				//校验订单价目项目是否可用优惠并赋值
				if (checkAndSetItemUsable(FALSE.getCode(), itemTypes, benefitBo, itemMap)) {
					continue;
				}
				//校验订单商品项目是否可用优惠并赋值
				if (checkAndSetItemUsable(TRUE.getCode(), itemTypes, benefitBo, itemMap)) {
					continue;
				}
			}
			if (EXCHANGE.equals(benefitBo.getCouponType())) {
				//校验兑换券价目是否可用优惠并赋值
				if (checkExchangeAndSetUsable(FALSE.getCode(), itemMap, benefitBo, EXCHANGE.getCode())) {
					continue;
				}
				//校验兑换券商品是否可用优惠并赋值
				if (checkExchangeAndSetUsable(TRUE.getCode(), itemMap, benefitBo, EXCHANGE.getCode())) {
					continue;
				}
			}
			if (SPECIAL_PACKAGE.equals(benefitBo.getCouponType())) {
				//校验套餐券价目是否可用优惠并赋值
				if (checkPackageAndSetUsable(FALSE.getCode(), itemMap, benefitBo, SPECIAL_PACKAGE.getCode())) {
					continue;
				}
				//校验套餐券商品是否可用优惠并赋值
				checkPackageAndSetUsable(TRUE.getCode(), itemMap, benefitBo, SPECIAL_PACKAGE.getCode());
			}
		}
		//排序（截止时间 asc）
		Comparator<PatientBenefitBo> comparator = Comparator.comparing(PatientBenefitBo::getUseDeadline)
				.thenComparing(PatientBenefitBo::getMixable, Comparator.reverseOrder());
		benefitBos.sort(comparator);
		//患者优惠信息转换
		return benefitBoConvertVo(patientId, benefitBos);
	}

	private boolean checkPackageAndSetUsable(Integer itemType, Map<Integer, Set<Integer>> itemMap, PatientBenefitBo benefitBo,
	                                         Integer couponType) {
		//订单项目明细
		Set<Integer> orderItemIds = itemMap.get(itemType);
		//查询套餐券优惠项目ids
		List<SpecialPackageCouponItem> items = getPackageItemInfo(benefitBo.getCouponId(), itemType);
		Set<Integer> itemIds = items.stream().map(SpecialPackageCouponItem::getItemId).collect(toSet());
		//查询卡券使用数量信息
		List<CouponItemUseBo> list = mapper.getCouponItemUseInfo(benefitBo.getCouponId(), benefitBo.getCardId(), itemType, couponType);
		//设置优惠券是否可以作用订单项目
		return setItemUsableStatus(itemIds, orderItemIds, benefitBo, list);
	}

	private boolean checkExchangeAndSetUsable(Integer itemType, Map<Integer, Set<Integer>> itemMap, PatientBenefitBo benefitBo,
	                                          Integer couponType) {
		//订单项目明细
		Set<Integer> orderItemIds = itemMap.get(itemType);
		//查询套餐券优惠项目ids
		List<PackageCouponItem> items = getExchangeItemInfo(benefitBo.getCouponId(), itemType);
		Set<Integer> itemIds = items.stream().map(PackageCouponItem::getItemId).collect(toSet());
		//查询卡券使用数量信息
		List<CouponItemUseBo> list = mapper.getCouponItemUseInfo(benefitBo.getCouponId(), benefitBo.getCardId(), itemType, couponType);
		//设置优惠券是否可以作用订单项目
		return setItemUsableStatus(itemIds, orderItemIds, benefitBo, list);
	}

	private boolean setItemUsableStatus(Set<Integer> itemIds, Set<Integer> orderItemIds, PatientBenefitBo benefitBo,
	                                    List<CouponItemUseBo> list) {
		if (CollectionUtils.isNotEmpty(itemIds)) {
			Set<Integer> joinIds = SetUtils.intersection(orderItemIds, itemIds);
			if (CollectionUtils.isNotEmpty(joinIds)) {
				//查找订单的项目是否可用该卡券
				Optional<CouponItemUseBo> optional = list.stream().filter(obj -> joinIds.contains(obj.getItemId()) && TRUE.equals(obj.getUsable())).findFirst();
				if (optional.isPresent()) {
					benefitBo.setItemUsable(TRUE.getCode());
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkAndSetItemUsable(Integer itemType, List<Integer> itemTypes, PatientBenefitBo benefitBo,
	                                      Map<Integer, Set<Integer>> itemMap) {
		//代金折扣券基础，价目范围映射 <选择范围，项目id（分类/明细）>
		Map<Integer, Set<Integer>> rangeTypeMap = getVoucherItemInfo(benefitBo.getCouponId(), itemType);
		Set<Integer> itemIds = Sets.newHashSet();
		if (!org.springframework.util.CollectionUtils.isEmpty(rangeTypeMap)) {
			//根据type获取订单项目明细 type  (0:价目  1:商品)
			Set<Integer> orderItemIdsForType = itemMap.get(itemType);
			if (rangeTypeMap.containsKey(SELECT_ALL.getCode()) && itemTypes.contains(itemType)) {
				benefitBo.setItemUsable(TRUE.getCode());
				return true;
			}
			if (CollectionUtils.isNotEmpty(orderItemIdsForType)) {
				if (rangeTypeMap.containsKey(SELECT_ITEM_DETAIL.getCode())) {
					//代金折扣券商品对应的项目ids
					Set<Integer> benefitItemIdsForType = rangeTypeMap.get(SELECT_ITEM_DETAIL.getCode());
					//订单和优惠券项目id交集，如不为空，则可以使用优惠
					Set<Integer> sameIds = SetUtils.intersection(orderItemIdsForType, benefitItemIdsForType);
					if (CollectionUtils.isNotEmpty(sameIds)) {
						benefitBo.setItemUsable(TRUE.getCode());
						return true;
					}
				}
				if (rangeTypeMap.containsKey(SELECT_ITEM_CATEGORY.getCode())) {
					Set<Integer> itemBenefitCategoryIds = rangeTypeMap.get(SELECT_ITEM_CATEGORY.getCode());
					for (Integer itemCategoryId : itemBenefitCategoryIds) {
						//价目
						if (FALSE.equals(itemType)) {
							//获取价目项目ids
							itemIds = getTariffIds(itemCategoryId);
						}
						//商品
						if (TRUE.equals(itemType)) {
							//获取商品项目ids
							itemIds = getShopTariffIds(itemCategoryId);
						}
						//订单和优惠券项目id交集，如不为空，则可以使用优惠
						Set<Integer> shopJoinIds = SetUtils.intersection(orderItemIdsForType, itemIds);
						if (CollectionUtils.isNotEmpty(shopJoinIds)) {
							benefitBo.setItemUsable(TRUE.getCode());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private PatientOptionalBenefitVo benefitBoConvertVo(Integer patientId, List<PatientBenefitBo> benefitBos) {
		//查询卡主信息
		Set<Integer> ownerIds = benefitBos.stream().map(PatientBenefitBo::getOwnerId).collect(toSet());
		List<PatientBaseInfoVo> owners = patientFeign.findPatientInfoByIds(Lists.newArrayList(ownerIds));
		Map<Integer, PatientBaseInfoVo> patientMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(owners)) {
			patientMap = owners.stream().collect(toMap(PatientBaseInfoVo::getId, Function.identity()));
		}
		PatientOptionalBenefitVo vo = new PatientOptionalBenefitVo();
		//卡主信息映射
		final Map<Integer, PatientBaseInfoVo> finalPatientMap = patientMap;
		//bo结果集映射
		Map<Integer, List<PatientBenefitBo>> boMap = benefitBos.stream().collect(groupingBy(PatientBenefitBo::getCouponType));
		boMap.forEach((k, v) -> {
			if (DISCOUNT.equals(k)) {
				List<PatientDiscountVo> discountVoList = v.stream().map(bo -> {
					PatientBaseInfoVo owner = finalPatientMap.get(bo.getOwnerId());
					PatientDiscountVo discountVo = BeanCopierUtils.generalCopyBean(bo, PatientDiscountVo.class);
					if (!patientId.equals(bo.getOwnerId())) {
						discountVo.setOwner(owner == null ? null : owner.getName());
					}
					return discountVo;
				}).collect(toList());
				vo.setDiscountVoList(discountVoList);
			}
			if (EXCHANGE.equals(k)) {
				List<PatientExchangeVo> exchangeVoList = v.stream().map(bo -> {
					PatientBaseInfoVo owner = finalPatientMap.get(bo.getOwnerId());
					PatientExchangeVo exchangeVo = BeanCopierUtils.generalCopyBean(bo, PatientExchangeVo.class);
					if (!patientId.equals(bo.getOwnerId())) {
						exchangeVo.setOwner(owner == null ? null : owner.getName());
					}
					return exchangeVo;
				}).collect(toList());
				vo.setExchangeVoList(exchangeVoList);
			}
			if (SPECIAL_PACKAGE.equals(k)) {
				List<PatientPackageVo> packageVoList = v.stream().map(bo -> {
					PatientBaseInfoVo owner = finalPatientMap.get(bo.getOwnerId());
					PatientPackageVo packageVo = BeanCopierUtils.generalCopyBean(bo, PatientPackageVo.class);
					if (!patientId.equals(bo.getOwnerId())) {
						packageVo.setOwner(owner == null ? null : owner.getName());
					}
					return packageVo;
				}).collect(toList());
				vo.setPackageVoList(packageVoList);
			}
			if (VOUCHER.equals(k)) {
				List<PatientVoucherVo> voucherVoList = v.stream().map(bo -> {
					PatientBaseInfoVo owner = finalPatientMap.get(bo.getOwnerId());
					PatientVoucherVo voucherVo = BeanCopierUtils.generalCopyBean(bo, PatientVoucherVo.class);
					if (!patientId.equals(bo.getOwnerId())) {
						voucherVo.setOwner(owner == null ? null : owner.getName());
					}
					return voucherVo;
				}).collect(toList());
				vo.setVoucherVoList(voucherVoList);
			}
		});
		//设置患者会员卡集合
		List<PatientMemberCardVo> memberCards = getPatientMemberCards(patientId);
		if (CollectionUtils.isNotEmpty(memberCards)) {
			vo.setMemberCardVoList(memberCards);
		}
		return vo;
	}

	/**
	 * 获取患者的会员卡信息
	 *
	 * @param patientId patientId
	 * @return list
	 */
	private List<PatientMemberCardVo> getPatientMemberCards(Integer patientId) {
		PatientMemberInfoQueryForm form = new PatientMemberInfoQueryForm();
		form.setPatientId(patientId);
		form.setBindType(FALSE.getCode());
		//查询患者的会员卡集合
		MemberInfoVo memberInfo = patientFeign.findMemberInfo(form);
		List<PatientMemberCardVo> memberCardVos = Lists.newArrayList();
		if (memberInfo != null) {
			MasertMemberInfoVo masertMemberInfoVo = memberInfo.getMasertMemberInfoVo();
			List<SecondaryMemberInfoVo> secondaryMemberInfoVos = memberInfo.getSecondaryMemberInfoVos();
			if (CollectionUtils.isNotEmpty(secondaryMemberInfoVos)) {
				memberCardVos = secondaryMemberInfoVos.stream().map(obj -> {
					PatientMemberCardVo memberCardVo = new PatientMemberCardVo();
					memberCardVo.setMemberCardId(obj.getId());
					memberCardVo.setMemberCardName(obj.getMemberCardName());
					//卡号
					memberCardVo.setMemberCardNumber(obj.getSecondaryCardNumber());
					//卡主
					memberCardVo.setOwner(obj.getSecondaryName());
					memberCardVo.setMemberCardRate(null);
					memberCardVo.setMemberCardRate(BigDecimal.valueOf(obj.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
					//todo
					memberCardVo.setPath(null);
					return memberCardVo;
				}).collect(toList());
			}
			if (masertMemberInfoVo != null) {
				PatientMemberCardVo memberCardVo = new PatientMemberCardVo();
				memberCardVo.setMemberCardId(masertMemberInfoVo.getId());
				memberCardVo.setMemberCardName(masertMemberInfoVo.getMasterMemberCardName());
				//卡号
				memberCardVo.setMemberCardNumber(masertMemberInfoVo.getMasterCardNumber());
				memberCardVo.setMemberCardRate(BigDecimal.valueOf(masertMemberInfoVo.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
				//todo
				memberCardVo.setPath(null);
				memberCardVos.add(memberCardVo);
			}
		}
		return memberCardVos;
	}

	private CardQrCodeVo checkCouponDeadline(Integer couponId, Integer type) {
		Date now = Date.from(LocalDate.now().atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
		CardQrCodeVo vo = new CardQrCodeVo();
		vo.setCardQrCodeType(QR_CODE_NORMAL.getCode());
		//查询产品有效期
		Date deadline = getCouponDeadLine(type, couponId);
		if (deadline != null && now.after(deadline)) {
			log.warn("优惠券[{}]已过期", couponId);
			vo.setCardQrCodeType(QR_CODE_EXPIRED.getCode());
			return vo;
		}
		vo.setActivationDeadline(deadline == null ? COUPON_ALWAYS_EFFECT : deadline.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate()
				.format(DateTimeFormatter.ISO_LOCAL_DATE));
		return vo;
	}

	/**
	 * 查询产品有效期
	 *
	 * @param type     type
	 * @param couponId couponId
	 * @return date
	 */
	private Date getCouponDeadLine(Integer type, Integer couponId) {
		Date deadline = null;
		Example example;
		if (VOUCHER.equals(type)) {
			example = new Example(VoucheCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			VoucheCoupon voucheCoupon = voucherMapper.selectOneByExample(example);
			deadline = voucheCoupon.getActivationDeadline();
		}
		if (DISCOUNT.equals(type)) {
			example = new Example(DiscountCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			DiscountCoupon discountCoupon = discountMapper.selectOneByExample(example);
			deadline = discountCoupon.getActivationDeadline();
		}
		if (EXCHANGE.equals(type)) {
			example = new Example(PackageCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			PackageCoupon packageCoupon = packageMapper.selectOneByExample(example);
			deadline = packageCoupon.getActivationDeadline();
		}
		if (SPECIAL_PACKAGE.equals(type)) {
			example = new Example(SpecialPackageCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			SpecialPackageCoupon specialPackageCoupon = specialPackageMapper.selectOneByExample(example);
			deadline = specialPackageCoupon.getActivationDeadline();
		}
		if (RECHARGE.equals(type)) {
			example = new Example(RechargeCard.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			RechargeCard rechargeCard = rechargeCardMapper.selectOneByExample(example);
			deadline = rechargeCard.getRechargeDeadline();
		}
		return deadline;
	}

	/**
	 * 查询产品配置共享人
	 *
	 * @param type     type
	 * @param couponId couponId
	 * @return int
	 */
	private Boolean getShareStatus(Integer type, Integer couponId) {
		Boolean share = null;
		Example example;
		if (VOUCHER.equals(type)) {
			example = new Example(VoucheCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			VoucheCoupon voucheCoupon = voucherMapper.selectOneByExample(example);
			share = voucheCoupon.getIsShare();
		}
		if (DISCOUNT.equals(type)) {
			example = new Example(DiscountCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			DiscountCoupon discountCoupon = discountMapper.selectOneByExample(example);
			share = discountCoupon.getIsShare();
		}
		if (EXCHANGE.equals(type)) {
			example = new Example(PackageCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			PackageCoupon packageCoupon = packageMapper.selectOneByExample(example);
			share = packageCoupon.getIsShare();
		}
		if (SPECIAL_PACKAGE.equals(type)) {
			example = new Example(SpecialPackageCoupon.class);
			example.createCriteria().andEqualTo("couponId", couponId);
			SpecialPackageCoupon specialPackageCoupon = specialPackageMapper.selectOneByExample(example);
			share = specialPackageCoupon.getIsShare();
		}
		return share;
	}

	private List<Future<Card>> createCardEntity(Integer couponId, String couponCode, List<AllocateNumBo> numBoList, Integer loginUserId,
	                                            LocalDateTime generateDate, CountDownLatch cardLatch, int totalTask) {
		List<Future<Card>> cardFutureList = Lists.newArrayListWithCapacity(totalTask);
		for (AllocateNumBo allocateNumBo : numBoList) {
			//当前组织分配数
			int count = allocateNumBo.getCount();
			//当前组织起始index
			AtomicInteger generateNum = new AtomicInteger(allocateNumBo.getStartIndex());
			for (int i = 0; i < count; i++) {
				cardFutureList.add(cardThreadPool.submit(() -> {
					Card card = new Card();
					card.setOrgId(allocateNumBo.getOrgId());
					card.setCouponId(couponId);
					card.setCouponAllocateId(allocateNumBo.getCouponAllocateId());
					card.setCardNumber(String.format(couponCode + "%06d", generateNum.getAndIncrement()));
					//生成卡密
					card.setCardPassword(generatePass());
					card.setStatus(SALE_PENDING.getCode());
					card.setCrtId(loginUserId);
					card.setCrtTime(generateDate);
					card.setUpdId(loginUserId);
					cardLatch.countDown();
					return card;
				}));
			}
		}
		return cardFutureList;
	}

	private List<Card> getAllocateFutureResult(List<Future<Card>> futureList) throws Exception {
		List<Card> list = Lists.newArrayList();
		for (Future<Card> future : futureList) {
			list.add(future.get());
		}
		return list;
	}

	private List<AllocateNumBo> getCalculateBoFutureResult(List<Future<AllocateNumBo>> boFutureList) throws Exception {
		List<AllocateNumBo> list = Lists.newArrayList();
		for (Future<AllocateNumBo> future : boFutureList) {
			list.add(future.get());
		}
		return list;
	}

	private List<Future<AllocateNumBo>> calculateNumber(List<ClinicAllocateModel> allocateList, int maxNum, CountDownLatch latch) {
		//创建线程分配卡券
		AtomicInteger maxNumAto = new AtomicInteger(maxNum);
		List<Future<AllocateNumBo>> boFutureList = Lists.newArrayListWithCapacity(allocateList.size());
		for (ClinicAllocateModel model : allocateList) {
			boFutureList.add(cardThreadPool.submit(() -> {
				AllocateNumBo numBo = AllocateNumBo.getInstance();
				numBo.setOrgId(model.getOrgId());
				numBo.setCouponAllocateId(model.getCouponAllocateId());
				numBo.setStartIndex(maxNumAto.get() + 1);
				numBo.setCount(model.getAllocateNum());
				maxNumAto.addAndGet(model.getAllocateNum());
				latch.countDown();
				return numBo;
			}));
		}
		return boFutureList;
	}

	private GenerateAllocatePageVo allocateBoConvertVo(GenerateAllocatePageBo bo) {
		//属性复制
		GenerateAllocatePageVo vo = BeanCopierUtils.generalCopyBean(bo, GenerateAllocatePageVo.class);
		//提交人信息
		SysUserInfoDetail submitUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(bo.getSubmitUserId());
		//配给人信息
		if (bo.getAllocateUserId() != null) {
			SysUserInfoDetail allocateUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(bo.getAllocateUserId());
			vo.setAllocateUserName(allocateUser == null ? null : allocateUser.getName());
		}
		vo.setSubmitterName(submitUser == null ? null : submitUser.getName());
		vo.setCouponTypeName(CouponTypeEnum.getValue(bo.getCouponType()));
		return vo;
	}

	private RestErrorBo checkCouponAllocate(List<ClinicAllocateModel> allocateList, Integer couponId, LocalDateTime submitDate) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		//该产品卡券已生成数量
		int sumGenerateNum = mapper.getSumNumByCouponId(couponId);
		//所有组织卡券总数
		int sumAllocate = allocateList.stream().mapToInt(ClinicAllocateModel::getAllocateNum).sum();
		if (sumGenerateNum >= 999999 || (sumGenerateNum + sumAllocate) > 999999) {
			log.warn("【卡券生成失败】：已超过卡券最大生成数量");
			errorBo.setError(DiscountError.BEYOND_CARD_LIMIT_NUM);
			return errorBo;
		}
		Example example = new Example(CouponAllocate.class);
		example.createCriteria().andEqualTo("couponId", couponId)
				.andEqualTo("crtTime", submitDate);
		List<CouponAllocate> list = allocateMapper.selectByExample(example);
		if (CollectionUtils.isEmpty(list)) {
			log.warn("【卡券生成失败】：优惠券[{}]未分配，请先分配再生成", couponId);
			errorBo.setError(DiscountError.COUPON_NOT_ALLOCATE);
			return errorBo;
		}
		//校验数量
		if (list.size() != allocateList.size()) {
			errorBo.setError(DiscountError.NUM_NOT_EQUAL);
			return errorBo;
		}
		//已分配优惠券映射
		Map<Integer, CouponAllocate> entityMap = list.stream()
				.collect(toMap(CouponAllocate::getId, Function.identity()));
		for (ClinicAllocateModel allocateModel : allocateList) {
			CouponAllocate couponAllocate = entityMap.get(allocateModel.getCouponAllocateId());
			if (couponAllocate != null && !couponAllocate.getOrgId().equals(allocateModel.getOrgId())) {
				//获取组织名
				String orgName = getOrgName(allocateModel.getOrgId());
				log.warn("【卡券生成失败】：{}未分配优惠券[{}]，不能生成分配", orgName, couponId);
				errorBo.setError(DiscountError.ORG_NOT_ALLOCATE);
				errorBo.setMsg(orgName);
				return errorBo;
			}
			//校验组织分配优惠券数量和时间
			if (couponAllocate == null || !allocateModel.getAllocateNum().equals(couponAllocate.getAllocateNum())
					|| !submitDate.equals(DateUtil.dateToLocalDateTime(couponAllocate.getCrtTime()))) {
				//获取组织名
				String orgName = getOrgName(allocateModel.getOrgId());
				log.warn("【卡券生成失败】：[{}]优惠券分配时间[{}]", orgName, submitDate);
				errorBo.setError(DiscountError.ORG_BATCH_ERROR);
				errorBo.setMsg(orgName);
				return errorBo;
			}
		}
		return errorBo;
	}

	/**
	 * 加密加密生成卡券密码
	 *
	 * @return str
	 */
	private static String generatePass() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < CARD_PASS_BIT; i++) {
			builder.append(new SecureRandom().nextInt(10));
		}
		return Base64.getEncoder().encodeToString(builder.toString().getBytes());
	}

	private List<Integer> listIdsBySubmitParam(GenerateAllocateCardQuery query) {
		Example example = new Example(CouponAllocate.class);
		example.createCriteria().andEqualTo("couponId", query.getCouponId())
				.andEqualTo("crtTime", query.getSubmitDate());
		List<CouponAllocate> list = allocateMapper.selectByExample(example);
		return list.stream().map(CouponAllocate::getId).collect(toList());
	}

	private CardSalePageVo cardConvertPageVo(Card card) {
		CardSalePageVo vo = BeanCopierUtils.generalCopyBean(card, CardSalePageVo.class);
		vo.setCardPassword(new String(Base64.getDecoder().decode(card.getCardPassword())));
		vo.setSoldTypeName(SoldTypeEnum.getValue(card.getSoldType()));
		vo.setSoldStatusName(CardStatusEnum.getValue(card.getStatus()));
		vo.setPayStatus(TrueFalseEnum.getValue(card.getPay()));
		vo.setSoldWayName(SoldWayEnum.getValue(card.getSoldWay()));
		return vo;
	}

	private void updateCardForSold(Card card, CardSoldForm form, Integer loginUserId) {
		Card updateCard = BeanCopierUtils.generalCopyBean(form, Card.class);
		updateCard.setStatus(ACTIVE_PENDING.getCode());
		if (SoldTypeEnum.SOLD.equals(form.getSoldType())) {
			updateCard.setPay(TRUE.equals(form.getSoldAndPay()) ? TRUE.getCode() : FALSE.getCode());
		}
		updateCard.setSoldDate(LocalDateTime.now());
		updateCard.setSellerUserId(loginUserId);
		updateCard.setUpdId(loginUserId);
		updateCard.setId(card.getId());
		//卡券二维码签名
		updateCard.setLink(Base64.getEncoder().encodeToString(Joiner.on(":").join(new BCryptPasswordEncoder(UserConstant.PW_ENCODER_SALT)
				.encode(Joiner.on(":").join(card.getCardNumber(), card.getCardPassword())), card.getId())
				.getBytes()));
		mapper.updateByPrimaryKeySelective(updateCard);
	}

	private void updateCardForCancel(Card card) {
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		Card cancelCard = new Card();
		cancelCard.setOrgId(card.getOrgId());
		cancelCard.setCouponId(card.getCouponId());
		cancelCard.setCouponAllocateId(card.getCouponAllocateId());
		cancelCard.setCardNumber(card.getCardNumber());
		//重新生成卡密
		cancelCard.setCardPassword(generatePass());
		cancelCard.setStatus(SALE_PENDING.getCode());
		cancelCard.setCrtId(card.getCrtId());
		cancelCard.setCrtTime(card.getCrtTime());
		cancelCard.setUpdId(loginUserId);
		cancelCard.setId(card.getId());
		mapper.updateByPrimaryKey(cancelCard);
	}

	/**
	 * 自有平台卡券激活
	 *
	 * @param patientId
	 * @param form
	 * @param loginUserId
	 */
	private void updateOwnActiveCard(Integer patientId, OwnCardActiveForm form, Integer loginUserId) {
		Integer activeOrgId = StringUtils.isBlank(BaseContextHandler.getOrgId()) ? null : Integer.valueOf(BaseContextHandler.getOrgId());
		LocalDateTime now = LocalDateTime.now();
		Card ownActiveCard = new Card();
		ownActiveCard.setId(form.getCardId());
		ownActiveCard.setPatientId(patientId);
		ownActiveCard.setActiveOrgId(activeOrgId);
		ownActiveCard.setActiveUserId(loginUserId);
		ownActiveCard.setStatus(ACTIVATED.getCode());
		if (form.getPayId() != null) {
			ownActiveCard.setSoldAndPay(TRUE.getCode());
			ownActiveCard.setPayId(form.getPayId());
			ownActiveCard.setPay(TRUE.getCode());
		}
		ownActiveCard.setUpdId(loginUserId);
		ownActiveCard.setActiveDate(now);
		mapper.updateByPrimaryKeySelective(ownActiveCard);
	}

	/**
	 * 第三方平台卡券激活
	 *
	 * @param patientId   患者id
	 * @param form        参数
	 * @param loginUserId 登录人
	 */
	private Card insertOtherActiveCard(Integer patientId, OtherCardActiveForm form, Integer loginUserId) {
		Integer activeOrgId = StringUtils.isBlank(BaseContextHandler.getOrgId()) ? null : Integer.valueOf(BaseContextHandler.getOrgId());
		Card insertOtherCard = BeanCopierUtils.generalCopyBean(form, Card.class);
		insertOtherCard.setOrgId(0);
		insertOtherCard.setThirdCardNumber(form.getThirdCardNumber());
		insertOtherCard.setActiveOrgId(activeOrgId);
		insertOtherCard.setActiveUserId(loginUserId);
		insertOtherCard.setCouponAllocateId(0);
		insertOtherCard.setPatientId(patientId);
		insertOtherCard.setStatus(ACTIVATED.getCode());
		insertOtherCard.setCrtId(loginUserId);
		insertOtherCard.setUpdId(loginUserId);
		insertOtherCard.setActiveDate(LocalDateTime.now());
		mapper.insertSelective(insertOtherCard);
		return insertOtherCard;
	}

	private Card configShareVoConvertCard(Integer cardId, ConfigSharerForm form) {
		Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
		Card shareCard = new Card();
		shareCard.setId(cardId);
		shareCard.setSharer(form.getSharerIdStr());
		shareCard.setUpdId(loginUserId);
		return shareCard;
	}

	private String getOrgName(Integer orgId) {
		if (orgId == null) {
			return null;
		}
		OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
		//获取组织名称
		return orgInfo == null ? null : orgInfo.getName();
	}

	/**
	 * 检查优惠券
	 *
	 * @param couponInfo couponInfo
	 * @return RestErrorBo
	 */
	private RestErrorBo checkCouponForSale(Integer couponId, CouponCommonInfo couponInfo) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		if (couponInfo == null || !couponInfo.getIsInservice()) {
			log.warn("【售卖失败】优惠券[{}]不存在", couponId);
			errorBo.setError(DiscountError.COUPON_NOT_EXIST);
			return errorBo;
		}
		//售出开始时间
		Date saleStartDate = couponInfo.getAvailableSaleStartDate();
		//售出结束时间
		Date saleEndDate = couponInfo.getAvailableSaleEndDate();
		Date now = new Date();
		if (saleStartDate != null && saleEndDate != null &&
				(now.before(saleStartDate) || now.after(saleEndDate))) {
			log.warn("【售卖失败】卡券不在优惠券[{}]售出时间范围内", couponInfo.getId());
			errorBo.setError(DiscountError.SOLD_DATE_RANGE_ERROR);
			return errorBo;
		}
		return errorBo;
	}

	/**
	 * 检查优惠券激活信息
	 *
	 * @return RestErrorBo
	 */
	private RestErrorBo checkCouponForActive(Integer couponId) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
		if (couponInfo == null || !couponInfo.getIsInservice()) {
			log.warn("【激活失败】优惠券[{}]不存在", couponId);
			errorBo.setError(DiscountError.COUPON_NOT_EXIST);
			return errorBo;
		}
		//查询产品有效期
		Date deadline = getCouponDeadLine(couponInfo.getType().intValue(), couponId);
		Date now = Date.from(LocalDate.now().atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
		if (deadline != null && now.after(deadline)) {
			log.warn("优惠券{}已过期", couponInfo.getName());
			errorBo.setError(DiscountError.CARD_BEYOND_DEADLINE);
			return errorBo;
		}
		return errorBo;
	}

	/**
	 * 检查卡券售出信息
	 *
	 * @param card     card
	 * @param couponId couponId
	 * @param orgId    orgId
	 * @param orgName  orgName
	 * @return RestErrorBo
	 */
	private RestErrorBo checkCardForSale(Integer cardId, Card card, Integer couponId, Integer orgId, String orgName) {
		//校验卡券基础信息
		RestErrorBo errorBo = checkCardBaseInfo(cardId, card, couponId, orgId, orgName);
		if (!SALE_PENDING.equals(card.getStatus())) {
			log.warn("【取消售卖失败】卡券[{}]售卖状态异常", card.getCardNumber());
			errorBo.setError(DiscountError.CARD_SOLD_STATUS_ERROR);
			return errorBo;
		}
		return errorBo;
	}

	/**
	 * 检查卡券取消售出信息
	 *
	 * @param card     card
	 * @param couponId couponId
	 * @param orgId    orgId
	 * @return RestErrorBo
	 */
	private RestErrorBo checkCardForCancelSale(Integer cardId, Card card, Integer couponId, Integer orgId) {
		//获取组织名
		String orgName = getOrgName(orgId);
		//校验卡券基础信息
		RestErrorBo errorBo = checkCardBaseInfo(cardId, card, couponId, orgId, orgName);
		if (!ACTIVE_PENDING.equals(card.getStatus())) {
			log.warn("【取消售卖失败】卡券[{}]售卖状态异常", card.getCardNumber());
			errorBo.setError(DiscountError.CARD_SOLD_STATUS_ERROR);
			return errorBo;
		}
		return errorBo;
	}

	/**
	 * 校验卡券信息（激活卡券）
	 *
	 * @param payId
	 * @param card
	 * @return
	 */
	private RestErrorBo checkCardForOwnActive(Integer payId, Card card) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		if (card == null) {
			log.warn("【激活失败】卡券不存在");
			errorBo.setError(DiscountError.CARD_NOT_EXIST);
			return errorBo;
		}
		if (card.getActiveOrgId() != null || ACTIVATED.equals(card.getStatus()) || card.getPatientId() != null) {
			log.warn("【激活失败】卡券[{}]已被激活", card.getCardNumber());
			errorBo.setError(DiscountError.CARD_IS_ACTIVATED);
			return errorBo;
		}
		if (!ACTIVE_PENDING.equals(card.getStatus())) {
			log.warn("【激活失败】该卡券[{}]不是待激活状态，不能激活", card.getCardNumber());
			errorBo.setError(DiscountError.CARD_ACTIVE_STATUS_ERROR);
			return errorBo;
		}
		if (FALSE.equals(card.getSoldAndPay())) {
			if (payId == null) {
				log.warn("卡券未收费：{}", card.getCardNumber());
				errorBo.setError(DiscountError.CARD_NOT_CHARGE);
				return errorBo;
			}
		} else {
			if (payId != null) {
				log.warn("卡券已收费：{}", card.getCardNumber());
				errorBo.setError(DiscountError.CARD_IS_CHARGED);
				return errorBo;
			}
		}
		return errorBo;
	}

	private RestErrorBo checkCardForOtherActive(String cardNumber) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		Example example = new Example(Card.class);
		example.createCriteria().andEqualTo("cardNumber", cardNumber)
				.andNotEqualTo("orgId", ZERO);
		Card card = mapper.selectOneByExample(example);
		if (card != null) {
			log.warn("【第三方平台激活失败】自有平台卡券{}不允许在第三方平台激活", cardNumber);
			errorBo.setError(DiscountError.OTHER_ALLOW_ACTIVE_OWN);
			return errorBo;
		}
		return errorBo;
	}

	private RestErrorBo checkCardBaseInfo(Integer cardId, Card card, Integer couponId, Integer orgId, String orgName) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		if (card == null) {
			log.warn("【卡券校验失败】卡券[{}]不存在", cardId);
			errorBo.setError(DiscountError.CARD_NOT_EXIST);
			return errorBo;
		}
		if (!orgId.equals(card.getOrgId())) {
			log.warn("【卡券校验失败】卡券[{}]不属于[{}]", card.getCardNumber(), orgName);
			errorBo.setError(DiscountError.CARD_NOT_BELONG_ORG);
			errorBo.setMsg(orgName);
			return errorBo;
		}
		if (!couponId.equals(card.getCouponId())) {
			CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(card.getCouponId());
			log.warn("【卡券校验失败】该卡券是{}, 卡券类型异常", couponInfo.getName());
			errorBo.setError(DiscountError.CARD_NOT_BELONG_COUPON);
			errorBo.setMsg(couponInfo.getName());
			return errorBo;
		}
		return errorBo;
	}

	private PatientCardBaseVo patientCardBoConvertVo(Integer queryType, PatientCardBo bo) {
		PatientCardBaseVo baseVo;
		if (TRUE.equals(queryType)) {
			PatientShareCardVo shareCardVo = BeanCopierUtils.generalCopyBean(bo, PatientShareCardVo.class);
			//查询卡主
			PatientBaseInfo patientInfo = patientFeign.findPatientInfoById(bo.getCardOwner());
			shareCardVo.setCardOwner(patientInfo == null ? null : patientInfo.getName());
			baseVo = shareCardVo;
		} else {
			PatientOwnCardVo ownCardVo = BeanCopierUtils.generalCopyBean(bo, PatientOwnCardVo.class);
			//查询销售渠道
			SalesChannel salesChannel = salesChannelMapper.selectByPrimaryKey(bo.getSaleChannelId());
			ownCardVo.setSaleChannelName(salesChannel == null ? null : salesChannel.getName());
			baseVo = ownCardVo;
		}
		baseVo.setCouponTypeName(CouponTypeEnum.getValue(bo.getCouponType()));
		//产品分类
		ProductType productType = productTypeMapper.selectByPrimaryKey(bo.getProductTypeId());
		baseVo.setProductTypeName(productType == null ? null : productType.getName());
		baseVo.setUseWayName(UseWayEnum.getValue(bo.getUseWay()));
		return baseVo;
	}

	/**
	 * 获取价目项目id集合
	 *
	 * @param itemCategoryId itemCategoryId
	 * @return set
	 */
	private Set<Integer> getTariffIds(Integer itemCategoryId) {
		Set<Integer> itemIds = Sets.newHashSet();
		BaseTariff tariffEntity = new BaseTariff();
		tariffEntity.setTariffCategoryId(itemCategoryId);
		List<BaseTariff> baseTariffList = treatmentServiceFeign.findBaseTariffList(tariffEntity);
		if (CollectionUtils.isNotEmpty(baseTariffList)) {
			//代金折扣券价目分类对应的项目ids
			itemIds = baseTariffList.stream().map(BaseTariff::getId).collect(toSet());
		}
		return itemIds;
	}

	/**
	 * 获取商品项目id集合
	 *
	 * @param itemCategoryId itemCategoryId
	 * @return set
	 */
	private Set<Integer> getShopTariffIds(Integer itemCategoryId) {
		Set<Integer> itemIds = Sets.newHashSet();
		BaseOralTariff entity = new BaseOralTariff();
		entity.setOralTariffCategoryId(itemCategoryId);
		List<BaseOralTariff> baseShopList = treatmentServiceFeign.findBaseOralTariffList(entity);
		if (CollectionUtils.isNotEmpty(baseShopList)) {
			//代金折扣券商品分类对应的项目ids
			itemIds = baseShopList.stream().map(BaseOralTariff::getId).collect(toSet());
		}
		return itemIds;
	}

	/**
	 * 查询代金券优惠项目明细
	 *
	 * @param couponId 优惠券id
	 * @param itemType 项目类型（0：价目  1：商品）
	 * @return map
	 */
	private Map<Integer, Set<Integer>> getVoucherItemInfo(Integer couponId, Integer itemType) {
		List<VoucherDiscountItem> voucherDiscountItems = getCouponItemByCouponId(couponId, voucherDiscountItemMapper, VoucherDiscountItem.class);
		//<项目类型，选择范围，项目明细集合>
		Map<Integer, Map<Integer, Set<Integer>>> voucherDiscountItemMap = voucherDiscountItems.stream().collect(
				groupingBy(VoucherDiscountItem::getType,
						groupingBy(obj -> obj.getChoiceRangType().intValue(), mapping(VoucherDiscountItem::getItemId, toSet()))));
		//代金折扣券基础，价目范围映射 <选择范围，项目id（分类/明细）>
		return voucherDiscountItemMap.get(itemType);
	}

	/**
	 * 查询兑换券优惠项目ids
	 *
	 * @param couponId 优惠券id
	 * @param itemType 项目类型（0：价目  1：商品）
	 * @return map
	 */
	private List<PackageCouponItem> getExchangeItemInfo(Integer couponId, Integer itemType) {
		List<PackageCouponItem> packageCouponItems = getCouponItemByCouponId(couponId, packageCouponItemMapper, PackageCouponItem.class);
		//<项目类型，项目明细集合>
		Map<Integer, List<PackageCouponItem>> exchangeItemMap = packageCouponItems.stream().collect(
				groupingBy(PackageCouponItem::getType));
		return exchangeItemMap.get(itemType);
	}

	private List getCouponItemByCouponId(Integer couponId, Mapper mapper, Class<?> clazz) {
		if (couponId != null) {
			Example example = new Example(clazz);
			example.createCriteria().andEqualTo("couponId", couponId);
			return mapper.selectByExample(example);
		}
		return Collections.emptyList();
	}

	/**
	 * 查询套餐券优惠项目ids
	 *
	 * @param couponId 优惠券id
	 * @param itemType 项目类型（0：价目  1：商品）
	 * @return map
	 */
	private List<SpecialPackageCouponItem> getPackageItemInfo(Integer couponId, Integer itemType) {
		Example example = new Example(SpecialPackageCouponItem.class);
		example.createCriteria().andEqualTo("couponId", couponId);
		List<SpecialPackageCouponItem> specialPackageCouponItems = specialPackageCouponItemMapper.selectByExample(example);
		//<项目类型，项目明细集合>
		Map<Integer, List<SpecialPackageCouponItem>> specialPackageItemMap = specialPackageCouponItems.stream().collect(
				groupingBy(SpecialPackageCouponItem::getType));
		return specialPackageItemMap.get(itemType);
	}

	/**
	 * 校验患者选择优惠信息
	 *
	 * @param benefitVo 患者可用优惠信息
	 * @param form      患者选择优惠信息
	 * @return errorbo
	 */
	private RestErrorBo checkChoiceBenefitForItem(PatientOptionalBenefitVo benefitVo, PatientChooseBenefitForm form) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		//优惠券使用顺序（兑换、套餐、折扣、会员卡、代金）
		boolean isExist;
		//校验会员卡
		Integer useMemberCardId = form.getMemberCardId();
		List<PatientMemberCardVo> memberCardVoList = benefitVo.getMemberCardVoList();
		if (useMemberCardId != null) {
			if (CollectionUtils.isEmpty(memberCardVoList)) {
				errorBo.setError(DiscountError.PATIENT_MEMBER_NULL);
				return errorBo;
			} else {
				isExist = memberCardVoList.stream().map(PatientMemberCardVo::getMemberCardId).collect(toList()).contains(useMemberCardId);
				if (!isExist) {
					errorBo.setError(DiscountError.PATIENT_NOT_OWN_MEMBER);
					return errorBo;
				}
			}
		}
		//校验折扣券
		Integer useDiscountId = form.getDiscountId();
		List<PatientDiscountVo> discountVoList = benefitVo.getDiscountVoList();
		if (useDiscountId != null) {
			if (CollectionUtils.isEmpty(discountVoList)) {
				errorBo.setError(DiscountError.PATIENT_DISCOUNT_NULL);
				return errorBo;
			} else {
				isExist = discountVoList.stream().filter(obj -> TRUE.equals(obj.getItemUsable())).map(PatientDiscountVo::getCardId)
						.collect(toList()).contains(useDiscountId);
				if (!isExist) {
					errorBo.setError(DiscountError.PATIENT_NOT_OWN_DISCOUNT);
					return errorBo;
				}
			}
		}
		//校验兑换券
		List<Integer> useExchangeIds = form.getExchangeIds();
		List<PatientExchangeVo> exchangeVoList = benefitVo.getExchangeVoList();
		if (CollectionUtils.isNotEmpty(useExchangeIds)) {
			if (CollectionUtils.isEmpty(exchangeVoList)) {
				errorBo.setError(DiscountError.PATIENT_EXCHANGE_NULL);
				return errorBo;
			} else {
				//可作用在项目的兑换券卡券
				List<Integer> exchangeIdsForItem = exchangeVoList.stream().filter(obj -> TRUE.equals(obj.getItemUsable()))
						.map(PatientExchangeVo::getCardId).collect(toList());
				for (Integer exchangeId : useExchangeIds) {
					if (!exchangeIdsForItem.contains(exchangeId)) {
						errorBo.setError(DiscountError.PATIENT_NOT_OWN_EXCHANGE);
						Card card = mapper.selectByPrimaryKey(exchangeId);
						errorBo.setMsg(card == null ? null : card.getCardNumber());
						return errorBo;
					}
				}
				Map<Integer, Long> limitCountMap = exchangeVoList.stream().filter(obj -> useExchangeIds.contains(obj.getCardId())).
						collect(groupingBy(PatientExchangeVo::getCouponId, counting()));
				//校验单个账单使用限制数
				errorBo = checkLimitCount(limitCountMap, PackageCoupon.class, EXCHANGE.getCode());
				if (errorBo.getError() != null) {
					return errorBo;
				}
			}
		}
		//校验套餐券
		List<Integer> usePackageIds = form.getPackageIds();
		List<PatientPackageVo> packageVoList = benefitVo.getPackageVoList();
		if (CollectionUtils.isNotEmpty(usePackageIds)) {
			if (CollectionUtils.isEmpty(packageVoList)) {
				errorBo.setError(DiscountError.PATIENT_PACKAGE_NULL);
				return errorBo;
			} else {
				List<Integer> choiceCardIds = Lists.newArrayList();
				//可作用在项目的兑换券卡券
				List<Integer> packageIdsForItem = packageVoList.stream().filter(obj -> TRUE.equals(obj.getItemUsable()))
						.map(PatientPackageVo::getCardId).collect(toList());
				for (Integer packageId : usePackageIds) {
					if (!packageIdsForItem.contains(packageId)) {
						errorBo.setError(DiscountError.PATIENT_NOT_OWN_PACKAGE);
						Card card = mapper.selectByPrimaryKey(packageId);
						errorBo.setMsg(card == null ? null : card.getCardNumber());
						return errorBo;
					} else {
						choiceCardIds.add(packageId);
					}
				}
				Map<Integer, Long> limitCountMap = packageVoList.stream().filter(obj -> choiceCardIds.contains(obj.getCardId())).
						collect(groupingBy(PatientPackageVo::getCouponId, counting()));
				//校验单个账单使用限制数
				errorBo = checkLimitCount(limitCountMap, SpecialPackageCoupon.class, SPECIAL_PACKAGE.getCode());
				if (errorBo.getError() != null) {
					return errorBo;
				}
			}
		}
		//校验代金券
		List<Integer> useVoucherIds = form.getVoucherIds();
		List<PatientVoucherVo> voucherVoList = benefitVo.getVoucherVoList();
		if (CollectionUtils.isNotEmpty(useVoucherIds)) {
			if (CollectionUtils.isEmpty(voucherVoList)) {
				errorBo.setError(DiscountError.PATIENT_VOUCHER_NULL);
				return errorBo;
			} else {
				List<Integer> choiceCardIds = Lists.newArrayList();
				//可作用在项目的兑换券卡券
				List<Integer> voucherIdsForItem = voucherVoList.stream().filter(obj -> TRUE.equals(obj.getItemUsable()))
						.map(PatientVoucherVo::getCardId).collect(toList());
				for (Integer voucherId : usePackageIds) {
					if (!voucherIdsForItem.contains(voucherId)) {
						errorBo.setError(DiscountError.PATIENT_NOT_OWN_VOUCHER);
						Card card = mapper.selectByPrimaryKey(voucherId);
						errorBo.setMsg(card == null ? null : card.getCardNumber());
						return errorBo;
					} else {
						choiceCardIds.add(voucherId);
					}
				}
				Map<Integer, Long> limitCountMap = voucherVoList.stream().filter(obj -> choiceCardIds.contains(obj.getCardId())).
						collect(groupingBy(PatientVoucherVo::getCouponId, counting()));
				//校验单个账单使用限制数
				errorBo = checkLimitCount(limitCountMap, VoucheCoupon.class, VOUCHER.getCode());
				if (errorBo.getError() != null) {
					return errorBo;
				}
			}
		}
		return errorBo;
	}

	/**
	 * 检查锁定患者选择的优惠信息
	 *
	 * @param form form
	 * @return RestErrorBo
	 */
	private RestErrorBo lockedBenefitByPatient(PatientChooseBenefitForm form) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		Set<String> addCardIds;
		Set<String> delCardIds;
		Integer patientId = form.getPatientId();
		Set<String> cardIds = assembleCardIds(form);
		if (CollectionUtils.isNotEmpty(cardIds)) {
			log.info("【开始锁定】开始锁定患者选择的优惠信息");
			Set<String> keys = redisUtils.keys(RedisConstants.LOCK_CHOICE_CARD + "*");
			if (CollectionUtils.isNotEmpty(keys)) {
				keys = keys.stream().map(key -> key.replace(RedisConstants.LOCK_CHOICE_CARD + ":", "")).collect(toSet());
				addCardIds = SetUtils.difference(cardIds, keys);
				delCardIds = SetUtils.difference(keys, cardIds);
				//需要删除的key
				if (CollectionUtils.isNotEmpty(delCardIds)) {
					for (String delCardId : delCardIds) {
						String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHOICE_CARD, String.valueOf(delCardId));
						String lockVal = String.valueOf(patientId);
						// 释放患者取消选择的卡券的锁
						redisUtils.unlock(lockKey, lockVal);
					}
				}
				//需要增加的key
				if (CollectionUtils.isNotEmpty(addCardIds)) {
					for (String addCardId : addCardIds) {
						errorBo = lockChoiceCard(patientId, addCardId);
						if (errorBo.getError() != null) {
							return errorBo;
						}
					}
				}
			} else {
				log.info("【开始锁定】开始锁定患者选择的优惠信息");
				for (String cardId : cardIds) {
					errorBo = lockChoiceCard(patientId, cardId);
					if (errorBo.getError() != null) {
						return errorBo;
					}
				}
			}
			log.info("【锁定成功】患者选择优惠成功");
		}
		return errorBo;
	}

	/**
	 * 组合优惠信息
	 *
	 * @param form 患者选择优惠信息
	 * @return set
	 */
	private Set<String> assembleCardIds(PatientChooseBenefitForm form) {
		Set<String> cardIds = Sets.newHashSet();
		Integer discountId = form.getDiscountId();
		if (discountId != null) {
			cardIds.add(String.valueOf(discountId));
		}
		List<Integer> exchangeIds = form.getExchangeIds();
		if (CollectionUtils.isNotEmpty(exchangeIds)) {
			exchangeIds.forEach(exchangeId -> cardIds.add(String.valueOf(exchangeId)));
		}
		List<Integer> packageIds = form.getPackageIds();
		if (CollectionUtils.isNotEmpty(packageIds)) {
			packageIds.forEach(packageId -> cardIds.add(String.valueOf(packageId)));
		}
		List<Integer> voucherIds = form.getVoucherIds();
		if (CollectionUtils.isNotEmpty(voucherIds)) {
			voucherIds.forEach(voucherId -> cardIds.add(String.valueOf(voucherId)));
		}
		return cardIds;
	}

	private RestErrorBo lockChoiceCard(Integer patientId, String cardId) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CHOICE_CARD, cardId);
		String lockVal = String.valueOf(patientId);
		// 锁定
		boolean locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
		if (!locked) {
			Card card = mapper.selectByPrimaryKey(cardId);
			String cardNumber = (card == null) ? null : card.getCardNumber();
			log.warn("【锁定失败】卡号是[{}]的卡券正在被使用，请取消使用该卡券！", cardNumber);
			errorBo.setError(DiscountError.CARD_HAS_CHOICE);
			errorBo.setMsg(cardNumber);
			return errorBo;
		}
		return errorBo;
	}

	/**
	 * 获取优惠券使用限制数
	 *
	 * @param couponId   优惠券id
	 * @param clazz      查询类
	 * @param couponType 优惠券类型
	 * @return int
	 */
	private int getCouponLimitCount(Integer couponId, Class<?> clazz, Integer couponType) {
		int limitCount = 0;
		Example example = new Example(clazz);
		example.createCriteria().andEqualTo("couponId", couponId);
		if (EXCHANGE.equals(couponType)) {
			PackageCoupon packageCoupon = packageMapper.selectOneByExample(example);
			if (packageCoupon != null) {
				limitCount = packageCoupon.getLimitCount();
			}
		}
		if (SPECIAL_PACKAGE.equals(couponType)) {
			SpecialPackageCoupon exchangeCoupon = specialPackageMapper.selectOneByExample(example);
			if (exchangeCoupon != null) {
				limitCount = exchangeCoupon.getLimitCount();
			}
		}
		if (VOUCHER.equals(couponType)) {
			VoucheCoupon voucherCoupon = voucherMapper.selectOneByExample(example);
			if (voucherCoupon != null) {
				limitCount = voucherCoupon.getLimitCount();
			}
		}
		return limitCount;
	}

	/**
	 * 校验单个账单使用限制数
	 *
	 * @param limitCountMap 优惠券和数量映射
	 * @param clazz         查询类
	 * @param couponType    优惠券类型
	 * @return 错误bo
	 */
	private RestErrorBo checkLimitCount(Map<Integer, Long> limitCountMap, Class<?> clazz, Integer couponType) {
		RestErrorBo errorBo = RestErrorBo.getInstance();
		for (Map.Entry<Integer, Long> entry : limitCountMap.entrySet()) {
			int limitCount = getCouponLimitCount(entry.getKey(), clazz, couponType);
			if (limitCount < entry.getValue()) {
				errorBo.setError(DiscountError.COUPON_BEYOND_LIMIT_COUNT);
				CouponCommonInfo couponCommonInfo = couponMapper.selectByPrimaryKey(entry.getKey());
				errorBo.setMsg(couponCommonInfo == null ? null : couponCommonInfo.getName());
				return errorBo;
			}
		}
		return errorBo;
	}

	public List<OrderItemUseBo> assignedItemVos(List<OrderDetail> orderDetail) {
		return orderDetail.stream().map(order -> {
			OrderItemUseBo bo = OrderItemUseBo.getInstance();
			bo.setOrderDetailId(order.getId());
			bo.setItemId(order.getBillingItemId());
			bo.setReceivableAmount(order.getReceivableAmount());
			bo.setQuantity(order.getQuantity());
			bo.setType(order.getType().intValue());
			return bo;
		}).collect(Collectors.toList());
	}

	/**
	 * 设置兑换券、套餐券、折扣券、代金券的优惠项目明细
	 *
	 * @param benefitUseBo 患者选择的优惠券集合
	 */
	private void assignedCouponItemDetail(BenefitUseBo benefitUseBo) {
		List<PatientUseBenefitBo> exchangeBenefitBos = benefitUseBo.getExchangeBenefitBos();
		List<PatientUseBenefitBo> packageBenefitBos = benefitUseBo.getPackageBenefitBos();
		PatientUseBenefitBo discountBenefitBo = benefitUseBo.getDiscountBenefitBos();
		List<PatientUseBenefitBo> voucherBenefitBos = benefitUseBo.getVoucherBenefitBos();
		//兑换券设置可使用项目
		exchangeBenefitBos.forEach(obj -> {
			List<CouponItemUseBo> couponItemUseBos = mapper.getCouponItemUseInfo(obj.getCouponId(), obj.getCardId(), null, EXCHANGE.getCode());
			obj.setExchangeCouponItemDetail(couponItemUseBos);
		});
		//套餐券设置可使用项目
		packageBenefitBos.forEach(obj -> {
			List<CouponItemUseBo> couponItemUseBos = mapper.getCouponItemUseInfo(obj.getCouponId(), obj.getCardId(), null, SPECIAL_PACKAGE.getCode());
			obj.setPackageCouponItemDetail(couponItemUseBos);
		});
		//代金折扣券基础，价目范围映射 <选择范围，项目id（分类/明细）>
		if (discountBenefitBo != null) {
			List<VoucherDiscountItem> discountItems = getCouponItemByCouponId(discountBenefitBo.getCouponId(), voucherDiscountItemMapper, VoucherDiscountItem.class);
			if (CollectionUtils.isNotEmpty(discountItems)) {
				assignedCouponItem(discountItems, discountBenefitBo);
			}
		}
		//代金券设置可使用项目
		voucherBenefitBos.forEach(voucherBo -> {
			//代金折扣券基础，价目范围映射 <选择范围，项目id（分类/明细）>
			List<VoucherDiscountItem> voucherItems = getCouponItemByCouponId(voucherBo.getCouponId(), voucherDiscountItemMapper, VoucherDiscountItem.class);
			assignedCouponItem(voucherItems, voucherBo);
		});
	}

	/**
	 * 设置折扣券或代金券的优惠项目明细
	 *
	 * @param discountItems         项目集合
	 * @param disCountVoucherItemBo 折扣券或代金券
	 */
	private void assignedCouponItem(List<VoucherDiscountItem> discountItems, PatientUseBenefitBo disCountVoucherItemBo) {
		Set<Integer> set = Sets.newHashSet();
		//价目全选
		Optional<VoucherDiscountItem> tariffOptional = discountItems.stream().filter(obj -> FALSE.equals(obj.getType())
				&& SELECT_ALL.equals(obj.getChoiceRangType().intValue())).findAny();
		if (tariffOptional.isPresent()) {
			disCountVoucherItemBo.setDiscountVoucherCouponItemDetail(null, FALSE.getCode(), SELECT_ALL.getCode());
		}
		//商品全选
		Optional<VoucherDiscountItem> shopOptional = discountItems.stream().filter(obj -> TRUE.equals(obj.getType())
				&& SELECT_ALL.equals(obj.getChoiceRangType().intValue())).findAny();

		if (shopOptional.isPresent()) {
			disCountVoucherItemBo.setDiscountVoucherCouponItemDetail(null, TRUE.getCode(), SELECT_ALL.getCode());
		}
		//选择价目分类
		List<Integer> tariffCategoryIds = discountItems.stream().filter(obj -> FALSE.equals(obj.getType())
				&& SELECT_ITEM_CATEGORY.equals(obj.getChoiceRangType().intValue())).map(VoucherDiscountItem::getItemId).collect(toList());
		//选择商品分类
		List<Integer> shopCategoryIds = discountItems.stream().filter(obj -> TRUE.equals(obj.getType())
				&& SELECT_ITEM_CATEGORY.equals(obj.getChoiceRangType().intValue())).map(VoucherDiscountItem::getItemId).collect(toList());
		for (Integer itemCategoryId : tariffCategoryIds) {
			//获取价目项目ids
			set.addAll(getTariffIds(itemCategoryId));
		}
		//设置折扣券的价目项目
		disCountVoucherItemBo.setDiscountVoucherCouponItemDetail(set, FALSE.getCode(), SELECT_ITEM_CATEGORY.getCode());
		set.clear();
		for (Integer itemCategoryId : shopCategoryIds) {
			//获取商品项目ids
			set.addAll(getShopTariffIds(itemCategoryId));
		}
		//设置折扣券的商品项目
		disCountVoucherItemBo.setDiscountVoucherCouponItemDetail(set, TRUE.getCode(), SELECT_ITEM_CATEGORY.getCode());

		//设置价目明细
		Set<Integer> tariffItemIds = discountItems.stream().filter(obj -> FALSE.equals(obj.getType())
				&& SELECT_ITEM_DETAIL.equals(obj.getChoiceRangType().intValue())).map(VoucherDiscountItem::getItemId).collect(toSet());
		disCountVoucherItemBo.setDiscountVoucherCouponItemDetail(tariffItemIds, FALSE.getCode(), SELECT_ITEM_DETAIL.getCode());
		//设置商品明细
		Set<Integer> shopItemIds = discountItems.stream().filter(obj -> TRUE.equals(obj.getType())
				&& SELECT_ITEM_DETAIL.equals(obj.getChoiceRangType().intValue())).map(VoucherDiscountItem::getItemId).collect(toSet());
		disCountVoucherItemBo.setDiscountVoucherCouponItemDetail(shopItemIds, TRUE.getCode(), SELECT_ITEM_DETAIL.getCode());
	}

	/**
	 * 查询订单项目id对应的优惠券信息
	 *
	 * @param orgId        orgId
	 * @param useBenefitBo bo
	 * @param order        订单
	 * @return PatientUseBenefitBo
	 */
	private ItemBenefitUseDetailBo findBenefitForOrderItem(Integer orgId, PatientUseBenefitBo useBenefitBo, OrderItemUseBo order) {
		List<Integer> usableClinic = useBenefitBo.getUsableClinic();
		//是否是可用门诊
		if (CollectionUtils.isNotEmpty(usableClinic) && usableClinic.contains(orgId)) {
			Integer couponType = useBenefitBo.getCouponType();
			List<ItemBenefitUseDetailBo> benefitUseDetail = useBenefitBo.getBenefitUseDetail();
			if (EXCHANGE.equals(couponType) || SPECIAL_PACKAGE.equals(couponType)) {
				//查询优惠券使用项目是否匹配订单某个项目明细，并且检查优惠券数量是否大于0
				Optional<ItemBenefitUseDetailBo> optional = benefitUseDetail.stream().filter(obj -> obj.getItemId().equals(order.getItemId())
						&& obj.getType().equals(order.getType()) && obj.getCount() > 0).findFirst();
				if (optional.isPresent()) {
					return optional.get();
				}
			}
			if (DISCOUNT.equals(couponType) || VOUCHER.equals(couponType)) {
				//查询优惠券使用项目是否匹配订单某个项目明细
				Optional<ItemBenefitUseDetailBo> optional = benefitUseDetail.stream().filter(obj -> (SELECT_ALL.equals(obj.getItemId())
						|| obj.getItemId().equals(order.getItemId())) && obj.getType().equals(order.getType())).findFirst();
				if (optional.isPresent()) {
					return optional.get();
				}
			}
		}
		return null;
	}

	/**
	 * 查找当前订单项目可用的优惠券
	 *
	 * @param useBenefitBos 患者选择的所有优惠券
	 * @param item          订单项目
	 */
	private List<PatientUseBenefitBo> getBenefitBosByItem(List<PatientUseBenefitBo> useBenefitBos, OrderItemUseBo item) {
		Integer type = item.getType();
		Integer itemId = item.getItemId();
		if (CollectionUtils.isNotEmpty(useBenefitBos)) {
			return useBenefitBos.stream().filter(obj -> {
				List<ItemBenefitUseDetailBo> detail = obj.getBenefitUseDetail();
				Optional<ItemBenefitUseDetailBo> optional = detail.stream().filter(itemDetail -> type.equals(itemDetail.getType())
						&& itemId.equals(itemDetail.getItemId())).findAny();
				if (optional.isPresent()) {
					return true;
				}
				Optional<ItemBenefitUseDetailBo> voucherOptional = detail.stream().filter(voucherItem -> (SELECT_ALL.equals(voucherItem.getItemId())
						|| voucherItem.getItemId().equals(itemId)) && voucherItem.getType().equals(type)).findFirst();
				return voucherOptional.isPresent();
			}).collect(toList());
		}
		return null;
	}

	/**
	 * 套餐券按售出套餐单价正序排序
	 *
	 * @param useBenefitBos 患者选择的所有优惠券
	 * @param item          订单项目
	 */
	private void sortPackageBySoldUnit(List<PatientUseBenefitBo> useBenefitBos, OrderItemUseBo item) {
		Integer type = item.getType();
		Integer itemId = item.getItemId();
		useBenefitBos.sort(Comparator.comparing(obj -> {
			List<ItemBenefitUseDetailBo> detail = obj.getBenefitUseDetail();
			Optional<ItemBenefitUseDetailBo> optional = detail.stream().filter(itemDetail -> type.equals(itemDetail.getType())
					&& itemId.equals(itemDetail.getItemId())).findAny();
			if (optional.isPresent()) {
				return optional.get().getPackageUnitPrice();
			}
			return BigDecimal.valueOf(Double.MAX_VALUE);
		}));
	}

	/**
	 * 获取患者选择优惠券的可用门诊
	 *
	 * @param couponIds couponIds
	 * @return map
	 */
	private Map<Integer, UseClinicBo> getCouponUseClinic(List<Integer> couponIds) {
		if (CollectionUtils.isNotEmpty(couponIds)) {
			List<UseClinicBo> useClinicIdStr = mapper.getUseClinicIdStr(couponIds);
			return useClinicIdStr.stream().collect(toMap(UseClinicBo::getCouponId,
					Function.identity(), (v1, v2) -> v2));
		}
		return Collections.emptyMap();
	}

	public void assignedBenefitBos(BenefitUseBo benefitUseBo, PatientOptionalBenefitVo benefitVo, PatientChooseBenefitForm form) {
		List<PatientExchangeVo> exchangeVoList = benefitVo.getExchangeVoList();
		List<Integer> exchangeIds = form.getExchangeIds();
		//设置兑换券
		if (CollectionUtils.isNotEmpty(exchangeIds) && CollectionUtils.isNotEmpty(exchangeVoList)) {
			Map<Integer, PatientExchangeVo> exchangeVoMap = exchangeVoList.stream().collect(toMap(PatientExchangeVo::getCardId, Function.identity()));
			exchangeVoList = exchangeIds.stream().filter(cardId -> exchangeVoMap.get(cardId) != null).
					map(exchangeVoMap::get).collect(toList());
			//优惠券vo 生成 对应bo对象
			List<PatientUseBenefitBo> exchangeUseBos = exchangeVoList.stream().map(obj -> {
				PatientUseBenefitBo exchangeBenefitBo = BeanCopierUtils.generalCopyBean(obj, PatientUseBenefitBo.class);
				exchangeBenefitBo.setCouponType(EXCHANGE.getCode());
				exchangeBenefitBo.setLimitCount(getCouponLimitCount(obj.getCouponId(), PackageCoupon.class, EXCHANGE.getCode()));
				return exchangeBenefitBo;
			}).collect(toList());
			//设置可用门诊
			assignedUseClinicForBo(exchangeUseBos);
			benefitUseBo.setExchangeBenefitBos(exchangeUseBos);
		}

		List<PatientPackageVo> packageVoList = benefitVo.getPackageVoList();
		List<Integer> packageIds = form.getPackageIds();
		//设置套餐券
		if (CollectionUtils.isNotEmpty(packageIds) && CollectionUtils.isNotEmpty(packageVoList)) {
			Map<Integer, PatientPackageVo> packageVoMap = packageVoList.stream().collect(toMap(PatientPackageVo::getCardId, Function.identity()));
			List<PatientUseBenefitBo> packageUseBos = packageIds.stream().filter(cardId -> packageVoMap.get(cardId) != null)
					.map(cardId -> {
						PatientPackageVo packageVo = packageVoMap.get(cardId);
						PatientUseBenefitBo packageBenefitBo = BeanCopierUtils.generalCopyBean(packageVo, PatientUseBenefitBo.class);
						packageBenefitBo.setCouponType(SPECIAL_PACKAGE.getCode());
						packageBenefitBo.setLimitCount(getCouponLimitCount(packageVo.getCouponId(), SpecialPackageCoupon.class, SPECIAL_PACKAGE.getCode()));
						return packageBenefitBo;
					}).collect(toList());
			//设置可用门诊
			assignedUseClinicForBo(packageUseBos);
			benefitUseBo.setPackageBenefitBos(packageUseBos);
		}

		List<PatientDiscountVo> discountVoList = benefitVo.getDiscountVoList();
		Integer discountId = form.getDiscountId();
		//设置折扣券
		if (discountId != null && CollectionUtils.isNotEmpty(discountVoList)) {
			List<PatientUseBenefitBo> discountUseBos = discountVoList.stream().filter(obj -> discountId.equals(obj.getCardId()))
					.map(obj -> {
						PatientUseBenefitBo discountBenefitBo = BeanCopierUtils.generalCopyBean(obj, PatientUseBenefitBo.class);
						discountBenefitBo.setCouponType(DISCOUNT.getCode());
						discountBenefitBo.setLimitCount(1);
						DiscountCoupon discountCoupon = new DiscountCoupon();
						discountCoupon.setCouponId(obj.getCouponId());
						discountBenefitBo.setDiscountRate(discountMapper.selectOne(discountCoupon).getDiscountRate());
						return discountBenefitBo;
					}).collect(toList());
			//设置可用门诊
			assignedUseClinicForBo(discountUseBos);
			benefitUseBo.setDiscountBenefitBos(discountUseBos.get(0));
		}

		List<PatientMemberCardVo> memberCardVoList = benefitVo.getMemberCardVoList();
		Integer memberCardId = form.getMemberCardId();
		//设置会员卡
		if (memberCardId != null && CollectionUtils.isNotEmpty(memberCardVoList)) {
			List<PatientUseBenefitBo> memberUseBos = memberCardVoList.stream().filter(obj -> memberCardId.equals(obj.getMemberCardId()))
					.map(obj -> {
						PatientUseBenefitBo memberCard = BeanCopierUtils.generalCopyBean(obj, PatientUseBenefitBo.class);
						memberCard.setDiscountRate(obj.getMemberCardRate());
						memberCard.setCouponType(MEMBER_CARD.getCode());
						memberCard.setMixable(TRUE.getCode());
						return memberCard;
					}).collect(toList());
			//设置可用门诊
			assignedUseClinicForBo(memberUseBos);
			benefitUseBo.setMemberBenefitBo(memberUseBos.get(0));
		}

		List<PatientVoucherVo> voucherVoList = benefitVo.getVoucherVoList();
		List<Integer> voucherIds = form.getVoucherIds();
		//设置代金券
		if (CollectionUtils.isNotEmpty(voucherIds) && CollectionUtils.isNotEmpty(voucherVoList)) {
			Map<Integer, PatientVoucherVo> voucherVoMap = voucherVoList.stream().collect(toMap(PatientVoucherVo::getCardId, Function.identity()));
			List<PatientUseBenefitBo> voucherUseBos = voucherIds.stream().filter(cardId -> voucherVoMap.get(cardId) != null)
					.map(cardId -> {
						PatientVoucherVo voucherVo = voucherVoMap.get(cardId);
						PatientUseBenefitBo voucherBenefitBo = BeanCopierUtils.generalCopyBean(voucherVo, PatientUseBenefitBo.class);
						voucherBenefitBo.setCouponType(VOUCHER.getCode());
						voucherBenefitBo.setLimitCount(getCouponLimitCount(voucherVo.getCouponId(), VoucheCoupon.class, VOUCHER.getCode()));
						VoucheCoupon voucheCoupon = new VoucheCoupon();
						voucheCoupon.setCouponId(voucherVo.getCouponId());
						voucherBenefitBo.setFace(voucherMapper.selectOne(voucheCoupon).getFaceValue());
						return voucherBenefitBo;
					}).collect(toList());
			//设置可用门诊
			assignedUseClinicForBo(voucherUseBos);
			benefitUseBo.setVoucherBenefitBos(voucherUseBos);
		}
	}

	/**
	 * 设置患者选择优惠券的可用门诊信息
	 *
	 * @param benefitUseBo benefitUseBo
	 */
	private void assignedUseClinicForBo(List<PatientUseBenefitBo> benefitUseBo) {
		//查询优惠券的可用门诊
		Map<Integer, UseClinicBo> useClinicMap = getCouponUseClinic(benefitUseBo.stream().
				map(PatientUseBenefitBo::getCouponId).collect(toList()));
		if (!org.springframework.util.CollectionUtils.isEmpty(useClinicMap)) {
			//设置优惠券可用门诊
			benefitUseBo.stream().filter(obj -> useClinicMap.get(obj.getCouponId()) != null).forEach(obj -> {
				String useClinicIdStr = useClinicMap.get(obj.getCouponId()).getUseClinicIdStr();
				if (StringUtils.isNotBlank(useClinicIdStr)) {
					obj.setUsableClinic(Lists.newArrayList(Splitter.on(",").split(useClinicIdStr)).stream().map(Integer::valueOf)
							.collect(toList()));
				}
			});
		}
	}

	private MessageModel buildMessage(Integer id, LocalDateTime submitDate) {
		MessageModel messageModel = new MessageModel();
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);
		map.put("submitDate", submitDate);
		messageModel.setParamMap(map);
		messageModel.setMsgCategoryEnum(BaseCardBatch);
		messageModel.setOperateType(ADD);
		return messageModel;
	}
}
