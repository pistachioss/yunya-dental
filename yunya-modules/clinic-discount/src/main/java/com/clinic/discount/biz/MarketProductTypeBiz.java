package com.clinic.discount.biz;

import com.clinic.discount.entity.MarketProductType;
import com.clinic.discount.mapper.MarketProductTypeMapper;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;


/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-09 15:40
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MarketProductTypeBiz extends BaseBiz<MarketProductTypeMapper, MarketProductType> {
    /**
     * 新增
     *
     * @param marketProductType
     */
    public void saveMarketProductType(MarketProductType marketProductType) {
        MarketProductType data = new MarketProductType();
        data.setName(marketProductType.getName());
        if (mapper.selectOne(data) != null) {
            throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
        }

        insertSelective(marketProductType);
    }

    /**
     * 修改
     *
     * @param marketProductType
     */
    public void updateMarketProductType(MarketProductType marketProductType) {
        MarketProductType data = new MarketProductType();
        String name = marketProductType.getName();
        data.setName(name);

        if (StringUtils.isNotBlank(name)) {
            data.setName(name);
            if (mapper.select(data).size() >= 2) {
                throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
            }
        }

        updateSelectiveById(marketProductType);
    }
}
