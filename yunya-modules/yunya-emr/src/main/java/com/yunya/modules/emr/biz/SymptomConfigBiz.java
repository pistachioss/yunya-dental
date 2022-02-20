package com.yunya.modules.emr.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.SymptomConfigForm;
import com.yunya.feign.emr.domain.model.SymptomConfigModel;
import com.yunya.feign.emr.domain.query.SymptomConfigQuery;
import com.yunya.feign.emr.domain.vo.SymptomConfigVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.emr.SymptomConfig;
import com.yunya.modules.emr.mapper.SymptomConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * 简介：症状设置
 *
 * @author: chenlin @Description: 症状设置 @Date: 2022/1/7 18:28
 * @since: 1.0.0
 */
@Service
public class SymptomConfigBiz extends BaseBiz<SymptomConfigMapper, SymptomConfig> {

  /**
   * 新增症状
   *
   * @param model
   * @return
   */
  public int add(SymptomConfigModel model) {
    Date now = new Date(System.currentTimeMillis());
    Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
    checkNameRepeated(model.getSymptomName());
    SymptomConfig entity = new SymptomConfig();
    entity.setCheckId(model.getCheckId());
    entity.setSymptomName(model.getSymptomName());
    entity.setRemark(model.getRemark());
    entity.setInservice(true);
    entity.setCrtId(userId);
    entity.setCrtTime(now);
    entity.setUptId(userId);
    entity.setUptTime(now);
    return mapper.insertSelective(entity);
  }

  /**
   * 重名检查
   *
   * @param symptomName
   * @return
   */
  private void checkNameRepeated(String symptomName) {
    SymptomConfig symptomConfig = new SymptomConfig();
    symptomConfig.setSymptomName(symptomName);
    int count = mapper.selectCount(symptomConfig);
    if (count > 0) {
      throw new ClientServiceException("该症状名称已存在", OperationCodeConstants.DATA_EXIST);
    }
  }

  /**
   * 修改症状
   *
   * @param form
   * @return
   */
  public int update(SymptomConfigForm form) {
    SymptomConfig symptomConfig = mapper.selectByPrimaryKey(form.getId());
    if (ObjectUtils.isEmpty(symptomConfig)) {
      throw new ClientServiceException("修改失败，该数据不存在", OperationCodeConstants.DATA_NOT_EXIST);
    }
    String symptomName = form.getSymptomName();
    if (!symptomConfig.getSymptomName().equals(symptomName)) {
      checkNameRepeated(symptomName);
    }
    symptomConfig.setSymptomName(symptomName);
    symptomConfig.setRemark(form.getRemark());
    symptomConfig.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
    symptomConfig.setUptTime(new Date(System.currentTimeMillis()));
    return mapper.updateByPrimaryKeySelective(symptomConfig);
  }

  /**
   * 根据id删除症状
   *
   * @param id
   * @return
   */
  public int delete(Integer id) {
    Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
    SymptomConfig entity = checkEntityExists(id);
    entity.setInservice(false);
    entity.setUptId(userId);
    return mapper.updateByPrimaryKeySelective(entity);
  }

  /**
   * 检查是否存在
   *
   * @param id
   * @return
   */
  private SymptomConfig checkEntityExists(Integer id) {
    SymptomConfig entity = mapper.selectByPrimaryKey(id);
    if (ObjectUtils.isEmpty(entity)) {
      throw new ClientServiceException("该症状不存在", OperationCodeConstants.DATA_NOT_EXIST);
    }
    return entity;
  }

  /**
   * 分页查询
   *
   * @param query
   * @return
   */
  public PageInfo<SymptomConfigVO> findSymptomConfigList(SymptomConfigQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<SymptomConfigVO> list = mapper.selectSymptomConfigList(query);
    return new PageInfo<>(list);
  }

  /**
   * 根据id查询
   *
   * @param id
   * @return
   */
  public SymptomConfigVO findOneById(Integer id) {
    SymptomConfig entity = mapper.selectByPrimaryKey(id);
    SymptomConfigVO vo = new SymptomConfigVO();
    vo.setId(entity.getId());
    vo.setRemark(entity.getRemark());
    vo.setSymptomName(entity.getSymptomName());
    return vo;
  }
}
