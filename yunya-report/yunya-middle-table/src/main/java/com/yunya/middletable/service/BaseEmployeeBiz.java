package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseEmployeeMapper;
import com.yunya.middletable.dao.system.SysEmployeeMapper;
import com.yunya.middletable.dao.system.SysUserMapper;
import com.yunya.models.report.BaseEmployee;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.system.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 简介: 报表中间表员工信息同步业务层
 *
 * @author: chow
 * @date: 2020/10/15 16:47
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseEmployeeBiz extends BaseBiz<BaseEmployeeMapper, BaseEmployee> {

  /** 用户信息 */
  @Autowired private SysUserMapper userMapper;

  /** 员工信息 */
  @Autowired private SysEmployeeMapper employeeMapper;

  /**
   * 根据消息操作中间表员工信息
   *
   * @param msg 消息对象
   */
  public void operateEmployee(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseEmployee employee = generateEmployee(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.deleteByPrimaryKey(dataId);
        if (null != employee) {
          mapper.insertSelective(employee);
        }
        break;
      case 1:
        if (null != employee) {
          BaseEmployee result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.deleteByPrimaryKey(dataId);
            mapper.insertSelective(employee);
          } else {
            mapper.updateByPrimaryKeySelective(employee);
          }
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 2:
        if (null == employee) {
          mapper.deleteByPrimaryKey(dataId);
        } else {
          mapper.insertSelective(employee);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 构建中间表员工信息
   *
   * @param userId 员工ID
   */
  private BaseEmployee generateEmployee(Integer userId) {
    SysUser sysUser = userMapper.selectByPrimaryKey(userId);
    return null != sysUser ? setBaseEmployeeValue(userId) : null;
  }

  /**
   * 设置员工字段属性
   *
   * @param userId 员工id
   * @return
   */
  private BaseEmployee setBaseEmployeeValue(Integer userId) {
    BaseEmployee employee = new BaseEmployee();
    employee.setUserId(userId);
    SysEmployee sysEmp = new SysEmployee();
    sysEmp.setUserId(userId);
    SysEmployee empResult = employeeMapper.selectOne(sysEmp);
    if (null != empResult) {
      employee.setEmployeeName(empResult.getName());
      employee.setGender(empResult.getGender());
      employee.setMobile(empResult.getMobilePhone());
      employee.setWorkStatus(empResult.getWorkStatus());
      employee.setTitle(empResult.getTitle());
      employee.setPostLevel(empResult.getPostLevel());
      employee.setBonusCoefficient(empResult.getBonusCoefficient());
      employee.setWorkAmount(empResult.getWorkAmount());
      employee.setWorkNumber(empResult.getWorkNumber());
    }
    return employee;
  }

  /**
   * 拉取某段时间内的员工数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullEmpData(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example emp = new Example(SysUser.class);
    emp.createCriteria().andBetween("updTime", startDate, endDate);
    List<SysUser> sysUsers = userMapper.selectByExample(emp);
    if (StringHelper.isNotEmpty(sysUsers)) {
      sysUsers.forEach(
          su -> {
            Integer userId = su.getId();
            mapper.deleteByPrimaryKey(userId);
            BaseEmployee employee = setBaseEmployeeValue(userId);
            mapper.insertSelective(employee);
          });
    }
  }
}
