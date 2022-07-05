package com.yunya365.wechat.controller;

import com.yunya.feign.wechat.domain.form.WxAutoReplyForm;
import com.yunya.feign.wechat.domain.form.WxSuCaiForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.wechat.service.impl.KfReply;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = {"微信关键字自动回复消息"})
@RestController
@Slf4j
@CrossOrigin
public class WxAutoReplyController {

  @Resource
  KfReply kfReply;

  @ApiOperation(value = "获取微信关键字列表")
  @GetMapping(value = "/wxAutoReply")
  @CurrentUser
  public ResponseResult list() {
    return ResponseUtil.success(kfReply.list());
  }

  @ApiOperation(value = "获取微信公众号素材列表")
  @GetMapping(value = "/wxMedia")
  @CurrentUser
  public ResponseResult listMedia(WxSuCaiForm pushModel) {
    return ResponseUtil.success(kfReply.listMedia(pushModel));
  }

  @ApiOperation(value = "新建微信关键字")
  @PostMapping(value = "/wxAutoReply")
  @CurrentUser
  public ResponseResult create(@Valid @RequestBody WxAutoReplyForm wxAutoReplyForm) {
    if(kfReply.create(wxAutoReplyForm) > 0){
      return ResponseUtil.success();
    }
    else {
      return ResponseUtil.error("新增保存失败", null);
    }
  }

  @ApiOperation(value = "编辑微信关键字")
  @PutMapping(value = "/wxAutoReply/{id}")
  @CurrentUser
  public ResponseResult edit(@Valid @RequestBody WxAutoReplyForm wxAutoReplyForm, @PathVariable(value = "id") Integer id) {
    if(kfReply.edit(wxAutoReplyForm, id) > 0){
      return ResponseUtil.success();
    }
    else {
      return ResponseUtil.error("编辑保存失败", null);
    }
  }

  @ApiOperation(value = "删除微信关键字")
  @DeleteMapping(value = "/wxAutoReply/{id}")
  @CurrentUser
  public ResponseResult delete(@PathVariable(value = "id") Integer id) {
    if(kfReply.delete(id) > 0){
      return ResponseUtil.success();
    }
    else {
      return ResponseUtil.error("删除失败，请刷新后重试", null);
    }
  }
}
