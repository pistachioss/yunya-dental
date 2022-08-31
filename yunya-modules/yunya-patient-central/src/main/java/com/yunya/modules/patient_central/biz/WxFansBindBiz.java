package com.yunya.modules.patient_central.biz;


import com.yunya.feign.patient_central.domain.query.WxFansBindForm;
import com.yunya.feign.patient_central.domain.query.WxUserQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.WxWechatbindAppListVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.DictionaryItemModel;
import com.yunya.feign.wechat.RemoteWechatServiceFeign;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.feign.wechat.enums.TemplateEnum;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.patient_central.WxFans;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.WxFansBindMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;


/**
 * 简介:公司 微信公众号粉丝与患者绑定关系业务层
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Service
//@Transactional(rollbackFor = Exception.class)
public class WxFansBindBiz extends BaseBiz<WxFansBindMapper, WxFansBind> {
    public static String BEN_REN = "本人";
    @Autowired
    private WxFansBiz wxFansBiz;
    @Autowired
    private PatientBaseInfoMapper patientBaseInfoMapper;
    @Resource
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private RemoteWechatServiceFeign remoteWechatServiceFeign;

    public Integer batchInsert(List<WxFansBind> list) {
        return mapper.batchInsert(list);
    }

    public Integer isBind(WxFansBindForm wxFansBindForm) {
        WxFansBind wxFansBind = new WxFansBind();
        wxFansBind.setPatientId(wxFansBindForm.getPatientId());
//        wxFansBind.setUnionId(wxFansBindForm.getUnionId());
        //判断是否已经被绑定 每名患者只能绑定一个微信号
        int a = mapper.selectCount(wxFansBind);
        if (a > 0) {
            throw new ClientServiceException("该患者已经绑定微信", DATA_EXIST);
        }
        return a;
    }

    public Integer bind(WxFansBindForm wxFansBindForm) {

        WxFansBind wxFansBind = new WxFansBind();
        Date date = new Date();
        wxFansBind.setPatientId(wxFansBindForm.getPatientId());
        wxFansBind.setUnionId(wxFansBindForm.getUnionId());
        //判断是否已经被绑定 每名患者只能绑定一个微信号
        int a = mapper.selectCount(wxFansBind);
        if (a > 0) {
            throw new ClientServiceException("该患者已经绑定微信", DATA_EXIST);
        }
        wxFansBind.setOpenId(wxFansBindForm.getOpenId());
        wxFansBind.setUnionId(wxFansBindForm.getUnionId());
        wxFansBind.setBind(true);
        wxFansBind.setBindTime(date);
        wxFansBind.setDictionaryId(wxFansBindForm.getDictionaryId());
//        wxFansBind.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        wxFansBind.setCrtTime(date);

        WxFans wxFansVip = new WxFans();
        wxFansVip.setPatientId(wxFansBindForm.getPatientId());
        long num = wxFansBiz.selectCount(wxFansVip);
        if (num == 0L) {
            wxFansBind.setIsVip(false);
        } else {
            wxFansBind.setIsVip(true);
        }
        //处理微信粉丝表绑定状态以及卡主ID
        WxFans wxFans = new WxFans();
        wxFans.setId(wxFansBindForm.getWxId());
        wxFans.setBind(true);
        wxFans.setBindTime(date);
        if (BEN_REN.equals(wxFansBindForm.getDictionaryName())) {
            wxFansBind.setIsOwner(true);
            //如果是本人 则添加卡主ID
            WxFans op = new WxFans();
            op.setOpenId(wxFansBindForm.getOpenId());
            op = wxFansBiz.selectOne(op);
            if (op.getPatientId() != null) {
                throw new ClientServiceException("该微信号已经绑定卡主", DATA_EXIST);
            }
            wxFans.setPatientId(wxFansBindForm.getPatientId());
        } else {
            wxFansBind.setIsOwner(false);
        }
        wxFansBiz.updateSelectiveById(wxFans);
        int re = mapper.insert(wxFansBind);

        //发送微信推送消息
//        WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
//        wxTemplateMsgModel.setPatientId(wxFansBindForm.getPatientId());
//        wxTemplateMsgModel.setTemplateEnum(TemplateEnum.BIND_SUCCESS);
//
//        PatientBaseInfoVo patientBaseInfoVo = patientBaseInfoMapper.selectOneById(wxFansBindForm.getPatientId());
//
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("first", "有一位新的患者绑定成功，绑定信息如下:");
//        if (patientBaseInfoVo != null) {
//            paramMap.put("keyword1", patientBaseInfoVo.getName());
//            paramMap.put("keyword2", patientBaseInfoVo.getMobile());
//        } else {
//            throw new ClientServiceException("无此患者信息", DATA_NOT_EXIST);
//        }
//        wxTemplateMsgModel.setParamMap(paramMap);
//        remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);

        return re;
    }

    @Transactional(rollbackFor = Exception.class)
    public int unbind(WxFansBindForm wxFansBindForm) {
        WxFans wxFans = wxFansBiz.selectById(wxFansBindForm.getWxId());

        WxFansBind wxFansBind = new WxFansBind();
        wxFansBind.setUnionId(wxFansBindForm.getUnionId());
//        wxFansBind.setPatientId(wxFansBindForm.getPatientId());
        int num = mapper.selectCount(wxFansBind);
        if (num == 1) {
            wxFans.setBind(false);
            wxFans.setBindTime(null);
        } else if (num > 1) {

        } else {
            throw new ClientServiceException("解绑异常", OperationCodeConstants.DELETE_NOT_ALLOW);
        }
        wxFansBind.setPatientId(wxFansBindForm.getPatientId());
        wxFansBind.setDictionaryId(wxFansBindForm.getDictionaryId());
        int de = mapper.delete(wxFansBind);
        if (de > 0) {
            //如果解绑的是本人 则把卡主ID设置为空
            if (BEN_REN.equals(wxFansBindForm.getDictionaryName())) {
                wxFans.setPatientId(null);
            }
        }
        //处理粉丝表中绑定状态，绑定时间以及卡主ID
        wxFansBiz.updateById(wxFans);

//        //发送微信推送消息
//        WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
//        wxTemplateMsgModel.setPatientId(wxFansBindForm.getPatientId());
//        wxTemplateMsgModel.setTemplateEnum(TemplateEnum.UNBIND_SUCCESS);
//
//        PatientBaseInfoVo patientBaseInfoVo = patientBaseInfoMapper.selectOneById(wxFansBindForm.getPatientId());
//
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("first", "您好，您的账号已经解绑，解绑账号信息如下:");
//        if (patientBaseInfoVo != null) {
//            paramMap.put("keyword1", patientBaseInfoVo.getName());
//            paramMap.put("keyword2", patientBaseInfoVo.getMobile());
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
//            paramMap.put("keyword3", sdf.format(new Date()));
//        } else {
//            throw new ClientServiceException("无此患者信息", DATA_NOT_EXIST);
//        }
//        wxTemplateMsgModel.setParamMap(paramMap);
//        remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);
        return de;
    }

    public List<PatientBaseInfoVo> wxFansBindPatientList(WxUserQuery query) {
        return mapper.wxFansBindPatientList(query);
    }

    public List<WxWechatbindAppListVO> findPatientBaseInfo(String unionId) {
        DictionaryItemModel model = new DictionaryItemModel();
        model.setDictionaryTypeId(19);
        List<DictionaryItem> dLsit = remoteSystemServiceFeign.findDictionaryItemList(model);
        Map<String, DictionaryItem> dicMap = new HashMap(16);
        dLsit.forEach(z -> dicMap.put(z.getId() + "", z));
        List<WxWechatbindAppListVO> list = mapper.findPatientBaseInfo(unionId);
        list.forEach(item ->
                item.setDictionaryName(dicMap.get(item.getDictionaryId() + "").getName())
        );
        return list;
    }
}
