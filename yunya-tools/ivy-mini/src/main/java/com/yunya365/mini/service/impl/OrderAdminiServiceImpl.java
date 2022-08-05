package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.yunya.feign.ivy_mini.domain.form.*;
import com.yunya.feign.ivy_mini.domain.vo.OrderVO;
import com.yunya.feign.ivy_mini.domain.vo.OrderWechatDetailVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.WxUserQuery;
import com.yunya.feign.patient_central.domain.vo.web.WxPatientVo;
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

import java.util.*;

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

        List<OrderVO> result = new ArrayList<>();
//        if (!StringUtils.isEmpty(form.getName())) {
//            wxFanByNameForm.setName(form.getName());
//            List<WxFansVo> fansVoList = remotePatientCentralServiceFeign.findListByName(wxFanByNameForm);
//            List<Integer> collect = fansVoList.stream().map(WxFansVo::getId).collect(Collectors.toList());
//            form.setNameList(collect);
//            if(collect.size()>0){
                result  = mapper.findOrderList(form);
//            }else{
//                return new PageInfo<>(result);
//            }
//        }else{
//            result  = mapper.findOrderList(form);
//        }

        for (OrderVO a : result) {
//            WxFansVo copy = clinicMap.get(a.getFansId() + "");
            a.setReceivingInformation(a.getReceiverName() + " " + a.getReceiverPhone() + " " + a.getAddress());
//            a.setOpenId(copy.getOpenId());
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

        order.setUpdTime(new Date(System.currentTimeMillis()));
        order.setDeliveryTime(new Date(System.currentTimeMillis()));
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
        OrderWechatDetailVO result =  mapper.findDetail(form.getId());

        WxFans wxFans = remotePatientCentralServiceFeign.getWxfansInfo(result.getFansId());

        result.setFansName(wxFans.getNickName());
        result.setReceivingInformation(result.getReceiverName() + " " + result.getReceiverPhone() + " " + result.getAddress());
        return result;
    }
}
