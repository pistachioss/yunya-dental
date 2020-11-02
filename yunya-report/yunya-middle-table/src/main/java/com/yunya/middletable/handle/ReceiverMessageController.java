package com.yunya.middletable.handle;

import com.rabbitmq.client.Channel;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.middletable.service.*;
import com.yunya.middletable.service.patient.BasePatientBiz;
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
  /**账单退费*/
  @Autowired private BaseRefundBiz refundBiz;

  @Autowired private BaseBenefitServiceImpl baseBenefitService;

  @Resource
  private BaseCardServiceImpl baseCardService;

  @RabbitHandler
  public void handleMiddleSingle(MessageModel messageModel, Channel channel, Message message)
      throws Exception {
    // 处理消息
    log.info("handleMessage[{}]", messageModel);
    int result = 0;
    try {
      switch (messageModel.getMsgCategoryEnum()) {
        case BaseOrganization:
          organizationBiz.operateOrganization(messageModel);
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
        case BaseTariffInfo:
          tariffInfoBiz.operateTariff(messageModel);
          break;
        case BaseTreatmentProcess:
          treatmentProcessBiz.operateTreatmentProcess(messageModel);
          break;
        case BaseBill:
          billBiz.operateBill(messageModel);
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
        default:
          break;
      }
    } catch (Exception e) {
      //            result = 2;
    }
    switch (result) {
      case 0:
        // 消费成功：确认收到消息，消息将被队列移除，false只确认当前consumer一个消息收到，true确认所有consumer获得的消息。
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        break;
      case 1:
        // 确认否定消息：第一个boolean表示一个consumer还是所有，第二个boolean表示requeue是否重新回到队列，true重新入队。
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        break;
      case 2:
        // 拒绝消息：requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列。
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        break;
      default:
        break;
    }
  }
}
