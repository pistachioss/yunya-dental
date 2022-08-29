package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.BaseTariffFellowUpRelationByItemIdQuery;
import com.yunya.feign.treatment.domain.vo.BaseTariffFellowUpRelationVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BaseTariffFellowUpRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Class BaseTariffFellowUpRelationController
 * @Description 基础项目随访关系控制器
 * @Author lihuibin
 * @Date 2022/8/19 23:42
 * @Version 1.0
 */
@Api("基础项目随访关系控制器")
@RestController
@RequestMapping("/tariffFellowUpRelation")
public class BaseTariffFellowUpRelationController {

    @Resource
    private BaseTariffFellowUpRelationBiz baseTariffFellowUpRelationBiz;

    @ApiOperation("根据ID删除基础项目随访信息")
    @DeleteMapping("/deleteById/{id}")
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        baseTariffFellowUpRelationBiz.deleteById(id);
        return ResponseUtil.success();
    }

    @ApiOperation("根据项目ID查询随访信息")
    @PostMapping("/queryByItemId")
    public ResponseResult<PageInfo<BaseTariffFellowUpRelationVO>> queryByItemId(@RequestBody @Validated BaseTariffFellowUpRelationByItemIdQuery query) {

        PageInfo<BaseTariffFellowUpRelationVO> pageResult = baseTariffFellowUpRelationBiz.queryByItemId(query);
        return ResponseUtil.success(pageResult);
    }
}
