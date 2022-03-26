package com.yunya.modules.emr.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.model.MedicalPictureRecordModel;
import com.yunya.feign.emr.domain.query.MedicalPictureRecordExistsQuery;
import com.yunya.feign.emr.domain.query.MedicalPictureRecordQuery;
import com.yunya.feign.emr.domain.vo.MedicalPictureRecordVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.MedicalPictureRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 简介：病历照片记录控制器
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/3/24 10:02
 * @since: 1.0.0
 */
@Api(tags = {"病历照片记录控制器"})
@RestController
@RequestMapping("/medicalPicture")
public class MedicalPictureRecordController {

    @Autowired
    private MedicalPictureRecordBiz medicalPictureRecordBiz;

    @ApiOperation(value = "条件查询病历照片记录及照片")
    @PostMapping("/list")
    public ResponseResult<PageInfo<MedicalPictureRecordVO>> findList(@Valid @RequestBody MedicalPictureRecordQuery query) {
        PageInfo<MedicalPictureRecordVO> pageInfo = medicalPictureRecordBiz.findList(query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation(value = "查询病历照片记录是否存在")
    @GetMapping("/isExists")
    public ResponseResult<Boolean> isExistsMedicalPictureRecord(@Valid @RequestBody MedicalPictureRecordExistsQuery query) {
        Boolean isExists = medicalPictureRecordBiz.isExistsMedicalPictureRecord(query);
        return ResponseUtil.success(isExists);
    }

    @ApiOperation(value = "保存病历照片记录")
    @PostMapping("/save")
    @CurrentUser
    public ResponseResult<Integer> save(@Valid @RequestBody MedicalPictureRecordModel model) {
        return ResponseUtil.success(medicalPictureRecordBiz.save(model));
    }

    @ApiOperation(value = "根据id删除病历照片记录")
    @PostMapping("/delete/{id}")
    @CurrentUser
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        medicalPictureRecordBiz.tombstoneById(id);
        return ResponseUtil.success();
    }
}
