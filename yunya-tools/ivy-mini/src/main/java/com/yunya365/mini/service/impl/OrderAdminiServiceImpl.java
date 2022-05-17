package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.OrderForm;
import com.yunya.feign.ivy_mini.domain.vo.OrderVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.WxFanByNameForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya365.mini.entity.Order;
import com.yunya365.mini.mapper.OrderMapper;
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
 * @date: 2022/5/17
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderAdminiServiceImpl  extends BaseBiz<OrderMapper, Order> {

    @Autowired
    private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;
    public PageInfo<OrderVO> findList(OrderForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        WxFanByNameForm wxFanByNameForm = new WxFanByNameForm();
        List<WxFansVo>AllfansVoList = remotePatientCentralServiceFeign.findListByName(wxFanByNameForm);
        Map<String, WxFansVo> clinicMap = new HashMap(16);
        AllfansVoList.forEach(z -> clinicMap.put(z.getId() + "", z));

        if(!StringUtils.isEmpty( form.getName())){
            wxFanByNameForm.setName(form.getName());
            List<WxFansVo>fansVoList = remotePatientCentralServiceFeign.findListByName(wxFanByNameForm);
            List<Integer> collect = fansVoList.stream().map(WxFansVo::getId).collect(Collectors.toList());
            form.setNameList(collect);
        }

        List<OrderVO> result = mapper.findOrderList(form);

        for(OrderVO a:result){
            WxFansVo copy = clinicMap.get(a.getFansId()+"");
            a.setReceivingInformation(copy.getNickName()+" "+copy.getMobile()+" "+a.getAddress());
        }
        return new PageInfo<>(result);
    }
}
