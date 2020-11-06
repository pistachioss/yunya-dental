package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.model.CustomerRegistrationModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientMemberInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientOriginMapper;
import com.yunya.modules.patient_central.mapper.PatientPrepaymentsInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 添加客户登记
     * @param customerRegistrationModel  客户登记
     * @return PatientBaseInfoVo
     */
    public PatientBaseInfoVo addPatient(CustomerRegistrationModel customerRegistrationModel) {
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        BeanUtils.copyProperties(customerRegistrationModel, patientBaseInfo);
        if (patientBaseInfo.getOriginId() != null) {
            PatientOrigin patientOrigin =
                    this.patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
            if (patientOrigin != null) {
                patientBaseInfo.setOriginType(patientOrigin.getOriginType());
            }
        }
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setCrtId(1);
        patientBaseInfo.setCrtName("客户登记");
        patientBaseInfo.setOrgId(35);
        mapper.insertPatientInfo(patientBaseInfo);

        PatientBaseInfoVo patientBaseInfoVo =
                mapper.selectPatientInfoByNameAndMobileAndOrgId(patientBaseInfo);
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
                    this.generateCardNumber(
                            "Y", "patient_prepayments_info", "prepayment_number"));
            patientPrepaymentsInfo.setCrtId(1);
            patientPrepaymentsInfo.setCrtName("管理员");
            this.patientPrepaymentsInfoMapper.insertSelective(patientPrepaymentsInfo);
            remoteRabbitMqServiceFeign.sendMessage(
                    patientPrepaymentsInfo.getId(), 1, 0, MsgCategoryEnum.BasePatientMember);
        }
    }


    /**
     * 生产会员卡号
     *
     * @param mark 会员号标识 H：会员卡，Y：预付款
     * @param tableName 数据库表名
     * @param column 表中列的名称
     * @return String 卡号
     */
    public String generateCardNumber(String mark, String tableName, String column) {
        String number =
                this.patientMemberInfoMapper.generateCardNumber(
                        35, tableName, column);
        String suffix = String.format("%06d", Integer.parseInt(number) + 1);
        // 获取门诊简称
        OrganizationInfo organizationInfo =
                this.remoteSystemServiceFeign.findOrgInfoByOrgId(35);
        if (organizationInfo != null) {
            return mark + organizationInfo.getClinicNumber() + suffix;
        }
        return null;
    }
}