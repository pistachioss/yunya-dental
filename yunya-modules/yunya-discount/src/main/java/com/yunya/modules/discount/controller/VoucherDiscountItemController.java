package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.modules.discount.biz.VoucherDiscountItemBiz;
import com.yunya.modules.discount.form.VoucherDiscountItemForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 设置适用项目
 * @author 杨柳絮
 * @className VoucherDiscountItemController
 * @description
 * @date 2020/8/20 15:03
 */
@Api(tags = "设置适用项目")
@RestController
@RequestMapping("/voucherDiscountItem")
public class VoucherDiscountItemController {

    @Autowired private VoucherDiscountItemBiz voucherDiscountItemBiz;

    /**
     * 新增代金券折扣券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/saveVouAndDis")
    @ApiOperation("新增和修改代金券折扣券适用项目(每次修改都要传回所有的适用项目进行重新新增)")
    @CurrentUser
    public ResponseResult saveVouAndDis(@RequestBody @Valid List<VoucherDiscountItemForm> voucherDiscountItems) {
        Date date = new Date();
        VoucherDiscountItem voucherDiscountItem = new VoucherDiscountItem();
        voucherDiscountItem.setCouponId(voucherDiscountItems.get(0).getCouponId());
        voucherDiscountItemBiz.delete(voucherDiscountItem);//清除之前的适用项目
        voucherDiscountItems.forEach(t -> {
          t.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
          t.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
          t.setUpdTime(date);
          t.setCrtTime(date);
        });
        return ResponseUtil.success(voucherDiscountItemBiz.saveVouAndDis(voucherDiscountItems));
    }
    /**
     * 查询代金券折扣券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("查询代金券折扣券适用项目")
    @CurrentUser
    public ResponseResult findList(@RequestBody @Valid VoucherDiscountItem voucherDiscountItem){
        return ResponseUtil.success(voucherDiscountItemBiz.selectList(voucherDiscountItem));
    }

}
