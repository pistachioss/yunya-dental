package com.yunya.modules.emr.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.query.MedicalCheckRecordQuery;
import com.yunya.feign.emr.domain.vo.MedicalCheckRecordVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.MedicalCheckRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 普通电子病历-检查记录控制器
 *
 * @author chenlin
 * @description: 普通电子病历-检查记录控制器
 * @date 2022/1/07
 */
@Api(tags = {"普通电子病历-检查记录控制器"})
@RestController
@RequestMapping("medicalCheckRecord")
public class MedicalCheckRecordController {

    @Autowired
    private MedicalCheckRecordBiz medicalCheckRecordBiz;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    @CurrentUser
    public ResponseResult<PageInfo<MedicalCheckRecordVO>> findList(@Valid @RequestBody MedicalCheckRecordQuery query) {
        PageInfo<MedicalCheckRecordVO> page = medicalCheckRecordBiz.findMedicalCheckRecordList(query);
        return ResponseUtil.success(page);
    }
}
