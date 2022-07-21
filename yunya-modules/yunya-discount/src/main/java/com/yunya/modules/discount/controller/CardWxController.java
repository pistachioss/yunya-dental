package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.vo.CardWxDetailVO;
import com.yunya.feign.discount.domain.vo.CardWxVO;
import com.yunya.feign.discount.domain.vo.PatientCardSharerVo;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.vo.BenefitItemVo;
import com.yunya.feign.report.domain.vo.CardWxItemVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.BaseCategoryInfoVO;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.modules.discount.biz.CardBiz;
import com.yunya.modules.discount.biz.VoucherDiscountItemBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/19
 * @description:
 */
@Api(value = "小程序端-我的-待使用产品",description = "小程序端-我的-待使用产品")
@RestController
@RequestMapping("/wx")
@IgnoreUserToken
public class CardWxController {
    @Resource
    private CardBiz cardBiz;
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;
    @Autowired
    private RemoteReportServiceFeign remoteReportServiceFeign;
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Autowired private VoucherDiscountItemBiz voucherDiscountItemBiz;

    /**
     * 我的-会员信息
     *
     * @param
     */
    @ApiOperation("小程序-我的-待使用产品列表")
    @GetMapping("/card/{patientId}")
    public ResponseResult<List<CardWxVO>> findCardWxList(@PathVariable("patientId") Integer patientId) {
        return ResponseUtil.success( cardBiz.findCardWxList(patientId));
    }
    @ApiOperation("小程序-我的-待使用产品列表-获取门诊列表")
    @GetMapping("/card/orgList")
    public ResponseResult<List<OrganizationInfoDetail>> findOrgInfoListList() {
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = systemServiceFeign.findOrgInfoList(organizationModel);
        return  ResponseUtil.success( clinics);
    }

    @ApiOperation("小程序-我的-待使用产品列表-产品详情")
    @GetMapping("/card/detail/{couponId}")
    public ResponseResult<List<CardWxDetailVO>> findCardWxDetail(@PathVariable("couponId") Integer couponId) {
        return ResponseUtil.success( cardBiz.findCardWxDetail(couponId));
    }

    @ApiOperation(value = "小程序-我的-待使用产品列表-查询已配置共享人")
    @GetMapping("/configuration/{cardId}")
    public ResponseResult<List<PatientCardSharerVo>> getConfiguredSharer(@PathVariable(value = "cardId") Integer cardId) {
        List<PatientCardSharerVo> configuredSharer = cardBiz.getConfiguredSharer(cardId);
        return ResponseUtil.success(configuredSharer);
    }

    @ApiOperation(value = "小程序-我的-待使用产品列表-使用权益（2-兑换券，3-套餐券）")
    @GetMapping("/benefit/multi/{cardId}")
    public ResponseResult<List<CardWxItemVO>> getUseRecord(@PathVariable(value = "cardId") Integer cardId) {
        List<BenefitItemVo>list = remoteReportServiceFeign.listWxCouponsUseItem(cardId);
        List<CardWxItemVO> newList = list.stream()
                .map(e -> new CardWxItemVO(e.getItemName(), e.getOriginalQuantity(), e.getRemainingQuantity()))
                .collect(Collectors.toList());
        return ResponseUtil.success(newList);
    }

    @ApiOperation(value = "小程序-我的-待使用产品列表-使用权益（0-代金券，1-折扣券）")
    @GetMapping("/vouDis/multi/{couponId}")
    public ResponseResult<List<CardWxItemVO>> getvouDis(@PathVariable(value = "couponId") Integer couponId) {
        VoucherDiscountItem voucherDiscountItem = new VoucherDiscountItem();
        voucherDiscountItem.setCouponId(couponId);
        List<VoucherDiscountItem>list = voucherDiscountItemBiz.selectList(voucherDiscountItem);

        List<String> tids = list.stream().map(obj->{
            if(obj.getType()==0){
                return obj.getItemId()+"";
            }
            return null;
        }
        ).collect(Collectors.toList());
        tids.removeAll(Collections.singleton(null));
        String[] tarIds = tids.toArray(new String[tids.size()]);

        List<String> oids = list.stream().map(obj->{
                    if(obj.getType()==1){
                        return obj.getItemId()+"";
                    }
                    return null;
                }
        ).collect(Collectors.toList());
        oids.removeAll(Collections.singleton(null));

        String[] oraIds = oids.toArray(new String[oids.size()]);

        List<CardWxItemVO>reList = new ArrayList<>();
        if(tids.size()>0){
            String tarString =  remoteTreatmentServiceFeign.findBaseTariffNamesByIds(tarIds);
            List<String> tarList = Arrays.asList(tarString.split(","));
            List<CardWxItemVO>tarReList = tarList.stream().map(t -> {
                CardWxItemVO cardWxItemVO = new CardWxItemVO(t,null,null);
                return cardWxItemVO;
            }).collect(Collectors.toList());
            reList.addAll(tarReList);
        }
        //价目
        if(oids.size()>0) {
            //商品
            String oraString = remoteTreatmentServiceFeign.findBaseOralNamesByIds(oraIds);
            List<String> oraList = Arrays.asList(oraString.split(","));
            List<CardWxItemVO>oraReList = oraList.stream().map(t -> {
                CardWxItemVO cardWxItemVO = new CardWxItemVO(t,null,null);
                return cardWxItemVO;
            }).collect(Collectors.toList());
            reList.addAll(oraReList);
        }
        return ResponseUtil.success(reList);
    }


}
