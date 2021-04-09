package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：月工作量完成VO
 *
 * @author: chenlin
 * @Description: 月工作量完成VO
 * @Date: 2021/3/30 9:27
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("月工作量完成VO")
public class WorkloadMonthGoalCompletedVO implements Serializable {

    @Excel(name = "门诊")
    private String itemTitle;

    @Excel(name = "古墩路")
    private String guDunRoad;

    @Excel(name = "金沙大道")
    private String jinShaRoad;

    @Excel(name = "乾元")
    private String qianYuan;

    @Excel(name = "常春藤")
    private String changChunTeng;

    @Excel(name = "西溪路")
    private String xiXiRoad;

    @Excel(name = "春江花月")
    private String chunJiangHuaYue;

    @Excel(name = "鲲鹏")
    private String kunPengRoad;

    @Excel(name = "滨江龙湖")
    private String longHu;

    @Excel(name = "雅文")
    private String yaWen;

    @Excel(name = "博方")
    private String boFang;

    @Excel(name = "艾芃")
    private String aiPeng;

    @Excel(name = "文二西路")
    private String wenErXiRoad;

    @Excel(name = "合计")
    private String total;
}
