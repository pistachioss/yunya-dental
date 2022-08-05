package com.yunya.feign.discount.domain.form;

import lombok.Data;

import java.util.List;

/**
 * @author xiangyang
 * @date 2020/8/26
 */
@Data
public class BatchCancelCardForm {
    private List<Integer> cardIds;
    private Integer orgId;
    private Integer loginUserId;

}
