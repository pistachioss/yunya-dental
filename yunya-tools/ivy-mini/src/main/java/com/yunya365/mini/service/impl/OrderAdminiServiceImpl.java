package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.OrderDetailForm;
import com.yunya.feign.ivy_mini.domain.form.OrderForm;
import com.yunya.feign.ivy_mini.domain.form.OrderUpdateForm;
import com.yunya.feign.ivy_mini.domain.vo.OrderVO;
import com.yunya.feign.ivy_mini.domain.vo.OrderWechatDetailVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.WxFanByNameForm;
import com.yunya.feign.patient_central.domain.query.WxUserQuery;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.biz.BaseBiz;

import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;

import com.yunya.models.patient_central.WxFans;
import com.yunya365.mini.entity.OrderInfo;

import com.yunya365.mini.entity.OrderOperateHistory;
import com.yunya365.mini.mapper.OrderInfoMapper;

import com.yunya365.mini.mapper.OrderOperateHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
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
public class OrderAdminiServiceImpl extends BaseBiz<OrderInfoMapper, OrderInfo> {

    @Autowired
    private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;
    @Autowired
    private OrderOperateHistoryMapper orderOperateHistoryMapper;
    public PageInfo<OrderVO> findList(OrderForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        WxFanByNameForm wxFanByNameForm = new WxFanByNameForm();
        List<WxFansVo> AllfansVoList = remotePatientCentralServiceFeign.findListByName(wxFanByNameForm);
        Map<String, WxFansVo> clinicMap = new HashMap(16);
        AllfansVoList.forEach(z -> clinicMap.put(z.getId() + "", z));

        if (!StringUtils.isEmpty(form.getName())) {
            wxFanByNameForm.setName(form.getName());
            List<WxFansVo> fansVoList = remotePatientCentralServiceFeign.findListByName(wxFanByNameForm);
            List<Integer> collect = fansVoList.stream().map(WxFansVo::getId).collect(Collectors.toList());
            form.setNameList(collect);
        }

        List<OrderVO> result = mapper.findOrderList(form);

        for (OrderVO a : result) {
            WxFansVo copy = clinicMap.get(a.getFansId() + "");
            a.setReceivingInformation(copy.getName() + " " + copy.getMobile() + " " + a.getAddress());
            a.setOpenId(copy.getOpenId());
        }
        return new PageInfo<>(result);
    }

    public ResponseResult update(OrderUpdateForm form) {
        Integer id = form.getId();
        OrderInfo order = mapper.selectByPrimaryKey(id);
        if (order == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }

        BeanUtils.copyProperties(form, order);

        order.setModifyTime(new Date(System.currentTimeMillis()));

        int result = mapper.updateByPrimaryKeySelective(order);
        if (result <= 0) {
            return ResponseUtil.success("数据修改失败！");
        }
        OrderOperateHistory orderOperateHistory = new OrderOperateHistory();
          orderOperateHistory.setOperateMan(BaseContextHandler.getUsername());
        //测试用，发布切换
//        orderOperateHistory.setOperateMan("管理员");
        orderOperateHistory.setOrderId(id);
        orderOperateHistory.setOrderStatus(Integer.valueOf(form.getStatus()));
        orderOperateHistory.setRemark(order.getRemark());
        orderOperateHistoryMapper.insertSelective(orderOperateHistory);

        return ResponseUtil.success();
    }

    public OrderWechatDetailVO findDetail(OrderDetailForm form) {
        WxUserQuery query = new WxUserQuery();
        query.setOpenId(form.getOpenId());
        WxFans wxFans = remotePatientCentralServiceFeign.getWxFans(query);
        OrderWechatDetailVO result =  mapper.findDetail(form.getId());
        result.setReceivingInformation(wxFans.getRegisterName() + " " + wxFans.getRegisterMobile() + " " + result.getAddress());
        return result;
    }
}
