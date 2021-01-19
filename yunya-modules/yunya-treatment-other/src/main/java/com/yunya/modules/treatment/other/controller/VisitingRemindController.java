package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.treatment_other.domain.form.VisitingRemindForm;
import com.yunya.feign.treatment_other.domain.model.VisitingRemindModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRemindQuery;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.other.biz.VisitingRemindBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    /** 注入服务 */
    @Autowired
    private VisitingRemindBiz visitingRemindBiz;

    /**
     * 新增随访提醒
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
     * 根据id删除随访提醒
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
     * @param id 随访提醒id
     * @return  ResponseResult
     */
    @ApiOperation("根据id查询随访提醒")
    @GetMapping("/find/{id}")
    public ResponseResult findVisitingRemindById(@PathVariable("id") Integer id) {
        return visitingRemindBiz.findVisitingRemindById(id);
    }

    /**
     * 根据条件查询随访提醒
     * @param query 查询条件
     * @return  ResponseResult
     */
    @ApiOperation("根据条件查询随访提醒")
    @PostMapping("/find")
    @CurrentUser
    public ResponseResult findVisitingRemindByCondition(@RequestBody @Validated VisitingRemindQuery query) {
        return visitingRemindBiz.findVisitingRemindByCondition(query);
    }

    /**
     * 根据条件查询随访提醒
     * @param query 查询条件
     * @return  ResponseResult
     */
    @ApiOperation("根据条件查询随访提醒")
    @PostMapping("/get")
    @CurrentUser
    public ResponseResult getVisitingRemindByCondition(@RequestBody @Validated VisitingRemindQuery query) {
        return visitingRemindBiz.getVisitingRemindByCondition(query);
    }

    /**
     * 完成提醒
     * @param id 提醒id
     * @return ResponseResult
     */
    @ApiOperation("完成提醒")
    @CurrentUser
    @PutMapping("/finish/{id}")
    public ResponseResult finishVisitingRemind(@PathVariable("id") Integer id) {
        return visitingRemindBiz.finishVisitingRemind(id);
    }


}
