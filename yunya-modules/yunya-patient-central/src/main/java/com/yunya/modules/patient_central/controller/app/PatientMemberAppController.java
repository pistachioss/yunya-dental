package com.yunya.modules.patient_central.controller.app;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.MasertMemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.WxWechatbindAppListVO;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.DictionaryItemModel;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.BillDetailGroupVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.query.XRayFilmQuery;
import com.yunya.feign.treatment_other.domain.vo.XRayFilmVO;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.biz.PatientBaseInfoBiz;
import com.yunya.modules.patient_central.biz.PatientMemberInfoBiz;
import com.yunya.modules.patient_central.biz.WxFansBindBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@IgnoreUserToken
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
    @Resource
    private  PatientBaseInfoBiz patientBaseInfoBiz;
    @Resource
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
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
    public ResponseResult<List<PatientTreatmentRecordVO>> findPatientListInfo(@PathVariable("patientId") Integer patientId) {
        PatientTreatmentRecordQueryForm form = new PatientTreatmentRecordQueryForm();
        form.setWhetherPage(false);
        form.setPatientId(patientId);
        return ResponseUtil.success(remoteTreatmentServiceFeign.patientTreatmentRecordList(form).getList());
    }

    /**
     * 我的-就诊人管理-就诊记录-账单信息
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-就诊记录-账单信息")
    @GetMapping("patient/list/detail/{treatmentRecordId}")
    public ResponseResult<OrderDetailInfoVO> findPatientDetailInfo(@PathVariable(value = "treatmentRecordId") Integer treatmentRecordId) {
        return ResponseUtil.success(remoteTreatmentServiceFeign.findOrderInfoByTreatmentId(treatmentRecordId));
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
    /**
     * 我的-就诊人管理-就诊记录-照片影像
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-就诊记录-根据姓名/病例编号/手机号/姓名拼音模糊查询患者")
    @PostMapping("patient/likePatient")
    public ResponseResult<List<PatientBaseInfoVo>> findPatientLikePatientInfo(@RequestBody @Validated PatientLikeFinleQueryForm query) {
        List<PatientBaseInfoVo> appPatientBaseInfoVos = patientBaseInfoBiz.findPatientByNameAndMobile(query);
        DictionaryItemModel model = new DictionaryItemModel();
        model.setDictionaryTypeId(11);
        List<DictionaryItem> dLsit = remoteSystemServiceFeign.findDictionaryItemList(model);
        Map<String, DictionaryItem> dicMap = new HashMap(16);
        dLsit.forEach(z -> dicMap.put(z.getId() + "", z));
        appPatientBaseInfoVos.forEach(item ->{
            if(dicMap.get(item.getMobileOwner() + "")!=null){
                item.setMobileOwnerName(dicMap.get(item.getMobileOwner() + "").getName());
            }
                }
        );
        return ResponseUtil.success(appPatientBaseInfoVos);
    }
}
