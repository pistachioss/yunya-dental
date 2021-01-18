package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.discount.Card;
import com.yunya.modules.discount.biz.CardBiz;
import com.yunya.modules.discount.biz.SalesChannelBiz;
import com.yunya.models.discount.SalesChannel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.yunya.framework.common.constant.OperationCodeConstants.DELETE_NOT_ALLOW;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-09 12:32
 */
@Api(tags = "销售渠道")
@RestController
@RequestMapping("/sales_channel")
@CurrentUser
public class SalesChannelController {
    @Autowired
    private SalesChannelBiz salesChannelBiz;
    @Autowired
    private CardBiz cardBiz;
    /**
     * 新增销售渠道
     *
     * @param salesChannel
     * @return
     */
    @PostMapping
    @ApiOperation("新增销售渠道")
    public ResponseResult save(@RequestBody @Valid SalesChannel salesChannel) {
        salesChannelBiz.saveSalesChannel(salesChannel);
        return ResponseUtil.success();
    }

    /**
     * 修改销售渠道
     *
     * @param salesChannel
     * @return
     */
    @PutMapping
    @ApiOperation("修改销售渠道")
    public ResponseResult update(@RequestBody SalesChannel salesChannel) {
        salesChannelBiz.updateSalesChannel(salesChannel);
        return ResponseUtil.success();
    }

    /**
     * 获取销售渠道
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取销售渠道")
    public ResponseResult find(@PathVariable(name = "id") Integer id) {
        return ResponseUtil.success(salesChannelBiz.selectById(id));
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("列表")
    public ResponseResult list() {
        return ResponseUtil.success(salesChannelBiz.selectListAll());
    }

    /**
     * 删除
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除")
    public ResponseResult delete(@PathVariable(name = "id") Integer id) {
        Card card = new Card();
        card.setSaleChannelId(id);
        Long num = cardBiz.selectCount(card);
        if(num>0){
            throw new ClientServiceException("销售渠道已经被使用，不允许删除！", DELETE_NOT_ALLOW);
        }else{
            salesChannelBiz.deleteById(id);
        }

        return ResponseUtil.success();
    }
}
