package com.yunya.modules.system.controller;

import com.yunya.feign.system.form.AppVersionCheckForm;
import com.yunya.feign.system.form.AppVersionAddForm;
import com.yunya.feign.system.form.AppVersionEditForm;
import com.yunya.feign.system.vo.AppVersionVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.system.AppVersion;
import com.yunya.modules.system.biz.AppVersionBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

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
    public ResponseResult<AppVersionAddForm> add(@RequestBody AppVersionAddForm form) {
        return this.appVersionBiz.addAppVersion(form);
    }

    @ApiOperation("根据OSName查询版本信息")
    @GetMapping("/find/all/{osName}")
    @ApiImplicitParam(name = "osName",value = "系统名称",dataTypeClass = String.class,allowableValues = "IOS,Android",required = true)
    public ResponseResult<List<AppVersion>> findVersionList(@PathVariable("osName")
                                                            @NotNull(message = "系统名称不能为空") String osName) {
        return this.appVersionBiz.findVersionList(osName);
    }

    @ApiOperation("编辑APP版本信息")
    @PutMapping("/edit")
    public ResponseResult editAppVersion(@RequestBody AppVersionEditForm form) {
        return this.appVersionBiz.editAppVersion(form);
    }

    @ApiOperation("检测程序是否更新")
    @PostMapping("update/check")
    public ResponseResult<AppVersionVO> checkUpdate(@RequestBody AppVersionCheckForm form) {
        return this.appVersionBiz.checkUpdate(form);
    }
}
