package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.model.PatientTransferRecordModel;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import com.yunya.models.patient_central.PatientTransferRecord;
import com.yunya.modules.patient_central.mapper.PatientMemberInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientPrepaymentsInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientTransferRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.enums.PatientDepositAccountTypeEnum.NORMAL_PREPAYMENT;

/**
 * @author: chenlin
 * @date: 2023/7/13 15:22
 * @description:
 * @since: 1.0.0
 */
@Service
public class PatientTransferRecordBiz extends BaseBiz<PatientTransferRecordMapper, PatientTransferRecord> {

    /** 注入付款基本信息Mapper */
    @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

    @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

    @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

    @Transactional
    public void transferPrepaymentAccount(PatientTransferRecordModel model) {
        BigDecimal principal = model.getPrincipal();
        BigDecimal bonus = model.getBonus();
        if (StringHelper.isAllNull(principal, bonus)) {
            throw new ClientServiceException("转账失败，转账金额不能为空！", PARAMETERS_IS_ILLEGAL);
        }
        if (StringHelper.leZero(principal) && StringHelper.leZero(bonus)) {
            throw new ClientServiceException("转账失败，转账金额不能小于等于0！", PARAMETERS_IS_ILLEGAL);
        }
        String transferorNumber = model.getTransferorNumber();
        String acceptorNumber = model.getAcceptorNumber();
        if (StringHelper.equals(transferorNumber, acceptorNumber)) {
            throw new ClientServiceException("转账失败，转出账户不能是自己", PARAMETERS_IS_ILLEGAL);
        }
        // 转出
        transferPrepaymentAccount(transferorNumber, principal.negate(), bonus.negate());

        // 转入
        if (model.getOperateType() == 1) {
            transferPrepaymentAccount(model.getAcceptorNumber(), principal, bonus);
        } else {
            transferMemberAccount(acceptorNumber, principal, bonus);
        }
        if (StringHelper.isEmpty(acceptorNumber)) {
            throw new ClientServiceException("转账方式暂不支持", PARAMETERS_IS_ILLEGAL);
        }
        // 生成转出记录
        generateTransferRecord(transferorNumber, acceptorNumber, model, (byte)2);
        // 生成转入记录
        generateTransferRecord(acceptorNumber, transferorNumber, model, (byte)1);
    }

    /**
     * 生成转账记录
     *
     * @param transferorNumber
     * @param acceptorNumber
     * @param model
     * @param type
     */
    private void generateTransferRecord(String transferorNumber, String acceptorNumber, PatientTransferRecordModel model, Byte type) {
        PatientTransferRecord entity = new PatientTransferRecord();
        entity.setMainNumber(transferorNumber);
        entity.setMinorNumber(acceptorNumber);
        entity.setPrincipal(model.getPrincipal());
        entity.setBonus(model.getBonus());
        entity.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        entity.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        entity.setCrtTime(BaseContextHandler.getCurTime());
        entity.setRemark(model.getRemark());
        entity.setType(type);
        int i = mapper.insertSelective(entity);
        if (i != 1) {
            throw new ClientServiceException("转账失败，请稍后再试！", DATA_ERROR);
        }
        sendMemberLogMessages(entity.getId(), model.getOperateType(), 0, type + 6);
    }

    /**
     * 转账至预付款账号（转入或转出）
     *
     * @param cardNumber 卡号
     * @param principal 金额小于0位转出
     * @param bonus 金额小于0位转出
     * @return
     */
    private void transferPrepaymentAccount(String cardNumber, BigDecimal principal, BigDecimal bonus) {
        Date curTime = BaseContextHandler.getCurTime();
        System.out.println("跨服远程调用接口的时间: " + DateUtil.format(curTime));
        PatientPrepaymentsInfo entity = new PatientPrepaymentsInfo();
        entity.setType(NORMAL_PREPAYMENT.getType());
        entity.setInservice(true);
        entity.setPrepaymentNumber(cardNumber);
        PatientPrepaymentsInfo account = patientPrepaymentsInfoMapper.selectOne(entity);
        if (StringHelper.isNull(account)) {
            throw new ClientServiceException("转账失败，账户不存在", PARAMETERS_IS_ILLEGAL);
        }
        BigDecimal prepaymentPrincipal = account.getPrepaymentPrincipal();
        BigDecimal prepaymentBonus = account.getPrepaymentBonus();
        BigDecimal newPrincipal = prepaymentPrincipal.add(principal);
        if (StringHelper.ltZero(newPrincipal)) {
            throw new ClientServiceException("转账失败，账户本金不足", OPERATION_NOT_ALLOW);
        }
        BigDecimal newBonus = prepaymentBonus.add(bonus);
        if (StringHelper.ltZero(newBonus)) {
            throw new ClientServiceException("转账失败，账户赠金不足", OPERATION_NOT_ALLOW);
        }
        account.setPrepaymentPrincipal(newPrincipal);
        account.setPrepaymentBonus(newBonus);
        account.setUpdName(BaseContextHandler.getName());
        account.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        account.setUpdTime(curTime);

        Example condition = new Example(PatientPrepaymentsInfo.class);
        condition.createCriteria().andEqualTo("id", account.getId())
                .andEqualTo("prepaymentPrincipal", prepaymentPrincipal)
                .andEqualTo("prepaymentBonus", prepaymentBonus);
        int count = patientPrepaymentsInfoMapper.updateByExampleSelective(account, condition);
        if (count != 1) {
            throw new ClientServiceException("转账失败，请稍后再试", DATA_ERROR);
        }
    }

    /**
     * 转账至会员卡账号（转入或转出）
     *
     * @param cardNumber 卡号
     * @param principal 金额小于0位转出
     * @param bonus 金额小于0位转出
     * @return
     */
    private String transferMemberAccount(String cardNumber, BigDecimal principal, BigDecimal bonus) {
        Date curTime = BaseContextHandler.getCurTime();
        System.out.println("跨服远程调用接口的时间: " + DateUtil.format(curTime));
        PatientMemberInfo entity = new PatientMemberInfo();
        entity.setInservice(true);
        entity.setCardNumber(cardNumber);
        PatientMemberInfo account = patientMemberInfoMapper.selectOne(entity);
        if (StringHelper.isNull(account)) {
            throw new ClientServiceException("转账失败，账户不存在", PARAMETERS_IS_ILLEGAL);
        }
        BigDecimal memberPrincipal = account.getPrincipalAmount();
        BigDecimal memberBonus = account.getBonusAmount();
        account.setPrincipalAmount(memberPrincipal.add(principal));
        account.setBonusAmount(memberBonus.add(bonus));
        account.setUpdName(BaseContextHandler.getName());
        account.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        account.setUpdTime(curTime);

        Example condition = new Example(PatientMemberInfo.class);
        condition.createCriteria().andEqualTo("id", account.getId())
                        .andEqualTo("principalAmount", memberPrincipal)
                                .andEqualTo("bonusAmount", memberBonus);
        int count = patientMemberInfoMapper.updateByExampleSelective(account, condition);
        if (count != 1) {
            throw new ClientServiceException("转账失败，请稍后再试", DATA_ERROR);
        }
        return account.getCardNumber();
    }

    /**
     * 会员操作消息 参数模板
     *
     * @param id 操作LogId
     * @param type 类型：0-会员卡，1-预付款
     * @param operateType 操作类型: 0-新增，1-修改，2-删除
     * @param occurType Log类型: 1.充值 2.消费 3.退款 4.撤销收费 5.账单退费 6.账单返点 7.转账转入 8.转账转出
     */
    public void sendMemberLogMessages(
            Integer id, Integer type, Integer operateType, Integer occurType) {
        Map<String, Object> paramMap = new HashMap(3);
        paramMap.put("id", id);
        paramMap.put("type", type);
        paramMap.put("operationType", occurType);
        rabbitMqServiceFeign.sendMessage(
                paramMap, operateType, MsgCategoryEnum.BasePatientMemberOccurLog);
    }

}
