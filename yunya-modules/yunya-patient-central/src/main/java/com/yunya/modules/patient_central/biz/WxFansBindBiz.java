package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.WxFansQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.modules.patient_central.mapper.WxFansBindMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介:公司 微信公众号粉丝与患者绑定关系业务层
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WxFansBindBiz extends BaseBiz<WxFansBindMapper, WxFansBind> {


}
