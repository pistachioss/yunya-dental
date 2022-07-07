package com.yunya.modules.discount.biz;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.sms.model.SmsAutoEventSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.SmsAutosendEventEnum;
import com.yunya.framework.common.enums.SmsTemplateItemEnum;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CardActivedSms;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.mapper.CardActivedSmsMapper;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/7/7 9:43
 * @since: 1.0.0
 */
@Service
public class CardActivedSmsBiz extends BaseBiz<CardActivedSmsMapper, CardActivedSms> {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RemotePatientCentralServiceFeign patientFeign;
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;

    /**
     * 记录下短信发送
     * @param cardId
     * @param couponId
     * @param crtId
     */
    public void recordSendSms(Integer cardId, Integer couponId, Integer crtId) {
        CardActivedSms record = new CardActivedSms();
        record.setCardId(cardId);
        record.setCouponId(couponId);
        record.setCrtId(crtId);
        record.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(record);
    }

    /**
     * 发送通知短信并记录入库
     *
     * @param card
     * @param coupon
     */
    public void sendAndrecordSms(Card card, CouponCommonInfo coupon) {
        PatientBaseInfo patient = patientFeign.findPatientInfoById(card.getPatientId());
        JSONObject tmpParam = new JSONObject();
        // 患者姓名
        tmpParam.put(SmsTemplateItemEnum.PATIENT_NAME.getAction(), patient.getName());
        //产品类型
        tmpParam.put(SmsTemplateItemEnum.PRODUCT_MODEL.getAction(), CouponTypeEnum.getValue(coupon.getType().intValue()));
        //产品名称
        tmpParam.put(SmsTemplateItemEnum.PRODUCT_NAME.getAction(), coupon.getName());
        SmsAutoEventSendRecordModel smsModel = new SmsAutoEventSendRecordModel();
        SmsCommonSendRecordModel model = new SmsCommonSendRecordModel();
        model.setMobile(card.getSoldPhoneNumber());
        model.setSendObject(card.getSoldTarget());
        model.setTemplateParam(tmpParam);
        Integer orgId = card.getOrgId();
        Integer crtId = card.getCrtId();
        String name = "";
        SysEmployee employee = systemServiceFeign.findSysEmployeeById(crtId);
        if (!ObjectUtils.isEmpty(employee)) {
            name = employee.getName();
        }
        smsModel.setEventCode(SmsAutosendEventEnum.SP_COUPON_ACTIVED.getCode());
        smsModel.setModels(Collections.singletonList(model));
        smsModel.setUserId(crtId);
        smsModel.setOrgId(orgId);
        smsModel.setName(name);
        redisUtils.lPush(RedisConstants.SMS_SEND_MESSAGE_QUEUE + orgId, smsModel);
        recordSendSms(card.getId(), card.getCouponId(), crtId);
    }
}
