package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.WxFansDetailForm;
import com.yunya.feign.patient_central.domain.query.WxFansQueryForm;
import com.yunya.feign.patient_central.domain.query.WxFansSaveForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansDetailVO;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.DictionaryItemModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.patient_central.WxFans;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.WxFansMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** 系统服务调用 */
    @Autowired private RemoteSystemServiceFeign systemServiceFeign;

    @Autowired private WxFansBindBiz wxFansBindBiz;

    public PageInfo<WxFansVo> findList(WxFansQueryForm wxFansQueryForm){
        if (wxFansQueryForm.getWhetherPage()) {
            PageHelper.startPage(wxFansQueryForm.getPageNum(), wxFansQueryForm.getPageSize());
        }
        List<WxFansVo> list = mapper.findList(wxFansQueryForm);
        return new PageInfo<>(list);
    }

    public List<WxFansDetailVO> findDetail(WxFansDetailForm wxFansDetailForm){
        List<WxFansDetailVO>list = mapper.findDetail(wxFansDetailForm);
        DictionaryItemModel model = new DictionaryItemModel();
        List<DictionaryItem>dicList =  systemServiceFeign.findDictionaryItemList(model);
        Map<String, DictionaryItem> dicMap = new HashMap(16);
        dicList.forEach(z -> dicMap.put(z.getId() + "", z));
        list.forEach(item -> item.setDictionaryName(dicMap.get(item.getDictionaryId()+"").getName()));
        return list;
    }

    public void save(WxFansSaveForm wxFansSaveForm){
        wxFansBindBiz.insert(wxFansSaveForm.getFansBind());
        mapper.insert(wxFansSaveForm.getWxFans());
    }
}
