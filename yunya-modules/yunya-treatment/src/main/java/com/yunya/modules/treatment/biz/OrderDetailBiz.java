package com.yunya.modules.treatment.biz;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.tariff.RemoteTariffServiceFeign;
import com.yunya.feign.treatment.domain.model.GoodsDetailModel;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.ClinicOralTariff;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.treatment.mapper.OrderDetailMapper;
import com.yunya.modules.treatment.mapper.OrderRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介: 开单明细业务层（开单明细列表查询、添加商品、删除开单明细）
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

  @Autowired private OrderRecordMapper orderRecordMapper;

  /**
   * 根据开单记录ID查询开单详情列表
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  public List<OrderDetailVO> findOrderDetailVOList(Integer orderRecordId) {
    List<OrderDetailVO> resultList = mapper.selectOrderDetailVOList(orderRecordId);
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

  /**
   * 添加商品
   *
   * @param model 商品参数
   */
  public void addGoodDetail(GoodsDetailModel model) {
    Integer orderRecordId = model.getOrderRecordId();
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException(
          "添加商品失败，传入参数有误，为查询到与之匹配的开单记录！", OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
    }
    // 新增商品明细并更新开单记录总额
    List<OrderDetailModel> orderDetails = model.getOrderDetails();
    if (StringHelper.isNotEmpty(orderDetails)) {
      BigDecimal totalAmount = orderRecord.getTotalAmount();
      Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
      OrderDetail entity = new OrderDetail();
      ClinicOralTariff oralTariff = new ClinicOralTariff();
      BigDecimal price = BigDecimal.valueOf(0);
      Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      for (OrderDetailModel detail : orderDetails) {
        entity.setTreatmentRecordId(treatmentRecordId);
        entity.setOrderRecordId(orderRecordId);
        entity.setToothBit(detail.getToothBit());
        entity.setExecutorId(detail.getExecutorId());
        entity.setRemarks(detail.getRemarks());
        Byte type = detail.getType();
        entity.setType(type);
        Integer itemId = detail.getBillingItemId();
        entity.setBillingItemId(itemId);
        // todo 从缓存查询开单项目
        switch (type) {
          case 0:
            throw new ClientServiceException(
                "添加商品失败，传入的参数有误！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
          case 1:
            oralTariff.setClinicId(orgId);
            oralTariff.setOralTariffId(itemId);
            ClinicOralTariff clinicOralTariff = tariffServiceFeign.findClinicOralTariff(oralTariff);
            if (null != clinicOralTariff) {
              price = clinicOralTariff.getPrice();
            }
            break;
          default:
            break;
        }
        entity.setPrice(price);
        Integer quantity = detail.getQuantity();
        entity.setQuantity(quantity);
        BigDecimal detailTotal = price.multiply(BigDecimal.valueOf(quantity));
        entity.setReceivableAmount(detailTotal);
        entity.setOrgId(orgId);
        entity.setCrtId(userId);
        entity.setCrtName(name);
        mapper.insertSelective(entity);
        totalAmount = totalAmount.add(detailTotal);
      }
      orderRecord.setTotalAmount(totalAmount);
      orderRecord.setUpdId(userId);
      orderRecord.setUpdName(name);
      orderRecordMapper.updateByPrimaryKeySelective(orderRecord);
    }
  }

  /**
   * 根据开单明细ID删除开单明细
   *
   * @param id 开单明细ID
   */
  public void deleteOrderDetailById(Integer id) {
    OrderDetail orderDetail = mapper.selectByPrimaryKey(id);
    if (null != orderDetail) {
      Integer crtId = orderDetail.getCrtId();
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      if (!crtId.equals(userId)) {
        throw new ClientServiceException(
            "删除失败,只能删除本人添加项目！", OperationCodeConstants.DELETE_NOT_ALLOW);
      }
      mapper.deleteByPrimaryKey(id);
    }
  }
}
