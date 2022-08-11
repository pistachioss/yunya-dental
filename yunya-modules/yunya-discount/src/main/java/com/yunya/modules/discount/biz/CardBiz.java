package com.yunya.modules.discount.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.yunya.feign.discount.domain.bo.*;
import com.yunya.feign.discount.domain.form.*;
import com.yunya.feign.discount.domain.model.ClinicAllocateModel;
import com.yunya.feign.discount.domain.model.GenerateAllocateModel;
import com.yunya.feign.discount.domain.query.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.ivy_mini.RemoteIvyMiniServiceFeign;
import com.yunya.feign.ivy_mini.domain.form.VirtualActiveForm;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.feign.sms.model.SmsAutoEventSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.SmsAutosendEventEnum;
import com.yunya.framework.common.enums.SmsTemplateItemEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.model.RestError;
import com.yunya.framework.common.utils.*;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.discount.*;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.AccountItem;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.discount.enums.*;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.*;
import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.modules.discount.enums.BenefitTypeEnum.*;
import static com.yunya.modules.discount.enums.CardQrCodeEnum.*;
import static com.yunya.modules.discount.enums.CardStatusEnum.*;
import static com.yunya.modules.discount.enums.CouponTypeEnum.EXCHANGE;
import static com.yunya.modules.discount.enums.CouponTypeEnum.*;
import static com.yunya.modules.discount.enums.RangTypeEnum.*;
import static com.yunya.modules.discount.enums.SoldTypeEnum.*;
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

    //优惠券是否共用
    private static final ThreadLocal<Map<Integer, List<Integer>>> mixUsedThreadLocal = ThreadLocal.withInitial(Maps::newHashMap);
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;
    @Resource
    private CouponCommonInfoMapper couponMapper;
    @Resource
    private CouponAllocateMapper allocateMapper;
    @Resource
    private VoucheCouponMapper voucherMapper;
    @Resource
    private DiscountCouponMapper discountCouponMapper;
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
    private CardCancelLogMapper cardCancelLogMapper;
    @Resource
    private CardActivedSmsBiz cardActivedSmsBiz;
    @Resource
    private RedisUtils redisUtils;
    @Resource(name = "customizeThreadPool")
    private ExecutorService cardThreadPool;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private CardBenefitMapper cardBenefitMapper;
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Resource
    private RemoteIvyMiniServiceFeign ivyMiniServiceFeign;
    @Value("${cardSold.selfChannel}")
    private String selfChannel;
    /**
     * 卡券二维码前缀
     */
    @Value("${codeUrl.url}")
    private String serverPort;

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

    public List<CardWxVO> findCardWxList(Integer patientId) {
        return mapper.findCardWxList(patientId);
    }

    public List<CardWxDetailVO> findCardWxDetail(Integer couponId) {
        return mapper.findCardWxDetail(couponId);
    }

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
        return allocateList.stream().map(obj -> {
            GenerateAllocateDetailVo vo = new GenerateAllocateDetailVo();
            OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(obj.getOrgId());
            vo.setOrgId(obj.getOrgId());
            vo.setAllocateNum(obj.getAllocateNum());
            vo.setCouponAllocateId(obj.getId());
            vo.setOrgName(orgInfo == null ? null : orgInfo.getName());
            return vo;
        }).collect(toList());
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
        int sumGenerateNum = allocateMapper.countCouponAllocate(couponId);
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
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        //查询优惠券售卖信息
        mapper.listSaleInfoByParam(query.getCouponTypeList(), query.getCouponName(), orgId);
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
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        couponCommonInfo.setId(query.getCouponId());
        couponCommonInfo = couponMapper.selectOne(couponCommonInfo);
        Page<Card> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        mapper.listCardInfosByParam(query.getCardNumber(), query.getSoldTypeList(), query.getCardStatsList(), query.getPhoneNumber(),
                query.getCouponId(), query.getOrgId());

        //支付方式
        List<AccountItem> aiList = systemServiceFeign.findAccountItemList(new AccountItem());
        Map<String, AccountItem> AccMap = new HashMap(16);
        aiList.forEach(z -> AccMap.put(z.getId() + "", z));
        //门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

        //实体转换为pageVo
        List<CardSalePageVo> list = page.getResult().stream().map(this::cardConvertPageVo).collect(toList());
        for (CardSalePageVo cardSalePageVo : list) {
            cardSalePageVo.setCardName(couponCommonInfo.getName());
            cardSalePageVo.setSoldAmount(couponCommonInfo.getSoldAmount());
            cardSalePageVo.setOrgName(clinicMap.get(query.getOrgId() + "").getName());
            cardSalePageVo.setOrgAddress(clinicMap.get(query.getOrgId() + "").getAddress());
            cardSalePageVo.setOrgIphone(clinicMap.get(query.getOrgId() + "").getTel());
            if (cardSalePageVo.getPayId() != null) {
                cardSalePageVo.setSoldType(AccMap.get(cardSalePageVo.getPayId() + "").getName());
            }
            cardSalePageVo.setCouponType(couponCommonInfo.getType().intValue());
        }
        PageInfo<CardSalePageVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
    }

    /**
     * 卡券售出
     *
     * @param form form
     * @return res
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult soldCard(CardSoldForm form) throws ExecutionException, InterruptedException {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        return soldCard(orgId, form, loginUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult miniSoldCard(MiniCardSoldForm form){
        return soldCard(form.getOrgId(), form.getForm(), form.getLoginUserId());
    }

    private ResponseResult soldCard(Integer orgId, CardSoldForm form, Integer loginUserId) {
        List<Integer> cardIds = form.getCardIds();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        long start = System.currentTimeMillis();
        RestErrorBo errorBo;
        try {
            SalesChannel salesChannel = new SalesChannel();
            salesChannel.setName(selfChannel);
            salesChannel = salesChannelMapper.selectOne(salesChannel);
            if (salesChannel == null) {
                throw new ClientServiceException("请先设置销售渠道：".concat(selfChannel), OPERATION_NOT_ALLOW);
            }
            //获取组织名
            String orgName = getOrgName(orgId);
            StringBuilder cardNos = new StringBuilder();
            StringBuilder cardSecrets = new StringBuilder();
            //校验优惠券信息
            Card cardCheck = mapper.selectByPrimaryKey(cardIds.get(0));
            Integer couponId = cardCheck.getCouponId();
            CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
            errorBo = checkCouponForSale(couponId, couponInfo);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //检查优惠券分配
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
            // 并行检查卡券
            CompletableFuture<List<Card>> queryCf = this.parallelQuery(cardIds, orgId, orgName);
            // 并行售卖卡券
            SalesChannel finalSalesChannel = salesChannel;
            queryCf.thenComposeAsync(cf -> this.parallelSoldCard(cardIds, form, loginUserId, finalSalesChannel.getId(), requestAttributes), cardThreadPool)
                    .thenAcceptAsync(cardList -> {
                        if (form.getSendText() == 1) {
                            for (Card card : cardList) {
                                if (cardNos.length() > 0) {
                                    cardNos.append(",");
                                }
                                cardNos.append(card.getCardNumber());
                                if (cardSecrets.length() > 0) {
                                    cardSecrets.append(",");
                                }
                                cardSecrets.append(new String(Base64.getDecoder().decode(card.getCardPassword())));
                            }
                            sendMessage(form, cardNos, cardSecrets);
                        }
                    }, cardThreadPool).handle((res, ex) -> {
                        if (ex != null) {
                            throw (CompletionException)ex;
                        }
                        return res;
                    });
            return ResponseUtil.success();
        } finally {
            CompletableFuture.runAsync(() -> {
                manualUnLock(loginUserId, RedisConstants.LOCK_CARD_SOLD);
                log.info("【卡券售卖】解锁成功");
                log.info("全部时长：{}", (System.currentTimeMillis() - start) / 1000);
            }, cardThreadPool);
        }
    }

    private CompletableFuture<List<Card>> parallelQuery(List<Integer> cardIds, int orgId, String orgName) {
        CompletableFuture<List<Card>> cf = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(Card.class);
            example.createCriteria().andIn("id", cardIds);
            return mapper.selectByExample(example);
        }).thenApplyAsync(cardList -> {
            cardList.forEach(card -> {
                RestErrorBo errorBo = checkCardForSale(card.getId(), card, orgId, orgName);
                if (errorBo.getError() != null) {
                    throw new ClientServiceException(errorBo.getError(), errorBo.getMsg());
                }
            });
            return cardList;
        }, cardThreadPool);
        return cf;
    }

    private CompletableFuture<List<Card>> parallelSoldCard(List<Integer> cardIds, CardSoldForm form, Integer loginUserId, Integer channelId,
                                                           RequestAttributes requestAttributes) {
        LocalDateTime now = LocalDateTime.now();
        List<CompletableFuture<Card>> queryCardFutures = Lists.newArrayListWithCapacity(cardIds.size());
        for (Integer cardId : cardIds) {
            queryCardFutures.add(CompletableFuture.supplyAsync(() -> {
                return this.updateCardForSold(cardId, form, loginUserId, now, channelId);
            }, cardThreadPool));
        }
        CompletableFuture<Void> allFuture = CompletableFuture.allOf(queryCardFutures.toArray(new CompletableFuture[0]));
        //阻塞构建更新卡券对象集合
        return allFuture.thenApplyAsync(cf ->
                        queryCardFutures.stream().map(CompletableFuture::join).collect(toList())
                    , cardThreadPool)
                .thenApplyAsync(cardList -> {
                        //更新卡券信息
                        List<List<Card>> partition = Lists.partition(cardList, 100);
                        partition.forEach(list1 -> CompletableFuture.runAsync(() -> mapper.updateList(list1), cardThreadPool));
                        return cardList;
                    }, cardThreadPool)
                .thenApplyAsync(cardList -> {
                    cardList.forEach(card -> {
                        RequestContextHolder.setRequestAttributes(requestAttributes);
                        mqServiceFeign.sendMessage(card.getId(), UPDATE, BaseCardSingle);
                        log.info("【售卖卡券发送消息成功】：卡券id[{}]", card.getId());
                    });
                    return cardList;
                });
    }

    /**
     * 发送短信
     *
     * @param form
     * @param cardNos
     * @param cardSecrets
     */
    private void sendMessage(CardSoldForm form, StringBuilder cardNos, StringBuilder cardSecrets) {
        if (form.getSendText() == 0) {
            return;
        }
        JSONObject templateParam = new JSONObject();
        //产品类型
        templateParam.put(SmsTemplateItemEnum.PRODUCT_MODEL.getAction(), CouponTypeEnum.getValue(form.getCouponType()));
        //产品名称
        templateParam.put(SmsTemplateItemEnum.PRODUCT_NAME.getAction(), form.getCouponName());
        //卡券卡号
        templateParam.put(SmsTemplateItemEnum.COUPON_CARD_NUMBER.getAction(), cardNos.toString());
        //卡券卡密
        templateParam.put(SmsTemplateItemEnum.COUPON_CARD_SECRET.getAction(), cardSecrets.toString());
        SmsAutoEventSendRecordModel smsModel = new SmsAutoEventSendRecordModel();
        SmsCommonSendRecordModel model = new SmsCommonSendRecordModel();
        model.setMobile(form.getSoldPhoneNumber());
        model.setSendObject(form.getSoldTarget());
        model.setTemplateParam(templateParam);
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        smsModel.setEventCode(SmsAutosendEventEnum.COUPON_SOLD.getCode());
        smsModel.setModels(Collections.singletonList(model));
        smsModel.setUserId(Integer.parseInt(BaseContextHandler.getUserID()));
        smsModel.setOrgId(orgId);
        smsModel.setName(BaseContextHandler.getName());
        redisUtils.lPush(RedisConstants.SMS_SEND_MESSAGE_QUEUE + orgId, smsModel);
    }

    /**
     * 校验卡券二维码信息
     *
     * @param cardId 卡券二维码信息
     * @return vo
     */
    public CardQrCodeVo cardQrCodeCheck(Integer cardId) {
        CardQrCodeVo vo = new CardQrCodeVo();
        vo.setCardQrCodeType(QR_CODE_NORMAL.getCode());
        Card card = mapper.selectByPrimaryKey(cardId);
        if (card == null) {
            vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
            vo.setErrorMsg("卡券不存在");
            return vo;
        }
        //已失效
        if (SALE_PENDING.equals(card.getStatus())) {
            vo.setCardQrCodeType(QR_CODE_INVALID.getCode());
            return vo;
        }
        //已核销
        if (ACTIVATED.equals(card.getStatus()) || PARTIAL_USE.equals(card.getStatus()) || USE_ALL.equals(card.getStatus())) {
            vo.setCardQrCodeType(QR_CODE_DESTROY.getCode());
            return vo;
        }
        CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(card.getCouponId());
        if (coupon == null) {
            log.warn("优惠券不存在或已停用");
            vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
            vo.setErrorMsg("优惠券不存在或已停用");
            return vo;
        }
        int couponType = coupon.getType().intValue();
        //查询优惠券过期信息
        vo = checkCouponDeadline(card.getCouponId(), couponType);
        if (QR_CODE_NORMAL.equals(vo.getCardQrCodeType())) {
            vo.setCouponName(coupon.getName());
            vo.setQrCode(Base64.getEncoder().encodeToString(Joiner.on(":").join(new BCryptPasswordEncoder(UserConstant.PW_ENCODER_SALT)
                    .encode(Joiner.on(":").join(card.getCardNumber(), card.getCardPassword())), card.getId())
                    .getBytes()));
        }
        return vo;
    }

    public List<CardQrCodeVo> batchCardQrCode(List<Integer> cardIds) {
        List<CardQrCodeVo> list = Lists.newArrayListWithCapacity(cardIds.size());
        Example example = new Example(Card.class);
        example.createCriteria().andIn("id", cardIds);
        List<Card> cards = mapper.selectByExample(example);
        Map<Integer, Card> cardMap = cards.stream().collect(toMap(Card::getId, Function.identity(), (o,n) -> n));
        //优惠券信息
        Set<Integer> couponIds = cards.stream().map(Card::getCouponId).collect(toSet());
        Example example1 = new Example(CouponCommonInfo.class);
        example1.createCriteria().andIn("id", couponIds);
        List<CouponCommonInfo> coupons = couponMapper.selectByExample(example1);
        Map<Integer, CouponCommonInfo> couponMap = coupons.stream()
                .collect(toMap(CouponCommonInfo::getId, Function.identity(), (o,n) -> n));
        Map<Integer, Integer> map = queryQrCodeLimit(coupons);
        for (Integer cardId : cardIds) {
            Card card = cardMap.get(cardId);
            CardQrCodeVo vo = new CardQrCodeVo();
            vo.setCardQrCodeType(QR_CODE_NORMAL.getCode());
            if (card == null) {
                vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
                vo.setErrorMsg("卡券不存在");
                list.add(vo);
                continue;
            }
            //已失效
            if (SALE_PENDING.equals(card.getStatus())) {
                vo.setCardQrCodeType(QR_CODE_INVALID.getCode());
                list.add(vo);
                continue;
            }
            //已核销
            if (ACTIVATED.equals(card.getStatus()) || PARTIAL_USE.equals(card.getStatus()) || USE_ALL.equals(card.getStatus())) {
                vo.setCardQrCodeType(QR_CODE_DESTROY.getCode());
                list.add(vo);
                continue;
            }
            CouponCommonInfo coupon = couponMap.get(card.getCouponId());
            if (coupon == null) {
                log.warn("【批量】优惠券不存在或已停用");
                vo.setCardQrCodeType(QR_CODE_OTHER.getCode());
                vo.setErrorMsg("【批量】优惠券不存在或已停用");
                list.add(vo);
                continue;
            }
            int couponType = coupon.getType().intValue();
            //查询优惠券过期信息
            vo = checkCouponDeadline(card.getCouponId(), couponType);
            if (QR_CODE_NORMAL.equals(vo.getCardQrCodeType())) {
                vo.setCouponName(coupon.getName());
                vo.setQrCode(Base64.getEncoder().encodeToString(Joiner.on(":").join(new BCryptPasswordEncoder(UserConstant.PW_ENCODER_SALT)
                                .encode(Joiner.on(":").join(card.getCardNumber(), card.getCardPassword())), card.getId())
                        .getBytes()));
            }
            Integer limitCount = map.get(coupon.getId());
            vo.setLimitCount(limitCount);
            list.add(vo);
        }
        return list;
    }

    private Map<Integer, Integer> queryQrCodeLimit(List<CouponCommonInfo> coupons) {
        Map<Integer, Integer> map1 = Maps.newHashMapWithExpectedSize(coupons.size());
        Map<Byte, Set<Integer>> map = coupons.stream()
                .collect(groupingBy(CouponCommonInfo::getType, mapping(CouponCommonInfo::getId, toSet())));
        Set<Integer> voucher = map.get(0);
        Set<Integer> _package = map.get(2);
        Set<Integer> special = map.get(3);
        Example voucherExample = new Example(VoucheCoupon.class);
        voucherExample.createCriteria().andIn("couponId", voucher);
        List<VoucheCoupon> voucherCoupon = voucherMapper.selectByExample(voucherExample);

        Example _packageExample = new Example(PackageCoupon.class);
        _packageExample.createCriteria().andEqualTo("couponId", _package);
        List<PackageCoupon>  packageCoupon = packageMapper.selectByExample(_packageExample);

        Example specialExample = new Example(SpecialPackageCoupon.class);
        specialExample.createCriteria().andEqualTo("couponId", special);
        List<SpecialPackageCoupon> specialPackageCoupon = specialPackageMapper.selectByExample(specialExample);
        voucherCoupon.forEach(t -> map1.put(t.getCouponId(), t.getLimitCount()));
        packageCoupon.forEach(t -> map1.put(t.getCouponId(), t.getLimitCount()));
        packageCoupon.forEach(t -> map1.put(t.getCouponId(), t.getLimitCount()));
        return map1;
    }

    /**
     * 卡券取消售出
     *
     * @param cardId cardId
     * @return res
     */
    @Transactional
    public ResponseResult cancelCardSold(Integer cardId) {
        int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        RestErrorBo errorBo;
        //1. 检查卡券
        Card card = mapper.selectByPrimaryKey(cardId);
        errorBo = checkCardForCancelSale(cardId, card, orgId);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
        }
        Integer couponId = card.getCouponId();
        //2. 检查优惠券
        CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
        if (couponInfo == null || !couponInfo.getIsInservice()) {
            log.warn("【售卖失败】优惠券[{}]不存在", couponId);
            return ResponseUtil.error(DiscountError.COUPON_NOT_EXIST);
        }
        //更新取消卡券售出
        updateCardForCancel(card);
        //取消售出增加日志记录
        this.saveCancelCardLog(cardId);
        mqServiceFeign.sendMessage(cardId, UPDATE, BaseCardSingle);
        log.info("【取消售出卡券发送消息成功】：卡券id[{}]", couponId);
        return ResponseUtil.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchCancelCardSold(BatchCancelCardForm form) {
        try {
            BaseContextHandler.setUserID(String.valueOf(form.getLoginUserId()));
            int orgId = form.getOrgId();
            List<Integer> cardIds = form.getCardIds();
            RestErrorBo errorBo;
            Example example = new Example(Card.class);
            example.createCriteria().andIn("id", cardIds);
            List<Card> cards = mapper.selectByExample(example);
            Map<Integer, Card> cardMap = cards.stream().collect(toMap(Card::getId, Function.identity(), (o,n) -> n));
            //优惠券信息
            Set<Integer> couponIds = cards.stream().map(Card::getCouponId).collect(toSet());
            Example example1 = new Example(CouponCommonInfo.class);
            example1.createCriteria().andIn("id", couponIds);
            List<CouponCommonInfo> coupons = couponMapper.selectByExample(example1);
            Map<Integer, CouponCommonInfo> couponMap = coupons.stream()
                    .collect(toMap(CouponCommonInfo::getId, Function.identity(), (o,n) -> n));
            for (Integer cardId : cardIds) {
                //1. 检查卡券
                Card card = cardMap.get(cardId);
                errorBo = checkCardForCancelSale(cardId, card, orgId);
                Integer couponId = card.getCouponId();
                //2. 检查优惠券
                CouponCommonInfo couponInfo = couponMap.get(card.getCouponId());
                if (couponInfo == null || !couponInfo.getIsInservice()) {
                    log.warn("【批量售卖失败】优惠券[{}]不存在", couponId);
                }
                //更新取消卡券售出
                updateCardForCancel(card);
                //取消售出增加日志记录
                this.saveCancelCardLog(cardId);
                mqServiceFeign.sendMessage(cardId, UPDATE, BaseCardSingle);
                log.info("【批量取消售出卡券发送消息成功】：卡券id[{}]", couponId);
            }
        } finally {
            BaseContextHandler.remove();
        }
    }

    /**
     * 查询代金、充值、套餐、兑换卡券详情
     *
     * @param query query
     * @return res
     */
    public ResponseResult<CardActiveDetailVo> getCardDetailByManual(CardActiveQuery query) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        //校验卡号卡密
        errorBo = checkCardActiveInfo(query);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError());
        }
        String cardPass = (String) errorBo.getMsg()[0];
        return ResponseUtil.success(mapper.findByCardNumAndPass(query.getCardNumber(), cardPass));
    }

    /**
     * 查询充值卡详情
     *
     * @param query query
     * @return res
     */
    public ResponseResult<CardActiveDetailVo> getRechargeDetailByManual(CardActiveQuery query) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        errorBo = checkRechargeActiveInfo(query);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError());
        }
        String cardPass = (String) errorBo.getMsg()[0];
        return ResponseUtil.success(mapper.findByCardNumAndPass(query.getCardNumber(), cardPass));
    }

    public ResponseResult<CardActiveDetailVo> getCardDetailByMachine(String qrCode) {
        Card card = null;
        try {
            String qrCodeData = new String(Base64.getDecoder().decode(qrCode.trim()));
            List<String> data = Lists.newArrayList(Splitter.on(":").trimResults().omitEmptyStrings().split(qrCodeData));
            card = mapper.selectByPrimaryKey(Integer.valueOf(data.get(1)));
        } catch (Exception e) {
            throw new ClientServiceException("编码格式有误", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        //校验卡券
        RestErrorBo errorBo = checkCardInfo(card);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError());
        }
        return ResponseUtil.success(mapper.findByCardNumAndPass(card.getCardNumber(), card.getCardPassword()));
    }

    public ResponseResult<CardActiveDetailVo> getRechargeDetailByMachine(String qrCode) {
        Card card = null;
        try {
            String qrCodeData = new String(Base64.getDecoder().decode(qrCode.trim()));
            List<String> data = Lists.newArrayList(Splitter.on(":").trimResults().omitEmptyStrings().split(qrCodeData));
            card = mapper.selectByPrimaryKey(Integer.valueOf(data.get(1)));
        } catch (Exception e) {
            throw new ClientServiceException("编码格式有误", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
        }
        //校验充值卡券
        RestErrorBo errorBo = checkRechargeCardInfo(card);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError());
        }
        return ResponseUtil.success(mapper.findByCardNumAndPass(card.getCardNumber(), card.getCardPassword()));
    }

    /**
     * 自有平台激活
     *
     * @param patientId patientId
     * @param form      form
     * @return res
     */
    @Transactional
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
            //3. 检查卡券
            Card card = mapper.selectByPrimaryKey(cardId);
            errorBo = checkCardForOwnActive(form.getPayId(), card);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
            }
            //2. 检查优惠券
            errorBo = checkCouponForActive(card.getCouponId(), DiscountError.CARD_BEYOND_DEADLINE);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //4. 卡券激活
            card = updateOwnActiveCard(patientId, form, loginUserId, card.getCouponId(), card);
            mqServiceFeign.sendMessage(cardId, UPDATE, BaseCardSingle);
            log.info("【自有平台激活卡券发送消息成功】：卡券id[{}]", cardId);
            cardActivedSendSms(card);
            return ResponseUtil.success();
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    /** 套餐券 */
    private static final Byte PACKAGE_VOUCHER = 3;
    /** 365卡系列*/
    private static final Byte PROD_TYPE_365 = 14;
    /** 艾芽卡系列*/
    private static final Byte PROD_TYPE_IVY = 17;
    /**
     * 艾芽卡、365卡创建定时任务，每三个月发一次短信通知客户
     *
     * @param card
     */
    private void cardActivedSendSms(Card card) {
        Integer couponId = card.getCouponId();
        Example example = new Example(CouponCommonInfo.class);
        Example.Criteria c = example.createCriteria();
        c.andIn("id", Collections.singleton(couponId));
        c.andEqualTo("type", SPECIAL_PACKAGE.getCode());
        c.andIn("productTypeId", Arrays.asList(PROD_TYPE_365, PROD_TYPE_IVY));
        List<CouponCommonInfo> coupons = couponMapper.selectByExample(example);
        if (StringHelper.isNotEmpty(coupons)) {
            cardActivedSmsBiz.sendAndrecordSms(card, coupons.get(0));
        }
    }

    /**
     * 第三方平台激活卡券
     *
     * @param patientId patientId
     * @param form      form
     * @return res
     */
    @Transactional
    public ResponseResult otherActiveCard(Integer patientId, OtherCardActiveForm form) {
        boolean locked = false;
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String cardNumber = form.getThirdCardNumber();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CARD_ACTIVE, form.getCouponId(), form.getSaleChannelId(), cardNumber);
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
            //1. 检查卡券
            errorBo = checkCardForOtherActive(form);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //2. 检查优惠券
            errorBo = checkCouponForActive(form.getCouponId(), DiscountError.CARD_BEYOND_DEADLINE);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //3.校验产品是否可以共享
            errorBo = this.checkCouponShare(form.getCouponId(), form.getSharerIdStr());
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError());
            }
            //4. 第三方平台卡券激活
            Card activeCard = insertOtherActiveCard(patientId, form, loginUserId);
            mqServiceFeign.sendMessage(activeCard.getId(), ADD, BaseCardSingle);
            log.info("【第三方激活发送消息成功】：卡券id[{}]", activeCard.getId());
            cardActivedSendSms(activeCard);
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
    @Transactional
    public ResponseResult configSharer(Integer patientId, Integer cardId, ConfigSharerForm form) {
        //1. 校验卡券
        Card card = mapper.selectByPrimaryKey(cardId);
        if (card == null || !patientId.equals(card.getPatientId())) {
            log.warn("卡券[{}]不存在", cardId);
            return ResponseUtil.error(DiscountError.CARD_NOT_EXIST);
        }
        if (SALE_PENDING.equals(card.getStatus()) || ACTIVE_PENDING.equals(card.getStatus())) {
            log.warn("卡券[{}]未激活", cardId);
            return ResponseUtil.error(DiscountError.CARD_NOT_ACTIVATED);
        }
        if (USE_ALL.equals(card.getStatus())) {
            log.warn("卡券[{}]已全部使用", cardId);
            return ResponseUtil.error(DiscountError.CARD_ALL_USED);
        }
        if (StringUtils.isNotBlank(form.getSharerIdStr())) {
            //配置共享人不能是自己
            List<String> shareIds = Lists.newArrayList(Splitter.on(",").trimResults().omitEmptyStrings().split(form.getSharerIdStr()));
            if (shareIds.contains(String.valueOf(patientId))) {
                log.warn("【配置共享人失败】：共享人不能是患者自己");
                return ResponseUtil.error(DiscountError.SHARER_NOT_ALLOW_OWNER);
            }
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

    public List<PatientCardSharerVo> getConfiguredSharer(Integer patientId, Integer couponId) {
        int countCoupon = 0;
        String sharer = null;
        List<PatientCardSharerVo> list = Lists.newArrayList();
        //查询患者是否有改产品的卡券
        countCoupon = mapper.countPatientCoupon(patientId, couponId);
        if (countCoupon != 0) {
            sharer = mapper.getRecentCard(patientId, couponId);
            this.buildSharer(sharer, list);
        } else {
            //查询患者是否有激活的卡券
            countCoupon = mapper.countPatientCoupon(patientId, null);
            if (countCoupon != 0) {
                sharer = mapper.getRecentCard(patientId, null);
                this.buildSharer(sharer, list);
            }
        }
        return list;
    }

    private void buildSharer(String sharer, List<PatientCardSharerVo> list) {
        if (StringUtils.isNotBlank(sharer)) {
            //共享人id
            List<Integer> sharerIds = Arrays.stream(sharer.split(",")).map(Integer::parseInt).collect(toList());
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
        }
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

    public List<WxPatientEffectiveVo> getPatientEffectiveCard(Integer patientId) {
        return mapper.listPatientEffectiveCard(patientId);
    }

    public WxCardUsageVo getWxUserCardUsage(Integer cardId) {
        return mapper.getCardUsage(cardId);
    }

    public RestErrorBo removeCard(Integer patientId, Integer cardId) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        errorBo.setError(DiscountError.SUCCESS);
        Card card = mapper.selectByPrimaryKey(cardId);
        if (card == null) {
            errorBo.setError(DiscountError.CARD_NOT_EXIST);
            return errorBo;
        }
        if (!patientId.equals(card.getPatientId())) {
            errorBo.setError(DiscountError.OTHER_CARD_NOT_ALLOW_DELETE);
            return errorBo;
        }
        int useCount = cardBenefitMapper.countCardUsed(cardId);
        if (useCount > 0) {
            errorBo.setError(DiscountError.CARD_IS_USED);
            return errorBo;
        }
        mapper.deleteByPrimaryKey(cardId);
        mqServiceFeign.sendMessage(cardId, DELETE, BaseCardSingle);
        log.info("【卡券删除发送消息成功】：卡券id[{}]", cardId);
        return errorBo;
    }

    public void removeCardList(List<Integer> cardIds) {
        Example example = new Example(Card.class);
        example.createCriteria().andIn("id", cardIds);
        mapper.deleteByExample(example);
        for (Integer cardId : cardIds) {
            mqServiceFeign.sendMessage(cardId, DELETE, BaseCardSingle);
            log.info("【批量卡券删除发送消息成功】：卡券id[{}]", cardId);
        }
    }

    /**
     * 选择优惠
     *
     * @param form form
     * @return res
     */
    public ResponseResult<List<OrderItemUseBo>> choiceBenefitBo(PatientChooseBenefitForm form) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer orderId = form.getOrderId();
        Integer patientId = form.getPatientId();
        Integer orgId = form.getOrgId();
        RestErrorBo errorBo;
        //查询患者可用优惠
        PatientOptionalBenefitVo benefitVo = this.getPatientBenefit(patientId, orderId, orgId);
        log.info("患者可使用的优惠券：[{}]", benefitVo);
        if (CollectionUtils.isEmpty(benefitVo.getDiscountVoList()) && CollectionUtils.isEmpty(benefitVo.getMemberCardVoList()) &&
                CollectionUtils.isEmpty(benefitVo.getExchangeVoList()) && CollectionUtils.isEmpty(benefitVo.getPackageVoList()) &&
                CollectionUtils.isEmpty(benefitVo.getVoucherVoList())) {
            log.info("【选择优惠】，患者没有可使用优惠券信息");
            return ResponseUtil.error(DiscountError.CANT_USE_BENEFIT);
        }
        log.info("订单选择的优惠券：[{}]", form);
        errorBo = checkChoiceBenefitForItem(benefitVo, form);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
        }
        try {
            //锁定患者选择的优惠信息
            errorBo = needLockKeys(assembleCardIds(form), loginUserId, RedisConstants.LOCK_CHOICE_CARD, DiscountError.CARD_HAS_CHOICE);
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
            log.warn("【选择优惠】优惠选择发生异常，解除卡券锁定");
            manualUnLock(loginUserId, RedisConstants.LOCK_CHOICE_CARD);
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
        log.info("订单选择的优惠信息：[{}]", form);
        form.setOrgId(treatmentServiceFeign.findOrderRecordById(form.getOrderId()).getOrgId());
        ResponseResult<List<OrderItemUseBo>> responseResult = choiceBenefitBo(form);
        log.info("操作人员选择的优惠信息：[{}]", responseResult);
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
        try {
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
                mixUsedThreadLocal.get().clear();
            }
        } finally {
            mixUsedThreadLocal.remove();
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
            setUpSingleBenefitInfoForOrder(orderItem, orgId, packageBenefitBos);
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
            setUpSingleBenefitInfoForOrder(orderItem, orgId, Collections.singletonList(memberBenefitBo));
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
        PatientOrderBenefitVo result = null;
        if (CollectionUtils.isNotEmpty(orderItemBos)) {
            result = new PatientOrderBenefitVo();
            BigDecimal totalBenefitAmount = orderItemBos.stream().filter(obj -> obj.getBenefitAmount() != null).map(OrderItemUseBo::getBenefitAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setBenefitTotalAmount(totalBenefitAmount);
            List<PatientItemBenefitVo> itemList = orderItemBos.stream().filter(obj -> CollectionUtils.isNotEmpty(obj.getItemUseBenefitBos()))
                    .map(obj -> {
                        BigDecimal benefitAmount = obj.getBenefitAmount() == null ? BigDecimal.ZERO : obj.getBenefitAmount();
                        PatientItemBenefitVo vo = new PatientItemBenefitVo();
                        vo.setOrderDetailId(obj.getOrderDetailId());
                        vo.setType(obj.getType());
                        vo.setItemId(obj.getItemId());
                        vo.setBenefitDiscountRate(obj.getBenefitDiscountRate());
                        vo.setItemBenefitAmount(benefitAmount);
                        List<ItemUseBenefitVo> itemUseBenefitVos = BeanCopierUtils.listGeneralCopyBean(obj.getItemUseBenefitBos(), ItemUseBenefitVo.class);
                        vo.setItemBenefitList(itemUseBenefitVos);
                        vo.setSupplyWorkload(this.calculateTotalWordLoad(obj));
                        return vo;
                    }).collect(toList());
            result.setItemList(itemList);
        }
        return result;
    }

    /**
     * 计算项目总工作量
     *
     * @param itemBenefitBo itemBenefitBo
     */
    private BigDecimal calculateTotalWordLoad(OrderItemUseBo itemBenefitBo) {
        Example example;
        BigDecimal supplyWorkload = BigDecimal.ZERO;
        List<ItemUseBenefitBo> itemUseBenefitBos = itemBenefitBo.getItemUseBenefitBos();
        for (ItemUseBenefitBo itemUseBenefitBo : itemUseBenefitBos) {
            if (COUPON_TYPE.equals(itemUseBenefitBo.getBenefitType())) {
                Integer couponType = itemUseBenefitBo.getCouponType();
                Integer couponId = itemUseBenefitBo.getCouponId();
                if (VOUCHER.equals(couponType)) {
                    example = new Example(VoucheCoupon.class);
                    example.createCriteria().andEqualTo("couponId", couponId);
                    VoucheCoupon voucheCoupon = voucherMapper.selectOneByExample(example);
                    if (voucheCoupon != null) {
                        supplyWorkload = supplyWorkload.add(itemUseBenefitBo.getBenefitAmount()
                                .multiply(voucheCoupon.getWorkloadRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (DISCOUNT.equals(couponType)) {
                    example = new Example(DiscountCoupon.class);
                    example.createCriteria().andEqualTo("couponId", couponId);
                    DiscountCoupon discountCoupon = discountCouponMapper.selectOneByExample(example);
                    if (discountCoupon != null) {
                        supplyWorkload = supplyWorkload.add(itemUseBenefitBo.getBenefitAmount()
                                .multiply(discountCoupon.getWorkloadRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (EXCHANGE.equals(couponType)) {
                    example = new Example(PackageCouponItem.class);
                    example.createCriteria().andEqualTo("couponId", couponId).andEqualTo("itemId", itemBenefitBo.getItemId()).
                            andEqualTo("type", itemBenefitBo.getType());
                    PackageCouponItem packageCouponItem = packageCouponItemMapper.selectOneByExample(example);
                    if (packageCouponItem != null) {
                        supplyWorkload = supplyWorkload.add(packageCouponItem.getWorkloadLoad());
                    }
                }
                if (SPECIAL_PACKAGE.equals(couponType)) {
                    example = new Example(SpecialPackageCouponItem.class);
                    example.createCriteria().andEqualTo("couponId", couponId).andEqualTo("itemId", itemBenefitBo.getItemId()).
                            andEqualTo("type", itemBenefitBo.getType());
                    SpecialPackageCouponItem specialPackageCouponItem = specialPackageCouponItemMapper.selectOneByExample(example);
                    if (specialPackageCouponItem != null) {
                        supplyWorkload = supplyWorkload.add(specialPackageCouponItem.getWorkloadLoad());
                    }
                }
            }
        }
        log.info("选择优惠，开单明细id：{}，计算补入工作量：{}", itemBenefitBo.getOrderDetailId(), supplyWorkload);
        return supplyWorkload;
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
            if (checkMixUsed(benefitBo)) {
                //订单项目id对应的可用的优惠券信息
                ItemBenefitUseDetailBo benefitUseDetailBo = findBenefitForOrderItem(orgId, benefitBo, orderItem);
                Integer couponType = benefitBo.getCouponType();
                if (MEMBER_CARD.equals(couponType) || benefitUseDetailBo != null) {
                    log.info("【单个数量】匹配优惠券，订单明细id：[{}], 卡券id：[{}], 优惠券id：[{}]", orderItem.getOrderDetailId()
                            , benefitBo.getCardId(), benefitBo.getCouponId());
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
                            putUseMixMapIfPresent(benefitBo);
                        }
                        if (DISCOUNT.equals(couponType)) {
                            benefitAmount = receivableAmount.multiply(BigDecimal.valueOf(1).subtract(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP);
                            //个体项目设置优惠相关信息
                            buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, TRUE.getCode());
                            putUseMixMapIfPresent(benefitBo);
                        }
                        if (MEMBER_CARD.equals(couponType)) {
                            BigDecimal memberPrice = BigDecimal.ZERO;
                            if (ZERO.equals(orderItem.getType())) {
                                ClinicTariffMemberPrice tariff = treatmentServiceFeign.findClinicTariffMemberPrice(orgId, benefitBo.getCardId(), orderItem.getItemId());
                                if (tariff == null) {
                                    memberPrice = originalPrice.multiply(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
                                } else {
                                    memberPrice = tariff.getDiscountPrice();
                                }
                            }
                            if (ONE.equals(orderItem.getType())) {
                                ClinicOralTariffMemberPrice oral = treatmentServiceFeign.findClinicOralTariffMemberPrice(orgId, benefitBo.getCardId(), orderItem.getItemId());
                                if (oral == null) {
                                    memberPrice = originalPrice.multiply(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
                                } else {
                                    memberPrice = oral.getDiscountPrice();
                                }
                            }
                            //订单项目id对应的可用的优惠券信息
                            benefitAmount = receivableAmount.subtract(memberPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                            buildOrderProperty(benefitAmount, orderItem, benefitBo, null, MEMBER_TYPE.getCode(), MEMBER_CARD.getCode(), TRUE.getCode());
                        }
                        if (VOUCHER.equals(couponType)) {
                            if (benefitBo.getFace().compareTo(BigDecimal.valueOf(0)) > 0) {
                                benefitAmount = receivableAmount.compareTo(benefitBo.getFace()) >= 0 ? benefitBo.getFace() : receivableAmount;
                                //个体项目设置优惠相关信息
                                buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, TRUE.getCode());
                                putUseMixMapIfPresent(benefitBo);
                            }
                        }
                        break;
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
//		order.setQuantity(order.getQuantity() - 1);
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
        itemUseBenefitBo.setBenefitAmount(benefitAmount);
        itemUseBenefitBos.add(itemUseBenefitBo);
        order.setItemUseBenefitBos(itemUseBenefitBos);
        if (EXCHANGE.equals(couponType) || SPECIAL_PACKAGE.equals(couponType)) {
            benefitUseDetailBo.setCount(benefitUseDetailBo.getCount() - 1);
        }
    }

    private int setUpMultiItemForOrder(List<PatientUseBenefitBo> benefitBos, OrderItemUseBo orderItem, Integer orgId,
                                       Integer itemIndex) {
        for (PatientUseBenefitBo benefitBo : benefitBos) {
            if (checkMixUsed(benefitBo)) {
                //订单项目id对应的可用的优惠券信息
                ItemBenefitUseDetailBo benefitUseDetailBo = findBenefitForOrderItem(orgId, benefitBo, orderItem);
                Integer couponType = benefitBo.getCouponType();
                if (benefitUseDetailBo != null || MEMBER_CARD.equals(couponType)) {
                    log.info("【多个数量】匹配优惠券，订单明细id：[{}], 卡券id：[{}], 优惠券id：[{}]", orderItem.getOrderDetailId()
                            , benefitBo.getCardId(), benefitBo.getCouponId());
                    //订单项目原价
                    BigDecimal originalPrice = orderItem.getReceivableAmount().divide(BigDecimal.valueOf(orderItem.getQuantity()), 4, BigDecimal.ROUND_HALF_UP);
                    //订单项目index已优惠金额
                    OrderItemChangeBo changeBo = getItemBenefitByIndex(orderItem, itemIndex);
                    //订单项目应收金额（原价 - 已优惠金额）
                    BigDecimal receivableAmount = originalPrice.subtract(changeBo.getDiscountedAmount());
                    BigDecimal benefitAmount = BigDecimal.ZERO;
                    if (receivableAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
                        if (EXCHANGE.equals(couponType) || SPECIAL_PACKAGE.equals(couponType)) {
                            BigDecimal packageUnitPrice = benefitUseDetailBo.getPackageUnitPrice();
                            benefitAmount = receivableAmount.compareTo(packageUnitPrice) > 0 ? benefitAmount = receivableAmount.subtract(packageUnitPrice)
                                    : BigDecimal.valueOf(0);
                            buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, itemIndex);
                            putUseMixMapIfPresent(benefitBo);
                            //设置项目index已使用金额
                            changeBo.setDiscountedAmount(changeBo.getDiscountedAmount().add(benefitAmount));
                            return TRUE.getCode();
                        }
                        if (DISCOUNT.equals(couponType)) {
                            benefitAmount = receivableAmount.multiply(BigDecimal.valueOf(1).subtract(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP);
                            buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), DISCOUNT.getCode(), itemIndex);
                            putUseMixMapIfPresent(benefitBo);
                            //设置项目index已使用金额
                            changeBo.setDiscountedAmount(changeBo.getDiscountedAmount().add(benefitAmount));
                            return TRUE.getCode();
                        }
                        if (MEMBER_CARD.equals(couponType)) {
                            BigDecimal memberPrice = BigDecimal.ZERO;
                            if (ZERO.equals(orderItem.getType())) {
                                ClinicTariffMemberPrice tariff = treatmentServiceFeign.findClinicTariffMemberPrice(orgId, benefitBo.getCardId(), orderItem.getItemId());
                                if (tariff == null) {
                                    memberPrice = originalPrice.multiply(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
                                } else {
                                    memberPrice = tariff.getDiscountPrice();
                                }
                            }
                            if (ONE.equals(orderItem.getType())) {
                                ClinicOralTariffMemberPrice oral = treatmentServiceFeign.findClinicOralTariffMemberPrice(orgId, benefitBo.getCardId(), orderItem.getItemId());
                                if (oral == null) {
                                    memberPrice = originalPrice.multiply(benefitBo.getDiscountRate().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
                                } else {
                                    memberPrice = oral.getDiscountPrice();
                                }
                            }
                            //订单项目id对应的可用的优惠券信息
                            benefitAmount = receivableAmount.subtract(memberPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                            buildOrderProperty(benefitAmount, orderItem, benefitBo, null, MEMBER_TYPE.getCode(), MEMBER_CARD.getCode(), itemIndex);
                            //设置项目index已使用金额
                            changeBo.setDiscountedAmount(changeBo.getDiscountedAmount().add(benefitAmount));
                            return TRUE.getCode();
                        }
                        if (VOUCHER.equals(couponType)) {
                            if (benefitBo.getFace().compareTo(BigDecimal.valueOf(0)) > 0) {
                                benefitAmount = receivableAmount.compareTo(benefitBo.getFace()) >= 0 ? benefitBo.getFace() : receivableAmount;
                                buildOrderProperty(benefitAmount, orderItem, benefitBo, benefitUseDetailBo, COUPON_TYPE.getCode(), couponType, itemIndex);
                                putUseMixMapIfPresent(benefitBo);
                                //设置项目index已使用金额
                                changeBo.setDiscountedAmount(changeBo.getDiscountedAmount().add(benefitAmount));
                            }
                        }
                    }
                }
            }
        }
        return FALSE.getCode();
    }

    private boolean checkMixUsed(PatientUseBenefitBo benefitBo) {
        Map<Integer, List<Integer>> mixUsedMap = mixUsedThreadLocal.get();
        List<Integer> mixUsedList = mixUsedMap.get(benefitBo.getCouponId());
        //当前个体被优惠的卡券共用属性
        Set<Integer> set = Sets.newHashSet(mixUsedMap.values().stream().flatMap(Collection::stream).collect(toSet()));
        return CollectionUtils.isNotEmpty(mixUsedList) || set.isEmpty()
                || (!set.contains(FALSE.getCode()) && ONE.equals(benefitBo.getMixable()));
    }

    /**
     * 查询患者可选择优惠信息
     *
     * @param query query
     * @return PatientOptionalBenefitVo
     */
    public PatientOptionalBenefitVo initBenefit(PatientBenefitQuery query) {
        int count = countByBenefit(query.getOrderId());
        if (count != 0) {
            return null;
        }
        OrderRecord record = treatmentServiceFeign.findOrderRecordById(query.getOrderId());
        return getPatientBenefit(query.getPatientId(), query.getOrderId(), record.getOrgId());
    }

    public List<Integer> listPatientAllCard(Integer patientId) {
        return mapper.listPatientAllCard(patientId);
    }

    private PatientOptionalBenefitVo getPatientBenefit(Integer patientId, Integer orderId, Integer orgId) {
        //查询患者可用优惠
        List<PatientBenefitBo> benefitBos = mapper.listBenefitByPatientId(patientId, orgId);
        //获取订单明细
        List<OrderDetail> orderDetail = treatmentServiceFeign.findOrderDetailByOrderRecordId(orderId);
        if (CollectionUtils.isEmpty(orderDetail)) {
            return new PatientOptionalBenefitVo();
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
        Comparator<PatientBenefitBo> comparator = Comparator.comparing(PatientBenefitBo::getItemUsable, Comparator.reverseOrder())
                .thenComparing(PatientBenefitBo::getUseDeadline).thenComparing(obj -> patientId.equals(obj.getOwnerId()) ? 0 : 1);
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
        if (CollectionUtils.isNotEmpty(items) && CollectionUtils.isNotEmpty(orderItemIds)) {
            Set<Integer> itemIds = items.stream().map(SpecialPackageCouponItem::getItemId).collect(toSet());
            //查询卡券使用数量信息
            List<CouponItemUseBo> list = mapper.getCouponItemUseInfo(benefitBo.getCouponId(), benefitBo.getCardId(), itemType, couponType);
            //设置优惠券是否可以作用订单项目
            return setItemUsableStatus(itemIds, orderItemIds, benefitBo, list);
        }
        return false;
    }

    private boolean checkExchangeAndSetUsable(Integer itemType, Map<Integer, Set<Integer>> itemMap, PatientBenefitBo benefitBo,
                                              Integer couponType) {
        //订单项目明细
        Set<Integer> orderItemIds = itemMap.get(itemType);
        //查询套餐券优惠项目ids
        List<PackageCouponItem> items = getExchangeItemInfo(benefitBo.getCouponId(), itemType);
        if (CollectionUtils.isNotEmpty(items) && CollectionUtils.isNotEmpty(orderItemIds)) {
            Set<Integer> itemIds = items.stream().map(PackageCouponItem::getItemId).collect(toSet());
            //查询卡券使用数量信息
            List<CouponItemUseBo> list = mapper.getCouponItemUseInfo(benefitBo.getCouponId(), benefitBo.getCardId(), itemType, couponType);
            //设置优惠券是否可以作用订单项目
            return setItemUsableStatus(itemIds, orderItemIds, benefitBo, list);
        }
        return false;
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
        PatientOptionalBenefitVo vo = new PatientOptionalBenefitVo();
        //查询卡主信息
        Set<Integer> ownerIds = benefitBos.stream().map(PatientBenefitBo::getOwnerId).collect(toSet());
        Map<Integer, PatientBaseInfoVo> patientMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(ownerIds)) {
            List<PatientBaseInfoVo> owners = patientFeign.findPatientInfoByIds(Lists.newArrayList(ownerIds));
            if (CollectionUtils.isNotEmpty(owners)) {
                patientMap = owners.stream().collect(toMap(PatientBaseInfoVo::getId, Function.identity()));
            }
        }
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
                    memberCardVo.setMemberCardId(obj.getSecondaryMemberTypeId());
                    memberCardVo.setMemberCardName(obj.getMemberCardName());
                    //卡号
                    memberCardVo.setMemberCardNumber(obj.getSecondaryCardNumber());
                    //卡主
                    memberCardVo.setOwner(obj.getSecondaryName());
                    memberCardVo.setMemberCardRate(null);
                    memberCardVo.setMemberCardRate(BigDecimal.valueOf(obj.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    memberCardVo.setPath(obj.getPictureCode());
                    return memberCardVo;
                }).collect(toList());
            }
            if (masertMemberInfoVo != null) {
                PatientMemberCardVo memberCardVo = new PatientMemberCardVo();
                memberCardVo.setMemberCardId(masertMemberInfoVo.getMasterCardTypeId());
                memberCardVo.setMemberCardName(masertMemberInfoVo.getMasterMemberCardName());
                //卡号
                memberCardVo.setMemberCardNumber(masertMemberInfoVo.getMasterCardNumber());
                memberCardVo.setMemberCardRate(BigDecimal.valueOf(masertMemberInfoVo.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
                memberCardVo.setPath(masertMemberInfoVo.getPictureCode());
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
        vo.setLink(serverPort + "/#/cardQrData?" + "cardId=" + card.getId());
        return vo;
    }

    private Card updateCardForSold(Integer cardId, CardSoldForm form, Integer loginUserId, LocalDateTime now, Integer saleChannelId) {
        Card updateCard = BeanCopierUtils.generalCopyBean(form, Card.class);
        updateCard.setStatus(ACTIVE_PENDING.getCode());
        if (SOLD.equals(form.getSoldType())) {
            if (ONE.equals(form.getSoldAndPay())) {
                updateCard.setSoldAndPay(ONE);
                updateCard.setPayId(form.getPayId());
                updateCard.setPayDate(now);
                updateCard.setPay(TRUE.getCode());
            }
            if (ZERO.equals(form.getSoldAndPay())) {
                updateCard.setSoldAndPay(ZERO);
                updateCard.setPayId(null);
                updateCard.setPay(FALSE.getCode());
            }
        } else {
            updateCard.setSoldAndPay(null);
            updateCard.setPayId(null);
        }
        updateCard.setSoldDate(now);
        updateCard.setSellerUserId(loginUserId);
        updateCard.setUpdId(loginUserId);
        updateCard.setId(cardId);
        updateCard.setSaleChannelId(saleChannelId);
        return updateCard;
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
        cancelCard.setUpdTime(LocalDateTime.now());
        mapper.updateByPrimaryKey(cancelCard);
    }

    private void saveCancelCardLog(Integer cardId) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        CardCancelLog cancelCard = new CardCancelLog();
        cancelCard.setCardId(cardId);
        cancelCard.setCancelDate(new Date());
        cancelCard.setCrtId(loginUserId);
        cancelCard.setUpdId(loginUserId);
        cardCancelLogMapper.insertSelective(cancelCard);
    }

    /**
     * 自有平台卡券激活
     *
     * @param patientId   patientId
     * @param form        form
     * @param loginUserId loginUserId
     */
    private Card updateOwnActiveCard(Integer patientId, OwnCardActiveForm form, Integer loginUserId, Integer couponId, Card card) {
        Integer activeOrgId = StringUtils.isBlank(BaseContextHandler.getOrgId()) ? null : Integer.valueOf(BaseContextHandler.getOrgId());
        CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(card.getCouponId());
        LocalDateTime now = LocalDateTime.now();
        Card ownActiveCard = new Card();
        ownActiveCard.setId(form.getCardId());
        ownActiveCard.setPatientId(patientId);
        ownActiveCard.setActiveOrgId(activeOrgId);
        ownActiveCard.setActiveUserId(loginUserId);
        ownActiveCard.setStatus(ACTIVATED.getCode());
        if (RECHARGE.equals(coupon.getType().intValue())) {
            ownActiveCard.setStatus(USE_ALL.getCode());
        }
        if (form.getPayId() != null) {
            ownActiveCard.setSoldAndPay(TRUE.getCode());
            ownActiveCard.setPayId(form.getPayId());
            ownActiveCard.setPay(TRUE.getCode());
            ownActiveCard.setPayDate(now);
        }
        ownActiveCard.setUpdId(loginUserId);
        ownActiveCard.setActiveDate(now);
        mapper.updateByPrimaryKeySelective(ownActiveCard);
        return ownActiveCard;
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
        insertOtherCard.setSharer(form.getSharerIdStr());
        insertOtherCard.setCrtId(loginUserId);
        insertOtherCard.setUpdId(loginUserId);
        LocalDateTime now = LocalDateTime.now();
        if (isAiYa(form.getCouponId())) {
            insertOtherCard.setActiveDate(LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear())
                    , LocalTime.MIN));
        } else {
            insertOtherCard.setActiveDate(LocalDateTime.now());
        }
        mapper.insertSelective(insertOtherCard);
        return insertOtherCard;
    }

    private boolean isAiYa(Integer couponId) {
        CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
        ProductType productType = productTypeMapper.selectByPrimaryKey(couponInfo.getProductTypeId());
        return productType != null && "套餐有效期至12.31".equals(productType.getName());
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
        Date now = Date.from(LocalDate.now().atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
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
    private RestErrorBo checkCouponForActive(Integer couponId, RestError error) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
        if (couponInfo == null || !couponInfo.getIsInservice()) {
            log.warn("【激活失败】优惠券[{}]不存在或已停用", couponId);
            errorBo.setError(DiscountError.COUPON_NOT_EXIST);
            return errorBo;
        }
        //查询产品有效期
        Date deadline = getCouponDeadLine(couponInfo.getType().intValue(), couponId);
        Date now = Date.from(LocalDate.now().atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
        if (deadline != null && now.after(deadline)) {
            log.warn("优惠券{}已过期", couponInfo.getName());
            errorBo.setError(error);
            return errorBo;
        }
        return errorBo;
    }

    private RestErrorBo checkCouponShare(Integer couponId, String sharerIdStr) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        if (StringUtils.isNotBlank(sharerIdStr)) {
            CouponCommonInfo couponInfo = couponMapper.selectByPrimaryKey(couponId);
            //优惠券是否与人共享使用
            Boolean shareStatus = getShareStatus(couponInfo.getType().intValue(), couponId);
            if (!shareStatus) {
                log.warn("{}不能与他人共享", couponInfo.getName());
                errorBo.setError(DiscountError.COUPON_NOT_ALLOW_SHARE);
            }
        }
        return errorBo;
    }

    /**
     * 检查卡券售出信息
     *
     * @param card    card
     * @param orgId   orgId
     * @param orgName orgName
     * @return RestErrorBo
     */
    private RestErrorBo checkCardForSale(Integer cardId, Card card, Integer orgId, String orgName) {
        //校验卡券基础信息
        RestErrorBo errorBo = checkCardBaseInfo(cardId, card, orgId, orgName);
        if (!SALE_PENDING.equals(card.getStatus())) {
            log.warn("【售卖失败】卡券[{}]售卖状态异常", card.getCardNumber());
            errorBo.setError(DiscountError.CARD_SOLD_STATUS_ERROR);
            return errorBo;
        }
        return errorBo;
    }

    /**
     * 检查卡券取消售出信息
     *
     * @param card  card
     * @param orgId orgId
     * @return RestErrorBo
     */
    private RestErrorBo checkCardForCancelSale(Integer cardId, Card card, Integer orgId) {
        //获取组织名
        String orgName = getOrgName(orgId);
        //校验卡券基础信息
        RestErrorBo errorBo = checkCardBaseInfo(cardId, card, orgId, orgName);
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
     * @param payId payId
     * @param card  card
     * @return res
     */
    private RestErrorBo checkCardForOwnActive(Integer payId, Card card) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        if (card == null) {
            log.warn("【激活失败】卡券不存在");
            errorBo.setError(DiscountError.CARD_NOT_EXIST);
            return errorBo;
        }
        if (card.getActiveOrgId() != null || ACTIVATED.equals(card.getStatus()) || card.getPatientId() != null ||
                PARTIAL_USE.equals(card.getStatus()) || USE_ALL.equals(card.getStatus())) {
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

    private RestErrorBo checkCardForOtherActive(OtherCardActiveForm form) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        String cardNumber = form.getThirdCardNumber();
        Example example = new Example(Card.class);
        example.createCriteria().andEqualTo("cardNumber", cardNumber);
//				.andNotEqualTo("orgId", ZERO);
        Card card = mapper.selectOneByExample(example);
        if (card != null) {
            log.warn("【第三方平台激活失败】自有平台卡券{}不允许在第三方平台激活", cardNumber);
            errorBo.setError(DiscountError.OTHER_ALLOW_ACTIVE_OWN);
            return errorBo;
        }
        List<Card> thirdCards = this.getThirdCard(form);
        if (CollectionUtils.isNotEmpty(thirdCards)) {
            log.warn("【第三方平台激活失败】第三方平台卡券已激活", cardNumber);
            errorBo.setError(DiscountError.OTHER_CARD_IS_ACTIVATED);
            return errorBo;
        }
        return errorBo;
    }

    private List<Card> getThirdCard(OtherCardActiveForm form) {
        Example example = new Example(Card.class);
        example.createCriteria().andEqualTo("thirdCardNumber", form.getThirdCardNumber())
                .andEqualTo("couponId", form.getCouponId())
                .andEqualTo("saleChannelId", form.getSaleChannelId());
        return mapper.selectByExample(example);
    }

    private RestErrorBo checkCardBaseInfo(Integer cardId, Card card, Integer orgId, String orgName) {
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
        baseVo.setUseDeadline(bo.getUseDeadline() == null ? "永久有效" : bo.getUseDeadline());
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
                        errorBo.setMsg(card == null ? null : card.getOrgId() == 0 ? card.getThirdCardNumber() : card.getCardNumber());
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
                        errorBo.setMsg(card == null ? null : card.getOrgId() == 0 ? card.getThirdCardNumber() : card.getCardNumber());
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
                for (Integer voucherId : useVoucherIds) {
                    if (!voucherIdsForItem.contains(voucherId)) {
                        errorBo.setError(DiscountError.PATIENT_NOT_OWN_VOUCHER);
                        Card card = mapper.selectByPrimaryKey(voucherId);
                        errorBo.setMsg(card == null ? null : card.getOrgId() == 0 ? card.getThirdCardNumber() : card.getCardNumber());
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
     * 组合优惠信息
     *
     * @param form 患者选择优惠信息
     * @return set
     */
    protected List<Integer> assembleCardIds(PatientChooseBenefitForm form) {
        List<Integer> cardIds = Lists.newArrayList();
        Integer discountId = form.getDiscountId();
        if (discountId != null) {
            cardIds.add(discountId);
        }
        List<Integer> exchangeIds = form.getExchangeIds();
        if (CollectionUtils.isNotEmpty(exchangeIds)) {
            cardIds.addAll(exchangeIds);
        }
        List<Integer> packageIds = form.getPackageIds();
        if (CollectionUtils.isNotEmpty(packageIds)) {
            cardIds.addAll(packageIds);
        }
        List<Integer> voucherIds = form.getVoucherIds();
        if (CollectionUtils.isNotEmpty(voucherIds)) {
            cardIds.addAll(voucherIds);
        }
        return cardIds;
    }

    private String lockChoiceCard(String key, Integer value, String lockPrefix) {
        String lockKey = Joiner.on(":").join(lockPrefix, key);
        String lockVal = String.valueOf(value);
        // 锁定
        boolean locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
        if (!locked) {
            Card card = mapper.selectByPrimaryKey(key);
            String cardNumber = (card == null) ? null : card.getOrgId() == 0 ? card.getThirdCardNumber() : card.getCardNumber();
            log.warn("【锁定失败】卡号是[{}]的卡券正在被使用，请取消使用该卡券！", cardNumber);
            return cardNumber;
        }
        return null;
    }

    protected void unLockByIds(Set<String> delCardIds, String lockPrefix, Integer requestId) {
        log.info("开始释放卡券资源");
        if (CollectionUtils.isNotEmpty(delCardIds)) {
            for (String delCardId : delCardIds) {
                String lockKey = Joiner.on(":").join(lockPrefix, String.valueOf(delCardId));
                String lockVal = String.valueOf(requestId);
                // 释放患者取消选择的卡券的锁
                redisUtils.unlock(lockKey, lockVal);
            }
            log.info("解锁完成");
        }
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
            OrderItemUseBo bo = OrderItemUseBo.getInstance(order.getQuantity());
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
                        //会员卡类型id
                        memberCard.setCardId(obj.getMemberCardId());
                        memberCard.setDiscountRate(obj.getMemberCardRate());
                        memberCard.setCouponName(obj.getMemberCardName());
                        memberCard.setCouponType(MEMBER_CARD.getCode());
                        memberCard.setMixable(TRUE.getCode());
                        return memberCard;
                    }).collect(toList());
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

    private RestErrorBo needLockKeys(List<Integer> resourceIds, Integer requestId, String lockPrefix, RestError error) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        List<String> conflictList = Lists.newArrayList();
        log.info("【开始锁定】开始锁定用户选择的卡券");
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            Set<String> cardStrList = resourceIds.stream().map(String::valueOf).collect(toSet());
            //获取用户已被锁定的卡券
            Set<String> existLockKeys = getExistLock(requestId, lockPrefix);
            if (CollectionUtils.isNotEmpty(existLockKeys)) {
                Set<String> addCardIds;
                Set<String> delCardIds;
                Set<String> commonIds;
                addCardIds = SetUtils.difference(cardStrList, existLockKeys);
                delCardIds = SetUtils.difference(existLockKeys, cardStrList);
                commonIds = SetUtils.intersection(existLockKeys, cardStrList);
                log.info("新增需要锁定的卡券：{}", addCardIds);
                log.info("删除锁定的卡券：{}", delCardIds);
                log.info("需要刷新锁定的卡券：{}", commonIds);
                //需要增加的key
                for (String addCardId : addCardIds) {
                    String result = lockChoiceCard(addCardId, requestId, lockPrefix);
                    if (StringUtils.isNotBlank(result)) {
                        conflictList.add(result);
                    }
                }
                //如果发生冲突，释放释放已选择卡券信息
                if (CollectionUtils.isNotEmpty(conflictList)) {
                    manualUnLock(requestId, lockPrefix);
                } else {
                    //需要删除的key
                    if (CollectionUtils.isNotEmpty(delCardIds)) {
                        unLockByIds(delCardIds, lockPrefix, requestId);
                    }
                    //需要更新的key
                    commonIds.forEach(key -> {
                        String lockKey = Joiner.on(":").join(lockPrefix, String.valueOf(key));
                        redisUtils.expire(lockKey, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
                    });
                }
            } else {
                for (String cardId : cardStrList) {
                    String result = lockChoiceCard(cardId, requestId, lockPrefix);
                    if (StringUtils.isNotBlank(result)) {
                        conflictList.add(result);
                    }
                }
                //释放选择的卡券
                if (CollectionUtils.isNotEmpty(conflictList)) {
                    manualUnLock(requestId, lockPrefix);
                }
            }
            log.info("【锁定成功】患者选择卡券成功");
        }
        //释放选择的卡券
        if (CollectionUtils.isNotEmpty(conflictList)) {
            log.info("选择卡券时，冲突的卡券：{}", conflictList);
            errorBo.setError(error);
            errorBo.setMsg(Joiner.on(",").join(conflictList));
        }
        return errorBo;
    }

    /**
     * 获取当前用户已被锁定的卡券
     *
     * @param requestId  登录用户
     * @param lockPrefix lockPrefix
     * @return set
     */
    private Set<String> getExistLock(Integer requestId, String lockPrefix) {
        Set<String> keys = redisUtils.keys(lockPrefix + "*");
        if (CollectionUtils.isNotEmpty(keys)) {
            return keys.stream().filter(key -> requestId.equals(Integer.valueOf(redisUtils.get(key))))
                    .map(key -> key.replace(lockPrefix + ":", ""))
                    .collect(toSet());
        }
        return null;
    }

    public ResponseResult<Boolean> manualLock(List<Integer> ids, String lockPrefix) {
        log.info("【手动加锁】锁信息：{}，需要加锁的keys：{}", lockPrefix, ids);
        if (CollectionUtils.isNotEmpty(ids)) {
            Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
            RestErrorBo errorBo = needLockKeys(ids, loginUserId, lockPrefix, DiscountError.CARD_IS_ON_SALE);
            if (errorBo.getError() != null) {
                return ResponseUtil.error(errorBo.getError(), errorBo.getMsg());
            }
        }
        return ResponseUtil.success(true);
    }

    /**
     * 手动解锁
     *
     * @param requestId  requestId
     * @param lockPrefix lockPrefix
     */
    public void manualUnLock(Integer requestId, String lockPrefix) {
        Set<String> existLock = getExistLock(requestId, lockPrefix);
        log.info("【手动解锁】锁信息：[{}]，需要解锁的keys：{}", lockPrefix, existLock);
        if (CollectionUtils.isNotEmpty(existLock)) {
            unLockByIds(existLock, lockPrefix, requestId);
        }
    }

    private int countByBenefit(Integer orderId) {
        Example example = new Example(CardBenefit.class);
        example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("deleted", FALSE.getCode());
        return cardBenefitMapper.selectCountByExample(example);
    }

    private OrderItemChangeBo getItemBenefitByIndex(OrderItemUseBo orderItem, Integer index) {
        List<OrderItemChangeBo> changeBos = orderItem.getChangeBos();
        Optional<OrderItemChangeBo> first = changeBos.stream().filter(obj -> index.equals(obj.getIndex())).findFirst();
        return first.orElse(null);
    }

    /**
     * 校验卡券激活信息
     *
     * @param query query
     * @return error
     */
    private RestErrorBo checkCardActiveInfo(CardActiveQuery query) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        String cardPassEncode = Base64.getEncoder().encodeToString(query.getCardPassword().getBytes());
        Example example = new Example(Card.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("cardNumber", query.getCardNumber());
        int countNum = mapper.selectCountByExample(example);
        if (countNum == 0) {
            errorBo.setError(DiscountError.CARD_NUMBER_ERROR);
            return errorBo;
        }
        criteria.andEqualTo("cardPassword", cardPassEncode);
        Card card = mapper.selectOneByExample(example);
        if (card == null) {
            errorBo.setError(DiscountError.CARD_PASSWORD_ERROR);
            return errorBo;
        }
        errorBo = checkCardInfo(card);
        //传递优惠券id、卡密
        errorBo.setMsg(card.getCardPassword());
        return errorBo;
    }

    /**
     * 校验代金、折扣、兑换、套餐的卡券售卖信息
     *
     * @param card card
     * @return error
     */
    private RestErrorBo checkCardInfo(Card card) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        if (ACTIVATED.equals(card.getStatus()) || PARTIAL_USE.equals(card.getStatus()) || USE_ALL.equals(card.getStatus())) {
            errorBo.setError(DiscountError.CARD_IS_ACTIVATED);
            return errorBo;
        }
        if (!ACTIVE_PENDING.equals(card.getStatus())) {
            errorBo.setError(DiscountError.CARD_ACTIVE_STATUS_ERROR);
            return errorBo;
        }
        CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(card.getCouponId());
        if (RECHARGE.equals(coupon.getType().intValue())) {
            errorBo.setError(DiscountError.RECHARGE_NOT_ALLOW);
            return errorBo;
        }
        //校验卡券有效期
        errorBo = checkCouponForActive(card.getCouponId(), DiscountError.CARD_BEYOND_DEADLINE);
        return errorBo;
    }

    /**
     * 校验充值卡激活信息
     *
     * @param query query
     * @return error
     */
    private RestErrorBo checkRechargeActiveInfo(CardActiveQuery query) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        String cardPassEncode = Base64.getEncoder().encodeToString(query.getCardPassword().getBytes());
        Example example = new Example(Card.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("cardNumber", query.getCardNumber());
        int countNum = mapper.selectCountByExample(example);
        if (countNum == 0) {
            errorBo.setError(DiscountError.CARD_NUMBER_ERROR);
            return errorBo;
        }
        criteria.andEqualTo("cardPassword", cardPassEncode);
        Card card = mapper.selectOneByExample(example);
        if (card == null) {
            errorBo.setError(DiscountError.CARD_PASSWORD_ERROR);
            return errorBo;
        }
        errorBo = checkRechargeCardInfo(card);
        //传递优惠券id、卡密
        errorBo.setMsg(card.getCardPassword());
        return errorBo;
    }

    /**
     * 校验充值的卡券售卖信息
     *
     * @param card card
     * @return error
     */
    private RestErrorBo checkRechargeCardInfo(Card card) {
        RestErrorBo errorBo = RestErrorBo.getInstance();
        if (ACTIVATED.equals(card.getStatus()) || PARTIAL_USE.equals(card.getStatus()) || USE_ALL.equals(card.getStatus())) {
            errorBo.setError(DiscountError.RECHARGE_HAS_RECHARGED);
            return errorBo;
        }
        if (!ACTIVE_PENDING.equals(card.getStatus())) {
            errorBo.setError(DiscountError.RECHARGE_NOT_SOLD);
            return errorBo;
        }
        CouponCommonInfo coupon = couponMapper.selectByPrimaryKey(card.getCouponId());
        if (!RECHARGE.equals(coupon.getType().intValue())) {
            errorBo.setError(DiscountError.OTHER_CARD_NOT_ALLOW);
            return errorBo;
        }
        //校验充值卡券有效期
        errorBo = checkCouponForActive(card.getCouponId(), DiscountError.RECHARGE_TIME_OUT);
        return errorBo;
    }

    /**
     * 本地线程变量存入优惠券的共用属性
     *
     * @param benefitBo benefitBo
     */
    private void putUseMixMapIfPresent(PatientUseBenefitBo benefitBo) {
        mixUsedThreadLocal.get().compute(benefitBo.getCouponId(), (k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                return Lists.newArrayList(benefitBo.getMixable());
            }
            v.add(benefitBo.getMixable());
            return v;
        });
    }

    /**
     * 根据条件查询卡券售出现金收款总和
     *
     * @param query 查询条件
     * @return BigDecimal
     */
    public BigDecimal findCardSaleCashReceipt(CashReceiptOrRefundQuery query) {
        return mapper.selectCardSaleCashReceipt(query);
    }

    public List<CardIyOr365VO> findIyOr365CardActivedList(CardIyOr365ActivedQuery query) {
        return mapper.selectIyOr365CardActivedList(query);
    }

    public boolean whetherUseCard(List<Integer> cardIds) {
        Example example = new Example(CardBenefit.class);
        example.createCriteria().andIn("cardId", cardIds)
                .andEqualTo("benefitType", 1)
                .andEqualTo("deleted", 0);
        int useCount = cardBenefitMapper.selectCountByExample(example);
        return useCount > 0;
    }

    private void miniActive(Integer patientId, Card card, LocalDateTime now, Integer loginUserId) {
        if (Objects.equals("小程序虚拟服务售卖", card.getRemark())) {
            log.info("小程序卡券激活：cardId：{}", card.getId());
            PatientBaseInfo patientBaseInfo = patientFeign.findPatientInfoById(patientId);
            VirtualActiveForm form = new VirtualActiveForm();
            form.setCardId(card.getId());
            form.setActiveDate(now);
            form.setActiveUserId(loginUserId);
            form.setOrderSn(card.getSoldPhoneNumber());
            form.setPatientId(patientId);
            form.setPatientMobile(patientBaseInfo.getMobile());
            form.setPatientName(patientBaseInfo.getName());
            ivyMiniServiceFeign.activeCard(form);
        }
    }
}
