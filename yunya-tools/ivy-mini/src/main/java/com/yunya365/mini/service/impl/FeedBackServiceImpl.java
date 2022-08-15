package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.FeedBackAddForm;
import com.yunya.feign.ivy_mini.domain.form.FeedBackForm;
import com.yunya.feign.ivy_mini.domain.vo.FeedBackVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.WxFanByNameForm;
import com.yunya.feign.patient_central.domain.query.WxFansDetailForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.FeedBack;
import com.yunya365.mini.mapper.FeedBackMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class FeedBackServiceImpl extends BaseBiz<FeedBackMapper, FeedBack> {

    @Autowired private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;

    public PageInfo<FeedBackVO> findList(FeedBackForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        WxFanByNameForm wxFanByNameForm = new WxFanByNameForm();
        wxFanByNameForm.setName(form.getName());
        List<WxFansVo>AllfansVoList = remotePatientCentralServiceFeign.findListByName(wxFanByNameForm);
        Map<String, WxFansVo> clinicMap = new HashMap(16);
        AllfansVoList.forEach(z -> clinicMap.put(z.getId() + "", z));

        if(!StringUtils.isEmpty( form.getName())){
//            List<WxFansVo>fansVoList = remotePatientCentralServiceFeign.findListByName(wxFanByNameForm);
//            List<Integer> collect = fansVoList.stream().map(WxFansVo::getId).collect(Collectors.toList());
            List<Integer> collect = AllfansVoList.stream().map(WxFansVo::getId).collect(Collectors.toList());
            form.setNameList(collect);
        }
        List<FeedBackVO> result = mapper.findFeedBackList(form);
        for(FeedBackVO a:result){
            a.setFanName(clinicMap.get(a.getFanId()+"").getNickName());
        }
        return new PageInfo<>(result);
    }

    public ResponseResult delete(Integer id) {
        FeedBack feedBack = mapper.selectByPrimaryKey(id);
        if (feedBack == null) {
            return ResponseUtil.success("删除的记录不存在！");
        }
        int result = mapper.delete(feedBack);
        if (result <= 0){
            return ResponseUtil.success("数据删除失败！");
        }
        return ResponseUtil.success();
    }
    public ResponseResult add(FeedBackAddForm form) {
        FeedBack feedBack = new FeedBack();
        Integer fansId = Integer.valueOf(BaseContextHandler.getUserID());
        feedBack.setContext(form.getContext());
        feedBack.setFanId(fansId);
        return ResponseUtil.success(mapper.insertSelective(feedBack));
    }
}
