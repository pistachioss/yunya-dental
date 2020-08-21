package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.treatment_other.domain.form.VisitingRecordForm;
import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.VisitingRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: yunya-dental
 * @description: 随访记录Controller
 * @author: LHB
 * @create: 2020-08-21 17:06
 **/
@Api(tags = "随访记录Controller")
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
        visitingRecordBiz.insertVisitingRecord(model);
        return ResponseUtil.success();
    }

    /**
     * 根据id删除随访记录
     * @param id 根据id删除随访记录
     * @return  ResponseResult
     */
    @ApiOperation(value = "新增随访记录")
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
    public ResponseResult updateRecord(@RequestBody @Validated VisitingRecordForm form){
        return visitingRecordBiz.updateVisitingRecord(form);
    }

    /**
     * 根据id查询随访记录
     * @param id 随访记录id
     * @return  ResponseResult
     */
    @ApiOperation(value = "根据id查询随访记录")
    @PutMapping("/find/{id}")
    public ResponseResult findRecordById(@PathVariable("id") Integer id){
        return visitingRecordBiz.findVisitingRecordById(id);
    }

    /**
     * 根据条件查询随访记录
     * @param query 根据条件查询随访记录
     * @return  ResponseResult
     */
    @ApiOperation(value = "根据条件查询随访记录")
    @PutMapping("/find")
    public ResponseResult findRecordByCondition(@RequestBody @Validated VisitingRecordQuery query){

        return ResponseUtil.success();
    }


}
