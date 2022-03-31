package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.treatment_other.domain.form.XUploadFileForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.XUploadFileBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description: 文件信息控制层
 * @Date: 2021/10/19 17:31
 * @since: 1.0.0
 */
@Api(tags = "文件信息控制层")
@RestController
@RequestMapping("/xUploadFile")
public class XUploadFileController {

    @Autowired
    private XUploadFileBiz xUploadFileBiz;

    /**
     * 根据文件ID编辑上传日期
     *
     * @param form
     * @return
     */
    @ApiOperation("根据文件ID编辑上传日期")
    @PutMapping("/edit/uploadTime")
    @CurrentUser
    public ResponseResult editUploadTime(@Validated @RequestBody XUploadFileForm form) {
        xUploadFileBiz.editUploadTime(form);
        return ResponseUtil.success();
    }

    /**
     * 根据文件ID删除文件信息
     *
     * @param fileId
     * @return
     */
    @ApiOperation("根据文件ID删除文件信息")
    @DeleteMapping("/del/{fileId}")
    @CurrentUser
    @ApiImplicitParams({@ApiImplicitParam(name = "fileId",value = "文件ID")})
    public ResponseResult delById(@PathVariable("fileId") Integer fileId) {
        xUploadFileBiz.del(fileId, true);
        return ResponseUtil.success();
    }

    /**
     * 根据文件ID删除文件信息
     *
     * @param fileId
     * @return
     */
    @ApiOperation("根据文件ID删除文件信息")
    @PutMapping("/del/{fileId}")
    @CurrentUser
    @ApiImplicitParams({@ApiImplicitParam(name = "fileId",value = "文件ID")})
    public ResponseResult del(@PathVariable("fileId") Integer fileId) {
        xUploadFileBiz.del(fileId, false);
        return ResponseUtil.success();
    }
}
