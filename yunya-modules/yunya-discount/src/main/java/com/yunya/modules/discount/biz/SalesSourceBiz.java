package com.yunya.modules.discount.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.models.discount.SalesSource;
import com.yunya.modules.discount.mapper.SalesSourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/11/29
 * @description:
 */
@Service
@Slf4j
public class SalesSourceBiz extends BaseBiz<SalesSourceMapper, SalesSource> {
    /**
     * 新增
     *
     * @param
     */
    public void saveSalesSource(SalesSource salesSource) {
        SalesSource data = new SalesSource();
        data.setName(salesSource.getName());
        if (mapper.selectOne(data) != null) {
            throw new BaseException("来源名称已被占用", NAME_IS_OCCUPIED);
        }
        insertSelective(salesSource);
    }

    /**
     * 修改
     *
     * @param
     */
    public void updateSalesChannel(SalesSource salesSource) {
        SalesSource data = new SalesSource();
        String name = salesSource.getName();
        data.setName(name);

        if (StringUtils.isNotBlank(name)) {
            data.setName(name);
            if (mapper.select(data).size() >= 2) {
                throw new BaseException("来源名称已被占用", NAME_IS_OCCUPIED);
            }
        }
        updateSelectiveById(salesSource);
    }

}
