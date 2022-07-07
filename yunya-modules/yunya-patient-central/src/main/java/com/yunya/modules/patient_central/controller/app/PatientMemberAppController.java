package com.yunya.modules.patient_central.controller.app;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.vo.web.MasertMemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.WxWechatbindAppListVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.BillDetailGroupVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.query.XRayFilmQuery;
import com.yunya.feign.treatment_other.domain.vo.XRayFilmVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientMemberInfoBiz;
import com.yunya.modules.patient_central.biz.WxFansBindBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/30
 * @description:
 */
@Api(value = "小程序端-就诊人信息",description = "小程序端-就诊人信息")
@RestController
@RequestMapping("/wx")
public class PatientMemberAppController {

    /** 注入对象 */
    @Resource
    private  PatientMemberInfoBiz patientMemberInfoBiz;
    @Resource
    private WxFansBindBiz wxFansBindBiz;
    @Resource
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Resource
    private RemoteTreatmentOtherFeign remoteTreatmentOtherFeign;
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

    /**
     * 我的-就诊人管理-就诊记录
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-就诊记录")
    @GetMapping("patient/list/{patientId}")
    public ResponseResult<PageInfo<PatientTreatmentRecordVO>> findPatientListInfo(@PathVariable("patientId") Integer patientId) {
        PatientTreatmentRecordQueryForm form = new PatientTreatmentRecordQueryForm();
        form.setWhetherPage(false);
        form.setPatientId(patientId);
        return ResponseUtil.success(remoteTreatmentServiceFeign.patientTreatmentRecordList(form));
    }

    /**
     * 我的-就诊人管理-就诊记录-账单信息
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-就诊记录-账单信息")
    @GetMapping("patient/list/detail/{orderRecordId}")
    public ResponseResult<BillDetailGroupVO> findPatientDetailInfo(@PathVariable("orderRecordId") Integer orderRecordId) {
        return ResponseUtil.success(remoteTreatmentServiceFeign.findOrderDetailAndBillDetailByOrderRecordId(orderRecordId));
    }

    /**
     * 我的-就诊人管理-就诊记录-照片影像
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-就诊记录-照片影像")
    @PostMapping("patient/list/photo")
    public ResponseResult<List<XRayFilmVO>> findPhotoListInfo(@RequestBody @Validated XRayFilmQuery query) {
        return ResponseUtil.success(remoteTreatmentOtherFeign.findPhotoListInfo(query));
    }

}
