package com.yunya.framework.common.controller;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.model.PageQueryParams;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-15 8:48
 */
@Slf4j
public class BaseController<Biz extends BaseBiz, Entity> {

  @Autowired protected HttpServletRequest request;

  @Autowired protected Biz baseBiz;

  /**
   * 新增
   *
   * @param entity
   * @return
   */
  @RequestMapping(value = "", method = RequestMethod.POST)
  @ResponseBody
  public ResponseResult add(@RequestBody Entity entity) {
    baseBiz.insertSelective(entity);
    return ResponseUtil.success();
  }

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseResult get(@PathVariable int id) {
    Object obj = baseBiz.selectById(id);
    return ResponseUtil.success(obj);
  }

  /**
   * 根据ID更新
   *
   * @param entity
   * @return
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseResult update(@RequestBody Entity entity) {
    baseBiz.updateSelectiveById(entity);
    return ResponseUtil.success();
  }

  /**
   * 根据ID删除
   *
   * @param id
   * @return
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseResult remove(@PathVariable int id) {
    baseBiz.deleteById(id);
    return ResponseUtil.success();
  }

  /**
   * 查询列表
   *
   * @return
   */
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  @ResponseBody
  public ResponseResult all() {
    return ResponseUtil.success(baseBiz.selectListAll());
  }

  /**
   * 查询列表并分页
   *
   * @param params
   * @return
   */
  @RequestMapping(value = "/page", method = RequestMethod.GET)
  @ResponseBody
  public ResponseResult list(@RequestParam Map<String, Object> params) {
    // 查询列表数据
    PageQueryParams query = new PageQueryParams(params);
    return ResponseUtil.success(baseBiz.selectByQuery(query));
  }
}
