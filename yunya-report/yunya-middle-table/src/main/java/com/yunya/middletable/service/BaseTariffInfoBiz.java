package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseTariffInfoMapper;
import com.yunya.middletable.dao.treatment.BaseOralTariffCategoryMapper;
import com.yunya.middletable.dao.treatment.BaseOralTariffMapper;
import com.yunya.middletable.dao.treatment.BaseTariffCategoryMapper;
import com.yunya.middletable.dao.treatment.BaseTariffMapper;
import com.yunya.models.middletable.BaseTariffInfo;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseOralTariffCategory;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.BaseTariffCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 简介: 报表价目信息业务层
 *
 * @author: chow
 * @date: 2020/10/16 10:04
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTariffInfoBiz extends BaseBiz<BaseTariffInfoMapper, BaseTariffInfo> {

  /** 价目表分类 */
  @Autowired private BaseTariffCategoryMapper tariffCategoryMapper;
  /** 价目表信息 */
  @Autowired private BaseTariffMapper tariffMapper;
  /** 商品分类 */
  @Autowired private BaseOralTariffCategoryMapper oralTariffCategoryMapper;
  /** 商品信息 */
  @Autowired private BaseOralTariffMapper oralTariffMapper;

  /**
   * 根据消息更新中间表价目表信息
   *
   * @param msg 消息参数
   */
  public void operateTariff(MessageModel msg) {
    Map<String, Object> paramMap = msg.getParamMap();
    Integer dataId = (Integer) paramMap.get("id");
    Integer dateType = (Integer) paramMap.get("type");
    BaseTariffInfo tariffInfo = generateTariffInfo(dataId, dateType);
    if (null == tariffInfo) {
      return;
    }
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.delete(tariffInfo);
        mapper.insertSelective(tariffInfo);
        break;
      case 1:
        BaseTariffInfo result = mapper.selectByPrimaryKey(dataId, dateType);
        if (null == result) {
          mapper.insertSelective(tariffInfo);
        } else {
          mapper.updateByPrimaryKeySelective(tariffInfo);
        }
        break;
      case 2:
        mapper.delete(tariffInfo);
        break;
      default:
        break;
    }
  }

  /**
   * 构建中间表价目信息
   *
   * @param itemId 价目ID
   * @param dateType 数据类型
   */
  private BaseTariffInfo generateTariffInfo(Integer itemId, Integer dateType) {
    switch (dateType) {
      case 0:
        BaseTariff tariff = tariffMapper.selectByPrimaryKey(itemId);
        if (null != tariff) {
          BaseTariffInfo baseTariffInfo = new BaseTariffInfo();
          baseTariffInfo.setItemId(itemId);
          baseTariffInfo.setItemType(dateType);
          Integer categoryId = tariff.getTariffCategoryId();
          BaseTariffCategory tariffCategory = tariffCategoryMapper.selectByPrimaryKey(categoryId);
          String categoryName = null != tariffCategory ? tariffCategory.getName() : "--";
          String itemNumber = tariff.getItemNumber();
          String itemName = tariff.getName();
          String unit = tariff.getUnit();
          BigDecimal price = tariff.getPrice();
          baseTariffInfo.setCategoryId(categoryId);
          baseTariffInfo.setCategoryName(categoryName);
          baseTariffInfo.setItemNum(itemNumber);
          baseTariffInfo.setItemName(itemName);
          baseTariffInfo.setUnit(unit);
          baseTariffInfo.setPrice(price);
          return baseTariffInfo;
        }
        return null;
      case 1:
        BaseOralTariff oralTariff = oralTariffMapper.selectByPrimaryKey(itemId);
        if (null != oralTariff) {
          BaseTariffInfo baseTariffInfo = new BaseTariffInfo();
          baseTariffInfo.setItemId(itemId);
          baseTariffInfo.setItemType(dateType);
          Integer categoryId = oralTariff.getOralTariffCategoryId();
          BaseOralTariffCategory oralTariffCategory =
              oralTariffCategoryMapper.selectByPrimaryKey(categoryId);
          String categoryName = null != oralTariffCategory ? oralTariffCategory.getName() : "--";
          String itemNumber = oralTariff.getItemNumber();
          String itemName = oralTariff.getName();
          String unit = oralTariff.getUnit();
          BigDecimal price = oralTariff.getPrice();
          baseTariffInfo.setCategoryId(categoryId);
          baseTariffInfo.setCategoryName(categoryName);
          baseTariffInfo.setItemNum(itemNumber);
          baseTariffInfo.setItemName(itemName);
          baseTariffInfo.setUnit(unit);
          baseTariffInfo.setPrice(price);
          return baseTariffInfo;
        }
        return null;
      default:
        return null;
    }
  }

  /**
   * 拉取某段时间内的价目数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullTariffData(PullForm form) {
    Integer dataType = form.getDataType();
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    switch (dataType) {
      case 0:
        {
          Example emp = new Example(BaseTariff.class);
          emp.createCriteria().andBetween("updTime", startDate, endDate);
          List<BaseTariff> tariffs = tariffMapper.selectByExample(emp);
          if (StringHelper.isNotEmpty(tariffs)) {
            tariffs.forEach(
                t -> {
                  Integer itemId = t.getId();
                  mapper.deleteByPrimaryKey(itemId, dataType);
                  BaseTariffInfo tariffInfo = generateTariffInfo(itemId, dataType);
                  mapper.insertSelective(tariffInfo);
                });
          }
          break;
        }
      case 1:
        {
          Example emp = new Example(BaseOralTariff.class);
          emp.createCriteria().andBetween("updTime", startDate, endDate);
          List<BaseOralTariff> oralTariffs = oralTariffMapper.selectByExample(emp);
          if (StringHelper.isNotEmpty(oralTariffs)) {
            oralTariffs.forEach(
                ot -> {
                  Integer itemId = ot.getId();
                  mapper.deleteByPrimaryKey(itemId, dataType);
                  BaseTariffInfo tariffInfo = generateTariffInfo(itemId, dataType);
                  mapper.insertSelective(tariffInfo);
                });
          }
          break;
        }
      default:
        break;
    }
  }
}
