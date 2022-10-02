package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.ivy_mini.domain.bo.WeChatSessionBO;
import com.yunya.feign.ivy_mini.domain.form.WxSaveFansForm;
import com.yunya.feign.ivy_mini.domain.form.WxUserInfoForm;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.DictionaryItemModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 简介: 公司微信公众号粉丝业务层
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
@Transactional(rollbackFor = Exception.class)
public class WxFansBiz extends BaseBiz<WxFansMapper, WxFans> {
    private Logger log = LoggerFactory.getLogger(WxFansBiz.class);

    /**
     * 系统服务调用
     */
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;

    @Autowired
    private WxFansBindBiz wxFansBindBiz;
    @Resource
    private PatientExpInfoMapper expInfoMapper;
    @Resource
    private PatientBaseInfoMapper baseInfoMapper;
    @Resource
    private PatientExtInfoMapper extInfoMapper;
    @Resource
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Resource
    private PatientMemberInfoBiz patientMemberInfoBiz;
    @Resource
    private PatientPrepaymentRelationBiz prepaymentRelationBiz;
    @Resource
    private WxFansBindMapper wxFansBindMapper;

    public Integer syncUnionId(SyncUnionIdForm form){
       return wxFansBindMapper.syncUnionId(form);
    }

    public PageInfo<WxWechatFansVo> findWechatList(WxFansWechatQueryForm wxFansQueryForm) {
        if (wxFansQueryForm.getWhetherPage()) {
            PageHelper.startPage(wxFansQueryForm.getPageNum(), wxFansQueryForm.getPageSize());
        }
        DictionaryItemModel model = new DictionaryItemModel();
        model.setDictionaryTypeId(11);
        List<DictionaryItem> dLsit = remoteSystemServiceFeign.findDictionaryItemList(model);
        Map<String, DictionaryItem> dicMap = new HashMap(16);
        dLsit.forEach(z -> dicMap.put(z.getId() + "", z));
        List<WxWechatFansVo> list = mapper.findWechatList(wxFansQueryForm);
        list.forEach(item ->
                item.getBindPantlist().forEach(items -> {
                            if (!StringUtils.isEmpty(item.getBindPant())) {
                                item.setBindPant(item.getBindPant() + "," + items.getName() + "(" + dicMap.get(items.getDictionaryId() + "").getName() + ")");
                            } else {
                                item.setBindPant(items.getName() + "(" + dicMap.get(items.getDictionaryId() + "").getName() + ")");
                            }
                            items.setDictionaryName(dicMap.get(items.getDictionaryId() + "").getName());
                        }

                )
        );

        return new PageInfo<>(list);
    }

    public PageInfo<WxFansVo> findList(WxFansQueryForm wxFansQueryForm) {
        if (wxFansQueryForm.getWhetherPage()) {
            PageHelper.startPage(wxFansQueryForm.getPageNum(), wxFansQueryForm.getPageSize());
        }
        List<WxFansVo> list = mapper.findList(wxFansQueryForm);
        return new PageInfo<>(list);
    }

    public List<WxFansVo> findListByName(WxFanByNameForm wxFansQueryForm) {

        List<WxFansVo> list = mapper.findListByName(wxFansQueryForm);
        return list;
    }
    public WxWechatMapAndBindFansVo findMapList(WxFansMapQueryForm wxFansMapQueryForm) {
        DictionaryItemModel model = new DictionaryItemModel();
        List<DictionaryItem> dicList = systemServiceFeign.findDictionaryItemList(model);
        Map<String, DictionaryItem> dicMap = new HashMap(16);
        dicList.forEach(z -> dicMap.put(z.getId() + "", z));

        List<WxWechatMapFansVo> list = mapper.findMapList(wxFansMapQueryForm);
        for(WxWechatMapFansVo wxWechatMapFansVo:list){
            wxWechatMapFansVo.setNumBind(wxWechatMapFansVo.getBindPantlist().size());
            if(wxWechatMapFansVo.getBindPantlist().size()>0){
                for(WxWechatbindListVO wb:wxWechatMapFansVo.getBindPantlist()){
                    wb.setDictionaryName(dicMap.get(wb.getDictionaryId() + "").getName());
                }
            }
        }
        WxWechatMapBindNumFansVo wxWechatMapBindNumFansVo = mapper.findNumBind(wxFansMapQueryForm);
        WxWechatMapAndBindFansVo wxWechatMapAndBindFansVo = new WxWechatMapAndBindFansVo();
        wxWechatMapAndBindFansVo.setMapFansVoList(list);
        wxWechatMapAndBindFansVo.setWxWechatMapBindNumFansVo(wxWechatMapBindNumFansVo);
        return wxWechatMapAndBindFansVo;
    }
    public List<WxFansDetailVO> findDetail(WxFansDetailForm wxFansDetailForm) {
        List<WxFansDetailVO> list = mapper.findDetail(wxFansDetailForm);
        DictionaryItemModel model = new DictionaryItemModel();
        List<DictionaryItem> dicList = systemServiceFeign.findDictionaryItemList(model);
        Map<String, DictionaryItem> dicMap = new HashMap(16);
        dicList.forEach(z -> dicMap.put(z.getId() + "", z));
        list.forEach(item -> item.setDictionaryName(dicMap.get(item.getDictionaryId() + "").getName()));
        return list;
    }

    public Integer save(WxFansSaveForm wxFansSaveForm) {
        if (CollectionUtils.isNotEmpty(wxFansSaveForm.getFansBind())) {
            wxFansBindBiz.batchInsert(wxFansSaveForm.getFansBind());
        }
        WxFans wxFans = wxFansSaveForm.getWxFans();
        WxFans register = getRegister(wxFans.getOpenId());
        if (register != null) {
            wxFans.setId(register.getId());
            return mapper.updateByPrimaryKeySelective(wxFans);
        } else {
            return mapper.insertSelective(wxFansSaveForm.getWxFans());
        }
    }

    public WxFans getRegister(String openId) {
        Example example = new Example(WxFans.class);
        example.createCriteria().andEqualTo("openId", openId);
        return mapper.selectOneByExample(example);
    }

    public WxFans getOwnWxFans(WxUserQuery query) {
        Example example = new Example(WxFans.class);
        example.selectProperties("registerName", "registerMobile", "headImgurl", "country", "province", "city", "patientId", "sex", "subscribe","unionId");
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(query.getOpenId())) {
            criteria.andEqualTo("openId", query.getOpenId());
        }
        if (query.getPatientId() != null) {
            criteria.andEqualTo("patientId", query.getPatientId());
        }
        return mapper.selectOneByExample(example);
    }

    public int update(WxFansUpdateForm wxFansUpdateForm) {
        WxFans wxFans = new WxFans();
        wxFans.setId(wxFansUpdateForm.getId());
        wxFans.setRemark(wxFansUpdateForm.getRemark());
        wxFans.setUpdTime(new Date());
        return mapper.updateByPrimaryKeySelective(wxFans);
    }

    public WxPatientVo getWxPatientInfo(Integer patientId) {
        //患者基础信息
        PatientBaseInfoVo baseInfoVo = baseInfoMapper.selectPatienInfoById(patientId);
        //患者的基础扩展信息
        PatientExpInfo expInfo = this.getPatientExpInfo(patientId);
        //患者疾病扩展信息
        List<PatientExtInfoVo> extInfoVos = extInfoMapper.patientExtInfoListByid(patientId);
        WxUserQuery query = new WxUserQuery();
        query.setPatientId(patientId);
        //患者的wx信息
        WxFans ownWxFans = this.getOwnWxFans(query);
        WxPatientVo wxPatientVo = BeanCopierUtils.generalCopyBean(baseInfoVo, WxPatientVo.class);
        if (StringUtils.isNotBlank(baseInfoVo.getFaceUrl())) {
            wxPatientVo.setHeadImgUrl(baseInfoVo.getFaceUrl());
        }
        if (StringUtils.isNotBlank(baseInfoVo.getName())) {
            wxPatientVo.setUserName(baseInfoVo.getName());
        }
        if (expInfo != null) {
            //家庭住址
            wxPatientVo.setAddress(expInfo.getProvince() + expInfo.getCity() + expInfo.getCountry() + expInfo.getAddress());
        }
        if (CollectionUtils.isNotEmpty(extInfoVos)) {
            //设置疾病史，过敏原
            this.assembleMedical(extInfoVos, wxPatientVo);
        }
        if (ownWxFans != null) {
            if (StringUtils.isBlank(wxPatientVo.getHeadImgUrl())) {
                wxPatientVo.setHeadImgUrl(ownWxFans.getHeadImgurl());
            }
            if (StringUtils.isBlank(wxPatientVo.getAddress())) {
                wxPatientVo.setAddress(ownWxFans.getCountry() + ownWxFans.getProvince() + ownWxFans.getCity());
            }
        }
        return wxPatientVo;
    }

    public List<WxCardUseVo> listPatientCardRecord(String cardNumber, Integer type) {
        RechargeRecordQueryForm recordQueryForm = new RechargeRecordQueryForm();
        MemberExpendRecordQueryForm queryForm = new MemberExpendRecordQueryForm();
        MemberReturnRecordQueryForm returnRecordQueryForm = new MemberReturnRecordQueryForm();
        PrepaidRechargeRecordQueryForm preRechargeForm = new PrepaidRechargeRecordQueryForm();
        PrepaidExpendRecordQueryForm preExpendForm = new PrepaidExpendRecordQueryForm();
        PrepaidMeturnRecordQueryForm preRefundForm = new PrepaidMeturnRecordQueryForm();
        List<WxCardUseVo> list = Lists.newArrayList();
        //会员卡记录
        if (type == 1) {
            recordQueryForm.setWhetherPage(false);
            recordQueryForm.setCardNumber(cardNumber);
            List<RechargeRecordVo> rechargeRecordVoList = patientMemberInfoBiz.rechargeRecord(recordQueryForm).getList();
            if (CollectionUtils.isNotEmpty(rechargeRecordVoList)) {
                list.addAll(rechargeRecordVoList.stream().map(obj -> {
                    WxCardUseVo cardUseVo = new WxCardUseVo();
                    cardUseVo.setAmount("+" + this.setAmount(obj.getRechargePrincipal(), obj.getRechargeBonus()));
                    cardUseVo.setOperateTypeName("充值");
                    cardUseVo.setOperatingTime(obj.getOperatingTime());
                    cardUseVo.setOperatorName(obj.getOperatorName());
                    return cardUseVo;
                }).collect(Collectors.toList()));
            }
            queryForm.setMemberId(cardNumber);
            queryForm.setWhetherPage(false);
            List<MemberExpendRecordVo> expendList = patientMemberInfoBiz.expendList(queryForm).getList();
            if (CollectionUtils.isNotEmpty(expendList)) {
                list.addAll(expendList.stream().map(obj -> {
                    WxCardUseVo cardUseVo = new WxCardUseVo();
                    cardUseVo.setAmount("-" + this.setAmount(obj.getExpendPrincipal(), obj.getExpendGift()));
                    cardUseVo.setOperateTypeName("消费");
                    cardUseVo.setOperatingTime(obj.getOperatingTime());
                    cardUseVo.setOperatorName(obj.getOperatorName());
                    return cardUseVo;
                }).collect(Collectors.toList()));
            }
            returnRecordQueryForm.setMemberId(cardNumber);
            returnRecordQueryForm.setWhetherPage(false);
            List<MemberReturnRecordVo> refundList = patientMemberInfoBiz.refundList(returnRecordQueryForm).getList();
            if (CollectionUtils.isNotEmpty(refundList)) {
                list.addAll(refundList.stream().map(obj -> {
                    WxCardUseVo cardUseVo = new WxCardUseVo();
                    cardUseVo.setAmount("-" + this.setAmount(obj.getReturnPrincipalAmount(), obj.getReturnGiftAmount()));
                    cardUseVo.setOperateTypeName("退费");
                    cardUseVo.setOperatingTime(obj.getOperatingTime());
                    cardUseVo.setOperatorName(obj.getOperatorName());
                    return cardUseVo;
                }).collect(Collectors.toList()));
            }
        }
        if (type == 2) {
            preRechargeForm.setWhetherPage(false);
            preRechargeForm.setPrepaidId(cardNumber);
            List<PrepaidRechargeRecordVo> rechargeList = prepaymentRelationBiz.rechargeRecord(preRechargeForm).getList();
            if (CollectionUtils.isNotEmpty(rechargeList)) {
                list.addAll(rechargeList.stream().map(obj -> {
                    WxCardUseVo cardUseVo = new WxCardUseVo();
                    cardUseVo.setAmount("+" + this.setAmount(obj.getRechargePrincipal(), obj.getRechargeBonus()));
                    cardUseVo.setOperateTypeName("充值");
                    cardUseVo.setOperatingTime(obj.getOperatingTime());
                    cardUseVo.setOperatorName(obj.getOperatorName());
                    return cardUseVo;
                }).collect(Collectors.toList()));
            }
            preExpendForm.setWhetherPage(false);
            preExpendForm.setPrepaidId(cardNumber);
            List<PrepaidExpendRecordVo> expendList = prepaymentRelationBiz.expendList(preExpendForm).getList();
            if (CollectionUtils.isNotEmpty(expendList)) {
                list.addAll(expendList.stream().map(obj -> {
                    WxCardUseVo cardUseVo = new WxCardUseVo();
                    cardUseVo.setAmount("-" + this.setAmount(obj.getExpendPrincipal(), obj.getExpendGift()));
                    cardUseVo.setOperateTypeName("消费");
                    cardUseVo.setOperatingTime(obj.getOperatingTime());
                    cardUseVo.setOperatorName(obj.getOperatorName());
                    return cardUseVo;
                }).collect(Collectors.toList()));
            }
            preRefundForm.setWhetherPage(false);
            preRefundForm.setPrepaidId(cardNumber);
            List<PrepaidMeturnRecordVo> refundList = prepaymentRelationBiz.refundList(preRefundForm).getList();
            if (CollectionUtils.isNotEmpty(refundList)) {
                list.addAll(refundList.stream().map(obj -> {
                    WxCardUseVo cardUseVo = new WxCardUseVo();
                    cardUseVo.setAmount("-" + this.setAmount(obj.getReturnRrincipalAmount(), obj.getReturnGiftAmount()));
                    cardUseVo.setOperateTypeName("退费");
                    cardUseVo.setOperatingTime(obj.getOperatingTime());
                    cardUseVo.setOperatorName(obj.getOperatorName());
                    return cardUseVo;
                }).collect(Collectors.toList()));
            }
        }
        return list;
    }

    public WxFans getPushWxUser(Integer patientId) {
        Example example = new Example(WxFans.class);
        example.createCriteria().andEqualTo("patientId", patientId);
        WxFans wxFans = mapper.selectOneByExample(example);
        if (wxFans != null) {
            return wxFans;
        }
        Example bindExample = new Example(WxFansBind.class);
        bindExample.createCriteria().andEqualTo("patientId", patientId);
        List<WxFansBind> list = wxFansBindMapper.selectByExample(bindExample);
        if (CollectionUtils.isNotEmpty(list)) {
            return this.getRegister(list.get(0).getOpenId());
        }
        return null;
    }

    public List<WxFans> getPushWxUser(List<Integer> patientIds) {
        return wxFansBindMapper.listWxUsers(patientIds);
    }

    public void saveMiniAuth(WxSaveFansForm form) {
        Integer fansId = form.getFansId();
        WeChatSessionBO sessionBO = form.getSessionBO();
        WxUserInfoForm userInfo = form.getUserInfo();
        //授权保存用户
        WxFans wxFans = BeanUtil.copy(sessionBO, WxFans.class);
        //更新用户扩展信息
        BeanUtil.copy(userInfo, wxFans);
        wxFans.setSex(userInfo.getGender().shortValue());
        wxFans.setLanguage(userInfo.getLanguage());
        wxFans.setHeadImgurl(userInfo.getAvatarUrl());
        wxFans.setLastLoginDate(new Date());
        if (Objects.nonNull(fansId)) {
            wxFans.setId(fansId);
            super.updateSelectiveById(wxFans);
        } else {
            super.insert(wxFans);
        }
    }

    public void saveOrUpdate(WxFans wxFans) {
        if (Objects.nonNull(wxFans)) {
            mapper.updateByPrimaryKeySelective(wxFans);
        } else {
            mapper.insertSelective(wxFans);
        }
    }

    private String setAmount(BigDecimal principal, BigDecimal bonus) {
        BigDecimal zero = BigDecimal.ZERO;
        principal = principal == null ? zero : principal;
        bonus = bonus == null ? zero : bonus;
        return principal.add(bonus).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private void assembleMedical(List<PatientExtInfoVo> extInfoVos, WxPatientVo wxPatientVo) {
        Map<Byte, String> dictMap = extInfoVos.stream()
                .peek(obj -> {
                    //有itemId的 1-疾病史；2-过敏原
                    if (obj.getDictItemId() != null) {
                        DictionaryItem item = remoteSystemServiceFeign.findDictionaryItemById(obj.getDictItemId());
                        if (item != null) {
                            obj.setDescription(item.getName());
                        }
                    }
                })
                .collect(Collectors.groupingBy(PatientExtInfoVo::getType
                        , Collectors.mapping(PatientExtInfoVo::getDescription
                                , Collectors.joining(","))));
        dictMap.forEach((k, v) -> {
            if (k == 1) {
                wxPatientVo.setMedicalHistory(v);
            }
            if (k == 2) {
                wxPatientVo.setAllergen(v);
            }
        });
    }

    private PatientExpInfo getPatientExpInfo(Integer patientId) {
        Example example = new Example(PatientExpInfo.class);
        example.selectProperties("address", "province", "city", "country");
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo("patientId", patientId);
        return expInfoMapper.selectOneByExample(example);
    }

    public Integer getPatientIdByUonId(String UnionId) {
        WxFans wxFans = new WxFans();
        wxFans.setUnionId(UnionId);
        List<WxFans>list = mapper.select(wxFans);
        for(WxFans w:list){
            if(w.getPatientId()!=null){
                return w.getPatientId();
            }
        }
        return null;
    }
}
