package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.XRayFilmForm;
import com.yunya.feign.treatment_other.domain.model.XRayFilmModel;
import com.yunya.feign.treatment_other.domain.query.ToothRootQuery;
import com.yunya.feign.treatment_other.domain.query.XRayFilmQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothRootCountVo;
import com.yunya.feign.treatment_other.domain.vo.ToothRootVo;
import com.yunya.feign.treatment_other.domain.vo.XRayFilmVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.XRayFilmBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.List;

/**
 * 简介: 就诊牙周期模块管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "图片影像模块(增删改查)")
@RestController
@RequestMapping("/photo")
public class XRayFilmController {

    @Autowired
    private XRayFilmBiz XRayFilmBiz;

    @ApiOperation("查询图片影像列表")
    @PostMapping("/findCycleList")
    public ResponseResult<PageInfo<XRayFilmVo>> findList(@RequestBody @Validated XRayFilmQuery query){
        PageInfo<XRayFilmVo> data = this.XRayFilmBiz.findList(query);
        return ResponseUtil.success(data);
    }

    @ApiOperation("添加图片影像批量上传")
    @PostMapping("/add/batch/{patientId}")
    @CurrentUser
    public ResponseResult addBatch(@PathVariable(value = "patientId") Integer patientId,
                                   @RequestBody XRayFilmModel models){
        this.XRayFilmBiz.addBatch(patientId,models.getList());
        return ResponseUtil.success();
    }
    /**
     * 修改图片uri路径（除了根尖片）
     *
     * @param
     * @return
     */
    @ApiOperation("修改图片影像")
    @PutMapping("/upd/{id}")
    @CurrentUser
    public ResponseResult upd(@PathVariable("id") Integer id,@RequestBody @Validated XRayFilmForm form) throws ParseException {
        this.XRayFilmBiz.upd(id, form);
        return ResponseUtil.success();
    }

    @ApiOperation("删除图片影像记录")
    @DeleteMapping("/del/{id}")
    @CurrentUser
    @ApiImplicitParams({@ApiImplicitParam(name = "id",value = "照片记录ID")})
    public ResponseResult del(@PathVariable("id") Integer id) {
        this.XRayFilmBiz.del(id);
        return ResponseUtil.success();
    }

    @ApiOperation("牙根尖图列表")
    @PostMapping("/tooth/root/img/{patientId}")
    @CurrentUser
    @ApiImplicitParams({
            @ApiImplicitParam(name = "patientId",value = "患者ID",required = true)
    })
    public ResponseResult<PageInfo<ToothRootVo>> findToothRootPhotos(
                                                           @PathVariable("patientId") Integer patientId,
                                                           @RequestBody ToothRootQuery query) {
        PageInfo<ToothRootVo> toothRootPhotos = this.XRayFilmBiz.findToothRootPhotos(patientId, query);
        return ResponseUtil.success(toothRootPhotos);
    }

    @ApiOperation("牙位根尖片数量(APP)用")
    @GetMapping("/count/{patientId}")
    @CurrentUser
    public ResponseResult<List<ToothRootCountVo>> toothRootCount(@PathVariable("patientId") Integer patientId){
        List<ToothRootCountVo> toothRootCountVoList = this.XRayFilmBiz.toothRootCount(patientId);
        return ResponseUtil.success(toothRootCountVoList);
    }


}
