package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.WxFansDetailVO;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.feign.patient_central.domain.vo.web.WxWechatFansVo;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.WxFans;
import com.yunya.modules.patient_central.biz.WxFansBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.OPERATION_NOT_ALLOW;

/**
 * 简介:公司微信公众号粉丝控制层
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(value = "WxFansController",description = "公司微信公众号粉丝控制层")
@RestController
@RequestMapping("wxFans")
public class WxFansController {

    @Autowired private WxFansBiz wxFansBiz;

    /**
     * 客服中心-用户管理列表
     *
     * @param
     * @return ResponseResult<PageInfo<WxFansVo>>
     */
    @ApiOperation("客服中心-用户管理列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<WxFansVo>> findList(
            @RequestBody @Validated WxFansQueryForm wxFansQueryForm) {
        return ResponseUtil.success(
               wxFansBiz.findList(wxFansQueryForm));
    }

    /**
     * 客服中心-用户管理列表-查看详情
     *
     * @param
     * @return ResponseResult<PageInfo<WxFansVo>>
     */
    @ApiOperation("客服中心-用户管理列表-查看详情")
    @PostMapping("/detail")
    public ResponseResult<List<WxFansDetailVO>> findDetail(
            @RequestBody @Validated WxFansDetailForm wxFansDetailForm) {
        return ResponseUtil.success(
                wxFansBiz.findDetail(wxFansDetailForm));
    }

    /**
     * 客服中心-新增粉丝信息以及绑定关系
     *
     * @param
     * @return
     */
    @ApiOperation("客服中心-新增粉丝信息以及绑定关系(后端调用)")
    @PostMapping("/save")
    public ResponseResult<Integer> save(
            @RequestBody @Validated WxFansSaveForm wxFansSaveForm) {
        return ResponseUtil.success(wxFansBiz.save(wxFansSaveForm));
    }

    /**
     * 客服中心-用户管理列表-查看详情-修改备注
     *
     * @param
     * @return
     */
    @ApiOperation("客服中心-用户管理列表-查看详情-修改备注")
    @PostMapping("/update")
    public ResponseResult<Integer> update(@RequestBody @Validated WxFansUpdateForm wxFansUpdateForm){
        return ResponseUtil.success(wxFansBiz.update(wxFansUpdateForm));
    }

    /**
     * 根据openId或患者ID获取微信公众号的粉丝（判断用户是否关注了微信公众号）
     *
     * @param query
     * @return
     */
    @ApiOperation("根据openId或患者ID获取微信公众号的粉丝（判断用户是否关注了微信公众号）")
    @PostMapping("/getOwnWxFans")
    public ResponseResult<WxFans> getOwnWxFans(@RequestBody WxUserQuery query) {
        WxFans ownWxFans = wxFansBiz.getOwnWxFans(query);
        if (ownWxFans == null) {
            throw new ClientServiceException("非关注公众号用户请先关注艾维口腔公众号！",OPERATION_NOT_ALLOW);
        }
        return ResponseUtil.success(ownWxFans);
    }

    /**
     * 微信用户-用户管理列表
     *
     * @param
     * @return ResponseResult<PageInfo<WxFansVo>>
     */
    @ApiOperation("微信用户-用户管理列表")
    @PostMapping("/wechat/list")
    public ResponseResult<PageInfo<WxWechatFansVo>> findWechatList(
            @RequestBody @Validated WxFansWechatQueryForm wxFansWechatQueryForm) {
        return ResponseUtil.success(
                wxFansBiz.findWechatList(wxFansWechatQueryForm));
    }
    /**
     * 微信用户-用户管理列表
     *
     * @param
     * @return ResponseResult<PageInfo<WxFansVo>>
     */
    @ApiOperation("微信用户-启用禁用")
    @PutMapping("/wechat/update")
    public ResponseResult findWechatList(
            @RequestBody @Validated WxFansWechatUpdateForm wxFansWechatUpdateForm) {
        WxFans wxFans = new WxFans();
        BeanUtil.copy(wxFansWechatUpdateForm,wxFans);
        return ResponseUtil.success(
                wxFansBiz.updateSelectiveById(wxFans));
    }
}
