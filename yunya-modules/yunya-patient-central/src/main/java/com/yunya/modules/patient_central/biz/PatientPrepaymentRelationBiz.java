package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PaymentRecordDetailQuery;
import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunya.framework.common.constant.BusinessConstants.*;

/**
 * 简单介绍:</br> 患者预付款
 *
 * @author: WY
 * @date 2020/7/31 9:31
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientPrepaymentRelationBiz
    extends BaseBiz<PatientPrepaymentRelationMapper, PatientPrepaymentRelation> {

  /** 注入预付款关联Mapper */
  @Autowired PatientPrepaymentRelationMapper patientPrepaymentRelationMapper;

  /** 注入付款基本信息Mapper */
  @Autowired PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  /** 注入预付款充值记录Mapper */
  @Autowired PrepaidRechargeTollRecordMapper prepaidRechargeTollRecordMapper;

  /** 注入预付款充值记录明细Mapper */
  @Autowired PrepaidRechargeRecordMapper prepaidRechargeRecordMapper;

  /** 注入患者信息Mapper */
  @Autowired PatientBaseInfoMapper patientBaseInfoMapper;

  /** 注入系统服务 */
  @Autowired RemoteSystemServiceFeign remoteSystemServiceFeign;

  /** 注入服务 */
  @Autowired RemoteDiscountFeign remoteDiscountFeign;

  /** 注入预付款退款信息Mapper */
  @Autowired PrepaidReturnRecordMapper prepaidReturnRecordMapper;

  /** 注入预付款消费记录Mapper */
  @Autowired PrepaidExpendRecordMapper prepaidExpendRecordMapper;

  /** 注入服务 */
  @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;
  /** 注入会员卡消费记录Mapper */
  @Autowired private MemberExpendRecordMapper memberExpendRecordMapper;

  /**
   * 患者预付款基本信息查询
   *
   * @return PatientPrepaymentRelationVo
   */
  public PatientPrepaymentsInfoVo findPrepaymentInfo(Integer id) {
    return patientPrepaymentsInfoMapper.findPrepaymentInfo(id);
  }

  /**
   * 预付款关联
   *
   * @param model 新增关联
   * @return ResponseResult
   */
  public ResponseResult addPrepaymentLink(PatientPrepaymentRelationModel model) {
    if (model.getMasterCardId().equals(model.getSecondaryCardId())) {
      return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL, "副卡人不能为患者本人", "");
    }
    PatientPrepaymentRelation patientPrepaymentRelation =
        this.patientPrepaymentRelationMapper.findBindingRelation(model);
    if (patientPrepaymentRelation != null) {
      return ResponseUtil.error("该副卡人已存在,不能重复绑定！", patientPrepaymentRelation);
    }
    PatientPrepaymentRelation patientPrepaymentRelationyi = new PatientPrepaymentRelation();
    BeanUtils.copyProperties(model, patientPrepaymentRelationyi);
    patientPrepaymentRelationyi.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientPrepaymentRelationyi.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientPrepaymentRelationyi.setCrtName(BaseContextHandler.getName());
    patientPrepaymentRelationMapper.insertSelective(patientPrepaymentRelationyi);
    // 发送预付款关联消息
    remoteRabbitMqServiceFeign.sendMessage(
        patientPrepaymentRelationyi.getId(), 1, 0, MsgCategoryEnum.BasePatientMemberRelation);
    PatientPrepaymentRelation patientPrepaymentRelationer = new PatientPrepaymentRelation();
    patientPrepaymentRelationer.setMasterCardId(model.getSecondaryCardId());
    patientPrepaymentRelationer.setSecondaryCardId(model.getMasterCardId());
    patientPrepaymentRelationer.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientPrepaymentRelationer.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientPrepaymentRelationer.setCrtName(BaseContextHandler.getName());
    patientPrepaymentRelationMapper.insertSelective(patientPrepaymentRelationer);
    // 发送预付款关联消息
    sendPrepaidRelationMessages(patientPrepaymentRelationer.getId(), 0);
    return ResponseUtil.success();
  }

  /**
   * 预付款关联消息 参数模板
   *
   * @param id 操作
   * @param OperateType 操作类型
   */
  public void sendPrepaidRelationMessages(Integer id, Integer OperateType) {
    MessageModel messageModel = new MessageModel();
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", id);
    map.put("type", 1);
    messageModel.setParamMap(map);
    messageModel.setOperateType(OperateType);
    messageModel.setMsgCategoryEnum(MsgCategoryEnum.BasePatientMemberRelation);
    remoteRabbitMqServiceFeign.sendMessage(messageModel);
  }

  /**
   * 预付款操作消息 参数模板
   *
   * @param id 操作LogId
   * @param operateType 操作类型
   * @param type 会员类型
   * @param operationType Log类型
   */
  public void sendPrepaidLogMessages(
      Integer id, Integer operateType, Integer type, Integer operationType) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("id", id);
    paramMap.put("type", type);
    paramMap.put("operationType", operationType);
    remoteRabbitMqServiceFeign.sendMessage(
        paramMap, operateType, MsgCategoryEnum.BasePatientMemberOccurLog);
  }

  /**
   * 查询预付款关联
   *
   * @param id 卡主本人id
   * @return List<PatientPrepaymetRelation>
   */
  public List<PatientPrepaymentRelationVo> findPrepaymentLink(Integer id) {
    List<PatientPrepaymentRelationVo> prepaymentLinkList =
        patientPrepaymentRelationMapper.findPrepaymentLinkList(id);
    if (!StringHelper.isEmpty(prepaymentLinkList)) {
      for (PatientPrepaymentRelationVo patientPrepaymentRelationVo : prepaymentLinkList) {
        PatientBaseInfo patientBaseInfo =
            patientBaseInfoMapper.selectByPrimaryKey(
                patientPrepaymentRelationVo.getSecondaryCardId());
        if (patientBaseInfo != null) {
          patientPrepaymentRelationVo.setSecondaryCardName(patientBaseInfo.getName());
        }
      }
    }
    return prepaymentLinkList;
  }

  /**
   * 删除预付款关联(双向删除)
   *
   * @param id 关联关系id
   */
  public void deletePrepaymentLink(Integer id) {
    PatientPrepaymentRelation patientPrepaymentRelation =
        patientPrepaymentRelationMapper.selectByPrimaryKey(id);
    if (patientPrepaymentRelation != null) {
      int prepaymentRelationId =
          patientPrepaymentRelationMapper.selectPrepaymentRelationId(patientPrepaymentRelation);
      patientPrepaymentRelationMapper.deletePrepaymentRelation(patientPrepaymentRelation);
      // 发送预付款删除消息
      remoteRabbitMqServiceFeign.sendMessage(
          prepaymentRelationId, 1, 2, MsgCategoryEnum.BasePatientMemberRelation);
      patientPrepaymentRelationMapper.delete(patientPrepaymentRelation);
      // 发送预付款删除消息
      sendPrepaidRelationMessages(patientPrepaymentRelation.getId(), 2);
    }
  }

  /**
   * 充值
   *
   * @param model
   */
  public ResponseResult recharge(PrepaidRechargeModel model) {
    // 查询预付款余额 增加余额
    PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidCard());
    if (patientPrepaymentsInfo != null) {
      BigDecimal rechargePrincipal = model.getRechargePrincipal();
      if (rechargePrincipal == null) {
        rechargePrincipal = new BigDecimal(0);
      }
      BigDecimal rechargeBonus = model.getRechargeBonus();
      if (rechargeBonus == null) {
        rechargeBonus = new BigDecimal(0);
      }
      patientPrepaymentsInfo.setPrepaymentPrincipal(
          patientPrepaymentsInfo.getPrepaymentPrincipal().add(rechargePrincipal));
      patientPrepaymentsInfo.setPrepaymentBonus(
          patientPrepaymentsInfo.getPrepaymentBonus().add(rechargeBonus));
      patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);

      // 添加预付款充值记录
      PrepaidRechargeRecord prepaidRechargeRecord = new PrepaidRechargeRecord();
      BeanUtils.copyProperties(model, prepaidRechargeRecord);
      prepaidRechargeRecord.setPrepaidId(model.getPrepaidCard());
      prepaidRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      prepaidRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidRechargeRecord.setCrtName(BaseContextHandler.getName());
      prepaidRechargeRecord.setCurrentRechargePrincipal(
          patientPrepaymentsInfo.getPrepaymentPrincipal());
      prepaidRechargeRecord.setCurrentRechargeBonus(patientPrepaymentsInfo.getPrepaymentBonus());
      prepaidRechargeRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidRechargeRecord.setUpdName(BaseContextHandler.getName());
      prepaidRechargeRecord.setRemarks(model.getPrepaidRechargeTollRecordModel().getRemarks());
      prepaidRechargeRecordMapper.insertSelective(prepaidRechargeRecord);

      // 添加预付款充值收费记录
      PrepaidRechargeTollRecord prepaidRechargeTollRecord = new PrepaidRechargeTollRecord();
      BeanUtils.copyProperties(
          model.getPrepaidRechargeTollRecordModel(), prepaidRechargeTollRecord);
      if (prepaidRechargeRecord.getRechargeBonus() == null){
        prepaidRechargeRecord.setRechargeBonus(new BigDecimal(0));
      }
      prepaidRechargeTollRecord.setCreditAmount(prepaidRechargeRecord.getRechargePrincipal().add(prepaidRechargeRecord.getRechargeBonus()));
      prepaidRechargeTollRecord.setRechargeRecordId(prepaidRechargeRecord.getId());
      prepaidRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      prepaidRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidRechargeTollRecord.setCrtName(BaseContextHandler.getName());
      prepaidRechargeTollRecordMapper.insertSelective(prepaidRechargeTollRecord);
      if (patientPrepaymentsInfo.getPatientId() != null && model.getCardId() != null){
        OwnCardActiveForm ownCardActiveForm = new OwnCardActiveForm();
        ownCardActiveForm.setCardId(model.getCardId());
        ResponseResult result = remoteDiscountFeign.ownActiveCard(patientPrepaymentsInfo.getPatientId(), ownCardActiveForm);
        if (!result.getStatus().equals(ZERO)) {
          return result;
        }
      }
      // 发送消息 预付款充值
      sendPrepaidLogMessages(prepaidRechargeRecord.getId(), 0, 1, 1);
    }
    return ResponseUtil.fail(OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到预付款记录", "");
  }

/*

对于资深车评人韩路而言，“折腾”绝对是他人生中的主题。

作为国内专业汽车网站“汽车之家”的3号员工，韩路见证了这个被称为中国第一汽车垂直网站的兴起。

而反过来，被称为业界神话的韩路，也给整个行业带来了巨大的影响——汽车网站曾经极其流行的“图说”形式，就是由他率先发起的。

韩路

在32岁那年，他决定离开自己一手创立的“汽车之家”，开启全新的人生道路。

在离职信中，他称这段工作经历是“最好的职业生涯”，而此时，他想走出自己的舒适圈，去挑战一下不同的人生。

过往都被留下，带走的只有他对于汽车的热爱。

离开汽车之家后，韩路开始把更多的时间用在汽车测评以及自己的《韩路游记》中。十几年间，韩路几乎开着车跑遍了中国各种有故事的地方，一边欣赏风光，一边对车进行测评。

《韩路游记》之于韩路，并非一个全新的事物，早在2004年，他22岁那年，他就已经开始用这种方式记录自己的生活了。

而这一拍，就是16年。


快手上韩路的游记

打开韩路的快手主页，你会发现他的视频可以被分为两类：一类是专业，另一类，则是情怀。

当他对一辆车进行测评时，你会很清晰地感受到他身上的专业性：

一辆车的优缺点在他嘴里，可以通过短短的几十秒钟就被讲解明白，在他的语句中，往往很容易就能让人感受到，一个爱车之人对于汽车的痴迷。

而更可贵的是，痴迷之外，他又足够客观，客观之下， 他的视频又充满趣味：

“六辆600万的汽车，比比看谁的车速更快？”
“两辆400万的房车，哪辆更厉害？”
“30万的电动车和30万的汽油车，哪辆更快？ ”

也正是因为韩路的专业性与趣味性，为他吸引了不少爱车的粉丝，如今，在快手上，他已经拥有近30万粉丝。

在每条视频下面，总有粉丝来向韩路咨询一些专业问题，这也给了他许多与大家交流的机会。



而另一方面，他的视频账号里，还存在着诗与远方。

他曾和车队一起探秘过电影《九层妖塔》中的石油小镇，展现了这座如今已是空城的城市过去的光景。

也曾三次挑战穿越西藏阿里北线的无人区，带着大家观看雅鲁藏布江边的美景。

更曾在休眠的火山口边露营，在阵雨后迎来过美丽的夕阳。这些都被他用视频记录在了快手的账号上，视频中他说：

“坐在火山口边，看着美丽的日落，这大概就是旅行的意义吧。”



而在美景外，韩路还会给大家带来一些干货，比如在沙漠里应当如何存活；自驾出行需要注意什么；无人区到底能不能够穿越。

韩路说，希望自己的《韩路游记》能够给观看者带来“诗与远方”，并向他们提供另一种生活的可能性——一种朝气蓬勃的生活方式。

而同样是探寻远方，“荒野熊队”田小禾探寻的方式，却有些过于艰辛。



后来，每当田小禾想到那次贝阿公路无人区穿越，脑海中率先浮现的，仍是一连串数字和西伯利亚呼啸而来的飓风。

12个人，9天8夜，零下20度，1374公里。

那是田小禾记忆中最难的一次自驾探险。


田小禾

田小禾说，在自己18岁之前，对于“旅行”两个字，没有太具体的概念：“我哪里都没去过，甚至连辽宁都没出过”。

然而，在他20岁出头那年，一个新的世界在他面前徐徐铺开。

彼时，在英国留学的田小禾第一次了解到“自驾探险”这一概念：那时他的外国同学们一有空，就会开着越野车去四处探险，田小禾头一次发现，还能够以这样的方式认知世界。

从那时起，他就对“自驾探险”这件事，充满了向往。


回国后，正值国内电商兴起，田小禾抓住机会做起了国际物流，眼看事业越来越顺，但田小禾却并不太开心：“我的心里好像始终惦记着什么事情。”

转折在2008年到来，那一年，他的多年好友因为心脏病猝然离世，这件事情，给田小禾带来了极大的震动。

悲伤之余，他开始思考人存在于世的意义，在度过无数个辗转难眠的夜晚后，他做出了一个决定——辞职，去自驾探险。

在当时，他的这个决定并没有被家人和朋友所理解，在他们眼中，田小禾口中反复提及的“自驾探险”是一个极其不靠谱的事情。

但是对于田小禾而言，此时的他比任何时候都坚定：“生命太脆弱了，我不想留遗憾。”

辞职后，田小禾没有急着开始自驾探险，而是先借钱开了一家汽车改造厂。因为对于自驾探险而言，就算是越野车，也要经过改造才能上路：

“越野车要能爬坡，能下水，才算合格。”



除此之外，他还看了许多相关书籍，学会了一些专业知识，对他来说：探险，绝对不是冒险。

在掌握了足够多的车辆知识后，田小禾与一群志同道合的朋友组建起一支探险车队，取名“荒野熊队”，在车队里，他给自己起名叫熊二，给自己的车取名叫“熊坚强”。

在此后10年间，他组织了近百次穿越活动，和他的“熊坚强”一起成功穿越过英格兰、老挝、泰国等境外多个地区。并带领队员们游走于无人区以及大河源头。

走在路上，田小禾总会产生许多感触，也总想记录下转瞬即逝的美景，于是他开始用快手记录自己的旅途经历。

对于自驾探险者来说，路途中的孤独总是常态，可每当田小禾将自己的视频发布到自己拥有170万粉丝的快手账号上时，总有许多粉丝来和他互动，提醒他注意安全。

对于田小禾而言，粉丝的关心是与美景一样，让他感到幸福的事情。



在这些探险中，最让田小禾难忘的，是他2018年的那次俄罗斯贝阿公路无人区穿越。

这条被称为“世界上最危险的公路”的贝阿公路，是俄罗斯在修建贝阿铁路时，用来运送物资的公路，如今这条道路早已被遗弃，而且因为常年没有维护，道路凶险。

田小禾和团队来到俄罗斯时，已是4月，贝阿公路的冰雪正在飞速融化，给这趟本就艰难的穿越带来了更大的困难。

再此之前，这条道路上并没有任何实际有用的中文信息，田小禾说：

“有时候踏出第一步，带回一些实际有用的信息给后人，不只需要勇气，还需要一些运气。”

这一路他们有过与死亡擦肩而过的时刻，陷入过冰冻，掉入过水中，也有过车辆水箱破裂后，不得不弃车的艰难决定。

在这个过程中，荒野熊队甚至还途经了世界第一危桥——康定斯基大桥。



但也是因为这些经历，让他更深刻的感觉到，正是因为有团队过往十几年的经验与沉淀，才能够让大家在面对这些问题时，更加从容且坚定。

最终，田小禾和他带领的荒野熊队成为了第一支自驾穿过贝阿公路的中国车队，而这一过程也被他用视频记录了下来，发布到快手上，引起了广泛的关注与讨论。



当有人在视频的评论底下问田小禾，下一站出发想去哪里时，他回答：

“我的选择很简单，远方。”



对于李乐皓、韩路和田小禾而言，汽车早已成为了他们人生中十分重要的模块。见证着他们的人生进程，贯穿于他们的事业走向。

他们大概未曾想过，当初自己对于汽车的热爱，会在日后，以如此深入全面的方式，渗入生活的方方面面。


李乐皓与儿子

他们也未曾想到，自己对于爱好的坚持，竟然真的能够转变成为事业，改变人生的风向。

但是唯一可以确定的是，这一路走来，他们从未动摇过“汽车是自己人生重要组成”的这一想法。


 韩路

无论是花费近30万修复一台老式捷达车的李乐皓，还是辞去工作进行荒野探险的田小禾，抑或是果断放弃自己已有事业，开始全新人生的韩路。

在快手上，像这样的汽车爱好者还有很多，从某种意义上讲，他们都选择了一种不太普通的人生道路，但是也正是有他们的存在，才为我们提供了另一种生活态度：

“人生从来不该有那么多禁锢，每一种生活都值得被拥抱。”

毕竟，人生很短，必须精彩
* */
  /**
   * 充值记录
   *
   * @param form 充值记录queryForm
   * @return PageInfo<PrepaidRechargeRecordVo>
   */
  public PageInfo<PrepaidRechargeRecordVo> rechargeRecord(PrepaidRechargeRecordQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    form.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    List<PrepaidRechargeRecordVo> resultList = prepaidRechargeRecordMapper.RechargeRecord(form);
    if (!StringHelper.isEmpty(resultList)) {
      for (PrepaidRechargeRecordVo prepaidRechargeRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(
                prepaidRechargeRecordVo.getOrgId());
        if (organizationInfo != null) {
          prepaidRechargeRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
        if (prepaidRechargeRecordVo.getPaymentId() != null){
          AccountItem accountItem =
                  remoteSystemServiceFeign.findAccountItemById(prepaidRechargeRecordVo.getPaymentId());
          if (accountItem != null) {
            // 获取支付方式名称
            prepaidRechargeRecordVo.setPaymentName(accountItem.getName());
          }
        }
      }

    }
    return new PageInfo<>(resultList);
  }
  /**
   * 预付款退费
   *
   * @param model 预付款退费
   */
  public void refund(PrepaidMeturnRecordModel model) {
    // 查询会员余额 退减余额
    PatientPrepaymentsInfo patientPrepaymentsInfo =
        patientPrepaymentsInfoMapper.selectOneByPrepaymentNumberAndPatientId(model.getPrepaidId());
    if (patientPrepaymentsInfo != null) {
      BigDecimal prepaymentPrincipal = patientPrepaymentsInfo.getPrepaymentPrincipal();
      BigDecimal returnPrincipalAmount = model.getReturnPrincipalAmount();
      BigDecimal principalSubtract = prepaymentPrincipal.subtract(returnPrincipalAmount);
      if (principalSubtract.intValue() < 0) {
        throw new ClientServiceException("预付款本金余额不足",OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
      patientPrepaymentsInfo.setPrepaymentPrincipal(principalSubtract);
      BigDecimal prepaymentBonus = patientPrepaymentsInfo.getPrepaymentBonus();
      BigDecimal returnGiftAmount = model.getReturnGiftAmount();
      BigDecimal bonusSubtract = prepaymentBonus.subtract(returnGiftAmount);
      if (bonusSubtract.intValue() < 0) {
        throw new ClientServiceException("预付款赠金余额不足",OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
      patientPrepaymentsInfo.setPrepaymentBonus(bonusSubtract);
      patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
      // 添加会员卡退费记录
      PrepaidReturnRecord prepaidReturnRecord = new PrepaidReturnRecord();
      BeanUtils.copyProperties(model, prepaidReturnRecord);
      prepaidReturnRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      AccountItem accountItem =
          remoteSystemServiceFeign.findAccountItemById(prepaidReturnRecord.getReturnWayId());
      if (accountItem != null) {
        prepaidReturnRecord.setReturnWayType(accountItem.getName()); // 获取退费方式类型名称
      }
      prepaidReturnRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidReturnRecord.setCrtName(BaseContextHandler.getName());
      prepaidReturnRecord.setCurrentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
      prepaidReturnRecord.setCurrentBonus(patientPrepaymentsInfo.getPrepaymentBonus());
      prepaidReturnRecord.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidReturnRecord.setUpdName(BaseContextHandler.getName());
      prepaidReturnRecordMapper.insertSelective(prepaidReturnRecord);

      // 发送消息 退费
      sendPrepaidLogMessages(prepaidReturnRecord.getId(), 0, 1, 3);
    }
  }

  /**
   * 退费记录列表
   *
   * @param queryForm 查询条件
   * @return MemberReturnRecordVo
   */
  public PageInfo<PrepaidMeturnRecordVo> refundList(PrepaidMeturnRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<PrepaidMeturnRecordVo> resultList = prepaidReturnRecordMapper.refundList(queryForm);
    if (!StringHelper.isEmpty(resultList)) {
      for (PrepaidMeturnRecordVo prepaidMeturnRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(prepaidMeturnRecordVo.getOrgId());
        if (organizationInfo != null) {
          prepaidMeturnRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
        AccountItem accountItem =
            remoteSystemServiceFeign.findAccountItemById(prepaidMeturnRecordVo.getReturnWayId());
        if (accountItem != null) {
          // 获取支付方式名称
          prepaidMeturnRecordVo.setReturnWayType(accountItem.getName());
        }
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 消费记录
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<PrepaidExpendRecordVo> expendList(PrepaidExpendRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<PrepaidExpendRecordVo> resultList = prepaidExpendRecordMapper.expendList(queryForm);
    if (!StringHelper.isEmpty(resultList)) {
      for (PrepaidExpendRecordVo prepaidExpendRecordVo : resultList) {
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(prepaidExpendRecordVo.getOrgId()); // 获取门诊简称
        if (organizationInfo != null) {
          prepaidExpendRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据账单记录ID和预付款ID查询支付记录详细
   * @param query
   * @return 预付款支付记录
   */
  public PrepaidExpendRecord prePaidPaymentRecordDetail(PaymentRecordDetailQuery query) {
    PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord();
    prepaidExpendRecord.setBillPayRecordId(query.getBillRecordId());
    prepaidExpendRecord.setPrepaidId(query.getCardId());
    return prepaidExpendRecordMapper.selectOne(prepaidExpendRecord);
  }


  /**
   * 预付款
   *
   * @param model 预付款消费Model
   * @return ResponseResult
   */
  public ResponseResult expend(PrepaidExpendRecordModel model) {
    PatientPrepaymentsInfo patientPrepaymentsInfo =
        patientPrepaymentsInfoMapper.selectOneByPrepaymentNumberAndPatientId(
            model.getPrepaidId());
    if (patientPrepaymentsInfo != null) {
      if (patientPrepaymentsInfo
              .getPrepaymentPrincipal()
              .add(patientPrepaymentsInfo.getPrepaymentBonus())
              .compareTo(model.getExpendTotal())
          < 0) { // 如果本金+赠金 小于 消费金额
        return ResponseUtil.fail(
            OperationCodeConstants.BALANCE_INSUFFICIENT, "预付款余额不足", patientPrepaymentsInfo);
      }
      spending(model, patientPrepaymentsInfo);
    }
    return ResponseUtil.success();
  }

  /**
   * 消费
   *
   * @param model 消费参数
   * @param patientPrepaymentsInfo 预付款信息
   */
  public void spending(
      PrepaidExpendRecordModel model, PatientPrepaymentsInfo patientPrepaymentsInfo) {
    // 消费本金
    BigDecimal expendePrincipal = null;
    // 消费赠金
    BigDecimal expendeBonus = null;
    // 账户本金
    BigDecimal principalAmount = patientPrepaymentsInfo.getPrepaymentPrincipal();
    // 账户赠金
    BigDecimal bonusAmount = null;

    PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord(); // 创建消费记录对象
    BeanUtils.copyProperties(model, prepaidExpendRecord);
    // 会员卡本金余额 小于 消费金额
    if (principalAmount.compareTo(model.getExpendTotal()) < 0) {
      // 小于的情况下 依然先用本金去抵扣消费金额
      // 本金-消费总额
      BigDecimal surplus = principalAmount.subtract(model.getExpendTotal());
      // 本金已用完
      patientPrepaymentsInfo.setPrepaymentPrincipal(new BigDecimal(0));
      // 获取消费本金
      prepaidExpendRecord.setExpendPrincipal(principalAmount);
      // 获取赠金
      bonusAmount = patientPrepaymentsInfo.getPrepaymentBonus();
      // 用赠金去抵扣
      patientPrepaymentsInfo.setPrepaymentBonus(patientPrepaymentsInfo.getPrepaymentBonus().add(surplus));
      // 原账户赠金-抵扣后赠金余额 = 用了多少赠金
      expendeBonus = bonusAmount.subtract(patientPrepaymentsInfo.getPrepaymentBonus());
      // 获取消费赠金
      prepaidExpendRecord.setExpendGift(expendeBonus);
    } else {
      patientPrepaymentsInfo.setPrepaymentPrincipal(principalAmount.subtract(model.getExpendTotal()));
      // 获取消费本金
      prepaidExpendRecord.setExpendPrincipal(model.getExpendTotal());
    }
    patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
    // 添加消费记录
    prepaidExpendRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    prepaidExpendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    prepaidExpendRecord.setCrtName(BaseContextHandler.getName());
    prepaidExpendRecord.setCurrentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
    prepaidExpendRecord.setCurrentBonus(patientPrepaymentsInfo.getPrepaymentBonus());
    prepaidExpendRecord.setCurrentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
    prepaidExpendRecord.setCurrentBonus(patientPrepaymentsInfo.getPrepaymentBonus());
    prepaidExpendRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    prepaidExpendRecord.setUpdName(BaseContextHandler.getName());
    prepaidExpendRecordMapper.insertSelective(prepaidExpendRecord);
    // 发送消息 预付款消费
    sendPrepaidLogMessages(prepaidExpendRecord.getId(), 0, 1, 2);
  }

  /**
   * 预付款付款余额查询
   *
   * @param id
   * @return PatientPrepaymentBalanceVo
   */
  public PatientPrepaymentBalanceVo balancePayment(Integer id) {
    PatientPrepaymentsInfoVo prepaymentInfo = patientPrepaymentsInfoMapper.findPrepaymentInfo(id);
    if (null != prepaymentInfo) {
      PatientPrepaymentBalanceVo patientPrepaymentBalanceVo = new PatientPrepaymentBalanceVo();
      patientPrepaymentBalanceVo.setPatientPrepaymentsInfoVo(prepaymentInfo);

      List<PatientPrepaymentsInfoVo> prepaymentsInfoVoList =
          patientPrepaymentsInfoMapper.selectPrepaymentRelationByMasterPatientId(id);
      if (StringHelper.isNotNull(prepaymentsInfoVoList)) {
        patientPrepaymentBalanceVo.setPrepaymentsInfoVoList(prepaymentsInfoVoList);
      }
      return patientPrepaymentBalanceVo;
    }
    return null;
  }

  /**
   * 账单退费
   * @param model 退费model
   */
    public void billRefund(PrepaidBillRechargeModel model) {
      PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidId());
      if (patientPrepaymentsInfo != null){

        if (model.getRechargePrincipal() != null){
          patientPrepaymentsInfo.setPrepaymentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal().add(model.getRechargePrincipal()));
        }
        if (model.getRechargeBonus() != null){
          patientPrepaymentsInfo.setPrepaymentBonus(patientPrepaymentsInfo.getPrepaymentBonus().add(model.getRechargeBonus()));
        }
        patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
        PrepaidRechargeRecord prepaidRechargeRecord = new PrepaidRechargeRecord();
        BeanUtils.copyProperties(model,prepaidRechargeRecord);
        prepaidRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        prepaidRechargeRecord.setType(2);
        prepaidRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeRecord.setCrtName(BaseContextHandler.getName());
        prepaidRechargeRecord.setCurrentRechargePrincipal(prepaidRechargeRecord.getRechargePrincipal());
        prepaidRechargeRecord.setCurrentRechargeBonus(prepaidRechargeRecord.getRechargeBonus());
        prepaidRechargeRecordMapper.insertSelective(prepaidRechargeRecord);
        // 发送消息 撤销收费
        sendPrepaidLogMessages(prepaidRechargeRecord.getId(), 0, 1, 5);
      }
    }

  /**
   * 预付款撤销收费
   * @param model 撤销model
   */
  public ResponseResult revocationFee(PrepaidRevocationFeeModel model) {
    PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord();
    prepaidExpendRecord.setPrepaidId(model.getPrepaidCard());
    prepaidExpendRecord.setBillPayRecordId(model.getBillPayRecordId());
    prepaidExpendRecord.setInservice(true);
    PrepaidExpendRecord prepaidExpend = prepaidExpendRecordMapper.selectOne(prepaidExpendRecord);
    if (prepaidExpend != null){
      prepaidExpend.setInservice(false);
      prepaidExpendRecordMapper.updateByPrimaryKeySelective(prepaidExpend);
      remoteRabbitMqServiceFeign.sendMessage(prepaidExpend.getId(), 1, 2, MsgCategoryEnum.BasePatientMemberOccurLog);
      PatientPrepaymentsInfo patientPrepaymentsInfo =
              patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidCard());
      if (patientPrepaymentsInfo != null) {
        // 撤销消费
        patientPrepaymentsInfo.setPrepaymentPrincipal(
                patientPrepaymentsInfo.getPrepaymentPrincipal().add(prepaidExpend.getExpendPrincipal()));
        patientPrepaymentsInfo.setPrepaymentBonus(
                patientPrepaymentsInfo.getPrepaymentBonus().add(prepaidExpend.getExpendGift()));
        patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
        // 创建消费记录对象
        PrepaidRechargeRecord prepaidRechargeRecord = new PrepaidRechargeRecord();
        BeanUtils.copyProperties(model, prepaidRechargeRecord);
        // 撤销本金
        prepaidRechargeRecord.setRechargePrincipal(prepaidExpend.getExpendPrincipal());
        // 撤销赠金
        prepaidRechargeRecord.setRechargeBonus(prepaidExpend.getExpendGift());
        prepaidRechargeRecord.setInservice(false);
        prepaidRechargeRecord.setType(1);
        prepaidRechargeRecord.setPrepaidId(model.getPrepaidCard());
        prepaidRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        prepaidRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeRecord.setCrtName(BaseContextHandler.getName());
        prepaidRechargeRecord.setCurrentRechargePrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
        prepaidRechargeRecord.setCurrentRechargeBonus(patientPrepaymentsInfo.getPrepaymentBonus());
        prepaidRechargeRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeRecord.setUpdName(BaseContextHandler.getName());
        prepaidRechargeRecordMapper.insertSelective(prepaidRechargeRecord);
        // 发送消息 撤销收费
        sendPrepaidLogMessages(prepaidRechargeRecord.getId(), 2, 1, 4);
        return ResponseUtil.success();
      }else {
        return ResponseUtil.fail(OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到预付款", patientPrepaymentsInfo);
      }
    }
    return ResponseUtil.fail(OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到预付款消费记录", prepaidExpend);
  }

}
