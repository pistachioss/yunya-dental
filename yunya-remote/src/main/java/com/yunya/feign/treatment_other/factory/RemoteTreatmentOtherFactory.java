package com.yunya.feign.treatment_other.factory;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.treatment.domain.form.QcTreatmentImportForm;
import com.yunya.feign.treatment.domain.vo.QcRecommondOrderVO;
import com.yunya.feign.treatment.domain.vo.QcTreatmentVO;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.model.MedicalRayFilmModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.query.XRayFilmQuery;
import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.feign.treatment_other.domain.vo.*;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.XRayFilm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: yunya-dental
 * @description: 就诊扩展外部调用接口降级处理
 * @author: LHB
 * @create: 2020-08-25 20:03
 **/
@Component
@Slf4j
public class RemoteTreatmentOtherFactory implements RemoteTreatmentOtherFeign {

    @Override
    public List<XRayFilmVO> findPhotoListInfo(XRayFilmQuery query) {
        return null;
    }

    @Override
    public void insertVisitingRecord(List<VisitingRecord> visitingRecords) {
    }

    @Override
    public List<VisitingRecordVo> findVisitingRecordByConditionRest(VisitingRecordQuery query) {
        return null;
    }

    @Override
    public void deleteVisitingRecordByTreatmentIdRest(Integer treatmentId) {

    }

    @Override
    public Integer countNextVisiting(Integer patientId, String regDate) {
        return null;
    }

    @Override
    public List<NextVisitingRecordVo> countNextVisitingListByIds(List<Integer> patientIds, String regDate) {
        return null;
    }

    @Override
    public List<XRayFilm> findXRayFilmListByPatientIds(List<Integer> patientIds, String currentDate) {
        return null;
    }

    @Override
    public FindAllRemindRecordVO findAllRecord(PullForm pullForm) {
        return null;
    }

    @Override
    public void saveXRayFile2XUploadFile(MedicalRayFilmModel model) {

    }

    @Override
    public List<XUploadFileVO> findXUploadFileList(XUploadFileQuery query) {
        return null;
    }

    @Override
    public void tombstoneUploadFile(MedicalRayFilmModel model) {

    }

    @Override
    public QcRecommondOrderVO orderMatchQcTreatmentList(QcTreatmentImportForm form) {
        return null;
    }

    @Override
    public List<QcTreatmentVO> findBindingQcTreatmentList(Integer orderRecordId) {
        return null;
    }

    @Override
    public void updateQcTreatmentAndItems(QcTreatmentImportForm form) {

    }

}
