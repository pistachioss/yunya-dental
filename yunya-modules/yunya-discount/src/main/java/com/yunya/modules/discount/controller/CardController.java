package com.yunya.modules.discount.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.form.CardSoldForm;
import com.yunya.feign.discount.domain.form.ConfigSharerForm;
import com.yunya.feign.discount.domain.form.LockForm;
import com.yunya.feign.discount.domain.form.OtherCardActiveForm;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.form.UnLockForm;
import com.yunya.feign.discount.domain.model.GenerateAllocateModel;
import com.yunya.feign.discount.domain.query.CardActiveQuery;
import com.yunya.feign.discount.domain.query.CardSaleQuery;
import com.yunya.feign.discount.domain.query.CouponAllocateQuery;
import com.yunya.feign.discount.domain.query.CouponSaleQuery;
import com.yunya.feign.discount.domain.query.GenerateAllocateCardQuery;
import com.yunya.feign.discount.domain.query.GenerateAllocateDetailQuery;
import com.yunya.feign.discount.domain.query.PatientCardQuery;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.CardBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.yunya.framework.common.constant.BusinessConstants.*;

/**
 * 描述:
 *
 * @author xiangyang
 * @create 2020-08-19
 */
@Api(tags = {"卡券-生成、售卖、激活"})
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
    @PostMapping("/coupon/generate/allocation/export")
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
    @CurrentUser
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
    @PutMapping("/coupon/card/sale")
    @CurrentUser
    public ResponseResult soldCard(@Valid @RequestBody CardSoldForm form) throws ExecutionException, InterruptedException {
        return  cardBiz.soldCard(form);
    }

    @ApiOperation(value = "卡券二维码页面打开")
    @GetMapping("/coupon/card/QRCode/init")
    public ResponseResult<CardQrCodeVo> cardQrCodeCheck(@NotNull @RequestParam Integer cardId) {
        CardQrCodeVo codeVo = cardBiz.cardQrCodeCheck(cardId);
        return ResponseUtil.success(codeVo);
    }

    @ApiOperation(value = "卡券二维码页面打开(批量)")
    @GetMapping("/coupon/card/QRCode/batch/init")
    public List<CardQrCodeVo> batchCardQrCode(@NotEmpty @RequestBody List<Integer> cardIds) {
        return cardBiz.batchCardQrCode(cardIds);
    }

    @ApiOperation(value = "取消售出")
    @PutMapping("/coupon/card/cancel/{id}")
    @CurrentUser
    public ResponseResult cancelCardSold(@PathVariable(value = "id") Integer cardId) {
        return cardBiz.cancelCardSold(cardId);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-手动查询卡券详情（代金、折扣、兑换、套餐）")
    @PostMapping("/patient/product/card/manual/detail")
    public ResponseResult<CardActiveDetailVo> cardManualDetail(@Valid @RequestBody CardActiveQuery query) {
        return cardBiz.getCardDetailByManual(query);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-手动查询卡券详情（充值）")
    @PostMapping("/patient/product/recharge/manual/detail")
    public ResponseResult<CardActiveDetailVo> rechargeCardManualDetail(@Valid @RequestBody CardActiveQuery query) {
        return cardBiz.getRechargeDetailByManual(query);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-扫码枪卡券详情（代金、折扣、兑换、套餐）")
    @GetMapping("/patient/product/card/machine/detail")
    public ResponseResult<CardActiveDetailVo> cardMachineDetail(@NotBlank @RequestParam String cardQrCode) {
        return cardBiz.getCardDetailByMachine(cardQrCode);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-扫码枪卡券详情（充值）")
    @GetMapping("/patient/product/recharge/machine/detail")
    public ResponseResult<CardActiveDetailVo> rechargeMachineDetail(@NotBlank @RequestParam String cardQrCode) {
        return cardBiz.getRechargeDetailByMachine(cardQrCode);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-自有平台激活")
    @PutMapping("/{patientId}/product/card/owner/activation")
    @CurrentUser
    public ResponseResult ownActiveCard(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody OwnCardActiveForm form) {
        return cardBiz.ownActiveCard(patientId, form);
    }

    @ApiOperation(value = "患者档案-产品管理-激活-第三方平台激活")
    @PutMapping("/{patientId}/product/card/other/activation")
    @CurrentUser
    public ResponseResult otherActiveCard(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody OtherCardActiveForm form) {
        return cardBiz.otherActiveCard(patientId, form);
    }

    @ApiOperation(value = "患者档案-产品管理-配置共享人")
    @PutMapping("/{patientId}/product/card/{cardId}/configuration/sharer")
    @CurrentUser
    public ResponseResult configSharer(@PathVariable(value = "patientId") Integer patientId, @PathVariable(value = "cardId") Integer cardId,
                                       @RequestBody ConfigSharerForm form) {
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
    @PostMapping("/{patientId}/product/card/page")
    public ResponseResult<PageInfo<PatientCardBaseVo>> getPatientCardList(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody PatientCardQuery query) {
        PageInfo<PatientCardBaseVo> pageInfo = cardBiz.getPatientCardPage(patientId, query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "加锁")
    @PostMapping("/lock")
    @CurrentUser
    public ResponseResult<Boolean> lock(@RequestBody LockForm form) {
        return cardBiz.manualLock(form.getIds(), form.getLockPrefix());
    }

    @ApiOperation(value = "解锁（卡券售卖，优惠-选择卡券）")
    @PostMapping("/unlock")
    public ResponseResult unlock(@RequestBody UnLockForm form) {
        cardBiz.manualUnLock(form.getRequestId(), form.getLockPrefix());
        return ResponseUtil.success();
    }

    @ApiOperation(value = "患者档案-产品管理-自有产品-删除")
    @DeleteMapping("patient/{patientId}/card/{cardId}")
    @CurrentUser
    public ResponseResult getPatientCardList(@PathVariable(value = "patientId") Integer patientId
            , @PathVariable(value = "cardId") Integer cardId) {
        RestErrorBo restErrorBo = cardBiz.removeCard(patientId, cardId);
        return ResponseUtil.error(restErrorBo.getError(),restErrorBo.getMsg());
    }

    @ApiOperation(value = "患者档案-产品管理-第三方卡券激活-查询患者该产品已配置共享人")
    @GetMapping("/patient/{patientId}/coupon/{couponId}/sharer")
    public ResponseResult<List<PatientCardSharerVo>> getConfiguredSharer(@PathVariable(value = "patientId") Integer patientId,
                                                                         @PathVariable(value = "couponId") Integer couponId) {
        List<PatientCardSharerVo> configuredSharer = cardBiz.getConfiguredSharer(patientId, couponId);
        return ResponseUtil.success(configuredSharer);
    }
}
