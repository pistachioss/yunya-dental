package com.yunya.modules.sms.async;

import com.yunya.feign.sms.query.SmsAutosendEventQueryForm;
import com.yunya.feign.sms.vo.SmsAutosendEventVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.enums.SmsAutosendEventEnum;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.sms.biz.SmsAutosendEventBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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
                Map<String, SmsAutosendEventEnum> data = new HashMap<>(16);
                for (Map.Entry<Integer, Boolean> entry : orgIds.entrySet()) {
                    Integer orgId = entry.getKey();
                    List<SmsAutosendEventEnum> list = SmsAutosendEventEnum.values(entry.getValue());
                    for (SmsAutosendEventEnum event : list) {
                        data.put(StringHelper.joinWith(".", orgId, event.getCode()), event);
                    }
                }
                // 移除已存在的数据
                smsAutosendEvents.forEach(vo->data.remove(StringHelper.joinWith(".", vo.getOrgId(), vo.getEventCode())));
                if (StringHelper.isNotEmpty(data)) {
                    data.forEach((key, event)-> {
                        Integer orgId = Integer.parseInt(key.substring(0, key.lastIndexOf(".")));
                        smsAutosendEventBiz.initAutoSendEvent(orgId, Collections.singleton(event));
                    });
                }
            }
        } catch (Exception e) {
            log.error("smsAutoSendEvent init error: {}", e);
        }
    }
}
