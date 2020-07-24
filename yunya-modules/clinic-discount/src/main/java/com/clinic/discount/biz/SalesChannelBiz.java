package com.clinic.discount.biz;

import com.clinic.discount.entity.SalesChannel;
import com.clinic.discount.mapper.SalesChannelMapper;
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
public class SalesChannelBiz extends BaseBiz<SalesChannelMapper, SalesChannel> {
    /**
     * 新增
     *
     * @param salesChannel
     */
    public void saveSalesChannel(SalesChannel salesChannel) {
        SalesChannel data = new SalesChannel();
        data.setName(salesChannel.getName());
        if (mapper.selectOne(data) != null) {
            throw new BaseException("销售名称已被占用", NAME_IS_OCCUPIED);
        }

        insertSelective(salesChannel);
    }

    /**
     * 修改
     *
     * @param salesChannel
     */
    public void updateSalesChannel(SalesChannel salesChannel) {
        SalesChannel data = new SalesChannel();
        String name = salesChannel.getName();
        data.setName(name);

        if (StringUtils.isNotBlank(name)) {
            data.setName(name);
            if (mapper.select(data).size() >= 2) {
                throw new BaseException("销售名称已被占用", NAME_IS_OCCUPIED);
            }
        }

        updateSelectiveById(salesChannel);
    }
}
