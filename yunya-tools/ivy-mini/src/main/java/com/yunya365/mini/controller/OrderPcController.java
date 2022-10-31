package com.yunya365.mini.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.yunya.feign.ivy_mini.domain.form.*;
import com.yunya.feign.ivy_mini.domain.model.OrderRefundModel;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderItem;
import com.yunya365.mini.service.IOrderInfoService;
import com.yunya365.mini.service.IOrderItemService;
import com.yunya365.mini.service.impl.OrderAdminiServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static com.yunya.framework.common.enums.TrueFalseEnum.*;
import static java.util.stream.Collectors.*;

/**
 * @description:
 * @author: xy
 * @date 2022/4/28 15:52
 **/
@Api(tags = {"后台-订单api"})
@RestController
public class OrderPcController extends PcBaseController{
    @Resource
    private OrderAdminiServiceImpl orderAdminiService;

    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private IOrderItemService orderItemService;

    @PostMapping("/order/findlist")
    @ApiOperation("后台-订单-列表")
    @IgnoreUserToken
    public ResponseResult<PageInfo<OrderVO>> findList(@RequestBody @Valid OrderForm form) {
        return ResponseUtil.success(orderAdminiService.findList(form));
    }

    @GetMapping("/order/findcount")
    @ApiOperation("后台-订单-数量提醒")
    @IgnoreUserToken
    public ResponseResult<Integer> findCount() {
        return ResponseUtil.success(orderAdminiService.findCount());
    }

    @ApiOperation("后台-订单-发货")
    @PutMapping("/order/update")
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated OrderUpdateForm form) {
        return orderAdminiService.update(form);
    }

    @PostMapping("/order/findDetail")
    @ApiOperation("后台-订单-详情")
    public ResponseResult<OrderWechatDetailVO> findDetail(@RequestBody @Valid OrderDetailForm form) {
        return ResponseUtil.success(orderAdminiService.findDetail(form));
    }

    @PostMapping("/order/confirm/{orderId}")
    @ApiOperation("后台-完成取货")
    public ResponseResult<PayOrderVO> confirmDelivery(@PathVariable Integer orderId) {
        return ResponseUtil.success(orderInfoService.confirmDelivery(orderId));
    }

    @ApiOperation("后台-订单-退款")
    @PutMapping("/order/refund")
    @CurrentUser
    public ResponseResult refund(@RequestBody @Validated OrderRefundModel form) {
         orderInfoService.refund(form);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "后台-小程序订单-导出")
    @PostMapping("/order/export")
    public void exportOrder(HttpServletResponse response, @RequestBody @Valid PcOrderExportForm form) throws IOException {
        List<Integer> orderIds = form.getOrderIds();
        List<PcOrderExportVO> voList = null;
        if (CollectionUtils.isNotEmpty(orderIds)) {
            List<OrderInfo> orderInfos = orderInfoService.listByOrderIds(orderIds);
            Map<Integer, String> stringMap = itemMap(orderIds);
            voList = orderInfos.stream().map(t -> {
                PcOrderExportVO pcOrderExportVO = new PcOrderExportVO();
                pcOrderExportVO.setOrderSn(t.getOrderSn());
                if (Objects.equals(FALSE.getCode().byteValue(), t.getProductType())) {
                    pcOrderExportVO.setReceiverAddress(Joiner.on(",").join(t.getReceiverName(), t.getReceiverPhone(), t.getReceiverDetailAddress()));
                }
                pcOrderExportVO.setItemStr(stringMap.get(t.getId()));
                return pcOrderExportVO;
            }).collect(toList());
        } else {
            OrderForm orderForm = form.getForm();
            orderForm.setWhetherPage(false);
            List<OrderVO> list = orderAdminiService.findList(orderForm).getList();
            if(CollectionUtils.isNotEmpty(list)){
                Map<Integer, String> stringMap = itemMap(list.stream().map(OrderVO::getId).collect(toSet()));
                voList = list.stream().map(t -> {
                    PcOrderExportVO pcOrderExportVO = new PcOrderExportVO();
                    pcOrderExportVO.setOrderSn(t.getOrderSn());
                    if (Objects.equals(FALSE.getCode().byteValue(), t.getProductType())) {
                        pcOrderExportVO.setReceiverAddress(Joiner.on(",").join(t.getReceiverName(), t.getReceiverPhone(), t.getAddress()));
                    }
                    pcOrderExportVO.setItemStr(stringMap.get(t.getId()));
                    return pcOrderExportVO;
                }).collect(toList());
                orderInfoService.buildResponse(response, "小程序订单导出模板");
            }
        }
        orderInfoService.buildResponse(response, "小程序订单导出模板");
        EasyExcel.write(response.getOutputStream(), PcOrderExportVO.class)
                .sheet("sheet").doWrite(voList);
    }
    private Map<Integer, String> itemMap(Collection<Integer> orderIds) {
        return  orderItemService.listByOrderIds(orderIds)
                .stream().filter(t -> Objects.nonNull(t.getProductName()))
                .collect(groupingBy(OrderItem::getOrderId, mapping(OrderItem::getProductName, joining(","))));
    }
}
