package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.Card;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CardMapper extends Mapper<Card> {
    /**
     * 批量添加
     *
     * @param list
     */
    void batchInsert(List<Card> list);

    /**
     * 修改售出相关内容
     *
     * @param cards
     */
    void updateSaleStatusById(List<Card> cards);
}