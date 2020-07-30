package com.yunya.modules.employeeattend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleCopyVO
 * @description 复制排班VO类
 * @date 2020/7/16 10:48
 */
@Data
public class EmployeeScheduleCopyVO{
  /**
   * 主键ID
   */
  private Integer id;

  /**
   * 门诊ID
   */
  private Integer clinicId;

  /**
   * 排班ID
   */
  private Integer scheduleId;

  /**
   * 员工ID
   */

  private Integer employeeId;

  /**
   * 工作日
   */
  private Date workDate;

  /**
   * 是否启用
   */
  private Boolean inservice;

  /**
   * 创建人ID
   */
  private Integer crtId;

  /**
   * 创建人名称
   */
  private String crtName;

  /**
   * 创建时间
   */
  private Date crtTime;


  private Integer updId;

  /**
   * 修改人名称
   */
  private String updName;

  /**
   * 修改时间
   */
  private Date updTime;
  /**
   * 开始时间点1
   */
  @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
  private Date firstStartTime;

  /**
   * 结束时间点1
   */
  @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
  private Date firstEndTime;

  /**
   * 开始时间点2
   */
  @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
  private Date secondStartTime;

  /**
   * 结束时间点2
   */
  @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
  private Date secondEndTime;

  /**
   * 员工姓名
   */
  private String userName;
  /**
   * 排班名称
   */
  private String scheduleName;

}
