package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ApiModel("会员分析报表VO")
@Data
@ToString
public class VipLogoVo implements Serializable {
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String patientName;
    @Excel(name = "手机号")
    @ApiModelProperty("手机号")
    private String mobile;
    @Excel(name = "性别", readConverterExp = "0=男,1=女", type = Excel.Type.EXPORT)
    @ApiModelProperty("性别（0-男；1-女)")
    private Byte gender;
    @Excel(name = "年龄")
    @ApiModelProperty("年龄")
    private Integer age;
    @Excel(name = "病历号")
    @ApiModelProperty("病历号")
    private String medicalNumber;
    @Excel(name = "末次就诊门诊")
    @ApiModelProperty("末次就诊门诊")
    private String abbreviation;
    @Excel(name = "本次统计的会员级别")
    @ApiModelProperty("本次统计的会员级别")
    private String vipLogo;
    @Excel(name = "年平均就诊次数（次）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("年平均就诊次数（次）")
    private String visitRate;
    @Excel(name = "累计消费（万）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("累计消费（万）")
    private String cumulativeConsumption;
    @Excel(name = "总计得分（分）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("总计得分（分）")
    private String vipScore;
    @Excel(name = "末次就诊日期")
    @ApiModelProperty("末次就诊日期")
    private String lastVisitDate;
    @Excel(name = "下次预约")
    @ApiModelProperty("下次预约")
    private String nextAppointTime;
    @Excel(name = "下次提醒")
    @ApiModelProperty("下次提醒")
    private String nextRemindTime;
    @Excel(name = "会员卡名称")
    @ApiModelProperty("会员卡名称")
    private String memberLevelName;
    @Excel(name = "绑定会员产品名称")
    @ApiModelProperty("绑定会员产品名称")
    private String couponName;
    @Excel(name = "上一次统计的会员级别")
    @ApiModelProperty("上一次统计的会员级别")
    private String vipLogoOld;
}
