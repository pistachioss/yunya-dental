package com.yunya.middletable.service.treatment_other;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseReturnVisitMapper;
import com.yunya.middletable.dao.treatment_other.ReturnVisitRecordMapper;
import com.yunya.models.report.BaseReturnVisit;
import com.yunya.models.treatment_other.ReturnVisitRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: chenlin
 * @date: 2022/9/26 17:23
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseReturnVisitBiz extends BaseBiz<BaseReturnVisitMapper, BaseReturnVisit> {
    @Autowired
    private ReturnVisitRecordMapper returnVisitRecordMapper;

    /**
     * 随访提醒中间表-操作
     *
     * @param msg 消息
     */
    public void operate(MessageModel msg) {
        Integer operateType = msg.getOperateType();
        Integer id = (Integer) msg.getParamMap().get("id");
        Integer type = (Integer) msg.getParamMap().get("type");
        switch (operateType) {
            case 0:
                BaseReturnVisit insertEntity = getBaseReturnVisitInfo(id);
                if (StringHelper.isNotNull(insertEntity)) {
                    mapper.deleteByPrimaryKey(insertEntity);
                    mapper.insertSelective(insertEntity);
                }
                break;
            case 1:
                BaseReturnVisit updateEntity = getBaseReturnVisitInfo(id);
                mapper.updateByPrimaryKeySelective(updateEntity);
                break;
            case 2:
                BaseReturnVisit query = new BaseReturnVisit();
                query.setId(id);
                mapper.deleteByPrimaryKey(query);
                break;
            default:
                break;
        }
    }

    private BaseReturnVisit getBaseReturnVisitInfo(Integer id) {
        ReturnVisitRecord returnVisit = returnVisitRecordMapper.selectByPrimaryKey(id);
        if (StringHelper.isNotNull(returnVisit)) {
            BaseReturnVisit baseReturnVisit = new BaseReturnVisit();
            BeanUtil.copyProperties(returnVisit, baseReturnVisit);
            return baseReturnVisit;
        }
        return null;
    }
}
