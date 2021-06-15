package com.yunya365.wechat.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.vo.web.WxCardUseVo;
import com.yunya.feign.patient_central.domain.vo.web.WxFansDetailVO;
import com.yunya.feign.patient_central.domain.vo.web.WxPatientVo;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.wechat.domain.model.WxAppointConfirmModel;
import com.yunya.feign.wechat.domain.model.WxRegisterModel;
import com.yunya.feign.wechat.domain.vo.WxAppointDetailVo;
import com.yunya.feign.wechat.domain.vo.WxAuthVo;
import com.yunya.feign.wechat.domain.vo.WxMemberRelationVO;
import com.yunya.feign.wechat.domain.vo.WxRegisterVo;
import com.yunya.feign.wechat.domain.vo.WxVipInfoVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.wechat.service.impl.WXService;
import com.yunya365.wechat.task.WxTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.yunya.framework.common.constant.WXConstant.*;

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
    @Resource
    private WxTask wxTask;

    @GetMapping(value = "/wxVip/auth")
    @ApiOperation(value = "获取用户授权信息")
    public ResponseResult<WxAuthVo> getUserOpenId(@RequestParam String code, HttpServletRequest request) {
        WxAuthVo authInfo = wxService.getAuthInfo(code);
        request.getSession().setAttribute(GZH_SESSION_KEY, authInfo.getOpenId());
        return ResponseUtil.success(authInfo);
    }

    @GetMapping(value = "/wxVip/auth/online/appointment")
    @ApiOperation(value = "线上预约获取用户授权信息")
    public ResponseResult<WxAuthVo> getAuthInfoAndCheckUser(@RequestParam String code,HttpServletRequest request) {
        return wxService.getAuthInfoAndCheckUser(code,request);
    }

    @PostMapping(value = "/wxVip/home/register")
    @ApiOperation(value = "会员注册")
    public ResponseResult<WxRegisterVo> wxRegister(HttpServletRequest request, @Valid @RequestBody WxRegisterModel model) {
        String openId = request.getSession().getAttribute(GZH_SESSION_KEY).toString();
        WxRegisterVo register = wxService.register(openId, model);
        return ResponseUtil.success(register);
    }

    @GetMapping(value = "/wxVip/home/vipInfo")
    @ApiOperation(value = "会员中心")
    public ResponseResult<WxVipInfoVo> vipInfo(HttpServletRequest request,
                                               @RequestParam(required = false) Integer patientId) {
        String openId = request.getSession().getAttribute(GZH_SESSION_KEY).toString();
        WxVipInfoVo wxVipInfoVo = wxService.vipInfo(openId, patientId);
        return ResponseUtil.success(wxVipInfoVo);
    }

    @GetMapping(value = "/wxVip/home/account/list")
    @ApiOperation(value = "会员中心-切换账号")
    public ResponseResult<List<WxFansDetailVO>> listAccount(HttpServletRequest request) {
        String openId = request.getSession().getAttribute(GZH_SESSION_KEY).toString();
        List<WxFansDetailVO> list = wxService.listAccount(openId);
        return ResponseUtil.success(list);
    }

    @PostMapping(value = "/wxVip/home/treat/record")
    @ApiOperation(value = "会员中心-就诊记录（有分页）")
    public ResponseResult<PageInfo<PatientTreatmentRecordVO>> treatPage(@RequestBody(required = true) PatientTreatmentRecordQueryForm form) {
        PageInfo<PatientTreatmentRecordVO> page = wxService.treatRecordPage(form);
        return ResponseUtil.success(page);
    }

    @GetMapping(value = "/wxVip/home/treat/{treatmentRecordId}")
    @ApiOperation(value = "会员中心-就诊记录-详情")
    public ResponseResult<OrderDetailInfoVO> treatDetail(@PathVariable("treatmentRecordId") Integer treatmentRecordId) {
        OrderDetailInfoVO infoVO = wxService.treatDetail(treatmentRecordId);
        return ResponseUtil.success(infoVO);
    }

    @GetMapping(value = "/wxVip/home/member/record")
    @ApiOperation(value = "会员中心-会员卡/预付款记录")
    public ResponseResult<List<WxCardUseVo>> memberRecord(@RequestParam(required = true) String cardNumber,
                                                             @RequestParam(required = true) Integer type) {
        List<WxCardUseVo> wxCardUseVos = wxService.listCardRecord(cardNumber, type);
        return ResponseUtil.success(wxCardUseVos);
    }

    @GetMapping(value = "/wxVip/home/setting")
    @ApiOperation(value = "会员中心-设置")
    public ResponseResult<WxPatientVo> settingInfo(HttpServletRequest request,
                                               @RequestParam(required = false) Integer patientId) {
        String openId = request.getSession().getAttribute(GZH_SESSION_KEY).toString();
        WxPatientVo wxPatientVo = wxService.settingInfo(openId, patientId);
        return ResponseUtil.success(wxPatientVo);
    }

    @GetMapping(value = "/wxVip/home/card")
    @ApiOperation(value = "会员中心-礼包详情")
    public ResponseResult<WxCardUsageVo> cardDetail(@NotNull @RequestParam(required = true) Integer cardId,
                                                          @NotBlank @RequestParam(required = true) String couponName) {
        WxCardUsageVo wxCardUsageVo = wxService.listCouponCardUsage(cardId, couponName);
        return ResponseUtil.success(wxCardUsageVo);
    }

    @GetMapping(value = "/wxVip/home/member/relation")
    @ApiOperation(value = "会员中心-会员卡关联")
    public ResponseResult<WxMemberRelationVO> memberRelation(@RequestParam(required = true) Integer patientId) {
        WxMemberRelationVO relationVO = wxService.memberRelation(patientId);
        return ResponseUtil.success(relationVO);
    }

    @GetMapping(value = "/wxVip/push/appoint")
    @ApiOperation(value = "预约确认推送-预约详情")
    public ResponseResult<WxAppointDetailVo> appointDetail(@RequestParam(required = true) Integer appointId) {
        WxAppointDetailVo appointDetail = wxService.getAppointDetail(appointId);
        return ResponseUtil.success(appointDetail);
    }

    @PutMapping(value = "/wxVip/push/appoint/confirm")
    @ApiOperation(value = "预约确认推送-确认预约")
    public ResponseResult confirmAppoint(@RequestBody WxAppointConfirmModel model) {
         return wxService.confirmAppoint(model);
    }

    @GetMapping(value = "/wxVip/msg/pull")
    public ResponseResult pullTemplate() {
        wxService.pullTemplate();
        return ResponseUtil.success();
    }

    @GetMapping(value = "/wxVip/push/unused/card")
    public void pushUnusedCard() {
        wxTask.cardUnusedTask();
    }

    @GetMapping(value = "/wxVip/push/expiring/card")
    public void pushExpiringCard() {
        wxTask.cardExpiringTask();
    }

    @GetMapping(value = "/wxVip/push/expired/card")
    public void pushExpiredCard() {
        wxTask.cardExpiredTask();
    }

    @GetMapping(value = "/wxVip/push/appoint/confirm")
    public void pushConfirmAppoint() {
        wxTask.appointConfirmTask();
    }

}
