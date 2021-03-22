package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.yunya.middletable.constant.SynConstant.*;
import static com.yunya.middletable.enums.BenefitEnum.*;
import static com.yunya.middletable.enums.TrueFalseEnum.*;
import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/10/20
 */
@Slf4j
@Service
public class BaseBenefitServiceImpl extends BaseBiz<BaseBenefitMapper, BaseBenefit> {
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Resource
    private CardBenefitMapper cardBenefitMapper;
    @Resource
    private OrderBenefitMapper orderBenefitMapper;
    @Resource
    private AuthDiscountBenefitMapper authBenefitMapper;
    @Resource(name = "customizeThreadPool")
    private ExecutorService cardThreadPool;

    public void operateBaseBenefit(MessageModel model) throws InterruptedException {
        Integer orderId = (Integer) model.getParamMap().get("id");
//		Integer operateType = model.getOperateType();
        operateData(orderId);
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
                List<CardBenefit> originData = getBenefitDetail(Sets.newHashSet(orderId), CardBenefit.class, cardBenefitMapper);
                baseBenefits = cardTransform(originData, orderBenefit.getRemark());
            }
            if (AUTH_BENEFIT.equals(orderBenefit.getBenefitType())) {
                List<AuthDiscountBenefit> originData = getBenefitDetail(Collections.singleton(orderId), AuthDiscountBenefit.class, authBenefitMapper);
                baseBenefits = authTransform(originData, orderBenefit.getRemark());
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
                        log.error("pull benefit batchInsert error",e);
                    }
                });
            }
            downLatch.await();
        }
    }

    /**
     * 批量删除
     *
     * @param ids 删除ids
     */
    private void batchDelete(List<Integer> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            Example example = new Example(BaseBenefit.class);
            example.createCriteria().andEqualTo("orderId", ids);
            mapper.deleteByExample(example);
        }
    }

    private List<BaseBenefit> cardTransform(List<CardBenefit> cardBenefits, String remark) {
        return cardBenefits.stream().map(obj -> {
            BaseBenefit benefit = BeanCopierUtils.generalCopyBean(obj, BaseBenefit.class, getBenefitConvert());
            benefit.setItemType(obj.getItemType().byteValue());
            benefit.setChoiceBenefitType(CARD_BENEFIT.getCode());
            benefit.setOperateUserId(obj.getCrtId());
            benefit.setUseDate(obj.getCrtTime());
            benefit.setRemark(remark);
            return benefit;
        }).collect(toList());
    }

    private List<BaseBenefit> authTransform(List<AuthDiscountBenefit> authBenefits, String remark) {
        return authBenefits.stream().map(obj -> {
            BaseBenefit benefit = BeanCopierUtils.generalCopyBean(obj, BaseBenefit.class, getBenefitConvert());
            benefit.setItemType(obj.getItemType().byteValue());
            benefit.setChoiceBenefitType(AUTH_BENEFIT.getCode());
            benefit.setOperateUserId(obj.getCrtId());
            benefit.setUseDate(obj.getCrtTime());
            benefit.setRemark(remark);
            return benefit;
        }).collect(toList());
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

    private List<BaseBenefit> getExistByOrderIds(List<Integer> orderIds) {
        Example example = new Example(BaseBenefit.class);
        example.createCriteria().andIn("orderId", orderIds);
        return mapper.selectByExample(example);
    }

}
