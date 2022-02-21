package com.yunya.feign.emr.domain.vo;

import com.yunya.feign.emr.domain.model.TreatPlanRecordModel;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：普通电子病历治疗计划VO
 *
 * @author: chenlin
 * @Description: 普通电子病历治疗计划VO
 * @Date: 2022/1/12 17:19
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("普通电子病历治疗计划VO")
public class MedicalTreatPlanRecordVO extends TreatPlanRecordVO implements Serializable {
    /** 照片影像列表*/
    @ApiModelProperty("照片影像列表")
    private List<XUploadFileVO> xRayFilms;

    /** 主述*/
    @ApiModelProperty("主述")
    private String chiefComplaint;

    /** 现病史*/
    @ApiModelProperty("现病史")
    private String presentIllness;

    /** 既往史*/
    @ApiModelProperty("既往史")
    private String pastHistory;

    /** 检查 */
    @ApiModelProperty("检查")
    private List<ExaminationsVO> examination;

    /** 诊断 */
    @ApiModelProperty("诊断")
    private List<ExaminationsVO> diagnosis;
}
