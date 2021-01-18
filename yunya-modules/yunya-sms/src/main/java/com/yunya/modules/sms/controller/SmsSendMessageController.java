package com.yunya.modules.sms.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.model.AppointmentSmsSendRecordModel;
import com.yunya.feign.sms.query.SmsSendBatchQueryForm;
import com.yunya.feign.sms.query.SmsSendRecordQueryForm;
import com.yunya.feign.sms.vo.SmsSendBatchVO;
import com.yunya.feign.sms.vo.SmsSendVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.sms.biz.SmsSendBatchBiz;
import com.yunya.modules.sms.biz.SmsSendRecordBiz;
import com.yunya.modules.sms.enums.SmsTypeEnum;
import com.yunya.modules.sms.vo.SmsSendReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_ERROR;

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
    @CurrentUser
    public ResponseResult<PageInfo<SmsSendBatchVO>> findSmsSendBatchList(@RequestBody SmsSendBatchQueryForm queryForm) {
        queryForm.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
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
    @CurrentUser
    public ResponseResult<SmsSendVO> findSmsSendRecordList(@RequestBody SmsSendRecordQueryForm queryForm) {
        if (SmsTypeEnum.VERIFY_CODE.getCode().equals(queryForm.getType())) {
            Set<Integer> batchIds = new HashSet<>();
            SmsSendBatchQueryForm batchQueryForm = new SmsSendBatchQueryForm();
            batchQueryForm.setWhetherPage(false);
            batchQueryForm.setType(SmsTypeEnum.VERIFY_CODE.getCode());
            batchQueryForm.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            List<SmsSendBatchVO> smsSendBatchVOS = smsSendBatchBiz.findSmsSendBatchList(batchQueryForm);
            smsSendBatchVOS.forEach(smsSendBatchVO -> batchIds.add(smsSendBatchVO.getId()));
            queryForm.setBatchIds(batchIds);
        }
        queryForm.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        SmsSendVO smsSendVO = smsSendRecordBiz.findSmsSendRecordPageList(queryForm);
        return ResponseUtil.success(smsSendVO);
    }

    /**
     * 发送预约短信
     *
     * @param templateId 短信模板id
     * @param models 短信预约提醒列表
     * @return
     */
    @ApiOperation(value = "发送预约短信")
    @PostMapping("/sendAppointmentBatchSms/{templateId}")
    @CurrentUser
    @RepeatSubmit
    public ResponseResult<T> sendAppointmentBatchSms(@PathVariable(value = "templateId") @NotNull Integer templateId, @RequestBody @Validated List<AppointmentSmsSendRecordModel> models) {
        return smsSendRecordBiz.sendAppointmentBatchSms(templateId, models);
    }

    /**
     * 阿里云短信发送状态推送通知
     *
     * @param
     */
    @PostMapping("/smsReport")
    public void smsReport(@RequestBody List<SmsSendReportVO> smsSendReportVOS, HttpServletResponse response) {
        smsSendRecordBiz.smsReport(smsSendReportVOS);
        try (PrintWriter out = response.getWriter()) {
            JSONObject object = new JSONObject();
            object.put("code", 0);
            object.put("msg", "成功");
            out.println(object);
            out.flush();
        } catch (IOException e) {
            throw new ClientServiceException("smsReport response io error", DATA_ERROR);
        }
    }
}
