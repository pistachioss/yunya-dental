package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.VisitingRemind;
import lombok.Data;

import java.util.List;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class FindAllRemindRecordVO {
    /**
     * 随访集合
     */
    private List<VisitingRecord> recordList;
    /**
     * 提醒集合
     */
    private List<VisitingRemind> remindList;
}
