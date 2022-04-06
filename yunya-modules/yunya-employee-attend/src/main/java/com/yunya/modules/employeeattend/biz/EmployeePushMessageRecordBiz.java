package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.EmployeePushMessageRecordQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeePushMessageRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.MessagePushTypeEnum;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.employee_attend.EmployeePush;
import com.yunya.models.employee_attend.EmployeePushMessageRecord;
import com.yunya.modules.employeeattend.form.EmployeePushForm;
import com.yunya.modules.employeeattend.mapper.EmployeePushMessageRecordMapper;
import com.yunya.modules.employeeattend.util.JpushManager;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.yunya.framework.common.enums.MessagePushTypeEnum.*;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/4/2 9:41
 * @since: 1.0.0
 */
@Slf4j
@Service
public class EmployeePushMessageRecordBiz extends BaseBiz<EmployeePushMessageRecordMapper, EmployeePushMessageRecord> {
    @Resource(name = "poolExecutor")
    private ExecutorService executorService;
    @Autowired
    private EmployeePushBiz employeePushBiz;

    public void crtMsgRecord(EmployeePushForm form, Integer pushType) {
        if (!ObjectUtils.isEmpty(form)) {
            executorService.submit(()->{
                Date now = new Date(System.currentTimeMillis());
                List<Integer> ids = form.getIds();
                List<String> userDevices = form.getUserList();
                if (StringHelper.isNotEmpty(ids)) {
                    for (int i = 0; i < ids.size(); i++) {
                        String regId = userDevices.get(i);
                        EmployeePush push = employeePushBiz.findOneByRegId(regId);
                        if (ObjectUtils.isEmpty(push)) {
                            log.error("JPush device regId:{} was not binding!", regId);
                            continue;
                        }
                        EmployeePushMessageRecord entity = new EmployeePushMessageRecord();
                        entity.setSourceId(ids.get(i));
                        entity.setUserId(push.getEmployeeId());
                        entity.setPushType(pushType);
                        entity.setContent(form.getContent());
                        entity.setTitle(form.getTitle());
                        entity.setPlatform(form.getPlatform());
                        entity.setHadRead(false);
                        if (form.getIsSchedule()) {
                            String scheTime = form.getScheTime();
                            try {
                                Date pushTime = DateUtil.parse(scheTime, "yyyy-MM-dd HH:mm:ss");
                                entity.setPushTime(pushTime);
                            } catch (ParseException e) {
                                log.error("push message parse scheTime error: ", e);
                            }
                        } else {
                            entity.setPushTime(now);
                        }
                        entity.setCrtId(form.getOptId());
                        entity.setCrtTime(now);
                        entity.setUptId(form.getOptId());
                        entity.setUptTime(now);
                        mapper.insertSelective(entity);
                    }
                }
            });
        }
    }

    public void pushMessage(EmployeePushForm employeePushForm, MessagePushTypeEnum pushType) {
        JpushManager jPush = JpushManager.getInstance();
        Integer code = pushType.getCode();
        Boolean pushSuccess = false;
        if (pushType.equals(ATTENDANCE_PUNCH_HINT.getCode())) {
            // 考勤打卡
            pushSuccess = jPush.pushAttend(employeePushForm, code);
        } else if (pushType.inEnums(LEAVE_APPROVE_APPLY, WORKOVER_APPROVE_APPLY, FIELD_APPROVE_APPLY)) {
            // 审批申请
            pushSuccess = jPush.pushLeaveApproval(employeePushForm, code);
        } else if (pushType.inEnums(LEAVE_APPROVE_COPY, WORKOVER_APPROVE_COPY, FIELD_APPROVE_COPY)) {
            // 审批抄送
            pushSuccess = jPush.pushLeaveCope(employeePushForm, code);
        } else if (pushType.inEnums(LEAVE_APPROVE_PASS, WORKOVER_APPROVE_PASS, FIELD_APPROVE_PASS)) {
            // 审批通过
            pushSuccess = jPush.pushLeaveYes(employeePushForm, code);
        } else if (pushType.inEnums(LEAVE_APPROVE_UNPASS, WORKOVER_APPROVE_UNPASS, FIELD_APPROVE_UNPASS)) {
            // 审核未通过
            pushSuccess = jPush.pushLeaveNo(employeePushForm, code);
        } else if (pushType.inEnums(LEAVE_APPROVE_REVOKE, WORKOVER_APPROVE_REVOKE, FIELD_APPROVE_REVOKE)) {
            // 审核撤销
            pushSuccess = jPush.pushLeaveCancel(employeePushForm, code);
        }
        if (pushSuccess) {
            crtMsgRecord(employeePushForm, code);
        }
    }

    /**
     * 条件查询消息推送记录（查询时间之前的）
     *
     * @param query
     * @return
     */
    public PageInfo<EmployeePushMessageRecordVO> findList(EmployeePushMessageRecordQueryForm query) {
        Integer userId = query.getUserId();
        if (ObjectUtils.isEmpty(userId)) {
            query.setUserId(Integer.parseInt(BaseContextHandler.getUserID()));
        }
        String date = query.getPreDateTime();
        if (StringHelper.isEmpty(date)) {
            query.setPreDateTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<EmployeePushMessageRecordVO> result = mapper.selectPushMessageRecordList(query);
        return new PageInfo<>(result);
    }
}
