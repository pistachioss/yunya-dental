package com.yunya.modules.sms.async;

import com.yunya.feign.sms.query.SmsAutosendEventQueryForm;
import com.yunya.feign.sms.vo.SmsAutosendEventVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.modules.sms.biz.SmsAutosendEventBiz;
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

    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private SmsAutosendEventBiz smsAutosendEventBiz;

    @Override
    public void afterPropertiesSet() throws Exception {
        OrganizationModel model = new OrganizationModel();
        model.setWhetherPage(false);
        model.setTypes(new Byte[]{0,2});
        List<OrganizationInfoDetail> orgList = remoteSystemServiceFeign.findOrgInfoList(model);
        if (orgList!=null && !orgList.isEmpty()) {
            Map<Integer, Boolean> orgIds = new HashMap<>(orgList.size());
            orgList.forEach(org->orgIds.put(org.getId(),"2".equals(org.getType())));
            SmsAutosendEventQueryForm queryForm = new SmsAutosendEventQueryForm();
            queryForm.setWhetherPage(false);
            if (orgIds!=null && !orgIds.isEmpty()) {
                queryForm.setOrgIds(orgIds.keySet());
                List<SmsAutosendEventVO> smsAutosendEventVOS = smsAutosendEventBiz.findSmsAutosendEventList(queryForm);
                if (smsAutosendEventVOS != null && !smsAutosendEventVOS.isEmpty()) {
                    smsAutosendEventVOS.forEach(vo -> orgIds.remove(vo.getOrgId()));
                }
                if (orgIds != null && !orgIds.isEmpty()) {
                    orgIds.forEach((orgId, isClinic) -> {
                        smsAutosendEventBiz.initAutoSendEvent(orgId, isClinic);
                    });
                }
            }
        }
    }
}
