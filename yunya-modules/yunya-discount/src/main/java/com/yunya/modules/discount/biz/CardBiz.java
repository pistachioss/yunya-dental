package com.yunya.modules.discount.biz;

import com.yunya.framework.common.biz.*;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.mapper.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-10 14:21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CardBiz extends BaseBiz<CardMapper, Card> {




    private void calculateNumber(int startIndex, int count) {

    }
}
