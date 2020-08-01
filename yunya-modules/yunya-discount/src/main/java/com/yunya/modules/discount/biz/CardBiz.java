package com.yunya.modules.discount.biz;

import com.yunya.models.discount.Card;
import com.yunya.modules.discount.form.CardSaleForm;
import com.yunya.modules.discount.mapper.CardMapper;
import com.yunya.framework.common.biz.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-10 14:21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CardBiz extends BaseBiz<CardMapper, Card> {

    /**
     * 批量新增
     *
     * @param cards
     */
    public void batchInsert(List<Card> cards) {
        mapper.batchInsert(cards);
    }

    /**
     * 售出
     *
     * @param cardSaleForm
     */
    public void sale(CardSaleForm cardSaleForm) {
        Integer saleStatus = 1;

        List<Integer> cardIds = cardSaleForm.getCardIds();
        if (cardIds.isEmpty()) {
            return;
        }
        String buyerName = cardSaleForm.getBuyerName();
        String buyerPhone = cardSaleForm.getBuyerPhone();
        Integer accountingId = cardSaleForm.getAccountingId();
        Boolean smsType = cardSaleForm.getSmsType();
        Integer sellingType = cardSaleForm.getSellingType();
        Boolean charge = cardSaleForm.getCharge();
        List<Card> cards = new ArrayList<>();
        for (Integer cardId : cardIds) {
            Card card = new Card();
            card.setId(cardId);
            card.setBuyerName(buyerName);
            card.setBuyerPhone(buyerPhone);
            card.setAccountingId(accountingId);
            card.setSmsType(smsType);
            card.setSellingType(sellingType);
            card.setCharge(charge);
            card.setStatus(saleStatus);
            cards.add(card);
        }
        // 批量修改状态
        mapper.updateSaleStatusById(cards);
    }
}
