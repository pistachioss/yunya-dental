package com.yunya.modules.patient_central.controller.app;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.app.MasertMemberRechargeRecordDetailVo;
import com.yunya.feign.patient_central.domain.vo.app.MasertMemberRechargeRecordVo;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.DictionaryItemModel;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.AppMemberRechargePrepaidFrom;
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
import java.util.Objects;

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
    @GetMapping("patientMember/{patientId}")
    public ResponseResult<PatientPublicInfoVo> findMemberBaseInfo(@PathVariable("patientId") Integer patientId) {
        return ResponseUtil.success( patientBaseInfoBiz.findPatientPublicInfoById(patientId));
    }

    @ApiOperation("小程序-我的-会员信息-详情")
    @GetMapping("patientMember/detail/{patientId}/{unionId}")
    public ResponseResult<MasertMemberDetailVo> findMemberDetailInfo(@PathVariable("patientId") Integer patientId,
                                                                     @PathVariable("unionId") String unionId) {
        MasertMemberDetailVo masertMemberDetailVo = new MasertMemberDetailVo();
        //已绑定主卡信息
        List<PatientCardOwnerInfoVo> patientCardOwnerInfoVos = patientMemberInfoBiz.findPatientCardOwnerInfo(patientId);
        //患者会员卡关联关系
        PatientMemberRelationQueryForm form = new PatientMemberRelationQueryForm();
        form.setPatientId(patientId);
        MemberRelationVo memberRelationVo = patientMemberInfoBiz.findMemberBindingRelation(form);

        PatientPublicInfoVo patientPublicInfoVo = patientBaseInfoBiz.findPatientPublicInfoById(patientId);

        masertMemberDetailVo.setMemberRelationVo(memberRelationVo);
        masertMemberDetailVo.setPatientCardOwnerInfoVos(patientCardOwnerInfoVos);
        masertMemberDetailVo.setPatientPublicInfoVo(patientPublicInfoVo);

        MasertMemberInfoVo masertMemberInfoVo = patientMemberInfoBiz.findMasertMember(unionId);
        if(masertMemberInfoVo!=null){
            masertMemberDetailVo.setPoint(masertMemberInfoVo.getPoint());
        }
        List<WxWechatbindAppListVO>list = wxFansBindBiz.findPatientBaseInfo(unionId);
        WxWechatbindAppListVO wxWechatbindAppListVO =
                list.stream().filter(s -> Objects.equals(s.getPatientId(), patientId)).findFirst().orElse(null);
        if(wxWechatbindAppListVO!=null){
            masertMemberDetailVo.setDictionaryName(wxWechatbindAppListVO.getDictionaryName());
        }
        return ResponseUtil.success(masertMemberDetailVo);
    }

    @ApiOperation("小程序-我的-会员卡记录")
    @PostMapping("patientMember/rechargePrepaid")
    public ResponseResult<MasertMemberRechargeRecordVo> findMemberRechargeRecordInfo(@RequestBody @Validated AppMemberRechargePrepaidFrom from) {
        MasertMemberRechargeRecordVo masertMemberRechargeRecordVo = new MasertMemberRechargeRecordVo();

        //会员信息
        PatientPublicInfoVo patientPublicInfoVo = patientBaseInfoBiz.findPatientPublicInfoById(from.getPatientId());
        //会员卡记录
        MemberExpendRecordQueryForm recordQueryForm = new MemberExpendRecordQueryForm();
        recordQueryForm.setMemberId(from.getCardNumber());
        recordQueryForm.setPatientId(from.getPatientId());
        List<MasertMemberRechargeRecordDetailVo>list = patientMemberInfoBiz.findMemberRechargeRecordInfo(recordQueryForm);
        //预付款记录
        recordQueryForm.setMemberId(from.getPrepaymentNumber());
        List<MasertMemberRechargeRecordDetailVo>prelist = patientMemberInfoBiz.findMemberPrepaidRecordInfo(recordQueryForm);

        masertMemberRechargeRecordVo.setPrelist(prelist);
        masertMemberRechargeRecordVo.setList(list);
        masertMemberRechargeRecordVo.setMemberCardMoneySum(patientPublicInfoVo.getMemberCardMoneySum());
        masertMemberRechargeRecordVo.setPrepaymentsMoneySum(patientPublicInfoVo.getPrepaymentsMoneySum());

        return ResponseUtil.success(masertMemberRechargeRecordVo);
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
     * 小程序-我的-就诊记录-根据姓名/病例编号/手机号/姓名拼音模糊查询患者
     *
     * @param
     * @return ResponseResult<MemberBaseInfoVo>
     */
    @ApiOperation("小程序-我的-就诊记录-根据姓名/病例编号/手机号/姓名拼音模糊查询患者")
    @PostMapping("patient/likePatient")
    public ResponseResult<PageInfo<PatientBaseInfoVo>> findPatientLikePatientInfo(@RequestBody @Validated PatientLikeFinleQueryForm query) {

        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
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
        return ResponseUtil.success(new PageInfo<>(appPatientBaseInfoVos));
    }
}
