package com.yunya.middletable.service.credits_shop;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.*;
import com.yunya.models.report.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2021-07-02 11:03
 **/
@Service
@Slf4j
public class BillCreditsCallbackImpl implements BillCreditsCallback {
    @Autowired
    private BaseBillMapper baseBillMapper;
    @Autowired
    private BaseBillPayMapper baseBillPayMapper;
    @Autowired
    private CreditsShopBiz creditsShopBiz;
    @Autowired
    private BaseBillDetailMapper baseBillDetailMapper;
    @Autowired
    private BaseBillPayDetailMapper baseBillPayDetailMapper;
    @Autowired
    private BaseRefundDetailMapper baseRefundDetailMapper;
    @Autowired
    private BaseRefundMapper baseRefundMapper;

    /**
     * 收费完成，增加积分
     * @param billId 账单ID
     * @return
     */
    @Override
    public BillCreditsCallback baseBillBizHandlerFinish(Integer billId) {
        BaseBill baseBill = baseBillMapper.selectByPrimaryKey(billId);
        if (baseBill == null || baseBill.getBillStatus() == 0) {
            return this;
        }
        Example example = new Example(BaseBillPay.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("billId",billId);
        example.orderBy("billPayId").desc();
        List<BaseBillPay> baseBillPays = baseBillPayMapper.selectByExample(example);
        log.info("收费完成: baseBillPays = {}",baseBillPays);
        if (StringHelper.isNotEmpty(baseBillPays)) {
            BaseBillPay baseBillPay = baseBillPays.get(0);
            // 增加会员积分
            addCredits(baseBillPay.getReceivedAmount(),baseBillPay.getBillPayId(),baseBillPay.getBillId());
        }
        return this;
    }

    /**
     * 撤销收费积分处理
     * @param baseBillPayId 支付记录ID
     */
    @Override
    public BillCreditsCallback scrapCredits(Integer baseBillPayId) {
        BaseBillPayDetail baseBillPayQuery = new BaseBillPayDetail();
        baseBillPayQuery.setBillPayId(baseBillPayId);
        List<BaseBillPayDetail> baseBillPays = baseBillPayDetailMapper.select(baseBillPayQuery);
        BaseBillPayDetail baseBillPay = baseBillPays.get(0);
        BaseBill baseBill = baseBillMapper.selectByPrimaryKey(baseBillPay.getBillId());
        Integer patientId = baseBill.getPatientId();
        Integer billId = baseBillPay.getBillId();
        Example example = new Example(CreditsShop.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("patientId",patientId);
        criteria.andEqualTo("type","offlineConsume");
        criteria.andEqualTo("channel",0);
        criteria.andEqualTo("creditsOption",0);
        String remarks = "{\"baseBillId\":"+billId+",\"baseBillPayId\":";
        criteria.andLike("remarks",remarks);
        example.orderBy("id").desc();
        List<CreditsShop> creditsShops = creditsShopBiz.selectByExample(example);
        if (StringHelper.isNotEmpty(creditsShops)) {
            CreditsShop creditsShop = creditsShops.get(0);
            String jsonRemarks = creditsShop.getRemarks();
            if (StringHelper.isNotBlank(jsonRemarks)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonRemarks);
                jsonObject.put("scrapCharges",1);
                creditsShop.setRemarks(jsonObject.toJSONString());
            }
            creditsShop.setUpdId(patientId);
            creditsShop.setUpdTime(new Date(System.currentTimeMillis()));
            creditsShopBiz.updateSelectiveById(creditsShop);
        }
        return this;
    }

    @Override
    public BillCreditsCallback refundCredits(Integer refundId, Integer billId) {
        if (refundId != null && billId != null) {
            BaseRefundDetail query = new BaseRefundDetail();
            query.setRefundId(refundId);
            List<BaseRefundDetail> bfds = baseRefundDetailMapper.select(query);
            BaseBillDetail bbdQuery = new BaseBillDetail();
            bbdQuery.setBillId(billId);
            List<BaseBillDetail> bbds = baseBillDetailMapper.select(bbdQuery);
            if (StringHelper.isNotEmpty(bfds) && StringHelper.isNotEmpty(bbds)) {
                // 过滤出非商品账单项目ID集合
                List<Integer> medicalBillDetailIds = bbds.stream().filter(item -> item.getItemType().intValue() == 0).mapToInt(BaseBillDetail::getBillDetailId).boxed().collect(Collectors.toList());
                if (StringHelper.isNotEmpty(medicalBillDetailIds)) {
                    // 过滤出退费项目是非商品的所有项目
                    List<BaseRefundDetail> medicalBaseRefunds = bfds.stream().filter(item -> medicalBillDetailIds.contains(item.getBillDetailId())).collect(Collectors.toList());
                    if (StringHelper.isNotEmpty(medicalBaseRefunds)) {
                        // 非商品类项目退款总额
                        BigDecimal refundMedicalMony = new BigDecimal(0);
                        for (BaseRefundDetail item: medicalBaseRefunds) {
                            refundMedicalMony = refundMedicalMony.add(item.getRefundAmount());
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("refundId",refundId);
                        jsonObject.put("scrapCharges",0);
                        jsonObject.put("baseBillId",billId);
                        jsonObject.put("baseBillPayId",null);
                        BaseRefund baseRefund = baseRefundMapper.selectByPrimaryKey(refundId);
                        // 增加会员积分  1元=1积分
                        creditsShopBiz.ivyConsumeAddCredits(baseRefund.getPatientId(), refundMedicalMony, jsonObject.toJSONString(),(byte) 1);
                    }
                }
            }
        }
        return this;
    }

    /**
     * 增加会员积分
     * @param receivedAmount 应收金额
     * @param baseBillPayId 支付记录ID
     */
    private void addCredits(BigDecimal receivedAmount, Integer baseBillPayId, Integer billId) {
        log.info("(BillCreditsCallbackImpl.java) billId = {}",billId);
        if (billId != null) {
            BaseBill baseBill = baseBillMapper.selectByPrimaryKey(billId);
            log.info("(BillCreditsCallbackImpl.java) baseBill = {}",baseBill);
            if (baseBill == null || baseBill.getBillStatus() == 0 || hasScrapedCredits(baseBill.getPatientId(),billId)) {
                return ;
            }
            log.info("(BillCreditsCallbackImpl.java) receivedAmount = {}",receivedAmount);
            if (receivedAmount != null && baseBillPayId != null) {
                receivedAmount = baseBill.getReceivedAmount();
                // 从总的收费中过滤出医疗费用（不包含医疗护理用品和其他商品）
                BigDecimal medicalExpenses = medicalExpenses(receivedAmount, billId);
                // 增加会员积分  1元=1积分
                JSONObject jsonObject = new JSONObject();
                BaseBillPay query = new BaseBillPay();
                query.setBillId(billId);
                List<BaseBillPay> baseBillPays = baseBillPayMapper.select(query);
                if (StringHelper.isNotEmpty(baseBillPays)) {
                    List<Integer> baseBillPayIds = baseBillPays.stream().mapToInt(BaseBillPay::getBillPayId).boxed().collect(Collectors.toList());
                    jsonObject.put("baseBillPayId",baseBillPayIds);
                    jsonObject.put("baseBillId",billId);
                    jsonObject.put("scrapCharges",0);
                }
                creditsShopBiz.ivyConsumeAddCredits(baseBill.getPatientId(), medicalExpenses, jsonObject.toJSONString(),(byte) 0);
            }
        }
    }

    /**
     * 从总的收费中过滤出医疗费用（不包含医疗护理用品和其他商品）
     * @param receivedAmount 订单总费用
     * @param billId  订单编号
     * @return  返回医疗总费用（不包含医疗护理用品和其他商品）
     */
    private BigDecimal medicalExpenses(BigDecimal receivedAmount, Integer billId) {
        BaseBillDetail bb = new BaseBillDetail();
        bb.setBillId(billId);
        List<BaseBillDetail> billDetails = baseBillDetailMapper.select(bb);
        BigDecimal pPCPsExpenses = new BigDecimal(0);
        if (StringHelper.isNotEmpty(billDetails)) {
            for (BaseBillDetail item: billDetails) {
                Byte itemType = item.getItemType();
                if (itemType.intValue() == 1) {
                    pPCPsExpenses = pPCPsExpenses.add(item.getReceivedAmount() == null ? new BigDecimal(0) : item.getReceivedAmount());
                }
            }
        }
        if (receivedAmount != null && receivedAmount.compareTo(pPCPsExpenses) >= 0) {
            return receivedAmount.subtract(pPCPsExpenses);
        }
        return new BigDecimal(0);
    }

    /**
     * 是否有过调整收费
     * @param patientId 患者ID
     * @return 调整过账单返回true 否则返回false
     */
    private boolean hasScrapedCredits(Integer patientId,Integer baseBillId) {
        try {
            CreditsShop lastCreditsInfo = creditsShopBiz.lastPatientCredits(patientId).getData();
            String remarks = lastCreditsInfo.getRemarks();
            if (StringHelper.isBlank(remarks)) {
                return false;
            }
            JSONObject jsonObject = JSONObject.parseObject(remarks);
            Integer scrapCharges = jsonObject.getInteger("scrapCharges");
            Integer billId = jsonObject.getInteger("baseBillId");
            return scrapCharges != null && scrapCharges == 1 && baseBillId.equals(billId);
        } catch (JSONException jsonExp) {
            return false;
        }
    }
}
