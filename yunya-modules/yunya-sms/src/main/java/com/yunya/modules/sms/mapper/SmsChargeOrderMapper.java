package com.yunya.modules.sms.mapper;

import com.yunya.feign.sms.query.SmsChargeOrderQueryForm;
import com.yunya.feign.sms.vo.SmsChargeOrderVO;
import com.yunya.models.sms.SmsChargeOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SmsChargeOrderMapper extends Mapper<SmsChargeOrder> {
    /**
     * 分页查询短信充值列表
     *
     * @param queryForm 查询参数
     * @return
     */
    List<SmsChargeOrderVO> findSmsChargeOrderList(@Param("queryForm") SmsChargeOrderQueryForm queryForm);

    @Override
    int insert(SmsChargeOrder smsChargeOrder);

    SmsChargeOrderVO findSmsChargeOrderByOrderNo(@Param("orderNo") String orderNo);
}