package com.yunya.modules.sms.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.model.SmsAutosendEventModel;
import com.yunya.feign.sms.query.SmsAutosendEventQueryForm;
import com.yunya.feign.sms.vo.SmsAutosendEventVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.sms.biz.SmsAutosendEventBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 简介：短信自动发送管理
 *
 * @author: chenlin
 * @Description: 短信自动发送管理
 * @Date: 2020/12/16 12:45
 * @since: 1.0.0
 */
@Api(tags = "短信自动发送管理")
@RestController
@RequestMapping("smsAutosendEvent")
public class SmsAutosendEventController {

    @Autowired
    private SmsAutosendEventBiz smsAutosendEventBiz;

    /**
     * 分页查询短信自动发送列表
     *
     * @param queryForm 查询参数
     * @return
     */
    @ApiOperation(value = "分页查询短信自动发送列表")
    @PostMapping("/list")
    @CurrentUser
    public ResponseResult<PageInfo<SmsAutosendEventVO>> findSmsAutosendEventList(@RequestBody SmsAutosendEventQueryForm queryForm) {
        queryForm.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        List<SmsAutosendEventVO> smsAutosendEventList = smsAutosendEventBiz.findSmsAutosendEventList(queryForm);
        PageInfo<SmsAutosendEventVO> result = new PageInfo<>(smsAutosendEventList);
        return ResponseUtil.success(result);
    }

    /**
     * 提交关联模板
     *
     * @param smsAutosendEventModel 签名设置添加模型
     */
    @ApiOperation("提交关联模板")
    @PostMapping("/submit")
    @CurrentUser
    @RepeatSubmit
    public ResponseResult<T> submit(@RequestBody @Validated SmsAutosendEventModel smsAutosendEventModel) {
        smsAutosendEventBiz.submit(smsAutosendEventModel);
        return ResponseUtil.success(null);
    }

    /**
     * 启用/关闭
     *
     * @param id 主键id
     */
    @ApiOperation("启用/关闭")
    @PutMapping("/openOrClose/{id}")
    @CurrentUser
    public ResponseResult<String> openOrClose(@PathVariable(value = "id") @NotNull Integer id) {
        smsAutosendEventBiz.openOrClose(id);
        return ResponseUtil.success(null);
    }
}
