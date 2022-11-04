package com.yunya365.mini.service.impl;

import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya365.mini.entity.WxRequestRecord;
import com.yunya365.mini.mapper.WxRequestRecordMapper;
import com.yunya365.mini.service.IWxRequestRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 微信请求记录 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-11-02
 */
@Service
public class WxRequestRecordServiceImpl extends ServiceImpl<WxRequestRecordMapper, WxRequestRecord> implements IWxRequestRecordService {

    @Override
    public void saveRecord(Integer type, String requestJson, String responseJson) {
        String userID = BaseContextHandler.getUserID();
        Integer userId = Objects.isNull(userID) ? null : Integer.valueOf(userID);
        WxRequestRecord wxRequestRecord = new WxRequestRecord();
        wxRequestRecord.setType(type);
        wxRequestRecord.setRequestJson(requestJson);
        wxRequestRecord.setResponseJson(responseJson);
        wxRequestRecord.setCrtId(userId);
        wxRequestRecord.setUpdId(userId);
        super.save(wxRequestRecord);
    }
}
