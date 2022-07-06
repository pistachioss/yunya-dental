package com.yunya.modules.patient_central.controller.app;

import com.yunya.feign.patient_central.domain.vo.web.MasertMemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.WxWechatbindAppListVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientMemberInfoBiz;
import com.yunya.modules.patient_central.biz.WxFansBindBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/30
 * @description:
 */
@Api(value = "小程序端-会员信息")
@RestController
@RequestMapping("/wx")
public class PatientMemberAppController {

    /** 注入对象 */
    @Resource
    private  PatientMemberInfoBiz patientMemberInfoBiz;
    @Resource
    private WxFansBindBiz wxFansBindBiz;

    /**
     * 我的-会员信息
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-会员信息")
    @GetMapping("patientMember/{unionId}")
    public ResponseResult<MasertMemberInfoVo> findMemberBaseInfo(@PathVariable("unionId") String unionId) {
        return ResponseUtil.success(patientMemberInfoBiz.findMasertMember(unionId));
    }

    /**
     * 我的-就诊人管理
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-就诊人管理")
    @GetMapping("patient/{unionId}")
    public ResponseResult<List<WxWechatbindAppListVO>> findPatientBaseInfo(@PathVariable("unionId") String unionId) {
        return ResponseUtil.success(wxFansBindBiz.findPatientBaseInfo(unionId));
    }

}
