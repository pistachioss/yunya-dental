package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.WxFansDetailForm;
import com.yunya.feign.patient_central.domain.query.WxFansQueryForm;
import com.yunya.feign.patient_central.domain.query.WxFansSaveForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansDetailVO;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.WxFans;
import com.yunya.modules.patient_central.biz.WxFansBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @ApiOperation("客服中心-新增粉丝信息以及绑定关系")
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
    private ResponseResult<Integer> update(@RequestBody @Validated WxFans wxFans){
        return ResponseUtil.success(wxFansBiz.updateSelectiveById(wxFans));
    }

}
