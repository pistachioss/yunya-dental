package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.ResetVisitingRemindForm;
import com.yunya.feign.treatment_other.domain.form.VisitingRemindForm;
import com.yunya.feign.treatment_other.domain.model.VisitingRemindModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRemindQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingRemindContentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRemindDetailVO;
import com.yunya.feign.treatment_other.domain.vo.VisitingRemindVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.VisitingRemindBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 随访提醒
 * @author: LHB
 * @create: 2020-08-24 19:51
 **/
@Api(tags = "随访提醒管理")
@RestController
@RequestMapping("visiting/remind")
public class VisitingRemindController {
    /**
     * 注入服务
     */
    @Autowired
    private VisitingRemindBiz visitingRemindBiz;

    /**
     * 新增随访提醒
     *
     * @param model 表单
     * @return ResponseResult
     */
    @ApiOperation("新增随访提醒")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult insertVisitingRemind(@RequestBody @Validated VisitingRemindModel model) {
        return visitingRemindBiz.insertVisitingRemind(model);
    }

    /**
     * 批量到执行提醒
     *
     * @param model 表单
     * @return ResponseResult
     */
    @ApiOperation("批量到执行提醒用")
    @PostMapping("/addImplement")
    @CurrentUser
    public ResponseResult addImplement(@RequestBody @Validated List<VisitingRemindModel> model) {
        return visitingRemindBiz.addImplement(model);
    }

    /**
     * 根据id删除随访提醒
     *
     * @param id 提醒记录id
     * @return ResponseResult
     */
    @ApiOperation("根据id删除随访提醒")
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteVisitingRemind(@PathVariable("id") Integer id) {
        return visitingRemindBiz.deleteVisitingRemindById(id);
    }

    /**
     * 修改随访提醒
     *
     * @param form 表单
     * @return ResponseResult
     */
    @ApiOperation("修改随访提醒")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult updateVisitingRemind(@RequestBody @Validated VisitingRemindForm form) {
        return visitingRemindBiz.updateVisitingRemind(form);
    }

    /**
     * 根据id查询随访提醒
     *
     * @param id 随访提醒id
     * @return ResponseResult
     */
    @ApiOperation("根据id查询随访提醒")
    @GetMapping("/find/{id}")
    public ResponseResult<VisitingRemindContentVo> findVisitingRemindById(@PathVariable("id") Integer id) {
        return visitingRemindBiz.findVisitingRemindById(id);
    }

    /**
     * 根据id查询随访提醒详情
     *
     * @param id 随访提醒id
     * @return ResponseResult
     */
    @ApiOperation("根据id查询随访提醒详情")
    @GetMapping("/one/{id}")
    public ResponseResult<VisitingRemindDetailVO> findRemindOneById(@PathVariable("id") Integer id) {
        VisitingRemindDetailVO result = visitingRemindBiz.findRemindOneById(id);
        return ResponseUtil.success(result);
    }

    /**
     * 根据条件查询随访提醒
     *
     * @param query 查询条件
     * @return ResponseResult
     */
    @ApiOperation("根据条件查询随访提醒")
    @PostMapping("/find")
    public ResponseResult<PageInfo<VisitingRemindVo>> findVisitingRemindByCondition(@RequestBody @Validated VisitingRemindQuery query) {
        return visitingRemindBiz.findVisitingRemindByCondition(query);
    }

    /**
     * 根据条件导出执行提醒列表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("根据条件导出执行提醒列表")
    @PostMapping("/execute/export")
    public void executeRemindExport(HttpServletResponse response,
                                    @RequestBody @Validated VisitingRemindQuery query) throws IOException {
        visitingRemindBiz.executeRemindExport(response, query);
    }

    /**
     * 完成提醒
     *
     * @param id 提醒id
     * @return ResponseResult
     */
    @ApiOperation("完成提醒")
    @CurrentUser
    @PutMapping("/finish/{id}")
    public ResponseResult finishVisitingRemind(@PathVariable("id") Integer id) {
        return visitingRemindBiz.finishVisitingRemind(id);
    }

    /**
     * 批量修改提醒
     *
     * @param form 数据表单
     * @return 返回结果
     */
    @ApiOperation("批量修改提醒")
    @CurrentUser
    @PutMapping("/reset")
    public ResponseResult<?> resetVisitingRemindBatch(@RequestBody @Validated ResetVisitingRemindForm form) {
        Integer result = visitingRemindBiz.resetVisitingRemindBatch(form);
        if (result > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.success("批量修改失败", null);
    }


    /**
     * rabbitmq数据补偿接口
     * @return
     */
    @ApiOperation("rabbitmq数据补偿接口")
    @PutMapping("/compensate/data")
    public ResponseResult<?> reportmodify() {
        Integer result = visitingRemindBiz.compensateModify();
        if (result > 0) {
            return ResponseUtil.success(result);
        }
        return ResponseUtil.success("失败",null);
    }


}
