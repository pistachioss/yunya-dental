package com.yunya.feign.discount.valid;

import com.yunya.feign.discount.domain.form.*;
import lombok.extern.slf4j.*;
import org.hibernate.validator.spi.group.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/26
 */
@Slf4j
public class CardSoldGroupSequenceProvider implements DefaultGroupSequenceProvider<CardSoldForm> {
    @Override
    public List<Class<?>> getValidationGroups(CardSoldForm cardSoldForm) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(CardSoldForm.class);
        if (cardSoldForm != null) {
            Integer soldType = cardSoldForm.getSoldType();
            Integer soldAndPay = cardSoldForm.getSoldAndPay();
            log.info("售出类型为type：[{}]执行校验", soldType);
            if (soldType == 0) {
                if (new Integer(1).equals(soldAndPay)) {
                    defaultGroupSequence.add(CardSoldForm.PayViewGroup.class);
                } else {
                    defaultGroupSequence.add(CardSoldForm.PayNotViewGroup.class);
                }
                defaultGroupSequence.add(CardSoldForm.SaleTypeViewGroup.class);
            } else {
                defaultGroupSequence.add(CardSoldForm.SaleTypeNotViewsGroup.class);
                defaultGroupSequence.add(CardSoldForm.PayNotViewGroup.class);
            }
        }
        return defaultGroupSequence;
    }
}
