package com.yunya.feign.rabbitmq;

import com.yunya.feign.rabbitmq.factory.RabbitMqFallBackFactory;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_RABBIT_MQ,
    fallbackFactory = RabbitMqFallBackFactory.class)
public interface RemoteRabbitMqServiceFeign {

  /**
   * 发送更新中间表的消息
   *
   * @param messageModel 消息体
   * @return 成功返回ok
   */
  @RequestMapping(value = "/api/direct/single", method = RequestMethod.POST)
  String sendMessage(@RequestBody MessageModel messageModel);

  /**
   * 通过消息更新中间表
   *
   * @param dataId 准备更新的数据ID；键：id
   * @param operateType 操作类型：0-新增；1-更新；2-删除
   * @param msgCategoryEnum 对应更新表的枚举，决定调用哪个中间表更新业务
   * @return
   */
  @RequestMapping(value = "/api/direct/single1", method = RequestMethod.POST)
  String sendMessage(
      @RequestParam("dataId") Integer dataId,
      @RequestParam("operateType") Integer operateType,
      @RequestParam("msgCategoryEnum") MsgCategoryEnum msgCategoryEnum);

  /**
   * 通过消息更新中间表
   *
   * @param dataId 准备更新的数据ID；键：id
   * @param dateType 数据来源表定义，自行定义; 键：type
   * @param operateType 操作类型：0-新增；1-更新；2-删除
   * @param msgCategoryEnum 对应更新表的枚举，决定调用哪个中间表更新业务
   * @return
   */
  @RequestMapping(value = "/api/direct/single2", method = RequestMethod.POST)
  String sendMessage(
      @RequestParam("dataId") Integer dataId,
      @RequestParam("dateType") Integer dateType,
      @RequestParam("operateType") Integer operateType,
      @RequestParam("msgCategoryEnum") MsgCategoryEnum msgCategoryEnum);

  /**
   * ¬ 通过消息更新中间表
   *
   * @param paramMap map参数，自行封装
   * @param operateType 操作类型
   * @param msgCategoryEnum 中间表枚举
   * @return
   */
  @RequestMapping(value = "/api/direct/single3", method = RequestMethod.POST)
  String sendMessage(
      @RequestBody Map<String, Object> paramMap,
      @RequestParam("operateType") Integer operateType,
      @RequestParam("msgCategoryEnum") MsgCategoryEnum msgCategoryEnum);
}
