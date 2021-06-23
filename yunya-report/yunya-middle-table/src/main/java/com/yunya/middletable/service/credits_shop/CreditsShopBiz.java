package com.yunya.middletable.service.credits_shop;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.credits_shop.AddCreditsParams;
import com.yunya.feign.report.domain.credits_shop.CreditConsumeParams;
import com.yunya.feign.report.domain.credits_shop.CreditResult;
import com.yunya.feign.report.domain.query.PatientCreditsRecordQuery;
import com.yunya.feign.report.domain.vo.CreditsRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.middletable.config.DuiBaConfig;
import com.yunya.middletable.dao.report.credits_shop.CreditsShopMapper;
import com.yunya.middletable.utils.CreditTool;
import com.yunya.middletable.utils.SignTool;
import com.yunya.models.report.BasePatientConsumptionCountVo;
import com.yunya.models.report.CreditsShop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description: 积分商城
 * @author: LHB
 * @create: 2021-04-22 16:01
 */
@Slf4j
@Service
public class CreditsShopBiz extends BaseBiz<CreditsShopMapper, CreditsShop> {

  @Autowired private DuiBaConfig duiBaConfig;

  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

  @Resource private CreditTool creditTool;

  @Resource private RedisUtils redisUtils;

  /**
   * 增加积分
   *
   * @param creditsShop
   * @return 成功返回非0正整数
   */
  @Transactional(rollbackFor = Exception.class)
  public Integer addCredits(CreditsShop creditsShop) {
    int insert = mapper.insert(creditsShop);
    return insert;
  }

  /**
   * 艾维线下门店消费增加积分
   *
   * @param patientId 患者ID
   * @param money 患者消费金额
   * @param payId 患者支付ID
   * @return
   */
  public Integer ivyConsumeAddCredits(Integer patientId, BigDecimal money, Integer payId) {
    Integer result = 0;
    // 如果已经加过积分则不增加
    CreditsShop t = new CreditsShop();
    t.setPatientId(patientId);
    t.setChannel((byte) 0);
    t.setRemarks(payId.toString());
    int count = mapper.selectCount(t);
    if (count >= 1) {
      return result;
    }
    try {
      CreditsShop creditsShop = mapper.selectLastCredits(patientId);
      Long creditsAccount = 0L;
      if (creditsShop != null) {
        creditsAccount = creditsShop.getCreditsAccount();
      }
      CreditsShop entity = new CreditsShop();
      entity.setPatientId(patientId);
      // 艾维自有渠道
      entity.setChannel((byte) 0);
      long l = money.setScale(0, RoundingMode.HALF_UP).longValue();
      entity.setCredits(l);
      entity.setCreditsAccount(creditsAccount + l);
      entity.setDescription("门店消费获取积分");
      entity.setType("offlineConsume");
      // 积分新增
      entity.setCreditsOption((byte) 0);
      entity.setRemarks(payId.toString());
      entity.setCrtId(patientId);
      entity.setCrtTime(new Date(System.currentTimeMillis()));
      result = addCredits(entity);
    } catch (Exception e) {
      StringBuilder sb = new StringBuilder();
      sb.append("\n↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓艾维线下门店消费增加积分异常↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓\n");
      sb.append("==>patientId: " + patientId +"\n");
      sb.append("==>money: " + money.longValue() +"\n");
      sb.append("==>payId: " + payId +"\n");
      sb.append("错误原因: " + e.getMessage() + "\n");
      sb.append("错误描述: " + e.getCause() + "\n");
      sb.append("\n↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑\n");
      log.info(sb.toString());
    }
    return result;
  }

  /**
   * 查询患者积分记录
   *
   * @param query 查询参数
   * @return
   */
  public ResponseResult<List<CreditsRecordVO>> selectPatientCreditsRecord(
      PatientCreditsRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    CreditsShop entity = new CreditsShop();
    entity.setPatientId(query.getPatientId());
    List<CreditsRecordVO> creditsRecordVOList =
        mapper.selectPatientCreditsRecord(query.getPatientId());
    return ResponseUtil.success(creditsRecordVOList);
  }

  /**
   * 生成兑吧自动登录URl
   *
   * @param openId
   * @param patientId
   * @return
   */
  public ResponseResult<Map<String, String>> duibaAutoLogin(String openId, String patientId) {
    Map<String, String> params = new HashMap<String, String>(16);
    String uidStr = openId + "#" + patientId;
    String uid = URLEncoder.encode(uidStr);
    params.put("uid", uid);
    // 设置积分余额
    params.put("credits",getCreditsAccount(uid,patientId).toString());
    params.put("appKey", duiBaConfig.getAppKey());
    params.put("appSecret", duiBaConfig.getAppSecret());
    params.put("timestamp", String.valueOf(System.currentTimeMillis()));
    String sign = SignTool.sign(params);
    params.remove("appSecret");
    String autoLoginUrl = SignTool.signRequestUrl(params, sign, duiBaConfig.getAutoLoginUrl());
    Map<String, String> result = new HashMap<>(16);
    result.put("url", autoLoginUrl);

    return ResponseUtil.success(result);
  }

  private Long getCreditsAccount(String uid, String patientId) {
    Long credits = 0L;
    Long creditsBalance = credits;
    if (StringHelper.isNotBlank(patientId) && !"null".equals(patientId)) {
      CreditsShop creditsShop = mapper.selectLastCredits(Integer.parseInt(patientId));
      if (creditsShop != null) {
        credits = creditsShop.getCreditsAccount();
      } else {
        return 0L;
      }
      Long ordersPaymentAmount = ordersPaymentAmountByUid(uid);
      creditsBalance = credits - ordersPaymentAmount;
    }
    return creditsBalance;
  }

  /**
   * 获取用户未签收订单总额（实际支付总额）
   * @param uid 用户唯一标识  openId + # + 患者ID
   * @return 实际支付总额
   */
  public Long ordersPaymentAmountByUid(String uid) {
    Long ordersPaymentAmount = 0L;
    String parseUid = URLDecoder.decode(uid);
    String ordersCacheKey = RedisConstants.CREDITS_SHOP_ORDER + uid;
    log.info("uid解析之前{}",uid);
    log.info("redis中是否有相应的key({})：{}",parseUid,redisUtils.hasKey(parseUid));
    log.info("redis中的value:{}",redisUtils.getJSONArray(ordersCacheKey,CreditsShop.class));
    // 从redis缓存中获取订单支付总额
    ordersPaymentAmount = getOrdersPaymentAmountFromCache(ordersCacheKey);
    return ordersPaymentAmount;
  }

  /**
   * 获取用户未签收订单总额（实际支付总额）
   * @param patientId  患者ID
   * @return 实际支付总额
   */
  public Long ordersPaymentAmountByPatientId(Integer patientId) {
    Long ordersPaymentAmount = 0L;
    Set<String> keys = redisUtils.keys(RedisConstants.CREDITS_SHOP_ORDER + "*" + patientId);
    if (StringHelper.isEmpty(keys)) {
      return ordersPaymentAmount;
    }
    String orderCacheKey = keys.stream().findAny().get();
    // 从redis缓存中获取订单支付总额
    ordersPaymentAmount = getOrdersPaymentAmountFromCache(orderCacheKey);
    return ordersPaymentAmount;
  }

  /**
   * 从redis缓存中获取订单支付总额
   * @param key 缓存key
   * @return 订单支付总额
   */
  private Long getOrdersPaymentAmountFromCache(String key) {
    Long ordersPaymentAmount = 0L;
    if(redisUtils.hasKey(key)) {
      List<CreditsShop> unreceivedOrders = redisUtils.getJSONArray(key, CreditsShop.class);
      if (StringHelper.isNotEmpty(unreceivedOrders)) {
        for (CreditsShop creditsShop : unreceivedOrders) {
          ordersPaymentAmount += creditsShop.getCredits();
        }
      }
    }
    return ordersPaymentAmount;
  }


  /**
   * 查询患者积分
   *
   * @param patientId
   * @return
   */
  public ResponseResult<CreditsShop> lastPatientCredits(Integer patientId) {
    CreditsShop creditsShop = mapper.selectLastCredits(patientId);
    if (creditsShop != null) {
      Long ordersPaymentAmount = ordersPaymentAmountByPatientId(patientId);
      creditsShop.setCreditsAccount(creditsShop.getCreditsAccount() - ordersPaymentAmount);
    } else {
      creditsShop = new CreditsShop();
      creditsShop.setCreditsAccount(0L);
    }
    return ResponseUtil.success(creditsShop);
  }

  /** 初始化患者积分 */
  public void initialization() throws InterruptedException {
    List<CreditsShop> creditsShopList = new ArrayList<>();
    List<BasePatientConsumptionCountVo> basePatientConsumptionCountVos =
        mapper.selectPatientConsumptionCount();
    if (basePatientConsumptionCountVos != null) {
      for (BasePatientConsumptionCountVo basePatientConsumptionCountVo :
          basePatientConsumptionCountVos) {
        if (basePatientConsumptionCountVo.getIntegral() > 0) {
          CreditsShop creditsShop = new CreditsShop();
          creditsShop.setPatientId(basePatientConsumptionCountVo.getPatientId());
          creditsShop.setType("offlineConsume");
          creditsShop.setChannel((byte) 0);
          creditsShop.setOrderNum(null);
          creditsShop.setCreditsAccount(basePatientConsumptionCountVo.getIntegral().longValue());
          creditsShop.setCredits(basePatientConsumptionCountVo.getIntegral().longValue());
          creditsShop.setCreditsOption((byte) 0);
          creditsShop.setActualPrice(0);
          creditsShop.setDescription("初始化积分");
          creditsShop.setInservice(true);
          creditsShop.setCrtId(0);
          creditsShop.setCrtTime(new Date(System.currentTimeMillis()));
          creditsShop.setUpdId(0);
          creditsShopList.add(creditsShop);
        }
      }
      List<List<CreditsShop>> creditsShopLists = Lists.partition(creditsShopList, 100);
      CountDownLatch countDownLatch = new CountDownLatch(creditsShopLists.size());
      long start = System.currentTimeMillis();
      for (List<CreditsShop> creditsShopListVo : creditsShopLists) {
        importExcelThreadPool.execute(
            () -> {
              try {
                mapper.insetCreditsShopList(creditsShopListVo);
              } catch (Exception e) {
                log.info("批量添加患者初始化积分信息异常", e);
              } finally {
                countDownLatch.countDown();
              }
            });
      }
      countDownLatch.await();
      long end = System.currentTimeMillis();
      log.info("批量添加患者初始化积分信息完成，时长：[{}]秒", (end - start) / 1000);
    }
  }

  /**
   * 增加积分
   *
   * @param request 请求体
   * @return 响应
   */
  public CreditResult increasePoints(HttpServletRequest request) {
    CreditResult creditResult = new CreditResult();
    String uid = request.getParameter("uid");
    Map<String, String> userInfo = creditTool.parseUid(uid);
    String key = "patientId";
    AddCreditsParams addCreditsParams = null;
    CreditsShop creditsShop = null;
    try {
      Integer patientId = Integer.parseInt(userInfo.get(key));
      creditsShop = mapper.selectLastCredits(patientId);
      addCreditsParams = creditTool.parseaddCredits(request);
      if (null != addCreditsParams) {
        if (!StringHelper.isEmpty(userInfo)) {
          // 新增患者积分变动信息
          CreditsShop addCreditsShop = new CreditsShop();
          if (null != creditsShop) {
            setCreditsEntity(addCreditsShop,addCreditsParams,patientId,creditsShop.getCreditsAccount());
          }else {
            setCreditsEntity(addCreditsShop,addCreditsParams,patientId,0L);
          }
          // 设置成功响应体
          setCreditsResult(creditResult,"ok",
                  addCreditsParams.getOrderNum(),
                  addCreditsShop.getCreditsAccount().toString(),
                  null);
          mapper.insertSelective(addCreditsShop);
          return creditResult;
        }
      }
    } catch (Exception e) {
      log.info("异常信息===>{}",e.getMessage());
      log.info("异常原因===>\n{}",e.getCause());
      String orderNum = null;
      if (addCreditsParams != null) {
        orderNum = addCreditsParams.getOrderNum();
      }
      String creditsAccount = "0";
      if (creditsShop != null) {
        creditsAccount = creditsShop.getCreditsAccount().toString();
      }
      // 设置失败响应体
      setCreditsResult(creditResult,"fail",
              orderNum,
              creditsAccount,
              e.getMessage());
      return creditResult;
    }

    return creditResult;
  }

  /**
   * 设置积分信息
   * @param addCreditsParams
   * @param creditsShop
   * @throws UnsupportedEncodingException
   */
  private void setCreditsEntity(CreditsShop creditsShop,AddCreditsParams addCreditsParams,Integer patientId,Long creditsAccount) throws UnsupportedEncodingException {
    creditsShop.setPatientId(patientId);
    creditsShop.setType(addCreditsParams.getType());
    creditsShop.setChannel((byte) 0);
    creditsShop.setOrderNum(addCreditsParams.getOrderNum());
    creditsShop.setCreditsAccount(creditsAccount + addCreditsParams.getCredits());
    creditsShop.setCredits(addCreditsParams.getCredits());
    creditsShop.setCreditsOption((byte) 0);
    String description = URLDecoder.decode(addCreditsParams.getDescription(), "UTF-8");
    if (null != description){
      creditsShop.setDescription(description);
    }
    creditsShop.setInservice(true);
    creditsShop.setCrtId(patientId);
    creditsShop.setCrtTime(new Date(System.currentTimeMillis()));
  }

  /**
   * 设置积分返回结果
   * @param creditResult
   * @param status
   * @param bizId
   * @param credits
   * @param errorMessage
   */
  private void setCreditsResult(CreditResult creditResult,String status, String bizId, String credits,String errorMessage) {
    // 设置成功响应体
    creditResult.setStatus(status);
    creditResult.setBizId(bizId);
    creditResult.setCredits(credits);
    creditResult.setErrorMessage(errorMessage);
  }


  /**
   * 消费积分
   * @param request 请求体
   * @return 响应
   */
  public CreditResult consumptionPoints(HttpServletRequest request) {
    CreditResult creditResult = new CreditResult();
    String uid = request.getParameter("uid");
    Map<String, String> userInfo = creditTool.parseUid(uid);
    String key = "patientId";
    String patientId = userInfo.get(key);
    if (!"null".equals(patientId)) {
      CreditsShop creditsShop = mapper.selectLastCredits(Integer.parseInt(patientId));
      try {
        CreditConsumeParams addCreditConsumeParams = creditTool.parseCreditConsume(request);
        if (null != addCreditConsumeParams) {
          if (!StringHelper.isEmpty(userInfo)) {
            if (null != creditsShop) {
              // 用户积分余额（账户实际余额-订单总额）
              Long creditsAccount = getCreditsAccount(uid, patientId);
              // 判断帐户积分是否够用
              if ( creditsAccount < addCreditConsumeParams.getCredits()){
                // 设置失败响应体
                creditResult.setStatus("fail");
                creditResult.setErrorMessage("帐户积分不足");
                creditResult.setCredits(creditsShop.getCredits().toString());
                log.info("====积分账户余额不足====");
                log.info("==>当前账户余额：{}",creditsAccount);
                log.info("==>需要支付积分: {}", addCreditConsumeParams.getCredits());
                log.info("返回信息:{}",creditResult);
                return creditResult;
              }
              Long creditsBalance = creditsAccount - addCreditConsumeParams.getCredits();
              // 新增患者积分变动信息
              CreditsShop addCreditsShop = new CreditsShop();
              addCreditsShop.setPatientId(creditsShop.getPatientId());
              addCreditsShop.setType(addCreditConsumeParams.getType());
              addCreditsShop.setChannel((byte) 1);
              addCreditsShop.setOrderNum(addCreditConsumeParams.getOrderNum());
              addCreditsShop.setCreditsAccount(creditsBalance);
              addCreditsShop.setCredits(addCreditConsumeParams.getCredits());
              addCreditsShop.setCreditsOption((byte) 1);
              String description = URLDecoder.decode(addCreditConsumeParams.getDescription(), "UTF-8");
              if (null != description){
                addCreditsShop.setDescription(description);
              }
              addCreditsShop.setActualPrice(addCreditConsumeParams.getActualPrice());
              addCreditsShop.setRemarks(addCreditConsumeParams.getParams());
              addCreditsShop.setInservice(true);
              addCreditsShop.setCrtId(creditsShop.getPatientId());
              addCreditsShop.setCrtTime(new Date(System.currentTimeMillis()));
              // 设置订单缓存
              setCache(addCreditConsumeParams.getUid(),addCreditsShop);
              // 设置成功响应体
              creditResult.setStatus("ok");
              creditResult.setBizId(addCreditConsumeParams.getOrderNum());
              creditResult.setCredits(creditsBalance.toString());
            }else {
              creditResult.setStatus("ok");
              creditResult.setCredits("0");
              return creditResult;
            }
          }
        }
      } catch (Exception e) {
        log.info("==>错误信息: {}",e.getMessage());
        log.info("==>错误栈信息:{}",e.getCause());
        // 设置失败响应体
        creditResult.setStatus("fail");
        creditResult.setErrorMessage(e.getMessage());
        creditResult.setCredits(null != creditsShop ? creditsShop.getCredits().toString() : "0");
        return creditResult;
      }
    } else {
      // 设置失败响应体
      creditResult.setStatus("fail");
      creditResult.setErrorMessage("未关联患者");
      creditResult.setCredits("0");
      return creditResult;
    }

    return creditResult;
  }

  private void setCache(String uid,CreditsShop creditsShop) {
    List<CreditsShop> unreceivedOrders;
    if(redisUtils.hasKey(RedisConstants.CREDITS_SHOP_ORDER + uid)) {
      unreceivedOrders = redisUtils.getJSONArray(RedisConstants.CREDITS_SHOP_ORDER + uid, CreditsShop.class);
    } else {
      unreceivedOrders = new ArrayList<>();
    }
    unreceivedOrders.add(creditsShop);
    if (StringHelper.isNotEmpty(unreceivedOrders)) {
      redisUtils.set(RedisConstants.CREDITS_SHOP_ORDER + uid, unreceivedOrders);
    }
    log.info("积分兑换详细信息\n{}",creditsShop);
    log.info("设置缓冲信息\n{}",redisUtils.get(RedisConstants.CREDITS_SHOP_ORDER + uid));
  }

  /**
   * 兑换结果
   * @param request 请求
   * @return 响应
   */
  @Transactional
  public String exchangeResult(HttpServletRequest request) {
    if (!SignTool.signVerify(duiBaConfig.getAppSecret(), request)) {
      return "fail";
    }
    String success = request.getParameter("success");
    String orderNum = request.getParameter("orderNum");
    String uid = request.getParameter("uid");
    String status = "true";
    if (redisUtils.hasKey(RedisConstants.CREDITS_SHOP_ORDER + uid)) {
      List<CreditsShop> jsonArray = redisUtils.getJSONArray(RedisConstants.CREDITS_SHOP_ORDER + uid, CreditsShop.class);
      CreditsShop creditsShop = jsonArray.stream().filter(entity -> orderNum.equals(entity.getOrderNum())).findAny().get();
      jsonArray = jsonArray.stream().filter(entity -> !orderNum.equals(entity.getOrderNum())).collect(Collectors.toList());
      int i = 0;
      if (status.equals(success)) {
        Map<String, String> parseUidMap = creditTool.parseUid(uid);
        String patientId = parseUidMap.get("patientId");
        CreditsShop oldCreditsShopInfo = mapper.selectLastCredits(Integer.valueOf(patientId));
        creditsShop.setCreditsAccount(oldCreditsShopInfo.getCreditsAccount() - creditsShop.getCredits());
        i = mapper.insert(creditsShop);
      }
      if (StringHelper.isNotEmpty(jsonArray)) {
        try {
          if (i <= 0) {
            return "fail";
          }
        } finally {
          redisUtils.set(RedisConstants.CREDITS_SHOP_ORDER + uid, jsonArray);
        }
      } else {
        redisUtils.delete(RedisConstants.CREDITS_SHOP_ORDER + uid);
      }
    } else {
      return "fail";
    }
    return "ok";
  }
}
