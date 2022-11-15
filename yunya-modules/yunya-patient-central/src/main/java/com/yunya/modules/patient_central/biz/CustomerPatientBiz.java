package com.yunya.modules.patient_central.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.vo.PatientCardBaseVo;
import com.yunya.feign.middle.RemoteMiddleServiceFeign;
import com.yunya.feign.patient_central.domain.query.CustomerBindPatientQueryForm;
import com.yunya.feign.patient_central.domain.query.CustomerPatientQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.enums.PatientTrajectoryEventEnum;
import com.yunya.framework.common.utils.*;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.report.CreditsShop;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.PatientExpInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientOriginMapper;
import com.yunya.modules.patient_central.mapper.WxFansBindMapper;
import com.yunya.modules.patient_central.mapper.WxFansMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * @author: chenlin
 * @date: 2022/11/7 10:46
 * @description:
 * @since: 1.0.0
 */
@Service
public class CustomerPatientBiz {

    @Autowired
    private PatientBaseInfoBiz patientBaseInfoBiz;
    @Autowired
    private PatientOriginMapper patientOriginMapper;
    @Autowired
    private PatientExpInfoMapper patientExpInfoMapper;
    @Autowired
    private WxFansBindMapper wxFansBindMapper;
    @Autowired
    private RemoteMiddleServiceFeign remoteMiddleServiceFeign;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private RemoteAppointmentFeign remoteAppointmentFeign;
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Autowired
    private WxFansMapper wxFansMapper;
    @Autowired
    private PatientMemberInfoBiz patientMemberInfoBiz;
    @Autowired
    private PatientPrepaymentRelationBiz patientPrepaymentBiz;
    @Autowired
    private RemoteDiscountFeign remoteDiscountFeign;
    public static String QIN_SHU_GUAN_XI = "亲属关系";


    /**
     * 根据患者id查询患者基础资料
     *
     * @param patientId
     * @return
     */
    public PatientSimpleInfoVO findPatientSimpleInfo(Integer patientId) {
        PatientBaseInfo patientBaseInfo = this.patientBaseInfoBiz.selectById(patientId);
        if (StringHelper.isNull(patientBaseInfo)) {
            return null;
        }
        PatientSimpleInfoVO result = new PatientSimpleInfoVO();
        BeanUtil.copyProperties(patientBaseInfo, result);
        result.setPatientName(patientBaseInfo.getName());
        result.setPatientId(patientId);
        Date birthday = patientBaseInfo.getBirthday();
        if (StringHelper.isNotNull(birthday)) {
            // 计算年龄
            Integer age = DateUtil.differFromDate(birthday, new Date(System.currentTimeMillis()));
            patientBaseInfo.setAge(age);
            String timeStr = new DateTime(birthday).toString("yyyy-MM-dd");
            result.setBirthday(timeStr);
        }
        int originType = 2;
        if (patientBaseInfo.getOriginType() != null && patientBaseInfo.getOriginType().intValue() > originType) {
            PatientOrigin patientOrigin =
                    patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
            if (patientOrigin != null) {
                // 获取患者来源的父级id
                result.setSourceParentId(patientOrigin.getParentId());
                result.setSourceName(patientOrigin.getName());
            }
        }
        getTypeName(result);
        PatientExpInfoVo patientExpInfoVo = patientExpInfoMapper.selectByPatientId(patientId);
        if (StringHelper.isNotNull(patientExpInfoVo)) {
            result.setAddress(patientExpInfoVo.getAddress());
        }
        CreditsShop creditsShop = remoteMiddleServiceFeign.lastPatientCredits(patientId).getData();
        if (StringHelper.isNotNull(creditsShop)) {
            result.setCreditsAccount(creditsShop.getCreditsAccount());
        }
        return result;
    }

    /**
     * 获取 来源名称 推荐人名称 推荐来源名称
     *
     * @param patientBaseInfoVo 患者信息
     * @return PatientBaseInfoVo
     */
    public PatientSimpleInfoVO getTypeName(PatientSimpleInfoVO patientBaseInfoVo) {
        if (patientBaseInfoVo.getOriginType() != null) {
            PatientOrigin patientOrig;
            PatientOrigin patientOrigin =
                    patientOriginMapper.getTypeName(patientBaseInfoVo.getOriginType());
            if (patientOrigin != null) {
                patientBaseInfoVo.setOriginTypeName(patientOrigin.getName());
            }
            if (patientBaseInfoVo.getOriginId() != null) {
                switch (patientBaseInfoVo.getOriginType()) {
                    // 查询员工
                    case 1:
                        SysUserEmployeeModel model = new SysUserEmployeeModel();
                        model.setUserId(patientBaseInfoVo.getOriginId());
                        model.setWhetherPage(false);
                        List<SysUserInfoDetail> list =
                                remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
                        if (!StringHelper.isEmpty(list)) {
                            patientBaseInfoVo.setOriginName(list.get(0).getName());
                        }
                        //              patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
                        patientOrig = patientOriginMapper.getTypeName(1);
                        if (patientOrig != null) {
                            patientBaseInfoVo.setSourceName(patientOrig.getName());
                        }
                        patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
                        break;
                    // 查询患者
                    case 2:
                        PatientBaseInfo patientBaseInfo =
                                patientBaseInfoBiz.selectById(patientBaseInfoVo.getOriginId());
                        if (patientBaseInfo != null) {
                            patientBaseInfoVo.setOriginName(patientBaseInfo.getName());
                        }
                        //              patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
                        patientOrig = patientOriginMapper.getTypeName(2);
                        if (patientOrig != null) {
                            patientBaseInfoVo.setSourceName(patientOrig.getName());
                        }
                        patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
                        break;
                    default:
                        PatientOrigin activity =
                                patientOriginMapper.selectByPrimaryKey(patientBaseInfoVo.getOriginId());
                        if (activity != null) {
                            if (activity.getSourceAttribute() != null) {
                                DictionaryItem dictionaryItemById =
                                        remoteSystemServiceFeign.findDictionaryItemById(activity.getSourceAttribute());
                                if (dictionaryItemById != null) {
                                    patientBaseInfoVo.setOriginName(
                                            dictionaryItemById.getName() + "-" + activity.getName());
                                    patientBaseInfoVo.setSourceName(
                                            dictionaryItemById.getName() + "-" + activity.getName());
                                } else {
                                    patientBaseInfoVo.setOriginName(activity.getName());
                                    patientBaseInfoVo.setSourceName(activity.getName());
                                }
                            }
                        }
                        break;
                }
            }
        }
        Integer mobileOwner = patientBaseInfoVo.getMobileOwner();
        if (!ObjectUtils.isEmpty(mobileOwner)) {
            // 根据ID查询字典明细
            DictionaryItem dictionaryItem =
                    remoteSystemServiceFeign.findDictionaryItemById(mobileOwner);
            if (dictionaryItem != null) {
                // 手机号所属名称
                patientBaseInfoVo.setMobileOwnerName(dictionaryItem.getName());
            }
        }
        return patientBaseInfoVo;
    }

    /**
     * 根据患者id查询患者推荐人列表
     *
     * @param query
     * @return
     */
    public PageInfo<PatientSimpleRefererVO> findPatientReferrerList(CustomerPatientQueryForm query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        PatientBaseInfo introducer = patientBaseInfoBiz.findPatientIntroducerByPatientId(query.getPatientId());
        List<PatientSimpleRefererVO> result = new ArrayList<>();
        if (StringHelper.isNotNull(introducer)) {
            PatientSimpleRefererVO referer = new PatientSimpleRefererVO();
            referer.setName(introducer.getName());
            referer.setGender(introducer.getGender());
            referer.setMobile(introducer.getMobile());
            result.add(referer);
        }
        return new PageInfo<>(result);
    }

    /**
     * 根据患者id查询患者动态列表
     *
     * @param query
     * @return
     */
    public PageInfo<PatientTrajectoryVO> findPatientTrajectoryList(CustomerPatientQueryForm query) {
        // 患者登记
        PatientBaseInfo patient = patientBaseInfoBiz.selectById(query.getPatientId());
        List<PatientTrajectoryVO> patientTrajectory = convertPatientTrajectory(patient);
        // 预约记录
        List<PatientEventVO>  appointTrajectory = remoteAppointmentFeign.findPatientAppointTrajectory(query.getPatientId());
        addTrajectory(patientTrajectory, appointTrajectory);
        // 就诊记录
        List<PatientEventVO> treatTrajectory = remoteTreatmentServiceFeign.findPatientTreatmentTrajectory(query.getPatientId());
        addTrajectory(patientTrajectory, treatTrajectory);
        // 卡券激活记录
        List<PatientEventVO> cardTrajectory = remoteDiscountFeign.findPatientCardTrajectory(query.getPatientId());
        for (PatientEventVO vo : cardTrajectory) {
            PatientTrajectoryVO traject = new PatientTrajectoryVO();
            traject.setTimePoint(vo.getTimePoint());
            String content = PatientTrajectoryEventEnum.getContentByCode(vo.getEventCode());
            content += vo.getEffectBody();
            traject.setContent(content);
            patientTrajectory.add(traject);
        }
        patientTrajectory = SortUtil.sort(patientTrajectory, Comparator.comparing(PatientTrajectoryVO::getTimePoint).reversed());
        return PageUtl.doPage(query, patientTrajectory);
    }

    private void addTrajectory(List<PatientTrajectoryVO> patientTrajectory, List<PatientEventVO> trajectories) {
        for (int i = 0; i < trajectories.size(); i++) {
            PatientEventVO trajectory = trajectories.get(i);
            String content = findAbbreviation(trajectory.getOrgId());
            if (i == 0) {
                content += "首次";
            }
            content += PatientTrajectoryEventEnum.getContentByCode(trajectory.getEventCode());
            PatientTrajectoryVO trajectVO = new PatientTrajectoryVO();
            trajectVO.setContent(content);
            trajectVO.setTimePoint(trajectory.getTimePoint());
            patientTrajectory.add(trajectVO);
        }
    }

    private List<PatientTrajectoryVO> convertPatientTrajectory(PatientBaseInfo patient) {
        List<PatientTrajectoryVO> list = new ArrayList<>();
        if (StringHelper.isNotNull(patient)) {
            Integer eventCode = PatientTrajectoryEventEnum.PATIENT_REGISTE.getEventCode();
            PatientTrajectoryVO trajectory = new PatientTrajectoryVO();
            trajectory.setTimePoint(patient.getCrtTime());
            String content = findAbbreviation(patient.getOrgId());
            content += PatientTrajectoryEventEnum.getContentByCode(eventCode);
            trajectory.setContent(content);
            list.add(trajectory);
        }
        return list;
    }

    private String findAbbreviation(Integer orgId) {
        OrganizationInfo org = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
        String abbreviation = "";
        if (StringHelper.isNotNull(org)) {
            abbreviation = "在" + org.getAbbreviation();
        }
        return abbreviation;
    }

    /**
     * 根据unionid查询绑定患者列表
     *
     * @param query
     * @return
     */
    public PageInfo<CustomerBindPatientVO> findBindPatientList(CustomerBindPatientQueryForm query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<CustomerBindPatientVO> result = new ArrayList<>();
        List<WxWechatbindAppListVO> list = wxFansBindMapper.findPatientBaseInfo(query.getUnionid());
        PageInfo page = new PageInfo(list);
        if (StringHelper.isNotEmpty(list)) {
            List<DictionaryItem> items = remoteSystemServiceFeign.findDictItemByTypeName(QIN_SHU_GUAN_XI);
            Map<Integer, String> itemNameMap = items.stream().collect(toMap(DictionaryItem::getId, DictionaryItem::getName));
            list.forEach(vo -> {
                CustomerBindPatientVO bindPatient = new CustomerBindPatientVO();
                BeanUtil.copyProperties(vo, bindPatient);
                Integer dictionaryId = vo.getDictionaryId();
                String itemName = itemNameMap.get(dictionaryId);
                if (StringHelper.isNotEmpty(itemName)) {
                    bindPatient.setBindShipName(itemName);
                    bindPatient.setShipId(dictionaryId);
                }
                result.add(bindPatient);
            });
            page.setList(result);
        }
        return page;
    }

    /**
     * 根据unionid查询微信用户已关注公众号和小程序信息
     *
     * @param unionid
     * @return
     */
    public JSONObject findWxFansSubscribeInfo(String unionid) {
        JSONObject result = new JSONObject();
        result.put("wxPubAccount", false);
        result.put("wxApplet", false);
        List<WxFansVo> fans = wxFansMapper.selectWxFansSubscibedList(unionid);
        fans.forEach(fan->{
            Integer sourceType = fan.getSourceType();
            if (StringHelper.isNotNull(sourceType)) {
                if (sourceType == 0) {// 公众号
                    result.put("wxPubAccount", true);
                } else if (sourceType == 1) {// 小程序
                    result.put("wxApplet", true);
                }
            }
        });
        return result;
    }

    /**
     * 根据patientId查询客户画像侧边栏的会员权益
     *
     * @param patientId
     * @return
     */
    public PatientVipRightInterestVO findPatientVipRightInterest(Integer patientId) {
        PatientVipRightInterestVO result = new PatientVipRightInterestVO();
        PatientCardBaseVo lastestActivedCard = remoteDiscountFeign.findPatientLastestActivedCardInfo(patientId);
        if (StringHelper.isNotNull(lastestActivedCard)) {
            result.setCouponName(lastestActivedCard.getCouponName());
            result.setUseDeadline(lastestActivedCard.getUseDeadline());
            result.setCouponLogo(lastestActivedCard.getCouponLogo());
        }

        MemberBaseInfoVo memberBaseInfo = patientMemberInfoBiz.findMemberBaseInfo(patientId);
        if (StringHelper.isNotNull(memberBaseInfo)) {
            result.setMemberTypeId(memberBaseInfo.getMemberTypeId());
            result.setMemberCardName(memberBaseInfo.getMemberCardName());
            result.setMemberCardMoneySum(memberBaseInfo.getMemberCardMoneySum());
        }

        PatientPrepaymentsInfoVo prepaymentInfo = patientPrepaymentBiz.findPrepaymentInfo(patientId);
        if (StringHelper.isNotNull(prepaymentInfo)) {
            result.setPrepaymentMoneySum(prepaymentInfo.getPrepaymentMoneySum());
        }

        PatientMemberRelationQueryForm query = new PatientMemberRelationQueryForm();
        query.setPatientId(patientId);
        MemberRelationVo memberBindingRelation = patientMemberInfoBiz.findMemberBindingRelation(query);
        if (StringHelper.isNotNull(memberBindingRelation)) {
            result.setMemberRelationList(memberBindingRelation.getMemberRelationList());
            result.setMemberBalanceRelationList(memberBindingRelation.getMemberBalanceRelationList());
        }
        return result;
    }
}
