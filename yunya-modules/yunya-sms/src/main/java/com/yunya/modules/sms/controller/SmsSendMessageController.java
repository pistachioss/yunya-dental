package com.yunya.modules.sms.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.query.SmsSendBatchQueryForm;
import com.yunya.feign.sms.query.SmsSendRecordQueryForm;
import com.yunya.feign.sms.vo.SmsSendBatchVO;
import com.yunya.feign.sms.vo.SmsSendVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.sms.biz.SmsSendBatchBiz;
import com.yunya.modules.sms.biz.SmsSendRecordBiz;
import com.yunya.modules.sms.enums.SmsTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 简介：短信发送管理
 *
 * @author: chenlin
 * @Description: 短信发送管理
 * @Date: 2020/12/14 20:45
 * @since: 1.0.0
 */
@Api(tags = "短信发送管理")
@RestController
@RequestMapping("smsSendMessage")
public class SmsSendMessageController {

    @Autowired
    private SmsSendBatchBiz smsSendBatchBiz;
    @Autowired
    private SmsSendRecordBiz smsSendRecordBiz;

    /**
     * 分页查询短信发送批次列表
     *
     * @param queryForm 查询参数
     * @return
     */
    @ApiOperation(value = "分页查询短信发送批次列表")
    @PostMapping("/batchlist")
    public ResponseResult<PageInfo<SmsSendBatchVO>> findSmsSendBatchList(@RequestBody SmsSendBatchQueryForm queryForm) {
        List<SmsSendBatchVO> smsSendBatchList = smsSendBatchBiz.findSmsSendBatchList(queryForm);
        PageInfo<SmsSendBatchVO> result = new PageInfo<>(smsSendBatchList);
        return ResponseUtil.success(result);
    }

    /**
     * 分页查询短信发送记录列表
     *
     * @param queryForm 查询参数
     * @return
     */
    @ApiOperation(value = "分页查询短信发送记录列表")
    @PostMapping("/recordlist")
    public ResponseResult<SmsSendVO> findSmsSendRecordList(@RequestBody SmsSendRecordQueryForm queryForm) {
        if (SmsTypeEnum.VERIFY_CODE.getCode().equals(queryForm.getType())) {
            Set<Integer> batchIds = new HashSet<>();
            SmsSendBatchQueryForm batchQueryForm = new SmsSendBatchQueryForm();
            batchQueryForm.setWhetherPage(false);
            batchQueryForm.setType(SmsTypeEnum.VERIFY_CODE.getCode());
            List<SmsSendBatchVO> smsSendBatchVOS = smsSendBatchBiz.findSmsSendBatchList(batchQueryForm);
            smsSendBatchVOS.forEach(smsSendBatchVO -> batchIds.add(smsSendBatchVO.getId()));
            queryForm.setBatchIds(batchIds);
        }
        SmsSendVO smsSendVO = smsSendRecordBiz.findSmsSendRecordPageList(queryForm);
        return ResponseUtil.success(smsSendVO);
    }
}
