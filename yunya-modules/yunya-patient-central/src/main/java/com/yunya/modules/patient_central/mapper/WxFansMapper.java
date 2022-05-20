package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.WxFanByNameForm;
import com.yunya.feign.patient_central.domain.query.WxFansDetailForm;
import com.yunya.feign.patient_central.domain.query.WxFansQueryForm;
import com.yunya.feign.patient_central.domain.query.WxFansWechatQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansDetailVO;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.feign.patient_central.domain.vo.web.WxWechatFansVo;
import com.yunya.models.patient_central.WxFans;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WxFansMapper extends Mapper<WxFans> {

    List<WxFansVo> findList(WxFansQueryForm wxFansQueryForm);

    List<WxWechatFansVo> findWechatList(WxFansWechatQueryForm wxFansQueryForm);

    List<WxFansVo> findListByName(WxFanByNameForm wxFanByNameForm);

    List<WxFansDetailVO> findDetail(WxFansDetailForm wxFansDetailForm);
}