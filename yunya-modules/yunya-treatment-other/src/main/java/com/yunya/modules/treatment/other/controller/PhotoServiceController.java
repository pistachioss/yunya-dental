package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.treatment_other.domain.form.PhotoServiceForm;
import com.yunya.feign.treatment_other.domain.model.PhotoServiceModel;
import com.yunya.feign.treatment_other.domain.query.PhotoServiceQuery;
import com.yunya.feign.treatment_other.domain.vo.PhotoServiceVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.treatment_other.PhotoService;
import com.yunya.modules.treatment.other.biz.PhotoServiceBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
@RequestMapping("photo")
public class PhotoServiceController {


    @Autowired
    private PhotoServiceBiz photoServiceBiz;

    /**
     * 查询影像服务列表(分模块列表)
     *
     * @param
     * @return
     */
    @ApiOperation("查询图片影像列表")
    @PostMapping("/findCycleList")
    public ResponseResult findPhotoServiceData(PhotoServiceQuery query){
        List<PhotoServiceVo> data = photoServiceBiz.findPhotoServiceData(query);
        return ResponseUtil.success(data);
    }
    /**
     * 添加图片uri路径（除了根尖片）
     *
     * @param
     * @return
     */
    @ApiOperation("添加图片影像")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult add(@Valid @RequestBody PhotoServiceModel model){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        PhotoService photoService = new PhotoService();
        photoService.setCrtId((crtId));
        BeanUtils.copyProperties(model,photoService);
        photoServiceBiz.add(photoService);
        return ResponseUtil.success();
    }
    /**
     * 修改图片uri路径（除了根尖片）
     *
     * @param
     * @return
     */
    @ApiOperation("修改图片影像")
    @PutMapping("/upd")
    @CurrentUser
    public ResponseResult upd(@Valid @RequestBody PhotoServiceForm form){
        Integer updId = Integer.valueOf(BaseContextHandler.getUserID());
        PhotoService photoService = new PhotoService();
        photoService.setUpdId((updId));
        BeanUtils.copyProperties(form,photoService);
        photoServiceBiz.upd(photoService);
        return ResponseUtil.success();
    }

    /**
     * 删除牙周期记录
     *
     * @param
     * @return
     */
    @ApiOperation("删除图片影像记录")
    @DeleteMapping("/del")
    public ResponseResult upd(Integer id){
        photoServiceBiz.del(id);
        return ResponseUtil.success();
    }

}
