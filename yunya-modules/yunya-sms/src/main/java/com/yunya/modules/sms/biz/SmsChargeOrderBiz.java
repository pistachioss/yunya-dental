package com.yunya.modules.sms.biz;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yunya.feign.sms.model.SmsChargeOrderModel;
import com.yunya.feign.sms.query.SmsChargeOrderQueryForm;
import com.yunya.feign.sms.vo.SmsChargeOrderVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.sms.SmsChargeOrder;
import com.yunya.modules.sms.enums.SmsOrderStatusEnum;
import com.yunya.modules.sms.exception.SignException;
import com.yunya.modules.sms.mapper.SmsChargeOrderMapper;
import com.yunya.modules.sms.utl.WikiUtl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_TRANSFORMATION_EXIST;

/**
 * 简介：短信充值订单业务层
 *
 * @author: chenlin
 * @Description: 短信充值订单业务层
 * @Date: 2020/12/12 13:39
 * @since: 1.0.0
 */
@Service
@Transactional
public class SmsChargeOrderBiz extends BaseBiz<SmsChargeOrderMapper, SmsChargeOrder> {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsOrgStatisticsBiz smsOrgStatisticsBiz;

    /**
     * 分页查询短信充值列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<SmsChargeOrderVO> findSmsChargeOrderList(SmsChargeOrderQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findSmsChargeOrderList(queryForm);
    }

    /**
     * 创建短信充值订单，返回支付二维码链接
     *
     * @param smsChargeOrderModel 签名设置添加模型
     */
    public SmsChargeOrderVO create(SmsChargeOrderModel smsChargeOrderModel) {
        SmsChargeOrder smsChargeOrder = new SmsChargeOrder();
        BeanUtil.copyProperties(smsChargeOrderModel, smsChargeOrder);
        smsChargeOrder.setOrderStatus(SmsOrderStatusEnum.WAIT_PAY.getCode());
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        smsChargeOrder.setOrgId(orgId);
        Long amount = smsChargeOrder.getPrice().multiply(new BigDecimal(100)).longValue();
        String orderNo = UUID.randomUUID().toString();
        smsChargeOrder.setOrderNo(orderNo);
        int count = mapper.insert(smsChargeOrder);
        if (count != 1) {
            throw new ClientServiceException("插入数据失败", OperationCodeConstants.INSERT_MODEL);
        }
        JSONArray goodList = getGoodList(smsChargeOrder.getSmsGoods(), smsChargeOrder.getPrice());
        SmsChargeOrderVO smsChargeOrderVO = new SmsChargeOrderVO();
        smsChargeOrderVO.setId(smsChargeOrder.getId());
        smsChargeOrderVO.setQrcode(WikiUtl.createOrder(orderNo, amount, goodList));
        return smsChargeOrderVO;
    }

    /**
     * 获取订单的商品列表
     *
     * @param goodName
     * @param goodPrice
     * @return
     */
    private JSONArray getGoodList(String goodName, BigDecimal goodPrice) {
        JSONObject good = new JSONObject();
        good.put("goods_name", goodName);
        good.put("sell_amount", "1");
        good.put("goods_price", goodPrice);
        good.put("goods_id","");
        good.put("goods_num","");
        good.put("goods_sku_id","");
        JSONArray goodList = new JSONArray();
        goodList.add(good);
        return goodList;
    }

    /**
     * 刷新充值二维码
     *
     * @param id 主键id
     */
    public String refreshQrCode(Integer id) {
        SmsChargeOrder smsChargeOrder = selectById(id);
        if (smsChargeOrder == null) {
            throw new ClientServiceException("充值订单不存在", DATA_NOT_EXIST);
        }
        Byte orderStatus = smsChargeOrder.getOrderStatus();
        if (!SmsOrderStatusEnum.WAIT_PAY.getCode().equals(orderStatus)) {
            throw new ClientServiceException("充值订单已支付，请稍后", DATA_NOT_EXIST);
        }
        String orderNo = UUID.randomUUID().toString();
        smsChargeOrder.setOrderNo(orderNo);
        updateSelectiveById(smsChargeOrder);
        Long amount = smsChargeOrder.getPrice().multiply(new BigDecimal(100)).longValue();
        JSONArray goodList = getGoodList(smsChargeOrder.getSmsGoods(), smsChargeOrder.getPrice());
        return WikiUtl.createOrder(orderNo, amount, goodList);
    }

    /**
     * 采商支付返回的异步通知处理
     *
     * @param request 请求
     */
    public void notifyUrl(HttpServletRequest request) {
        Map<String, String> params = getRequestParameters(request);
        String sign = params.remove("sign");
        if (WikiUtl.rsaCheck(params,sign)) {
            throw new SignException("caibao sign invalid");
        }
        String orderNo = params.get("orderNo");
        SmsChargeOrderVO smsChargeOrderVO = mapper.findSmsChargeOrderByOrderNo(orderNo);
        if (orderNo == null) {
            throw new SignException("sign invalid,  request params: " + JSONObject.toJSONString(request.getParameterMap()));
        }
        String payTimeStr = params.get("payTime");
        String orderStatus = params.get("orderStatus");
        Byte status = smsChargeOrderVO.getOrderStatus();
        if (!SmsOrderStatusEnum.PAY_SUC.getCode().equals(status)) {
            SmsChargeOrder smsChargeOrder = new SmsChargeOrder();
            smsChargeOrder.setId(smsChargeOrderVO.getId());
            Date payTime = null;
            if (StringHelper.isNotEmpty(payTimeStr )) {
                try {
                    payTime = new SimpleDateFormat("yyyyMMddHHMmmss").parse(payTimeStr);
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误",DATA_TRANSFORMATION_EXIST);
                }
            }
            switch (orderStatus) {
                case "PAY_SUC": {//付款成功
                    status = SmsOrderStatusEnum.PAY_SUC.getCode();
                    smsOrgStatisticsBiz.incrByOrgId(smsChargeOrderVO.getSmsNum(),
                            smsChargeOrderVO.getPrice(), smsChargeOrderVO.getOrgId());
                    break;
                }
                case "PAY_FAIL": {//付款失败
                    status = SmsOrderStatusEnum.PAY_FAIL.getCode();
                    break;
                }
                case "WAIT_PAY"://等待付款
                case "PART_REFUND"://部分退款成功
                case "ALL_REFUND"://退款成功
                case "REVERSED"://订单撤销成功
                case "CLOSED"://订单撤销成功
                case "CANCEL"://已取消 (历史状态，已废弃，新接入用户不用考虑)
                default:break;
            }
            smsChargeOrder.setOrderStatus(status);
            if (payTime != null) {
                smsChargeOrder.setPayTime(payTime);
            }
            smsChargeOrder.setUptTime(new Date(System.currentTimeMillis()));
            mapper.updateByPrimaryKeySelective(smsChargeOrder);
        }
    }

    /**
     * 获取请求参数
     * @param request
     * @return
     */
    private Map<String, String> getRequestParameters(HttpServletRequest request) {
        String cbOrderNo = request.getParameter("cbOrderNo");//采商订单号
        String orderNo = request.getParameter("appOrderNo");//充值订单号
        String outOrderNo = request.getParameter("outOrderNo");//支付宝或微信交易订单号
        String orderStatus = request.getParameter("orderStatus");//订单状态
        String totalAmount = request.getParameter("totalAmount");//订单总额，以分为单位
        String receiveAmount = request.getParameter("receiveAmount");//实收金额，以分为单位
        String paymentChannel = request.getParameter("paymentChannel");//支付渠道： ALIPAY-支付宝，WECHAT-微信
        String subject = request.getParameter("subject"); //订单简介
        String discountAmount = request.getParameter("discountAmount");//优惠金额，以分为单位
        String paymentWay = request.getParameter("paymentWay");//支付方式：BARCODE-条码支付，SCAN-扫码支付，SMILE-刷脸支付，VIPCARD-会员余额支付
        String payTimeStr = request.getParameter("payTime");//支付时间，数字格式，值为距离1970.1.1日的毫秒数
        String sign = request.getParameter("sign");//签名
        Map<String, String> params = new HashMap<>();
        params.put("cbOrderNo", cbOrderNo);
        params.put("orderNo", orderNo);
        params.put("outOrderNo", outOrderNo);
        params.put("orderStatus", orderStatus);
        params.put("totalAmount", totalAmount);
        params.put("receiveAmount", receiveAmount);
        params.put("paymentChannel", paymentChannel);
        params.put("subject", subject);
        params.put("discountAmount", discountAmount);
        params.put("paymentWay", paymentWay);
        params.put("payTime", payTimeStr);
        params.put("sign", sign);
        log.info("sms charge order notifyUrl request params: {}", JSONObject.toJSONString(params));
        return params;
    }
}
