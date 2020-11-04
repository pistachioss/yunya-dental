package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.FinishVisitingForm;
import com.yunya.feign.treatment_other.domain.form.VisitingRecordForm;
import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.feign.treatment_other.domain.query.VisitingContentAfterCurrentQuery;
import com.yunya.feign.treatment_other.domain.query.VisitingForMonthInfo;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingContentAfterCurrentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingContentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingForMonthVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.VisitingRecordBiz;
import io.swagger.annotations.*;
import lombok.CustomLog;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: yunya-dental
 * @description: 随访记录Controller
 * @author: LHB
 * @create: 2020-08-21 17:06
 **/
@Api(tags = "随访记录管理")
@RestController
@RequestMapping("visiting/record")
public class VisitingRecordController {

    /** 注入随访记录服务 */
    @Autowired
    private VisitingRecordBiz visitingRecordBiz;

    /**
     * 新增随访记录
     * @param model 新增记录表单
     * @return  ResponseResult
     */
    @ApiOperation(value = "新增随访记录")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult addRecord(@RequestBody @Validated VisitingRecordModel model){
        return visitingRecordBiz.insertVisitingRecord(model);
    }

    /**
     * 根据id删除随访记录
     * @param id 根据id删除随访记录
     * @return  ResponseResult
     */
    @ApiOperation(value = "删除随访记录")
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteRecord(@PathVariable("id") Integer id){
        return visitingRecordBiz.deleteVisitingRecord(id);
    }

    /**
     * 修改随访记录
     * @param form 修改随访记录
     * @return  ResponseResult
     */
    @ApiOperation(value = "修改随访记录")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult updateRecord(@RequestBody @Validated VisitingRecordForm form){
        return visitingRecordBiz.updateVisitingRecord(form);
    }

    /**
     * 根据id查询随访记录
     * @param id 随访记录id
     * @return  ResponseResult
     */
    @ApiOperation(value = "根据id查询随访记录")
    @GetMapping("/find/{id}")
    public ResponseResult<VisitingRecordVo> findRecordById(@PathVariable("id") Integer id){
        return visitingRecordBiz.findVisitingRecordById(id);
    }

    /**
     * 根据条件查询随访记录
     * @param query 根据条件查询随访记录
     * @return  ResponseResult
     */
    @ApiOperation(value = "根据条件查询随访记录")
    @PostMapping("/find")
    @CurrentUser
    public ResponseResult<PageInfo<VisitingRecordVo>> findRecordByCondition(@RequestBody @Validated VisitingRecordQuery query){
        return visitingRecordBiz.findVisitingRecordByCondition(query);
    }

    /**
     * 随访内容（执行随访按钮用）
     * @param id 随访记录id
     * @return ResponseResult
     */
    @ApiOperation(value = "随访内容（执行随访按钮用）")
    @GetMapping("/execute/visiting/{id}")
    public ResponseResult<VisitingContentVo> executeVisiting(@PathVariable("id") Integer id){
        return visitingRecordBiz.executeVisiting(id);
    }

    /**
     * 后续随访查询（随访管理-执行随访-随访-后续随访）
     * @param query 查询条件
     * @return ResponseResult
     */
    @ApiOperation(value = "后续随访查询（随访管理-执行随访-随访-后续随访）")
    @PostMapping("/find/after/visiting")
    public ResponseResult<PageInfo<VisitingContentAfterCurrentVo>> findAfterVisitingContent(@RequestBody @Validated VisitingContentAfterCurrentQuery query){
        return visitingRecordBiz.findAfterVisitingContent(query);
    }

    /**
     * 随访完成（随访完成-提交）
     * @param form 随访内容
     * @return ResponseResult
     */
    @ApiOperation(value = "随访完成（随访完成-提交）")
    @PostMapping("/finish")
    @CurrentUser
    public ResponseResult<T> finishVisiting(@RequestBody @Validated FinishVisitingForm form) {
        return visitingRecordBiz.finishVisiting(form);
    }

    /**
     * 查询指定时间段内指定医生的每一天的随访数(app用)
     * @param forMonthInfo 表单
     * @return 返回实体
     */
    @ApiOperation(value = "查询指定时间段内指定医生的每一天的随访数(app用)")
    @PostMapping("/visit/range")
    public ResponseResult<List<VisitingForMonthVo>> findVisitingForMonth(@RequestBody @Validated VisitingForMonthInfo forMonthInfo) {
        List<VisitingForMonthVo> visitingForMonth = visitingRecordBiz.findVisitingForMonth(forMonthInfo);
        return ResponseUtil.success(visitingForMonth);
    }

}
