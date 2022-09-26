package com.yunya.feign.treatment_other.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2022/9/26 13:11
 * @description: 回访查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("回访查询模型")
public class ReturnVisitQuery extends PageQuery implements Serializable {

    /** 距离末次开单时长 */
    @ApiModelProperty("距离末次开单时长")
    private Integer days;

    /** 距离末次开单的查询方式：0-在范围之内，1-在范围之外*/
    @ApiModelProperty(value = "距离末次开单的查询方式：0-在范围之内，1-在范围之外", required = true)
    private Integer dayOps = 0;
    
    /** 初复诊类型：0-初诊，1-复诊 */
    @ApiModelProperty(value = "初复诊：0-初诊，1-复诊")
    private List<Integer> treatTypes;

    /** 挂号医生ID列表 */
    @ApiModelProperty(value = "挂号医生ID列表")
    private List<Integer> dentistIds;

    /** 患者条件: 患者手机号、姓名、拼音 */
    @ApiModelProperty("患者手机号、姓名、拼音")
    private String combination;

    /** 开单类型：0-价目，1-商品 */
    @ApiModelProperty("开单类型：0-价目，1-商品")
    private Integer itemType;
    
    /** 开单项目id列表 */
    @ApiModelProperty(value = "开单项目id列表")
    private List<Integer> itemIds;
}
