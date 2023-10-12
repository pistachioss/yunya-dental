package com.yunya.middletable.service.treatment_other;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseQcylTreatmentMapper;
import com.yunya.middletable.dao.treatment_other.QcTreatmentRecordMapper;
import com.yunya.models.report.BaseQcylTreatment;
import com.yunya.models.report.BaseVisitRemind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.yunya.middletable.constant.SynConstant.CUT_SLICE_100;

/**
 * @author: chenlin
 * @date: 2023/9/26 9:32
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class BaseQcylTreatmentBiz extends BaseBiz<BaseQcylTreatmentMapper, BaseQcylTreatment> {

    @Resource
    private QcTreatmentRecordMapper qcTreatmentRecordMapper;
    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    public void operateData(MessageModel model) {
        Integer qcTreatmentId = (Integer) model.getParamMap().get("id");
        BaseQcylTreatment data = generateData(qcTreatmentId);
        Integer operateType = model.getOperateType();
        switch (operateType) {
            case 0:
            case 2:
            case 1:
                mapper.deleteByPrimaryKey(qcTreatmentId);
                if (StringHelper.isNotNull(data)) {
                    mapper.insertSelective(data);
                }
                break;
            default:
        }
    }

    private BaseQcylTreatment generateData(Integer qcTreatmentId) {
        return qcTreatmentRecordMapper.selectQcylTreatmentRecordList(qcTreatmentId, null, null).get(0);
    }

    public void pullData(PullForm form) throws InterruptedException {
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        List<BaseQcylTreatment> treatments = qcTreatmentRecordMapper.selectQcylTreatmentRecordList(null, startDate, endDate);
        if (StringHelper.isNotEmpty(treatments)) {
            insertList(treatments);
        }
    }

    public void insertList(List<BaseQcylTreatment> treatments) throws InterruptedException {
        List<List<BaseQcylTreatment>> list = Lists.partition(treatments, CUT_SLICE_100);
        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        long start = System.currentTimeMillis();
        for (List<BaseQcylTreatment>  datas : list) {
            importExcelThreadPool.execute(() -> {
                try {
                    datas.forEach(data->{
                        mapper.deleteByPrimaryKey(data.getQcTreatmentId());
                        mapper.insertSelective(data);
                    });
                } catch (Exception e) {
                    log.info("全程医疗就诊记录批量拉取异常",e);
                    e.printStackTrace();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        log.info("全程医疗就诊记录批量拉取完成，耗时：[{}]秒", (end - start) / 1000);
    }
}
