package com.yunya365.wechat.controller;

import com.github.pagehelper.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.report.domain.vo.BenefitItemVo;
import com.yunya.feign.treatment.domain.query.*;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.feign.wechat.domain.model.*;
import com.yunya.feign.wechat.domain.vo.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya365.wechat.service.impl.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;
import javax.validation.*;
import javax.validation.constraints.*;
import java.util.*;

/**
 * @description:
 * @author: xy
 * @date 2021/4/2 10:53
 **/
@Api(tags = {"微信会员中心"})
@RestController
@Slf4j
public class WxController {

    @Resource
    private WXService wxService;

    @GetMapping(value = "/wxVip/auth")
    @ApiOperation(value = "获取用户授权信息")
    public ResponseResult<WxAuthVo> getUserOpenId(@RequestParam String code) {
        return ResponseUtil.success(wxService.getAuthInfo(code));
    }

    @PostMapping(value = "/wxVip/home/register")
    @ApiOperation(value = "会员注册")
    public ResponseResult wxRegister(@NotBlank @RequestParam(required = true) String openId, @Valid @RequestBody WxRegisterModel model) {
        wxService.register(openId, model);
        return ResponseUtil.success();
    }

    @GetMapping(value = "/wxVip/home/vipInfo")
    @ApiOperation(value = "会员中心")
    public ResponseResult<WxVipInfoVo> vipInfo(@NotBlank @RequestParam(required = true) String openId,
                                               @RequestParam(required = false) Integer patientId) {
        WxVipInfoVo wxVipInfoVo = wxService.vipInfo(openId, patientId);
        return ResponseUtil.success(wxVipInfoVo);
    }

    @GetMapping(value = "/wxVip/home/account/list")
    @ApiOperation(value = "会员中心-切换账号")
    public ResponseResult<List<WxFansDetailVO>> listAccount(@NotBlank @RequestParam(required = true) String openId) {
        List<WxFansDetailVO> list = wxService.listAccount(openId);
        return ResponseUtil.success(list);
    }

    @PostMapping(value = "/wxVip/home/treat/record")
    @ApiOperation(value = "会员中心-就诊记录（有分页）")
    public ResponseResult<PageInfo<PatientTreatmentRecordVO>> treatPage(@NotBlank @RequestParam(required = true) String openId,
                                                                           @RequestBody(required = true) PatientTreatmentRecordQueryForm form) {
        PageInfo<PatientTreatmentRecordVO> page = wxService.treatRecordPage(form);
        return ResponseUtil.success(page);
    }

    @GetMapping(value = "/wxVip/home/treat/{treatmentRecordId}")
    @ApiOperation(value = "会员中心-就诊记录-详情")
    public ResponseResult<OrderDetailInfoVO> treatDetail(@NotBlank @RequestParam(required = true) String openId,
                                                                           @PathVariable("treatmentRecordId") Integer treatmentRecordId) {
        OrderDetailInfoVO infoVO = wxService.treatDetail(treatmentRecordId);
        return ResponseUtil.success(infoVO);
    }

    @GetMapping(value = "/wxVip/home/member/record")
    @ApiOperation(value = "会员中心-会员卡/预付款记录")
    public ResponseResult<List<WxCardUseVo>> memberRecord(@NotBlank @RequestParam(required = true) String openId,
                                                             @RequestParam(required = true) String cardNumber,
                                                             @RequestParam(required = true) Integer type) {
        List<WxCardUseVo> wxCardUseVos = wxService.listCardRecord(cardNumber, type);
        return ResponseUtil.success(wxCardUseVos);
    }

    @GetMapping(value = "/wxVip/home/setting")
    @ApiOperation(value = "会员中心-设置")
    public ResponseResult<WxPatientVo> settingInfo(@NotBlank @RequestParam(required = true) String openId,
                                               @RequestParam(required = false) Integer patientId) {
        WxPatientVo wxPatientVo = wxService.settingInfo(openId, patientId);
        return ResponseUtil.success(wxPatientVo);
    }

    @GetMapping(value = "/wxVip/home/card")
    @ApiOperation(value = "会员中心-礼包详情")
    public ResponseResult<List<BenefitItemVo>> cardDetail(@NotNull @RequestParam(required = true) Integer cardId,
                                                          @NotBlank @RequestParam(required = true) String couponName,
                                                          @NotBlank @RequestParam(required = true) String openId) {
        List<BenefitItemVo> benefitItemVos = wxService.listCouponCardUsage(cardId, couponName);
        return ResponseUtil.success(benefitItemVos);
    }

}
