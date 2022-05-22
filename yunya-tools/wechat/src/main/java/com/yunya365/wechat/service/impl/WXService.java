package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.vo.WxPatientEffectiveVo;
import com.yunya.feign.oss.RemoteOssServiceFeign;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.query.WxFansDetailForm;
import com.yunya.feign.patient_central.domain.query.WxFansSaveForm;
import com.yunya.feign.patient_central.domain.query.WxUserQuery;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.vo.BenefitItemVo;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.wechat.domain.model.*;
import com.yunya.feign.wechat.domain.vo.*;
import com.yunya.framework.common.constant.WXConstant;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.WxFans;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.wechat.WxMsgTemplates;
import com.yunya.models.wechat.WxTemplateMsgRecords;
import com.yunya365.wechat.enums.WeChatError;
import com.yunya365.wechat.mapper.WxMsgTemplatesMapper;
import com.yunya365.wechat.mapper.WxTemplateMsgRecordsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;
import static com.yunya.feign.wechat.enums.TemplateDataEnum.*;
import static com.yunya.framework.common.constant.WXConstant.*;
import static java.util.stream.Collectors.*;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:35
 **/
@Slf4j
@Service
public class WXService extends AbstractWxBaseApi {
    public static String QIN_SHU_GUAN_XI = "亲属关系";
    public static String BEN_REN = "本人";
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RemotePatientCentralServiceFeign patientFeign;
    @Resource
    private RemoteDiscountFeign discountFeign;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemoteOssServiceFeign ossServiceFeign;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private RemoteReportServiceFeign reportServiceFeign;
    @Resource
    private RemoteAppointmentFeign appointmentFeign;
    @Resource
    private WxMsgTemplatesMapper templatesMapper;
    @Value("${mp.domain}")
    private String mpDomain;
    @Resource(name = "customizeThreadPool")
    private ExecutorService cardThreadPool;
    @Resource
    private WxTemplateMsgRecordsMapper recordsMapper;

    public WxAuthVo getAuthInfo(String code) {
        WxAuthVo vo = new WxAuthVo();
        //根据code获取access_token和openid(非基础的那个)
        JSONObject jsonObject = super.getAuthOpenId(code);
        String openId = jsonObject.getString("openid");
        //拉取用户信息(需scope为 snsapi_userinfo)并且先保存
        authAndSave(jsonObject, openId);
        vo.setIsRegister(false);
        vo.setOpenId(openId);
        WxFans wxFans = this.getOwnInfo(openId, null);
        if (wxFans != null && StringUtils.isNotBlank(wxFans.getRegisterMobile())) {
            vo.setIsRegister(true);
            vo.setPatientId(wxFans.getPatientId());
            this.authSaveRedis(wxFans, openId);
        } else {
            this.authSaveRedis(vo, openId);
        }
        log.info("用户注册授权信息：{}", vo);
        return vo;
    }

    /**
     * 获取用户信息并且判断用户是否已关注公众号
     * @param code
     * @return
     */
    public ResponseResult<WxAuthVo> getAuthInfoAndCheckUser(String code, HttpServletRequest request) {
        //根据code获取access_token和openid(非基础的那个)
        JSONObject jsonObject = super.getAuthOpenId(code);
        String openId = jsonObject.getString("openid");
        JSONObject userInfo = super.getAndCheckUserInfo(openId);
        WxFans wxFans = this.assembleWxFans(userInfo);
        WxAuthVo vo = new WxAuthVo();
        vo.setIsRegister(true);
        vo.setOpenId(wxFans.getOpenId());
        request.getSession().setAttribute(GZH_SESSION_KEY, vo.getOpenId());
        return ResponseUtil.success(vo);
    }

    private void authSaveRedis(Object object, String openId) {
        String weChatOpenIdKey = String.format(WXConstant.WECHAT_OPENID_KEY, openId);
        String openInfoStr = redisUtils.get(weChatOpenIdKey);
        if (StringUtils.isBlank(openInfoStr)) {
            redisUtils.set(weChatOpenIdKey, object, 30, TimeUnit.DAYS);
        }
    }

    public WxRegisterVo register(String openId, WxRegisterModel model) {
        log.info("公众号注册openId：{}", openId);
        WxRegisterVo vo = new WxRegisterVo();
        WxFans wxFansReg = this.getOwnInfo(openId, null);
        if (wxFansReg != null && StringUtils.isNotBlank(wxFansReg.getRegisterMobile())) {
            throw new ClientServiceException(WeChatError.USER_IS_REGISTERED);
        }
        WxFans wxFans = this.saveWxPatient(openId, model);
        if (wxFans.getPatientId() != null) {
            vo.setPatientId(wxFans.getPatientId());
            vo.setMobile(wxFans.getRegisterMobile());
            vo.setPatientName(wxFans.getRegisterName());
        }
        redisUtils.set(String.format(WXConstant.WECHAT_OPENID_KEY, openId), wxFans, 30, TimeUnit.DAYS);
        return vo;
    }

    public WxFans saveWxPatient(String openId, WxRegisterModel model) {
        WxFansSaveForm fansSaveForm = new WxFansSaveForm();
        List<WxFansBind> wxFansBinds = Lists.newArrayList();
        //获取微信用户信息
        JSONObject userInfoJson = super.getAndCheckUserInfo(openId);
        WxFans wxFans = this.assembleWxFans(userInfoJson);
        wxFans.setBind(false);
        if (StringUtils.isNotBlank(model.getMobile()) && StringUtils.isNotBlank(model.getUserName())) {
            wxFansBinds = this.buildWxFansBind(wxFans, model);
        }
        fansSaveForm.setWxFans(wxFans);
        fansSaveForm.setFansBind(wxFansBinds);
        //调用患者服务的保存微信用户接口，患者绑定关系表
        patientFeign.saveWx(fansSaveForm);
        return wxFans;
    }

    public WxVipInfoVo vipInfo(String openId, Integer patientId) {
        WxFans wxFans = this.getOwnInfo(openId, patientId);
        if (patientId == null || patientId == 0) {
            if (wxFans == null) {
                throw new ClientServiceException(WeChatError.USER_NOT_FOLLOW);
            }
            return this.getVipInfo(wxFans.getPatientId(), wxFans);
        } else {
            if (wxFans == null) {
                return this.assembleWxPatientInfo(patientId);
            } else {
                return this.getVipInfo(patientId, wxFans);
            }
        }
    }

    public List<WxFansDetailVO> listAccount(String openId) {
        WxFansDetailForm wxFansDetailForm = new WxFansDetailForm();
        wxFansDetailForm.setOpenId(openId);
        return patientFeign.findDetail(wxFansDetailForm);
    }

    public WxPatientVo settingInfo(String openId, Integer patientId) {
        WxUserQuery query = new WxUserQuery();
        query.setOpenId(openId);
        WxFans wxFans = patientFeign.getWxFans(query);
        if (patientId == null || patientId == 0) {
            return this.assembleWxPatientVo(wxFans);
        } else {
            return patientFeign.getWxPatientInfo(patientId);
        }
    }

    public List<WxCardUseVo> listCardRecord(String cardNumber, Integer type) {
        List<WxCardUseVo> wxCardUseVos = patientFeign.listPatientCardRecord(cardNumber, type);
        wxCardUseVos.sort(Comparator.comparing(WxCardUseVo::getOperatingTime).reversed());
        return wxCardUseVos;
    }

    public WxCardUsageVo listCouponCardUsage(Integer cardId, String couponName) {
        WxCardUsageVo cardUsage = discountFeign.getUserCardUsage(cardId);
        if (cardUsage == null) {
            return null;
        }
        List<BenefitItemVo> voList = Lists.newArrayList();
        List<String> list = Lists.newArrayList("905 IVY 365 KIDS 2021版", "906 IVY 365卡 2021版", "907 IVY 1365卡 2021版", "908 IVY 2365家庭卡 2021版");
        if (list.contains(couponName)) {
            voList.add(fixedItem("口腔健康管理咨询"));
            voList.add(fixedItem("口腔健康管理档案"));
        }
        List<BenefitItemVo> benefitItemVos = reportServiceFeign.listWxCouponsUseItem(cardId);
        if (CollectionUtils.isNotEmpty(benefitItemVos)) {
            voList.addAll(benefitItemVos);
        }
        cardUsage.setCardUsageList(voList);
        //拼接oss图片
        if (StringUtils.isNotBlank(cardUsage.getPath())) {
            List<String> urls = this.getOssUrls(Lists.newArrayList(cardUsage.getPath()));
            cardUsage.setPath(urls.get(0));
        }
        return cardUsage;
    }

    public WxMemberRelationVO memberRelation(Integer patientId) {
        PatientMemberRelationQueryForm form = new PatientMemberRelationQueryForm();
        form.setPatientId(patientId);
        MemberRelationVo vo = patientFeign.findMemberBindingRelation(form);
        WxMemberRelationVO relationVO = new WxMemberRelationVO();
        if (CollectionUtils.isNotEmpty(vo.getMemberRelationList())) {
            relationVO.setViceCarder(vo.getMemberRelationList().stream()
                    .map(PatientMemberRelationVo::getName).collect(toList()));
        }
        if (CollectionUtils.isNotEmpty(vo.getMemberBalanceRelationList())) {
            relationVO.setBalanceSharer(vo.getMemberBalanceRelationList().stream()
                    .map(PatientMemberRelationVo::getName).collect(toList()));
        }
        return relationVO;
    }

    public void pullTemplate() {
        String template = super.listTemplate();
        List<WxMsgTemplates> list = JSONArray.parseArray(template, WxMsgTemplates.class);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(obj -> {
                String content = this.filterAndGenData(obj);
                obj.setContent(content);
            });
            templatesMapper.batchInsert(list);
        }
    }

    public void pushTemplateMsg(WxTemplateMsgModel msgModel) {
        log.info("客户端模板推送消息：{}", msgModel);
        Map<String, Object> paramMap = msgModel.getParamMap();
        Integer patientId = msgModel.getPatientId();
        WxFans wxPushUser = patientFeign.getWxPushUser(patientId);
        if (wxPushUser == null) {
            throw new ClientServiceException(WeChatError.PATIENT_UNBIND_WX);
        }
        log.info("患者id：{}，被推送微信用户：{}", patientId, wxPushUser.getOpenId());
        //获取模板信息
        WxMsgTemplates template = this.getTemplate(msgModel.getTemplateEnum().getTitle());
        if (template != null) {
            WxTemplatePushModel pushModel = this.generatePushModel(wxPushUser, template, paramMap);
            //推送消息
            String msgId = super.pushTemplate(pushModel);
            //消息暂存缓存
            redisUtils.set(WXConstant.WX_TEMPLATE_MSGID_KEY + msgId, this.transferTemplateRecord(pushModel, msgId, wxPushUser), 1, TimeUnit.MINUTES);
        }
    }

    public void batchPushTemplate(List<WxTemplateMsgModel> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<Integer> patientIds = list.stream()
                    .map(WxTemplateMsgModel::getPatientId).collect(toList());
            if (CollectionUtils.isEmpty(patientIds)) {
                throw new ClientServiceException(WeChatError.WX_TEMP_PUSH_ERROR);
            }
            //查询需要推送的wx用户
            List<WxFans> wxFans = patientFeign.listWxPushUser(patientIds);
            Map<Integer, WxFans> patientWxMap = wxFans.stream()
                    .collect(toMap(WxFans::getPatientId, Function.identity()));
            //获取模板信息
            List<WxMsgTemplates> templates = this.listTemplate(list.stream()
                    .map(obj -> obj.getTemplateEnum().getTitle()).collect(toSet()));
            this.createAndPushTemplate(list, patientWxMap, templates);
        }
    }

    public void pushAutoReplyMsg(WxAutoReplyModel msgModel) {
        log.info("自动回复推送消息：{}", msgModel);
        if (msgModel != null) {
            //推送消息
            String msgId = super.pushAutoReply(msgModel);
        }
    }

    public WxAppointDetailVo getAppointDetail(Integer appointId) {
        AppointmentVo appointmentVo = appointmentFeign.findAppointmentDetailById(appointId);
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setId(appointmentVo.getOrgId());
        OrganizationInfoDetail org = systemServiceFeign.findOrgInfoList(organizationModel).get(0);
        WxAppointDetailVo appointDetailVo = BeanCopierUtils.generalCopyBean(appointmentVo, WxAppointDetailVo.class);
        appointDetailVo.setAppointDate(LocalDate.fromDateFields(appointmentVo.getAppointDate()).toString("yyyy-MM-dd ") + appointmentVo.getAppointTime());
        appointDetailVo.setOrgName(org.getAbbreviation());
        appointDetailVo.setOrgAddress(org.getAddress());
        return appointDetailVo;
    }

    public ResponseResult confirmAppoint(WxAppointConfirmModel model) {
        return appointmentFeign.confirmWxAppoint(model);
    }

    private void createAndPushTemplate(List<WxTemplateMsgModel> list, Map<Integer, WxFans> patientWxMap, List<WxMsgTemplates> templates) {
        Map<String, WxMsgTemplates> templateMap = templates.stream()
                .collect(toMap(WxMsgTemplates::getTitle, Function.identity()));
        for (WxTemplateMsgModel model : list) {
            cardThreadPool.execute(() -> {
                Integer patientId = model.getPatientId();
                Map<String, Object> paramMap = model.getParamMap();
                WxFans wxPushUser = patientWxMap.get(patientId);
                if (wxPushUser == null) {
                    throw new ClientServiceException(WeChatError.WX_USER_NOT_EXIST.setErrorMsg(patientId));
                }
                WxTemplatePushModel pushModel = this.generatePushModel(wxPushUser, templateMap.get(model.getTemplateEnum().getTitle()), paramMap);
                //推送消息
                String msgId = super.pushTemplate(pushModel);
                //消息暂存缓存
                redisUtils.set(WXConstant.WX_TEMPLATE_MSGID_KEY + msgId
                        , this.transferTemplateRecord(pushModel, msgId, wxPushUser), 1, TimeUnit.MINUTES);
            });
        }
    }

    private List<WxTemplatePushModel> getTemplateFutureResult(List<Future<WxTemplatePushModel>> futureList) throws Exception {
        List<WxTemplatePushModel> list = Lists.newArrayListWithCapacity(futureList.size());
        for (Future<WxTemplatePushModel> future : futureList) {
            list.add(future.get());
        }
        return list;
    }

    private WxTemplatePushModel generatePushModel(WxFans wxPushUser, WxMsgTemplates template, Map<String, Object> paramMap) {
        Integer appointId = (Integer) paramMap.get("appointId");
        StrSubstitutor strSubstitutor = new StrSubstitutor(paramMap);
        String context = strSubstitutor.replace(template.getContent());
        paramMap = JSON.parseObject(context, new TypeReference<Map<String, Object>>() {
        }, Feature.OrderedField);
        WxTemplatePushModel pushModel = WxTemplatePushModel.builder()
                .touser(wxPushUser.getOpenId())
                .template_id(template.getTemplateId())
                .data(paramMap).build();
        if ("预约确认通知".equals(template.getTitle())) {
            pushModel.setUrl(mpDomain + "/mobile/#/registerBtn?appointId=" + appointId);
        }
        return pushModel;
    }

    private WxTemplateMsgRecords transferTemplateRecord(WxTemplatePushModel pushModel, String msgId, WxFans wxPushUser) {
        WxTemplateMsgRecords record = new WxTemplateMsgRecords();
        record.setMsgId(msgId);
        record.setTemplateId(pushModel.getTemplate_id());
        record.setPatientId(wxPushUser.getPatientId());
        record.setOpenId(pushModel.getTouser());
        record.setNickName(wxPushUser.getNickName());
        record.setContent(JSONObject.toJSONString(pushModel, WriteMapNullValue));
        record.setMsgDate(new Date());
        return record;
    }

    private WxMsgTemplates getTemplate(String title) {
        Example example = new Example(WxMsgTemplates.class);
        example.createCriteria().andEqualTo("title", title);
        return templatesMapper.selectOneByExample(example);
    }

    private List<WxMsgTemplates> listTemplate(Set<String> titles) {
        Example example = new Example(WxMsgTemplates.class);
        example.createCriteria().andIn("title", titles);
        return templatesMapper.selectByExample(example);
    }

    private String filterAndGenData(WxMsgTemplates wxMsgTemplates) {
        Map<String, WxTemplateDataVo> map = Maps.newLinkedHashMap();
        String content = wxMsgTemplates.getContent();
        String title = wxMsgTemplates.getTitle();
        int count = 0;
        if (content.indexOf("{first") > 0) {
            count = (content.length() - content.replace("{{first", "").length()) / "{{first".length();
            this.assembleTemplate(count, map, "first", "", title);

        }
        if (content.indexOf("{keyword") > 0) {
            count = (content.length() - content.replace("{{keyword", "").length()) / "{{keyword".length();
            this.assembleTemplate(count, map, "keyword", "#00b9b2", title);
        }
        if (content.indexOf("{remark") > 0) {
            count = (content.length() - content.replace("{{remark", "").length()) / "{{remark".length();
            this.assembleTemplate(count, map, "remark", "", title);
        }
        return new Gson().toJson(map);
    }

    private void assembleTemplate(int count, Map<String, WxTemplateDataVo> map, String key, String color, String title) {
        StringBuilder sb = null;
        for (int i = 1; i <= count; i++) {
            if ("keyword".equals(key)) {
                sb = new StringBuilder("${").append(key);
                map.put(key + i, new WxTemplateDataVo(sb.append(i).append("}").toString(), color));
            } else {
                map.put(key, new WxTemplateDataVo("${" + key + "}", color));
                if ("预约确认通知".equals(title)) {
                    sb = new StringBuilder("您好，${").append(PATIENT_NAME.getArgName()).append("}")
                            .append("，您的预约时间是：").append("${").append(APPOINT_DATE.getArgName()).append("}")
                            .append("，是否确认按时就诊，请点击【详情】 进行查看，谢谢！");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    this.assembleRemark(key, color, map);
                }
                if ("预约成功提醒".equals(title)) {
                    sb = new StringBuilder("您好，${").append(PATIENT_NAME.getArgName()).append("}，");
                    map.put(key, new WxTemplateDataVo(sb.append("您已预约成功").toString(), color));

                    this.assembleRemark(key, color, map);
                }
                if ("预约变更成功通知".equals(title)) {
                    sb = new StringBuilder("您好，您原定${").append(APPOINT_DATE.getArgName()).append("}")
                            .append("的预约已变更为：");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    this.assembleRemark(key, color, map);
                }
                if ("预约取消提醒".equals(title)) {
                    sb = new StringBuilder("您好，您原定${").append(APPOINT_DATE.getArgName()).append("}")
                            .append("的预约已取消。");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    this.assembleRemark(key, color, map);
                }
                if ("会员卡开卡通知".equals(title)) {
                    map.put(key, new WxTemplateDataVo("您的会员卡已成功开卡！", color));
                    if ("remark".equals(key)) {
                        map.put(key, new WxTemplateDataVo("开卡后将开始计算有效时长，请悉知！", color));
                    }
                }
                if ("充值成功提醒".equals(title)) {
                    sb = new StringBuilder("您好，${").append(PATIENT_NAME.getArgName()).append("}")
                            .append("，您的会员卡充值成功！");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    this.assembleRemark(key, color, map);
                }
                if ("会员消费提醒".equals(title)) {
                    sb = new StringBuilder("您好，${").append(PATIENT_NAME.getArgName()).append("}")
                            .append("消费您的会员卡，详情如下：");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    this.assembleRemark(key, color, map);
                }
                if ("缴费成功提醒".equals(title)) {
                    map.put(key, new WxTemplateDataVo("您已成功缴费", color));
                }
                if ("次卡使用提醒".equals(title)) {
                    sb = new StringBuilder("亲爱的用户，您有一张${").append(COUPON_NAME.getArgName()).append("}")
                            .append("至今还未激活使用，不要忘了哦~");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    if ("remark".equals(key)) {
                        map.put(key, new WxTemplateDataVo("赶紧用起来吧！", color));
                    }
                }
                if ("授权到期提醒".equals(title)) {
                    sb = new StringBuilder("你好，您的${").append(COUPON_NAME.getArgName()).append("}")
                            .append("即将到期。存在项目次数未使用完");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    if ("remark".equals(key)) {
                        map.put(key, new WxTemplateDataVo("请及时使用，避免过期作废", color));
                    }
                }
                if ("服务到期提醒".equals(title)) {
                    sb = new StringBuilder("你好，你的${").append(COUPON_NAME.getArgName()).append("}")
                            .append("服务已到期");
                    map.put(key, new WxTemplateDataVo(sb.toString(), color));
                    if ("remark".equals(key)) {
                        map.put(key, new WxTemplateDataVo("为避免影响使用，请及时续费。", color));
                    }
                }
                if ("绑定成功通知".equals(title)) {
                    if ("remark".equals(key)) {
                        map.put(key, null);
                    }
                }
                if ("解绑成功通知".equals(title)) {
                    if ("remark".equals(key)) {
                        map.put(key, null);
                    }
                }
            }
        }
    }

    private void assembleRemark(String key, String color, Map<String, WxTemplateDataVo> map) {
        if ("remark".equals(key)) {
            map.put(key, new WxTemplateDataVo("如有疑问，请联系我们。联系电话：${" + LINK_MOBILE.getArgName() + "}", color));
        }
    }

    private BenefitItemVo fixedItem(String itemName) {
        BenefitItemVo vo = new BenefitItemVo();
        vo.setItemName(itemName);
        vo.setOriginalQuantity(-1);
        vo.setRemainingQuantity(-1);
        return vo;
    }

    private WxPatientVo assembleWxPatientVo(WxFans wxFans) {
        WxPatientVo wxPatientVo = new WxPatientVo();
        wxPatientVo.setHeadImgUrl(wxFans.getHeadImgurl());
        wxPatientVo.setUserName(wxFans.getRegisterName());
        wxPatientVo.setMobile(wxFans.getRegisterMobile());
        if (wxFans.getSex() == 1) {
            wxPatientVo.setGender((byte) 0);
        }
        if (wxFans.getSex() == 2) {
            wxPatientVo.setGender((byte) 1);
        }
        wxPatientVo.setAddress(wxFans.getCountry() + wxFans.getProvince() + wxFans.getCity());
        return wxPatientVo;
    }

    public PageInfo<PatientTreatmentRecordVO> treatRecordPage(PatientTreatmentRecordQueryForm form) {
        return treatmentServiceFeign.patientTreatmentRecordList(form);
    }

    public OrderDetailInfoVO treatDetail(Integer treatmentRecordId) {
        return treatmentServiceFeign.findOrderInfoByTreatmentId(treatmentRecordId);
    }

    public WxFans getOwnInfo(String openId, Integer patientId) {
        WxUserQuery query = new WxUserQuery();
        if (patientId == null || patientId == 0) {
            query.setOpenId(openId);
        } else {
            query.setPatientId(patientId);
        }
        //查询微信患者id
        return patientFeign.getWxFans(query);
    }

    private WxVipInfoVo getVipInfo(Integer patientId, WxFans wxFans) {
        WxVipInfoVo wxVipInfoVo;
        //未绑定患者直接返回注册信息
        if (patientId == null || patientId == 0) {
            wxVipInfoVo = this.assembleWxUserInfo(wxFans);
        } else {
            wxVipInfoVo = this.assembleWxPatientInfo(patientId);
            if (wxVipInfoVo == null) {
                wxVipInfoVo = this.assembleWxUserInfo(wxFans);
            }
            //患者头像没有取微信用户头像
            if (StringUtils.isBlank(wxVipInfoVo.getHeadImgUrl())) {
                wxVipInfoVo.setHeadImgUrl(wxFans.getHeadImgurl());
            }
        }
        wxVipInfoVo.setPatientId(patientId);
        return wxVipInfoVo;
    }

    private WxVipInfoVo assembleWxUserInfo(WxFans wxFans) {
        //未注册
        WxVipInfoVo wxVipInfoVo = new WxVipInfoVo();
        wxVipInfoVo.setRegisterName(wxFans.getRegisterName());
        wxVipInfoVo.setRegisterMobile(wxFans.getRegisterMobile());
        wxVipInfoVo.setHeadImgUrl(wxFans.getHeadImgurl());
        return wxVipInfoVo;
    }

    private WxVipInfoVo assembleWxPatientInfo(Integer patientId) {
        WxVipInfoVo wxVipInfoVo = null;
        PatientPublicInfoVo patientInfo = patientFeign.findPatientPublicInfoById(patientId);
        if (patientInfo != null) {
            wxVipInfoVo = new WxVipInfoVo();
            wxVipInfoVo.setRegisterName(patientInfo.getName());
            wxVipInfoVo.setRegisterMobile(patientInfo.getMobile());
            wxVipInfoVo.setHeadImgUrl(patientInfo.getFaceUrl());
            wxVipInfoVo.setMemberType(patientInfo.getMemberTypeId());
            wxVipInfoVo.setExistPrePayment(false);
            wxVipInfoVo.setExistMemberCard(false);
            //是否有预付款账号
            if (StringUtils.isNotBlank(patientInfo.getPrepaymentNumber())) {
                wxVipInfoVo.setPrepaymentNumber(patientInfo.getPrepaymentNumber());
                wxVipInfoVo.setExistPrePayment(true);
            }
            //是否有会员卡账号
            if (StringUtils.isNotBlank(patientInfo.getCardNumber())) {
                wxVipInfoVo.setMemberNumber(patientInfo.getCardNumber());
                wxVipInfoVo.setExistMemberCard(true);
                wxVipInfoVo.setMemberBalance(patientInfo.getMemberCardMoneySum());
            }
            List<WxPatientEffectiveVo> effectCardList = discountFeign.getPatientEffectCardList(patientId);
            if (CollectionUtils.isNotEmpty(effectCardList)) {
                //组装oss文件路径
                this.assembleFileUrl(effectCardList);
                wxVipInfoVo.setCardList(effectCardList);
            }
            wxVipInfoVo.setPatientId(patientId);
        }
        return wxVipInfoVo;
    }

    private void assembleFileUrl(List<WxPatientEffectiveVo> effectCardList) {
        List<String> urls = effectCardList.stream()
                .filter(obj -> StringUtils.isNotBlank(obj.getPath()))
                .map(WxPatientEffectiveVo::getPath).collect(toList());
        if (CollectionUtils.isNotEmpty(urls)) {
            List<String> data = this.getOssUrls(urls);
            effectCardList.stream()
                    .filter(obj -> StringUtils.isNotBlank(obj.getPath()))
                    .forEach(obj -> {
                        Optional<String> first = data.stream().filter(urlStr -> urlStr.contains(obj.getPath())).findFirst();
                        first.ifPresent(obj::setPath);
                    });
        }
    }

    private List<String> getOssUrls(List<String> urls) {
        List<OssUrlForm> ossObs = urls.stream().map(url -> {
            OssUrlForm ossUrlForm = new OssUrlForm();
            ossUrlForm.setCompanyId(0);
            ossUrlForm.setIsThumb(false);
            ossUrlForm.setObjectId(111111);
            ossUrlForm.setOssCategory(4);
            ossUrlForm.setOssFilename(url);
            return ossUrlForm;
        }).collect(toList());
        try {
            final ResponseResult url = ossServiceFeign.getUrl(ossObs);
            return (List<String>) url.getData();
        } catch (Exception e) {
            log.warn("获取图片异常，e", e);
            throw new ClientServiceException(WeChatError.WX_FILE_URL_ERROR);
        }
    }

    private WxFans assembleWxFans(JSONObject userInfoJson) {
        WxFans wxFans = new WxFans();
        wxFans.setSubscribe(userInfoJson.getBoolean("subscribe"));
        wxFans.setOpenId(userInfoJson.getString("openid"));
        wxFans.setLanguage(userInfoJson.getString("language"));
        wxFans.setSubscribeTime(new Date(userInfoJson.getLongValue("subscribe_time") * 1000));
        wxFans.setUnionId(userInfoJson.getString("unionid"));
        wxFans.setRemark(userInfoJson.getString("remark"));
        wxFans.setGroupId(userInfoJson.getString("groupid"));
        JSONArray tagList = userInfoJson.getJSONArray("tagid_list");
        if (CollectionUtils.isNotEmpty(tagList)) {
            wxFans.setTagidList(Joiner.on(",").join(tagList));
        }
        return wxFans;
    }

    private List<WxFansBind> buildWxFansBind(WxFans wxFans, WxRegisterModel model) {
        List<WxFansBind> list = Lists.newArrayList();
        wxFans.setRegisterName(model.getUserName());
        wxFans.setRegisterMobile(model.getMobile());
        PatientBaseInfo baseInfo = new PatientBaseInfo();
        baseInfo.setMobile(model.getMobile());
        baseInfo.setName(model.getUserName());
        List<PatientBaseInfo> patientInfoList = patientFeign.findPatientInfoList(baseInfo);
        if (CollectionUtils.isNotEmpty(patientInfoList)) {
            DictionaryItem dictItem = systemServiceFeign.getDictItemByNames(QIN_SHU_GUAN_XI, BEN_REN);
            if (dictItem == null) {
                throw new ClientServiceException(WeChatError.DICT_NO_CONFIG);
            }
            wxFans.setBind(true);
            wxFans.setBindTime(new Date());
            wxFans.setPatientId(patientInfoList.get(0).getId());
            log.info("微信用户存在的患者信息：{}", wxFans.getPatientId());
            list = patientInfoList.stream().map(obj -> {
                WxFansBind wxFansBind = new WxFansBind();
                wxFansBind.setPatientId(obj.getId());
                wxFansBind.setOpenId(wxFans.getOpenId());
                wxFansBind.setDictionaryId(dictItem.getId());
                wxFansBind.setBind(true);
                wxFansBind.setBindTime(wxFans.getBindTime());
                wxFansBind.setIsVip(true);
                wxFansBind.setIsOwner(true);
                wxFansBind.setUpdId(wxFansBind.getPatientId());
                wxFansBind.setCrtId(wxFansBind.getPatientId());
                return wxFansBind;
            }).collect(toList());
        }
        return list;
    }


    private void authAndSave(JSONObject jsonObject, String openId) {
        JSONObject userJson = getWxApi(WX_GET_USERINFO_ACCESS_URL, jsonObject.getString("access_token"), openId);
        WxFansSaveForm fansSaveForm = new WxFansSaveForm();
        WxFans wxFans = new WxFans();
        wxFans.setOpenId(userJson.getString("openid"));
        wxFans.setNickName(userJson.getString("nickname"));
        wxFans.setSex(userJson.getShort("sex"));
        wxFans.setProvince(userJson.getString("province"));
        wxFans.setCity(userJson.getString("city"));
        wxFans.setCountry(userJson.getString("country"));
        wxFans.setHeadImgurl(userJson.getString("headimgurl"));
        wxFans.setUnionId(userJson.getString("unionid"));
        fansSaveForm.setWxFans(wxFans);
        patientFeign.saveWx(fansSaveForm);
    }
}
