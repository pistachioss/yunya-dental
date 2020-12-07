package com.yunya.modules.employeeattend.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.vo.ApprovalAllListVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.VacationSet;
import com.yunya.modules.employeeattend.biz.FieldInfoBiz;
import com.yunya.modules.employeeattend.form.ApprovalAllListForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(tags = "申请记录接口")
@RestController
@RequestMapping("/approval_all_list")
@CrossOrigin
public class ApprovalAllListController {

    @Autowired
    private FieldInfoBiz fieldInfoBiz;

    /**
     * 申请记录
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("申请记录")
    public ResponseResult<PageInfo<ApprovalAllListVO>> findList(@RequestBody @Validated ApprovalAllListForm approvalAllListForm) {
        return ResponseUtil.success(fieldInfoBiz.findApprovalAllList(approvalAllListForm));
    }

}
