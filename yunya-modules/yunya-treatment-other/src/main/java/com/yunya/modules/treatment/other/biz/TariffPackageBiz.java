package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment_other.domain.form.TariffPackageDetailForm;
import com.yunya.feign.treatment_other.domain.model.TariffPackageDetailModel;
import com.yunya.feign.treatment_other.domain.model.TariffPackageModel;
import com.yunya.feign.treatment_other.domain.query.TariffPackageDetailQuery;
import com.yunya.feign.treatment_other.domain.query.TariffPackageQuery;
import com.yunya.feign.treatment_other.domain.vo.TariffPackageDetailVO;
import com.yunya.feign.treatment_other.domain.vo.TariffPackageVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.treatment_other.TariffPackage;
import com.yunya.models.treatment_other.TariffPackageDetail;
import com.yunya.modules.treatment.other.mapper.TariffPackageDetailMapper;
import com.yunya.modules.treatment.other.mapper.TariffPackageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:08
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class TariffPackageBiz extends BaseBiz<TariffPackageMapper, TariffPackage> {
    @Autowired
    private TariffPackageDetailMapper tariffPackageDetailMapper;
    @Autowired
    private RemoteTreatmentServiceFeign treatmentServiceFeign;

    /**
     * 保存
     *
     * @param id
     * @param model
     */
    public void save(Integer id, TariffPackageModel model) {
        String name = model.getName();
        TariffPackage entity = checkSameName(id, name);
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        entity.setName(name);
        entity.setUptId(userId);
        entity.setUptTime(now);
        if (StringHelper.isNull(id)) {
            entity.setCrtId(userId);
            entity.setCrtTime(now);
            mapper.insertSelective(entity);
        } else {
            mapper.updateByPrimaryKey(entity);
        }
    }

    /**
     * 检查同名
     *
     * @param id
     * @param name
     * @return
     */
    public TariffPackage checkSameName(Integer id, String name) {
        Example example = new Example(TariffPackage.class);
        example.createCriteria().andEqualTo("name", name);
        TariffPackage entity = mapper.selectOneByExample(example);
        if (StringHelper.isNotNull(entity) && !entity.getId().equals(id)) {
            throw new ClientServiceException("名称已存在", OperationCodeConstants.DATA_EXIST);
        } else if (StringHelper.isNull(id)){
            return new TariffPackage();
        } else if (StringHelper.isNull(entity)) {
            return mapper.selectByPrimaryKey(id);
        }
        return entity;
    }

    public PageInfo<TariffPackageVO> findList(TariffPackageQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<TariffPackageVO> result = mapper.selectTariffPackageList(query);
        return new PageInfo<>(result);
    }

    /**
     * 批量添加明细
     *
     * @param packageId
     * @param models
     */
    public void insertDetail(Integer packageId, List<TariffPackageDetailModel> models) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        TariffPackageDetail entity = new TariffPackageDetail();
        entity.setPackageId(packageId);
        entity.setUptId(userId);
        entity.setUptTime(now);
        models.forEach(item->{
            entity.setCategoryName(item.getCategoryName());
            entity.setItemType(item.getItemType());
            entity.setItemId(item.getItemId());
            entity.setQuantity(item.getQuantity());
            entity.setCrtId(userId);
            entity.setCrtTime(now);
            tariffPackageDetailMapper.insertSelective(entity);
        });
    }

    /**
     * 修改项目数量
     *
     * @param form
     */
    public void editDetail(TariffPackageDetailForm form) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        TariffPackageDetail entity = new TariffPackageDetail();
        entity.setId(form.getId());
        entity.setQuantity(form.getQuantity());
        entity.setUptId(userId);
        entity.setUptTime(now);
        tariffPackageDetailMapper.updateByPrimaryKeySelective(entity);
    }

    public void delWithDetailById(Integer id) {
        mapper.deleteByPrimaryKey(id);
        tariffPackageDetailMapper.deleteByPackageId(id);
    }

    public PageInfo<TariffPackageDetailVO> findPackageDetailList(TariffPackageDetailQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<TariffPackageDetailVO> result = tariffPackageDetailMapper.selectTariffPackageDetailList(query);
        return new PageInfo<>(result);
    }

    public PageInfo<TariffPackageDetailVO> findDetailList(TariffPackageDetailQuery query) {
        PageInfo<TariffPackageDetailVO> pageInfo = findPackageDetailList(query);
        List<TariffPackageDetailVO> result = pageInfo.getList();
        if (StringHelper.isNotEmpty(result)) {
            result.forEach(item -> {
                Integer itemId = item.getItemId();
                if (item.getItemType().intValue() == 0) {
                    BaseTariff tariff = treatmentServiceFeign.findBaseTariffById(itemId);
                    if (StringHelper.isNotNull(tariff)) {
                        item.setItemNumber(tariff.getItemNumber());
                        item.setItemName(tariff.getName());
                    }
                }
            });
        }
        return pageInfo;
    }

    public void delDetailById(Integer id) {
        tariffPackageDetailMapper.deleteByPrimaryKey(id);
    }
}
