package com.yunya.modules.discount.controller;

import com.alibaba.excel.*;
import com.github.pagehelper.*;
import com.yunya.feign.discount.domain.form.*;
import com.yunya.feign.discount.domain.model.*;
import com.yunya.feign.discount.domain.query.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.framework.common.annation.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya.modules.discount.biz.*;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;
import javax.servlet.http.*;
import javax.validation.*;
import javax.validation.constraints.*;
import java.io.*;
import java.net.*;
import java.util.*;

import static com.yunya.framework.common.constant.BusinessConstants.*;

/**
 * 描述:
 *
 * @author xiangyang
 * @create 2020-08-19
 */
@Api(tags = {"卡券"})
@RestController
public class CardController {

    @Resource
    private CardBiz cardBiz;

    @ApiOperation(value = "产品生成分配分页查询")
    @PostMapping("/coupon/generate/allocation/page")
    public ResponseResult<PageInfo<GenerateAllocatePageVo>> getCouponAllocatePage(@Valid @RequestBody CouponAllocateQuery query) {
        PageInfo<GenerateAllocatePageVo> pageInfo = cardBiz.getCouponAllocatePage(query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "产品生成分配--生成分配明细查询")
    @PostMapping("/coupon/generate/allocation/list")
    public ResponseResult<List<GenerateAllocateDetailVo>> getGenerateAllocateList(@Valid @RequestBody GenerateAllocateDetailQuery query) {
        List<GenerateAllocateDetailVo> list = cardBiz.getGenerateAllocateList(query);
        return ResponseUtil.success(list);
    }

    @ApiOperation(value = "产品生成分配--生成分配")
    @PostMapping("/coupon/generate/allocation")
    @CurrentUser
    public ResponseResult generateCard(@Valid @RequestBody GenerateAllocateModel allocateModel) throws Exception {
        return cardBiz.generateAllocate(allocateModel);
    }

    @ApiOperation(value = "产品生成分配--查看配给")
    @PostMapping("/coupon/card/generate/detail")
    public ResponseResult<List<ViewAllocateVo>> getAllocateDetail(@Valid @RequestBody GenerateAllocateCardQuery query) {
        List<ViewAllocateVo> list = cardBiz.getAllocateDetail(query);
        return ResponseUtil.success(list);
    }

    @ApiOperation(value = "产品生成分配--查看配给--导出")
    @PostMapping("/coupon/generate/allocation/detail/export")
    public void exportAllocateDetail(HttpServletResponse response, @Valid @RequestBody GenerateAllocateCardQuery query) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(EXPORT_CARD_FILENAME, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExportCardAllocateVo.class)
                .sheet("sheet").doWrite(cardBiz.getExportCardAllocateList(query));
    }

    @ApiOperation(value = "产品售卖分页查询")
    @PostMapping("/coupon/sale/page")
    public ResponseResult<PageInfo<CouponSalePageVo>> getGenerateAllocateList(@Valid @RequestBody CouponSaleQuery query) {
        PageInfo<CouponSalePageVo> pageInfo = cardBiz.getCouponSalePage(query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "产品售卖--查看配给分页查询")
    @PostMapping("/coupon/sale/card/page")
    public ResponseResult<PageInfo<CardSalePageVo>> getCardSalePageVo(@Valid @RequestBody CardSaleQuery query) {
        PageInfo<CardSalePageVo> pageInfo = cardBiz.getCardSalePage(query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "产品售卖--查看配给-卡券售出")
    @PutMapping("/coupon/card/sale/{id}")
    @CurrentUser
    public ResponseResult soldCard(@PathVariable(value = "id") Integer cardId,@Valid @RequestBody CardSoldForm form) {
        return cardBiz.soldCard(cardId, form);
    }

    @ApiOperation(value = "卡券二维码页面打开")
    @GetMapping("/coupon/card/QRCode/init")
    public ResponseResult<CardQrCodeVo> cardQrCodeCheck(@NotBlank @RequestParam String cardQrData) {
        CardQrCodeVo codeVo = cardBiz.cardQrCodeCheck(cardQrData);
        return ResponseUtil.success(codeVo);
    }

    @ApiOperation(value = "取消售出")
    @PutMapping("/coupon/card/cancel/{id}")
    @CurrentUser
    public ResponseResult cancelCardSold(@PathVariable(value = "id") Integer cardId, @Valid @RequestBody CancelCardSoldForm form) {
        return cardBiz.cancelCardSold(cardId, form);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-手动查询卡券详情")
    @PostMapping("/patient/product/card/manual/detail")
    public ResponseResult<CardActiveDetailVo> cardManualDetail(@Valid @RequestBody CardActiveQuery query) {
        CardActiveDetailVo detail = cardBiz.getCardDetailByManual(query);
        return ResponseUtil.success(detail);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-扫码枪卡券详情")
    @GetMapping("/patient/product/card/machine/detail")
    public ResponseResult<CardActiveDetailVo> cardMachineDetail(@NotBlank @RequestParam String cardQrCode) {
        CardActiveDetailVo detail = cardBiz.getCardDetailByMachine(cardQrCode);
        return ResponseUtil.success(detail);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-自有平台激活")
    @PutMapping("/patient/{patientId}/product/card/owner/activation")
    @CurrentUser
    public ResponseResult ownActiveCard(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody OwnCardActiveForm form) {
        return cardBiz.ownActiveCard(patientId, form);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-第三方平台激活")
    @PutMapping("/patient/{patientId}/product/card/other/activation")
    @CurrentUser
    public ResponseResult otherActiveCard(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody OtherCardActiveForm form) {
        return cardBiz.otherActiveCard(patientId, form);
    }

    @ApiOperation(value = "患者档案-产品管理-配置共享人")
    @PutMapping("/patient/{patientId}/product/card/{cardId}/configuration/sharer")
    @CurrentUser
    public ResponseResult configSharer(@PathVariable(value = "patientId") Integer patientId, @PathVariable(value = "cardId") Integer cardId,
                                        @Valid @RequestBody ConfigSharerForm form) {
        return cardBiz.configSharer(patientId, cardId, form);
    }

    @ApiOperation(value = "患者档案-产品管理-查询已配置共享人")
    @GetMapping("/patient/product/card/{cardId}/configuration/sharer")
    @CurrentUser
    public ResponseResult<List<PatientCardSharerVo>> getConfiguredSharer(@PathVariable(value = "cardId") Integer cardId) {
        List<PatientCardSharerVo> configuredSharer = cardBiz.getConfiguredSharer(cardId);
        return ResponseUtil.success(configuredSharer);
    }

    @ApiOperation(value = "患者档案-产品管理-分页查询")
    @PostMapping("/patient/{patientId}/product/card/page")
    public ResponseResult<PageInfo<PatientCardBaseVo>> getPatientCardList(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody PatientCardQuery query) {
        PageInfo<PatientCardBaseVo> pageInfo = cardBiz.getPatientCardPage(patientId, query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "收费-选择优惠")
    @PostMapping("/order/choice/coupon")
    public ResponseResult<PatientOptionalBenefitVo> chooseCoupon(@Valid @RequestBody PatientBenefitQuery query) {
        PatientOptionalBenefitVo benefit = cardBiz.getPatientBenefit(query);
        return ResponseUtil.success(benefit);
    }
}
