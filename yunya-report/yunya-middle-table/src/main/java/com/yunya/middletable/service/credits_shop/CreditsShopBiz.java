package com.yunya.middletable.service.credits_shop;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.query.PatientCreditsRecordQuery;
import com.yunya.feign.report.domain.vo.CreditsRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.config.DuiBaConfig;
import com.yunya.middletable.dao.credits_shop.CreditsShopMapper;
import com.yunya.middletable.utils.SignTool;
import com.yunya.models.credits_shop.CreditsShop;
import org.springframework.beans.factory.annotation.Autowired;
import com.yunya.models.report.BasePatientConsumptionCountVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @program: yunya-dental
 * @description: 积分商城
 * @author: LHB
 * @create: 2021-04-22 16:01
 **/
@Service
public class CreditsShopBiz extends BaseBiz<CreditsShopMapper, CreditsShop> {

    @Autowired
    private DuiBaConfig duiBaConfig;

    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    /**
     * 增加积分
     * @param creditsShop
     * @return 成功返回非0正整数
     */
    public Integer addCredits(CreditsShop creditsShop) {
        int insert = mapper.insert(creditsShop);
        return insert;
    }

    /**
     * 艾维线下门店消费增加积分
     * @param patientId 患者ID
     * @param money 患者消费金额
     * @param payId 患者支付ID
     * @return
     */
    public Integer ivyConsumeAddCredits(Integer patientId, BigDecimal money, Integer payId) {
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
        entity.setId(patientId);
        return addCredits(entity);
    }

    /**
     * 查询患者积分记录
     * @param query 查询参数
     * @return
     */
    public ResponseResult selectPatientCreditsRecord(PatientCreditsRecordQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.offsetPage(query.getPageNum(),query.getPageSize());
        }
        CreditsShop entity = new CreditsShop();
        entity.setPatientId(query.getPatientId());
        List<CreditsRecordVO> creditsRecordVOList = mapper.selectPatientCreditsRecord(query.getPatientId());
        return ResponseUtil.success(creditsRecordVOList);
    }

    /**
     * 生成兑吧自动登录URl
     * @param openId
     * @param patientId
     * @return
     */
    public String duibaAutoLogin(String openId, Integer patientId) {
        Map<String,String> params = new HashMap<String,String>();
        String uidStr = openId + "#" + patientId.toString();
        CreditsShop creditsShop = mapper.selectLastCredits(patientId);
        Long credits = 0L;
        if (creditsShop != null) {
            credits = creditsShop.getCreditsAccount();
        }
        params.put("uid",uidStr);
        params.put("credits",credits.toString());
        params.put("appKey",duiBaConfig.getAppKey());
        params.put("timestamp",String.valueOf(System.currentTimeMillis()));
        String sign = SignTool.sign(params);
        String autoLoginUrl = SignTool.signRequestUrl(params, sign, duiBaConfig.getAutoLoginUrl());
        return autoLoginUrl;
    }

    /**
     * 查询患者积分
     * @param patientId
     * @return
     */
    public ResponseResult lastPatientCredits(Integer patientId) {
        CreditsShop creditsShop = mapper.selectLastCredits(patientId);
        return ResponseUtil.success(creditsShop);
    }


    /**
     * 初始患者化积分
     */
    public void initialization() {
        List<CreditsShop> creditsShopList = new ArrayList<>();
        List<BasePatientConsumptionCountVo> basePatientConsumptionCountVos = mapper.selectPatientConsumptionCount();
        if (basePatientConsumptionCountVos != null){
            for (BasePatientConsumptionCountVo basePatientConsumptionCountVo : basePatientConsumptionCountVos) {
                CreditsShop creditsShop = new CreditsShop();
                creditsShop.setPatientId(basePatientConsumptionCountVo.getPatientId());
                creditsShop.setType("offlineConsume");
                creditsShop.setChannel((byte)0);
                creditsShop.setOrderNum(null);
                creditsShop.setCreditsAccount(basePatientConsumptionCountVo.getIntegral().longValue());
                creditsShop.setCredits(0L);
                creditsShop.setCreditsOption((byte)0);
                creditsShop.setActualPrice(0);
                creditsShop.setItemCode("");
                creditsShop.setDescription("");
                creditsShop.setRemarks("初始化积分");
                creditsShop.setInservice(false);
                creditsShop.setCrtId(0);
                creditsShop.setCrtTime(new Date());
                creditsShop.setUpdId(0);
                creditsShop.setUpdTime(new Date());
                creditsShopList.add(creditsShop);
            }
            List<List<CreditsShop>> creditsShopLists = Lists.partition(creditsShopList, 100);
            CountDownLatch countDownLatch = new CountDownLatch(creditsShopLists.size());
            for (List<CreditsShop> creditsShopListVo: creditsShopLists) {
                importExcelThreadPool.execute(()->{

                });
            }
        }
    }
}
