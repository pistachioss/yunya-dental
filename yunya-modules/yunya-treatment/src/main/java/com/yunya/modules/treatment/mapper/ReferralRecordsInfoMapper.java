package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.form.ReferredInfoForm;
import com.yunya.feign.treatment.domain.form.ReferredRrportForm;
import com.yunya.feign.treatment.domain.vo.ReferredInfoVO;
import com.yunya.feign.treatment.domain.vo.ReferredRrportVO;
import com.yunya.models.treatment.ReferralRecordsInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ReferralRecordsInfoMapper extends Mapper<ReferralRecordsInfo> {

    List<ReferredInfoVO> referredInfo(ReferredInfoForm referredForm);

    List<ReferredRrportVO> referredReport(ReferredRrportForm referredRrportForm);

}