package com.yunya.modules.sms.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.form.SmsTemplateSetForm;
import com.yunya.feign.sms.model.SmsTemplateSetModel;
import com.yunya.feign.sms.query.SmsTemplateSetQueryForm;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.sms.biz.SmsTemplateSetBiz;
import com.yunya.modules.sms.vo.SmsTemplateReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_ERROR;

/**
 * 简介：短信模板设置管理
 *
 * @author: chenlin
 * @Description: 短信模板设置管理
 * @Date: 2020/12/10 20:45
 * @since: 1.0.0
 */
@Api(tags = "短信模板设置管理")
@RestController
@RequestMapping("smsTemplateSet")
public class SmsTemplateSetController {

    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;

    /**
     * 分页查询短信模板列表
     *
     * @param smsTemplateSetQueryForm 查询参数
     * @return
     */
    @ApiOperation(value = "分页查询短信模板列表")
    @PostMapping("/list")
    @CurrentUser
    public ResponseResult<PageInfo<SmsTemplateSetVO>> findSmsTemplateSetList(@RequestBody SmsTemplateSetQueryForm smsTemplateSetQueryForm) {
        smsTemplateSetQueryForm.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        List<SmsTemplateSetVO> smsTemplateSetList = smsTemplateSetBiz.findSmsTemplateSetList(smsTemplateSetQueryForm);
        PageInfo<SmsTemplateSetVO> result = new PageInfo<>(smsTemplateSetList);
        return ResponseUtil.success(result);
    }

    /**
     * 添加短信模板
     *
     * @param smsTemplateSetModel 模板设置添加模型
     */
    @ApiOperation("添加短信模板")
    @PostMapping("/add")
    @CurrentUser
    @RepeatSubmit
    public ResponseResult add(@RequestBody @Validated SmsTemplateSetModel smsTemplateSetModel) {
        smsTemplateSetBiz.add(smsTemplateSetModel);
        return ResponseUtil.success(null);
    }

    /**
     * 修改短信模板
     *
     * @param smsTemplateSetForm 模板设置修改模型
     */
    @ApiOperation("修改短信模板")
    @PutMapping("/update")
    @CurrentUser
    @RepeatSubmit
    public ResponseResult update(@RequestBody @Validated SmsTemplateSetForm smsTemplateSetForm) {
        smsTemplateSetBiz.update(smsTemplateSetForm);
        return ResponseUtil.success(null);
    }

    /**
     * 删除短信模板
     *
     * @param id 主键id
     */
    @ApiOperation("删除短信模板")
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") @NotNull Integer id) {
        smsTemplateSetBiz.delete(id);
        return ResponseUtil.success(null);
    }

    /**
     * 根据id获取短信模板
     *
     * @param id 主键id
     */
    @ApiOperation(value = "根据id获取短信模板")
    @GetMapping("/info/{id}")
    public ResponseResult<SmsTemplateSetVO> findSmsTemplateSetById(@PathVariable(value = "id") @NotNull Integer id) {
        SmsTemplateSetVO smsSignatureSetVO = smsTemplateSetBiz.findSmsTemplateSetById(id, true);
        return ResponseUtil.success(smsSignatureSetVO);
    }

    /**
     * 阿里云短信模板审核推送通知
     *
     * @param
     */
    @PostMapping("/templateSmsReport")
    public void templateSmsReport(@RequestBody List<SmsTemplateReportVO> smsTemplateReportVOS, HttpServletResponse response) {
        smsTemplateSetBiz.templateSmsReport(smsTemplateReportVOS);
        try (PrintWriter out = response.getWriter()) {
            JSONObject object = new JSONObject();
            object.put("code", 0);
            object.put("msg", "成功");
            out.println(object);
            out.flush();
        } catch (IOException e) {
            throw new ClientServiceException("templateSmsReport response io error", DATA_ERROR);
        }
    }
}
