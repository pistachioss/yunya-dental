package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 门诊可用科室VO
 *
 * @author: chow
 * @date: 2020/9/30 10:41
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊可用科室VO")
@Data
@ToString
public class ClinicDeptRoomListVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 科室列表 */
  private List<DeptRoomVO> clinicDeptRooms;
}
