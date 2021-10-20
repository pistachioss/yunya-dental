package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.model.TreatPlanModel;
import com.yunya.feign.treatment_other.domain.model.XUploadFileModel;
import com.yunya.feign.treatment_other.domain.query.TreatPlanQuery;
import com.yunya.feign.treatment_other.domain.vo.TreatPlanVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.FileSourceTypeEnum;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment_other.TreatPlan;
import com.yunya.modules.treatment.other.mapper.TreatPlanMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class TreatPlanBiz extends BaseBiz<TreatPlanMapper, TreatPlan> {

    @Autowired
    private XUploadFileBiz xUploadFileBiz;

    /**
     * 查询治疗计划列表
     * @param query 查询条件
     * @return 返回数据列表
     */
    public PageInfo<TreatPlanVO> findTreatPlanList(TreatPlanQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<TreatPlanVO> data = mapper.selectTreatPlanList(query);
        return new PageInfo<>(data);
    }

    /**
     * 批量上传（可单独上传）
     * @param model 参数模型
     */
    public ResponseResult<T> addBatch(TreatPlanModel model){
        Date now = new Date(System.currentTimeMillis());
        List<XUploadFileModel> files = model.getList();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        TreatPlan treatPlan = new TreatPlan();
        treatPlan.setPatientId(model.getPatientId());
        treatPlan.setCrtId(userId);
        treatPlan.setCrtTime(now);
        treatPlan.setUpdId(userId);
        treatPlan.setUpdTime(now);
        mapper.insertSelective(treatPlan);
        if (StringHelper.isNotEmpty(files)) {
            int count = xUploadFileBiz.addBatch(files, treatPlan.getId(), FileSourceTypeEnum.TREAT_PLAN.getCode(), now);
            if (count > 0) {
                return ResponseUtil.success();
            }
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"上传文件失败",null);
        }
        return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL,"上传文件列表不能为空",null);
    }
}
