package com.yunya.modules.sms.async;

import com.yunya.feign.sms.query.SmsAutosendEventQueryForm;
import com.yunya.feign.sms.vo.SmsAutosendEventVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.sms.biz.SmsAutosendEventBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介：自动发送事件初始化
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/15 15:17
 * @since: 1.0.0
 */
@Component
public class SmsAutosendEventInitializingBean implements InitializingBean {
    private Logger log = LoggerFactory.getLogger(SmsAutosendEventInitializingBean.class);

    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private SmsAutosendEventBiz smsAutosendEventBiz;

    @Override
    public void afterPropertiesSet() {
        OrganizationModel model = new OrganizationModel();
        model.setWhetherPage(false);
        model.setTypes(new Byte[]{0,2});
        try {
            List<OrganizationInfoDetail> orgList = remoteSystemServiceFeign.findOrgInfoList(model);
            if (StringHelper.isNotEmpty(orgList)) {
                Map<Integer, Boolean> orgIds = new HashMap<>(orgList.size());
                orgList.forEach(org -> orgIds.put(org.getId(), "2".equals(org.getType())));
                SmsAutosendEventQueryForm queryForm = new SmsAutosendEventQueryForm();
                queryForm.setWhetherPage(false);
                queryForm.setOrgIds(orgIds.keySet());
                List<SmsAutosendEventVO> smsAutosendEvents = smsAutosendEventBiz.findSmsAutosendEventList(queryForm);
                if (StringHelper.isNotEmpty(smsAutosendEvents)) {
                    smsAutosendEvents.forEach(vo -> orgIds.remove(vo.getOrgId()));
                }
                if (StringHelper.isNotEmpty(orgIds)) {
                    orgIds.forEach((orgId, isClinic) -> smsAutosendEventBiz.initAutoSendEvent(orgId, isClinic));
                }
            }
        } catch (Exception e) {
            log.error("smsAutoSendEvent init error",e.getMessage());
        }
    }
}
