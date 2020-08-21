package com.yunya.modules.treatment.biz;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.tariff.RemoteTariffServiceFeign;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.modules.treatment.mapper.OrderDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/18 11:16
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderDetailBiz extends BaseBiz<OrderDetailMapper, OrderDetail> {

  /** 系统管理服务 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /** 价目表服务 */
  @Autowired private RemoteTariffServiceFeign tariffServiceFeign;

  /**
   * 根据就诊ID查询开单详情列表
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  public List<OrderDetailVO> findOrderDetailVOList(Integer treatmentRecordId) {
    List<OrderDetailVO> resultList = mapper.selectOrderDetailVOList(treatmentRecordId);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            // todo 从缓存中获取开单项目信息
            Integer billingItemId = vo.getBillingItemId();
            Byte type = vo.getType();
            switch (type) {
              case 0:
                BaseTariff tariff = tariffServiceFeign.findBaseTariffById(billingItemId);
                if (null != tariff) {
                  vo.setBillingItemName(tariff.getName());
                  vo.setUnit(tariff.getUnit());
                }
                break;
              case 1:
                BaseOralTariff oralTariff =
                    tariffServiceFeign.findBaseOralTariffById(billingItemId);
                if (null != oralTariff) {
                  vo.setBillingItemName(oralTariff.getName());
                  vo.setUnit(oralTariff.getUnit());
                }
                break;
              default:
                break;
            }
            // todo 从缓存中获取用户（员工）信息
            Integer executorId = vo.getExecutorId();
            SysUserInfoDetail employeeInfo =
                systemServiceFeign.findSysUserEmployeeInfoByUserId(executorId);
            if (null != employeeInfo) {
              vo.setExecutorName(employeeInfo.getName());
            }
          });
    } else {
      resultList = new ArrayList<>();
    }
    return resultList;
  }
}
