package com.yunya.modules.employeeattend.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.employee_attend.EmployeePushMessageRecord;
import com.yunya.modules.employeeattend.form.EmployeePushForm;
import com.yunya.modules.employeeattend.mapper.EmployeePushMessageRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/4/2 9:41
 * @since: 1.0.0
 */
@Service
public class EmployeePushMessageRecordBiz extends BaseBiz<EmployeePushMessageRecordMapper, EmployeePushMessageRecord> {
    @Resource(name = "poolExecutor")
    private ExecutorService executorService;

    public void crtMsgRecord(EmployeePushForm form, Integer pushType) {
        if (!ObjectUtils.isEmpty(form)) {
            executorService.submit(()->{
                Date now = new Date(System.currentTimeMillis());
                List<Integer> ids = form.getIds();
                if (StringHelper.isNotEmpty(ids)) {
                    ids.forEach(id->{
                        EmployeePushMessageRecord entity = new EmployeePushMessageRecord();
                        entity.setSourceId(id);
                        entity.setPushType(pushType);
                        entity.setContent(form.getContent());
                        entity.setTitle(form.getTitle());
                        entity.setHadRead(false);
                        entity.setPlatform(form.getPlatform());
                        entity.setCrtId(form.getOptId());
                        entity.setCrtTime(now);
                        entity.setUptId(form.getOptId());
                        entity.setUptTime(now);
                        mapper.insertSelective(entity);
                    });
                }
            });
        }
    }
}
