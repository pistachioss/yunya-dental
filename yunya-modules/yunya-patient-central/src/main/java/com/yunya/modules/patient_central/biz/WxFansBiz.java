package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.system.*;
import com.yunya.feign.system.form.*;
import com.yunya.framework.common.biz.*;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.*;
import com.yunya.modules.patient_central.mapper.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import tk.mybatis.mapper.entity.*;

import java.util.*;

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

    /**
     * 系统服务调用
     */
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;

    @Autowired
    private WxFansBindBiz wxFansBindBiz;

    public PageInfo<WxFansVo> findList(WxFansQueryForm wxFansQueryForm) {
        if (wxFansQueryForm.getWhetherPage()) {
            PageHelper.startPage(wxFansQueryForm.getPageNum(), wxFansQueryForm.getPageSize());
        }
        List<WxFansVo> list = mapper.findList(wxFansQueryForm);
        return new PageInfo<>(list);
    }

    public List<WxFansDetailVO> findDetail(WxFansDetailForm wxFansDetailForm) {
        List<WxFansDetailVO> list = mapper.findDetail(wxFansDetailForm);
        DictionaryItemModel model = new DictionaryItemModel();
        List<DictionaryItem> dicList = systemServiceFeign.findDictionaryItemList(model);
        Map<String, DictionaryItem> dicMap = new HashMap(16);
        dicList.forEach(z -> dicMap.put(z.getId() + "", z));
        list.forEach(item -> item.setDictionaryName(dicMap.get(item.getDictionaryId() + "").getName()));
        return list;
    }

    public Integer save(WxFansSaveForm wxFansSaveForm) {
        wxFansBindBiz.batchInsert(wxFansSaveForm.getFansBind());
        return mapper.insertSelective(wxFansSaveForm.getWxFans());
    }

    public int countRegister(String openId) {
        Example example = new Example(WxFans.class);
        example.createCriteria().andEqualTo("openId", openId);
        return mapper.selectCountByExample(example);
    }

    public WxFans getOwnWxFans(WxUserQuery query) {
        Example example = new Example(WxFans.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(query.getOpenId())) {
            criteria.andEqualTo("openId", query.getOpenId());
        }
        if (query.getPatientId() != null) {
            criteria.andEqualTo("patientId", query.getPatientId());
        }
        return mapper.selectOneByExample(example);
    }

    public int update(WxFansUpdateForm wxFansUpdateForm){
        WxFans wxFans = new WxFans();
        wxFans.setId(wxFansUpdateForm.getId());
        wxFans.setRemark(wxFansUpdateForm.getRemark());
        wxFans.setUpdTime(new Date());
        return mapper.updateByPrimaryKeySelective(wxFans);
    }
}
