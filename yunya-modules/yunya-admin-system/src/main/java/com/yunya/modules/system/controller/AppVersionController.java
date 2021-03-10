package com.yunya.modules.system.controller;

import com.yunya.feign.system.form.AppVersionCheckForm;
import com.yunya.feign.system.form.AppVersionForm;
import com.yunya.feign.system.vo.AppVersionVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.biz.AppVersionBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: yunya-dental
 * @description: APP端版本控制器
 * @author: LHB
 * @create: 2021-03-09 16:33
 **/
@Api(value = "APP端版本控制器",description = "新增版本信息、检测程序是否更新")
@RestController
@RequestMapping("/app/version/manager")
public class AppVersionController {
    @Autowired
    private AppVersionBiz appVersionBiz;

    @ApiOperation("新增版本信息")
    @PostMapping("add")
    public ResponseResult<AppVersionForm> add(@RequestBody AppVersionForm form) {
        return this.appVersionBiz.addAppVersion(form);
    }

    @ApiOperation("检测程序是否更新")
    @PostMapping("update/check")
    public ResponseResult<AppVersionVO> checkUpdate(@RequestBody AppVersionCheckForm form) {
        return this.appVersionBiz.checkUpdate(form);
    }
}
