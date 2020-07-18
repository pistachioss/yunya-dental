package com.yunya.employee.common.service;

import com.github.pagehelper.PageHelper;

import com.github.pagehelper.PageInfo;
import com.yunya.employee.common.constant.*;
import com.yunya.employee.common.mapper.BaseEmployeeMapper;
import com.yunya.employee.common.mapper.ClinicEmployeeMapper;
import com.yunya.employee.common.model.request.BaseBasicEmployeeReq;
import com.yunya.employee.common.model.request.BaseEmployeeCreateReq;
import com.yunya.employee.common.model.request.BaseEmployeePageReq;
import com.yunya.employee.common.model.request.BaseEmployeeUpdateReq;
import com.yunya.employee.common.model.response.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.clinic.BaseEmployee;
import com.yunya.models.clinic.ClinicEmployee;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;


/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-25 14:15
 */
@Service
public class BaseEmployeeBiz extends BaseBiz<BaseEmployeeMapper, BaseEmployee> {

//    @Resource
//    private SysUserPostClient sysUserPostClient;
//
//    @Resource
//    private SysUserClient sysUserClient;
//
//    @Resource
//    private ClinicEmployeeMapper clinicEmployeeMapper;
//
//    /**
//     * 新增员工
//     *
//     * @param createReq
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void saveBaseEmployee(BaseEmployeeCreateReq createReq) {
//        Integer employeeId = createReq.getEmployeeId();
//        BaseBasicEmployeeReq basicReq = createReq.getBasicEmployeeReq();
//        //校验用户名 手机号码 身份证字段
//        checkEmployeeColumn(employeeId, basicReq);
//        BaseEmployee createEmployee = EntityUtils.build(basicReq, BaseEmployee.class);
//        createEmployee.setName(basicReq.getEmployeeName());
//        createEmployee.setUserId(employeeId);
//        //插入门诊员工基础表
//        mapper.insertSelective(createEmployee);
//        //插入门诊员工权限表
//        List<Integer> orgIds = basicReq.getEmployeeLoginOrgIds();
//        if (CollectionUtils.isNotEmpty(orgIds)) {
//            List<ClinicEmployee> list = new ArrayList<>(orgIds.size());
//            for (Integer orgId : orgIds) {
//                ClinicEmployee clinicEmployee = new ClinicEmployee();
//                clinicEmployee.setEmployeeId(employeeId);
//                clinicEmployee.setClinicId(orgId);
//                list.add(clinicEmployee);
//            }
//            clinicEmployeeMapper.insertBatch(list);
//        }
//    }
//
//    /**
//     * 删除员工
//     * @param employeeId
//     */
//    public void deleteBaseEmployee(Integer employeeId) {
//        Example example = new Example(BaseEmployee.class);
//        example.createCriteria().andEqualTo("userId",employeeId);
//        mapper.deleteByExample(example);
//    }
//
//    private void checkEmployeeColumn(Integer employeeId, BaseBasicEmployeeReq basicReq) {
//        int countByEmployeeName = mapper.countByEmployeeName(employeeId, basicReq.getEmployeeName());
//        if (countByEmployeeName > 0) {
//            throw new BaseException("该员工姓名与系统中已有员工重复，不允许新增！");
//        }
//        int countByIdCard = mapper.countByIdCard(employeeId, basicReq.getIdCard());
//        if (countByIdCard > 0) {
//            throw new BaseException("该身份证号与系统中已有员工身份证号重复，不允许新增！");
//        }
//        int countByMobilePhone = mapper.countByMobilePhone(employeeId, basicReq.getMobilePhone());
//        if (countByMobilePhone > 0) {
//            throw new BaseException("该手机号与系统中已有手机号重复，不允许新增！");
//        }
//        Matcher idCardMatcher = EmployeeCommonConstant.ID_CARD_PATTER.matcher(basicReq.getIdCard());
//        if (!idCardMatcher.matches()) {
//            throw new BaseException("身份证号码格式不正确");
//        }
//        if (EmployeeTypeEnum.LEAVING.getCode().equals(basicReq.getType()) && basicReq.getLeaveTime() == null) {
//            throw new BaseException("员工离职，请输入离职时间");
//        }
//    }
//
//    /**
//     * 修改员工信息
//     *
//     * @param employeeId
//     * @param updateReq
//     */
//    public void updateBaseEmployee(Integer employeeId, BaseEmployeeUpdateReq updateReq) {
//        ClinicEmployee clinicEmployee;
//        BaseBasicEmployeeReq basicReq = updateReq.getBasicEmployeeReq();
//        //校验用户名 手机号码 身份证字段
//        checkEmployeeColumn(employeeId, basicReq);
//
//        BaseEmployee updateEmployee = EntityUtils.build(basicReq, BaseEmployee.class);
//        updateEmployee.setName(basicReq.getEmployeeName());
//        updateEmployee.setUserId(employeeId);
//        Example example = new Example(BaseEmployee.class);
//        example.createCriteria().andEqualTo("userId",employeeId);
//        mapper.updateByExampleSelective(updateEmployee, example);
//
//        clinicEmployee = new ClinicEmployee();
//        clinicEmployee.setEmployeeId(employeeId);
//        //当前员工所在的门诊组织
//        List<ClinicEmployee> employeeClinicList = clinicEmployeeMapper.select(clinicEmployee);
//        Set<Integer> oldClinicIds = employeeClinicList.stream().map(ClinicEmployee::getClinicId).collect(Collectors.toSet());
//        //新的员工门诊组织Id
//        List<Integer> newOrgIds = basicReq.getEmployeeLoginOrgIds();
//        Set<Integer> newClinicIds =  SetUtils.hashSet(newOrgIds.toArray(new Integer[0]));
//        SetUtils.SetView<Integer> delClinicIds = SetUtils.difference(oldClinicIds, newClinicIds);
//        if (CollectionUtils.isNotEmpty(delClinicIds)) {
//            //需要删除的员工所在的门诊组织
//            Example clinicExample = new Example(ClinicEmployee.class);
//            clinicExample.createCriteria().andIn("clinicId", delClinicIds).andEqualTo("employeeId",employeeId);
//            clinicEmployeeMapper.deleteByExample(clinicExample);
//        }
//        //新增员工门诊组织
//        SetUtils.SetView<Integer> addClinicIds = SetUtils.difference(newClinicIds, oldClinicIds);
//        if (CollectionUtils.isNotEmpty(addClinicIds)) {
//            List<ClinicEmployee> addList = new ArrayList<>(addClinicIds.size());
//            for (Integer addClinicId : addClinicIds) {
//                clinicEmployee = new ClinicEmployee();
//                clinicEmployee.setEmployeeId(employeeId);
//                clinicEmployee.setClinicId(addClinicId);
//                addList.add(clinicEmployee);
//            }
//            clinicEmployeeMapper.insertBatch(addList);
//        }
//    }
//
//    /**
//     * 查询
//     *
//     * @param name
//     * @param mobilePhone
//     * @param type
//     */
//    public List<BaseEmployee> search(String name, String mobilePhone, Integer type) {
//        return mapper.selectByNameAndTelAndType(name, mobilePhone, type);
//    }
//
//    /**
//     * 公司端员工详情
//     * @param employeeId
//     * @return
//     */
//    public BaseEmployeeDetailRes getEmployeeDetail(Integer employeeId) {
//        BaseEmployeeDetailRes detailRes = new BaseEmployeeDetailRes();
//        BaseEmployee baseEmployee = new BaseEmployee();
//        baseEmployee.setUserId(employeeId);
//        baseEmployee = mapper.selectOne(baseEmployee);
//        //员工基本信息
//        BaseEmployeeBasicRes basicInfo = assembleBasicInfo(employeeId, baseEmployee);
//        detailRes.setBasicInfo(basicInfo);
//        return detailRes;
//    }
//
//    /**
//     * 公司端员工列表
//     * @param pageReq
//     * @return
//     */
//    public PageInfo<BaseEmployeePageRes> getEmployeePageList(BaseEmployeePageReq pageReq) {
//        //查询岗位下的用户Ids
//        List<Integer> postUserIds = sysUserPostClient.getPostUser(pageReq.getPostIds());
//        PageHelper.startPage(pageReq.getPageNum(), pageReq.getPageSize());
//        List<BaseEmployeePageRes> resultList = mapper.getBaseEmployeeList(pageReq.getEmployeeKeywords(),
//                postUserIds, pageReq.getTypes());
//        List<Integer> employeeIds = resultList.stream().map(BaseEmployeePageRes::getEmployeeId).collect(Collectors.toList());
//        //获取用户岗位名称映射
//        Map<Integer, String> userPostNameMap = sysUserPostClient.getUserPostName(employeeIds);
//        resultList.forEach(r -> r.setEmployeePost(userPostNameMap.get(r.getEmployeeId())));
//        return new PageInfo<BaseEmployeePageRes>(resultList);
//    }
//
//    private BaseEmployeeBasicRes assembleBasicInfo(Integer employeeId, BaseEmployee baseEmployee) {
//        BaseEmployeeBasicRes basicInfo = EntityUtils.build(baseEmployee, BaseEmployeeBasicRes.class);
//        basicInfo.setEmployeeName(baseEmployee.getName());
//        basicInfo.setSex(SexEnum.getName(baseEmployee.getSex()));
//        basicInfo.setTypeName(EmployeeTypeEnum.getName(baseEmployee.getType()));
//        basicInfo.setWorkState(EmployeeWorkEnum.getName(baseEmployee.getWorkState()));
//        //员工是否享有折扣权限
//        basicInfo.setEnableDiscount(TrueOrFalseEnum.getName(baseEmployee.getDiscount()));
//        //设置员工生日
//        List<SysUserDetailRes> userRecord = sysUserClient.getUserRecord(Collections.singletonList(employeeId));
//        basicInfo.setBirthday(userRecord.get(0).getBirthday());
//        //查询员工组织
//        List<BaseEmployeeLoginOrgRes> employeePostInfo = sysUserPostClient.getEmployeePostInfo(employeeId);
//        basicInfo.setEmployeeOrgList(employeePostInfo);
//        return basicInfo;
//    }
}
