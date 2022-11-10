package com.yunya.modules.patient_central.rpc;

import com.yunya.feign.patient_central.domain.model.WorkWxUserModel;
import com.yunya.feign.patient_central.domain.vo.web.WorkWxPatientBindVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.WxFansBiz;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/11/10 17:01
 **/
@RestController
@Slf4j
public class WechatRest {

    @Resource
    private WxFansBiz wxFansBiz;

    @ApiOperation("企微、小程序、公众号-保存客户信息")
    @PostMapping(value = "/white/wechat/user")
    public ResponseResult<Boolean> saveWechat(@Valid @RequestBody WorkWxUserModel model) {
        wxFansBiz.saveWorkWx(model);
        return ResponseUtil.success();
    }

    @ApiOperation("企微、小程序、公众号-客户患者关系列表")
    @GetMapping(value = "/white/wechat/user/relate")
    public ResponseResult<List<WorkWxPatientBindVO>> wechatRelateList(@NotBlank @RequestParam String unionId) {
        return ResponseUtil.success( wxFansBiz.wechatRelateList(unionId));
    }
}
