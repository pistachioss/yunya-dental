package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.query.PrepaidQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BasePatientMemberOccurLog;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BasePatientMemberMapper;
import com.yunya.report.ultimate.mapper.BasePatientMemberOccurLogMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介:会员卡/预付款概况控制层
 *
 * @author: WY
 * @date: 2020/10/24 13:37
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOccurLogBiz extends BaseBiz<BasePatientMemberOccurLogMapper, BasePatientMemberOccurLog> {

    @Autowired private BaseOrganizationMapper baseOrganizationMapper;

    /** 患者Mapper */
    @Autowired private BasePatientMapper basePatientMapper;

    /** 患者会员卡信息 */
    @Autowired private BasePatientMemberMapper basePatientMemberMapper;

    /** 注入服务 */
    @Autowired MemberOccurLogBiz memberOccurLogBiz;


    /**
     * 会员卡充值查询
     * @param form 会员卡充值查询Form
     * @return List<MemberRechargeLogBizVo>
     */
    public PageInfo<BaseMemberRechargeLogVo> memberRechargeList(MemberQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BaseMemberRechargeLogVo> memberRechargeLogBizVos = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            memberRechargeLogBizVos = mapper.selectMemberRechargeList(form,patientIds);
        }
        return new PageInfo<>(memberRechargeLogBizVos);
    }

    /**
     * 导出会员充值列表
     * @param response
     * @param form
     */
    public void exportMemberRechargeList(HttpServletResponse response, MemberQueryForm form) throws IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BaseMemberRechargeLogVo> memberRechargeLogBizVos = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            memberRechargeLogBizVos = mapper.selectMemberRechargeList(form,patientIds);
        }
        ExcelUtil<BaseMemberRechargeLogVo> excelUtil = new ExcelUtil<>(BaseMemberRechargeLogVo.class);
        if (StringHelper.isNotNull(form.getOrgId())){
            BaseOrganization baseOrganization = new BaseOrganization();
            baseOrganization.setOrgId(form.getOrgId());
            BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
            if (baseOrganizationv != null){
                excelUtil.exportExcel(response, memberRechargeLogBizVos, "会员卡充值记录表",baseOrganizationv.getAbbreviation()+"会员卡充值记录表");
            }else {
                excelUtil.exportExcel(response, memberRechargeLogBizVos, "会员卡充值记录表","会员卡充值记录表");
            }
        }else {
            excelUtil.exportExcel(response, memberRechargeLogBizVos, "会员卡充值记录表","会员卡充值记录表");
        }
    }

    /**
     * 会员卡消费查询
     * @param form 会员卡消费查询Form
     * @return
     */
    public PageInfo<BaseMemberExpendLogVo> memberExpendList(MemberQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BaseMemberExpendLogVo> baseMemberExpendLogVos = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            baseMemberExpendLogVos = mapper.selectMemberExpendtList(form,patientIds);
        }
        return new PageInfo<>(baseMemberExpendLogVos);
    }

    /**
     * 导出会员消费列表
     * @param response
     * @param form
     */
    public void exportMemberExpendList(HttpServletResponse response, MemberQueryForm form) throws ParseException, IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BaseMemberExpendLogVo> baseMemberExpendLogVos = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            baseMemberExpendLogVos = mapper.selectMemberExpendtList(form,patientIds);
        }
            ExcelUtil<BaseMemberExpendLogVo> excelUtil = new ExcelUtil<>(BaseMemberExpendLogVo.class);
            if (StringHelper.isNotNull(form.getOrgId())){
                BaseOrganization baseOrganization = new BaseOrganization();
                baseOrganization.setOrgId(form.getOrgId());
                BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
                if (baseOrganizationv != null){
                    excelUtil.exportExcel(response, baseMemberExpendLogVos, "会员卡消费记录表",baseOrganizationv.getAbbreviation()+"会员卡消费记录表");
                }else {
                    excelUtil.exportExcel(response, baseMemberExpendLogVos, "会员卡消费记录表","会员卡消费记录表");
                }
            }else {
                excelUtil.exportExcel(response, baseMemberExpendLogVos, "会员卡消费记录表","会员卡消费记录表");
            }
    }

    /**
     * 会员卡退费查询
     * @param form 会员卡退费查询Form
     * @return List<MemberReturnLogBizVo>
     */
    public PageInfo<BaseMemberReturnLogVo> memberReturnList(MemberQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BaseMemberReturnLogVo> baseMemberReturnLogVos = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            baseMemberReturnLogVos = mapper.selectMemberReturnList(form,patientIds);
        }
        return new PageInfo<>(baseMemberReturnLogVos);
    }

    /**
     * 导出会员退费列表
     * @param response
     * @param form
     */
    public void exportMemberReturnList(HttpServletResponse response, MemberQueryForm form) throws ParseException, IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BaseMemberReturnLogVo> baseMemberReturnLogVos = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            baseMemberReturnLogVos = mapper.selectMemberReturnList(form,patientIds);
        }
        ExcelUtil<BaseMemberReturnLogVo> excelUtil = new ExcelUtil<>(BaseMemberReturnLogVo.class);
        if (StringHelper.isNotNull(form.getOrgId())){
            BaseOrganization baseOrganization = new BaseOrganization();
            baseOrganization.setOrgId(form.getOrgId());
            BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
            if (baseOrganizationv != null){
                excelUtil.exportExcel(response, baseMemberReturnLogVos, "会员卡退费记录表",baseOrganizationv.getAbbreviation()+"会员卡退费记录表");
            }else {
                excelUtil.exportExcel(response, baseMemberReturnLogVos, "会员卡退费记录表","会员卡退费记录表");
            }
        }else {
            excelUtil.exportExcel(response, baseMemberReturnLogVos, "会员卡退费记录表","会员卡退费记录表");
        }
    }


    /**
     * 预付款充值查询
     * @param form 预付款充值form
     * @return List<PrepaidRechargeLogBizVo>
     */
    public PageInfo<BasePrepaidRechargeLogVo> prepaidRechargeList(PrepaidQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePrepaidRechargeLogVoList = mapper.selectPrepaidRechargeList(form,patientIds);
        }
        return new PageInfo<>(basePrepaidRechargeLogVoList);
    }

    /**
     * 导出预付款充值列表
     * @param response
     * @param form
     */
    public void exportPrepaidRechargeList(HttpServletResponse response, PrepaidQueryForm form) throws ParseException, IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePrepaidRechargeLogVoList = mapper.selectPrepaidRechargeList(form,patientIds);
        }
        ExcelUtil<BasePrepaidRechargeLogVo> excelUtil = new ExcelUtil<>(BasePrepaidRechargeLogVo.class);
        if (StringHelper.isNotNull(form.getOrgId())){
            BaseOrganization baseOrganization = new BaseOrganization();
            baseOrganization.setOrgId(form.getOrgId());
            BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
            if (baseOrganizationv != null){
                excelUtil.exportExcel(response, basePrepaidRechargeLogVoList, "预付款充值记录表",baseOrganizationv.getAbbreviation()+"预付款充值记录表");
            }else {
                excelUtil.exportExcel(response, basePrepaidRechargeLogVoList, "预付款充值记录表","预付款消费记录表");
            }
        }else {
            excelUtil.exportExcel(response, basePrepaidRechargeLogVoList, "预付款充值记录表","预付款消费记录表");
        }
    }

    /**
     * 预付款消费查询
     * @param form 预付款消费form
     * @return List<PrepaidExpendLogBizVo>
     */
    public PageInfo<BasePrepaidExpendLogVo> prepaidExpendList(PrepaidQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BasePrepaidExpendLogVo> basePrepaidExpendLogVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePrepaidExpendLogVoList = mapper.selectPrepaidExpendList(form,patientIds);
        }
        return new PageInfo<>(basePrepaidExpendLogVoList);
    }

    /**
     * 导出预付款消费列表
     * @param response
     * @param form
     */
    public void exportPrepaidExpendList(HttpServletResponse response, PrepaidQueryForm form) throws ParseException, IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BasePrepaidExpendLogVo> basePrepaidExpendLogVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePrepaidExpendLogVoList = mapper.selectPrepaidExpendList(form,patientIds);
        }
        ExcelUtil<BasePrepaidExpendLogVo> excelUtil = new ExcelUtil<>(BasePrepaidExpendLogVo.class);
        if (StringHelper.isNotNull(form.getOrgId())){
            BaseOrganization baseOrganization = new BaseOrganization();
            baseOrganization.setOrgId(form.getOrgId());
            BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
            if (baseOrganizationv != null){
                excelUtil.exportExcel(response, basePrepaidExpendLogVoList, "预付款消费记录表",baseOrganizationv.getAbbreviation()+"预付款消费记录表");
            }else {
                excelUtil.exportExcel(response, basePrepaidExpendLogVoList, "预付款消费记录表","预付款消费记录表");
            }
        }else {
            excelUtil.exportExcel(response, basePrepaidExpendLogVoList, "预付款消费记录表","预付款消费记录表");
        }
    }


    /**
     * 预付款退款查询
     * @param form 预付款退款form
     * @return List<PrepaidReturnLogBizVo>
     */
    public PageInfo<BasePrepaidReturnLogVo> prepaidReturnList(PrepaidQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BasePrepaidReturnLogVo> basePrepaidReturnLogVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePrepaidReturnLogVoList = mapper.selectPrepaidReturnList(form,patientIds);
        }
        return new PageInfo<>(basePrepaidReturnLogVoList);
    }

    /**
     * 导出预付款退款列表
     * @param response
     * @param form
     */
    public void exportPrepaidReturnList(HttpServletResponse response, PrepaidQueryForm form) throws ParseException, IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<BasePrepaidReturnLogVo> basePrepaidReturnLogVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePrepaidReturnLogVoList = mapper.selectPrepaidReturnList(form,patientIds);
        }
        ExcelUtil<BasePrepaidReturnLogVo> excelUtil = new ExcelUtil<>(BasePrepaidReturnLogVo.class);
        if (StringHelper.isNotNull(form.getOrgId())){
            BaseOrganization baseOrganization = new BaseOrganization();
            baseOrganization.setOrgId(form.getOrgId());
            BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
            if (baseOrganizationv != null){
                excelUtil.exportExcel(response, basePrepaidReturnLogVoList, "预付款退款记录表",baseOrganizationv.getAbbreviation()+"预付款退费记录表");
            }else {
                excelUtil.exportExcel(response, basePrepaidReturnLogVoList, "预付款退款记录表","预付款退费记录表");
            }
        }else {
            excelUtil.exportExcel(response, basePrepaidReturnLogVoList, "预付款退款记录表","预付款退费记录表");
        }

    }

    /**
     * 查询所有门诊信息
     * @return List<BaseOrganization>
     */
    public List<BaseOrganization> orgList() {
        List<BaseOrganization> baseOrganizations = baseOrganizationMapper.selectAll();
        return baseOrganizations;
    }

    /**
     * 查询会员余额结存表
     * @param form 会员余额结存条件
     * @return List<BaseMemberBalanceInfoVo>
     */
    public PageInfo<BaseMemberBalanceInfoVo> memberBalanceList(MemberQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<Integer> patientIds = null;
        List<BaseMemberBalanceInfoVo> basePrepaidReturnLogVoList = new ArrayList<>();
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePrepaidReturnLogVoList = mapper.selectMemberBalanceList(form,patientIds);
        }
        return new PageInfo<>(basePrepaidReturnLogVoList);
    }
    /*

    * */


    /**
     * 导出会员余/预付款余额结存信息记录列表
     * @param response
     * @param form
     * @return PageInfo<BaseMemberBalanceInfoVo>
     */
    public void exportMemberBalanceList(HttpServletResponse response, MemberQueryForm form) throws ParseException, IOException {
        String endDate = form.getEndDate();
        if (StringHelper.isNotEmpty(endDate)){
             endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
            form.setEndDate(endDate);
        }
        List<Integer> patientIds = null;
        List<BaseMemberBalanceInfoVo> resultList = new ArrayList<>();
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            resultList = mapper.selectMemberBalanceList(form,patientIds);
        }

        if (form.getType() == 0){
            ExcelUtil<ExcelBaseMemberBalanceInfoVo> excelUtil = new ExcelUtil<>(ExcelBaseMemberBalanceInfoVo.class);
            List<ExcelBaseMemberBalanceInfoVo> build = EntityUtils.build(resultList, ExcelBaseMemberBalanceInfoVo.class);
            excelUtil.exportExcel(response, build, "会员余额结存表",(form.getStartDate()+"-"+ endDate)+"会员余额结存表");
        }else {
            ExcelUtil<ExcelBasePrepaymentsBalanceInfoVo> excelUtil = new ExcelUtil<>(ExcelBasePrepaymentsBalanceInfoVo.class);
            List<ExcelBasePrepaymentsBalanceInfoVo> build = EntityUtils.build(resultList, ExcelBasePrepaymentsBalanceInfoVo.class);
            excelUtil.exportExcel(response, build, "预付款余额结存表",(form.getStartDate()+"-"+ endDate)+"预付款余额结存表");
        }

    }


}