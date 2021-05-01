package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.model.CustomerRegistrationModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.patient_central.PatientOriginLog;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 客户登记业务层
 *
 * @author: WY
 * @date: 2020/11/5 17:49
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerRegistrationBiz extends BaseBiz<PatientBaseInfoMapper, PatientBaseInfo> {

    /** 注入患者Biz */
    @Autowired private PatientBaseInfoBiz  patientBaseInfoBiz;

    /** 注入患者来源Mapper */
    @Autowired private PatientOriginMapper patientOriginMapper;

    @Autowired private PatientMemberInfoBiz patientMemberInfoBiz;

    @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

    @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

    @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

    @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

    @Resource private PatientOriginLogMapper patientOriginLogMapper;

    /**
     * 添加客户登记
     * @param customerRegistrationModel  客户登记
     * @return PatientBaseInfoVo
     */
    public PatientBaseInfoVo addPatient(CustomerRegistrationModel customerRegistrationModel) {
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        int type = 2;
        BeanUtils.copyProperties(customerRegistrationModel, patientBaseInfo);
        if (patientBaseInfo.getOriginId() != null) {
            if (patientBaseInfo.getOriginType() > type){
                PatientOrigin patientOrigin =
                        this.patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
                if (patientOrigin == null) {
                    throw new ClientServiceException("该患者来源不存在", DATA_NOT_EXIST);
                }
                if (patientOrigin.getTimeLimit() ==1) {
                    Date curDate = DateUtil.getCurrentDate();
                    Date startDate = DateUtil.toDate(patientOrigin.getLimitStartDate());
                    Date endDate = DateUtil.toDate(patientOrigin.getLimitEndDate());
                    if (curDate.before(startDate) || curDate.after(endDate)) {
                        throw new ClientServiceException("该患者来源已过期", PARAMETERS_IS_ILLEGAL);
                    }
                }
            }
        }
        // 设置患者登记默认的门诊为总院
        patientBaseInfo.setOrgId(39);
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setCrtId(1);
        patientBaseInfo.setCrtName("客户登记");
        mapper.insertPatientInfo(patientBaseInfo);
        PatientBaseInfoVo patientBaseInfoVo = mapper.selectPatienInfoById(patientBaseInfo.getId());
        if (patientBaseInfoVo.getOriginId() != null) {
            PatientOriginLog patientOriginLog = new PatientOriginLog();
            patientOriginLog.setPatientId(patientBaseInfoVo.getId());
            patientOriginLog.setOriginType(patientBaseInfo.getOriginType());
            patientOriginLog.setOriginId(patientBaseInfo.getOriginId());
            patientOriginLog.setInservice(patientBaseInfo.getInservice());
            patientOriginLog.setCrtId(patientBaseInfo.getCrtId());
            patientOriginLog.setCrtName(patientBaseInfo.getCrtName());
            patientOriginLog.setCrtTime(patientBaseInfo.getCrtTime());
            patientOriginLog.setUptId(patientBaseInfo.getUptId());
            patientOriginLog.setUpdName(patientBaseInfo.getUpdName());
            patientOriginLog.setUpdTime(patientBaseInfo.getUpdTime());
            patientOriginLogMapper.insertSelective(patientOriginLog);
            remoteRabbitMqServiceFeign.sendMessage(patientOriginLog.getId(), 0, MsgCategoryEnum.BasePatientOriginLog);
        }
        // 创建预付款 并发送消息
        patientBaseInfoBiz.sendMessages(patientBaseInfo.getId(), 0);
        addPatientPrepaymentsInfo(patientBaseInfo);
        return patientBaseInfoVo;
    }

    /**
     * 添加患者时,创建预付款账户
     *
     * @param patientBaseInfo 患者信息
     */
    public void addPatientPrepaymentsInfo(PatientBaseInfo patientBaseInfo) {
        if (patientBaseInfo.getId() != null) {
            PatientPrepaymentsInfo patientPrepaymentsInfo = new PatientPrepaymentsInfo();
            patientPrepaymentsInfo.setOrgId(patientBaseInfo.getOrgId());
            patientPrepaymentsInfo.setPatientId(patientBaseInfo.getId());
            // 预付款卡号生成规则 开通Y
            patientPrepaymentsInfo.setPrepaymentNumber(
                    this.generateCardNumber("Y",patientBaseInfo.getOrgId()));
            patientPrepaymentsInfo.setCrtId(1);
            patientPrepaymentsInfo.setCrtName("管理员");
            this.patientPrepaymentsInfoMapper.insertSelective(patientPrepaymentsInfo);
            remoteRabbitMqServiceFeign.sendMessage(
                    patientPrepaymentsInfo.getId(), 1, 0, MsgCategoryEnum.BasePatientMember);
        }
    }


    /**
     * 生产预付款卡号
     *
     * @param mark 会员号标识 H：会员卡，Y：预付款
     * @return String 卡号
     */
    public String generateCardNumber(String mark, Integer orgId) {
    String number = this.patientMemberInfoMapper.generateCardNumber4Prepay(39);
        String suffix = String.format("%06d", Integer.parseInt(number) + 1);
        // 获取门诊简称
        OrganizationInfo organizationInfo =
                this.remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
        if (organizationInfo != null) {
            return mark + organizationInfo.getClinicNumber() + suffix;
        }
        return null;
    }
}