package com.yunya365.mini.service;

import com.yunya365.mini.entity.WxRequestRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 微信请求记录 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-11-02
 */
public interface IWxRequestRecordService extends IService<WxRequestRecord> {

    /**
     * 保存微信http请求
     * @param type: 类型(0-请求付款 1-请求退款 2-付款通知 3-退款通知)
	 * @param requestJson:
	 * @param responseJson:
     */
    void saveRecord(Integer type, String requestJson, String responseJson);
}
