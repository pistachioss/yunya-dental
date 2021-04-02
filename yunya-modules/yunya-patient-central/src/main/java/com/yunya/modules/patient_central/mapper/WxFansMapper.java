package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.WxFansQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.models.patient_central.WxFans;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WxFansMapper extends Mapper<WxFans> {

    List<WxFansVo> findList(WxFansQueryForm wxFansQueryForm);
}