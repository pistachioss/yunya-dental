package com.yunya.modules.discount.biz;

import com.github.pagehelper.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.yunya.feign.discount.domain.bo.*;
import com.yunya.feign.discount.domain.model.*;
import com.yunya.feign.discount.domain.query.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.system.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.biz.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya.framework.redis.util.*;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.enums.*;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.*;
import org.apache.commons.collections4.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import tk.mybatis.mapper.entity.*;

import javax.annotation.*;
import java.security.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Function;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.modules.discount.enums.CardStatusEnum.*;
import static com.yunya.modules.discount.enums.DiscountError.*;
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
    private CouponCommonInfoMapper couponMapper;
    @Resource
    private CouponAllocateMapper allocateMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource(name = "customizeThreadPool")
    private ExecutorService cardThreadPool;

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

    /**
     * 生成分配
     * @param allocateModel model
     * @return res
     * @throws Exception ex
     */
    public ResponseResult generateAllocate(GenerateAllocateModel allocateModel) throws Exception {
        boolean locked = false;
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer couponId = allocateModel.getCouponId();
        LocalDateTime submitDate = allocateModel.getSubmitDate();
        String lockKey = Joiner.on(":").join(RedisConstants.LOCK_CARD_GENERATE, String.valueOf(couponId), submitDate.toEpochSecond(ZoneOffset.of("+8")));
        String lockVal = String.valueOf(loginUserId);
        long start = System.currentTimeMillis();
        ResponseResult result;
        log.info("卡券生成分配开始提交：[{}]，提交日期：[{}]", couponId, submitDate);
        try {
            // 1. 锁定草稿病例
            locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("【锁定失败】卡券[{}]正在分配：[{}]，无法提交", couponId, submitDate);
                return ResponseUtil.error(KEY_IS_LOCKED);
            }
            log.info("【锁定成功】准备提交卡券生成分配...");
            //1. 校验优惠券分配信息
            int count = allocateMapper.countGeneratedByParam(couponId, submitDate);
            if (count > 0) {
                log.warn("【卡券生成失败】：[{}]该批次[{}]已有组织生成卡券", couponId, submitDate);
                return ResponseUtil.error(CARD_IS_GENERATED);
            }
            List<ClinicAllocateModel> allocateList = allocateModel.getAllocateList();
            List<Integer> couponAllocateIds = allocateList.stream().map(ClinicAllocateModel::getCouponAllocateId)
                    .collect(toList());
            int allocateCount = mapper.countByAllocateId(couponAllocateIds);
            if (allocateCount > 0) {
                log.warn("【卡券生成失败】：[{}]该批次[{}]已有组织生成卡券", couponId, submitDate);
                return ResponseUtil.error(CARD_IS_GENERATED);
            }
            //2. 校验组织优惠券分配明细
            result = checkCouponAllocate(allocateList, couponId, submitDate);
            if (result != null) {
                return result;
            }
            //2. 提交生成分配
            result = generateAllocateDetail(allocateList, couponId, submitDate, allocateModel.getCouponCode());
            long end = System.currentTimeMillis();
            log.info("卡券[{}]生成分配完成，执行时间[{}]秒[{}]毫秒", couponId, (end - start) / 1000, (end - start) % 1000);
            return result;
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult generateAllocateDetail(List<ClinicAllocateModel> allocateList, Integer couponId,
                                                 LocalDateTime submitDate, String couponCode) throws Exception {
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
            return ResponseUtil.error(FAIL_TO_GENERATE);
        }
        LocalDateTime generateDate = LocalDateTime.now();
        CountDownLatch cardLatch = new CountDownLatch(sumAllocate);
        //2. 计算每个卡券的生成信息
        List<Future<Card>> cardFutureList = createCardEntity(couponId, couponCode, numBoList, loginUserId,
                generateDate, cardLatch, sumAllocate);
        cardLatch.await();
        //取出卡券明细任务的执行结果
        List<Card> cardList = getAllocateFutureResult(cardFutureList);
        if (CollectionUtils.isEmpty(cardList) || sumAllocate != cardList.size()) {
            log.warn("【卡券生成失败】");
            return ResponseUtil.error(FAIL_TO_GENERATE);
        }
        log.info("【卡券明细任务执行结束】卡券数量count：[{}]", cardList.size());
        //3. 生成卡券信息
        mapper.insertList(cardList);
        //4. 更新优惠券分配记录
        allocateMapper.updateAllocateByIds(couponAllocateIds, submitDate, loginUserId, generateDate);
        return ResponseUtil.success();
    }

    /**
     * 查看配给详情
     * @param query query
     * @return list
     */
    public List<ViewAllocateVo> getAllocateDetail(GenerateAllocateQuery query) {
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
     * @param query query
     * @return list
     */
    public List<ExportCardAllocateVo> getExportCardAllocateList(GenerateAllocateQuery query) {
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
                vo.setCardPass(new String(Base64.getDecoder().decode(obj.getPassword())));
                return vo;
            }).collect(toList());
        }
        log.info("查询导出卡券分配信息完成");
        return list;
    }

    /**
     * 产品售卖分页查询
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

    public PageInfo<CardSalePageVo> getCardSalePageVo(CardSaleQuery query) {
        Page<Card> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        mapper.listCardInfosByParam(query.getCardNumber(), query.getSoldTypeList(), query.getCardStatsList(), query.getPhoneNumber(),
                                    query.getCouponId(),query.getOrgId());
        //实体转换为pagevo
        List<CardSalePageVo> list = page.getResult().stream().map(this::cardConvertPageVo).collect(toList());
        PageInfo<CardSalePageVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
    }

    private List<Future<Card>> createCardEntity(Integer couponId, String couponCode, List<AllocateNumBo> numBoList,
                                                Integer loginUserId, LocalDateTime generateDate, CountDownLatch cardLatch, int totalTask) {
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
                    card.setPassword(generatePass());
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

    private ResponseResult checkCouponAllocate(List<ClinicAllocateModel> allocateList, Integer couponId, LocalDateTime submitDate) {
        Example example = new Example(CouponAllocate.class);
        example.createCriteria().andEqualTo("couponId", couponId)
                .andEqualTo("crtTime", submitDate);
        List<CouponAllocate> list = allocateMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            log.warn("【卡券生成失败】：优惠券[{}]未分配，请先分配再生成", couponId);
            return ResponseUtil.error(DiscountError.COUPON_NOT_ALLOCATE);
        }
        //校验数量
        if (list.size() != allocateList.size()) {
            return ResponseUtil.error(DiscountError.NUM_NOT_EQUAL);
        }
        //已分配优惠券映射
        Map<Integer, CouponAllocate> entityMap = list.stream()
                .collect(toMap(CouponAllocate::getId, Function.identity()));
        for (ClinicAllocateModel allocateModel : allocateList) {
            CouponAllocate couponAllocate = entityMap.get(allocateModel.getCouponAllocateId());
            //校验组织分配优惠券数量和时间
            if (couponAllocate == null || !allocateModel.getAllocateNum().equals(couponAllocate.getAllocateNum())
                    || !submitDate.equals(DateUtil.dateToLocalDateTime(couponAllocate.getCrtTime()))) {
                OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(allocateModel.getOrgId());
                log.warn("【卡券生成失败】：[{}]优惠券分配时间[{}]", orgInfo.getName(), submitDate);
                return ResponseUtil.error(DiscountError.ORG_BATCH_ERROR, orgInfo.getName());
            }
        }
        return null;
    }

    /**
     * 加密加密生成卡券密码
     *
     * @return str
     */
    private static String generatePass() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < CARD_PASS_BIT; i++) {
            builder.append(new SecureRandom().nextInt(9));
        }
        return Base64.getEncoder().encodeToString(builder.toString().getBytes());
    }

    private List<Integer> listIdsBySubmitParam(GenerateAllocateQuery query) {
        Example example = new Example(CouponAllocate.class);
        example.createCriteria().andEqualTo("couponId", query.getCouponId())
                .andEqualTo("crtTime", query.getSubmitDate());
        List<CouponAllocate> list = allocateMapper.selectByExample(example);
        return list.stream().map(CouponAllocate::getId).collect(toList());
    }

    private CardSalePageVo cardConvertPageVo(Card card) {
        CardSalePageVo vo = BeanCopierUtils.generalCopyBean(card, CardSalePageVo.class);
        vo.setCardPass(new String(Base64.getDecoder().decode(card.getPassword())));
        vo.setSoldTypeName(SoldTypeEnum.getValue(card.getSoldType()));
        vo.setSoldStatusName(CardStatusEnum.getValue(card.getStatus()));
        vo.setPayStatus(TrueFalseEnum.getValue(card.getPay()));
        vo.setSoldWayName(SoldWayEnum.getValue(card.getSoldWay()));
        return vo;
    }
}
