package com.yunya.modules.discount.biz;

import com.yunya.models.discount.CardClinic;
import com.yunya.models.discount.CardStatistics;
import com.yunya.modules.discount.form.CardClinicForm;
import com.yunya.modules.discount.form.CardDistributionForm;
import com.yunya.modules.discount.mapper.CardClinicMapper;
import com.yunya.modules.discount.vo.CardStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.modules.discount.constant.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-10 14:34
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CardClinicBiz extends BaseBiz<CardClinicMapper, CardClinic> {
    // 状态
    private static final Integer FINISH = 1;
    private static final Integer PLAN = 0;

    @Autowired
    private CardStatisticsBiz cardStatisticsBiz;
    @Autowired
    private CardBiz cardBiz;

    /**
     * 配给计划
     *
     * @param cardDistributionForm
     */
    public void plan(CardDistributionForm cardDistributionForm) {
        Integer relevanceId = cardDistributionForm.getRelevanceId();
        Integer type = cardDistributionForm.getType();
        int planCount = 0;

        List<CardDistributionForm.Node> nodes = cardDistributionForm.getNodes();
        // 段号头
        String header = "";
        if (type == 0) {
            header = "D";
        } else if (type == 1) {
            header = "Z";
        } else if (type == 2) {
            header = "T";
        } else if (type == 3) {
            header = "C";
        }
        if (nodes != null && (!nodes.isEmpty())) {
            // 批次,记录时第几次生成的配给计划
            Integer revision = 1;
            List<CardClinic> cardClinics = new ArrayList<>();
            CardClinic data = new CardClinic();
            data.setRelevanceId(relevanceId);
            data.setType(type);
            List<CardClinic> list = selectList(data);
            if (null != list && (!list.isEmpty())) {
                revision = list.get(0).getRevision() + 1;
            }

            //  生成配给计划
            for (CardDistributionForm.Node node : nodes) {
                planCount += node.getCount();
                CardClinic cardClinic = new CardClinic();
                cardClinic.setRelevanceId(relevanceId);
                cardClinic.setType(type);
                cardClinic.setStatus(PLAN);
                cardClinic.setClinicId(node.getClinicId());
                cardClinic.setCount(node.getCount());
                cardClinic.setOrgType(node.getOrgType());
                cardClinic.setRevision(revision);
                cardClinics.add(cardClinic);
            }
            mapper.batchInsert(cardClinics);

            // 统计表相关数据生成
            CardStatistics cardStatistics = new CardStatistics();
            cardStatistics.setType(type);
            cardStatistics.setRelevanceId(relevanceId);
            cardStatistics.setRelevanceName(cardDistributionForm.getRelevanceName());
            cardStatistics.setRevision(revision);
            cardStatistics.setCount(planCount);
            cardStatisticsBiz.insertSelective(cardStatistics);
        }
    }

    public void updatePlan(CardDistributionForm cardDistributionForm) {
        Integer relevanceId = cardDistributionForm.getRelevanceId();
        Integer type = cardDistributionForm.getType();

        // 只要分配完成了,就不再允许修改
        CardClinic data = new CardClinic();
        data.setRelevanceId(relevanceId);
        data.setType(type);
        data.setStatus(FINISH);
        if (!mapper.select(data).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法修改配给计划", ExceptionCode.CARD_EXIST);
        }

        // 删除原有配给计划
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(relevanceId);
        cardClinic.setRevision(cardDistributionForm.getRevision());
        cardClinic.setType(type);
        cardClinic.setStatus(PLAN);
        delete(cardClinic);

        // 删除原有批次统级内容
        CardStatistics cardStatistics = new CardStatistics();
        cardStatistics.setType(type);
        cardStatistics.setRelevanceId(relevanceId);
        cardStatistics.setRevision(cardDistributionForm.getRevision());
        cardStatisticsBiz.delete(cardStatistics);

        // 重新分配
        plan(cardDistributionForm);
    }

    /**
     * 分配查询列表
     *
     * @param cardClinicForm
     */
    public List<CardStatisticsVO> search(CardClinicForm cardClinicForm) {
        return cardStatisticsBiz.getVOByNameAndTypes(cardClinicForm.getName(), cardClinicForm.getTypes());
    }

    /**
     * 分配
     *
     * @param type
     * @param relevanceId
     * @param revision
     */
    public void rationing(Integer type, Integer relevanceId, Integer revision) {
        // card统计内容修改
        CardStatistics cardStatistics = new CardStatistics();
        cardStatistics.setType(type);
        cardStatistics.setRelevanceId(relevanceId);
        cardStatistics.setRevision(revision);
        CardStatistics data = cardStatisticsBiz.selectOne(cardStatistics);
        if (data == null) {
            throw new BaseException("该批次的卡券不存在配给计划", DATA_NOT_EXIST);
        }

        if (data.getStatus() == FINISH) {
            throw new BaseException("该批次的卡券已完成分配，无法再次分配", ExceptionCode.CARD_RATIONING);
        }
        // 计划配给数量
        int planCount = 0;
        Integer sumCount = mapper.selectSumCount(type, relevanceId);
        if (sumCount == null) {
            sumCount = 0;
        }

        CardClinic cardClinic = new CardClinic();
        cardClinic.setRevision(revision);
        cardClinic.setType(type);
        cardClinic.setRelevanceId(relevanceId);
        List<CardClinic> nodes = mapper.select(cardClinic);
        String header = "";
        if (type == 0) {
            header = "D";
        } else if (type == 1) {
            header = "Z";
        } else if (type == 2) {
            header = "T";
        } else if (type == 3) {
            header = "C";
        }

        List<CardClinic> cardClinics = new ArrayList<>();
        for (CardClinic node : nodes) {
            sumCount = sumCount + 1;
            planCount += node.getCount();
            // 段号区间
            String startNumber = cardNumber(sumCount + "");
            int count = node.getCount() + sumCount - 1;
            String endNumber = cardNumber(count + "");
            CardClinic item = new CardClinic();
            item.setStartNumber(header + relevanceId + startNumber);
            item.setEndNumber(header + relevanceId + endNumber);
            item.setId(node.getId());
            item.setStatus(FINISH);
            cardClinics.add(item);
            sumCount = sumCount + node.getCount() - 1;
        }
        mapper.batchUpdateNumber(cardClinics);

        // 修改统计
        data.setStatus(FINISH);
        data.setRationingDate(new Date());
        data.setExecutorName(BaseContextHandler.getName());
        cardStatisticsBiz.updateSelectiveById(data);

        // 生成卡号,卡密
        saveCard(type, relevanceId, revision);
    }

    /**
     * 获取优惠活动计划配给列表
     *
     * @param relevanceId
     * @return
     */
    public List<CardStatistics> getPlanList(Integer type, Integer relevanceId) {
        CardStatistics cardStatistics = new CardStatistics();
        cardStatistics.setType(type);
        cardStatistics.setRelevanceId(relevanceId);
        return cardStatisticsBiz.selectList(cardStatistics);
    }

    /**
     * 生成卡号卡密
     *
     * @param type
     * @param relevanceId
     * @param revision
     */
    public void saveCard(Integer type, Integer relevanceId, Integer revision) {
//        CardClinic cardClinic = new CardClinic();
//        cardClinic.setType(type);
//        cardClinic.setRelevanceId(relevanceId);
//        cardClinic.setRevision(revision);
//        List<CardClinic> cardClinics = selectList(cardClinic);
//        cardClinics.forEach(z -> {
//            int count = z.getCount() + 1;
//            StringBuffer stringBuffer = new StringBuffer(z.getStartNumber());
//            int length = stringBuffer.length();
//            List<Card> cards = new ArrayList<>();
//            for (int i = 1; i < count; i++) {
//                Card card = new Card();
//                card.setCardType(type);
//                card.setRelevanceId(relevanceId);
//                // 截取末尾6位数
//                String startNo = stringBuffer.substring(0, length - 5);
//                Integer num = Integer.valueOf(stringBuffer.substring(length - 6, length)) + i - 1;
//                String cardNum = cardNumber(num + "");
//                card.setCardNumber(startNo + cardNum);
//                card.setPassword(randomNumber());
//                card.setClinicId(z.getClinicId());
//                cards.add(card);
//            }
//            cardBiz.batchInsert(cards);
//        });
    }

    /**
     * 生成段号
     *
     * @param num
     * @return
     */
    private String cardNumber(String num) {
        int z = Integer.valueOf(num);
        StringBuffer buffer = new StringBuffer();
        buffer.append(z);
        String zero = String.valueOf(0);
        int startLength = buffer.length();
        if (startLength < 6) {
            int i = 6 - startLength;
            for (int j = 0; j < i; j++) {
                buffer.insert(0, zero);
            }
        }
        return buffer.toString();
    }

    /**
     * 生成随机6位数密码
     *
     * @return
     */
    private String randomNumber() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
