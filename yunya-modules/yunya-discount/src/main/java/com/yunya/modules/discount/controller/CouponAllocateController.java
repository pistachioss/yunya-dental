package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.modules.discount.biz.CardBiz;
import com.yunya.modules.discount.biz.CouponAllocateBiz;
import com.yunya.modules.discount.form.CouponAllocateForm;
import com.yunya.modules.discount.vo.CouponAllocateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className CouponAllocateController
 * @description
 * @date 2020/8/21 10:00
 */
@Api(tags = "配给")
@RestController
@RequestMapping("/coupon_allocate")
public class CouponAllocateController {

    @Autowired
    private CouponAllocateBiz couponAllocateBiz;
    @Autowired
    private CardBiz cardBiz;

    /**
     * 新增配给信息
     *
     * @param
     * @return
     */
    @PostMapping
    @ApiOperation("新增配给信息")
    @CurrentUser
    public ResponseResult saveAllocate(@RequestBody @Valid List<CouponAllocateForm> list) {
        Date date = new Date();
        list.forEach(t -> {
            t.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            t.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
            t.setUpdTime(date);
            t.setCrtTime(date);
            t.setAllocateUserId(Integer.parseInt(BaseContextHandler.getUserID()));
            t.setAllocateDate(date);
        });
        return ResponseUtil.success(couponAllocateBiz.insertAll(list));
    }

    /**
     * 修改配给信息
     *
     * @param
     * @return
     */
    @PutMapping
    @ApiOperation("修改配给信息")
    @CurrentUser
    public ResponseResult UpdateAllocate(@RequestBody @Valid CouponAllocateForm couponAllocateForm) {
        Date date = new Date();
        Card card = new Card();
        card.setCouponAllocateId(couponAllocateForm.getId());
        if (cardBiz.selectList(card).size() > 0) {
            throw new ClientServiceException("本次配给计划已在财务部生成卡券，不允许修改配给数量！", OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
        couponAllocateForm.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
        couponAllocateForm.setUpdTime(date);
        CouponAllocate couponAllocate = new CouponAllocate();
        BeanUtils.copyProperties(couponAllocateForm, couponAllocate);
        couponAllocateBiz.updateSelectiveById(couponAllocate);
        return ResponseUtil.success();
    }

    /**
     * 根据卡券Id查询配给信息列表
     *
     * @param
     * @return
     */
    @GetMapping("/findAllocate/{id}")
    @ApiOperation("根据卡券Id查询配给信息列表")
    @CurrentUser
    public ResponseResult findAllocate(@PathVariable("id") Integer id) {
        return ResponseUtil.success(couponAllocateBiz.findVOList(id));
    }

    /**
     * 根据配给时间查询配给详情
     *
     * @param
     * @return
     */
    @PostMapping("/findAllocateDetail")
    @ApiOperation("根据卡券Id查询配给信息列表")
    @CurrentUser
    public ResponseResult findAllocateDetail(@RequestBody @Valid CouponAllocateVO couponAllocateVO){
        return ResponseUtil.success(couponAllocateBiz.findVODetailList(couponAllocateVO));
    }

}
