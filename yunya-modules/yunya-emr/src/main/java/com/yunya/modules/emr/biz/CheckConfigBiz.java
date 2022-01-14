package com.yunya.modules.emr.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.CheckConfigForm;
import com.yunya.feign.emr.domain.model.CheckConfigModel;
import com.yunya.feign.emr.domain.query.SymptomConfigQuery;
import com.yunya.feign.emr.domain.vo.CheckConfigVO;
import com.yunya.feign.emr.domain.vo.SymptomConfigVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.PageQuery;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.emr.CheckConfig;
import com.yunya.modules.emr.mapper.CheckConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * 简介：检查设置
 *
 * @author: chenlin
 * @Description: 检查设置
 * @Date: 2022/1/7 18:28
 * @since: 1.0.0
 */
@Service
public class CheckConfigBiz extends BaseBiz<CheckConfigMapper, CheckConfig> {

    @Autowired
    private SymptomConfigBiz symptomConfigBiz;

    /**
     * 添加检查设置
     *
     * @param model
     * @return
     */
    public int add(CheckConfigModel model) {
        Date now = new Date(System.currentTimeMillis());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        checkNameRepeated(null, model.getCheckName());
        CheckConfig entity = new CheckConfig();
        entity.setCheckName(model.getCheckName());
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
     * @param id
     * @param checkName
     * @return
     */
    private void checkNameRepeated(Integer id, String checkName) {
        CheckConfig entity = new CheckConfig();
        entity.setCheckName(checkName);
        entity.setInservice(true);
        entity = mapper.selectOne(entity);
        if (!ObjectUtils.isEmpty(entity) && entity.getId()!=id) {
            throw new ClientServiceException("该检查名称已存在", OperationCodeConstants.DATA_EXIST);
        }
    }

    /**
     * 修改检查设置
     *
     * @param form
     * @return
     */
    public int update(CheckConfigForm form) {
        Date now = new Date(System.currentTimeMillis());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        checkNameRepeated(form.getId(), form.getCheckName());
        CheckConfig entity = checkEntityExists(form.getId());
        entity.setCheckName(form.getCheckName());
        entity.setCrtId(userId);
        entity.setCrtTime(now);
        entity.setUptId(userId);
        entity.setUptTime(now);
        return mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 根据id删除检查设置
     *
     * @param id
     * @return
     */
    public int delete(Integer id) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        checkHasSymptom(id);
        CheckConfig checkConfig = checkEntityExists(id);
        checkConfig.setInservice(false);
        checkConfig.setUptId(userId);
        return mapper.updateByPrimaryKeySelective(checkConfig);
    }

    /**
     * 校验该检查下是否还有症状
     *
     * @param id
     */
    private void checkHasSymptom(Integer id) {
        SymptomConfigQuery query = new SymptomConfigQuery();
        query.setCheckId(id);
        List<SymptomConfigVO> list = symptomConfigBiz.findSymptomConfigList(query).getList();
        if (StringHelper.isNotEmpty(list)) {
            throw new ClientServiceException("该检查已有症状，不能删除", OperationCodeConstants.OPERATION_NOT_ALLOW);
        }
    }

    /**
     * 检查是否存在
     *
     * @param id
     * @return
     */
    private CheckConfig checkEntityExists(Integer id) {
        CheckConfig checkConfig = mapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(checkConfig) || !checkConfig.getInservice()) {
            throw new ClientServiceException("该检查不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        return checkConfig;
    }

    /**
     * 条件分页查询
     *
     * @param query
     * @return
     */
    public PageInfo<CheckConfigVO> findCheckConfigList(PageQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<CheckConfigVO> list = mapper.selectCheckConfigList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public CheckConfigVO findOneById(Integer id) {
        CheckConfigVO vo = new CheckConfigVO();
        CheckConfig entity = mapper.selectByPrimaryKey(id);
        vo.setCheckName(entity.getCheckName());
        vo.setId(entity.getId());
        return vo;
    }
}
