package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.WxFansQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.patient_central.WxFans;
import com.yunya.modules.patient_central.mapper.WxFansMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简介: 公司微信公众号粉丝业务层
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
public class WxFansBiz extends BaseBiz<WxFansMapper, WxFans> {

    public PageInfo<WxFansVo> findList(WxFansQueryForm wxFansQueryForm){
        if (wxFansQueryForm.getWhetherPage()) {
            PageHelper.startPage(wxFansQueryForm.getPageNum(), wxFansQueryForm.getPageSize());
        }
        List<WxFansVo> list = mapper.findList(wxFansQueryForm);
        return new PageInfo<>(list);
    }
}
