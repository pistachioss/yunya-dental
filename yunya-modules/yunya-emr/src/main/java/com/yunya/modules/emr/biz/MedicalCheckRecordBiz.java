package com.yunya.modules.emr.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.model.MedicalCheckRecordModel;
import com.yunya.feign.emr.domain.query.MedicalCheckRecordQuery;
import com.yunya.feign.emr.domain.vo.MedicalCheckRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.emr.MedicalCheckHistory;
import com.yunya.models.emr.MedicalCheckRecord;
import com.yunya.modules.emr.mapper.MedicalCheckHistoryMapper;
import com.yunya.modules.emr.mapper.MedicalCheckRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 简介：牙位检查记录业务层
 *
 * @author: chenlin
 * @Description: 牙位检查记录业务层
 * @Date: 2022/1/10 10:02
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MedicalCheckRecordBiz extends BaseBiz<MedicalCheckRecordMapper, MedicalCheckRecord> {
    /** 牙位检查历史记录 */
    @Autowired
    private MedicalCheckHistoryMapper medicalCheckHistoryMapper;

    /**
     * 保存牙位检查记录列表
     *
     * @param medicalId
     * @param checkRecords
     */
    public void saveCheckRecord(Integer medicalId, List<MedicalCheckRecordModel> checkRecords) {
        MedicalCheckRecord query = new MedicalCheckRecord();
        query.setMedicalRecordId(medicalId);
        // 已删除的记录
        List<MedicalCheckRecord> deleted = mapper.select(query);
        Iterator<MedicalCheckRecord> it = deleted.iterator();
        mapper.delete(query);
        List<MedicalCheckHistory> histories = new ArrayList<>();
        if (StringHelper.isNotEmpty(checkRecords)) {
            if (StringHelper.isNotEmpty(deleted)) {
                for (int i = 0; i < checkRecords.size(); i++) {
                    MedicalCheckRecordModel vo = checkRecords.get(i);
                    Integer id = vo.getRecordId();
                    if (!ObjectUtils.isEmpty(id)) {
                        while (it.hasNext()) {// 移除存在的
                            MedicalCheckRecord entity = it.next();
                            if (entity.getId().equals(id)) {
                                vo.setCrtId(entity.getCrtId());
                                vo.setCrtTime(entity.getCrtTime());
                                it.remove();
                                break;
                            }
                        }
                    }
                }
            }

            Date now = new Date(System.currentTimeMillis());
            Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
            checkRecords.forEach(vo -> {
                MedicalCheckRecord entity = model2Entity(vo, medicalId, userId, now);
                mapper.insert(entity);
                histories.add(entity2History(entity, (byte) (ObjectUtils.isEmpty(vo.getRecordId())?0:1)));
            });
        }
        if (StringHelper.isNotEmpty(deleted)) {
            deleted.forEach(vo-> histories.add(entity2History(vo, (byte) 2)));
        }
        if (StringHelper.isNotEmpty(histories)) {
            medicalCheckHistoryMapper.insertBatch(histories);
        }
    }

    /**
     * 数据模型转换
     *
     * @param vo
     * @param medicalId
     * @param userId
     * @param now
     * @return
     */
    private MedicalCheckRecord model2Entity(MedicalCheckRecordModel vo, Integer medicalId, Integer userId, Date now) {
        MedicalCheckRecord entity = new MedicalCheckRecord();
        entity.setId(vo.getRecordId());
        entity.setMedicalRecordId(medicalId);
        entity.setToothPosition(vo.getToothPosition());
        entity.setSymptomId(vo.getSymptomId());
        entity.setCheckId(vo.getCheckId());
        entity.setRemark(vo.getRemark());
        Integer crtId = vo.getCrtId();
        if (ObjectUtils.isEmpty(crtId)) {
            crtId = userId;
        }
        Date crtTime = vo.getCrtTime();
        if (ObjectUtils.isEmpty(crtTime)) {
            crtTime = now;
        }
        entity.setCrtId(crtId);
        entity.setCrtTime(crtTime);
        entity.setUptId(userId);
        entity.setUptTime(now);
        return entity;
    }

    /**
     * 检查记录实体转换成历史记录
     *
     * @param entity
     * @param operation
     * @return
     */
    private MedicalCheckHistory entity2History(MedicalCheckRecord entity, Byte operation) {
        MedicalCheckHistory history = new MedicalCheckHistory();
        history.setOperation(operation);
        history.setMedicalCheckRecordId(entity.getId());
        history.setMedicalRecordId(entity.getMedicalRecordId());
        history.setToothPosition(entity.getToothPosition());
        history.setSymptomId(entity.getSymptomId());
        history.setCheckId(entity.getCheckId());
        history.setCrtId(entity.getUptId());
        history.setCrtTime(entity.getUptTime());
        history.setRemark(entity.getRemark());
        return history;
    }

    /**
     * 条件查询检查记录列表
     *
     * @param query
     * @return
     */
    public PageInfo<MedicalCheckRecordVO> findMedicalCheckRecordList(MedicalCheckRecordQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<MedicalCheckRecordVO> list = mapper.selectMedicalCheckRecordList(query);
        return new PageInfo<>(list);
    }
}
