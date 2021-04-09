package com.yunya.middletable.service.discount;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.middletable.dao.discount.AuthDiscountBenefitMapper;
import com.yunya.middletable.dao.discount.CardBenefitMapper;
import com.yunya.middletable.dao.discount.OrderBenefitMapper;
import com.yunya.middletable.dao.report.BaseBenefitMapper;
import com.yunya.models.discount.AuthDiscountBenefit;
import com.yunya.models.discount.CardBenefit;
import com.yunya.models.discount.OrderBenefit;
import com.yunya.models.report.BaseBenefit;
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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.yunya.middletable.constant.SynConstant.CUT_SLICE_100;
import static com.yunya.middletable.constant.SynConstant.CUT_SLICE_5000;
import static com.yunya.middletable.enums.BenefitEnum.AUTH_BENEFIT;
import static com.yunya.middletable.enums.BenefitEnum.CARD_BENEFIT;
import static com.yunya.middletable.enums.TrueFalseEnum.FALSE;
import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/10/20
 */
@Slf4j
@Service
public class BaseBenefitServiceNewImpl extends BaseBiz<BaseBenefitMapper, BaseBenefit> {
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Resource
    private CardBenefitMapper cardBenefitMapper;
    @Resource
    private OrderBenefitMapper orderBenefitMapper;
    @Resource
    private AuthDiscountBenefitMapper authBenefitMapper;
    @Resource(name = "customizeThreadPool")
    private ExecutorService cardThreadPool;


    public void pullBenefit(String startDateStr, String endDateStr) throws InterruptedException {
        if (!checkPullDate(startDateStr, endDateStr)) {
            throw new BaseException("结束时间不能小于开始时间", 500);
        }
        //查询源数据
        List<BaseBenefit> originData = getOriginDataByDate(startDateStr, endDateStr);
        if (CollectionUtils.isNotEmpty(originData)) {
            List<Integer> orderIds = originData.stream().map(BaseBenefit::getOrderId).collect(toList());
            List<BaseBenefit> existData = getExistInOrderIds(orderIds);
            //批量删除
            batchDelete(getDeleteBenefit(originData, existData));
            //批量新增
            batchInsert(getAddBenefit(originData, existData));
        }
    }

    /**
     * sync
     */
    private void operateData(Integer orderId) throws InterruptedException {
        OrderBenefit orderBenefit = getOrderBenefit(orderId);
        if (orderBenefit == null) {
            deleteBenefit(orderId);
        } else {
            List<BaseBenefit> baseBenefits = Lists.newArrayList();
            if (CARD_BENEFIT.equals(orderBenefit.getBenefitType())) {
                Set<Integer> key = Sets.newHashSet(orderId);
                List<CardBenefit> originData = getBenefitDetail(key, CardBenefit.class, cardBenefitMapper);
                baseBenefits = cardTransform(originData, Maps.asMap(key, obj->orderBenefit));
            }
            if (AUTH_BENEFIT.equals(orderBenefit.getBenefitType())) {
                Set<Integer> key = Collections.singleton(orderId);
                List<AuthDiscountBenefit> originData = getBenefitDetail(key, AuthDiscountBenefit.class, authBenefitMapper);
                baseBenefits = authTransform(originData, Maps.asMap(key,obj->orderBenefit));
            }
            List<BaseBenefit> existBenefits = getExistByOrderIds(Collections.singletonList(orderId));

            if (CollectionUtils.isNotEmpty(baseBenefits)) {
                if (CollectionUtils.isEmpty(existBenefits)) {
                    mapper.insertList(baseBenefits);
                } else {
                    //批量删除
                    batchDelete(getDeleteBenefit(baseBenefits, existBenefits));
                    //批量新增
                    batchInsert(getAddBenefit(baseBenefits, existBenefits));
                }
            }
        }
    }

    /**
     * 需要新增的订单优惠
     *
     * @param pullList  拉取数据
     * @param existData 已存在基础数据
     * @return list
     */
    private List<BaseBenefit> getAddBenefit(List<BaseBenefit> pullList, List<BaseBenefit> existData) {
        List<BaseBenefit> addBenefits = Lists.newArrayList();
        if (CollectionUtils.isEmpty(existData)) {
            addBenefits = pullList;
        } else {
            Set<Integer> existIds = existData.stream().map(BaseBenefit::getOrderId).collect(toSet());
            addBenefits = pullList.stream().filter(obj -> !existIds.contains(obj.getOrderId())).collect(toList());
        }
        log.info("订单优惠基础表，需要新增的数据[{}]", addBenefits.size());
        return addBenefits;
    }

    /**
     * 需要删除的订单优惠
     *
     * @param pullList  拉取数据
     * @param existData 已存在基础数据
     * @return list
     */
    private List<Integer> getDeleteBenefit(List<BaseBenefit> pullList, List<BaseBenefit> existData) {
        List<Integer> deleteBenefit = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existData)) {
            Map<Integer, Long> existMap = existData.stream().collect(groupingBy(BaseBenefit::getOrderId, counting()));
            Map<Integer, Long> pullMap = pullList.stream().collect(groupingBy(BaseBenefit::getOrderId, counting()));
            existMap.forEach((k, v) -> {
                if (!v.equals(pullMap.get(k))) {
                    deleteBenefit.add(k);
                }
            });
        }
        //移除已经删除的订单优惠
        existData.removeIf((obj) -> deleteBenefit.contains(obj.getOrderId()));
        log.info("订单优惠基础表，需要删除的数据[{}]", deleteBenefit.size());
        return deleteBenefit;
    }

    private void deleteBenefit(Integer orderId) {
        Example example = new Example(BaseBenefit.class);
        example.createCriteria().andEqualTo("orderId", orderId);
        mapper.deleteByExample(example);
    }

    /**
     * 批量插入基础数据
     *
     * @param list 原数据集合
     */
    private void batchInsert(List<BaseBenefit> list) throws InterruptedException {
        if (CollectionUtils.isNotEmpty(list)) {
            List<List<BaseBenefit>> partition = Lists.partition(list, CUT_SLICE_100);
            CountDownLatch downLatch = new CountDownLatch(partition.size());
            for (List<BaseBenefit> baseBenefits : partition) {
                //多线程异步插入
                cardThreadPool.execute(() -> {
                    try {
                        mapper.insertList(baseBenefits);
                        downLatch.countDown();
                    } catch (Exception e) {
                        downLatch.countDown();
                        log.error("pull benefit batchInsert error", e);
                    }
                });
            }
            downLatch.await();
        }
    }

    /**
     * 批量删除
     *
     * @param list 删除ids
     */
    private void batchDelete(List<Integer> list) throws InterruptedException {
        if (CollectionUtils.isNotEmpty(list)) {
            List<List<Integer>> partition = Lists.partition(list, CUT_SLICE_5000);
            CountDownLatch downLatch = new CountDownLatch(partition.size());
            for (List<Integer> ids : partition) {
                //多线程异步插入
                cardThreadPool.execute(() -> {
                    try {
                        Example example = new Example(BaseBenefit.class);
                        example.createCriteria().andEqualTo("orderId", ids);
                        mapper.deleteByExample(example);
                        downLatch.countDown();
                    } catch (Exception e) {
                        downLatch.countDown();
                        log.error("pull benefit batchInsert error", e);
                    }
                });
            }
            downLatch.await();
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new HashMap<>(16);
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 通过时间段查询原始数据
     *
     * @param startDateStr 开始时间
     * @param endDateStr   结束时间
     * @return list
     */
    private List<BaseBenefit> getOriginDataByDate(String startDateStr, String endDateStr) {
        //查询订单总信息
        List<OrderBenefit> orderBenefits = getOrderBenefitByDate(startDateStr, endDateStr);
        List<BaseBenefit> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(orderBenefits)) {
            //查询订单优惠汇总信息
            Map<Integer, Map<Integer, OrderBenefit>> orderBenefitMap = orderBenefits.stream().filter(distinctByKey(OrderBenefit::getOrderId))
                    .collect(groupingBy(obj -> obj.getBenefitType().intValue(), Collectors.toMap(OrderBenefit::getOrderId, Function.identity())));
            orderBenefitMap.forEach((k, map) -> {
                //产品优惠
                if (CARD_BENEFIT.equals(k)) {
                    List<CardBenefit> cardBenefits = getBenefitDetail(map.keySet(), CardBenefit.class, cardBenefitMapper);
                    //卡券优惠转换
                    List<BaseBenefit> templateList = cardTransform(cardBenefits, map);
                    if (CollectionUtils.isNotEmpty(templateList)) {
                        list.addAll(templateList);
                    }
                }
                //授权折扣优惠
                if (AUTH_BENEFIT.equals(k)) {
                    List<AuthDiscountBenefit> authBenefits = getBenefitDetail(map.keySet(), AuthDiscountBenefit.class, authBenefitMapper);
                    if (CollectionUtils.isNotEmpty(authBenefits)) {
                        //授权优惠转换
                        List<BaseBenefit> templateList = authTransform(authBenefits, map);
                        list.addAll(templateList);
                    }
                }
            });
        }
        return list;
    }

    private void setOperateUserId(List<AuthDiscountBenefit> authBenefits, Set<Integer> v) {
        List<OrderBenefit> authOrderBenefit = getBenefitDetail(v, OrderBenefit.class, orderBenefitMapper);
        Map<Integer, Integer> collect = authOrderBenefit.stream().collect(toMap(OrderBenefit::getOrderId, OrderBenefit::getCrtId));
        authBenefits.forEach(obj -> obj.setCrtId(collect.get(obj.getOrderId())));
    }

    private List<BaseBenefit> cardTransform(List<CardBenefit> cardBenefits, Map<Integer, OrderBenefit> map) {
        return cardBenefits.stream().map(obj -> {
            BaseBenefit benefit = BeanCopierUtils.generalCopyBean(obj, BaseBenefit.class, getBenefitConvert());
            benefit.setItemType(obj.getItemType().byteValue());
            benefit.setChoiceBenefitType(CARD_BENEFIT.getCode());
            benefit.setOperateUserId(obj.getCrtId());
            benefit.setUseDate(obj.getCrtTime());
            setOrderBenefit(benefit, map);
            return benefit;
        }).collect(toList());
    }

    private List<BaseBenefit> authTransform(List<AuthDiscountBenefit> authBenefits, Map<Integer, OrderBenefit> map) {
        return authBenefits.stream().map(obj -> {
            BaseBenefit benefit = BeanCopierUtils.generalCopyBean(obj, BaseBenefit.class, getBenefitConvert());
            benefit.setItemType(obj.getItemType().byteValue());
            benefit.setChoiceBenefitType(AUTH_BENEFIT.getCode());
            benefit.setOperateUserId(obj.getCrtId());
            benefit.setUseDate(obj.getCrtTime());
            setOrderBenefit(benefit, map);
            return benefit;
        }).collect(toList());
    }

    /**
     * 设置OrderBenefit的数据
     *
     * @param benefit
     * @param map
     */
    private void setOrderBenefit(BaseBenefit benefit, Map<Integer, OrderBenefit> map) {
        OrderBenefit orderBenefit = map.get(benefit.getOrderId());
        if (orderBenefit != null) {
            benefit.setAuthorizedId(orderBenefit.getAuthorizedId());
            benefit.setRemark(orderBenefit.getRemark());
        }
    }

    private Converter getBenefitConvert() {
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

    private List<OrderBenefit> getOrderBenefitByDate(String startDateStr, String endDateStr) {
        Example example = new Example(OrderBenefit.class);
        example.createCriteria().andGreaterThanOrEqualTo("updTime", startDateStr)
                .andLessThan("updTime", endDateStr)
                .andEqualTo("deleted", FALSE.getCode());
        return orderBenefitMapper.selectByExample(example);
    }

    private <T> List<T> getBenefitDetail(Set<Integer> orderIds, Class<?> clazz, Mapper<T> mapper) {
        Example example = new Example(clazz);
        example.createCriteria().andIn("orderId", orderIds)
                .andEqualTo("deleted", FALSE.getCode());
        return mapper.selectByExample(example);
    }

    private OrderBenefit getOrderBenefit(Integer orderId) {
        Example example = new Example(OrderBenefit.class);
        example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("deleted", FALSE.getCode());
        return orderBenefitMapper.selectOneByExample(example);
    }

    private List<BaseBenefit> getExistInOrderIds(List<Integer> orderIds) throws InterruptedException {
        List<BaseBenefit> result = new CopyOnWriteArrayList<>();
        List<List<Integer>> partition = Lists.partition(orderIds, CUT_SLICE_5000);
        CountDownLatch downLatch = new CountDownLatch(partition.size());
        for (List<Integer> ids : partition) {
            //多线程异步插入
            cardThreadPool.execute(() -> {
                try {
                    downLatch.countDown();
                    result.addAll(getExistByOrderIds(ids));
                } catch (Exception e) {
                    downLatch.countDown();
                    log.error("pull benefit batchInsert error", e);
                }
            });
        }
        downLatch.await();
        return result;
    }

    private List<BaseBenefit> getExistByOrderIds(List<Integer> orderIds) {
        Example example = new Example(BaseBenefit.class);
        example.createCriteria().andIn("orderId", orderIds);
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
}
