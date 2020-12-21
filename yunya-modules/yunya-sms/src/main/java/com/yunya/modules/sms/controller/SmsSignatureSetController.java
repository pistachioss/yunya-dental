package com.yunya.modules.sms.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.form.SmsSignatureSetForm;
import com.yunya.feign.sms.model.SmsSignatureSetModel;
import com.yunya.feign.sms.query.SmsSignatureSetQueryForm;
import com.yunya.feign.sms.vo.SmsSignatureSetVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.sms.biz.SmsSignatureSetBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 简介：短信签名设置管理
 *
 * @author: chenlin
 * @Description: 短信签名设置管理
 * @Date: 2020/12/10 20:45
 * @since: 1.0.0
 */
@Api(tags = "短信签名设置管理")
@RestController
@RequestMapping("smsSignatureSet")
public class SmsSignatureSetController {

    @Autowired
    private SmsSignatureSetBiz smsSignatureSetBiz;

    /**
     * 分页查询短信签名列表
     *
     * @param smsSignatureSetQueryForm 查询参数
     * @return
     */
    @ApiOperation(value = "分页查询短信签名列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<SmsSignatureSetVO>> findSmsSignatureSetList(@RequestBody SmsSignatureSetQueryForm smsSignatureSetQueryForm) {
        List<SmsSignatureSetVO> smsSignatureSetVOS = smsSignatureSetBiz.findSmsSignatureSetList(smsSignatureSetQueryForm);
        PageInfo<SmsSignatureSetVO> result = new PageInfo<>(smsSignatureSetVOS);
        return ResponseUtil.success(result);
    }

    /**
     * 添加短信签名
     *
     * @param smsSignatureSetModel 签名设置添加模型
     */
    @ApiOperation("添加短信签名")
    @CurrentUser
    @RepeatSubmit
    @RequestMapping(value = "add", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseResult add(@RequestParam("files") final List<MultipartFile> files, SmsSignatureSetModel smsSignatureSetModel) {
        smsSignatureSetBiz.add(files, smsSignatureSetModel);
        return ResponseUtil.success(null);
    }

    /**
     * 修改短信签名
     *
     * @param smsSignatureSetForm 签名设置修改模型
     */
    @ApiOperation("修改短信签名")
    @PutMapping("/update")
    @CurrentUser
    @RepeatSubmit
    @Deprecated
    public ResponseResult update(@RequestBody @Validated SmsSignatureSetForm smsSignatureSetForm) {
        smsSignatureSetBiz.update(smsSignatureSetForm);
        return ResponseUtil.success(null);
    }

    /**
     * 删除短信签名
     *
     * @param id 主键id
     */
    @ApiOperation("删除短信签名")
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") @NotNull Integer id) {
        smsSignatureSetBiz.delete(id);
        return ResponseUtil.success(null);
    }

    /**
     * 根据id获取短信签名
     *
     * @param id 主键id
     */
    @ApiOperation(value = "根据id获取短信签名")
    @GetMapping("/info/{id}")
    public ResponseResult<SmsSignatureSetVO> findSmsSignatureSetById(@PathVariable(value = "id") @NotNull Integer id) {
        SmsSignatureSetVO smsSignatureSetVO = smsSignatureSetBiz.findSmsSignatureSetById(id);
        return ResponseUtil.success(smsSignatureSetVO);
    }
}
