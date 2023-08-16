package com.yunya.middletable.handle;

import com.rabbitmq.client.Channel;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.middletable.service.*;
import com.yunya.middletable.service.emr.TreatPlanDetailBiz;
import com.yunya.middletable.service.patient.*;
import com.yunya.middletable.service.treatment_other.BaseEmployeeScheduleBiz;
import com.yunya.middletable.service.treatment_other.BaseReturnVisitBiz;
import com.yunya.middletable.service.treatment_other.BaseVisitRemindBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RabbitListener(queues = {"DirectQueue_MiddleSingle"})
@Slf4j
public class ReceiverMessageController {
  /** 组织 */
  @Autowired private BaseOrganizationBiz organizationBiz;
  /** 入账方式 */
  @Autowired private BaseAccountItemBiz accountItemBiz;
  /** 员工 */
  @Autowired private BaseEmployeeBiz employeeBiz;
  /** 员工可登录组织 */
  @Autowired private BaseUserPostBiz userPostBiz;
  /** 患者 */
  @Autowired private BasePatientBiz basePatientBiz;
  /** 开单项目 */
  @Autowired private BaseTariffInfoBiz tariffInfoBiz;
  /** 就诊流程 */
  @Autowired private BaseTreatmentProcessBiz treatmentProcessBiz;
  /** 账单 */
  @Autowired private BaseBillBiz billBiz;
  /** 账单收费记录 */
  @Autowired private BaseBillPayBiz billPayBiz;
  /** 账单退费 */
  @Autowired private BaseRefundBiz refundBiz;

  @Autowired private BaseBenefitServiceImpl baseBenefitService;

  @Resource private BaseCardServiceImpl baseCardService;

  @Resource private BaseCouponServiceImpl baseCouponService;

  @Resource private BaseCouponItemServiceImpl baseCouponItemService;

  @Autowired private BasePatientMemberBiz basePatientMemberBiz;

  @Autowired private BasePatientMemberRelationBiz basePatientMemberRelationBiz;

  @Autowired private BasePatientMemberOccurLogBiz basePatientMemberOccurLogBiz;

  @Autowired private BaseVisitRemindBiz baseVisitRemindBiz;

  @Autowired private BaseReturnVisitBiz baseReturnVisitBiz;

  @Autowired private BaseEmployeeScheduleBiz baseEmployeeScheduleBiz;

  @Autowired private BaseAppointmentModifyBiz appointmentModifyBiz;

  @Autowired private BasePatientOriginBiz basePatientOriginBiz;

  @Autowired private BasePatientOriginLogBiz basePatientOriginLogBiz;

  @Autowired private TreatPlanDetailBiz treatPlanDetailBiz;
  @Resource
  private BaseCouponBillBiz baseCouponBillBiz;
  @Resource
  private BaseCouponBillPayBiz baseCouponBillPayBiz;
  @Resource
  private BaseCouponRefundBiz baseCouponRefundBiz;

  @RabbitHandler
  public void handleMiddleSingle(MessageModel messageModel, Channel channel, Message message)
      throws Exception {
    log.info(
        "-----------------------------------------消息开始消费--------------------------------------------------");
    // 处理消息
    log.info("【消息体】：handleMessage[{}]", messageModel);
    int result = 0;
    try {
      switch (messageModel.getMsgCategoryEnum()) {
        case BaseOrganization:
          organizationBiz.operateOrganization(messageModel);
          break;
        case BaseAccountItem:
          accountItemBiz.operateAccountItem(messageModel);
          break;
        case BaseEmployee:
          employeeBiz.operateEmployee(messageModel);
          break;
        case BaseUserPost:
          userPostBiz.operateUserPost(messageModel);
          break;
        case BasePatient:
          basePatientBiz.operate(messageModel);
          break;
        case BasePatientMember:
          basePatientMemberBiz.operate(messageModel);
          break;
        case BasePatientMemberRelation:
          basePatientMemberRelationBiz.operate(messageModel);
          break;
        case BasePatientMemberOccurLog:
          basePatientMemberOccurLogBiz.operate(messageModel);
          break;
        case BaseTariffInfo:
          tariffInfoBiz.operateTariff(messageModel);
          break;
        case BaseTreatmentProcess:
          treatmentProcessBiz.operateTreatmentProcess(messageModel);
          break;
        case BaseBill:
          billBiz.operateBill(messageModel);
          break;
        case BaseBillDetail:
          billBiz.updateBaseBillDetail(messageModel);
          break;
        case BaseBillPay:
          billPayBiz.operateBillPay(messageModel);
          break;
        case BaseRefund:
          refundBiz.operateRefund(messageModel);
          break;
        case BaseBenefit:
          baseBenefitService.operateBaseBenefit(messageModel);
          break;
        case BaseCardBatch:
          baseCardService.operateBatch(messageModel);
          break;
        case BaseCardSingle:
          baseCardService.operateSingle(messageModel);
          break;
        case BaseCoupon:
          baseCouponService.operateBaseCoupon(messageModel);
          break;
        case BaseCouponItem:
          baseCouponItemService.operateBaseCouponItem(messageModel);
          break;
        case BaseVisitRemind:
          baseVisitRemindBiz.operate(messageModel);
          break;
        case BaseReturnVisit:
          baseReturnVisitBiz.operate(messageModel);
          break;
        case BaseEmployeeSchedule:
          baseEmployeeScheduleBiz.operateEmployeeSchedule(messageModel);
          break;
        case BaseAppointmentModify:
          appointmentModifyBiz.operateAppointmentModify(messageModel);
          break;
        case BasePatientOrigin:
          basePatientOriginBiz.operate(messageModel);
          break;
        case BasePatientOriginLog:
          basePatientOriginLogBiz.operate(messageModel);
          break;
        case TreatPlanDetail:
          treatPlanDetailBiz.operate(messageModel);
          break;
      case BaseCouponBill:
          baseCouponBillBiz.operateBill(messageModel);
          break;
      case BaseCouponPayBill:
          baseCouponBillPayBiz.operateBillPay(messageModel);
          break;
      case BaseCouponRefund:
          baseCouponRefundBiz.operateRefund(messageModel);
          break;
    default:
      log.info("消息中没有对应的枚举类型[{}]！", messageModel.getMsgCategoryEnum());
      break;
      }
    } catch (Exception e) {
      log.warn("【消费异常】:", e);
      result = 2;
    }
    switch (result) {
      case 0:
        // 消费成功：确认收到消息，消息将被队列移除，false只确认当前consumer一个消息收到，true确认所有consumer获得的消息。
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        log.info("【消费成功】消息已被确认");
        break;
      case 1:
        // 确认否定消息：第一个boolean表示一个consumer还是所有，第二个boolean表示requeue是否重新回到队列，true重新入队。
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        log.info("【消费否定】消息已被否定");
        break;
      case 2:
        // 拒绝消息：requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列。
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        log.info("【消费失败】消息已被拒绝");
        break;
      default:
        break;
    }
  }
}
