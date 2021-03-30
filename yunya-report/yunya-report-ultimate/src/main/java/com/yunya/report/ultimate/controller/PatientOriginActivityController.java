package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientOriginActivityQuery;
import com.yunya.feign.patient_central.domain.query.ReceiverkLoadQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginActivityVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedWorkloadDetailsVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.PatientOriginActivityRelationsBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介:公司端-市场营销菜单内-活动推荐
 *
 * @author: WY
 * @date: 2021/3/19 16:32
 * @description: 公司端-市场营销菜单内-活动推荐
 * @since: 1.0.0
 */
@Api(tags = "公司端-市场营销-活动推荐报表")
@RestController
@RequestMapping("originActivity")
public class PatientOriginActivityController {

    /** 业务层 */
    @Resource
    PatientOriginActivityRelationsBiz patientOriginActivityRelationsBiz;

    /**
     * 活动推荐推荐分页列表查询
     * @param query 查询条件
     * @return 活动推荐推荐分页列表信息
     */
    @ApiOperation("活动推荐列表")
    @PostMapping(value = "/employeeReferral",name = "公司端-市场营销-活动推荐列表")
    public ResponseResult<PageInfo<PatientOriginActivityVo>> activityReferral(@RequestBody PatientOriginActivityQuery query){
       List<PatientOriginActivityVo> patientOriginActivityVos = patientOriginActivityRelationsBiz.finleActivityReferral(query);
       if (query.getWhetherPage()) {
           Integer pageNum = query.getPageNum();
           Integer pageSize = query.getPageSize();
           int total = patientOriginActivityVos.size();
           PageInfo<PatientOriginActivityVo> pageInfo = new PageInfo<>();
           pageInfo.setPageNum(pageNum);
           pageInfo.setPageSize(pageSize);
           pageInfo.setTotal(total);
           List<PatientOriginActivityVo> list =
                   patientOriginActivityVos.subList(
                           pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
           pageInfo.setList(list);
           return ResponseUtil.success(pageInfo);
       }
       return ResponseUtil.success(new PageInfo<>(patientOriginActivityVos));
    }

    /**
     * 导出活动推荐推荐分页列表查询
     *
     * @param response 响应
     * @param query 查询条件
     * @return 导出活动推荐推荐分页列表查询
     */
    @ApiOperation("导出活动推荐记录列表")
    @PostMapping(value = "/employeeReferral/export", name = "公司端-市场营销-活动推荐-导出")
    public ResponseResult<T> exportEmployeeReferral(
            HttpServletResponse response, @RequestBody PatientOriginActivityQuery query)
            throws IOException {
        patientOriginActivityRelationsBiz.exportActivityReferralList(response,query);
        return ResponseUtil.success(null);
    }



    /**
     * 已收工作量明细列表分页列表查询
     * @param query 查询条件
     * @return 已收工作量明细列表分页列表查询
     */
    @ApiOperation("活动推荐-各项明细列表")
    @PostMapping(value = "/workloadBreakdown",name = "公司端-市场营销-活动推荐-各项明细列表 type区分")
    public ResponseResult<PageInfo<ReceivedWorkloadDetailsVo>> workloadBreakdown(@RequestBody ReceiverkLoadQuery query){
        List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsVoList = patientOriginActivityRelationsBiz.findEreceiverkLoad(query);
        if (query.getWhetherPage()) {
            Integer pageNum = query.getPageNum();
            Integer pageSize = query.getPageSize();
            int total = receivedWorkloadDetailsVoList.size();
            PageInfo<ReceivedWorkloadDetailsVo> pageInfo = new PageInfo<>();
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setTotal(total);
            List<ReceivedWorkloadDetailsVo> list =
                    receivedWorkloadDetailsVoList.subList(
                            pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
            pageInfo.setList(list);
            return ResponseUtil.success(pageInfo);
        }
        return ResponseUtil.success(new PageInfo<>(receivedWorkloadDetailsVoList));
    }



}