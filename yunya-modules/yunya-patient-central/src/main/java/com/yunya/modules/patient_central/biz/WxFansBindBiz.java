package com.yunya.modules.patient_central.biz;


import com.yunya.feign.patient_central.domain.query.WxFansBindForm;
import com.yunya.feign.patient_central.domain.query.WxUserQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.WxWechatbindAppListVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.DictionaryItemModel;
import com.yunya.feign.wechat.RemoteWechatServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.WxFans;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.WxFansBindMapper;
import com.yunya.modules.patient_central.mapper.WxFansMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_EXIST;


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
    private WxFansMapper wxFansMapper;
    @Autowired
    private PatientBaseInfoMapper patientBaseInfoMapper;
    @Resource
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private RemoteWechatServiceFeign remoteWechatServiceFeign;

    public Integer batchInsert(List<WxFansBind> list) {
        return mapper.batchInsert(list);
    }

    public List<DictionaryItem> wxFindDictionaryItemList(DictionaryItemModel model) {
       return remoteSystemServiceFeign.findDictionaryItemList(model);
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

    public Integer allBind() {
        WxFans wxFans = new WxFans();
        wxFans.setBind(false);
        wxFans.setFansStatus(2);
        //未绑定患者的微信用户list
        List<WxFans>list = wxFansBiz.selectList(wxFans).parallelStream().filter(Objects::nonNull).collect(Collectors.toList());
        //已绑定微信的患者手机号list
        List<WxFans>moblieList = wxFansBiz.findAllListWithMobile().parallelStream().filter(Objects::nonNull).collect(Collectors.toList());

        //未绑定患者的微信用户list并去除已绑定其他人的患者手机号
//        List<WxFans> collect = list.stream()
//                .filter(u -> moblieList.contains(u.getRegisterMobile()))
//                .collect(Collectors.toList());
        List<WxFans>removelist = new ArrayList<>();
       for(WxFans wx:list){
           for(WxFans wxmb:moblieList){
               if(wxmb.getRegisterMobile().equals(wx.getRegisterMobile())){
                   removelist.add(wx);
               }
           }
       }
        list.removeAll(removelist);
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        Date date = new Date();
        List<WxFansBind>saveList = new ArrayList();
        List<WxFans>upList = new ArrayList();
        //获取全部患者
        List<PatientBaseInfo>plist = patientBaseInfoMapper.select(patientBaseInfo);
        list = list.stream().filter(student -> "16657113075".equals(student.getRegisterMobile())).collect(Collectors.toList());
        for(WxFans fans:list){
            if(fans.getRegisterMobile()!=null){
                WxFansBind wxFansBind;
                List<PatientBaseInfo> result =
                        plist.stream().filter(student -> fans.getRegisterMobile().equals(student.getMobile())).collect(Collectors.toList());
                //处理微信粉丝表绑定状态以及卡主ID
                WxFans upfans = new WxFans();
                upfans.setId(fans.getId());
                upfans.setBind(true);
                upfans.setBindTime(date);
                //同一手机号有多个患者
                if(result.size() > 1){
                    for (int i = 0; i < result.size(); i++) {
                        wxFansBind = new WxFansBind();
                        wxFansBind.setPatientId(result.get(i).getId());
                        wxFansBind.setOpenId(fans.getOpenId());
                        wxFansBind.setUnionId(fans.getUnionId());
                        wxFansBind.setBind(true);
                        wxFansBind.setBindTime(date);
                        wxFansBind.setIsVip(true);
                        if(i==0){
                            wxFansBind.setDictionaryId(142);
                            wxFansBind.setIsOwner(true);
                            upfans.setPatientId(result.get(i).getId());
                        }else{
                            wxFansBind.setDictionaryId(120);
                            wxFansBind.setIsOwner(false);
                        }
                        wxFansBind.setCrtTime(date);
                        saveList.add(wxFansBind);
                    }
                    upList.add(upfans);
                    //同一手机号有一个患者
                }else if(result.size() == 1){
                    wxFansBind = new WxFansBind();
                    wxFansBind.setPatientId(result.get(0).getId());
                    wxFansBind.setOpenId(fans.getOpenId());
                    wxFansBind.setUnionId(fans.getUnionId());
                    wxFansBind.setBind(true);
                    wxFansBind.setBindTime(date);
                    wxFansBind.setDictionaryId(142);
                    wxFansBind.setCrtTime(date);
                    wxFansBind.setIsOwner(true);
                    wxFansBind.setIsVip(true);
                    upfans.setPatientId(plist.get(0).getId());
                    saveList.add(wxFansBind);
                    upList.add(upfans);
                }

            }
        }
       if(saveList.size()>0){
           if(upList.size()>0){
               wxFansMapper.updateList(upList);
           }
           return  mapper.batchInsert(saveList);
       }
       return 0;
    }


    public Integer bind(WxFansBindForm wxFansBindForm) {

        WxFansBind wxFansBind = new WxFansBind();
        Date date = new Date();
        wxFansBind.setPatientId(wxFansBindForm.getPatientId());
//        wxFansBind.setUnionId(wxFansBindForm.getUnionId());
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

    public List<WxFansBind> listWxByPatientIds(Collection<Integer> patientIds) {
        Example example = new Example(WxFansBind.class);
        example.createCriteria()
                .andEqualTo("bind", true)
                .andIn("patientId", patientIds)
                .andIsNotNull("unionId");
        return mapper.selectByExample(example);
    }

    public List<WxFansBind> listWxByNotPatientIds(Collection<Integer> patientIds) {
        Example example = new Example(WxFansBind.class);
        example.createCriteria()
                .andEqualTo("bind", true)
                .andNotIn("patientId", patientIds);
        return mapper.selectByExample(example);
    }
}
