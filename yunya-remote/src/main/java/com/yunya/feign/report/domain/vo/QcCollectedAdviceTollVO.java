package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * @author: chenlin
 * @date: 2023/9/25 16:22
 * @description: 全程医疗医嘱收费数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗医嘱收费数据模型")
public class QcCollectedAdviceTollVO implements Serializable {

    /** 全程医疗就诊记录id */
    @ApiModelProperty("全程医疗就诊记录id")
    private Integer qcTreatmentId;

    /** 登记号 */
    @Excel(name = "登记号")
    @ApiModelProperty("登记号")
    private String admNo;

    /** 姓名 */
    @Excel(name = "姓名")
    @ApiModelProperty("姓名")
    private String customerName;

    /** 手机号码 */
    @Excel(name = "手机号码")
    @ApiModelProperty("手机号码")
    private String customerMobile;

    /** 绑定患者 */
    @Excel(name = "绑定患者")
    @ApiModelProperty("绑定患者")
    private String patientName;

    /** 推荐类型 */
    @Excel(name = "推荐类型")
    @ApiModelProperty("推荐类型")
    private String type;

    /** 艾维账单收费时间 */
    @Excel(name = "艾维账单收费时间", dateFormat = "yyyy-MM-dd HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @ApiModelProperty("艾维账单收费时间")
    private Date payeeDate;

    /** 关联账单编号 */
    @Excel(name = "关联账单编号")
    @ApiModelProperty("关联账单编号")
    private String billNum;

    /** 账单费用 */
    @Excel(name = "账单费用", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("账单费用")
    private BigDecimal billReceivedAmount;

    /** 执行医嘱费用 */
    @Excel(name = "执行医嘱费用", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("执行医嘱费用")
    private BigDecimal adviceItemExecAmount;

    /** 未执行医嘱费用 */
    @Excel(name = "未执行医嘱费用", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("未执行医嘱费用")
    private BigDecimal adviceItemUnExecAmount;
}
