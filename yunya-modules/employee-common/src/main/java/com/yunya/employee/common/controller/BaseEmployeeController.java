package com.yunya.employee.common.controller;

import com.github.pagehelper.PageInfo;

import com.yunya.employee.common.model.request.BaseEmployeeCreateReq;
import com.yunya.employee.common.model.request.BaseEmployeePageReq;
import com.yunya.employee.common.model.request.BaseEmployeeUpdateReq;
import com.yunya.employee.common.model.response.BaseEmployeeDetailRes;
import com.yunya.employee.common.model.response.BaseEmployeePageRes;
import com.yunya.employee.common.service.BaseEmployeeBiz;
import com.yunya.employee.common.service.ClinicEmployeeBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.clinic.BaseEmployee;
import com.yunya.models.clinic.ClinicEmployee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 门诊端员工管理
 * @author bruce
 * @date 2020/7/9
 */
@Api(tags = {"公司端员工管理"})
@RestController
@RequestMapping("base/employee")
public class BaseEmployeeController {

//    @Resource
//    private BaseEmployeeBiz baseEmployeeBiz;
//
//    @Resource
//    private ClinicEmployeeBiz clinicEmployeeBiz;
//
//    /**
//     * 新增员工基础信息
//     *
//     * @param createReq
//     * @return
//     */
//    @PostMapping("/create")
//    @ApiOperation("新增员工基础信息")
//    public ResponseResult saveBaseEmployee(@Valid @RequestBody BaseEmployeeCreateReq createReq) {
//        baseEmployeeBiz.saveBaseEmployee(createReq);
//        return ResponseResult.success();
//    }
//
//    /**
//     * 修改员工基础信息
//     *
//     * @param employeeId
//     * @param updateReq
//     * @return
//     */
//    @PutMapping("/{employeeId}")
//    @ApiOperation("修改员工基础信息")
//    public ResponseResult modifyEmployee(@PathVariable("employeeId") Integer employeeId,
//                                                    @RequestBody BaseEmployeeUpdateReq updateReq) {
//        baseEmployeeBiz.updateBaseEmployee(employeeId, updateReq);
//        return ResponseResult.success();
//    }
//
//    /**
//     * 删除员工基础信息
//     *
//     * @param employeeId
//     * @return
//     */
//    @DeleteMapping("/{employeeId}")
//    @ApiOperation("删除员工基础信息")
//    public ResponseResult deleteBaseEmployee(@PathVariable("employeeId") Integer employeeId) {
//        baseEmployeeBiz.deleteBaseEmployee(employeeId);
//        return ResponseResult.success();
//    }
//
//    /**
//     * 新增门诊员工基础信息
//     *
//     * @param clinicEmployee
//     * @return
//     */
//    @PostMapping(value = "/clinic_employee")
//    public Map<String, Object> saveClinicEmployee(@RequestBody @Valid ClinicEmployee clinicEmployee) {
//        clinicEmployeeBiz.saveClinicEmployee(clinicEmployee);
//        return ResponseUtil.success();
//    }
//
//    /**
//     * 修改门诊员工基础信息
//     *
//     * @param clinicEmployee
//     * @return
//     */
//    @PutMapping(value = "/clinic_employee")
//    public Map<String, Object> updateClinicEmployee(@RequestBody @Valid ClinicEmployee clinicEmployee) {
//        clinicEmployeeBiz.updateClinicEmployee(clinicEmployee);
//        return ResponseUtil.success();
//    }
//
//    /**
//     * 删除门诊员工基础信息
//     *
//     * @param clinicId
//     * @param userId
//     * @return
//     */
//    @DeleteMapping("/clinic_employee/{clinicId}/{userId}")
//    Map<String, Object> deleteClinicEmployee(@PathVariable(name = "clinicId") Integer clinicId,
//                                             @PathVariable(name = "userId") Integer userId) {
//        BaseEmployee baseEmployee = new BaseEmployee();
//        baseEmployee.setUserId(userId);
//        BaseEmployee data = baseEmployeeBiz.selectOne(baseEmployee);
//
//        ClinicEmployee clinicEmployee = new ClinicEmployee();
//        clinicEmployee.setClinicId(clinicId);
//        clinicEmployee.setEmployeeId(data.getId());
//        clinicEmployeeBiz.delete(clinicEmployee);
//        return ResponseUtil.success();
//    }
//
//    /**
//     * 公司端员工列表查询
//     * @return
//     */
//    @ApiOperation("公司端-员工管理-列表")
//    @PostMapping("/list")
//    public RestDataResult<PageInfo<BaseEmployeePageRes>> getEmployeePage(@RequestBody BaseEmployeePageReq pageReq) {
//        PageInfo<BaseEmployeePageRes> pageInfo = baseEmployeeBiz.getEmployeePageList(pageReq);
//        return ResponseUtil.successRes(pageInfo);
//    }
//
//    /**
//     * 公司端员工详情
//     * @return
//     */
//    @ApiOperation("公司端-员工管理-查看")
//    @GetMapping("/{employeeId}")
//    public RestDataResult<BaseEmployeeDetailRes> getEmployeeRecord(@PathVariable("employeeId") Integer employeeId) {
//        BaseEmployeeDetailRes employeeDetail = baseEmployeeBiz.getEmployeeDetail(employeeId);
//        return ResponseUtil.successRes(employeeDetail);
//    }



}
