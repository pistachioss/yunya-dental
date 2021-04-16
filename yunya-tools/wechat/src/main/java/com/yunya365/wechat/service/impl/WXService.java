package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.vo.WxPatientEffectiveVo;
import com.yunya.feign.oss.RemoteOssServiceFeign;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.WxFansDetailForm;
import com.yunya.feign.patient_central.domain.query.WxFansSaveForm;
import com.yunya.feign.patient_central.domain.query.WxUserQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientPublicInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.WxCardUseVo;
import com.yunya.feign.patient_central.domain.vo.web.WxFansDetailVO;
import com.yunya.feign.patient_central.domain.vo.web.WxPatientVo;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.vo.BenefitItemVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.wechat.domain.model.WxRegisterModel;
import com.yunya.feign.wechat.domain.vo.WxAuthVo;
import com.yunya.feign.wechat.domain.vo.WxVipInfoVo;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.WxFans;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.models.system.DictionaryItem;
import com.yunya365.wechat.enums.WeChatError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private RestTemplate restTemplate;
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

    public WxAuthVo getAuthInfo(String code) {
        WxAuthVo vo = new WxAuthVo();
        //根据code获取access_token和openid(非基础的那个)
        String openId = super.getAuthOpenId(code);
        vo.setSubscribe(false);
        vo.setOpenId(openId);
        int count = patientFeign.countRegister(openId);
        if (count > 0) {
            vo.setSubscribe(true);
        }
        log.info("用户注册授权信息：{}", vo);
        return vo;
    }

    public void register(String openId, WxRegisterModel model) {
        WxFansSaveForm fansSaveForm = new WxFansSaveForm();
        int count = patientFeign.countRegister(openId);
        if (count > 0) {
            throw new ClientServiceException(WeChatError.USER_IS_REGISTERED);
        }
        //获取微信用户信息
        String userInfoStr = redisUtils.get(openId);
        WxFans wxFans = this.assembleWxFans(userInfoStr);
        List<WxFansBind> wxFansBinds = this.buildWxFansBind(wxFans, model);
        fansSaveForm.setWxFans(wxFans);
        fansSaveForm.setFansBind(wxFansBinds);
        //调用患者服务的保存微信用户接口，患者绑定关系表
        patientFeign.saveWx(fansSaveForm);
    }

    public WxVipInfoVo vipInfo(String openId, Integer patientId) {
        WxFans wxFans = this.getOwnInfo(openId, patientId);
        if (patientId == null) {
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
        if (patientId == null) {
            return this.assembleWxPatientVo(wxFans);
        } else {
            return patientFeign.getWxPatientInfo(patientId);
        }
    }

    public List<WxCardUseVo> listCardRecord(String cardNumber, Integer type) {
        return patientFeign.listPatientCardRecord(cardNumber, type);
    }

    public List<BenefitItemVo> listCouponCardUsage(Integer cardId, String couponName) {
        List<BenefitItemVo> voList = Lists.newArrayList();
        List<String> list = Lists.newArrayList("938  IVY365 kids（新）","937  IVY365 Youngs（新）","936 IVY365 Adults（新）");
        if (list.contains(couponName)) {
            voList.add(fixedItem("初/复诊检查费"));
            voList.add(fixedItem("影像检查"));
            voList.add(fixedItem("口腔卫生宣教"));
            voList.add(fixedItem("口腔健康管理咨询"));
        }
        List<BenefitItemVo> benefitItemVos = reportServiceFeign.listWxCouponsUseItem(cardId);
        voList.addAll(benefitItemVos);
        return voList;
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

    private WxFans getOwnInfo(String openId, Integer patientId) {
        WxUserQuery query = new WxUserQuery();
        if (patientId == null) {
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
        if (patientId == null) {
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
            List<String> data = (List<String>) url.getData();
            effectCardList.stream()
                    .filter(obj -> StringUtils.isNotBlank(obj.getPath()))
                    .forEach(obj -> {
                        Optional<String> first = data.stream().filter(urlStr -> urlStr.contains(obj.getPath())).findFirst();
                        first.ifPresent(obj::setPath);
                    });
        } catch (Exception e) {
            log.warn("获取图片异常，e", e);
            throw new ClientServiceException(WeChatError.WX_FILE_URL_ERROR);
        }
    }

    private WxFans assembleWxFans(String userInfoStr) {
        WxFans wxFans = JSONObject.parseObject(userInfoStr, WxFans.class);
        this.jsonToFans(wxFans, userInfoStr);
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

    private void jsonToFans(WxFans wxFans, String userInfoStr) {
        JSONObject userJson = JSONObject.parseObject(userInfoStr);
        JSONArray tagList = userJson.getJSONArray("tagid_list");
        if (CollectionUtils.isNotEmpty(tagList)) {
            wxFans.setTagidList(Joiner.on(",").join(tagList));
        }
    }
}
