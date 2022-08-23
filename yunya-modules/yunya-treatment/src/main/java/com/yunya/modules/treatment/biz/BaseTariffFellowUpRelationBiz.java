package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.BaseTariffFellowUpRelationByItemIdQuery;
import com.yunya.feign.treatment.domain.vo.BaseTariffFellowUpRelationVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.models.tariff.BaseTariffFellowupRelation;
import com.yunya.modules.treatment.mapper.BaseTariffFellowupRelationMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Class BaseTariffFellowUpRelationService
 * @Description
 * @Author lihuibin
 * @Date 2022/8/19 23:48
 * @Version 1.0
 */
@Service
public class BaseTariffFellowUpRelationBiz extends BaseBiz<BaseTariffFellowupRelationMapper, BaseTariffFellowupRelation> {

    /**
     * 根据ID删除基础项目随访信息
     * @param id
     */
    public void deleteById(Integer id) {
        if (ObjectUtils.isEmpty(id) || id < CommonConstants.INT_ONE) {
            throw new BaseException("入参格式错误");
        }
        BaseTariffFellowupRelation baseTariffFellowupRelation = mapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(baseTariffFellowupRelation)) {
            throw new BaseException("该数据不存在");
        }
        mapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据项目ID查询随访信息
     *
     * @param query
     * @return
     */
    public PageInfo<BaseTariffFellowUpRelationVO> queryByItemId(BaseTariffFellowUpRelationByItemIdQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<BaseTariffFellowupRelation> btrList = mapper.queryByItemId(query.getItemId());
        List<BaseTariffFellowUpRelationVO> result = BeanCopierUtils.listGeneralCopyBean(btrList, BaseTariffFellowUpRelationVO.class);
        return new PageInfo<>(result);
    }
}
